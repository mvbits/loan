package com.hrbb.loan.pos.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class TBankAccnoInfo implements Serializable{
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 3742901874392726609L;

	private String bankAccno;

	private String custId;

	private String bankName;

	private String bankBranName;

	private String bankSubbName;
	
	private String ptcptnm;
	
    private String cdtbranchid;

	private Date createTime;
	
	private String status;


	public String getBankAccno() {
		return bankAccno;
	}

	public void setBankAccno(String bankAccno) {
		this.bankAccno = bankAccno == null ? null : bankAccno.trim();
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId == null ? null : custId.trim();
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName == null ? null : bankName.trim();
	}

	public String getBankBranName() {
		return bankBranName;
	}

	public void setBankBranName(String bankBranName) {
		this.bankBranName = bankBranName == null ? null : bankBranName.trim();
	}

	public String getBankSubbName() {
		return bankSubbName;
	}

	public void setBankSubbName(String bankSubbName) {
		this.bankSubbName = bankSubbName == null ? null : bankSubbName.trim();
	}

	public String getPtcptnm() {
        return ptcptnm;
    }

    public void setPtcptnm(String ptcptnm) {
        this.ptcptnm = ptcptnm;
    }

    public String getCdtbranchid() {
        return cdtbranchid;
    }

    public void setCdtbranchid(String cdtbranchid) {
        this.cdtbranchid = cdtbranchid;
    }
    
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}