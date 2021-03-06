package com.hrbb.loan.pos.dao.entity;

import java.io.Serializable;

public class TAICLiguidationInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -3581740075199020615L;

	private Integer id;

    private String posCustId;

    private String orderNo;

    private String ligEntity;

    private String ligPrincipal;

    private String liqMen;

    private String ligSt;

    private String ligEndDate;

    private String debtTranee;

    private String claimTranee;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPosCustId() {
        return posCustId;
    }

    public void setPosCustId(String posCustId) {
        this.posCustId = posCustId == null ? null : posCustId.trim();
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getLigEntity() {
        return ligEntity;
    }

    public void setLigEntity(String ligEntity) {
        this.ligEntity = ligEntity == null ? null : ligEntity.trim();
    }

    public String getLigPrincipal() {
        return ligPrincipal;
    }

    public void setLigPrincipal(String ligPrincipal) {
        this.ligPrincipal = ligPrincipal == null ? null : ligPrincipal.trim();
    }

    public String getLiqMen() {
        return liqMen;
    }

    public void setLiqMen(String liqMen) {
        this.liqMen = liqMen == null ? null : liqMen.trim();
    }

    public String getLigSt() {
        return ligSt;
    }

    public void setLigSt(String ligSt) {
        this.ligSt = ligSt == null ? null : ligSt.trim();
    }

    public String getLigEndDate() {
        return ligEndDate;
    }

    public void setLigEndDate(String ligEndDate) {
        this.ligEndDate = ligEndDate == null ? null : ligEndDate.trim();
    }

    public String getDebtTranee() {
        return debtTranee;
    }

    public void setDebtTranee(String debtTranee) {
        this.debtTranee = debtTranee == null ? null : debtTranee.trim();
    }

    public String getClaimTranee() {
        return claimTranee;
    }

    public void setClaimTranee(String claimTranee) {
        this.claimTranee = claimTranee == null ? null : claimTranee.trim();
    }
}