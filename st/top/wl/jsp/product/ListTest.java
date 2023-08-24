package top.wl.jsp.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import top.wl.BaseTest;
import top.wl.dao.CategoryDao;
import top.wl.dao.ProductDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.domain.Category;
import top.wl.web.servlet.ProductServlet;

public class ListTest extends BaseTest {
  private static Logger log = LogManager.getLogger(ListTest.class);
  
  @Test
  public void t() throws DaoUnavailable {
    tearDown_testSystem();
    setUp_testSystem();
    login();
// case:   页码准确，页码链接可用
    String cid = openProductList_where_page_count_greater_than_1();
    assertCurrentPage("product_list.jsp");
    
    driver.findElement(By.id("page_index"));
    List<WebElement> pages = driver.findElements(By.id("page"));
    int a_pageCount = pages.size();
    assertEquals(getPageCount(cid), a_pageCount);
    
    pages.get(pages.size()-1).click();
    assertCurrentPage("product_list.jsp");
    int a_productCount = driver.findElements(By.id("product_item")).size();
    int e_productCount = new ProductDao().getTotalRecord(cid) - (a_pageCount-1) * ProductServlet.pageSize;
    assertEquals(e_productCount, a_productCount);


// case:   浏览历史为空时显示准确
    assertNull(findElement(By.id("history_item")));
    
    
// case:   商品详情链接可用。
    driver.findElements(By.id("page")).get(0).click();
    assertCurrentPage("product_list.jsp");
    List<WebElement> products = driver.findElements(By.id("product_item"));
    products.get(0).click();
    assertCurrentPage("product_info.jsp");
    
    
// case:   浏览历史不为空时显示准确
    openProductList_where_page_count_greater_than_1();
    assertCurrentPage("product_list.jsp");
    List<WebElement> history_items = driver.findElements(By.id("history_item"));
    assertEquals(1, history_items.size());
    
    
// case:   浏览历史-商品详情链接可用。
    history_items.get(0).click();
    assertCurrentPage("product_info.jsp");
    
    
  }

}