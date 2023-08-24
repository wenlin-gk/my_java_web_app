package top.wl.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

import top.wl.BaseTest4admin;
import top.wl.dao.OrderDao;
import top.wl.dao.ProductDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.domain.Order;
import top.wl.domain.Product;
import top.wl.utils.UUIDUtils;
import top.wl.web.servlet.AdminProductServlet;

public class AdminProductTest extends BaseTest4admin {
  private static final String url = serverAddr + "/admin/product";
  private static Logger log = LogManager.getLogger(AdminProductTest.class);

  @Test
  public void list() throws FileNotFoundException, SQLException {
// case:   系统故障（db故障）时，返回提示页面，提示服务不可用。--忽略
// case:   系统正常，参数非法（pageNumber=-1）时，返回提示页面，提示参数非法。
    String params = "?method=getByPage&pageNumber=-1";
    driver.get(url + params);
    assertMsg(String.format(AdminProductServlet.errMsg4PageNumInvidate, -1));

// case:   系统正常，参数合法，2页用户，pageNumber=100超出最大页码时，返回list页面，内容为空，分页为空。
    params = "?method=getByPage&pageNumber=100";
    driver.get(url + params);
    assertCurrentPage("admin/product/list.jsp");
    assertNull(findElement(By.id("product_item")));
    assertNull(findElement(By.id("page_index")));

// case:   系统正常，参数合法，2页用户，pageNumber=1时，返回list页面，内容，分页准确。
    params = "?method=getByPage&pageNumber=1";
    driver.get(url + params);
    assertCurrentPage("admin/product/list.jsp");
    driver.findElement(By.id("page_index"));
    assertEquals(AdminProductServlet.pageSize,
        driver.findElements(By.id("product_item")).size());
  }

  @Test
  public void editui() throws DaoUnavailable {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
    String params = "?method=editUI";
    driver.get(url + params);
    assertMsg(String.format(AdminProductServlet.errMsg4editUIPI, null));

// case:   系统正常，参数非法（id过长）时，返回提示页面，提示参数非法。
    String pid = "11111111111111111111111111111111111111111111111111111";
    params = "?method=editUI&pid=" + pid;
    driver.get(url + params);
    assertMsg(String.format(AdminProductServlet.errMsg4editUIPI, pid));

// case:   系统正常，参数非法（id=noexist）时，返回提示页面，提示xx不存在。
    pid = "noexist" + UUIDUtils.getCode().substring(0, 5);
    params = "?method=editUI&pid=" + pid;
    driver.get(url + params);
    assertMsg(String.format(AdminProductServlet.errMsg4editUINA, pid));

// case:   系统正常，参数合法（id=从list页面获取）时，返回edit.jsp，product信息准确。
    Product p = new ProductDao().getAllByHot().get(0);
    params = "?method=editUI&pid=" + p.getPid();
    driver.get(url + params);
    assertCurrentPage("admin/product/edit.jsp");

    String pname = driver.findElement(By.name("pname")).getAttribute("value");
    String market_price = driver.findElement(By.name("market_price"))
        .getAttribute("value");
    String shop_price = driver.findElement(By.name("shop_price"))
        .getAttribute("value");
    String pdesc = driver.findElement(By.name("pdesc")).getText();
    assertEquals(p.getPname(), pname);
    assertTrue(p.getMarket_price().equals(Double.parseDouble(market_price)));
    assertTrue(p.getShop_price().equals(Double.parseDouble(shop_price)));
    assertEquals(p.getPdesc(), pdesc);
  }

  @Test
  public void update()
      throws DaoUnavailable, FileNotFoundException, SQLException,
      InterruptedException, IsReferenced, IdNotExist, DaoUnknownError {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
    String _url = url + "?method=update";
    Product product_v0 = new Product();
    updateProduct(product_v0);
    waitPage("msg.jsp");
    assertMsg(String.format(AdminProductServlet.errMsg4updatePI, product_v0));

// case:   系统正常，参数非法（product属性非法，如name过长）时，返回提示页面，提示参数非法。
    String pid = "11111111111111111111111111111111111111111111111111111";
    product_v0.setPid(pid);
    updateProduct(product_v0);
    waitPage("msg.jsp");
    assertMsg(String.format(AdminProductServlet.errMsg4updatePI, product_v0));

// case:   系统正常，参数合法，业务不允许，如id不存在 时，返回提示页面，提示xx不存在。
    pid = UUIDUtils.getId();
    product_v0.setPid(pid);
    updateProduct(product_v0);
    waitPage("msg.jsp");
    assertMsg(String.format(AdminProductServlet.errMsg4updateNA, product_v0));

// case:   系统正常，参数合法，业务允许(修改所有字段)，返回list.jsp页面。修改成功。中文不乱码。
    product_v0 = addProdcut();
    String pimage = System.getProperty("user.dir") + "/st/product_test.jpg";
    Product expect_product_v1 = genNewProduct(product_v0, pimage);
    updateProduct(expect_product_v1);
    waitPage("admin/product/list.jsp");
    assertCurrentPage("admin/product/list.jsp");
    // 更新准确；
    Product actual_product_v1 = new ProductDao().getById(expect_product_v1.getPid());
    
    assertTrue(expect_product_v1.equalsIgnoreImage(actual_product_v1));
    assertNotEquals(actual_product_v1.getPimage(), product_v0.getPimage());
    assertTrue(actual_product_v1.getPimage().endsWith("product_test.jpg"));
    // 旧图片消失，新图片存在。
    String o_image = serverAddr + "/" + product_v0.getPimage();
    driver.get(o_image);
    assertCurrentPage("404.jsp");
    assertImageExist(actual_product_v1.getPimage());


// case:   系统正常，参数合法，业务允许(修改部分字段)，返回list.jsp页面。修改成功，空字段未更新。
    Product expect_product_v2 = new Product();
    expect_product_v2.setPid(expect_product_v1.getPid());
    expect_product_v2.setPname(UUIDUtils.getId().substring(0, 5));
    updateProduct(expect_product_v2);
    waitPage("admin/product/list.jsp");
    assertCurrentPage("admin/product/list.jsp");

    Product actual_product_v2 = new ProductDao().getById(expect_product_v1.getPid());

    expect_product_v1.setPname(expect_product_v2.getPname());
    assertTrue(expect_product_v1.equalsIgnoreImage(actual_product_v2));

    new ProductDao().delete(expect_product_v1.getPid());
  }

