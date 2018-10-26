/**
 * Copyright (C) 2009 武汉金策略信息科技有限公司
 *
 * 版权所有。
 *
 * 类名　　  : FundPzLogController.java
 * 功能概要  : 资金记录信息 管理Controller类
 * 做成日期  : 2018年04月16日 
 * 修改日期  :
 */
package com.jcl.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jcl.comm.PaginationContext;
import com.jcl.pojo.FundPzlog;
import com.jcl.pojo.FundPzlogExample;
import com.jcl.pojo.FundPzlogExample.Criteria;
import com.jcl.pojo.FundSettlelog;
import com.jcl.pojo.FundSettlelogExample;
import com.jcl.pojo.FundSumlog;
import com.jcl.pojo.Subzh;
import com.jcl.pojo.SubzhExample;
import com.jcl.pojo.TradeParam;
import com.jcl.service.FundPzLogService;
import com.jcl.service.FundSettlelogService;
import com.jcl.service.SubzhService;
import com.jcl.util.Constant;
import com.jcl.util.DateUtil;
import com.jcl.util.MyExcelUtil;
import com.jcl.util.StringUtil;

/**
 * 资金记录信息 管理类
 * 
 * @author jiangzq
 * @version 1.0
 */
@Controller
@RequestMapping("/fundPzlog")
public class FundPzLogController {
	
	public static Logger log = Logger.getLogger(FundPzLogController.class);
	
	private FundPzlog fundPzlog;
	//private Object synObj = "127";
	
	/*提现记录信息管理service*/
	@Autowired
	private FundPzLogService fundPzLogService;
	
	/*销售报表记录信息管理service*/
	@Autowired
	private FundSettlelogService fundSettlelogService;
	
	/*销售统计报表记录信息管理service*/
	//@Autowired
	//private FundSumlogService fundSumlogService;
	
	@Autowired
	private SubzhService subzhservice;
	
	/*子账户信息管理service*/
	/*@Autowired
	private SubzhService subzhService;*/

	// 资金流水管理页面跳转
	@RequestMapping("/fundPzLogList")
	public String fundPzLogList(HttpSession session, Model model,String nickname) {
		session.setAttribute(Constant.SESSION_LEFTMENU, "4");
		List<String> strlist=new ArrayList<String>();
		List<Subzh> sublist=new ArrayList<Subzh>();
		try {
			String username = (String) session.getAttribute(Constant.SESSION_LOGINNAME);
			Integer isAdmin = (Integer)session.getAttribute(Constant.SESSION_ISADMIN);
			//String manage = (String) session.getAttribute(Constant.SESSION_AGENTZHGL);
			Subzh condition = new Subzh();
			if(isAdmin==1 || isAdmin==2){//如果是机构登录，那就查询条件中带机构
				condition.setIsadmin(3);
				if(isAdmin==1){
					condition.setManage(username);
				}else{
					condition.setAllocpt(username);
				}
				List<Subzh> qdlist=subzhservice.getSubzhLevelList(condition);//查渠道集合
				model.addAttribute("channelList", qdlist);
				if(qdlist!=null && qdlist.size()>0){
					String qdsub=qdlist.get(0).getSubzh();
					condition.setAllocchannel(qdsub);
					condition.setIsadmin(4);
					List<Subzh> dlslist=subzhservice.getSubzhLevelList(condition);//查代理商集合
					model.addAttribute("agentList", dlslist);
					if(dlslist!=null && dlslist.size()>0){
						String dlssub=dlslist.get(0).getSubzh();
						condition.setAllocagent(dlssub);
						condition.setIsadmin(5);
						List<Subzh> jjrlist=subzhservice.getSubzhLevelList(condition);//查代理商集合
						model.addAttribute("brokerList", jjrlist);
					}
				}
				
				Subzh condition2 = new Subzh();
				condition2.setIsadmin(6);
				sublist=subzhservice.getSubzhLevelList(condition2);
				
			}else if(isAdmin==3){//渠道登录后，需要加载代理商，经纪人集合
				condition.setAllocchannel(username);
				condition.setIsadmin(4);
				List<Subzh> dlslist=subzhservice.getSubzhLevelList(condition);//查代理商集合
				model.addAttribute("agentList", dlslist);
				if(dlslist!=null && dlslist.size()>0){
					String dlssub=dlslist.get(0).getSubzh();
					condition.setAllocagent(dlssub);
					condition.setIsadmin(5);
					List<Subzh> jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
					model.addAttribute("brokerList", jjrlist);
				}
				Subzh condition2 = new Subzh();
				condition2.setIsadmin(6);
				condition2.setAllocchannel(username);
				sublist=subzhservice.getSubzhLevelList(condition2);
			}else if(isAdmin==4){//代理商登录时，需要加载经纪人下拉框数据
				condition.setAllocagent(username);
				condition.setIsadmin(5);
				List<Subzh> jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
				model.addAttribute("brokerList", jjrlist);
				
				Subzh condition2 = new Subzh();
				condition2.setIsadmin(6);
				condition2.setAllocagent(username);
				sublist=subzhservice.getSubzhLevelList(condition2);
			}else{
				Subzh condition2 = new Subzh();
				condition2.setIsadmin(6);
				condition2.setAllocbroker(username);
				sublist=subzhservice.getSubzhLevelList(condition2);
			}
		} catch (Exception es) {
			log.error("查询渠道、经纪人列表数据异常：" + es.getMessage());
		}
		try {
			if(sublist!=null && sublist.size()>0){
				for(Subzh su:sublist){
					strlist.add(su.getSubzh());
				}
			}
			PageHelper.startPage(PaginationContext.getPageNum(), PaginationContext.getPageSize());
			//List<FundPzLog> userlist = FundPzLogservice.selectAll();
			FundPzlogExample example = new FundPzlogExample();
			example.setOrderByClause(" id desc, createtime desc");
			Criteria criteria = example.createCriteria();
			if(StringUtil.isAnyNullOrEmpty(nickname)){
				criteria.andSubzhIn(strlist);
			}else{
				criteria.andSubzhLike(nickname);
			}
			//criteria.andTypeEqualTo("3");//
			//criteria.andStatusGreaterThan((byte)2);//状态   2:预提现  3：提现成功   4:提现申请  5：审核通过  6：拒绝
			List<FundPzlog> userlist = fundPzLogService.selectByExample(example);
			PageInfo<FundPzlog> userpage = new PageInfo<FundPzlog>(userlist);
			if(userlist != null && userlist.size() > 0){
				//List<RightsRole> roles = null;
				/*查询权限列表*/
				/*try {
					roles = rightsRoleService.getListByLikeName(null);
					model.addAttribute("rightsRoles", roles);
				} catch (Exception e1) {
					log.error("查询权限列表异常：" + e1.getMessage());
				}*/
				/*boolean roleFlag = false;
				if(roles != null && roles.size() > 0){
					roleFlag = true;
				}*/
				/*for(FundPzLog FundPzLog : userlist){
					if(roleFlag){
						for(RightsRole rightsRole : roles){
							if(rightsRole.getId().equals(FundPzLog.getUsertype())){
								FundPzLog.setFuncinfo(rightsRole.getRolename());
								break;
							}
						}
					}
					if(FundPzLog.getStarttime() != null){//起始时间显示
						FundPzLog.setSyncinfo(DateUtil.dateToString(FundPzLog.getStarttime()));
					} else {
						FundPzLog.setSyncinfo("--");
					}
					if(FundPzLog.getEndtime() != null){//结束时间显示
						FundPzLog.setClientfrom(DateUtil.dateToString(FundPzLog.getEndtime()));
					} else {
						FundPzLog.setClientfrom("--");
					}
				}*/
			}
			model.addAttribute("userlist", userlist);
			model.addAttribute("userpage", userpage);
		} catch (Exception e) {
			log.error("查询提现记录列表异常：" + e.getMessage());
		}
		return "fund/fundPzLogList";
	}

