package top.wl.jsp.admin;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import top.wl.BaseTest4admin;
import top.wl.Const;
import top.wl.web.servlet.AdminUserServlet;

public class LoginTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(LoginTest.class);

  @Test
  public void t() {
    adminLogout();
    driver.get(serverAddr + "/admin/login.jsp");

    login("admin", "xxx");
    assertCurrentPage("admin/login.jsp");
    assertEquals(AdminUserServlet.errMsg4loginErrorPwd, msg());
    // 原始值会被回填。
    assertValue(driver.findElement(By.name("adminName")), "admin");
    assertValue(driver.findElement(By.name("adminPasswd")), "xxx");

    
    String username = "xxx";
    login(username, "xxx");
    assertCurrentPage("admin/login.jsp");
    String e_msg = String.format(AdminUserServlet.errMsg4loginInvalidateName, username);
    assertEquals(e_msg, msg());

    
    login("admin", Const.ADMIN_PASSWD);
    assertCurrentPage("admin/index.jsp");
  }

  private String msg() {
    return driver.findElement(By.cssSelector("span")).getText();
  }

  private static void login(String v1, String v2) {
    driver.findElement(By.name("adminName")).clear();
    driver.findElement(By.name("adminName")).sendKeys(v1);
    driver.findElement(By.name("adminPasswd")).clear();
    driver.findElement(By.name("adminPasswd")).sendKeys(v2);
  
    driver.findElement(By.className("button")).click();
  }

  public void assertValue(WebElement e, String msg) {
    assertEquals(msg, e.getAttribute("value"));
  }

}