  private void updateProduct(Product p) {
    String _url = url + "?method=update";
    HashMap<String, String> parameters = new HashMap<String, String>();
    fillProperties(parameters, p);
    if(p.getPimage() == null)
      post(_url, parameters, true);
    else {
      post(_url, parameters, false);
      driver.findElement(By.name("pimage")).sendKeys(p.getPimage());
      driver.findElement(By.id("submit")).click();
    }
  }

  @Test
  public void delete() throws DaoUnavailable, DaoUnknownError,
      FileNotFoundException, SQLException, IsReferenced, IdNotExist {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
    String params = "?method=delete";
    driver.get(url + params);
    assertMsg(String.format(AdminProductServlet.errMsg4deletePI, null));

// case:   系统正常，参数非法（id过长）时，返回提示页面，提示参数非法。
    String pid = "11111111111111111111111111111111111111111111111111111";
    params = "?method=delete&pid=" + pid;
    driver.get(url + params);
    assertMsg(String.format(AdminProductServlet.errMsg4deletePI, pid));

// case:   系统正常，参数非法（id=noexist）时，返回提示页面，提示参数非法。
    pid = "noexist"+UUIDUtils.getCode().substring(0,5);
    params = "?method=delete&pid=" + pid;
    driver.get(url + params);
    assertMsg(String.format(AdminProductServlet.errMsg4deleteNA, pid));

// case:   系统正常，参数非法（id=被订单条目依赖）时，返回提示页面，提示被依赖不允许删除。
    String oid = new OrderDao().getAllByState(Order.ORDER_WEIFUKUAN).get(0)
        .getOid();
    pid = new OrderDao().getById(oid).getItems().get(0).getProduct().getPid();
    params = "?method=delete&pid=" + pid;
    driver.get(url + params);
    assertMsg(String.format(AdminProductServlet.errMsg4deleteNA, pid));

// case:   系统正常，参数合法（id=从list页面获取）时，返回list.jsp，product删除成功。
    Product p = addProdcut();
    pid = p.getPid();
    assertNotNull(pid);
    params = "?method=delete&pid=" + pid;
    driver.get(url + params);
    assertCurrentPage("admin/product/list.jsp");
    assertEquals(null, new ProductDao().getById(pid));
    // 图片资源删除成功
    String o_image = serverAddr + "/" + p.getPimage();
    log.info(o_image);
    driver.get(o_image);
    assertCurrentPage("404.jsp");
  }

  @Test
  public void add() throws DaoUnavailable, SQLException, IsReferenced,
      IdNotExist, DaoUnknownError {
// case:     系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:     系统正常，参数非法（product属性非法，如name过长）时，返回提示页面，提示参数非法。
    Product p = new Product();
    String _url = url + "?method=add";
    HashMap<String, String> parameters = new HashMap<String, String>();
    String pname = "11111111111111111111111111111111111111111111111111111";
    parameters.put("pname", pname);
    p.setPname(pname);
    post(_url, parameters, true);
    waitPage("msg.jsp");
    assertMsg(String.format(AdminProductServlet.errMsg4addPI, p));

// case:     系统正常，参数合法，业务不允许，如cid不存在 时，返回提示页面，提示cid不存在。
    driver.get(_url);
    waitPage("msg.jsp");
    assertMsg(AdminProductServlet.NotMultipartType);

// case:     系统正常，参数合法，业务允许，返回list.jsp页面。中文不乱码。
    p = addProdcut();
    Product a_p = getProduct(p.getPname());
    p.setPid(a_p.getPid());
    p.equalsIgnoreImage(a_p);
    a_p.getPimage().endsWith("product_test.jpg");
    assertImageExist(a_p.getPimage());

    new ProductDao().delete(p.getPid());
  }

  private Product getProduct(String pname) throws DaoUnavailable {
    List<Product> ps = new ProductDao().getAllByHot();
    for (Product _p : ps) {
      if (pname.equals(_p.getPname())) {
        return _p;
      }
    }
    return null;
  }

  private void assertImageExist(String pimage) {
    String n_image = serverAddr + "/" + pimage;
    log.info(n_image);

    int attempts = 0;
    while (attempts < 20) {
      try {
        driver.get(n_image);
        driver.findElement((By.xpath("/html/body/img")));
        break;
      } catch (NoSuchElementException | StaleElementReferenceException e) {
        attempts += 1;
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
        }
        log.info(attempts);
      }
    }

    driver.get(n_image);
    assertEquals(n_image,
        findElement_RetrySeveralTimes(By.xpath("/html/body/img"), 10).getAttribute("src"));
  }
}