<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>网关充值</title>
<link href="${path }/apppay/pay/pay.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/icon.css" />

<script src="${path}/apppay/js/jquery-1.9.1.min.js" type="text/javascript" ></script>
<script type="text/javascript" src="${path}/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${path}/js/easyui/locale/easyui-lang-zh_CN.js"></script>
<script type="text/JavaScript" src="${path }/js/DateFormatter.js"></script>
<link rel="stylesheet" href="${path}/layui/css/layui.css"  media="all">
<script src="${path}/layui/layui.js" charset="utf-8"></script>
<script src="http://pv.sohu.com/cityjson?ie=utf-8"></script>
<script type="text/javascript">
$(function () {
    var nowDate = new Date().format("yyyyMMddhhmmss");
    var subzh=$("#subzh").val();
    $("#orderno").val($("#subzh").val()+nowDate);
});

	$("#Moneys").bind("keypress", function(event) {
	    var event= event || window.event;  
	    var getValue = $(this).val();  
	    //控制只能输入的值  
	    
	    if (event.which && (event.which < 48 || event.which > 57) && event.which != 8 && event.which != 46) {  
	        event.preventDefault();  
	         return;  
	    }
	    if (getValue.length == 1 && getValue.substring(0,1) == "0" && event.which != 46) {  
	        event.preventDefault();  
	        return;  
	    }
	  	//控制只能输入一个小数点"."  
	    if (getValue.indexOf('.') != -1 && event.which == 46) {  
	        event.preventDefault();   
	        return;  
	    }
	  	//小数点后面只能输2位
	    if(getValue.indexOf('.') != -1 && event.which != 46){
	    	var arr = getValue.split(".");
		    if(arr[1].length > 1){
		    	event.preventDefault();  
		        return; 
		    }
	    }
	 })
	
	function buy(){
		var moneys =$.trim($("#Moneys").val());
		
    	if (moneys.length < 1 || moneys.length > 20) {
    		$.messager.alert('提示',"请输入充值金额",'info');
            return false;
        }
    	var bFlag = true;
		if(!checkDouble(moneys, "充值金额")){
			bFlag = false;
	        return false;
	    }
		
		var chooseCode=$("input[name='choose']:checked").val();
		 
		
		if(bFlag){
			if(chooseCode==0){
				$("#fund").val(moneys);
				$("#payfrm").submit();
			}else if(chooseCode==1){//线下支付
				window.location.href="${path}/guqipay/offlinePay";
			}
			
		}
	}
	
	//Double验证
	function checkDouble(strValue, strName) {
	   var pattern=/^[\-\+]?([0-9]\d*|0|[1-9]\d{0,2}(,\d{3})*)(\.\d+)?$/;
	   if(!pattern.test(strValue)){
		   $.messager.alert('提示',strName + "的数值不正确！",'info');
		   
	  	   return false;
	   }
	   return true;
	}
	
	//判断输入的金额是否为数字
	function checkNum(num){
		 
		var b=true;
		if(isNaN(num)){

			$.messager.alert('提示',"充值金额必须为数字",'info');

			$("#Moneys").val("");
			return false;
		}		
		return b;
	}
	
</script>

</head>
<body>
   <div class="bg">
	
		<div class="top">
			<div class="rechange"></div>
		</div>
	
	
	<div class="main0">
		<table class="pay">
			<tr>
				<td class="tit1">充值金额：</td>
				<td><input type="text" placeholder="请输入充值金额" name="Moneys" id="Moneys"  onkeyup="checkNum(this.value)" onpaste="checkNum(this.value)" class="num"/><i>元</i></td>
			</tr>
			
			<tr >
				<td class="choosebox"><input type="radio" name="choose" value="0" checked="checked"/><i>快捷支付</i></td>
				
			</tr>
			<tr>
				<td class="choosebox"><input type="radio" name="choose" value="1"  /><i>线下支付</i></td>
				
			</tr>
			
			
		</table>
		
		
	
	</div>
	<div class="line0"></div>
	<a class="ok" onclick="buy()" href="JavaScript:void(0)">确认支付</a>
	
	
	 <form id="payfrm" action="${path}/guqipay/doPay" method="post">
    	<input type="hidden" id="subzh" name="subzh" value="${sub.subzh}">
    	<input type="hidden" id="fund" name="fund">
    	<input type="hidden" id="orderno" name="orderno">
    	<input type="hidden" id="bankcode" name="bankcode" value="U_Q_01">
    </form>
</div>	
</body>
</html>