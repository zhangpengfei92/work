<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>资金流水</title>
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
p select.selectyz{
			    width: 160px;
			    line-height: 30px;
			    height: 30px;
			    padding: 0 5px;
			    background: none;
			    border: 1px solid #efe7e7;
		}
</style>
<script type="text/javascript">
	$(function(){
		$("#left_a_2").addClass('on');
		$("#nav_2").addClass('.current');
	});
	
	/* 全选和反选 */
	var Acheckbox = false;
	function checkAll() {
		if (Acheckbox) {
			//选择每个被选中的input
			$(":checkbox").each(function() {
				this.checked = false;
			});
			Acheckbox = false;
		} else {
			$(":checkbox").each(function() {
				this.checked = true;
			});
			Acheckbox = true;
		}
	}

	//通过
	function reinstate(id) {
		$.messager.confirm('提示', '确定资金流水通过?', function(yn) {
			if (yn) {
				$.ajax({
					url : "${path}/fundPzlog/reinstate",
					type : "post",
					data : {
						"id" : id,
						"status" : 5
					},
					traditional : true,
					dataType : "text",
					success : function(data) {
						if (data == "true") {
							$.messager.alert('提示!', '审核通过!', 'info',
							function() {
								parent.RefreshPage();
							});
						}
						if (data == "false") {
							$.messager.alert('提示!', '提交失败!', 'error',
							function() {
								parent.RefreshPage();
							});
						}
					}
				});
			}
		});
	}
	
	//拒绝
	function refuseCheck(id) {
		$.messager.confirm('提示', '确定资金流水拒绝?', function(yn) {
			if (yn) {
				$.ajax({
					url : "${path}/fundPzlog/reinstate",
					type : "post",
					data : {
						"id" : id,
						"status" : 6
					},
					traditional : true,
					dataType : "text",
					success : function(data) {
						if (data == "true") {
							$.messager.alert('提示!', '审核拒绝!', 'info',
							function() {
								parent.RefreshPage();
							});
						}
						if (data == "false") {
							$.messager.alert('提示!', '提交失败!', 'error',
							function() {
								parent.RefreshPage();
							});
						}
					}
				});
			}
		});
	}
	
	//查询
	function serach(pageNum) {
		if (pageNum == 0) {
			pageNum = 1;
		}
		var form = document.getElementById("form1");
		form.action = "${path}/fundPzlog/selectPage?pageNum=" + pageNum;
		form.submit();
	}
	//翻页
	function tzfy() {

		var pageNum = $.trim($("#pageNum").val());
		if (!pageNum) {
			$.messager.alert('提示', '请输入页码!', 'info');
			return false;
		}
		var pages = $.trim($("#pages").val());
		if (pageNum*1 > pages*1) {
			$.messager.alert('提示', '输入的页数无效!', 'info'); 
			return false;
		}
		serach(pageNum);
	}

	/* 编辑页面跳转 */
	function findsubzh(id) {
		if(id == "" || id == null){
			window.location.href = "${path}/fundPzlog/addsubzh";
		} else {
			window.location.href = "${path}/fundPzlog/editsubzh?id=" + id;
		}
	}

	//刷新页面
	function RefreshPage() {
		window.location.href = "${path}/fundPzlog/subzhList";
	}
	
	function setNext(){//重置
		$("#nickname").val("");
		$("#fundType").val("");
	}
	
