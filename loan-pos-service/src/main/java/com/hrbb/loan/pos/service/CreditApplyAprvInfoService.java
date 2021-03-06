/**
 * 
 *	哈尔滨银行
 * Copyright (c) 2007-2015 HRBB,Inc.All Rights Reserved.
 */
package com.hrbb.loan.pos.service;

import java.util.List;
import java.util.Map;

import com.hrbb.loan.pos.dao.entity.TApproveResult;
import com.hrbb.loan.pos.dao.entity.TCreditApplyAprvInfo;
import com.hrbb.loan.pos.dao.entity.TCreditApplyAprvInfoKey;

/**
 * 
 * @author marco
 * @version $Id: CreditApplyAprvInfoService.java, v 0.1 2015-3-10 下午5:41:09
 *          marco Exp $
 */
public interface CreditApplyAprvInfoService {

	public int deleteByPrimaryKey(TCreditApplyAprvInfoKey key);

	public int insert(TCreditApplyAprvInfo record);

	public int insertSelective(TCreditApplyAprvInfo record);

	public TCreditApplyAprvInfo selectByPrimaryKey(TCreditApplyAprvInfoKey key);

	public List<TCreditApplyAprvInfo> selectBackToInfo(
			TCreditApplyAprvInfo record);

	public int updateByPrimaryKeySelective(TCreditApplyAprvInfo record);

	public int updateByPrimaryKey(TCreditApplyAprvInfo record);

	public Map<String, Object> update(TCreditApplyAprvInfo record);

	public TCreditApplyAprvInfo selectNotSubmit(TCreditApplyAprvInfoKey key);

	public int updateByBankSerialInfo(TCreditApplyAprvInfo record,
			List<Map<String, Object>> bankSerialList);

	public List<TCreditApplyAprvInfo> selectInforAuditList(
			TCreditApplyAprvInfo record);

	public int updateBackToReview(TCreditApplyAprvInfo ca);

	public int sendMsg(String loanId);

	public int updateCreditApplyRefuse(TCreditApplyAprvInfo record);

	public int insertCreditApplyAprvInfo30(TCreditApplyAprvInfo record);

	public TCreditApplyAprvInfo selectLastSubbmitted(TCreditApplyAprvInfoKey key);

	public int updateCreditApplyPass(TCreditApplyAprvInfo record);

	/**
	 * 
	 * @param record
	 * @return
	 */
	Map<String, Object> updateCreditApplyForUpSign(TCreditApplyAprvInfo record);

	/**
	 * 
	 * @param reqMap
	 * @return
	 */
	int updateCreditApplyAprv(Map<String, Object> reqMap);

	public int automaticAssignmentTask(TCreditApplyAprvInfo record);

	public Map<String, Object> updateCreditApplyForCashPooling(
			TCreditApplyAprvInfo record, Map<String, Object> resultMap);

	public int updateCreditApplyPassForCP(TCreditApplyAprvInfo record,
			TApproveResult ar);

	public int updateCreditApplyRefuseForCP(TCreditApplyAprvInfo record);
	
	/**
	 * @param timeout 超时
	 * @return 补件状态的申请超过2周的申请编号
	 */
	List<String> getLoanIdsByDate(String timeout);
	
	public int adjustApprovalInfo(String loanId, String applyStatus, String userName);
}