	// 资金流水列表模糊查询
	@RequestMapping("/selectPage")
	public String selectPage(String nickname, String fundType, Model model,HttpSession session) throws UnsupportedEncodingException {
		List<String> strlist=new ArrayList<String>();
		List<Subzh> sublist=new ArrayList<Subzh>();
		try {
			String username = (String) session.getAttribute(Constant.SESSION_LOGINNAME);
			Integer isAdmin = (Integer)session.getAttribute(Constant.SESSION_ISADMIN);
			//String manage = (String) session.getAttribute(Constant.SESSION_AGENTZHGL);
			Subzh condition = new Subzh();
			if(isAdmin==1 || isAdmin==2){//如果是机构登录，那就查询条件中带机构
				condition.setIsadmin(6);
				sublist=subzhservice.getSubzhLevelList(condition);
				
			}else if(isAdmin==3){//渠道登录后，需要加载代理商，经纪人集合
				
				condition.setIsadmin(6);
				condition.setAllocchannel(username);
				sublist=subzhservice.getSubzhLevelList(condition);
			}else if(isAdmin==4){//代理商登录时，需要加载经纪人下拉框数据
				condition.setIsadmin(6);
				condition.setAllocagent(username);
				sublist=subzhservice.getSubzhLevelList(condition);
			}else{
				condition.setIsadmin(6);
				condition.setAllocbroker(username);
				sublist=subzhservice.getSubzhLevelList(condition);
			}
		} catch (Exception es) {
			log.error("查询渠道、经纪人列表数据异常：" + es.getMessage());
		}
		try {
			if(sublist!=null && sublist.size()>0){
				for(Subzh su:sublist){
					strlist.add(su.getSubzh());
				}
			}
			
			PageHelper.startPage(PaginationContext.getPageNum(), PaginationContext.getPageSize());
			FundPzlogExample example = new FundPzlogExample();
			example.setOrderByClause(" id desc, createtime desc");
			Criteria criteria = example.createCriteria();
			//criteria.andStatusGreaterThan((byte)2);//状态   2:预提现  3：提现成功   4:提现申请  5：审核通过  6：拒绝
			if(StringUtil.isAnyNullOrEmpty(nickname)){
				criteria.andSubzhIn(strlist);
			}else{
				criteria.andSubzhLike(nickname);
			}
			if(!StringUtil.isAnyNullOrEmpty(fundType)){
				criteria.andFundtypeEqualTo(Integer.parseInt(fundType));
			}

			List<FundPzlog> userlist = fundPzLogService.selectByExample(example);
			PageInfo<FundPzlog> userpage = new PageInfo<FundPzlog>(userlist);
			if(userlist != null && userlist.size() > 0){
				/*for(FundPzLog FundPzLog : userlist){
					if(roleFlag){
						for(RightsRole rightsRole : roles){
							if(rightsRole.getId().equals(FundPzLog.getUsertype())){
								FundPzLog.setFuncinfo(rightsRole.getRolename());
								break;
							}
						}
					}
					if(FundPzLog.getStarttime() != null){//起始时间显示
						FundPzLog.setSyncinfo(DateUtil.dateToString(FundPzLog.getStarttime()));
					} else {
						FundPzLog.setSyncinfo("--");
					}
					if(FundPzLog.getEndtime() != null){//结束时间显示
						FundPzLog.setClientfrom(DateUtil.dateToString(FundPzLog.getEndtime()));
					} else {
						FundPzLog.setClientfrom("--");
					}
				}*/
			}
			model.addAttribute("fundType", fundType);
			model.addAttribute("nickname", nickname);
			model.addAttribute("userlist", userlist);
			model.addAttribute("userpage", userpage);
		} catch (Exception e) {
			log.error("查询提现记录列表异常：" + e.getMessage());
		}
		return "fund/fundPzLogList";
	}

