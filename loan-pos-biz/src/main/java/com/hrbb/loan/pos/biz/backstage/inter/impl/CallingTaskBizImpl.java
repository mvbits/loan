package com.hrbb.loan.pos.biz.backstage.inter.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hrbb.loan.pos.biz.backstage.inter.ICallingTaskBiz;
import com.hrbb.loan.pos.dao.entity.TCallingTask;
import com.hrbb.loan.pos.service.CallingTaskService;

@Component("iCallingTaskBiz")
public class CallingTaskBizImpl implements ICallingTaskBiz{

	@Autowired
	@Qualifier("CallingTaskService")
	CallingTaskService service;
	
	@Override
	public List<Map<String, Object>> selectSelective(Map<String, Object> ct) {
		// TODO Auto-generated method stub
		return service.selectSelective(ct);
	}
	@Override
	public long selectSelectiveForReviewCount(Map<String, Object> ct) {
		// TODO Auto-generated method stub
		return service.selectSelectiveForReviewCount(ct);
	}

	@Override
	public TCallingTask selectOne(String taskNo) {
		// TODO Auto-generated method stub
		return service.selectOne(taskNo);
	}

	@Override
	public boolean updateTaskClaimer(Map<String, Object> reqMap) {
		// TODO Auto-generated method stub
		return service.updateTaskClaimer(reqMap);
	}

	@Override
	public boolean updateGenerateTime(Map<String, Object> reqMap) {
		// TODO Auto-generated method stub
		return service.updateGenerateTime(reqMap);
	}
	
	@Override
	public int insertSelective(TCallingTask record) {
		return service.insertSelective(record);
	}
	
}
