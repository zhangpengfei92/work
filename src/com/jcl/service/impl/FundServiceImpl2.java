/**
 * Copyright (C) 2009 武汉金策略信息科技有限公司
 *
 * 版权所有。
 *
 * 类名　　  : @FundServiceImpl.java
 * 功能概要  : 
 * 做成日期  : @2018年6月29日
 * 修改日期  :
 */
package com.jcl.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcl.dao.AgentzhfundLogMapper;
import com.jcl.dao.FundPzlogMapper;
import com.jcl.dao.SubzhMapper;
import com.jcl.pojo.AgentzhfundLog;
import com.jcl.pojo.FundPzlog;
import com.jcl.pojo.FundPzlogExample;
import com.jcl.pojo.FundPzlogExample.Criteria;
import com.jcl.pojo.Fundset;
import com.jcl.pojo.Subzh;
import com.jcl.service.FundService;
import com.jcl.service.FundService2;
import com.jcl.service.FundsetService;
import com.jcl.service.FundService2;
import com.jcl.stock.entity.CONST;
import com.jcl.stock.simulate.DefineNumber;
import com.jcl.stock.simulate.service.SimulateStockService;
import com.jcl.util.DateUtil;
import com.jcl.util.DeployProperties;
import com.jcl.util.StringUtil;
import com.jcl.util.pay.BankCodeEnum;
import com.jcl.util.pay.MerchantApiUtil;
import com.jcl.util.pay.PayConfigUtil;
import com.jcl.util.pay.SimpleHttpUtils;
import com.jcl.util.spt.HttpRequest;
import com.jcl.util.spt.MD5Utils;

/** 
 * @author zpf
 * @version 1.0
 */
/**
 * 出入金--裕通
 */
@Service
public class FundServiceImpl2 implements FundService2{
	//资金明细的mapper
	@Autowired
	private AgentzhfundLogMapper agentzhfundLogMapper;
	
	//用户的mapper
	@Autowired
	private SubzhMapper subzhMapper;
	
	//用户配资的mapper
	@Autowired
	private FundPzlogMapper pzlogMapper;
	
	//出入金设置
	@Autowired
	private FundsetService fundsetService;
	
	//调协议的服务
	@Autowired
	private  SimulateStockService simulateStockService;
	
	public static Logger log = Logger.getLogger(FundServiceImpl2.class);
	
/*国际期货顺付
 * 	public static final  String pay_memberid="10025";//商务号
	public static final  String keyValue="wetj942no7e5r8xh7sttibs77nhytvn9";//秘钥
	
*/
	/**
	 * 裕通期货顺付
	 */
	public static final  String pay_memberid="10027";//商务号
	public static final  String keyValue="rhxoqsjp6f0lptxa4w12ofz4n5r6tpgp";//秘钥
	private static final String project_path = DeployProperties.getInstance().getSptReturn();
	public static final  String GRTURL="http://6shun.masakn.top:1818/Payment_Dfpay_add.html";//提现地址
	public static final  String QUERYURL="http://6shun.masakn.top:1818/Payment_Dfpay_query.html";//提现查询地址
	public static final  String EXCHANGE_RATE=DeployProperties.getInstance().getPublicexchange_rate();//汇率
	
	//生成充值信息AAAA
	@Override
	public Map<String, Object> savePay(String bankco, Double moneys,String cardno,String subzh) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pay_memberid", pay_memberid);//商务号
		map.put("keyValue", keyValue);//秘钥	
		String 	pay_bankcode=null;
		if(bankco.equals("wgzf")){  
			pay_bankcode="903";   //'银行编码
			
		}else if(bankco.equals("kjzf")){
			
			pay_bankcode="903";   //'银行编码
			
		}
		map.put("card_no", cardno);	
		map.put("pay_bankcode", pay_bankcode);
		
		String dates = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String	pay_orderid= dates + StringUtil.getRandomString(3);//20位订单号 时间戳+6位随机字符串组成
		map.put("pay_orderid", pay_orderid);
			
		String	pay_applydate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//yyyy-MM-dd HH:mm:ss
		map.put("pay_applydate", pay_applydate);//交易时间
			
		String	pay_notifyurl=project_path+"/subzhfund/payNotyfy";//回调通知
		map.put("pay_notifyurl", pay_notifyurl);
			
		String	pay_callbackurl=project_path+"/subzhfund/payCallBack";//回调页面
		map.put("pay_callbackurl", pay_callbackurl);
			
		BigDecimal 	pay_amount=new BigDecimal(moneys);//订单金额  
		map.put("pay_amount", pay_amount);
			
		String	pay_productname="账户充值";
		map.put("pay_productname", pay_productname);
		
		String	pay_attach=subzh;
		map.put("pay_attach", pay_attach);
		AgentzhfundLog agentzhfundLog=new AgentzhfundLog();
		/*agentzhfundLog.setAmount(String.valueOf(moneys));*/
		//保存一条充值信息，状态为充值申请状态
		agentzhfundLog.setSubzh(subzh);
		saveAgentzhfundLog(agentzhfundLog,subzh, pay_orderid, DateUtil.getSystemDate(), pay_amount, 0, 1);
		
