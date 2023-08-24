package top.wl.jsp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import top.wl.BaseTest;
import top.wl.dao.ProductDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.domain.Product;

public class CartTest extends BaseTest {
  private static Logger log = LogManager.getLogger(CartTest.class);

  @Test
  public void t() throws DaoUnavailable, FileNotFoundException, SQLException {
// case:   无商品，显示无商品。
    openJsp("cart.jsp");
    assertCartIsEmpty();
    
    
// case:   有商品，显示商品准确。
    int expect_itmeSize = addProduct2Cart();
    openJsp("cart.jsp");
    int actual_itmeSize = driver.findElements(By.id("cartItem")).size();
    assertEquals(expect_itmeSize, actual_itmeSize);

    
// case:   移除按钮可用。
    click("delete");
    assertCurrentPage("cart.jsp");
    actual_itmeSize = driver.findElements(By.id("cartItem")).size();
    assertEquals(expect_itmeSize-1, actual_itmeSize);

    
// case:   清空购物车按钮可用
    click("clear");
    assertCurrentPage("cart.jsp");
    assertCartIsEmpty();

    
// case:   提交到订单可用。
    addProduct2Cart();
    openJsp("cart.jsp");
    click("submit");
    assertCurrentPage("order/order_info.jsp");
    init_db();
  }

  private void click(String target) {
    switch (target) {
    case "clear":
      driver.findElement(By.id("clear")).click();
      break;

    case "delete":
      driver.findElement(By.id("delete")).click();
      driver.switchTo().alert().accept();
      break;
      
    case "submit":
      driver.findElement(By.name("submit")).click();
      break;
      
    default:
      throw new UnknownError();
    }
  }

  private void assertCartIsEmpty() {
    try {
      driver.findElement(By.id("cartItem"));
      assertTrue(false);
    } catch (NoSuchElementException e) {
    }
    String emptyInfo = driver.findElement(By.id("emptyInfo")).getText();
    assertEquals("购物车空空如也,亲,请先去逛逛去吧~~~~~~~~~~~", emptyInfo);
  }

  private int addProduct2Cart() throws DaoUnavailable {
    List<Product> ps = new ProductDao().getAllByHot();
    String templ = serverAddr + "/cart"
        +"?method=put&pid=%s&count=%s";
    int count = 2;
    while(count>0) {
      Product p = ps.get(count-1);
      String url = String.format(templ, p.getPid(), 2);
      driver.get(url);
      assertCurrentPage("cart.jsp");
      --count;
    }
    return 2;
  }
}