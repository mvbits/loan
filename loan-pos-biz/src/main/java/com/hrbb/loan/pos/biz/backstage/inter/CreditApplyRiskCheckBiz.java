/**
 * 
 *	哈尔滨银行
 * Copyright (c) 2007-2015 HRBB,Inc.All Rights Reserved.
 */
package com.hrbb.loan.pos.biz.backstage.inter;

/**
 * 
 * @author marco
 * @version $Id: CreditApplyAprvInfoBiz.java, v 0.1 2015-3-10 下午5:56:19 marco
 *          Exp $
 */
public interface CreditApplyRiskCheckBiz {

	public void doRiskCheck(String loanId);
}
