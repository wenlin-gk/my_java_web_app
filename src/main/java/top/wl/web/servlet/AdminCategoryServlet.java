package top.wl.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.wl.domain.Category;
import top.wl.service.CategoryService;
import top.wl.service.error.BusinessNotAllowed;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcFault;
import top.wl.service.error.SvcUnavailable;

public class AdminCategoryServlet extends BaseServlet {
  private static final long serialVersionUID = 1L;
  private static Logger log = LogManager.getLogger(AdminCategoryServlet.class);
  private CategoryService cs = new CategoryService();

  public static final String errMsg4addPI = "名称为空 或者 太长：name='%s'。要求1-20个字符。";
  public static final String errMsg4deletePI = "ID为空 或者 太长：ID='%s'。要求1-20个字符。";
  public static final String errMsg4deleteNA = "分类不存在 或者 分类被其他实体引用不允许删除。(ID=%s)";
  public static final String errMsg4updatePI = errMsg4addPI;
  public static final String errMsg4updateNA = "分类不存在。(id=%s, name=%s)";
  public static final String errMsg4editUINA = "分类不存在。(id=%s)";
  public static final String errMsg4editUIPI = "ID为空 或者 太长：ID='%s'。要求1-20个字符。";

  public String add(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String cname = getParam(request, "cname");
    try {
      cs.add(cname);

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4addPI, cname));
      return BaseServlet.msgPage;

    } catch (BusinessNotAllowed e) {
      log.error(e.getMessage(), e);
      setMsg(request, "太巧了，新生成的分类ID和已有分类ID重复了。");
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

    response.sendRedirect(
        request.getContextPath() + "/admin/category?method=getAll");
    return null;
  }

  public String delete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String cid = getParam(request, "cid");
    try {
      cs.delete(cid);

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4deletePI, cid));
      return BaseServlet.msgPage;

    } catch (BusinessNotAllowed e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4deleteNA, cid));
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
    response.sendRedirect(
        request.getContextPath() + "/admin/category?method=getAll");
    return null;
  }

  public String update(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String cid = getParam(request, "cid");
    String cname = getParam(request, "cname");
    try {
      cs.update(new Category(cid, cname));
    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4updatePI, cname));
      return BaseServlet.msgPage;

    } catch (BusinessNotAllowed e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4updateNA, cid, cname));
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
    response.sendRedirect(
        request.getContextPath() + "/admin/category?method=getAll");
    return null;
  }

  public String editUI(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String cid = getParam(request, "cid");
    Category c = null;
    try {
      c = cs.get(cid);
    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4editUIPI, cid));
      return BaseServlet.msgPage;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }
    if(null==c) {
      setMsg(request, String.format(errMsg4editUINA, cid));
      return BaseServlet.msgPage;
    }
    
    request.setAttribute("cid", c.getCid());
    request.setAttribute("cname", c.getCname());
    return "/admin/category/edit.jsp";
  }

  public String getAll(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    List<Category> list = null;
    try {
      list = cs.getAll();
    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }

    request.setAttribute("list", list);
    return "/admin/category/list.jsp";
  }

}
