/**
 * 
 *	哈尔滨银行
 * Copyright (c) 2007-2015 HRBB,Inc.All Rights Reserved.
 */
package com.hrbb.loan.pos.biz.backstage.inter.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.hrbb.loan.pos.biz.backstage.inter.DataTransBiz;
import com.hrbb.loan.pos.dao.entity.TBankAccnoInfo;
import com.hrbb.loan.pos.dao.entity.TCreditApply;
import com.hrbb.loan.pos.dao.entity.TCustomer;
import com.hrbb.loan.pos.dao.entity.TPosCustInfo;
import com.hrbb.loan.pos.service.BankAccnoInfoService;
import com.hrbb.loan.pos.service.BusinessDictionaryService;
import com.hrbb.loan.pos.service.LoanPosCreditApplyService;
import com.hrbb.loan.pos.service.LoanPosCustomerService;
import com.hrbb.loan.pos.util.DateUtil;
import com.hrbb.loan.pos.util.IdUtil;
import com.hrbb.loan.pos.util.StringUtil;
import com.hrbb.loan.pos.util.constants.BusinessDictionaryConstants;
import com.hrbb.loan.pos.util.dataTrans.DataTransferUtil;
import com.hrbb.sh.framework.HResponse;
import com.hrbb.sh.frontier.biz.route.facade.RouteFacade;

/**
 * 小贷系统数据迁移
 * 
 * @author marco
 * @version $Id: LedgerBizImpl.java, v 0.1 2015-4-24 下午4:20:22 marco Exp $
 */
@Component("dataTransBiz")
public class DataTransBizImpl implements DataTransBiz {

	private Logger logger = LoggerFactory.getLogger(DataTransBizImpl.class);

	@Autowired
	@Qualifier("businessDictionaryService")
	private BusinessDictionaryService serviceBD;

	@Autowired
	private LoanPosCreditApplyService loanPosCreditApplyService;

	@Autowired
	private LoanPosCustomerService loanPosCustomerService;
	
	@Autowired
	private RouteFacade routeFacade;

	@Autowired
	private BankAccnoInfoService bankAccnoInfoService;

	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private final String BLANK = "";
	private final String COMMA = ",";
	private final String YEAR_NAME = "年";
	private final String YEAR_CODE = "Y";
	private final String MONTH_NAME = "月";
	private final String MONTH_CODE = "M";
	private final String DAY_NAME = "日";
	private final String DAY_CODE = "D";
	private final String INCHANNELKIND_04 = "04";
	private final String CURRSIGN_CNY = "CNY";
	private final String REPAYMETHOD_01 = "01";
	private final String RETURNKIND_90 = "90";
	private final String PAPERKIND_10 = "10";
	private final String OCCURTYPE_01 = "01";
	private final String LASTOPERID_SYSTEM = "system";
	private final String LASTOPERID_SYSTEM_NAME = "系统自动处理";

	private final String LOAN_PREFIX = "LO";
	private final String POSCUSTID_PREFIX = "MH";

	private final String NO = "NO";
	private final String CHANNEL = "channel";
	private final String APPLYAMT = "applyAmt";
	private final String BANKID = "bankId";
	private final String PRODID = "prodId";
	private final String PRODID_POS = "1001020306";
	private final String PRODID_POS_NAME = "POS贷";
	private final String PRODID_CASHFLOW = "1001020305";
	private final String PRODID_CASHFLOW_NAME = "流量贷";
	private final String PROD_CASHFLOW = "cashflow";
	private final String APPLYTERM = "applyTerm";
	private final String APPLYSTATUS = "applystatus";

	private final String BEGINDATE = "beginDate";
	private final String PAPERID = "paperId";
	private final String CUSTNAME = "custName";
	private final String SEXSIGN = "sexSign";
	private final String MARRSIGN = "marrSign";
	private final String EDUCSIGN = "educSign";
	private final String CHILDNUM = "childNum";
	private final String BUSIYEAR = "busiYear";
	private final String WEIXINNO = "weixinNo";
	private final String QQNO = "qqNo";
	private final String EMAIL = "email";
	private final String MOBILEPHONE = "mobilePhone";
	private final String TEL = "tel";
	private final String RESIDENTDETAIL = "residentDetail";
	private final String CONTACTADDRFLAG = "contactAddrFlag";
	private final String FAMILYCUSTNAME = "familyCustName";
	private final String MATEPAPERID = "matePaperId";
	private final String MATEMOBILEPHONE = "mateMobilephone";

