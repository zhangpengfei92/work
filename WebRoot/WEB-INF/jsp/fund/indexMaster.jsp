<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>left menu</title>
<link rel="stylesheet" type="text/css" href="${path}/css/nav.css" />
<script type="text/javascript">
	$(function(){
		$("#index_1").addClass('current');
	});

	//首页
	function indexFunction(){
		window.location.href = "${path}/fund/welcomeIndex";
	}

	//普通会员
	function commonMember() {
		var isadmin="${sessionScope.SESSION_ISADMIN}";
		if(isadmin==1 || isadmin==2){
			isadmin=3;
		}else{
			isadmin=1+parseInt(isadmin);
		}
		window.location.href = "${path}/fund/subzhList?isadmin="+isadmin;
	}
	//主账户管理
	function bdzhList() {
		window.location.href = "${path}/main/querybdzh";
	}
	
	function feeSchemeList() {
		window.location.href = "${path}/codefeeset/codefeesetList";
	}
	function bzjmbList() {
		window.location.href = "${path}/cost/bzjmbList";
	}
	function riskTemplateList() {
		window.location.href = "${path}/cost/riskTemplateList";
	}
	
	//管理員列表
	function wardenList(){
	    window.location.href = "${path}/membersys/jumpwardenList";
	}
	//权限管理
	function roleList(){
	    window.location.href = "${path}/membersys/jumproleList";
	}
	//提现审核
	function withdrawList(){
	    window.location.href = "${path}/fundlog/agentzhfundLogList";
	}
	//资金流水
	function fundList(){
	    window.location.href = "${path}/fundPzlog/fundPzLogList";
	}
	
	//用户调账
	function addFund(){
	    window.location.href = "${path}/accounts/toAccount";
	}
	
	//销售日报
	function fundSettlelogList(){
	    window.location.href = "${path}/fundPzlog/fundSettlelogList";
	}
	//销售月报
	function fundSettlelogMonthList(){
	    window.location.href = "${path}/fundPzlog/fundSumlogList";
	}
	
	//交易管理
	function dealCjList(){
		  window.location.href = "${path}/order/orderlist";
	}
	
	//交易管理--实时数据
	function drTradeList(){
	    window.location.href = "${path}/trade/fundlist";
	}
	//历史记录数据
	function lsTradeList(){
	    window.location.href = "${path}/order/orderlist";
	}
	
	function ccdbList(){
	    window.location.href = "${path}/HolderComparsion/queryList";
	}
	function closeOutList(){
		window.location.href = "${path}/order/closeOutList";
	}
	
	function exceptionOrderList(){
		window.location.href = "${path}/tradeData/exceptionOrderList";
		$('#ulIndex > li').click(function(){	
			$(this).addClass('.current'); //给当前对象加上class='current'
			$(this).siblings().removeClass('.current'); 
		});
	}
	
	//操盘方案
	function  schemeList(){
		window.location.href = "${path}/OperateScheme/queryAll";
	}
	
	//去查看文档信息
	function toApp(){
		 window.location.href = "${path}/doc/queryAll";
	}
	
	//查看合约
	function contractList(){
		 window.location.href = "${path}/contract/queryAll";
	}
	
	//样本账户
	function tradeHedgingList() {
		window.location.href = "${path}/tradeData/tradeHedgingList";
	}
	//机构平台
	function agentPt(){
		window.location.href = "${path}/fund/agentPt";
	}
	
	function riskList(){
		window.location.href = "${path}/fund/welcomeRisk";
	}
	
