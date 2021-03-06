package com.hrbb.loan.pos.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class TCreditReportSpecial implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1790038183370187001L;

	private String serialNo;

    private String reportNo;

    private String account;

    private String type;

    private String occurDate;

    private Integer months;

    private BigDecimal amount;

    private String content;

    private int countSerialNo;
    
    private String state;
    private String state1;
    private String queryId;
    
    /**
	 * Getter method for property <tt>queryId</tt>.
	 * 
	 * @return property value of queryId
	 */
	public String getQueryId() {
		return queryId;
	}

	/**
	 * Setter method for property <tt>queryId</tt>.
	 * 
	 * @param queryId value to be assigned to property queryId
	 */
	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	/**
	 * Getter method for property <tt>state1</tt>.
	 * 
	 * @return property value of state1
	 */
	public String getState1() {
		return state1;
	}

	/**
	 * Setter method for property <tt>state1</tt>.
	 * 
	 * @param state1 value to be assigned to property state1
	 */
	public void setState1(String state1) {
		this.state1 = state1;
	}

	/**
	 * Getter method for property <tt>countSerialNo</tt>.
	 * 
	 * @return property value of countSerialNo
	 */
	public int getCountSerialNo() {
		return countSerialNo;
	}

	/**
	 * Setter method for property <tt>countSerialNo</tt>.
	 * 
	 * @param countSerialNo value to be assigned to property countSerialNo
	 */
	public void setCountSerialNo(int countSerialNo) {
		this.countSerialNo = countSerialNo;
	}

	/**
	 * Getter method for property <tt>state</tt>.
	 * 
	 * @return property value of state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Setter method for property <tt>state</tt>.
	 * 
	 * @param state value to be assigned to property state
	 */
	public void setState(String state) {
		this.state = state;
	}

	public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo == null ? null : serialNo.trim();
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo == null ? null : reportNo.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(String occurDate) {
        this.occurDate = occurDate == null ? null : occurDate.trim();
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}