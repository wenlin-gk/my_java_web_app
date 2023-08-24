package top.wl.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.wl.domain.User;
import top.wl.service.UserService;
import top.wl.service.error.BusinessNotAllowed;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcFault;
import top.wl.service.error.SvcUnavailable;
import top.wl.utils.CookieUtils;
import top.wl.utils.UUIDUtils;
import top.wl.web.PrivilegeFilter;

public class UserServlet extends BaseServlet {
  private static final long serialVersionUID = 1L;
  private static Logger log = LogManager.getLogger(UserServlet.class);
  public static final String savename = "savename";

  public static UserService us = new UserService();

  public static final String errMsg4loginPI = "用户名/密码 为空 或者 太长：user='%s', passwd='%s'。要求1-20个字符。";
  public static final String errMsg4loginInvalidateName = "账号(%s)不存在。";
  public static final String errMsg4loginErrorPwd = "密码错误。";
  public static final String errMsg4loginInvalidateState = "请先去邮箱激活,再登录!";
  public static final String msg4AlreadyLogin = "已经登录，请刷新页面！";
  public static final String errMsg4activeNullUser = "用户不存在。";
  public static final String errMsg4activeFailed = "激活失败,请重新激活或者重新注册~";
  public static final String msg4activeSucc = "恭喜你,激活成功了,可以登录了~";
  public static final String errMsg4addPI = "用户属性不合法。%s";
  public static final String errMsg4addNA = "用户ID冲突，请重试。";
  public static final String msg4registSucc = "恭喜你,注册成功,请登录邮箱完成激活!";

  //ajax检查用户名
  public String checkUsername(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    String username = getParam(request, "username");

    User existUser = null;
    try {
      existUser = us.getByUsername(username);
    } catch (SvcUnavailable | ParamInvalidate e) {
      log.error(e.getMessage(), e);
      // 故障
      response.getWriter().println(0);
      return null;
    }

    log.debug(existUser);
    if (existUser == null) {
      // 用户名没有使用
      response.getWriter().println(1);
    } else {
      // 用户名已经被使用
      response.getWriter().println(2);
    }
    return null;
  }

  public String logout(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    
    request.getSession().invalidate();
    CookieUtils.delCookieByName(request, response,
        PrivilegeFilter.s_k_4_user_auto_login);
    return "/index.jsp";
  }
  public String invalidateSession4test(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    
    request.getSession().invalidate();
    return "/msg.jsp";
  }
  public String login(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String username = getParam(request, "username");
    String password = getParam(request, "password");

    User user = null;
    try {
      user = us.login(username, password);
      
    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4loginPI, username, password));
      return "/login.jsp";
    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      this.setMsg(request, serviceUnavailable);
      return "/login.jsp";
    }

    log.info(user);
    if (user == null) {
      request.setAttribute("msg", errMsg4loginErrorPwd);
      return "/login.jsp";
    }

    if (User.USER_IS_ACTIVE != user.getState()) {
      request.setAttribute("msg", errMsg4loginInvalidateState);
      return "/login.jsp";
    }

    request.getSession().setAttribute("user", user);
    
    // 用户勾选了自动登录
    if ("1"
        .equals(getParam(request, PrivilegeFilter.s_k_4_user_auto_login))) {
      Cookie c = new Cookie(PrivilegeFilter.s_k_4_user_auto_login,
          genCookieValue4AutoLogin(user));
      c.setPath(request.getContextPath() + "/");
      c.setMaxAge(60 * 60 * 24 * 7);
      response.addCookie(c);
    } else {
      CookieUtils.delCookieByName(request, response,
          PrivilegeFilter.s_k_4_user_auto_login);
    }
    // 判断是否勾选了记住用户名
    if ("1".equals(getParam(request, UserServlet.savename))) {
      Cookie c = new Cookie(UserServlet.savename,
          URLEncoder.encode(username, "utf-8"));
      c.setPath(request.getContextPath() + "/");
      c.setMaxAge(60 * 60 * 24 * 7);
      response.addCookie(c);
    } else {
      CookieUtils.delCookieByName(request, response, UserServlet.savename);
    }
    
    return "/index.jsp";
  }

  public String loginUI(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    User user = (User) request.getSession().getAttribute("user");

    if (user != null) {
      request.setAttribute("msg", msg4AlreadyLogin);
      return "/msg.jsp";
    } else {
      Cookie c = CookieUtils.getCookieByName(UserServlet.savename,
          request.getCookies());
      if (c != null)
        request.setAttribute("username", c.getValue());
      return "/login.jsp";
    }
  }

  public String active(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String code = getParam(request, "code");
    String msg = msg4activeSucc;

    User u = null;
    try {
      us.active(code);
    } catch (SvcUnavailable | ParamInvalidate | BusinessNotAllowed e) {
      log.error(e.getMessage(), e);
      msg = errMsg4activeFailed;
    }

    if (null == u)
      msg = errMsg4activeNullUser;

    setMsg(request, msg);
    return "/msg.jsp";
  }

  public String regist(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    User user = new User();
    try {
      BeanUtils.populate(user, getParams(request));
    } catch (IllegalAccessException | InvocationTargetException e) {
      request.setAttribute("msg", serviceUnavailable);
      return "/msg.jsp";
    }

    try {
      us.add(user);
    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4addPI, user.toMsg()));
      return BaseServlet.msgPage;

    } catch (BusinessNotAllowed e) {
      log.error(e.getMessage(), e);
      setMsg(request, errMsg4addNA);
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

    request.setAttribute("msg", msg4registSucc);
    return "/msg.jsp";
  }

  /**
   * @param cookies
   * @return null or "${user}@${passwd}"
   */
  public static String getUserPasswd(Cookie[] cookies) {
    return CookieUtils.getValue(PrivilegeFilter.s_k_4_user_auto_login, cookies);
  }

  public static String genCookieValue4AutoLogin(User user) {
    return user.getUsername() + '@' + user.getPassword();
  }

}
