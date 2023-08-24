<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html id="order/order_list.jsp">

<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css" type="text/css" />
</head>

<body>

  <div class="container">
    <div class="row">

      <div style="margin:0 auto; margin-top:10px;width:950px;">
        <strong>我的订单</strong>
        <table class="table table-bordered">
          <c:forEach items="${pb.data }" var="o">
            <tbody id="orderItem">
              <tr class="success">
                <th colspan="2">订单编号:<a id="oid">${o.oid }</a></th>
                <th colspan="1">
                  <c:if test="${o.state == 0 }">
                    <a id="orderInfo" href="${pageContext.request.contextPath }/order?method=getById&oid=${o.oid}">去付款</a>
                  </c:if>
                  <c:if test="${o.state == 1 }">已付款</c:if>
                  <c:if test="${o.state == 2 }">确认收货</c:if>
                  <c:if test="${o.state == 3 }">已完成</c:if>
                </th>
                <th colspan="2">金额:${o.total }元 </th>
              </tr>
              <tr class="warning">
                <th>图片</th>
                <th>商品</th>
                <th>价格</th>
                <th>数量</th>
                <th>小计</th>
              </tr>
              <c:forEach items="${o.items }" var="oi">
                <tr class="active">
                  <td width="60" width="40%">
                    <input type="hidden" name="id" value="22">
                    <img src="${pageContext.request.contextPath}/${oi.product.pimage}" width="70" height="60">
                  </td>
                  <td width="30%">
                    <a target="_blank">${oi.product.pname}</a>
                  </td>
                  <td width="20%">
                    ￥${oi.product.shop_price}
                  </td>
                  <td width="10%">
                    ${oi.count }
                  </td>
                  <td width="15%">
                    <span class="subtotal">￥${oi.subtotal }</span>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </c:forEach>

        </table>
      </div>
    </div>
    <%@include file="/page_4_order_list.jsp" %>
  </div>
</body>

</html>