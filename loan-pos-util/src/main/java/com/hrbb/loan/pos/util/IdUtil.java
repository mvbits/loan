package com.hrbb.loan.pos.util;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *<h1>生成id</h1>
 *@author Johnson Song
 *@version Id: Id.java, v 1.0 2015-3-4 下午2:46:52 Johnson Song Exp
 */
public class IdUtil {
    
    private static Logger logger = LoggerFactory.getLogger(IdUtil.class);
    
	public static String getId(String prefix){
		String id = prefix + DateUtil.getCurrentTimePattern5() + RandomUtil.getRandom(9);
		id = StringUtils.rightPad(id, 30, "0");
		return id;
		
	}
	
	public static String getCreditInvestigateId(){
		String id =  "CI" + DateUtil.getCurrentTimePattern5() + RandomUtil.getRandom(4);
		id = StringUtils.rightPad(id, 25, "0");
		return id;
	}
	
	/**
	 * 兼顾数据回行的客户号长度(20bit)要求，调整客户号生成规则:C+yyyyMMddHHmmss+身份证后4位+校验位
	 * @param paperId
	 * @return
	 */
	public static String getCustId(String paperId){
//		return "CU"+ DateUtil.getCurrentTimePattern5() + paperId +RandomUtil.getRandom(3);
		
		final int CUST_ID_LEN = 20;		//回行的客户号长度(20bit)要求
		final String ID_PREFIX = "C";
		String id = "";
		
		if(paperId==null || paperId.trim().length() != 18){
			id = ID_PREFIX+ DateUtil.getCurrentTimePatterna() + RandomUtil.getRandom(4);
			
		}else{
		
			String baseId = ID_PREFIX + DateUtil.getCurrentTimePattern5();	//yyyyMMddHHmmssSSS
			String showId = baseId.substring(0,baseId.length()-3);		//yyyyMMddHHmmss
			String rmd = RandomUtil.getRandom(3);
			String vCode = "";
			try {
				vCode = MessageDigest.getDigestAsUpperHexString("MD5", baseId+paperId+rmd).substring(0, 1);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				vCode = RandomUtil.getRandom(1);
			}
			
			id = showId + paperId.substring(14, 18)+vCode;
		}
		
		return StringUtils.rightPad(id, CUST_ID_LEN, "0");
	}
	
	public static void main(String[] args) {
		logger.debug(getCustId(""));
		logger.debug(getCustId("12345676543"));
		logger.debug(getCustId("32010519770101205X"));
	}
	
	/**
	 * 按照如下规则生成业务申请编号：日期(8位)+合作渠道(2位)+进件渠道(2位)+流水号(10位)
	 * @param channel
	 * @param implType
	 * @return
	 */
	/*改用trigger生成
	public static String getBizLoanId(String channel, String implType){
		String bizId = DateUtil.getTodayDatePattern1()+channel+implType+DateUtil.getCurrentTimePattern8()+RandomUtil.getRandom(4);
		return StringUtils.rightPad(bizId, 22, "0");
	}
	*/
	
	public static String getPaymentApplyId(){
		return getId("PY");
	}
	
	public static String getRepaymentApplyId(){
		return getId("RP");
	}	
	
	public static String getReceiptId(){
	    return getId("RC");
	}
	
	public static String getCreditInvestigateSeqNo(){
		return "IFS"+DateUtil.getTodayDatePattern1()+StringUtils.leftPad(RandomUtil.getRandom(7), 9, "0");
	}
	/*
	public static String getBizContractId(String channel, String implType){
		return "SXXY"+DateUtil.getTodayDatePattern1()+channel+implType+DateUtil.getCurrentTimePattern8()+RandomUtil.getRandom(4);
	}
	*/
	
	public static String getBankCheckSeqNo(){
		return DateUtil.getTodayDatePattern1() + StringUtils.leftPad(RandomUtil.getRandom(9), 9, "0");
	}

	public static String getCreditInId() {
		return "POS"+ DateUtil.getCurrentTimePatterna() + RandomUtil.getRandom(3);
	}

	public static String getQueryId() {
		return getId("RT");
	}
	
	public static String getqueryBalSeq(){
		return "BAL" + DateUtil.getTodayDatePattern1()+StringUtils.leftPad(RandomUtil.getRandom(4), 5, "0");
	}
	
	//身份证转换用数据字段
    private static int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1};
	private static char[] vcode = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
	/**
	 * 校验身份证的合法性
	 * @param certId 18位身份证号
	 * @return
	 */
	public static final boolean verifyCertId(String certId){
		if(certId==null || certId.trim().length()!=18) return false;
		
		StringBuffer ID18 = new StringBuffer(certId); 
		int vsum = 0;
		for (int i = 0; i < 17; i++) {
			vsum += Character.digit(ID18.charAt(i),10)*weight[i];
		}
		char vCode = vcode[vsum%11];
		
		return (certId.endsWith(String.valueOf(vCode)));
	}

	public static String getActId() {
		return getId("ACT");
	}

	public static String getInnerTranserId() {
		return DateUtil.getCurrentTimePattern5() + StringUtils.leftPad(RandomUtil.getRandom(3), 3, "0");
	}
}

