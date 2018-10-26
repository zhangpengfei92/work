/**
 * Copyright (C) 2009 武汉金策略信息科技有限公司
 *
 * 版权所有。
 *
 * 类名　　  : @OrderController.java
 * 功能概要  : 
 * 做成日期  : @2018年5月3日
 * 修改日期  :
 */
package com.jcl.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jcl.comm.PaginationContext;
import com.jcl.mongodb.BeanUtil;
import com.jcl.mongodb.MongoUtils;
import com.jcl.mongodb.Page;
import com.jcl.pojo.Order;
import com.jcl.pojo.RiskLog;
import com.jcl.pojo.Subzh;
import com.jcl.service.RiskLogService;
import com.jcl.service.SubzhService;
import com.jcl.stock.entity.CONST;
import com.jcl.stock.simulate.DefineNumber;
import com.jcl.stock.simulate.service.SimulateStockService;
import com.jcl.util.Constant;
import com.jcl.util.DateUtil;
import com.jcl.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/** 
 * @author zpf
 * @version 1.0
 */
@Controller
@RequestMapping("/order")
public class OrderController {
	
	public static Logger log = Logger.getLogger(OrderController.class);
	
	private static final String RISK_STATE = "risk_state";
	private static final String RISK_LOG = "risk_log";
	private static final String SUB_CJ = "sub_cj";
	
	
	/*止损预警Service*/
	@Autowired
	private RiskLogService riskLogService;
	@Autowired
	private SubzhService subzhservice;
	
	/*交易Service*/
	@Autowired
    private SimulateStockService simulateStockService;
	
