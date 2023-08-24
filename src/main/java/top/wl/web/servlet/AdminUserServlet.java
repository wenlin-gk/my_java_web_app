package top.wl.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.wl.Const;
import top.wl.domain.PageBean;
import top.wl.domain.User;
import top.wl.service.UserService;
import top.wl.service.error.BusinessNotAllowed;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcUnavailable;

public class AdminUserServlet extends BaseServlet {
  private static final long serialVersionUID = 1L;
  private static Logger log = LogManager.getLogger(AdminUserServlet.class);
  private static UserService us = new UserService();

  public static final String errMsg4loginInvalidateName = "账号(%s)不存在。";
  public static final String errMsg4loginErrorPwd = "密码错误。";
  public static final String errMsg4PageNumInvidate = "页码<0或者超出尾页码：pageNumber=%s。要求pageNumber>0";
  public static final String errMsg4editUIPI = "ID为空 或者 太长：ID='%s'。要求1-32个字符。";
  public static final String errMsg4editUINA = "用户不存在。ID=%s";
  public static final String errMsg4updateNA = "用户不存在。ID=%s，name=%s";
  public static final String errMsg4updatePI = "用户属性不合法。%s";
  public static final String errMsg4deletePI = "ID为空 或者 太长：ID='%s'。要求1-20个字符。";
  public static final String errMsg4deleteNA = "用户不存在 或者 被其他实体引用不允许删除。(ID=%s)";
  // admin账号密码为系统默认值
  public String login(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String username = getParam(request, "adminName");
    String passwd = getParam(request, "adminPasswd");
    log.debug(String.format("%s-%s", username, passwd));

    boolean loginSucc = "admin".equals(username)
        && Const.ADMIN_PASSWD.equals(passwd);
    if (loginSucc) {
      request.getSession().setAttribute("adminuser", "admin");
      return "/admin/index.jsp";
    } else {
      if (!"admin".equals(username)) {
        request.setAttribute("adminName", username);
        setMsg(request, String.format(errMsg4loginInvalidateName, username));
      } else if (!Const.ADMIN_PASSWD.equals(passwd)) {
        request.setAttribute("adminName", username);
        request.setAttribute("adminPasswd", passwd);
        setMsg(request, errMsg4loginErrorPwd);
      }
      return "/admin/login.jsp";
    }
  }

  public String getByPage(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    int pageNumber = 1;
    try {
      pageNumber = Integer.parseInt(getParam(request, "pageNumber"));
    } catch (NumberFormatException e) {
      log.error(e.getMessage(), e);
    }

    try {
      PageBean<User> bean = us.getByPage(pageNumber, 4);
      request.setAttribute("pb", bean);
      return "/admin/user/list.jsp";
      
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

  public String logout(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    request.getSession().invalidate();
    return "/admin/login.jsp";
  }

  public String editUI(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    User u = null;
    try {
      u = us.getByUid(getParam(request, "uid"));

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4editUIPI, getParam(request, "uid")));
      return BaseServlet.msgPage;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, e.toString());
      return BaseServlet.msgPage;
    }
    if(null==u) {
      setMsg(request, String.format(errMsg4editUINA, getParam(request, "uid")));
      return BaseServlet.msgPage;
    }
    
    request.setAttribute("model", u);
    return "/admin/user/edit.jsp";
  }
  public String update(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    User user = new User();
    try {
      BeanUtils.populate(user, getParams(request));
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }
    log.debug(user);
    try {
      us.update(user);

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;

    } catch (BusinessNotAllowed e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, String.format(errMsg4updateNA, user.getUid(), user.getName()));
      return BaseServlet.msgPage;

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, String.format(errMsg4updatePI, user));
      return BaseServlet.msgPage;
    }

    response.sendRedirect(
        request.getContextPath() + "/admin/user?method=getByPage&pageNumber=1");
    return null;
  }

  public String delete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      us.delete(getParam(request, "uid"));
      
    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;

    } catch (BusinessNotAllowed e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, String.format(errMsg4deleteNA, getParam(request, "uid")));
      return BaseServlet.msgPage;

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, String.format(errMsg4deletePI, getParam(request, "uid")));
      return BaseServlet.msgPage;
    }
    response.sendRedirect(
        request.getContextPath() + "/admin/user?method=getByPage&pageNumber=1");
    return null;
  }
}
