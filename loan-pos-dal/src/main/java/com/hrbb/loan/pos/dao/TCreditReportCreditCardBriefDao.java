package com.hrbb.loan.pos.dao;

import java.util.Map;

import com.hrbb.loan.pos.dao.entity.TCreditReportCreditCardBrief;

public interface TCreditReportCreditCardBriefDao {
	int deleteByPrimaryKey(String reportNo);

	int insert(TCreditReportCreditCardBrief record);

	int insertSelective(TCreditReportCreditCardBrief record);

	TCreditReportCreditCardBrief selectByPrimaryKey(String reportNo);

	int updateByPrimaryKeySelective(TCreditReportCreditCardBrief record);

	int updateByPrimaryKey(TCreditReportCreditCardBrief record);

	public int insertSelectiveMap(Map<String, Object> reqMap);

	public TCreditReportCreditCardBrief selectByQueryId(String queryId);
}