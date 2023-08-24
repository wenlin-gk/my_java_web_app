package top.wl.servlet;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import net.sf.json.JsonConfig;
import redis.clients.jedis.Jedis;
import top.wl.BaseTest;
import top.wl.Const;
import top.wl.dao.CategoryDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.domain.Category;
import top.wl.utils.JedisUtils;
import top.wl.utils.JsonUtil;

public class CategoryTest extends BaseTest {
  private static Logger log = LogManager.getLogger(CategoryTest.class);
  private static String url = serverAddr + "/category";

  @Test
  public void getAll()
      throws SQLException, FileNotFoundException, DaoUnavailable {
// case:   系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
// case:   系统正常，没有category时，返回空jsonarr。
    clear_db();
    clear_redis();

    assertEquals("[null]", getAllCategory());

    init_db();

// case:   系统正常，有category时，返回jsonarr准确。
    assertEquals(expect_category(), getAllCategory());
  }

  private String expect_category() throws DaoUnavailable {
    JsonConfig config = JsonUtil
        .configJson(new String[] { "validate", "validate4Update" });
    List<Category> list = new CategoryDao().getAll();
    return JsonUtil.list2json(list, config);
  }

  private String getAllCategory() {
    driver.get(serverAddr + "/404.jsp");
    assertCurrentPage("404.jsp");

    String s_template = "const http = new XMLHttpRequest();\n"
        + "const url='%s';\n" + "http.open('GET', url);\n" + "http.send();\n"
        + "http.onload = (e) => {" + "if(http.status == 200){"
        + "const input = document.createElement('input');"
        + "input.id = 'Info'; input.value = http.responseText; "
        + "document.body.appendChild(input);}}";
    String _url = url + "?method=getAll";
    ((FirefoxDriver) driver).executeScript(String.format(s_template, _url));

    return findElement_RetrySeveralTimes(By.id("Info")).getAttribute("value");
  }

  private void clear_redis() {
    Jedis j = null;
    try {
      j = JedisUtils.getJedis();
      j.del(Const.KEY_4_STORE_CATEGORY_LIST);
    } finally {
      JedisUtils.closeJedis(j);
    }
  }
}