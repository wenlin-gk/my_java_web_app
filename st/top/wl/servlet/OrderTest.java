package top.wl.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import top.wl.BaseTest;
import top.wl.dao.OrderDao;
import top.wl.dao.ProductDao;
import top.wl.dao.UserDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.domain.Order;
import top.wl.domain.PageBean;
import top.wl.domain.Product;
import top.wl.domain.User;
import top.wl.utils.UUIDUtils;
import top.wl.web.servlet.OrderServlet;

public class OrderTest extends BaseTest {
  private static Logger log = LogManager.getLogger(OrderTest.class);
  private static String url = serverAddr + "/order";
  private static OrderDao od = new OrderDao();

  @Test
  public void getById() throws DaoUnknownError, DaoUnavailable {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
    String paramsTempl = "?method=getById";
    driver.get(url + paramsTempl);
    assertMsg(String.format(OrderServlet.errMsg4getPI, null));

    // case: 系统正常，参数非法（id=""）时，返回提示页面，提示参数非法。
    paramsTempl = "?method=getById&oid=%s";
    String oid = UUIDUtils.getCode() + UUIDUtils.getCode();
    driver.get(url + String.format(paramsTempl, oid));
    assertMsg(String.format(OrderServlet.errMsg4getPI, oid));

    // case: 系统正常，参数非法，业务允许（id="noexist"）时，返回提示页面。
    oid = UUIDUtils.getCode();
    driver.get(url + String.format(paramsTempl, oid));
    assertMsg(String.format(OrderServlet.errMsg4updateNA, oid));

    // case: 系统正常，参数合法，业务允许，返回/order/info.jsp页面。
    List<Order> os = new OrderDao().getAllByPage(new PageBean<Order>(1, 3),
        test_user.getUid());
    assertTrue(os.size() > 0);
    Order o = os.get(0);
    driver.get(url + String.format(paramsTempl, o.getOid()));

    assertCurrentPage("order/order_info.jsp");
    assertEquals(o.getOid(),
        findElement_RetrySeveralTimes(By.id("oid")).getText());

  }

  @Test
  public void getAllByPage() throws DaoUnavailable {
// case: 系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case: 系统正常，参数非法（pageNumber=-1）时，返回提示页面，提示参数非法。
    String paramsTempl = "?method=getAllByPage&pageNumber=%s";
    driver.get(url + String.format(paramsTempl, -1));
    assertMsg(String.format(OrderServlet.errMsg4PageNumInvidate, -1));

    // case: 系统正常，参数合法，2页订单，pageNumber=100超出最大页码时，返回list页面，内容为空，分页为空。
    paramsTempl = "?method=getAllByPage&pageNumber=%s";
    driver.get(url + String.format(paramsTempl, 100));
    assertCurrentPage("order/order_list.jsp");
    assertNull(findElement(By.id("orderItem")));
    assertNull(findElement(By.id("page_index")));

    // case: 系统正常，参数合法，2页订单，pageNumber=1时，返回list页面，内容，分页准确。
    int count = new OrderDao().getTotalRecord(test_user.getUid());
    assertTrue(count > 0);
    paramsTempl = "?method=getAllByPage&pageNumber=%s";
    driver.get(url + String.format(paramsTempl, 1));
    assertCurrentPage("order/order_list.jsp");
    int a_count = driver.findElements(By.id("orderItem")).size();
    if (OrderServlet.pageSize > count)
      assertEquals(count, a_count);
    else
      assertEquals(OrderServlet.pageSize, a_count);
    a_count = driver.findElements(By.id("page")).size();
    int e_count = (int) Math.ceil(count * 1.0 / OrderServlet.pageSize);
    assertEquals(e_count, a_count);
  }

