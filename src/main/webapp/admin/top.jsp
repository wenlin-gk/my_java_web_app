<%@ page language="java" pageEncoding="UTF-8"%>

<html id="admin/top.jsp">
<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link href="${pageContext.request.contextPath}/css/style1.css" rel="stylesheet" type="text/css">
</head>

<body>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td height="30" valign="bottom" background="${pageContext.request.contextPath}/images/mis_01.jpg">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td width="85%" align="left">
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <font color="#000000">
              </font>
            </td>
            <td width="15%">
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td width="16" background="${pageContext.request.contextPath}/images/mis_05b.jpg">
                    <img src="${pageContext.request.contextPath}/images/mis_05a.jpg" width="6" height="18">
                  </td>
                  <td width="155" valign="bottom" background="${pageContext.request.contextPath}/images/mis_05b.jpg">
                    用户名： ${sessionScope.adminuser } &nbsp;|&nbsp;
                    <font color="blue">
                      <a href="${pageContext.request.contextPath }/admin/user?method=logout" target="_parent">退出</a>
                    </font>
                  </td>
                  <td width="10" align="right" background="${pageContext.request.contextPath}/images/mis_05b.jpg">
                    <img src="${pageContext.request.contextPath}/images/mis_05c.jpg" width="6" height="18">
                  </td>
                </tr>
              </table>
            </td>
            <td align="right" width="5%"></td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</body>
</HTML>