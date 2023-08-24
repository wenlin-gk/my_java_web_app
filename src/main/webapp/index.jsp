<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html id="index.jsp">

<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>我的商店</title>
</head>

<frameset rows="60,*,60" frameborder=0 border="0" framespacing="0">
  <frame src="${pageContext.request.contextPath}/top.jsp" name="topFrame" scrolling="NO" noresize/>
  <frame src="${pageContext.request.contextPath}/product?method=index" name="mainFrame"/>
  <frame src="${pageContext.request.contextPath}/buttom.jsp" name="bottomFrame" scrolling="NO" noresize height=20px/>
</frameset>

</html>