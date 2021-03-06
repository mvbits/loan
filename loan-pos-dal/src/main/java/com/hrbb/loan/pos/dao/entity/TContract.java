package com.hrbb.loan.pos.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TContract implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 3815065272715471182L;

	private String contNo;

    private String cnContNo;

    private String contKind;

    private String loanId;

    private String custId;

    private String custName;

    private String prodId;

    private String prodName;

    private String loanForm;

    private String lowRiskType;

    private String currSign;

    private BigDecimal apprTotalAmt;

    private BigDecimal contTotalAmt;

    private BigDecimal contTouseAmt;

    private String maxAssuFlag;

    private String termUnit;

    private String apptTerm;

    private String contTerm;

    private Date beginDate;

    private Date endDate;

    private String assuKind;

    private String retuKind;

    private String retuKind2;

    private String retuSourRemark;

    private BigDecimal amt;

    private String subsFlag;

    private String inteType;

    private String inteAdjuKind;

    private String rateCode;

    private String rateNote;

    private BigDecimal basicInteRate;

    private BigDecimal floatInteRate;

    private BigDecimal moreInteRate;

    private BigDecimal inteRate;

    private String issetFineRate;

    private String fineRateType;

    private String fineCompKind;

    private String inteBalKind;

    private String inteCompKind;

    private String reckDay;

    private String turnGraceDay;

    private String riskFlag;

    private String riskRemark;

    private String fundPut;

    private String fundPutRemark;

    private String depeSubsac;

    private String depeSubsacNo;

    private String indeSubsac;

    private String indeSubsacNo;

    private String apprPrecond;

    private String payPrecond;

    private String riskPrecautmeas;

    private String otherCond;

    private String apprOperId;

    private String apprBankId;

    private String apprOperLev;

    private String otherAgre;

    private String loanPrecond;

    private Date signDate;

    private String initBankId;

    private String initOperId;

    private String bankId;

    private String operId;

    private String acBankId;

    private String contStatus;

    private Date apprDate;

    private Date apprEndDate;

    private String lastChanPerson;

    private String lastChanBankId;

    private Date lastChanDate;

    private String agentBankId;

    private String agentOperId;

    private String retuDay;

    private String fundSour;

    public String getContNo() {
        return contNo;
    }

    public void setContNo(String contNo) {
        this.contNo = contNo == null ? null : contNo.trim();
    }

    public String getCnContNo() {
        return cnContNo;
    }

    public void setCnContNo(String cnContNo) {
        this.cnContNo = cnContNo == null ? null : cnContNo.trim();
    }

    public String getContKind() {
        return contKind;
    }

    public void setContKind(String contKind) {
        this.contKind = contKind == null ? null : contKind.trim();
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId == null ? null : loanId.trim();
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId == null ? null : custId.trim();
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName == null ? null : custName.trim();
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId == null ? null : prodId.trim();
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName == null ? null : prodName.trim();
    }

    public String getLoanForm() {
        return loanForm;
    }

    public void setLoanForm(String loanForm) {
        this.loanForm = loanForm == null ? null : loanForm.trim();
    }

    public String getLowRiskType() {
        return lowRiskType;
    }

    public void setLowRiskType(String lowRiskType) {
        this.lowRiskType = lowRiskType == null ? null : lowRiskType.trim();
    }

    public String getCurrSign() {
        return currSign;
    }

    public void setCurrSign(String currSign) {
        this.currSign = currSign == null ? null : currSign.trim();
    }

    public BigDecimal getApprTotalAmt() {
        return apprTotalAmt;
    }

    public void setApprTotalAmt(BigDecimal apprTotalAmt) {
        this.apprTotalAmt = apprTotalAmt;
    }

    public BigDecimal getContTotalAmt() {
        return contTotalAmt;
    }

    public void setContTotalAmt(BigDecimal contTotalAmt) {
        this.contTotalAmt = contTotalAmt;
    }

    public BigDecimal getContTouseAmt() {
        return contTouseAmt;
    }

    public void setContTouseAmt(BigDecimal contTouseAmt) {
        this.contTouseAmt = contTouseAmt;
    }

    public String getMaxAssuFlag() {
        return maxAssuFlag;
    }

    public void setMaxAssuFlag(String maxAssuFlag) {
        this.maxAssuFlag = maxAssuFlag == null ? null : maxAssuFlag.trim();
    }

    public String getTermUnit() {
        return termUnit;
    }

    public void setTermUnit(String termUnit) {
        this.termUnit = termUnit == null ? null : termUnit.trim();
    }

    public String getApptTerm() {
        return apptTerm;
    }

    public void setApptTerm(String apptTerm) {
        this.apptTerm = apptTerm == null ? null : apptTerm.trim();
    }

    public String getContTerm() {
        return contTerm;
    }

    public void setContTerm(String contTerm) {
        this.contTerm = contTerm == null ? null : contTerm.trim();
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getAssuKind() {
        return assuKind;
    }

    public void setAssuKind(String assuKind) {
        this.assuKind = assuKind == null ? null : assuKind.trim();
    }

    public String getRetuKind() {
        return retuKind;
    }

    public void setRetuKind(String retuKind) {
        this.retuKind = retuKind == null ? null : retuKind.trim();
    }

    public String getRetuKind2() {
        return retuKind2;
    }

    public void setRetuKind2(String retuKind2) {
        this.retuKind2 = retuKind2 == null ? null : retuKind2.trim();
    }

    public String getRetuSourRemark() {
        return retuSourRemark;
    }

    public void setRetuSourRemark(String retuSourRemark) {
        this.retuSourRemark = retuSourRemark == null ? null : retuSourRemark.trim();
    }

    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    public String getSubsFlag() {
        return subsFlag;
    }

    public void setSubsFlag(String subsFlag) {
        this.subsFlag = subsFlag == null ? null : subsFlag.trim();
    }

    public String getInteType() {
        return inteType;
    }

    public void setInteType(String inteType) {
        this.inteType = inteType == null ? null : inteType.trim();
    }

    public String getInteAdjuKind() {
        return inteAdjuKind;
    }

    public void setInteAdjuKind(String inteAdjuKind) {
        this.inteAdjuKind = inteAdjuKind == null ? null : inteAdjuKind.trim();
    }

    public String getRateCode() {
        return rateCode;
    }

    public void setRateCode(String rateCode) {
        this.rateCode = rateCode == null ? null : rateCode.trim();
    }

    public String getRateNote() {
        return rateNote;
    }

    public void setRateNote(String rateNote) {
        this.rateNote = rateNote == null ? null : rateNote.trim();
    }

    public BigDecimal getBasicInteRate() {
        return basicInteRate;
    }

    public void setBasicInteRate(BigDecimal basicInteRate) {
        this.basicInteRate = basicInteRate;
    }

    public BigDecimal getFloatInteRate() {
        return floatInteRate;
    }

    public void setFloatInteRate(BigDecimal floatInteRate) {
        this.floatInteRate = floatInteRate;
    }

    public BigDecimal getMoreInteRate() {
        return moreInteRate;
    }

    public void setMoreInteRate(BigDecimal moreInteRate) {
        this.moreInteRate = moreInteRate;
    }

    public BigDecimal getInteRate() {
        return inteRate;
    }

    public void setInteRate(BigDecimal inteRate) {
        this.inteRate = inteRate;
    }

    public String getIssetFineRate() {
        return issetFineRate;
    }

    public void setIssetFineRate(String issetFineRate) {
        this.issetFineRate = issetFineRate == null ? null : issetFineRate.trim();
    }

    public String getFineRateType() {
        return fineRateType;
    }

    public void setFineRateType(String fineRateType) {
        this.fineRateType = fineRateType == null ? null : fineRateType.trim();
    }

    public String getFineCompKind() {
        return fineCompKind;
    }

    public void setFineCompKind(String fineCompKind) {
        this.fineCompKind = fineCompKind == null ? null : fineCompKind.trim();
    }

    public String getInteBalKind() {
        return inteBalKind;
    }

    public void setInteBalKind(String inteBalKind) {
        this.inteBalKind = inteBalKind == null ? null : inteBalKind.trim();
    }

    public String getInteCompKind() {
        return inteCompKind;
    }

    public void setInteCompKind(String inteCompKind) {
        this.inteCompKind = inteCompKind == null ? null : inteCompKind.trim();
    }

    public String getReckDay() {
        return reckDay;
    }

    public void setReckDay(String reckDay) {
        this.reckDay = reckDay == null ? null : reckDay.trim();
    }

    public String getTurnGraceDay() {
        return turnGraceDay;
    }

    public void setTurnGraceDay(String turnGraceDay) {
        this.turnGraceDay = turnGraceDay == null ? null : turnGraceDay.trim();
    }

    public String getRiskFlag() {
        return riskFlag;
    }

    public void setRiskFlag(String riskFlag) {
        this.riskFlag = riskFlag == null ? null : riskFlag.trim();
    }

    public String getRiskRemark() {
        return riskRemark;
    }

    public void setRiskRemark(String riskRemark) {
        this.riskRemark = riskRemark == null ? null : riskRemark.trim();
    }

    public String getFundPut() {
        return fundPut;
    }

    public void setFundPut(String fundPut) {
        this.fundPut = fundPut == null ? null : fundPut.trim();
    }

    public String getFundPutRemark() {
        return fundPutRemark;
    }

    public void setFundPutRemark(String fundPutRemark) {
        this.fundPutRemark = fundPutRemark == null ? null : fundPutRemark.trim();
    }

    public String getDepeSubsac() {
        return depeSubsac;
    }

    public void setDepeSubsac(String depeSubsac) {
        this.depeSubsac = depeSubsac == null ? null : depeSubsac.trim();
    }

    public String getDepeSubsacNo() {
        return depeSubsacNo;
    }

    public void setDepeSubsacNo(String depeSubsacNo) {
        this.depeSubsacNo = depeSubsacNo == null ? null : depeSubsacNo.trim();
    }

    public String getIndeSubsac() {
        return indeSubsac;
    }

    public void setIndeSubsac(String indeSubsac) {
        this.indeSubsac = indeSubsac == null ? null : indeSubsac.trim();
    }

    public String getIndeSubsacNo() {
        return indeSubsacNo;
    }

    public void setIndeSubsacNo(String indeSubsacNo) {
        this.indeSubsacNo = indeSubsacNo == null ? null : indeSubsacNo.trim();
    }

    public String getApprPrecond() {
        return apprPrecond;
    }

    public void setApprPrecond(String apprPrecond) {
        this.apprPrecond = apprPrecond == null ? null : apprPrecond.trim();
    }

    public String getPayPrecond() {
        return payPrecond;
    }

    public void setPayPrecond(String payPrecond) {
        this.payPrecond = payPrecond == null ? null : payPrecond.trim();
    }

    public String getRiskPrecautmeas() {
        return riskPrecautmeas;
    }

    public void setRiskPrecautmeas(String riskPrecautmeas) {
        this.riskPrecautmeas = riskPrecautmeas == null ? null : riskPrecautmeas.trim();
    }

    public String getOtherCond() {
        return otherCond;
    }

    public void setOtherCond(String otherCond) {
        this.otherCond = otherCond == null ? null : otherCond.trim();
    }

    public String getApprOperId() {
        return apprOperId;
    }

    public void setApprOperId(String apprOperId) {
        this.apprOperId = apprOperId == null ? null : apprOperId.trim();
    }

    public String getApprBankId() {
        return apprBankId;
    }

    public void setApprBankId(String apprBankId) {
        this.apprBankId = apprBankId == null ? null : apprBankId.trim();
    }

    public String getApprOperLev() {
        return apprOperLev;
    }

    public void setApprOperLev(String apprOperLev) {
        this.apprOperLev = apprOperLev == null ? null : apprOperLev.trim();
    }

    public String getOtherAgre() {
        return otherAgre;
    }

    public void setOtherAgre(String otherAgre) {
        this.otherAgre = otherAgre == null ? null : otherAgre.trim();
    }

    public String getLoanPrecond() {
        return loanPrecond;
    }

    public void setLoanPrecond(String loanPrecond) {
        this.loanPrecond = loanPrecond == null ? null : loanPrecond.trim();
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public String getInitBankId() {
        return initBankId;
    }

    public void setInitBankId(String initBankId) {
        this.initBankId = initBankId == null ? null : initBankId.trim();
    }

    public String getInitOperId() {
        return initOperId;
    }

    public void setInitOperId(String initOperId) {
        this.initOperId = initOperId == null ? null : initOperId.trim();
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId == null ? null : bankId.trim();
    }

    public String getOperId() {
        return operId;
    }

    public void setOperId(String operId) {
        this.operId = operId == null ? null : operId.trim();
    }

    public String getAcBankId() {
        return acBankId;
    }

    public void setAcBankId(String acBankId) {
        this.acBankId = acBankId == null ? null : acBankId.trim();
    }

    public String getContStatus() {
        return contStatus;
    }

    public void setContStatus(String contStatus) {
        this.contStatus = contStatus == null ? null : contStatus.trim();
    }

    public Date getApprDate() {
        return apprDate;
    }

    public void setApprDate(Date apprDate) {
        this.apprDate = apprDate;
    }

    public Date getApprEndDate() {
        return apprEndDate;
    }

    public void setApprEndDate(Date apprEndDate) {
        this.apprEndDate = apprEndDate;
    }

    public String getLastChanPerson() {
        return lastChanPerson;
    }

    public void setLastChanPerson(String lastChanPerson) {
        this.lastChanPerson = lastChanPerson == null ? null : lastChanPerson.trim();
    }

    public String getLastChanBankId() {
        return lastChanBankId;
    }

    public void setLastChanBankId(String lastChanBankId) {
        this.lastChanBankId = lastChanBankId == null ? null : lastChanBankId.trim();
    }

    public Date getLastChanDate() {
        return lastChanDate;
    }

    public void setLastChanDate(Date lastChanDate) {
        this.lastChanDate = lastChanDate;
    }

    public String getAgentBankId() {
        return agentBankId;
    }

    public void setAgentBankId(String agentBankId) {
        this.agentBankId = agentBankId == null ? null : agentBankId.trim();
    }

    public String getAgentOperId() {
        return agentOperId;
    }

    public void setAgentOperId(String agentOperId) {
        this.agentOperId = agentOperId == null ? null : agentOperId.trim();
    }

    public String getRetuDay() {
        return retuDay;
    }

    public void setRetuDay(String retuDay) {
        this.retuDay = retuDay == null ? null : retuDay.trim();
    }

    public String getFundSour() {
        return fundSour;
    }

    public void setFundSour(String fundSour) {
        this.fundSour = fundSour == null ? null : fundSour.trim();
    }
}