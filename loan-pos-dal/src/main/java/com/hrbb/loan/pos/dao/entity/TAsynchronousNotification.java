package com.hrbb.loan.pos.dao.entity;

import java.util.Date;

public class TAsynchronousNotification {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_asynchronous_notification.id
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_asynchronous_notification.channel
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    private String channel;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_asynchronous_notification.notifyType
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    private String notifyType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_asynchronous_notification.notifyTime
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    private Date schedualTime;
    
    private Date executeTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_asynchronous_notification.notifyContent
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    private String notifyContent;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_asynchronous_notification.notifyStatus
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    private String notifyStatus;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_asynchronous_notification.remark
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    private String remark;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_asynchronous_notification.id
     *
     * @return the value of t_asynchronous_notification.id
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_asynchronous_notification.id
     *
     * @param id the value for t_asynchronous_notification.id
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_asynchronous_notification.channel
     *
     * @return the value of t_asynchronous_notification.channel
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    public String getChannel() {
        return channel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_asynchronous_notification.channel
     *
     * @param channel the value for t_asynchronous_notification.channel
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    public void setChannel(String channel) {
        this.channel = channel == null ? null : channel.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_asynchronous_notification.notifyType
     *
     * @return the value of t_asynchronous_notification.notifyType
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    public String getNotifyType() {
        return notifyType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_asynchronous_notification.notifyType
     *
     * @param notifytype the value for t_asynchronous_notification.notifyType
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    public void setNotifyType(String notifytype) {
        this.notifyType = notifytype == null ? null : notifytype.trim();
    }



    public Date getSchedualTime() {
		return schedualTime;
	}

	public void setSchedualTime(Date schedualTime) {
		this.schedualTime = schedualTime;
	}

	public Date getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(Date executeTime) {
		this.executeTime = executeTime;
	}

	/**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_asynchronous_notification.notifyContent
     *
     * @return the value of t_asynchronous_notification.notifyContent
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    public String getNotifyContent() {
        return notifyContent;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_asynchronous_notification.notifyContent
     *
     * @param notifycontent the value for t_asynchronous_notification.notifyContent
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    public void setNotifyContent(String notifycontent) {
        this.notifyContent = notifycontent == null ? null : notifycontent.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_asynchronous_notification.notifyStatus
     *
     * @return the value of t_asynchronous_notification.notifyStatus
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    public String getNotifyStatus() {
        return notifyStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_asynchronous_notification.notifyStatus
     *
     * @param notifystatus the value for t_asynchronous_notification.notifyStatus
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    public void setNotifyStatus(String notifystatus) {
        this.notifyStatus = notifystatus == null ? null : notifystatus.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_asynchronous_notification.remark
     *
     * @return the value of t_asynchronous_notification.remark
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    public String getRemark() {
        return remark;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_asynchronous_notification.remark
     *
     * @param remark the value for t_asynchronous_notification.remark
     *
     * @mbggenerated Sun Aug 30 14:58:40 CST 2015
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}