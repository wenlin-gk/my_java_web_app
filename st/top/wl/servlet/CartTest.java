package top.wl.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
import top.wl.utils.UUIDUtils;
import top.wl.web.servlet.CartServlet;

public class CartTest extends BaseTest {
  private static final String url = serverAddr + "/cart";
  private static Logger log = LogManager.getLogger(CartTest.class);


  @Test
  public void put() throws DaoUnavailable{
// case:   系统正常，参数非法（pid=""）时，返回提示页面，提示参数非法。
    String paramsTempl = "?method=put&pid=%s&count=%s";
    driver.get(url + String.format(paramsTempl, "", 1));
    assertMsg(String.format(CartServlet.errMsg4getPI, null, 1));
    
    
// case:   系统正常，参数非法（count=0）时，返回提示页面，提示参数非法。
    String pid = new ProductDao().getAllByHot().get(0).getPid();
    driver.get(url + String.format(paramsTempl, pid, 0));
    assertMsg(String.format(CartServlet.errMsg4getPI, pid, 0));
    
    
// case:   系统正常，参数非法（id=noexist）时，返回提示页面，提示xx不存在。
    pid = UUIDUtils.getCode();
    driver.get(url + String.format(paramsTempl, pid, 2));
    assertMsg(String.format(CartServlet.errMsg4getNA, pid));
    
    
// case:   系统正常，参数合法时，返回/cart.jsp
    pid = new ProductDao().getAllByHot().get(0).getPid();
    driver.get(url + String.format(paramsTempl, pid, 2));
    assertCurrentPage("cart.jsp");
    int actual_itmeSize = driver.findElements(By.id("cartItem")).size();
    assertEquals(1, actual_itmeSize);
  }
  
  @Test
  public void remove() throws DaoUnavailable{
    String paramsTempl = "?method=put&pid=%s&count=%s";
    String pid = new ProductDao().getAllByHot().get(0).getPid();
    driver.get(url + String.format(paramsTempl, pid, 2));
    assertCurrentPage("cart.jsp");
    
    paramsTempl = "?method=remove&pid=%s";
    driver.get(url + String.format(paramsTempl, UUIDUtils.getCode(), 2));
    assertCurrentPage("cart.jsp");
    int actual_itmeSize = driver.findElements(By.id("cartItem")).size();
    assertEquals(1, actual_itmeSize);

    driver.get(url + String.format(paramsTempl, pid));
    assertCurrentPage("cart.jsp");
    assertNull(findElement(By.id("cartItem")));
  }
  
  @Test
  public void clear() throws DaoUnavailable{
    String paramsTempl = "?method=put&pid=%s&count=%s";
    List<Product> ps = new ProductDao().getAllByHot();
    driver.get(url + String.format(paramsTempl, ps.get(0).getPid(), 2));
    driver.get(url + String.format(paramsTempl, ps.get(1).getPid(), 2));
    assertCurrentPage("cart.jsp");
    int actual_itmeSize = driver.findElements(By.id("cartItem")).size();
    assertEquals(2, actual_itmeSize);

    driver.get(url + "?method=clear");
    assertCurrentPage("cart.jsp");
    assertNull(findElement(By.id("cartItem")));
  }
}