	// 编辑资金流水
	@RequestMapping("/editFundPzLog")
	public String editFundPzLog(Integer id, Model model) {
		try {
			/*List<RightsRole> roles = rightsRoleService.getListByLikeName(null);
			model.addAttribute("rightsRoles", roles);*/
			FundPzlog user = fundPzLogService.selectByPrimaryKey(id);
			/*if(user.getStarttime() != null){
				model.addAttribute("starttimeStr", DateUtil.dateToString(user.getStarttime()));
			}
			if(user.getEndtime() != null){
				model.addAttribute("endtimeStr", DateUtil.dateToString(user.getEndtime()));
			}*/
			model.addAttribute("user", user);
		} catch (Exception e) {
			log.error("获取权限列表异常：" + e.getMessage());
		}
		return "fund/addFundPzLog";
	}
	
	// 新增资金流水
	@RequestMapping("/addFundPzLog")
	public String addFundPzLog(Integer id, Model model) {
		try {
			/*List<RightsRole> roles = rightsRoleService.getListByLikeName(null);
			model.addAttribute("rightsRoles", roles);*/
			FundPzlog user = fundPzLogService.selectByPrimaryKey(id);
			model.addAttribute("user", user);
		} catch (Exception e) {
			log.error("获取权限列表异常：" + e.getMessage());
		}
		return "fund/addFundPzLog";
	}
	
	// 编辑资金流水
	@RequestMapping("/editFundPzLogMsg")
	@ResponseBody
	public String editFundPzLogMsg(FundPzlog user, String starttimeStr, String endtimeStr, Integer id) {
		
		return "false";
	}

	// 批量删除
	@RequestMapping("/deleteFundPzLogList")
	@ResponseBody
	public String deleteFundPzLogList(Integer[] ids) {
		if (ids != null) {
			/*for (Integer id : ids) {
				try {
					//FundPzLog user = FundPzLogservice.selectByPrimaryKey(id);
					//user.setStatus(0);
					//i = FundPzLogservice.updateByPrimaryKey(user);
					//FundPzLogservice.deleteByPrimaryKey(id);
				} catch (Exception e) {
					log.error("删除提现记录异常：" + e.getMessage());
					return "false";
				}
			}*/
		}
		return "true";
	}

	// 单个删除
	@RequestMapping("/deleteFundPzLog")
	@ResponseBody
	public String deleteFundPzLog(Integer id) {
		try {
			fundPzLogService.deleteByPrimaryKey(id);
		} catch (Exception e) {
			log.error("删除提现记录异常：" + e.getMessage());
			return "false";
		}
		return "true";
	}
	