	private final String POSCUSTID = "posCustId";
	private final String POSCUSTNAME = "posCustName";
	private final String POSCUSTBUSISCOPE = "posCustBusiScope";
	private final String POSCUSTADDRESS = "posCustAddress";
	private final String REGICODE = "regiCode";
	private final String REGISTDATE = "registDate";
	private final String REGCAPITAL = "regCapital";
	private final String STATE = "state";
	private final String CITY = "city";

	private final String BANKACCNO = "bankAccno";
	private final String BANKNAME = "bankName";

	private final String CODENO = "codeNo";
	private final String ITEMNO = "itemNo";
	private final String ITEMNOLIKE = "itemNoLike";
	private final String ITEMNOLIKE_PREFIX = "____";
	private final String ITEMNAME = "itemName";
	private final String STATUS = "status";
	private final String STATUS_ACTIVE = "0";

	private final BigDecimal TEN_THOUSAND = new BigDecimal(10000);

	/**
	 * 小贷系统数据迁移
	 * 
	 * @param args
	 */

	@Override
	public String DataTrans(String jsonStr) {
		logger.debug("小贷系统数据迁移开始。。。");
		// 格式化数据，并插入数据库
		String msg = insert(jsonStr);
		logger.debug("小贷系统数据迁移结束。");
		return msg;
	}

	/**
	 * 获取数据文件内容
	 * 
	 * @param filePath
	 * @return
	 */
	private String insert(String jsonStr) {

		JSONObject jsonObject = JSONObject.parseObject(jsonStr);
		JSONArray data = JSONArray.parseArray(jsonObject.getString("RECORDS"));
		int cnt = data.size();
		logger.debug("数据条数cnt=" + cnt);

		TCreditApply ca = null;
		TBankAccnoInfo bai = null;

		Map<String, Object> reqMap = Maps.newHashMap();

		Date now = new Date();
		logger.debug("now=" + sdf.format(now));
		// 插入成功条数
		int commitCnt = 0;

		StringBuilder errorMsg = new StringBuilder();

		for (int i = 0; i < cnt; ++i) {

			JSONObject jobj = data.getJSONObject(i);

			try {
				
				ca = new TCreditApply();
				ca.setLastOperTime(now);
				// 设置申请信息
				setCreditApply(jobj, ca);

				// 申请人信息
				// 身份证号码
				String paperId = jobj.getString(PAPERID);
				logger.debug("PAPERID=" + paperId);
				if (StringUtil.isNotEmpty(paperId)) {
					// 申请人姓名
					reqMap.put(PAPERID, paperId);
					List<TCustomer> cusList = loanPosCustomerService
							.getCustomerInfoSelective(reqMap);
					reqMap.clear();

					if (cusList == null || cusList.size() == 0) {
						logger.debug("非存量客户。");
						String custId = "";
						try{
							Map<String, Object> bodyMap = Maps.newHashMap();
							bodyMap.put("transcode", "RAP0001");
							bodyMap.put("sessionid", "");
							bodyMap.put("TransType", "RAP0001");
							bodyMap.put("isTransacted", "1");
							bodyMap.put("requestMsg", "登录");
							bodyMap.put("requestCode", "RAP0001");
							bodyMap.put("certCode", paperId);
							bodyMap.put("certType", "1");
							Map<String, Object> headerMap = Maps.newHashMap();
							HResponse response = routeFacade.routeService("RAP0001", headerMap, bodyMap);
							Map<String, Object> body = (Map<String, Object>)response.getProperties().get("body");
							custId = (String) body.get("custId");
							logger.info("用户系统返回的custId为:" + custId);
						}catch(Exception e){
							logger.error("调用户系统获取custId发生异常", e);
							custId = IdUtil.getCustId(paperId);
							
						}
						if(StringUtil.isEmpty(custId)){
							logger.error("用户系统返回的custId为空");
							custId = IdUtil.getCustId(paperId);
						}
						// 插入客户表
						insertCust(jobj, paperId, custId);

						ca.setCustId(custId);
						ca.setCustName(jobj.getString(CUSTNAME));

					} else {
						logger.debug("存量客户。");
						ca.setCustId(cusList.get(0).getCustId());
						ca.setCustName(cusList.get(0).getCustName());
					}
				}

				// 商户信息
				String posCustName = jobj.getString(POSCUSTNAME);
				logger.debug("POSCUSTNAME=" + posCustName);

				if (StringUtil.isNotEmpty(posCustName)) {

					reqMap.put(POSCUSTNAME, posCustName);

					List<Map<String, Object>> resList = loanPosCustomerService
							.getPosCustInfo(reqMap);
					reqMap.clear();

					// 非存量商户
					if (resList == null || resList.size() == 0) {
						logger.debug("非存量商户。");

						String posCustId = IdUtil.getId(POSCUSTID_PREFIX);
						logger.debug("posCustId=" + posCustId);
						// 插入商户表
						insertPosCust(jobj, posCustId, ca.getProdId(),
								ca.getCustId(), posCustName, ca.getRegion());

						ca.setPosCustId(posCustId);
					} else {
						logger.debug("存量商户。");

						ca.setPosCustId(resList.get(0).get(POSCUSTID)
								.toString());
						logger.debug("posCustId=" + ca.getPosCustId());
					}
					ca.setPosCustName(posCustName);
				}

				// 银行信息
				String bankAccno = jobj.getString(BANKACCNO);
				logger.debug("BANKACCNO=" + bankAccno);
				ca.setBankAccno(bankAccno);
				if (StringUtil.isNotEmpty(bankAccno)) {
					bai = bankAccnoInfoService.selectByPrimaryKey(bankAccno);
					// 非存量银行
					if (bai == null) {
						logger.debug("非存量银行。");
						// 插入银行卡表
						insertBankAccno(jobj, bankAccno, ca.getCustId(), now);
					} else {
						logger.debug("存量银行。");
					}
				}

				int result = loanPosCreditApplyService.insertCreditApply(ca);
				if (result == 1) {
					logger.debug("插入申请表成功。");
				} else {
					logger.debug("插入申请表失败！");
				}
				commitCnt++;
			} catch (Exception e) {
				logger.error("出现异常：", e);
				errorMsg.append("序号:[" + jobj.getString(NO) + "],")
						.append("商户名:[" + jobj.getString(POSCUSTNAME) + "],")
						.append("申请人姓名:[" + jobj.getString(CUSTNAME) + "],")
						.append("身份证号:[" + jobj.getString(PAPERID)
								+ "]的申请导入失败，请确认错误日志。<br/>");
			}
		}
		String msg = "总记录数cnt=" + cnt + "；成功插入数据条数commitCnt=" + commitCnt
				+ "<br/>";
		logger.debug(msg);
		if (StringUtil.isNotEmpty(errorMsg.toString())) {
			logger.error(errorMsg.toString());
			return msg.concat(errorMsg.toString());
		} else {
			return msg.concat("更新数据成功！");
		}
	}

