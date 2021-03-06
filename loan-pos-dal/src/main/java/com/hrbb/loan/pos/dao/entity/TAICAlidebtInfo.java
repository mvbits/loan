package com.hrbb.loan.pos.dao.entity;

import java.io.Serializable;

public class TAICAlidebtInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2139969056110507642L;

	private Integer id;

    private String posCustId;

    private String orderNo;

    private String sexyClean;

    private String ageClean;

    private String areaNameClean;

    private String ysFzd;

    private String qked;

    private String wyqk;

    private String dkdqsj;

    private String tbzh;

    private String legalPerson;

    private String dkqx;

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

    public String getSexyClean() {
        return sexyClean;
    }

    public void setSexyClean(String sexyClean) {
        this.sexyClean = sexyClean == null ? null : sexyClean.trim();
    }

    public String getAgeClean() {
        return ageClean;
    }

    public void setAgeClean(String ageClean) {
        this.ageClean = ageClean == null ? null : ageClean.trim();
    }

    public String getAreaNameClean() {
        return areaNameClean;
    }

    public void setAreaNameClean(String areaNameClean) {
        this.areaNameClean = areaNameClean == null ? null : areaNameClean.trim();
    }

    public String getYsFzd() {
        return ysFzd;
    }

    public void setYsFzd(String ysFzd) {
        this.ysFzd = ysFzd == null ? null : ysFzd.trim();
    }

    public String getQked() {
        return qked;
    }

    public void setQked(String qked) {
        this.qked = qked == null ? null : qked.trim();
    }

    public String getWyqk() {
        return wyqk;
    }

    public void setWyqk(String wyqk) {
        this.wyqk = wyqk == null ? null : wyqk.trim();
    }

    public String getDkdqsj() {
        return dkdqsj;
    }

    public void setDkdqsj(String dkdqsj) {
        this.dkdqsj = dkdqsj == null ? null : dkdqsj.trim();
    }

    public String getTbzh() {
        return tbzh;
    }

    public void setTbzh(String tbzh) {
        this.tbzh = tbzh == null ? null : tbzh.trim();
    }

    public String getLegalPerson() {
        return legalPerson;
    }

    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson == null ? null : legalPerson.trim();
    }

    public String getDkqx() {
        return dkqx;
    }

    public void setDkqx(String dkqx) {
        this.dkqx = dkqx == null ? null : dkqx.trim();
    }
}