	// 跳转手续费分成报表
	@RequestMapping("/fundSettlelogList")
	public String fundSettlelogList(HttpSession session, Model model) {
		session.setAttribute(Constant.SESSION_LEFTMENU, "15");
		try {
			Map<String, String> nameMap = new HashMap<String, String>();
			SubzhExample exampleSubzh = new SubzhExample();
			com.jcl.pojo.SubzhExample.Criteria criteriaSubzh = exampleSubzh.createCriteria();
			criteriaSubzh.andIsadminLessThan(6);
			List<Subzh> qdlistMap = subzhservice.selectByExample(exampleSubzh);
			if(qdlistMap != null && qdlistMap.size()>0){
				for(Subzh subzhTemp : qdlistMap){
					nameMap.put(subzhTemp.getSubzh(), subzhTemp.getName());
				}
			}
			
			Integer isAdmin = (Integer)session.getAttribute(Constant.SESSION_ISADMIN);
			String username = (String) session.getAttribute(Constant.SESSION_LOGINNAME);
			//Subzh subzh = subzhservice.selectBySubzh(username);
			Subzh subzh = new Subzh();
			if(isAdmin==1){//如果是机构登录，那就查询条件中带机构
				subzh.setManage(username);
			}else if(isAdmin==2){//如果是平台登录，那就查询条件中带平台
				subzh.setAllocpt(username);
			}else if(isAdmin==3){//如果是渠道登录，那就查询条件中带渠道
				subzh.setAllocchannel(username);
			}else if(isAdmin==4){//如果是代理商登录，那就查询条件中带代理商
				subzh.setAllocagent(username);
			}else if(isAdmin==5){//如果是经纪人登录，那就查询条件中带经纪人
				subzh.setAllocbroker(username);
			}
			subzh.setIsadmin(6);
			Subzh condition=new Subzh();
			if(isAdmin==1 || isAdmin==2){//如果是机构登录，那就查询条件中带机构
				condition.setIsadmin(3);
				if(isAdmin==1){
					condition.setManage(username);
				}else{
					condition.setAllocpt(username);
				}
				if(subzh.getIsadmin()>3){
					List<Subzh> qdlist=subzhservice.getSubzhLevelList(condition);//查渠道集合
					model.addAttribute("channelList", qdlist);
					if(qdlist!=null && qdlist.size()>0){
						
						String qdsub=qdlist.get(0).getSubzh();
						if(!StringUtil.isAnyNullOrEmpty(subzh.getAllocchannel())){
							qdsub=subzh.getAllocchannel();
						}
						if(subzh.getIsadmin()>4){
							condition.setAllocchannel(qdsub);
							condition.setIsadmin(4);
							List<Subzh> dlslist=subzhservice.getSubzhLevelList(condition);//查代理商集合
							model.addAttribute("agentList", dlslist);
							if(subzh.getIsadmin()==6 && dlslist!=null && dlslist.size()>0){
								String dlssub=dlslist.get(0).getSubzh();
								if(!StringUtil.isAnyNullOrEmpty(subzh.getAllocagent())){
									dlssub=subzh.getAllocagent();
								}
								condition.setAllocagent(dlssub);
								condition.setIsadmin(5);
								List<Subzh> jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
								model.addAttribute("brokerList", jjrlist);
							}
						}
						
					}
				}
			}else if(isAdmin==3){//渠道登录后，需要加载代理商，经纪人集合
				condition.setAllocchannel(username);
				condition.setIsadmin(4);
				if(subzh.getIsadmin()>4){
					List<Subzh> dlslist=subzhservice.getSubzhLevelList(condition);//查代理商集合
					model.addAttribute("agentList", dlslist);
					if(subzh.getIsadmin()==6 && dlslist!=null && dlslist.size()>0){
						String dlssub=dlslist.get(0).getSubzh();
						if(!StringUtil.isAnyNullOrEmpty(subzh.getAllocagent())){
							dlssub=subzh.getAllocagent();
						}
						condition.setAllocagent(dlssub);
						condition.setIsadmin(5);
						List<Subzh> jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
						model.addAttribute("brokerList", jjrlist);
					}
				}
				
			}else if(isAdmin==4){//代理商登录时，需要加载经纪人下拉框数据
				condition.setAllocagent(username);
				condition.setIsadmin(5);
				if(subzh.getIsadmin()==6){
					List<Subzh> jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
					model.addAttribute("brokerList", jjrlist);
				}
			}
			
			PageHelper.startPage(PaginationContext.getPageNum(), PaginationContext.getPageSize());
			//List<FundPzLog> userlist = FundPzLogservice.selectAll();
			FundSettlelogExample example = new FundSettlelogExample();
			example.setOrderByClause(" id desc, createtime desc");
			com.jcl.pojo.FundSettlelogExample.Criteria criteria = example.createCriteria();
			if(isAdmin==3){//如果是渠道登录，那就查询条件中带渠道
				criteria.andAllocchannelEqualTo(username);
			}else if(isAdmin==4){//如果是代理商登录，那就查询条件中带代理商
				criteria.andAgentzhEqualTo(username);
			}else if(isAdmin==5){//如果是经纪人登录，那就查询条件中带经纪人
				criteria.andAllocbrokerEqualTo(username);
			}
			//criteria.andTypeEqualTo("3");//
			//criteria.andStatusGreaterThan((byte)2);//状态   2:预提现  3：提现成功   4:提现申请  5：审核通过  6：拒绝
			List<FundSettlelog> userlist = fundSettlelogService.selectByExample(example);
			PageInfo<FundSettlelog> userpage = new PageInfo<FundSettlelog>(userlist);
			if(userlist != null && userlist.size() > 0){
				for(FundSettlelog logEntity : userlist){
					if(!StringUtil.isAnyNullOrEmpty(logEntity.getAllocchannel())){
						logEntity.setAllocchannel(nameMap.get(logEntity.getAllocchannel()));
					}
					if(!StringUtil.isAnyNullOrEmpty(logEntity.getAllocbroker())){
						logEntity.setAllocbroker(nameMap.get(logEntity.getAllocbroker()));
					}
					if(!StringUtil.isAnyNullOrEmpty(logEntity.getAgentzh())){
						logEntity.setAgentzh(nameMap.get(logEntity.getAgentzh()));
					}
				}
			}
			model.addAttribute("userlist", userlist);
			model.addAttribute("userpage", userpage);
		} catch (Exception e) {
			log.error("查询提现记录列表异常：" + e.getMessage());
		}
		return "fund/fundSettlelogList";
	}

