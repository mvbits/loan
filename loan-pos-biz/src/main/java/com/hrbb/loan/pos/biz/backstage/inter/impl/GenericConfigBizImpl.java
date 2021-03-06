/**
 * 
 */
package com.hrbb.loan.pos.biz.backstage.inter.impl;


import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.hrbb.loan.pos.biz.backstage.inter.IGenericConfigBiz;
import com.hrbb.loan.pos.service.IGenericConfigService;

/**
 * @author S
 *
 */
@Component("genericConfigBiz")
public class GenericConfigBizImpl implements IGenericConfigBiz {
	
	private Logger log = LoggerFactory.getLogger(GenericConfigBizImpl.class);
	
	@Autowired
	@Qualifier("genericConfigService")
	private IGenericConfigService service;

	/* (non-Javadoc)
	 * @see com.hrbb.loan.pos.biz.backstage.inter.IGenericConfigBiz#getIssuerInfo(java.lang.String)
	 */
	@Override
	public Map<String, Object> getIssuerInfo(String cardno) {
		log.debug("executing getIssuerInfo....");
		
		Map<String, Object> map = service.getIssuerInfo(cardno);
		
		return map;
	}

	@Override
	public String getMobileBelong(String mobile) {
		if(mobile==null || mobile.trim().length()<7){
			return null;
		}
		String prefix = mobile.trim().substring(0,7);
		
		return service.getMobileBelong(prefix);
	}

	@Override
	public Map<String, Object> getRecInfo(String contactNo) {
		return service.getRecInfo(contactNo);
	}

	@Override
	public String getNextWorkingDate(Date source) {
		return service.getNextWorkingDate(source);
	}

	@Override
	public Map<String, Object> getRecInfoById(Integer id) {
		return service.getRecInfoById(id);
	}
	

}