	@RequestMapping("/orderlist")
	public String queryOrderList(HttpSession session,Model model) {
		session.setAttribute(Constant.SESSION_LEFTMENU, "35");
		String username = (String) session.getAttribute(Constant.SESSION_LOGINNAME);
		Integer isAdmin=(Integer)session.getAttribute(Constant.SESSION_ISADMIN);
		String manage = (String) session.getAttribute(Constant.SESSION_AGENTZHGL);
		Subzh condition=new Subzh();
		if (isAdmin == 1 || isAdmin == 2) {// 如果是机构登录，那就查询条件中带机构
			if (isAdmin == 1) {
				condition.setManage(username);
			} else {
				condition.setAllocpt(username);
			}
			condition.setIsadmin(6);
			List<Subzh> jylist = subzhservice.getSubzhLevelList(condition);// 查普通交易用户集合
			model.addAttribute("jyList", jylist);

			condition.setIsadmin(3);
			List<Subzh> qdlist = subzhservice.getSubzhLevelList(condition);// 查渠道集合
			model.addAttribute("channelList", qdlist);

			condition.setIsadmin(4);
			List<Subzh> dlslist = subzhservice.getSubzhLevelList(condition);// 查代理商集合
			model.addAttribute("agentList", dlslist);

			condition.setIsadmin(5);
			List<Subzh> jjrlist = subzhservice.getSubzhLevelList(condition);// 查经纪人集合
			model.addAttribute("brokerList", jjrlist);

			/*
			 * if(qdlist!=null && qdlist.size()>0){ String qdsub=qdlist.get(0).getSubzh();
			 * model.addAttribute("channel", qdsub); condition.setAllocchannel(qdsub);
			 * condition.setIsadmin(4); List<Subzh>
			 * dlslist=subzhservice.getSubzhLevelList(condition);//查代理商集合
			 * model.addAttribute("agentList", dlslist); if(dlslist!=null &&
			 * dlslist.size()>0){ String dlssub=dlslist.get(0).getSubzh();
			 * condition.setAllocagent(dlssub); condition.setIsadmin(5); List<Subzh>
			 * jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
			 * model.addAttribute("brokerList", jjrlist);
			 * 
			 * } }
			 */

		} else if (isAdmin == 3) {// 渠道登录后，需要加载代理商，经纪人集合
			condition.setAllocchannel(username);

			condition.setIsadmin(6);
			List<Subzh> jylist = subzhservice.getSubzhLevelList(condition);// 查经纪人集合
			model.addAttribute("jyList", jylist);

			condition.setIsadmin(4);
			List<Subzh> dlslist = subzhservice.getSubzhLevelList(condition);// 查代理商集合
			model.addAttribute("agentList", dlslist);
			/*
			 * if(dlslist!=null && dlslist.size()>0){ String
			 * dlssub=dlslist.get(0).getSubzh(); condition.setAllocagent(dlssub);
			 * condition.setIsadmin(5); List<Subzh>
			 * jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
			 * model.addAttribute("brokerList", jjrlist);
			 * 
			 * }
			 */
			condition.setIsadmin(5);
			List<Subzh> jjrlist = subzhservice.getSubzhLevelList(condition);// 查经纪人集合
			model.addAttribute("brokerList", jjrlist);
		} else if (isAdmin == 4) {// 代理商登录时，需要加载经纪人下拉框数据
			condition.setAllocagent(username);

			condition.setIsadmin(6);
			List<Subzh> jylist = subzhservice.getSubzhLevelList(condition);// 查普通交易用户集合
			model.addAttribute("jyList", jylist);

			condition.setIsadmin(5);
			List<Subzh> jjrlist = subzhservice.getSubzhLevelList(condition);// 查经纪人集合
			model.addAttribute("brokerList", jjrlist);

		} else if (isAdmin == 5) {// 经纪人登录时，需要加载交易用户下拉框数据
			List<Subzh> jjrlist = new ArrayList<Subzh>();
			Subzh jjr = subzhservice.getPuriSubzh(username);
			jjrlist.add(jjr);
			model.addAttribute("brokerList", jjrlist);
			if (jjrlist != null && jjrlist.size() > 0) {
				condition.setAllocbroker(jjrlist.get(0).getSubzh());
				condition.setIsadmin(6);
				List<Subzh> jylist = subzhservice.getSubzhLevelList(condition);// 查经纪人集合
				model.addAttribute("jyList", jylist);
			}
		}
		model.addAttribute("isadmin", isAdmin);
		return "fund/orderlist";	
	} 
	
	
	
	
	@RequestMapping("/queryPage")
	@ResponseBody
	public Map<String,Object> queryPage(Model model,Integer pagenum,String subzh) {
		Map<String,Object> map=new HashMap<String,Object>();
		try {
			Page<Order> userpage=new Page<Order>();
			userpage.setPagenum(pagenum);
			userpage.setInitialSize(pagenum);
			MongoUtils.change("SUB_CJ");
			List<Order> olist=new ArrayList<Order>();
			DBCursor cursor ;
			if(subzh==null||subzh=="") {
			subzh="";
			cursor = MongoUtils.queryPage(1, 10);
			}else {
				DBObject documents=new BasicDBObject("sub_zh",subzh);
				cursor = MongoUtils.queryPageByExample(userpage.getInitialSize(), userpage.getPagesize(),documents);
			}
			while(cursor.hasNext()) {
				Order order=new Order();
				DBObject object = cursor.next();
				olist.add(BeanUtil.dbObject2Bean(object, order));
			}
			Order order1=new Order();
			DBObject dbObject = BeanUtil.bean2DBObject(order1);
			Long count = MongoUtils.count(dbObject);
			userpage.setCountsize(count);
			//userpage.setPagecount(count);
			userpage.setList(olist);
			model.addAttribute("olist", olist);
			model.addAttribute("subzh", subzh);
			model.addAttribute("userpage", userpage);
			map.put("data", userpage);
			map.put("status", 0);
		} catch (Exception e) {
			log.error("查询账户列表异常："+e.getMessage());
			map.put("status", 1);
			map.put("describe", "查询用户账户异常");
			e.printStackTrace();
		}
		return map;	
	} 
	
