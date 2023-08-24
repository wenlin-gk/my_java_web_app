package top.wl.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import top.wl.BaseTest4admin;
import top.wl.Const;
import top.wl.dao.OrderDao;
import top.wl.dao.UserDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.domain.Order;
import top.wl.domain.User;
import top.wl.utils.UUIDUtils;
import top.wl.web.servlet.AdminUserServlet;
import top.wl.web.servlet.BaseServlet;

public class AdminUserTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(AdminUserTest.class);
  private static String url = serverAddr + "/admin/user";

  @Test
  public void delete() throws DaoUnavailable {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（uid=null）时，返回提示页面，提示参数非法。
    String params = "?method=delete";
    driver.get(url + params);
    assertMsg(String.format(AdminUserServlet.errMsg4deletePI, null));

// case:   系统正常，参数非法（uid过长）时，返回提示页面，提示参数非法。
    String uid = "11111111111111111111111111111111111111111111111111111";
    params = "?method=delete&uid=" + uid;
    driver.get(url + params);
    assertMsg(String.format(AdminUserServlet.errMsg4deletePI, uid));

// case:   系统正常，参数非法（uid=noexist）时，返回提示页面，提示xx不存在。
    uid = "noexist"+UUIDUtils.getCode().substring(0, 5);
    params = "?method=delete&uid=" + uid;
    driver.get(url + params);
    assertMsg(String.format(AdminUserServlet.errMsg4deleteNA, uid));

// case:   系统正常，参数合法，但是业务不允许，如uid=被订单条目依赖 时，返回提示页面，提示被依赖不允许删除。
    uid = new OrderDao().getAllByState(Order.ORDER_WEIFUKUAN).get(0).getUid();
    params = "?method=delete&uid=" + uid;
    driver.get(url + params);
    assertMsg(String.format(AdminUserServlet.errMsg4deleteNA, uid));

// case:   系统正常，参数合法（uid=从list页面获取）时，返回list.jsp，user删除成功。
    uid = new UserDao().getAll(0, 1).get(0).getUid();
    params = "?method=delete&uid=" + uid;
    driver.get(url + params);
    assertCurrentPage("admin/user/list.jsp");
    assertEquals(null, new UserDao().getByUid(uid));
  }

  @Test
  public void update()
      throws DaoUnavailable, ParseException, InterruptedException,
      FileNotFoundException, SQLException, UnsupportedEncodingException {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
    String params = "?method=update";
    User u = new User();
    driver.get(url + params);
    assertMsg(String.format(AdminUserServlet.errMsg4updatePI, u));

// case:   系统正常，参数非法（id过长）时，返回提示页面，提示参数非法。
    String uid = "11111111111111111111111111111111111111111111111111111";
    params = "?method=update&uid=" + uid;
    u.setUid(uid);
    driver.get(url + params);
    assertMsg(String.format(AdminUserServlet.errMsg4updatePI, u));

// case:   系统正常，参数合法，业务不允许，如uid不存在 时，返回提示页面，提示xx不存在。
    uid = "noexist"+UUIDUtils.getCode().substring(0, 5);
    params = "?method=update&uid=" + uid;
    driver.get(url + params);
    assertMsg(String.format(AdminUserServlet.errMsg4updateNA, uid, null));

// case:   系统正常，参数合法，业务允许，返回list.jsp页面。更新准确。中文不乱码。
    u = new UserDao().getAll(0, 1).get(0);
    User u_v1 = new User(
        u.getUid(), "新名称"+ randomStr(), "password",
        "name", "email@163.com", "12345678901",
        new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
        "man", 0, "1234");
    updateUser(url + "?method=update", u_v1);
    waitPage("admin/user/list.jsp");
    assertCurrentPage("admin/user/list.jsp");
    assertUpdateSucc(u_v1);
    init_db();
  }

  private void assertUpdateSucc(User u) throws DaoUnavailable {
    UserDao d = new UserDao();
    User _u = d.getByUsername(u.getUsername());
    assertNotNull(_u);
    log.info(_u);
    assertTrue(u.equals(_u));
  }

  private void updateUser(String _url, User u) {
    HashMap<String, String> parameters = new HashMap<String, String>(){{
      put("uid", u.getUid());
      put("username", u.getUsername());
      put("password", u.getPassword());
      put("name", u.getName());
      put("email", u.getEmail());
      put("telephone", u.getTelephone());
      put("birthday", u.getBirthday());
      put("sex", u.getSex());
      put("state", u.getState() == null ? null : u.getState().toString());
      put("code", u.getCode());
    }};
    post(_url, parameters, true, null);
  }

  @Test
  public void logut() {
    String params = "?method=logout";
    driver.get(url + params);
    assertCurrentPage("admin/login.jsp");

    driver.get(serverAddr + "/admin/index.jsp");
    assertCurrentPage("admin/login.jsp");

    params = "?method=login&adminName=admin&adminPasswd=" + Const.ADMIN_PASSWD;
    driver.get(url + params);
  }

  @Test
  public void getbypage()
      throws SQLException, FileNotFoundException, DaoUnavailable {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。
// case:   系统正常，参数非法（pageNumber=-1）时，返回提示页面，提示参数非法。
// case:   系统正常，参数合法，2页用户，pageNumber=100超出最大页码时，返回list页面，内容为空，分页为空。
// case:   系统正常，参数合法，2页用户，pageNumber=1时，返回list页面，内容，分页准确。
    clear_db();
    drop_table();
    String params = "?method=getByPage&pageNumber=1";
    driver.get(url + params);
    assertMsg(BaseServlet.serviceUnavailable);
    init_db();

    params = "?method=getByPage&pageNumber=-1";
    driver.get(url + params);
    assertMsg(String.format(AdminUserServlet.errMsg4PageNumInvidate, -1));

    params = "?method=getByPage&pageNumber=100";
    driver.get(url + params);
    assertCurrentPage("admin/user/list.jsp");
    try {
      driver.findElement(By.id("user_item"));
      assertTrue(false);
    } catch (NoSuchElementException e) {
    }
    try {
      driver.findElement(By.id("page_index"));
      assertTrue(false);
    } catch (NoSuchElementException e) {
    }

    params = "?method=getByPage&pageNumber=1";
    driver.get(url + params);
    assertCurrentPage("admin/user/list.jsp");
    int countPerPage = driver.findElements(By.id("user_item")).size();
    int PageCount = Integer
        .parseInt(driver.findElement(By.cssSelector("#page_index > td"))
            .getText().split("/")[1].split("页")[0]);
    driver.findElement(By.linkText("尾页")).click();
    int count4LastPage = driver.findElements(By.id("user_item")).size();
    int expectC = new UserDao().getCount();
    int actualC = countPerPage * (PageCount - 1) + count4LastPage;
    assertEquals(expectC, actualC);

    driver.findElement(By.id("user_item"));
    driver.findElement(By.id("page_index"));
    driver.findElement(By.linkText("首页")).click();
    driver.findElement(By.linkText("尾页")).click();
    driver.findElement(By.linkText("上一页")).click();
    driver.findElement(By.linkText("下一页")).click();
    assertCurrentPage("admin/user/list.jsp");
  }

  @Test
  public void editUI() throws SQLException, DaoUnavailable {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（uid=null）时，返回提示页面，提示参数非法。
// case:   系统正常，参数非法（uid过长）时，返回提示页面，提示参数非法。
// case:   系统正常，参数非法（uid=noexist）时，返回提示页面，提示参数非法。
// case:   系统正常，参数合法（uid=从list页面获取）时，返回edit.jsp，user信息准确。
    String params = "?method=editUI";
    driver.get(url + params);
    assertMsg(String.format(AdminUserServlet.errMsg4editUIPI, null));

    String uid = "11111111111111111111111111111111111111111111111111111";
    params = "?method=editUI&uid=" + uid;
    driver.get(url + params);
    assertMsg(String.format(AdminUserServlet.errMsg4editUIPI, uid));

    uid = "noexist";
    params = "?method=editUI&uid=" + uid;
    driver.get(url + params);
    assertMsg(String.format(AdminUserServlet.errMsg4editUINA, uid));

    User u = new UserDao().getAll(0, 2).get(0);
    uid = u.getUid();
    params = "?method=editUI&uid=" + uid;
    driver.get(url + params);
    assertCurrentPage("admin/user/edit.jsp");

    String username = driver.findElement(By.name("username"))
        .getAttribute("value");
    String password = driver.findElement(By.name("password"))
        .getAttribute("value");
    String name = driver.findElement(By.name("name")).getAttribute("value");
    String email = driver.findElement(By.name("email")).getAttribute("value");
    String telephone = driver.findElement(By.name("telephone"))
        .getAttribute("value");
    assertEquals(u.getUsername(), username);
    assertEquals(u.getPassword(), password);
    assertEquals(u.getName(), name);
    assertEquals(u.getEmail(), email);
    assertEquals(u.getTelephone(), telephone);
  }

}