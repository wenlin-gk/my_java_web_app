package top.wl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class ResourceAccessControlTest extends BaseTest {
  private static Logger log = LogManager.getLogger(ResourceAccessControlTest.class);

  @Test
  public void Failed_to_access_restricted_resources_if_access_is_anonymous() {
    driver.get(serverAddr + "/admin/index.jsp");
    assertCurrentPage("admin/login.jsp");

    logout();
    driver.get(serverAddr + "/order/*");
    assertCurrentPage("login.jsp");
    login();
  }

  @Test
  public void Successfully_accessed_restricted_resources_if_the_user_is_logged_in() {
    adminLogin();
    driver.get(serverAddr + "/admin/index.jsp");
    assertCurrentPage("admin/index.jsp");
    adminLogout();
    
    login();
    openJsp("order/order_list.jsp");
    assertCurrentPage("order/order_list.jsp");
  }

}