	/*止损平仓预警*/
	@RequestMapping("/closeOutList")
	public String closeOutList(HttpSession session, Model model) {
		session.setAttribute(Constant.SESSION_LEFTMENU, "17");
		try {
			Page userpage = new Page();
			MongoUtils.change(RISK_STATE);
			userpage.setInitialSize(userpage.getPagenum());
			DBCursor cursor = MongoUtils.queryPage(userpage.getInitialSize(), userpage.getPagesize());
			/*数据集合*/
			List<DBObject> pageData = new ArrayList<DBObject>();
			BasicDBObject dbObj = null;
			while(cursor.hasNext()) {
				dbObj = (BasicDBObject) cursor.next();
				String subzh = dbObj.getString("subzh");//子账号
				boolean can_buy = dbObj.getBoolean("can_buy");
				boolean can_sell = dbObj.getBoolean("can_sell");
				boolean can_cancel = dbObj.getBoolean("can_cancel");
				//int time = dbObj.getInt("time");//更新时间
				dbObj.put("subzh", subzh);
				dbObj.put("total_assets", dbObj.getDouble("total_assets"));
				dbObj.put("market_value", dbObj.getDouble("market_value"));
				dbObj.put("warning_line", dbObj.getDouble("warning_line"));
				dbObj.put("close_line", dbObj.getDouble("close_line"));
				dbObj.put("risk_rate", dbObj.getInt("risk_state"));//风险率
				dbObj.put("risk_state", dbObj.getInt("risk_state"));
				if(can_buy){
					dbObj.put("can_buy", 1);//是否禁买  是
				} else {
					dbObj.put("can_buy", 2);
				}
				if(can_sell){
					dbObj.put("can_sell", 1);//是否禁卖  是
				} else {
					dbObj.put("can_sell", 2);
				}
				if(can_cancel){
					dbObj.put("can_cancel", 1);//是否禁撤  是
				} else {
					dbObj.put("can_cancel", 2);
				}
				dbObj.put("currency", dbObj.getString("currency"));
				long time = dbObj.getLong("time");
				if(time > 0){
					dbObj.put("timeShow", DateUtil.dateTimeToString(new Date(time*1000)));
				}
				
				pageData.add(dbObj);
			}
			
			DBObject dbObject = new BasicDBObject();
			Long count = MongoUtils.count(dbObject);
			userpage.setCountsize(count);
			userpage.setPagecount(count);
			
			model.addAttribute("olist", pageData);
			model.addAttribute("userpage", userpage);
		} catch (Exception e) {
			log.error("查询账户列表异常："+e.getMessage());
		}
		return "fund/closeOutList";	
	} 
	
