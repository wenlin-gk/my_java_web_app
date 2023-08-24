package top.wl.jsp.admin.order;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import top.wl.BaseTest4admin;

public class ListTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(ListTest.class);

  @Test
  public void t() throws FileNotFoundException, SQLException {
// case:   order的update链接可用
    openAdminJsp("order/list.jsp");
    assertNull(getOrderInYiFaHua());
    
    openAdminJsp("order/list.jsp");
    driver.findElement(By.linkText("去发货")).click();
    assertCurrentPage("admin/order/list.jsp");
    assertNotNull(getOrderInYiFaHua());

// case:   order的详情链接可用（订单条目显示准确）
    assertNull(getOrderDetail());
    driver.findElement(By.xpath("//input[@value=\'订单详情\']")).click();
    assertNotNull(getOrderDetail());
    init_db();
  }

  private Object getOrderDetail() {
    try {
      return driver.findElement(By.cssSelector(".layui-layer-content"));
    } catch (NoSuchElementException e) {
      return null;
    }
  }

  private Object getOrderInYiFaHua() {
    driver.switchTo().defaultContent();
    driver.switchTo().frame(1);
    driver.findElement(By.id("sd13")).click();
    driver.switchTo().defaultContent();
    driver.switchTo().frame(2);
    try {
      return driver.findElement(By.id("order_item"));
    } catch (NoSuchElementException e) {
      return null;
    }
  }

}