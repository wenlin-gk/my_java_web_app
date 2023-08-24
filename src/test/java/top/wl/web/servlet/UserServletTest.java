package top.wl.web.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import top.wl.domain.User;
import top.wl.service.UserService;
import top.wl.service.error.BusinessNotAllowed;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcFault;
import top.wl.service.error.SvcUnavailable;
import top.wl.utils.UUIDUtils;

@RunWith(MockitoJUnitRunner.class)
public class UserServletTest {

  @Test
  public void test_service() throws ServletException, IOException {
    // 分支：方法参数==null，转发到msg.jsp
    RequestDispatcher dispatcher = Mockito.mock(RequestDispatcher.class);
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    String method = null;
    Mockito.when(request.getParameter("method")).thenReturn(method);
    Mockito.when(request.getRequestDispatcher(ArgumentMatchers.argThat(
        page -> page instanceof String))).thenReturn(dispatcher);
    
    new UserServlet().service(request, response);
    
    assertForward("/msg.jsp", UserServlet.msg4MethodFeildIsNull,
        request, response);
  }
  @Test
  public void test_service1() throws ServletException, IOException {
    // 分支：调用接口错误，转发到msg.jsp
    HashMap<String, String[]> params = new HashMap<String, String[]>();
    String method = "no_exist"+UUIDUtils.getCode().substring(0,5);
    params.put("method", new String[] {method});
    
    RequestDispatcher dispatcher = Mockito.mock(RequestDispatcher.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getParameter("method")).thenReturn(method);
    Mockito.when(request.getRequestDispatcher(ArgumentMatchers.argThat(
        page -> page instanceof String))).thenReturn(dispatcher);

    
    new UserServlet().service(request, response);
    
    assertForward("/msg.jsp", 
        String.format(UserServlet.msgTmplate4MethodFeildInvalidate, method),
        request, response);
  }

