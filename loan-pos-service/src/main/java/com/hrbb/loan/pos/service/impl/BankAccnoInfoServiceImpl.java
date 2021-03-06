/**
 * 
 *	哈尔滨银行
 * Copyright (c) 2007-2015 HRBB,Inc.All Rights Reserved.
 */
package com.hrbb.loan.pos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hrbb.loan.pos.dao.TBankAccnoInfoDao;
import com.hrbb.loan.pos.dao.entity.TBankAccnoInfo;
import com.hrbb.loan.pos.service.BankAccnoInfoService;

/**
 * 
 * @author marco
 * @version $Id: BankAccnoInfoServiceImpl.java, v 0.1 2015-8-7 下午5:30:39 marco
 *          Exp $
 */
@Service("bankAccnoInfoService")
public class BankAccnoInfoServiceImpl implements BankAccnoInfoService {
	@Autowired
	private TBankAccnoInfoDao tBankAccnoInfoDao;

	/**
	 * @see com.hrbb.loan.pos.service.BankAccnoInfoService#insertSelective(com.hrbb.loan.pos.dao.entity.TBankAccnoInfo)
	 */
	@Override
	public int insertSelective(TBankAccnoInfo record) {
		return tBankAccnoInfoDao.insertSelective(record);
	}

	/**
	 * @see com.hrbb.loan.pos.service.BankAccnoInfoService#selectByPrimaryKey(java.lang.String)
	 */
	@Override
	public TBankAccnoInfo selectByPrimaryKey(String bankAccno) {
		return tBankAccnoInfoDao.selectByPrimaryKey(bankAccno);
	}

	@Override
	public String getLoanBankNameByBankAccno(String bankAccno) {
		return tBankAccnoInfoDao.getLoanBankNameByBankAccno(bankAccno);
	}

}
