package com.jcl.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jcl.pojo.RightsColumn;
import com.jcl.pojo.RightsRole;
import com.jcl.pojo.Role;
import com.jcl.pojo.Subzh;
import com.jcl.pojo.Sysuser;
import com.jcl.pojo.UserInfo;
import com.jcl.service.RightsColumnService;
import com.jcl.service.RightsRoleService;
import com.jcl.service.RoleService;
import com.jcl.service.SubzhService;
import com.jcl.service.SysuserService;
import com.jcl.service.UserInfoService;
import com.jcl.util.Constant;
import com.jcl.util.StringUtil;

@Controller
@RequestMapping("/membersys")
public class WardenController {
	
	public static Logger log = Logger.getLogger(WardenController.class);

	//注入子账户服务层
	@Autowired
	private SubzhService subzhservice;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private SysuserService sysuserService;
	
	@Autowired
	private RightsColumnService rightsColumnService;
	
	/*权限角色Service*/
	@Autowired
	private RightsRoleService rightsRoleService;
	
	@Autowired
	private UserInfoService userInfoService;
	
	// 管理员列表页面跳转
	@RequestMapping("/jumpwardenList")
	public String jumpwarden(HttpSession session, String username,Model model) throws UnsupportedEncodingException {
		session.setAttribute(Constant.SESSION_LEFTMENU, "12");
		Subject currentUser = SecurityUtils.getSubject();
		if(currentUser.hasRole("sys_user")){
			if(null != username){
				username= new String(username.getBytes("ISO-8859-1"),"UTF-8");  
			}
			List<Sysuser> sysusers = sysuserService.getListByLikeName(username);
			model.addAttribute("username", username);
			model.addAttribute("sysusers", sysusers);
			return "member/wardenList";
		}else{
			return "redirect:/membersys/jumproleList";
		}
	}

	// 角色列表页面跳转
	@RequestMapping("/jumproleList")
	public String jumproleList(String name,Model model) throws UnsupportedEncodingException {
		String returnName = "";
		if(null != name){
			if(name=="" || name.equals("")){
				name = null;
			}else{
				//name = new String(name.getBytes("ISO-8859-1"),"UTF-8"); 
				returnName = name;
				name = "%"+name+"%";
			}
		}
		List<RightsRole> roles = rightsRoleService.getListByLikeName(name);
		model.addAttribute("name", returnName);
		model.addAttribute("rightsRoles", roles);
		return "member/roleList";
	}
	
	
	//批量删除管理员
	@RequestMapping("/deleteWarden")
	@ResponseBody
	public String deleteWarden(Integer[] ids){
		int j = 0;
		boolean isadmin = false;
		if (ids != null) {
			for (Integer id : ids) {
				Sysuser sysuser = sysuserService.selectByID(id);
				if(sysuser.getUsername().equals("admin")){
					isadmin = true;
					continue;
				}else{
					j = sysuserService.deleteByPrimaryKey(id);
				}
			}
		}
		if(isadmin){
			return "admin";
		}else{
			if (j > 0) {
				return "true";
			} else {
				return "false";
			}
		}
	}

	//删除单个管理员
	@RequestMapping("/deleteWardenOne")
	@ResponseBody
	public String deleteWardenOne(Integer id){
		Sysuser sysuser = sysuserService.selectByID(id);
		if(sysuser.getUsername().equals("admin")){
			return "admin";
		}else{
			int	j = sysuserService.deleteByPrimaryKey(id);
			if(j > 0) {
				return "true";
			} else {
				return "false";
			}
		}
	}
	
	
	//添加管理员
	@RequestMapping("/jumpAddWarden")
	public String jumpAddWarden(Integer id,Model model) {
		Sysuser sysuser = sysuserService.selectByID(id);
		int roleId = 0;
		if(null != sysuser && sysuser.getRoleList() != null && sysuser.getRoleList().size() > 1){
			roleId = sysuser.getRoleList().get(0).getId();
		}
		List<Role> roles = roleService.getListByLikeName(null);
		model.addAttribute("sysuser", sysuser);
		model.addAttribute("roles", roles);
		model.addAttribute("roleId", roleId);
		return "member/addwarden";
	}
	