	// 手续费分成报表分页
	@RequestMapping("/selectPageFundSettlelog")
	public String selectPageFundSettlelog(TradeParam trade, String usertype, HttpSession session, Model model) throws UnsupportedEncodingException {
		try {
			Map<String, String> nameMap = new HashMap<String, String>();
			SubzhExample exampleSubzh = new SubzhExample();
			com.jcl.pojo.SubzhExample.Criteria criteriaSubzh = exampleSubzh.createCriteria();
			criteriaSubzh.andIsadminLessThan(6);
			List<Subzh> qdlistMap = subzhservice.selectByExample(exampleSubzh);
			if(qdlistMap != null && qdlistMap.size()>0){
				for(Subzh subzhTemp : qdlistMap){
					nameMap.put(subzhTemp.getSubzh(), subzhTemp.getName());
				}
			}
			
			Integer isAdmin = (Integer) session.getAttribute(Constant.SESSION_ISADMIN);
			String username = (String) session.getAttribute(Constant.SESSION_LOGINNAME);
			//Subzh subzh = subzhservice.selectBySubzh(username);
			Subzh subzh = new Subzh();
			if(isAdmin==1){//如果是机构登录，那就查询条件中带机构
				subzh.setManage(username);
			}else if(isAdmin==2){//如果是平台登录，那就查询条件中带平台
				subzh.setAllocpt(username);
			}else if(isAdmin==3){//如果是渠道登录，那就查询条件中带渠道
				subzh.setAllocchannel(username);
			}else if(isAdmin==4){//如果是代理商登录，那就查询条件中带代理商
				subzh.setAllocagent(username);
			}else if(isAdmin==5){//如果是经纪人登录，那就查询条件中带经纪人
				subzh.setAllocbroker(username);
			}
			subzh.setIsadmin(6);
			Subzh condition=new Subzh();
			if(isAdmin==1 || isAdmin==2){//如果是机构登录，那就查询条件中带机构
				condition.setIsadmin(3);
				if(isAdmin==1){
					condition.setManage(username);
				}else{
					condition.setAllocpt(username);
				}
				if(subzh.getIsadmin()>3){
					List<Subzh> qdlist=subzhservice.getSubzhLevelList(condition);//查渠道集合
					for (Subzh subzh2 : qdlist) {
						System.out.println(subzh2.getSubzh());
					}
					model.addAttribute("channelList", qdlist);
					if(qdlist!=null && qdlist.size()>0){
						String qdsub=qdlist.get(0).getSubzh();
						if(!StringUtil.isAnyNullOrEmpty(subzh.getAllocchannel())){
							qdsub=subzh.getAllocchannel();
						}
						if(subzh.getIsadmin()>4){
							condition.setAllocchannel(trade.getChannel());
							condition.setIsadmin(4);
							List<Subzh> dlslist=subzhservice.getSubzhLevelList(condition);//查代理商集合
							for (Subzh subzh3 : dlslist) {
								System.out.println(subzh3.getSubzh());
							}
							model.addAttribute("agentList", dlslist);
							if(subzh.getIsadmin()==6 && dlslist!=null && dlslist.size()>0){
								String dlssub=dlslist.get(0).getSubzh();
								if(!StringUtil.isAnyNullOrEmpty(subzh.getAllocagent())){
									dlssub=subzh.getAllocagent();
								}
								condition.setAllocagent(trade.getAgentzh());
								condition.setIsadmin(5);
								List<Subzh> jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
								for (Subzh subzh4 : jjrlist) {
									System.out.println(subzh4.getSubzh());
								}
								model.addAttribute("brokerList", jjrlist);
							}
						}
						
					}
				}
			}else if(isAdmin==3){//渠道登录后，需要加载代理商，经纪人集合
				condition.setAllocchannel(username);
				condition.setIsadmin(4);
				if(subzh.getIsadmin()>4){
					List<Subzh> dlslist=subzhservice.getSubzhLevelList(condition);//查代理商集合
					model.addAttribute("agentList", dlslist);
					if(subzh.getIsadmin()==6 && dlslist!=null && dlslist.size()>0){
						String dlssub=dlslist.get(0).getSubzh();
						if(!StringUtil.isAnyNullOrEmpty(subzh.getAllocagent())){
							dlssub=subzh.getAllocagent();
						}
						condition.setAllocagent(dlssub);
						condition.setIsadmin(5);
						List<Subzh> jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
						model.addAttribute("brokerList", jjrlist);
					}
				}
				
			}else if(isAdmin==4){//代理商登录时，需要加载经纪人下拉框数据
				condition.setAllocagent(username);
				condition.setIsadmin(5);
				if(subzh.getIsadmin()==6){
					List<Subzh> jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
					model.addAttribute("brokerList", jjrlist);
				}
			}
			
			PageHelper.startPage(PaginationContext.getPageNum(), PaginationContext.getPageSize());
			FundSettlelogExample example = new FundSettlelogExample();
			example.setOrderByClause(" id desc, createtime desc");
			com.jcl.pojo.FundSettlelogExample.Criteria criteria = example.createCriteria();
			//criteria.andStatusGreaterThan((byte)2);//状态   2:预提现  3：提现成功   4:提现申请  5：审核通过  6：拒绝
			if(!StringUtil.isAnyNullOrEmpty(trade.getSubzh())){
				criteria.andSubzhLike("%" + trade.getSubzh() + "%");
			}
			if(!StringUtil.isAnyNullOrEmpty(trade.getStockCode())){
				criteria.andNameLike("%" + trade.getStockCode() + "%");
			}
			if(!StringUtil.isAnyNullOrEmpty(trade.getBroker())){
				criteria.andAllocbrokerEqualTo(trade.getBroker());
			}
			if(!StringUtil.isAnyNullOrEmpty(trade.getChannel())){
				criteria.andAllocchannelEqualTo(trade.getChannel());
			}
			
			if(!StringUtil.isAnyNullOrEmpty(trade.getAgentzh())){
				criteria.andAgentzhEqualTo(trade.getAgentzh());
			}
			
			if(!StringUtil.isAnyNullOrEmpty(trade.getStarttime())){
				criteria.andSettletimeGreaterThanOrEqualTo(DateUtil.stringToDate(trade.getStarttime(), "yyyy-MM-dd"));
			}
			if(!StringUtil.isAnyNullOrEmpty(trade.getEndtime())){
				criteria.andSettletimeLessThanOrEqualTo(DateUtil.stringToDate(trade.getEndtime(), "yyyy-MM-dd"));
			}
			/*if(!StringUtil.isAnyNullOrEmpty(usertype)){
				criteria.andIsadminEqualTo(Integer.parseInt(usertype));
			}*/
			if(isAdmin==3){//如果是渠道登录，那就查询条件中带渠道
				criteria.andAllocchannelEqualTo(username);
			}else if(isAdmin==4){//如果是代理商登录，那就查询条件中带代理商
				criteria.andAgentzhEqualTo(username);
			}else if(isAdmin==5){//如果是经纪人登录，那就查询条件中带经纪人
				criteria.andAllocbrokerEqualTo(username);
			}
			List<FundSettlelog> userlist = fundSettlelogService.selectByExample(example);
			PageInfo<FundSettlelog> userpage = new PageInfo<FundSettlelog>(userlist);
			if(userlist != null && userlist.size() > 0){
				for(FundSettlelog logEntity : userlist){
					if(!StringUtil.isAnyNullOrEmpty(logEntity.getAllocchannel())){
						logEntity.setAllocchannel(nameMap.get(logEntity.getAllocchannel()));
					}
					if(!StringUtil.isAnyNullOrEmpty(logEntity.getAllocbroker())){
						logEntity.setAllocbroker(nameMap.get(logEntity.getAllocbroker()));
					}
					if(!StringUtil.isAnyNullOrEmpty(logEntity.getAgentzh())){
						logEntity.setAgentzh(nameMap.get(logEntity.getAgentzh()));
					}
				}
			}
			model.addAttribute("trade", trade);
			model.addAttribute("userlist", userlist);
			model.addAttribute("userpage", userpage);
	
		} catch (Exception e) {
			log.error("查询提现记录列表异常：" + e.getMessage());
		}
		return "fund/fundSettlelogList";
	}
	
