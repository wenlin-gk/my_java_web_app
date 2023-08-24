package top.wl.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
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
import top.wl.domain.User;

public class UserDao {
  private static Logger log = LogManager.getLogger(UserDao.class);
  public MyQueryRunner qr = new MyQueryRunner();
  public BeanHandler<User> beanHandler = new BeanHandler<User>(User.class);
  public ScalarHandler scalarHandler = new ScalarHandler();
  public BeanListHandler<User> beanListHandler = new BeanListHandler<User>(User.class);

  /**
   * @param username
   * @return null代表user不存在
   * @throws DaoUnavailable
   */
  public User getByUsername(String username) throws DaoUnavailable {
    String sql = "select * from user where username = ? limit 1";
    try {
      return qr.query(DataSourceUtils.getConnection(), sql,
          beanHandler, username);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  public User getByCode(String code) throws DaoUnavailable {
    String sql = "select * from user where code = ? limit 1";
    try {
      return qr.query(DataSourceUtils.getConnection(), sql,
          beanHandler, code);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }

  }

  public User getByUsernameAndPwd(String username, String password)
      throws DaoUnavailable {
    String sql = "select * from user where username = ? and password = ? limit 1";
    try {
      return qr.query(DataSourceUtils.getConnection(), sql,
          beanHandler, username, password);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }
  
  public int getCount() throws DaoUnavailable {
    String sql = "select count(*) from user";
    try {
      return ((Long) qr.query(DataSourceUtils.getConnection(), sql,
          scalarHandler)).intValue();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  public List<User> getAll(int startIndex, int pageSize) throws DaoUnavailable {
    String sql = "select * from user order by state desc limit ?,?";
    try {
      return qr.query(DataSourceUtils.getConnection(), sql,
          beanListHandler , startIndex, pageSize);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  public User getByUid(String uid) throws DaoUnavailable {
    String sql = "select * from user where uid = ?";
    try {
      return qr.query(DataSourceUtils.getConnection(), sql,
          beanHandler, uid);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  public void addPre(Connection conn, User user)
      throws DaoUnknownError, PropertyInvalidate, IdExisted, DaoUnavailable {
    String sql = "insert into user values(?,?,?,?,?,?,?,?,?,?);";
    int affect = 0;
    try {
      affect = qr.update4Transaction(conn, sql, user.getUid(),
          user.getUsername(), user.getPassword(), user.getName(),
          user.getEmail(), user.getTelephone(), user.getBirthday(),
          user.getSex(), user.getState(), user.getCode());
      log.debug(affect);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      switch (e.getErrorCode()) {
      case MysqlErrorNumbers.ER_DATA_TOO_LONG:// 字段非法
      case MysqlErrorNumbers.ER_TRUNCATED_WRONG_VALUE_FOR_FIELD:
        throw new PropertyInvalidate();
      case MysqlErrorNumbers.ER_DUP_ENTRY:// 主键重复
        throw new IdExisted();
      default:
        throw new DaoUnavailable();
      }
    }
    if (affect != 1) {
      throw new DaoUnknownError();
    } else {
      log.info("Add user succ. " + user.toString());
    }
  }

  public void update(User user)
      throws PropertyInvalidate, DaoUnavailable, IdNotExist, DaoUnknownError {

    String sql = "update user set username=?, password = ?, name=?, email=?, telephone=?, birthday=?, sex = ?,state = ?,code = ? where uid = ?";
    int affect = 0;
    try {
      affect = qr.update(DataSourceUtils.getConnection(), sql,
          user.getUsername(), user.getPassword(), user.getName(),
          user.getEmail(), user.getTelephone(), user.getBirthday(),
          user.getSex(), user.getState(), user.getCode(), user.getUid());
      log.debug(affect);

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
      log.info("Update succ. " + user.toString());
    } else if (affect == 0) {// id不存在
      throw new IdNotExist();
    } else {
      throw new DaoUnknownError();
    }
  }

  public void delete(String uid)
      throws IsReferenced, DaoUnavailable, IdNotExist, DaoUnknownError {
    String sql = "delete from user where uid=?";

    int affect = 0;
    try {
      affect = qr.update(DataSourceUtils.getConnection(), sql, uid);

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
      log.info("Delete succ. uid=" + uid);
    } else if (affect == 0) {
      throw new IdNotExist();
    } else {
      throw new DaoUnknownError();
    }
  }

}
