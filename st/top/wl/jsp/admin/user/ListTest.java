package top.wl.jsp.admin.user;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import top.wl.BaseTest4admin;

public class ListTest extends BaseTest4admin {

  @Test
  public void t() throws SQLException, FileNotFoundException {
// case:   user的编辑链接可用
    openAdminJsp("user/list.jsp");
    click("edit");
    assertCurrentPage("admin/user/edit.jsp");

// case:   user的删除链接可用
    openAdminJsp("user/list.jsp");
    click("delete");
    assertCurrentPage("admin/user/list.jsp", "msg.jsp");
    init_db();
  }

  private void click(String target) {
    driver.switchTo().defaultContent();
    
    switch (target) {
    case "edit":
      driver.switchTo().frame(2);
      findElement_RetrySeveralTimes(By.xpath("//tr[@id=\'user_item\']/td[4]/a/img")).click();
      break;
    case "delete":
      driver.switchTo().frame(2);
      findElement_RetrySeveralTimes(By.xpath("//tr[@id=\'user_item\']/td[5]/a/img")).click();
      break;
    default:
      throw new UnknownError();
    }
  }

}
