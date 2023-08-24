package top.wl.dao;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdExisted;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.domain.User;
import top.wl.jsp.admin.user.EditTest;
import top.wl.utils.UUIDUtils;

public class UserDaoTest {
  private static Logger log = LogManager.getLogger(UserDaoTest.class);
  private static UserDaoTest d = new UserDaoTest();

  @Test
  public void t() throws DaoUnknownError, PropertyInvalidate, IdExisted, DaoUnavailable, SQLException, IsReferenced, IdNotExist, ParseException {
    /*
    date=null, 写入db成功, 从db读出,值为null
    date!=null, 写入db成功, 从db读出,值!=null
    date="2000-01-01"，写入db成功，从db读出，值=="2000-01-01"
     */
    User u = new User();
    u.setUid(UUIDUtils.getId());
    u.setUsername("username");
    u.setPassword("password");
    u.setName("name");
    u.setEmail("email@163.com");
    Connection conn = DataSourceUtils.getConnection();
    try {
      d.addPre(conn, u);
      u = d.getByUid(u.getUid());
      log.info(u);
      assertEquals(null, u.getBirthday());
      d.delete(u.getUid());
    } finally {
      conn.close();
    }
    
    SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String birthday = df.format(new Date());
    u.setBirthday(birthday);
    conn = DataSourceUtils.getConnection();
    try {
      d.addPre(conn, u);
      log.info(u);
      u = d.getByUid(u.getUid());
      log.info(u);
      assertEquals(birthday, u.getBirthday());
      d.delete(u.getUid());
    } finally {
      conn.close();
    }
    
  }
}
