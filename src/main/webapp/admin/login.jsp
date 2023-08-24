<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html id="admin/login.jsp">
<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>网上商城管理系统</title>

  <style type="text/css">
    body {
      color: white;
    }
  </style>
</head>

<body style="background: #278296">
  <form method="get" action="${pageContext.request.contextPath }/admin/user" target="_parent" name='theForm' onsubmit="return validate()">
    <input type="hidden" name="method" value="login">
    <table cellspacing="0" cellpadding="0" style="margin-top: 100px" align="center">
      <tr>
        <td style="padding-left: 50px">
          <table>
            <tr>
              <td>管理员姓名：</td>
              <td>
                <input type="text" name="adminName" value="${adminName }" />
              </td>
            </tr>
            <tr>
              <td>管理员密码：</td>
              <td>
                <input type="password" name="adminPasswd" value="${adminPasswd }" />
              </td>
            </tr>
            <tr>
              <td>&nbsp;</td>
              <td>
                <input type="submit" value="进入管理系统" class="button" />
              </td>
            </tr>
            <tr>
              <td>&nbsp;</td>
              <td>
                <span id="msg">${msg }</span>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </form>
</body>