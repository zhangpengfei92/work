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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jcl.dao.AgentzhfundLogMapper;
import com.jcl.pojo.AgentzhfundLog;
import com.jcl.pojo.Fundset;
import com.jcl.pojo.Subzh;
import com.jcl.service.AgentzhfundLogService;
import com.jcl.service.FundService;
import com.jcl.service.FundsetService;
import com.jcl.service.SubzhService;
import com.jcl.util.Constant;
import com.jcl.util.DateUtil;
import com.jcl.util.StringUtil;
/**
 * 资金充值提现的控制层--国际
 */

@Controller
@RequestMapping("/subzhfund")
public class FundController {
	public static Logger log = Logger.getLogger(FundController.class);
	
	public static final  String URL="http://6shun.masakn.top:1818/Pay_Index.html";//充值地址

	
	@Autowired
	private FundService fundServiceimpl;
	
	@Autowired
	private SubzhService subzhService;
	
	@Autowired
	private FundsetService fundsetService;
	
	@Autowired
	private AgentzhfundLogService agentzhfundLogService;
	//去h5充值页面
	@RequestMapping("/goH5Pay")
	public String goH5Pay(String subzh,Model model,HttpSession session) {
		//判断当前用户是否已经实名认证
		session.setAttribute(Constant.SESSION_LOGINNAME, subzh);
		//判断当前用户是否已经实名认证
		Map<String, Object> map=isok(subzh, "register_h5/fund_h5");
		if((Boolean) map.get("status")) {//true
			
			log.info(map.get("msg"));
			Subzh sub=(Subzh) map.get("users");
			model.addAttribute("sub", sub);
			return (String) map.get("payPage");
		
		}else {
			log.info(map.get("msg"));
			model.addAttribute("msg", map.get("msg"));
			return (String) map.get("payPage");
		}
	}
	
	//去h5充值页面
	@RequestMapping("/goPCPay")
	public String goPCPay(String subzh,Model model,HttpSession session) {
		session.setAttribute(Constant.SESSION_LOGINNAME, subzh);
		//判断当前用户是否已经实名认证
		Map<String, Object> map=isok(subzh, "register_h5/fund_pc");
		if((Boolean) map.get("status")) {//
			
			log.info(map.get("msg"));
			Subzh sub=(Subzh) map.get("users");
			model.addAttribute("sub", sub);
			return (String) map.get("payPage");
		
		}else {
			log.info(map.get("msg"));
			model.addAttribute("msg", map.get("msg"));
			return (String) map.get("payPage");
		}
	}
	
