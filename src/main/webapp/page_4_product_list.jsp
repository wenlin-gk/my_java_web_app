<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!--分页 -->
<c:if test="${pb.data.size() > 0}">
<div id="page_index" style="width: 380px; margin: 0 auto; margin-top: 50px;">
  <ul class="pagination" style="text-align: center; margin-top: 10px;">

    <!-- 判断是否是第一页 -->
    <c:if test="${pb.pageNumber == 1 }">
      <li class="disabled">
        <a id="Previous" href="javascript:void(0)" aria-label="Previous">
          <span aria-hidden="true">&laquo;</span>
        </a>
      </li>
    </c:if>

    <!-- 不是第一页 -->
    <c:if test="${pb.pageNumber != 1 }">
      <li>
        <a id="Previous" href="${pageContext.request.contextPath }/product?method=getByPage&pageNumber=${pb.pageNumber-1}&cid=${param.cid}" aria-label="Previous">
          <span aria-hidden="true">&laquo;</span>
        </a>
      </li>
    </c:if>

    <!-- 展示所有页码 -->
    <c:forEach begin="1" end="${pb.totalPage }" var="n">
      <!-- 判断是否是当前页 -->
      <c:if test="${pb.pageNumber == n }">
        <li class="active">
          <a id="page" href="javascript:void(0)">${n }</a>
        </li>
      </c:if>
      <c:if test="${pb.pageNumber != n }">
        <li>
          <a id="page" href="${pageContext.request.contextPath }/product?method=getByPage&cid=${param.cid}&pageNumber=${n}">${n }</a>
        </li>
      </c:if>
    </c:forEach>

    <!-- 判断是否是最后一页 -->
    <c:if test="${pb.pageNumber == pb.totalPage }">
      <li class="disabled">
        <a id="Next" href="javascript:void(0)" aria-label="Next">
          <span aria-hidden="true">&raquo;</span>
        </a>
      </li>
    </c:if>
    <c:if test="${pb.pageNumber != pb.totalPage }">
      <li>
        <a id="Next" href="${pageContext.request.contextPath }/product?method=getByPage&cid=${param.cid}&pageNumber=${pb.pageNumber+1}" aria-label="Next">
          <span aria-hidden="true">&raquo;</span>
        </a>
      </li>
    </c:if>

  </ul>
</div>
</c:if>
<!-- 分页结束=======================        -->
