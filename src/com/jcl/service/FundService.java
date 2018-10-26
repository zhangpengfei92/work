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
import com.jcl.pojo.AgentzhfundLog;
import com.jcl.pojo.Subzh;

/** 
 * @author zpf
 * @version 1.0
 * --国际
 */
public interface FundService {

	Map<String, Object> savePay(String bankco, Double moneys, String cardno, String subzh);

	 Map<String, Object>  payNotyfy(HttpServletRequest request);

	 Map<String, Object> payCallBack(HttpServletRequest request);

	 Map<String, Object> savePay(AgentzhfundLog afl, String subzh);

	Map<String, Object> isPay(Integer id, Integer status);

	Map<String, Object> checkWithdraw(Integer id, Integer status);

	Map<String, Object> saveAccount(String username, String accmoney, Integer mstatus);

	BigDecimal getfundbalance(Subzh subzh);

	Map<String, Object> saveGetMoney(String orderno, String subzh);
	
	boolean isPosition(Subzh subzh);

}
