package com.hrbb.loan.pos.biz.backstage.inter.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.hrbb.loan.pos.aicutil.RSAEncryption;
import com.hrbb.loan.pos.aicutil.RandomTools;
import com.hrbb.loan.pos.biz.backstage.inter.IPoliceAndAICConnectBiz;
import com.hrbb.loan.pos.dao.TBankAccnoInfoDao;
import com.hrbb.loan.pos.dao.entity.TAICAlterInfo;
import com.hrbb.loan.pos.dao.entity.TAICLiguidationInfo;
import com.hrbb.loan.pos.dao.entity.TAICMordetailInfo;
import com.hrbb.loan.pos.dao.entity.TAICMorguaInfo;
import com.hrbb.loan.pos.dao.entity.TAICSharesFrostInfo;
import com.hrbb.loan.pos.dao.entity.TAICSharesimpawnInfo;
import com.hrbb.loan.pos.dao.entity.TBankAccnoInfo;
import com.hrbb.loan.pos.service.LoanPosCreditApplyService;
import com.hrbb.loan.pos.service.LoanPosCustomerService;
import com.hrbb.loan.pos.service.PoliceAndAICConnectService;
import com.hrbb.loan.pos.util.Dom4jUtil;
import com.hrbb.loan.pos.util.IdUtil;
import com.hrbb.loan.pos.util.ListUtil;
import com.hrbb.loan.pos.util.StringUtil;
import com.hrbb.sh.framework.util.BankUtil;
import com.longcredit.common.Encryption.Authcode;

@Component("policeAndAICConnectBiz")
public class PoliceAndAICConnectBizImpl implements IPoliceAndAICConnectBiz {

	Logger logger = LoggerFactory.getLogger(PoliceAndAICConnectBizImpl.class);

	@Autowired
	private PoliceAndAICConnectService policeAndAICConnectService;

	@Autowired
	private LoanPosCreditApplyService loanPosCreditApplyService;

	@Autowired
	private LoanPosCustomerService loanPosCustomerService;
	
	@Autowired
	private TBankAccnoInfoDao tBankAccnoInfoDao;

	Properties p = new Properties();

	@PostConstruct
	public void initProperties() {
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream("connectPoliceAndAIC.properties");
		try {
			p.load(inputStream);
		} catch (IOException e1) {
			logger.error("读取 connectPoliceAndAIC.properties异常");
		}
	}

	private String getText(String str, String startTag, String endTag) {
		try {
			return str.substring(str.indexOf(startTag), str.indexOf(endTag))
					.replace(startTag, "");

		} catch (Exception e) {
			logger.error("解析公安信息异常:", e);
			return "";
		}
	}

	/**
	 * 根据公安返回报文入库
	 */
	@Override
	public Map<String, Object> getPoliceInfo(String reqXml, String custId) {
		Map<String, Object> respMap = Maps.newHashMap();
		String inputString = getText(reqXml, "<INPUT>", "</INPUT>");
		String outputString = getText(reqXml, "<OUTPUT>", "</OUTPUT>");
		String custName = getText(inputString, "<xm>", "</xm>");
		String idNo = getText(inputString, "<gmsfhm>", "</gmsfhm>");
		try {
			if (reqXml.contains("errormesage")) {
				respMap.put("returnCode", "01");
				respMap.put("returnMsg", "查询失败");
				logger.error("查询失败信息为:"
						+ getText(reqXml, "<errormesage>", "</errormesage>"));
				if(getText(reqXml, "<errormesage>", "</errormesage>").contains("没有符合条件的记录")){
					logger.debug("没有符合条件的记录, idNo=" + idNo +"name=" + custName);
					Map<String, Object> reqMap = Maps.newHashMap();
					reqMap.put("idNo", idNo);
					reqMap.put("custName", custName);
					Map<String, Object> insertMap = Maps.newHashMap();
					Date date = new Date();
					if(ListUtil.isNotEmpty(policeAndAICConnectService.queryPoliceInfoList(reqMap))){
						insertMap.put("result", "没有符合条件的记录");
						insertMap.put("updateTime", date);
						insertMap.put("queryTime", date);
						policeAndAICConnectService.updatePoliceInfo(insertMap);
					}else{
						insertMap.put("result", "没有符合条件的记录");
						insertMap.put("createTime", date);
						insertMap.put("queryTime", date);
						insertMap.put("updateTime", date);
						policeAndAICConnectService.insertPoliceInfo(insertMap);
						
					}
					
				}
				return respMap;
			}

			if (reqXml.contains("ErrorCode")) {
				respMap.put("returnCode", "01");
				respMap.put("returnMsg", "查询失败");
				logger.error("查询失败, errormesage:[], errorCode:[]",
						getText(reqXml, "<ErrorMsg>", "</ErrorMsg>"),
						getText(reqXml, "<ErrorCode>", "</ErrorCode>"));
				return respMap;
			}
			Map<String, Object> insertMap = Maps.newHashMap();
			
			insertMap.put("custName", custName);
			insertMap
					.put("idNo", idNo);
			insertMap.put("photo", getText(outputString, "<result_xp>", "</result_xp>")
					.replaceAll("\u0020", "").replaceAll("\r\n", ""));
			
			String policeCustName = getText(outputString, "<result_xm>", "</result_xm>");
			insertMap.put("policeCustName",
					policeCustName);
			insertMap.put("servPlace",
					getText(outputString, "<result_fwcs>", "</result_fwcs>"));
			insertMap.put("address", getText(outputString, "<result_zz>", "</result_zz>"));
			insertMap.put("mariSign",
					getText(outputString, "<result_hyzk>", "</result_hyzk>"));
			insertMap
					.put("eduSign", getText(outputString, "<result_whcd>", "</result_whcd>"));
			insertMap.put("birthPlace",
					getText(outputString, "<result_csdssx>", "</result_csdssx>"));
			insertMap.put("birthPlace",
					getText(outputString, "<result_csdssx>", "</result_csdssx>"));
			insertMap.put("nativePlace",
					getText(outputString, "<result_jgssx>", "</result_jgssx>"));
			insertMap.put("territorial", getText(outputString, "<result_ssssxq>", "</result_ssssxq>"));
			insertMap.put("birthDate",
					getText(outputString, "<result_csrq>", "</result_csrq>"));
			insertMap.put("nation", getText(outputString, "<result_mz>", "</result_mz>"));
			insertMap.put("sexSign", getText(outputString, "<result_xb>", "</result_xb>"));
			String policeIdNo = getText(outputString, "<result_gmsfhm>", "</result_gmsfhm>");
			insertMap.put("policeIdNo",
					policeIdNo);
			insertMap.put("result", "查询结果一致");
			//先查询表中是否有该用户的公安信息，如果有公安信息，则更新数据
			Map<String, Object> reqMap = Maps.newHashMap();
			reqMap.put("idNo", idNo);
			reqMap.put("custName", custName);
			if(ListUtil.isNotEmpty(policeAndAICConnectService.queryPoliceInfoList(reqMap))){
				insertMap.put("updateTime", new Date());
				insertMap.put("queryTime", new Date());
				policeAndAICConnectService.updatePoliceInfo(insertMap);
			}else{
				
				insertMap.put("createTime", new Date());
				insertMap.put("queryTime", new Date());
				policeAndAICConnectService.insertPoliceInfo(insertMap);
				
			}
			// 更新用户表
			Map<String, Object> custMap = Maps.newHashMap();
			custMap.put("custName", policeCustName);
			custMap.put("paperId",
					policeIdNo);
			custMap.put("custId", custId);
			loanPosCustomerService.updateCustomerInfoMap(custMap);
			respMap.put("returnCode", "00");
			respMap.put("returnMsg", "查询成功");
			return respMap;

		} catch (Exception e) {
			logger.error("getPoliceInfo error:[]", e.getMessage());
			respMap.put("returnCode", "01");
			respMap.put("returnMsg", "查询异常");
			return respMap;
		}
	}

