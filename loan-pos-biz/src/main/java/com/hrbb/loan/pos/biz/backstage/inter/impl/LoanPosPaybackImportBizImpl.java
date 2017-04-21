package com.hrbb.loan.pos.biz.backstage.inter.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.hrbb.loan.acct.facade.MadeLoanProcessBizHession;
import com.hrbb.loan.acct.facade.bean.MadeLoanAheadPrepaymentReq;
import com.hrbb.loan.acct.facade.bean.MadeLoanAheadPrepaymentRes;
import com.hrbb.loan.acct.facade.common.constants.ChannelConstants;
import com.hrbb.loan.pos.biz.backstage.inter.LoanPosPaybackImportBiz;
import com.hrbb.loan.pos.dao.entity.TCfgChannelAccount;
import com.hrbb.loan.pos.dao.entity.TContractManagement;
import com.hrbb.loan.pos.dao.entity.TPaybackApplyInfo;
import com.hrbb.loan.pos.dao.entity.TPaybackImportInfo;
import com.hrbb.loan.pos.dao.entity.TReceiptInfo;
import com.hrbb.loan.pos.service.LoanPosContractManagementService;
import com.hrbb.loan.pos.service.LoanPosPaybackImportService;
import com.hrbb.loan.pos.service.LoanPosPaybackService;
import com.hrbb.loan.pos.service.RepaymentApplyService;
import com.hrbb.loan.pos.util.ListUtil;
@Component("loanPosPaybackImportBiz")
public class LoanPosPaybackImportBizImpl implements LoanPosPaybackImportBiz {
	private Logger logger = LoggerFactory.getLogger(LoanPosPaybackImportBizImpl.class);
	@Autowired
	private RepaymentApplyService repaymentApplyService;
	@Autowired
	private MadeLoanProcessBizHession madeLoanProcessBizHession;
	@Autowired
	private LoanPosPaybackImportService loanPosPaybackImportService;
	@Autowired
	private LoanPosPaybackService loanPosPaybackService;
	@Autowired
	private LoanPosContractManagementService loanPosContractManagementService;
	private BigDecimal ZERO = new BigDecimal("0");
	public Map<String, String> effectiveAcctMap = null;
	@PostConstruct
	public void init(){
		effectiveAcctMap = Maps.newHashMap();
		List<TCfgChannelAccount> channelAccounts = repaymentApplyService.getEffectAccount();
		if(ListUtil.isNotEmpty(channelAccounts)){
			for(TCfgChannelAccount account : channelAccounts){
				effectiveAcctMap.put(account.getChannel(), account.getAccountIssue());
			}
		}
	}
	@Override
	public List<TPaybackImportInfo> queryPaybackImportInfo(Map<String, Object> reqMap) {
		List<TPaybackImportInfo> l =  loanPosPaybackImportService.queryPaybackImportInfo(reqMap);
		return l;
	}
	@Override
	public List<TPaybackImportInfo> queryMatchedPaybackImportInfo(Map<String, Object> reqMap) {
		List<TPaybackImportInfo> l =  loanPosPaybackImportService.queryMatchedPaybackImportInfo(reqMap);
		return l;
	}
	@Override
	public List<TPaybackImportInfo> queryTransferInRecord(
			Map<String, Object> reqMap) {
		return loanPosPaybackImportService.queryTransferInRecord(reqMap);
	}
	@Override
	public long countImportNumber(Map<String, Object> reqMap) {
		return loanPosPaybackImportService.countImportNumber(reqMap);
	}
	@Override
	public Map<String, Object> addListRepayment(String receiptId,String actualTotalAmount, String actualCapital,String actualInterest) {
		
		try{
		
		//根据借据id获取借据记录
		
	    TReceiptInfo receiptInfo = loanPosPaybackService.getReceiptInfoByReceiptId(receiptId);
	
		if(null == receiptInfo){
			logger.error(receiptId + "没有该借据");
			return Maps.newHashMap();
		}
		//通过合同号获取合同记录
		String contNo = receiptInfo.getContNo();
		TContractManagement cont = loanPosContractManagementService.getContractInfoByContNo(contNo);
		if(cont==null){
			logger.error(contNo + "没有该合同");
			return Maps.newHashMap();
		}
		//请求Bean
		MadeLoanAheadPrepaymentReq madeLoanAheadPrepaymentReq = new MadeLoanAheadPrepaymentReq();
		
		madeLoanAheadPrepaymentReq.setChannelId(ChannelConstants.POS.getValue());
		
		madeLoanAheadPrepaymentReq.setListId(receiptInfo.getListId());
		madeLoanAheadPrepaymentReq.setContNo(cont.getAcctContNo());
		madeLoanAheadPrepaymentReq.setLoanId(cont.getLoanId());
		madeLoanAheadPrepaymentReq.setBillId(receiptInfo.getListId());
		madeLoanAheadPrepaymentReq.setLoanAcNo(receiptInfo.getLoanAcNo());// 哪个账号？
		madeLoanAheadPrepaymentReq.setCustId(receiptInfo.getCustId());
		madeLoanAheadPrepaymentReq.setCustName(receiptInfo.getCustName());
		madeLoanAheadPrepaymentReq.setProdId("1001020305");//产品代码
		madeLoanAheadPrepaymentReq.setProdName("POS");
		madeLoanAheadPrepaymentReq.setTcapi(receiptInfo.getPayApplyAmt().toString());//贷款金额
		
		madeLoanAheadPrepaymentReq.setBeginDate(receiptInfo.getBeginDate());//起始日期 --日期怎么定？
		madeLoanAheadPrepaymentReq.setEndDate(receiptInfo.getEndDate());//结束日期
		
		madeLoanAheadPrepaymentReq.setLoanIntRate(receiptInfo.getLoanInterest().multiply(new BigDecimal("10")).toString());//贷款利率，千分比？
		
//		FIXME 字段取值存在疑议
		madeLoanAheadPrepaymentReq.setTterm(Integer.valueOf(receiptInfo.getPayApplyTerm()));//贷款期限
		madeLoanAheadPrepaymentReq.setSterm(1);//期次
		madeLoanAheadPrepaymentReq.setScterm(1);//当前期次(理论)
		madeLoanAheadPrepaymentReq.setCterm(1);//贷款期数(理论)
		madeLoanAheadPrepaymentReq.setLastreceDate(new Date());//上次计收计提日 - ？
		madeLoanAheadPrepaymentReq.setSendreceDate(new Date());//上次计提上送日 - ？
		madeLoanAheadPrepaymentReq.setSumrece(new BigDecimal("0").toString());//每日计提累计值
		madeLoanAheadPrepaymentReq.setAmt(new BigDecimal("0").toString());//调整前分期还款额 
		madeLoanAheadPrepaymentReq.setListNum(1);//记账顺序  - 能否不填，让核算自己填充？
		//0是不逾期 ， 1是逾期
		madeLoanAheadPrepaymentReq.setOverFlag("0");//逾期标志
		madeLoanAheadPrepaymentReq.setMonthInte(new BigDecimal("0").toString());//月末计提 ？
		madeLoanAheadPrepaymentReq.setRetWay("01");//回收方式 01:正常回收
		madeLoanAheadPrepaymentReq.setListType("03");//单据类型 03提前还款
		
		madeLoanAheadPrepaymentReq.setBusiType("10");//业务类型 - 10：提前结清 ?
		
		//计息方式： 0-按整期算（分期型）；1-按实际天数算（白条型）
		madeLoanAheadPrepaymentReq.setCompdayKind("1");
		
		madeLoanAheadPrepaymentReq.setCompcapiKind("0");//计息本金取值方式  0:按余额计算  1:按提前归还本金计算。（学生贷：分期型，提前还清当期；学生贷、POS贷提前结清）
		madeLoanAheadPrepaymentReq.setRetuType("2");//提前还款约定   2:不调整分期还款额
		madeLoanAheadPrepaymentReq.setListStat("1");//单据状态  1：未记账
		madeLoanAheadPrepaymentReq.setAheaKind("2");//提前还款类型  1:还本付息; 2:提前结清; 4:提前结清当期
		madeLoanAheadPrepaymentReq.setAheaamtkind("2");//确定还款金额类型
		madeLoanAheadPrepaymentReq.setCurrency("CNY");//币种
		//还日期 
		madeLoanAheadPrepaymentReq.setSdate(receiptInfo.getEndDate());
		
		madeLoanAheadPrepaymentReq.setRcapi(actualCapital);//提前归还本金 - 试算后的rcapi ?
		
		madeLoanAheadPrepaymentReq.setRinte(actualInterest);//提前归还利息 - 试算后的rInte ?
		logger.info("rcapi=[{}],rinte=[{}]",actualCapital,actualInterest);
		madeLoanAheadPrepaymentReq.setSsubsamt(actualTotalAmount);//应扣金额 - 试算后的sSubsAmt ?
		madeLoanAheadPrepaymentReq.setSsubsinte(actualInterest);//应扣利息 - 试算后的ssubsinte ?
		madeLoanAheadPrepaymentReq.setScapi(actualCapital);//应还本金 - 试算后的rcapi ?
		madeLoanAheadPrepaymentReq.setSinte(actualInterest);//应还利息  - 试算后的rInte ?
		madeLoanAheadPrepaymentReq.setSumamt(actualTotalAmount);//最终应还款额 - 试算后sumamt ?
		madeLoanAheadPrepaymentReq.setNewamt(ZERO.toString());//调整后分期还款额 - 试算后的newamt ?
		
		madeLoanAheadPrepaymentReq.setRdate(new Date());//还款日期 - 取当前系统日期workDate ?
		madeLoanAheadPrepaymentReq.setAcDate(new Date());//记账日期 - 取当前系统日期workDate ?
		madeLoanAheadPrepaymentReq.setAcTime(new SimpleDateFormat("HHmmss").format(new Date()));//记账时间 - 取当前系统时间
		
		/**
		 * 	<select name="subskind" desc="扣款账户类型（信贷）">
			<option value="0" text="临时存欠/应解汇款" />
			<option value="1" text="卡" />
			<option value="2" text="活期一本通" />
			<option value="3" text="理财卡" />
			<option value="4" text="单位基金" />
			<option value="5" text="存款账户" />
			<option value="6" text="美元储蓄" />
			<option value="7" text="港币储蓄" />
			<option value="8" text="信用卡" />
			<option value="9" text="现金" />
			</select>
		 */
		madeLoanAheadPrepaymentReq.setSubsKind("1");//账户类型
		
//		BigDecimal zero = new BigDecimal(0.00);
		madeLoanAheadPrepaymentReq.setSainte(ZERO.toString());
		madeLoanAheadPrepaymentReq.setSbinte(ZERO.toString());
		madeLoanAheadPrepaymentReq.setSafine(ZERO.toString());
		madeLoanAheadPrepaymentReq.setSbfine(ZERO.toString());
		madeLoanAheadPrepaymentReq.setScfine(ZERO.toString());
		madeLoanAheadPrepaymentReq.setSdfine(ZERO.toString());
		madeLoanAheadPrepaymentReq.setSallointe(ZERO.toString());
		madeLoanAheadPrepaymentReq.setRainte(ZERO.toString());
		madeLoanAheadPrepaymentReq.setRbinte(ZERO.toString());
		madeLoanAheadPrepaymentReq.setRafine(ZERO.toString());
		madeLoanAheadPrepaymentReq.setRbfine(ZERO.toString());
		madeLoanAheadPrepaymentReq.setRcfine(ZERO.toString());
		madeLoanAheadPrepaymentReq.setRdfine(ZERO.toString());
		madeLoanAheadPrepaymentReq.setOtherkillamt(ZERO.toString());//其他抵扣金额
		madeLoanAheadPrepaymentReq.setSsubsamt(ZERO.toString());//应扣金额
		madeLoanAheadPrepaymentReq.setSsubsinte(ZERO.toString());//应扣利息
		madeLoanAheadPrepaymentReq.setRcapiandint(ZERO.toString());//提前归还本息总额
		madeLoanAheadPrepaymentReq.setFoulfetch("0");//是否收违约金
		madeLoanAheadPrepaymentReq.setRfoul(ZERO.toString());//违约金金额
		madeLoanAheadPrepaymentReq.setSuminte(ZERO.toString());//应收利息累计
		madeLoanAheadPrepaymentReq.setDbal(ZERO.toString());//呆账本金/已减值本金
		
		madeLoanAheadPrepaymentReq.setSubsacno((String)effectiveAcctMap.get(cont.getChannel()));//还款账号
		
		madeLoanAheadPrepaymentReq.setBal(receiptInfo.getLoanTotalBalance().toString());//贷款余额 = 剩余本金-本次还款本金
		
		madeLoanAheadPrepaymentReq.setOperId("680199");
		madeLoanAheadPrepaymentReq.setBankId("6801");
		madeLoanAheadPrepaymentReq.setChargeId("");
		madeLoanAheadPrepaymentReq.setCheckId("6801");
		Map<String, Object> resMap = Maps.newHashMap();
		MadeLoanAheadPrepaymentRes resBean = madeLoanProcessBizHession.addListPrepayment(madeLoanAheadPrepaymentReq);
		logger.debug("还款准备返回结果code:" + resBean.getRespCode());
		resMap.put("madeLoanRes", resBean);
		resMap.put("loanAcNo", receiptInfo.getLoanAcNo());
		resMap.put("prepaymentListId", resBean.getListId());
		return resMap;
		}catch(Exception e){
			logger.error(receiptId + "还款准备发生异常" + e.getMessage());
			Map<String, Object> resMap = Maps.newHashMap();
			return resMap;
		}
	}
	@Override
	public Map<String, Object> getImportInfobyId(String importRunningId) {
		return loanPosPaybackImportService.getImportInfobyId(importRunningId);
	}

}