	/**
	 * 设置申请信息
	 * 
	 * @param sqlSession
	 * @param jobj
	 * @param ca
	 */
	private void setCreditApply(JSONObject jobj, TCreditApply ca) {

		Map<String, Object> reqMap = Maps.newHashMap();

		ca.setLoanId(IdUtil.getId(LOAN_PREFIX));

		// 产品代码
		logger.debug("PRODID=" + jobj.getString(PRODID));
		// 流量贷
		if (PROD_CASHFLOW.equals(jobj.getString(PRODID))) {
			ca.setProdId(PRODID_CASHFLOW);
			ca.setProdName(PRODID_CASHFLOW_NAME);
			// POS贷
		} else {
			ca.setProdId(PRODID_POS);
			ca.setProdName(PRODID_POS_NAME);
		}

		ca.setCurrSign(CURRSIGN_CNY);

		// 申请金额
		String applyAmt = jobj.getString(APPLYAMT);
		logger.debug("APPLYAMT=" + jobj.getString(APPLYAMT));
		if (StringUtil.isEmpty(applyAmt)) {
			ca.setApplyAmt(BigDecimal.ZERO);
		} else {
			ca.setApplyAmt(new BigDecimal(applyAmt.replaceAll(COMMA, BLANK)));
		}

		// 申请期限
		String applyTerm = jobj.getString(APPLYTERM);
		logger.debug("APPLYTERM=" + applyTerm);
		if (StringUtil.isNotEmpty(applyTerm)) {
			if (applyTerm.indexOf(YEAR_NAME) >= 0) {
				ca.setTermUnit(YEAR_CODE);
				ca.setApplyTerm(applyTerm.replaceAll(YEAR_NAME, BLANK));
			} else if (applyTerm.indexOf(MONTH_NAME) >= 0) {
				ca.setTermUnit(MONTH_CODE);
				ca.setApplyTerm(applyTerm.replaceAll(MONTH_NAME, BLANK));
			} else if (applyTerm.indexOf(DAY_NAME) >= 0) {
				ca.setTermUnit(DAY_CODE);
				ca.setApplyTerm(applyTerm.replaceAll(DAY_NAME, BLANK));
			} else {
				ca.setTermUnit(MONTH_CODE);
				ca.setApplyTerm(applyTerm);
			}
		}

		// 申请日期
		String beginDate = jobj.getString(BEGINDATE);
		logger.debug("beginDate=" + beginDate);
		if (StringUtil.isNotEmpty(beginDate)) {
			try {
				ca.setBeginDate(DateUtil.getDatePattern9(beginDate));
			} catch (Exception e) {
				logger.error("申请日期格式不对！", e);
			}
		}

		ca.setReturnKind(RETURNKIND_90);
		ca.setBankId(jobj.getString(BANKID));
		logger.debug("BANKID=" + ca.getBankId());
		ca.setOperId(LASTOPERID_SYSTEM);
		ca.setApplyStatus(jobj.getString(APPLYSTATUS));
		logger.debug("APPLYSTATUS=" + ca.getApplyStatus());
		ca.setLastOperId(LASTOPERID_SYSTEM);
		ca.setDeleteFlag("0");

		// 区划国标码
		ca.setRegion(getRegion(jobj));

		ca.setOperName(LASTOPERID_SYSTEM_NAME);

		// 业务来源
		String channel = jobj.getString(CHANNEL);
		logger.debug("channelName=" + channel);
		reqMap.put(CODENO, BusinessDictionaryConstants.BizChannel);
		reqMap.put(ITEMNAME, channel);
		reqMap.put(STATUS, STATUS_ACTIVE);
		List<Map<String, Object>> resListChannel = serviceBD
				.getBusinessCode(reqMap);
		reqMap.clear();
		if (resListChannel != null && resListChannel.size() > 0) {
			channel = resListChannel.get(0).get(ITEMNO).toString();
			logger.debug("channelCode=" + channel);
			ca.setChannel(channel);
		}

		ca.setInChannelKind(INCHANNELKIND_04);
		ca.setOccurType(OCCURTYPE_01);
		ca.setRegDate(ca.getLastOperTime());
		ca.setRepayMethod(REPAYMETHOD_01);
	}

