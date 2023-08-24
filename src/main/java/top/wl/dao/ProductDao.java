package top.wl.dao;

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
import top.wl.dao.error.NoRefencedRow;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.dao.utils.MyQueryRunner;
import top.wl.domain.PageBean;
import top.wl.domain.Product;

public class ProductDao {
  
  private static Logger log = LogManager.getLogger(ProductDao.class);
  private MyQueryRunner qr = new MyQueryRunner();

  public List<Product> getAllByHot() throws DaoUnavailable {
    String sql = "select * from product where is_hot = ? and pflag = ? order by pdate desc limit 9";
    try {
      return qr.query(DataSourceUtils.getConnection(), sql,
          new BeanListHandler<>(Product.class), Product.PRODUCT_IS_HOT,
          Product.PRODUCT_IS_UP);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  public List<Product> getAllByNew() throws DaoUnavailable {
    String sql = "select * from product where pflag = ? order by pdate desc limit 9";
    try {
      return qr.query(DataSourceUtils.getConnection(), sql,
          new BeanListHandler<>(Product.class), Product.PRODUCT_IS_UP);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  public List<Product> getAllByPflag() throws DaoUnavailable {
    String sql = "select * from product where pflag = ? order by pdate desc";
    try {
      return qr.query(DataSourceUtils.getConnection(), sql,
          new BeanListHandler<>(Product.class), Product.PRODUCT_IS_UP);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  /**
   * 
   * @param productflag: 取值范围[-1,0,1]
   * @return
   * @throws DaoUnavailable
   */
  public int getCount(int productflag) throws DaoUnavailable {
    if (productflag != Product.PRODUCT_IS_DOWN
        && productflag != Product.PRODUCT_IS_UP) {
      try {
        return ((Long) qr.query(DataSourceUtils.getConnection(),
            "select count(*) from product", new ScalarHandler())).intValue();
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        throw new DaoUnavailable();
      }
    } else {
      try {
        return ((Long) qr.query(DataSourceUtils.getConnection(),
            "select count(*) from product where pflag = ?", new ScalarHandler(),
            productflag)).intValue();
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        throw new DaoUnavailable();
      }
    }
  }

  public int getCount() throws DaoUnavailable {
    return this.getCount(-1);
  }

  /**
   * 
   * @param productflag取值范围[-1,0,1]
   * @param startIndex
   * @param pageSize
   * @return
   * @throws DaoUnavailable
   */
  public List<Product> getAll(int productflag, int startIndex, int pageSize)
      throws DaoUnavailable {
    if (productflag != Product.PRODUCT_IS_DOWN
        && productflag != Product.PRODUCT_IS_UP) {
      String sql = "select * from product order by pdate desc limit ?,?";
      try {
        return qr.query(DataSourceUtils.getConnection(), sql,
            new BeanListHandler<>(Product.class), startIndex, pageSize);
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        throw new DaoUnavailable();
      }
    } else {
      String sql = "select * from product where pflag = ? order by pdate desc limit ?,?";
      try {
        return qr.query(DataSourceUtils.getConnection(), sql,
            new BeanListHandler<>(Product.class), productflag, startIndex,
            pageSize);
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        throw new DaoUnavailable();
      }
    }
  }

  public Product getById(String pid) throws DaoUnavailable {
    String sql = "select * from product where pid = ?";
    try {
      return qr.query(DataSourceUtils.getConnection(), sql,
          new BeanHandler<>(Product.class), pid);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  public List<Product> getByPage(PageBean<Product> pb, String cid)
      throws DaoUnavailable {
    String sql = "select * from product where cid = ? and pflag = ? order by pdate desc limit ?,?";
    try {
      return qr.query(DataSourceUtils.getConnection(), sql,
          new BeanListHandler<>(Product.class), cid, Product.PRODUCT_IS_UP,
          pb.getStartIndex(), pb.getPageSize());
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  public int getTotalRecord(String cid) throws DaoUnavailable {
    try {
      return ((Long) qr.query(DataSourceUtils.getConnection(),
          "select count(*) from product where cid = ? and pflag = ?",
          new ScalarHandler(), cid, Product.PRODUCT_IS_UP)).intValue();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  public void add(Product p) throws DaoUnknownError, PropertyInvalidate,
      IdExisted, DaoUnavailable, NoRefencedRow {
    String sql = "insert into product values(?,?,?,?,?,?,?,?,?,?);";
    int affect = 0;
    try {
      affect = qr.update(DataSourceUtils.getConnection(), sql, p.getPid(),
          p.getPname(), p.getMarket_price(), p.getShop_price(), p.getPimage(),
          p.getPdate(), p.getIs_hot(), p.getPdesc(), p.getPflag(),
          p.getCategory().getCid());
    } catch (SQLException e) {
      log.error(e.getMessage(), e);

      switch (e.getErrorCode()) {
      case MysqlErrorNumbers.ER_DATA_TOO_LONG:// 字段非法
        throw new PropertyInvalidate();
      case MysqlErrorNumbers.ER_DUP_ENTRY:// 主键重复
        throw new IdExisted();
      case MysqlErrorNumbers.ER_NO_REFERENCED_ROW:
        throw new NoRefencedRow();
      default:
        throw new DaoUnavailable();
      }
    }
    if (affect != 1) {
      throw new DaoUnknownError();
    } else {
      log.info("Add product succ. " + p.toString());
    }
  }

  public void delete(String pid)
      throws IsReferenced, DaoUnavailable, IdNotExist, DaoUnknownError {

    String sql = "delete from product where pid=?";
    int affect = 0;
    try {
      affect = qr.update(DataSourceUtils.getConnection(), sql, pid);
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
      log.info("Delete succ. pid=" + pid);
    } else if (affect == 0) {
      throw new IdNotExist();
    } else {
      throw new DaoUnknownError();
    }
  }

  public void update(Product p) throws DaoUnknownError, PropertyInvalidate,
      IdNotExist, DaoUnavailable, NoRefencedRow {
    String sql = "update product set cid=?, is_hot=?, market_price=?, pdate=?, pdesc=?, pflag=?, pimage=?, shop_price=?, pname=? where pid=?";
    int affect = 0;
    try {
      affect = qr.update(DataSourceUtils.getConnection(), sql, p.getCid(),
          p.getIs_hot(), p.getMarket_price(), p.getPdate(), p.getPdesc(),
          p.getPflag(), p.getPimage(), p.getShop_price(), p.getPname(),
          p.getPid());
      log.debug(affect);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);

      switch (e.getErrorCode()) {
      case MysqlErrorNumbers.ER_DATA_TOO_LONG:// 字段非法
        throw new PropertyInvalidate();
      case MysqlErrorNumbers.ER_NO_REFERENCED_ROW:
        throw new NoRefencedRow();
      default:
        throw new DaoUnavailable();
      }
    }

    if (affect == 1) {
      log.info("Update succ. " + p.toString());
    } else if (affect == 0) {// id不存在
      throw new IdNotExist();
    } else {
      throw new DaoUnknownError();
    }
  }

}