	/**
	 * 获取发送报文
	 */
	@Override
	public Map<String, String> getPolicReqXmp(String idNo, String name) {
		String SBM = p.getProperty("SBM");
		String con = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
				+ "<ROWS><INFO><SBM>" + SBM + "</SBM></INFO>"
				+ "<ROW><GMSFHM>公民身份号码</GMSFHM><XM>姓名</XM></ROW>"
				+ "<ROW FSD=\"310000\" YWLX=\"查询业务\" ><GMSFHM>" + idNo
				+ "</GMSFHM><XM>" + name + "</XM></ROW>" + "</ROWS>";
		String license = p.getProperty("license");
		String url = p.getProperty("url");
		Map<String, String> map = Maps.newHashMap();
		map.put("license", license);
		map.put("reqXml", con);
		map.put("url", url);
		return map;
	}

	@Override
	public boolean hasPoliceInfo(String idNo, String name) {
		Map<String, Object> map = policeAndAICConnectService
				.queryOnePoliceInfo(name, idNo, null);
		return !map.isEmpty();
	}

	@Override
	public boolean hasAICInfo(String posCustName) {
		Map<String, Object> map = policeAndAICConnectService
				.queryOneAICOrderlist(posCustName);
		return !map.isEmpty();
	}

	@Override
	public Map<String, String> getAICReqXml(Map<String, Object> reqMap) throws Exception{
		List<Map<String, Object>> resMapList = loanPosCustomerService.getPosCustInfo(reqMap);
		String regCode = null;
		if(!resMapList.isEmpty()){
			regCode = (String) resMapList.get(0).get("regiCode");
		}
		String userId = p.getProperty("uid");
		String upwd = p.getProperty("upwd");
		String aicUrl = p.getProperty("aicUrl");
		String posCustName = (String) reqMap.get("posCustName");
		// String posCustId = (String) reqMap.get("posCustId");
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		stringBuffer.append("<DATA>");
		stringBuffer.append("<ORDER>");
		stringBuffer.append("<UID>");
		stringBuffer.append(userId);
		stringBuffer.append("</UID>");
		if(StringUtil.isEmpty(regCode)){
			stringBuffer.append("<KEY>");
			stringBuffer.append(posCustName);
			stringBuffer.append("</KEY>");
			stringBuffer.append("<KEYTYPE>");
			stringBuffer.append("2");
			stringBuffer.append("</KEYTYPE>");
			
		}else{
			stringBuffer.append("<KEY>");
			stringBuffer.append(regCode);
			stringBuffer.append("</KEY>");
			stringBuffer.append("<KEYTYPE>");
			stringBuffer.append("3");
			stringBuffer.append("</KEYTYPE>");

		}
		stringBuffer.append("<ORDERTYPE>");
		stringBuffer.append("11010001110000110000000000010000100");
		stringBuffer.append("</ORDERTYPE>");
		stringBuffer.append("<PASSWORD>");
		stringBuffer.append(upwd);
		stringBuffer.append("</PASSWORD>");
		stringBuffer.append("</ORDER>");
		stringBuffer.append("</DATA>");
		logger.debug("工商发送报文为:" + stringBuffer.toString());
		Authcode authcode = new Authcode();
		// des加密
		// 生成DES加密秘钥
		String xmlkey = RandomTools.GenerateRandom();
		String desStr = null;
		// System.out.println(xmlkey);
		desStr = authcode.AuthcodeEncode(
				URLEncoder.encode(stringBuffer.toString(), "UTF-8"), xmlkey);
		String rsaStr = RSAEncryption.PublicEncrypt(xmlkey);
		StringBuffer reqStringBuffer = new StringBuffer();
		reqStringBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		reqStringBuffer.append("<DATA>");
		reqStringBuffer.append("<KEY>");
		reqStringBuffer.append(rsaStr);
		reqStringBuffer.append("</KEY>");
		reqStringBuffer.append("<VALUE>");
		reqStringBuffer.append(desStr);
		reqStringBuffer.append("</VALUE>");
		reqStringBuffer.append("</DATA>");
		String reqXml = reqStringBuffer.toString();
		// 对请求报文做utf8编码
		Map<String, String> respMap = Maps.newHashMap();
		respMap.put("url", aicUrl);
		respMap.put("reqXml", reqXml);
		return respMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getAICInfo(Map<String, String> reqMap)
			throws Exception {
		// 标识位， 0代表正常, 1代表异常
		String queryTime = reqMap.get("queryTime");
		int flag = 0;
		String posCustId = reqMap.get("posCustId");
		String posCustName = reqMap.get("posCustName");
		String resXmlBefore = reqMap.get("resXml");
		String keyString = resXmlBefore
				.substring(resXmlBefore.indexOf("<KEY>"),
						resXmlBefore.indexOf("</KEY>")).replaceAll("<KEY>", "")
				.trim();
		String valueString = resXmlBefore
				.substring(resXmlBefore.indexOf("<VALUE>"),
						resXmlBefore.indexOf("/VALUE"))
				.replaceAll("<VALUE>", "").trim();
		// RSA解密
		String desDecryptKey = RSAEncryption.PublicDecrypt(keyString);
		logger.debug("RSA解密后:" + desDecryptKey);
		// des解密
		String resXml = new Authcode().AuthcodeDecode(valueString,
				desDecryptKey);
		resXml = URLDecoder.decode(resXml, "UTF-8");
		logger.debug("des解密后:" + resXml);
		if (StringUtil.isEmpty(resXml)) {
			Map<String, String> resMap = Maps.newHashMap();
			resMap.put("resCode", "01");
			resMap.put("resMsg", "报文为空");
			return resMap;
		} else {
			// 异常情况
			if (resXml.indexOf("ERRORCODE") >= 0) {
				logger.error("工商返回错误.resCode:"
						+ resXml.substring(resXml.indexOf("<ERRORCODE>"),
								resXml.indexOf("</ERRORCODE>")).replace(
								"<ERRORCODE>", "")
						+ "resMsg:"
						+ resXml.substring(resXml.indexOf("<ERRORMSG>"),
								resXml.indexOf("</ERRORMSG>")).replace(
								"<ERRORMSG>", ""));
				Map<String, String> resMap = Maps.newHashMap();
				resMap.put("resCode", "01");
				resMap.put(
						"resMsg",
						resXml.substring(resXml.indexOf("<ERRORMSG>"),
								resXml.indexOf("</ERRORMSG>")).replace(
								"<ERRORMSG>", ""));
				return resMap;
			}
			Document document = Dom4jUtil.getDocumentFromText(resXml);
			Element root = Dom4jUtil.getRootElement(document);
			String orderNo = "";
			if (root.element("ORDERLIST") != null
					&& root.element("ORDERLIST").element("ITEM") != null) {
				Element orderListEle = root.element("ORDERLIST")
						.element("ITEM");
				// orderList信息入库
				Map<String, Object> orderListMap = Maps.newHashMap();
				orderNo = orderListEle.element("ORDERNO").getText();
				orderListMap.put("queryUid", orderListEle.element("UID")
						.getText());
				orderListMap.put("orderNo", orderNo);
				orderListMap.put("posCustId", posCustId);
				orderListMap.put("posCustName", posCustName);
				orderListMap.put("keyName", orderListEle.elementText("KEY"));
				orderListMap.put("keyType", orderListEle.element("KEYTYPE")
						.getText());
				orderListMap.put("status", orderListEle.element("STATUS")
						.getText());
				orderListMap.put("finishTime",
						orderListEle.elementText("FINISHTIME"));
				orderListMap.put("queryTime", queryTime);
				policeAndAICConnectService.insertAICOrderlistInfo(orderListMap);
			}
			// 企业照面信息
			if (root.element("BASIC") != null
					&& root.element("BASIC").element("ITEM") != null) {
				Element basicEle = root.element("BASIC").element("ITEM");
				Map<String, Object> basicMap = Maps.newHashMap();
				basicMap.put("posCustId", posCustId);
				basicMap.put("orderNo", orderNo);
				basicMap.put("entName", basicEle.elementText("ENTNAME"));
				basicMap.put("frName", basicEle.elementText("FRNAME"));
				basicMap.put("regNo", basicEle.elementText("REGNO"));
				basicMap.put("oriRegNo", basicEle.elementText("ORIREGNO"));
				basicMap.put("orgCodes", basicEle.elementText("ORGCODES"));
				basicMap.put("regCap", basicEle.elementText("REGCAP"));
				basicMap.put("recCap", basicEle.elementText("RECCAP"));
				basicMap.put("regCapCur", basicEle.elementText("REGCAPCUR"));
				basicMap.put("entStatus", basicEle.elementText("ENTSTATUS"));
				basicMap.put("entType", basicEle.elementText("ENTTYPE"));
				basicMap.put("esDate", basicEle.elementText("ESDATE"));
				basicMap.put("opFrom", basicEle.elementText("OPFROM"));
				basicMap.put("opTo", basicEle.elementText("OPTO"));
				basicMap.put("addr", basicEle.elementText("DOM"));
				basicMap.put("regOrg", basicEle.elementText("REGORG"));
				basicMap.put("abuiTem", basicEle.elementText("ABUITEM"));
				basicMap.put("cbuiTem", basicEle.elementText("CBUITEM"));
				basicMap.put("opScope", basicEle.elementText("OPSCOPE"));
				basicMap.put("opScoAndForm",
						basicEle.elementText("OPSCOANDFORM"));
				basicMap.put("anCheYear", basicEle.elementText("ANCHEYEAR"));
				basicMap.put("canDate", basicEle.elementText("CANDATE"));
				basicMap.put("revDate", basicEle.elementText("REVDATE"));
				basicMap.put("anCheDate", basicEle.elementText("ANCHEDATE"));
				basicMap.put("industryPhyCode",
						basicEle.elementText("INDUSTRYPHYCODE"));
				basicMap.put("industryCoCode",
						basicEle.elementText("INDUSTRYCOCODE"));
				basicMap.put("cdId", basicEle.elementText("CDID"));
				logger.debug(posCustName + "开始更新商户照面信息");
				policeAndAICConnectService.insertAICBasicInfo(basicMap);
				logger.debug(posCustName + "更新商户照面信息完成");
				logger.debug(posCustName + "更新商户信息");
				Map<String, Object> posCustUpMap = Maps.newHashMap();
				logger.debug("商户号为:" + posCustId);
				posCustUpMap.put("posCustId", posCustId);
				posCustUpMap.put("legalPersonName",
						basicEle.elementText("FRNAME"));
				posCustUpMap.put("regCapital",
						new BigDecimal(
								basicEle.elementText("REGCAP") == null ? "0"
										: basicEle.elementText("REGCAP"))
								.multiply(new BigDecimal("10000")).toString());
				posCustUpMap
						.put("industryTypeId",
								(basicEle.elementText("INDUSTRYPHYCODE") == null ? ""
										: basicEle
												.elementText("INDUSTRYPHYCODE"))
										+ (basicEle
												.elementText("INDUSTRYCOCODE") == null ? ""
												: basicEle
														.elementText("INDUSTRYCOCODE")));
				posCustUpMap.put("registDate", basicEle.elementText("ESDATE"));
				loanPosCreditApplyService
						.updatePosCustByPrimaryKeySelectiveMap(posCustUpMap);
				logger.debug(posCustName + "更新商户信息结束");

			}
			if (root.element("SHAREHOLDER") != null
					&& root.element("SHAREHOLDER").elements("ITEM") != null) {
				// 企业股东
				List<Element> shareHolderEle = root.element("SHAREHOLDER")
						.elements("ITEM");
				for (Element element : shareHolderEle) {

					Map<String, Object> shareHolderMap = Maps.newHashMap();
					shareHolderMap.put("posCustId", posCustId);
					shareHolderMap.put("orderNo", orderNo);
					shareHolderMap.put("shaName",
							element.elementText("SHANAME"));
					shareHolderMap.put("subConAm",
							element.elementText("SUBCONAM"));
					shareHolderMap.put("regCapCur",
							element.elementText("REGCAPCUR"));
					shareHolderMap.put("funDedRatio",
							element.elementText("FUNDEDRATIO"));
					shareHolderMap.put("conDate",
							element.elementText("CONDATE"));
					shareHolderMap.put("cdId", element.elementText("CDID"));
					policeAndAICConnectService
							.insertAICShareHolder(shareHolderMap);

				}
			}
			// 企业主要管理人员信息
			if(root.element("PERSON") != null && root.element("PERSON").elements("ITEM") != null){
				List<Element> personEle = root.element("PERSON").elements("ITEM");
				for (Element element : personEle) {
					
					Map<String, Object> personMap = Maps.newHashMap();
					personMap.put("posCustId", posCustId);
					personMap.put("orderNo", orderNo);
					personMap.put("perName", element.elementText("PERNAME"));
					personMap.put("position", element.elementText("POSITION"));
					personMap.put("cdId", element.elementText("CDID"));
					policeAndAICConnectService.insertAICPersonInfo(personMap);
					
				}
			}

			// 企业法定代表人对外投资信息
			if (root.element("FRINV") != null
					&& root.element("FRINV").elements("ITEM") != null) {
				List<Element> frinvEle = root.element("FRINV").elements("ITEM");
				for (Element element : frinvEle) {

					Map<String, Object> frinvMap = Maps.newHashMap();
					frinvMap.put("posCustId", posCustId);
					frinvMap.put("orderNo", orderNo);
					frinvMap.put("name", element.elementText("NAME"));
					frinvMap.put("entName", element.elementText("ENTNAME"));
					frinvMap.put("regNo", element.elementText("REGNO"));
					frinvMap.put("entType", element.elementText("ENTTYPE"));
					frinvMap.put("regCap", element.elementText("REGCAP"));
					frinvMap.put("regCapCur", element.elementText("REGCAPCUR"));
					frinvMap.put("entStatus", element.elementText("ENTSTATUS"));
					frinvMap.put("canDate", element.elementText("CANDATE"));
					frinvMap.put("revDate", element.elementText("REVDATE"));
					frinvMap.put("regOrg", element.elementText("REGORG"));
					frinvMap.put("subConAm", element.elementText("SUBCONAM"));
					frinvMap.put("currency", element.elementText("CURRENCY"));
					frinvMap.put("funDedRatio",
							element.elementText("FUNDEDRATIO"));
					frinvMap.put("esDate", element.elementText("ESDATE"));
					policeAndAICConnectService.insertAICFrinvInfo(frinvMap);

				}

			}

			if (root.element("FRPOSITION") != null
					&& root.element("FRPOSITION").elements("ITEM") != null) {
				// 企业法定代表人其他公司任职信息

				List<Element> frpositionEle = root.element("FRPOSITION")
						.elements("ITEM");
				for (Element element : frpositionEle) {

					Map<String, Object> frpositionMap = Maps.newHashMap();
					frpositionMap.put("posCustId", posCustId);
					frpositionMap.put("orderNo", orderNo);
					frpositionMap.put("name", element.elementText("NAME"));
					frpositionMap
							.put("entName", element.elementText("ENTNAME"));
					frpositionMap.put("regNo", element.elementText("REGNO"));
					frpositionMap
							.put("entType", element.elementText("ENTTYPE"));
					frpositionMap.put("regCap", element.elementText("REGCAP"));
					frpositionMap.put("regCapCur",
							element.elementText("REGCAPCUR"));
					frpositionMap.put("entStatus",
							element.elementText("ENTSTATUS"));
					frpositionMap
							.put("canDate", element.elementText("CANDATE"));
					frpositionMap
							.put("revDate", element.elementText("REVDATE"));
					frpositionMap.put("regOrg", element.elementText("REGORG"));
					frpositionMap.put("position",
							element.elementText("POSITION"));
					frpositionMap.put("lerepSign",
							element.elementText("LEREPSIGN"));
					frpositionMap.put("esDate", element.elementText("ESDATE"));
					policeAndAICConnectService
							.insertAICFrpositionInfo(frpositionMap);

				}
			}

			// 企业对外投资信息
			if (root.element("ENTINV") != null
					&& root.element("ENTINV").elements("ITEM") != null) {
				List<Element> entinvEle = root.element("ENTINV").elements(
						"ITEM");
				for (Element element : entinvEle) {

					Map<String, Object> entinvMap = Maps.newHashMap();
					entinvMap.put("posCustId", posCustId);
					entinvMap.put("orderNo", orderNo);
					entinvMap.put("entName", element.elementText("ENTNAME"));
					entinvMap.put("regNo", element.elementText("REGNO"));
					entinvMap.put("entType", element.elementText("ENTTYPE"));
					entinvMap.put("regCap", element.elementText("REGCAP"));
					entinvMap
							.put("regCapCur", element.elementText("REGCAPCUR"));
					entinvMap
							.put("entStatus", element.elementText("ENTSTATUS"));
					entinvMap.put("canDate", element.elementText("CANDATE"));
					entinvMap.put("revDate", element.elementText("REVDATE"));
					entinvMap.put("regOrg", element.elementText("REGORG"));
					entinvMap.put("subConAm", element.elementText("SUBCONAM"));
					entinvMap
							.put("congroCur", element.elementText("CONGROCUR"));
					entinvMap.put("funDedRatio",
							element.elementText("FUNDEDRATIO"));
					entinvMap.put("esDate", element.elementText("ESDATE"));
					policeAndAICConnectService.insertAICEntinvInfo(entinvMap);

				}
			}

			// 变更信息

			// 失信被执行人信息
			if (root.element("PUNISHBREAK") != null
					&& root.element("PUNISHBREAK").elements("ITEM") != null) {

				List<Element> punishBreakEle = root.element("PUNISHBREAK")
						.elements("ITEM");
				for (Element element : punishBreakEle) {

					Map<String, Object> punishBreakMap = Maps.newHashMap();
					punishBreakMap.put("posCustId", posCustId);
					punishBreakMap.put("orderNo", orderNo);
					punishBreakMap.put("caseCode",
							element.elementText("CASECODE"));
					punishBreakMap.put("iNameClean",
							element.elementText("INAMECLEAN"));
					punishBreakMap.put("type", element.elementText("TYPE"));
					punishBreakMap.put("sexyClean",
							element.elementText("SEXYCLEAN"));
					punishBreakMap.put("ageClean",
							element.elementText("AGECLEAN"));
					punishBreakMap.put("cardNum",
							element.elementText("CARDNUM"));
					punishBreakMap.put("ysFzd", element.elementText("YSFZD"));
					punishBreakMap.put("businessEntity",
							element.elementText("BUSINESSENTITY"));
					punishBreakMap.put("regDateClean",
							element.elementText("REGDATECLEAN"));
					punishBreakMap.put("publishDateClean",
							element.elementText("PUBLISHDATECLEAN"));
					punishBreakMap.put("courtName",
							element.elementText("COURTNAME"));
					punishBreakMap.put("areaNameClean",
							element.elementText("AREANAMECLEAN"));
					punishBreakMap.put("gistId", element.elementText("GISTID"));
					punishBreakMap.put("gistUnit",
							element.elementText("GISTUNIT"));
					punishBreakMap.put("duty", element.elementText("DUTY"));
					punishBreakMap.put("disruptTypeName",
							element.elementText("DISRUPTTYPENAME"));
					punishBreakMap.put("performance",
							element.elementText("PERFORMANCE"));
					punishBreakMap.put("performedPart",
							element.elementText("PERFORMEDPART"));
					punishBreakMap.put("unperformPart",
							element.elementText("UNPERFORMPART"));
					punishBreakMap.put("focusNumber",
							element.elementText("FOCUSNUMBER"));
					punishBreakMap.put("exitDate",
							element.elementText("EXITDATE"));
					policeAndAICConnectService
							.insertAICPunishBreakInfo(punishBreakMap);

				}
			}

			if (root.element("PUNISHED") != null
					&& root.element("PUNISHED").elements("ITEM") != null) {
				// 被执行人详细信息
				List<Element> punishedEle = root.element("PUNISHED").elements(
						"ITEM");
				for (Element element : punishedEle) {

					Map<String, Object> punishedMap = Maps.newHashMap();
					punishedMap.put("posCustId", posCustId);
					punishedMap.put("orderNo", orderNo);
					punishedMap
							.put("caseCode", element.elementText("CASECODE"));
					punishedMap.put("iNameClean",
							element.elementText("INAMECLEAN"));
					punishedMap.put("cardNumClean",
							element.elementText("CARDNUMCLEAN"));
					punishedMap.put("sexyClean",
							element.elementText("SEXYCLEAN"));
					punishedMap
							.put("ageClean", element.elementText("AGECLEAN"));
					punishedMap.put("areaNameClean",
							element.elementText("AREANAMECLEAN"));
					punishedMap.put("ysFzd", element.elementText("YSFZD"));
					punishedMap.put("courtName",
							element.elementText("COURTNAME"));
					punishedMap.put("regDateClean",
							element.elementText("REGDATECLEAN"));
					punishedMap.put("caseState",
							element.elementText("CASESTATE"));
					punishedMap.put("execMoney", element.element("EXECMONEY"));
					punishedMap.put("focusNumber",
							element.element("FOCUSNUMBER"));
					policeAndAICConnectService
							.insertAICPunishedInfo(punishedMap);

				}
			}

			if (root.element("ALIDEBT") != null
					&& root.element("ALIDEBT").elements("ITEM") != null) {
				// 阿里欠贷信息
				List<Element> aliDebtEle = root.element("ALIDEBT").elements(
						"ITEM");
				for (Element element : aliDebtEle) {

					Map<String, Object> aliDebtMap = Maps.newHashMap();
					aliDebtMap.put("posCustId", posCustId);
					aliDebtMap.put("orderNo", orderNo);
					aliDebtMap.put("sexyClean",
							element.elementText("SEXYCLEAN"));
					aliDebtMap.put("ageClean", element.elementText("AGECLEAN"));
					aliDebtMap.put("areaNameClean",
							element.elementText("AREANAMECLEAN"));
					aliDebtMap.put("ysFzd", element.elementText("YSFZD"));
					aliDebtMap.put("qked", element.elementText("QKED"));
					aliDebtMap.put("wyqk", element.elementText("WYQK"));
					aliDebtMap.put("dkdqsj", element.elementText("DKDQSJ"));
					aliDebtMap.put("tbzh", element.elementText("TBZH"));
					aliDebtMap.put("legalPerson",
							element.elementText("LEGALPERSON"));
					aliDebtMap.put("dkqx", element.elementText("DKQX"));
					policeAndAICConnectService.insertAICAlidebtInfo(aliDebtMap);

				}
			}
			// 行政处罚信息
			if (root.element("CASEINFO") != null
					&& root.element("CASEINFO").elements("ITEM") != null) {

				List<Element> caseInfoEle = root.element("CASEINFO").elements(
						"ITEM");
				for (Element element : caseInfoEle) {

					Map<String, Object> caseInfoMap = Maps.newHashMap();
					caseInfoMap.put("posCustId", posCustId);
					caseInfoMap.put("orderNo", orderNo);
					caseInfoMap
							.put("caseTime", element.elementText("CASETIME"));
					caseInfoMap.put("caseReason",
							element.elementText("CASEREASON"));
					caseInfoMap.put("caseVal", element.elementText("CASEVAL"));
					caseInfoMap
							.put("caseType", element.elementText("CASETYPE"));
					caseInfoMap.put("exeSort", element.elementText("EXESORT"));
					caseInfoMap.put("caseResult",
							element.elementText("CASERESULT"));
					caseInfoMap
							.put("pendecNo", element.elementText("PENDECNO"));
					caseInfoMap.put("pendecissDate",
							element.elementText("PENDECISSDATE"));
					caseInfoMap.put("penAuth", element.elementText("PENAUTH"));
					caseInfoMap.put("illegFact",
							element.elementText("ILLEGFACT"));
					caseInfoMap
							.put("penBasis", element.elementText("PENBASIS"));
					caseInfoMap.put("penType", element.elementText("PENTYPE"));
					caseInfoMap.put("penResult",
							element.elementText("PENRESULT"));
					caseInfoMap.put("penAm", element.elementText("PENAM"));
					caseInfoMap
							.put("penExest", element.elementText("PENEXEST"));
					policeAndAICConnectService.insertAICCaseInfo(caseInfoMap);

				}
			}

			// 变更信息
			if (root.element("ALTER") != null
					&& root.element("ALTER").elements("ITEM") != null) {
				List<Element> alterInfos = root.element("ALTER").elements(
						"ITEM");
				for (Element element : alterInfos) {
					TAICAlterInfo alterInfo = new TAICAlterInfo();
					alterInfo.setOrderNo(orderNo);
					alterInfo.setPosCustId(posCustId);
					alterInfo.setAltItem(element.elementText("ALTITEM"));
					alterInfo.setAltDate(element.elementText("ALTDATE"));
					alterInfo.setAltAf(element.elementText("ALTAF"));
					alterInfo.setAltBe(element.elementText("ALTBE"));
					policeAndAICConnectService.insetAlterInfo(alterInfo);
				}
			}

			// 股权出质信息
			if (root.element("SHARESIMPAWN") != null
					&& root.element("SHARESIMPAWN").elements("ITEM") != null) {
				List<Element> shareSimpawns = root.element("SHARESIMPAWN")
						.elements("ITEM");
				for (Element ele : shareSimpawns) {
					TAICSharesimpawnInfo taicSharesimpawnInfo = new TAICSharesimpawnInfo();
					taicSharesimpawnInfo.setOrderNo(orderNo);
					taicSharesimpawnInfo.setPosCustId(posCustId);
					taicSharesimpawnInfo.setImpAm(ele.elementText("IMPAM"));
					taicSharesimpawnInfo.setImpExAeep(ele
							.elementText("IMPEXAEEP"));
					taicSharesimpawnInfo.setImpOnRecDate(ele
							.elementText("IMPONRECDATE"));
					taicSharesimpawnInfo.setImpOrg(ele.elementText("IMPORG"));
					taicSharesimpawnInfo.setImpOrgType(ele
							.elementText("IMPORGTYPE"));
					taicSharesimpawnInfo.setImpSanDate(ele
							.elementText("IMPSANDATE"));
					taicSharesimpawnInfo.setImpTo(ele.elementText("IMPTO"));
					policeAndAICConnectService
							.insertSharesimpaw(taicSharesimpawnInfo);
				}
			}

			// 动产抵押信息
			if (root.element("MORDETAIL") != null
					&& root.element("MORDETAIL").element("ITEM") != null) {
				List<Element> mordetails = root.element("MORDETAIL").elements(
						"ITEM");
				for (Element element : mordetails) {
					TAICMordetailInfo taicMordetailInfo = new TAICMordetailInfo();
					taicMordetailInfo.setAppRegRea(element
							.elementText("APPREGREA"));
					taicMordetailInfo
							.setCanDate(element.elementText("CANDATE"));
					taicMordetailInfo.setOrderNo(orderNo);
					taicMordetailInfo.setPosCustId(posCustId);
					taicMordetailInfo.setMore(element.elementText("MORE"));
					taicMordetailInfo.setMorRegcNo(element
							.elementText("MORREGCNO"));
					taicMordetailInfo.setMortGagor(element
							.elementText("MORTGAGOR"));
					taicMordetailInfo.setMorRegId(element
							.elementText("MORREGID"));
					taicMordetailInfo
							.setMorType(element.elementText("MORTYPE"));
					taicMordetailInfo.setPefPerForm(element
							.elementText("PEFPERFORM"));
					taicMordetailInfo.setPefPerTo(element
							.elementText("PEFPERTO"));
					taicMordetailInfo.setPriClasecAm(element
							.elementText("PRICLASECAM"));
					taicMordetailInfo.setPriClasecKind(element
							.elementText("PRICLASECKIND"));
					taicMordetailInfo.setRegiDate(element
							.elementText("REGIDATE"));
					taicMordetailInfo.setRegOrg(element.elementText("REGORG"));
					policeAndAICConnectService
							.insertMordetail(taicMordetailInfo);

				}
			}

			// 动产抵押物信息
			if (root.element("MORGUAINFO") != null
					&& root.element("MORGUAINFO").elements("ITEM") != null) {
				List<Element> morguaiInfos = root.element("MORGUAINFO")
						.elements("ITEM");
				for (Element ele : morguaiInfos) {
					TAICMorguaInfo taicMorguaInfo = new TAICMorguaInfo();
					taicMorguaInfo.setOrderNo(orderNo);
					taicMorguaInfo.setPosCustId(posCustId);
					taicMorguaInfo.setGuaName(ele.elementText("GUANAME"));
					taicMorguaInfo.setMorRegId(ele.elementText("MORREGID"));
					taicMorguaInfo.setQuan(ele.elementText("QUAN"));
					taicMorguaInfo.setValue(ele.elementText("VALUE"));
					policeAndAICConnectService.insertMorguaInfo(taicMorguaInfo);
				}
			}

			// 股权冻结历史
			if (root.element("SHARESFROST") != null
					&& root.element("SHARESFROST").elements("ITEM") != null) {
				List<Element> sharesFrosts = root.element("SHARESFROST")
						.elements("ITEM");
				for (Element ele : sharesFrosts) {
					TAICSharesFrostInfo taicSharesFrostInfo = new TAICSharesFrostInfo();
					taicSharesFrostInfo.setOrderNo(orderNo);
					taicSharesFrostInfo.setPosCustId(posCustId);
					taicSharesFrostInfo.setFroAm(ele.elementText("FROAM"));
					taicSharesFrostInfo.setFroAuth(ele.elementText("FROAUTH"));
					taicSharesFrostInfo
							.setFroDocNo(ele.elementText("FRODOCNO"));
					taicSharesFrostInfo.setFroFrom(ele.elementText("FROFROM"));
					taicSharesFrostInfo.setFroTo(ele.elementText("FROTO"));
					taicSharesFrostInfo
							.setThawAuth(ele.elementText("THAWAUTH"));
					taicSharesFrostInfo.setThawComment(ele
							.elementText("THAWCOMMENT"));
					taicSharesFrostInfo
							.setThawDate(ele.elementText("THAWDATE"));
					taicSharesFrostInfo.setThawDocNo(ele
							.elementText("THAWDOCNO"));
					policeAndAICConnectService
							.insertShareFrost(taicSharesFrostInfo);
				}
			}

			// 清算信息
			if (root.element("LIQUIDATION") != null
					&& root.element("LIQUIDATION").elements("ITEM") != null) {
				List<Element> liquidations = root.element("LIQUIDATION")
						.elements("ITEM");
				for (Element ele : liquidations) {
					TAICLiguidationInfo taicLiguidationInfo = new TAICLiguidationInfo();
					taicLiguidationInfo.setOrderNo(orderNo);
					taicLiguidationInfo.setPosCustId(posCustId);
					taicLiguidationInfo.setClaimTranee(ele
							.elementText("CLAIMTRANEE"));
					taicLiguidationInfo.setDebtTranee(ele
							.elementText("DEBTTRANEE"));
					taicLiguidationInfo.setLigEndDate(ele
							.elementText("LIGENDDATE"));
					taicLiguidationInfo.setLigEntity(ele
							.elementText("LIGENTITY"));
					taicLiguidationInfo.setLigPrincipal(ele
							.elementText("LIGPRINCIPAL"));
					taicLiguidationInfo.setLigSt(ele.elementText("LIGST"));
					taicLiguidationInfo.setLiqMen(ele.elementText("LIQMEN"));
					policeAndAICConnectService
							.insertLiquidationInfo(taicLiguidationInfo);
				}
			}
			logger.debug(posCustName + "查询工商信息完成");

		}
		// 返回为空代表有异常
		Map<String, String> resMap = Maps.newHashMap();
		logger.debug("解析工商返回报文成功");
		resMap.put("resCode", "00");
		resMap.put("respMsg", "工商信息入库成功");
		return resMap;
	}

	@Override
	public String getBankCardCheckUrl(Map<String, String> reqMap) {
		try {
			reqMap.put("service", p.getProperty("SERVICENAME"));
			reqMap.put("partnerId", p.getProperty("PARTNERID"));
			reqMap.put("signType", "MD5");
			reqMap.put("certType", "Identity_Card");
			reqMap.put("orderNo", IdUtil.getBankCheckSeqNo());
			logger.debug("调账户验真参数为:" + reqMap.toString());
			TreeMap<String, String> treeMap = new TreeMap<String, String>(
					reqMap); // 对Map里的每一个成员按字符ASC 码的顺序排序，若遇到相同首字符，则看第二个字符，
			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, String> entry : treeMap.entrySet()) {
				sb.append(entry.getKey() + "=" + entry.getValue() + "&");
			}
			String plain = sb.toString().substring(0,
					sb.toString().length() - 1);
			String sign = BankUtil.getMD5(plain + p.getProperty("SAFECODE"),
					"UTF-8").toLowerCase(); // 参数+安全码 做MD5然后转成小写
			logger.debug("账户验真url：" + p.getProperty("BANKCHECKURL") + "?sign="
					+ sign + "&" + plain);
			return p.getProperty("BANKCHECKURL") + "?sign=" + sign + "&"
					+ plain;
		} catch (Exception e) {
			logger.error("获取账户验真url异常:" + e);
			return null;
		}

	}

