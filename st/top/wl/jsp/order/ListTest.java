package top.wl.jsp.order;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;

import top.wl.BaseTest;

public class ListTest extends BaseTest {
  private static Logger log = LogManager.getLogger(ListTest.class);

  @Test
  public void t() {
// case:   订单更新链接有效
    openJsp("order/order_list.jsp");
    assertCurrentPage("order/order_list.jsp");

    driver.findElement(By.id("orderInfo")).click();
    assertCurrentPage("order/order_info.jsp");
  }
}