<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-Ua-Compatible" content="IE=EmulateIE8" />
<title>风控方案-编辑方案</title>
	<link rel="stylesheet" type="text/css" href="${path}/css/style.css" />
	<script type="text/javascript" src="${path}/js/jquery-1.11.3.min.js"></script>
	<!-- 引入默认css -->
	<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/default/easyui.css" />
	<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/icon.css" />
	<script type="text/javascript" src="${path}/js/easyui/jquery.min.js"></script>
	<script type="text/javascript" src="${path}/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="${path}/js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/javascript">
	
	//保存
	function saveUser() {
	
	
		
		
		var regFlag = true;
		var username = $.trim($("#type").val());
		if (username == null || username == "") {
			$.messager.alert('提示', "方案名称不能为空!", 'info');
			return false;
		}
		
		if (username.length<5|| username.length>10) {
			$.messager.alert('提示', "方案名称字数请控制在5~10个字", 'info');
			return false;
		}
		
		
		var warningvalue = $.trim($("#warningvalue").val());
		if ($.trim(warningvalue).length==0 || isNaN(warningvalue) || warningvalue.length>10 || warningvalue*1 < 0) {
			$.messager.alert('提示', "预警阈值请输入正确数值!", 'info');
			return false;
		}
		
		var closeoutvalue = $.trim($("#closeoutvalue").val());
		if ($.trim(closeoutvalue).length==0 || isNaN(closeoutvalue) || closeoutvalue.length>10 || closeoutvalue*1 < 0) {
			$.messager.alert('提示', "强平阈值请输入正确数值!", 'info');
			return false;
		}
		
		var closevalue = $.trim($("#closevalue").val());
		if ($.trim(closevalue).length==0 || isNaN(closevalue) || closevalue.length>10 || closevalue*1 < 0) {
			$.messager.alert('提示', "收盘强平阈值请输入正确数值!", 'info');
			return false;
		}
		
	
		if(regFlag){
			var formData = $("#infoForm").serialize();
			$.ajax({
				url : "${path}/cost/editRiskTempallMsg",
				data: formData,
				dataType:"json",
				type : "POST",
				async : false,
				success : function(msg) {
					if (msg == true) {
						$.messager.alert('提示!', '保存成功!','info',function() {
							refreshPage();
						});
					}
					if (msg == false) {
						$.messager.alert('提示!', '保存失败!','error',function() {
							$("#submitId").attr("disabled", false);
						});
					} else if (msg == isExist) {
						$.messager.alert('提示!', '方案名称已存在，请重新输入!','error',function() {
							$("#submitId").attr("disabled", false);
						});
					}
				}
			});
		}
	}
	
	function refreshPage() {//取消
		<c:if test="${empty user.closeoutvalue}">window.location.href = "${path}/cost/riskTemplateList";</c:if>
		<c:if test="${!empty user.closeoutvalue}">location.replace(document.referrer);</c:if>
	}