	//添加角色
	@RequestMapping("/jumpAddRole")
	public String jumpAddRole(Integer id,Model model) {
		/*Role role = roleService.selectByPrimaryKey(id);
		model.addAttribute("role", role);*/
		
		/*Role role = roleService.selectById(id);
		List<Integer> perids = new ArrayList<Integer>();
		if(null != role){
			List<Permission> permissions = role.getPerlist();
			for (Permission permission : permissions) {
				perids.add(permission.getpId());
			}
		}
		model.addAttribute("role", role);
		model.addAttribute("perids", perids);*/
		/*查询权限子栏目列表*/
		try {
			RightsRole role = null;
			List<RightsRole> roleList = rightsRoleService.selectById(id);
			List<Integer> perids = new ArrayList<Integer>();
			if(null != roleList && roleList.size() > 0){
				role = roleList.get(0);
				for (RightsRole rightsRole : roleList) {
					perids.add(rightsRole.getColumnId());
				}
			}
			model.addAttribute("role", role);
			model.addAttribute("perids", perids);
			
			Map<String,Object> mapCondition = new HashMap<String, Object>();
			mapCondition.put("status", Constant.SETTLE_PROFIT_TYPE_DAY);
			List<RightsColumn> columnList = rightsColumnService.selectRightsColumnListByCondition(mapCondition);
			/*分组显示*/
			if(columnList != null && columnList.size() > 0){
				List<RightsColumn> columnList_1 = new ArrayList<RightsColumn>();
				List<RightsColumn> columnList_2 = new ArrayList<RightsColumn>();
				List<RightsColumn> columnList_3 = new ArrayList<RightsColumn>();
				List<RightsColumn> columnList_4 = new ArrayList<RightsColumn>();
				List<RightsColumn> columnList_5 = new ArrayList<RightsColumn>();
				List<RightsColumn> columnList_6 = new ArrayList<RightsColumn>();
				if(columnList.size() < 6){
					columnList_1 = columnList;
				} else if (columnList.size() < 11){
					for(int i = 0; i < columnList.size(); i++){
						if(i < 5){
							columnList_1.add(columnList.get(i));
						} else {
							columnList_2.add(columnList.get(i));
						} 
					}
				} else if (columnList.size() < 16){
					for(int i = 0; i < columnList.size(); i++){
						if(i < 5){
							columnList_1.add(columnList.get(i));
						} else if(i < 10) {
							columnList_2.add(columnList.get(i));
						} else {
							columnList_3.add(columnList.get(i));
						} 
					}
				} else if (columnList.size() < 21){
					for(int i = 0; i < columnList.size(); i++){
						if(i < 5){
							columnList_1.add(columnList.get(i));
						} else if(i < 10) {
							columnList_2.add(columnList.get(i));
						} else if(i < 15) {
							columnList_3.add(columnList.get(i));
						} else {
							columnList_4.add(columnList.get(i));
						} 
					}
				} else if (columnList.size() < 26){
					for(int i = 0; i < columnList.size(); i++){
						if(i < 5){
							columnList_1.add(columnList.get(i));
						} else if(i < 10) {
							columnList_2.add(columnList.get(i));
						} else if(i < 15) {
							columnList_3.add(columnList.get(i));
						} else if(i < 20) {
							columnList_4.add(columnList.get(i));
						} else {
							columnList_5.add(columnList.get(i));
						} 
					}
				} else {
					for(int i = 0; i < columnList.size(); i++){
						if(i < 5){
							columnList_1.add(columnList.get(i));
						} else if(i < 10) {
							columnList_2.add(columnList.get(i));
						} else if(i < 15) {
							columnList_3.add(columnList.get(i));
						} else if(i < 20) {
							columnList_4.add(columnList.get(i));
						} else if(i < 25) {
							columnList_5.add(columnList.get(i));
						} else {
							columnList_6.add(columnList.get(i));
						} 
					}
				}
				model.addAttribute("columnList1", columnList_1);
				model.addAttribute("columnList2", columnList_2);
				model.addAttribute("columnList3", columnList_3);
				model.addAttribute("columnList4", columnList_4);
				model.addAttribute("columnList5", columnList_5);
				model.addAttribute("columnList6", columnList_6);
			}
		} catch (Exception e) {
			log.error("查询权限子栏目集合异常：" + e.getMessage());
		}
		return "member/addrole";
	}
	
