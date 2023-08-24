package top.wl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.By;

public class BaseTest4admin extends BaseTest {
  private static Logger log = LogManager.getLogger(BaseTest4admin.class);

  @BeforeClass
  public static void setUpClass() {
    BaseTest.setUpClass();
    adminLogin();
  }

  @AfterClass
  public static void tearDownClass() {
    adminLogout();
    BaseTest.tearDownClass();
  }

  public static void openAdminJsp(String page) {
    driver.switchTo().defaultContent();
    
    driver.get(serverAddr + "/admin/index.jsp");
    
    _openAdminJsp(page);
  }
  
  private static void _openAdminJsp(String page) {
    switch (page) {
    case "index.jsp":
      driver.switchTo().frame(1);
      driver.findElement(By.linkText("展开所有")).click();
      driver.switchTo().defaultContent();
      break;
    case "user/list.jsp":
      driver.switchTo().frame(1);
      driver.findElement(By.linkText("展开所有")).click();
      driver.findElement(By.id("sd2")).click();
      driver.switchTo().defaultContent();

      driver.switchTo().frame(2);
      break;
    case "user/edit.jsp":
      _openAdminJsp("user/list.jsp");
      driver.findElement(By.xpath("//tr[@id=\'user_item\']/td[4]/a/img"))
          .click();
      break;
    case "category/list.jsp":
      driver.switchTo().frame(1);
      driver.findElement(By.linkText("展开所有")).click();
      driver.findElement(By.id("sd4")).click();
      driver.switchTo().defaultContent();

      driver.switchTo().frame(2);
      break;
    case "category/add.jsp":
      driver.switchTo().frame(1);
      driver.findElement(By.linkText("展开所有")).click();
      driver.findElement(By.id("sd5")).click();
      driver.switchTo().defaultContent();

      driver.switchTo().frame(2);
      break;
    case "category/edit.jsp":
      _openAdminJsp("category/list.jsp");
      driver.findElement(By.xpath("//tr[@id=\'category_item\']/td[3]/a/img"))
          .click();
      break;
    case "product/list.jsp":
      driver.switchTo().frame(1);
      driver.findElement(By.linkText("展开所有")).click();
      driver.findElement(By.id("sd7")).click();
      driver.switchTo().defaultContent();

      driver.switchTo().frame(2);
      break;
    case "product/add.jsp":
      driver.switchTo().frame(1);
      driver.findElement(By.linkText("展开所有")).click();
      driver.findElement(By.id("sd8")).click();
      driver.switchTo().defaultContent();

      driver.switchTo().frame(2);
      break;
    case "product/edit.jsp":
      _openAdminJsp("product/list.jsp");
      driver.findElement(By.xpath("//tr[@id=\'product_item\']/td[6]/a/img"))
          .click();
      break;
    case "order/list.jsp":
      driver.switchTo().frame(1);
      driver.findElement(By.linkText("展开所有")).click();
      driver.findElement(By.id("sd10")).click();
      driver.switchTo().defaultContent();

      driver.switchTo().frame(2);
      break;
    default:
      throw new UnknownError();
    }
  }

}