	//手续费分成的导出
	@RequestMapping("/selectExport")
	public void selectExport(TradeParam trade, String usertype, HttpSession session, Model model,HttpServletResponse response) throws UnsupportedEncodingException {
		try {
			Integer isAdmin = (Integer) session.getAttribute(Constant.SESSION_ISADMIN);
			String username = (String) session.getAttribute(Constant.SESSION_LOGINNAME);
			Map<String, String> nameMap = new HashMap<String, String>();
			SubzhExample exampleSubzh = new SubzhExample();
			com.jcl.pojo.SubzhExample.Criteria criteriaSubzh = exampleSubzh.createCriteria();
			criteriaSubzh.andIsadminLessThan(6);
			List<Subzh> qdlistMap = subzhservice.selectByExample(exampleSubzh);
			if(qdlistMap != null && qdlistMap.size()>0){
				for(Subzh subzhTemp : qdlistMap){
					nameMap.put(subzhTemp.getSubzh(), subzhTemp.getName());
				}
			}
			
			
			//Subzh subzh = subzhservice.selectBySubzh(username);
			Subzh subzh = new Subzh();
			if(isAdmin==1){//如果是机构登录，那就查询条件中带机构
				subzh.setManage(username);
			}else if(isAdmin==2){//如果是平台登录，那就查询条件中带平台
				subzh.setAllocpt(username);
			}else if(isAdmin==3){//如果是渠道登录，那就查询条件中带渠道
				subzh.setAllocchannel(username);
			}else if(isAdmin==4){//如果是代理商登录，那就查询条件中带代理商
				subzh.setAllocagent(username);
			}else if(isAdmin==5){//如果是经纪人登录，那就查询条件中带经纪人
				subzh.setAllocbroker(username);
			}
			subzh.setIsadmin(6);
			Subzh condition=new Subzh();
			if(isAdmin==1 || isAdmin==2){//如果是机构登录，那就查询条件中带机构
				condition.setIsadmin(3);
				if(isAdmin==1){
					condition.setManage(username);
				}else{
					condition.setAllocpt(username);
				}
				if(subzh.getIsadmin()>3){
					List<Subzh> qdlist=subzhservice.getSubzhLevelList(condition);//查渠道集合
					model.addAttribute("channelList", qdlist);
					if(qdlist!=null && qdlist.size()>0){
						String qdsub=qdlist.get(0).getSubzh();
						if(!StringUtil.isAnyNullOrEmpty(subzh.getAllocchannel())){
							qdsub=subzh.getAllocchannel();
						}
						if(subzh.getIsadmin()>4){
							condition.setAllocchannel(qdsub);
							condition.setIsadmin(4);
							List<Subzh> dlslist=subzhservice.getSubzhLevelList(condition);//查代理商集合
							model.addAttribute("agentList", dlslist);
							if(subzh.getIsadmin()==6 && dlslist!=null && dlslist.size()>0){
								String dlssub=dlslist.get(0).getSubzh();
								if(!StringUtil.isAnyNullOrEmpty(subzh.getAllocagent())){
									dlssub=subzh.getAllocagent();
								}
								condition.setAllocagent(dlssub);
								condition.setIsadmin(5);
								List<Subzh> jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
								model.addAttribute("brokerList", jjrlist);
							}
						}
						
					}
				}
			}else if(isAdmin==3){//渠道登录后，需要加载代理商，经纪人集合
				condition.setAllocchannel(username);
				condition.setIsadmin(4);
				if(subzh.getIsadmin()>4){
					List<Subzh> dlslist=subzhservice.getSubzhLevelList(condition);//查代理商集合
					model.addAttribute("agentList", dlslist);
					if(subzh.getIsadmin()==6 && dlslist!=null && dlslist.size()>0){
						String dlssub=dlslist.get(0).getSubzh();
						if(!StringUtil.isAnyNullOrEmpty(subzh.getAllocagent())){
							dlssub=subzh.getAllocagent();
						}
						condition.setAllocagent(dlssub);
						condition.setIsadmin(5);
						List<Subzh> jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
						model.addAttribute("brokerList", jjrlist);
					}
				}
				
			}else if(isAdmin==4){//代理商登录时，需要加载经纪人下拉框数据
				condition.setAllocagent(username);
				condition.setIsadmin(5);
				if(subzh.getIsadmin()==6){
					List<Subzh> jjrlist=subzhservice.getSubzhLevelList(condition);//查经纪人集合
					model.addAttribute("brokerList", jjrlist);
				}
			}
			FundSettlelogExample example = new FundSettlelogExample();
			example.setOrderByClause(" id desc, createtime desc");
			com.jcl.pojo.FundSettlelogExample.Criteria criteria = example.createCriteria();
			//criteria.andStatusGreaterThan((byte)2);//状态   2:预提现  3：提现成功   4:提现申请  5：审核通过  6：拒绝
			if(!StringUtil.isAnyNullOrEmpty(trade.getSubzh())){
				criteria.andSubzhLike("%" + trade.getSubzh() + "%");
			}
			if(!StringUtil.isAnyNullOrEmpty(trade.getStockCode())){
				criteria.andNameLike("%" + trade.getStockCode() + "%");
			}
			if(!StringUtil.isAnyNullOrEmpty(trade.getBroker())){
				criteria.andAllocbrokerEqualTo(trade.getBroker());
			}
			if(!StringUtil.isAnyNullOrEmpty(trade.getChannel())){
				criteria.andAllocchannelEqualTo(trade.getChannel());
			}
			
			if(!StringUtil.isAnyNullOrEmpty(trade.getAgentzh())){
				criteria.andAgentzhEqualTo(trade.getAgentzh());
			}
			if(!StringUtil.isAnyNullOrEmpty(trade.getStarttime())){
				criteria.andSettletimeGreaterThanOrEqualTo(DateUtil.stringToDate(trade.getStarttime(), "yyyy-MM-dd"));
			}
			if(!StringUtil.isAnyNullOrEmpty(trade.getEndtime())){
				criteria.andSettletimeLessThanOrEqualTo(DateUtil.stringToDate(trade.getEndtime(), "yyyy-MM-dd"));
			}
			/*if(!StringUtil.isAnyNullOrEmpty(usertype)){
				criteria.andIsadminEqualTo(Integer.parseInt(usertype));
			}*/
			if(isAdmin==3){//如果是渠道登录，那就查询条件中带渠道
				criteria.andAllocchannelEqualTo(username);
			}else if(isAdmin==4){//如果是代理商登录，那就查询条件中带代理商
				criteria.andAgentzhEqualTo(username);
			}else if(isAdmin==5){//如果是经纪人登录，那就查询条件中带经纪人
				criteria.andAllocbrokerEqualTo(username);
			}
			List<FundSettlelog> userlist = fundSettlelogService.selectByExample(example);
			if(userlist != null && userlist.size() > 0){
				for(FundSettlelog logEntity : userlist){
					if(!StringUtil.isAnyNullOrEmpty(logEntity.getAllocchannel())){
						logEntity.setAllocchannel(nameMap.get(logEntity.getAllocchannel()));
					}
					if(!StringUtil.isAnyNullOrEmpty(logEntity.getAllocbroker())){
						logEntity.setAllocbroker(nameMap.get(logEntity.getAllocbroker()));
					}
					if(!StringUtil.isAnyNullOrEmpty(logEntity.getAgentzh())){
						logEntity.setAgentzh(nameMap.get(logEntity.getAgentzh()));
					}
				}
			}
			SimpleDateFormat myFmt = new SimpleDateFormat("yyyy年MM月dd日");
			JSONArray ja = new JSONArray();// 获取业务数据集
			ja.addAll(userlist);
			Map<String, String> headMap = new LinkedHashMap<String, String>();
			headMap.put("subzh", "用户账户");
			headMap.put("allocchannel", "所属渠道");
			headMap.put("agentzh", "所属代理商");
			headMap.put("allocbroker", "所属经纪人");
			headMap.put("name", "合约代码");
			headMap.put("settledesc", "合约名称");
			headMap.put("flage", "方向");
			headMap.put("cmd", "开平");
			headMap.put("feebalance", "数量");
			headMap.put("createtime", "成交时间");
			headMap.put("deferredbalance", "手续费总额");
			if(isAdmin<4){
				headMap.put("feenextbalance", "渠道存留手续费");
			}
			if(isAdmin<5){
				headMap.put("addbalance", "代理商存留手续费");
			}
			headMap.put("overbalance", "经纪人存留手续费");
			MyExcelUtil.downloadExcelFile("手续费分成报表" + myFmt.format(new Date()), headMap, ja, "yyyy-MM-dd HH:mm:ss", 0,
					response);
		} catch (Exception e) {
			log.error("手续费分成报表导出异常：",e);
		}
	}
	