	/**
	 * 插入客户表
	 * 
	 * @param sqlSession
	 * @param jobj
	 * @param paperId
	 * @param custId
	 * @return
	 */
	private int insertCust(JSONObject jobj, String paperId, String custId) {
		TCustomer c = new TCustomer();

		c.setCustId(custId);
		c.setCustName(jobj.getString(CUSTNAME));
		c.setPaperKind(PAPERKIND_10);
		c.setPaperId(paperId);

		// 出生日期
		if (paperId.length() < 18) {
			logger.debug("申请人的身份证号码小于18位！");
		} else {
			try {
				String birtDateStr = paperId.substring(6, 14);
				logger.debug("birtDateStr=" + birtDateStr);
				c.setBirtDate(DateUtil.getStrToDate1(birtDateStr));
			} catch (ParseException e) {
				logger.debug("申请人的身份证中的出生日期不是日期！");
			}
		}

		// 性别
		String sexSign = jobj.getString(SEXSIGN);
		logger.debug("SEXSIGN=" + sexSign);
		c.setSexSign(DataTransferUtil.getSexSign(sexSign));

		// 婚姻状况:0;未婚；1，已婚
		String marrSign = jobj.getString(MARRSIGN);
		logger.debug("MARRSIGN=" + marrSign);
		c.setMarrSign(DataTransferUtil.getMarrSign(marrSign));

		// 最高学历
		String educSign = jobj.getString(EDUCSIGN);
		logger.debug("EDUCSIGN=" + educSign);
		c.setEducSign(DataTransferUtil.getEducSign(educSign));

		// 子女人数
		String childNum = jobj.getString(CHILDNUM);
		logger.debug("CHILDNUM=" + childNum);
		c.setChildNum(childNum);

		// 居住地址-具体
		String residentDetail = jobj.getString(RESIDENTDETAIL);
		logger.debug("RESIDENTDETAIL=" + residentDetail);
		if (StringUtil.isNotEmpty(residentDetail)) {
			// 截取省市，转化编码
			c.setResidentDetail(residentDetail);
		}

		// 联系地址
		String contactAddrFlag = jobj.getString(CONTACTADDRFLAG);
		logger.debug("CONTACTADDRFLAG=" + contactAddrFlag);
		c.setContactAddrFlag(contactAddrFlag);

		// 手机号码
		String mobilePhone = jobj.getString(MOBILEPHONE);
		logger.debug("MOBILEPHONE=" + mobilePhone);
		if (mobilePhone == null) {
			c.setMobilePhone("");
		} else {
			c.setMobilePhone(mobilePhone);
		}
		// 固定电话
		String tel = jobj.getString(TEL);
		logger.debug("TEL=" + tel);
		c.setTel(tel);
		// 微信号
		String weixinNo = jobj.getString(WEIXINNO);
		logger.debug("WEIXINNO=" + weixinNo);
		c.setWeixinNo(weixinNo);
		// QQ号
		String qqNo = jobj.getString(QQNO);
		logger.debug("QQNO=" + qqNo);
		c.setQqNo(qqNo);
		// 电子邮件
		String email = jobj.getString(EMAIL);
		logger.debug("EMAIL=" + email);
		c.setEmail(email);

		// 从业年限
		String busiYear = jobj.getString(BUSIYEAR);
		logger.debug("BUSIYEAR=" + busiYear);
		c.setBusiYear(busiYear);

		// 配偶姓名
		String familyCustName = jobj.getString(FAMILYCUSTNAME);
		logger.debug("FAMILYCUSTNAME=" + familyCustName);
		c.setFamilyCustName(familyCustName);

		// 配偶证件类型
		c.setMatePaperKind(PAPERKIND_10);

		// 配偶证件号码
		String matePaperId = jobj.getString(MATEPAPERID);
		logger.debug("MATEPAPERID=" + matePaperId);

		if (StringUtil.isNotEmpty(matePaperId)) {
			c.setMatePaperId(matePaperId);

			// 出生日期
			if (matePaperId.length() < 18) {
				logger.debug("配偶的身份证号码小于18位！");
			} else {
				try {
					String mateBirtDateStr = matePaperId.substring(6, 14);
					logger.debug("mateBirtDateStr=" + mateBirtDateStr);
					c.setMateBirtDate(DateUtil.getStrToDate1(mateBirtDateStr));
				} catch (ParseException e) {
					logger.debug("配偶的身份证中的出生日期不是日期！");
				}

				// 性别
				String mateSexSign = matePaperId.substring(16, 17);
				logger.debug("mateSexSign(判断奇偶)=" + mateSexSign);
				c.setMateSexSign(DataTransferUtil.getSexSignByID(mateSexSign));
			}
		}

		// 配偶手机
		String mateMobilephone = jobj.getString(MATEMOBILEPHONE);
		logger.debug("MATEMOBILEPHONE=" + mateMobilephone);
		c.setMateMobilephone(mateMobilephone);
		int result = loanPosCustomerService.insertTCustermerInfo(c);
		if (result == 1) {
			logger.debug("插入客户表成功。");
		} else {
			logger.debug("插入客户表失败！");
		}

		return result;
	}

