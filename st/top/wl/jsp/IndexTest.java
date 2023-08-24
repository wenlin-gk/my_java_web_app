package top.wl.jsp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import top.wl.BaseTest;
import top.wl.dao.CategoryDao;
import top.wl.dao.ProductDao;
import top.wl.dao.error.DaoUnavailable;

public class IndexTest extends BaseTest {
  private static Logger log = LogManager.getLogger(IndexTest.class);

  @Test
  public void content_link_is_ok() {
    // 首页链接可用, 分类链接可用, 商品详情链接可用
    openJsp("index.jsp");

    clickLink_AssertReturnPage("某类商品", 1, "product_list.jsp");
    clickLink_AssertReturnPage("首页", -1, "index.jsp");
    clickLink_AssertReturnPage("某商品", 1, "product_info.jsp");
  }

  private void clickLink_AssertReturnPage(String link, int frame,
      String returnPageId) {
    click(link);
    assertCurrentPage(frame, returnPageId);
  }

  private void click(String target) {
    driver.switchTo().defaultContent();

    switch (target) {
    case "首页":
    case "登录":
    case "注册":
    case "退出":
    case "购物车":
    case "我的订单":
      driver.switchTo().frame(0);
      WebElement e = findElement_RetrySeveralTimes(By.linkText(target));
      e.click();
      break;
    case "某类商品":
      driver.switchTo().frame(0);
      findElement_RetrySeveralTimes(By.id("category_item"), 100).click();
      break;
    case "某商品":
      driver.switchTo().frame(1);
      findElement_RetrySeveralTimes(By.id("newItem")).click();
      break;
    default:
      throw new UnknownError();
    }
  }

  @Test
  public void content_is_ok() throws DaoUnavailable {
    // 分类显示准确，最新商品列表显示准确
    openJsp("index.jsp");
    wait_load_succ();
    assertCurrentPage("index.jsp");

    int a_size = getCategoryCount();
    int e_size = new CategoryDao().getAll().size();
    assertEquals(a_size, e_size);

    a_size = getHotProductInPage();
    e_size = new ProductDao().getAllByHot().size();
    assertEquals(a_size, e_size);

    a_size = getNewProductInPage();
    e_size = new ProductDao().getAllByNew().size();
    assertEquals(a_size, e_size);
  }

  private int getNewProductInPage() {
    driver.switchTo().defaultContent();
    driver.switchTo().frame(1);
    return driver.findElements(By.id("newItem")).size();
  }

  private int getHotProductInPage() {
    driver.switchTo().defaultContent();
    driver.switchTo().frame(1);
    return driver.findElements(By.id("hotItem")).size();
  }

  private int getCategoryCount() {
    driver.switchTo().defaultContent();
    driver.switchTo().frame(0);
    return driver.findElements(By.id("category_item")).size();
  }

  private void wait_load_succ() {
    findElement_RetrySeveralTimes(By.id("category_item"), 100);
  }

  @Test
  public void frameset_is_ok() {
    openJsp("index.jsp");

    assertCurrentPage(0, "top.jsp");
    assertCurrentPage(1, "body.jsp");
    assertCurrentPage(2, "buttom.jsp");
  }

  @Test
  public void link_is_ok_when_state_is_logout()
      throws SQLException, FileNotFoundException, DaoUnavailable {
    // 用户未登录时，登录，注册，我的购物车，链接可用
    logout();

    openJsp("index.jsp");
    assertCurrentPage("index.jsp");

    assertLinkNotExist_byText(0, "我的订单");
    assertLinkNotExist_byText(0, "退出");
    clickLink_AssertReturnPage("登录", 1, "login.jsp");

    openJsp("index.jsp");
    clickLink_AssertReturnPage("注册", 1, "register.jsp");

    openJsp("index.jsp");
    clickLink_AssertReturnPage("购物车", 1, "cart.jsp");

    login();
  }

  private void assertLinkNotExist_byText(int frame, String text) {
    driver.switchTo().defaultContent();
    driver.switchTo().frame(frame);
    try {
      driver.findElement(By.linkText(text));
      assertTrue(false);
    } catch (NoSuchElementException e) {
    }
  }

  @Test
  public void link_is_ok_when_state_is_login()
      throws SQLException, FileNotFoundException, DaoUnavailable {
// case: 用户已登录时，用户名显示准确，我的购物车，我的订单，退出，链接可用
    openJsp("index.jsp");
    assertCurrentPage("index.jsp");

    assertLinkNotExist_byText(0, "登录");
    assertLinkNotExist_byText(0, "注册");

    String e_username = "|  用户：" + test_user.getName() + "  |";
    String a_username = findElement_RetrySeveralTimes(By.id("user_name"))
        .getText();
    assertEquals(e_username, a_username);

    clickLink_AssertReturnPage("我的订单", 1, "order/order_list.jsp");

    openJsp("index.jsp");
    clickLink_AssertReturnPage("购物车", 1, "cart.jsp");
    openJsp("index.jsp");
    clickLink_AssertReturnPage("退出", -1, "index.jsp");
    assertLinkNotExist_byText(0, "我的订单");

    login();
  }
}