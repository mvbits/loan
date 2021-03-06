package com.hrbb.loan.pos.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TPaybackRunningRecord implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2334815329902258997L;

	private String paybackRunningRecordId;

    private String paybackApplyId;

    private String receiptId;

    private Date supposedPaybackDate;

    private BigDecimal supposedTotalAmount;

    private BigDecimal supposedCapital;

    private BigDecimal supposedInterest;

    private Date actualPaybackDate;

    private BigDecimal actualTotalAmount;

    private BigDecimal actualCapital;

    private BigDecimal actualInterest;

    private String paybackChannel;

    private String paybackImportId;

    private String runningStatus;

    //还款类型
    private String paybackWay;
    private String stdshno;
    private String channelPosCustId;
    
    private String inputUser;
    private Date inputTime;
    //渠道还款流水编号
    private String stdrpno;
    
    private BigDecimal balance;
    
    private BigDecimal paybackPenalty;
    
    
    
    public BigDecimal getPaybackPenalty() {
		return paybackPenalty;
	}

	public void setPaybackPenalty(BigDecimal paybackPenalty) {
		this.paybackPenalty = paybackPenalty;
	}

	/**
	 * Getter method for property <tt>stdrpno</tt>.
	 * 
	 * @return property value of stdrpno
	 */
	public String getStdrpno() {
		return stdrpno;
	}

	/**
	 * Setter method for property <tt>stdrpno</tt>.
	 * 
	 * @param stdrpno value to be assigned to property stdrpno
	 */
	public void setStdrpno(String stdrpno) {
		this.stdrpno = stdrpno;
	}

	public String getInputUser() {
		return inputUser;
	}

	public void setInputUser(String inputUser) {
		this.inputUser = inputUser;
	}

	public Date getInputTime() {
		return inputTime;
	}

	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}

	/**
	 * Getter method for property <tt>stdshno</tt>.
	 * 
	 * @return property value of stdshno
	 */
	public String getStdshno() {
		return stdshno;
	}

	/**
	 * Setter method for property <tt>stdshno</tt>.
	 * 
	 * @param stdshno value to be assigned to property stdshno
	 */
	public void setStdshno(String stdshno) {
		this.stdshno = stdshno;
	}

	/**
	 * Getter method for property <tt>channelPosCustId</tt>.
	 * 
	 * @return property value of channelPosCustId
	 */
	public String getChannelPosCustId() {
		return channelPosCustId;
	}

	/**
	 * Setter method for property <tt>channelPosCustId</tt>.
	 * 
	 * @param channelPosCustId value to be assigned to property channelPosCustId
	 */
	public void setChannelPosCustId(String channelPosCustId) {
		this.channelPosCustId = channelPosCustId;
	}

	/**
	 * Getter method for property <tt>paybackWay</tt>.
	 * 
	 * @return property value of paybackWay
	 */
	public String getPaybackWay() {
		return paybackWay;
	}

	/**
	 * Setter method for property <tt>paybackWay</tt>.
	 * 
	 * @param paybackWay value to be assigned to property paybackWay
	 */
	public void setPaybackWay(String paybackWay) {
		this.paybackWay = paybackWay;
	}

	public String getPaybackRunningRecordId() {
        return paybackRunningRecordId;
    }

    public void setPaybackRunningRecordId(String paybackRunningRecordId) {
        this.paybackRunningRecordId = paybackRunningRecordId == null ? null : paybackRunningRecordId.trim();
    }

    public String getPaybackApplyId() {
        return paybackApplyId;
    }

    public void setPaybackApplyId(String paybackApplyId) {
        this.paybackApplyId = paybackApplyId == null ? null : paybackApplyId.trim();
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId == null ? null : receiptId.trim();
    }

    public Date getSupposedPaybackDate() {
        return supposedPaybackDate;
    }

    public void setSupposedPaybackDate(Date supposedPaybackDate) {
        this.supposedPaybackDate = supposedPaybackDate;
    }

    public BigDecimal getSupposedTotalAmount() {
        return supposedTotalAmount;
    }

    public void setSupposedTotalAmount(BigDecimal supposedTotalAmount) {
        this.supposedTotalAmount = supposedTotalAmount;
    }

    public BigDecimal getSupposedCapital() {
        return supposedCapital;
    }

    public void setSupposedCapital(BigDecimal supposedCapital) {
        this.supposedCapital = supposedCapital;
    }

    public BigDecimal getSupposedInterest() {
        return supposedInterest;
    }

    public void setSupposedInterest(BigDecimal supposedInterest) {
        this.supposedInterest = supposedInterest;
    }

    public Date getActualPaybackDate() {
        return actualPaybackDate;
    }

    public void setActualPaybackDate(Date actualPaybackDate) {
        this.actualPaybackDate = actualPaybackDate;
    }

    public BigDecimal getActualTotalAmount() {
        return actualTotalAmount;
    }

    public void setActualTotalAmount(BigDecimal actualTotalAmount) {
        this.actualTotalAmount = actualTotalAmount;
    }

    public BigDecimal getActualCapital() {
        return actualCapital;
    }

    public void setActualCapital(BigDecimal actualCapital) {
        this.actualCapital = actualCapital;
    }

    public BigDecimal getActualInterest() {
        return actualInterest;
    }

    public void setActualInterest(BigDecimal actualInterest) {
        this.actualInterest = actualInterest;
    }

    public String getPaybackChannel() {
        return paybackChannel;
    }

    public void setPaybackChannel(String paybackChannel) {
        this.paybackChannel = paybackChannel == null ? null : paybackChannel.trim();
    }

    public String getPaybackImportId() {
        return paybackImportId;
    }

    public void setPaybackImportId(String paybackImportId) {
        this.paybackImportId = paybackImportId == null ? null : paybackImportId.trim();
    }

    public String getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(String runningStatus) {
        this.runningStatus = runningStatus == null ? null : runningStatus.trim();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}