		String stringSignTemp="pay_amount="+pay_amount+
				"&pay_applydate="+pay_applydate+"&pay_bankcode="+pay_bankcode+
				"&pay_callbackurl="+pay_callbackurl+"&pay_memberid="+pay_memberid+
				"&pay_notifyurl="+pay_notifyurl+"&pay_orderid="+pay_orderid+"&key="+keyValue+"";
		String pay_md5sign=MD5Utils.md5(stringSignTemp);
		map.put("pay_md5sign", pay_md5sign);
		log.info("充值信息==>"+map.toString());
		return map;
	}

	//充值异步回调BBBB
	@Override
	public  Map<String, Object>  payNotyfy(HttpServletRequest request) {
		log.info("充值回调开始");
		Map<String, Object>map=new HashMap<String, Object>();
		String orderid=request.getParameter("orderid");//订单号
		String amount=request.getParameter("amount");//金额
		String datetime=request.getParameter("datetime");//交易时间
		String returncode=request.getParameter("returncode");//交易状态
		String transaction_id=request.getParameter("transaction_id");//交易流水号
		String username=request.getParameter("attach");//扩展返回
		String sign=request.getParameter("sign");//签名
		String SignTemp="amount="+amount+"&datetime="+datetime+"&memberid="+pay_memberid+"&orderid="+orderid+
				"&returncode="+returncode+"&transaction_id="+transaction_id+"&key="+keyValue;
		String md5sign=MD5Utils.md5(SignTemp);//MD5加密
		log.info("充值金额  ： "+amount);
		AgentzhfundLog afl=new AgentzhfundLog();
	    if (sign.equals(md5sign)){
	        if(returncode.equals("00")){
	        	log.info("datetime===>:"+datetime);
	    		log.info("orderid===>:"+orderid);
	    		List<AgentzhfundLog> alist= null;
	    			try {
	    				alist = agentzhfundLogMapper.selectByorderno(orderid);
	    			} catch (Exception e) {
	    				log.info("查询失败",e);
	    			}
	    			log.info("alist ===>"+alist.size());
	    		if(alist!=null&&alist.size()>0) {
	    			afl=alist.get(0);
	    			if(afl.getStatus()==1){//已经充值成功了。
	    				log.info("该单号充值已完成"+new Date().toString());
	    				map.put("msg", "该单号充值已完成");
	    				return map;
	    			}
	    		}
	    		//查询出入金设置信息
	    		Fundset fundset = fundsetService.selectone();
	    		Double czcost=1.0;
	    		if(fundset!=null&&fundset.getCzcost()>0) {
	    		czcost=(double) (1-(fundset.getCzcost()/100));
	    		}
 	    		afl.setFund(new BigDecimal(amount).multiply(new BigDecimal(czcost)));
	    		username=afl.getSubzh();
	    		log.info("当前充值用户===》：" + afl.getSubzh());

	    		Subzh sub = subzhMapper.selectBySubzh(username);
	
	    	if(sub!=null) {	    			
	    		log.info("log8:subzh"+sub);
	    		sub.setZhstate(1);
	    		int count=subzhMapper.updateByPrimaryKeySelective(sub);
	    		log.info("log11:subzhService"+count);
	    		List<FundPzlog> sfplist = getFundPzlogList(sub);
	    		//调柜台
	    	    Map<String, Object> amap=addMoney(sub, afl);
	    	    if(null!=amap&&amap.size()>0) {
	    	    	if(null!=amap.get("status")&&(Boolean) amap.get("status")) {
	    	    		afl.setStatus(1);//修改状态为1 --表示充值成功
	    	    		map.put("status", true);
	    	        	map.put("msg", "充值成功");
	    	        	FundPzlog fundPzlog = saveFundpzlog(username, afl.getFund(), orderid, DateUtil.getSystemDate(), 6);	    	
	    	    	}else {
	    	    		afl.setStatus(2);//修改状态为2 --表示充值失败	 
	    	    		map.put("status", false);
	    	        	map.put("msg", "充值失败");
	    	        	
	    	   		 	if ((sfplist == null) && (sfplist.size() <= 1)) {
	    	   		 		sub.setZhstate(0);
	    	   		 	}
	    	    	  }
	    	    	}else {
		    	    	afl.setStatus(2);//修改状态为2 --表示充值失败
		    	    	map.put("status", false);
			        	map.put("msg", "充值失败");
			        	if ((sfplist == null) && (sfplist.size() <= 1)) {
	    	   		 		sub.setZhstate(0);
	    	   		 	}
		    	    }
	    	    subzhMapper.updateByPrimaryKeySelective(sub);
	    		log.info("log11:subzhService");
	    	    }
	    		agentzhfundLogMapper.updateByPrimaryKeySelective(afl);	        	
	        }else{
	        	map.put("status", false);
	        	map.put("msg", "充值失败");
	        }
	    }else{
	    	log.info("验签失败");
        	map.put("status", false);
        	map.put("msg", "充值失败");
	    }	 
		return map;
		
	}
	
	//充值完成后跳转的页面CCCC
	@Override
	public Map<String, Object> payCallBack(HttpServletRequest request) {
		Map<String, Object>map=new HashMap<String, Object>();
		
		String orderid=request.getParameter("orderid");//订单号
		String amount=request.getParameter("amount");//金额
		String datetime=request.getParameter("datetime");//交易时间
		String returncode=request.getParameter("returncode");//交易状态
		String transaction_id=request.getParameter("transaction_id");//交易流水号
		String attach=request.getParameter("attach");//扩展返回
		String sign=request.getParameter("sign");//签名
		String SignTemp="amount="+amount+"&datetime="+datetime+"&memberid="+pay_memberid+"&orderid="+orderid+
				"&returncode="+returncode+"&transaction_id="+transaction_id+"&key="+keyValue;		
		
		System.out.println(SignTemp);
		String md5sign=MD5Utils.md5(SignTemp);//MD5加密
    
	    if (sign.equals(md5sign)){
	        if(returncode.equals("00")){
	        	map.put("status", true);
	        	map.put("msg", "充值成功");
	        }else{
	        	map.put("status", false);
	        	map.put("msg", "充值失败");
	        }
	    }else{
        	map.put("status", false);
        	map.put("msg", "验签失败");
	    }
		return map;
	}

	//保存提现信息DDDD
	@Override
	public  Map<String, Object> savePay(AgentzhfundLog afl, String subzh) {
		Map<String, Object> map=new HashMap<String, Object>();
		//根据传过来subzh去查询用户信息
		Subzh sub = subzhMapper.selectBySubzh(subzh);
		
		//判断用户信息是否存在
		if(null==sub) {
			log.info("sub==null:用户信息为null");	
			map.put("status", false);
			map.put("msg", "用户信息为存在");
			return map;
		}
		
		//判断用户的账户资金是否大于提现金额
		if (sub.getFundbalance() == null&&sub.getFundbalance().doubleValue() - Double.valueOf(afl.getAmount()) < 0) {
			log.info("result1:余额不足");	
			map.put("status", false);
			map.put("msg", "余额不足1");
			return map;
		}
		
		log.info("getSubzh:==》"+sub.getSubzh());
		String dates = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		Date paydate=DateUtil.getSystemDate();
		String	pay_orderid= System.currentTimeMillis()+"_"+subzh;//20位订单号 时间戳+6位随机字符串组成
		BigDecimal fund=new BigDecimal(afl.getAmount());
		sub.setFundbalance(sub.getFundbalance().subtract(new BigDecimal(afl.getAmount())));
		afl.setFund(fund);
		
		//调柜台协议
		afl= saveAgentzhfundLog(afl,subzh, pay_orderid, paydate, fund, 6,2);
		if(afl!=null){
			int num=subzhMapper.updateByPrimaryKeySelective(sub);
			if(num>0) {
				map.put("status", true); 
			}
			
			log.info("提现减去fundbalance字段的钱  ：  " +num);
		}
		
		//调柜台协议
		if(null!=sub.getIsadmin()&&sub.getIsadmin()==6){
		Map<String, Object> gmap = getMoney(sub, afl);
		if(null!=gmap&&gmap.size()>0) {
			if(null!=gmap.get("status")&& (Boolean)gmap.get("status")){
				map.put("status", true);
	        	map.put("msg", "提现申请提交成功");	        	
				
			}else {
				map.put("status", false);
	        	map.put("msg", "提现申请提交失败");
	        	afl.setStatus(10);
	        
			}
		}else {
			map.put("status", false);
        	map.put("msg", "提现申请提交失败");
        	afl.setStatus(10);
		}
		}
		if(afl.getStatus()==10){
			log.info("保存提现信息 :"+agentzhfundLogMapper.updateByPrimaryKeySelective(afl));
		}
		
		return map;
	}
	
	//提现审核服务EEEE
	@Override
	public Map<String, Object> isPay(Integer id, Integer status) {
		Map<String, Object> map=new HashMap<String, Object>();
		try {
		AgentzhfundLog afl=null;
		if(null==id&&id==0) {
			map.put("msg", "提现信息不存在");
			map.put("status", false);
			return map;
		}
		
		afl = agentzhfundLogMapper.selectByPrimaryKey(id);

		
		if(afl!=null&&afl.getStatus()==6){
			if(status!=7) {
				map.put("status", false);
				map.put("msg", "提现信息不存在");
				return map;
			}
		}else {
			map.put("status", false);
			map.put("msg", "审核未通过");
			return map;
		}
		//将数据库保存的对应提现分别set到对应的实体类字段
		String description= afl.getDescription();
 		JSONObject jsonobj = JSONObject.parseObject(description);
		
		afl.setAmount(jsonobj.getString("amount"));
		afl.setBankCardNumber(jsonobj.getString("bankCardNumber"));	
		afl.setPhoneNum(jsonobj.getString("phoneNum"));
		afl.setIdcard(jsonobj.getString("idcard"));
		afl.setBankcode(jsonobj.getString("bankcode"));
		
			
	
		String subzh = afl.getSubzh();
			
		if (subzh == null) {
			map.put("status", false);
			map.put("msg", "result用户不存在");
			return map;
		}			
			
			TreeMap<String, Object> sortedMap = new TreeMap();
			sortedMap.put("payKey", PayConfigUtil.readConfig("payKey")); // 商户支付Key
			sortedMap.put("outTradeNo",afl.getOrderno());
			
			Fundset fundset = fundsetService.selectone();
    		Double czcost=0.0;
    		if(fundset!=null&&fundset.getCzcost()>0) {
    			czcost=fundset.getCzcost();
    		}
						
			DecimalFormat decimalFormat = new DecimalFormat("###################.##");
			sortedMap.put("orderPrice",decimalFormat.format(Double.valueOf(afl.getAmount())-czcost));// 代付金额
			sortedMap.put("proxyType","T1");//交易类型 T0 或 T1
			sortedMap.put("productType","B2CPAY"); //产品类型 B2CPAY  WEIXIN  ALIPAY
			sortedMap.put("bankAccountType","PRIVATE_DEBIT_ACCOUNT");//PRIVATE_DEBIT_ACCOUNT 对私借记卡  PUBLIC_ACCOUNT 对公
			sortedMap.put("phoneNo",afl.getPhoneNum()); //代付银行手机号
			sortedMap.put("receiverName",afl.getName());   //账户名 收款人账户名
			
			sortedMap.put("certType","IDENTITY");//证件类型 IDENTITY 身份证
			sortedMap.put("certNo",afl.getIdcard());  //代付证件号码
			sortedMap.put("receiverAccountNo",afl.getBankCardNumber());//银行账号
			sortedMap.put("bankName",BankCodeEnum.toMap().get("CCB").get("desc"));////开户行名称
			sortedMap.put("bankCode",afl.getBankcode()); //联行号（简称）
			sortedMap.put("bankBranchNo","xxxxx");
			sortedMap.put("notifyUrl", PayConfigUtil.readConfig("proxynotifyUrl"));
			
			System.out.println("提现输入参数"+sortedMap);
	        String sign = MerchantApiUtil.getSign(sortedMap, PayConfigUtil.readConfig("paySecret"));
	        sortedMap.put("sign",sign);
	        String payResult = SimpleHttpUtils.httpPost(PayConfigUtil.readConfig("toAccountProxyPayUrl"), sortedMap);
	        System.out.println("提现同步返回值  ："+payResult);
	        JSONObject jsonObject = JSON.parseObject(payResult);
	        Object resultCode = jsonObject.get("resultCode");//返回码
	        Object errMsg = jsonObject.get("errMsg");//错误信息(请求失败时)

	        if ("0000".equals(resultCode.toString())){//请求成功
	            System.out.println("请求成功");
	        }else{//请求失败
	            System.out.println(errMsg);
	        }
		
			log.info("提现申请状态:===>"+errMsg+"==提现申请状态码：====》"+resultCode);
			
			if("0000".equals(resultCode)||"9996".equals(resultCode)) {//tstatus=success表示提现成功
				Object remitStatus = jsonObject.get("remitStatus");
				if("REMITTING".equals(remitStatus)) {//tstatus=success表示提现成功
					//修改操作状态为审核中
					afl.setStatus(7);
					map.put("status", "true");
					map.put("msg", errMsg);
				}else if("REMIT_SUCCESS".equals(remitStatus)){
					//修改操作状态为提现成功
					afl.setStatus(9);
					map.put("status", "true");
					map.put("msg", errMsg);
				}else {
					afl.setStatus(10);//提现失败
					map.put("status", "false");
					map.put("msg", errMsg);
				}
			}
			Subzh sub=subzhMapper.selectBySubzh(subzh);
			if(null!=afl.getStatus()&&afl.getStatus()==10) {
				if(null!=sub) {
					//调柜台协议，把钱加到柜台账户
				log.info("调柜台协议将用户的提现失败的金额反回给客户");
				Map<String, Object> amap = addMoney(sub, afl);
				if(null!=amap&&amap.size()>0) {
					if(null!=amap.get("status")&& (Boolean)amap.get("status")){
						log.info("将用户提现失败金额返回给客户成功");
							}
						}
					}
				}			
			int num=agentzhfundLogMapper.updateByPrimaryKeySelective(afl);	
		} catch (Exception e) {
			log.info("提现记录异常=>"+e);
			map.put("status", "false");
			map.put("msg", "提现失败");
			return map;
		}
		return map;
	}
	
	//用户点击刷新提现进度
	@Override
	public Map<String, Object>  saveGetMoney(String pay_orderid,String username) {
		Map<String, Object> map=new HashMap<String, Object>();
		TreeMap<String, Object> tmap = new TreeMap();
		tmap.put("payKey", PayConfigUtil.readConfig("payKey")); // 商户支付Key
		tmap.put("outTradeNo",pay_orderid);//原交易订单号
		//tmap.put("subPayKey",PayConfigUtil.readConfig("subPayKey"));//原交易订单号
		
		//封装验签参数
		String sign = MerchantApiUtil.getSign(tmap, PayConfigUtil.readConfig("paySecret"));
		//加密验签
		tmap.put("sign",sign);
		log.info("查询提现进度输入参数 ："+tmap);
        String payResult = SimpleHttpUtils.httpPost(PayConfigUtil.readConfig("proxyPayQueryUrl"), tmap);
		Map<String, String> resmap = JSON.parseObject(payResult, Map.class);
		log.info("查询提现进度输出参数 ："+payResult);
		String status=resmap.get("resultCode");
		
		Subzh subzh = subzhMapper.selectBySubzh(username);
		
		AgentzhfundLog agentzhfundLog=null;
		List<AgentzhfundLog> alist = agentzhfundLogMapper.selectByorderno(pay_orderid);
		
		log.info("alist"+alist.size()+"---"+alist);
		
		if(alist!=null&&alist.size()>0) {
			agentzhfundLog=alist.get(0);
		}
		
		if(agentzhfundLog.getStatus()==10) {
			map.put("status", false);
			map.put("status","该订单提现失败");
			log.info("该订单提现失败");
			return map;
		}
		
		if("0000".equals(status)) {
			String outTradeNo=resmap.get("outTradeNo");
			String remitStatus=resmap.get("remitStatus");
			String settAmount=resmap.get("settAmount");
			String completeDate=resmap.get("completeDate");
			String pay_md5sign=resmap.get("sign");
			String signTime=resmap.get("signTime");
			log.info("查询提现结果验签sign="+sign+"<======>pay_md5sign="+pay_md5sign);
			if("REMIT_SUCCESS".equals(remitStatus)) {
				if(agentzhfundLog!=null&&agentzhfundLog.getStatus()==7) {
					agentzhfundLog.setStatus(9);//修改提现专题为成功
					agentzhfundLog.setModifytime(DateUtil.strToDateLong(completeDate));
					BigDecimal fund = agentzhfundLog.getFund();
					saveFundpzlog(username,fund, outTradeNo, DateUtil.getSystemDate(), 7);
					map.put("status", true);	
			  }
			}else if("REMITTING".equals(remitStatus)){			
					map.put("status", false);
					map.put("status","处理中");				
			}else {
				agentzhfundLog.setStatus(10);
				map.put("status", false);
				map.put("status","打款失败");	
			}
		}else {
			String msg=resmap.get("errMsg");
			log.info("查询进度失败  --"+msg);
			map.put("status", false);
			map.put("status","查询进度失败,"+msg);
		}
		
		log.info("修改提现状态 ："+agentzhfundLogMapper.updateByPrimaryKeySelective(agentzhfundLog));
		//判断状态为10，表示提现失败，把钱返回到客户手中
		Object object = new Object();
		synchronized (object) {
		if(null!=agentzhfundLog.getStatus()&&agentzhfundLog.getStatus()==10) {
			if(null!=subzh) {
				//调柜台协议，把钱加到柜台账户
			log.info("调柜台协议将用户的提现失败的金额反回给客户");
			Map<String, Object> amap = addMoney(subzh, agentzhfundLog);
			if(null!=amap&&amap.size()>0) {
				if(null!=amap.get("status")&& (Boolean)amap.get("status")){
					log.info("将用户提现失败金额返回给客户成功");
						}else {
					log.info("将用户提现失败金额返回给客户失败1");		
						}
					}else {
					log.info("将用户提现失败金额返回给客户失败2");	
					}
				}else {
					log.info("用户信息不存在");
				}
			}
		}
		return map;
	}
	
	//审核不通过时调用FFFF
	@Override
	public Map<String, Object> checkWithdraw(Integer id, Integer status) {
		Map<String, Object> map=new HashMap<String, Object>();
		try {
			AgentzhfundLog fundLog = agentzhfundLogMapper.selectByPrimaryKey(id); 
			
			Subzh subzhEntity=null;
			if(fundLog != null){
				if(status!=null && status==8){
					try {
		 				fundLog.setStatus(8);/*状态   0是申请状态。1是成功 5：审核通过  6：拒绝*/
		 				fundLog.setModifytime(Calendar.getInstance().getTime());
		 				log.info("审核通过保存状态：" + agentzhfundLogMapper.updateByPrimaryKeySelective(fundLog));
		 			} catch (Exception efundlog) {
		 				log.error("保存预提现信息异常：" + efundlog.getMessage());
		 				map.put("msg", "false");
		 				return map;
		 			}
					subzhEntity=subzhMapper.selectBySubzh(fundLog.getSubzh());
					//调柜台协议，把钱加到柜台账户
					log.info("提现审核拒绝后调入金协议");
					Map<String, Object> amap = addMoney(subzhEntity, fundLog);
					if(null!=amap&&amap.size()>0) {
						if(null!=amap.get("status")&& (Boolean)amap.get("status")){
							log.info("将用户提现失败金额返回给客户成功");
					   }
					}
					map.put("status", "true");
					
	 				return map;
				}
			}
		}catch(Exception e) {
			map.put("status", "false");
			return map;
		}
		return map;
	}

	//调账功能业务实现GGGG
	@Override
	public Map<String, Object> saveAccount(String username, String accmoney,Integer mstatus) {
		Map<String, Object> map=new HashMap<String, Object>();
		if(StringUtil.isAnyNullOrEmpty(username,accmoney)) {
			log.info("调账账户或者调账金额输入有误，请检查");
			map.put("status", false);
			map.put("msg", "调账账户或者调账金额输入有误，请检查");
			return map;
		}
		BigDecimal total_fee=new BigDecimal(accmoney);//调账金额
		Subzh subzh=null;
		try {
			subzh = subzhMapper.selectBySubzh(username);
			if(null==subzh) {
				log.info("输入用户不存在");
				map.put("status", false);
				map.put("msg", "输入用户不存在");
				return map;
			}
			if(subzh.getIsadmin()==6&&mstatus==1) {
				subzh.setZhstate(1);
			}		
			int count=subzhMapper.updateByPrimaryKeySelective(subzh);
			log.info("log11:subzhService"+count);
		} catch (Exception e) {
			log.error("查询用户表异常：" + e);
			map.put("status", false);
			map.put("msg", "输入用户不存在");
			return map;
		}
		

		if(mstatus==null&&mstatus==0){//入金--判断入金信息状态	
			log.info("调账类型未选择");
			map.put("status", false);
			map.put("msg", "请选择调账类型");
			return map;
		}
		if(mstatus==2) {//如果是出金先要调柜台协议查询可用资金
			if(subzh.getIsadmin()==6) {
				log.info("查可用");
				if(getfundbalance(subzh).doubleValue()-total_fee.doubleValue()<0) {
					log.info("该账户余额不足，禁止提现");
					map.put("status", false);
					map.put("msg", "该账户余额不足，禁止提现");
					return map;
				}
			}else {
				if(subzh.getFundbalance().doubleValue()-total_fee.doubleValue()<0) {
					log.info("该账户余额不足，禁止提现");
					map.put("status", false);
					map.put("msg", "该账户余额不足，禁止提现");
					return map;
				}
			}			
		}		
		int bz=mstatus==1?1:2;
		int fundtype=mstatus==1?8:9;//--资金流水表 8调账入金 9调账出金
		int status=mstatus==1?3:11;//--提现审核表 3表示调账入金申请 1表示调账出金申请
		int tstatus=mstatus==1?4:12;//--提现审核表 4表示调账入金成功 12表示调账出金成功
		log.info("bz===>"+bz+"<===>fundtype===>"+fundtype);

		String dates = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String orderno =  dates + StringUtil.getRandomString(4)+username;
		try {
			AgentzhfundLog afl=new AgentzhfundLog();
			afl.setAmount(accmoney);
			afl=saveAgentzhfundLog(afl,username, orderno, DateUtil.getSystemDate(), total_fee, status, bz);

			if(null!=afl&&afl.getId()!=null&&afl.getId()!=0) {
			
				//计算税率
				BigDecimal fund=afl.getFund().divide(new BigDecimal(EXCHANGE_RATE),2,RoundingMode.HALF_UP);
				try {
					if(subzh.getIsadmin()==6) {
					log.info("提现调柜台协议开始");
					
					String  str1="{\"seq_no\":"+afl.getId()+",\"client_id\":\""+username+"\",\"fund_type\":5,\"prev_fund\":0.00,\"after_fund\":"+fund+
							",\"warning_line\":0.00,\"open_line\":0.00}";
					String  str2="{\"seq_no\":"+afl.getId()+",\"client_id\":\""+username+"\",\"fund_type\":1,\"prev_fund\":0.00,\"after_fund\":"+fund+
							",\"warning_line\":0.00,\"open_line\":0.00}";
					String  str=mstatus==1?str1:str2;
					
					String rbStr = simulateStockService.commonFunction(str,DefineNumber.JCL_PROTOCOL_ID_REQ_FUNDMESSAGE);
					log.info("传到柜台的参数"+rbStr);
					if(!StringUtil.isAnyNullOrEmpty(rbStr) && rbStr.indexOf("{") > -1){
						JsonParser parser = new JsonParser();
						JsonElement element = parser.parse(rbStr);
						JsonObject jsonObj = element.getAsJsonObject();
			    		if(CONST.STATUS_INT_INSTANCE_SUCCESS == jsonObj.get("status").getAsInt()){
			    			log.info("调柜台协议成功");
			    			afl.setStatus(tstatus);//调账成功
			    			saveFundpzlog(username, total_fee, orderno, DateUtil.getSystemDate(),fundtype);
			    			map.put("status", true);
			    			map.put("msg", "调账成功");
			    		} else{
			    			//调柜台协议失败删除该条记录
			    			log.info(jsonObj.get("describe"));
			    			afl.setStatus(3);//调账失败
			    			try {
			    				//当用户入金失败，判断用户是否是第一次入金，如果是则还是把用户状态改为未激活
			    				List<FundPzlog> sfplist = getFundPzlogList(subzh);
			    				if((sfplist==null&&sfplist.size()<=1)&&mstatus==1) {
			    					subzh.setZhstate(0);
			    				}		    			
			    			} catch (Exception e) {
			    				log.error("查询该用户资金流水失败", e);
							}			    			
			    			map.put("status", false);
			    			map.put("msg", "调账出现异常，请联系管理员");
			    		}
					}
				}else {
					if(mstatus==1){
						subzh.setFundbalance(subzh.getFundbalance().subtract(total_fee));
					}else {
						subzh.setFundbalance(subzh.getFundbalance().add(total_fee));
					}
					log.info("普通用户调账  :"+subzhMapper.updateByPrimaryKeySelective(subzh));
				}
				} catch (Exception efund) {
					//调柜台协议失败删除该条记录
					afl.setStatus(13);//调账失败
					log.error("柜台调协议异常：" + efund.getMessage());
					map.put("status", false);
	    			map.put("msg", "调账出现异常，请联系管理员");
				}
			}
		  			
    			int num=agentzhfundLogMapper.updateByPrimaryKeySelective(afl);
    			log.info("updateByPrimaryKeySelective==success"+num);	
		} catch (Exception e) {
			log.error("调账出现异常", e);
			map.put("status", false);
			map.put("msg", "调账出现异常，请联系管理员");
			return map;
		}	
		return map;
	}
	/**
	 * 
	 * @param agentzhfundLog 
	 * @param username 用户id--subzh
	 * @param order_no  交易订单号
	 * @param pay_time	交易时间
	 * @param total_fee 交易金额
	 * @param paystate  操作状态
	 * @param bz 操作类型  
	 * @return
	 */
	private AgentzhfundLog saveAgentzhfundLog(AgentzhfundLog agentzhfundLog,String username,String order_no,Date pay_time,BigDecimal total_fee,int paystate,int bz){
		try{
			String type=paystate==0?"rj":paystate==3?"tzrj":paystate==6?"tx":paystate==11?"tzcj":"qt";
			agentzhfundLog.setSubzh(username);
			agentzhfundLog.setOrderno(order_no);
			agentzhfundLog.setType(type);
			agentzhfundLog.setBz((byte)bz);//充值
			agentzhfundLog.setModifytime(pay_time);
			agentzhfundLog.setFund(total_fee);
			agentzhfundLog.setStatus(paystate);//支付成功
			agentzhfundLog.setName(agentzhfundLog.getName());
			
			TreeMap<String, Object> tmap =new TreeMap<String, Object>();
			if(bz==2) {//提现
				tmap.put("amount",total_fee);
				tmap.put("bankCardNumber",agentzhfundLog.getBankCardNumber());
				tmap.put("phoneNum", agentzhfundLog.getPhoneNum());
				tmap.put("idcard", agentzhfundLog.getIdcard());
				tmap.put("bankcode", agentzhfundLog.getBankcode());
				JSONObject description = JSONObject.parseObject(JSON.toJSONString(tmap));
				agentzhfundLog.setDescription(description.toJSONString());
			}else {
				tmap.put("充值方式",type);
				tmap.put("充值人", agentzhfundLog.getName());
				tmap.put("充值单号", order_no);
				tmap.put("充值金额", agentzhfundLog.getFund());
				tmap.put("充值时间", agentzhfundLog.getModifytime());
				JSONObject description = JSONObject.parseObject(JSON.toJSONString(tmap));
				agentzhfundLog.setDescription(description.toJSONString());
			}
				log.info("log6:"+agentzhfundLog.getDescription());
				int num=agentzhfundLogMapper.insertSelective(agentzhfundLog);
				log.info("agentzhfundLogMapper==SUCCESS:"+num);
		}catch(Exception e){
			e.printStackTrace();
		}
		return agentzhfundLog;
	}
	
	/**
	 * 配资方法
	 * @param username 用户名
	 * @param accmoney 配资金额
	 * @param orderno 订单号
	 * @param paytime 配资时间
	 * @param fundtype 配资类型
	 * @return
	 */
	public FundPzlog saveFundpzlog(String username,BigDecimal accmoney,String orderno,Date paytime,int fundtype) {
		FundPzlog fundPzlog =new FundPzlog(); 
		log.info("log:开始记录配资"+username+
				"---8--"+"--"+accmoney+"--");
			fundPzlog.setSubzh(username);
			// 1:配资 资金比例 2:加配资金比例 3:减配  4:追加保证金 5:出金  6:充值 7:提现 8:调账入金 9调账出金*/
			fundPzlog.setFundtype(fundtype);
			fundPzlog.setSetpro(0);
			fundPzlog.setFundbalance(accmoney);
			fundPzlog.setCreatetime(DateUtil.getSystemDate());
			
			TreeMap<String, Object> tmap =new TreeMap<String, Object>();
			tmap.put("操作人", fundPzlog.getSubzh());
			tmap.put("操作单号", orderno);
			tmap.put("操作金额",accmoney);
			tmap.put("操作时间",paytime);
			JSONObject descriptions = JSONObject.parseObject(JSON.toJSONString(tmap));
			fundPzlog.setSetdesc(descriptions.toJSONString());
			try {
				int n=pzlogMapper.insertSelective(fundPzlog);
				log.info("log操作配资===》"+n+"=====>"+fundPzlog.getSetdesc());
			} catch (Exception e) {
				log.info("操作配资失败", e);
			}
			return fundPzlog;
	}
	
	//判断某个用户是否有持仓
	private boolean isHaveHold(String user){
		log.info("进来了");
		boolean b=true;
		try{
			log.info("进来了");
			String con="{\"start\":0,\"limit\":10,\"client_id\":\""+user+"\"}";
			
			String rbStr = simulateStockService.commonFunction(con, 11123);//持仓记录
			log.info("进来了2"+rbStr);
			if(rbStr==null||rbStr=="null" || rbStr.equals("")){
				log.info("进来了3"+rbStr);
				rbStr="{\"status\":10,\"describe\":\"地址错误或网络错误\"}";
				JSONObject obj = JSONObject.parseObject(rbStr);
			}else if(rbStr.indexOf("{")<0){
				log.info("进来了4"+rbStr);
				rbStr="{\"status\":11,\"describe\":\"返回信息出错\"}";
				JSONObject obj = JSONObject.parseObject(rbStr);
			}else{//正常情况下
				JSONObject obj = JSONObject.parseObject(rbStr);
				if(obj.containsKey("status") && obj.getIntValue("status")==0){
					JSONArray arr=obj.getJSONArray("holders");
					if(arr.size()>0){
						b=false;
						log.info("查持仓ok"+arr.size());
					}
				}else {
					log.info(obj.get("describe"));
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return b;
	}
	
	//调柜台资金协议
		public JSONObject queryfund(Subzh subzh){
			JSONObject data=null;
			try{
				log.info("调柜台资金协议接口");
				String con="{\"client_id\":\""+subzh.getSubzh()+"\"}";
				log.info("log——con===》"+con);
				String rbStr = simulateStockService.commonFunction(con, 11103);//用户资金信息查询
				log.info("log——rbStr===》"+rbStr);
				if(rbStr==null||rbStr=="null" || rbStr.equals("")){
					rbStr="{\"status\":10,\"describe\":\"地址错误或网络错误\"}";
					JSONObject obj = JSONObject.parseObject(rbStr);
					log.info("log1——obj===》"+obj);
					return null;
				}else if(rbStr.indexOf("{")<0){
					rbStr="{\"status\":11,\"describe\":\"返回信息出错\"}";
					JSONObject obj = JSONObject.parseObject(rbStr);
					log.info("log2——obj===》"+obj);
					return null;
				}else{//正常情况下
					Map<String, Object> map=JSONObject.parseObject(rbStr);
					if(map.containsKey("status") &&Integer.parseInt(map.get("status").toString())==0){
						data=(JSONObject) JSONObject.toJSON(map.get("data"));
					}else {
						data=null;
						log.info("调协议失败");
					}
					log.info("log3——obj===》"+data+"enable_balance===》"+data.get("enable_balance"));
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return data;
		}
		
		//通过查询用户账户的占用保证金和冻结保证金判断用户是否有持仓
		@Override
		public boolean isPosition(Subzh subzh) {
			log.info("-----------------------查持仓------------------------");
			Boolean flag=false;
			try {
				if(subzh!=null){
					JSONObject data = queryfund(subzh);
					if(data!=null){
						double margin_balance = 0.0;
						double freeze_balance = 0.0;
						if(!StringUtil.isAllNullOrEmpty(String.valueOf(data.get("margin_balance")))) {//占用
							margin_balance=Double.valueOf(String.valueOf(data.get("margin_balance")));
						}
						if(!StringUtil.isAllNullOrEmpty(String.valueOf(data.get("freeze_balance")))) {//冻结
							freeze_balance=Double.valueOf(String.valueOf(data.get("freeze_balance")));
						}	
						log.info("margin_balance :"+margin_balance+"---freeze_balance: "+freeze_balance );
						if(margin_balance+freeze_balance>0){
							flag=true;
						}
						
					}else {
						log.info("用户持仓信息存在");
					}
				}else {
					log.info("用户信息不存在");
				}
			} catch (Exception e) {
				log.info("查用户持仓异常", e);
			}
			return flag;
		}
		
		//查询可用资金
		@Override
		public BigDecimal getfundbalance(Subzh subzh) {
			if(subzh!=null){
					try{						
						log.info("调协议查询可用资金成功");
						JSONObject data = queryfund(subzh);
						if(data!=null){						
						if(!StringUtil.isAllNullOrEmpty(String.valueOf(data.get("enable_balance")))){
								BigDecimal fundbalance=new BigDecimal(String.valueOf(data.get("enable_balance")));
								fundbalance =fundbalance.setScale(2, BigDecimal.ROUND_DOWN);
								subzh.setFundbalance(fundbalance.multiply(new BigDecimal(EXCHANGE_RATE)));
							}else {
								subzh.setFundbalance(new BigDecimal(0));
							}
						}else {
							subzh.setFundbalance(new BigDecimal(0));
						}
					}catch(Exception e){
						log.info("查询可用资金异常", e);
					}
				}
				try {
					subzhMapper.updateByPrimaryKeySelective(subzh);
				} catch (Exception e) {
					log.error("查询柜台后修改客户可用资金", e);
				}
			return subzh.getFundbalance();
		}
	
	//根据用户名去查询配资信息
	public List<FundPzlog> getFundPzlogList(Subzh subzh){
		List<FundPzlog> sfplist = new ArrayList<FundPzlog>();
		try {
			FundPzlogExample example=new FundPzlogExample();
			Criteria createCriteria = example.createCriteria();
			createCriteria.andSubzhEqualTo(subzh.getSubzh());
			sfplist = pzlogMapper.selectByExample(example);
		} catch (Exception e) {
			log.error("查询用户配资信息", e);
		}
		return sfplist;
	}
	
	
	/**
	 * 调柜台入金协议
	 * @param subzh 账户信息
	 * @param agentzhfundLog  提现信息
	 * @return
	 */
	
	public Map<String, Object> addMoney(Subzh subzh,AgentzhfundLog agentzhfundLog){
			Map<String, Object> map=new HashMap<String, Object>();
			log.info("调柜台的入金金额"+agentzhfundLog.getFund());
			//计算汇率，调柜台协议，把钱加到柜台账户
			BigDecimal fundbalance =agentzhfundLog.getFund().divide(new BigDecimal(EXCHANGE_RATE), 2,RoundingMode.HALF_UP);
			try {
				if(subzh!=null&&subzh.getIsadmin()==6) {
					String rbStr = simulateStockService.commonFunction("{\"seq_no\":"+agentzhfundLog.getId()+",\"client_id\":\""+subzh.getSubzh()+"\",\"fund_type\":5,\"prev_fund\":0.00,\"after_fund\":"+fundbalance+
							",\"warning_line\":0.00,\"open_line\":0.00}",DefineNumber.JCL_PROTOCOL_ID_REQ_FUNDMESSAGE);
					if(!StringUtil.isAnyNullOrEmpty(rbStr) && rbStr.indexOf("{") > -1){
						JsonParser parser = new JsonParser();
						JsonElement element = parser.parse(rbStr);
						JsonObject jsonObj = element.getAsJsonObject();
						log.info("调入金协议成功1：" + rbStr+"---"+DefineNumber.JCL_PROTOCOL_ID_REQ_FUNDMESSAGE);
			    		if(CONST.STATUS_INT_INSTANCE_SUCCESS == jsonObj.get("status").getAsInt()){
			    		//掉协议成功，入金成功			    			
			    			log.info("调入金协议成功2：" + rbStr);
			    			map.put("status", true);
			    			map.put("msg", "调入金协议成功");
			    		} else {
			    			//入金失败
			    			log.info("调入金协议失败：" + rbStr);
			    			map.put("status", true);
			    			map.put("msg", "调入金协议失败");
			    		}
					}
				}else {
					if(subzh.getFundbalance()!=null) {
						subzh.setFundbalance(subzh.getFundbalance().add((agentzhfundLog.getFund())));
				}
					subzhMapper.updateByPrimaryKeySelective(subzh);
				}
			} catch (Exception e) {
				log.error("调入金协议出现异常",e);
			}
			return map;
		}

	/**
	 * //调柜台出金协议
	 * @param subzh 账户信息
	 * @param afl  出金信息
	 * @return
	 */
	
	public Map<String, Object> getMoney(Subzh subzh,AgentzhfundLog afl){
			Map<String, Object> map=new HashMap<String, Object>();
			//计算汇率，调柜台协议，把钱加到柜台账户
			log.info("调柜台的出金金额"+afl.getFund());
			BigDecimal fundbalance =afl.getFund().divide(new BigDecimal(EXCHANGE_RATE), 2,RoundingMode.HALF_UP);
			if(null!=afl&&afl.getId()!=null&&afl.getId()!=0){	
				try {
					log.info("提现调柜台协议开始");
					
					String  str="{\"seq_no\":"+afl.getId()+",\"client_id\":\""+subzh.getSubzh()+"\",\"fund_type\":1,\"prev_fund\":0.00,\"after_fund\":"+fundbalance+
							",\"warning_line\":0.00,\"open_line\":0.00}";
					
					String rbStr = simulateStockService.commonFunction(str,DefineNumber.JCL_PROTOCOL_ID_REQ_FUNDMESSAGE);
					log.info("传到柜台的参数"+rbStr);
					if(!StringUtil.isAnyNullOrEmpty(rbStr) && rbStr.indexOf("{") > -1){
						JsonParser parser = new JsonParser();
						JsonElement element = parser.parse(rbStr);
						JsonObject jsonObj = element.getAsJsonObject();
			    		if(CONST.STATUS_INT_INSTANCE_SUCCESS == jsonObj.get("status").getAsInt()){
			    			log.info("调柜台协议成功");
			    			//保存资金流水信息
			    			map.put("status", true);
			    			map.put("msg", "申请提现成功");
			    		} else{
			    			//调柜台协议失败删除该条记录
			    			log.info(jsonObj.get("describe"));  
			    			map.put("status", false);
			    			map.put("msg", "提现出现异常，请联系管理员");
			    		}
					}
				} catch (Exception efund) {
					//调柜台协议失败删除该条记录
					log.error("柜台调协议异常：" + efund.getMessage());
					map.put("status", false);
	    			map.put("msg", "提现出现异常，请联系管理员");
				}
			}
			return map;
		}

	@Override
	public Map<String, Object> savekPay(String bankcode, Double moneys, String subzh) {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("payKey", PayConfigUtil.readConfig("payKey"));// 商户支付Key
		paramMap.put("orderPrice", moneys); //支付金额
		String orderNo = String.valueOf(System.currentTimeMillis())+"_"+subzh; // 订单编号
		paramMap.put("outTradeNo", orderNo);
		paramMap.put("productType","50000101");//B2C T0支付
		Date orderTime = new Date();// 订单时间
		String orderTimeStr = new SimpleDateFormat("yyyyMMddHHmmss").format(orderTime);// 订单时间
		paramMap.put("orderTime", orderTimeStr);
		paramMap.put("productName", "背包");// 商品名称
		String orderIp = PayConfigUtil.readConfig("orderIp"); // 下单IP
		paramMap.put("orderIp", orderIp);
		paramMap.put("bankCode", bankcode);//银行编码
		paramMap.put("bankAccountType", "PRIVATE_DEBIT_ACCOUNT");//银行卡类型
		String returnUrl = PayConfigUtil.readConfig("returnUrl"); // 页面通知返回url
		paramMap.put("returnUrl", returnUrl);
		String notifyUrl = PayConfigUtil.readConfig("notifyUrl"); // 后台消息通知Url
		paramMap.put("notifyUrl", notifyUrl);
		paramMap.put("remark", "支付备注");
		
		AgentzhfundLog agentzhfundLog=new AgentzhfundLog();
		saveAgentzhfundLog(agentzhfundLog,subzh, orderNo, DateUtil.getSystemDate(), new BigDecimal(moneys), 0, 1);

		///// 签名及生成请求API的方法///
		String sign = MerchantApiUtil.getSign(paramMap, PayConfigUtil.readConfig("paySecret"));
		paramMap.put("sign", sign);
		System.out.println(PayConfigUtil.readConfig("b2cPayUrl"));
		System.out.println(paramMap);

		return paramMap;
	}
	
	//金付支付网关充值回调
	@Override
	public Map<String, Object> knotify(HttpServletRequest request) {
		Map<String, Object> map=new HashMap<>();
		try {
		String payKey=request.getParameter("payKey");//秘钥
		if(payKey.equals(PayConfigUtil.readConfig("payKey"))){
			String tradeStatus=request.getParameter("tradeStatus");
			String productName =request.getParameter("productName");//商品名称
			String outTradeNo=request.getParameter("outTradeNo");//订单号
			String orderPrice=request.getParameter("orderPrice");//充值金额
			String successTime=request.getParameter("successTime");//超过时间
			String orderTim=request.getParameter("orderTim");//下单时间
			String trxNo=request.getParameter("trxNo ");//流水号
			String remark=request.getParameter("remark");//备注
			String sign=request.getParameter("sign");//签名
			List<AgentzhfundLog> alist = agentzhfundLogMapper.selectByorderno(outTradeNo);
			
			if(alist!=null&&alist.size()>1){
				map.put("status", "false");
				map.put("msg", "订单重复");
				return map;
			}
			if(alist!=null&&alist.size()<1){
				map.put("status", "false");
				map.put("msg", "订单不存在");
				return map;
			}
			AgentzhfundLog agentzhfundLog = alist.get(0);
			if(agentzhfundLog!=null&&agentzhfundLog.getStatus()==1){
				map.put("status", "false");
				map.put("msg", "订单已完成");
				return map;
			}
			
    		Fundset fundset = fundsetService.selectone();
    		Double czcost=1.0;
    		if(fundset!=null&&fundset.getCzcost()>0) {
    		czcost=(double) (1-(fundset.getCzcost()/100));
    		}
    		agentzhfundLog.setFund(new BigDecimal(orderPrice).multiply(new BigDecimal(czcost)));

			
			Subzh subzh=subzhMapper.selectBySubzh(agentzhfundLog.getSubzh());		
			
			if(null==subzh||StringUtil.isAllNullOrEmpty(subzh.getSubzh())){
				map.put("status", "false");
				map.put("msg", "用户信息不存在");
				return map;
			}
				
	    	log.info("log8:subzh"+subzh.getSubzh());
	    	subzh.setZhstate(1);
	    	int count=subzhMapper.updateByPrimaryKeySelective(subzh);
	    	log.info("log11:subzhService"+count);
	    	
			List<FundPzlog> sfplist = getFundPzlogList(subzh);
			
			if(tradeStatus.equals("SUCCESS")) {
				agentzhfundLog.setStatus(1);
				agentzhfundLog.setModifytime(DateUtil.strToDateLong(successTime));//保存成功时间
				agentzhfundLog.setCreatetime(DateUtil.strToDateLong(orderTim));//保存下单时间
				Map<String, Object> amap = addMoney(subzh, agentzhfundLog);
				   if(null!=amap&&amap.size()>0) {
		    	    	if(null!=amap.get("status")&&(Boolean) amap.get("status")) {
		    	    		agentzhfundLog.setStatus(1);//修改状态为1 --表示充值成功
		    	    		map.put("status", true);
		    	        	map.put("msg", "充值成功");
		    	        	FundPzlog fundPzlog = saveFundpzlog(subzh.getSubzh(), agentzhfundLog.getFund(), outTradeNo, DateUtil.getSystemDate(), 6);	    	
		    	    	}else {
		    	    		agentzhfundLog.setStatus(2);//修改状态为2 --表示充值失败	 
		    	    		map.put("status", false);
		    	        	map.put("msg", "充值失败");
		    	        	
		    	   		 	if ((sfplist == null) && (sfplist.size() <= 1)) {
		    	   		 		subzh.setZhstate(0);
		    	   		 	}
		    	    	  }
		    	    	}else {
		    	    		agentzhfundLog.setStatus(2);//修改状态为2 --表示充值失败
			    	    	map.put("status", false);
				        	map.put("msg", "充值失败");
				        	if ((sfplist == null) && (sfplist.size() <= 1)) {
				        		subzh.setZhstate(0);
		    	   		 	}
			    	    }
		    	    ;
		    		log.info("log11:subzhService"+subzhMapper.updateByPrimaryKeySelective(subzh));
			}else if(tradeStatus.equals("FINISH")){
				agentzhfundLog.setStatus(1);
			}else {
				agentzhfundLog.setStatus(2);
			}
			
			log.info("log12:agentzhfundLogMapper"+agentzhfundLogMapper.updateByPrimaryKeySelective(agentzhfundLog));
		}else {//秘钥不一致			
				map.put("status", "false");
				map.put("msg", "秘钥不一致");
				return map;
		}
		}catch (Exception e) {
			log.error("金付支付充值回调异常", e);
			map.put("status", "false");
			map.put("msg", "回调异常");
			return map;
		}
		return map;
	}

	@Override
	public Map<String, Object> kreturn(HttpServletRequest request) {
		Map<String, Object> map=new HashMap<>();
		String payKey=request.getParameter("payKey");//秘钥
		if(payKey.equals(PayConfigUtil.readConfig("payKey"))){
			String tradeStatus=request.getParameter("tradeStatus");
			String productName =request.getParameter("productName");//商品名称
			String outTradeNo=request.getParameter("outTradeNo");//订单号
			String orderPrice=request.getParameter("orderPrice");//充值金额
			String successTime=request.getParameter("successTime");//超过时间
			String orderTim=request.getParameter("orderTim");//下单时间
			String trxNo=request.getParameter("trxNo ");//流水号
			String remark=request.getParameter("remark");//备注
			String sign=request.getParameter("sign");//签名
			
			if(tradeStatus.equals("SUCCESS")) {
				map.put("status", "true");
				map.put("msg", "充值成功");
			}else {
				map.put("status", "false");
				map.put("msg", "充值失败");
			}
		}else {//秘钥不一致			
				map.put("status", "false");
				map.put("msg", "秘钥不一致");
				return map;
		}
		return map;
	}
}
