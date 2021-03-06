package com.hrbb.loan.pos.dao.entity;

import java.io.Serializable;

public class TActivityDimInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -446091983650797265L;

	private Integer id;

    private String dimenSql;

    private String dimName;
    
    private String actFlag;
    
    private String dimParamType;
    
    private String activityType;
    
    
    
    
    
    

    public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getDimParamType() {
		return dimParamType;
	}

	public void setDimParamType(String dimParamType) {
		this.dimParamType = dimParamType;
	}

	public String getActFlag() {
		return actFlag;
	}

	public void setActFlag(String actFlag) {
		this.actFlag = actFlag;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDimenSql() {
        return dimenSql;
    }

    public void setDimenSql(String dimenSql) {
        this.dimenSql = dimenSql == null ? null : dimenSql.trim();
    }

    public String getDimName() {
        return dimName;
    }

    public void setDimName(String dimName) {
        this.dimName = dimName == null ? null : dimName.trim();
    }
}