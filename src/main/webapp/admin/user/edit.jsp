<%@ page language="java" pageEncoding="UTF-8"%>
<HTML id="admin/user/edit.jsp">
<HEAD><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <LINK href="${pageContext.request.contextPath}/css/style1.css" type="text/css" rel="stylesheet">
</HEAD>

<body>
  <form id="userAction_save_do" name="Form1" action="${pageContext.request.contextPath}/admin/user?method=update" method="post">
    <input type="hidden" name="uid" value="${model.uid}" />
    <input type="hidden" name="state" value="${model.state}" />
    <input type="hidden" name="code" value="${model.code}" />
    &nbsp;
    <table cellSpacing="1" cellPadding="5" width="100%" align="center" bgColor="#eeeeee" style="border: 1px solid #8ba7e3" border="0">
      <tr>
        <td class="ta_01" align="center" bgColor="#afd1f3" colSpan="4" height="26">
            <STRONG>编辑用户</STRONG>
        </td>
      </tr>

      <tr>
        <td width="18%" align="center" bgColor="#f5fafe" class="ta_01">
          用户名称：
        </td>
        <td class="ta_01" bgColor="#ffffff">
          <input type="text" name="username" value="${model.username}" required class="bg" />
          必填，1-20个字符
        </td>
        <td width="18%" align="center" bgColor="#f5fafe" class="ta_01">
          密码：
        </td>
        <td class="ta_01" bgColor="#ffffff">
          <input type="text" name="password" value="${model.password}" required class="bg" />
          必填，1-20个字符
        </td>
      </tr>
      <tr>
        <td width="18%" align="center" bgColor="#f5fafe" class="ta_01">
          真实姓名：
        </td>
        <td class="ta_01" bgColor="#ffffff">
          <input type="text" name="name" value="${model.name}" required class="bg" />必填，1-20个字符
        </td>
        <td width="18%" align="center" bgColor="#f5fafe" class="ta_01">
          邮箱：
        </td>
        <td class="ta_01" bgColor="#ffffff">
          <input type="text" name="email" value="${model.email}" required class="bg" />必填，6-30个字符
        </td>
      </tr>
      <tr>
        <td width="18%" align="center" bgColor="#f5fafe" class="ta_01">
          电话：
        </td>
        <td class="ta_01" bgColor="#ffffff">
          <input type="text" name="telephone" value="${model.telephone}" class="bg" />选填，11个字符
        </td>
      </tr>

      <tr>
        <td class="ta_01" style="WIDTH: 100%" align="center" bgColor="#f5fafe" colSpan="4">
          <button type="submit" id="userAction_save_do_submit" value="确定" class="button_ok">
            &#30830;&#23450;
          </button>

          <FONT face="宋体">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</FONT>
          <button type="reset" value="重置" class="button_cancel">&#37325;&#32622;</button>

          <FONT face="宋体">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</FONT>
          <INPUT class="button_ok" type="button" onclick="history.go(-1)" value="返回" />
          <span id="Label1"></span>
        </td>
      </tr>
    </table>
  </form>
</body>
</HTML>