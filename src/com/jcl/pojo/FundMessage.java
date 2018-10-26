/**
 * 
 */
package com.jcl.pojo;

/**
 * @author heqiwen
 * @date 2018-5-22
 * @describe 交易管理   ----资金信息
 * @modify 
 * @Copyright jcl
 */
public class FundMessage {

	private String subzh;//用户账户
	private String pt;//所在平台
	private String channel;//所属渠道
	private String agentzh;//所属代理
	private String broker;//所属经纪人
	private String totalRight;//总权益
	private String demicRight;//动态权益
	private String holdprofit;//持仓盈亏
	private Double djbzj;//冻结保证金
	private String username;//
	private Double fee;         // 手续费总额
	public Double getFee() {
		return fee;
	}
	public void setFee(Double fee) {
		this.fee = fee;
	}
	private Integer settleDate;//结算日期
	private Double preFundBalance;//静态权益
	private Double fundBalance;//动态权益
	private Double enableBalance;//可用资金
	private Double warningLine;//预警线
	private Double openLine;//强平线
	private String moneyType;//币种
	private Double freezeBalance;//占用保证金
	private Double marketBalance;//持仓市值
	private Double rate;//风险度-----------
	private Double totalFloatprofit;//浮动盈亏,持仓盈亏 
	private Double totalCloseprofit;//平仓盈亏
	private Double incomingBalance;//当日入金
	private Double outcomingBalance;//当日出金
	private Double sell_freeze_balance;//冻结保证金
	
	private String settleTime;//结算日期
	private Double drSettle;//当日结存
	private Double initLoan;//
	private Double initBalance;//
	private Double noFetch;
	private String channelname;
	private String brokername;
	private String agentname;//
	
	public Double getSell_freeze_balance() {
		return sell_freeze_balance;
	}
	public void setSell_freeze_balance(Double sell_freeze_balance) {
		this.sell_freeze_balance = sell_freeze_balance;
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
	public String getTotalRight() {
		return totalRight;
	}
	public void setTotalRight(String totalRight) {
		this.totalRight = totalRight;
	}
	public String getDemicRight() {
		return demicRight;
	}
	public void setDemicRight(String demicRight) {
		this.demicRight = demicRight;
	}
	public String getHoldprofit() {
		return holdprofit;
	}
	public void setHoldprofit(String holdprofit) {
		this.holdprofit = holdprofit;
	}
	public Double getDjbzj() {
		return djbzj;
	}
	public void setDjbzj(Double djbzj) {
		this.djbzj = djbzj;
	}
	public Integer getSettleDate() {
		return settleDate;
	}
	public void setSettleDate(Integer settleDate) {
		this.settleDate = settleDate;
	}
	public Double getPreFundBalance() {
		return preFundBalance;
	}
	public void setPreFundBalance(Double preFundBalance) {
		this.preFundBalance = preFundBalance;
	}
	public Double getFundBalance() {
		return fundBalance;
	}
	public void setFundBalance(Double fundBalance) {
		this.fundBalance = fundBalance;
	}
	public Double getEnableBalance() {
		return enableBalance;
	}
	public void setEnableBalance(Double enableBalance) {
		this.enableBalance = enableBalance;
	}
	public Double getWarningLine() {
		return warningLine;
	}
	public void setWarningLine(Double warningLine) {
		this.warningLine = warningLine;
	}
	public Double getOpenLine() {
		return openLine;
	}
	public void setOpenLine(Double openLine) {
		this.openLine = openLine;
	}
	public String getMoneyType() {
		return moneyType;
	}
	public void setMoneyType(String moneyType) {
		this.moneyType = moneyType;
	}
	public Double getFreezeBalance() {
		return freezeBalance;
	}
	public void setFreezeBalance(Double freezeBalance) {
		this.freezeBalance = freezeBalance;
	}
	public Double getMarketBalance() {
		return marketBalance;
	}
	public void setMarketBalance(Double marketBalance) {
		this.marketBalance = marketBalance;
	}
	
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	public Double getTotalFloatprofit() {
		return totalFloatprofit;
	}
	public void setTotalFloatprofit(Double totalFloatprofit) {
		this.totalFloatprofit = totalFloatprofit;
	}
	public Double getTotalCloseprofit() {
		return totalCloseprofit;
	}
	public void setTotalCloseprofit(Double totalCloseprofit) {
		this.totalCloseprofit = totalCloseprofit;
	}
	public Double getIncomingBalance() {
		return incomingBalance;
	}
	public void setIncomingBalance(Double incomingBalance) {
		this.incomingBalance = incomingBalance;
	}
	public Double getOutcomingBalance() {
		return outcomingBalance;
	}
	public void setOutcomingBalance(Double outcomingBalance) {
		this.outcomingBalance = outcomingBalance;
	}
	public String getSettleTime() {
		return settleTime;
	}
	public void setSettleTime(String settleTime) {
		this.settleTime = settleTime;
	}
	public Double getDrSettle() {
		return drSettle;
	}
	public void setDrSettle(Double drSettle) {
		this.drSettle = drSettle;
	}
	public Double getInitLoan() {
		return initLoan;
	}
	public void setInitLoan(Double initLoan) {
		this.initLoan = initLoan;
	}
	public Double getInitBalance() {
		return initBalance;
	}
	public void setInitBalance(Double initBalance) {
		this.initBalance = initBalance;
	}
	public Double getNoFetch() {
		return noFetch;
	}
	public void setNoFetch(Double noFetch) {
		this.noFetch = noFetch;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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
	
	
	
	
	
	
	
	
	
	
}
