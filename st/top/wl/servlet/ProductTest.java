package top.wl.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

import top.wl.BaseTest;
import top.wl.dao.CategoryDao;
import top.wl.dao.OrderDao;
import top.wl.dao.ProductDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.domain.Category;
import top.wl.domain.Order;
import top.wl.domain.Product;
import top.wl.utils.UUIDUtils;
import top.wl.web.servlet.AdminProductServlet;
import top.wl.web.servlet.ProductServlet;

public class ProductTest extends BaseTest {
  private static final String url = serverAddr + "/product";
  private static Logger log = LogManager.getLogger(ProductTest.class);


  @Test
  public void getById() throws DaoUnavailable{
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（pid=null）时，返回提示页面，提示参数非法。
    String paramsTempl = "?method=getById";
    driver.get(url + paramsTempl);
    assertMsg(String.format(ProductServlet.errMsg4getPI, null));
    

// case:   系统正常，参数非法（pid=过长）时，返回提示页面，提示参数非法。
    paramsTempl = "?method=getById&pid=%s";
    String pid = UUIDUtils.getCode()+UUIDUtils.getCode();
    driver.get(url + String.format(paramsTempl, pid));
    assertMsg(String.format(ProductServlet.errMsg4getPI, pid));

    
// case:   系统正常，参数非法（id=noexist）时，返回提示页面，提示xx不存在。
    pid = UUIDUtils.getCode();
    driver.get(url + String.format(paramsTempl, pid));
    assertMsg(String.format(ProductServlet.errMsg4getNA, pid));
    
    
// case:   系统正常，参数合法时，返回/product/detail.jsp。显示商品信息准确。
    Product p = new ProductDao().getAllByHot().get(0);
    driver.get(url + String.format(paramsTempl, p.getPid()));
    assertCurrentPage("product_info.jsp");
    assertEquals(p.getPname(), findElement_RetrySeveralTimes(By.id("pname")).getText());
    assertEquals(p.getPid(), findElement_RetrySeveralTimes(By.id("pid")).getText());
  }
  
  @Test
  public void list() throws FileNotFoundException, SQLException, DaoUnavailable {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（pageNumber=-1）时，返回提示页面，提示参数非法。
    String paramsTempl = "?method=getByPage&pageNumber=%s&cid=%s";
    String cid = getCidWhichHasProduct();
    driver.get(url + String.format(paramsTempl, -1, cid));
    assertMsg(String.format(ProductServlet.errMsg4PageNumInvidate, -1));

    
// case:   系统正常，参数合法，2页商品，pageNumber=100超出最大页码时，返回list页面，内容为空，分页为空。
    driver.get(url + String.format(paramsTempl, 100, cid));
    assertCurrentPage("product_list.jsp");
    assertNull(findElement(By.id("product_item")));
    assertNull(findElement(By.id("page_index")));

// case:   系统正常，参数合法，2页商品，pageNumber=1超出最大页码时，返回list页面，内容，分页准确。
    driver.get(url + String.format(paramsTempl, 1, cid));
    assertCurrentPage("product_list.jsp");
    assertEquals(ProductServlet.pageSize,
        driver.findElements(By.id("product_item")).size());
    driver.findElement(By.id("page_index"));
  }

  private String getCidWhichHasProduct() throws DaoUnavailable {
    List<Category> cs = new CategoryDao().getAll();
    for(Category c: cs) {
      if(new ProductDao().getTotalRecord(c.getCid())>ProductServlet.pageSize) {
        return c.getCid();
      }
    }
    assertTrue(false);
    return null;
  }
}