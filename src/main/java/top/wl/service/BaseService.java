package top.wl.service;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.wl.dao.utils.DataSourceUtils;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcFault;
import top.wl.service.error.SvcUnavailable;

public class BaseService {
  private static Logger log = LogManager.getLogger(BaseService.class);

  protected static void checkValidate(int... values) throws ParamInvalidate {
    for (int v : values) {
      if (v <= 0)
        throw new ParamInvalidate();
    }
  }

  /**
   * 
   * @return 返回的conn不为null
   * @throws SvcUnavailable
   */
  protected static Connection beginTransaction() throws SvcUnavailable {
    Connection conn = null;

    try {
      conn = DataSourceUtils.getConnection();
      DataSourceUtils.beginTransaction(conn);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      DataSourceUtils.releaseConnection(conn);
      throw new SvcUnavailable();
    }

    return conn;
  }

  /**
   * 
   * @param conn 不能为null
   * @throws SvcFault
   */
  protected static void rollbackTransactionAndRelease(Connection conn)
      throws SvcFault {
    try {
      conn.rollback();
    } catch (SQLException e) {
      log.fatal(e.getMessage(), e);
      throw new SvcFault();
    } finally {
      DataSourceUtils.releaseConnection(conn);
    }
  }

  /**
   * 
   * @param conn 不能为null
   * @throws SvcFault
   */
  protected static void commitTransactionAndRelease(Connection conn)
      throws SvcFault {
    try {
      conn.commit();
    } catch (SQLException e) {
      log.fatal(e.getMessage(), e);
      throw new SvcFault();
    } finally {
      DataSourceUtils.releaseConnection(conn);
    }
  }

}