	/**
	 * 插入商户表
	 * 
	 * @param sqlSession
	 * @param jobj
	 * @param posCustId
	 * @param prodId
	 * @param custId
	 * @return
	 */
	private int insertPosCust(JSONObject jobj, String posCustId, String prodId,
			String custId, String posCustName, String region) {

		TPosCustInfo pci = new TPosCustInfo();
		pci.setPosCustId(posCustId);
		pci.setCustId(custId);
		pci.setPosCustName(posCustName);

		// 主营业务
		pci.setPosCustBusiScope(jobj.getString(POSCUSTBUSISCOPE));
		logger.debug("POSCUSTBUSISCOPE=" + jobj.getString(POSCUSTBUSISCOPE));

		// 互金行业分类(内部)
		pci.setIndustryTypeId2(DataTransferUtil.getIndustryType(posCustName));
		logger.debug("IndustryTypeId2=" + pci.getIndustryTypeId2());

		// 实际经营地址详细
		pci.setPosCustAddress(jobj.getString(POSCUSTADDRESS));
		logger.debug("POSCUSTADDRESS=" + jobj.getString(POSCUSTADDRESS));

		// 区划国标码
		pci.setOperAddrCode(region);

		// 营业执照编号
		pci.setRegiCode(jobj.getString(REGICODE));
		logger.debug("REGICODE=" + jobj.getString(REGICODE));

		// 注册资本
		String regCapitalStr = jobj.getString(REGCAPITAL);
		logger.debug("regCapitalStr=" + regCapitalStr);
		if (StringUtil.isNotEmpty(regCapitalStr)) {
			try {
				BigDecimal regCapital = new BigDecimal(regCapitalStr);
				pci.setRegCapital(regCapital.multiply(TEN_THOUSAND));
			} catch (NumberFormatException e) {
				logger.debug("注册资本转化失败！");
			}
		}

		// 注册日期
		pci.setRegistDate(jobj.getString(REGISTDATE));
		logger.debug("RegistDate=" + pci.getRegistDate());

		// 是否安装POS机 0未安装 1安装
		// 流量贷
		if (PRODID_CASHFLOW.equals(prodId)) {
			pci.setIsPosInstall("0");
			// POS贷
		} else {
			pci.setIsPosInstall("1");
		}
		int result = loanPosCustomerService.insertSelective(pci);
		if (result == 1) {
			logger.debug("插入商户表成功。");
		} else {
			logger.debug("插入商户表失败！");
		}
		return result;
	}

