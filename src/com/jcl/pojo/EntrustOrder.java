/**
 * 
 */
package com.jcl.pojo;

/**
 * @author heqiwen
 * @date 2018-5-22
 * @describe 交易管理------委托记录
 * @modify 
 * @Copyright jcl
 */
public class EntrustOrder {

	private String subzh;//用户账户
	private String pt;//所在平台
	private String channel;//所属渠道
	private String agentzh;//所属代理
	private String broker;//所属经纪人
	private Double cjjj;//成交均价
	
	private String entrustNo;//委托编号
	
	private Integer directionType;//开平
	private Integer entrustBs;//方向  
	
	private String stockCode;//合约代码
	private String stockName;//合约名称
	
	private Double entrustPrice;//委托价格
	private Double entrustVol;//委托数量
	private Double businessVol;//成交数量
	private Double businessPrice;//成交价格
	private Integer entrustStatus;//委托状态
	private Integer entrustTime;//委托时间
	private Integer entrustDate;//委托日期
	private Integer businessTime;//成交时间
	private Integer businessDate;//成交日期
	private Integer entrustType;//指令类型
	private Integer exchangeType;//市场
	
	private String entime;//委托时间
	private String dealtime;//成交时间
	private String mainzh;//
	private Integer ordertype;//
	
	private String channelname;
	private String agentname;//
	private String brokername;
	private String phone;
	private Double wtje;
	private Long mdealtime;//
	private Long mentrusttime;//
	private String eno;//
	private String cmd; //方向中文
	private String mark; //开平
	private String username;  //认证姓名
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	private String exchangeTypeShift;
	public String getExchangeTypeShift() {
		if(this.exchangeType != null) {
			if(this.exchangeType == 15) {
				return "国际期货交易市场";
			}else if(this.exchangeType == 16) {
				return "香港期货交易所";
			}else {
				return "";
			}
		}else {
			return "";
		}
	}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public String getSubzh() {
		return subzh;
	}
	public void setSubzh(String subzh) {
		this.subzh = subzh;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getBroker() {
		return broker;
	}
	public void setBroker(String broker) {
		this.broker = broker;
	}
	public Double getCjjj() {
		return cjjj;
	}
	public void setCjjj(Double cjjj) {
		this.cjjj = cjjj;
	}
	public String getEntrustNo() {
		return entrustNo;
	}
	public void setEntrustNo(String entrustNo) {
		this.entrustNo = entrustNo;
	}
	public Integer getDirectionType() {
		return directionType;
	}
	public void setDirectionType(Integer directionType) {
		this.directionType = directionType;
	}
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public Integer getEntrustBs() {
		return entrustBs;
	}
	public void setEntrustBs(Integer entrustBs) {
		this.entrustBs = entrustBs;
	}
	public Double getEntrustPrice() {
		return entrustPrice;
	}
	public void setEntrustPrice(Double entrustPrice) {
		this.entrustPrice = entrustPrice;
	}
	public Double getEntrustVol() {
		return entrustVol;
	}
	public void setEntrustVol(Double entrustVol) {
		this.entrustVol = entrustVol;
	}
	public Double getBusinessVol() {
		return businessVol;
	}
	public void setBusinessVol(Double businessVol) {
		this.businessVol = businessVol;
	}
	public Double getBusinessPrice() {
		return businessPrice;
	}
	public void setBusinessPrice(Double businessPrice) {
		this.businessPrice = businessPrice;
	}
	public Integer getEntrustStatus() {
		return entrustStatus;
	}
	public void setEntrustStatus(Integer entrustStatus) {
		this.entrustStatus = entrustStatus;
	}
	public Integer getEntrustTime() {
		return entrustTime;
	}
	public void setEntrustTime(Integer entrustTime) {
		this.entrustTime = entrustTime;
	}
	public Integer getEntrustDate() {
		return entrustDate;
	}
	public void setEntrustDate(Integer entrustDate) {
		this.entrustDate = entrustDate;
	}
	public Integer getBusinessTime() {
		return businessTime;
	}
	public void setBusinessTime(Integer businessTime) {
		this.businessTime = businessTime;
	}
	public Integer getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(Integer businessDate) {
		this.businessDate = businessDate;
	}
	public Integer getEntrustType() {
		return entrustType;
	}
	public void setEntrustType(Integer entrustType) {
		this.entrustType = entrustType;
	}
	public Integer getExchangeType() {
		return exchangeType;
	}
	public void setExchangeType(Integer exchangeType) {
		this.exchangeType = exchangeType;
	}
	
	public String getDealtime() {
		return dealtime;
	}
	public void setDealtime(String dealtime) {
		this.dealtime = dealtime;
	}
	public String getMainzh() {
		return mainzh;
	}
	public void setMainzh(String mainzh) {
		this.mainzh = mainzh;
	}
	public Integer getOrdertype() {
		return ordertype;
	}
	public void setOrdertype(Integer ordertype) {
		this.ordertype = ordertype;
	}
	public String getChannelname() {
		return channelname;
	}
	public void setChannelname(String channelname) {
		this.channelname = channelname;
	}
	public String getBrokername() {
		return brokername;
	}
	public void setBrokername(String brokername) {
		this.brokername = brokername;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Double getWtje() {
		return wtje;
	}
	public void setWtje(Double wtje) {
		this.wtje = wtje;
	}
	public Long getMdealtime() {
		return mdealtime;
	}
	public void setMdealtime(Long mdealtime) {
		this.mdealtime = mdealtime;
	}
	public Long getMentrusttime() {
		return mentrusttime;
	}
	public void setMentrusttime(Long mentrusttime) {
		this.mentrusttime = mentrusttime;
	}
	public String getEno() {
		return eno;
	}
	public void setEno(String eno) {
		this.eno = eno;
	}
	public String getPt() {
		return pt;
	}
	public void setPt(String pt) {
		this.pt = pt;
	}
	public String getAgentzh() {
		return agentzh;
	}
	public void setAgentzh(String agentzh) {
		this.agentzh = agentzh;
	}
	public String getAgentname() {
		return agentname;
	}
	public void setAgentname(String agentname) {
		this.agentname = agentname;
	}
	public String getEntime() {
		return entime;
	}
	public void setEntime(String entime) {
		this.entime = entime;
	}
	
	
	
	
}
