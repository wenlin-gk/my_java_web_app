package top.wl.jsp.product;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import top.wl.BaseTest;
import top.wl.dao.error.DaoUnavailable;

public class InfoTest extends BaseTest {
  private static Logger log = LogManager.getLogger(InfoTest.class);

  @Test
  public void t() throws DaoUnavailable {
    openProductList_where_page_count_greater_than_1();
    assertCurrentPage("product_list.jsp");
    List<WebElement> es4product = driver.findElements(By.id("product_item"));
    es4product.get(0).click();
    assertCurrentPage("product_info.jsp");
    
// case:   数量输入框可用
    findElement_RetrySeveralTimes(By.id("quantity")).clear();
    findElement_RetrySeveralTimes(By.id("quantity")).sendKeys("2");
// case:   提交按钮可用
    findElement_RetrySeveralTimes(By.id("add2cart")).click();
    assertCurrentPage("cart.jsp");

    assertEquals(driver.findElements(By.id("cartItem")).size(), 1);
    assertEquals(driver.findElement(By.name("quantity")).getAttribute("value"), "2");
  }
}