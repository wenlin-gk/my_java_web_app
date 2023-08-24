package top.wl.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.exceptions.MysqlErrorNumbers;

import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdExisted;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.NoRefencedRow;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.dao.utils.DataSourceUtils;
import top.wl.dao.utils.MyQueryRunner;
import top.wl.domain.Order;
import top.wl.domain.OrderItem;
import top.wl.domain.PageBean;
import top.wl.domain.Product;

public class OrderDao {

  private static Logger log = LogManager.getLogger(OrderDao.class);
  private MyQueryRunner qr = new MyQueryRunner();

  public int getTotalRecord(String uid) throws DaoUnavailable {
    String sql = "select count(*) from orders where uid = ?";
    try {
      return ((Long) qr.query(DataSourceUtils.getConnection(), sql,
          new ScalarHandler(), uid)).intValue();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  /**
   * 
   * @param s: -1表示无效
   * @return
   * @throws DaoUnavailable
   */
  public List<Order> getAllByState(int s) throws DaoUnavailable {
    if (-1 == s) {
      String sql = "select * from orders order by ordertime desc";
      try {
        return qr.query(DataSourceUtils.getConnection(), sql,
            new BeanListHandler<>(Order.class));
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        throw new DaoUnavailable();
      }

    } else {
      String sql = "select * from orders where state = ? order by ordertime desc";
      try {
        return qr.query(DataSourceUtils.getConnection(), sql,
            new BeanListHandler<>(Order.class), Integer.toString(s));
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        throw new DaoUnavailable();
      }
    }

  }

  public Order getById(String oid) throws DaoUnknownError, DaoUnavailable {
    String sql = "select * from orders where oid = ?";
    Order order = null;
    try {
      order = qr.query(DataSourceUtils.getConnection(), sql,
          new BeanHandler<>(Order.class), oid);
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }

    if (order != null) {
      sql = "SELECT * FROM orderitem oi,product p WHERE oi.pid = p.pid AND oi.oid = ?";
      List<Map<String, Object>> maplist = null;
      try {
        maplist = qr.query(DataSourceUtils.getConnection(), sql,
            new MapListHandler(), oid);
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        throw new DaoUnavailable();
      }
      if (maplist != null) {
        for (Map<String, Object> map : maplist) {
          log.debug(map);
          OrderItem oi = new OrderItem();
          Product p = new Product();
          try {
            BeanUtils.populate(oi, map);
            BeanUtils.populate(p, map);
          } catch (IllegalAccessException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
            throw new DaoUnknownError();
          }

          oi.setProduct(p);
          order.getItems().add(oi);
        }
      }
    }

    return order;
  }

  public List<Order> getAllByPage(PageBean<Order> pb, String uid)
      throws DaoUnknownError, DaoUnavailable {
    String sql = "select * from orders where uid = ? order by ordertime desc limit ?,?";
    List<Order> list = null;
    try {
      list = qr.query(DataSourceUtils.getConnection(), sql,
          new BeanListHandler<>(Order.class), uid, pb.getStartIndex(),
          pb.getPageSize());
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }

    if (null == list)
      return list;

    for (Order order : list) {
      sql = "SELECT * FROM orderitem oi,product p WHERE oi.pid = p.pid AND oi.oid = ?";
      List<Map<String, Object>> maplist = null;
      try {
        maplist = qr.query(DataSourceUtils.getConnection(), sql,
            new MapListHandler(), order.getOid());
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        throw new DaoUnavailable();
      }

      if (null == maplist)
        continue;

      for (Map<String, Object> map : maplist) {
        OrderItem oi = new OrderItem();
        Product p = new Product();

        try {
          BeanUtils.populate(oi, map);
          BeanUtils.populate(p, map);
        } catch (IllegalAccessException | InvocationTargetException e) {
          log.error(e.getMessage(), e);
          throw new DaoUnknownError();
        }

        oi.setProduct(p);
        order.getItems().add(oi);
      }
    }
    return list;
  }

  public void update(Order o) throws PropertyInvalidate, DaoUnavailable,
      IdNotExist, DaoUnknownError, NoRefencedRow {
    String sql = "update orders set total=?, uid=?, state = ?,address = ?,name =?,telephone = ? where oid = ?";
    int affect = 0;
    try {
      affect = qr.update(DataSourceUtils.getConnection(), sql, o.getTotal(),
          o.getUid(), o.getState(), o.getAddress(), o.getName(),
          o.getTelephone(), o.getOid());
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
      log.info("Update succ. " + o.toString());
    } else if (affect == 0) {
      throw new IdNotExist();
    } else {
      throw new DaoUnknownError();
    }
  }

  public void addPre(Connection conn, Order o) throws DaoUnknownError,
      PropertyInvalidate, IdExisted, DaoUnavailable, NoRefencedRow {
    String sql = "insert into orders values(?,?,?,?,?,?,?,?)";
    int affect = 0;
    try {
      affect = qr.update4Transaction(conn, sql, o.getOid(), o.getOrdertime(),
          o.getTotal(), o.getState(), o.getAddress(), o.getName(),
          o.getTelephone(), o.getUser().getUid());

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
      log.info("Add order succ. " + o.toString());
    }
  }

  public void addItemPre(Connection conn, OrderItem oi) throws DaoUnknownError,
      PropertyInvalidate, IdExisted, DaoUnavailable, NoRefencedRow {

    String sql = "insert into orderitem values(?,?,?,?,?)";
    int affect = 0;
    try {
      affect = qr.update4Transaction(conn, sql, oi.getItemid(), oi.getCount(),
          oi.getSubtotal(), oi.getProduct().getPid(), oi.getOrder().getOid());

    } catch (SQLException e) {
      log.error(e.getMessage(), e);

      switch (e.getErrorCode()) {
      case MysqlErrorNumbers.ER_DATA_TOO_LONG:// 字段非法
        throw new PropertyInvalidate();
      case MysqlErrorNumbers.ER_DUP_ENTRY:// 主键重复
        throw new IdExisted();
      case MysqlErrorNumbers.ER_NO_REFERENCED_ROW:
      case MysqlErrorNumbers.ER_NO_REFERENCED_ROW_2:
        throw new NoRefencedRow();
      default:
        throw new DaoUnavailable();
      }
    }

    if (affect != 1) {
      throw new DaoUnknownError();
    } else {
      log.info("Add orderItem succ. " + oi.toString());
    }
  }

  public int getOrderItemCountByPid(String pid) throws DaoUnavailable {
    String sql = "select count(*) from orderitem where pid = ?";
    try {
      return ((Long) qr.query(DataSourceUtils.getConnection(), sql,
          new ScalarHandler(), pid)).intValue();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
  }

  public int delete(String oid) throws DaoUnavailable, IdNotExist, DaoUnknownError {
    String sql = "delete from orders where oid=?";

    int affect = 0;
    try {
      affect = qr.update(DataSourceUtils.getConnection(), sql, oid);

    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw new DaoUnavailable();
    }
    
    log.debug(affect);
    if (affect == 1) {
      log.info("Delete succ. oid=" + oid);
    } else if (affect == 0) {
      throw new IdNotExist();
    } else {
      throw new DaoUnknownError();
    }
    return affect;
  }

}
