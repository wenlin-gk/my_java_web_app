<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<HTML id="admin/product/edit.jsp">
<HEAD><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <LINK href="${pageContext.request.contextPath}/css/style1.css" type="text/css" rel="stylesheet">
</HEAD>

<body>
  <form id="product_update" action="${pageContext.request.contextPath}/admin/product?method=update" method="post" enctype="multipart/form-data">
    <input type="hidden" name="pid" value="${p.pid }">
    &nbsp;
    <table cellSpacing="1" cellPadding="5" width="100%" align="center" bgColor="#eeeeee" style="border: 1px solid #8ba7e3" border="0">
      <tr>
        <td class="ta_01" align="center" bgColor="#afd1f3" colSpan="4" height="26">
            <STRONG>编辑商品</STRONG>
        </td>
      </tr>
      <tr>
        <td width="18%" align="center" bgColor="#f5fafe" class="ta_01">
          商品名称：
        </td>
        <td class="ta_01" bgColor="#ffffff">
          <input type="text" name="pname" value="${p.pname }" required class="bg" />必填，1-50个字符
        </td>
        <td width="18%" align="center" bgColor="#f5fafe" class="ta_01">
          是否热门：
        </td>
        <td class="ta_01" bgColor="#ffffff">
          <select name="is_hot">
            <option value="1" ${p.is_hot eq 1 ? "selected" :""}>是</option>
            <option value="0" ${p.is_hot eq 0 ? "selected" :""}>否</option>
          </select>
        </td>
      </tr>
      <tr>
        <td width="18%" align="center" bgColor="#f5fafe" class="ta_01">
          市场价格：
        </td>
        <td class="ta_01" bgColor="#ffffff">
          <input type="text" name="market_price" value="${p.market_price }"
           required class="bg" />必填，1-200000
        </td>
        <td width="18%" align="center" bgColor="#f5fafe" class="ta_01">
          商城价格：
        </td>
        <td class="ta_01" bgColor="#ffffff">
          <input type="text" name="shop_price" value="${p.shop_price }"
           required class="bg" />必填，1-200000
        </td>
      </tr>
      <tr>
        <td width="18%" align="center" bgColor="#f5fafe" class="ta_01">
          商品图片：
        </td>
        <td class="ta_01" bgColor="#ffffff" colspan="3">
          <input type="file" name="pimage" />
        </td>
      </tr>
      <tr>
        <td width="18%" align="center" bgColor="#f5fafe" class="ta_01">
          所属分类：
        </td>
        <td class="ta_01" bgColor="#ffffff" colspan="3">
          <select name="cid">
            <c:forEach items="${list }" var="c">
              <option value="${c.cid }" ${p.cid eq c.cid ? "selected" :""}>${c.cname }</option>
            </c:forEach>
          </select>
        </td>
      </tr>
      <tr>
        <td width="18%" align="center" bgColor="#f5fafe" class="ta_01">
          商品描述：
        </td>
        <td class="ta_01" bgColor="#ffffff" colspan="3">
          <textarea name="pdesc" rows="5" cols="30">${p.pdesc }</textarea>
        </td>
      </tr>
      <tr>
        <td class="ta_01" style="WIDTH: 100%" align="center" bgColor="#f5fafe" colSpan="4">
          <button type="submit" id="product_update_submit" value="确定" class="button_ok">
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