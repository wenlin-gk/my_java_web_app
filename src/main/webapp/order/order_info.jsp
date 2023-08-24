<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html id="order/order_info.jsp">

<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css" type="text/css" />
</head>

<body>
  <div class="container">
    <div class="row">

      <div style="margin: 0 auto; margin-top: 10px; width: 950px;">
        <strong>订单详情</strong>
        <table class="table table-bordered">
          <tbody>
            <tr class="warning">
              <th colspan="2">编号:<a id="oid">${bean.oid }</a></th>
              <th colspan="1">
                <c:if test="${bean.state == 0 }">去付款</c:if>
                <c:if test="${bean.state == 1 }">已付款</c:if>
                <c:if test="${bean.state == 2 }">确认收货</c:if>
                <c:if test="${bean.state == 3 }">已完成</c:if>
              </th>
              <th colspan="2">时间:
                <fmt:formatDate value="${bean.ordertime }" pattern="yyyy-MM-dd HH:mm:ss" />
              </th>
            </tr>
            <tr class="warning">
              <th>图片</th>
              <th>商品</th>
              <th>价格</th>
              <th>数量</th>
              <th>小计</th>
            </tr>
            <c:forEach items="${bean.items }" var="oi">
              <tr id="item" class="active">
                <td width="60" width="40%">
                  <input type="hidden" name="id" value="22">
                  <img src="${pageContext.request.contextPath}/${oi.product.pimage}" width="70" height="60">
                </td>
                <td width="30%">
                  <a target="_blank">${oi.product.pname}</a>
                </td>
                <td width="20%">￥${oi.product.shop_price}</td>
                <td width="10%">${oi.count}</td>
                <td width="15%">
                  <span class="subtotal">￥${oi.subtotal}</span>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <div style="text-align: right; margin-right: 120px;">
        商品金额: <strong style="color: #ff6600;">￥${bean.total }元</strong>
      </div>

    </div>

    <div>
      <hr />
      <form action="${pageContext.request.contextPath }/order" 
      id="orderForm" method="get" class="form-horizontal" style="margin-top: 5px; margin-left: 150px;">
        <input type="hidden" name="method" value="pay">
        <input type="hidden" name="oid" value="${bean.oid }">
        <div class="form-group">
          <label for="username" class="col-sm-1 control-label">地址</label>
          <div class="col-sm-5">
            <input type="text" name="address" value="${bean.address }" class="form-control" id="username" placeholder="请输入收货地址">
          </div>
        </div>
        <div class="form-group">
          <label for="inputPassword3" class="col-sm-1 control-label">收货人</label>
          <div class="col-sm-5">
            <input type="text" name="name" value="${bean.name }" class="form-control" id="inputPassword3" placeholder="请输收货人">
          </div>
        </div>
        <div class="form-group">
          <label for="confirmpwd" class="col-sm-1 control-label">电话</label>
          <div class="col-sm-5">
            <input type="text" name="telephone" value="${bean.telephone }" class="form-control" id="confirmpwd" placeholder="请输入联系方式">
          </div>
        </div>


        <hr />

        <div style="margin-top: 5px; margin-left: 150px;">
          <p style="text-align: right; margin-right: 100px;">
            <a id="submit" href="javascript:document.getElementById('orderForm').submit();">
              <img src="${pageContext.request.contextPath}/images/finalbutton.gif" width="204" height="51" border="0" />
            </a>
          </p>
          <hr />

        </div>
    </div>
    </form>
  </div>

</body>

</html>