package top.wl.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.wl.domain.PageBean;
import top.wl.domain.Product;
import top.wl.service.CategoryService;
import top.wl.service.ProductService;
import top.wl.service.error.BusinessNotAllowed;
import top.wl.service.error.ParamInvalidate;
import top.wl.service.error.SvcUnavailable;
import top.wl.utils.UUIDUtils;


public class AdminProductServlet extends BaseServlet {
  public class FileFeildNameErr extends Exception {

  }

  public class SaveFileErr extends Exception {

  }

  public class NotMultipartTypeErr extends Exception {
  }

  private static final long serialVersionUID = 1L;

  private static Logger log = LogManager.getLogger(AdminProductServlet.class);
  private static ProductService ps = new ProductService();
  private static CategoryService cs = new CategoryService();

  public static final String errMsg4editUIPI = "ID为空 或者 太长：ID='%s'。要求1-32个字符。";
  
  public static final String errMsg4editUINA ="商品不存在。(id=%s)";

  public static final String errMsg4PageNumInvidate = "页码<0或者超出尾页码：pageNumber=%s。要求pageNumber>0";

  public static final String errMsg4deletePI = "ID为空 或者 太长：ID='%s'。要求1-20个字符。";

  public static final String errMsg4deleteNA = "商品不存在 或者 商品被其他实体引用不允许删除。(ID=%s)";

  public static final String errMsg4addPI = "商品属性不合法。%s";

  public static final String errMsg4addNA = "商品所属的分类不存在 或者 ID已经被占用。%s";

  public static final String errMsg4updatePI = errMsg4addPI;

  public static final String errMsg4updateNA = "商品所属的分类不存在 或者 ID不存在。%s";

  public static final int pageSize = 6;

  public static final String NotMultipartType = "Content type is not start with multipart.";

  public static final String FileUploadExceptionMsg = "字段大小过大，或者图片字段名称不是pimage";

