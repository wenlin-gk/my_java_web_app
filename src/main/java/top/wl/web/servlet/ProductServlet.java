package top.wl.web.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JsonConfig;
import top.wl.domain.PageBean;
import top.wl.domain.Product;
import top.wl.service.ProductService;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcUnavailable;
import top.wl.utils.CookieUtils;
import top.wl.utils.JsonUtil;

public class ProductServlet extends BaseServlet {
  private static final long serialVersionUID = 1L;

  public static final String KEY_4_HISTORY = "history";
  public static final int HISTORY_SIZE = 6;
  private static Logger log = LogManager.getLogger(ProductServlet.class);
  private static ProductService ps = new ProductService();

  public static final String errMsg4PageNumInvidate = "页码<0或者超出尾页码：pageNumber=%s。要求pageNumber>0";
  public static final String errMsg4getNA = "商品不存在。(id=%s)";
  public static final String errMsg4getPI = "ID为空 或者 太长：ID='%s'。要求1-32个字符。";

  public static final int pageSize = 12;

  public String index(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    List<Product> hotList = null;
    List<Product> newList = null;
    try {
      hotList = ps.getHot();
      newList = ps.getNew();

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }

    request.setAttribute("hList", hotList);
    request.setAttribute("nList", newList);

    return "/body.jsp";
  }

  public String getByPage(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    int pageNumber = 1;
    try {
      pageNumber = Integer.parseInt(getParam(request, "pageNumber"));
    } catch (NumberFormatException e) {
      log.error(e.getMessage(), e);
    }

    String cid = getParam(request, "cid");

    PageBean<Product> bean = null;
    try {
      bean = ps.getByPage(pageNumber, pageSize, cid);

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4PageNumInvidate, pageNumber));
      return BaseServlet.msgPage;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }

    request.setAttribute("pb", bean);
    return "/product_list.jsp";
  }

  public String getInJsonById(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    String pid = getParam(request, "pid");
    Product pro = null;
    try {
      pro = ps.getById(pid);

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      response.getWriter().println("{}");
      return null;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      response.getWriter().println("{}");
      return null;
    }
    
    log.debug(pro);
    JsonConfig config = JsonUtil.configJson(new String[] { "pdate" });
    response.getWriter().println(JsonUtil.object2json(pro, config));// pro为null时，返回'null'字符串。
    
    return null;
  }

  public String getById(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    String pid = getParam(request, "pid");
    Product pro = null;
    try {
      pro = ps.getById(pid);

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4getPI, pid));
      return BaseServlet.msgPage;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }

    if (null == pro) {
      setMsg(request, String.format(errMsg4getNA, pid));
      return BaseServlet.msgPage;
    }
      
    
    request.setAttribute("bean", pro);
    addPid2History(request, response, pid);

    return "/product_info.jsp";
  }

  private void addPid2History(HttpServletRequest request,
      HttpServletResponse response, String pid) {

    Cookie c = CookieUtils.getCookieByName(ProductServlet.KEY_4_HISTORY,
        request.getCookies());

    if (null == c) {
      c = new Cookie(ProductServlet.KEY_4_HISTORY, pid);
      c.setPath(request.getContextPath() + "/");
      c.setMaxAge(60 * 60 * 24 * 7);
      response.addCookie(c);
    } else {
      String[] ids = c.getValue().split("-");

      LinkedList<String> list = new LinkedList<String>(Arrays.asList(ids));
      if (list.contains(pid)) {
        list.remove(pid);
        list.addFirst(pid);
      } else {
        if (list.size() >= ProductServlet.HISTORY_SIZE) {
          list.removeLast();
          list.addFirst(pid);
        } else
          list.addFirst(pid);
      }

      c = new Cookie(ProductServlet.KEY_4_HISTORY, String.join("-", list));
      c.setPath(request.getContextPath() + "/");
      c.setMaxAge(60 * 60 * 24 * 7);
      response.addCookie(c);
    }
  }
}
