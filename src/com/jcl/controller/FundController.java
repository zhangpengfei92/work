/**
 * Copyright (C) 2009 武汉金策略信息科技有限公司
 *
 * 版权所有。
 *
 * 类名　　  : @FundController.java
 * 功能概要  : 
 * 做成日期  : @2018年6月13日
 * 修改日期  :
 */
package com.jcl.controller;
/** 
 * @author zpf
 * @version 1.0
 */


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.jcl.dao.AgentzhfundLogMapper;
import com.jcl.pojo.AgentzhfundLog;
import com.jcl.pojo.Subzh;
import com.jcl.service.AgentzhfundLogService;
import com.jcl.service.FundService;
import com.jcl.service.SubzhService;
import com.jcl.util.Constant;
import com.jcl.util.StringUtil;
/**
 * 资金充值提现的控制层
 */

@Controller
@RequestMapping("/subzhfund")
public class FundController {
	public static Logger log = Logger.getLogger(FundController.class);
	
	@Autowired
	private FundService fundServiceimpl;
	
	@Autowired
	private SubzhService subzhService;
	
	@Autowired
	private AgentzhfundLogService agentzhfundLogService;
	//去h5充值页面
	@RequestMapping("/toPayH5")
	public String goH5Pay(String subzh,Model model,HttpSession session) {
	
	/*	//判断当前用户是否已经实名认证
		session.setAttribute(Constant.SESSION_LOGINNAME, subzh);
		//判断当前用户是否已经实名认证
		Map<String, Object> map=isok(subzh, "register_h5/fundChartHFive");
		if((Boolean) map.get("status")) {//true
			
			log.info(map.get("msg"));
			Subzh sub=(Subzh) map.get("users");
			model.addAttribute("sub", sub);
			return (String) map.get("payPage");
		
		}else {
			log.info(map.get("msg"));
			model.addAttribute("msg", map.get("msg"));
			return (String) map.get("payPage");
		}*/
		return "register_h5/isclose";
	}
	
	//去网关充值页面
	@RequestMapping("/toPay")
	public String goPCPay(String subzh,Model model,HttpSession session) {
		/*session.setAttribute(Constant.SESSION_LOGINNAME, subzh);
		//判断当前用户是否已经实名认证
		Map<String, Object> map=isok(subzh, "register_h5/fundChart");
		if((Boolean) map.get("status")) {//true
			
			log.info(map.get("msg"));
			Subzh sub=(Subzh) map.get("users");
			model.addAttribute("sub", sub);
			return (String) map.get("payPage");
		
		}else {
			log.info(map.get("msg"));
			model.addAttribute("msg", map.get("msg"));
			return (String) map.get("payPage");
		}*/
		return "register_h5/isclose";
	}
	
	//去pc提现页面
	@RequestMapping("/drawMoney")
	public  String  goGetMoney(String subzh,Model model,HttpSession session) {
		session.setAttribute(Constant.SESSION_LOGINNAME, subzh);
		//判断当前用户是否已经实名认证
	
		Map<String, Object> map=isok(subzh, "register_h5/fundRaising");
		if((Boolean) map.get("status")) {//true
			
			log.info(map.get("msg"));
			Subzh sub=(Subzh) map.get("users");
			if(null!=sub&&sub.getIsadmin()==6){
				BigDecimal fundbalance=fundServiceimpl.getfundbalance(sub);
				log.info("用户可提金额 : "+fundbalance);
				sub.setFundbalance(fundbalance);
			}
			model.addAttribute("sub", sub);
			return (String) map.get("payPage");
		
		}else {
			log.info(map.get("msg"));
			model.addAttribute("msg", map.get("msg"));
			return (String) map.get("payPage");
		}
		//return "register_h5/isclose";
	}
	
	
	//去手机提现页面
	@RequestMapping("/drawMoneyH5")
	public  String  drawMoneyH5(String subzh,Model model,HttpSession session) {
		session.setAttribute(Constant.SESSION_LOGINNAME, subzh);
		//判断当前用户是否已经实名认证
	
		Map<String, Object> map=isok(subzh, "register_h5/fundRaising");
		if((Boolean) map.get("status")) {//true

			log.info(map.get("msg"));
			Subzh sub=(Subzh) map.get("users");
			if(null!=sub&&sub.getIsadmin()==6){
				BigDecimal fundbalance=fundServiceimpl.getfundbalance(sub);
			
				   if (fundbalance== null ||fundbalance.doubleValue() <0) {
	                    log.info("账户资产不足，非法提交");
	                    return "phonePay/nofund";
	                }
					log.info("用户可提金额 : "+fundbalance.setScale(2, BigDecimal.ROUND_DOWN));
					sub.setFundbalance(fundbalance);
			}
			if(sub.getFundbalance()==null) {
				sub.setFundbalance(new BigDecimal(0));
			}else{
				sub.setFundbalance(sub.getFundbalance().setScale(2, BigDecimal.ROUND_DOWN));
			}
			
			
			model.addAttribute("sub", sub);
			return (String) map.get("payPage");
		
		}else {
			log.info(map.get("msg"));
			model.addAttribute("msg", map.get("msg"));
			return (String) map.get("payPage");
		}
		//return "register_h5/isclose";
	}
	//保存快捷充值信息
	@RequestMapping("/payMentFive")
	public String ispay(String username,String txmoney,String channel,Model model,HttpSession session) {
		String subzh = (String) session.getAttribute(Constant.SESSION_LOGINNAME);

		String url = Constant.SPT_H5_PAY_URL;
		Map<String, String> map= fundServiceimpl.savePay(username, txmoney,channel);
		log.info(map+"    <url   : "+url + ">");
		System.out.println(map+"    <url   : "+url + ">");
		if(map!=null||map.size()>0){
			model.addAttribute("map", map);
			model.addAttribute("url", url);
			
		}
			return "register_h5/payMentFive";

	}
	
