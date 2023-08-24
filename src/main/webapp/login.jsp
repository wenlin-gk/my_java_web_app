<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!doctype html>
<html id="login.jsp">
<head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css" type="text/css" />
  <script src="${pageContext.request.contextPath}/js/jquery-1.11.3.min.js" type="text/javascript"></script>

  <style>
    body {
      margin-top: 20px;
      margin: 0 auto;
    }

    .carousel-inner .item img {
      width: 100%;
      height: 300px;
    }

    font {
      color: #666;
      font-size: 22px;
      font-weight: normal;
      padding-right: 17px;
    }
  </style>
</head>

<body>
  <div class="container" style="width:100%;height:460px;background:#FF2C4C url('${pageContext.request.contextPath}/images/loginbg.jpg') no-repeat;">
    <div class="row">
      <div class="col-md-5">
        <div style="width: 440px; border: 1px solid #E7E7E7; padding: 20px 0 20px 30px; border-radius: 5px; margin-top: 60px; background: #fff;">
          <font>会员登录</font>USER LOGIN <a id="msg">${msg }</a>

          <div>&nbsp;</div>
          <form class="form-horizontal" action="${pageContext.request.contextPath }/user" method="get" target="_parent">
            <input type="hidden" name="method" value="login">

            <div class="form-group">
              <label for="username" class="col-sm-2 control-label">用户名</label>
              <div class="col-sm-6">
                <input type="text" class="form-control" id="username" placeholder="请输入用户名" name="username" value="${username }">
                <span id="s1"></span>
              </div>
            </div>
            <div class="form-group">
              <label for="inputPassword3" class="col-sm-2 control-label">密码</label>
              <div class="col-sm-6">
                <input type="password" class="form-control" id="inputPassword3" placeholder="请输入密码" name="password">
              </div>
            </div>
            <div class="form-group">
              <div class="col-sm-offset-2 col-sm-10">
                <div class="checkbox">
                  <label>
                    <input type="checkbox" name="autologin" value="1"> 自动登录
                  </label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                  <label>
                    <input type="checkbox" name="savename" value="1">
                    记住用户名
                  </label>
                </div>
              </div>
            </div>
            <div class="form-group">
              <div class="col-sm-offset-2 col-sm-10">
                <input disabled type="submit" id="loginBut" class="form-control" width="100" value="登录" name="submit" border="0" style="background: url('${pageContext.request.contextPath}/images/login.gif') no-repeat scroll 0 0 rgba(0, 0, 0, 0);
    height:35px;width:100px;color:white;">
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>

  <script type="text/javascript">
    $(
        function(){
          $("#username").blur(
            function(){
              //获得文本框的值：
              var val =$(this).val();
              //异步发送数据：
              if(val!=""){
                var url = "${pageContext.request.contextPath }/user";
                var params = {"method":"checkUsername","username":val};
                $.post(url,params,function(data){
                  if(data == 2){
                    $("#s1").html("用户名可以使用").css("color","#0f0");
                    $("#loginBut").attr("disabled",false);
                  }else if(data == 1){
                    $("#s1").html("用户名不存在").css("color","#f00");
                    $("#loginBut").attr("disabled",true);
                  }
                });
              }
            }
          );
        }
      );
  </script>

</body>
</html>