	//去提现页面
	@RequestMapping("/goGetMoney")
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
			if(sub.getFundbalance()==null) {
				sub.setFundbalance(new BigDecimal(0));
			}else {
				sub.setFundbalance(sub.getFundbalance().setScale(2, BigDecimal.ROUND_DOWN));
			}
			model.addAttribute("sub", sub);
			return (String) map.get("payPage");
		
		}else {
			log.info(map.get("msg"));
			model.addAttribute("msg", map.get("msg"));
			return (String) map.get("payPage");
		}
	}
	//保存充值信息
	@RequestMapping("/ispay/{bankco}/{moneys}/{cardno}")
	public String ispay(@PathVariable String bankco, @PathVariable Double moneys,@PathVariable String cardno,Model model,HttpSession session) {
		String subzh = (String) session.getAttribute(Constant.SESSION_LOGINNAME);
		Map<String, Object> map= fundServiceimpl.savePay(bankco, moneys/100,cardno, subzh);
		if(map!=null||map.size()>0){
			model.addAttribute("map", map);
			model.addAttribute("url", URL);
		}else {
			map.put("error","充值失败，请联系管理员");
			model.addAttribute("map", map);
		}
			return "register_h5/payMentFive";

	}
	
	//成功页面
	@RequestMapping("/stockoptionJump")
	public String stockoptionJump() {
		
		return "register_h5/callback";	
	}
	
	//支付成功跳转的页面
	@RequestMapping(value = "/payCallBack",produces="text/html;charset=UTF-8;")
	public String  payCallBack(HttpServletRequest request,Model model) {
		Map<String, Object> map=fundServiceimpl.payCallBack(request);
		
		if((Boolean) map.get("status")) {
			model.addAttribute("msg", map.get("msg"));
			log.info(map.get("msg"));
			  
		}else {
			model.addAttribute("msg", map.get("msg"));
			log.info(map.get("msg"));
		}

		
		return "register_h5/callback";
	}
	
	//充值回调的方法payNotyfy
	@RequestMapping(value = "/payNotyfy",method=RequestMethod.POST,produces="text/html;charset=UTF-8;")
	@ResponseBody
	public  String  payNotyfy(HttpServletRequest request,Model model) {
		String notifystr="";
		try {
			
			Map<String, Object> map=fundServiceimpl.payNotyfy(request);
			
		} catch (Exception e) {
			log.error("充值回调操作失败", e);
		}
	

		return "ok";
	}
	
	//保存提现信息
	  @RequestMapping(value={"/savePay"}, produces={"text/html;charset=UTF-8;"})
	  @ResponseBody
	  public String savePay(AgentzhfundLog afl, HttpSession session, Model model)
	  {
	    log.info("保存提现信息");
	    String subzh = (String)session.getAttribute("SESSION_LOGINNAME");
	    log.info("subzh===》" + subzh);
	    Map<String, Object> map = null;
	    if (subzh == null)
	    {
	      log.info("subzh:账户为null");
	      return "用户不存在";
	    }
	    try
	    {
	      map = this.fundServiceimpl.savePay(afl, subzh);
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	      log.error("savePay--保存体提现信息异常", e);
	    }
	    if ((map != null) && (map.size() > 0))
	    {
	      log.info("保存提现信息状态===》" + map.get("status"));
	      if ((Boolean)map.get("status")) {
	        return "ok";
	      }
	      return "提现失败";
	    }
	    return "提现失败";
	  }
	
	//审核并且提现
	@RequestMapping(value = "/isPay",produces="text/html;charset=UTF-8;")
	@ResponseBody
	public String isPay(Integer id, Integer status) {
		log.info("提现审核开始");
		Map<String, Object>map=fundServiceimpl.isPay(id,status);
		   if ((map != null) && (map.size() > 0)) {
		      log.info("保存提现信息状态===》" + map.get("status"));
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
	//查询提现的进度
	@RequestMapping(value = "/queryMoeney",produces="text/html;charset=UTF-8;")
	@ResponseBody
	public Map<String, Object> queryMoney(Integer id){
		Map<String, Object> map=new HashMap<String, Object>();
		if(null==id) {
			map.put("status", false);
			map.put("msg","审核信不存在");
			log.info("审核信不存在");
			return map;
		}
		
		try {
			AgentzhfundLog agentzhfundlog = agentzhfundLogService.selectByPrimaryKey(id);
			map=fundServiceimpl.saveGetMoney(agentzhfundlog.getOrderno(),agentzhfundlog.getSubzh());
			if(map==null){
				map.put("status", false);
				map.put("msg","用户调账失败");
			}
		} catch (Exception e) {
			log.error("保存用户调账信息失败", e);
		}
		
		return map;
		
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
		try {
		Fundset fundset = fundsetService.selectone();
		
		if(fundset!=null){
			if(payPage=="register_h5/fund_h5"&&fundset.getAppswitch()!=1) {
				log.info("平台禁止移动端入金");
				map.put("msg","平台禁止移动端入金");
				map.put("status",false);
				map.put("payPage",errorpay);
				return map;
			}
			if((payPage=="register_h5/fund_pc"||payPage=="register_h5/kpay_pc")&&fundset.getPcswitch()!=1){
				log.info("平台禁止客户端入金");
				map.put("msg","平台禁止客户端入金");
				map.put("status",false);
				map.put("payPage",errorpay);
				return map;
			}
			
			long currentTimeMillis = System.currentTimeMillis();			
			long starttime = DateUtil.getdateandtime(fundset.getStarttime()).getTime();
			long endtime = DateUtil.getdateandtime(fundset.getEndtime()).getTime();
			
			if(currentTimeMillis<starttime||currentTimeMillis>endtime){
				log.info("该时间段禁止客户端入金");
				map.put("msg","该时间段禁止客户端出入金");
				map.put("status",false);
				map.put("payPage",errorpay);
				return map;
			}
		}
		
		Subzh sub =null;
		if(null==subzh||subzh=="") {
			log.info("用户名不存在");
			map.put("msg","用户名不存在");
			map.put("status",false);
			map.put("payPage",errorpay);
			return map;
		}
		
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
			Integer isadmin=sub.getIsadmin();
			Boolean flag=true;
			if(payPage=="register_h5/fund_h5"||payPage=="register_h5/fund_pc") {
				if(null!=isadmin&&isadmin!=0&&isadmin!=6) {
					log.info("非普通用户无法充值");
					map.put("msg","非普通用户无法充值");
					map.put("status",false);
					map.put("payPage",errorpay);
					return map;
				}
			}
			//当用户操作的是提现，判读用户是否是普通用户,如果用户不是普通用户,则无需实名认证
			
			if(payPage=="register_h5/fundRaising"&&isadmin!=6) {					
					flag=false;		
					
			}else if(payPage=="register_h5/fundRaising"&&isadmin==6){
				if(fundServiceimpl.isPosition(sub)){//如果用户是普通用户-则当用户提现时需要判断用户是否有持仓
				map.put("msg","账户有持仓无法提现");
				map.put("status",false);
				map.put("payPage",errorpay);
				return map;
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