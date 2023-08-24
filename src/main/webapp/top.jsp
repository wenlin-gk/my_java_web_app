<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html id="top.jsp">
<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <script src="${pageContext.request.contextPath}/js/jquery-1.11.3.min.js" type="text/javascript"></script>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css" type="text/css" />
</head>

<body>

  <div class="container-fluid">
    <nav class="navbar navbar-inverse">
      <div class="container-fluid">
        <div class="navbar-header">
          <a class="navbar-brand" href="${pageContext.request.contextPath }" target="_parent">首页</a>
        </div>

        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
          <ul class="nav navbar-nav" id="c_ul"></ul>

          <div class="col-md-3" style="text-align:right;padding-top: 15px">
            <ol class="list-inline">
              <c:if test="${empty sessionScope.user }">
                <li>
                  <a href="${pageContext.request.contextPath }/user?method=loginUI" target="mainFrame">登录</a>
                </li>
                <li>
                  <a href="${pageContext.request.contextPath }/register.jsp" target="mainFrame">注册</a>
                </li>
              </c:if>

              <c:if test="${not empty sessionScope.user }">
                <font id="user_name" color="green">|&nbsp;&nbsp;用户：${sessionScope.user.name }&nbsp;&nbsp;|</font>
                <li>
                  <a href="${pageContext.request.contextPath }/order?method=getAllByPage&pageNumber=1" target="mainFrame">我的订单</a>
                </li>
                <li>
                  <a href="${pageContext.request.contextPath }/user?method=logout" target="_parent">退出</a>
                </li>
              </c:if>

              <li>
                <a href="${pageContext.request.contextPath }/cart.jsp" target="mainFrame">购物车</a>
              </li>
            </ol>
          </div>
        </div>

      </div>
    </nav>
  </div>

  <!-- 浏览器载入body时执行 -->
  <script type="text/javascript">
    $(function(){
        $.post("${pageContext.request.contextPath}/category",{"method":"getAll"},function(obj){
          $(obj).each(function(){
            $("#c_ul").append(
              "<li><a id='category_item' href='${pageContext.request.contextPath}/product?method=getByPage&pageNumber=1&cid="+this.cid+"' target='mainFrame'>"+this.cname+"</a></li>");
          });
        },"json");
      })
  </script>

</body>

</HTML>