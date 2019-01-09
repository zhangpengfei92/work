package com.jcl.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class FundPzlog {
    private Integer id;

    private String subzh;
    /* 类型 1:配资 资金比例 2:加配资金比例 3:减配  4:追加保证金 5:出金  6:充值 7:提现 */
    private Integer fundtype;
    /* 比例 */
    private Integer setpro;
    /* 金额 */
    private BigDecimal fundbalance;
    /* 保证金 */
    private BigDecimal nowbalance;
    /* 预警线（金额）  */
    private BigDecimal yjxbalance;
    /* 平仓线（金额）  */
    private BigDecimal pcxbalance;
    /* 服务费  */
    private BigDecimal feebalance;
    /* 加配服务费  */
    private BigDecimal againfeebalance;
    /* 递延费  */
    private BigDecimal deferredbalance;
    /* 描述  */
    private String setdesc;

    private Date createtime;
    
    //操作类型
    private String cmd;
    //收入
    private Double income;
	
    //支出
	private Double output;
	
	

	private String accoutType;
	
	private String allocbroker;/* 被分配的经纪人 */
	private String allocchannel;/* 被分配的渠道*/
	private String allocagent;//分配的代理商，（在渠道和经纪人之间）
	
	
	
	public String getAllocbroker() {
		return allocbroker;
	}

	public void setAllocbroker(String allocbroker) {
		this.allocbroker = allocbroker;
	}

	public String getAllocchannel() {
		return allocchannel;
	}

	public void setAllocchannel(String allocchannel) {
		this.allocchannel = allocchannel;
	}

	public String getAllocagent() {
		return allocagent;
	}

	public void setAllocagent(String allocagent) {
		this.allocagent = allocagent;
	}


    public String getCmd() {
    	if(this.getFundtype()==1){
    		return "充值";
    	}else if(this.getFundtype()==2){
    		return "充值";
    	}else if(this.getFundtype()==3){
    		return "减配";
    	}else if(this.getFundtype()==4){
    		return "充值";
    	}else if(this.getFundtype()==5){
    		return "清算";
    	}else if(this.fundtype==6){
    		return "充值";
    	}else if(this.fundtype==7){
    		return "提现";
    	}else if(this.fundtype==8){
    		return "调账入金";
    	}else if(this.fundtype==9){
    		return "调账出金";
    	}
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public Double getIncome() {
		/*<c:when test="${user.fundtype==1}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
		<c:when test="${user.fundtype==2}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
		<c:when test="${user.fundtype==4}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
		<c:when test="${user.fundtype==5}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
		<c:when test="${user.fundtype==6}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
		<c:otherwise>0.00</c:otherwise>*/
		if(this.getFundtype()==1 || this.getFundtype()==2 || this.getFundtype()==4 || this.getFundtype()==5 || this.getFundtype()==6|| this.getFundtype()==8){
			return this.getFundbalance().doubleValue();
		}else {
			return 0.00;
		}
	}

	public void setIncome(Double income) {
		this.income = income;
	}

	public Double getOutput() {
		
		/*<c:when test="${user.fundtype==3}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>

		<c:when test="${user.fundtype==7}"><fmt:formatNumber value="${user.fundbalance}" pattern="0.00"/></c:when>
		<c:otherwise>0.00</c:otherwise>*/
		if(this.getFundtype()==3 || this.getFundtype()==7|| this.getFundtype()==9){
			return this.getFundbalance().doubleValue();
		}else{
			return 0.00;
		}
	}

	public void setOutput(Double output) {
		this.output = output;
	}

	public String getAccoutType() {
		return "人民币账户";
	}

	public void setAccoutType(String accoutType) {
		this.accoutType = accoutType;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubzh() {
        return subzh;
    }

    public void setSubzh(String subzh) {
        this.subzh = subzh == null ? null : subzh.trim();
    }

    public Integer getFundtype() {
        return fundtype;
    }

    public void setFundtype(Integer fundtype) {
        this.fundtype = fundtype;
    }

    public Integer getSetpro() {
        return setpro;
    }

    public void setSetpro(Integer setpro) {
        this.setpro = setpro;
    }

    public BigDecimal getFundbalance() {
        return fundbalance;
    }

    public void setFundbalance(BigDecimal fundbalance) {
        this.fundbalance = fundbalance;
    }

    public BigDecimal getNowbalance() {
        return nowbalance;
    }

    public void setNowbalance(BigDecimal nowbalance) {
        this.nowbalance = nowbalance;
    }

    public BigDecimal getYjxbalance() {
        return yjxbalance;
    }

    public void setYjxbalance(BigDecimal yjxbalance) {
        this.yjxbalance = yjxbalance;
    }

    public BigDecimal getPcxbalance() {
        return pcxbalance;
    }

    public void setPcxbalance(BigDecimal pcxbalance) {
        this.pcxbalance = pcxbalance;
    }

    public BigDecimal getFeebalance() {
        return feebalance;
    }

    public void setFeebalance(BigDecimal feebalance) {
        this.feebalance = feebalance;
    }

    public BigDecimal getAgainfeebalance() {
        return againfeebalance;
    }

    public void setAgainfeebalance(BigDecimal againfeebalance) {
        this.againfeebalance = againfeebalance;
    }

    public BigDecimal getDeferredbalance() {
        return deferredbalance;
    }

    public void setDeferredbalance(BigDecimal deferredbalance) {
        this.deferredbalance = deferredbalance;
    }

    public String getSetdesc() {
        return setdesc;
    }

    public void setSetdesc(String setdesc) {
        this.setdesc = setdesc == null ? null : setdesc.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
    
    
}