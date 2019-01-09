/**
 * Copyright (C) 2009 武汉金策略信息科技有限公司
 *
 * 版权所有。
 *
 * 类名　　  : @FundService.java
 * 功能概要  : 
 * 做成日期  : @2018年6月29日
 * 修改日期  :
 */
package com.jcl.service;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jcl.pojo.AgentzhfundLog;
import com.jcl.pojo.Subzh;

/** 
 * @author zpf
 * @version 1.0
 */
public interface FundServices {

	 Map<String, Object>  payNotyfy(HttpServletRequest request);

	 Map<String, Object> savePay(AgentzhfundLog afl, String subzh);

	Map<String, Object> isPay(Integer id, Integer status);

	Map<String, Object> checkWithdraw(Integer id, Integer status);

	BigDecimal getfundbalance(Subzh subzh);

	Map<String, Object> payment(HttpServletRequest request,HttpServletResponse response);

	Map<String, Object> saveGetMoney(HttpServletRequest request);


	void paymentH5(HttpServletRequest request, HttpServletResponse response);

	Map<String, Object> payNotyfyh5(HttpServletRequest request);

}