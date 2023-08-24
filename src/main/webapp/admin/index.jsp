<%@ page language="java" pageEncoding="UTF-8"%>
<html id="admin/index.jsp">
<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>商店管理系统</title>
</head>

<frameset rows="32,*,43" frameborder=0 border="0" framespacing="0">
  <frame src="${pageContext.request.contextPath}/admin/top.jsp" name="topFrame" scrolling="NO" noresize/>
  <frameset cols="159,*" frameborder="0" border="0" framespacing="0">
    <frame src="${pageContext.request.contextPath}/admin/left.jsp" name="leftFrame" noresize scrolling="YES"/>
    <frame src="${pageContext.request.contextPath}/admin/welcome.jsp" name="mainFrame"/>
  </frameset>
  <frame src="${pageContext.request.contextPath}/admin/buttom.jsp" name="bottomFrame" scrolling="NO" noresize/>
</frameset>

</html>