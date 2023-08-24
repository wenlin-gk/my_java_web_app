package top.wl.jsp.order;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import top.wl.BaseTest;
import top.wl.dao.OrderDao;
import top.wl.dao.UserDaoTest;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.NoRefencedRow;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.domain.Order;

public class InfoTest extends BaseTest {
  private static Logger log = LogManager.getLogger(InfoTest.class);
  @Test
  public void t() throws DaoUnknownError, DaoUnavailable, PropertyInvalidate, IdNotExist, NoRefencedRow, FileNotFoundException, SQLException {
// case:   订单信息显示准确
    String e_oid = openOrderInfoPage();
    String a_oid = findElement_RetrySeveralTimes(By.id("oid")).getText();
    assertEquals(e_oid, a_oid);
    
    Order o = new OrderDao().getById(e_oid);
    int e_orderitem_size = o.getItems().size();
    int a_orderitem_size = driver.findElements(By.id("item")).size();
    assertEquals(e_orderitem_size, a_orderitem_size);
    
    
// case:   电话收件人地址输入字段选填
    findElement_RetrySeveralTimes(By.id("submit")).click();
    assertCurrentPage("msg.jsp");
    //rollback
    new OrderDao().update(o);
    
    
// case:   电话收件人地址输入字段可用
// case:   支付按钮可用。中文不乱码。
    openOrderInfoPage();
    findElement_RetrySeveralTimes(By.name("name")).sendKeys("中文");
    findElement_RetrySeveralTimes(By.name("address")).sendKeys("中文");
    findElement_RetrySeveralTimes(By.name("telephone")).sendKeys("01234567890");
    findElement_RetrySeveralTimes(By.id("submit")).click();
    assertCurrentPage("msg.jsp");
    //rollback
    new OrderDao().update(o);
  }

  private String openOrderInfoPage() {
    openJsp("order/order_list.jsp");
    assertCurrentPage("order/order_list.jsp");
    findElement_RetrySeveralTimes(By.id("orderItem"));
    List<WebElement> es0 = driver.findElements(By.id("oid"));
    String oid = es0.get(0).getText();
    String url = serverAddr + "/order?method=getById&oid="+oid;
    driver.get(url);
    assertCurrentPage("order/order_info.jsp");
    return oid;
  }
}