	@Override
	public boolean hasBankCardCheck(String bankAccno) {
		Map<String, Object> reqMap = Maps.newHashMap();
		reqMap.put("bankAccno", bankAccno);
		List<Map<String, Object>> resList = loanPosCreditApplyService
				.selectBankAccMap(reqMap);
		if (ListUtil.isNotEmpty(resList)
				&& !StringUtil.isEmpty((String) resList.get(0).get("status"))) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Map<String, String> updateBankAccnoStatus(String resultMessage,
			boolean success, String bankAccno, String channel,
			String checkChannel, Map<String, String> reqMap) throws Exception {
		Map<String, Object> updateMap = Maps.newHashMap();
		Map<String, String> resMap = Maps.newHashMap();
		// 银商渠道是外部验真，不用调验真接口
		/*if ("UM".equals(channel)) {
			// 外部验真
			updateMap.put("status", "03");
			updateMap.put("bankAccno", bankAccno);
			loanPosCreditApplyService
					.updateBankAccByPrimaryKeySelectiveMap(updateMap);
			resMap.put("respCode", "00");
			resMap.put("respMsg", "账户验真成功");
			return resMap;
		}*/
		String status = "";
		logger.debug(bankAccno + "账户验证结果为:" + success);
		updateMap.put("bankAccno", bankAccno);
		if (success) {
			status = "01";
			resMap.put("respCode", "00");
			resMap.put("respMsg", "账户验真成功");
		} else {
			// 代表易极付
			if ("1".equals(checkChannel)) {
				// 如果是不支持校验
				if (resultMessage.contains("不支持")) {
					status = "00";
				} else {
					status = "02";
				}
				// 代表银联智慧
			} else {
				if (resultMessage.contains("不存在")
						|| resultMessage.contains("禁用")) {
					status = "02";
				} else {
					status = "00";
				}
			}
			resMap.put("respCode", "01");
			resMap.put("respMsg", "账户验真失败");
		}
		//先查询t_bankaccount_info表中是否该银行卡的记录
		TBankAccnoInfo bankInfo = tBankAccnoInfoDao.selectByPrimaryKey(bankAccno);
		updateMap.put("status", status);
		updateMap.put("custId", reqMap.get("custId"));
		updateMap.put("bankName", reqMap.get("bankName"));
		updateMap.put("bankBranName", reqMap.get("bankBranName"));
		updateMap.put("bankSubbName", reqMap.get("bankSubbName"));
		updateMap.put("createTime", new Date());
		if(bankInfo == null){
			tBankAccnoInfoDao.insertSelectiveMap(updateMap);
		}else{
			loanPosCreditApplyService
			.updateBankAccByPrimaryKeySelectiveMap(updateMap);
		}
		
		
		return resMap;

	}

	@Override
	public Map<String, List<Map<String, Object>>> queryAicInfo(
			String posCustName, String posCustId) {
		try {
			if (StringUtil.isEmpty(posCustName)
					&& StringUtil.isEmpty(posCustId)) {
				return null;
			} else {
				return policeAndAICConnectService.queryAicInfo(posCustName,
						posCustId);
			}

		} catch (Exception e) {
			logger.error("查询工商信息异常:", e);
			return null;
		}
	}

}
