package top.wl.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.mysql.cj.exceptions.MysqlErrorNumbers;

import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdExisted;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.dao.utils.MyQueryRunner;
import top.wl.domain.User;

@RunWith(MockitoJUnitRunner.class)
public class UserDaoTest {
  private static Logger log = LogManager.getLogger(UserDaoTest.class);
  @Test
  public void test_addPre() throws SQLException, DaoUnavailable, DaoUnknownError, PropertyInvalidate, IdExisted {
    // db提示字段非法，抛出异常PropertyInvalidate
    _test_db_err(MysqlErrorNumbers.ER_DATA_TOO_LONG,
        PropertyInvalidate.class);
  }

  @Test
  public void test_addPre1() throws SQLException, DaoUnavailable, DaoUnknownError, PropertyInvalidate, IdExisted {
    // db提示主键重复，抛出异常IdExisted。
    _test_db_err(MysqlErrorNumbers.ER_DUP_ENTRY,
        IdExisted.class);
  }

  @Test
  public void test_addPre2() throws SQLException, DaoUnavailable, DaoUnknownError, PropertyInvalidate, IdExisted {
    // db故障，抛出异常DaoUnavailable。
    _test_db_err(MysqlErrorNumbers.ER_STARTUP,
        DaoUnavailable.class);
  }

  private <T> void _test_db_err(int errCode, Class<T> clazz) throws SQLException {
    Connection conn = Mockito.mock(Connection.class);
    User u = new User();
    UserDao ud = new UserDao();
    MyQueryRunner qr = Mockito.mock(MyQueryRunner.class);
    ud.qr = qr;
    
    Mockito.when(qr.update4Transaction(
        ArgumentMatchers.argThat(e -> e == conn),
        ArgumentMatchers.argThat(e -> e instanceof String),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null))).thenThrow(
            new SQLException("","",errCode));
  
    try {
      ud.addPre(conn, u);
      assertFalse(true);
    } catch (Exception e) {
      assertTrue(e.getClass()==clazz);
    }
    
    Mockito.verify(qr).update4Transaction(
        ArgumentMatchers.argThat(e -> e == conn),
        ArgumentMatchers.argThat(e -> e instanceof String),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null));
  }

  @Test
  public void test_addPre3() throws SQLException, DaoUnavailable, DaoUnknownError, PropertyInvalidate, IdExisted {
    // affect != 1，抛出异常DaoUnknownError
    Connection conn = Mockito.mock(Connection.class);
    User u = new User();
    UserDao ud = new UserDao();
    MyQueryRunner qr = Mockito.mock(MyQueryRunner.class);
    ud.qr = qr;
    
    Mockito.when(qr.update4Transaction(
        ArgumentMatchers.argThat(e -> e == conn),
        ArgumentMatchers.argThat(e -> e instanceof String),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null))).thenReturn(0);

    try {
      ud.addPre(conn, u);
      assertFalse(true);
    } catch (Exception e) {
      assertTrue(e instanceof DaoUnknownError);
    }
    
    Mockito.verify(qr).update4Transaction(
        ArgumentMatchers.argThat(e -> e == conn),
        ArgumentMatchers.argThat(e -> e instanceof String),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null));
  }

  @Test
  public void test_addPre4() throws SQLException, DaoUnavailable, DaoUnknownError, PropertyInvalidate, IdExisted {
    // 添加成功。
    Connection conn = Mockito.mock(Connection.class);
    User u = new User();
    UserDao ud = new UserDao();
    MyQueryRunner qr = Mockito.mock(MyQueryRunner.class);
    ud.qr = qr;
    
    Mockito.when(qr.update4Transaction(
        ArgumentMatchers.argThat(e -> e == conn),
        ArgumentMatchers.argThat(e -> e instanceof String),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null))).thenReturn(1);
    
    ud.addPre(conn, u);
    
    Mockito.verify(qr).update4Transaction(
        ArgumentMatchers.argThat(e -> e == conn),
        ArgumentMatchers.argThat(e -> e instanceof String),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null),
        ArgumentMatchers.argThat(e -> e == null));
  }

}
