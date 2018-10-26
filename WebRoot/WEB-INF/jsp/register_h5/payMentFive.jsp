<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${path}/js/h5/js/jquery-1.11.0.min.js"></script>
<link type="text/css" href="${path}/css/h5/css/css.css" rel="stylesheet"/>
<link type="text/css" href="${path}/css/h5/css/animation.css" rel="stylesheet"/>
<link type="text/css" href="${path}/css/h5/css/DateTimePicker.css" rel="stylesheet"/>
<title>订单支付</title>
</head>
<script type="text/javascript">

</script>
<body class="gray">
    <form id="form" action="${url}" method="post">
		<input type="hidden" name="pay_memberid"  value="${map.pay_memberid}">
		<input type="hidden" name="pay_orderid"  value="${map.pay_orderid}">
		<input type="hidden" name="pay_applydate"  value="${map.pay_applydate}">
		<input type="hidden" name="pay_bankcode"  value="${map.pay_bankcode}">
		<input type="hidden" name="pay_notifyurl"  value="${map.pay_notifyurl}">
		<input type="hidden" name="pay_callbackurl"  value="${map.pay_callbackurl}">
		<input type="hidden" name="pay_amount"  value="${map.pay_amount}">
		<input type="hidden" name="pay_productname"  value="${map.pay_productname}">
		<input type="hidden" name="pay_md5sign"  value="${map.pay_md5sign}">
		<input type="hidden" name="card_no"  value="${map.card_no}">
	</form>

<script type="text/javascript">
    $(document).ready(function(){
        $("#form").submit();
    });
</script>
</body>
</html>