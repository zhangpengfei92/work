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
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
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
import org.springframework.web.bind.annotation.RequestBody;

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
import com.jcl.pojo.Subzh;

import com.jcl.service.FundService;
import com.jcl.stock.entity.CONST;
import com.jcl.stock.simulate.DefineNumber;
import com.jcl.stock.simulate.service.SimulateStockService;
import com.jcl.util.Constant;
import com.jcl.util.DateUtil;
import com.jcl.util.DeployProperties;
import com.jcl.util.StringUtil;
import com.jcl.util.spt.HttpRequest;
import com.jcl.util.spt.MD5Utils;
import com.jcl.util.spt.SignMd5Utils;
/** 
 * @author zpf
 * @version 1.0
 */
/**
 * 出入金
 */
@Service
public class FundServiceImpl implements FundService{
	//资金明细的mapper
	@Autowired
	private AgentzhfundLogMapper agentzhfundLogMapper;
	
	//用户的mapper
	@Autowired
	private SubzhMapper subzhMapper;
	
	//用户配资的mapper
	@Autowired
	private FundPzlogMapper pzlogMapper;
	
	//调协议的服务
	@Autowired
	private  SimulateStockService simulateStockService;
	
	public static Logger log = Logger.getLogger(FundServiceImpl.class);
	
	private static final String project_path = DeployProperties.getInstance().getSptReturn();
	public static final  String EXCHANGE_RATE="1";//汇率
	
	public static final  String objstr="9527";
	
	//生成充值信息AAAA
	@Override
	public Map<String, String> savePay(String username,String txmoney,String channel) {

		String dates = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		BigDecimal money = new BigDecimal(txmoney);
		money = money.multiply(new BigDecimal("100")).setScale(0);
	
		
		String testKey = Constant.SPT_SECRTKEY;// 秘钥

		Map<String, String> payMap = new HashMap<String, String>();
		// 必填参数
		payMap.put("merch", Constant.SPT_MERCHART);// 商户号
		String orderno=dates + StringUtil.getRandomString(4)+username;
		payMap.put("orderno",orderno); // 商户订单号，查询时需要

		payMap.put("amount", money.toString()); // 金额，单位分
		payMap.put("body", "充值");// 商品描述

		payMap.put("notifyurl",project_path+ "/subzhfund/returnSptPayMent");// 异步回调通知地址
		payMap.put("pageurl", project_path+ "/subzhfund/stockoptionJump");// 异步回调通知地址
		payMap.put("biztype", channel);// 通道类型
		payMap.put("createip", "");// 通道类型为wx时 填写客户端ip
		
		payMap.put("comment", username);// 附加信息
		String sign = SignMd5Utils.createSign(payMap, testKey);// 签名
		payMap.put("sign", sign);// 签名串
		payMap.put("signtype", "MD5");
		
		AgentzhfundLog agentzhfundLog = new AgentzhfundLog();	
		saveAgentzhfundLog(agentzhfundLog, username, orderno, new Date(),  new BigDecimal(txmoney), 0, 1);
		return payMap;
	}

