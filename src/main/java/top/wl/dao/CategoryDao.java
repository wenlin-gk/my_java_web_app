package top.wl.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.exceptions.MysqlErrorNumbers;

import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdExisted;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.dao.utils.MyQueryRunner;
import top.wl.domain.Category;

public class CategoryDao {
  private static Logger log = LogManager.getLogger(CategoryDao.class);
  private MyQueryRunner qr = new MyQueryRunner();

  public Category getById(String cid) throws DaoUnavailable {
    String sql = "select * from category where cid = ?";
    try {
      return qr.query(DataSourceUtils.getConnection(), sql,
          new BeanHandler<>(Category.class), cid);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  public List<Category> getAll() throws DaoUnavailable {
    String sql = "select * from category";
    try {
      return qr.query(DataSourceUtils.getConnection(), sql,
          new BeanListHandler<>(Category.class));
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  /**
   * 
   * @param conn不能为null
   * @param c不能为null，c.cid,c.cname不能为null
   * @throws DaoUnknownError
   * @throws PropertyInvalidate
   * @throws IdExisted
   * @throws DaoUnavailable
   */
  public void addPre(Connection conn, Category c)
      throws DaoUnknownError, PropertyInvalidate, IdExisted, DaoUnavailable {
    String sql = "insert into category values (?,?)";
    int affect = 0;
    try {
      affect = qr.update4Transaction(conn, sql, c.getCid(), c.getCname());
    } catch (SQLException e) {
      log.error(e.getMessage(), e);

      switch (e.getErrorCode()) {
      case MysqlErrorNumbers.ER_DATA_TOO_LONG:// 字段非法
        throw new PropertyInvalidate();
      case MysqlErrorNumbers.ER_DUP_ENTRY:// 主键重复
        throw new IdExisted();
      default:
        throw new DaoUnavailable();
      }
    }

    if (affect != 1) {
      log.error("意外的affect value：" + String.valueOf(affect));
      throw new DaoUnknownError();
    } else {
      log.info("Add category succ. " + c.toString());
    }
  }

  public void deletePre(Connection conn, String cid)
      throws IsReferenced, DaoUnavailable, IdNotExist, DaoUnknownError {
    String sql = "delete from category where cid=?";

    int affect = 0;
    try {
      affect = qr.update4Transaction(conn, sql, cid);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);

      switch (e.getErrorCode()) {
      case MysqlErrorNumbers.ER_ROW_IS_REFERENCED:
      case MysqlErrorNumbers.ER_ROW_IS_REFERENCED_2:// 外键约束
        throw new IsReferenced();
      default:
        throw new DaoUnavailable();
      }
    }
    log.debug(affect);
    if (affect == 1) {
      log.info("Delete succ. cid=" + cid);
    } else if (affect == 0) {
      throw new IdNotExist();
    } else {
      throw new DaoUnknownError();
    }
  }

  public void updatePre(Connection conn, Category c)
      throws DaoUnknownError, PropertyInvalidate, DaoUnavailable, IdNotExist {
    String sql = "update category set cname=? where cid=?";
    int affect = 0;
    try {
      affect = qr.update4Transaction(conn, sql, c.getCname(), c.getCid());
    } catch (SQLException e) {
      log.error(e.getMessage(), e);

      switch (e.getErrorCode()) {
      case MysqlErrorNumbers.ER_DATA_TOO_LONG:// 字段非法
        throw new PropertyInvalidate();
      default:
        throw new DaoUnavailable();
      }
    }

    if (affect == 1) {
      log.info("Update succ. " + c.toString());
    } else if (affect == 0) {// id不存在
      throw new IdNotExist();
    } else {
      throw new DaoUnknownError();
    }
  }

}
