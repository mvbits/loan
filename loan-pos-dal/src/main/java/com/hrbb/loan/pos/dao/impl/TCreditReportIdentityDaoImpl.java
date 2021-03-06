package com.hrbb.loan.pos.dao.impl;

import java.util.Map;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import com.hrbb.loan.pos.dao.TCreditReportIdentityDao;
import com.hrbb.loan.pos.dao.entity.TCreditReportIdentity;

/**
 *<h1>身份信息</h1>
 *@author Johnson Song
 *@version Id: TCreditApplyDaoImpl.java, v 1.0 2015-2-15 上午11:03:53 Johnson Song Exp
 */
@Repository("tCreditReportIdentityDao")
public class TCreditReportIdentityDaoImpl extends SqlSessionDaoSupport implements TCreditReportIdentityDao {

	@Override
	public int deleteByPrimaryKey(String reportNo) {
		// TODO 自动生成的方法存根
		return 0;
	}

	@Override
	public int insert(TCreditReportIdentity record) {
		// TODO 自动生成的方法存根
		return 0;
	}

	@Override
	public int insertSelective(TCreditReportIdentity record) {
		// TODO 自动生成的方法存根
		return 0;
	}

	@Override
	public TCreditReportIdentity selectByPrimaryKey(String reportNo) {
		// TODO 自动生成的方法存根
		return getSqlSession().selectOne("TCreditReportIdentityMapper.selectByPrimaryKey", reportNo);
	}

	@Override
	public int updateByPrimaryKeySelective(TCreditReportIdentity record) {
		// TODO 自动生成的方法存根
		return 0;
	}

	@Override
	public int updateByPrimaryKey(TCreditReportIdentity record) {
		// TODO 自动生成的方法存根
		return 0;
	}

	@Override
	public int insertSelectiveMap(Map<String, Object> reqMap) {
		return getSqlSession().insert("TCreditReportIdentityMapper.insertSelectiveMap", reqMap);
	}

	@Override
	public TCreditReportIdentity selectInfor(Map<String, Object> reqMap) {
		// TODO 自动生成的方法存根
		return getSqlSession().selectOne("TCreditReportIdentityMapper.selectInfor", reqMap);
	}

	@Override
	public TCreditReportIdentity selectByQueryId(String queryId) {
		// TODO 自动生成的方法存根
		return getSqlSession().selectOne("TCreditReportIdentityMapper.selectByQueryId", queryId);
	}
}