	//充值异步回调快捷BBBB
	@Override
	public  Map<String, Object>  payNotyfy(String requestBody) {
		log.info("充值回调开始");
		log.info("requestBody:" + JSONObject.parseObject(requestBody));
		JSONObject allStr =  JSONObject.parseObject(requestBody);
		String return_code = allStr.getString("return_code");//SUCCESS 代表成功，其它请参考错误代码
		String return_msg = allStr.getString("return_msg");//提示
		String merch_id = allStr.getString("merch_id");//商户号
		String trade_no = allStr.getString("trade_no");//平台交易号
		String order_no = allStr.getString("order_no");//商户订单号
		Integer total_fee = allStr.getInteger("total_fee");//总金额
		String pay_time = allStr.getString("pay_time");//支付完成时间，
		
		
		Map<String,Object> map = new HashMap<String,Object>();

		AgentzhfundLog afl=new AgentzhfundLog();
	    if ("SUCCESS".equals(return_code)){
	        	log.info("pay_time===>:"+pay_time);
	    		log.info("order_no===>:"+order_no);
	    		List<AgentzhfundLog> alist= null;
	    			try {
	    				alist = agentzhfundLogMapper.selectByorderno(order_no);
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
	    		//快捷千分之15的手续费
	    		afl.setFund(afl.getFund().multiply(new BigDecimal(0.994)));
	    		String	username=afl.getSubzh();
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
	    	        	FundPzlog fundPzlog = saveFundpzlog(username, afl.getFund(), order_no, DateUtil.getSystemDate(), 6);	    	
	    	    	}else {
	    	    		afl.setStatus(2);//修改状态为2 --表示充值失败	 
	    	    		map.put("status", false);
	    	        	map.put("msg", "充值失败");
	    	        	
	    	   		 	if ((sfplist == null) && (sfplist.size() <= 1)) {
	    	   		 		sub.setZhstate(Integer.valueOf(0));
	    	   		 	}
	    	    	  }
	    	    	}else {
		    	    	afl.setStatus(2);//修改状态为2 --表示充值失败
		    	    	map.put("status", false);
			        	map.put("msg", "充值失败");
			        	if ((sfplist == null) && (sfplist.size() <= 1)) {
	    	   		 		sub.setZhstate(Integer.valueOf(0));
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
		if (sub.getFundbalance() == null||sub.getFundbalance().doubleValue() - Double.valueOf(afl.getAmount()) < 0) {
			log.info("result1:余额不足");	
			map.put("status", false);
			map.put("msg", "余额不足1");
			return map;
		}
		
		log.info("getSubzh:==》"+sub.getSubzh());
		String dates = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		String pay_orderid =  dates + StringUtil.getRandomString(4)+subzh;
		
		Date paydate=DateUtil.getSystemDate();
		BigDecimal fund=new BigDecimal(afl.getAmount());
		
		sub.setFundbalance(sub.getFundbalance().subtract(new BigDecimal(afl.getAmount())));
		
		int num=subzhMapper.updateByPrimaryKeySelective(sub);
		if(num>0) {
			map.put("status", true); 
		}
		
		log.info("提现减去fundbalance字段的钱  ：  " +num);
		
		afl.setFund(fund);
		
		
		afl= saveAgentzhfundLog(afl,subzh, pay_orderid, paydate, fund, 4,2);
		//调柜台协议
		if(null!=sub.getIsadmin()&&sub.getIsadmin()==6){
		Map<String, Object> gmap = getMoney(sub, afl);
		
		if(null!=gmap&&gmap.size()>0) {
			if(null!=gmap.get("status")&& (Boolean)gmap.get("status")){
				afl= saveAgentzhfundLog(afl,subzh, pay_orderid, paydate, fund, 4,2);
				map.put("status", true);
	        	map.put("msg", "提现申请提交成功");	        	
				
			}else {
				map.put("status", false);
	        	map.put("msg", "提现申请提交失败");  	
			}
		}else {
			map.put("status", false);
        	map.put("msg", "提现申请提交失败");	
		}
		}
		return map;
	}
	
	//提现审核服务EEEE
	@Override
	public Map<String, Object> isPay(Integer id, Integer status) {
		Map<String, Object> map=new HashMap<String, Object>();
		AgentzhfundLog afl=null;
		if(null==id&&id==0) {
			map.put("msg", "提现信息不存在1："+id);
			map.put("status", false);
			return map;
		}

		afl = agentzhfundLogMapper.selectByPrimaryKey(id);

		
		if(afl!=null&&afl.getStatus()==4){
			if(status!=5) {
				map.put("status", false);
				map.put("msg", "提现信息不存在2："+status);
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
		
			
	try {
		String subzh = afl.getSubzh();
			
		if (subzh == null) {
			map.put("status", false);
			map.put("msg", "result用户不存在");
			return map;
		}			
			
		String url = "https://gw.lpt-pay.com/settle";
		String testKey = Constant.SPT_SECRTKEY;//秘钥

		TreeMap<String, String> sortedMap = new TreeMap();
		sortedMap.put("merch", Constant.SPT_MERCHART);//商户号
		sortedMap.put("orderno", afl.getOrderno());//商户订单号
		DecimalFormat decimalFormat = new DecimalFormat("###################");
		sortedMap.put("amount", decimalFormat.format((Double.valueOf(afl.getAmount())) * 100));//提现金额
		sortedMap.put("notifyurl", project_path+"/subzhfund/isPay");//异步回调通知地址
		sortedMap.put("cardtype", "1");//提现--对公对私
		sortedMap.put("bankcode", afl.getBankcode());//银行卡编码：例如工商银行：ICBC
		sortedMap.put("bankname", afl.getBankname());//银行卡开户行名称
		sortedMap.put("bankbranch", afl.getBanchname());//银行卡开户支行名称
		sortedMap.put("cardname", afl.getName());//收款人姓名
		sortedMap.put("cardno", afl.getBankCardNumber());//收款人银行账号
		sortedMap.put("cardmobile", afl.getPhoneNum());//银行预留手机号
		sortedMap.put("certid", afl.getIdcard());//身份证号
		sortedMap.put("province", afl.getProvince());//开户所在省
		sortedMap.put("city", afl.getCity());//开户所在市

		System.out.println(sortedMap.toString());
		String key_sign_zn = SignMd5Utils.createSign(sortedMap, testKey);// 签名
		sortedMap.put("sign", key_sign_zn);
		sortedMap.put("signtype", "MD5");

		String result = HttpRequest.sendPost(url, sortedMap);
			log.info("请求状态==》"+result);
			
			Map<String, String> resmap = JSON.parseObject(result, Map.class);			
			String recode = resmap.get("return_code");
			String msg = resmap.get("return_msg");
			log.info("提现申请状态:===>"+msg+"==提现申请状态码：====》"+recode);
			if ("SETTLE_NOT_WALLET".equals(recode)) {
				afl.setStatus(3);//提现失败
				map.put("status", false);
				map.put("msg",  "result"+recode+":"+msg);				
			}
			if ("SETTLE_OUT_UPPER".equals(recode)) {
				afl.setStatus(3);//提现失败
				map.put("status", false);
				map.put("msg",  "result"+recode+":"+msg);
			}

			if ("SUCCESS".equals(recode)){//tstatus=success表示提现成功
				
				//修改操作状态为处理中
				afl.setStatus(1);
				map.put("status", "true");
				map.put("msg", msg);
			}else {
				afl.setStatus(3);//提现失败
				map.put("status", "false");
				map.put("msg", msg);
			}
			Subzh sub=subzhMapper.selectBySubzh(subzh);
			if(null!=afl.getStatus()&&afl.getStatus()==3) {
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
	
	//提现回调
	@Override
	public Map<String, Object>  saveGetMoney(@RequestBody String requestBody) {
		Map<String, Object> map=new HashMap<String, Object>();
		try {

		AgentzhfundLog agentzhfundLog=null;
		Subzh subzh=null;
		log.info("requestBody"+requestBody);
		JSONObject allStr = JSONObject.parseObject(requestBody);
		TreeMap<String, String> sortedMap = new TreeMap();
		String return_code=allStr.getString("return_code");
		//String return_msg =allStr.getString("return_msg");
		
		if("SUCCESS".equals(return_code)) {//回调成功
			String order_no=allStr.getString("order_no");
			String total_fee=allStr.getString("total_fee");
			String merch_id=allStr.getString("merch_id");
			String settle_status=allStr.getString("settle_status");		
			String settle_no=allStr.getString("settle_no");
			String signtype =allStr.getString("signtype ");

			
			sortedMap.put("order_no",order_no);
			sortedMap.put("total_fee",total_fee);
			sortedMap.put("merch_id",merch_id);
			sortedMap.put("settle_status",settle_status);
			sortedMap.put("return_code",return_code);
			sortedMap.put("settle_no",settle_no);
			String camparesign = SignMd5Utils.createSign(sortedMap, Constant.SPT_SECRTKEY);
			//根据订单号去查询订单信息
			
			List<AgentzhfundLog> alist = agentzhfundLogMapper.selectByorderno(order_no);
			
			if(alist!=null&&alist.size()>0) {
				agentzhfundLog=alist.get(0);
			}
			
			if(agentzhfundLog.getStatus()==3) {
				map.put("status", false);
				map.put("msg","该订单提现失败");
				log.info("该订单提现失败");
				return map;
			}
			 subzh=subzhMapper.selectBySubzh(agentzhfundLog.getSubzh());
			log.info("签名号"+allStr.getString("sign")+"-----"+camparesign);
			
			log.info("判断签名号"+allStr.getString("sign").equals(camparesign));
			
			//if (allStr.getString("sign").equals(camparesign)) {		
			if(settle_status.equals("00000")) {
				if(agentzhfundLog!=null&&agentzhfundLog.getStatus()==1) {
					agentzhfundLog.setStatus(5);//修改提现状态为成功
					agentzhfundLogMapper.updateByPrimaryKeySelective(agentzhfundLog);					
					BigDecimal fund = agentzhfundLog.getFund();
					saveFundpzlog(agentzhfundLog.getSubzh(),fund, order_no, DateUtil.getSystemDate(),7);
					map.put("status", true);	
			  }
			}else {
				int num=0;
				if(settle_status.equals("10000")) {
					map.put("status", false);
					map.put("msg","失败");
					agentzhfundLog.setStatus(3);
					 num=agentzhfundLogMapper.updateByPrimaryKeySelective(agentzhfundLog);
				}else if(settle_status.equals("20000")){
					map.put("status", false);
					map.put("msg","处理中");
				}else if(settle_status.equals("30000")){
					map.put("status", false);
					map.put("msg","提交失败");
					agentzhfundLog.setStatus(3);
					 num=agentzhfundLogMapper.updateByPrimaryKeySelective(agentzhfundLog);
				}else if(settle_status.equals("40000")){
					map.put("status", false);
					map.put("msg","状态未知");
					agentzhfundLog.setStatus(3);
					 num=agentzhfundLogMapper.updateByPrimaryKeySelective(agentzhfundLog);
				}else if(settle_status.equals("50000")){
					map.put("status", false);
					map.put("msg","订单不存在");
					agentzhfundLog.setStatus(3);
					 num=agentzhfundLogMapper.updateByPrimaryKeySelective(agentzhfundLog);
				}
				log.info("updateByPrimaryKeySelective   :"+num);
			}
	}

		
		//判断状态为3，表示提现失败，把钱返回到客户手中
		Object object = new Object();
		synchronized (object) {
		if(null!=agentzhfundLog.getStatus()&&agentzhfundLog.getStatus()==3) {
			if(null!=subzh) {
				//调柜台协议，把钱加到柜台账户
			log.info("调柜台协议将用户的提现失败的金额反回给客户");
			Map<String, Object> amap = addMoney(subzh, agentzhfundLog);
			if(null!=amap&&amap.size()>0) {
				if(null!=amap.get("status")&& (Boolean)amap.get("status")){
					log.info("将用户提现失败金额返回给客户成功");
						}
					}
				}
			}
		}
		
	} catch (Exception e) {
	log.error("提现回调异常", e);
	}
		return map;
	}
	
	//审核不通过时调用FFFF
	@Override
	public Map<String, Object> checkWithdraw(Integer id, Integer status) {
		Map<String, Object> map=new HashMap<String, Object>();
		try {
			synchronized (objstr) {
				AgentzhfundLog fundLog = agentzhfundLogMapper.selectByPrimaryKey(id); 
				if(fundLog.getStatus()!=4){
					log.info("审核不通过-订单异常");
					map.put("msg", "false");
	 				return map;
				}				
				Subzh subzhEntity=null;
				if(fundLog != null){
					if(status!=null && status==6){
						try {
			 				fundLog.setStatus(6);/*状态   0是申请状态。1是成功 5：审核通过  6：拒绝*/
			 				fundLog.setModifytime(Calendar.getInstance().getTime());
			 				log.info("审核通过保存状态：" + agentzhfundLogMapper.updateByPrimaryKeySelective(fundLog));
			 			} catch (Exception efundlog) {
			 				log.error("保存预提现信息异常：" + efundlog);
			 				map.put("msg", "false");
			 				return map;
			 			}
						subzhEntity=subzhMapper.selectBySubzh(fundLog.getSubzh());
						//调柜台协议，把钱加到柜台账户
						log.info("提现审核拒绝后调入金协议");
						Map<String, Object> amap = addMoney(subzhEntity, fundLog);
						if(null!=amap&&amap.size()>0) {
							if(null!=amap.get("status")&& (Boolean)amap.get("status")){
								map.put("status", "true");
								log.info("将用户提现失败金额返回给客户成功");								
						   }
						}						
		 				return map;
					}
				}
			}			
		}catch(Exception e) {
			map.put("status", "false");
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
	
	//查询可用资金
	@Override
	public BigDecimal getfundbalance(Subzh subzh) {
		if(subzh!=null){
				try{
					log.info("调协议查询可用资金");
					String con="{\"client_id\":\""+subzh.getSubzh()+"\"}";
					log.info("log——con===》"+con);
					String rbStr = simulateStockService.commonFunction(con, 11103);//用户资金信息查询
					log.info("log——rbStr===》"+rbStr);
					if(rbStr==null||rbStr=="null" || rbStr.equals("")){
						rbStr="{\"status\":10,\"describe\":\"地址错误或网络错误\"}";
						JSONObject obj = JSONObject.parseObject(rbStr);
						log.info("log1——obj===》"+obj);
					}else if(rbStr.indexOf("{")<0){
						rbStr="{\"status\":11,\"describe\":\"返回信息出错\"}";
						JSONObject obj = JSONObject.parseObject(rbStr);
						log.info("log2——obj===》"+obj);
					}else{//正常情况下
						Map<String, Object> map=JSONObject.parseObject(rbStr);
						JSONObject data=(JSONObject) JSONObject.toJSON(map.get("data"));
						if(data==null) {
							log.info("调协议失败，data=null");
							return null;
						}
						log.info("log3——obj===》"+data+"enable_balance===》"+data.get("enable_balance"));
						if(map.containsKey("status") &&Integer.parseInt(map.get("status").toString())==0){
							log.info("调协议查询可用资金成功");
							BigDecimal fundbalance=new BigDecimal(String.valueOf(data.get("enable_balance")));
							subzh.setFundbalance(fundbalance.multiply(new BigDecimal(EXCHANGE_RATE)));
						}
					}
				}catch(Exception e){
					e.printStackTrace();
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
					log.info("{\"seq_no\":"+agentzhfundLog.getId()+",\"client_id\":\""+subzh.getSubzh()+"\",\"fund_type\":5,\"prev_fund\":0.00,\"after_fund\":"+fundbalance+
							",\"warning_line\":0.00,\"open_line\":0.00}"+"-------"+DefineNumber.JCL_PROTOCOL_ID_REQ_FUNDMESSAGE);
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
			    			map.put("status", false);
			    			map.put("msg", "调入金协议失败");
			    		}
					}else {
						log.info("调入金协议失败：" + rbStr);
		    			map.put("status", false);
		    			map.put("msg", "调入金协议失败");
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
	public Map<String, String> payMent(String username, String txmoney, String channel) {
		String dates = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		BigDecimal money = new BigDecimal(txmoney);
		money = money.multiply(new BigDecimal("100")).setScale(0);

		//String url = Constant.SPT_WG_PAY_URL;//网关支付地址
		String testKey = Constant.SPT_SECRTKEY;// 秘钥

		Map<String, String> payMap = new HashMap<String, String>();
		// 必填参数
		payMap.put("merch", Constant.SPT_MERCHART);// 商户号
		String orderno=dates + StringUtil.getRandomString(4)+username;
		payMap.put("orderno", orderno); // 商户订单号，查询时需要

		payMap.put("amount", money.toString()); // 金额，单位分
		payMap.put("tranchannel", "CMB"); // 银行渠道编码
		payMap.put("body", "充值");// 商品描述

		payMap.put("notifyurl",project_path+ "/subzhfund/notifySptPayMent");// 异步回调通知地址
		payMap.put("pageurl", project_path+ "/subzhfund/stockoptionJump?flag=1");// 同步回调通知地址
		payMap.put("comment", username);// 附加信息
		String sign = SignMd5Utils.createSign(payMap, testKey);// 签名
		payMap.put("sign", sign);// 签名串
		payMap.put("signtype", "MD5");
		AgentzhfundLog agentzhfundLog = new AgentzhfundLog();	
		saveAgentzhfundLog(agentzhfundLog, username, orderno, new Date(),  new BigDecimal(txmoney), 0, 1);
		
		return payMap;
	}

	//网关的充值回调
	@Override
	public Map<String, Object> notifySptPayMent(String requestBody) {
		log.info("充值回调开始");
		log.info("requestBody:" + JSONObject.parseObject(requestBody));
		JSONObject allStr =  JSONObject.parseObject(requestBody);
		String return_code = allStr.getString("return_code");//SUCCESS 代表成功，其它请参考错误代码
		String return_msg = allStr.getString("return_msg");//提示
		String merch_id = allStr.getString("merch_id");//商户号
		String trade_no = allStr.getString("trade_no");//平台交易号
		String order_no = allStr.getString("order_no");//商户订单号
		Integer total_fee = allStr.getInteger("total_fee");//总金额
		String pay_time = allStr.getString("pay_time");//支付完成时间，
		
		
		Map<String,Object> map = new HashMap<String,Object>();

		AgentzhfundLog afl=new AgentzhfundLog();
	    if ("SUCCESS".equals(return_code)){
	        	log.info("pay_time===>:"+pay_time);
	    		log.info("order_no===>:"+order_no);
	    		List<AgentzhfundLog> alist= null;
	    			try {
	    				alist = agentzhfundLogMapper.selectByorderno(order_no);
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
	    		//网关千分之6	    	
	    		afl.setFund(afl.getFund().multiply(new BigDecimal(0.994)));
	    		String	username=afl.getSubzh();
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
	    	    	if(null!=amap.get("status")&&(Boolean) amap.get("status")){
	    	    		afl.setStatus(1);//修改状态为1 --表示充值成功
	    	    		map.put("status", true);
	    	        	map.put("msg", "充值成功");
	    	        	FundPzlog fundPzlog = saveFundpzlog(username, afl.getFund(), order_no, DateUtil.getSystemDate(), 6);	    	
	    	    	}else{
	    	    		afl.setStatus(2);//修改状态为2 --表示充值失败	 
	    	    		map.put("status", false);
	    	        	map.put("msg", "充值失败");	    	        	
	    	   		 	if ((sfplist == null) && (sfplist.size() <= 1)) {
	    	   		 		sub.setZhstate(Integer.valueOf(0));
	    	   		 	}
	    	    	  }
	    	    	}else {
		    	    	afl.setStatus(2);//修改状态为2 --表示充值失败
		    	    	map.put("status", false);
			        	map.put("msg", "充值失败");
			        	if ((sfplist == null) && (sfplist.size() <= 1)) {
	    	   		 		sub.setZhstate(Integer.valueOf(0));
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
		return map;
	}

	//线下支付调账
		@Override
		public Map<String, Object> saveAccount(String username, String accmoney, Integer mstatus) {
			Map<String, Object> map=new HashMap<String, Object>();
			log.info(username+"线下调账");
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
			int status=mstatus==1?4:11;//--提现审核表 4表示调账入金成功 11表示调账出金成功
			int tstatus=mstatus==1?5:9;//--提现审核表 5表示调账入金失败 9表示调账出金失败
			log.info("bz===>"+bz+"<===>fundtype===>"+fundtype);

			String dates = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
			String orderno =  dates + StringUtil.getRandomString(4)+username;
				AgentzhfundLog afl=new AgentzhfundLog();
				afl.setAmount(accmoney);
				afl=saveAgentzhfundLog(afl,username, orderno, new Date(), total_fee, 0, bz);

				if(null!=afl&&afl.getId()!=null&&afl.getId()!=0) {
				
					//计算税率
					BigDecimal fund=afl.getFund();
					try {
						if(subzh.getIsadmin()==6) {
						log.info("调账调柜台协议开始");
						
						String  str1="{\"seq_no\":"+afl.getId()+",\"client_id\":\""+username+"\",\"fund_type\":5,\"prev_fund\":0.00,\"after_fund\":"+fund+
								",\"warning_line\":0.00,\"open_line\":0.00}";
						String  str2="{\"seq_no\":"+afl.getId()+",\"client_id\":\""+username+"\",\"fund_type\":1,\"prev_fund\":0.00,\"after_fund\":"+fund+
								",\"warning_line\":0.00,\"open_line\":0.00}";
						String  str=mstatus==1?str1:str2;
						log.info("传到柜台的参数"+str);
						String rbStr = simulateStockService.commonFunction(str,DefineNumber.JCL_PROTOCOL_ID_REQ_FUNDMESSAGE);
						log.info("柜台返回参数"+rbStr);
						if(!StringUtil.isAnyNullOrEmpty(rbStr) && rbStr.indexOf("{") > -1){
							JsonParser parser = new JsonParser();
							JsonElement element = parser.parse(rbStr);
							JsonObject jsonObj = element.getAsJsonObject();
				    		if(CONST.STATUS_INT_INSTANCE_SUCCESS == jsonObj.get("status").getAsInt()){
				    			log.info("调柜台协议成功");
				    			afl.setStatus(status);//调账成功
				    			saveFundpzlog(username, total_fee, orderno, DateUtil.getSystemDate(),fundtype);
				    			map.put("status", true);
				    			map.put("msg", username+"调账成功");
				    		} else{
				    			//调柜台协议失败
				    			log.info(jsonObj.get("describe"));
				    			map.put("status", false);
				    			map.put("msg", username+"调账失败："+jsonObj.get("describe"));
				    			afl.setStatus(tstatus);//调账失败
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
						}else {
							afl.setStatus(tstatus);//调账失败
							map.put("status", false);
			    			map.put("msg", "调账出现异常，请联系管理员");
						}
					}else {
						BigDecimal fundbalance = subzh.getFundbalance()==null?new BigDecimal(0.00):subzh.getFundbalance();
						if(mstatus==1){
							
							subzh.setFundbalance(fundbalance.add(total_fee));
						}else {
							
							subzh.setFundbalance(fundbalance.subtract(total_fee));
						}
						int count = subzhMapper.updateByPrimaryKeySelective(subzh);
						log.info("修改用户账户资金  :"+count);
						if(count>0){
							map.put("status", true);
			    			map.put("msg", "调账成功");
			    			afl.setStatus(status);
			    			saveFundpzlog(username, total_fee, orderno, DateUtil.getSystemDate(),fundtype);
						}else {
							afl.setStatus(tstatus);
							map.put("status", false);
			    			map.put("msg", username+"调账失败");
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
			
		}
			return map;
	}
	
	
}