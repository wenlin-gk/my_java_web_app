package top.wl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import top.wl.dao.CategoryDao;
import top.wl.dao.OrderDao;
import top.wl.dao.ProductDao;
import top.wl.dao.UserDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.dao.utils.MyQueryRunner;
import top.wl.domain.Category;
import top.wl.domain.Product;
import top.wl.domain.User;
import top.wl.utils.UUIDUtils;
import top.wl.web.servlet.OrderServlet;
import top.wl.web.servlet.ProductServlet;

public class BaseTest {
  public static final String serverAddr = "http://localhost:8080/my_store";

  private static Logger log = LogManager.getLogger(BaseTest.class);
  public static WebDriver driver;

  private static MyQueryRunner qr = new MyQueryRunner();
  public static User test_user = null;
  public static boolean setUpFlag = true;
  public static boolean tearDownFlag = true;
  
  @BeforeClass
  public static void setUpClass() {
    if(setUpFlag) {
      setUp_testSystem();
      setUp_webServer();
      test_user = initTestUser();
    }
    login(test_user);
  }
  public static User initTestUser() {
    UserDao ud = new UserDao();
    OrderDao od = new OrderDao();
    List<User> us = null;
    User targetUser = null;
    try {
      us = ud.getAll(0, 100);
      for(User _u: us) {
        if(od.getTotalRecord(_u.getUid())>OrderServlet.pageSize) {
          targetUser = _u;
          break;
        }
      }
      assertNotNull(targetUser);
    } catch (DaoUnavailable e) {
      assertTrue(false);
    }
    return targetUser;
  }

  public static void setUp_webServer() {
    log.info("Web server已经提前部署了。");
  }

  public static void setUp_testSystem() {
    FirefoxOptions options = new FirefoxOptions();
    options.addPreference("webdriver.gecko.driver",
        System.getProperty("user.dir") + "/st/geckodriver");
    driver = new FirefoxDriver(options);
  }

  @AfterClass
  public static void tearDownClass() {
    logout();
    if(tearDownFlag) {
      tearDown_webServer();
      tearDown_testSystem();
    }
  }

  protected static void tearDown_testSystem() {
    driver.quit();
  }

  protected static void tearDown_webServer() {
    log.info("请手动关闭web server.");
  }

  public static void invalidateSession() {
    driver.get(serverAddr + "/user?method=invalidateSession4test");
    assertCurrentPage("msg.jsp");
  }

  public static String randomStr() {
    return UUIDUtils.getCode().substring(0, 5);
  }

  public static void assertCurrentPage(int frame, String returnPageId) {
    driver.switchTo().defaultContent();
    
    if(frame != -1)
      driver.switchTo().frame(frame);
    
    assertCurrentPage(returnPageId);
  }

  public static void login() {
    login(test_user);
  }

  public static void login(User u) {
    String url = serverAddr + "/user";
    String params = String.format(
        "?method=login&username=%s&password=%s",
        u.getUsername(), u.getPassword());
    driver.get(url + params);
  }

  public static void logout() {
    driver.get(serverAddr + "/user?method=logout");
  }

  public static void adminLogin() {
    driver.get(serverAddr + "/admin/user"
  +"?method=login&adminName=admin&adminPasswd=" + Const.ADMIN_PASSWD);
  }

  public static void adminLogout() {
    driver.get(serverAddr + "/admin/user?method=logout");
  }

  public String openProductList_where_page_count_greater_than_1() throws DaoUnavailable {
    driver.switchTo().defaultContent();
    driver.switchTo().frame(0);
    List<WebElement> es = driver.findElements(By.id("category_item"));
    List<Category> cs = new CategoryDao().getAll();
    for(WebElement e: es) {
      String cname = e.getText();
      String cid = getCid(cname, cs);
      int e_pageCount = getPageCount(cid);
      if(e_pageCount>1) {
        e.click();
        driver.switchTo().defaultContent();
        driver.switchTo().frame(1);
        return cid;
      }
    }
    assertTrue(false);
    return null;
  }

  public String getCid(String cname, List<Category> cs) {
    for(Category c: cs) {
      if(cname.equals(c.getCname())) {
        return c.getCid();
      }
    }
    assertTrue(false);
    return null;
  }

  public int getPageCount(String cid) throws DaoUnavailable {
    int total = new ProductDao().getTotalRecord(cid);
    return (int) Math.ceil(total * 1.0 / ProductServlet.pageSize);
  }
  public static void deleteUser(String uid) {
    driver.get(
        serverAddr + "/admin/user?method=login&adminName=admin&adminPasswd="
            + Const.ADMIN_PASSWD);

    String params = "?method=delete&uid=" + uid;
    driver.get(serverAddr + "/admin/user" + params);
    
    driver.get(serverAddr + "/admin/user?method=logout");
  }

  public static void openJsp(String page) {
    driver.switchTo().defaultContent();
    driver.get(serverAddr + "/index.jsp");
    
    if( ! "index.jsp".equals(page))
      _openJsp(page);
  }

