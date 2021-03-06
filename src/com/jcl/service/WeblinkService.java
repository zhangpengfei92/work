/**
 * Copyright (C) 2009 武汉金策略信息科技有限公司
 *
 * 版权所有。
 *
 * 类名　　  : @WeblinkService.java
 * 功能概要  : 
 * 做成日期  : @2018年5月24日
 * 修改日期  :
 */
package com.jcl.service;

import java.util.List;

import com.jcl.pojo.News;
import com.jcl.pojo.Weblink;

/** 
 * @author zpf
 * @version 1.0
 */
public interface WeblinkService {

	List<Weblink> selectAll();

	Weblink queryByid(Integer id);

	int delete(Integer id);

	int add(Weblink weblink);

	int update(Weblink weblink);

}
