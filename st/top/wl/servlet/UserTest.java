package top.wl.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import top.wl.BaseTest;
import top.wl.dao.UserDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.domain.User;
import top.wl.utils.UUIDUtils;
import top.wl.web.servlet.UserServlet;

public class UserTest extends BaseTest {
  private static Logger log = LogManager.getLogger(UserTest.class);
  private static String url = serverAddr + "/user";


  @Test
  public void test_logout() {
// case:   返回index.jsp。处于退出状态（访问/order/list返回msg.jsp）。
    logout();
    assertFalse(is_privilegePage_Accessable());
    
// case:   自动登录cookie被清理。
    genAutoLoginCookie();
    invalidateSession();
    assertTrue(is_privilegePage_Accessable());

    logout();
    assertFalse(is_privilegePage_Accessable());
    
    login();
  }

  private void genAutoLoginCookie() {
    String _url = url+String.format(
        "?method=login&username=%s&password=%s&autologin=1",
        test_user.getUsername(),
        test_user.getPassword());
    log.info(_url);
    driver.get(_url);
  }
  private boolean is_privilegePage_Accessable() {
    driver.get(serverAddr + "/order/order_list.jsp");
    return ! "login.jsp".equals(getCurrentPageID());
  }

  @Test
  public void test_checkUsername() {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（必填字段不存在，如name不存在）时，返回提示页面，提示参数非法。
    String _url = url + "?method=checkUsername";
    assertEquals("0", checkUsername(_url));
    
 // case:   系统正常，参数非法（属性非法，如name过长）时，返回提示页面，提示参数非法。
 // case:   系统正常，参数合法，name不存在时，返回1
    String username = randomStr();
    _url = url + "?method=checkUsername&username="+username;
    assertEquals("1", checkUsername(_url));

    // case:   系统正常，参数合法，name存在时，返回2
    username = test_user.getUsername();
    _url = url + "?method=checkUsername&username="+username;
    assertEquals("2", checkUsername(_url));
  }
  
  private String checkUsername(String _url) {
    driver.get(serverAddr + "/404.jsp");
    assertCurrentPage("404.jsp");
    
    String s_template = "const http = new XMLHttpRequest();\n"
        + "const url='%s';\n" + "http.open('GET', url);\n" + "http.send();\n"
        + "http.onload = (e) => {if(http.status == 200){const input = document.createElement('input'); input.id = 'isExist'; input.value = http.responseText; document.body.appendChild(input);}}";
    
    ((FirefoxDriver) driver).executeScript(String.format(s_template, _url));
    
    return findElement_RetrySeveralTimes(By.id("isExist")).getAttribute("value");
  }

  @Test
  public void register() throws DaoUnavailable {
// case: 系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case: 系统正常，参数非法（必填字段不存在，如name不存在）时，返回提示页面，提示参数非法。
    User _u = new User();
    registUser(_u);
    assertMsg(String.format(UserServlet.errMsg4addPI, _u.toMsg()));
    
 // case: 系统正常，参数非法（属性非法，如name过长）时，返回提示页面，提示参数非法。
    _u = genUser();
    _u.setName(UUIDUtils.getCode());
    registUser(_u);
    assertMsg(String.format(UserServlet.errMsg4addPI, _u.toMsg()));
    
    // case: 系统正常，参数合法，业务不允许，如id已经存在 时，返回提示页面，提示xx已经存在。--忽略
    // case: 系统正常，参数合法，业务允许，返回index.jsp页面。中文不乱码。
    _u = registUser();
    assertMsg(UserServlet.msg4registSucc);
    User a_u = new UserDao().getByUsername(_u.getUsername());
    assertEquals(_u.getPassword(), _u.getPassword());
  }
  @Test
  public void loginUI() throws SQLException, DaoUnavailable {
// case:   如果已经登录，返回msg.jsp
    String params = "?method=loginUI";
    driver.get(url + params);
    assertCurrentPage("msg.jsp");

// case:   如果没有登录，返回login.jsp
    logout();
    driver.get(url + params);
    assertCurrentPage("login.jsp");
  }

  @Test
  public void test_login() {
// case:   系统故障（db故障）时，返回原始页面，提示服务故障。--忽略
// case:   系统正常，参数非法（密码非法超出20个字符）时，返回原始页面，提示参数非法。
    logout();
    String tmp = "?method=login&username=%s&password=%s";
    String password = "11111111111111111111111111111111111111111111111111111111111";
    String params = String.format(tmp, test_user.getUsername(), password);
    driver.get(url + params);
    assertLoginMsg(
        String.format(UserServlet.errMsg4loginPI, test_user.getUsername(), password));

// case:   用户密码错误
    password = "11111111";
    params = String.format(tmp, test_user.getUsername(), password);
    driver.get(url + params);
    assertLoginMsg(UserServlet.errMsg4loginErrorPwd);

// case:   用户未激活
    User _u = registUser();
    assertMsg(UserServlet.msg4registSucc);
    params = String.format(tmp, _u.getUsername(), _u.getPassword());
    driver.get(url + params);
    assertLoginMsg(UserServlet.errMsg4loginInvalidateState);
    deleteUser(_u.getUid());

    login();
  }

  public void assertLoginMsg(String msg) {
    assertCurrentPage("login.jsp");
    assertEquals(msg, driver.findElement(By.id("msg")).getText());
  }

  private User registUser(User _u) {
    StringBuilder params = new StringBuilder("?method=regist");
    if(null!=_u.getUsername())
      params.append("&username="+_u.getUsername());
    if(null!=_u.getPassword())
      params.append("&password="+_u.getPassword());
    if(null!=_u.getEmail())
      params.append("&email="+_u.getEmail());
    if(null!=_u.getName())
      params.append("&name="+_u.getName());
    if(null!=_u.getSex())
      params.append("&sex="+_u.getSex());
    if(null!=_u.getBirthday())
      params.append("&birthday="+_u.getBirthday());
    if(null!=_u.getTelephone())
      params.append("&telephone="+_u.getTelephone());
    
    driver.get(url + params);
    return _u;
  }
  private User registUser() {
    return registUser(genUser());
  }
  private User genUser() {
    User _u = new User();
    _u.setUsername("中文"+UUIDUtils.getId().substring(0, 10));
    _u.setPassword(UUIDUtils.getId().substring(0, 10));
    _u.setEmail(UUIDUtils.getId().substring(0, 10));
    _u.setName(UUIDUtils.getId().substring(0, 10));
    return _u;
  }
}