/**
 * 
 *	哈尔滨银行
 * Copyright (c) 2007-2015 HRBB,Inc.All Rights Reserved.
 */
package com.hrbb.loan.pos.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.hrbb.loan.pos.dao.TCreditReportBriefDao;
import com.hrbb.loan.pos.dao.TCreditReportPoolDao;
import com.hrbb.loan.pos.dao.TCustomerDao;
import com.hrbb.loan.pos.dao.entity.TCreditReportBrief;
import com.hrbb.loan.pos.dao.entity.TCreditReportPool;
import com.hrbb.loan.pos.dao.entity.TCustomer;
import com.hrbb.loan.pos.service.CreditReportService;
import com.hrbb.loan.pos.util.DateUtil;
import com.hrbb.loan.pos.util.constants.ReviewNoteConstants;

/**
 * 
 * @author marco
 * @version $Id: CreditReportServiceImpl.java, v 0.1 2015-3-2 下午1:59:24 marco
 *          Exp $
 */
@Service("creditReportService")
public class CreditReportServiceImpl implements CreditReportService {

	@Autowired
	@Qualifier("tCreditReportBriefDao")
	private TCreditReportBriefDao tCreditReportBriefDao;

	@Autowired
	@Qualifier("tCustomerDao")
	private TCustomerDao custDao;
	
	@Autowired
	private TCreditReportPoolDao poolDao;

	/**
	 * 查询征信报告
	 * 
	 * @throws Exception
	 * 
	 * @see com.hrbb.loan.pos.service.CreditReportService#queryCreditReport(java.lang.String)
	 */
	@Override
	public int checkCreditReport(String paperId) {
		Map<String, Object> reqMap = Maps.newHashMap();
		reqMap.put(ReviewNoteConstants.CREDIT_REPORT_MAP_KEY_CERTNO, paperId);
		reqMap.put(ReviewNoteConstants.CREDIT_REPORT_MAP_KEY_CERTTYPE,
				ReviewNoteConstants.CREDIT_REPORT_MAP_VALUE_CERTTYPE_0);
		TCreditReportBrief crf = tCreditReportBriefDao
				.selectOneByCertNo(reqMap);
		// 没有记录
		if (crf == null) {
			return ReviewNoteConstants.CREDIT_REPORT_RESULT_VALUE_1;
		}
		// 记录存在
		// 征信报告不完整
		if (!ReviewNoteConstants.CREDIT_REPORT_COMPLETEFLAG_0.equals(crf
				.getCompleteFlag())) {
			return ReviewNoteConstants.CREDIT_REPORT_RESULT_VALUE_2;
		}
		// 征信报告完整
		long diffDay = DateUtil.getDateDiffFromToday(crf.getCommitTime());
		// 查询时间大于20天
		if (diffDay > 20) {
			return ReviewNoteConstants.CREDIT_REPORT_RESULT_VALUE_4;
		}
		// 申请人姓名
		Map<String, Object> map = Maps.newHashMap();
		map.put("paperId", Objects.firstNonNull(paperId, ""));
		List<TCustomer> cusList = custDao.selectSelective(map);
		if (cusList.size() == 0) {
			return ReviewNoteConstants.CREDIT_REPORT_RESULT_VALUE_5;
		}
		TCustomer c = cusList.get(0);
		// 申请人姓名与征信报告不一致
		if (!crf.getName().equals(c.getCustName())) {
			return ReviewNoteConstants.CREDIT_REPORT_RESULT_VALUE_5;
		}
		return ReviewNoteConstants.CREDIT_REPORT_RESULT_VALUE_0;
	}

	@Override
	public TCreditReportBrief getBrief(String paperId) {
		TCreditReportBrief brief = new TCreditReportBrief();
		brief.setCertNo(paperId);
		brief.setCertType(ReviewNoteConstants.CREDIT_REPORT_MAP_VALUE_CERTTYPE_0);

		brief = tCreditReportBriefDao.selectOne(brief);

		return brief;
	}

	@Override
	public List<TCreditReportPool> getCreditReportPool(
			Map<String, Object> reqMap) {
		return poolDao.selectSelective(reqMap);
	}

	@Override
	public int updateCreditReportPool(TCreditReportPool record) {
		return poolDao.updateByPrimaryKeySelective(record);
	}

	@Override
	public int insertCreditReportPool(TCreditReportPool record) {
		return poolDao.insertSelective(record);
	}
}