	//保存网关充值信息
	@RequestMapping("/payMent")
	public String payMent(String username,String txmoney,String channel,Model model,HttpSession session) {
		String subzh = (String) session.getAttribute(Constant.SESSION_LOGINNAME);
		String url = Constant.SPT_WG_PAY_URL;
		Map<String, String> map= fundServiceimpl.payMent(username, txmoney,channel);
		log.info(map+"    <url   : "+url + ">");
		System.out.println(map+"    <url   : "+url + ">");
		if(map!=null||map.size()>0){
			model.addAttribute("map", map);
			model.addAttribute("url", url);
		}
			return "register_h5/payMent";

	}
	
	
	//成功页面
	@RequestMapping("/stockoptionJump")
	public String stockoptionJump() {
		
		return "register_h5/callback";	
	}
	
	//快捷充值回调的方法payNotyfy  
	@RequestMapping(value = "/returnSptPayMent",method=RequestMethod.POST,produces="text/html;charset=UTF-8;")
	@ResponseBody
	public  String  payNotyfy(@RequestBody String requestBody) {
			Map<String, Object> map=fundServiceimpl.payNotyfy(requestBody);
			
		    if ((map != null) && (map.size() > 0)) {
			      log.info("充值状态===》" + map.get("status"));
			      if ((Boolean)map.get("status")) {
			        return "SUCCESS";
			      }else {
			    	 log.info("充值失败    ：  " + map.get("msg"));
			    	 return "false";		    	 
			      }   
			    }else {
			    	log.info("充值失败    ：  " + map.get("msg"));
			    	return "false";
			    }
			
		
	}
	
	//网关充值回调 
	@RequestMapping(value = "/notifySptPayMent",method=RequestMethod.POST,produces="text/html;charset=UTF-8;")
	@ResponseBody
	public  String  notifySptPayMent(@RequestBody String requestBody) {
			Map<String, Object> map=fundServiceimpl.notifySptPayMent(requestBody);
			
		    if ((map != null) && (map.size() > 0)) {
			      log.info("充值状态===》" + map.get("status"));
			      if ((Boolean)map.get("status")) {
			        return "SUCCESS";
			      }else {
			    	 log.info("充值失败    ：  " + map.get("msg"));
			    	 return "false";		    	 
			      }   
			    }else {
			    	log.info("充值失败    ：  " + map.get("msg"));
			    	return "false";
			    }
			
		
	}
	
