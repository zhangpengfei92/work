/**
 * Copyright (C) 2009 武汉金策略信息科技有限公司
 *
 * 版权所有。
 *
 * 类名　　  : @BdzhServiceImpl.java
 * 功能概要  : 
 * 做成日期  : @2018年4月13日
 * 修改日期  :
 */
package com.jcl.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jcl.dao.BdzhMapper;
import com.jcl.pojo.Bdzh;
import com.jcl.pojo.BdzhExample;
import com.jcl.service.BdzhService;
import com.jcl.stock.simulate.StringUtil;

/** 
 * @author zpf
 * @version 1.0
 * 
 * 保单账户表--业务处理
 */

@Service
public class BdzhServiceImpl implements BdzhService{
	
	@Autowired
	private BdzhMapper bdzhMapper;
	
	//全查询
	@Override
	public List<Bdzh> queryAll() {
		
		return bdzhMapper.selectAll();
	}

	//条件查询
	@Override
	public List<Bdzh> queryBy(Bdzh bdzh) {
		List<Bdzh> list=bdzhMapper.selectByBdzh(bdzh);
		return list;
	}
	
	//添加报单账户
	@Override
	public int addBdzh(Bdzh bdzh) {
		
		return bdzhMapper.insertSelective(bdzh);		
	}

	@Override
	public void deleteBdzh(String accountid) {
	
		bdzhMapper.updateZhstate(accountid);
		
	}
	
	public Bdzh selectByPrimaryKey(Integer bdzhid){
		Bdzh bdzh = bdzhMapper.selectByPrimaryKey(bdzhid);
		if(bdzh!=null && !StringUtil.isAnyNullOrEmpty(bdzh.getConfig())){
			JSONObject jsonArray=JSONArray.parseObject(bdzh.getConfig());
			jsonArray.get("server_ip");
			bdzh.setServerip(jsonArray.get("server_ip").toString());
			bdzh.setBroker(jsonArray.get("broker").toString());
		}
		
		return bdzh;
	}
	
	public List<Bdzh> selectLikeAccount(BdzhExample example){
		return bdzhMapper.selectByExample(example);
	}
	
	public int updateBdzh(Bdzh bdzh){
		
		return bdzhMapper.updateByPrimaryKeySelective(bdzh);
	}
	
	public boolean isexist(Bdzh bdzh){
		List<Bdzh> list=bdzhMapper.isexist(bdzh);
		if(list!=null && list.size()>0){
			return true;
		}else{
			return false;
		}
		
	}
}
