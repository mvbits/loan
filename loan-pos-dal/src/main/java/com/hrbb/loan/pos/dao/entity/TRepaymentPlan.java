package com.hrbb.loan.pos.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TRepaymentPlan implements Serializable, Comparable<Object>{
    /**
     * 
     */
    private static final long serialVersionUID = 5710070190137649762L;
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_repayment_plan.receiptId
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    private String receiptid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_repayment_plan.period
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    private String period;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_repayment_plan.schedDate
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    private Date scheddate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_repayment_plan.schedPrincipal
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    private BigDecimal schedprincipal;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_repayment_plan.schedInterest
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    private BigDecimal schedinterest;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_repayment_plan.unpaidInterest
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    private BigDecimal unpaidinterest;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_repayment_plan.schedTotalAmt
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    private BigDecimal schedtotalamt;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_repayment_plan.graceDays
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    private Integer gracedays;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_repayment_plan.reducedInterest
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    private BigDecimal reducedinterest;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_repayment_plan.memo
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    private String memo;

    private BigDecimal owePrincipal;

    private BigDecimal oweInterest;
    
    private String overFlag;// 拖欠标识:0是不拖欠，非0 拖欠

    private String payoffFlag; // 本期贷款结清标识：00标识未结清，10标识结清

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_repayment_plan.receiptId
     *
     * @return the value of t_repayment_plan.receiptId
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public String getReceiptid() {
        return receiptid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_repayment_plan.receiptId
     *
     * @param receiptid the value for t_repayment_plan.receiptId
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public void setReceiptid(String receiptid) {
        this.receiptid = receiptid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_repayment_plan.period
     *
     * @return the value of t_repayment_plan.period
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public String getPeriod() {
        return period;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_repayment_plan.period
     *
     * @param period the value for t_repayment_plan.period
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_repayment_plan.schedDate
     *
     * @return the value of t_repayment_plan.schedDate
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public Date getScheddate() {
        return scheddate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_repayment_plan.schedDate
     *
     * @param scheddate the value for t_repayment_plan.schedDate
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public void setScheddate(Date scheddate) {
        this.scheddate = scheddate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_repayment_plan.schedPrincipal
     *
     * @return the value of t_repayment_plan.schedPrincipal
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public BigDecimal getSchedprincipal() {
        return schedprincipal;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_repayment_plan.schedPrincipal
     *
     * @param schedprincipal the value for t_repayment_plan.schedPrincipal
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public void setSchedprincipal(BigDecimal schedprincipal) {
        this.schedprincipal = schedprincipal;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_repayment_plan.schedInterest
     *
     * @return the value of t_repayment_plan.schedInterest
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public BigDecimal getSchedinterest() {
        return schedinterest;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_repayment_plan.schedInterest
     *
     * @param schedinterest the value for t_repayment_plan.schedInterest
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public void setSchedinterest(BigDecimal schedinterest) {
        this.schedinterest = schedinterest;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_repayment_plan.unpaidInterest
     *
     * @return the value of t_repayment_plan.unpaidInterest
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public BigDecimal getUnpaidinterest() {
        return unpaidinterest;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_repayment_plan.unpaidInterest
     *
     * @param unpaidinterest the value for t_repayment_plan.unpaidInterest
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public void setUnpaidinterest(BigDecimal unpaidinterest) {
        this.unpaidinterest = unpaidinterest;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_repayment_plan.schedTotalAmt
     *
     * @return the value of t_repayment_plan.schedTotalAmt
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public BigDecimal getSchedtotalamt() {
        return schedtotalamt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_repayment_plan.schedTotalAmt
     *
     * @param schedtotalamt the value for t_repayment_plan.schedTotalAmt
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public void setSchedtotalamt(BigDecimal schedtotalamt) {
        this.schedtotalamt = schedtotalamt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_repayment_plan.graceDays
     *
     * @return the value of t_repayment_plan.graceDays
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public Integer getGracedays() {
        return gracedays;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_repayment_plan.graceDays
     *
     * @param gracedays the value for t_repayment_plan.graceDays
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public void setGracedays(Integer gracedays) {
        this.gracedays = gracedays;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_repayment_plan.reducedInterest
     *
     * @return the value of t_repayment_plan.reducedInterest
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public BigDecimal getReducedinterest() {
        return reducedinterest;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_repayment_plan.reducedInterest
     *
     * @param reducedinterest the value for t_repayment_plan.reducedInterest
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public void setReducedinterest(BigDecimal reducedinterest) {
        this.reducedinterest = reducedinterest;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_repayment_plan.memo
     *
     * @return the value of t_repayment_plan.memo
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public String getMemo() {
        return memo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_repayment_plan.memo
     *
     * @param memo the value for t_repayment_plan.memo
     *
     * @mbggenerated Thu Jul 30 09:30:28 CST 2015
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    public BigDecimal getOwePrincipal() {
        return owePrincipal;
    }

    public void setOwePrincipal(BigDecimal owePrincipal) {
        this.owePrincipal = owePrincipal;
    }

    public BigDecimal getOweInterest() {
        return oweInterest;
    }

	public void setOweInterest(BigDecimal oweInterest) {
		this.oweInterest = oweInterest;
	}

    public String getPayoffFlag() {
        return payoffFlag;
    }

    public void setPayoffFlag(String payoffFlag) {
        this.payoffFlag = payoffFlag;
    }

    public String getOverFlag() {
        return overFlag;
    }

    public void setOverFlag(String overFlag) {
        this.overFlag = overFlag;
    }

    @Override
    public int compareTo(Object o) {
        if(this == o){
            return 0;
        }else if(o != null && o instanceof TRepaymentPlan){
            TRepaymentPlan plan = (TRepaymentPlan)o;
            if(Integer.parseInt(period) < Integer.parseInt(plan.getPeriod())){
                return -1;
            }else{
                return 1;
            }
        }else{
            return -1;
        }
    }
}