</script>
</head>
<body>
	<%@include file="../../../head.jsp"%>
	<div class="wrap">
		<%@include file="subzhMaster.jsp"%>
		<div class="Rbox">
     		<div class="mainbox">
                   <h3><a href="${path}/cost/riskTemplateList">风控方案</a><i>/</i><a href="#" class="on">新建方案</a></h3>
                   <div class="main">
                      	<div class="Form">
                      		<form id="infoForm" method="post">
                      		<input type="hidden" value="${user.id}" name="id" id="id"/>
                            <div class="dline">
                                <label>方案名称：</label>
			                   	<p><input type="text" name="type" id="type" maxlength="30" placeholder="请输入风控方案名称" value="${user.type}"/></p>
                            </div>
                            <div class="dline">
                                <label>预警阈值(%)：</label>
			                   	<p><input type="text" name="warningvalue" id="warningvalue" maxlength="10" placeholder="请输入预警阀值" value="${user.warningvalue}"/></p>
                            </div>
                            <div class="dline">
                                <label>强平阈值(%)：</label>
			                   	<p><input type="text" name="closeoutvalue" id="closeoutvalue" maxlength="10" placeholder="请输入强平阈值" value="${user.closeoutvalue}"/></p>
                            </div>
                            <div class="dline">
                                <label>收盘强平阈值(%)：</label>
			                   	<p><input type="text" name="closevalue" id="closevalue" maxlength="10" placeholder="请输入收盘强平阈值" value="${user.closevalue}"/></p>
                            </div>
                            
                            <%-- <div class="dline">
                                <label>中金所CFFEX：</label>
			                   	<p>
                                   	<span style="float:left;">
                                   	<input type="radio" name="newstock" value="1" id="newstock" style="width:14px;height:14px" 
                                   	<c:if test="${user.newstock==1}"> checked</c:if> /> 禁止
                                   	</span>
                                   	<span style="float:left;">
                                   	<input type="radio" name="newstock" value="0" id="newstock1" style="width:14px;height:14px" 
                                   	<c:if test="${user.newstock==0||user.newstock!=1}"> checked</c:if> /> 不禁止
                                   	</span>
                                </p>
                            </div>
                            
                            <div class="dline">
                                <label>上期所SHFE：</label>
			                   	<p>
			                   		<span style="float:left;">
			                   		<input type="radio" name="newstockDay" value="1" id="newstockDay" style="width:14px;height:14px" 
                                   	<c:if test="${user.newstockDay==1}"> checked</c:if> /> 禁止
                                   	</span>
                                   	<span style="float:left;"><input type="radio" name="newstockDay" value="0" id="newstockDay1" style="width:14px;height:14px" 
                                   	<c:if test="${user.newstockDay==0||user.newstockDay!=1}"> checked</c:if> /> 不禁止
                                   	</span>
			                   	</p>
                            </div>
                            <div class="dline">
                                <label>大商所DCE：</label>
			                   	<p>
			                   		<span style="float:left;">
			                   		<input type="radio" name="rubbishstock" value="1" id="rubbishstock" style="width:14px;height:14px" 
                                   	<c:if test="${user.rubbishstock==1}"> checked</c:if> /> 禁止
                                   	</span>
                                   	<span style="float:left;"><input type="radio" name="rubbishstock" value="0"  id="rubbishstock1" style="width:14px;height:14px" 
                                   	<c:if test="${user.rubbishstock==0||user.rubbishstock!=1}"> checked</c:if> /> 不禁止
                                   	</span>
                                </p>
                            </div>
                            <div class="dline">
                                <label>郑商所CZCE：</label>
			                   	<p>
			                   		<span style="float:left;">
			                   		<input type="radio" name="businessplate" value="1" id="businessplate" style="width:14px;height:14px"  
                                   	<c:if test="${user.businessplate==1}"> checked</c:if> /> 禁止
                                   	</span>
                                   	<span style="float:left;"><input type="radio" name="businessplate" value="0" id="businessplate1" style="width:14px;height:14px"  
                                   	<c:if test="${user.businessplate==0||user.businessplate!=1}"> checked</c:if> /> 不禁止
                                   	</span>
                                </p>
                            </div>
                            <div class="dline">
                                <label>上期能源INE：</label>
			                   	<p>
			                   		<span style="float:left;">
			                   		<input type="radio" name="ineLimit" value="1" id="ineLimit" style="width:14px;height:14px"  
                                   	<c:if test="${user.ineLimit==1}"> checked</c:if> /> 禁止
                                   	</span>
                                   	<span style="float:left;"><input type="radio" name="ineLimit" value="0" id="ineLimit1" style="width:14px;height:14px"  
                                   	<c:if test="${user.ineLimit==0||user.ineLimit!=1}"> checked</c:if> /> 不禁止
                                   	</span>
                                </p>
                            </div> --%>
                            </form>
                            <div class="dline">
                                 <label style="color:#fff;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                                 <button class="Submit" onclick="saveUser();" id="submitId">提交</button>
                                 <button class="Submit cancel" onclick="refreshPage()">取消</button>
                            </div>
                       </div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>