</script>
<body>
	<%@include file="../../../head.jsp"%>
	<div class="wrap">
		<%@include file="subzhMaster.jsp"%>
		<div class="Rbox">
            <div class="mainbox">
                 <h3><a href="${path}/fundPzlog/fundPzLogList" class="on">资金流水</a></h3>
                 <div class="main">
                      <div class="Inputbox">
		                   <form id="form1" method="post" style="float:left;">
	                           <p><label>账号代码：</label><input type="text" id="nickname" name="nickname" value="${nickname}" placeholder="请输入"/>
	                           <label >出入金类型：</label>
	                           		<select name="fundType" id="fundType"  class="selectyz">
			                         	<option value="" ${fundType==""?selected:''}>==请选择==</option>
			                         	<option value="6"  ${fundType=='6'?'selected':''} >充值</option>
			                         	<option value="7" ${fundType=="7"?'selected':''} >提现</option>
			                         	<option value="8" ${fundType=="8"?'selected':''} >调账入金</option>
			                         	<option value="9" ${fundType=="9"?'selected':''} >调账出金</option>
			                        </select>
	                              <button class="cx" onclick="serach('1')">查询</button>
	                              <button class="cz" onclick="setNext()">重置</button>
	                           </p>
                           </form>
                           <p class="rbutbox">
                           </p>
                      </div>
                    <div class="mtable">
                           <table width="100%;">
	                           <tr class="tit">
								<td width="5%">序号</td>
								<td>更新时间</td>
								<td>操作类型</td>
								<td>账户代码</td>
								<td>收入（元）</td>
								<td>支出（元）</td>
								<td>币种账户</td>
							</tr>
	                      	<c:forEach items="${userlist}" var="user" varStatus="st">
								<tr>
									<td>${st.count}</td>
									<td><fmt:formatDate value="${user.createtime}" type="both"/></td>
									<td>
										<c:choose>
											<c:when test="${user.fundtype==1}">充值</c:when>
											<c:when test="${user.fundtype==2}">充值</c:when>
											<c:when test="${user.fundtype==3}">减配</c:when>
											<c:when test="${user.fundtype==4}">充值</c:when>
											<c:when test="${user.fundtype==5}">清算</c:when>
											<c:when test="${user.fundtype==6}">充值</c:when>
											<c:when test="${user.fundtype==7}">提现</c:when>
											<c:when test="${user.fundtype==8}">调账入金</c:when>
											<c:when test="${user.fundtype==9}">调账出金</c:when>
										</c:choose>
									</td>
									<td>${user.subzh}</td>
									<td>
										<c:choose>
											<c:when test="${user.fundtype==1}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
											<c:when test="${user.fundtype==2}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
											<c:when test="${user.fundtype==4}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
											<c:when test="${user.fundtype==5}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
											<c:when test="${user.fundtype==6}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
											<c:when test="${user.fundtype==8}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
											<c:otherwise>0.00</c:otherwise>
										</c:choose>
									</td>
									<td>
										<c:choose>
											<c:when test="${user.fundtype==3}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>

											<c:when test="${user.fundtype==7}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
											<c:when test="${user.fundtype==9}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
											<c:otherwise>0.00</c:otherwise>
										</c:choose>
									</td>
									<td>人民币账户</td>
								</tr>
							</c:forEach>
						</table>
		 				</div>
		 				<div class="page">
							<c:choose>
									<c:when test="${userpage.pages==0}">0/${userpage.pages}</c:when>
									<c:otherwise>${userpage.pageNum}/${userpage.pages}</c:otherwise>
								</c:choose> 共<i>${userpage.pages}</i>页
							<a class="bs" href="javascript:;" onclick="serach('1')">首页</a>
							<input type="hidden" value="${userpage.pages}" id="pages" />
							<c:if test="${userpage.pageNum!=1}">
								<a class="bs" href="javascript:;" onclick="serach('${userpage.prePage}')">上一页</a>
							</c:if>
							<c:if test="${userpage.pageNum!=userpage.pages}">
								<a class="bs" href="javascript:;" onclick="serach('${userpage.nextPage}')">下一页</a>
							</c:if>
							<a class="bs" href="javascript:;" onclick="serach('${userpage.pages}')">尾页</a>
							<a ><input type="text" id="pageNum" value="${userpage.pageNum}" style="height: 30px;line-height: 30px;padding: 0 5px;"/></a>
							<a style="cursor: pointer;" type="button" onclick="tzfy()">跳转</a>
						</div>
                    </div>
               </div>
          </div>
     </div>
</body>
</html>