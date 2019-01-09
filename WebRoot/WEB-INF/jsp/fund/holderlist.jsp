<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<!--历史持仓  -->
	
	<c:if test="${status gt 0 }">
		<c:if test="${status eq 3 }">
			<h1>查询用户信息条件中必须有交易用户</h1>
		</c:if>
	
	</c:if>		
	<c:if test="${status eq 0 }">
			<div class="mtable">
                <table width="100%;">
                          <tr class="tit">
						<td >序号</td>
						<c:if test="${not empty lsflag and lsflag eq 2}">
							<td>日期</td>
						</c:if>
						<td >用户账户</td>
						<td >用户姓名</td>
						<td >所属渠道</td>
						<td >所属代理商</td>
						<td >所属经纪人</td>
						<td >合约代码</td>
						<td >合约名称</td>
						<td >方向</td>
						<td >总持仓</td>
						<c:if test="${empty lsflag or lsflag eq 1}">
							<td >昨仓</td>
							<td >今仓</td>
							<td >可平量</td>
						</c:if>
						<td >持仓均价</td>
						<td >持仓盈亏</td>
					</tr>
                     	<c:forEach items="${page.list}" var="holder" varStatus="st">
						<tr>
							<td>${(page.pagenum-1)*page.pagesize + st.count}</td>
							<c:if test="${not empty lsflag and lsflag eq 2}">
								<td>${holder.holderTime }</td>
							</c:if>
							<td>${holder.subzh}</td>
							<td>${holder.username}</td>
							<td>${holder.channelname}</td>
							<td>${holder.agentname}</td>
							<td>${holder.brokername}</td>
							<td>${holder.stockCode}</td>
							<td>${holder.stockName}</td>
							<td>${holder.fangx}</td>
							<td>${holder.currentVol}</td>
							<c:if test="${empty lsflag or lsflag eq 1}">
								<td>${holder.zuocang}</td>
								<td>${holder.todayVol}</td>
								<td>${holder.enableVol}</td>
							</c:if>
							<td>${holder.keepCostPrice}</td>
							<td>${holder.profit}</td>
						</tr>
					</c:forEach>
					<tr id="currtable">
					</tr>
				</table>
 				</div>
 				<div class="page">
					<c:choose>
							<c:when test="${empty page.pagecount || page.pagecount==0}">0/0</c:when>
							<c:otherwise>${page.pagenum}/${page.pagecount}</c:otherwise>
						</c:choose> 共<i>${empty page.pagecount ? 0 : page.pagecount}</i>页
					<a class="bs" href="javascript:;" onclick="serach('1')">首页</a>
					<input type="hidden" value="${page.pagecount}" id="pages" />
					<c:if test="${page.pagenum!=1}">
						<a class="bs" href="javascript:;" onclick="serach('${page.pagenum-1}')">上一页</a>
					</c:if>
					<c:if test="${page.pagenum!=page.pagecount}">
						<a class="bs" href="javascript:;" onclick="serach('${page.pagenum+1}')">下一页</a>
					</c:if>
					<a class="bs" href="javascript:;" onclick="serach('${page.pagecount}')">尾页</a>
					<a ><input type="text" id="pageNum" value="${page.pagenum}" style="height: 30px;line-height: 30px;padding: 0 5px;"/></a>
					<a style="cursor: pointer;" type="button" onclick="tzfy()">跳转</a>
				</div>
				
		</c:if>
		
		<script type="text/javascript">
		$(function(){
		     var formdate=$("#frm").serializeArray();
		     var html='';
			$.post("${path}/trade/getCountLsHold",formdate,function(data){
				console.log(data);
				if(data !=null ){
					html=html+"<td>合计:</td> <td></td> <td></td> <td></td> <td></td> <td></td> <td></td> <td></td><td></td><td></td><td>"+data.countVol.toFixed(2)+"</td><td>"+data.countProfit.toFixed(2)+"</td>";
				}else{
					html=html+"<td>合计:</td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td><td></td>";	
				}
			$("#currtable").html("");
			$("#currtable").html(html);
			})
		});
		</script>