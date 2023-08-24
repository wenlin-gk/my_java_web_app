<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html id="body.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet"
  href="${pageContext.request.contextPath}/css/bootstrap.min.css"
  type="text/css" />
</head>

<body>

  <div class="container-fluid">
    <!--轮播热门商品-->
    <div class="container-fluid">
      <div id="carousel-example-generic" class="carousel slide"
        data-ride="carousel">
        <!-- Wrapper for slides -->
        <div class="carousel-inner" role="listbox">
          <c:forEach items="${hList }" var="p" varStatus="status">
            <div id="hotItem" class="item ${status.index eq 0 ? "active":""}" style="text-align: center">
              <a
                href="${pageContext.request.contextPath }/product?method=getById&pid=${p.pid}">
                <img
                src="${pageContext.request.contextPath}/${p.pimage}"
                width="130" height="130" style="display: inline-block;">
              </a>
              <p>
                <a
                  href="${pageContext.request.contextPath }/product?method=getById&pid=${p.pid}"
                  style='color: #666'> ${fn:substring(p.pname,0,10) }..
                </a>
              </p>
              <p>
                <font color="#E4393C" style="font-size: 16px">&yen;${p.shop_price }</font>
              </p>
              <div class="carousel-caption"></div>
            </div>
          </c:forEach>
        </div>
        <!-- Controls -->
        <a class="left carousel-control"
          href="#carousel-example-generic" role="button"
          data-slide="prev"> <span
          class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
          <span class="sr-only">Previous</span>
        </a> <a class="right carousel-control"
          href="#carousel-example-generic" role="button"
          data-slide="next"> <span
          class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
          <span class="sr-only">Next</span>
        </a>
      </div>
    </div>

    <!--展示最新商品-->
    <div class="container-fluid">
      <div class="col-md-12">
        <h2>最新商品</h2>
      </div>
      <div class="list-inline">
        <c:forEach items="${nList }" var="p">
          <div class="col-md-2"
            style="text-align: center; height: 200px; width: 130px; padding: 10px 0px;">
            <a id="newItem"
              href="${pageContext.request.contextPath }/product?method=getById&pid=${p.pid}">
              <img src="${pageContext.request.contextPath}/${p.pimage}"
              width="130" height="130" style="display: inline-block;">
            </a>
            <p>
              <a
                href="${pageContext.request.contextPath }/product?method=getById&pid=${p.pid}"
                style='color: #666'>${fn:substring(p.pname,0,9) }..</a>
            </p>
            <p>
              <font color="#E4393C" style="font-size: 16px">&yen;${p.shop_price }</font>
            </p>
          </div>
        </c:forEach>
      </div>
    </div>

  </div>
</body>


</html>