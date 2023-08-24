<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html id="product_info.jsp">

<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css" type="text/css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" type="text/css" />

</head>

<body>

  <div class="container">
    <div class="row">

      <div style="margin: 0 auto; width: 950px;">
        <div class="col-md-6">
          <img style="opacity: 1; width: 400px; height: 350px;" title="" class="medium" src="${pageContext.request.contextPath}/${bean.pimage}">
        </div>

        <div class="col-md-6">
          <div>
            <strong id="pname">${bean.pname }</strong>
          </div>
          <div style="border-bottom: 1px dotted #dddddd; width: 350px; margin: 10px 0 10px 0;">
            <div>编号：<a id="pid">${bean.pid }</a></div>
          </div>

          <div style="margin: 10px 0 10px 0;">
            商城价: <strong style="color: #ef0101;">￥${bean.shop_price }元</strong>
            &nbsp;&nbsp;<del>（市场价：￥${bean.market_price }元）</del>
          </div>

          <div style="margin: 10px 0 10px 0;">
            促销: <a target="_blank" title="限时抢购 (2014-07-30 ~ 2015-01-01)" style="background-color: #f07373;">限时抢购</a>
          </div>

          <div style="padding: 10px; border: 1px solid #e7dbb1; width: 330px; margin: 15px 0 10px 0;; background-color: #fffee6;">
            <form action="${pageContext.request.contextPath }/cart" id="form1" method="get">
              <!-- 提交的方法  -->
              <input type="hidden" name="method" value="put">

              <!-- 商品的pid -->
              <input type="hidden" name="pid" value="${bean.pid }">

              <div style="border-bottom: 1px solid #faeac7; margin-top: 20px; padding-left: 10px;">
                购买数量:
                <input id="quantity" name="count" value="1" maxlength="4" size="10" type="text">
              </div>

              <div style="margin: 20px 0 10px 0;; text-align: center;">
            </form>
            <a href="javascript:void(0)" onclick="subForm()">
              <input id="add2cart" style="background: url('${pageContext.request.contextPath}/images/product.gif') no-repeat scroll 0 -600px rgba(0, 0, 0, 0);height:36px;width:127px;" value="加入购物车" type="button">
            </a>
          </div>
        </div>
      </div>
    </div>
    <div class="clear"></div>
    <div style="width: 950px; margin: 0 auto;">
      <div style="background-color: #d3d3d3; width: 930px; padding: 10px 10px; margin: 10px 0 10px 0;">
        <strong>商品介绍</strong>
      </div>

      <div><strong>${bean.pdesc }</strong>
      </div>
    </div>
  </div>


</body>
<script type="text/javascript">
  function subForm(){
        //让指定的表单提交
        document.getElementById("form1").submit();
      }
</script>
</html>