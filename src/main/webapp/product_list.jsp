<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html id="product_list.jsp">

<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css" type="text/css" />
  <script src="${pageContext.request.contextPath}/js/jquery-1.11.3.min.js" type="text/javascript"></script>
</head>

<body>

  <div class="row" style="width: 1210px; margin: 0 auto;">
    <c:forEach items="${pb.data }" var="p">
      <div class="col-md-2">
        <a id="product_item" href="${pageContext.request.contextPath }/product?method=getById&pid=${p.pid}">
          <img src="${pageContext.request.contextPath}/${p.pimage}" width="170" height="170" style="display: inline-block;">
        </a>
        <p>
          <a href="${pageContext.request.contextPath }/product?method=getById&pid=${p.pid}" style='color: green'>${fn:substring(p.pname,0,10) }..</a>
        </p>
        <p>
          <font color="#FF0000">商城价：&yen;${p.shop_price }</font>
        </p>
      </div>
    </c:forEach>

  </div>

  <%@include file="/page_4_product_list.jsp" %>

  <!--
    商品浏览记录:
  -->
  <div style="width: 1210px; margin: 0 auto; padding: 0 9px; border: 1px solid #ddd; border-top: 2px solid #999; height: 246px;">

    <h4 style="width: 50%; float: left; font: 14px/30px;">浏览记录</h4>
    <div style="width: 50%; float: right; text-align: right;">
      <a href="">more</a>
    </div>
    <div style="clear: both;"></div>

    <div style="overflow: hidden;">

      <ul id="history" style="list-style: none;">
      </ul>

    </div>
  </div>

  <script type="text/javascript">
    $(function(){
      //从cookie获取浏览记录
      var pids = getCookie("history").split('-');
      if(pids != ""){
        pids.forEach(function (pid) {
          $.post("${pageContext.request.contextPath }/product",{"method":"getInJsonById","pid":pid},function(p){
            if ( !(JSON.stringify(p)=='{}') ){
              $("#history").append(
                '<li style="width: 150px; height: 216; float: left; margin: 0 8px 0 0; padding: 0 18px 15px; text-align: center;">'
                +'<a id="history_item" href="${pageContext.request.contextPath }/product?method=getById&pid='+p.pid+'">'
                +'  <img src="${pageContext.request.contextPath }/'+p.pimage+'" width="130px" height="130px" />'
                +'</a>'
                +'</li>'
                );
            }
          },"json");
        });
      }

    })
    
    
    function getCookie(cname){
      var name = cname + "=";
    
      var ca = document.cookie.split(';');
    
      for(var i=0; i<ca.length; i++) {
          var c = ca[i].trim();
          if (c.indexOf(name)==0) {
            return c.substring(name.length,c.length);
          }
      }
      return "";
    }
  </script>

</body>

</html>