	//进入手续费分成页面时异步加载汇总信息
	@RequestMapping("/getCountFundSettlelog")
	@ResponseBody
	public Map<String, Object> getCountFundSettlelog(TradeParam trade,HttpSession session){
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		try {
			
			
			Integer isAdmin = (Integer) session.getAttribute(Constant.SESSION_ISADMIN);
			String username = (String) session.getAttribute(Constant.SESSION_LOGINNAME);
			
			if(isAdmin==3){//如果是渠道登录，那就查询条件中带渠道
				
				trade.setChannel(username);
				
			}else if(isAdmin==4){//如果是代理商登录，那就查询条件中带代理商
				
				trade.setAgentzh(username);
			
			}else if(isAdmin==5){//如果是经纪人登录，那就查询条件中带经纪人
				
				trade.setBroker(username);
				
			}
			FundSettlelog fundSettlelog=fundSettlelogService.sumFundSettlelogFund(trade);
			if(fundSettlelog!=null){
				Double summoney = fundSettlelog.getSummoney()==null?0:fundSettlelog.getSummoney();
				Double sumfeenextbalance = fundSettlelog.getSumfeenextbalance()==null?0:fundSettlelog.getSumfeenextbalance();
				Double sumaddbalance = fundSettlelog.getSumaddbalance()==null?0:fundSettlelog.getSumaddbalance();
				Double sumoverbalance = fundSettlelog.getSumoverbalance()==null?0:fundSettlelog.getSumoverbalance();
				
				map.put("summoney", summoney);
				map.put("sumfeenextbalance", sumfeenextbalance);
				map.put("sumaddbalance", sumaddbalance);
				map.put("sumoverbalance", sumoverbalance);
			}else {
				map.put("summoney", 0);
				map.put("sumfeenextbalance", 0);
				map.put("sumaddbalance", 0);
				map.put("sumoverbalance", 0);
			}
		
			
		}catch (Exception e) {
				log.error("收续费分成报表汇总异常",e);
		}
		return map;	
	}
	
