<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>首页</title>
	<link rel="stylesheet" type="text/css" href="${path}/css/style.css" />
	<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/default/easyui.css" />
	<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/icon.css" />
	
	<script type="text/javascript" src="${path}/js/jquery-1.11.3.min.js"></script>
	<script type="text/javascript" src="${path}/js/easyui/jquery.min.js"></script>
	<script type="text/javascript" src="${path}/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="${path}/js/easyui/locale/easyui-lang-zh_CN.js"></script>
</head>
<script type="text/javascript">
	$(function(){
		$("#nav_1").addClass('.current');
	});
	
</script>
<body>
	<%@include file="../../../head.jsp"%>
	<div class="wrap">
		<%@include file="subzhMaster.jsp"%>
		<div class="Rbox">
			<div class="mainbox">
	       		<h3><a href="${path}/tradeData/holderCompareList" class="on">首页</a></h3>	         	
					<div class="main" style="width:auto;min-height: 800px;background:url(${path}/images/bjt.jpg)no-repeat;">
						<div class="Inputbox" style="color:#fff; font-size:48px; text-align:center; padding-top:200px;">
							<label>您好，<span>${sessionScope.SESSION_LOGINNAME}</span>&nbsp;&nbsp;欢迎进入国际期货综合管理系统</label><br/>
						</div>
					</div>
			</div>
		</div>
    </div>
</body>
</html>