	//添加权限 子栏目
	@RequestMapping("/jumpAddSubRole")
	public String jumpAddSubRole(Model model) {
		return "member/addsubrole";
	}
	
	/*权限子栏目是否存在*/
	@RequestMapping(value = "/isExistColumn", produces = "text/html; charset=utf-8")
	@ResponseBody
	public String isExistColumn(String name) {
		String returnstr = "";
		try {
			if(!StringUtil.isAnyNullOrEmpty(name)){
				List<RightsColumn> columnList = rightsColumnService.isExist(name);
				if(null != columnList && columnList.size() > 0){
					returnstr =  "true";
				} else {
					returnstr = "false";
				}
			} else {
				returnstr =  "true";
			}
		} catch (Exception e) {
			log.error("查询权限子栏目是否存在异常：" + e.getMessage());
		}
		
		return returnstr;
	}
	
	//保存权限子栏目
	@RequestMapping("/saveRightsColumn")
	@ResponseBody
	public String saveRightsColumn(RightsColumn column) {
		if(column.getColumnname() == null){
			return "redirect:/membersys/jumproleList";
		}
		try {
			column.setModifytime(Calendar.getInstance().getTime());
			rightsColumnService.insertSelective(column);
		} catch (Exception e) {
			log.error("保存权限子栏目异常：" + e.getMessage());
			return "false";
		}
		return "true";
	}
	
	//保存管理员
	@RequestMapping("/saveWarden")
	@ResponseBody
	public String saveWarden(Sysuser sysuser, Integer rid) {
		if(StringUtil.isAnyNullOrEmpty(sysuser.getUsername())){
			//return "redirect:/membersys/jumpwardenList";
			return "false";
		}
		try {
			if(null == sysuser.getId()){
				sysuser.setModifytime(new Date());
				sysuser.setStatus(1);
				List<Sysuser> sysusersList = sysuserService.isExist(sysuser.getUsername());
				if(null == sysusersList || sysusersList.size() == 0){
					sysuserService.insert(sysuser);
				}
			} else {
				sysuser.setModifytime(new Date());
				sysuser.setStatus(1);
				sysuserService.update(sysuser);
			}
			int sysuserId = sysuser.getId();
			if(sysuserId > 0){
				sysuserService.insertUserRole(sysuserId, rid);
				return "true";
			} else {
				return "false";
			}
		} catch (Exception e) {
			log.error("保存系统账户异常：" + e.getMessage());
			return "false";
		}
		//return "redirect:/membersys/jumpwardenList";
	}
	
