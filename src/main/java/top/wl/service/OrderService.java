package top.wl.service;

import java.sql.Connection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.wl.dao.OrderDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdExisted;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.NoRefencedRow;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.domain.Order;
import top.wl.domain.OrderItem;
import top.wl.domain.PageBean;
import top.wl.service.error.BusinessNotAllowed;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcFault;
import top.wl.service.error.SvcUnavailable;

public class OrderService extends BaseService {
  private static Logger log = LogManager.getLogger(OrderService.class);
  OrderDao od = new OrderDao();

  public PageBean<Order> getAllByPage(int pageNumber, int pageSize,
      String uid) throws SvcUnavailable, ParamInvalidate {

    checkValidate(pageNumber, pageSize);

    PageBean<Order> pb = new PageBean<>(pageNumber, pageSize);

    try {
      int totalRecord = od.getTotalRecord(uid);
      pb.setTotalRecord(totalRecord);
      
      List<Order> data = null;
      if(pb.getStartIndex()<totalRecord)
        data = od.getAllByPage(pb, uid);
      pb.setData(data);
    } catch (DaoUnknownError | DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
    return pb;
  }

  public Order getById(String oid) throws SvcUnavailable, ParamInvalidate {
    if(!Order.isIdValidate(oid))
      throw new ParamInvalidate();

    try {
      return od.getById(oid);
    } catch (DaoUnknownError | DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }

  public List<Order> getAllByState(String state)
      throws SvcUnavailable, ParamInvalidate {
    int s = -1;
    if (null != state)
      s = checkOrderState(state);

    try {
      return od.getAllByState(s);
    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }

  public Order add(Order o)
      throws SvcUnavailable, ParamInvalidate, BusinessNotAllowed, SvcFault {
    
    if( !o.isValidate() )
      throw new ParamInvalidate();
    
    Connection conn = beginTransaction();
    try {
      od.addPre(conn, o.revise(Order.ORDER_WEIFUKUAN));

    } catch (DaoUnavailable | DaoUnknownError e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();

    } catch (PropertyInvalidate e) {
      log.error(e.getMessage(), e);
      throw new ParamInvalidate();

    } catch (NoRefencedRow | IdExisted e) {
      log.error(e.getMessage(), e);
      throw new BusinessNotAllowed();
    }

    try {
      for (OrderItem oi : o.getItems()) {
        od.addItemPre(conn, oi);
      }
    } catch (DaoUnavailable | DaoUnknownError e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new SvcUnavailable();

    } catch (NoRefencedRow | IdExisted e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new BusinessNotAllowed();

    } catch (PropertyInvalidate e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new ParamInvalidate();
    }

    commitTransactionAndRelease(conn);
    return o;
  }

  /**
   * 忽略Items
   * @param o
   * @param uid 如果uid==null，不执行用户匹配操作。
   * @return
   * @throws ParamInvalidate
   * @throws SvcUnavailable
   * @throws BusinessNotAllowed
   */
  public Order updateIgnoreItems(Order o, String uid)
      throws ParamInvalidate, SvcUnavailable, BusinessNotAllowed {

    if( !o.isValidate4Update() )
      throw new ParamInvalidate();

    Order o_o = getById(o.getOid());
    if (null == o_o) {
      log.warn("Entry not exists.");
      throw new BusinessNotAllowed();
    }
    if(uid!=null && !o_o.getUid().equals(uid)) {
      log.warn("只允许修改本人的订单。");
      throw new BusinessNotAllowed();
    }

    boolean isChange = o_o.merge(o);
    if (!isChange) {
      return o_o;
    }

    try {
      od.update(o_o);
      return o_o;
    } catch (PropertyInvalidate e) {
      log.error(e.getMessage(), e);
      throw new ParamInvalidate();

    } catch (NoRefencedRow | IdNotExist e) {
      log.error(e.getMessage(), e);
      throw new BusinessNotAllowed();

    } catch (DaoUnavailable | DaoUnknownError e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }

  public void update(String oid, int stat)
      throws SvcUnavailable, ParamInvalidate, BusinessNotAllowed {
    updateIgnoreItems(new Order(oid, stat), null);
  }

  /**
   * @param state不为空
   * @return
   * @throws ParamInvalidate
   */
  private static int checkOrderState(String state) throws ParamInvalidate {
    int s = -1;
    try {
      s = Integer.parseInt(state);
    } catch (NumberFormatException e) {
      log.error(e.getMessage(), e);
      throw new ParamInvalidate();
    }

    if (!Order.isStateValidate(s))
      throw new ParamInvalidate();

    return s;
  }

  public Order pay(Order o, String uid) throws ParamInvalidate, SvcUnavailable, BusinessNotAllowed {
    if( !o.isValidate4Update() )
      throw new ParamInvalidate();

    Order o_o = getById(o.getOid());
    if (null == o_o) {
      log.warn("Entry not exists.");
      throw new BusinessNotAllowed();
    }
    if(uid!=null && !o_o.getUid().equals(uid)) {
      log.warn("只允许修改本人的订单。");
      throw new BusinessNotAllowed();
    }
    if(o_o.getState().equals(Order.ORDER_WEIFUKUAN))
      o.setState(Order.ORDER_YIFUKUAN);
    else {
      log.error("订单不处于待支付状态。");
      throw new BusinessNotAllowed();
    }
    boolean isChange = o_o.merge(o);
    if (!isChange) {
      return o_o;
    }

    try {
      od.update(o_o);
      return o_o;
    } catch (PropertyInvalidate e) {
      log.error(e.getMessage(), e);
      throw new ParamInvalidate();

    } catch (NoRefencedRow | IdNotExist e) {
      log.error(e.getMessage(), e);
      throw new BusinessNotAllowed();

    } catch (DaoUnavailable | DaoUnknownError e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }

}
