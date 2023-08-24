package top.wl.dao.utils;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

public class MyQueryRunner extends QueryRunner {

  @Override
  public int[] batch(Connection con, String sql, Object[][] params)
      throws SQLException {
    try {
      return super.batch(con, sql, params);
    } finally {
      DataSourceUtils.releaseConnection(con);
    }
  }

  @Override
  public <T> T query(Connection con, String sql, ResultSetHandler<T> rsh,
      Object... params) throws SQLException {
    try {
      return super.query(con, sql, rsh, params);
    } finally {
      DataSourceUtils.releaseConnection(con);
    }
  }

  @Override
  public <T> T query(Connection con, String sql, ResultSetHandler<T> rsh)
      throws SQLException {
    try {
      return super.query(con, sql, rsh);
    } finally {
      DataSourceUtils.releaseConnection(con);
    }
  }

  @Override
  public int update(Connection con, String sql, Object params)
      throws SQLException {
    try {
      return super.update(con, sql, params);
    } finally {
      DataSourceUtils.releaseConnection(con);
    }
  }

  @Override
  public int update(Connection con, String sql, Object... params)
      throws SQLException {
    try {
      return super.update(con, sql, params);
    } finally {
      DataSourceUtils.releaseConnection(con);
    }
  }

  public int update4Transaction(Connection con, String sql, Object params)
      throws SQLException {
    return super.update(con, sql, params);
  }
  public int update4Transaction(Connection con, String sql, Object... params)
      throws SQLException {
    return super.update(con, sql, params);
  }
}
