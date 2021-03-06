package com.hrbb.loan.pos.biz.backstage.inter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.hrbb.loan.pos.dao.entity.TApproveReject;
import com.hrbb.loan.pos.dao.entity.TApproveResult;
import com.hrbb.loan.pos.dao.entity.TContractManagement;
import com.hrbb.loan.pos.util.DateUtil;
import com.hrbb.loan.pos.util.MoneyUtils;

public interface LoanPosContractManagementBiz{
	
	public void deleteContractManagementInfo(String approveId);
	public int modifyContractManagementInfo(Map<String, Object> updateMap);
	public List<Map<String, Object>> queryContractManagement(Map<String,Object> reqMap);
	int insertContractManagementMap(Map<String, Object> reqMap);
	public TContractManagement selectByPrimaryKey(String custId);
	public int insertApproveReject(Map<String, Object> map);
	public int insertApproveAdjust(Map<String, Object> map);
	public int selectRejectMaxApproveNumByApproveId(String approveId);
	public int selectAdjustMaxApproveNumByApproveId(String approveId);
	public TApproveReject getRejectInfo(Map<String, Object> updateMap);
	public int updateApproveReject(Map<String, Object> map);
    public List<Map<String, Object>> selectSelectiveMap(Map<String, Object> reqMap);
    public Long countSelectiveMap(Map<String, Object> reqMap);
    public long countApprove(Map<String, Object> reqMap);
    public List<Map<String, Object>> queryAgreementDetail(String contNo);
    public void updateAgreement(Map<String, Object> reqMap);
	public TApproveResult getApproveInfo(String approveId);
	public List<Map<String, Object>> getApproveMap(String loanId);
	public String getcustId(String contNo);
	public TContractManagement getContractInfoByContNo(String contNo);
	public int updateContractStatus(Map<String, Object> updateMap);
    public TContractManagement getContractByContNo(String contNo);

    /**
     * 查询满足用款申请的协议列表
     * 
     * @param reqMap
     * @return
     */
    public List<Map<String, Object>> selectAvalibleSelectiveMap(Map<String, Object> reqMap);
    
    /**
     * 获取满足用款申请的协议总数
     * 
     * @param reqMap
     * @return
     */
    Long countAvalibleSelectiveMap(Map<String, Object> reqMap);
    
    /**
     * 查询协议详情，对象格式
     * 
     * @param contNo
     * @return
     */
    Map<String, Object> queryAgreementDetailForObjectFormat(String contNo);
    
    /**
     * 查询可以生成协议的批复列表
     * 
     * @param reqMap
     * @return
     */
    List<TApproveResult> queryAvailableContractManagement(Map<String, Object> reqMap);
    
    /**
     * 查询可生成协议的批复总数
     * 
     * @param reqMap
     * @return
     */
    Long countAvailableContractManagement(Map<String, Object> reqMap);
    
    /**
     * 插入协议生效信息
     * 
     * @param contNo
     * @param contract
     */
    void addContractStatusChangeMessage(String contNo, TContractManagement contract, String contStatus, String adjustReason);
    
    /**
	 * 调整修改审批结果
	 * @param updateMap
	 * @return
	 * @author Lin,Zhaolin
	 */
	public int modifyApprovalInfo(Map<String, Object> updateMap);
	/**
	 * 获取用于展示的合同对象，其中参数代码转换为中文
	 * @param contNo
	 * @return
	 */
	public TContractManagement getContractForDisplay(String contNo);
	
	public TContractManagement getContractByPayApplyId(String payApplyId);
	
	
	/**
     * @param contractMap 合同对象
     * @return pdf中model
     */
    public Map<String, Object> transferToPdfArgee(Map<String, Object> contractMap);
    
}