  @Test
  public void pay()
      throws DaoUnknownError, DaoUnavailable, UnsupportedEncodingException {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法，如oid=null 时，返回提示页面，提示异常。
    String params = "?method=pay";
    driver.get(url + params);
    Order o = new Order();
    assertMsg(String.format(OrderServlet.errMsg4updatePI, o));

// case:   系统正常，参数非法（order属性非法，如name过长）时，返回提示页面，提示参数非法。
    o = new OrderDao()
        .getAllByPage(new PageBean<Order>(1, 1), test_user.getUid()).get(0);
    String o_name = o.getName();
    String o_oid = o.getOid();
    o.setName(UUIDUtils.getCode());
    updateOrder(o);
    assertMsg(String.format(OrderServlet.errMsg4updatePI, revise4update(o)));

// case:   系统正常，参数合法，业务不允许，如oid不存在 时，返回提示页面，提示异常。
    o = new OrderDao()
        .getAllByPage(new PageBean<Order>(1, 1), test_user.getUid()).get(0);
    o.setName(UUIDUtils.getCode().substring(0, 5));
    o.setOid(UUIDUtils.getCode().substring(0, 5));
    updateOrder(o);
    assertMsg(String.format(OrderServlet.errMsg4updateNA, revise4update(o)));

// case:   系统正常，参数合法，业务不允许，如uid不匹配 时，返回提示页面，提示异常。
    o = new OrderDao()
        .getAllByPage(new PageBean<Order>(1, 1), test_user.getUid()).get(0);
    o.setName(o_name);
    o.setOid(o_oid);
    List<User> us = new UserDao().getAll(1, 2);
    int index = us.get(0).getUid().equals(test_user.getUid()) ? 1 : 0;
    o.setUid(us.get(index).getUid());
    updateOrder(o);
    assertMsg(String.format(OrderServlet.errMsg4updateNA, revise4update(o)));
    o.setUid(test_user.getUid());

// case:   系统正常，参数合法，业务不允许，如state不处于待支付 时，返回提示页面，提示异常。
    o = new OrderDao()
        .getAllByPage(new PageBean<Order>(1, 1), test_user.getUid()).get(0);
    o = getOrderInState(Order.ORDER_YIFUKUAN);
    updateOrder(o);
    assertMsg(String.format(OrderServlet.errMsg4updateNA, revise4update(o)));

// case:   系统正常，参数合法，业务允许，提示支付成功页面。各个字段更新准确。中文不乱码。
    o = new OrderDao()
        .getAllByPage(new PageBean<Order>(1, 1), test_user.getUid()).get(0);
    o = getOrderInState(Order.ORDER_WEIFUKUAN);
    o.setAddress("中文" + UUIDUtils.getCode().substring(0, 5));
    o.setName("中文" + UUIDUtils.getCode().substring(0, 5));
    o.setTelephone("12345678910");
    updateOrder(o);
    Order a_o = od.getById(o.getOid());
    o.setState(Order.ORDER_YIFUKUAN);
    assertTrue(o.equals(a_o));
  }

  private Object revise4update(Order o) {
    o.setAddress(null);
    o.getItems().clear();
    o.setTotal(null);
    o.setUid(null);
    o.setUser(null);
    o.setOrdertime(null);
    return o;
  }

  private Order getOrderInState(int s) throws DaoUnavailable {
    for (Order o : new OrderDao().getAllByState(s)) {
      if (o.getUid().equals(test_user.getUid()))
        return o;
    }
    assertTrue(false);
    return null;
  }

  private void updateOrder(Order o) throws UnsupportedEncodingException {
    String params = "?method=pay";
    if (o.getOid() != null) {
      params += "&oid=" + o.getOid();
    }
    if (o.getName() != null) {
      params += "&name=" + o.getName();
    }
    if (o.getTelephone() != null) {
      params += "&telephone=" + o.getTelephone();
    }
    if (o.getState() != null) {
      params += "&state=" + o.getState();
    }
    if (o.getAddress() != null) {
      params += "&address=" + o.getAddress();
    }
    driver.get(url + params);
  }

  @Test
  public void add() throws DaoUnavailable, SQLException, IsReferenced,
      IdNotExist, DaoUnknownError {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，购物车为空时，返回提示页面，提示异常信息。
    String params = "?method=save";
    driver.get(url + params);
    assertMsg(OrderServlet.errMsg4CartEmpty);

// case:   系统正常，商品不存在 时，返回提示页面，提示异常信息。订单未生成。
    adminLogin();
    addProduct_add2Cart_deleteProduct();
    driver.get(url + params);
    assertCurrentPage("msg.jsp");
    WebElement e = driver.findElement(By.cssSelector("h3"));
    assertTrue(
        e.getText().startsWith(OrderServlet.errMsg4addNA.substring(0, 23)));
    adminLogout();
    login();

// case:   系统正常，参数合法，业务允许，返回/order/info.jsp页面。用户新增一个订单，购物车清空。
    adminLogin();
    
    int count = od.getTotalRecord(test_user.getUid());
    addProduct_add2Cart_deleteProduct(false);
    driver.get(url + params);
    log.info(driver.getPageSource());
    assertCurrentPage("order/order_info.jsp");
    String oid = findElement_RetrySeveralTimes(By.id("oid")).getText();
    assertEquals(count + 1, od.getTotalRecord(test_user.getUid()));
    driver.get(serverAddr + "/cart.jsp");
    assertNull(findElement(By.id("cartItem")));
    int affect = od.delete(oid);
    assertEquals(1, affect);
    
    adminLogout();
    login();
  }

  private void addProduct_add2Cart_deleteProduct() throws DaoUnavailable,
      SQLException, IsReferenced, IdNotExist, DaoUnknownError {
    addProduct_add2Cart_deleteProduct(true);
  }

  private void addProduct_add2Cart_deleteProduct(boolean delete)
      throws DaoUnavailable, SQLException, IsReferenced, IdNotExist,
      DaoUnknownError {
    int count = 2;
    for (int i = 0; i < count; i++) {
      Product p = addProdcut();
      String templ = serverAddr + "/cart" + "?method=put&pid=%s&count=%s";
      String url = String.format(templ, p.getPid(), 2);
      driver.get(url);
      assertCurrentPage("cart.jsp");
      if (delete)
        new ProductDao().delete(p.getPid());
    }
  }
}