package top.wl.dao;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
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
import top.wl.dao.error.NoRefencedRow;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.domain.Product;

public class ProductDaoTest {
  private static Logger log = LogManager.getLogger(ProductDaoTest.class);
  private static ProductDao d = new ProductDao();
  @Test
  public void t() throws DaoUnknownError, PropertyInvalidate, IdExisted, DaoUnavailable, SQLException, IsReferenced, IdNotExist, ParseException, NoRefencedRow {
    /*
    date="2000-01-01"，写入db成功，从db读出，值=="2000-01-01"
     */
    SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
    
    Product p = d.getAllByHot().get(0);
    log.info(p);
    p.setPdate(dtf.format(new Date()));
    d.update(p);
    Product newP = d.getById(p.getPid());
    log.info(p);
    assertEquals(p.getPdate(), newP.getPdate());
  }
}
