package top.wl.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;

import top.wl.domain.Cart;
import top.wl.domain.CartItem;
import top.wl.domain.Order;
import top.wl.domain.OrderItem;
import top.wl.domain.PageBean;
import top.wl.domain.User;
import top.wl.service.OrderService;
import top.wl.service.error.BusinessNotAllowed;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcFault;
import top.wl.service.error.SvcUnavailable;
import top.wl.utils.UUIDUtils;

public class OrderServlet extends BaseServlet {
  private static final long serialVersionUID = 1L;
  private static Logger log = LogManager.getLogger(OrderServlet.class);
  private OrderService os = new OrderService();

  public static final String errMsg4getPI = "ID为空 或者 太长：ID='%s'。要求1-32个字符。";

  public static final String errMsg4PageNumInvidate = "页码<0或者超出尾页码：pageNumber=%s。要求pageNumber>0";
  public static final String errMsg4addPI = "订单属性不合法。%s";
  public static final String errMsg4addNA = "订单商品/所属用户不存在 或者 ID已经被占用。%s";
  public static final String errMsg4updatePI = "订单属性不合法。%s";
  public static final String errMsg4updateNA = "订单商品/所属用户不存在 或者 订单不存在。%s";
  public static final String errMsg4CartEmpty = "空购物车不能生成订单。";
  public static final int pageSize = 3;

  public String getById(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    String oid = getParam(request, "oid");
    try {
      Order order = os.getById(oid);
      if(null==order) {
        log.error(String.format(errMsg4updateNA, oid));
        setMsg(request, String.format(errMsg4updateNA, oid));
        return BaseServlet.msgPage;
      }
      request.setAttribute("bean", order);
      return "/order/order_info.jsp";

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4getPI, oid));
      return BaseServlet.msgPage;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }

  }

  public String getAllByPage(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    int pageNumber = 1;
    try {
      pageNumber = Integer.parseInt(getParam(request, "pageNumber"));
    } catch (NumberFormatException e) {
      log.error(e.getMessage(), e);
    }

    User user = (User) request.getSession().getAttribute("user");

    try {
      PageBean<Order> bean = os.getAllByPage(pageNumber, pageSize,
          user.getUid());
      request.setAttribute("pb", bean);
      return "/order/order_list.jsp";

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4PageNumInvidate, pageNumber));
      return BaseServlet.msgPage;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }

  }

  public String save(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    
    User user = (User) request.getSession().getAttribute("user");
    Cart cart = (Cart) request.getSession().getAttribute("cart");

    if (null == cart || cart.getItemMap().isEmpty()) {
      log.error(errMsg4CartEmpty);
      this.setMsg(request, errMsg4CartEmpty);
      return BaseServlet.msgPage;
    }

    Order order = new Order();
    order.setTotal(cart.getTotal());
    order.setUser(user);
    
    for (CartItem ci : cart.getCartItems()) {
      OrderItem oi = new OrderItem();
      oi.setItemid(UUIDUtils.getId());
      oi.setCount(ci.getCount());
      oi.setSubtotal(ci.getSubtotal());
      oi.setProduct(ci.getProduct());
      oi.setOrder(order);
      order.getItems().add(oi);
    }

    try {
      order = os.add(order);
      
    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4addPI, order));
      return BaseServlet.msgPage;

    } catch (BusinessNotAllowed e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4addNA, order));
      return BaseServlet.msgPage;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;

    } catch (SvcFault e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceFault);
      return BaseServlet.msgPage;
    }

    cart.clearCart();
    request.setAttribute("bean", order);
    return "/order/order_info.jsp";
  }

  public String pay(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Order order = new Order();
    try {
      BeanUtils.populate(order, getParams(request));
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }
    User user = (User) request.getSession().getAttribute("user");
    
    try {
      order = os.pay(order, user.getUid());
      
    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4updatePI, order));
      return BaseServlet.msgPage;

    } catch (BusinessNotAllowed e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4updateNA, order));
      return BaseServlet.msgPage;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }

    request.setAttribute("msg", "您的订单号为:" + order.getOid() + ",金额为:"
        + order.getTotal() + "，已经支付成功，等待发货~~");
    return "/msg.jsp";
  }

}