  public String editUI(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Product p = null;
    try {
      request.setAttribute("list", cs.getAll());
      p = ps.getById(getParam(request, "pid"));

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4editUIPI, getParam(request, "pid")));
      return BaseServlet.msgPage;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }

    if(null==p) {
      setMsg(request, String.format(errMsg4editUINA, getParam(request, "pid")));
      return BaseServlet.msgPage;
    }
    request.setAttribute("p", p);
    return "/admin/product/edit.jsp";
  }

  public String addUI(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    try {
      request.setAttribute("list", cs.getAll());
    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }
    return "/admin/product/add.jsp";
  }

  public String getByPage(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    int pageNumber = 1;
    try {
      pageNumber = Integer.parseInt(getParam(request, "pageNumber"));
    } catch (NumberFormatException e) {
      log.error(e.getMessage(), e);
    }

    PageBean<Product> bean = null;
    try {
      bean = ps.getByPage(pageNumber, pageSize);

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;

    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4PageNumInvidate, pageNumber));
      return BaseServlet.msgPage;

    }

    request.setAttribute("pb", bean);
    return "/admin/product/list.jsp";
  }

  public String delete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String id = getParam(request, "pid");
    try {
      ps.delete(id, genRealPath("/"));
    } catch (ParamInvalidate e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4deletePI, id));
      return BaseServlet.msgPage;

    } catch (BusinessNotAllowed e) {
      log.error(e.getMessage(), e);
      setMsg(request, String.format(errMsg4deleteNA, id));
      return BaseServlet.msgPage;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      setMsg(request, serviceUnavailable);
      return BaseServlet.msgPage;
    }

    response.sendRedirect(
        request.getContextPath() + "/admin/product?method=getByPage");
    return null;
  }

  public String add(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Map<String, String> params = null;
    try {
      params = parseParams(request);
      log.debug(params);
      
    } catch (NotMultipartTypeErr | UnsupportedEncodingException
        | FileUploadException | FileFeildNameErr | SaveFileErr e) {
      log.error(e.getMessage(), e);

      if (e instanceof NotMultipartTypeErr) {
        this.setMsg(request, NotMultipartType);
      } else if (e instanceof FileUploadException || e instanceof FileFeildNameErr) {
        this.setMsg(request, FileUploadExceptionMsg);
      } else {
        this.setMsg(request, serviceUnavailable);
      }

      return BaseServlet.msgPage;
    }

    Product p = new Product();
    try {
      BeanUtils.populate(p, params);
      ps.add(p, genRealPath("/"));
  
    } catch (Exception e) {
      log.error(e.getMessage(), e);
  
      if(params.get("pimage") != null)
        deleteFile(new File(params.get("pimage")));
      
      if (e instanceof ParamInvalidate ||
          e instanceof IllegalAccessException ||
          e instanceof InvocationTargetException) {
        this.setMsg(request, String.format(errMsg4addPI, p));
      } else if (e instanceof BusinessNotAllowed) {
        this.setMsg(request, String.format(errMsg4updateNA, p));
      } else {
        this.setMsg(request, serviceUnavailable);
      }
  
      return BaseServlet.msgPage;
    } 
    response.sendRedirect(request.getContextPath()
        + "/admin/product?method=getByPage&pageNumber=1");
    return null;
  }

  public String update(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    Map<String, String> params = null;
    try {
      params = parseParams(request);
      log.debug(params);
      
    } catch (NotMultipartTypeErr | UnsupportedEncodingException
        | FileUploadException | FileFeildNameErr | SaveFileErr e) {
      log.error(e.getMessage(), e);
  
      if (e instanceof NotMultipartTypeErr) {
        this.setMsg(request, NotMultipartType);
      } else if (e instanceof FileUploadException || e instanceof FileFeildNameErr) {
        this.setMsg(request, FileUploadExceptionMsg);
      } else {
        this.setMsg(request, serviceUnavailable);
      }
  
      return BaseServlet.msgPage;
    }
  
    Product p = new Product();
    try {
      BeanUtils.populate(p, params);
      ps.update(p, genRealPath("/"));
  
    } catch (Exception e) {
      log.error(e.getMessage(), e);
  
      if(params.get("pimage") != null)
        deleteFile(new File(params.get("pimage")));
      
      if (e instanceof ParamInvalidate ||
          e instanceof IllegalAccessException ||
          e instanceof InvocationTargetException) {
        this.setMsg(request, String.format(errMsg4updatePI, p));
      } else if (e instanceof BusinessNotAllowed) {
        this.setMsg(request, String.format(errMsg4updateNA, p));
      } else {
        this.setMsg(request, serviceUnavailable);
      }
  
      return BaseServlet.msgPage;
    } 
    
    response.sendRedirect(request.getContextPath()
        + "/admin/product?method=getByPage&pageNumber=1");
    return null;
  }

  private Map<String, String> parseParams(HttpServletRequest request)
      throws NotMultipartTypeErr, FileUploadException,
      UnsupportedEncodingException, FileFeildNameErr, SaveFileErr {
    
    if ( ! ServletFileUpload.isMultipartContent(request)){
      throw new NotMultipartTypeErr();
    }
    
    DiskFileItemFactory factory = new DiskFileItemFactory();
    factory.setSizeThreshold(30*1024);
    ServletFileUpload upload = new ServletFileUpload(factory);
    upload.setSizeMax(20*1024);
    upload.setSizeMax(25*1024);
    List<FileItem> items = upload.parseRequest(request);
  
    if(null == items)
      throw new FileUploadException();
    
    Map<String, String> params = new HashMap<>();
  
    for (FileItem fi : items) {
      try {
        if (fi.isFormField()) {
          if (!"".equals(fi.getString())) {
            String key = fi.getFieldName();
            params.put(key, fi.getString("utf-8"));
          } else {
            log.info("ignore: " + fi.getFieldName() + "-" + fi.getString());
          }
        } else {
          if (fi.getSize() == 0)
            log.info("Ignore empty feild-" + fi.getFieldName() + ".");
          else {
            if(!"pimage".equals(fi.getFieldName()))
              throw new FileFeildNameErr();
            
            String imagePath = "products/" + UUIDUtils.getCode().substring(0,5) + "-"
                + fi.getName();
            params.put("pimage", imagePath);
            
            try {
              fi.write(new File(genRealPath(imagePath)));
            } catch (Exception e) {
              log.error(e.getMessage(), e);
              throw new SaveFileErr();
            }
          }
        }
      } catch (UnsupportedEncodingException | FileFeildNameErr
          | SaveFileErr e) {
        if(null != params.get("pimage")) {
          deleteFile(new File(params.get("pimage")));
        }
        throw e;
      }
      
      fi.delete();
    }
    return params;
  }

  public File createFile(String path) throws CreateFileFailed {
    File f = new File(path);

    File p = new File(f.getParent());
    if (!p.exists()) {
      p.mkdir();
      log.info("New dir: " + p.getPath());
    }

    if (!f.exists()) {
      try {
        f.createNewFile();
      } catch (IOException e) {
        log.info("New file failed: " + f.getPath());
        throw new CreateFileFailed();
      } finally {
        deleteFile(f);
      }
      log.info("New file: " + f.getPath());
    }
    return f;
  }

  public static void deleteFile(File f) {
    if (null == f) {
      log.warn("File is null");
      return;
    }
    if (!f.delete())
      log.fatal("Delete Failed: " + f.getPath());
    else
      log.info("Delete succ: " + f.getPath());
  }

  protected String genRealPath(String path) {
    return this.getServletContext().getRealPath(path);
  }

}
