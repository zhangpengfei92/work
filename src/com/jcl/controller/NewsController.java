/**
 * Copyright (C) 2009 武汉金策略信息科技有限公司
 *
 * 版权所有。
 *
 * 类名　　  : @NewController.java
 * 功能概要  : 
 * 做成日期  : @2018年5月23日
 * 修改日期  :
 */
package com.jcl.controller;
/** 
 * @author zpf
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jcl.comm.PaginationContext;
import com.jcl.pojo.CooperationPartner;
import com.jcl.pojo.News;
import com.jcl.service.CooperationPartnerService;
import com.jcl.service.NewsService;
import com.jcl.util.DateUtil;
import com.jcl.vo.NewsVo;

@RequestMapping("/news")
@Controller
public class NewsController {
	
	public static Logger log = Logger.getLogger(HomePageController.class);
	
	@Autowired
	private  NewsService newsserviceimpl;
	
	//@RequestMapping("/news/{nid}")
	
	//展示首页的内容
	@RequestMapping("/zxzt/{pageNum}")
	public String zxzt(Model model,@PathVariable Integer pageNum) {
		try {
			//使用PageHelper的分页----设置分页参数
			PageHelper.startPage(pageNum,PaginationContext.getPageSize());
			
			//查询最热专题
			List<News> list=newsserviceimpl.queryNewsByType(1);
			List<NewsVo> nlist =poTovo(list);
			PageInfo<News> userpage = new PageInfo<News>(list);
			model.addAttribute("nlist", nlist);
			model.addAttribute("userpage", userpage);
		} catch (Exception e) {
			log.error("showHomepage-查询新闻列表异常", e);
		}
		return "news1";	
	}
	
	//展示首页的内容
	@RequestMapping("/gsxw/{pageNum}")
	public String gsxw(Model model,@PathVariable Integer pageNum) {
		try {
			//使用PageHelper的分页----设置分页参数
			PageHelper.startPage(pageNum,PaginationContext.getPageSize());
			
			//查询最热专题
			List<News> list=newsserviceimpl.queryNewsByType(2);
			List<NewsVo> nlist =poTovo(list);
			PageInfo<News> userpage = new PageInfo<News>(list);
			model.addAttribute("nlist", nlist);
			model.addAttribute("userpage", userpage);
		} catch (Exception e) {
			log.error("showHomepage-查询新闻列表异常", e);
		}
		return "news2";	
	}
	
	//展示首页的内容
	@RequestMapping("/zxdt/{pageNum}")
	public String zxdt(Model model,@PathVariable Integer pageNum) {
		try {
			//使用PageHelper的分页----设置分页参数
			PageHelper.startPage(pageNum,PaginationContext.getPageSize());
			
			//查询最热专题
			List<News> list=newsserviceimpl.queryNewsByType(3);
			List<NewsVo> nlist =poTovo(list);
			PageInfo<News> userpage = new PageInfo<News>(list);
			model.addAttribute("nlist", nlist);
			model.addAttribute("userpage", userpage);
		} catch (Exception e) {
			log.error("showHomepage-查询新闻列表异常", e);
		}
		return "news3";	
	}
	
	//展示首页的内容
	@RequestMapping("/cjdy/{pageNum}")
	public String cjdy(Model model,@PathVariable Integer pageNum) {
		try {
			//使用PageHelper的分页----设置分页参数
			PageHelper.startPage(pageNum,PaginationContext.getPageSize());
			
			//查询最热专题
			List<News> list=newsserviceimpl.queryNewsByType(4);
			List<NewsVo> nlist =poTovo(list);
			PageInfo<News> userpage = new PageInfo<News>(list);
			model.addAttribute("nlist", nlist);
			model.addAttribute("userpage", userpage);
		} catch (Exception e) {
			log.error("showHomepage-查询新闻列表异常", e);
		}
		return "news4";	
	}
	
	public List<NewsVo> poTovo(List<News> list){
		List<NewsVo> nlist= new ArrayList<>();
		 for (News news : list) {
			 NewsVo newsVo= new NewsVo();
			 try {
				
				BeanUtils.copyProperties(newsVo, news);
				} catch (Exception e) {
					
					e.printStackTrace();
				} 
			 String [] dateString=DateUtil.getYearandMonthandDay(news.getModifytime());
			newsVo.setNewsyear(dateString[0]);
			newsVo.setNewsmonth(dateString[1]);
			newsVo.setNewsday(dateString[2]); 
			nlist.add(newsVo);
		}
		return nlist;
	}
}
