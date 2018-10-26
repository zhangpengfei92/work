package com.jcl.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class FundPzlog {
    private Integer id;

    private String subzh;
    /* 类型 1:配资 资金比例 2:加配资金比例 3:减配  4:追加保证金 5:出金  6:充值 7:提现 8:调账入金 9调账出金*/
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