package com.jcl.pojo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FundPzlogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public FundPzlogExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andSubzhIsNull() {
            addCriterion("subzh is null");
            return (Criteria) this;
        }

        public Criteria andSubzhIsNotNull() {
            addCriterion("subzh is not null");
            return (Criteria) this;
        }

        public Criteria andSubzhEqualTo(String value) {
            addCriterion("subzh =", value, "subzh");
            return (Criteria) this;
        }

        public Criteria andSubzhNotEqualTo(String value) {
            addCriterion("subzh <>", value, "subzh");
            return (Criteria) this;
        }

        public Criteria andSubzhGreaterThan(String value) {
            addCriterion("subzh >", value, "subzh");
            return (Criteria) this;
        }

        public Criteria andSubzhGreaterThanOrEqualTo(String value) {
            addCriterion("subzh >=", value, "subzh");
            return (Criteria) this;
        }

        public Criteria andSubzhLessThan(String value) {
            addCriterion("subzh <", value, "subzh");
            return (Criteria) this;
        }

        public Criteria andSubzhLessThanOrEqualTo(String value) {
            addCriterion("subzh <=", value, "subzh");
            return (Criteria) this;
        }

        public Criteria andSubzhLike(String value) {
            addCriterion("subzh like", value, "subzh");
            return (Criteria) this;
        }

        public Criteria andSubzhNotLike(String value) {
            addCriterion("subzh not like", value, "subzh");
            return (Criteria) this;
        }

        public Criteria andSubzhIn(List<String> values) {
            addCriterion("subzh in", values, "subzh");
            return (Criteria) this;
        }

        public Criteria andSubzhNotIn(List<String> values) {
            addCriterion("subzh not in", values, "subzh");
            return (Criteria) this;
        }

        public Criteria andSubzhBetween(String value1, String value2) {
            addCriterion("subzh between", value1, value2, "subzh");
            return (Criteria) this;
        }

        public Criteria andSubzhNotBetween(String value1, String value2) {
            addCriterion("subzh not between", value1, value2, "subzh");
            return (Criteria) this;
        }

        public Criteria andFundtypeIsNull() {
            addCriterion("fundtype is null");
            return (Criteria) this;
        }

        public Criteria andFundtypeIsNotNull() {
            addCriterion("fundtype is not null");
            return (Criteria) this;
        }

        public Criteria andFundtypeEqualTo(Integer value) {
            addCriterion("fundtype =", value, "fundtype");
            return (Criteria) this;
        }

        public Criteria andFundtypeNotEqualTo(Integer value) {
            addCriterion("fundtype <>", value, "fundtype");
            return (Criteria) this;
        }

        public Criteria andFundtypeGreaterThan(Integer value) {
            addCriterion("fundtype >", value, "fundtype");
            return (Criteria) this;
        }

        public Criteria andFundtypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("fundtype >=", value, "fundtype");
            return (Criteria) this;
        }

        public Criteria andFundtypeLessThan(Integer value) {
            addCriterion("fundtype <", value, "fundtype");
            return (Criteria) this;
        }

        public Criteria andFundtypeLessThanOrEqualTo(Integer value) {
            addCriterion("fundtype <=", value, "fundtype");
            return (Criteria) this;
        }

        public Criteria andFundtypeIn(List<Integer> values) {
            addCriterion("fundtype in", values, "fundtype");
            return (Criteria) this;
        }

        public Criteria andFundtypeNotIn(List<Integer> values) {
            addCriterion("fundtype not in", values, "fundtype");
            return (Criteria) this;
        }

        public Criteria andFundtypeBetween(Integer value1, Integer value2) {
            addCriterion("fundtype between", value1, value2, "fundtype");
            return (Criteria) this;
        }

        public Criteria andFundtypeNotBetween(Integer value1, Integer value2) {
            addCriterion("fundtype not between", value1, value2, "fundtype");
            return (Criteria) this;
        }

        public Criteria andSetproIsNull() {
            addCriterion("setpro is null");
            return (Criteria) this;
        }

        public Criteria andSetproIsNotNull() {
            addCriterion("setpro is not null");
            return (Criteria) this;
        }

        public Criteria andSetproEqualTo(Integer value) {
            addCriterion("setpro =", value, "setpro");
            return (Criteria) this;
        }

        public Criteria andSetproNotEqualTo(Integer value) {
            addCriterion("setpro <>", value, "setpro");
            return (Criteria) this;
        }

        public Criteria andSetproGreaterThan(Integer value) {
            addCriterion("setpro >", value, "setpro");
            return (Criteria) this;
        }

        public Criteria andSetproGreaterThanOrEqualTo(Integer value) {
            addCriterion("setpro >=", value, "setpro");
            return (Criteria) this;
        }

        public Criteria andSetproLessThan(Integer value) {
            addCriterion("setpro <", value, "setpro");
            return (Criteria) this;
        }

        public Criteria andSetproLessThanOrEqualTo(Integer value) {
            addCriterion("setpro <=", value, "setpro");
            return (Criteria) this;
        }

        public Criteria andSetproIn(List<Integer> values) {
            addCriterion("setpro in", values, "setpro");
            return (Criteria) this;
        }

        public Criteria andSetproNotIn(List<Integer> values) {
            addCriterion("setpro not in", values, "setpro");
            return (Criteria) this;
        }

        public Criteria andSetproBetween(Integer value1, Integer value2) {
            addCriterion("setpro between", value1, value2, "setpro");
            return (Criteria) this;
        }

        public Criteria andSetproNotBetween(Integer value1, Integer value2) {
            addCriterion("setpro not between", value1, value2, "setpro");
            return (Criteria) this;
        }

        public Criteria andFundbalanceIsNull() {
            addCriterion("fundBalance is null");
            return (Criteria) this;
        }

        public Criteria andFundbalanceIsNotNull() {
            addCriterion("fundBalance is not null");
            return (Criteria) this;
        }

        public Criteria andFundbalanceEqualTo(BigDecimal value) {
            addCriterion("fundBalance =", value, "fundbalance");
            return (Criteria) this;
        }

        public Criteria andFundbalanceNotEqualTo(BigDecimal value) {
            addCriterion("fundBalance <>", value, "fundbalance");
            return (Criteria) this;
        }

        public Criteria andFundbalanceGreaterThan(BigDecimal value) {
            addCriterion("fundBalance >", value, "fundbalance");
            return (Criteria) this;
        }

        public Criteria andFundbalanceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("fundBalance >=", value, "fundbalance");
            return (Criteria) this;
        }

        public Criteria andFundbalanceLessThan(BigDecimal value) {
            addCriterion("fundBalance <", value, "fundbalance");
            return (Criteria) this;
        }

        public Criteria andFundbalanceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("fundBalance <=", value, "fundbalance");
            return (Criteria) this;
        }

        public Criteria andFundbalanceIn(List<BigDecimal> values) {
            addCriterion("fundBalance in", values, "fundbalance");
            return (Criteria) this;
        }

        public Criteria andFundbalanceNotIn(List<BigDecimal> values) {
            addCriterion("fundBalance not in", values, "fundbalance");
            return (Criteria) this;
        }

        public Criteria andFundbalanceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("fundBalance between", value1, value2, "fundbalance");
            return (Criteria) this;
        }

        public Criteria andFundbalanceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("fundBalance not between", value1, value2, "fundbalance");
            return (Criteria) this;
        }

        public Criteria andNowbalanceIsNull() {
            addCriterion("nowBalance is null");
            return (Criteria) this;
        }

        public Criteria andNowbalanceIsNotNull() {
            addCriterion("nowBalance is not null");
            return (Criteria) this;
        }

        public Criteria andNowbalanceEqualTo(BigDecimal value) {
            addCriterion("nowBalance =", value, "nowbalance");
            return (Criteria) this;
        }

        public Criteria andNowbalanceNotEqualTo(BigDecimal value) {
            addCriterion("nowBalance <>", value, "nowbalance");
            return (Criteria) this;
        }

        public Criteria andNowbalanceGreaterThan(BigDecimal value) {
            addCriterion("nowBalance >", value, "nowbalance");
            return (Criteria) this;
        }

        public Criteria andNowbalanceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("nowBalance >=", value, "nowbalance");
            return (Criteria) this;
        }

        public Criteria andNowbalanceLessThan(BigDecimal value) {
            addCriterion("nowBalance <", value, "nowbalance");
            return (Criteria) this;
        }

        public Criteria andNowbalanceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("nowBalance <=", value, "nowbalance");
            return (Criteria) this;
        }

        public Criteria andNowbalanceIn(List<BigDecimal> values) {
            addCriterion("nowBalance in", values, "nowbalance");
            return (Criteria) this;
        }

        public Criteria andNowbalanceNotIn(List<BigDecimal> values) {
            addCriterion("nowBalance not in", values, "nowbalance");
            return (Criteria) this;
        }

        public Criteria andNowbalanceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("nowBalance between", value1, value2, "nowbalance");
            return (Criteria) this;
        }

        public Criteria andNowbalanceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("nowBalance not between", value1, value2, "nowbalance");
            return (Criteria) this;
        }

        public Criteria andYjxbalanceIsNull() {
            addCriterion("yjxBalance is null");
            return (Criteria) this;
        }

        public Criteria andYjxbalanceIsNotNull() {
            addCriterion("yjxBalance is not null");
            return (Criteria) this;
        }

        public Criteria andYjxbalanceEqualTo(BigDecimal value) {
            addCriterion("yjxBalance =", value, "yjxbalance");
            return (Criteria) this;
        }

        public Criteria andYjxbalanceNotEqualTo(BigDecimal value) {
            addCriterion("yjxBalance <>", value, "yjxbalance");
            return (Criteria) this;
        }

        public Criteria andYjxbalanceGreaterThan(BigDecimal value) {
            addCriterion("yjxBalance >", value, "yjxbalance");
            return (Criteria) this;
        }

        public Criteria andYjxbalanceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("yjxBalance >=", value, "yjxbalance");
            return (Criteria) this;
        }

        public Criteria andYjxbalanceLessThan(BigDecimal value) {
            addCriterion("yjxBalance <", value, "yjxbalance");
            return (Criteria) this;
        }

        public Criteria andYjxbalanceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("yjxBalance <=", value, "yjxbalance");
            return (Criteria) this;
        }

        public Criteria andYjxbalanceIn(List<BigDecimal> values) {
            addCriterion("yjxBalance in", values, "yjxbalance");
            return (Criteria) this;
        }

        public Criteria andYjxbalanceNotIn(List<BigDecimal> values) {
            addCriterion("yjxBalance not in", values, "yjxbalance");
            return (Criteria) this;
        }

        public Criteria andYjxbalanceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("yjxBalance between", value1, value2, "yjxbalance");
            return (Criteria) this;
        }

        public Criteria andYjxbalanceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("yjxBalance not between", value1, value2, "yjxbalance");
            return (Criteria) this;
        }

        public Criteria andPcxbalanceIsNull() {
            addCriterion("pcxBalance is null");
            return (Criteria) this;
        }

        public Criteria andPcxbalanceIsNotNull() {
            addCriterion("pcxBalance is not null");
            return (Criteria) this;
        }

        public Criteria andPcxbalanceEqualTo(BigDecimal value) {
            addCriterion("pcxBalance =", value, "pcxbalance");
            return (Criteria) this;
        }

        public Criteria andPcxbalanceNotEqualTo(BigDecimal value) {
            addCriterion("pcxBalance <>", value, "pcxbalance");
            return (Criteria) this;
        }

        public Criteria andPcxbalanceGreaterThan(BigDecimal value) {
            addCriterion("pcxBalance >", value, "pcxbalance");
            return (Criteria) this;
        }

        public Criteria andPcxbalanceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("pcxBalance >=", value, "pcxbalance");
            return (Criteria) this;
        }

        public Criteria andPcxbalanceLessThan(BigDecimal value) {
            addCriterion("pcxBalance <", value, "pcxbalance");
            return (Criteria) this;
        }

        public Criteria andPcxbalanceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("pcxBalance <=", value, "pcxbalance");
            return (Criteria) this;
        }

        public Criteria andPcxbalanceIn(List<BigDecimal> values) {
            addCriterion("pcxBalance in", values, "pcxbalance");
            return (Criteria) this;
        }

        public Criteria andPcxbalanceNotIn(List<BigDecimal> values) {
            addCriterion("pcxBalance not in", values, "pcxbalance");
            return (Criteria) this;
        }

        public Criteria andPcxbalanceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("pcxBalance between", value1, value2, "pcxbalance");
            return (Criteria) this;
        }

        public Criteria andPcxbalanceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("pcxBalance not between", value1, value2, "pcxbalance");
            return (Criteria) this;
        }

        public Criteria andFeebalanceIsNull() {
            addCriterion("feeBalance is null");
            return (Criteria) this;
        }

        public Criteria andFeebalanceIsNotNull() {
            addCriterion("feeBalance is not null");
            return (Criteria) this;
        }

        public Criteria andFeebalanceEqualTo(BigDecimal value) {
            addCriterion("feeBalance =", value, "feebalance");
            return (Criteria) this;
        }

        public Criteria andFeebalanceNotEqualTo(BigDecimal value) {
            addCriterion("feeBalance <>", value, "feebalance");
            return (Criteria) this;
        }

        public Criteria andFeebalanceGreaterThan(BigDecimal value) {
            addCriterion("feeBalance >", value, "feebalance");
            return (Criteria) this;
        }

        public Criteria andFeebalanceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("feeBalance >=", value, "feebalance");
            return (Criteria) this;
        }

        public Criteria andFeebalanceLessThan(BigDecimal value) {
            addCriterion("feeBalance <", value, "feebalance");
            return (Criteria) this;
        }

        public Criteria andFeebalanceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("feeBalance <=", value, "feebalance");
            return (Criteria) this;
        }

        public Criteria andFeebalanceIn(List<BigDecimal> values) {
            addCriterion("feeBalance in", values, "feebalance");
            return (Criteria) this;
        }

        public Criteria andFeebalanceNotIn(List<BigDecimal> values) {
            addCriterion("feeBalance not in", values, "feebalance");
            return (Criteria) this;
        }

        public Criteria andFeebalanceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("feeBalance between", value1, value2, "feebalance");
            return (Criteria) this;
        }

        public Criteria andFeebalanceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("feeBalance not between", value1, value2, "feebalance");
            return (Criteria) this;
        }

        public Criteria andAgainfeebalanceIsNull() {
            addCriterion("againFeeBalance is null");
            return (Criteria) this;
        }

        public Criteria andAgainfeebalanceIsNotNull() {
            addCriterion("againFeeBalance is not null");
            return (Criteria) this;
        }

        public Criteria andAgainfeebalanceEqualTo(BigDecimal value) {
            addCriterion("againFeeBalance =", value, "againfeebalance");
            return (Criteria) this;
        }

        public Criteria andAgainfeebalanceNotEqualTo(BigDecimal value) {
            addCriterion("againFeeBalance <>", value, "againfeebalance");
            return (Criteria) this;
        }

        public Criteria andAgainfeebalanceGreaterThan(BigDecimal value) {
            addCriterion("againFeeBalance >", value, "againfeebalance");
            return (Criteria) this;
        }

        public Criteria andAgainfeebalanceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("againFeeBalance >=", value, "againfeebalance");
            return (Criteria) this;
        }

        public Criteria andAgainfeebalanceLessThan(BigDecimal value) {
            addCriterion("againFeeBalance <", value, "againfeebalance");
            return (Criteria) this;
        }

        public Criteria andAgainfeebalanceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("againFeeBalance <=", value, "againfeebalance");
            return (Criteria) this;
        }

        public Criteria andAgainfeebalanceIn(List<BigDecimal> values) {
            addCriterion("againFeeBalance in", values, "againfeebalance");
            return (Criteria) this;
        }

        public Criteria andAgainfeebalanceNotIn(List<BigDecimal> values) {
            addCriterion("againFeeBalance not in", values, "againfeebalance");
            return (Criteria) this;
        }

        public Criteria andAgainfeebalanceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("againFeeBalance between", value1, value2, "againfeebalance");
            return (Criteria) this;
        }

        public Criteria andAgainfeebalanceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("againFeeBalance not between", value1, value2, "againfeebalance");
            return (Criteria) this;
        }

        public Criteria andDeferredbalanceIsNull() {
            addCriterion("deferredBalance is null");
            return (Criteria) this;
        }

        public Criteria andDeferredbalanceIsNotNull() {
            addCriterion("deferredBalance is not null");
            return (Criteria) this;
        }

        public Criteria andDeferredbalanceEqualTo(BigDecimal value) {
            addCriterion("deferredBalance =", value, "deferredbalance");
            return (Criteria) this;
        }

        public Criteria andDeferredbalanceNotEqualTo(BigDecimal value) {
            addCriterion("deferredBalance <>", value, "deferredbalance");
            return (Criteria) this;
        }

        public Criteria andDeferredbalanceGreaterThan(BigDecimal value) {
            addCriterion("deferredBalance >", value, "deferredbalance");
            return (Criteria) this;
        }

        public Criteria andDeferredbalanceGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("deferredBalance >=", value, "deferredbalance");
            return (Criteria) this;
        }

        public Criteria andDeferredbalanceLessThan(BigDecimal value) {
            addCriterion("deferredBalance <", value, "deferredbalance");
            return (Criteria) this;
        }

        public Criteria andDeferredbalanceLessThanOrEqualTo(BigDecimal value) {
            addCriterion("deferredBalance <=", value, "deferredbalance");
            return (Criteria) this;
        }

        public Criteria andDeferredbalanceIn(List<BigDecimal> values) {
            addCriterion("deferredBalance in", values, "deferredbalance");
            return (Criteria) this;
        }

        public Criteria andDeferredbalanceNotIn(List<BigDecimal> values) {
            addCriterion("deferredBalance not in", values, "deferredbalance");
            return (Criteria) this;
        }

        public Criteria andDeferredbalanceBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("deferredBalance between", value1, value2, "deferredbalance");
            return (Criteria) this;
        }

        public Criteria andDeferredbalanceNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("deferredBalance not between", value1, value2, "deferredbalance");
            return (Criteria) this;
        }

        public Criteria andSetdescIsNull() {
            addCriterion("setdesc is null");
            return (Criteria) this;
        }

        public Criteria andSetdescIsNotNull() {
            addCriterion("setdesc is not null");
            return (Criteria) this;
        }

        public Criteria andSetdescEqualTo(String value) {
            addCriterion("setdesc =", value, "setdesc");
            return (Criteria) this;
        }

        public Criteria andSetdescNotEqualTo(String value) {
            addCriterion("setdesc <>", value, "setdesc");
            return (Criteria) this;
        }

        public Criteria andSetdescGreaterThan(String value) {
            addCriterion("setdesc >", value, "setdesc");
            return (Criteria) this;
        }

        public Criteria andSetdescGreaterThanOrEqualTo(String value) {
            addCriterion("setdesc >=", value, "setdesc");
            return (Criteria) this;
        }

        public Criteria andSetdescLessThan(String value) {
            addCriterion("setdesc <", value, "setdesc");
            return (Criteria) this;
        }

        public Criteria andSetdescLessThanOrEqualTo(String value) {
            addCriterion("setdesc <=", value, "setdesc");
            return (Criteria) this;
        }

        public Criteria andSetdescLike(String value) {
            addCriterion("setdesc like", value, "setdesc");
            return (Criteria) this;
        }

        public Criteria andSetdescNotLike(String value) {
            addCriterion("setdesc not like", value, "setdesc");
            return (Criteria) this;
        }

        public Criteria andSetdescIn(List<String> values) {
            addCriterion("setdesc in", values, "setdesc");
            return (Criteria) this;
        }

        public Criteria andSetdescNotIn(List<String> values) {
            addCriterion("setdesc not in", values, "setdesc");
            return (Criteria) this;
        }

        public Criteria andSetdescBetween(String value1, String value2) {
            addCriterion("setdesc between", value1, value2, "setdesc");
            return (Criteria) this;
        }

        public Criteria andSetdescNotBetween(String value1, String value2) {
            addCriterion("setdesc not between", value1, value2, "setdesc");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIsNull() {
            addCriterion("createtime is null");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIsNotNull() {
            addCriterion("createtime is not null");
            return (Criteria) this;
        }

        public Criteria andCreatetimeEqualTo(Date value) {
            addCriterion("createtime =", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotEqualTo(Date value) {
            addCriterion("createtime <>", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeGreaterThan(Date value) {
            addCriterion("createtime >", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeGreaterThanOrEqualTo(Date value) {
            addCriterion("createtime >=", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeLessThan(Date value) {
            addCriterion("createtime <", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeLessThanOrEqualTo(Date value) {
            addCriterion("createtime <=", value, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeIn(List<Date> values) {
            addCriterion("createtime in", values, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotIn(List<Date> values) {
            addCriterion("createtime not in", values, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeBetween(Date value1, Date value2) {
            addCriterion("createtime between", value1, value2, "createtime");
            return (Criteria) this;
        }

        public Criteria andCreatetimeNotBetween(Date value1, Date value2) {
            addCriterion("createtime not between", value1, value2, "createtime");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}