package com.hrbb.loan.pos.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TUpsBillcardstatQueryInfo implements Serializable{
    private Integer billCardstatQueryId;

    private String bankCardNo;

    private String userName;

    private String resCode;

    private String resMsg;

    private BigDecimal totalReceipt;

    private BigDecimal totalExpense;

    private Integer totalCount;

    private String fileUuid;

    private String sign;
    
    private Date createTime;

    public Integer getBillCardstatQueryId() {
        return billCardstatQueryId;
    }

    public void setBillCardstatQueryId(Integer billCardstatQueryId) {
        this.billCardstatQueryId = billCardstatQueryId;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo == null ? null : bankCardNo.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode == null ? null : resCode.trim();
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg == null ? null : resMsg.trim();
    }

    public BigDecimal getTotalReceipt() {
        return totalReceipt;
    }

    public void setTotalReceipt(BigDecimal totalReceipt) {
        this.totalReceipt = totalReceipt;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid == null ? null : fileUuid.trim();
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign == null ? null : sign.trim();
    }
    
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}