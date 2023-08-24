package top.wl.jsp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.SessionId;

import top.wl.BaseTest;
import top.wl.utils.UUIDUtils;

public class LoginTest extends BaseTest {
  private static Logger log = LogManager.getLogger(LoginTest.class);
  
  @Test
  public void t() throws InterruptedException {
// case:   user名称不可用，提交按钮不可用。
    logout();
    
// case:   user名称可用，提交按钮可用。返回index.jsp，登录成功。
    openJsp("login.jsp");
    String name = driver.findElement(By.id("username")).getAttribute("value");
    assertEquals("", name);
    String isDisabled = driver.findElement(By.id("loginBut")).getAttribute("disabled");
    assertEquals("true", isDisabled);

// case:   记住用户名，退出后，登录页面，自动填充用户名。
    driver.findElement(By.id("username")).sendKeys(UUIDUtils.getId().subSequence(0, 5));
    waitCheckUsername(false);
    isDisabled = driver.findElement(By.id("loginBut")).getAttribute("disabled");
    assertEquals("true", isDisabled);
    
    
// case:   自动登录，session失效后，自动登录。
    assert_LoginButton_is_clickable_and_Login_succ_when_user_validate(false, false);
    invalidateSession();
    
    
    openJsp("login.jsp");
    assert_LoginButton_is_clickable_and_Login_succ_when_user_validate(true, false);
    logout();
    openJsp("login.jsp");
    name = driver.findElement(By.id("username")).getAttribute("value");
    assertEquals(test_user.getUsername(), name);
    
    
    openJsp("login.jsp");
    assert_LoginButton_is_clickable_and_Login_succ_when_user_validate(false, true);
    assertCurrentPage_is_IndexPage_and_login_succ();
    invalidateSession();
    driver.get(serverAddr + "/");
    assertCurrentPage_is_IndexPage_and_login_succ();
  }

  private void assertLogoutIndexPage() {
    // 返回未登录的首页（包含登录链接）
    assertCurrentPage("index.jsp");
    driver.switchTo().frame(0);
    driver.findElement(By.linkText("登录"));
    driver.switchTo().defaultContent();
  }

  private void assertCurrentPage_is_IndexPage_and_login_succ() {
    // 返回已登录的首页（包含我的订单链接）
    driver.switchTo().defaultContent();
    assertCurrentPage("index.jsp");
    
    driver.switchTo().frame(0);
    driver.findElement(By.linkText("我的订单"));
    driver.switchTo().defaultContent();
  }

  private void waitCheckUsername(boolean isUserValidate) throws InterruptedException {
    // 转移焦点，触发事件。
    driver.findElement(By.name("savename")).click();
    driver.findElement(By.name("savename")).click();
    
    int attempts = 0;
    String isDisabled = null;
    String expect_disable = isUserValidate ? null:"true";
    while (attempts < 30) {
      try {
        isDisabled = driver.findElement(By.id("loginBut"))
            .getAttribute("disabled");
        log.info(isDisabled);
        if (null==expect_disable && null==isDisabled ||expect_disable.equals(isDisabled))
          break;
        else {
          attempts += 1;
          Thread.sleep(100);
          log.info(attempts);
        }
      } catch (NoSuchElementException | StaleElementReferenceException e) {
        attempts += 1;
        Thread.sleep(100);
        log.info(attempts);
      }
    }
    assertTrue(attempts < 30);
  }

  private void assert_LoginButton_is_clickable_and_Login_succ_when_user_validate(boolean savename, boolean autologin) throws InterruptedException {
    driver.findElement(By.id("username")).clear();
    driver.findElement(By.id("inputPassword3")).clear();
    driver.findElement(By.id("username")).sendKeys(test_user.getUsername());
    driver.findElement(By.id("inputPassword3")).sendKeys(test_user.getPassword());
    if (savename)
      driver.findElement(By.name("savename")).click();
    if (autologin)
      driver.findElement(By.name("autologin")).click();
    waitCheckUsername(true);
    String isDisabled = driver.findElement(By.id("loginBut")).getAttribute("disabled");
    assertEquals(null, isDisabled);
    driver.findElement(By.id("loginBut")).click();
    assertCurrentPage_is_IndexPage_and_login_succ();
  }

}