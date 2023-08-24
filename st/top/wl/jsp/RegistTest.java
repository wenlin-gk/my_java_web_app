package top.wl.jsp;

import static org.junit.Assert.assertNotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;

import top.wl.BaseTest;
import top.wl.dao.UserDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.domain.User;
import top.wl.utils.UUIDUtils;
import top.wl.web.servlet.UserServlet;

public class RegistTest extends BaseTest {
  private static Logger log = LogManager.getLogger(RegistTest.class);

  @Test
  public void t() throws DaoUnavailable {
// case:   必填字段未填写时，点击提交无效。
// case:   提交按钮有效：点击提交，返回msg.jsp页面。中文不乱码。
    logout();
    
    openJsp("register.jsp");
    driver.findElement(By.id("regBut")).click();
    assertCurrentPage("register.jsp");
    

    String uname = "中文"+UUIDUtils.getCode().substring(0,5);
    driver.findElement(By.id("username")).sendKeys(uname);
    driver.findElement(By.id("inputPassword3")).sendKeys("zzzz");
    driver.findElement(By.id("confirmpwd")).sendKeys("zzzz");
    driver.findElement(By.id("inputEmail3")).sendKeys("wenlin_uestc@163.com");
    driver.findElement(By.id("usercaption")).sendKeys("wl");
    driver.findElement(By.id("inlineRadio1")).click();
    driver.findElement(By.name("birthday")).sendKeys("2023-07-19");
    
    driver.findElement(By.id("regBut")).click();
    assertMsg(UserServlet.msg4registSucc);
    User u = new UserDao().getByUsername(uname);
    assertNotNull(u);
    deleteUser(u.getUid());
    
    login();
  }
}