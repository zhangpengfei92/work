<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html> 
<head>
<meta charset="utf-8">
<title>H5提现</title>
<meta name="viewport" content="width=320,maximum-scale=1,user-scalable=no">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<!-- <meta content="telephone=no" name="format-detection"> -->
<meta name="format-detection" content="telephone=yes"/><!-- 打电话 -->
<link href="${path }/apppay/css/css.css" rel="stylesheet" type="text/css" />
<link href="${path }/apppay/css/animation.css" rel="stylesheet" type="text/css" />
<script src="${path }/apppay/js/jquery-1.9.1.min.js" type="text/javascript" ></script>
<script src="${path }/apppay/js/All.js" type="text/javascript"></script>
<script src="${path }/apppay/js/city.js" type="text/javascript"></script>
<script src="http://pv.sohu.com/cityjson?ie=utf-8"></script>

<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/default/easyui.css" />
<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/icon.css" />
<script type="text/javascript" src="${path}/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${path}/js/easyui/locale/easyui-lang-zh_CN.js"></script>

<style type="text/css">
	
	.sel{height:40px;
		padding-top:0.3rem;
		appearance: none;
		-webkit-appearance: none;
		-moz-appearance: none;
		}

</style>

<script type="text/javascript">
	function buy(){
		var amount = $.trim($("#amount").val());
    	if (amount.length < 1 || amount.length > 20) {
       	$.messager.alert('提示',"请输入提现金额",'info');
            return false;
        }
    	var bFlag = true;
		if(!checkDouble(amount, "提现金额")){
			bFlag = false;
	        return false;
	    }
		
		if(isNaN(amount)||amount<2.0){

			$.messager.alert('提示',"提现金额必须为数字,且金额必须大于2.0",'info');

			$(amount).val("");
			return false;
		}
		
		var fundbalance=$("#fund").val();
		
		if(fundbalance-amount<0){
			$.messager.alert('提示',"余额不足",'info');
			bFlag = false;
	        return false;
		}
		
		var name = $.trim($("#name").val());
    	if (name.length < 1 || name.length > 20) {
   	

				$.messager.alert('提示',"请输入姓名",'info');
			
            return false;
        }
    	
    	var bankCardNumber = $.trim($("#bankCardNumber").val());
    	if (bankCardNumber.length < 1 || bankCardNumber.length > 20) {
        
        $.messager.alert('提示',"请输入银行卡号",'info');
            return false;
        }
		//根据下拉列表中的银行编号获取开户银行名称
 	 var bankname =$("#bankname").val();
 	 var banchname =$("#banchName").val();
     var phoneNum =$("#phoneNum").val();
     var idcard =$("#idcard").val();
     var province=$("#s_province").val();
     var city=$("#s_city").val();
     if("省份"==province || "地级市"==city){
      	$.messager.alert('提示',"开户银行所在地必须选择",'info');
      	return false;
      }	
		if(bFlag){
			 $.ajaxSetup({async: false});
			 $.post("${path}/subzhfund/savePay",{
				 "amount":amount,
				 "bankname":bankname,
				 "banchname":banchname,
				 "name":name,
				 "bankCardNumber":bankCardNumber,
				 "phoneNum":phoneNum,
				 "idcard":idcard,
				 "province":province,
				 "city":city
			 },function(msg){
				 console.log(msg);
				 if(msg=="ok"){
					 $.messager.alert('提示',msg+"--提现申请提交成功",'info');
					 window.location.href="${path}/subzhfund/payCallBack";
				 }else{
					 alert(msg)
				 }
			 })
		}
	}
	
	//Double验证
	function checkDouble(strValue, strName) {
	   var pattern=/^[\-\+]?([0-9]\d*|0|[1-9]\d{0,2}(,\d{3})*)(\.\d+)?$/;
	   if(!pattern.test(strValue)){
		    $.messager.alert('提示',"数值不正确！",'info');
		   alert(strName + "的数值不正确！");

	  	   return false;
	   }
	   return true;
	}
	
</script>
</head> 
<body class="grey">
    <div class="category">
    <section class="phonewrap2">
           <div class="Monbox">
                   <div class="paymon">
                   <p class="num">账户资产：<p>
                   <fmt:formatNumber value="${sub.fundbalance==null?0:sub.fundbalance}" pattern="0.00"/>元 </p>             
                   </div>
                   
                 <input type="hidden" id="fund" value="<fmt:formatNumber value="${sub.fundbalance==null?0:sub.fundbalance}" pattern="0.00"/>">

             </div>
             <div class="paybox2">
                  <h3>提现</h3>
                  <div class="paymethod2">
                       <div class="paylist paylist2">
                       	
                            <ul>
                                <li>
                                    <div class="payicon wpayicon"><p class="icon02"><img src="${path }/apppay/images/jinb.png"></p><p class="icontit2">
                                    	提现金额：<input type="text" id="amount" name="amount" value="" placeholder="请输入提现金额"/>元</p><span></span></div>
                                </li>
                                <li>
                                    <div class="payicon wpayicon">
                                    <p class="icon02"></p>

                                <p class="icontit2">开户银行：<input type="text" id="bankname" name="bankname" value="${sub.bank}" placeholder="银行卡编号"/>
                                    <span></span></div>
                                </li>                              
                                 <li>
                                    <div class="payicon wpayicon"><p class="icon02"></p>
                                    <p class="icontit2">银行卡开户支行名称：<input type="text" id="banchName" name="banchName" value="${sub.qq}" placeholder="请输入银行卡开户支行名称"/>
                                    </p><span></span></div>
                                </li>
                                
                                 <li>
                                    <div class="payicon wpayicon"><p class="icon02"></p>
                                    <p class="icontit2">收款人姓名：<input type="text" id="name" name="name" value="${sub.name}" placeholder="请输入收款人姓名"/>
                                    </p><span></span></div>
                                </li>
                                
                                 <li>
                                    <div class="payicon wpayicon"><p class="icon02"></p>
                                    <p class="icontit2">收款人银行账号：<input type="text" id="bankCardNumber" name="bankCardNumber" value="${sub.bankCard}" placeholder="收款人银行账号"/></p>
                                    <span></span></div>
                                </li>
                                
                                   <li>
                                    <div class="payicon wpayicon"><p class="icon02"></p>
                                    <p class="icontit2">预留手机号：<input type="text" id="phoneNum" name="phoneNum" value="${sub.phone}" placeholder="预留手机号"/>
                                    </p><span></span></div>
                                </li>
                                
                                   <li>
                                    <div class="payicon wpayicon"><p class="icon02"></p>
                                    <p class="icontit2">身份证号：<input type="text" id="idcard" name="idcard" value="${sub.idNo}" placeholder="请输入身份证号"/>
                                    </p><span></span></div>
                                </li>
                            
                            <li>
                             <p class="icon02"></p>

                           	<div class="fblineb" style="font-size: 15px;margin: 0 0 0 45px">开户所在地：                           
                                 <select style="border:solid 1px #D9D9D9; margin-right: 10px" id="s_province" name="city" ></select>
                                 <select style="border:solid 1px #D9D9D9;" id="s_city" name="town"></select>
                            </div>
  
                             </li>  
                            </ul>
             				<div class="paymon">
	             				<p><a href="javascript:void(0);" class="consu" onclick="buy()">提交</a></p>
			                </div>
                      
                       </div>
                  </div>          
             </div>
            
    </section>
     </div>
</body>
<script type="text/javascript">
	_init_area();

</script>
</html>