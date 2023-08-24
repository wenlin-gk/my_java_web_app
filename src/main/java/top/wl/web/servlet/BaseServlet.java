package top.wl.web.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private static Logger log = LogManager.getLogger(BaseServlet.class);
  public static final String serviceUnavailable = "服务不可用。";
  public static final String serviceFault = "服务故障。";
  static final String msgTmplate4MethodFeildInvalidate = "Method(%s) invalidate.";
  static final String msg4MethodFeildIsNull = "Method feild is null.";

  public static final String msgPage = "/msg.jsp";

  public void setMsg(HttpServletRequest request, String msg) {
    request.setAttribute("msg", msg);
  }

  @Override
  public void service(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    
    String mName = request.getParameter("method");
    if (mName == null || mName.length() == 0) {
      request.setAttribute("msg", msg4MethodFeildIsNull);
      request.getRequestDispatcher("/msg.jsp").forward(request, response);
      return;
    }

    encodeParams(request);

    Method method = null;
    try {
      method = this.getClass().getMethod(mName, HttpServletRequest.class,
          HttpServletResponse.class);
    } catch (NoSuchMethodException e) {
      log.error(e.getMessage(), e);
      request.setAttribute("msg",
          String.format(msgTmplate4MethodFeildInvalidate, mName));
      request.getRequestDispatcher("/msg.jsp").forward(request, response);
      return;
    }

    String path = null;
    try {
      log.info("Recv req: " + request.getRequestURI() + " params: "
          + getParams(request).toString());

      path = (String) method.invoke(this, request, response);

      log.info("Req exec finished. succ.");
    } catch (SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      log.error(e.getMessage(), e);
      log.info("Req exec finished. failed.");
      request.setAttribute("msg", serviceUnavailable);
      request.getRequestDispatcher("/msg.jsp").forward(request, response);
      return;
    }

    if (path != null) {
      request.getRequestDispatcher(path).forward(request, response);
    }
  }

  private void encodeParams(HttpServletRequest request) {
    String method = request.getMethod();
    
    if ("post".equalsIgnoreCase(method)) {
      Map<String, String[]> map = request.getParameterMap();
      
      for (String key : map.keySet()) {
        String[] arr = map.get(key);
        if (null == arr)
          continue;
        log.debug(key);
        for (int i = 0; i < arr.length; i++) {
          try {
            log.debug(arr[i]);
            arr[i] = new String(arr[i].getBytes("ISO-8859-1"), "utf-8");
          } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
          }
        }
      }
    }

  }

  // 1.原始value有多个，处理后，只取第一个。2.如果第一个为""，直接剔除。
  protected static Map<String, String> getParams(HttpServletRequest request) {
    HashMap<String, String> reqParams = new HashMap<String, String>();

    for (Iterator<Entry<String, String[]>> it = request.getParameterMap()
        .entrySet().iterator(); it.hasNext();) {
      Entry<String, String[]> e = it.next();
      if (e.getValue() != null && e.getValue().length > 0
          && e.getValue()[0] != null && e.getValue()[0].trim().length() != 0)
        reqParams.put(e.getKey(), e.getValue()[0]);
    }

    return reqParams;
  }

  protected static String getParam(HttpServletRequest request, String name) {
    if (name == null || name.trim().length() == 0) {
      return null;
    }

    String[] values = getParameterValues(request, name);
    if (values == null || values.length == 0) {
      return null;

    } else if (values.length > 1) {
      log.warn(String.format("Select a value in 0 index, ignore other value.%s",
          values.toString()));
    }
    
    if (values[0] != null && values[0].trim().length() != 0)
      return values[0].trim();
    else
      return null;
  }

  private static String[] getParameterValues(HttpServletRequest request,
      String name) {
    Map<String, String[]> map = request.getParameterMap();
    if (map == null || map.size() == 0) {
      return null;
    }

    return map.get(name);
  }

}

class CreateFileFailed extends Exception {
}
