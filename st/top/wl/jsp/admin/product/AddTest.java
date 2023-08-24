package top.wl.jsp.admin.product;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;

import top.wl.BaseTest4admin;
import top.wl.dao.ProductDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.domain.Product;
import top.wl.utils.UUIDUtils;

public class AddTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(AddTest.class);

  @Test
  public void t() throws IsReferenced, DaoUnavailable, IdNotExist,
      DaoUnknownError, SQLException {
// case:   必填字段未填写时，点击提交按钮无效。
    openAdminJsp("product/add.jsp");
    findElement(By.id("product_add_submit")).click();

// case:   提交按钮有效：点击提交，返回list.jsp页面，添加成功。中文不乱码。
    String pname = "中文"+UUIDUtils.getId();
    driver.findElement(By.name("pname")).sendKeys(pname);
    driver.findElement(By.name("market_price")).sendKeys("10");
    driver.findElement(By.name("shop_price")).sendKeys("10");
    driver.findElement(By.id("product_add_submit")).click();
    assertCurrentPage("admin/product/list.jsp");
    assertProductAddSucc_and_rollback(pname, 10, 10);
  }

  private void assertProductAddSucc_and_rollback(String pname, int mp, int sp) throws DaoUnavailable, SQLException, IsReferenced, IdNotExist, DaoUnknownError {
    List<Product> ps = new ProductDao().getAllByHot();
    boolean flag = false;
    for (Product p : ps) {
      if (pname.equals(p.getPname())) {
        flag = true;

        assertTrue(new Double(mp).equals(p.getMarket_price()));
        assertTrue(new Double(sp).equals(p.getShop_price()));

        Connection con = null;
        try {
          con = DataSourceUtils.getConnection();
          new ProductDao().delete(p.getPid());
        } finally {
          if (null != con)
            con.close();
        }
        break;
      }
    }
    assertTrue(flag);
  }
}