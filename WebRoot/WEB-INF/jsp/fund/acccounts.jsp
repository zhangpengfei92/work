<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户调账</title>
<link rel="stylesheet" type="text/css" href="${path}/css/style.css" />
	<script type="text/javascript" src="${path}/js/jquery-1.11.3.min.js"></script>
	<!-- 引入默认css -->
	<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/default/easyui.css" />
	<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/icon.css" />
	<script type="text/javascript" src="${path}/js/easyui/jquery.min.js"></script>
	<script type="text/javascript" src="${path}/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="${path}/js/easyui/locale/easyui-lang-zh_CN.js"></script>
	<!--引入layui  -->
	<script type="text/javascript" src="${path}/js/layer/layer.js"></script>
	<link rel="stylesheet" href="${path}/layui/css/layui.css"  media="all">
	<script src="${path}/layui/layui.js" charset="utf-8"></script>
</head>
<script type="text/javascript">
    var rate= "<%=session.getAttribute("EXCHANGE_RATE")%>"; 
	$(function(){
		//$("#left_a_2").addClass('on');
	});
	
	function chargeRate() {
		var accmoney=$.trim($("#accmoney").val());
		var auto= Math.ceil(accmoney*100/rate);
		$("#rate").val(auto/100);
	}
	
	function saveData() {
		$.messager.confirm('提示', '您确定要给该用户调账吗?', function(yn) {
		if (yn) {
		var flag=true;
		var username=$.trim($("#username").val());
		if(username==null||username==""){
			alert("请输入调账账户")
			flag= false;
			return;
		}
		var accmoney=$.trim($("#accmoney").val());
		if(accmoney==""||accmoney==null){
			alert("调账金额不能为空")
			flag= false;
			return;
		}
		
		var mreg = /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/;
    	if (!mreg.test(accmoney) || accmoney<=0 ) {
        	$.messager.alert('提示', '请输入正确金额', 'info');
            return false;
        }
		
		
		 var mstatus = $("input[name='mstatus']:checked").val();
		 layer.confirm('确定要调账吗?', { btn : [ '确定', '取消' ]},function(index){
			 layer.close(index);
			 var index = layer.load(0,{shade: [0.7, '#393D49']}, {shadeClose: true});
			$.post("${path}/accounts/saveAccount",{
				"username":username,
				"accmoney":accmoney,
				"mstatus":mstatus
				},function(str){
					if(str.status==true){
						$.messager.confirm('提示',str.msg, function(r){
					if(str.status=="true"){
						$.messager.confirm('提示',str.msg, function(r){
					layer.close(index);
					if(str.status==true){
							cleanData();
						$.messager.alert('提示', '调账成功', 'info');
					}else{
						$.messager.alert('提示', '调账失败', 'info');
					}
			  });
		 });
		}
		});
	}
	
	
	
	
	function  cleanData() {
		$("#username").val('');
		$("#accmoney").val("");
		$("#rate").val("");
	}
	
	//加载layui的from表单
	$(function($) {
		layui.use(['form', 'layedit', 'laydate'], function(){
			  var form = layui.form
			  ,layer = layui.layer
			  ,layedit = layui.layedit
			  ,laydate = layui.laydate;
			});
	}); 
	
</script>
<body>
	<%@include file="../../../head.jsp"%>
	<div class="wrap">
		<%@include file="subzhMaster.jsp"%>
		<div class="Rbox">
            	<div class="mainbox">
            		<div class="Inputbox">
            	 		<h3><a href="${path}/contract/queryAll" class="on">用户调账</a></h3> 
                	 		<form class="layui-form"  style="width: 500px">
								  <div class="layui-form-item">
								    <label class="layui-form-label">调账账户</label>
								    <div class="layui-input-block">
								      <input type="text" name="username" id="username" lay-verify="title" autocomplete="off"  placeholder="账户" class="layui-input" >
								    </div>
								  </div>
									
									<div class="layui-form-item">
								    <label class="layui-form-label">调账金额</label>
								    <div class="layui-input-block">
								      <input type="text" name="accmoney" id="accmoney" lay-verify="title" autocomplete="off"   placeholder="金额" class="layui-input" onkeyup="chargeRate()" onkeyup="value=value.replace(/[^\-?\d.]/g,'')">
								      <font style="color: red;">*调账输入金额单位元(人民币)*</font>
								      <input type="text" name="rate" id="rate" lay-verify="title" autocomplete="off" placeholder="对应美元($)"   class="layui-input" readonly="readonly">
								      <font style="color: red;">*调账输入金额转换美元($)*</font>
								    </div>
								  </div>
								  <div class="layui-form-item">
									    <label class="layui-form-label">调账类型</label>
									    <div class="layui-input-block">
									      <input type="radio" name="mstatus" id="mstatus" value="1" title="入金" checked="">
									      <input type="radio" name="mstatus" id="mstatus" value="2" title="出金">
									    </div>
								  </div>
						</form> 
						<div class="layui-form-item" style="width: 500px">
								 <div class="layui-input-block">
								      <button class="layui-btn" lay-filter="demo1" onclick="saveData()">立即提交</button>
								     <!-- <button class="layui-btn" lay-filter="demo1" onclick="cleanData()">取消调账</button> -->
								 </div>
						</div>
               </div>
          </div>
     </div>
   </div> 
</body>
</html>