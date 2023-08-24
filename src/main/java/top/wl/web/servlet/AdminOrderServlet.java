package top.wl.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import top.wl.dao.OrderDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.domain.Order;
import top.wl.domain.OrderItem;
import top.wl.service.OrderService;
import top.wl.service.error.BusinessNotAllowed;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcUnavailable;
import top.wl.utils.JsonUtil;

public class AdminOrderServlet extends BaseServlet {
  private static final long serialVersionUID = 1L;
  private static Logger log = LogManager.getLogger(AdminOrderServlet.class);
  OrderService os = new OrderService();

  public static final String errMsg4updatePI = "ID为空 或者 ID太长：id='%s'。要求1-32个字符。";
  public static final String errMsg4updateNA = "订单不存在。(id=%s)";
  public static final String errMsg4getallPI = "订单状态参数不合法：state='%s'。可选值0,1,2,3";

  public String updateState(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    String oid = getParam(request, "oid");

    try {
      os.update(oid, Order.ORDER_YIFAHUO);
    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4updatePI, oid));
      return BaseServlet.msgPage;

    } catch (BusinessNotAllowed e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4updateNA, oid));
      return BaseServlet.msgPage;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }

    response.sendRedirect(request.getContextPath()
        + "/admin/order?method=getAllByState&state=" + Order.ORDER_YIFAHUO);
    return null;
  }

  public String getInJson(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    String oid = getParam(request, "oid");
    Order order = null;
    try {
      order = os.getById(oid);
    } catch (SvcUnavailable | ParamInvalidate e) {
      log.error(e.getMessage(), e);
    }

    JsonConfig config = JsonUtil
        .configJson(new String[] { "order", "pdate", "pdesc", "validate","validate4Update"});
    response.setContentType("text/html;charset=utf-8");

    List<OrderItem> items = null;
    if (null != order && null != order.getItems() && order.getItems().size() > 0)
      items = order.getItems();
    log.info(JSONArray.fromObject(items, config));
    response.getWriter().println(JSONArray.fromObject(items, config));
    
    return null;
  }
  public String getAllByState(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    String state = getParam(request, "state");

    List<Order> list = null;
    try {
      list = os.getAllByState(state);
    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4getallPI, state));
      return BaseServlet.msgPage;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }

    request.setAttribute("list", list);
    return "/admin/order/list.jsp";
  }
}
