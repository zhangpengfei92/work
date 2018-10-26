<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<!DOCTYPE html>
<html> 
<head>
<meta charset="utf-8">
<title>网关支付</title>
<meta name="viewport" content="width=320,maximum-scale=1,user-scalable=no">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<!-- <meta content="telephone=no" name="format-detection"> -->
<meta name="format-detection" content="telephone=yes"/><!-- 打电话 -->

<link href="${path }/apppay/css/css.css" rel="stylesheet" type="text/css" />
<link href="${path }/apppay/css/animation.css" rel="stylesheet" type="text/css" />
<script src="${path }/apppay/js/jquery-1.9.1.min.js" type="text/javascript" ></script>
<script src="${path }/apppay/js/All.js" type="text/javascript"></script>

	<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/default/easyui.css" />
	<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/icon.css" />
	<script type="text/javascript" src="${path}/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="${path}/js/easyui/locale/easyui-lang-zh_CN.js"></script>

<script src="http://pv.sohu.com/cityjson?ie=utf-8"></script>
<script type="text/javascript">
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
		debugger;
		var moneys =$.trim($("#Moneys").val());
		if(moneys<1){
			$.messager.alert('提示',"充值金额1元起",'info');
			bFlag = false;
	        return false;
		}
		moneys=moneys*100;
    	if (moneys.length < 1 || moneys.length > 20) {
        	try {
				javatojs.showToast("请输入充值金额");
			} catch (e){ 
				javatojs.showToast("请输入充值金额");
        	}
            return false;
        }
    	var bFlag = true;
		if(!checkDouble(moneys, "充值金额")){
			bFlag = false;
	        return false;
	    }

		var bankco = $.trim($("#Bankco").val()); 
		var cardno = $.trim($("#cardno").val()); 
		if(bFlag){
			window.location.href="${path}/subzhfund/ispay/"+bankco+"/"+moneys+"/"+cardno;
		}
	}
	
	//Double验证
	function checkDouble(strValue, strName) {
	   var pattern=/^[\-\+]?([0-9]\d*|0|[1-9]\d{0,2}(,\d{3})*)(\.\d+)?$/;
	   if(!pattern.test(strValue)){

		   try {
				javatojs.showToast(strName + "的数值不正确！");
			} catch (e){ 
   
				javatojs.showToast(strName + "的数值不正确！");
			}
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
<body class="grey">
    <div class="category">
    <section class="phonewrap2">
             <div class="paybox2">
                  <h3>顺付充值</h3>
                  <div class="paymethod2">
                       <div class="paylist paylist2">
                        <form action="" id="infoInputForm" method="post">
                        <!--使用的充值方式，app端默认快捷支付  -->
                        	<input type="hidden" value="wgzf" id="Bankco">
                            <ul>
                                <li>
                                    <div class="payicon wpayicon"><p class="icon02"><img src="${path }/apppay/images/jinb.png"></p>
                                    <p class="icontit2">充值金额：<input type="text" id="Moneys" name="Moneys" value="0.01" placeholder="请输入充值金额" 
                                    onkeyup="checkNum(this.value)" onpaste="checkNum(this.value)"/>$(美元)</p>
                                    <span></span>
                                     <p class="icontit2">银行卡号：<input type="text" id="cardno" name="cardno" value="00000000000000000000" placeholder="请输入银行卡号"/></p>
                                    <span></span>
                                    
                                    </div>
                                </li>
                            </ul>
             				<div class="paymon">
	             				<p><a href="javascript:void(0);" class="consu" onclick="buy()">提交</a></p>
			                </div>
                        </form>    
                       </div>
                  </div>
             </div>
    </section>
    </div>
</body>
</html>