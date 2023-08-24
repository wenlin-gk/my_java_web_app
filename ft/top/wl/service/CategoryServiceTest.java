package top.wl.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import redis.clients.jedis.exceptions.JedisConnectionException;
import top.wl.dao.error.DBError;
import top.wl.dao.error.IdExisted;
import top.wl.dao.error.PropertyIllegal;
import top.wl.domain.Category;
import top.wl.utils.UUIDUtils;

public class CategoryServiceTest {

  @Rule
  public final ExpectedException ee = ExpectedException.none();

  // 提前stop redis
  @Test
  public void test_findAllFromRedis_catch_runtimeexception_when_redis_conn_failed()
      throws DBError {
    CategoryService s = new CategoryService();
    ee.expect(JedisConnectionException.class);
    String v = s.getAllInJson();
  }

  // 启动mysql，停止redis
  @Test
  public void test_save_failed_when_redis_fault() {
    CategoryService s = new CategoryService();
    Category c = new Category();
    c.setCid(UUIDUtils.getCode());
    System.out.println(c);
//    ee.expect(DBError.class);
    try {
      s.savePre(c);
    } catch (PropertyIllegal | DBError | IdExisted e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      System.out.println(s.get(c.getCid()));
    } catch (DBError e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
//    assertNull(s.find(c.getCid()));
  }
}
