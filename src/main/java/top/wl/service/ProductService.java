package top.wl.service;

import java.awt.Image;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.wl.dao.ProductDao;
import top.wl.dao.error.DaoUnavailable;
import top.wl.dao.error.DaoUnknownError;
import top.wl.dao.error.IdExisted;
import top.wl.dao.error.IdNotExist;
import top.wl.dao.error.IsReferenced;
import top.wl.dao.error.NoRefencedRow;
import top.wl.dao.error.PropertyInvalidate;
import top.wl.domain.Category;
import top.wl.domain.PageBean;
import top.wl.domain.Product;
import top.wl.service.error.BusinessNotAllowed;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcUnavailable;
import top.wl.web.servlet.AdminProductServlet;

public class ProductService extends BaseService {
  private static Logger log = LogManager.getLogger(ProductService.class);
  private ProductDao pd = new ProductDao();

  public List<Product> getHot() throws SvcUnavailable {
    try {
      return pd.getAllByHot();
    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }

  public List<Product> getNew() throws SvcUnavailable {
    try {
      return pd.getAllByNew();
    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }

  public Product getById(String pid) throws SvcUnavailable, ParamInvalidate {
    if(!Product.isIdValidate(pid))
      throw new ParamInvalidate();

    try {
      return pd.getById(pid);
    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }

  // 忽略已下架商品
  public PageBean<Product> getByPage(int pageNumber, int pageSize)
      throws SvcUnavailable, ParamInvalidate {
    checkValidate(pageNumber, pageSize);

    try {
      int totalRecord = pd.getCount(Product.PRODUCT_IS_UP);
      PageBean<Product> pb = new PageBean<>(pageNumber, pageSize, totalRecord);

      List<Product> data = null;
      if(pb.getStartIndex()<totalRecord) 
        data = pd.getAll(Product.PRODUCT_IS_UP, pb.getStartIndex(), pb.getPageSize());
      pb.setData(data);
      return pb;

    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
  }

  public PageBean<Product> getByPage(int pageNumber, int pageSize, String cid)
      throws SvcUnavailable, ParamInvalidate {
    
    if(!Category.isIdValidate(cid))
      throw new ParamInvalidate();
    checkValidate(pageNumber, pageSize);

    PageBean<Product> pb = null;
    try {
      pb = new PageBean<>(pageNumber, pageSize);
      List<Product> data = pd.getByPage(pb, cid);
      pb.setData(data);
      int totalRecord = pd.getTotalRecord(cid);
      pb.setTotalRecord(totalRecord);
    } catch (DaoUnavailable e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    }
    return pb;
  }

  /**
   * @param p各个属性合法
   * @param context
   * @throws SvcUnavailable
   * @throws BusinessNotAllowed
   * @throws ParamInvalidate
   */
  public void add(Product p, String context)
      throws SvcUnavailable, BusinessNotAllowed, ParamInvalidate {

    if( !p.isValidate() )
      throw new ParamInvalidate();
    if( null != p.getPimage() && p.getPimage().length()>0 )
      checkImageValidate(p.getPimage(), context);

    try {
      pd.add(p.revise());

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
  }

  private void checkImageValidate(String image_path, String context) throws ParamInvalidate {
    StringBuffer msg = new StringBuffer();

    if (image_path != null) {
      // 校验图片格式和尺寸
      if (!image_path.toLowerCase().endsWith("jpg")
          && !image_path.toLowerCase().endsWith("png")) {
        msg.append("image扩展名不是JPG|PNG！");
      } else {
        Image image = new ImageIcon(context + image_path).getImage();
        if (image.getWidth(null) > 300 || image.getHeight(null) > 300) {
          msg.append("book_image尺寸超出了300 * 300！");
        }
        log.info(String.format("图片(%s)尺寸(%d*%d)", image_path,
            image.getWidth(null), image.getHeight(null)));
      }
    }

    if (msg.length() > 0) {
      log.error(msg.toString());
      throw new ParamInvalidate();
    }
  }

  public void delete(String pid, String context)
      throws SvcUnavailable, BusinessNotAllowed, ParamInvalidate {
    if(!Product.isIdValidate(pid))
      throw new ParamInvalidate();

    Product p = null;
    try {
      p = pd.getById(pid);
      if(null == p) {
        throw new IdNotExist();
      }
      pd.delete(pid);
      
    } catch (DaoUnavailable | DaoUnknownError e) {
      log.error(e.getMessage(), e);
      throw new SvcUnavailable();
    } catch (IsReferenced | IdNotExist e) {
      log.error(e.getMessage(), e);
      throw new BusinessNotAllowed();
    }
    
    if(p.getPimage() != null)
      AdminProductServlet.deleteFile(new File(context+p.getPimage()));
  }

  public Product update(Product p, String context)
      throws SvcUnavailable, BusinessNotAllowed, ParamInvalidate {
    
    if( !p.isValidate4Update() )
      throw new ParamInvalidate();
    if( null != p.getPimage() && p.getPimage().length()>0 )
      checkImageValidate(p.getPimage(), context);

    Product o_p = getById(p.getPid());
    if (null == o_p) {
      log.warn("Entry not exists.");
      throw new BusinessNotAllowed();
    }
    
    boolean isImageChange = p.getPimage()!=null && ! p.getPimage().equals(o_p.getPimage());
    String o_imagePath = context+o_p.getPimage();
    
    boolean isChange = o_p.merge(p);
    
    if( isChange ) {
      try {
        pd.update(o_p);

        if (isImageChange)
          AdminProductServlet.deleteFile(new File(o_imagePath));

      } catch (DaoUnavailable | DaoUnknownError e) {
        log.error(e.getMessage(), e);
        throw new SvcUnavailable();
        
      } catch (NoRefencedRow | IdNotExist e) {
        log.error(e.getMessage(), e);
        throw new BusinessNotAllowed();
        
      } catch (PropertyInvalidate e) {
        log.error(e.getMessage(), e);
        throw new ParamInvalidate();
      }
    }

    return o_p;
  }
}
