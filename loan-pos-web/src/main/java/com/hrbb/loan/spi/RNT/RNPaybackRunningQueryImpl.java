/**
 * 
 *	哈尔滨银行
 * Copyright (c) 2007-2015 HRBB,Inc.All Rights Reserved.
 */
package com.hrbb.loan.spi.RNT;

import org.springframework.stereotype.Service;

import com.hrbb.loan.spi.POSHService;
import com.hrbb.loan.spi.TC.TCPaybackRunningQueryImpl;

/**
 * T57还款申请查询
 * 
 * @author marco
 * @version $Id: TC2PaybackRunningQueryImpl.java, v 0.1 2015-8-20 上午11:40:35
 *          marco Exp $
 */
@Service("rnPaybackRunningQuery")
public class RNPaybackRunningQueryImpl extends TCPaybackRunningQueryImpl {
	@Override
	public String getChannel() {
		return POSHService.进件渠道_融360;
	}

	/**
	 * 前置加解密标签
	 * 
	 * @return
	 */
	@Override
	public String getEncryptedRender() {
		return "RNT";
	}
}