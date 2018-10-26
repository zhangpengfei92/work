/**
 * Copyright (C) 2009 武汉金策略信息科技有限公司
 *
 * 版权所有。
 *
 * 类名　　  : FundPzlogService.java
 * 功能概要  : 资金流水信息管理接口类
 * 做成日期  : 2018年04月16日 
 * 修改日期  :
 */
package com.jcl.service;

import java.util.List;

import com.jcl.pojo.RightsRole;
import com.jcl.pojo.FundPzlog;
import com.jcl.pojo.FundPzlogExample;

/**
 * 资金流水信息管理接口类
 * 
 * @author jiangzq
 * @version 1.0
 */
public interface FundPzLogService {

	/**
	 * 查询所有资金流水信息
	 * @return list
	 * @throws Exception
	 */
	List<FundPzlog> selectAll();

	/**
	 * 根据条件获取资金流水列表集合
	 * @param FundPzlogExample
	 * @return list
	 * @throws Exception
	 */
	List<FundPzlog> selectByExample(FundPzlogExample example);
	
	/**
	 * 根据主键查询提现信息实体
	 * @param id 主键
	 * @return FundPzlog
	 * @throws Exception
	 */
	FundPzlog selectByPrimaryKey(Integer id);

	/**
	 * 删除资金流水信息实体
	 * @param FundPzlog  资金流水信息实体
	 * @return boolean true 成功/false 失败
	 * @throws Exception
	 */
	int deleteByPrimaryKey(Integer id);
	
	/**
	 * 更新用户权限
	 * @param record 资金流水信息实体
	 * @return int
	 * @throws Exception
	 */
	int updateByPrimaryKeyAndRole(FundPzlog record, List<RightsRole> roleList);

	/**
	 * 更新资金流水信息实体
	 * @param record 资金流水信息实体
	 * @return int
	 * @throws Exception
	 */
	int updateByPrimaryKey(FundPzlog record);

	/**
	 * 更新资金流水信息实体
	 * @param 资金流水信息
	 * @return int
	 * @throws Exception
	 */
	int updateByid(String email, String phone, Integer id);
	
	/**
	 * 保存资金流水信息实体
	 * @param FundPzlog  资金流水信息实体
	 * @return int
	 * @throws Exception
	 */
	int insertSelective(FundPzlog record, List<RightsRole> roleList) throws Exception;
	
	/**
	 * 判断资金流水信息是否存在
	 * @param typeId  资金流水信息id
	 * 		  name 用户名
	 * @return boolean true 存在/false 不存在
	 * @throws Exception
	 */
	List<FundPzlog> isExist(String username);

	int insertFundPzlog(FundPzlog fpg);

	List<FundPzlog> selectBySubzh(String subzh);


}