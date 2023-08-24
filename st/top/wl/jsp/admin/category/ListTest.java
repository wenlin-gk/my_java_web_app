package top.wl.jsp.admin.category;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import top.wl.BaseTest4admin;

public class ListTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(ListTest.class);

  @Test
  public void t() throws FileNotFoundException, SQLException {
// case:   category的编辑链接可用
    openAdminJsp("category/list.jsp");
    click("edit");
    assertCurrentPage("admin/category/edit.jsp");

// case:   category的删除链接可用
    openAdminJsp("category/list.jsp");
    click("delete");
    assertCurrentPage("admin/category/list.jsp", "msg.jsp");
    init_db();
  }

  private void click(String target) {
    driver.switchTo().defaultContent();
    
    switch (target) {
    case "edit":
      driver.switchTo().frame(2);
      findElement_RetrySeveralTimes(By.xpath("//tr[@id=\'category_item\']/td[3]/a/img")).click();
      break;
    case "delete":
      driver.switchTo().frame(2);
      findElement_RetrySeveralTimes(By.xpath("//tr[@id=\'category_item\']/td[4]/a/img")).click();
      break;
    default:
      throw new UnknownError();
    }
  }

}