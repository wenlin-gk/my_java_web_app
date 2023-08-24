package top.wl.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import top.wl.service.CategoryService;
import top.wl.service.error.SvcUnavailable;
import top.wl.utils.JsonUtil;

public class CategoryServlet extends BaseServlet {
  private static final long serialVersionUID = 1L;
  private static Logger log = LogManager.getLogger(CategoryServlet.class);
  private static CategoryService cs = new CategoryService();

  public String getAll(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {

    response.setContentType("text/html;charset=utf-8");
    try {
      response.getWriter().println(cs.getAllInJson());
      return null;

    } catch (SvcUnavailable e) {
      log.error(e.getMessage(), e);
      response.getWriter().println(JsonUtil.list2json(null));
      return null;
    }
  }
}
