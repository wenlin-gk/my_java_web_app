package top.wl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/*
用例说明： 以jsp跳转路径Graph为线索，覆盖所有jsp和servlet。
          另外包括资源访问控制，中文编码。

具体如下：
admin/index.jsp
  logout链接可用。
  user list
  category list
  category add
  product list
  product add
  order list链接可用。

  admin/user/servlet.logout
  admin/user/servlet.getbypage
    系统故障（db故障）时，返回提示页面，提示服务故障。
    系统正常，参数非法（pageNumber=-1）时，返回提示页面，提示参数非法。
    系统正常，参数合法，2页用户，pageNumber=100超出最大页码时，返回list页面，内容为空，分页为空。
    系统正常，参数合法，2页用户，pageNumber=1时，返回list页面，内容，分页准确。

    admin/user/list.jsp
      user的编辑链接可用
      user的删除链接可用

      admin/user/servlet.editui
        系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
        系统正常，参数非法（uid=null）时，返回提示页面，提示参数非法。
        系统正常，参数非法（uid过长）时，返回提示页面，提示参数非法。
        系统正常，参数非法（uid=noexist）时，返回提示页面，提示参数非法。
        系统正常，参数合法（uid=从list页面获取）时，返回edit.jsp，user信息准确。
      
        admin/user/edit.jsp
          必填字段未填写时，点击提交按钮无效。
          提交按钮有效：点击提交，返回list.jsp页面
        
          admin/user/servlet.update
            系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
            系统正常，参数非法（uid=null）时，返回提示页面，提示参数非法。
            系统正常，参数非法（uid过长）时，返回提示页面，提示参数非法。
            系统正常，参数合法，业务不允许，如uid不存在 时，返回提示页面，提示xx不存在。
            系统正常，参数合法，业务允许，返回list.jsp页面。更新准确。
            系统正常，参数合法，业务允许(修改部分字段)，返回list.jsp页面。修改成功，空字段未更新。

      admin/user/servlet.delete
        系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
        系统正常，参数非法（uid=null）时，返回提示页面，提示参数非法。
        系统正常，参数非法（uid过长）时，返回提示页面，提示参数非法。
        系统正常，参数非法（uid=noexist）时，返回提示页面，提示xx不存在。
        系统正常，参数合法，但是业务不允许，如uid=被订单条目依赖 时，返回提示页面，提示被依赖不允许删除。
        系统正常，参数合法（uid=从list页面获取）时，返回list.jsp，user删除成功。

  admin/category/servlet.list
    系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
    系统正常，有category时，返回list页面，无内容。
    系统正常，没有category时，返回list页面，内容准确。

    admin/category/list.jsp
      category的编辑链接可用
      category的删除链接可用

      admin/category/servlet.editui
        系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
        系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
        系统正常，参数非法（id过长）时，返回提示页面，提示参数非法。
        系统正常，参数非法（id=noexist）时，返回提示页面，提示xx不存在。
        系统正常，参数合法（id=从list页面获取）时，返回edit.jsp，category信息准确。
      
        admin/category/edit.jsp
          必填字段未填写时，点击提交按钮无效。
          提交按钮有效：点击提交，返回list.jsp页面
      
          admin/category/servlet.update
            系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
            系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
            系统正常，参数非法（category属性非法，如name过长）时，返回提示页面，提示参数非法。
            系统正常，参数合法，业务不允许，如id不存在 时，返回提示页面，提示xx不存在。
            系统正常，参数合法，业务允许，返回list.jsp页面。
            系统正常，参数合法，业务允许(修改部分字段)，返回list.jsp页面。修改成功，空字段未更新。

      admin/category/servlet.delete
      系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
      系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
      系统正常，参数非法（id过长）时，返回提示页面，提示参数非法。
      系统正常，参数非法（id=noexist）时，返回提示页面，提示xx不存在。
      系统正常，参数合法（id=从list页面获取）时，返回list.jsp，category删除成功。
      系统正常，参数非法（id=被订单条目依赖）时，返回提示页面，提示被依赖不允许删除。

  admin/category/add.jsp
    必填字段未填写时，点击提交按钮无效。
    提交按钮有效：点击提交，返回list.jsp页面，添加成功。

    admin/category/servlet.add
      系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
      系统正常，参数非法（category属性非法，如name过长）时，返回提示页面，提示参数非法。
      系统正常，参数合法，业务不允许，如id已经存在 时，返回提示页面，提示xx已经存在。
      系统正常，参数合法，业务允许，返回list.jsp页面。

  admin/product/servlet.getbypage
    系统故障（db故障）时，返回提示页面，提示服务不可用。--忽略
    系统正常，参数非法（pageNumber=-1）时，返回提示页面，提示参数非法。
    系统正常，参数合法，2页用户，pageNumber=100超出最大页码时，返回list页面，内容为空，分页为空。
    系统正常，参数合法，2页用户，pageNumber=1时，返回list页面，内容，分页准确。

    admin/product/list.jsp
      product的编辑链接可用
      product的删除链接可用

      admin/product/servlet.editui
        系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
        系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
        系统正常，参数非法（id过长）时，返回提示页面，提示参数非法。
        系统正常，参数非法（id=noexist）时，返回提示页面，提示xx不存在。
        系统正常，参数合法（id=从list页面获取）时，返回edit.jsp，product信息准确。
      
        admin/product/edit.jsp
          必填字段未填写时，点击提交按钮无效。
          提交按钮有效：点击提交，返回list.jsp页面
      
          admin/product/servlet.update
            系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
            系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
            系统正常，参数非法（product属性非法，如name过长）时，返回提示页面，提示参数非法。
            系统正常，参数合法，业务不允许，如id不存在 时，返回提示页面，提示xx不存在。
            系统正常，参数合法，业务允许(修改所有字段)，返回list.jsp页面。修改成功。
            系统正常，参数合法，业务允许(修改部分字段)，返回list.jsp页面。修改成功，空字段未更新。

      admin/product/servlet.delete
      系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
      系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
      系统正常，参数非法（id过长）时，返回提示页面，提示参数非法。
      系统正常，参数非法（id=noexist）时，返回提示页面，提示xx不存在。
      系统正常，参数合法（id=从list页面获取）时，返回list.jsp，product删除成功。
      系统正常，参数非法（id=被订单条目依赖）时，返回提示页面，提示被依赖不允许删除。

  admin/product/servlet.addUI
    返回add.jsp

    admin/product/add.jsp
      必填字段未填写时，点击提交按钮无效。
      提交按钮有效：点击提交，返回list.jsp页面，添加成功。

      admin/product/servlet.add
        系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
        系统正常，参数非法（product属性非法，如name过长）时，返回提示页面，提示参数非法。
        系统正常，参数合法，业务不允许，如cid不存在 时，返回提示页面，提示cid不存在。--。
        系统正常，参数合法，业务允许，返回list.jsp页面。


  admin/order/servlet.getbypage
    系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
    系统正常，参数非法（state=-1）时，返回提示页面，提示参数非法。
    系统正常，参数合法，无订单，返回list页面，内容为空，分页为空。
    系统正常，参数合法，有订单，返回list页面，内容，分页准确。

    admin/order/list.jsp
      order的update链接可用
      order的详情链接可用（订单条目显示准确）

      admin/order/servlet.udpatestate
        系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
        系统正常，参数非法（id过长）时，返回提示页面，提示参数非法。
        系统正常，参数非法（id不存在）时，返回提示页面，提示参数非法。
        系统正常，参数合法，业务不允许，如id不存在 时，返回提示页面，提示xx不存在。
        系统正常，参数合法，业务允许，返回list.jsp页面。

      admin/order/servlet.getInJson
        系统正常，参数非法（oid=null），返回空。
        系统正常，参数非法（oid不存在），返回空。
        系统正常，参数合法，订单条目为空，返回空。
        系统正常，参数合法，订单条目非空，返回订单条目准确。

index.jsp
  用户未登录时，登录，注册，我的购物车，链接可用
  用户已登录时，用户名显示准确，我的购物车，我的订单，退出，链接可用
  框架结构为：top.jsp,body.jsp,bottom.jsp
  首页链接可用
  分类显示准确
  分类链接可用
  最新商品列表显示准确
  商品详情链接可用

  /user/loginUI
    如果已经登录，返回index.jsp
    如果没有登录，返回login.jsp

    login.jsp
      user名称不可用，提交按钮不可用。
      user名称可用，提交按钮可用。返回index.jsp，登录成功。
      记住用户名，退出后，登录页面，自动填充用户名。
      自动登录，session失效后，自动登录。--忽略

      /user/login
        系统故障（db故障）时，返回原始页面，提示服务故障。--忽略
        系统正常，参数非法（密码非法超出20个字符）时，返回原始页面，提示参数非法。
        用户密码错误
        用户未激活
  /user/regist.jsp
    必填字段未填写时，点击提交无效。
    提交按钮有效：点击提交，返回index.jsp页面

    /user/regist
      系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
      系统正常，参数非法（属性非法，如name过长）时，返回提示页面，提示参数非法。
      系统正常，参数非法（必填字段不存在，如name不存在）时，返回提示页面，提示参数非法。
      系统正常，参数合法，业务不允许，如id已经存在 时，返回提示页面，提示xx已经存在。--忽略
      系统正常，参数合法，业务允许，返回index.jsp页面。

      /user/checkUsername
        系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
        系统正常，参数非法（必填字段不存在，如name不存在）时，返回提示页面，提示参数非法。
        系统正常，参数非法（属性非法，如name过长）时，返回提示页面，提示参数非法。
        系统正常，参数合法，name不存在时，返回1
        系统正常，参数合法，name存在时，返回2

  /user/logout
    返回index.jsp。处于退出状态（访问/order/list返回msg.jsp）。
    自动登录cookie被清理。

  /cart.jsp
    无商品，显示无商品。
    有商品，显示商品准确。
    移除按钮可用。
    清空购物车按钮可用
    提交到订单可用。

    /order/add
      系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
      系统正常，购物车为空时，返回提示页面，提示异常信息。
      系统正常，商品不存在 时，返回提示页面，提示异常信息。订单未生成。
      系统正常，参数合法，业务允许，返回/order/info.jsp页面。用户新增一个订单，购物车清空。

      /order/info.jsp
        订单信息显示准确
        电话收件人地址输入字段可用
        电话收件人地址输入字段选填
        支付按钮可用

        /order/update
          系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
          系统正常，参数非法，如oid=null 时，返回提示页面，提示异常。
          系统正常，参数非法（order属性非法，如name过长）时，返回提示页面，提示参数非法。
          系统正常，参数合法，业务不允许，如oid不存在 时，返回提示页面，提示异常。
          系统正常，参数合法，业务不允许，如uid不匹配 时，返回提示页面，提示异常。
          系统正常，参数合法，业务不允许，如state不处于待支付 时，返回提示页面，提示异常。
          系统正常，参数合法，业务允许，提示支付成功页面。各个字段更新准确。

  /category/list
    系统故障（db故障）时，返回null。--忽略
    系统正常，没有category时，返回空jsonarr。
    系统正常，有category时，返回jsonarr准确。

  /product/list
    系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
    系统正常，参数非法（pageNumber=-1）时，返回提示页面，提示参数非法。
    系统正常，参数合法，2页商品，pageNumber=100超出最大页码时，返回list页面，内容为空，分页为空。
    系统正常，参数合法，2页商品，pageNumber=1超出最大页码时，返回list页面，内容，分页准确。

    /product/list.jsp
      页码准确，页码链接可用
      浏览历史为空时显示准确
      浏览历史不为空时显示准确
      商品详情链接可用。
      浏览历史-商品详情链接可用。
      
  /product/detail
    系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
    系统正常，参数非法（pid=null）时，返回提示页面，提示参数非法。
    系统正常，参数非法（pid=过长）时，返回提示页面，提示参数非法。
    系统正常，参数非法（id=noexist）时，返回提示页面，提示xx不存在。
    系统正常，参数合法时，返回/product/detail.jsp。显示商品信息准确。

    /product/detail.jsp
      数量输入框可用
      提交按钮可用

      /cart/put
        系统正常，参数非法（pid=过长）时，返回提示页面，提示参数非法。
        系统正常，参数非法（count=0）时，返回提示页面，提示参数非法。
        系统正常，参数非法（id=noexist）时，返回提示页面，提示xx不存在。
        系统正常，参数合法时，返回/cart.jsp
      /cart/delete
      /cart/clear
  /order/list
    系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
    系统正常，参数非法（pageNumber=-1）时，返回提示页面，提示参数非法。
    系统正常，参数合法，2页订单，pageNumber=100超出最大页码时，返回list页面，内容为空，分页为空。
    系统正常，参数合法，2页订单，pageNumber=1时，返回list页面，内容，分页准确。

    /order/list.jsp
      订单更新链接有效

      /order/get
        系统故障（db故障）时，返回提示页面，提示服务故障。--忽略
        系统正常，参数非法（id=null）时，返回提示页面，提示参数非法。
        系统正常，参数非法（id=""）时，返回提示页面，提示参数非法。
        系统正常，参数非法，业务允许（id="noexist"）时，返回提示页面。
        系统正常，参数合法，业务允许，返回/order/info.jsp页面。

  未登录访问/order/list失败
  登录后访问/order/list成功
*/

