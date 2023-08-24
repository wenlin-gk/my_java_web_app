package top.wl.jsp.admin.category;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;

import top.wl.BaseTest4admin;
import top.wl.dao.CategoryDao;
import top.wl.dao.UserDaoTest;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.domain.Category;
import top.wl.utils.UUIDUtils;

public class EditTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(EditTest.class);

  @Test
  public void t() throws DaoUnavailable, DaoUnknownError, PropertyInvalidate, IdNotExist, SQLException {
// case:   必填字段未填写时，点击提交按钮无效。
    openAdminJsp("category/edit.jsp");

    String o_cname = driver.findElement(By.name("cname")).getAttribute("value");
    driver.findElement(By.name("cname")).clear();
    driver.findElement(By.id("category_update_submit")).click();
    assertCurrentPage("admin/category/edit.jsp");

// case:   提交按钮有效：点击提交，返回list.jsp页面。中文不乱码。
    String cname = "中文" + UUIDUtils.getCode().substring(0, 5);
    driver.findElement(By.name("cname")).sendKeys(cname);
    driver.findElement(By.id("category_update_submit")).click();
    assertCurrentPage("admin/category/list.jsp");
    Category c = getCategory(cname);
    assertNotNull(c);
    // rollback
    rollback(c, o_cname);
  }

  private Category getCategory(String cname) throws DaoUnavailable {
    for (Category c : new CategoryDao().getAll()) {
      if (cname.equals(c.getCname()))
        return c;
    }
    assertTrue(false);
    return null;
  }

  private void rollback(Category c, String o_cname) throws DaoUnknownError,
      PropertyInvalidate, DaoUnavailable, IdNotExist, SQLException {
    c.setCname(o_cname);
    Connection conn = DataSourceUtils.getConnection();
    try {
      new CategoryDao().updatePre(conn, c);
    } finally {
      DataSourceUtils.releaseConnection(conn);
    }
  }
}