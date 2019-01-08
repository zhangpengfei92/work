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
<script type="text/JavaScript" src="${path }/js/DateFormatter.js"></script>
<script src="http://pv.sohu.com/cityjson?ie=utf-8"></script>

<!-- 引入layui -->
<link rel="stylesheet" href="${path}/layui/css/layui.css" media="all">
<script src="${path}/js/layer/layer.js" charset="utf-8"></script>
<script src="${path}/layui/layui.js" charset="utf-8"></script>
<script type="text/javascript">
	$(function () {
	    var nowDate = new Date().format("yyyyMMddhhmmss");
	    var subzh=$("#subzh").val();
	    $("#orderno").val($("#subzh").val()+nowDate);
	    
	   
	    console.log($(this).parent().parent());
		$.post("${path}/apppay/PhonePay/pagequeryfund?subzh="+subzh,function(data){
			
			console.log(data.fundbalance);
			var fund=data.fundbalance;	
			 $(".payp").text(fund.toFixed(2)+" 元");

			 $("#availMoney").val(fund.toFixed(2));
		})
	});
	
	function layer_alert(msg) {
		  layer.open({
			    content: msg
			    ,btn: '确定'
			  });		
	}
	
	function buy(){

		$(".consu").removeAttr("onclick");
		var bFlag = true;

		

		var fund = $.trim($("#fund").val());
    	if (fund.length < 1 || fund.length > 20) {
        	layer_alert('请输入提现金额')
        	bFlag = false;	
        	layer_alert('请输入提现金额长度在2-20之间')
            return false;
        }
    
		if(!checkDouble(fund, "提现金额") || fund<=0){
			bFlag = false;
	        return false;
	    }
		
		if(fund<50||fund>45000){
			layer_alert('单次可提现金额50~45000')		
			bFlag = false;
            return false;
		}
		
		if(fund<=0){
			layer_alert('提现金额不合法')
			bFlag = false;
	        return false;
	    }
		
		var availMoney = $("#availMoney").val();
		if(availMoney*1 < fund*1){
			layer_alert('账户资产不足')
			bFlag = false;			
            return false;
		} 
		
		var name = $.trim($("#name").val());
    	if (name.length < 1 || name.length > 20) {
        	layer_alert('请输入姓名')
        	bFlag = false;	
            return false;
        }
    	
    	var bankCardNumber = $.trim($("#bankCardNumber").val());
    	if (bankCardNumber.length < 1 || bankCardNumber.length > 20) {
    		layer_alert('请输入银行卡号') 
    		bFlag = false;	
            return false;
        }
		
    	
    	
		if(bFlag){
            $("#infoInputForm").submit();
		}
	}
	
	//Double验证
	function checkDouble(strValue, strName) {
		var pattern = /(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)/;
	   if(!pattern.test(strValue)){
		   alert(strName + "的数值不正确！");
		   try {
				javatojs.showToast(strName + "的数值不正确！");
			} catch (e){ 
       		window.location.href = "alert://alertInfo?message=" + strName + "的数值不正确！";
       	}
	  	   return false;
	   }
	   return true;
	}
	
	//消费记录
	function xfjl(){
		var subzh = $("#subzh").val();
		window.location.href="${path}/clientshow/phonePay!xfjl.action?subzh="+subzh;
	}
	
	//判断输入的金额是否为数字
	function checkNum(num){
		var b=true;
		if(isNaN(num)){
			layer_alert('充值金额必须为数字')  
			$("#fund").val("");
			return false;
		}
		return b;
	}
	
</script>
</head> 
<body class="grey">
    <div class="category">
    <section class="phonewrap2">
             <div class="Monbox">
                   <div class="paymon">
                   <p>账户资产</p>
                 <p class="payp"></p>   
                   </div>
             </div>
             <input type="hidden" id="availMoney" name="availMoney" value="<fmt:formatNumber value="${sub.fundbalance==null?0:sub.fundbalance}" pattern="0.00"/>"/>            
             <div class="paybox2">
                  <h3>提现</h3>
                  <div class="paymethod2">
                       <div class="paylist paylist2">
                        <form action="${path}/apppay/PhonePay/withdrawend" id="infoInputForm" method="post">
                            <ul>
                                <li>
                                    <div class="payicon wpayicon"><p class="icon02"><img src="${path }/apppay/images/jinb.png"></p><p class="icontit2">
                                    	提现金额：<input type="text" id="fund" name="fund" value="" placeholder="请输入提现金额"
                                    	onkeyup="checkNum(this.value)" onpaste="checkNum(this.value)"/>元</p><span></span></div>
                                </li>
                                <li>
                                    <div class="payicon wpayicon"><p class="icon02"></p><p class="icontit2">姓名：<input type="text" id="name" name="name" value="${sub.isadmin==6?sub.name:''}"  <c:if test="${sub.isadmin==6}">readonly='readonly'</c:if> placeholder="请输入姓名"/></p><span></span></div>
                                </li>
                                <li>
                                    <div class="payicon wpayicon"><p class="icon02"></p><p class="icontit2">银行卡号：<input type="text" id="bankCardNumber" name="bankCardNumber" value="${sub.isadmin==6?sub.bankCard:''}"<c:if test="${sub.isadmin==6}">readonly='readonly'</c:if> placeholder="请输入银行卡号"/></p><span></span></div>
                                </li>
                                
                                 <li>
                                    <div class="payicon wpayicon"><p class="icon02"></p><p class="icontit2">开户银行：<input type="text" id="bankname" name="bankname" value="${sub.isadmin==6?sub.bank:''}" <c:if test="${sub.isadmin==6}">readonly='readonly'</c:if>  placeholder="请输入开户行"/></p><span></span></div>
                                </li>
                                
                                 <li>
                                    <div class="payicon wpayicon"><p class="icon02"></p><p class="icontit2">开户支行：<input type="text" id="banchname" name="banchname" value="${sub.isadmin==6?sub.qq:''}" <c:if test="${sub.isadmin==6}">readonly='readonly'</c:if> placeholder="请输入开户支行"/></p><span></span></div>
                                </li>
                            </ul>
             				<input type="hidden" id="subzh" name="subzh" value="${sub.subzh}"/>  
             				<input type="hidden" id="orderno" name="orderno" />                	
             				<div class="paymon">
	             				<p><a href="javascript:void(0);" class="consu" onclick="buy()" id="sbn">提交</a></p>
			                </div>
                        </form>    
                       </div>
                  </div>
                 <!--  <h3>提现时间为周一至周五工作日内，到账时间为T+1日，如遇节假日顺延。提现中如遇到任何问题，请联系客服 <em></em></h3> -->
             </div>
    </section>
    </div>
</body>
</html>