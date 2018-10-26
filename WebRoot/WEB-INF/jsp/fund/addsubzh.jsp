<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-Ua-Compatible" content="IE=EmulateIE8" />
<title>账户列表-新建账户</title>
	<link rel="stylesheet" type="text/css" href="${path}/css/style.css" />
	<link  href="${path}/css/table/css.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${path}/js/jquery-1.9.1.min.js"></script>
	<!-- 引入默认css -->
	<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/default/easyui.css" />
	<link rel="stylesheet" type="text/css" href="${path}/js/easyui/themes/icon.css" />
	<script type="text/javascript" src="${path}/js/easyui/jquery.min.js"></script>
	<script type="text/javascript" src="${path}/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="${path}/js/easyui/locale/easyui-lang-zh_CN.js"></script>
	<style type="text/css">
		input.dis{
			background:#b3afaf;
		}
		
			.tabdiv{
				width:100%;
				margin: 20px 0px 0px 100px;
				overflow:hidden;
				
			}
		
	</style>
<script type="text/javascript">
	
	//当前页面加载的时候去查渠道绑定的方案
	$(function() {
		var isadmin="${sub.isadmin}";//当前页面编辑的角色
		
		if(isadmin==3){
			name="渠道";
			var codeids="";
			$("input[name='codeid']").each(function(i,data1) {
				$("input[name='code']").each(function(j,data2) {
					if(data1.value==data2.value){
						data1.checked="checked";
					}
				});
				
			});
			
	 		$("input[name='bzjid']").each(function(i,data1) {
	 			$("input[name='bzj']").each(function(j,data2) {
	 				if(data1.value==data2.value){
						data1.checked="checked";
					}
				});
			});
	 		
			$("input[name='raskid']").each(function(i,data1) {
				$("input[name='rask']").each(function(j,data2) {
					if(data1.value==data2.value){
						data1.checked="checked";
					}
				}); 
			}); 
			
		}
		var f = ${flag};
		if(isadmin==6){
			if(f==1){
				//当是修改时,页面加载时需根据加载到的渠道去查询渠道关联方案
				var subzhs=$("#allocchannel").val();
				var feeset=$("#hfeeset").val();//收费方案
				var marginset=$("#hmarginset").val();//保证金方案
				var riskset=$("#hriskset").val();//风控方案
				alert(feeset  +" //" +marginset+"//"+marginset);
				$.post("${path}/fund/ajaxgetschemes"
				,{
					subzhs:subzhs
				},function(datas){
					console.log(datas);
					var f = ${flag};
					if(datas!=null&&datas!=""){
						var cfmlist=datas.cfmlist;
						var fs=document.getElementById("feeset");
						fs.options.length=0;
					/* 	fs.options.add(new Option("=请选择=","")); */

						for (var i = 0; i < cfmlist.length; i++) {
							fs.options.add(new Option(cfmlist[i].title,cfmlist[i].id));
							if(f=="1"){
								if(cfmlist[i].id == feeset){ 		
									fs.options[i].selected=true;
								}
							}
						} 
						var bzlist=datas.bzlist;
						var bz=document.getElementById("marginset");
						bz.options.length=0;
						/* bz.options.add(new Option("=请选择=","")); */

						for (var i = 0; i < bzlist.length; i++) {
							bz.options.add(new Option(bzlist[i].title,bzlist[i].id));
							
							if(f=="1"){
								if(bzlist[i].id == marginset){
									bz.options[i].selected=true;
								}
							}
						}
						
						var rtlist=datas.rtlist;
						var rt=document.getElementById("riskset");
						rt.options.length=0;
						/* rt.options.add(new Option("=请选择=","")); */

						for (var i = 0; i < rtlist.length; i++) {
							rt.options.add(new Option(rtlist[i].type,rtlist[i].id));
							if(f=="1"){
								if(rtlist[i].id == riskset){
									rt.options[i].selected=true;
								}
							}
						}
					}

				});
			}
		}
	})
	
    function exe(obj){
		var f = ${flag};
		
		if(f==1){
	       var vals=obj.checked;
	       var subzh=$("#hsubzh").val();
	       
	        if(vals==false){
	        	var sid =obj.value;
	        	var tid="";
	        	if(obj.name=="codeid"){
	        		tid="1";//表示手续费方案
	        	}
	        	if(obj.name=="bzjid"){
	        		tid="2";//表示保证金方案
	        	}
	        	if(obj.name=="raskid"){
	        		tid="3";//表示风控方案
	        	}
	        	
	        	$.post("${path}/fund/ischeck", {
	        		subzh:subzh,	
	        		sid:sid,
	        		tid:tid
	        	},function(data){
					if(data=="NO"){
						alert("该方案有用户绑定，无法取消");
						obj.checked="checked";
					}
				
	           });
			}
	    }
	}
	//中文验证
	function funcChina(obj) {
        if (/^[A-Za-z0-9_]*$/g.test(obj)) {
            return true;
        }
        return false;
    }
	
	function checkCount(val,min,max,name){
		var b=true;
		if (val == null || val == "") {
			$.messager.alert('提示', name+"编号不能为空!", 'info');
			b = false;
		}
		if(val.length < min || val.length > max || !funcChina(val)){
			//alert("账户名称请输入8至20个字符（字母、数字、下划线）");
			$.messager.alert('提示', name+"编号长度必须在"+min+"和"+max+"之间", 'info');
			regFlag = false;
			b = false;
		}
		return b;
	}
	
	//保存
	 function saveUser() {
		var regFlag = true;
		var isadmin="${sub.isadmin}";//当前页面编辑的角色
		var isadmin2="${sessionScope.SESSION_ISADMIN}";//当前登录者角色
		var flag="${flag}";
		var name="交易用户";
		var schemes="";//渠道分配的方案串
		if(isadmin==3){
			name="渠道";
			var codeids="";
			$("input[name='codeid']:checked").each(function(index,data) {
				codeids +=data.value+",";
			});
			if(codeids==""){
				alert("请至少选择一种手续费方案");
				return false;
			}
			var bzjids="";
	 		$("input[name='bzjid']:checked").each(function(index,data) {
	 			bzjids +=data.value+",";
			});
	 		if(bzjids==""){
				alert("请至少选择一种保证金方案");
				return false;
			}	 		
	 		var raskids="";
			$("input[name='raskid']:checked").each(function(index,data) {
				raskids +=data.value+",";
			}); 
			
			if(raskids==""){
				alert("请至少选择一种风控方案");
				return false;
			}
			
			schemes=codeids+"-"+bzjids+"-"+raskids;
		}
		if(isadmin==4){
			name="代理商";
		}
		if(isadmin==5){
			name="经纪人";
		}
		<c:if test="${sessionScope.SESSION_ISADMIN lt 3 and (sub.isadmin eq 6 ? (isupdate eq 0) : (flag eq 0))}">
			if(isadmin == 6){
				var options = $("#allocchannel option:selected");//获取选中的option
				var allocchannel = options.val();
				if(allocchannel.length == 0){
					$.messager.alert('提示', "请选择归属渠道", 'info');
					return false;
				}
			}
		</c:if>
		var subzh = "";
		var password="";
		var phone = $.trim($("#phone").val());//电话marginset
		if(flag==="0"){
			subzh=$.trim($("#subzh").val());
			if(!checkUserno(subzh,isadmin)){
				return false;
			}
		}
		
		$("#submitId").attr("disabled",true);
		var condition=new Object();
		var subzhContract = {};
		var subzhContracts = new Array();
		<c:if test="${sub.isadmin != 6}">
	        <c:if test="${!empty subzhContractList}">
	        	<c:forEach items="${subzhContractList }" var="subzhContract" varStatus="idxStatus">
	        		var subzhContract_${idxStatus.index } = {};
	        		subzhContract_${idxStatus.index }.contractCode = '${subzhContract.contractCode}';
	        		subzhContract_${idxStatus.index }.contractName = '${subzhContract.contractName}';
	        		subzhContract_${idxStatus.index }.cost = $("#cost_${idxStatus.index }").val();
	        		subzhContracts[${idxStatus.index }] = subzhContract_${idxStatus.index };
	        	</c:forEach>
	        </c:if>
	    </c:if>
	    condition.subzhContractList = subzhContracts;
		condition.phone=phone;
		condition.isadmin=isadmin;
		condition.allocpt=$("#allocpt").val();
		condition.manage=$("#manage").val();
		if(isadmin==3 || isadmin==4 || isadmin==5){
			var subname = $.trim($("#name").val());
			subzh = $.trim($("#subzh").val());
			if(!checkName(name,subzh,isadmin)){
				$("#submitId").attr("disabled",false);
				return false;
			}
			var coefficient=$("#coefficient").val();
			/* if(!checkNum(coefficient)){ //分成系数取消
				$("#submitId").attr("disabled",false);
				regFlag = false;
	            return false;
			} */
			if(!isPhoneNum(phone)){
	            regFlag = false;
	            $("#submitId").attr("disabled",false);
	            return false;
			}
			if(flag==="0"){
				if(subzh.length!=3){
					$.messager.alert('提示', name+"编号长度必须是一个三位的数字", 'info');
					$("#submitId").attr("disabled",false);
					return false;
				}
				/* if(isadmin>3 && subzh.length!=6){
					$.messager.alert('提示', name+"编号只能是一个六位的数字", 'info');
					$("#submitId").attr("disabled",false);
					return false;
				} */
				password="123456";//设置渠道，代理商，经纪人的默认密码为123456
			}else{
				password=$("#hpassword").val();
			}
			
			condition.name=subname;
			condition.coefficient=coefficient;
			condition.password=password;
			if(isadmin==3){
				if(flag==="0"){
					condition.subzh="QD"+subzh;
				}else{
					condition.cookie=$("#cookie").val();
					condition.subzh=subzh;
					
				}
				condition.schemes=schemes;
			}else if(isadmin==4){//编辑代理商
				var allocchannel=$("#allocchannel").val();
				if(flag==="0"){
					var dls2=$("#presubzh").val();
					condition.subzh=dls2+subzh;
				}else{
					condition.cookie=$("#cookie").val();
					condition.subzh=subzh;
				}
				if(!allocchannel){
					$.messager.alert('提示', "所属渠道不能为空!", 'info');
						$("#submitId").attr("disabled",false);
						return false;
					
				}
				condition.allocchannel=allocchannel;
			}else{//编辑经纪人
				var allocchannel=$("#allocchannel").val();
				var allocagent=$("#allocagent").val();
				if(flag==="0"){
					var jjr2=$("#presubzh").val();
					condition.subzh=jjr2+subzh;
				}else{
					condition.cookie=$("#cookie").val();
					condition.subzh=subzh;
				}
				if(!allocchannel || !allocagent || allocagent.length==0){
					$.messager.alert('提示', "所属渠道或所属代理商不能为空!", 'info');
						$("#submitId").attr("disabled",false);
						return false;
					
				}
				condition.allocchannel=allocchannel;
				condition.allocagent=allocagent;
			}
		}else{//这里是编辑交易账号
			if(!isPhoneNum(phone)){
	            regFlag = false;
	            $("#submitId").attr("disabled",false);
	            return false;
			}
			//校验密码
			if(flag==="0"){
					password = $.trim($("#password").val());
					if(!password){
						$.messager.alert('提示', "账户密码不能为空!", 'info');
						$("#submitId").attr("disabled",false);
						return false;
					}
					if(password.length < 4 || password.length > 20) {
			            $.messager.alert('提示', "账户密码请输入4至20位字符", 'info');
			            $("#submitId").attr("disabled",false);
			            regFlag = false;
			            return false;
			        }
			}else{
				password = $("#hpassword").val();
			}
			
	        var allocchannel=$("#allocchannel").val();
	        var allocbroker=$("#allocbroker").val();
	        var allocagent=$("#allocagent").val();
	        if(!allocchannel || allocchannel==''){
	        	if(allocagent || $.trim(allocagent).length>0){
	        		 $.messager.alert('提示', "必须先有渠道才能有代理商", 'info');
			            $("#submitId").attr("disabled",false);
			            regFlag = false;
			            return false;
	        	}
	        	if(allocbroker || $.trim(allocbroker).length>0){
	        		 $.messager.alert('提示', "必须先有渠道和代理商才能有经纪人", 'info');
			            $("#submitId").attr("disabled",false);
			            regFlag = false;
			            return false;
	        	}
	        }
	        if(!allocagent || allocagent==''){
	        	if(allocbroker || $.trim(allocbroker).length>0){
	        		 $.messager.alert('提示', "必须先有渠道和代理商才能有经纪人", 'info');
			            $("#submitId").attr("disabled",false);
			            regFlag = false;
			            return false;
	        	}
	        }
	        if(flag==="1"){
	        	condition.cookie=$("#cookie").val();
	        }
	        var feeset=$("#feeset").val();//收费方案
	        //var bdtd=$("#bdtd").val();//报单通道
	        var marginset=$("#marginset").val();//保证金方案
	        var riskset=$("#riskset").val();//风控方案
	        //if(!feeset || !bdtd || !marginset ||!riskset){
	        if(!feeset || !marginset ||!riskset){
	        	//$.messager.alert('提示', "关联报单不能为空或方案不能为空", 'info');
	        	$.messager.alert('提示', "方案不能为空", 'info');
	            $("#submitId").attr("disabled",false);
	            regFlag = false;
	            return false;
	        }
	        if(!phone || $.trim(phone).length==0){
	        	$.messager.alert('提示', "手机号必须填写", 'info');
	            $("#submitId").attr("disabled",false);
	            regFlag = false;
	            return false;
	        }
	        subzh=phone;
	        condition.subzh=subzh;
	        condition.password=password;
	        condition.feeset=feeset;
	        //condition.bdtd=bdtd;
	        condition.marginset=marginset;
	        condition.riskset=riskset;
	        condition.allocchannel=allocchannel;
	        condition.allocbroker=allocbroker;
	        condition.allocagent=allocagent;
	       
	        //获取复选框中的交易品种信息
	       /*  var jypz ="";
			var sjypz =$("input[name='jypz']:checked");
			
			for(var i=0;i<sjypz.length;i++){
				 jypz=sjypz[i].value+","+jypz;
			} */
			/* var initmoney = $.trim($("#initmoney").val());
			if(isNaN(initmoney)){
				$.messager.alert('提示', "初始资金必须是数字", 'info');
	            $("#submitId").attr("disabled",false);
	            regFlag = false;
	            return false;
			} */
	        //condition.initmoney=initmoney;
	       // condition.jypz=jypz;
	        
		}
		////JSON.stringify()，将postdata对象转换成字符串形式
		var str = JSON.stringify(condition);
		//保证这些参数
		if(regFlag){
			$.ajax({	
				url : "${path}/fund/editsubzh",
				/* data :condition, */
				data : {
					"jsonData" : str
				},
				type : "POST",
				success : function(num) {
						if(num == 'ok'){
							refreshPage(isadmin);
						}else{
							$.messager.alert('提示', num, 'info');
						}
						$("#submitId").attr("disabled",false);
				}
			});
		}
	} 

	function refreshPage(isadmin) {//取消
		<c:if test="${flag eq 0 }">window.location.href = "${path}/fund/subzhList?isadmin="+isadmin;</c:if>
    	<c:if test="${flag eq 1 }">location.replace(document.referrer);</c:if>		
	} 
	
	
	function checkName(name,id,isadmin){
		var regFlag=true;
			var subname = $.trim($("#name").val());
			if (subname == null || subname == "") {
				$.messager.alert('提示', name+"名称不能为空!", 'info');
				regFlag= false;
			}
			if(subname.length > 30){
				$.messager.alert('提示', name+"名称长度不能超过30个", 'info');
				regFlag = false;
			}
			if(regFlag){
				$.ajax({
					url : "${path}/fund/checkNameExist",
					async: false,//同步
					type : "post",
					data : {"subzh":id,"name":subname,"isadmin":isadmin},
					traditional : true,
					dataType : "text",
					success : function(data) {
						if (data == "true") {
							$.messager.alert('提示!', '此'+name+'名称已存在!', 'info');
							regFlag=false;
						}
					}
				});
			}
			
			return regFlag;
	}
	
	//增加账户体验金
	function addUserFund(id) {
		$.messager.confirm('提示', '确定增加体验金?', function(yn) {
			if (yn) {
				$.ajax({
					url : "${path}/fundlog/addUserFund?subzh="+id,
					type : "post",
					data : {},
					traditional : true,
					dataType : "text",
					success : function(data) {
						if (data == "true") {
							$.messager.alert('提示!', '提交成功!', 'info');
						} else if (data == "false") {
							$.messager.alert('提示!', '提交失败!', 'error');
						} else {
							$.messager.alert('提示!', data, 'info');
						}
					}
				});
			}
		});
	}
	
	function check(url,str){
		var b=true;
		$.ajax({
					url : url,
					async: false,//同步
					type : "post",
					data : {},
					traditional : true,
					dataType : "text",
					success : function(data) {
						if (data == "true") {
							$.messager.alert('提示!', '此'+str+'已存在!', 'info');
							b=false;
						}
					}
				});
		return b;
	}
	
	//校验手机号是否合法
	function isPhoneNum(tell){
		var flag="${flag}";
		if(tell && tell.length>0){
			var phonenum = tell;
		    var myreg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(17[0-9]{1}))+\d{8})$/; 
		    if(!myreg.test(phonenum)){ 
		        $.messager.alert('提示', "手机号格式不正确", 'info');
		        return false; 
		    }else{
		    	var b=true;
		    	var url="${path}/fund/checkPhoneExist?phone="+tell;
		    	if(flag==1){
		    		url=url+"&subzh=${sub.subzh}";
		    	}
		    	b=check(url,"手机号");
		        return b;
		    }
		}else{
			$.messager.alert('提示', "手机号必须填写", 'info');
			return false;
		}
	    
	}
	
	/* function checkNum(num){
		if(!num || $.trim(num).length==0){ //分成系数取消
			$.messager.alert('提示', "分成系数必须填写", 'info');
			return false;
		}
		if(isNaN(num)){
			$.messager.alert('提示', "分成系数必须是小于或等于100的数字", 'info');
			return false;
		}
		if(num>100 || num<0){
			$.messager.alert('提示', "分成系数必须是小于或等于100的数字", 'info');
			return false;
		}
		return true;
		
	} */
	
	function checkD(initmoney){
		if(isNaN(initmoney)){
				$.messager.alert('提示', "初始资金必须是数字", 'info');
	            return false;
			}
	}
	
	//检渠道，代理商，经纪人的编号 
	function checkUserno(subzh,isadmin){
		if(isadmin<6){
			if(!subzh || $.trim(subzh).length<1){
				$.messager.alert('提示', "此编号不能为空", 'info');
				return false;
			}
		}
		
		if(isadmin==3){
			if($.trim(subzh).length!=3){
				$.messager.alert('提示', "渠道编号必须是三位数字", 'info');
				return false;
			}else{
				if(isNaN(subzh)){
					$.messager.alert('提示', "渠道编号必须是三位数字", 'info');
					return false;
				}
				//去验证此编号是否已存在
				var url="${path}/fund/checkSubzhExist?subzh=QD"+subzh;
				b=check(url,"渠道编号");
				return b;
			}
			
		}
		if(isadmin==4){
			if($.trim(subzh).length!=3){
				$.messager.alert('提示', "代理商编号必须是三位数字", 'info');
				return false;
			}else{
				if(isNaN(subzh)){
					$.messager.alert('提示', "代理商编号必须是三位数字", 'info');
					return false;
				}
				/* var channel=$("#allocchannel").val();
				var pre1=subzh.substring(0,3);
				var pre2=channel.substring(channel.length-3,channel.length);
				if(pre1!=pre2){
					$.messager.alert('提示', "代理商编号前三位必须是渠道编号", 'info');
					return false;
				} */
				var pre=$("#presubzh").val();
				//去验证此编号是否已存在
				var url="${path}/fund/checkSubzhExist?subzh="+pre+subzh;
				b=check(url,"代理商编号");
				return b;
			}
		}
		if(isadmin==5){
			if($.trim(subzh).length!=3){
				$.messager.alert('提示', "经纪人编号必须是三位数字", 'info');
				return false;
			}else{
				if(isNaN(subzh)){
					$.messager.alert('提示', "经纪人编号必须是三位数字", 'info');
					return false;
				}
				/* var channel=$("#allocagent").val();
				var pre1=subzh.substring(0,3);
				var pre2=channel.substring(channel.length-3,channel.length);
				if(pre1!=pre2){
					$.messager.alert('提示', "经纪人编号前三位必须是代理商编号的后三位", 'info');
					return false;
				} */
				var pre=$("#presubzh").val();
				//去验证此编号是否已存在
				var url="${path}/fund/checkSubzhExist?subzh="+pre+subzh;
				b=check(url,"经纪人编号");
				return b;
			}
		}
		return true;
	}
	
	//当渠道下拉框中改变值时，改变代理商和经纪人下拉值
	function getDlsJjrpre(op,isadmin){
		if(isadmin==4){//编辑代理商时，当渠道改变时
			var pre =op.value.substring(op.value.length-3);
			//$("#subzh").val(pre);
			$("#presubzh").val("DLS"+pre);
		}else if(isadmin==5){//编辑经纪人时，当渠道改变时，先变动代理商的下拉框。
			var url="${path}/fund/getJjrFormqd";
			$.ajax({
				type:"POST",
				url:url,
				dataType:"json",
				data:{"allocchannel":op.value,"isadmin":4},
				success:function(data){
					if(data.status==1){
						$.messager.alert('提示',data.describe, 'info');
					}else{
						var rhlist=data.list;
						var obj=document.getElementById("allocagent");
						obj.options.length=0;
						//obj.options.add(new Option("=请选择=",""));
						for(var i=0;i<rhlist.length;i++){
							obj.options.add(new Option(rhlist[i].name,rhlist[i].subzh));
							if(i==0){
								obj.options[0].selected = true;
								var pre =rhlist[0].subzh.substring(rhlist[0].subzh.length-3);
								//$("#subzh").val(pre);
								$("#presubzh").val("JJR"+pre);
							}
						}
					}
				}
				});
		}else{//
			var url="${path}/fund/getJjrFormqd";
			$.ajax({
				type:"POST",
				url:url,
				dataType:"json",
				data:{"allocchannel":op.value,"isadmin":4},
				success:function(data){
					if(data.status==1){
						$.messager.alert('提示',data.describe, 'info');
					}else{
						var rhlist=data.list;
						var obj=document.getElementById("allocagent");
						obj.options.length=0;
						obj.options.add(new Option("=请选择=",""));
						if(rhlist.length==0){
								//经纪人下拉框 也变动值。
								var broker=document.getElementById("allocbroker");
								broker.options.length=0;
								broker.options.add(new Option("=请选择=",""));
						}
						for(var i=0;i<rhlist.length;i++){
							obj.options.add(new Option(rhlist[i].name,rhlist[i].subzh));
							if(i==0){
								obj.options[0].selected = true;
								getJjrpre(obj,6);
							}
						}
						
					}
				}
				});
			
		}
		
		//当渠道改变时异步加载渠道关联方案
		var subzhs=op.value;
		var feeset=$("#hfeeset").val();
		var marginset=$("#hmarginset").val();
		var riskset=$("#hriskset").val();
		$.post("${path}/fund/ajaxgetschemes"
		,{
			subzhs:subzhs
		},function(datas){
			console.log(datas);
			var f = ${flag};
			if(datas!=null&&datas!=""){
				var cfmlist=datas.cfmlist;
				var fs=document.getElementById("feeset");
				fs.options.length=0;
				fs.options.add(new Option("=请选择=",""));

				for (var i = 0; i < cfmlist.length; i++) {
					fs.options.add(new Option(cfmlist[i].title,cfmlist[i].id));
					if(f=="1"){
						if(cfmlist[i].id == riskset){ 
							fs.options[i].selected=true;
						}
					}else{
						if(i == 0){
							fs.options[0].selected=true;
						}
					}
				
				} 
				var bzlist=datas.bzlist;
				var bz=document.getElementById("marginset");
				bz.options.length=0;
				bz.options.add(new Option("=请选择=",""));

				for (var i = 0; i < bzlist.length; i++) {
					bz.options.add(new Option(bzlist[i].title,bzlist[i].id));
					
					if(f=="1"){
						if(bzlist[i].id == marginset){
							bz.options[i].selected=true;
						}
					}else{
						if(i == 0){
							bz.options[0].selected=true;
						}
					}

				}
				
				var rtlist=datas.rtlist;
				var rt=document.getElementById("riskset");
				rt.options.length=0;
				rt.options.add(new Option("=请选择=",""));

				for (var i = 0; i < rtlist.length; i++) {
					rt.options.add(new Option(rtlist[i].type,rtlist[i].id));
					if(f=="1"){
						if(rtlist[i].id == riskset){
							rt.options[i].selected=true;
						}
					}else{
						if(i == 0){
							rt.options[0].selected=true;
						}
					}
				}
			}

		});	
		
	}
	
	//当代理商改变时，经纪人需要相应变化，
	function getJjrpre(op,isadmin){
		if(isadmin==5){
			var pre =op.value.substring(op.value.length-3,op.value.length);
			//$("#subzh").val(pre);
			$("#presubzh").val("JJR"+pre);
		}else{//
			var url="${path}/fund/getJjrFormqd";
			//var old=$("#risksetValue").val();
			$.ajax({
				type:"POST",
				url:url,
				dataType:"json",
				data:{"allocagent":op.value,"isadmin":5},
				success:function(data){
					if(data.status==1){
						$.messager.alert('提示',data.describe, 'info');
					}else{
						var rhlist=data.list;
						var obj=document.getElementById("allocbroker");
						obj.options.length=0;
						obj.options.add(new Option("=请选择=",""));
						for(var i=0;i<rhlist.length;i++){
							obj.options.add(new Option(rhlist[i].name,rhlist[i].subzh));
							if(i==0){
								obj.options[0].selected = true;
							}
						}
					}
				}
			});
		}
	}
	
	function verifyCost(obj,contractCode,contractName){//验证分成参数
		var cost = obj.value;
		if(cost.length == 0){
			return false;
		}
		var re=/^\d+(\.\d+)?$/;
		if(!re.test(cost)){
			$("#"+obj.id).val("");
			$.messager.alert('提示', "请输入正确的品种名称为["+contractName+"]成本价", 'info');
			return false;
		}
		var dataObj = {};
		var isadmin = '${sub.isadmin}';//3渠道，4代理商，5经纪人，6普通用户
		var flag = '${flag}';//0是新增，1是修改
		if(flag == '0'){
			var prefix = $("#presubzh").val();//编号前缀
			var suffix = $("#subzh").val();//编号后缀
			dataObj.subzh = prefix + suffix;
		}
		if(flag == '1'){
			var suffix = $("#subzh").val();//编号后缀
			dataObj.subzh = suffix;
		}
		dataObj.isadmin = isadmin;
		dataObj.cost = cost;
		dataObj.contractCode = contractCode;
		dataObj.contractName = contractName;
		if(suffix.length == 0){
			$("#"+obj.id).val("");
			$.messager.alert('提示', "请先输入账户编号", 'info');
			return false;
		}
		dataObj.flag = flag;
		if(flag == '0'){
			if(isadmin == '4'){
				var options = $("#allocchannel option:selected");//获取选中的option
				var allocchannel = options.val();
				if(allocchannel == undefined){
					allocchannel = $("#allocchannel").val();
				}
				if(allocchannel.length == 0){
					$("#"+obj.id).val("");
					$.messager.alert('提示', "请先选择渠道", 'info');
					return false;
				}
				dataObj.allocchannel = allocchannel;
			}
			if(isadmin == '5'){//allocagent
				var options = $("#allocagent option:selected");//获取选中的option
				var allocagent = options.val();
				if(allocagent == undefined){
					allocagent = $("#allocagent").val();
				}
				if(allocagent.length == 0){
					$("#"+obj.id).val("");
					$.messager.alert('提示', "请先选择代理商", 'info');
					return false;
				}
				dataObj.allocagent = allocagent;
			}
		}
		$.ajax({
			type:"POST",
			url:"${path}/fund/verifyCost",
			//dataType:"json",
			data:dataObj,
			success:function(data){
				if(data != 'ok'){
					$("#"+obj.id).val("");
					$.messager.alert('提示',data, 'info');
					return false;
				}
			},
			error:function(XMLHttpRequest, textStatus, errorThrown){
				this;
				$.messager.alert('提示',"数据异常", 'info');
			}
		});
	}
