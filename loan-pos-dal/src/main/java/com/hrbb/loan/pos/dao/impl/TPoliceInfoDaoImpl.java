package com.hrbb.loan.pos.dao.impl;

import java.util.List;
import java.util.Map;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;
import com.hrbb.loan.pos.dao.TPoliceInfoDao;
import com.hrbb.loan.pos.dao.entity.TPoliceInfo;
@Repository("tPoliceInfoDao")
public class TPoliceInfoDaoImpl extends SqlSessionDaoSupport implements TPoliceInfoDao{

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(TPoliceInfo record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(TPoliceInfo record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TPoliceInfo selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(TPoliceInfo record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(TPoliceInfo record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Map<String, Object>> selectSelectiveMap(
			Map<String, Object> reqMap) {
		return getSqlSession().selectList("TPoliceInfoMapper.selectSelectiveMap", reqMap);
	}

	@Override
	public int insertSelectiveMap(Map<String, Object> reqMap) {
		return getSqlSession().insert("TPoliceInfoMapper.insertSelectiveMap", reqMap);
	}

	@Override
	public List<Map<String, Object>> selectByCertNo(Map<String, Object> reqMap) {
		return getSqlSession().selectList("TPoliceInfoMapper.selectByCertNo", reqMap);
	}

    /* 
     * 根据身份证号查询公安信息
     */
    @Override
    public TPoliceInfo selectByLoanId(String loanId) {
        return getSqlSession().selectOne("TPoliceInfoMapper.selectByLoanId", loanId);
    }

	@Override
	public int updateSelectiveMap(Map<String, Object> reqMap) {
		return getSqlSession().update("TPoliceInfoMapper.updateSelectiveMap", reqMap);
	}

}