	/**
	 * 插入银行卡表
	 * 
	 * @param sqlSession
	 * @param jobj
	 * @param bankAccno
	 * @param custId
	 * @param now
	 * @return
	 */
	private int insertBankAccno(JSONObject jobj, String bankAccno,
			String custId, Date now) {
		TBankAccnoInfo bai = new TBankAccnoInfo();
		bai.setBankAccno(bankAccno);
		bai.setCustId(custId);
		String bankName = jobj.getString(BANKNAME);
		logger.debug("BANKNAME=" + bankName);
		bai.setBankBranName(bankName);
		bai.setCreateTime(now);
		int result = bankAccnoInfoService.insertSelective(bai);
		if (result == 1) {
			logger.debug("插入银行卡表成功。");
		} else {
			logger.debug("插入银行卡表失败！");
		}
		return result;
	}

	/**
	 * 取得省市编码
	 * 
	 * @param sqlSession
	 * @param jobj
	 * @return
	 */
	private String getRegion(JSONObject jobj) {

		// 区划国标码-省
		String state = jobj.getString(STATE);
		logger.debug("state=" + state);

		Map<String, Object> reqMap = Maps.newHashMap();

		String region = "";

		if (StringUtil.isNotEmpty(state)) {
			reqMap.put(CODENO, BusinessDictionaryConstants.AdminDivision);
			reqMap.put(ITEMNAME, state);
			reqMap.put(STATUS, STATUS_ACTIVE);
			List<Map<String, Object>> resListDB = serviceBD
					.getBusinessCode(reqMap);
			reqMap.clear();
			// 省
			if (resListDB != null && resListDB.size() > 0) {
				region = resListDB.get(0).get(ITEMNO).toString();
				logger.debug("省region=" + region);
				// 市
				String city = jobj.getString(CITY);
				logger.debug("city=" + city);

				if (StringUtil.isNotEmpty(city)) {
					reqMap.put(CODENO,
							BusinessDictionaryConstants.AdminDivision);
					reqMap.put(ITEMNOLIKE, region.substring(0, 2)
							+ ITEMNOLIKE_PREFIX);
					reqMap.put(ITEMNAME, city);
					List<Map<String, Object>> resListDB2 = serviceBD
							.getBusinessCode(reqMap);
					reqMap.clear();
					if (resListDB2 != null && resListDB2.size() > 0) {
						region = resListDB2.get(0).get(ITEMNO).toString();
						logger.debug("市region=" + region);
					} else {
						logger.debug("没有市region");
					}
				}
			} else {
				logger.debug("没有省region");
			}
		}
		return region;
	}
}
