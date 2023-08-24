package top.wl.dao.utils;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import top.wl.Const;

public class DataSourceUtils {
  private static Logger log = LogManager.getLogger(DataSourceUtils.class);
  public static ComboPooledDataSource ds = null;

  static {
    ds = new ComboPooledDataSource();

    // 两种配置方式：c3p0-config.xml和api。两种方式不能混合。
    try {
      ds.setDriverClass("com.mysql.cj.jdbc.Driver");
    } catch (PropertyVetoException e) {
      log.error(e.getMessage(), e);
    }

    ds.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", Const.MYSQL_HOST,
        Const.MYSQL_PORT, Const.STORE_DATABASE));
    ds.setUser("root");
    ds.setPassword(Const.MYSQL_ROOT_PASSWORD);

    ds.setInitialPoolSize(10);
    ds.setAcquireIncrement(3);
    ds.setMinPoolSize(10);
    ds.setMaxPoolSize(10);

    ds.setAcquireRetryAttempts(3);
    ds.setAcquireRetryDelay(10);

  }

  public static Connection getConnection() throws SQLException {
    Connection conn = ds.getConnection();
    log.info(conn);
    return conn;// 多线程安全
  }

  /**
   * 
   * @param conn 不能为空
   * @throws SQLException
   */
  public static void beginTransaction(Connection conn) throws SQLException {
    conn.setAutoCommit(false);// 设置为手动提交
  }

  /**
   * 
   * @param conn 不能为空
   * @throws SQLException
   */
  public static void releaseConnection(Connection conn) {
    try {
      if (!conn.isClosed()) {
        log.info("Close connection: " + conn.toString());
        conn.close();
      } else {
        log.warn("Connection already closed." + conn.toString());
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      // TODO: 启动线程关闭未成功关闭的conn
    } finally {
      try {
        String msg = String.format("Connnect Info, Busy-%s, Count-%s, idle-%s, unclose-%s",
            ds.getNumBusyConnections(),
            ds.getNumConnections(),
            ds.getNumIdleConnections(),
            ds.getNumUnclosedOrphanedConnections());
        log.debug(msg);
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
      }
    }
  }
}
