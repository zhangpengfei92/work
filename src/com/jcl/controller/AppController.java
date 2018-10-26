package com.jcl.controller;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;
import com.jcl.mongodb.BeanUtil;
import com.jcl.mongodb.MongoUtils;
import com.jcl.pojo.BzjmbMenu;
import com.jcl.pojo.CodefeesetMenu;
import com.jcl.pojo.RiskTempall;
import com.jcl.pojo.Subzh;
import com.jcl.pojo.UserInfo;
import com.jcl.service.BzjmbMenuService;
import com.jcl.service.CodefeesetMenuService;
import com.jcl.service.RiskTempallService;
import com.jcl.service.SubzhService;
import com.jcl.service.UserInfoService;
import com.jcl.util.Base64;
import com.jcl.util.StringUtil;
import com.jcl.util.Util;
import com.jcl.util.sms.MessageUtils;
import com.jcl.vo.Msm;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@RequestMapping("/app")
@Controller
public class AppController {
	
	@Autowired
	private SubzhService subzhService;
	
	@Autowired
	private UserInfoService userinfoserviceImpl;
	
	@Autowired
	private RiskTempallService riskTempallServiceImpl;
	
	@Autowired
	private BzjmbMenuService bzjmbMenuServiceimpl;
	
	@Autowired
	private CodefeesetMenuService codefeesetMenuService;
	
	public static Logger log = Logger.getLogger(AppController.class);
	
	/**
	 * 跳转到注册页面
	 * @param subzh
	 * @param model
	 * @return
	 */
	@RequestMapping("/skipRegister")
	public String skipRegister(Subzh subzh,Model model) {
		model.addAttribute("subzh", subzh);
		return "register_h5/registerDownload";
	}

