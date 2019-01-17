/**
 * Copyright (C) 2009 武汉金策略信息科技有限公司
 *
 * 版权所有。
 *
 * 类名　　  : @App.java
 * 功能概要  : 
 * 做成日期  : @2019年1月11日
 * 修改日期  :
 */
package com.zpf;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


/** 
 * @author zpf
 * @version 1.0
 */
@ServletComponentScan
@SpringBootApplication
public class App {
	
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}