	//保存角色
	@RequestMapping("/saverole")
	@ResponseBody
	public String saverole(RightsRole role, String rightsColumnData) {
		try {
			String [] rightsColumnDataArr = rightsColumnData.split(",");
			String desc = "";
			if(null == role.getId()){
				role.setKeyword(role.getRolename());
				role.setCreatetime(new Date());
				/*查询栏目列表*/
				Map<String,Object> mapCondition = new HashMap<String, Object>();
				List<RightsColumn> list = rightsColumnService.selectRightsColumnListByCondition(mapCondition);
				if(list != null && list.size() > 0){
					for(int i = 0; i < rightsColumnDataArr.length; i++){
						for(RightsColumn column : list){
							if(rightsColumnDataArr[i].equals(column.getId().toString())){
								if(i == (rightsColumnDataArr.length-1)){
									desc = desc + column.getColumnname();
								} else {
									desc = desc + column.getColumnname() + "、";
								}
								break;
							}
						}
					}
				}
				role.setDescript(desc);
				rightsRoleService.insertSelective(role);
			} else {
				RightsRole role2 = rightsRoleService.selectByPrimaryKey(role.getId());
				role.setCreatetime(role2.getCreatetime());
				role.setKeyword(role.getRolename());
				/*查询栏目列表*/
				Map<String,Object> mapCondition = new HashMap<String, Object>();
				List<RightsColumn> list = rightsColumnService.selectRightsColumnListByCondition(mapCondition);
				if(list != null && list.size() > 0){
					for(int i = 0; i < rightsColumnDataArr.length; i++){
						for(RightsColumn column : list){
							if(rightsColumnDataArr[i].equals(column.getId().toString())){
								if(i == (rightsColumnDataArr.length-1)){
									desc = desc + column.getColumnname();
								} else {
									desc = desc + column.getColumnname() + "、";
								}
								break;
							}
						}
					}
				}
				role.setDescript(desc);
				rightsRoleService.update(role);
			}
			int roleId = role.getId();
			//保存权限
			rightsRoleService.insertRightsRolePer(roleId, rightsColumnDataArr);
		} catch (Exception e) {
			log.error("保存权限角色信息异常：" + e.getMessage());
			return "false";
		}
		
		//return "redirect:/membersys/jumproleList";
		return "true";
	}
	
	//删除单个角色
	@RequestMapping("/deleteRoleOne")
	@ResponseBody
	public String deleteRoleOne(Integer id){
		//int	j = roleService.deleteByPrimaryKey(id);
		int	j = rightsRoleService.deleteByPrimaryKey(id);
		if(j > 0) {
			return "true";
		} else {
			return "false";
		}
	}
	
	//批量删除角色
	@RequestMapping("/deleteRole")
	@ResponseBody
	public String deleteRole(Integer[] ids){
		int j = 0;
		if (ids != null) {
			for (Integer id : ids) {
				j = roleService.deleteByPrimaryKey(id);
			}
		}
		if (j > 0) {
			return "true";
		} else {
			return "false";
		}
	}

	@RequestMapping(value = "/isExist/{type}", produces = "text/html; charset=utf-8")
	@ResponseBody
	public String isExist(String name,@PathVariable String type){
		String returnstr = "";
		if(type.equals("role")){
			List<Role> roles = roleService.isExist(name);
			if(null != roles && roles.size()>0){
				returnstr =  "true";
			}else{
				returnstr = "false";
			}
		}else if(type.equals("sysuser")){
			List<Sysuser> sysusers = sysuserService.isExist(name);
			if(null != sysusers && sysusers.size()>0){
				returnstr =  "true";
			}else{
				returnstr = "false";
			}
		}
		return returnstr;
	}
	
	//跳转修改密码页面
	@RequestMapping("/modifyPassword")
	public String modifyPassword(){
		return "member/modifypassword";
	}
	
	//修改密码
	@RequestMapping(value = "/savePass", produces = "text/html; charset=utf-8")
	@ResponseBody
	public String savePass(String oldpass,String newpass,HttpSession session){
		String userid=(String)session.getAttribute(Constant.SESSION_ACCOUNTID);
		Subzh sub=subzhservice.getPuriSubzh(userid);
		if(!sub.getPassword().equals(oldpass)){
			return "passerror";
		}else{
			sub.setPassword(newpass);
			int  num=subzhservice.updateByPrimaryKey(sub);
			if(num>0){
				
				UserInfo user=new UserInfo();
				user.setUsername(sub.getSubzh());
				user.setPassword(newpass);
				userInfoService.updateByPrimaryKey(user);
			}
			return "true";
		}
	}
	
	//处理时间
	/*@InitBinder    
	public void initBinder(WebDataBinder binder) {   
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		 dateFormat.setLenient(false);   
		 binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));   //true:允许输入空值，false:不能为空值 
	}*/
}