<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<HTML id="admin/user/list.jsp">
<HEAD><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link href="${pageContext.request.contextPath}/css/style1.css" rel="stylesheet" type="text/css" />

</HEAD>
<body>
  <br>
  <form>
    <table cellSpacing="1" cellPadding="0" width="100%" align="center" bgColor="#f5fafe" border="0">
      <TBODY>
        <tr>
          <td class="ta_01" align="center" bgColor="#afd1f3">
            <strong>用户列表</strong>
          </TD>
        </tr>
        <tr>

        </tr>
        <tr>
          <td class="ta_01" align="center" bgColor="#f5fafe">
            <table cellspacing="0" cellpadding="1" rules="all" bordercolor="gray" border="1" id="DataGrid1" style="BORDER-RIGHT: gray 1px solid; BORDER-TOP: gray 1px solid; BORDER-LEFT: gray 1px solid; WIDTH: 100%; WORD-BREAK: break-all; BORDER-BOTTOM: gray 1px solid; BORDER-COLLAPSE: collapse; BACKGROUND-COLOR: #f5fafe; WORD-WRAP: break-word">
              <tr style="FONT-WEIGHT: bold; FONT-SIZE: 12pt; HEIGHT: 25px; BACKGROUND-COLOR: #afd1f3">

                <td align="center" width="18%">
                  序号
                </td>
                <td align="center" width="17%">
                  用户名称
                </td>
                <td align="center" width="17%">
                  真实姓名
                </td>
                <td width="7%" align="center">
                  编辑
                </td>
                <td width="7%" align="center">
                  删除
                </td>
              </tr>
              <c:forEach items="${pb.data }" var="u" varStatus="status">
                <tr id="user_item" onmouseover="this.style.backgroundColor = 'white'" onmouseout="this.style.backgroundColor = '#F5FAFE';">
                  <td style="CURSOR: hand; HEIGHT: 22px" align="center" width="18%">
                    ${status.count }
                  </td>
                  <td style="CURSOR: hand; HEIGHT: 22px" align="center" width="17%">
                    ${u.username }
                  </td>
                  <td style="CURSOR: hand; HEIGHT: 22px" align="center" width="17%">
                    ${u.name }
                  </td>
                  <td align="center" style="HEIGHT: 22px">
                    <a href="${ pageContext.request.contextPath }/admin/user?method=editUI&uid=${u.uid }">
                      <img src="${pageContext.request.contextPath}/images/i_edit.gif" border="0" style="CURSOR: hand">
                    </a>
                  </td>

                  <td align="center" style="HEIGHT: 22px">
                    <a href="${ pageContext.request.contextPath }/admin/user?method=delete&uid=${u.uid }">
                      <img src="${pageContext.request.contextPath}/images/i_del.gif" width="16" height="16" border="0" style="CURSOR: hand">
                    </a>
                  </td>
                </tr>
              </c:forEach>
            </table>
          </td>
        </tr>
        <c:if test="${pb.data.size() > 0}">
          <tr id="page_index" align="center">
            <td colspan="7">
              第${pb.pageNumber }/${pb.totalPage }页
              <c:if test="${pb.pageNumber != 1 }">|
                <a href="${ pageContext.request.contextPath }/admin/user?method=getByPage&pageNumber=1">首页</a>|
                <a href="${ pageContext.request.contextPath }/admin/user?method=getByPage&pageNumber=${pb.pageNumber-1 }">上一页</a>|
              </c:if>
              <c:if test="${pb.pageNumber != pb.totalPage }">|
                <a href="${ pageContext.request.contextPath }/admin/user?method=getByPage&pageNumber=${pb.pageNumber+1 }">下一页</a>|
                <a href="${ pageContext.request.contextPath }/admin/user?method=getByPage&pageNumber=${pb.totalPage }">尾页</a>|
              </c:if>
            </td>
          </tr>
        </c:if>
      </TBODY>
    </table>
  </form>
</body>
</HTML>