  private static void _openJsp(String page) {
    switch (page) {
    case "login.jsp":
      driver.switchTo().frame(0);
      driver.findElement(By.linkText("登录")).click();
      driver.switchTo().defaultContent();

      driver.switchTo().frame(1);
      break;
    case "register.jsp":
      driver.switchTo().frame(0);
      driver.findElement(By.linkText("注册")).click();
      driver.switchTo().defaultContent();

      driver.switchTo().frame(1);
      break;
    case "cart.jsp":
      driver.switchTo().frame(0);
      driver.findElement(By.linkText("购物车")).click();
      driver.switchTo().defaultContent();

      driver.switchTo().frame(1);
      break;
    case "order/order_list.jsp":
      driver.switchTo().frame(0);
      driver.findElement(By.linkText("我的订单")).click();
      driver.switchTo().defaultContent();

      driver.switchTo().frame(1);
      break;
      
    default:
      throw new UnknownError();
    }
  }

  public static void init_db() throws SQLException, FileNotFoundException {
    Connection c = DataSourceUtils.getConnection();
    String sqlPath = System.getProperty("user.dir") + "/deployment/init_db.sql";
    InputStream in = new FileInputStream(sqlPath);

    try {
      importSQL(c, in);
    } finally {
      try {
        in.close();
      } catch (IOException e) {
      }
      DataSourceUtils.releaseConnection(c);
    }
    // wait health.
  }

  public static void importSQL(Connection conn, InputStream in)
      throws SQLException {
    Scanner s = new Scanner(in);
    s.useDelimiter("(;(\r)?\n)|(--\n)");
    Statement st = null;
    try {
      st = conn.createStatement();
      while (s.hasNext()) {
        String line = s.next();
        if (line.startsWith("/*!") && line.endsWith("*/")) {
          int i = line.indexOf(' ');
          line = line.substring(i + 1, line.length() - " */".length());
        }
        if (line.trim().length() > 0) {
          st.execute(line);
        }
      }
    } finally {
      if (st != null)
        st.close();
    }
  }

  public void clear_db() throws SQLException {
    String[] sqls = new String[] { "delete from orderitem",
        "delete from orders", "delete from product", "delete from category",
        "delete from user" };
    Connection c = DataSourceUtils.getConnection();
    try {
      for (String sql : sqls)
        qr.update4Transaction(c, sql);
    } finally {
      DataSourceUtils.releaseConnection(c);
    }
  }

  public void drop_table() throws SQLException {
    String[] sqls = new String[] { "DROP TABLE IF EXISTS `orderitem`;",
        "DROP TABLE IF EXISTS `orders`;", "DROP TABLE IF EXISTS `user`;" };
    Connection c = DataSourceUtils.getConnection();
    try {
      for (String sql : sqls)
        qr.update4Transaction(c, sql);
    } finally {
      DataSourceUtils.releaseConnection(c);
    }
  }

  public void assertMsg(String msg) {
    assertCurrentPage("msg.jsp");
    assertEquals(msg, driver.findElement(By.cssSelector("h3")).getText());
  }

  public static void waitPage(String page_id) {
    int attempt = 1;
    while (attempt < 100) {
      log.info(attempt);
      log.info(driver.getCurrentUrl());
      try {
        String id = driver.findElement(By.xpath("/html")).getAttribute("id");
        log.info(id);
        if (page_id.equals(id))
          break;
        else
          Thread.sleep(10);
      } catch (NoSuchElementException | StaleElementReferenceException
          | InterruptedException err) {
        log.error(err.getMessage());
      }
      attempt++;
    }

  }

  public static WebElement findElement_RetrySeveralTimes(By by, int interval) {
    // 避免窗口未刷新
    try {
      Thread.sleep(interval);
    } catch (InterruptedException e1) {
    }

    int attempts = 0;
    WebElement ele = null;
    while (attempts < 20) {
      try {
        ele = driver.findElement(by);
        break;
      } catch (NoSuchElementException | StaleElementReferenceException e) {
        attempts += 1;
        try {
          Thread.sleep(interval);
        } catch (InterruptedException e1) {
        }
        log.info(attempts);
      }
    }
    // assertTrue(attempts < 20);
    return ele;
  }

  public static WebElement findElement_RetrySeveralTimes(By by) {
    return findElement_RetrySeveralTimes(by, 10);
  }

  public static WebElement findElement(By by) {
    try {
      return driver.findElement(by);
    } catch (NoSuchElementException e) {
      return null;
    }
  }

  public void get(String url, HashMap<String, String> params) {
    call(url, params, true, "get", null);
  }

  public void post(String url, HashMap<String, String> params) {
    call(url, params, false, "post", "multipart/form-data");
  }

  public void post(String url, HashMap<String, String> params,
      Boolean submit) {
    call(url, params, submit, "post", "multipart/form-data");
  }

  public void post(String url, HashMap<String, String> params,
      Boolean submit, String enctype) {
    call(url, params, submit, "post", enctype);
  }