	@RequestMapping("/downloadPage")
	public String skipdownpage() {
		return "register_h5/downloadPage";
	}
	
	
	/**
	 * 注册接口
	 * @return
	 */
	@RequestMapping(value="/reg",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String reg(Subzh subzh,String code) {
		try {
			if(StringUtils.isEmpty(subzh.getPhone())) return "手机号不能为空";
			subzh.setSubzh(subzh.getPhone());
			if(StringUtils.isEmpty(subzh.getPassword())) return "密码不能为空";
			Subzh subzh2 = subzhService.selectByPrimaryKeySubzh(subzh.getPhone());
			if(subzh2 != null) {
				return "用户已存在";
			}
			Subzh subzh3 = subzhService.selectByPhone(subzh.getPhone());
			if(subzh3 != null) {
				return "用户已存在";
			}
			MongoUtils.change("sms");	
			DBObject d = new BasicDBObject();
			d.put("phone", subzh.getPhone());
			DBCursor cursor = MongoUtils.queryByDBObject(d);
			Msm msm = null;
			while (cursor.hasNext()) {
				DBObject result = cursor.next();
				msm = BeanUtil.dbObject2Bean(result, new Msm());
				long time = (new Date().getTime()-msm.getTime())/1000;
				//System.out.println("进来了？"+time);
				if((new Date().getTime()-msm.getTime())/1000>60*5) {
					return "短信已失效,请重新发送";
				}
				if(StringUtils.isEmpty(code)) {
					return "短信验证码不能为空";
				}
				if(!code.equals(msm.getCode())) {
					return "验证码不匹配";
				}
			}
			if(msm==null) {
				return "找不到匹配的验证码";
			}
			subzh.setIsadmin(6);
			subzh.setKhdate(new Date());
			subzh.setIsDelete(0);//是否删除
			subzh.setAuthState(0);//实名认证
			byte[] data = subzh.getPhone().getBytes();
			long cookie = Util.Hash64(data, 0, data.length);// 生成随机的cookie
			if (cookie < 0) {
				subzh.setCookie(String.valueOf(Util.readUnsignedLong(cookie)));
			} else {
				subzh.setCookie(String.valueOf(cookie));
			}
			subzh.setIsDelete(0);
			subzh.setZhstate(0);
			//查询风控菜单
			List<RiskTempall> rlist = riskTempallServiceImpl.selectAllByOrderBy();
			//查询手续费方案
			List<CodefeesetMenu> flist = codefeesetMenuService.selectAllByOrderBy();	
			//查询保证金模板
			List<BzjmbMenu> bzlist = bzjmbMenuServiceimpl.selectAllByOrderBy();
			if(rlist != null && rlist.size() > 0) {
				subzh.setRiskset(rlist.get(0).getId());
			}
			if(flist != null && flist.size() > 0){
				subzh.setFeeset(String.valueOf(flist.get(0).getId()));
			}
			if(bzlist != null && bzlist.size() > 0) {
				subzh.setMarginset(String.valueOf(bzlist.get(0).getId()));
			}
			int result = subzhService.insertSelective(subzh);
			if(result > 0) {
				//将用户信息保存到认证库
				UserInfo userInfo = new UserInfo();
				userInfo.setUsername(subzh.getSubzh());
				userInfo.setPassword(subzh.getPassword());
				userInfo.setTelephone(subzh.getPhone());
				int add = userinfoserviceImpl.add(userInfo);
				if(add > 0) {
					return "ok";
				}else {
					//删除
					boolean deleteBySubzh = subzhService.deleteBySubzh(subzh.getSubzh());
					if(!deleteBySubzh) {
						log.info("认证库插入失败，subzh表中：["+subzh.getSubzh()+"]删除失败！");
						return "数据回滚失败";
					}
					return "认证库插入失败";
				}
			}
			return "注册失败";
		} catch (Exception e) {
			log.info("注册失败");
			e.printStackTrace();
			return "系统异常";
		}
	}
	
	/**
	 * 短信发送接口
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/sendsms",produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String sendsms(String phone) {
		try {
			if(StringUtils.isEmpty(phone))return "手机号不能为空";
			if(!StringUtil.regularExpression(phone, "^(13|15|18|17|14)\\d{9}$")) {
				return "请输入正确的手机号格式";
			}
			/*Subzh subzh2 = subzhService.selectByPrimaryKeySubzh(phone);
			if(subzh2 != null) {
				return "用户已存在";
			}*/
			MongoUtils.change("sms");	
			DBObject d = new BasicDBObject();
			d.put("phone", phone);
			MongoUtils.deleteDB(d);
			String code = String.valueOf((int)(Math.random()*9000)+1000);
			//MessageUtils.sendMassager(phone, code);sendMsg
			log.info("手机验证："+code);
			String content = URLEncoder.encode("验证码:" + code, "GBK");   
			MessageUtils.sendMassager(phone, content);//阿里云短信模板 ,西奥
			DBObject db = new BasicDBObject();
			db.put("code", code);
			db.put("phone", phone);
			db.put("time", new Date().getTime());
			MongoUtils.addOne(db);
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "NoOk";
		}
	}
	
	/**
	 * 验证手机号是否唯一
	 * @param phone
	 * @return
	 */
	@RequestMapping(value = "/verifyPhone",produces = "text/plain;charset=utf-8")
	@ResponseBody
	public String verifyPhone(String phone) {
		try {
			Subzh subzh2 = subzhService.selectByPrimaryKeySubzh(phone);
			if(subzh2 != null) {
				return "用户已存在";
			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
			return "系统验证异常";
		}	
	}
	
	@RequestMapping("/getRegisterProtocol")
	public String getRegisterProtocol() {
		return "register_h5/registerProtocol";
	}
	
	
	/**
	 * 手机端短信发送接口
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/sendsmsPort",produces = "text/json;charset=UTF-8")
	@ResponseBody
	public String sendsmsPhone(HttpServletRequest request) {
		Map<String,String> dataMap = new HashMap<String,String>();
		try {
			String phone = request.getParameter("phone");
			if(StringUtils.isEmpty(phone)) {
				dataMap.put("code", "500");
				dataMap.put("msg", "手机号不能为空");
				return JSON.toJSONString(dataMap);
			}
			if(!StringUtil.regularExpression(phone, "^(13|15|18|17|14)\\d{9}$")) {
				dataMap.put("code", "501");
				dataMap.put("msg", "请输入正确的手机号格式");
				return JSON.toJSONString(dataMap);
			}
			Subzh subzh2 = subzhService.selectByPrimaryKeySubzh(phone);
			if(subzh2 != null) {
				dataMap.put("code", "502");
				dataMap.put("msg", "用户已存在");
				return JSON.toJSONString(dataMap);
			}
			MongoUtils.change("sms");	
			DBObject d = new BasicDBObject();
			d.put("phone", phone);
			MongoUtils.deleteDB(d);
			String code = String.valueOf((int)(Math.random()*9000)+1000);
			log.info("手机验证"+code);
			String content = URLEncoder.encode("验证码:" + code, "GBK");   
			MessageUtils.sendMassager(phone, content);//阿里云短信模板 ,西奥
			DBObject db = new BasicDBObject();
			db.put("code", code);
			db.put("phone", phone);
			db.put("time", new Date().getTime());
			MongoUtils.addOne(db);
			dataMap.put("code", "200");
			dataMap.put("msg", "短信发送成功");
			return JSON.toJSONString(dataMap);
		} catch (Exception e) {
			dataMap.put("code", "503");
			dataMap.put("msg", "系统异常");
			return JSON.toJSONString(dataMap);
		}
	}
	
	
	/**
	 * 手机端注册接口
	 * @return
	 */
	@RequestMapping(value="/regPort",produces="text/json;charset=UTF-8")
	@ResponseBody
	public String regPort(HttpServletRequest request) {
		Map<String,String> dataMap = new HashMap<String,String>();
		Subzh subzh = new Subzh();
		try {
			String phone = request.getParameter("phone").trim();
			String code = request.getParameter("code").trim();
			String password = new String(Base64.decode(request.getParameter("password")),"iso-8859-1");
			String affirmPassword = new String(Base64.decode(request.getParameter("affirmPassword")),"iso-8859-1");
			String inviteCode = request.getParameter("inviteCode").trim();
			if(StringUtils.isEmpty(phone)) {
				dataMap.put("code", "500");
				dataMap.put("msg", "手机号不能为空");
				return JSON.toJSONString(dataMap);
			}
			if(!StringUtil.regularExpression(phone, "^(13|15|18|17|14)\\d{9}$")) {
				dataMap.put("code", "501");
				dataMap.put("msg", "请输入正确的手机号格式");
				return JSON.toJSONString(dataMap);
			}
			MongoUtils.change("sms");	
			DBObject d = new BasicDBObject();
			d.put("phone", phone);
			DBCursor cursor = MongoUtils.queryByDBObject(d);
			Msm msm = null;
			while (cursor.hasNext()) {
				DBObject result = cursor.next();
				msm = BeanUtil.dbObject2Bean(result, new Msm());
				long time = (new Date().getTime()-msm.getTime())/1000;
				if(StringUtils.isEmpty(code)) {
					dataMap.put("code", "502");
					dataMap.put("msg", "短信验证码不能为空");
					return JSON.toJSONString(dataMap);
				}
				if((new Date().getTime()-msm.getTime())/1000>60*5) {
					dataMap.put("code", "503");
					dataMap.put("msg", "短信已失效,请重新发送");
					return JSON.toJSONString(dataMap);
				}
				if(!code.equals(msm.getCode())) {
					dataMap.put("code", "504");
					dataMap.put("msg", "验证码不匹配");
					return JSON.toJSONString(dataMap);
				}
			}
			if(msm==null) {
				dataMap.put("code", "505");
				dataMap.put("msg", "找不到匹配的验证码");
				return JSON.toJSONString(dataMap);
			}
			subzh.setSubzh(phone);
			if(StringUtils.isEmpty(password)) {
				dataMap.put("code", "506");
				dataMap.put("msg", "密码不能为空");
				return JSON.toJSONString(dataMap);
			}
			if(StringUtils.isEmpty(affirmPassword)) {
				dataMap.put("code", "507");
				dataMap.put("msg", "确认密码不能为空");
				return JSON.toJSONString(dataMap);
			}
			if(!password.equals(affirmPassword)) {
				dataMap.put("code", "508");
				dataMap.put("msg", "密码与确认密码不一致");
				return JSON.toJSONString(dataMap);
			}
			if(StringUtils.isEmpty("inviteCode")) {
				dataMap.put("code", "509");
				dataMap.put("msg", "邀请码不能为空");
				return JSON.toJSONString(dataMap);
			}
			Subzh subzh2 = subzhService.selectByPrimaryKeySubzh(phone);
			if(subzh2 != null) {
				dataMap.put("code", "510");
				dataMap.put("msg", "用户已存在");
				return JSON.toJSONString(dataMap);
			}
			Subzh subzh3 = subzhService.selectByPhone(phone);
			if(subzh3 != null) {
				dataMap.put("code", "510");
				dataMap.put("msg", "用户已存在");
				return JSON.toJSONString(dataMap);
			}
			Subzh inviter = subzhService.getSubzhByInviteCode(inviteCode);
			if(inviter != null) {
				subzh.setPagentzh(inviter.getSubzh());
				subzh.setManage(inviter.getManage());
				subzh.setAllocpt(inviter.getAllocpt());
				if (inviter.getIsadmin() == 3) {//渠道
				    subzh.setAllocchannel(inviter.getSubzh());
                }
				if (inviter.getIsadmin() == 4) {//代理
					subzh.setAllocchannel(inviter.getAllocchannel());
					subzh.setAllocagent(inviter.getSubzh());
				}
				if (inviter.getIsadmin() == 5) {//经纪人
					subzh.setAllocchannel(inviter.getAllocchannel());
					subzh.setAllocagent(inviter.getAllocagent());
					subzh.setAllocbroker(inviter.getSubzh());
				}
			}else {
				dataMap.put("code", "511");
				dataMap.put("msg", "不存在的邀请码");
				return JSON.toJSONString(dataMap);
			}
			subzh.setPassword(password);
			subzh.setIsadmin(6);
			subzh.setKhdate(new Date());
			subzh.setIsDelete(0);//是否删除
			subzh.setAuthState(0);//实名认证
			subzh.setPhone(phone);
			byte[] data = phone.getBytes();
			long cookie = Util.Hash64(data, 0, data.length);// 生成随机的cookie
			if (cookie < 0) {
				subzh.setCookie(String.valueOf(Util.readUnsignedLong(cookie)));
			} else {
				subzh.setCookie(String.valueOf(cookie));
			}
			subzh.setZhstate(0);
			//查询风控菜单
			List<RiskTempall> rlist = riskTempallServiceImpl.selectAllByOrderBy();
			//查询手续费方案
			List<CodefeesetMenu> flist = codefeesetMenuService.selectAllByOrderBy();	
			//查询保证金模板
			List<BzjmbMenu> bzlist = bzjmbMenuServiceimpl.selectAllByOrderBy();
			if(rlist != null && rlist.size() > 0) {
				subzh.setRiskset(rlist.get(0).getId());
			}
			if(flist != null && flist.size() > 0){
				subzh.setFeeset(String.valueOf(flist.get(0).getId()));
			}
			if(bzlist != null && bzlist.size() > 0) {
				subzh.setMarginset(String.valueOf(bzlist.get(0).getId()));
			}
			int result = subzhService.insertSelective(subzh);
			if(result > 0) {
				//将用户信息保存到认证库
				UserInfo userInfo = new UserInfo();
				userInfo.setUsername(subzh.getSubzh());
				userInfo.setPassword(subzh.getPassword());
				userInfo.setTelephone(subzh.getPhone());
				int add = userinfoserviceImpl.add(userInfo);
				if(add > 0) {
					dataMap.put("code", "200");
					dataMap.put("msg", "注册成功");
					return JSON.toJSONString(dataMap);
				}else {
					//删除
					boolean deleteBySubzh = subzhService.deleteBySubzh(subzh.getSubzh());
					if(!deleteBySubzh) {
						log.info("手机注册接口认证库插入失败，subzh表中：["+subzh.getSubzh()+"]删除失败！");
						dataMap.put("code", "512");
						dataMap.put("msg", "认证库插入失败，数据回滚失败");
						return JSON.toJSONString(dataMap);
					}
					dataMap.put("code", "513");
					dataMap.put("msg", "认证库插入失败");
					return JSON.toJSONString(dataMap);
				}
			}
			dataMap.put("code", "514");
			dataMap.put("msg", "注册失败");
			return JSON.toJSONString(dataMap);
		} catch (Exception e) {
			dataMap.put("code", "515");
			dataMap.put("msg", "系统异常");
			return JSON.toJSONString(dataMap);
		}
	}
}