@RunWith(Suite.class)
@Suite.SuiteClasses({
  PlaceHolder4setup.class,
  
  top.wl.ResourceAccessControlTest.class,

  top.wl.jsp.CartTest.class,
  top.wl.jsp.IndexTest.class,
  top.wl.jsp.LoginTest.class,
  top.wl.jsp.RegistTest.class,

  top.wl.jsp.admin.IndexTest.class,
  top.wl.jsp.admin.LoginTest.class,

  top.wl.jsp.admin.category.AddTest.class,
  top.wl.jsp.admin.category.EditTest.class,
  top.wl.jsp.admin.category.ListTest.class,

  top.wl.jsp.admin.order.ListTest.class,

  top.wl.jsp.admin.product.AddTest.class,
  top.wl.jsp.admin.product.EditTest.class,
  top.wl.jsp.admin.product.ListTest.class,

  top.wl.jsp.admin.user.EditTest.class,
  top.wl.jsp.admin.user.ListTest.class,

  top.wl.jsp.order.InfoTest.class,
  top.wl.jsp.order.ListTest.class,

  top.wl.jsp.product.InfoTest.class,
  top.wl.jsp.product.ListTest.class,

  top.wl.servlet.AdminCategoryTest.class,
  top.wl.servlet.AdminOrderTest.class,
  top.wl.servlet.AdminProductTest.class,
  top.wl.servlet.AdminUserTest.class,
  top.wl.servlet.CartTest.class,
  top.wl.servlet.CategoryTest.class,
  top.wl.servlet.OrderTest.class,
  top.wl.servlet.ProductTest.class,
  top.wl.servlet.UserTest.class,
  
  PlaceHolder4teardown.class,
  })
public class Main {
}