  @Test
  public void test_service2() throws ServletException, IOException, SvcUnavailable, BusinessNotAllowed, SvcFault, ParamInvalidate {
  //分支：method="post"，中文被正确编解码
    String username = "中文"+UUIDUtils.getCode().substring(0, 5);
    HashMap<String, String[]> params = new HashMap<String, String[]>();
    String method = "regist";
    params.put("method", new String[] {method});
    params.put("username", new String[] {new String(username.getBytes("utf-8"), "ISO-8859-1")});
    
    RequestDispatcher dispatcher = Mockito.mock(RequestDispatcher.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    Mockito.when(request.getMethod()).thenReturn("post");
    Mockito.when(request.getParameterMap()).thenReturn(params);
    Mockito.when(request.getParameter("method")).thenReturn(method);
    Mockito.when(request.getRequestDispatcher(ArgumentMatchers.argThat(
        page -> page instanceof String))).thenReturn(dispatcher);

    UserServlet userServlet = new UserServlet();
    UserServlet.us = Mockito.mock(UserService.class);
    userServlet.service(request, response);
    
    assertForward("/msg.jsp", UserServlet.msg4registSucc,
        request, response);
    Mockito.verify(UserServlet.us).add(ArgumentMatchers.argThat(
        e -> (e instanceof User) && ((User) e).getUsername().equals(username)));
  }
  
  @Test
  public void test_regist() throws ServletException, IOException, SvcUnavailable, BusinessNotAllowed, SvcFault, ParamInvalidate, InstantiationException, IllegalAccessException {
    // 分支：userservice抛出异常ParamInvalidate，转发到msg.jsp，提示异常信息。
    _test_userservice_raise_error(ParamInvalidate.class,
        String.format(UserServlet.errMsg4addPI, new User().toMsg()));
  }

  @Test
  public void test_regist1() throws ServletException, IOException, SvcUnavailable, BusinessNotAllowed, SvcFault, ParamInvalidate, InstantiationException, IllegalAccessException {
    // 分支：userservice抛出异常BusinessNotAllowed，转发到msg.jsp，提示异常信息。
    _test_userservice_raise_error(BusinessNotAllowed.class, UserServlet.errMsg4addNA);
  }

  @Test
  public void test_regist2() throws ServletException, IOException, SvcUnavailable, BusinessNotAllowed, SvcFault, ParamInvalidate, InstantiationException, IllegalAccessException {
    // 分支：userservice不可用，转发到msg.jsp，提示异常信息。
    _test_userservice_raise_error(SvcUnavailable.class, UserServlet.serviceUnavailable);
  }

  @Test
  public void test_regist3() throws ServletException, IOException, SvcUnavailable, BusinessNotAllowed, SvcFault, ParamInvalidate, InstantiationException, IllegalAccessException {
    // 分支：userservice故障，转发到msg.jsp，提示异常信息。
    _test_userservice_raise_error(SvcFault.class, UserServlet.serviceFault);
  }

  private <T> void _test_userservice_raise_error(Class<T> errClass, String errMsg) throws InstantiationException, IllegalAccessException, SvcUnavailable, BusinessNotAllowed, SvcFault, ParamInvalidate, ServletException, IOException {
    String method = "regist";
    HttpServletRequest request = genRequest("get", params(method, null));
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    
    UserServlet userServlet = new UserServlet();
    UserServlet.us = genUserService(errClass);
    userServlet.service(request, response);
    
    
    assertForward("/msg.jsp", errMsg, request, response);
    Mockito.verify(UserServlet.us).add(ArgumentMatchers.argThat(
        e -> (e instanceof User)));
  }

  @Test
  public void test_regist4() throws ServletException, IOException, SvcUnavailable, BusinessNotAllowed, SvcFault, ParamInvalidate, InstantiationException, IllegalAccessException {
    // 分支：注册成功，转发到msg.jsp，提示注册成功。
    String username = UUIDUtils.getCode().substring(0, 5);
    _test_regist_succ(username);
  }

  private void _test_regist_succ(String username) throws InstantiationException, IllegalAccessException, SvcUnavailable, BusinessNotAllowed, SvcFault, ParamInvalidate, ServletException, IOException {
    String method = "regist";
    HttpServletRequest request = genRequest("get", params(method, username));
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    
    UserServlet userServlet = new UserServlet();
    UserServlet.us = genUserService(null);
    userServlet.service(request, response);
    
    assertForward("/msg.jsp", UserServlet.msg4registSucc,
        request, response);
    Mockito.verify(UserServlet.us).add(ArgumentMatchers.argThat(
        e -> (e instanceof User) && ((User) e).getUsername().equals(username)));
  }
  private HashMap<String, String[]> params(String method, String username) throws UnsupportedEncodingException {
    HashMap<String, String[]> params = new HashMap<String, String[]>();
    if(method != null)
      params.put("method", new String[] {method});
    if(username != null)
      params.put("username", new String[] {new String(username.getBytes("utf-8"), "ISO-8859-1")});
    return params;
  }
  private void assertForward(String page, String msg,
      HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Mockito.verify(request).setAttribute("msg", msg);
    Mockito.verify(request).getRequestDispatcher(page);
    
    RequestDispatcher dispatcher = request.getRequestDispatcher("");
    
    Mockito.verify(dispatcher).forward(request, response);
  }
  private HttpServletRequest genRequest(String method,
      HashMap<String, String[]> params) {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
  
    Mockito.when(request.getMethod()).thenReturn(method);
    Mockito.when(request.getParameter("method")).thenReturn(params.get("method")[0]);
    Mockito.when(request.getRequestURI()).thenReturn("");
    Mockito.when(request.getParameterMap()).thenReturn(params);
    RequestDispatcher dispatcher = Mockito.mock(RequestDispatcher.class);
    Mockito.when(request.getRequestDispatcher(ArgumentMatchers.argThat(
        page -> page instanceof String))).thenReturn(dispatcher);
    
    return request;
  }
  private <T> UserService genUserService(Class<T> errClass) throws InstantiationException, IllegalAccessException, SvcUnavailable, BusinessNotAllowed, SvcFault, ParamInvalidate {
    UserService us = Mockito.mock(UserService.class);
    if (errClass != null) {
      Mockito.doThrow((Throwable) errClass.newInstance())
        .when(us).add(ArgumentMatchers.argThat(e -> e instanceof User));
    }    
    return us;
  }
}