	// 销售月报管理页面跳转
	@RequestMapping("/fundSumlogList")
	public String fundSumlogList(HttpSession session, Model model) {
		session.setAttribute(Constant.SESSION_LEFTMENU, "16");
		try {
			String agentzh = (String) session.getAttribute(Constant.SESSION_LOGINNAME);
			
			boolean qdFlag = true;
			//Integer amdinOrSub = (Integer)this.getSession().getAttribute(Constant.SESSION_ADMINORSUBZH);
			Integer amdinOrSub = 2;
			if(amdinOrSub != null && amdinOrSub == 2){//如果是管理子账号登录
				//Integer isqd = (Integer) this.getSession().getAttribute("cacheisadmin");
				Integer isqd = 3;
				if(isqd != null && isqd == 3){//如果是渠道登录的
					qdFlag = false;
					FundSumlog fundSumlog = new FundSumlog();
					/*合计渠道递延费*/
					/*Map<String, Object> mapCondition = new HashMap<String, Object>();
					mapCondition.put("fundtype", "3");
					mapCondition.put("allocchannel", agentzh);
					FundSettlelog fundSettlelog = fundSettlelogService.getFundSettlelogTotal(mapCondition);
					if(fundSettlelog != null){
						fundSumlog.setRegistnum(fundSettlelog.getRegistnum());
						fundSumlog.setAddfund(fundSettlelog.getAddfund());
						fundSumlog.setAddbalance(fundSettlelog.getAddbalance());
						fundSumlog.setFeeBalance(fundSettlelog.getFeeBalance());
						fundSumlog.setDeferredBalance(fundSettlelog.getDeferredBalance());
						fundSumlog.setFeenextBalance(fundSettlelog.getFeenextBalance());
						fundSumlog.setOverBalance(fundSettlelog.getOverBalance());
						//fundSumlog.setSumBalance(fundSettlelog.getSumBalance());
						fundSumlog.setSumBalance(fundSettlelog.getFeeBalance()+fundSettlelog.getDeferredBalance()+
								fundSettlelog.getFeenextBalance()+fundSettlelog.getOverBalance());
					}*/
				}
			}
			
			if(qdFlag){
				/*查询最新一条结算数据*/
				/*QueryParameter parameter = new QueryParameter();
				parameter.setAutoCount(true);
				parameter.setPageNo(1);
				parameter.setPageSize(3);
				Map<String, Object> mapCondition = new HashMap<String, Object>();
				mapCondition.put("fundtype", "1");
				if(!StringUtil.isAnyNullOrEmpty(agentzh)){//判断是否为一级代理商
					try {
						 Subzh subzh = subzhService.getSubzhById(agentzh);
						 if(subzh != null && (subzh.getPagentzh() == null || "-1".equals(subzh.getPagentzh()))){
							 mapCondition.put("agentzh", agentzh);
						 } else if(subzh != null && subzh.getPagentzh() != null && !"-1".equals(subzh.getPagentzh())){
							 agentzh = getAgentzhByUsername(subzh.getPagentzh());
							 mapCondition.put("agentzh", agentzh);
						 }
					} catch (Exception es) {
						log.error("查询账户信息异常：" + es.getMessage());
					}
				}
				Page<FundSumlog> pageList = fundSumlogService.getFundSumlogPageListByCondition(parameter, mapCondition);
				List<FundSumlog> logList = pageList.getResult();
				if(logList != null && logList.size() > 0){
					fundSumlog = logList.get(0);
					try {
						double sumTotal = 0.00;
						if(fundSumlog.getFeeBalance() != null){
							sumTotal = sumTotal + fundSumlog.getFeeBalance();
						}
						if(fundSumlog.getDeferredBalance() != null){
							sumTotal = sumTotal + fundSumlog.getDeferredBalance();
						}
						if(fundSumlog.getFeenextBalance() != null){
							sumTotal = sumTotal + fundSumlog.getFeenextBalance();
						}
						if(fundSumlog.getOverBalance() != null){
							sumTotal = sumTotal + fundSumlog.getOverBalance();
						}
						fundSumlog.setSumBalance(sumTotal);
					} catch (Exception es) {
						log.error("合计平台收入异常：" + es.getMessage());
					}
				} else {
					fundSumlog = new FundSumlog();
				}*/
			}
			
			PageHelper.startPage(PaginationContext.getPageNum(), PaginationContext.getPageSize());
			//List<FundPzLog> userlist = FundPzLogservice.selectAll();
			FundSettlelogExample example = new FundSettlelogExample();
			example.setOrderByClause(" id desc, createtime desc");
			com.jcl.pojo.FundSettlelogExample.Criteria criteria = example.createCriteria();
			//criteria.andTypeEqualTo("3");//
			//criteria.andStatusGreaterThan((byte)2);//状态   2:预提现  3：提现成功   4:提现申请  5：审核通过  6：拒绝
			List<FundSettlelog> userlist = fundSettlelogService.selectByExample(example);
			PageInfo<FundSettlelog> userpage = new PageInfo<FundSettlelog>(userlist);
			if(userlist != null && userlist.size() > 0){
			}
			model.addAttribute("userlist", userlist);
			model.addAttribute("userpage", userpage);
		} catch (Exception e) {
			log.error("查询提现记录列表异常：" + e.getMessage());
		}
		return "fund/fundSumlogList";
	}

	// 销售日报列表模糊查询
	@RequestMapping("/selectPageFundSumlog")
	public String selectPageFundSumlog(String nickname, String usertype, Model model) throws UnsupportedEncodingException {
		try {
			PageHelper.startPage(PaginationContext.getPageNum(), PaginationContext.getPageSize());
			FundSettlelogExample example = new FundSettlelogExample();
			example.setOrderByClause(" id desc, createtime desc");
			com.jcl.pojo.FundSettlelogExample.Criteria criteria = example.createCriteria();
			//criteria.andStatusGreaterThan((byte)2);//状态   2:预提现  3：提现成功   4:提现申请  5：审核通过  6：拒绝
			if(!StringUtil.isAnyNullOrEmpty(nickname)){
				criteria.andSubzhLike("%" + nickname + "%");
			}
			/*if(!StringUtil.isAnyNullOrEmpty(usertype)){
				criteria.andIsadminEqualTo(Integer.parseInt(usertype));
			}*/
			List<FundSettlelog> userlist = fundSettlelogService.selectByExample(example);
			PageInfo<FundSettlelog> userpage = new PageInfo<FundSettlelog>(userlist);
			
			model.addAttribute("nickname", nickname);
			model.addAttribute("userlist", userlist);
			model.addAttribute("userpage", userpage);
		} catch (Exception e) {
			log.error("查询提现记录列表异常：" + e.getMessage());
		}
		return "fund/fundSumlogList";
	}
	
	public FundPzlog getFundPzlog() {
		return fundPzlog;
	}

	public void setFundPzlog(FundPzlog fundPzlog) {
		this.fundPzlog = fundPzlog;
	}
	
}