</script>
</head>
<body>
	<%@include file="../../../head.jsp"%>
	<div class="wrap">
		<%@include file="subzhMaster.jsp"%>
		<div class="Rbox">		
     		 <div class="mainbox">
                   <h3><a href="${path}/fund/subzhList?isadmin=${sub.isadmin}">账户列表</a><i>/</i><a href="#" class="on">
                    <c:if test="${flag eq 0 }">
              			新建账户
			        </c:if>
			        <c:if test="${flag eq 1 }">
			                                   修改账户
			        </c:if>
			    </a></h3>
                   <div class="main">
        		   		<input type="hidden" value="${sub.subzh}" name="hsubzh" id="hsubzh"/>
        		   		<input type="hidden" value="${sub.password}" name="hpassword" id="hpassword"/>
        		   		<input type="hidden" value="${sub.cookie}" name="cookie" id="cookie"/>
        		   		<input type="hidden" value="${sub.feeset}" name="hfeeset" id="hfeeset"/>
        		   		<input type="hidden" value="${sub.riskset}" name="hriskset" id="hriskset"/>
        		   		<input type="hidden" value="${sub.bdtd}" name="hbdtd" id="hbdtd"/>
        		  		<input type="hidden" value="${sub.marginset}" name="hmarginset" id="hmarginset"/>
                      	<input type="hidden" value="${sub.jypz}" name="hjypz" id="hjypz"/>
                      	<input type="hidden" value="${sub.allocpt}" name="allocpt" id="allocpt"/>
                      	<input type="hidden" value="${sub.manage}" name="manage" id="manage"/>
                      	<div class="Form">
                      		<%-- <c:if test="${sub.isadmin eq 4 }">
                            <div class="dline">
                                <label>用户编号：</label>
					            <p><input type="text" value="${subzh}" readonly="readonly" name="subzh" id="subzh"/></p>
                            </div>
                            </c:if> --%>
                            <c:if test="${sub.isadmin gt 3}">
                            <div class="dline">
                                <label>归属渠道：</label>
                                <c:if test="${sessionScope.SESSION_ISADMIN lt 3 and (sub.isadmin eq 6 ? (isupdate eq 0) : (flag eq 0))}">
	                                	<div id="parentart" class="dselect">
					                        <select name="allocchannel" id="allocchannel" onchange="getDlsJjrpre(this,${sub.isadmin })">
					                        	<c:if test="${sub.isadmin eq 6}"><option value="">请选择</option> </c:if>
					                         	<c:forEach items="${channelList}" var="channel">
					                         		<option value="${channel.subzh}" <c:if test="${sub.allocchannel eq channel.subzh }">selected</c:if> >${channel.name}</option>
					                         	</c:forEach>
					                        </select>
					                    </div>
                                </c:if>
			                    <c:if test="${sessionScope.SESSION_ISADMIN ge 3 or (flag eq 1 and sub.isadmin ne 6) or (sub.isadmin eq 6 and isupdate eq 1)}">
			                    	<input type="text" name="channelname" value="${sub.channelname }" id="channelname" readonly class="dis"/>
			                    	<input type="hidden" name="allocchannel" value="${sub.allocchannel }" id="allocchannel" />
			                    </c:if>
                            </div>
                            </c:if>
                            
                            <c:if test="${sub.isadmin gt 4}">
                            <div class="dline">
                                <label>归属代理商：</label>
                                <c:if test="${sessionScope.SESSION_ISADMIN lt 4 and (sub.isadmin eq 6 ? (isupdate eq 0) : (flag eq 0))}">
	                                	<div id="parentart" class="dselect">
					                        <select name="allocagent" id="allocagent" onchange="getJjrpre(this,${sub.isadmin })">
					                         	<c:if test="${sub.isadmin eq 6}"><option value="">请选择</option> </c:if>
					                         	<c:forEach items="${agentList}" var="agent">
					                         		<option value="${agent.subzh}" <c:if test="${sub.allocagent eq agent.subzh }">selected</c:if> >${agent.name}</option>
					                         	</c:forEach>
					                        </select>
					                    </div>
                                </c:if>
			                    <c:if test="${sessionScope.SESSION_ISADMIN ge 4 or (flag eq 1 and sub.isadmin ne 6) or (sub.isadmin eq 6 and isupdate eq 1)}">
			                    	<input type="text" name="agentname" value="${sub.agentname }" id="agentname" readonly class="dis"/>
			                    	<input type="hidden" name="allocagent" value="${sub.allocagent }" id="allocagent" />
			                    </c:if>
                            </div>
                            </c:if>
                            
                           <c:if test="${sub.isadmin eq 6 }">
                            <div class="dline">
                                <label>归属经纪人：</label>
                                <c:if test="${sessionScope.SESSION_ISADMIN lt 5 and (isupdate eq 0)}">
				                    <div id="parentart" class="dselect">
				                        <select name="allocbroker" id="allocbroker">
				                         	<option value="">请选择</option>
				                         	<c:forEach items="${brokerList}" var="sn">
				                         		<option value="${sn.subzh}" <c:if test="${sub.allocbroker eq sn.subzh }">selected</c:if> >${sn.name}</option>
				                         	</c:forEach>
				                        </select>
				                    </div>
			                    </c:if>
			                    <c:if test="${sessionScope.SESSION_ISADMIN eq 5 or isupdate eq 1}">
			                    	<input type="text" name="brokername" value="${sub.brokername }" id="brokername" readonly class="dis"/>
			                    	<input type="hidden" name="allocbroker" value="${sub.allocbroker }" id="allocbroker" />
			                    </c:if>
			                    <button class="Submit cancel" onclick="addUserFund('${sub.subzh}');" >增加体验金</button>
                            </div>
                            <div class="dline">
                                 <span style="color:red;margin-left:60px;">*归属渠道必填。</span>
                            </div>
                            </c:if>
                            <c:if test="${sub.isadmin lt 6 }">
                            	<div class="dline">
	                                <label>${sub.isadmin eq 3 ? '渠道编号' : (sub.isadmin eq 4 ? '代理商编号' : '经纪人编号') }：</label>
	                                <c:if test="${flag eq 0 }">
						            <p><input type="text" value="${sub.isadmin eq 3 ? 'QD' : (sub.isadmin eq 4 ? 'DLS' : 'JJR') }${sub.subzh}" id="presubzh" readonly style="width:100px;background:#b3afaf;"/>
						            <span style="float:left;">_</span><input type="text" value="" name="subzh" id="subzh" style="width:280px;" onblur="checkUserno(this.value,'${sub.isadmin}')"/></p>
	                           		</c:if>
	                           		<c:if test="${flag eq 1 }">
	                           			<p><input type="text" value="${sub.subzh}" name="subzh" id="subzh" disabled/></p>
	                           		</c:if>
	                            </div>
	                            <div class="dline">
	                                <label>${sub.isadmin eq 3 ? '渠道名称' : (sub.isadmin eq 4 ? '代理商名称' : '经纪人名称') }：</label>
						            <p><input type="text" value="${sub.name}" name="name" id="name" <c:if test="${flag eq 1 }">disabled</c:if> onkeyup="value=value.replace(/[^\a-\z\A-\Z0-9_\u4E00-\u9FA5]/g,'')"/></p>
	                            </div>
	  
                            </c:if>
                            <div class="dline">
                                 <label>${sub.isadmin eq 6 ? '用户手机号' : '联系电话' }：</label>
                                 <input type="text" value="${sub.phone}" id="phone" name="phone" <c:if test="${sub.isadmin ne 6}">onblur="isPhoneNum(this.value)"</c:if>
                                  <c:if test="${sub.isadmin eq 6 and flag eq 1 }">readonly</c:if> />
                            </div>
                            <!-- 居间角色手续费成本 -->
                            
							<c:if test="${sub.isadmin != 6}">
							<c:if test="${sub.isadmin == 3}">
							 <div class="tabdiv">
								<div class="tab">
								   <h3>关联方案：</h3>
								   <div class="tabone">
								      <ul>
								         <li class="tit">&nbsp;&nbsp;&nbsp;手续费方案</li>
								         <c:forEach items="${clist}" var="cf" >
								         <li><span><input  type="checkbox" value="${cf.id}" onclick="exe(this)" name="codeid"/></span><em>${cf.title}</em></li>
								         </c:forEach>
								      </ul>
								   </div>
								   <div class="tabone tabtwo">
								      <ul>
								         <li class="tit">&nbsp;&nbsp;&nbsp;保证金方案</li>
								           <c:forEach items="${blist}" var="bl">
								         <li><span><input  type="checkbox" value="${bl.id}"  onclick="exe(this)" name="bzjid"/></span><em>${bl.title}</em></li>
								         </c:forEach>
								      </ul>
								   </div>
								   <div class="tabone tabtwo">
								      <ul>
								         <li class="tit">&nbsp;&nbsp;&nbsp;风控方案 </li>
								           <c:forEach items="${rlist}" var="rl" >
								         	<li><span><input  type="checkbox" value="${rl.id}" onclick="exe(this)" name="raskid"/></span><em>${rl.type}</em></li>
								         </c:forEach>
								      </ul>
								   </div>
								</div>
								</div>
								</c:if>
										<div class="dline">
											<label style="font-weight: bold;">手续费成本：</label>
										</div>
										<c:if test="${!empty subzhContractList}">
											<c:forEach items="${subzhContractList }" var="subzhContract"
												varStatus="idxStatus">
												<div class="dline">
													<label>${subzhContract.contractName }：</label>
													<input type="hidden" name="subzhContractList[${idxStatus.index }].contractCode" value="${subzhContract.contractCode }" />
													<input type="hidden" name="subzhContractList[${idxStatus.index }].contractName" value="${subzhContract.contractName }" />
													<input type="text" name="subzhContractList[${idxStatus.index }].cost" value="${subzhContract.cost }" id="cost_${idxStatus.index }" onchange="verifyCost(this,'${subzhContract.contractCode }','${subzhContract.contractName }')"/>
												</div>
											</c:forEach>
										</c:if>
									</c:if>
								

					
                     
                            <!-- 下面所属属性只有交易用户才有 -->
                            <c:if test="${flag eq 0 and sub.isadmin eq 6}">
	                            <div class="dline">
	                                 <label>密码：</label>
	                                 <input type="password" id="password" name="password" value="${sub.password}"/>
	                            </div>
                            </c:if>
                           <c:if test="${sub.isadmin eq 6 }">
                             <%-- <div class="dline">
                                 <label>关联报单账户：</label>
                                 <div id="parentart" class="dselect">
			                        <select name="bdtd" id="bdtd">
			                         	<c:forEach items="${blist}" var="sn">
			                         		<option value="${sn.bdzhid}" <c:if test="${sn.bdzhid eq sub.bdtd }">selected</c:if> >${sn.accountid}</option>
			                         	</c:forEach>
			                        </select>
			                    </div>
                            </div> --%>
                            <div class="dline">
                                 <label>手续费方案：</label>
                                  <div id="parentart" class="dselect">
			                        <select name="feeset" id="feeset">
			                        	<option>==请选择==</option>
			                         	<%-- <c:forEach items="${flist}" var="feesetEntity">
			                         		<option value="${feesetEntity.id}" <c:if test="${feesetEntity.id eq sub.feeset }">selected</c:if> >${feesetEntity.title}</option>
			                         	</c:forEach> --%>
			                        </select>
			                    </div>
                            </div>
                              <div class="dline">
                                <label>风控方案：</label>
			                    <div id="parentart" class="dselect">
			                        <select name="riskset" id="riskset">
			                        	<option>==请选择==</option>
			                         	<%-- <c:forEach items="${rlist}" var="r">
			                         		<option value="${r.id}" <c:if test="${r.id eq sub.riskset }">selected</c:if>>${r.type}</option>
			                         	</c:forEach> --%>
			                        </select>
			                    </div>
                            </div>
                            <div class="dline">
                                <label>保证金方案：</label>
			                    <div id="parentart" class="dselect">
			                        <select name=marginset id="marginset">
			                        	<option>==请选择==</option>
			                         	<%-- <c:forEach items="${bzlist}" var="sn">
			                         		<option value="${sn.id}" <c:if test="${sn.id eq sub.marginset }">selected</c:if>>${sn.title}</option>
			                         	</c:forEach> --%>
			                        </select>
			                    </div>
                            </div>
                             <!-- <div class="div-radio">
                                 <label>交易权限：</label>
                                 <span>
								        <input type="checkbox" name="jypz" value="0"/>中金所
								        <input type="checkbox" name="jypz" value="1"/>上期所
								        <input type="checkbox" name="jypz" value="2"/>郑商所
								        <input type="checkbox" name="jypz" value="3"/>大商所
								        <input type="checkbox" name="jypz" value="4"/>上期能源
								  </span>
                            </div> -->
                            <%-- <div class="dline">
                                 <label>初始资金：</label>
                                 <input type="text" value="${sub.initmoney}" id="initmoney" name="initmoney" onblur="checkD(this.value)"/>
                            </div> --%>
                            </c:if>
                            <div class="dline">
                                 <label style="color:#fff;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
                                 <button class="Submit" onclick="saveUser();" id="submitId">提交</button>
                                 
                                 <button class="Submit cancel" onclick="refreshPage('${sub.isadmin}')">取消</button>
                            </div>
                       </div>
				</div>
			</div>
		</div>
	</div>
	<div class="tabdiv" style="display: none">
     	<div class="tab">
			<h3>关联方案</h3>
				<div class="tabone">
					<ul>
						<li class="tit">手续方案</li>
							<c:forEach items="${cfmlist}" var="cf" >
							   <li><span><input  type="checkbox" value="${cf.id}" name="code"/></span><em>${cf.title}</em></li>
							</c:forEach>
					</ul>
				</div>
				<div class="tabone tabtwo">
					<ul>
						<li class="tit">保证金方案</li>
							<c:forEach items="${bzlist}" var="bl">
								<li><span><input  type="checkbox" value="${bl.id}"  name="bzj"/></span><em>${bl.title}</em></li>
							</c:forEach>
					</ul>
				</div>
					<div class="tabone tabtwo">
						<ul>
							<li class="tit">风控方案</li>
								<c:forEach items="${rtlist}" var="rl" >
								  <li><span><input  type="checkbox" value="${rl.id}" name="rask"/></span><em>${rl.type}</em></li>
								</c:forEach>
						</ul>
				 </div>
		</div>
	</div>	
</body>
</html>