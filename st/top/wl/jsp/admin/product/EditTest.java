package top.wl.jsp.admin.product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;

import top.wl.BaseTest4admin;
import top.wl.dao.error.DaoUnavailable;

public class EditTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(EditTest.class);

  @Test
  public void t() throws DaoUnavailable {
// case: 必填字段未填写时，点击提交按钮无效。
    openAdminJsp("product/edit.jsp");

    String pname = driver.findElement(By.name("pname")).getAttribute("value");
    driver.findElement(By.name("pname")).clear();
    driver.findElement(By.id("product_update_submit")).click();
    assertCurrentPage("admin/product/edit.jsp");

// case: 提交按钮有效：点击提交，返回list.jsp页面.
    driver.findElement(By.name("pname")).sendKeys(pname);
    driver.findElement(By.id("product_update_submit")).click();
    assertCurrentPage("admin/product/list.jsp");
  }
}