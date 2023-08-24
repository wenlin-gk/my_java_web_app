package top.wl.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.wl.domain.Cart;
import top.wl.domain.CartItem;
import top.wl.domain.Product;
import top.wl.service.ProductService;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcUnavailable;

public class CartServlet extends BaseServlet {
  private static final long serialVersionUID = 1L;
  private static Logger log = LogManager.getLogger(AdminUserServlet.class);
  private static ProductService ps = new ProductService();

  public static final String errMsg4getPI = "ID为空 或者 太长 或者 count<1：ID='%s',count='%s'。要求1-32个字符。";
  public static final String errMsg4getNA = "商品不存在。ID=%s";

  public String clear(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    getCart(request).clearCart();
    response.sendRedirect(request.getContextPath() + "/cart.jsp");
    return null;
  }

  public String remove(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String pid = getParam(request, "pid");
    getCart(request).removeFromCart(pid);
    response.sendRedirect(request.getContextPath() + "/cart.jsp");
    return null;
  }

  public String put(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    String pid = getParam(request, "pid");
    int count = 1;
    try {
      count = Integer.parseInt(getParam(request, "count"));
    } catch (NumberFormatException e) {
      log.error(e.getMessage(), e);
    }

    if(count<1) {
      log.error(String.format(errMsg4getPI, pid, count));
      this.setMsg(request, String.format(errMsg4getPI, pid, count));
      return BaseServlet.msgPage;
    }
    
    Product product = null;
    try {
      product = ps.getById(pid);

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, String.format(errMsg4getPI, pid, count));
      return BaseServlet.msgPage;
    }

    if (null == product) {
      this.setMsg(request, String.format(errMsg4getNA, pid));
      return BaseServlet.msgPage;
    }

    Cart cart = getCart(request);
    cart.add2cart(new CartItem(product, count));

    response.sendRedirect(request.getContextPath() + "/cart.jsp");
    return null;
  }

  private Cart getCart(HttpServletRequest request) {
    Cart cart = (Cart) request.getSession().getAttribute("cart");
    if (cart == null) {
      cart = new Cart();
      request.getSession().setAttribute("cart", cart);
    }
    return cart;
  }
}
