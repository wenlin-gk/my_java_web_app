package top.wl.jsp.admin.category;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;

import top.wl.BaseTest4admin;
import top.wl.dao.CategoryDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.domain.Category;

public class AddTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(AddTest.class);

  @Test
  public void t() throws IsReferenced, DaoUnavailable, IdNotExist,
      DaoUnknownError, SQLException {
// case:   必填字段未填写时，点击提交按钮无效。
    openAdminJsp("category/add.jsp");
    driver.findElement(By.id("category_add_submit")).click();

    
// case:   提交按钮有效：点击提交，返回list.jsp页面，添加成功。
    driver.findElement(By.name("cname")).sendKeys("newname");
    driver.findElement(By.id("category_add_submit")).click();
    assertCurrentPage("admin/category/list.jsp");
    deleteCategory("newname");
  }

  private void deleteCategory(String cname) throws DaoUnavailable, SQLException, IsReferenced, IdNotExist, DaoUnknownError {
    List<Category> cs = new CategoryDao().getAll();
    for (Category c : cs) {
      if (cname.equals(c.getCname())) {
        Connection con = null;
        try {
          con = DataSourceUtils.getConnection();
          new CategoryDao().deletePre(con, c.getCid());
        } finally {
          if (null != con)
            con.close();
        }
      }
    }
  }
}