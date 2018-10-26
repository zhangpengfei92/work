package com.jcl.util.sms;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class MessageUtils {

	private static final String PROP = "appconfig";

	private static Configuration conf = null;

	// 读取配置文件
	static {
		conf = Configuration.getInstance(PROP);
	}

	/**
	 * 发送短信
	 * 
	 * @param phone 手机号码
	 * @param content 需要发送的短信内容
	 * @return
	 * @throws Exception
	 */
	public static String sendMassager(String phone, String content) throws Exception {
		String resultStr = null;
		// 短信平台URL
		@SuppressWarnings("static-access")
		String auth = new MD5Change().GetMD5Code2(conf.getValue("public.message.username")
				+ conf.getValue("public.message.password"));
		// 短信接口账号和密码，以及手机号传参

		// 模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式
		NameValuePair[] data = new NameValuePair[5];
		String uid = conf.getValue("public.message.uid");
		System.out.println("uid:" + uid);
		data[0] = new NameValuePair("uid", uid);
		System.out.println("auth:" + auth);
		data[1] = new NameValuePair("auth", auth);
		data[2] = new NameValuePair("mobile", phone);
		data[3] = new NameValuePair("expid", "0");
		data[4] = new NameValuePair("msg", content);

		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(conf.getValue("public.message.url"));
		postMethod.setRequestBody(data);
		int statusCode = httpClient.executeMethod(postMethod);
		System.out.println("statusCode:" + statusCode);
		String responseBodyAsString = postMethod.getResponseBodyAsString();
		System.out.println("返回结果：" + responseBodyAsString);
		return resultStr;
	}
	public static String sendMsg(String phone, String content){
		return sendMsg(phone, content, null,null);
	}
	
	/**
	 * 发送短信(阿里云)
	 * 
	 * @param phone 手机号码
	 * @param content 需要发送的短信内容
	 * @return
	 * @throws Exception
	 */
	public static String sendMsg(String phone, String content, String type,String bankAccount) {
		// 设置超时时间-可自行调整
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");
		// 初始化ascClient需要的几个参数
		final String product = "Dysmsapi";// 短信API产品名称（短信产品名固定，无需修改）
		final String domain = "dysmsapi.aliyuncs.com";// 短信API产品域名（接口地址固定，无需修改）
		// 替换成你的AK
		final String accessKeyId = conf.getValue("message.accesskeyid");// 你的accessKeyId,参考本文档步骤2
		System.out.println(accessKeyId);
		final String accessKeySecret = conf.getValue("message.accesskeysecret");// 你的accessKeySecret，参考本文档步骤2
		System.out.println(accessKeySecret);
		// 初始化ascClient,暂时不支持多region
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		try {
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IAcsClient acsClient = new DefaultAcsClient(profile);
		// 组装请求对象
		SendSmsRequest request = new SendSmsRequest();
		// 使用post提交
		request.setMethod(MethodType.POST);
		// 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
		request.setPhoneNumbers(phone);
		// 必填:短信签名-可在短信控制台中找到
		request.setSignName(conf.getValue("message.signname"));
		if ("1".equals(type)) {//安置成功短信
			request.setTemplateCode(conf.getValue("message.templatecode1"));
			request.setTemplateParam("{\"username\":\"" + content + "\"}");
		} else if("2".equals(type)){//积分认购短信
			request.setTemplateCode(conf.getValue("message.templatecode2"));
			request.setTemplateParam("{\"username\":\"" + content + "\"}");
		} else if("3".equals(type)){//用户激活发送虚拟账号
			request.setTemplateCode(conf.getValue("message.templatecode3"));
			request.setTemplateParam("{\"account\":\""+content+"\", \"bankAccount\":\""+bankAccount+"\"}");
		} else {
			// 必填:短信模板-可在短信控制台中找到
			request.setTemplateCode(conf.getValue("message.templatecode"));
			// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
			// 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
			request.setTemplateParam("{\"code\":\"" + content + "\"}");
		}
		// 可选-上行短信扩展码(无特殊需求用户请忽略此字段)
		// request.setSmsUpExtendCode("90997");
		// 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
		request.setOutId("123456789");
		// 请求失败这里会抛ClientException异常
		SendSmsResponse sendSmsResponse = null;
		try {
			sendSmsResponse = acsClient.getAcsResponse(request);
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (ClientException e) {
			e.printStackTrace();
		}
		if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
			// 请求成功
		}
		System.out.println(sendSmsResponse.getCode());
		return sendSmsResponse.getMessage();
	}

	public static void main(String[] args) throws Exception {
		
		String sendMassager = sendMassager("17788538285", "8888");
		System.out.println(sendMassager);
		// String mail="13260598907@163.com";
		// String content=Integer.toString((int) (Math.random()*9000+1000));
		// sendMail(mail, "验证码", content);
		// sendMassager("18627074562", "test");
	}
}