	@RequestMapping("/queryPageCloseOut")
	public String queryPageCloseOut(Model model, Integer pagenum, String subzh) {
		
		try {
			Page userpage = new Page();
			//userpage.setPagenum(pagenum);
			userpage.setPagenum(pagenum);
			userpage.setInitialSize(PaginationContext.getPageSize());
			MongoUtils.change(RISK_STATE);
			/*数据集合*/
			List<DBObject> pageData = new ArrayList<DBObject>();
			DBCursor cursor ;
			if(StringUtil.isAnyNullOrEmpty(subzh)) {
				subzh="";
				cursor = MongoUtils.queryPage((pagenum-1)*PaginationContext.getPageSize(), PaginationContext.getPageSize());
			} else {
				DBObject documents = new BasicDBObject("subzh",subzh);
				cursor = MongoUtils.queryPageByExample((pagenum-1)*PaginationContext.getPageSize(), PaginationContext.getPageSize(),documents);
			}
			
			BasicDBObject dbObj = null;
			while(cursor.hasNext()) {
				dbObj = (BasicDBObject) cursor.next();
				boolean can_buy = dbObj.getBoolean("can_buy");
				boolean can_sell = dbObj.getBoolean("can_sell");
				boolean can_cancel = dbObj.getBoolean("can_cancel");
				//int time = dbObj.getInt("time");//更新时间
				dbObj.put("subzh", dbObj.getString("subzh"));
				dbObj.put("total_assets", dbObj.getDouble("total_assets"));
				dbObj.put("market_value", dbObj.getDouble("market_value"));
				dbObj.put("warning_line", dbObj.getDouble("warning_line"));
				dbObj.put("close_line", dbObj.getDouble("close_line"));
				dbObj.put("risk_rate", dbObj.getInt("risk_state"));//风险率
				dbObj.put("risk_state", dbObj.getInt("risk_state"));
				if(can_buy){
					dbObj.put("can_buy", 1);//是否禁买  是
				} else {
					dbObj.put("can_buy", 2);
				}
				if(can_sell){
					dbObj.put("can_sell", 1);//是否禁卖  是
				} else {
					dbObj.put("can_sell", 2);
				}
				if(can_cancel){
					dbObj.put("can_cancel", 1);//是否禁撤  是
				} else {
					dbObj.put("can_cancel", 2);
				}
				dbObj.put("currency", dbObj.getString("currency"));
				long time = dbObj.getLong("time");
				if(time > 0){
					dbObj.put("timeShow", DateUtil.dateTimeToString(new Date(time*1000)));
				}
				
				pageData.add(dbObj);
			}
			DBObject dbObject = new BasicDBObject();
			Long count = MongoUtils.count(dbObject);
			userpage.setCountsize(count);
			userpage.setPagecount(count);
			
			model.addAttribute("olist", pageData);
			model.addAttribute("subzh", subzh);
			model.addAttribute("userpage", userpage);
		} catch (Exception e) {
			log.error("查询账户列表异常："+e.getMessage());
		}
		return "fund/closeOutList";	
	}
	
	
	//一键平仓
	@RequestMapping("/reinstate")
	@ResponseBody
	public String reinstate(String subzh) {
		String flag = "false";
		String rbStr = "";
		try {
			rbStr = simulateStockService.commonFunction("{\"client_id\":\""+subzh+"\"}", DefineNumber.JCL_PROTOCOL_ID_REQ_SUBZHSELLDEAL);
			if(!StringUtil.isAnyNullOrEmpty(rbStr) && rbStr.indexOf("{") > -1){
				JsonObject jsonObj = null;
				JsonParser parser = new JsonParser();
				JsonElement element = parser.parse(rbStr);
				jsonObj = element.getAsJsonObject();
				
	    		if(CONST.STATUS_INT_INSTANCE_SUCCESS == jsonObj.get("status").getAsInt()){
	    			flag = "true";
	    			/*保存账户一键平仓记录*/
	    			try {
	    				String agentzh = "1";
	    				RiskLog riskLog = new RiskLog();
	    				riskLog.setUserno(subzh);
	    				riskLog.setWarnTime(Calendar.getInstance().getTime());//时间
	    				riskLog.setWarnno(String.valueOf(System.currentTimeMillis()));//预警编号
	    				riskLog.setUserType("4");//普通账户
	    				riskLog.setWarnType("CNY");
	    				riskLog.setWarnDetail("管理员" + agentzh + "对" + subzh + "执行一键平仓");//预警明细
	    				riskLog.setAgentzh(agentzh);
	    				riskLog.setUserid(1l);//子账户管理员
	    				log.info("保存一键平仓记录状态：" + riskLogService.insertSelective(riskLog, null));
	    			} catch (Exception erisk) {
						log.error("保存账户一键平仓记录异常：" + erisk.getMessage());
					}
	    		} else {
	    			flag = jsonObj.get("describe").getAsString();
	    		}
			} else {
				flag = "柜台服务异常";
			}
		} catch (Exception e) {
			log.error("子账户执行一键平仓异常：" + rbStr + e.getMessage());
		}
		return flag;
	}
	
	/*预警记录*/
	@RequestMapping("/riskLogList")
	public String riskLogList(HttpSession session, Model model) {
		session.setAttribute(Constant.SESSION_LEFTMENU, "17");
		try {
			Page userpage = new Page();
			MongoUtils.change(RISK_LOG);
			userpage.setPagenum(PaginationContext.getPageNum());
			userpage.setInitialSize(PaginationContext.getPageSize());
			DBCursor cursor = MongoUtils.queryPage((PaginationContext.getPageNum()-1)*PaginationContext.getPageSize(), PaginationContext.getPageSize());
			/*数据集合*/
			List<DBObject> pageData = new ArrayList<DBObject>();
			BasicDBObject dbObj = null;
			while(cursor.hasNext()) {
				dbObj = (BasicDBObject) cursor.next();
				String subzh = dbObj.getString("subzh");//子账号
				boolean can_buy = dbObj.getBoolean("can_buy");
				boolean can_sell = dbObj.getBoolean("can_sell");
				boolean can_cancel = dbObj.getBoolean("can_cancel");
				//int time = dbObj.getInt("time");//更新时间
				dbObj.put("subzh", subzh);
				dbObj.put("total_assets", dbObj.getDouble("total_assets"));
				dbObj.put("market_value", dbObj.getDouble("market_value"));
				dbObj.put("warning_line", dbObj.getDouble("warning_line"));
				dbObj.put("close_line", dbObj.getDouble("close_line"));
				dbObj.put("risk_rate", dbObj.getInt("risk_state"));//风险率
				dbObj.put("risk_state", dbObj.getInt("risk_state"));
				if(can_buy){
					dbObj.put("can_buy", 1);//是否禁买  是
				} else {
					dbObj.put("can_buy", 2);
				}
				if(can_sell){
					dbObj.put("can_sell", 1);//是否禁卖  是
				} else {
					dbObj.put("can_sell", 2);
				}
				if(can_cancel){
					dbObj.put("can_cancel", 1);//是否禁撤  是
				} else {
					dbObj.put("can_cancel", 2);
				}
				dbObj.put("currency", dbObj.getString("currency"));
				long time = dbObj.getLong("time");
				if(time > 0){
					dbObj.put("timeShow", DateUtil.dateTimeToString(new Date(time*1000)));
				}
				
				pageData.add(dbObj);
			}
			
			DBObject dbObject = new BasicDBObject();
			Long count = MongoUtils.count(dbObject);
			log.info("预警记录mongodb查询..." + count);
			userpage.setCountsize(count);
			userpage.setPagecount(count);
			
			if(count > 0){
				model.addAttribute("olist", pageData);
				model.addAttribute("userpage", userpage);
			}
		} catch (Exception e) {
			log.error("查询账户列表异常："+e.getMessage());
		}
		return "fund/riskLogList";	
	} 
	
