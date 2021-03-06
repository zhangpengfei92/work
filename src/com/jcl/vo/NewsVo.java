/**
 * Copyright (C) 2009 武汉金策略信息科技有限公司
 *
 * 版权所有。
 *
 * 类名　　  : @NewsVo.java
 * 功能概要  : 
 * 做成日期  : @2018年5月23日
 * 修改日期  :
 */
package com.jcl.vo;

import com.jcl.pojo.News;

/** 
 * @author zpf
 * @version 1.0
 * 
 * 扩展页面显示的新闻数据
 */
public class NewsVo extends News{
	
	private String newsday;
	
	private String newsmonth;
	
	private String newsyear;

	public String getNewsday() {
		return newsday;
	}

	public void setNewsday(String newsday) {
		this.newsday = newsday;
	}

	public String getNewsmonth() {
		return newsmonth;
	}

	public void setNewsmonth(String newsmonth) {
		this.newsmonth = newsmonth;
	}

	public String getNewsyear() {
		return newsyear;
	}

	public void setNewsyear(String newsyear) {
		this.newsyear = newsyear;
	}

}
