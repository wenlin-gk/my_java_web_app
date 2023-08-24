package top.wl.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import javax.mail.MessagingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import top.wl.dao.UserDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdExisted;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.domain.User;
import top.wl.service.error.BusinessNotAllowed;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcFault;
import top.wl.service.error.SvcUnavailable;
import top.wl.utils.MailUtils;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  @Test
  public void test_add() {
    // user无效，raise ParamInvalidate
    User u = Mockito.mock(User.class);
    Mockito.when(u.isValidate()).thenReturn(false);

    UserService us = new UserService();

    try {
      us.add(u);
      assertFalse(true);
    } catch (Exception e) {
      assertTrue(e instanceof ParamInvalidate);
    }
  }

  @Test
  public void test_add1() {
    // db连接失败/开启事务，raise SvcUnavailable
    try (MockedStatic<DataSourceUtils> mocked = Mockito
        .mockStatic(DataSourceUtils.class)) {
      mocked.when(DataSourceUtils::getConnection).thenThrow(SQLException.class);

      User u = Mockito.mock(User.class);
      Mockito.when(u.isValidate()).thenReturn(true);

      UserService us = new UserService();

      try {
        us.add(u);
        assertFalse(true);
      } catch (Exception e) {
        assertTrue(e instanceof SvcUnavailable);
      }
    }
  }

  @Test
  public void test_add2()
      throws DaoUnknownError, PropertyInvalidate, IdExisted, DaoUnavailable {
    // user id冲突，raise BusinessNotAllowed
    try (MockedStatic<DataSourceUtils> mocked = Mockito
        .mockStatic(DataSourceUtils.class)) {
      Connection conn = Mockito.mock(Connection.class);
      mocked.when(DataSourceUtils::getConnection).thenReturn(conn);

      User u = Mockito.mock(User.class);
      Mockito.when(u.revise()).thenReturn(null);
      Mockito.when(u.isValidate()).thenReturn(true);
      UserService us = new UserService();
      us.ud = Mockito.mock(UserDao.class);
      Mockito.doThrow(new IdExisted()).when(us.ud).addPre(
          ArgumentMatchers.argThat(e -> e.equals(conn)),
          ArgumentMatchers.argThat(e -> e == null));
      try {
        us.add(u);
        assertFalse(true);
      } catch (Exception e) {
        assertTrue(e instanceof BusinessNotAllowed);
      }

      Mockito.verify(u).isValidate();
      mocked.verify(DataSourceUtils::getConnection);
      Mockito.verify(us.ud).addPre(conn, null);
    }
  }

  @Test
  public void test_add3()
      throws DaoUnknownError, PropertyInvalidate, IdExisted, DaoUnavailable {
    // db操作不可用，raise SvcUnavailable
    try (MockedStatic<DataSourceUtils> mocked = Mockito
        .mockStatic(DataSourceUtils.class)) {
      Connection conn = Mockito.mock(Connection.class);
      mocked.when(DataSourceUtils::getConnection).thenReturn(conn);

      User u = Mockito.mock(User.class);
      Mockito.when(u.revise()).thenReturn(null);
      Mockito.when(u.isValidate()).thenReturn(true);
      UserService us = new UserService();
      us.ud = Mockito.mock(UserDao.class);
      Mockito.doThrow(new DaoUnavailable()).when(us.ud).addPre(
          ArgumentMatchers.argThat(e -> e.equals(conn)),
          ArgumentMatchers.argThat(e -> e == null));
      try {
        us.add(u);
        assertFalse(true);
      } catch (Exception e) {
        assertTrue(e instanceof SvcUnavailable);
      }

      Mockito.verify(u).isValidate();
      mocked.verify(DataSourceUtils::getConnection);
      Mockito.verify(us.ud).addPre(conn, null);
    }
  }

  @Test
  public void test_add4() throws DaoUnknownError, PropertyInvalidate, IdExisted,
      DaoUnavailable, SvcUnavailable, BusinessNotAllowed, SvcFault,
      ParamInvalidate, SQLException {
    // 邮件发送失败，db操作回滚，raise SvcUnavailable
    try (MockedStatic<DataSourceUtils> mocked = Mockito
        .mockStatic(DataSourceUtils.class)) {
      Connection conn = Mockito.mock(Connection.class);
      mocked.when(DataSourceUtils::getConnection).thenReturn(conn);

      User u = Mockito.mock(User.class);
      Mockito.when(u.revise()).thenReturn(null);
      Mockito.when(u.isValidate()).thenReturn(true);
      UserService us = new UserService();
      us.ud = Mockito.mock(UserDao.class);

      try (MockedStatic<MailUtils> mockedMail = Mockito
          .mockStatic(MailUtils.class)) {
        Mockito.when(u.getEmail()).thenReturn(null);
        mockedMail
            .when(() -> MailUtils.sendMail(
                ArgumentMatchers.argThat(e -> e == null),
                ArgumentMatchers.argThat(e -> e instanceof String)))
            .thenThrow(MessagingException.class);
        try {
          us.add(u);
          assertFalse(true);
        } catch (Exception e) {
          assertTrue(e instanceof SvcUnavailable);
        }
      }

      Mockito.verify(conn).rollback();
      Mockito.verify(u).isValidate();
      mocked.verify(DataSourceUtils::getConnection);
      Mockito.verify(us.ud).addPre(conn, null);
    }
  }

  @Test
  public void test_add5() throws DaoUnknownError, PropertyInvalidate, IdExisted,
      DaoUnavailable, SvcUnavailable, BusinessNotAllowed, SvcFault,
      ParamInvalidate, SQLException {
    // user添加成功。
    try (MockedStatic<DataSourceUtils> mocked = Mockito
        .mockStatic(DataSourceUtils.class)) {
      Connection conn = Mockito.mock(Connection.class);
      mocked.when(DataSourceUtils::getConnection).thenReturn(conn);

      User u = Mockito.mock(User.class);
      Mockito.when(u.revise()).thenReturn(null);
      Mockito.when(u.isValidate()).thenReturn(true);
      UserService us = new UserService();
      us.ud = Mockito.mock(UserDao.class);

      try (MockedStatic<MailUtils> mockedMail = Mockito
          .mockStatic(MailUtils.class)) {
        Mockito.when(u.getEmail()).thenReturn(null);
        us.add(u);

        Mockito.verify(conn).commit();
        Mockito.verify(u).isValidate();
        mocked.verify(() -> DataSourceUtils.getConnection());
        mocked.verify(() -> DataSourceUtils.releaseConnection(conn));
        Mockito.verify(us.ud).addPre(conn, null);
        mockedMail.verify(
            () -> MailUtils.sendMail(ArgumentMatchers.argThat(e -> e == null),
                ArgumentMatchers.argThat(e -> e instanceof String)));
      }
    }
  }

}
