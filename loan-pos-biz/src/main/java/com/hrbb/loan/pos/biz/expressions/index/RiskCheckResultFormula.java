/**
 * 
 *	哈尔滨银行
 * Copyright (c) 2007-2015 HRBB,Inc.All Rights Reserved.
 */
package com.hrbb.loan.pos.biz.expressions.index;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hrbb.loan.pos.biz.constants.RiskCheckConstants;
import com.hrbb.loan.pos.biz.constants.RiskSuggestionDescEnum;
import com.hrbb.loan.pos.biz.expressions.bean.MultResultBean;
import com.hrbb.loan.pos.biz.expressions.bean.RiskSuggestionBean;
import com.hrbb.loan.pos.dao.entity.TCreditApply;
import com.hrbb.loan.pos.dao.entity.TCreditReportIndicator;
import com.hrbb.loan.pos.dao.entity.TCustomer;
import com.hrbb.loan.pos.dao.entity.TModelResult;
import com.hrbb.loan.pos.dao.entity.TPosCustInfo;
import com.hrbb.loan.pos.dao.entity.TRiskCheckModelIndex;
import com.hrbb.loan.pos.dao.entity.TRiskCheckResult;
import com.hrbb.loan.pos.util.MathUtils;
import com.hrbb.loan.pos.util.StringUtil;

/**
 * 计算RiskCheckResultFormula
 * 
 * @author XLY
 * @version $Id: ModelIndexFormula.java, v 0.1 2015-3-13 下午3:36:40 XLY Exp $
 */
public class RiskCheckResultFormula {

	private final static Logger logger = LoggerFactory
			.getLogger(RiskCheckResultFormula.class);

	private final static BigDecimal TEN_THOUSAND = new BigDecimal(10000);

	/**
	 * 
	 * 
	 * @param posInfoBean
	 *            pos流水统计信息
	 * @param crBean
	 *            信用报告原始信息
	 * @param tCreditApplyBean
	 *            贷款申请相关信息
	 */
	public static TRiskCheckResult initRiskCheckResult(
			TCreditApply tCreditApply, TPosCustInfo tPosCustInfo,
			TCustomer tCustomer, RiskSuggestionBean riskSuggestionBean,
			BigDecimal finalModelCl, BigDecimal finalModelP,
			MultResultBean mrb, TCreditReportIndicator cri,
			TRiskCheckModelIndex modelIndexBean, TModelResult modelResult) {

		TRiskCheckResult r = new TRiskCheckResult();

		r.setLoanId(tCreditApply.getLoanId());
		r.setResult01(tCreditApply.getChannel());
		r.setResult02(tPosCustInfo.getOperAddrCode());
		r.setResult03(tPosCustInfo.getPosCustName());
		r.setResult04(tCustomer.getCustName());
		r.setResult05(tCustomer.getMobilePhone());
		r.setResult06(tCustomer.getPaperId());
		r.setResult07(riskSuggestionBean.getSuggestDecision());
		r.setResult43(modelResult.getApprovedPeriod());
		r.setVersion(modelResult.getVersion());
		// 拒绝时，额度和利率设为0
		if (RiskSuggestionDescEnum.SUGGESTIONREZULT_NG.getDescription().equals(
				riskSuggestionBean.getSuggestDecision())) {
			r.setResult08(BigDecimal.ZERO);
			r.setResult09(BigDecimal.ZERO);
		} else {
			r.setResult08(modelResult.getApprovedAmt().multiply(new BigDecimal(10000)));
			r.setResult09(modelResult.getApprovedRate().multiply(new BigDecimal(100)));
//			r.setResult08(finalModelCl);
//			r.setResult09(finalModelP.multiply(new BigDecimal(100)));
		}
		r.setResult10(riskSuggestionBean.getDeclineReason());
		r.setResult11(riskSuggestionBean.getSuggestDiligence());
		r.setResult12(mrb.getRiskTier());
		r.setResult13(cri.getCR009());
		// 征信手机与申请人手机是否一致
		if (tCustomer.getMobilePhone().equals(cri.getCR009())) {
			r.setResult14(RiskCheckConstants.Y);
		} else if(StringUtil.isNotEmpty(cri.getCR009())) {
			r.setResult14(RiskCheckConstants.N);
		}
		// 人行最高信用额度（万）
		r.setResult15(MathUtils.div(modelIndexBean.getModelIndex18(),
				TEN_THOUSAND));
		// 年负债（万）
		// 前面已经除过1万了，这里不用除了
		r.setResult16(modelIndexBean.getModelIndex16());
		// 年负债/年营业收入
		r.setResult17(modelIndexBean.getModelIndex38());
		// 月负债（万）
		r.setResult18(MathUtils.div(modelIndexBean.getModelIndex17(),
				TEN_THOUSAND));
		// 月负债/月营业收入
		r.setResult19(modelIndexBean.getModelIndex39());
		// 未结清贷款余额（万）
		r.setResult20(MathUtils.div(cri.getCR052(), TEN_THOUSAND));
		// 未销户信用卡总授信额度（万）
		r.setResult21(MathUtils.div(cri.getCR057(), TEN_THOUSAND));
		// 信用卡已用额度（万）
		r.setResult22(MathUtils.div(cri.getCR060(), TEN_THOUSAND));
		// 信用卡额度使用率
		r.setResult23(modelIndexBean.getModelIndex14());
		r.setResult24(MathUtils.div(modelIndexBean.getModelIndex26(),
				TEN_THOUSAND));
		r.setResult25(modelIndexBean.getModelIndex28());
		r.setResult26(modelIndexBean.getModelIndex25());
		r.setResult27(modelIndexBean.getModelIndex30() == null ? null
				: modelIndexBean.getModelIndex30().toString());
		// 营业时间内的POS交易占比
		r.setResult28(modelIndexBean.getModelIndex31());
		// 申请人人行工作单位1
		r.setResult29(cri.getCR021());
		r.setResult30(cri.getCR022());
		r.setResult31(cri.getCR023());
		// 身份证归属地
		r.setResult32(tCustomer.getPaperId().substring(0, 6));
		// 属相
		String yearStr = tCustomer.getPaperId().substring(6, 10);
		int yearInt = Integer.parseInt(yearStr);
		int zodiac = yearInt % 12;
		r.setResult33(zodiac < 10 ? "0" + String.valueOf(zodiac) : String
				.valueOf(zodiac));
		// 近2个月负债
		// 前面已经除过1万了，这里不用除了
		r.setResult34(modelIndexBean.getModelIndex51());
		// 近半年负债
		// 前面已经除过1万了，这里不用除了
		r.setResult35(modelIndexBean.getModelIndex50());
		// POS月营业额
		r.setResult36(modelIndexBean.getModelIndex26());
		// 近期交易月份数
		r.setResult37(modelIndexBean.getModelIndex29());
		// 单笔最大金额
		r.setResult38(MathUtils.div(modelIndexBean.getModelIndex37(),
				TEN_THOUSAND));
		// 近半年征信查询次数
		r.setResult39(modelIndexBean.getModelIndex20());
		// 近半年新增信用贷款
		r.setResult40(modelIndexBean.getModelIndex48());
		// 近半年新增信用卡额度
		r.setResult41(modelIndexBean.getModelIndex49());
		// 近半年平均交易笔数
		r.setResult42(modelIndexBean.getModelIndex27());
		return r;
	}

}
