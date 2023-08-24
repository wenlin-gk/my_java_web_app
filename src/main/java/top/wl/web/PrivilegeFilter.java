package top.wl.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.wl.domain.User;
import top.wl.service.UserService;
import top.wl.web.servlet.UserServlet;

public class PrivilegeFilter implements Filter {
  private static Logger log = LogManager.getLogger(PrivilegeFilter.class);

  public static final String s_k_4_admin = "adminuser"; // session key for admin
  public static final String s_k_4_user = "user";
  public static final String s_k_4_user_auto_login = "autologin";
  public static final String uri_4_user_login = "/login.jsp";
  public static final String uri_4_admin_login = "/admin/login.jsp";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    HttpServletRequest r = (HttpServletRequest) request;
    String uri = r.getRequestURI().replace(r.getContextPath(), "");

    if (uri.startsWith("/admin")) {
      doFilter4admin(request, response, chain, r, uri);
    } else {
      doFilter4user(request, response, chain, r, uri);
    }
  }

  private void doFilter4admin(ServletRequest request, ServletResponse response,
      FilterChain chain, HttpServletRequest r, String uri)
      throws IOException, ServletException {
    String adminUser = (String) r.getSession()
        .getAttribute(PrivilegeFilter.s_k_4_admin);

    if (adminUser != null || (uri.equals("/admin/user")
        && "login".equals(r.getParameter("method")))) {
      log.info(String.format("Allow-Admin-%s-%s.", adminUser, uri));
      chain.doFilter(request, response);
    } else {
      log.info(String.format("Deny-Admin-%s-%s.", adminUser, uri));
      request.getRequestDispatcher(PrivilegeFilter.uri_4_admin_login)
          .forward(request, response);
    }
  }

  private void doFilter4user(ServletRequest request, ServletResponse response,
      FilterChain chain, HttpServletRequest r, String uri)
      throws IOException, ServletException {
    User user = (User) r.getSession().getAttribute(PrivilegeFilter.s_k_4_user);
    if (null == user) {
      user = getUserFromCookie(r);
      if (user != null)
        r.getSession().setAttribute(PrivilegeFilter.s_k_4_user, user);
    }

    if (user == null && uri.startsWith("/order")) {
      log.info(String.format("Deny-user-%s-%s.", user, uri));
      request.setAttribute("msg", "请先登录");
      request.getRequestDispatcher(PrivilegeFilter.uri_4_user_login)
          .forward(request, response);
    } else {
      log.info(String.format("Allow-user-%s-%s.", user, uri));
      chain.doFilter(request, response);
    }
  }

  private User getUserFromCookie(HttpServletRequest httpRequest) {
    String u_p = UserServlet.getUserPasswd(httpRequest.getCookies());

    if (u_p != null) {
      String[] cv = u_p.split("@");
      try {
        return new UserService().login(cv[0], cv[1]);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }

    return null;
  }

  @Override
  public void destroy() {
  }

}
