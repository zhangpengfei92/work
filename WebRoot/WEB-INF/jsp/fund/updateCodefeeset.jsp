<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>收费方案</title>
<link rel="stylesheet" type="text/css" href="${path}/css/style.css" />
	<script type="text/javascript" src="${path}/js/jquery-1.11.3.min.js"></script>
	<!-- 引入默认css -->
	<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/default/easyui.css" />
	<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/icon.css" />
	<script type="text/javascript" src="${path}/js/easyui/jquery.min.js"></script>
	<script type="text/javascript" src="${path}/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="${path}/js/easyui/locale/easyui-lang-zh_CN.js"></script>
</head>
	<style type="text/css">
		.otherCss{ height:32px;border-bottom:1px solid #e9e9e9;}
		.otherCss li {float: left;cursor: pointer;display: block;width: 130px;text-align: center;height: 32px;line-height: 32px;background: #ddd; margin-right: 15px;}
		.otherCss li.select{background:#1890ff;color:#fff;}
	</style>
<script type="text/javascript">
	$(function(){
		//$("#left_a_2").addClass('on');
		//var riskTemplateIdValue = $("#riskTemplateIdValue").val();
		//if(riskTemplateIdValue != null && riskTemplateIdValue != ""){
		//	$("#riskTemplateId").val(riskTemplateIdValue);
		//}
	});
	
	function setNext(){//重置
		$("#nickname").val("");
	}

	//Double验证
	function checkDouble(strValue, strName) {
	   var pattern=/^[\-\+]?([0-9]\d*|0|[1-9]\d{0,2}(,\d{3})*)(\.\d+)?$/;
	   if(!pattern.test(strValue)){
		   $.messager.alert('提示', strName + "的数值不正确！", 'info');
	  	   return false;
	   }
	   return true;
	}
	
	function clickSave() {
		
    	var bFlag = true;
    	
        //验证开仓手续费(手)
    	var counterfee1 = $.trim($("#counterfee1").val());
    	if (counterfee1.length < 1) {
        	$.messager.alert('提示', '请输入正确的开仓手续费(按手数)', 'info');
            return false;
        }
    	if(!checkDouble(counterfee1, "开仓手续费(按手数)")){
			bFlag = false;
            return false;
        }
    	
        //验证开仓手续费(比)
    	var counterfee2 = $.trim($("#counterfee2").val());
    	if (counterfee2.length < 1) {
        	$.messager.alert('提示', '请输入正确的开仓手续费(按金额%)', 'info');
            return false;
        }
    	if(!checkDouble(counterfee2, "开仓手续费(按金额%)")){
			bFlag = false;
            return false;
        }
    	
        //验证平仓手续费(手)
    	var pcfee1 = $.trim($("#pcfee1").val());
    	if (pcfee1.length < 1) {
        	$.messager.alert('提示', '请输入正确的平仓手续费(按手数)', 'info');
            return false;
        }
    	if(!checkDouble(pcfee1, "平仓手续费(按手数)")){
			bFlag = false;
            return false;
        }
    	
        //验证平仓手续费(比)
    	var pcfee2 = $.trim($("#pcfee2").val());
        if (pcfee2.length < 1) {
        	$.messager.alert('提示', '请输入正确的平仓手续费(按金额%)', 'info');
            return false;
        }
        if(!checkDouble(pcfee2, "平仓手续费(按金额%)")){
			bFlag = false;
            return false;
        }
        
        if(bFlag){
	       	var url = "${path}/codefeeset/editcodefeesetMsgMore";
    		$.ajax({
    			type:"POST",
    			url:url,
    			data : {
    				"bz" : $("#codefeesetIds").val(),
					"counterfee1" : counterfee1,
					"counterfee2" : counterfee2,
					"pcfee1" : pcfee1,
					"pcfee2" : pcfee2
				},
				type : "POST",
    			async : false,
	         	success: function (data) {
	         		if (data == true) {
						$.messager.alert('提示!', '保存成功!','info',function() {
							refreshPage();
						});
					} else if (data == false) {
						$.messager.alert('提示!', '保存失败!','error',function() {
							$("#submitId").attr("disabled", false);
						});
					} else if (data == "isExist") {
						$.messager.alert('提示!', '方案名称已存在，请重新输入!','error',function() {
							$("#submitId").attr("disabled", false);
						});
					}
	         	},
	         	error: function (returndata) {  
		              $.messager.alert('提示!', returndata,'error',function() {
							$("#submitId").attr("disabled", false);
						});
		          },
					complete : function() {//请求完成之后调用 全查询
						//关闭弹出层
						var index = parent.layer.getFrameIndex(window.name);  
						window.parent.layer.close(index); 
					}
			});
        }
    }
	
	function changePage(value){//tab切换
		window.location.href = value;
	}
	
	function refreshPage() {
		//关闭弹出层
		var index = parent.layer.getFrameIndex(window.name);  
		window.parent.layer.close(index); 
	}
	
</script>
<body>
	<div class="main">
        <div class="layForm">
            <input type="hidden" value="${codefeesetId}" id="codefeesetIds" name="codefeesetIds" />
            <div class="laydline">
	            <label style="width:160px;text-align:left;"><font color="red">*</font>开仓手续费(按手数)：</label>
	            <input type="text" id="counterfee1" name="counterfee1" value=""/>
	        </div>
            <div class="laydline">
	            <label style="width:160px;text-align:left;"><font color="red">*</font>开仓手续费(按金额%)：</label>
	            <input type="text" id="counterfee2" name="counterfee2" value=""/>
	        </div>
	        <div class="laydline">
	          	<label style="width:160px;text-align:left;"><font color="red">*</font>平仓手续费(按手数)：</label>
	            <input type="text" id="pcfee1" name="pcfee1" value=""/>
	        </div>
	        <div class="laydline">
	            <label style="width:160px;text-align:left;"><font color="red">*</font>平仓手续费(按金额%)：</label>
	            <input type="text" id="pcfee2" name="pcfee2" value=""/>
	        </div>
            <div class="laydline">
               	<label style="color:#fff;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                   <button class="Submit" onclick="clickSave();" id="submitId">提交</button>
                   <button class="Submit cancel" onclick="refreshPage()">取消</button>
               </div>
           </div>
       </div>
</body>
</html>