/**
 * 
 *	哈尔滨银行
 * Copyright (c) 2007-2015 HRBB,Inc.All Rights Reserved.
 */
package com.hrbb.loan.pos.biz.constants;

import java.math.BigDecimal;

/**
 * 
 * @author XLY
 * @version $Id: RiskSuggestionDescEnum.java, v 0.1 2015-3-10 下午5:22:08 XLY Exp $
 */
public enum RiskSuggestionDescEnum {

	SUGGESTIONREZULT_OK( "建议通过",BigDecimal.ONE),
	SUGGESTIONREZULT_NG( "建议拒绝",BigDecimal.ONE),
	CALC_INDEX_00( "综合情况不符合哈行准入",BigDecimal.ONE),    
    CALC_INDEX_08( "申请人信用记录少",BigDecimal.ONE),    
    CALC_INDEX_09( "申请人授信金额低",BigDecimal.ONE),    
    CALC_INDEX_10( "申请人逾期笔数多",BigDecimal.ONE),    
    CALC_INDEX_11( "申请人贷款逾期月份数多",BigDecimal.ONE),    
    CALC_INDEX_12( "申请人贷记卡逾期月份数多",BigDecimal.ONE),    
    CALC_INDEX_13( "申请人近6月查询数多",BigDecimal.ONE),    
    CALC_INDEX_26( "最近四月至少一个月应该有流水",BigDecimal.ONE), 
    CALC_INDEX_27( "最近四月的月均交易额少于5000",BigDecimal.ONE), 
    CALC_INDEX_31( "申请人信用历史短",BigDecimal.ONE), 
    CALC_INDEX_33( "征信负债情况不满足准入",BigDecimal.ONE), 
    CALC_INDEX_35( "征信查询情况不满足准入",BigDecimal.ONE), 
    MODEL_RESULT_OK("通过",BigDecimal.ONE),
    MODEL_RESULT_NG("拒绝",BigDecimal.ONE),
    ;

    private BigDecimal value = null;

    private String description = null;
    
    RiskSuggestionDescEnum(String desc,BigDecimal value){
        this.value = value;
        this.description = desc;
    }

    /**
     * Getter method for property <tt>value</tt>.
     * 
     * @return property value of value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Setter method for property <tt>value</tt>.
     * 
     * @param value value to be assigned to property value
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Getter method for property <tt>description</tt>.
     * 
     * @return property value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter method for property <tt>description</tt>.
     * 
     * @param description value to be assigned to property description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
}
