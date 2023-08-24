package top.wl.jsp.admin.product;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;

import top.wl.BaseTest4admin;

public class ListTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(ListTest.class);

  @Test
  public void t() throws FileNotFoundException, SQLException {
// case:     product的编辑链接可用
    openAdminJsp("product/list.jsp");
    click("edit");
    assertCurrentPage("admin/product/edit.jsp");

// case:     product的删除链接可用
    openAdminJsp("product/list.jsp");
    click("delete");
    assertCurrentPage("admin/product/list.jsp", "msg.jsp");
    init_db();
  }
  private void click(String target) {
    driver.switchTo().defaultContent();
    
    switch (target) {
    case "edit":
      driver.switchTo().frame(2);
      findElement_RetrySeveralTimes(By.xpath("//tr[@id=\'product_item\']/td[6]/a/img")).click();
      break;
    case "delete":
      driver.switchTo().frame(2);
      findElement_RetrySeveralTimes(By.xpath("//tr[@id=\'product_item\']/td[7]/a/img")).click();
      break;
    default:
      throw new UnknownError();
    }
  }

}