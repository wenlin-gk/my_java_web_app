package top.wl.jsp.admin.user;

import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;

import top.wl.BaseTest4admin;
import top.wl.dao.UserDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.domain.User;

public class EditTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(EditTest.class);

  @Test
  public void test() throws DaoUnavailable, PropertyInvalidate, IdNotExist, DaoUnknownError {
// case:   必填字段未填写时，点击提交按钮无效。
    openAdminJsp("user/edit.jsp");
    String o_username = driver.findElement(By.name("username"))
        .getAttribute("value");
    driver.findElement(By.name("username")).clear();
    driver.findElement(By.id("userAction_save_do_submit")).click();
    assertCurrentPage("admin/user/edit.jsp");

// case:   提交按钮有效：点击提交，返回list.jsp页面。中文不乱码。
    String username = o_username+"中文";
    driver.findElement(By.name("username")).sendKeys(username);
    driver.findElement(By.id("userAction_save_do_submit")).click();
    assertCurrentPage("admin/user/list.jsp");
    User u = new UserDao().getByUsername(username);
    assertNotNull(u);
    //rollback
    u.setUsername(o_username);
    new UserDao().update(u);
  }

}