	//保存提现信息
	  @RequestMapping(value={"/paya"}, produces={"text/html;charset=UTF-8;"})
	  @ResponseBody
	  public String savePay(AgentzhfundLog afl, HttpSession session, Model model)
	  {
	    log.info("保存提现信息");
	    String subzh = (String)session.getAttribute("SESSION_LOGINNAME");
	    log.info("subzh===》" + subzh);
	    Map<String, Object> map = null;
	    if (subzh == null) {
	      log.info("subzh:账户为null");
	      return "用户不存在";
	    }
	    try {
	      map = this.fundServiceimpl.savePay(afl, subzh);
	    }
	    catch (Exception e){
	      e.printStackTrace();
	      log.error("savePay--保存体提现信息异常", e);
	    }
	    if ((map != null) && (map.size() > 0)){
	      log.info("保存提现信息状态===》" + map.get("status")+"    === :"+map.get("msg"));

	      if ((Boolean)map.get("status")) {
	        return "ok";
	      }
	      return "提现失败";
	    }
	    return "提现失败";
	  }
	  
	  
	  //去查询这调提现信息
	  @RequestMapping(value = "/queryAgentzhfundLog")
	  public  String  queryAgentzhfundLog(Integer id,Model model) {
		  AgentzhfundLog afl = agentzhfundLogService.selectByPrimaryKey(id);
			String description= afl.getDescription();
	 		JSONObject jsonobj = JSONObject.parseObject(description);
			
			afl.setAmount(jsonobj.getString("amount"));
			afl.setBankCardNumber(jsonobj.getString("bankCardNumber"));	
			afl.setPhoneNum(jsonobj.getString("phoneNum"));
			afl.setIdcard(jsonobj.getString("idcard"));
			afl.setBankcode(jsonobj.getString("bankcode"));
		  
			model.addAttribute("afl", afl);
		  
		  
		return "fund/auditing"; 
	  }
	  
	  
	//审核并且提现
	@RequestMapping(value = "/gopay",produces="text/html;charset=UTF-8;")
	@ResponseBody
	public String gopay(Integer id, Integer status) {
		log.info("提现审核开始");
		Map<String, Object>map=fundServiceimpl.isPay(id,status);
		   if ((map != null) && (map.size() > 0)) {
			   log.info("保存提现信息状态===》" + map.get("status")+"    === :"+map.get("msg"));
		      if (map.get("status")=="true") {
		    	  log.info("保存提现信息成功");
		    	  return "true";
		      }else {
		    	  return "false";
		      }
		    }else {
		    	  return "false";
		    }
		   
	}
	//提现回调
	@RequestMapping(value = "/isPay",produces="text/html;charset=UTF-8;")
	@ResponseBody
	public String queryMoney(HttpServletRequest request, Model model, @RequestBody String requestBody){
		Map<String, Object> map=new HashMap<String, Object>();
		log.info("提现回调开始");		
		try {	
			map=fundServiceimpl.saveGetMoney(requestBody);
			log.info("=====map====   :   "+map);
			   if ((map != null) && (map.size() > 0)) {
				   log.info("保存提现信息状态===》" + map.get("status")+"    === :"+map.get("msg"));
			      if ((Boolean) map.get("status")) {
			    	  log.info("保存提现信息成功");
			    	  return "true";
			      }else {
			    	  return "FAIL";
			      }
			    }else {
			    	  return "FAIL";
			    }
		} catch (Exception e) {
			log.error("保存用户提现信息失败", e);
			return "FAIL";
		}
	}
	
	//拒绝通过审核
		@RequestMapping("/checkWithdraw")
		@ResponseBody
		public String checkWithdraw(Integer id, Integer status) {
			
			Map<String, Object>map=fundServiceimpl.checkWithdraw(id,status);
			if(null!=map&&map.get("status")=="true") {
				return "true";
			}else {
				return "false";
			}
		}
	
	//判断该用户是否能出入金	
	public Map<String, Object> isok(String subzh,String payPage){
		Map<String, Object>map=new HashMap<String, Object>();
		String errorpay="register_h5/noneed";
		Subzh sub =null;
		if(null==subzh||subzh=="") {
			log.info("用户名不存在");
			map.put("msg","用户名不存在");
			map.put("status",false);
			map.put("payPage",errorpay);
			return map;
		}
		try {
			sub = subzhService.loginBySubzh(subzh);
			//判断用户信息是否存在
			if(null==sub) {
				log.info("用户信息不存在");
				map.put("msg","用户信息不存在");
				map.put("status",false);
				map.put("payPage",errorpay);
				return map;
			}
			//当用户操作充值（网关或者银联）判读用户是否是普通用户
			Boolean flag=true;
			if(payPage=="register_h5/fundChartHFive"||payPage=="register_h5/fundChart") {
				Integer isadmin=sub.getIsadmin();
				if(null!=isadmin&&isadmin!=0&&isadmin!=6) {
					log.info("非普通用户无法充值");
					map.put("msg","非普通用户无法充值");
					map.put("status",false);
					map.put("payPage",errorpay);
					return map;
				}
			}
			//当用户操作的是提现，判读用户是否是普通用户
			if(payPage=="register_h5/fundRaising") {
				Integer isadmin=sub.getIsadmin();
				if(null!=isadmin&&isadmin!=0&&isadmin!=6) {//如果用户不是普通用户，则无需实名认证
					flag=false;
				}
			}
			
			if(flag){//当用户操作提现且不是普通用户时，不必进行是否实名认证的判断
			//判断用户是否实名认证
			if (sub.getAuthState()!=null&&sub.getAuthState()!=2) {
				String msg="";
				Integer state=sub.getAuthState();
				msg=state==0?"用户未实名认证":state==1?"用户实名认证审核中":"用户实名认证未通过";
				map.put("msg",msg);
				map.put("status",false);
				map.put("payPage",errorpay);
				return map;
				}
			}
			map.put("msg","用户已实名认证");
			map.put("status",true);
			map.put("users",sub);
			map.put("payPage",payPage);
		} catch (Exception e) {
			log.error("查询用户异常");
		}

		return map;
	}	
}