  public void call(String url, HashMap<String, String> params,
      Boolean submit, String method, String enctype) {
    log.info(params);
    
    StringBuilder sb = new StringBuilder();
    
    sb.append("const form = document.createElement('form');\n"
        + "form.method = '" + method + "';\n");
    if(enctype != null) {
      sb.append("form.enctype = '" + enctype + "';\n");
    }
    sb.append(String.format("form.action='%s';\n", url)
        + "document.body.appendChild(form);\n");
    
    for (Entry<String, String> e : params.entrySet()) {
      if (null == e.getValue())
        continue;
      String name = e.getKey().split(":")[0];
      String type = e.getKey().split(":").length == 2 ? e.getKey().split(":")[1]
          : "hidden";
      String value = e.getValue();
      if ("file".equals(type)) {
        sb.append(String.format("const %s = document.createElement('input');\n"
            + "%s.type = '" + type + "';\n" + "%s.name = '" + name + "';\n"
            + "form.appendChild(%s);\n", name, name, name, name));
      } else {
        sb.append(String.format(
            "const %s = document.createElement('input');\n" + "%s.type = '"
                + type + "';\n" + "%s.name = '" + name + "';\n" + "%s.value = '"
                + value + "';\n" + "form.appendChild(%s);\n",
            name, name, name, name, name));
      }
    }
    if (submit)
      sb.append("form.submit();");
    else {
      sb.append("const button = document.createElement('button');\n"
          + "button.type = 'submit';\n" + "button.id = 'submit';\n"
          + "form.appendChild(button);\n");
    }
    log.info(sb.toString());
    
    driver.get(serverAddr + "/404.jsp");
    assertCurrentPage("404.jsp");
    ((FirefoxDriver) driver).executeScript(sb.toString());
  }

  public static void assertCurrentPage(String page_id) {
    assertEquals(page_id, findElement_RetrySeveralTimes(By.xpath("/html")).getAttribute("id"));
  }

  public static String getCurrentPageID() {
    return findElement_RetrySeveralTimes(By.xpath("/html")).getAttribute("id");
  }
 
  public static void assertCurrentPage(String page_id0, String page_id1) {
    String id = findElement_RetrySeveralTimes(By.xpath("/html")).getAttribute("id");
    assertTrue(page_id0.equals(id) || page_id1.equals(id));
  }

  public void fillProperties(HashMap<String, String> parameters, Product p) {
    if (null != p.getPid())
      parameters.put("pid", p.getPid());
    parameters.put("pname", p.getPname());
    if (null != p.getMarket_price())
      parameters.put("market_price", p.getMarket_price().toString());
    if (null != p.getShop_price())
      parameters.put("shop_price", p.getShop_price().toString());
    parameters.put("pimage:file", p.getPimage());
    parameters.put("pdate", p.getPdate());
    if (null != p.getIs_hot())
      parameters.put("is_hot", p.getIs_hot().toString());
    parameters.put("pdesc", p.getPdesc());
    if (null != p.getPflag())
      parameters.put("pflag", p.getPflag().toString());
    parameters.put("cid", p.getCid());
  }

  public Product genNewProduct(Product p, String pimage) {
    Product _p = new Product();
    _p.setPid(p.getPid());
    _p.setCid(p.getCid());
    _p.setIs_hot(p.getIs_hot());
    _p.setMarket_price(p.getMarket_price() + 1);
    _p.setPdate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    _p.setPdesc(
        p.getPdesc().substring(0, 20) + UUIDUtils.getCode().substring(0, 5));
    _p.setPflag(p.getPflag());
    _p.setPimage(pimage);
    if (p.getPname().length() > 10)
      _p.setPname(
          p.getPname().substring(0, 10) + "中文"+UUIDUtils.getCode().substring(0, 5));
    else
      _p.setPname(p.getPname() + "中文" + UUIDUtils.getCode().substring(0, 5));
    _p.setShop_price(p.getShop_price() + 1);
    return _p;
  }

  public Product getNotReferenceProduct() throws DaoUnavailable {
    List<Product> ps = new ProductDao().getAllByPflag();
    OrderDao od = new OrderDao();
    for (Product p : ps) {
      if (0 == od.getOrderItemCountByPid(p.getPid()))
        return p;
    }
    return null;
  }

  public Product addProdcut() throws DaoUnavailable, SQLException,
      IsReferenced, IdNotExist, DaoUnknownError {
    String _url = serverAddr + "/admin/product?method=add";
    HashMap<String, String> parameters = new HashMap<String, String>();
  
    Product p = new ProductDao().getAllByHot().get(0);
    String pimage = System.getProperty("user.dir") + "/st/product_test.jpg";
    Product new_p = genNewProduct(p, pimage);
    p.setPid(null);
    log.info(new_p);
    fillProperties(parameters, new_p);

    post(_url, parameters);
    log.info(driver.getPageSource());
    driver.findElement(By.name("pimage")).sendKeys(new_p.getPimage());
    driver.findElement(By.id("submit")).click();
  
    waitPage("admin/product/list.jsp");
    assertCurrentPage("admin/product/list.jsp");
    return new_p;
  }

}