	@RequestMapping("/queryPageRiskStock")
	public String queryPageRiskStock(Model model, Integer pagenum, String subzh) {
		
		try {
			Page userpage = new Page();
			//userpage.setPagenum(pagenum);
			userpage.setPagenum(pagenum);
			userpage.setInitialSize(PaginationContext.getPageSize());
			MongoUtils.change(RISK_LOG);
			/*数据集合*/
			List<DBObject> pageData = new ArrayList<DBObject>();
			DBCursor cursor ;
			if(StringUtil.isAnyNullOrEmpty(subzh)) {
				subzh="";
				cursor = MongoUtils.queryPage((pagenum-1)*PaginationContext.getPageSize(), PaginationContext.getPageSize());
			} else {
				DBObject documents = new BasicDBObject("subzh",subzh);
				cursor = MongoUtils.queryPageByExample((pagenum-1)*PaginationContext.getPageSize(), PaginationContext.getPageSize(),documents);
			}
			
			BasicDBObject dbObj = null;
			while(cursor.hasNext()) {
				dbObj = (BasicDBObject) cursor.next();
				boolean can_buy = dbObj.getBoolean("can_buy");
				boolean can_sell = dbObj.getBoolean("can_sell");
				boolean can_cancel = dbObj.getBoolean("can_cancel");
				//int time = dbObj.getInt("time");//更新时间
				dbObj.put("subzh", dbObj.getString("subzh"));
				dbObj.put("total_assets", dbObj.getDouble("total_assets"));
				dbObj.put("market_value", dbObj.getDouble("market_value"));
				dbObj.put("warning_line", dbObj.getDouble("warning_line"));
				dbObj.put("close_line", dbObj.getDouble("close_line"));
				dbObj.put("risk_rate", dbObj.getInt("risk_state"));//风险率
				dbObj.put("risk_state", dbObj.getInt("risk_state"));
				if(can_buy){
					dbObj.put("can_buy", 1);//是否禁买  是
				} else {
					dbObj.put("can_buy", 2);
				}
				if(can_sell){
					dbObj.put("can_sell", 1);//是否禁卖  是
				} else {
					dbObj.put("can_sell", 2);
				}
				if(can_cancel){
					dbObj.put("can_cancel", 1);//是否禁撤  是
				} else {
					dbObj.put("can_cancel", 2);
				}
				dbObj.put("currency", dbObj.getString("currency"));
				long time = dbObj.getLong("time");
				if(time > 0){
					dbObj.put("timeShow", DateUtil.dateTimeToString(new Date(time*1000)));
				}
				
				pageData.add(dbObj);
			}
			DBObject dbObject = new BasicDBObject();
			Long count = MongoUtils.count(dbObject);
			userpage.setCountsize(count);
			userpage.setPagecount(count);
			
			model.addAttribute("olist", pageData);
			model.addAttribute("subzh", subzh);
			model.addAttribute("userpage", userpage);
		} catch (Exception e) {
			log.error("查询账户列表异常："+e.getMessage());
		}
		return "fund/riskLogList";	
	}
	
}
