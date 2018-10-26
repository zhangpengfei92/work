package com.jcl.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.jcl.comm.SerialNo;
import com.jcl.pojo.FileUploadPathObject;
import com.jcl.pojo.Subzh;
import com.jcl.service.SubzhService;
import com.jcl.util.Constant;
import com.jcl.util.DeployProperties;
import com.jcl.util.FileUtil;
import com.jcl.util.StringUtil;

import sun.misc.BASE64Decoder;

/**
 * @author zhangyang 
 * 实名认证
 */
@RequestMapping("/auth")
@Controller
public class AuthController {
    private static Logger logger = Logger.getLogger(AuthController.class);
    @Autowired
    private SubzhService subzhService;

    
    //pc
    @RequestMapping("/apply/{id}")
    public String auth(@PathVariable("id")String id, Model model) {
        Subzh subzh = subzhService.selectByPrimaryKey(id);
        model.addAttribute("subzh", subzh);
        return "auth/apply";
    }
    
    //app
    @RequestMapping("/authority/{id}")
    public String authority(@PathVariable("id") String id, Model model)
    {
      Subzh subzh = this.subzhService.selectByPrimaryKey(id);
      model.addAttribute("subzh", subzh);
      if ((subzh.getAuthState() == null) || (subzh.getAuthState().intValue() == 0))
        return "auth/notAuth";
      if (subzh.getAuthState().intValue() == 1)
        return "auth/checking";
      if (subzh.getAuthState().intValue() == 2) {
        return "auth/passed";
      }
      return "auth/failauth";
    }
    
    
    /**
	 * 上传图片base64 认证图片
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> uploadImage(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userPhone = request.getParameter("subzh");
		if(StringUtil.isAllNullOrEmpty(userPhone)){
			resultMap.put("code", "1");
			resultMap.put("message", "参数错误");
			return resultMap;
		}
		Subzh subzh = subzhService.selectByPrimaryKey(userPhone);
		if(subzh ==null){
			resultMap.put("code", "1");
			resultMap.put("message", "用户不存在");
			return resultMap;
		}
		logger.info("认证的subzh=====" + userPhone);
		String frontPic = request.getParameter("frontPic");//身份证正面
		logger.info("身份证正面报文"+frontPic);
		String backPic = request.getParameter("backPic");//身份证背面
		logger.info("身份证背面报文"+backPic);
		String cardPic = request.getParameter("cardPic");//银行卡照片
		logger.info("银行卡照片报文"+cardPic);
		
		//图片对面访问的路径
		String publicApachePath = DeployProperties.getInstance().getPublicApachePath();
		//图片保存的地址
		String publicFile = DeployProperties.getInstance().getPublicFilePath();
		if (StringUtil.isAllNullOrEmpty(frontPic) || StringUtil.isAllNullOrEmpty(backPic) || StringUtil.isAllNullOrEmpty(cardPic)) {
			resultMap.put("code", "1");
			resultMap.put("message", "图片字段为空");
			return resultMap;
		} else {
			String uploadfrontPic = uploadPic(frontPic, userPhone, publicApachePath, publicFile);
			String uploadbackPic = uploadPic(backPic, userPhone, publicApachePath, publicFile);
			String uploadcardPic = uploadPic(cardPic, userPhone, publicApachePath, publicFile);
			if(StringUtil.isAllNullOrEmpty(uploadfrontPic) ){
				resultMap.put("code", "1");
				resultMap.put("message", "身份证正面上传错误");
				return resultMap;
			}
			if(StringUtil.isAllNullOrEmpty(uploadbackPic) ){
				resultMap.put("code", "1");
				resultMap.put("message", "身份证反面面上传错误");
				return resultMap;
			}
			if(StringUtil.isAllNullOrEmpty(uploadcardPic) ){
				resultMap.put("code", "1");
				resultMap.put("message", "银行卡上传错误");
				return resultMap;
			}
			//保存图片到数据库
			subzh.setFrontPic(uploadfrontPic);
			subzh.setBackPic(uploadbackPic);
			subzh.setCardPic(uploadcardPic);
			subzh.setAuthState(1);
			int i = subzhService.updateByPrimaryKey(subzh);
			if(i>0){
				resultMap.put("code", "0");
				resultMap.put("message", "");
				return resultMap;
			}else{
				resultMap.put("code", "1");
				resultMap.put("message", "图片上传失败");
				return resultMap;
			}
		}
		
	}
	
	
	
	private String uploadPic(String frontPic, String userPhone, String publicApachePath, String publicFile) {
		
		
		BASE64Decoder decoder = new BASE64Decoder();
		String result="";
        Random random=new Random();
        for(int i=0;i<4;i++){
            result+=random.nextInt(10);
        }
		String temp = userPhone+String.valueOf(System.currentTimeMillis())+result;
		String http_path=null;
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(frontPic);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpeg图片
			String imgFilePath = publicFile + "/images/" + temp + ".png";// 新生成的图片
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			/*
			 * long length = new File(imgFilePath).length();
			 * log.info("图片的大小为"+length);
			 */
			// apache访问的路径
			http_path = publicApachePath  + temp + ".png";
		} catch (Exception e) {
			logger.error("图片保存失败" + e.getMessage());
		}
		return http_path;
	}
    
    @RequestMapping("/toAuth")
    public String toAuth(String subzh, Model model)
    {
      Subzh sub = this.subzhService.selectByPrimaryKey(subzh);
      model.addAttribute("subzh", sub);
      return "auth/authority";
    }
    
    /**
     * app认证,base64 上传图片
     * @param subzh
     * @return
     */
    @RequestMapping(value = "/app/doAuth", produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String doAuthForapp(
            Subzh subzh) {
    	subzh.setAuthState(1);
        String result = subzhService.auth(subzh);
        return result;
    }
    
    /**
     * pc 端认证接口
     * @param frontFile
     * @param backFile
     * @param cardFile
     * @param subzh
     * @return
     */
    @RequestMapping(value = "/doAuth", produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String doAuth(@RequestParam("frontFile") MultipartFile frontFile,
            @RequestParam("backFile") MultipartFile backFile, 
            @RequestParam("cardFile") MultipartFile cardFile,
            Subzh subzh) {
        String frontPic = getUpdateFilePath(frontFile);
        String backPic = getUpdateFilePath(backFile);
        String cardPic = getUpdateFilePath(cardFile);
        subzh.setFrontPic(frontPic);
        subzh.setBackPic(backPic);
        subzh.setCardPic(cardPic);
        subzh.setCardPic(cardPic);
        //修改认证状态
        subzh.setAuthState(1);
        String result = subzhService.auth(subzh);
        return result;
    }

    private String getUpdateFilePath(MultipartFile frontFile) {
        CommonsMultipartFile cmf = (CommonsMultipartFile) frontFile;
        DiskFileItem dfi = (DiskFileItem) cmf.getFileItem();
        File file = dfi.getStoreLocation();
        String fileName = SerialNo.getUNID() + ".jpg";
        FileUploadPathObject uploadPathObject = null;
        try {
            uploadPathObject = FileUtil.uploadProjectLocal("images", file, fileName);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("实名认证上传图片异常：" + e.getMessage());
        }
        logger.info("uploadPathObject:" + uploadPathObject);
        // 判断上传地址不为空
//        String filePath = "";
        if (uploadPathObject != null) {
            // 获取该地址信息
//            filePath = uploadPathObject.getAbsoluteFullPath();
//            filePath = uploadPathObject.getShortPath();
            String publicApachePath = DeployProperties.getInstance().getPublicApachePath();
            fileName = publicApachePath + fileName;
            return fileName;
        }
        return null;
    }
}