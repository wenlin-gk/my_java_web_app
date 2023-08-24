package top.wl.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {
  /**
   * 
   * @param name:   不能为null；
   * @param cookies
   * @return 如果不存在，返回null；
   */
  public static Cookie getCookieByName(String name, Cookie[] cookies) {
    if (cookies != null) {
      for (Cookie c : cookies) {
        if (name.equals(c.getName())) {
          return c;
        }
      }
    }
    return null;
  }

  /**
   * 
   * @param name:   不能为null；
   * @param cookies
   * @return 如果不存在，返回null；
   */
  public static String getValue(String name, Cookie[] cookies) {
    Cookie c = getCookieByName(name, cookies);
    return c != null ? c.getValue() : null;
  }

  public static void delCookieByName(HttpServletRequest request,
      HttpServletResponse response, String name) {
    Cookie c = new Cookie(name, "");
    c.setPath(request.getContextPath() + "/");
    c.setMaxAge(0);
    response.addCookie(c);
  }
}
