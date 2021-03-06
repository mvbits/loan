package com.hrbb.loan.pos.dao.entity;

import java.io.Serializable;

public class TActivityContentInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2318862814050705227L;

	private Integer id;

    private String contentSql;

    private String contentName;
    
    private String actFlag;
    
    private String contentParamType;
    
    private String activityType;
    
    

    public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
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

    public String getContentSql() {
        return contentSql;
    }

    public void setContentSql(String contentSql) {
        this.contentSql = contentSql == null ? null : contentSql.trim();
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName == null ? null : contentName.trim();
    }

	public String getContentParamType() {
		return contentParamType;
	}

	public void setContentParamType(String contentParamType) {
		this.contentParamType = contentParamType;
	}
    
    
}