</script>
</head>
<body>
	<div class="sidebar">
        <div class="sidebar-bg"></div>
        <ul class="nav" id="ulIndex">
           	<li class="nav-li" id="nav_1">
           	  	<a href="javascript:;" class="ue-clear"><i class="nav-ivon"></i><span class="nav-text"><i class="Ficon"><img src="${path}/image/er-ji-dao-hang-1.png"/></i>首页</span></a>
           	  	<ul class="subnav">
	          		<li class="subnav-li" data-id="1" id="index_1"><a id="left_a_0" href="javascript;" onclick="indexFunction();" class="ue-clear"><span class="subnav-text">首页</span></a></li>
	            </ul>
	        </li>
               
            <li class="nav-li last-nav-li" id="nav_2">
              	<shiro:hasRole name="3">
                	<a href="javascript:;" class="ue-clear"><i class="nav-ivon"></i><span class="nav-text"><i class="Ficon"><img src="${path}/image/er-ji-dao-hang-1.png"/></i>账户管理</span></a>
                </shiro:hasRole>	
              	<ul class="subnav">	
              		<c:if test="${sessionScope.SESSION_ISADMIN eq 1 }">
              			<li class="subnav-li" data-id="30" id="index_30"><a id="left_a_30" href="javascript:;" onclick="agentPt()" class="ue-clear"><span class="subnav-text">机构/平台</span></a></li>
              		</c:if>
        			<shiro:hasRole name="zhlist">
		              	<li class="subnav-li" data-id="3" id="index_3"><a id="left_a_3" href="javascript:;" onclick="commonMember()" class="ue-clear"><span class="subnav-text">账户列表</span></a></li>
		             </shiro:hasRole>	
		             <shiro:hasRole name="bdzh">
		              <li class="subnav-li" data-id="14" id="index_14"><a id="left_a_14" href="javascript:;" onclick="bdzhList()" class="ue-clear"><span class="subnav-text">报单账户</span></a></li>
	              	</shiro:hasRole>
	              	
	              	<shiro:hasRole name="payset">
	              	<li class="subnav-li" data-id="8" id="index_8"><a id="left_a_8" href="javascript:;" onclick="feeSchemeList()" class="ue-clear"><span class="subnav-text">手续费方案</span></a></li>
	              	</shiro:hasRole>	
	              	
	              	<shiro:hasRole name="bzj">
	              	<li class="subnav-li" data-id="9" id="index_9"><a id="left_a_9" href="javascript:;" onclick="bzjmbList()" class="ue-clear"><span class="subnav-text">保证金方案</span></a></li>
	              	</shiro:hasRole>
	              	
	              	<shiro:hasRole name="fk">
	              	<li class="subnav-li" data-id="10" id="index_10"><a id="left_a_10" href="javascript:;" onclick="riskTemplateList()" class="ue-clear"><span class="subnav-text">风控方案</span></a></li>
	              	</shiro:hasRole>
              	</ul>
            </li>
            
            <li class="nav-li last-nav-li" id="nav_5">
            	<shiro:hasRole name="2">
                	<a href="javascript:;" class="ue-clear"><i class="nav-ivon"></i><span class="nav-text"><i class="Ficon"><img src="${path}/image/er-ji-dao-hang-1.png"/></i>资金管理</span></a>
                </shiro:hasRole>	
              	<ul class="subnav">
              	<shiro:hasRole name="zjls">
	              	<li class="subnav-li" data-id="4" id="index_4"><a id="left_a_4" href="javascript:;" onclick="fundList()" class="ue-clear"><span class="subnav-text">资金流水</span></a></li>
	              </shiro:hasRole>	
	              
	             <shiro:hasRole name="txsh">
	              	<li class="subnav-li" data-id="2" id="index_2"><a id="left_a_2" href="javascript:;" onclick="withdrawList()" class="ue-clear"><span class="subnav-text">提现审核</span></a></li>
	            </shiro:hasRole>
	              
	            <shiro:hasRole name="yhtz">
	              	<li class="subnav-li" data-id="22" id="index_22"><a href="javascript;" onclick="addFund();" class="ue-clear"><span class="subnav-text">用户调账</span></a></li>
                </shiro:hasRole>
	              
	              	
	               <shiro:hasRole name="sxffcbb">
	              	<li class="subnav-li" data-id="15" id="index_15"><a href="javascript;" onclick="fundSettlelogList();" class="ue-clear"><span class="subnav-text">手续费分成报表</span></a></li>
                    </shiro:hasRole>
                    
                     <shiro:hasRole name="crjsz">
	              	<li class="subnav-li" data-id="23" id="index_23"><a href="javascript;" onclick="fundSettlelogList();" class="ue-clear"><span class="subnav-text">出入金设置</span></a></li>
                    </shiro:hasRole>
              	</ul>
            </li>
            
            <li class="nav-li last-nav-li current" id="nav_3">
	           <shiro:hasRole name="4">
                	<a href="javascript:;" class="ue-clear"><i class="nav-ivon"></i><span class="nav-text"><i class="Ficon"><img src="${path}/image/er-ji-dao-hang-1.png"/></i>交易管理</span></a>
                </shiro:hasRole>	
	            <ul class="subnav">
	            <shiro:hasRole name="ssjysj">
		             <li class="subnav-li" data-id="5" id="index_5"><a id="left_a_5" href="javascript:;" onclick="drTradeList()" class="ue-clear"><span class="subnav-text">实时数据</span></a></li>
		          </shiro:hasRole>  
		          
		          <shiro:hasRole name="lsjyjl">
		             <li class="subnav-li" data-id="35" id="index_35"><a id="left_a_35" href="javascript:;" onclick="lsTradeList()" class="ue-clear"><span class="subnav-text">历史记录</span></a></li>
		          </shiro:hasRole>
		            
		           <shiro:hasRole name="ccdb"> 
		             <li class="subnav-li" data-id="6" id="index_6"><a id="left_a_6" href="javascript:;" onclick="ccdbList()" class="ue-clear"><span class="subnav-text">持仓对比</span></a></li>
		           </shiro:hasRole>  
		           
		           <shiro:hasRole name="exdd"> 
		             <li class="subnav-li" data-id="7" id="index_7"><a id="left_a_7" href="javascript:;" onclick="exceptionOrderList()" class="ue-clear"><span class="subnav-text">异常订单</span></a></li>
              		</shiro:hasRole>  
              		
              		<!-- <li class="subnav-li" data-id="20" id="index_20"><a id="left_a_20" href="javascript:;" onclick="riskList()" class="ue-clear"><span class="subnav-text">报单账户风控</span></a></li> -->
              		
              		<!-- <shiro:hasRole name="zspcyj"> 
              		<li class="subnav-li" data-id="17" id="index_17"><a id="left_a_17" href="javascript:;" onclick="closeOutList()" class="ue-clear"><span class="subnav-text">止损平仓预警</span></a></li>
              		</shiro:hasRole>  
              		
              		<shiro:hasRole name="opsch"> 
              		<li class="subnav-li" data-id="11" id="index_11"><a id="left_a_11" href="javascript:;" onclick="schemeList()" class="ue-clear"><span class="subnav-text">操盘方案</span></a></li>
              		</shiro:hasRole>   -->
              		
              		<shiro:hasRole name="hygl"> 
              		<li class="subnav-li" data-id="18" id="index_18"><a id="left_a_18" href="javascript:;" onclick="contractList()" class="ue-clear"><span class="subnav-text">合约管理</span></a></li>
              		</shiro:hasRole>  
              	</ul>
            </li>
            
          <%--   <li class="nav-li last-nav-li" id="nav_4">
               <shiro:hasRole name="5">
                	<a href="javascript:;" class="ue-clear"><i class="nav-ivon"></i><span class="nav-text"><i class="Ficon"><img src="${path}/image/er-ji-dao-hang-1.png"/></i>系统设置</span></a>
                </shiro:hasRole>
                <ul class="subnav">
		        <c:if test="${sessionScope.SESSION_LOGINNAME == '1272'}">

		       	<li id="left3"><a id="left_a_14" href="javascript:;" onclick="wardenList('3')" class="ue-clear"><span class="subnav-text">系统账户</span></a></li>
          		</c:if>
				<shiro:hasRole name="app_help"> 
			          	<li class="subnav-li" data-id="15" id="index_15"><a id="left_a_15" href="javascript:;" onclick="toApp()" class="ue-clear"><span class="subnav-text">APP帮助</span></a></li>
			    </shiro:hasRole>  
            
            	<li class="subnav-li" data-id="19" id="index_19"><a id="left_a_19" href="javascript:;" onclick="tradeHedgingList()" class="ue-clear"><span class="subnav-text">样本账户</span></a></li>
            </ul>
            </li> --%>
         </ul>
	</div>
</body>
<script type="text/javascript" src="${path}/js/nav.js"></script>
<script type="text/javascript" src="${path}/js/Menu.js"></script>
<script type="text/javascript">
	var menu = new Menu({
		defaultSelect: $('.nav').find('li[data-id="1"]')
	});
	
	$('.sidebar h2').click(function(e) {
        $('.tree-list').toggleClass('outwindow');
		$('.nav').toggleClass('outwindow');
    });
	
</script>
</html>