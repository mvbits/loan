package com.hrbb.loan.pos.dao.entity;

import java.io.Serializable;
import java.util.Date;

public class TBlacklist implements Serializable{
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -1138728745120473900L;

	private String id;

    private String infoType;
    private String infoTypeName;

    private String certType;

    private String infoDetail;

    private String confirmUser;

    private Date confirmTime;

    private String confirmReason;

    private Date effectTime;

    private Date uneffTime;

    private String intervalType;

    private Date modifyTime;

    private Date createTime;
    
    private String delFlag;
    
    private String confirmUserId;
    
    private String effectFlag;

    private String infoDimension;
    
    /**
	 * Getter method for property <tt>infoTypeName</tt>.
	 * 
	 * @return property value of infoTypeName
	 */
	public String getInfoTypeName() {
		return infoTypeName;
	}

	/**
	 * Setter method for property <tt>infoTypeName</tt>.
	 * 
	 * @param infoTypeName value to be assigned to property infoTypeName
	 */
	public void setInfoTypeName(String infoTypeName) {
		this.infoTypeName = infoTypeName;
	}

	/**
	 * Getter method for property <tt>infoDimension</tt>.
	 * 
	 * @return property value of infoDimension
	 */
	public String getInfoDimension() {
		return infoDimension;
	}

	/**
	 * Setter method for property <tt>infoDimension</tt>.
	 * 
	 * @param infoDimension value to be assigned to property infoDimension
	 */
	public void setInfoDimension(String infoDimension) {
		this.infoDimension = infoDimension;
	}

	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType == null ? null : infoType.trim();
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType == null ? null : certType.trim();
    }

    public String getInfoDetail() {
        return infoDetail;
    }

    public void setInfoDetail(String infoDetail) {
        this.infoDetail = infoDetail == null ? null : infoDetail.trim();
    }

    public String getConfirmUser() {
        return confirmUser;
    }

    public void setConfirmUser(String confirmUser) {
        this.confirmUser = confirmUser == null ? null : confirmUser.trim();
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getConfirmReason() {
        return confirmReason;
    }

    public void setConfirmReason(String confirmReason) {
        this.confirmReason = confirmReason == null ? null : confirmReason.trim();
    }

    public Date getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(Date effectTime) {
        this.effectTime = effectTime;
    }

    public Date getUneffTime() {
        return uneffTime;
    }

    public void setUneffTime(Date uneffTime) {
        this.uneffTime = uneffTime;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType == null ? null : intervalType.trim();
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getConfirmUserId() {
        return confirmUserId;
    }

    public void setConfirmUserId(String confirmUserId) {
        this.confirmUserId = confirmUserId;
    }

    public String getEffectFlag() {
        return effectFlag;
    }

    public void setEffectFlag(String effectFlag) {
        this.effectFlag = effectFlag;
    }
    
}