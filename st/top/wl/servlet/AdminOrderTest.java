package top.wl.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import top.wl.BaseTest4admin;
import top.wl.dao.CategoryDao;
import top.wl.dao.OrderDao;
import top.wl.dao.ProductDao;
import top.wl.dao.UserDaoTest;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.domain.Category;
import top.wl.domain.Order;
import top.wl.domain.OrderItem;
import top.wl.domain.PageBean;
import top.wl.domain.Product;
import top.wl.utils.JsonUtil;
import top.wl.utils.UUIDUtils;
import top.wl.web.servlet.AdminCategoryServlet;
import top.wl.web.servlet.AdminOrderServlet;
import top.wl.web.servlet.AdminProductServlet;

public class AdminOrderTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(AdminOrderTest.class);
  private static final String url = serverAddr + "/admin/order";

  @Test
  public void list()
      throws FileNotFoundException, SQLException, DaoUnavailable {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（state=-1）时，返回提示页面，提示参数非法。
    String params = "?method=getAllByState&state=-1";
    driver.get(url + params);
    assertMsg(String.format(AdminOrderServlet.errMsg4getallPI, -1));

// case:   系统正常，参数合法，无订单，返回list页面，内容为空，分页为空。
    params = "?method=getAllByState&state=" + Order.ORDER_YIFAHUO;
    driver.get(url + params);
    assertCurrentPage("admin/order/list.jsp");
    assertNull(findElement(By.id("order_item")));

// case:   系统正常，参数合法，有订单，返回list页面，内容，分页准确。
    params = "?method=getAllByState&state=" + Order.ORDER_WEIFUKUAN;
    driver.get(url + params);
    assertCurrentPage("admin/order/list.jsp");
    assertEquals(new OrderDao().getAllByState(Order.ORDER_WEIFUKUAN).size(),
        driver.findElements(By.id("order_item")).size());
  }
  
  @Test
  public void updateState() throws DaoUnavailable, DaoUnknownError,
      FileNotFoundException, SQLException {
    // case: 系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
    // case: 系统正常，参数非法（id过长）时，返回提示页面，提示参数非法。
    String oid = "11111111111111111111111111111111111111111111111111111";
    String params = "?method=updateState&oid=" + oid;
    driver.get(url + params);
    assertMsg(String.format(AdminOrderServlet.errMsg4updatePI, oid));

    // case: 系统正常，参数非法（id不存在）时，返回提示页面，提示参数非法。
    params = "?method=updateState";
    driver.get(url + params);
    assertMsg(String.format(AdminOrderServlet.errMsg4updatePI, null));

    // case: 系统正常，参数合法，业务不允许，如id不存在 时，返回提示页面，提示xx不存在。
    oid = UUIDUtils.getId();
    params = "?method=updateState&oid=" + oid;
    driver.get(url + params);
    assertMsg(String.format(AdminOrderServlet.errMsg4updateNA, oid));

    // case: 系统正常，参数合法，业务允许，返回list.jsp页面。
    oid = new OrderDao().getAllByState(Order.ORDER_YIFUKUAN).get(0).getOid();
    params = "?method=updateState&oid=" + oid;
    driver.get(url + params);
    assertCurrentPage("admin/order/list.jsp");
    assertTrue(
        new OrderDao().getById(oid).getState().equals(Order.ORDER_YIFAHUO));
    init_db();
  }

  @Test
  public void getInJson()
      throws DaoUnavailable, InterruptedException, DaoUnknownError {
// case: 系统正常，参数非法（oid=null），返回空。
    String _url = url + "?method=getInJson";
    String orderItems = request(_url);
    assertEquals("[null]", orderItems);

// case: 系统正常，参数非法（oid不存在），返回空。
// case: 系统正常，参数合法，订单条目为空，返回空。
// case: 系统正常，参数合法，订单条目非空，返回订单条目准确。
    Order expectOrder = getOrder_where_state_eq_WeiFuKuan_and_item_count_gt_1();

    _url = url + "?method=getInJson&oid=" + expectOrder.getOid();
    orderItems = request(_url);
    JSONArray items = JSONArray.fromObject(orderItems.toString());
    assertItemsEqual(expectOrder.getItems(), items);
  }

  private Order getOrder_where_state_eq_WeiFuKuan_and_item_count_gt_1() throws DaoUnavailable, DaoUnknownError {
    List<Order> os = new OrderDao().getAllByState(Order.ORDER_WEIFUKUAN);
    Order expectOrder = null;
    for (Order _o : os) {
      Order o2 = new OrderDao().getById(_o.getOid());
      if (o2.getItems().size() > 1) {
        expectOrder = o2;
      }
    }
    return expectOrder;
  }

  private String request(String _url) {
    driver.get(serverAddr + "/404.jsp");
    assertCurrentPage("404.jsp");
    String s_template = "const http = new XMLHttpRequest();\n"
        + "const url='%s';\n" + "http.open('GET', url);\n" + "http.send();\n"
        + "http.onload = (e) => {if(http.status == 200){const input = document.createElement('input'); input.id = 'orderInfo'; input.value = http.responseText; document.body.appendChild(input);}}";
    ((FirefoxDriver) driver).executeScript(String.format(s_template, _url));
    return findElement_RetrySeveralTimes(By.id("orderInfo")).getAttribute("value");
  }

  private void assertItemsEqual(List<OrderItem> expect_items, JSONArray ja) {
    assertEquals(expect_items.size(), ja.size());
    for (Object j : ja) {
      JSONObject jo = JSONObject.fromObject(j);
      OrderItem actual_item = (OrderItem) JSONObject.toBean(jo,
          OrderItem.class);
      boolean isFind = false;
      for (OrderItem expect_item : expect_items) {
        if (expect_item.getItemid().equals(actual_item.getItemid())) {
          isFind = true;
          log.info(actual_item);
          log.info(expect_item);
          assertTrue(isEquals(expect_item, actual_item));
        }
      }
      assertTrue(isFind);
    }
    log.info(expect_items);
  }

  private boolean isEquals(OrderItem e, OrderItem a) {
    return e.getCount().equals((a.getCount()))
        && e.getItemid().equals(a.getItemid())
        && e.getSubtotal().equals(a.getSubtotal())
        && e.getProduct().equalsIgnorePdatePdesc(a.getProduct());
  }
}