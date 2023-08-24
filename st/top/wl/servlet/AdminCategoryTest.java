package top.wl.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import top.wl.BaseTest4admin;
import top.wl.Const;
import top.wl.dao.CategoryDao;
import top.wl.dao.OrderDao;
import top.wl.dao.ProductDao;
import top.wl.dao.UserDaoTest;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdExisted;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.domain.Category;
import top.wl.domain.Order;
import top.wl.domain.User;
import top.wl.utils.UUIDUtils;
import top.wl.web.servlet.AdminCategoryServlet;
import top.wl.web.servlet.AdminUserServlet;
import top.wl.web.servlet.BaseServlet;

public class AdminCategoryTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(AdminCategoryTest.class);
  private static String url = serverAddr + "/admin/category";

  @Test
  public void list()
      throws SQLException, FileNotFoundException, DaoUnavailable {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，没有category时，返回list页面，无内容。
    clear_db();
    String params = "?method=getAll";
    driver.get(url + params);
    assertCurrentPage("admin/category/list.jsp");
    assertNull(findElement(By.id("category_item")));
    init_db();

// case:   系统正常，有category时，返回list页面，内容准确。
    driver.get(url + params);
    assertCurrentPage("admin/category/list.jsp");
    int actualC = driver.findElements(By.id("category_item")).size();
    int expectC = new CategoryDao().getAll().size();
    assertEquals(expectC, actualC);
  }

  @Test
  public void editui() throws DaoUnavailable {
// case: 系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case: 系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
    String params = "?method=editUI";
    driver.get(url + params);
    assertMsg(String.format(AdminCategoryServlet.errMsg4editUIPI, null));

// case: 系统正常，参数非法（id过长）时，返回提示页面，提示参数非法。
    String cid = "11111111111111111111111111111111111111111111111111111";
    params = "?method=editUI&cid=" + cid;
    driver.get(url + params);
    assertMsg(String.format(AdminCategoryServlet.errMsg4editUIPI, cid));

// case: 系统正常，参数非法（id=noexist）时，返回提示页面，提示参数非法。
    cid = UUIDUtils.getCode().substring(0, 5);
    params = "?method=editUI&cid=" + cid;
    driver.get(url + params);
    assertMsg(String.format(AdminCategoryServlet.errMsg4editUINA, cid));

// case: 系统正常，参数合法（id=从list页面获取）时，返回edit.jsp，category信息准确。
    Category c = new CategoryDao().getAll().get(0);
    params = "?method=editUI&cid=" + c.getCid();
    driver.get(url + params);
    assertCurrentPage("admin/category/edit.jsp");
    String cname = driver.findElement(By.name("cname")).getAttribute("value");
    assertEquals(c.getCname(), cname);
  }

  @Test
  public void update() throws DaoUnavailable, UnsupportedEncodingException {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
    String params = "?method=update";
    driver.get(url + params);
    assertMsg(String.format(AdminCategoryServlet.errMsg4updatePI, null));

// case:   系统正常，参数非法（category属性非法，如name过长）时，返回提示页面，提示参数非法。
    Category c = new CategoryDao().getAll().get(0);
    String cname = "11111111111111111111111111111111111111111111111111111";
    params = "?method=update&cid=" + c.getCid() + "&cname=" + cname;
    driver.get(url + params);
    assertMsg(String.format(AdminCategoryServlet.errMsg4updatePI, cname));

// case:   系统正常，参数合法，业务不允许，如id不存在 时，返回提示页面，提示xx不存在。
    cname = "111111111";
    String cid = UUIDUtils.getCode().substring(0, 5);
    params = "?method=update&cid=" + cid + "&cname=" + cname;
    driver.get(url + params);
    assertMsg(String.format(AdminCategoryServlet.errMsg4updateNA, cid, cname));

// case:   系统正常，参数合法，业务允许，返回list.jsp页面。中文不乱码。
    cname = "中文"+UUIDUtils.getCode().substring(0,5);
    params = "?method=update&cid=" + c.getCid() + "&cname=" + cname;
    driver.get(url + params);
    assertCurrentPage("admin/category/list.jsp");
    assertEquals(new CategoryDao().getById(c.getCid()).getCname(), cname);
  }

  @Test
  public void delete() throws DaoUnavailable, SQLException, DaoUnknownError,
      PropertyInvalidate, IdExisted {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
    String params = "?method=delete";
    driver.get(url + params);
    assertMsg(String.format(AdminCategoryServlet.errMsg4deletePI, null));

// case:   系统正常，参数非法（id过长）时，返回提示页面，提示参数非法。
    String cid = "11111111111111111111111111111111111111111111111111111";
    params = "?method=delete&cid=" + cid;
    driver.get(url + params);
    assertMsg(String.format(AdminCategoryServlet.errMsg4deletePI, cid));

// case:   系统正常，参数非法（id=noexist）时，返回提示页面，提示参数非法。
    cid = UUIDUtils.getCode().substring(0,5);
    params = "?method=delete&cid=" + cid;
    driver.get(url + params);
    assertMsg(String.format(AdminCategoryServlet.errMsg4deleteNA, cid));

// case:   系统正常，参数非法（id=被订单条目依赖）时，返回提示页面，提示被依赖不允许删除。
    cid = new ProductDao().getAllByHot().get(0).getCid();
    params = "?method=delete&cid=" + cid;
    driver.get(url + params);
    assertMsg(String.format(AdminCategoryServlet.errMsg4deleteNA, cid));

// case:   系统正常，参数合法（id=从list页面获取）时，返回list.jsp，category删除成功。
    List<Category> cs = new CategoryDao().getAll();
    int count = cs.size();
    String cname = cs.get(count - 1).getCname();
    cid = cs.get(count - 1).getCid();
    
    params = "?method=delete&cid=" + cid;
    driver.get(url + params);
    cs = new CategoryDao().getAll();
    assertEquals(count - 1, cs.size());
    // rollback
    addCategory(new Category(cid, cname));
  }

  private void addCategory(Category c) throws SQLException, DaoUnknownError, PropertyInvalidate, IdExisted, DaoUnavailable {
    Connection con = null;
    try {
      con = DataSourceUtils.getConnection();
      new CategoryDao().addPre(con, c);
    } finally {
      if (null != con)
        con.close();
    }
  }

  @Test
  public void add() throws DaoUnavailable, SQLException, IsReferenced,
      IdNotExist, DaoUnknownError {
// case:     系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:     系统正常，参数非法（category属性非法，如name过长）时，返回提示页面，提示参数非法。

    // post和get请求的参数都会被后端处理。本来应该使用post，这里为了简单，使用get请求。
    String cname = "11111111111111111111111111111111111111111111111111111";
    String params = "?method=add&cname=" + cname;
    driver.get(url + params);
    assertMsg(String.format(AdminCategoryServlet.errMsg4addPI, cname));

// case:     系统正常，参数合法，业务不允许，如id已经存在 时，返回提示页面，提示xx已经存在。--复现不了。
// case:     系统正常，参数合法，业务允许，返回list.jsp页面。中文不乱码。
    cname = "中文" + UUIDUtils.getCode().substring(0, 5);
    params = "?method=add&cname=" + cname;
    driver.get(url + params);
    assertCurrentPage("admin/category/list.jsp");
    
    deleteCategory(cname);
  }

  private void deleteCategory(String cname) throws DaoUnavailable, IsReferenced, IdNotExist, DaoUnknownError, SQLException {
    boolean flag = false;
    List<Category> cs = new CategoryDao().getAll();
    for (Category c : cs) {
      if (cname.equals(c.getCname())) {
        flag = true;
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
    assertEquals(true, flag);
  }
}