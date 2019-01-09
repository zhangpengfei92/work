package com.jcl.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jcl.dao.AgentzhfundLogMapper;
import com.jcl.dao.FundPzlogMapper;
import com.jcl.pojo.RightsRole;
import com.jcl.pojo.AgentzhfundLog;
import com.jcl.pojo.FundPzlog;
import com.jcl.pojo.FundPzlogExample;
import com.jcl.pojo.FundPzlogExample.Criteria;
import com.jcl.service.FundPzLogService;

@Service
@Transactional(rollbackFor=Exception.class)
public class FundPzLogServiceImpl implements FundPzLogService {

	@Autowired
	private FundPzlogMapper fundPzlogMapper;
	
	@Autowired
	private AgentzhfundLogMapper agentzhfundLogMapper;
	
	@Override
	public List<FundPzlog> selectAll() {
		return fundPzlogMapper.selectAll();
	}

	@Override
	public FundPzlog selectByPrimaryKey(Integer id) {
		return fundPzlogMapper.selectByPrimaryKey(id);
	}
	
	@Override
	public List<FundPzlog> selectByExample(FundPzlogExample example) {
		return fundPzlogMapper.selectByExample(example);
	}

	@Override
	public int deleteByPrimaryKey(Integer id) {
		/*FundPzlog record = FundPzlogMapper.selectByPrimaryKey(id);
		if(record != null){
			删除用户权限
			UserRightsExample example = new UserRightsExample();
			Criteria criteria = example.createCriteria();
			criteria.andUsernameEqualTo(record.getUsername());
			userRightsMapper.deleteByExample(example);
		}*/
		return fundPzlogMapper.deleteByPrimaryKey(id);
	}

	@Override
	public int updateByPrimaryKeyAndRole(FundPzlog record, List<RightsRole> roleList) {
		/*删除用户权限、更新用户权限
		UserRightsExample example = new UserRightsExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(record.getUsername());
		userRightsMapper.deleteByExample(example);
		if(roleList != null && roleList.size() > 0){//保存用户权限
			UserRights userRights = null;
			Date nowDate = Calendar.getInstance().getTime();
			for(RightsRole rightsRole : roleList){
				userRights = new UserRights();
				userRights.setUsername(record.getUsername());
				userRights.setInfo(rightsRole.getColumnId().toString());//权限ID
				userRights.setStarttime(record.getStarttime());//开始时间
				userRights.setEndtime(record.getEndtime());//结束时间
				userRights.setCreatedAt(nowDate);
				userRightsMapper.insertSelective(userRights);
			}
		}*/
		return fundPzlogMapper.updateByPrimaryKey(record);
	}
	
	@Override
	public int updateByPrimaryKey(FundPzlog record) {
		
		return	fundPzlogMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByid(String email, String phone, Integer id) {
		//return FundPzlogMapper.updateByid(email, phone, id);
		return 0;
	}
	
	@Override
	public int insertSelective(FundPzlog record, List<RightsRole> roleList) throws Exception {
		/*if(roleList != null && roleList.size() > 0){//保存用户权限
			UserRights userRights = null;
			Date nowDate = Calendar.getInstance().getTime();
			for(RightsRole rightsRole : roleList){
				userRights = new UserRights();
				userRights.setUsername(record.getUsername());
				userRights.setInfo(rightsRole.getColumnId().toString());//权限ID
				userRights.setStarttime(record.getStarttime());//开始时间
				userRights.setEndtime(record.getEndtime());//结束时间
				userRights.setCreatedAt(nowDate);
				userRightsMapper.insertSelective(userRights);
			}
		}*/
		return fundPzlogMapper.insertSelective(record);
	}
	
	/*@Override
	public List<Map<String,Object>> selectALlByFundPzlogAndAccount() {
		return FundPzlogMapper.selectALlByFundPzlogAndAccount();
	}*/
	
	@Override
	public List<FundPzlog> isExist(String username) {
		return fundPzlogMapper.isExist(username);
	}

	@Override
	public int insertFundPzlog(FundPzlog fpg) {
		return fundPzlogMapper.insertSelective(fpg);	
	}
	
	//根据subzh查询用户的资金流水
	@Override
	public List<FundPzlog> selectBySubzh(String subzh) {
		FundPzlogExample example=new FundPzlogExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andSubzhEqualTo(subzh);
		return fundPzlogMapper.selectByExample(example);
	}

	@Override
	public int fundAuto(AgentzhfundLog agentzhfundLog) {
		agentzhfundLog.setStatus(7);
		//agentzhfundLog.setModifytime(new Date());
		agentzhfundLog.setCreatetime(new Date());
		agentzhfundLogMapper.updateByPrimaryKeySelective(agentzhfundLog);
		FundPzlog fundPzlog = new FundPzlog();
		fundPzlog.setSubzh(agentzhfundLog.getSubzh());
		fundPzlog.setFundtype(7);
		fundPzlog.setSetpro(0);
		fundPzlog.setFundbalance(agentzhfundLog.getFund());
		fundPzlog.setCreatetime(new Date());
		int i = fundPzlogMapper.insertSelective(fundPzlog);
		return i;
	}

	@Override
	public int fundInto(AgentzhfundLog agentzhfundLog) {
		agentzhfundLog.setStatus(1);
		agentzhfundLog.setModifytime(new Date());
		agentzhfundLogMapper.updateByPrimaryKeySelective(agentzhfundLog);
		FundPzlog fundPzlog = new FundPzlog();
		fundPzlog.setSubzh(agentzhfundLog.getSubzh());
		fundPzlog.setFundtype(6);
		fundPzlog.setSetpro(1);
		fundPzlog.setFundbalance(agentzhfundLog.getFund());
		fundPzlog.setSetdesc(agentzhfundLog.getOrderno());
		fundPzlog.setCreatetime(new Date());
		int i = fundPzlogMapper.insertSelective(fundPzlog);
		if(i>0){
			return fundPzlog.getId();
		}
		
		return 0;
	}

	
}