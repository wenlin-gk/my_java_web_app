<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html id="register.jsp">
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
      color: #3164af;
      font-size: 18px;
      font-weight: normal;
      padding: 0 10px;
    }
  </style>
</head>
<body>
  <div class="container" style="width:100%;background:url('${pageContext.request.contextPath}/images/regist_bg.jpg');">
    <div class="row">

      <div class="col-md-8" style="background: #fff; padding: 40px 80px; margin: 30px; border: 7px solid #ccc;">
        <font>会员注册</font>USER REGISTER

        <form class="form-horizontal" style="margin-top: 5px;" method="get" action="${pageContext.request.contextPath }/user">
          <input type="hidden" name="method" value="regist">
          <div class="form-group">
            <label for="username" class="col-sm-2 control-label">用户名</label>
            <div class="col-sm-6">
              <input type="text" class="form-control" id="username" placeholder="请输入用户名" name="username" required>
              <span id="s1"></span>
            </div>
            必填，1-20个字符
          </div>
          <div class="form-group">
            <label for="inputPassword3" class="col-sm-2 control-label">密码</label>
            <div class="col-sm-6">
              <input type="password" class="form-control" id="inputPassword3" placeholder="请输入密码" name="password" required>
            </div>
            必填，1-20个字符
          </div>
          <div class="form-group">
            <label for="confirmpwd" class="col-sm-2 control-label">确认密码</label>
            <div class="col-sm-6">
              <input type="password" class="form-control" id="confirmpwd" placeholder="请输入确认密码" required>
            </div>
            必填，1-20个字符
          </div>
          <div class="form-group">
            <label for="inputEmail3" class="col-sm-2 control-label">Email</label>
            <div class="col-sm-6">
              <input type="email" class="form-control" id="inputEmail3" placeholder="Email" name="email" required>
            </div>
            必填，6-30个字符
          </div>
          <div class="form-group">
            <label for="usercaption" class="col-sm-2 control-label">姓名</label>
            <div class="col-sm-6">
              <input type="text" class="form-control" id="usercaption" placeholder="请输入姓名" name="name" required>
            </div>
            必填，1-20个字符
          </div>
          <div class="form-group opt">
            <label for="inlineRadio1" class="col-sm-2 control-label">性别</label>
            <div class="col-sm-6">
              <label class="radio-inline">
                <input type="radio" name="sex" id="inlineRadio1" value="1"> 男
              </label>
              <label class="radio-inline">
                <input type="radio" name="sex" id="inlineRadio2" value="0"> 女
              </label>
            </div>
          </div>
          <div class="form-group">
            <label for="date" class="col-sm-2 control-label">出生日期</label>
            <div class="col-sm-6">
              <input type="date" class="form-control" name="birthday">
            </div>
          </div>
          <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
              <input disabled type="submit" id="regBut" class="form-control" width="100" value="注册" name="submit" border="0" style="background: url('${pageContext.request.contextPath}/images/register.gif') no-repeat scroll 0 0 rgba(0, 0, 0, 0);
            height:35px;width:100px;color:white;">
            </div>
          </div>
        </form>

      </div>

      <div class="col-md-2"></div>

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
                  if(data == 1){
                    $("#s1").html("用户名可以使用").css("color","#0f0");
                    $("#regBut").attr("disabled",false);
                  }else if(data == 2){
                    $("#s1").html("用户名已经被注册").css("color","#f00");
                    $("#regBut").attr("disabled",true);
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