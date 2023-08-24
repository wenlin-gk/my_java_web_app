package top.wl.service;

import java.sql.Connection;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.wl.Const;
import top.wl.dao.UserDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdExisted;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.domain.PageBean;
import top.wl.domain.User;
import top.wl.service.error.BusinessNotAllowed;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcFault;
import top.wl.service.error.SvcUnavailable;
import top.wl.utils.MailUtils;

public class UserService extends BaseService {
  private static Logger log = LogManager.getLogger(UserService.class);
  public UserDao ud = new UserDao();
  
  public PageBean<User> getByPage(int pageNumber, int pageSize)
      throws SvcUnavailable, ParamInvalidate {
    checkValidate(pageNumber, pageSize);
    
    try {
      int totalRecord = ud.getCount();
      PageBean<User> pb = new PageBean<>(pageNumber, pageSize, totalRecord);

      List<User> data = null;
      if(pb.getStartIndex()<totalRecord) 
        data = ud.getAll(pb.getStartIndex(), pb.getPageSize());
      pb.setData(data);
      return pb;

    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }

  }

  public User getByUsername(String username)
      throws SvcUnavailable, ParamInvalidate {
    if(!User.isNameValidate(username))
      throw new ParamInvalidate();

    try {
      return ud.getByUsername(username);
    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }

  public User getByUid(String uid) throws SvcUnavailable, ParamInvalidate {
    if(!User.isIdValidate(uid))
      throw new ParamInvalidate();

    try {
      return ud.getByUid(uid);
    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }

  public void add(User user)
      throws SvcUnavailable, BusinessNotAllowed, SvcFault, ParamInvalidate {
    if( !user.isValidate() )
      throw new ParamInvalidate();

    Connection conn = beginTransaction();
    try {
      ud.addPre(conn, user.revise());

    } catch (IdExisted e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new BusinessNotAllowed();

    } catch (PropertyInvalidate e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new ParamInvalidate();

    } catch (DaoUnavailable | DaoUnknownError e) {
      log.error(e.getMessage(), e);
      rollbackTransactionAndRelease(conn);
      throw new SvcUnavailable();
    }

    String emailMsg = "恭喜" + user.getName() + ":成为我们商城的一员,<a href='"
        + Const.INGRESS + "/user?method=active&code=" + user.getCode()
        + "'>点此激活</a>";
    try {
      MailUtils.sendMail(user.getEmail(), emailMsg);
    } catch (MessagingException e) {
      log.error(e.getMessage(), e);

      rollbackTransactionAndRelease(conn);
      throw new SvcUnavailable();
    }

    commitTransactionAndRelease(conn);
  }

  public User active(String code)
      throws SvcUnavailable, BusinessNotAllowed, ParamInvalidate {
    if(!User.isCodeValidate(code))
      throw new ParamInvalidate();

    User user = null;
    try {
      user = ud.getByCode(code);

    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }

    if (user == null)
      return null;

    user.setState(User.USER_IS_ACTIVE);
    user.setCode(null);

    try {
      ud.update(user);

    } catch (DaoUnavailable | DaoUnknownError e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();

    } catch (IdNotExist e) {
      log.error(e.getMessage(), e);
      throw new BusinessNotAllowed();

    } catch (PropertyInvalidate e) {
      log.error(e.getMessage(), e);
      throw new ParamInvalidate();
    }

    return user;
  }

  public User login(String username, String password) throws SvcUnavailable, ParamInvalidate {
    if(!User.isNameValidate(username) || !User.isNameValidate(password))
      throw new ParamInvalidate();

    try {
      return ud.getByUsernameAndPwd(username, password);
    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }

  public User update(User user)
      throws SvcUnavailable, BusinessNotAllowed, ParamInvalidate {
    if( !user.isValidate4Update() )
      throw new ParamInvalidate();

    User o_u = null;
    try {
      o_u = ud.getByUid(user.getUid());
    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
    if (null == o_u) {
      log.warn("Entry not exists.");
      throw new BusinessNotAllowed();
    }

    boolean isChange = o_u.merge(user);
    if (isChange) {
      try {
        ud.update(o_u);
      } catch (DaoUnavailable | DaoUnknownError e) {
        log.error(e.getMessage(), e);
        throw new SvcUnavailable();
        
      } catch (IdNotExist e) {
        log.error(e.getMessage(), e);
        throw new BusinessNotAllowed();
        
      } catch (PropertyInvalidate e) {
        log.error(e.getMessage(), e);
        throw new ParamInvalidate();
      }
    } else {
      log.info("Not change.");
    }

    return o_u;
  }

  public void delete(String uid)
      throws SvcUnavailable, BusinessNotAllowed, ParamInvalidate {
    if(!User.isIdValidate(uid))
      throw new ParamInvalidate();

    try {
      ud.delete(uid);
    } catch (DaoUnavailable | DaoUnknownError e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();

    } catch (IsReferenced | IdNotExist e) {
      log.error(e.getMessage(), e);
      throw new BusinessNotAllowed();
    }
  }

}
