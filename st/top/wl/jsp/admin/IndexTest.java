package top.wl.jsp.admin;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.openqa.selenium.By;

import top.wl.BaseTest4admin;

public class IndexTest extends BaseTest4admin {
  private static Logger log = LogManager.getLogger(IndexTest.class);

  @Test
  public void frameset_is_ok() {
    openAdminJsp("index.jsp");

    assertCurrentPage(0, "admin/top.jsp");
    assertCurrentPage(1, "admin/left.jsp");
    assertCurrentPage(2, "admin/welcome.jsp");
    assertCurrentPage(3, "admin/buttom.jsp");
  }

  @Test
  public void index_link_is_ok() {
    openAdminJsp("index.jsp");

    clickLink_AssertReturnPage("用户列表", 2, "admin/user/list.jsp");
    clickLink_AssertReturnPage("分类列表", 2, "admin/category/list.jsp");
    clickLink_AssertReturnPage("添加分类", 2, "admin/category/add.jsp");
    clickLink_AssertReturnPage("已上架商品列表", 2, "admin/product/list.jsp");
    clickLink_AssertReturnPage("添加商品", 2, "admin/product/add.jsp");
    clickLink_AssertReturnPage("订单列表", 2, "admin/order/list.jsp");
    clickLink_AssertReturnPage("未付款订单", 2, "admin/order/list.jsp");
    clickLink_AssertReturnPage("已付款订单", 2, "admin/order/list.jsp");
    clickLink_AssertReturnPage("已发货订单", 2, "admin/order/list.jsp");
    clickLink_AssertReturnPage("已完成订单", 2, "admin/order/list.jsp");

    clickLink_AssertReturnPage("退出", -1, "admin/login.jsp");
    // reset
    adminLogin();
    openAdminJsp("index.jsp");
  }

  private void clickLink_AssertReturnPage(String link, int frame,
      String returnPageId) {
    click(link);
    assertCurrentPage(frame, returnPageId);
  }

  public void click(String target) {
    driver.switchTo().defaultContent();

    switch (target) {
    case "退出":
      driver.switchTo().frame(0);
      driver.findElement(By.linkText("退出")).click();
      driver.switchTo().defaultContent();
      break;
    case "展开所有":
      driver.switchTo().frame(1);
      driver.findElement(By.linkText("展开所有")).click();
      driver.switchTo().defaultContent();
    case "用户列表":
    case "分类列表":
    case "添加分类":
    case "已上架商品列表":
    case "添加商品":
    case "订单列表":
    case "未付款订单":
    case "已付款订单":
    case "已发货订单":
    case "已完成订单":
      HashMap<String, String> indexIDs = new HashMap<String, String>() {
        {
          put("用户列表", "sd2");
          
          put("分类列表", "sd4");
          put("添加分类", "sd5");
          
          put("已上架商品列表", "sd7");
          put("添加商品", "sd8");
          
          put("订单列表", "sd10");
          put("未付款订单", "sd11");
          put("已付款订单", "sd12");
          put("已发货订单", "sd13");
          put("已完成订单", "sd14");
        }
      };
      String linkId = indexIDs.get(target);

      driver.switchTo().frame(1);
      driver.findElement(By.id(linkId)).click();
      driver.switchTo().defaultContent();
      break;
    default:
      throw new UnknownError();
    }
  }

}