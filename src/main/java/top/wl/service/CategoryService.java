package top.wl.service;

import java.sql.Connection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import top.wl.Const;
import top.wl.dao.CategoryDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdExisted;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.domain.Category;
import top.wl.service.error.BusinessNotAllowed;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcFault;
import top.wl.service.error.SvcUnavailable;
import top.wl.utils.JedisUtils;
import top.wl.utils.JsonUtil;
import top.wl.utils.UUIDUtils;

public class CategoryService extends BaseService {
  private static Logger log = LogManager.getLogger(CategoryService.class);
  private CategoryDao cd = new CategoryDao();

  public List<Category> getAll() throws SvcUnavailable {
    try {
      return cd.getAll();
    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }
  public Category get(String cid) throws SvcUnavailable, ParamInvalidate {
    if (!new Category(cid).isValidate4Update())
      throw new ParamInvalidate();

    try {
      return cd.getById(cid);
    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }
  /**
   * 
   * @return 返回值为json
   * @throws SvcUnavailable
   */
  public String getAllInJson() throws SvcUnavailable {
    String result = null;
    Jedis j = null;

    try {
      j = JedisUtils.getJedis();
      result = j.get(Const.KEY_4_STORE_CATEGORY_LIST);

      if (!isEmpty(result)) {
        log.info("Get缓存数据：" + result);
        return result;
      } else {
        result = getAllInJsonFromDB();
        log.info("Get value from DB: " + result);

        j.set(Const.KEY_4_STORE_CATEGORY_LIST, result);
      }
    } catch (JedisConnectionException e) {
      log.error(e.getMessage(), e);
      result = getAllInJsonFromDB();
      log.info("Get value from DB: " + result);

    } finally {
      JedisUtils.closeJedis(j);
    }

    return result;
  }
  private static boolean isEmpty(String result) {
    JSONArray ja = JSONArray.fromObject(result);
    for (Object j : ja) {
      JSONObject jo = JSONObject.fromObject(j);
      if(!JSONNull.getInstance().equals(jo)) {
        return false;
      }
    }
    return true;
  }
  private String getAllInJsonFromDB() throws SvcUnavailable {
    JsonConfig config = JsonUtil
        .configJson(new String[] { "validate","validate4Update"});

    List<Category> list = getAll();
    if (list != null && list.size() > 0) {
      return JsonUtil.list2json(list, config);
    }
    return JsonUtil.list2json(null);
  }
  /**
   * 
   * @param cname
   * @throws SvcUnavailable
   * @throws ParamInvalidate
   * @throws BusinessNotAllowed
   * @throws SvcFault
   */
  public void add(String cname)
      throws SvcUnavailable, ParamInvalidate, BusinessNotAllowed, SvcFault {
    Category c = new Category();
    c.setCname(cname);
    c.setCid(UUIDUtils.getId());
    if (!c.isValidate()) {
      throw new ParamInvalidate();
    }

    Connection conn = beginTransaction();
    try {
      cd.addPre(conn, c);

    } catch (IdExisted e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new BusinessNotAllowed();

    } catch (PropertyInvalidate e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new ParamInvalidate();

    } catch (DaoUnavailable | DaoUnknownError e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new SvcUnavailable();
    }

    try {
      clear_category_in_redis();
    } catch (JedisConnectionException e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new SvcUnavailable();
    }
    commitTransactionAndRelease(conn);
  }

  public void delete(String cid)
      throws SvcUnavailable, ParamInvalidate, BusinessNotAllowed, SvcFault {
    if (!Category.isIdValidate(cid)) {
      throw new ParamInvalidate();
    }

    Connection conn = beginTransaction();
    try {
      cd.deletePre(conn, cid);

    } catch (DaoUnavailable | DaoUnknownError e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new SvcUnavailable();

    } catch (IsReferenced | IdNotExist e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new BusinessNotAllowed();
    }

    try {
      clear_category_in_redis();
    } catch (JedisConnectionException e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new SvcUnavailable();
    }
    commitTransactionAndRelease(conn);
  }

  public void update(Category c)
      throws SvcUnavailable, ParamInvalidate, BusinessNotAllowed, SvcFault {

    if (!c.isValidate4Update())
      throw new ParamInvalidate();

    Category o_c = get(c.getCid());
    if (null == o_c) {
      log.warn("Entry not exists.");
      throw new BusinessNotAllowed();
    }

    o_c.merge(c);
    Connection conn = beginTransaction();

    try {
      cd.updatePre(conn, c);

    } catch (DaoUnavailable | DaoUnknownError e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new SvcUnavailable();

    } catch (PropertyInvalidate e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new ParamInvalidate();

    } catch (IdNotExist e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new BusinessNotAllowed();
    }

    try {
      clear_category_in_redis();
    } catch (JedisConnectionException e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
    }
    commitTransactionAndRelease(conn);
  }

  private void clear_category_in_redis() throws JedisConnectionException {
    Jedis j = null;
    try {
      j = JedisUtils.getJedis();
      j.del(Const.KEY_4_STORE_CATEGORY_LIST);
    } finally {
      JedisUtils.closeJedis(j);
    }
  }

}
