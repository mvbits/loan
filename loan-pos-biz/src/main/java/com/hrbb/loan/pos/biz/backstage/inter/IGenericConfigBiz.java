/**
 * 
 */
package com.hrbb.loan.pos.biz.backstage.inter;

import java.util.Date;
import java.util.Map;

/**
 * @author S
 *
 */
public interface IGenericConfigBiz {
	
	public Map<String, Object> getIssuerInfo(String bin);
	
	public String getMobileBelong(String mobile);
	
	/**
	 * 获取展业机构/推荐人信息
	 * @param contactNo
	 * @return
	 */
	public Map<String, Object> getRecInfo(String contactNo);
	
	public Map<String, Object> getRecInfoById(Integer id);
	
	public String getNextWorkingDate(Date source);
}
