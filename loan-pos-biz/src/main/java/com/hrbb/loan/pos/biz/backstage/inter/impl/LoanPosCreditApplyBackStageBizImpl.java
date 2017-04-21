package com.hrbb.loan.pos.biz.backstage.inter.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.amarsoft.app.crq.report.CreditReport;
import com.amarsoft.app.crq.report.ReportChapter;
import com.amarsoft.app.crq.report.ReportPart;
import com.amarsoft.app.crq.report.ReportSection;
import com.amarsoft.app.crq.reportnew.CreditReportManager;
import com.amarsoft.are.jbo.BizObject;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.hrbb.loan.pos.biz.backstage.inter.ILoanPosCreditApplyBackStageBiz;
import com.hrbb.loan.pos.biz.backstage.inter.ILoanPosCreditApplyCheckRegBiz;
import com.hrbb.loan.pos.biz.backstage.inter.impl.reader.CYRowReader;
import com.hrbb.loan.pos.biz.backstage.inter.impl.reader.KQDRowReader;
import com.hrbb.loan.pos.biz.backstage.inter.impl.reader.POSRowReader;
import com.hrbb.loan.pos.biz.backstage.inter.impl.reader.UMMRowReader;
import com.hrbb.loan.pos.biz.backstage.inter.impl.reader.UPDRowReader;
import com.hrbb.loan.pos.biz.backstage.inter.impl.reader.UPMRowReader;
import com.hrbb.loan.pos.biz.backstage.inter.impl.reader.YBDRowReader;
import com.hrbb.loan.pos.dao.TApproveResultDao;
import com.hrbb.loan.pos.dao.TContractInfoDao;
import com.hrbb.loan.pos.dao.TCreditApplyAprvInfoDao;
import com.hrbb.loan.pos.dao.TCreditApplyDao;
import com.hrbb.loan.pos.dao.TCreditApplyForReviewDao;
import com.hrbb.loan.pos.dao.TCreditReportBriefDao;
import com.hrbb.loan.pos.dao.entity.TApproveResult;
import com.hrbb.loan.pos.dao.entity.TContractManagement;
import com.hrbb.loan.pos.dao.entity.TCreditApply;
import com.hrbb.loan.pos.dao.entity.TCreditApplyAprvInfo;
import com.hrbb.loan.pos.dao.entity.TCreditReportBrief;
import com.hrbb.loan.pos.dao.entity.TCustomer;
import com.hrbb.loan.pos.entity.event.EventException;
import com.hrbb.loan.pos.entity.event.POSEvent;
import com.hrbb.loan.pos.entity.event.POSEventsSource;
import com.hrbb.loan.pos.entity.event.cab.NotifyCab;
import com.hrbb.loan.pos.entity.listener.impl.NtfListener;
import com.hrbb.loan.pos.entity.track.TBankAcntTracker;
import com.hrbb.loan.pos.entity.track.TCreditApplyTracker;
import com.hrbb.loan.pos.entity.track.TCustomerTracker;
import com.hrbb.loan.pos.entity.track.TPosCustomerTracker;
import com.hrbb.loan.pos.entity.track.TRelativeTracker;
import com.hrbb.loan.pos.factory.ConfigService;
import com.hrbb.loan.pos.factory.FtpCodeFactory;
import com.hrbb.loan.pos.factory.StatusChannelMapFactory;
import com.hrbb.loan.pos.factory.SysConfigFactory;
import com.hrbb.loan.pos.factory.crquid.CRQuidFactory;
import com.hrbb.loan.pos.factory.crquid.ICRQuid;
import com.hrbb.loan.pos.service.BusinessDictionaryService;
import com.hrbb.loan.pos.service.CreditApplyAprvInfoService;
import com.hrbb.loan.pos.service.CreditInvestigateService;
import com.hrbb.loan.pos.service.LoanPosBlacklistService;
import com.hrbb.loan.pos.service.LoanPosBusinessCodeService;
import com.hrbb.loan.pos.service.LoanPosCreditApplyService;
import com.hrbb.loan.pos.service.LoanPosCustomerService;
import com.hrbb.loan.pos.util.DateUtil;
import com.hrbb.loan.pos.util.HttpUtil;
import com.hrbb.loan.pos.util.IdUtil;
import com.hrbb.loan.pos.util.ListUtil;
import com.hrbb.loan.pos.util.MapUtils;
import com.hrbb.loan.pos.util.PwdUtil;
import com.hrbb.loan.pos.util.StringUtil;
import com.hrbb.loan.pos.util.ZipUtils;
import com.hrbb.loan.pos.util.constants.BusinessDictionaryConstants;
import com.hrbb.loan.pos.util.constants.CreditInvestigateConstants;
import com.hrbb.loan.pos.util.constants.ReviewNoteConstants;
import com.hrbb.loan.pos.util.excel.ExcelReaderUtil;
import com.hrbb.sh.framework.HConnector;
import com.hrbb.sh.framework.HResponse;
import com.hrbb.sh.framework.domain.CreditInvestigateRequest;
import com.hrbb.sh.framework.domain.CreditInvestigateResponse;
import com.hrbb.sh.framework.ftp.FtpTransUpload;
import com.hrbb.sh.framework.ftp.HttpClient;
import com.hrbb.sh.framework.ftp.ParamReqBean;
import com.hrbb.sh.framework.ftp.ParamResBean;
import com.hrbb.sh.framework.ftpproxy.HFTPFile;
import com.hrbb.sh.framework.impl.CreditInvestigateImpl;
import com.hrbb.sh.frontier.biz.route.facade.RouteFacade;

/*
 *
 * <h1></h1>
 * 
 * @author Johnson Song
 * @version Id: LoanPosCreditApplyBackStageBizImpl.java, v 1.0 2015-2-28
 *          上午11:16:58 Johnson Song Exp
 */
@Component("loanPosCreditApplyBackStageBiz")
public class LoanPosCreditApplyBackStageBizImpl implements ILoanPosCreditApplyBackStageBiz{
    
    private static Logger logger = LoggerFactory.getLogger(LoanPosCreditApplyBackStageBizImpl.class);
    

	@Autowired
	private FtpTransUpload ftpTransUpload;

	@Autowired
	private LoanPosCreditApplyService loanPosCreditApplyService;
	
	@Autowired
	private TCreditReportBriefDao tCreditReportBriefDao;

	@Autowired
	@Qualifier("tCreditApplyForReviewDao")
	private TCreditApplyForReviewDao daoCA;

	@Autowired
	private LoanPosBlacklistService loanPosBlacklistService;

	@Autowired
	private LoanPosBusinessCodeService loanPosBusinessCodeService;

	@Autowired
	private CreditInvestigateService creditInvestigateService;

	@Autowired
	private TCreditApplyAprvInfoDao tCreditApplyAprvInfoDao;
	
	@Autowired
	private TApproveResultDao tApproveResultDao;

	@Autowired
	private TContractInfoDao tContractInfoDao;
	
	@Autowired
    private TCreditApplyDao tCreditApplyDao;

	@Autowired
	private ILoanPosCreditApplyCheckRegBiz loanPosCreditApplyCheckRegBiz;

	@Autowired
	@Qualifier("businessDictionaryService")
	private BusinessDictionaryService businessDictionaryService;

	@Autowired
	@Qualifier("creditApplyAprvInfoService")
	private CreditApplyAprvInfoService creditApplyAprvInfoService;
	
	@Autowired
	private LoanPosCustomerService loanPosCustomerService;
	
	@Autowired
	private RouteFacade routeFacade;

	private final String RETURN_KIND = "returnKind";

	private static String channelPosCustId = "channelPosCustId";

	private static final String createTime = "createTime";
	
	public static final String CUST_ID = "custId";

	public static final String PAPER_ID = "paperId";

	public static final String CUST_NAME = "custName";

	public static final String LOAN_ID = "loanId";

	public static final String POS_CUST_NAME = "posCustName";

	public static final String POS_CUST_ID = "posCustId";

	public static final String BANK_ACC_NO = "bankAccno";

	public static final String LOAN_PREFIX = "LO";

	public static final String CUST_PREFIX = "CU";

	public static final String POS_CUST_PREFIX = "MH";

	public static final String RELA_ID = "relativeId";

	public static final String RELA_CUST_NAME = "relaCustName";

	public static final String EFF_FLAG = "effectFlag";

	public static final String CERT_TYPE = "certType";

	public static final String INFO_TYPE = "infoType";
	
	public static final String INFO_DETAIL = "infoDetail";

	public static final String POS_CHANNEL = "posChannel";

	public static final String POS_KIND = "posKind";

	public static final String RELATIVE_PRFIX = "RE";

	public static final String MERCHANT_ID = "merchantId";

	public static final String MERCHANT_NAME = "merchantName";

	public static final String TRADE_TYPE = "tradeType";

	public static final String MERCHANT_TYPE_CODE = "merchantTypeCode";

	public static final String TRADE_CARD_NO = "tradeCardNo";

	public static final String ROWS = "rows";

	public static final String TOTAL = "total";

	public static final String birtDate = "birtDate";

	public static final String mateBirtDate = "mateBirtDate";

	public static final String industryTypeId2 = "industryTypeId2";

	public static final String industryTypeId = "industryTypeId";

	public static final String posCustProv = "posCustProv";

	public static final String posCustCity = "posCustCity";

	public static final String busiDivision = "busiDivision";

	public static final String operAddrCode = "operAddrCode";

	public static final String stdmerno = "stdmerno";

	public static final String channel = "channel";
	

	// 机构代码(UM：银商)
	public static final String ftp_instno_um = "UM";
	// 机构代码(LC：224测试服务器ftp)
	public static final String ftp_instno_lc = "LC";
	// 机构代码(AP: 手机)
	public static final String ftp_instno_ap = "AP";
	// 阶段前缀(申请：APP)
	public static final String ftp_prefix_app = "APP";
	// 阶段前缀(合同：CNT)
	public static final String ftp_prefix_cnt = "CNT";
	// 消息版本号
	public static final String ftp_version = "1.0.0";
	// 业务类型
	public static final String ftp_biztype = "0001";
	// 交易类型
	public static final String ftp_transtype = "0001";
	// 同步交易类型
    public static final String ftp_transtype_sync = "0005";
	// 接收处理结果(正常)
	public static final String ftp_resp_code_000 = "000";

	public static final String ftp_map_key_prefix = "Prefix";
	public static final String ftp_map_key_instno = "InstNo";
	public static final String ftp_map_key_remotefilename = "RemoteFileName";
	public static final String ftp_map_key_LocalSubFolderNameDef = "LocalSubFolderNameDef";
	public static final String ftp_map_key_url = "url";
	public static final String contract_map_key_returnUrl = "contractManagement/updateContractStatus.do";

	public static final String ftp_map_key_returnUrl = "creditApplyUpdate/updateBackToReview.do";

	public static final String ftp_map_key_returnUrl_imageData = "creditApply/uploadImageDataBackController.do";

	public static final String ftp_map_key_returnUrl_download_file = "creditApplyUpdate/updateApplyStatusForDownloadImages.do";
	
	public static final String ftp_map_key_returnUrl_download_file2 = "creditApplyUpdate/updateApplyStatusForDownloadImagesAuto.do";
	
	public static final String ftp_map_key_returnUrl_download_contract = "creditApplyUpdate/updateContractStatusForDownload.do";
	
	private static Vector<String> vector = new Vector<String>();
	
	
	@Override
	public Map<String, Object> creditApplyReg(Map<String, Object> creMap,
			Map<String, Object> custMap, Map<String, Object> posCustMap,
			Map<String, Object> bankMap, Map<String, Object> relMap,
			List<Map<String, Object>> bankSerList) {
		logger.info("in LoanPosCreditApplyBackStageBizImpl ...");
		Map<String, Object> resultMap = Maps.newHashMap();
		
		try {
			
			//查询是否存在该申请
			if(creMap.get("stdshno") != null){
				Map<String, Object> reqMap = Maps.newHashMap();
				reqMap.put("stdshno", creMap.get("stdshno"));
				List<Map<String, Object>> rList = loanPosCreditApplyService.getCreditApplyMap(reqMap);
				if(rList.size() != 0){
					logger.debug(reqMap.get("stdshno") + "已经有申请记录");
					resultMap.put("custid", rList.get(0).get("custId"));
					resultMap.put("loanid", rList.get(0).get("loanId"));
					return resultMap;
				}
			}
			// APP端发起请求的结果集体
			// pos贷产品代码1001020306
			String custId = null;
			String applyStatus = null;
			String posCustId = null;
			// 查询是否存在该用户，有则更新，无则新增

			Map<String, Object> queryCustMap = null;

			Map<String, Object> queryRelaMap = null;

			if (custMap.get(CUST_NAME) != null && custMap.get(PAPER_ID) != null) {
				logger.debug("身份证号为:" + custMap.get(PAPER_ID));
				//先查询该身份证号是否已存在
				List<TCustomer> checkCust = loanPosCustomerService.selectOneCustomerByPaper((String) custMap.get(PAPER_ID));
				logger.debug("共查出:" + checkCust.size() + "条记录");
				/*if(!checkCust.isEmpty()){
					logger.debug("数据库中的姓名为:" + checkCust.get(0).getCustName() +"传入的姓名为:" + custMap.get(CUST_NAME));
				}*/
				if(! checkCust.isEmpty() && checkCust.get(0).getCustName() != null && !checkCust.get(0).getCustName().trim().equals((String)custMap.get(CUST_NAME))){
					resultMap.put("hasPaperId", "1");
					return resultMap;
				}
				queryCustMap = loanPosCustomerService
						.selectOneCustomerByNameAndPaper(
								(String) custMap.get(CUST_NAME),
								(String) custMap.get(PAPER_ID));
			}
			
			
			// 如果存在则更新
			if (!queryCustMap.isEmpty()) {
				logger.info("queryCustMap = " + MapUtils.toString(queryCustMap));
				custId = (String) queryCustMap.get(CUST_ID);
				custMap.put(CUST_ID, custId);
				loanPosCustomerService.updateCustomerInfoMap(custMap);
				logger.info("updateCustomerInfoMap  success !");
				//亲戚Map不为空
				if(relMap != null){
					queryRelaMap = loanPosCustomerService.getOneRelativeInfo(
							(String) relMap.get(RELA_CUST_NAME), custId);
					// 如果没有亲属信息,则添加
					if (queryRelaMap.isEmpty()) {
						relMap.put(CUST_ID, custId);
						String relaId = IdUtil.getId(RELATIVE_PRFIX);
						relMap.put(RELA_ID, relaId);
						creMap.put(RELA_ID, relaId);
						loanPosCustomerService.insertRelativeInfo(relMap);
						logger.info("insertRelativeInfo success !");
					} else {
						creMap.put(RELA_ID, queryRelaMap.get(RELA_ID));
					}
					
				}
				
				if(posCustMap != null){
					// 查询是否存在该商户
					Map<String, Object> posCustQueryMap = loanPosCreditApplyService
							.selectOnePosCustMap((String) posCustMap.get(POS_CUST_NAME));
					
					// 如果不存在这个商户
					if (posCustQueryMap.isEmpty()) {
						if(posCustMap.get("posCustId") != null){
							posCustId = (String)posCustMap.get("posCustId");
						}else{
							posCustId = IdUtil.getId(POS_CUST_PREFIX);
						}
						posCustMap.put(POS_CUST_ID, posCustId);
						posCustMap.put(CUST_ID, custId);
						loanPosCreditApplyService.insertPosCustSelectiveMap(posCustMap);
						logger.info("insertPosCustSelectiveMap success !");
					} else {
						posCustId = (String) posCustQueryMap.get("posCustId");
					}
					
				}
				
			} else {
				logger.info("queryCustMap is null");
				try{
					Map<String, Object> bodyMap = Maps.newHashMap();
					bodyMap.put("mobile", custMap.get("mobilePhone"));
					bodyMap.put("isTransacted", "1");
					bodyMap.put("appsource", "1");
					Map<String, Object> headerMap = Maps.newHashMap();
					HResponse response = routeFacade.routeService("UMP0002", headerMap, bodyMap);
					Map<String, Object> body = (Map<String, Object>)response.getProperties().get("body");
					logger.debug("ump返回结果body为:" + body);
					custId = (String) body.get("custId");
					logger.info("用户系统返回的custId为:" + custId);
				}catch(Exception e){
					logger.error("调用户系统获取custId发生异常", e);
					custId = IdUtil.getCustId((String) custMap.get(PAPER_ID));
					
				}
				/*custId = IdUtil.getCustId((String) custMap.get(PAPER_ID));*/
				if(StringUtil.isEmpty(custId)){
					logger.error("用户系统返回的custId为空");
					custId = IdUtil.getCustId((String) custMap.get(PAPER_ID));
				}
				custMap.put(CUST_ID, custId);
				//判断性别
				if(custMap.get(PAPER_ID) != null){
					if(Integer.valueOf(String.valueOf(((String)custMap.get(PAPER_ID)).charAt(16))) % 2 != 0){
						//奇数为男性
						custMap.put("sexSign", "1");
					}else{
						//偶数为女性
						custMap.put("sexSign", "2");
					}
				}
				
				if(custMap.get("matePaperId") != null){
					if(Integer.valueOf(String.valueOf(((String)custMap.get("matePaperId")).charAt(16))) % 2 != 0){
						//奇数为男性
						custMap.put("mateSexSign", "1");
					}else{
						//偶数为女性
						custMap.put("mateSexSign", "2");
					}
				}
				loanPosCustomerService.insertCustomerInfoMap(custMap);
				logger.info("insertCustomerInfoMap  success !");
				if(relMap != null){
					relMap.put(CUST_ID, custId);
					String relaId = IdUtil.getId(RELATIVE_PRFIX);
					relMap.put(RELA_ID, relaId);
					creMap.put(RELA_ID, relaId);
					
					
					
					loanPosCustomerService.insertRelativeInfo(relMap);
					logger.info("insertRelativeInfo success !");
					
				}
				if(posCustMap != null){
						if(posCustMap.get("posCustId") != null){
							posCustId = (String)posCustMap.get("posCustId");
						}else{
							posCustId = IdUtil.getId(POS_CUST_PREFIX);
						}
						posCustMap.put(POS_CUST_ID, posCustId);
						posCustMap.put(CUST_ID, custId);
						loanPosCreditApplyService.insertPosCustSelectiveMap(posCustMap);
						logger.info("insertPosCustSelectiveMap success !");
					
				}
			}

			// 邮件接口

			// 短信接口
			// 插入记录
			// String loanId = IdUtil.getId(LOAN_PREFIX); //外部生成后传入
			String loanId = (String) creMap.get(LOAN_ID); // 直接取值 by Lin,Zhaolin
			if (loanId == null || loanId.trim().length() == 0) {
				loanId = IdUtil.getId(LOAN_PREFIX);
				creMap.put(LOAN_ID, loanId);
			}
			logger.info("loanid 生成 = [" + loanId + "]");
			
			// creMap.put(LOAN_ID, loanId);
			creMap.put(CUST_ID, custId);
			
			// 如果不存在这张银行卡号
			if(bankMap != null){

				if (loanPosCreditApplyService.selectOneBankAcc(
						(String) bankMap.get(BANK_ACC_NO)).isEmpty()) {
					bankMap.put(CUST_ID, custId);
					bankMap.put(createTime, new Date());
					loanPosCreditApplyService.insertBankAccSelectiveMap(bankMap);
					logger.info("insertBankAccSelectiveMap success !");
				}
			}
			// 如果这张银行卡存在
			if (ListUtil.isNotEmpty(bankSerList)) {
				for (Map<String, Object> insertMap : bankSerList) {
					insertMap.put(LOAN_ID, loanId);
					insertMap.put(CUST_ID, custId);
					insertMap.put(CUST_NAME, custMap.get(CUST_NAME));
					loanPosCreditApplyService.insertBankSerialMap(insertMap);
					logger.info("insertBankSerialMap success !");
				}
			}
			creMap.put(POS_CUST_ID, posCustId);
			//映射还款方式
			creMap.put(RETURN_KIND , StatusChannelMapFactory.getChannelReturnKind((String)creMap.get("channel"), (String)creMap.get(RETURN_KIND)));
			//regDate
			creMap.put("regDate", new Date());
			loanPosCreditApplyService.insertCreditApplyMap(creMap);
			logger.info("insertCreditApplyMap success !");
			// 渠道商户id与内部商户id映射，先查询t_channel_poscust_info 表
			if(posCustMap != null){
				
				if (posCustMap.get(stdmerno) != null) {
					Map<String, Object> channelPosCustMap = loanPosCreditApplyService
							.selectOneChannelPosCustByEle(
									(String) posCustMap.get(stdmerno),
									(String) creMap.get(channel));
					// 如果不存在则插入一条新的记录
					if (channelPosCustMap.isEmpty()) {
						Map<String, Object> insertChannelPosCustMap = Maps
								.newHashMap();
						insertChannelPosCustMap.put(channel, creMap.get(channel));
						insertChannelPosCustMap.put(POS_CUST_ID, posCustId);
						insertChannelPosCustMap.put(channelPosCustId,
								posCustMap.get(stdmerno));
						loanPosCreditApplyService
						.insertChannelPosCustSelectiveMap(insertChannelPosCustMap);
						logger.info("insertChannelPosCustSelectiveMap success !");
					}
				}
				
				logger.info("success create custId=[" + custId + "], loanId = ["
						+ loanId + "]");
			}
			
			resultMap.put("custid", custId);
			resultMap.put("loanid", loanId);
			return resultMap;
		} catch (Exception e) {
			logger.error("creditApplyError:", e);
			return Maps.newHashMap();
		}

	}

	/*
	 * 将代码转换为中文，并赋值到新的列中
	 * 
	 * @param vals [srcCol, codeNo, targetCol]
	 * 
	 * @param m
	 * 
	 * @return
	 */
	private boolean convertCode(String[] vals, Map<String, Object> m) {
		if (vals == null || vals.length != 3 || vals[0] == null
				|| vals[0].trim().length() == 0 // origCol
				|| vals[1] == null || vals[1].trim().length() == 0 // codeNo
				|| vals[2] == null || vals[2].trim().length() == 0) { // appCol
			return true; // 无值忽略
		}

		Object itemo = m.get(vals[0]);
		if (itemo != null) {
			String itemNo = itemo.toString();
			if (itemNo != null && itemNo.trim().length() > 0) {
				String itemName = loanPosBusinessCodeService.getItemNameByNo(
						vals[1], itemNo);
				if (itemName != null) { // 代码存在
					m.put(vals[2], itemName);
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param itemNo
	 * @param vals
	 * @param m
	 * @return
	 */
	private boolean convertCode(String itemNo, String[] vals,
			Map<String, Object> m) {
		if (vals == null || vals.length != 2 || itemNo == null
				|| itemNo.trim().length() == 0 || vals[0] == null
				|| vals[0].trim().length() == 0 // codeNo
				|| vals[1] == null || vals[1].trim().length() == 0) { // appCol
			return true; // 无值忽略
		}

		if (itemNo != null && itemNo.trim().length() > 0) {
			String itemName = loanPosBusinessCodeService.getItemNameByNo(
					vals[0], itemNo);
			if (itemName != null) { // 代码存在
				m.put(vals[1], itemName);
			}
		}
		return true;
	}

	@Override
	public List<Map<String, Object>> queryCreditApply(Map<String, Object> reqMap) {
		// 查询所有受理状态及之后状态的申请数据，列表展示
		try {

			List<Map<String, Object>> list = loanPosCreditApplyService
					.getCreditApplyMap(reqMap);

			// ------add by Lin,Zhaolin
			for (Map<String, Object> r : list) {
				// [part1]申请审批人信息
				Object oApplyStatus = r.get("applyStatus");
				if (oApplyStatus != null) {
					String valStatus = oApplyStatus.toString();
					if (valStatus != null && !valStatus.equals("00")) { // 待处理阶段除外
						TCreditApplyAprvInfo key = new TCreditApplyAprvInfo();
						key.setLoanId(String.valueOf(r.get("loanId")));
						TCreditApplyAprvInfo prevReviewer = tCreditApplyAprvInfoDao
								.selectLastOne(key);
						if (prevReviewer != null) {
							convertCode(prevReviewer.getApprState(),
									new String[] { "ApplyStatus", "lastStep" },
									r);
							r.put("lastStepOperName", prevReviewer.getApprvId());
							r.put("creditLine", prevReviewer.getApprAmount());
							r.put("creditInterest", prevReviewer.getApprInte());

						}// 上一阶段为空
						TCreditApplyAprvInfo executeReviewer = tCreditApplyAprvInfoDao
								.selectExecutor(key);
						if (executeReviewer != null) {
							r.put("execReviwer", executeReviewer.getApprvId());
						}// 没有认领
					}
				}

				// [part2] convert code to name
				convertCode(new String[] { "channel", "BizChannel",
						"channelName" }, r);
				convertCode(new String[] { "applyStatus", "ApplyStatus",
						"applyStatusName" }, r);
				convertCode(new String[] { "prodId", "ProductNo", "prodName" },
						r);
				// 转换经营地址
				Object oRegion = r.get("region");
				if (oRegion != null) {
					String itemoRegion = oRegion.toString();
					if (itemoRegion.length() >= 2) {
						itemoRegion = itemoRegion.substring(0, 2) + "0000"; // 显示经营地的省/市一级
						convertCode(itemoRegion, new String[] {
								"AdminDivision", "region" }, r);
					}
				}
			}

			// ------记录总数
			Long total = loanPosCreditApplyService.countCreditApply(reqMap);
			Map<String, Object> map = Maps.newHashMap();
			map.put("total", total);
			list.add(map);
			return list;
		} catch (Exception e) {
			logger.error("查询异常:[]", e.getMessage());
			return Lists.newArrayList();
		}
	}

	@Override
	public Map<String, Object> queryCreditApplyDetail(Map<String, Object> reqMap) {
		// 查看该笔申请的详细信息
		return null;
	}

	@Override
	public void deleteCreditApplyInfo(Map<String, Object> reqMap) {
		// 申请信息删除，逻辑删除
		loanPosCreditApplyService.updateCreditApplyMap(reqMap);
	}

	@Override
	public void cancelCreditApply(Map<String, Object> reqMap) {
		// 申请撤销，客户主动提出撤销受理
	}

	@Override
	public boolean uploadPosSerial(File file, String posChannel,
			String posType, String loanId) throws Exception {
		try {
			// Workbook wb = PoiUtil.read(file);
			// Sheet sheet1 = wb.getSheetAt(0);
			// //唯一性POS流水主键池
			Set<String> uniquePOSKey = Sets.newHashSet();

			String channelBiz = posChannel + posType;
			switch (channelBiz) {
			// 块钱日明细
			case BusinessDictionaryConstants.BIZ_CHANNEL_KQ
					+ BusinessDictionaryConstants.POS_TYPE_DAY: {
				List<Map<String, Object>> insertList = Lists.newArrayList();
				// for (Row row : sheet1) {
				// if (row.getRowNum() != 0) {
				// // 不解析标题
				// Map<String, Object> insertMap = Maps.newHashMap();
				// insertMap.put(LOAN_ID, loanId);
				// insertMap.put(POS_CHANNEL, posChannel);
				// insertMap.put(POS_KIND, posType);
				// insertMap.put(MERCHANT_NAME, row.getCell(3)
				// .getStringCellValue());
				// insertMap.put(TRADE_DATE, DateUtil.parDate(
				// DateUtil.sdf1, DateUtil.sdf9, row.getCell(4)
				// .getStringCellValue()));
				// insertMap.put(TRADE_AMT, row.getCell(5)
				// .getStringCellValue());
				// insertList.add(insertMap);
				// if(insertList.size() == batchNum){
				// loanPosCreditApplyService.insertPosSerialSelectiveMapBatch(insertList);
				// insertList.clear();
				// }
				//
				// }
				// }

				POSRowReader reader = new KQDRowReader(loanId, posType);
				reader.addHandleList(insertList);
				reader.addService(loanPosCreditApplyService);
				reader.addUniqueKey(uniquePOSKey);
				ExcelReaderUtil.readExcel(reader, file.getAbsolutePath());
				
				if(reader.fireTrigger()) return false;

				if (ListUtil.isNotEmpty(insertList)) {
					loanPosCreditApplyService
							.insertPosSerialSelectiveMapBatch(insertList);
					insertList.clear();
					uniquePOSKey.clear();
				}
				return true;
			}
			// 易宝日明细
			case BusinessDictionaryConstants.BIZ_CHANNEL_YB
					+ BusinessDictionaryConstants.POS_TYPE_DAY: {
				List<Map<String, Object>> insertList = Lists.newArrayList();
				// for (Row row : sheet1) {
				// if (row.getRowNum() != 0) {
				// Map<String, Object> insertMap = Maps.newHashMap();
				// insertMap.put(LOAN_ID, loanId);
				// insertMap.put(POS_CHANNEL, posChannel);
				// insertMap.put(POS_KIND, posType);
				// insertMap.put(MERCHANT_NAME, row.getCell(0)
				// .getStringCellValue());
				// insertMap.put(MERCHANT_ID, row.getCell(1)
				// .getStringCellValue());
				// insertMap.put(TRADE_DATE, row.getCell(2)
				// .getStringCellValue().trim().substring(0, 10));
				// insertMap.put(TRADE_TIME, row.getCell(2)
				// .getStringCellValue().trim().substring(12));
				// insertMap.put(TRADE_AMT, row.getCell(3)
				// .getStringCellValue());
				// insertMap.put(TRADE_LAST_FOUR_STATE, row.getCell(4)
				// .getStringCellValue());
				// insertMap.put(TRADE_CARD_KIND, row.getCell(5)
				// .getStringCellValue());
				// insertMap.put(TRADE_TYPE, row.getCell(6)
				// .getStringCellValue());
				// insertList.add(insertMap);
				// if(insertList.size() == batchNum){
				// loanPosCreditApplyService.insertPosSerialSelectiveMapBatch(insertList);
				// insertList.clear();
				// }
				//
				// }
				// }

				POSRowReader reader = new YBDRowReader(loanId, posType);
				reader.addHandleList(insertList);
				reader.addService(loanPosCreditApplyService);
				reader.addUniqueKey(uniquePOSKey);
				ExcelReaderUtil.readExcel(reader, file.getAbsolutePath());
				
				if(reader.fireTrigger()) return false;

				if (ListUtil.isNotEmpty(insertList)) {
					loanPosCreditApplyService
							.insertPosSerialSelectiveMapBatch(insertList);
					insertList.clear();
					uniquePOSKey.clear();
				}
				return true;
			}
			// 银联日明细
			case BusinessDictionaryConstants.BIZ_CHANNEL_UP
					+ BusinessDictionaryConstants.POS_TYPE_DAY: {
				List<Map<String, Object>> insertList = Lists.newArrayList();
				// for (Row row : sheet1) {
				// if (row.getRowNum() != 0) {
				// Map<String, Object> insertMap = Maps.newHashMap();
				// insertMap.put(LOAN_ID, loanId);
				// insertMap.put(POS_CHANNEL, posChannel);
				// insertMap.put(POS_KIND, posType);
				// insertMap.put(MERCHANT_NAME, row.getCell(0)
				// .getStringCellValue());
				// insertMap.put(MERCHANT_ID, row.getCell(1)
				// .getStringCellValue());
				// insertMap.put(MERCHANT_TYPE_CODE, row.getCell(2)
				// .getStringCellValue());
				// insertMap.put(TRADE_DATE, DateUtil.parDate(
				// DateUtil.sdf1, DateUtil.sdf9,
				// row.getCell(3).getStringCellValue().trim()
				// .substring(0, 8)));
				// insertMap.put(TRADE_TIME, DateUtil.parDate(
				// DateUtil.sdf8, DateUtil.sdf7,
				// row.getCell(3).getStringCellValue().trim()
				// .substring(8)));
				// insertMap.put(TRADE_AMT, row.getCell(4)
				// .getStringCellValue());
				// insertMap.put(TRADE_CARD_NO, row.getCell(5)
				// .getStringCellValue());
				// insertMap.put(TRADE_CARD_KIND, row.getCell(6)
				// .getStringCellValue());
				// insertMap.put(TRADE_TYPE, row.getCell(7)
				// .getStringCellValue());
				// insertList.add(insertMap);
				// if(insertList.size() == batchNum){
				// loanPosCreditApplyService.insertPosSerialSelectiveMapBatch(insertList);
				// insertList.clear();
				// }
				//
				// }
				// }

				POSRowReader reader = new UPDRowReader(loanId, posType);
				reader.addHandleList(insertList);
				reader.addService(loanPosCreditApplyService);
				reader.addUniqueKey(uniquePOSKey);
				ExcelReaderUtil.readExcel(reader, file.getAbsolutePath());
				
				if(reader.fireTrigger()) return false;

				if (ListUtil.isNotEmpty(insertList)) {
					loanPosCreditApplyService
							.insertPosSerialSelectiveMapBatch(insertList);
					insertList.clear();
					uniquePOSKey.clear();
				}
				return true;
			}

			// 银联周汇总
			case BusinessDictionaryConstants.BIZ_CHANNEL_UP
					+ BusinessDictionaryConstants.POS_TYPE_WEEK_COLLECT:
				// 银联月汇总
			case BusinessDictionaryConstants.BIZ_CHANNEL_UP
					+ BusinessDictionaryConstants.POS_TYPE_MONTH_COLLECT:
				// 银联日汇总
			case BusinessDictionaryConstants.BIZ_CHANNEL_UP
					+ BusinessDictionaryConstants.POS_TYPE_DAY_COLLECT: {
				List<Map<String, Object>> insertList = Lists.newArrayList();
				// for (Row row : sheet1) {
				// if (row.getRowNum() != 0) {
				// Map<String, Object> insertMap = Maps.newHashMap();
				// insertMap.put(LOAN_ID, loanId);
				// insertMap.put(POS_CHANNEL, posChannel);
				// insertMap.put(POS_KIND, posType);
				// insertMap.put(TRADE_DATE, row.getCell(0)
				// .getStringCellValue());
				// insertMap.put(MERCHANT_NAME, row.getCell(1)
				// .getStringCellValue());
				// insertMap.put(MERCHANT_ID, row.getCell(2)
				// .getStringCellValue());
				// insertMap.put(TRADE_NUM, row.getCell(3)
				// .getStringCellValue());
				// insertMap.put(TRADE_AMT, row.getCell(4)
				// .getStringCellValue());
				// insertList.add(insertMap);
				// if(insertList.size() == batchNum){
				// loanPosCreditApplyService.insertPosSerialSelectiveMapBatch(insertList);
				// insertList.clear();
				// }
				//
				// }
				// }

				POSRowReader reader = new UPMRowReader(loanId, posType);
				reader.addHandleList(insertList);
				reader.addService(loanPosCreditApplyService);
				reader.addUniqueKey(uniquePOSKey);
				ExcelReaderUtil.readExcel(reader, file.getAbsolutePath());
				
				if(reader.fireTrigger()) return false;

				if (ListUtil.isNotEmpty(insertList)) {
					loanPosCreditApplyService
							.insertPosSerialSelectiveMapBatch(insertList);
					insertList.clear();
					uniquePOSKey.clear();
				}
				return true;
			}
			// 银商月汇总
			case BusinessDictionaryConstants.BIZ_CHANNEL_UM
					+ BusinessDictionaryConstants.POS_TYPE_MONTH_COLLECT: {
				List<Map<String, Object>> insertList = Lists.newArrayList();
				// for (Row row : sheet1) {
				// if (row.getRowNum() != 0) {
				// Map<String, Object> insertMap = Maps.newHashMap();
				// insertMap.put(LOAN_ID, loanId);
				// insertMap.put(POS_CHANNEL, posChannel);
				// insertMap.put(POS_KIND, posType);
				// insertMap.put(TRADE_AMT, row.getCell(6)
				// .getStringCellValue());
				// insertMap.put(TRADE_NUM, row.getCell(7)
				// .getStringCellValue());
				// insertMap.put(REG_TIME_TRADE_AMT, row.getCell(8)
				// .getStringCellValue());
				// insertMap.put(REG_TIME_TRADE_NUM, row.getCell(9)
				// .getStringCellValue());
				// insertMap.put(MAX_TRADE_AMT_PER_MONTH, row.getCell(10)
				// .getStringCellValue());
				// insertMap.put(CREDIT_CARD_TRADE_AMT_RADE, row.getCell(11)
				// .getStringCellValue());
				// insertMap.put(CREDIT_CARD_TRADE_NUM_RATE, row.getCell(12)
				// .getStringCellValue());
				// insertMap.put(DIFFERENT_CARD_NO_NUM, row.getCell(13)
				// .getStringCellValue());
				// insertList.add(insertMap);
				// if(insertList.size() == batchNum){
				// loanPosCreditApplyService.insertPosSerialSelectiveMapBatch(insertList);
				// insertList.clear();
				// }
				//
				// }
				// }

				POSRowReader reader = new UMMRowReader(loanId, posType);
				reader.addHandleList(insertList);
				reader.addService(loanPosCreditApplyService);
				reader.addUniqueKey(uniquePOSKey);
				ExcelReaderUtil.readExcel(reader, file.getAbsolutePath());
				
				if(reader.fireTrigger()) return false;

				if (ListUtil.isNotEmpty(insertList)) {
					loanPosCreditApplyService
							.insertPosSerialSelectiveMapBatch(insertList);
					insertList.clear();
					uniquePOSKey.clear();
				}
				return true;
			}
			// 四川烟草日明细
			case BusinessDictionaryConstants.BIZ_CHANNEL_CY
					+ BusinessDictionaryConstants.POS_TYPE_DAY: {

				List<Map<String, Object>> insertList = Lists.newArrayList();

				POSRowReader reader = new CYRowReader(loanId, posType);
				reader.addHandleList(insertList);
				reader.addService(loanPosCreditApplyService);
				reader.addUniqueKey(uniquePOSKey);
				ExcelReaderUtil.readExcel(reader, file.getAbsolutePath());
				
				if(reader.fireTrigger()) return false;

				if (ListUtil.isNotEmpty(insertList)) {
					loanPosCreditApplyService
							.insertPosSerialSelectiveMapBatch(insertList);
					insertList.clear();
					uniquePOSKey.clear();
				}
				return true;
			}
			default:
				return false;
			}
		} catch (Exception e) {
			logger.error("上传pos流水异常:[]", e.getMessage());
			return false;
		}

	}

	@Override
	public List<Map<String, Object>> getCreditApplyDetail(String loanId) {
		Map<String, Object> creMap = loanPosCreditApplyService
				.getOneCreditApply(Objects.firstNonNull(loanId, ""));
		Map<String, Object> custMap = loanPosCustomerService
				.selectOneCustomer(Objects.firstNonNull(
						(String) creMap.get(CUST_ID), ""));
		if (custMap.get(mateBirtDate) != null) {
			custMap.put(mateBirtDate,
					DateUtil.getDateToString3((Date) custMap.get(mateBirtDate)));
			String provText = loanPosBusinessCodeService.getItemNameByNo(
					"AdminDivision", (String) custMap.get("residentProv"));
			String cityText = loanPosBusinessCodeService.getItemNameByNo(
					"AdminDivision", (String) custMap.get("residentCity"));
			String divisionText = loanPosBusinessCodeService.getItemNameByNo(
					"AdminDivision", (String) custMap.get("residentDivision"));
			custMap.put("region", provText + cityText + divisionText);
		}
		if (custMap.get(birtDate) != null) {
			custMap.put(birtDate,
					DateUtil.getDateToString3((Date) custMap.get(birtDate)));
		}
		Map<String, Object> relMap = loanPosCustomerService
				.getOneRelativeInfoById((String) creMap.get(RELA_ID));
		Map<String, Object> posCustMap = loanPosCreditApplyService
				.selectOnePosCustMapById((String) creMap.get(POS_CUST_ID));
		if (!StringUtil.isEmpty((String) posCustMap.get(operAddrCode))) {
			posCustMap.put(posCustProv,
					((String) posCustMap.get(operAddrCode)).substring(0, 2)
							+ "0000");
			posCustMap.put(posCustCity,
					((String) posCustMap.get(operAddrCode)).substring(0, 4)
							+ "00");
		}
		Map<String, Object> bankCardMap = loanPosCreditApplyService
				.selectOneBankAcc((String) creMap.get(BANK_ACC_NO));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> resList = Lists.newArrayList(creMap, custMap,
				relMap, posCustMap, bankCardMap);
		return resList;
	}

	@Override
	public JSONObject getPosSerialDetail(String loanId, Integer pageNo,
			Integer pageSize) {
		Map<String, Object> reqMap = Maps.newHashMap(); 
		reqMap.put(LOAN_ID, loanId);
		reqMap.put("startPage", (pageNo - 1) * pageSize);
		reqMap.put("limit", pageSize);
		List<Map<String, Object>> resList = loanPosCreditApplyService
				.selectPosSerialMap(reqMap);
		for (Map<String, Object> map : resList) {
			map.put(POS_KIND, loanPosBusinessCodeService.getItemNameByNo(
					BusinessDictionaryConstants.POSType,
					(String) map.get(POS_KIND)));
			map.put(POS_CHANNEL, loanPosBusinessCodeService.getItemNameByNo(
					BusinessDictionaryConstants.BizChannel,
					(String) map.get(POS_CHANNEL)));
		}
		Long num = loanPosCreditApplyService.countPosSerialNum(reqMap);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ROWS, resList);
		jsonObject.put(TOTAL, num);

		return jsonObject;
	}

	@Override
	public JSONObject getBankSerialDetail(String loanId, Integer pageNo,
			Integer pageSize) {
		Map<String, Object> reqMap = Maps.newHashMap();
		reqMap.put("startPage", (pageNo - 1) * pageSize);
		reqMap.put("limit", pageSize);
		reqMap.put(LOAN_ID, loanId);
		List<Map<String, Object>> resList = loanPosCreditApplyService
				.selectBankSerialMap(reqMap);
		Long num = loanPosCreditApplyService.countBankSerialNum(reqMap);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(ROWS, resList);
		jsonObject.put(TOTAL, num);
		return jsonObject;
	}

	@Override
	public void modifyCreditApplyInfo(Map<String, Object> creMap,
			Map<String, Object> custMap, Map<String, Object> posCustMap,
			Map<String, Object> bankMap, Map<String, Object> relMap,
			List<Map<String, Object>> bankSerList) {

		String custId = null;
		if (custMap != null) {
			custId = (String) custMap.get(CUST_ID);
			loanPosCustomerService.updateCustomerInfoMap(custMap);
		}
		if (bankMap != null && !bankMap.isEmpty()) {
			// 需要考虑变更银行卡的情况下并不是对原卡的更新 comment by Lin,Zhaolin
			// 如果不存在这张银行卡号
			if (loanPosCreditApplyService.selectOneBankAcc(
					(String) bankMap.get(BANK_ACC_NO)).isEmpty()) {
				bankMap.put(CUST_ID, custId);
				bankMap.put(createTime, new Date());
				loanPosCreditApplyService.insertBankAccSelectiveMap(bankMap);
			} else {
				loanPosCustomerService.updateBankAccno(bankMap);
			}
		}
		if (posCustMap != null) {
			loanPosCustomerService.updatePosCustInfo(posCustMap);
		}
		if (relMap != null) {
			// 需要考虑初次录入时没有亲属，修改时补充亲属的情况 comment by Lin,Zhaolin
			String custName = (String) relMap.get(RELA_CUST_NAME);
			if (custName != null && custName.trim().length() > 0) {
				if (loanPosCustomerService.getOneRelativeInfo(custName, custId)
						.isEmpty()) { // 如果没有亲属信息,则添加
					relMap.put(CUST_ID, custId);
					String relaId = IdUtil.getId(RELATIVE_PRFIX);
					relMap.put(RELA_ID, relaId);
					creMap.put(RELA_ID, relaId);
					loanPosCustomerService.insertRelativeInfo(relMap);
				} else {
					loanPosCustomerService.updateRelativeInfo(relMap);
				}
			}

		}
		// 如果这张银行卡存在
		if (ListUtil.isNotEmpty(bankSerList)) {
			// 查询银行流水条件
			Map<String, Object> reqMap = Maps.newHashMap(); 
			for (Map<String, Object> updateMap : bankSerList) {
				// 判断当前月份银行流水是否存在，如果存在则更新，否则新增流水
				reqMap.put("currMonth", updateMap.get("currMonth"));
				reqMap.put("loanId", creMap.get("loanId"));
				updateMap.put(LOAN_ID, creMap.get("loanId"));
				updateMap.put(CUST_ID, custId);
				updateMap.put(CUST_NAME, custMap.get(CUST_NAME));
				List<Map<String, Object>> bankSers = loanPosCreditApplyService
						.selectMapByCurrMonth(reqMap);
				// 更新银行流水
				if (bankSers != null && bankSers.size() > 0) {
					for (int i = 0; i < bankSers.size(); i++) {
						updateMap.put("serialNo",
								bankSers.get(i).get("serialNo"));
						loanPosCreditApplyService
								.updateBankSerialMap(updateMap);
					}
				} else {
					// 插入银行流水
					loanPosCreditApplyService.insertBankSerialMap(updateMap);
				}
			}
		}
		if (creMap != null) {
			int flag = loanPosCreditApplyService.updateCreditApplyMap(creMap);
			//提交成功
			if (flag == 1){
				if (creMap.get("applyStatus") != null && 
						ReviewNoteConstants.APPLYSTATUS_CODE_20.equals(creMap.get("applyStatus").toString())){
					TCreditApplyAprvInfo caai = new TCreditApplyAprvInfo();
					caai.setLoanId((String)creMap.get("loanId"));
					caai.setApprState((String)creMap.get("applyStatus"));
					//自动分配任务给资料审核人员
					//需要的时候，放开注释
					//creditApplyAprvInfoService.automaticAssignmentTask(caai);
				}
			}
		}
	}

	@Override
	public TCreditApply queryCreditApplyDetail(String loanId) {
		// 查看该笔申请的详细信息
		return loanPosCreditApplyService.queryCreditApply(loanId);
	}


	@Override
	public boolean updloadPosSerial(String posChannel, String posType,
			@SuppressWarnings("unchecked") List<Map<String, Object>> posMap) {
		try {
			String channelBiz = posChannel + posType;
			logger.debug("posType:"+posType+" posChannel:"+posChannel);
			switch (channelBiz) {
			// 银商月汇总
			case BusinessDictionaryConstants.BIZ_CHANNEL_UM
					+ BusinessDictionaryConstants.POS_TYPE_MONTH_COLLECT: {
				logger.debug("银商月汇总");
				for (Map<String, Object> insertMap : posMap) {

					insertMap.put(POS_CHANNEL, posChannel);
					insertMap.put(POS_KIND, posType);
					loanPosCreditApplyService
							.insertPosSerialSelectiveMap(insertMap);

				}
				return true;
			}
			default:
				return false;
			}
		} catch (Exception e) {
			logger.error("上传pos流水异常:[]", e.getMessage());
			return false;
		}

	}

	@Override
	public List<Map<String, Object>> getCreditApplyDetailByStdshno(
			String stdshno, String channel) {
		String loanId = loanPosCreditApplyService
				.selectLoanId(stdshno, channel);
		return getCreditApplyDetail(loanId);

	}

	/**
	 * 自助App查询申请明细信息及申请状态 查询.
	 * 
	 * @param loanId
	 * @param custId
	 * @param paperKind
	 * @param paperId
	 * @param custName
	 * @param beginDate
	 * @param endDate
	 * @return
	 */	
    public List<Map<String, Object>> queryCreditApplyDetailByZzApp(String loanId, String custId,
                                                                   String paperKind,
                                                                   String paperId, String custName,
                                                                   String beginDate, String endDate) {
    	Boolean flag = false;
        Map<String, Object> queryMap = Maps.newHashMap();
        try {
            // 拼装查询依据
            if (!StringUtil.isEmpty(loanId)) {
                queryMap.put("loanId", loanId);
            }
            if (!StringUtil.isEmpty(custId)) {
                queryMap.put("custId", custId);
            }
            if (!StringUtil.isEmpty(paperId)) {
                queryMap.put("paperKind", paperKind);
                queryMap.put("paperId", paperId);
            }
            if (!StringUtil.isEmpty(beginDate)) {
                queryMap.put("beginDate", new Timestamp(DateUtil.getTimeStamp(beginDate)));
            }
            if (!StringUtil.isEmpty(endDate)) {
                queryMap.put("endDate", new Timestamp(DateUtil.getTimeStamp(endDate)));
            }

            // 开启查询
            List<Map<String, Object>> returnList = loanPosCreditApplyService
                .queryListByZzApp(queryMap);
            if (!returnList.isEmpty()) {
                // 批复管理
                for (Map<String, Object> map : returnList) {
                    // 补件
                    if (ReviewNoteConstants.APPLYSTATUS_CODE_21.equals(map.get("apprstate"))
                        || ReviewNoteConstants.APPLYSTATUS_CODE_32.equals(map.get("apprstate"))) {
                        TCreditApplyAprvInfo tCreditApplyAprvInfoKey = new TCreditApplyAprvInfo();
                        tCreditApplyAprvInfoKey.setLoanId((String) map.get("loanid"));
                        tCreditApplyAprvInfoKey
                            .setApprResult(ReviewNoteConstants.APPRRESULT_CODE_31);
                        TCreditApplyAprvInfo tCreditApplyAprvInfo = tCreditApplyAprvInfoDao
                            .selectLastOne(tCreditApplyAprvInfoKey);
                        if (tCreditApplyAprvInfo != null) {
                            map.put("additspec", tCreditApplyAprvInfo.getNeedReason());
                            map.put("imgadditdetail", tCreditApplyAprvInfo.getNeedInforCodes());
                        }
                        
                        // 加入倒计时
                        map.put("loancountdown", calculatingTimeDiff((String)map.get("begindate"), "D", 14));
                        
                        // 拒绝
                    } else if (ReviewNoteConstants.APPLYSTATUS_CODE_92.equals(map.get("apprstate"))) {
                        TCreditApplyAprvInfo tCreditApplyAprvInfoKey = new TCreditApplyAprvInfo();
                        tCreditApplyAprvInfoKey.setLoanId((String) map.get("loanid"));
                        tCreditApplyAprvInfoKey
                            .setApprResult(ReviewNoteConstants.APPRRESULT_CODE_20);
                        TCreditApplyAprvInfo tCreditApplyAprvInfo = tCreditApplyAprvInfoDao
                            .selectLastOne(tCreditApplyAprvInfoKey);
                        if (tCreditApplyAprvInfo != null) {
                        	//20150728 guoyu 审批意见外部的内容从编号转变为内容
                        	//map.put("refusereason", tCreditApplyAprvInfo.getApprInfoExt());
                            map.put("refusereason",businessDictionaryService.getApprInfoExtMsg(tCreditApplyAprvInfo.getApprInfoExt()));
                        }
                        
                        // 加入倒计时
                        map.put("loancountdown", calculatingTimeDiff((String)map.get("begindate"), "M", 3));
                        // 通过
                    } else if (ReviewNoteConstants.APPLYSTATUS_CODE_90.equals(map.get("apprstate"))) {
                        TApproveResult tApproveResult = tApproveResultDao
                            .selectByLoanId((String) map.get("loanid"));
                        if (tApproveResult != null) {
                            map.put("appmaxcred", tApproveResult.getApproveAmount()); // 授信额度
                            map.put("apptterm", tApproveResult.getApproveTerm()); // 授信期限
                            map.put("interate", tApproveResult.getApproveInterest()); // 贷款利率
                            map.put(
                                "appenddate",
                                tApproveResult.getExpiryDate() == null ? "" : DateFormatUtils
                                    .format(tApproveResult.getExpiryDate(), "yyyyMMdd")); // 授信期限
                        }
                        TContractManagement tContractInfo = tContractInfoDao
                            .selectContractInfoByLoanId((String) map.get("loanid"));
                        if (tContractInfo != null
                            && "05|09".indexOf(tContractInfo.getAgreementStatus()) < 0) {
                            map.put("apprstate", "191"); // 暂放191, 已签约
                            Calendar calNow = Calendar.getInstance();
                            logger.debug("现在时间为:" + calNow.getTime());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(tContractInfo.getEndDate());
                            cal.add(Calendar.MONTH, -1);
                            logger.debug("合同期限前一个月时间为：" + cal.getTime());
                            Calendar calEnd = Calendar.getInstance();
                            logger.debug("合同期限为:" + calEnd.getTime());
                            calEnd.setTime(tContractInfo.getEndDate());
                            //再协议生效的情况下才能续贷
                            if(tContractInfo.getAgreementStatus() != null && "06".equals(tContractInfo.getAgreementStatus())){
                            	logger.debug(loanId + "可以续贷");
                            	if(calNow.compareTo(cal) >= 0 && calNow.compareTo(calEnd) < 0){
                            		flag = true;
                            	}
                            }
                        }
                        
                        // 加入倒计时
                        map.put("loancountdown", calculatingTimeDiff((String)map.get("begindate"), "D", 7));
                    } else if ("10|20|30|31|33|34".indexOf((String)map.get("apprstate")) > -1) {
                        // 加入倒计时
                        map.put("loancountdown", calculatingTimeDiff((String)map.get("begindate"), "D", 2));
                    }
                    if(flag){
                    	map.put("reloanflag", "Y"); // 可以续贷
                    }else{
                    	map.put("reloanflag", "N");	// 不可续贷
                    }
                }
            }
            return returnList;
        } catch (Exception e) {
            logger.error("自助App查询申请明细信息及申请状态 异常:[]", e.getMessage());
            return null;
        }
    }
    
    /**
     * 
     * 
     * @param beginDate
     * @param field
     * @param amount
     * @return
     * @throws Exception 
     */
    private long calculatingTimeDiff(String beginDate, String field, int amount) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.getDatePatternA(beginDate));
        
        if ("M".equals(field)) {
            calendar.add(Calendar.MONTH, amount);
        } else if ("D".equals(field)) {
            calendar.add(Calendar.DAY_OF_YEAR, amount);
        } else if ("Y".equals(field)) {
            calendar.add(Calendar.YEAR, amount);
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, amount);
        }
        
        Date endDate = calendar.getTime();
        
        if (endDate == null) {
            return 0;
        }
        
        if (endDate.getTime() <= System.currentTimeMillis()) {
            return 0;
        }
        
        return endDate.getTime() - System.currentTimeMillis();
    }


	public boolean hasCreditInvestigate(Map<String, Object> reqMap) {
		String beforeDate = SysConfigFactory.getInstance().getService("CRService").getPropertyValue("beforeDate");
		// 查询20天内是否有征信报告
		String queryTime = DateUtil.getSomeDateBefor(Integer
				.valueOf(beforeDate));
		List<Map<String, Object>> rList = creditInvestigateService
				.selectBriefByMap(reqMap);
		if (ListUtil.isNotEmpty(rList)) {
			return true;
		} else {
			return false;
		}
	}

	public Map<String, Object> creditInvestigate(Map<String, Object> rMap) {
		
		ConfigService cfgService = SysConfigFactory.getInstance().getService("CRService");	//征信查询服务
		
		File deleteFile = null;

		Map<String, Object> resMap = Maps.newHashMap();
		if ("on".equals(cfgService.getPropertyValue("ciOff"))) {
			resMap.put("respCode", "00");
			resMap.put("respMsg", "征信成功");
			return resMap;
		}
		int flag = 0;
		//为修改征信查询是否完整状态
		String reportNo = null;
		try {

			// 根据返回的文件路径从ftp下载

			// 解压下载的ftp文件

			// 解析JSON
			String jsonStr = "";
			// 暂时
			/*
			 * IOCVUtil.convert(
			 * "E:/EMAIL/CRQClient/CRQClient/CRQClient/src/demo/testICR.txt",
			 * "GB2312", "UTF-8", new FilenameFilter() {
			 * 
			 * @Override public boolean accept(File dir, String name) { return
			 * name.endsWith("txt"); } });
			 */
			CreditInvestigateRequest request = (CreditInvestigateRequest) rMap
					.get("creditInvestigateRequest");
			request.setReqDt(DateUtil.getTodayDatePattern1());
			request.setReqTm(DateUtil.getCurrentTimePattern8());
			request.setReqSeqNo(IdUtil.getCreditInvestigateSeqNo());
			request.setTrmNo("IFS");
			request.setMsgType("ICR");
			// 用户名外部传入
			// request.setCustNme("吴大力");
			request.setCertType("0");
			// 身份证号码外部传入
			// request.setCustId("165534197201131114");
			// 查询原因外部传入
			// request.setQryWay("01I");
			// 先查本地再查人行,要改成3
			request.setAgtBizCd("3");
			// 报告有效期
			//先查询出该征信报告
			Map<String, Object> queryBriefMap = Maps.newHashMap();
			queryBriefMap.put(ReviewNoteConstants.CREDIT_REPORT_MAP_KEY_CERTNO, request.getCustId());
			queryBriefMap.put(ReviewNoteConstants.CREDIT_REPORT_MAP_KEY_CERTTYPE,
					ReviewNoteConstants.CREDIT_REPORT_MAP_VALUE_CERTTYPE_0);
			TCreditReportBrief crf = tCreditReportBriefDao
					.selectOneByCertNo(queryBriefMap);
			logger.debug("查询结果为:" + crf);
			//如果结果为空或者时间大于20天，则传20
			if(crf == null || DateUtil.getDateDiffFromToday(crf.getCommitTime()) > 20){
				logger.debug(request.getCustNme() + "没有该用户的征信报告或者征信报告已经过了20天");
				request.setRiskValidDt("20");
			}else{
				logger.debug(request.getCustNme() + "已查过征信报告，且征信报告的时间小于20天");
				request.setRiskValidDt("3");
			}
			// 原始报告+结构化数据
			request.setQryAplFlag("3");
			request.setQryBrchNo("IFS");
			request.setBrchId(cfgService.getPropertyValue("orgId"));
			//工厂方式依次使用查询账号
			ICRQuid crquid = CRQuidFactory.getInstance().getCRQuid();
			if(crquid.isNull()){
				Map<String, Object> errResp = Maps.newHashMap();
				errResp.put("respCode", "09");
				errResp.put("respMsg", "查询次数查出限定.");
				return errResp;
			}
			request.setUsrNo(crquid.getId());
//			request.setUsrNo(cfgService.getPropertyValue("uid"));
			
			request.setCustFlg("Y");
			request.setQryStartTm(DateUtil.getCurrTimeSdf10());
			request.setAddrDes(cfgService.getPropertyValue("investigateUrl"));
			request.setChnlNo("IFS");


			HConnector hConnector = new HConnector();
			hConnector.setPattern(cfgService.getPropertyValue("creditInvestigatePat"));
			hConnector.setPort(Integer.valueOf(cfgService.getPropertyValue("investigatePort")));
			hConnector.setUrl(cfgService.getPropertyValue("investigateUrl"));

			CreditInvestigateImpl creditInvestigateImpl = new CreditInvestigateImpl();
			creditInvestigateImpl.sethConnector(hConnector);
			CreditInvestigateResponse response = creditInvestigateImpl.investigate(request);

			ParamReqBean bean = new ParamReqBean();
			bean.setVersion("1.0.0");
			bean.setBizType("0001");
			bean.setTransType("0003");
			bean.setKeyValue("InstNo", "ZX");
			bean.setKeyValue("Prefix", "ZX");
			bean.setKeyValue("LocalSubFolderNameDef", request.getCustId()+"_"+DateUtil.getCurrentTimePatterna());
			logger.debug("addrPort:"+response.getAddrPort() +", port:"+ response.getPort());
			if (StringUtil.isEmpty(response.getAddrPort())
					|| StringUtil.isEmpty(response.getPort())) {
				logger.error("查询征信报告失败,code:"+response.getFaultCode()+"message:"+response.getMessage());
				resMap.put("respCode", "01");
				resMap.put("respMsg", "查询征信系统失败");
				return resMap;
			}
			bean.setKeyValue("RemoteFileName", response.getPort() + "|"
					+ response.getAddrPort());
			ParamResBean resBean = HttpClient.send(bean);
			if (resBean == null
					|| !StringUtils.equals("000", resBean.getRespCode())) {
				logger.error("下载征信报告失败");
				resMap.put("respCode", "01");
				resMap.put("respMsg", "下载征信报告失败");
				return resMap;
			}
			logger.debug("下载结果txtPath:" + resBean.getTextPaths()[0]+
					"filepath:" + resBean.getFilePaths()[0]);
			
			 String txtPath = resBean.getTextPaths()[0]; 
			 String htmlPath = resBean.getFilePaths()[0];
			 String getInvestigateUrl = SysConfigFactory.getInstance().getService("CRService").getPropertyValue("getInvestigateUrl");
			 String destFileFolder = System.getProperty("java.io.tmpdir") + "/txtFolder";
			 if(!new File(destFileFolder).exists()){
				 new File(destFileFolder).mkdir();
			 }
			 String destFileName = System.getProperty("java.io.tmpdir") + "/txtFolder/" + txtPath.substring(txtPath.lastIndexOf("/") + 1, txtPath.length());
			 //下载征信报告格式化文件
			if(!new File(destFileName).exists()){
				if(!HttpUtil.getHttpFile(txtPath == null ? "" : getInvestigateUrl + txtPath.substring(txtPath.indexOf("/imagedatawork")), destFileName)){
					logger.info("下载征信txt文件失败,不做接下来操作");
					resMap.put("respCode", "01");
					resMap.put("respMsg", "查询征信系统失败");
					return resMap;
			} 
					 
			 }
			deleteFile = new File(destFileName);
			StringBuffer sb = new StringBuffer();
			List<String> strList = Files.readLines(new File(destFileName), Charset.forName("GBK"));
			for(String str : strList){
				sb.append(str);
			}
			jsonStr = sb.toString();
			//logger.debug("征信json:" + jsonStr);
			// System.out.println(jsonStr);
			CreditReport creditReport = CreditReportManager
					.convertJson2CreditReport(jsonStr);
			String queryId = IdUtil.getQueryId();
			// System.out.println("creditReport"+creditReport.getReportData().getPartByName("Header").getLabel());
			// Header
			ReportPart headerInfo = creditReport.getReportData().getPartByName(
					"MessageHeader");
			ReportPart queryReqInfo = creditReport.getReportData().getPartByName("QueryReq");
			
			List<BizObject> headerList = headerInfo.getContent();
			List<BizObject> queryReqList = queryReqInfo.getContent();
			try{
				if(ListUtil.isNotEmpty(headerList) && ListUtil.isNotEmpty(queryReqList) && headerList.get(0) != null && queryReqList.get(0) != null){
					BizObject headerBiz = headerList.get(0);
					BizObject queryReqBiz = queryReqList.get(0);
					
					Map<String, Object> reqMap = Maps.newHashMap();
					reqMap.put("queryId", queryId);
					reqMap.put(CreditInvestigateConstants.reportNo, headerBiz.getAttribute(CreditInvestigateConstants.ReportNo).getValue());
					reportNo = (String)headerBiz.getAttribute(
							CreditInvestigateConstants.ReportNo)
							.getValue();
					reqMap.put(
							CreditInvestigateConstants.queryTime,
							headerBiz.getAttribute(
									CreditInvestigateConstants.QueryTime)
									.getValue());
					reqMap.put(
							CreditInvestigateConstants.createTime,
							headerBiz
									.getAttribute(
											CreditInvestigateConstants.ReportCreateTime)
									.getValue());
					reqMap.put(CreditInvestigateConstants.name, queryReqBiz
							.getAttribute(CreditInvestigateConstants.Name)
							.getValue());
					
					reqMap.put(
							CreditInvestigateConstants.certType,
							queryReqBiz.getAttribute(
									CreditInvestigateConstants.Certtype)
									.getValue());
					reqMap.put(
							CreditInvestigateConstants.certNo,
							queryReqBiz.getAttribute(
									CreditInvestigateConstants.Certno)
									.getValue());
					reqMap.put(
							CreditInvestigateConstants.reason,
							queryReqBiz.getAttribute(
									CreditInvestigateConstants.QueryReason)
									.getValue());
					reqMap.put(
							CreditInvestigateConstants.userCode,
							queryReqBiz.getAttribute(
									CreditInvestigateConstants.UserCode)
									.getValue());
					reqMap.put(CreditInvestigateConstants.formatHtml,
							(StringUtil.isNotEmpty(htmlPath) ? htmlPath.substring(htmlPath.indexOf("/imagedatawork")) : ""));
					creditInvestigateService
							.insertCreditReportBrief(reqMap);
				}
			}catch (Exception e) {
				logger.error("reportBrief error:" + e);
				flag = 1;
			}
			
			/*if (headerList != null) {
				for (BizObject bizObject : headerList) {
					try {
						Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
						reqMap.put(
								CreditInvestigateConstants.reportNo,
								bizObject.getAttribute(
										CreditInvestigateConstants.ReportNo)
										.getValue());
						reportNo = (String)bizObject.getAttribute(
								CreditInvestigateConstants.ReportNo)
								.getValue();
						reqMap.put(
								CreditInvestigateConstants.queryTime,
								bizObject.getAttribute(
										CreditInvestigateConstants.QueryTime)
										.getValue());
						reqMap.put(
								CreditInvestigateConstants.createTime,
								bizObject
										.getAttribute(
												CreditInvestigateConstants.ReportCreateTime)
										.getValue());
						reqMap.put(CreditInvestigateConstants.name, bizObject
								.getAttribute(CreditInvestigateConstants.Name)
								.getValue());
						reqMap.put(
								CreditInvestigateConstants.certType,
								bizObject.getAttribute(
										CreditInvestigateConstants.Certtype)
										.getValue());
						reqMap.put(
								CreditInvestigateConstants.certNo,
								bizObject.getAttribute(
										CreditInvestigateConstants.Certno)
										.getValue());
						reqMap.put(
								CreditInvestigateConstants.reason,
								bizObject.getAttribute(
										CreditInvestigateConstants.QueryReason)
										.getValue());
						reqMap.put(
								CreditInvestigateConstants.format,
								bizObject.getAttribute(
										CreditInvestigateConstants.QueryFormat)
										.getValue());
						reqMap.put(
								CreditInvestigateConstants.queryOrg,
								bizObject.getAttribute(
										CreditInvestigateConstants.QueryOrg)
										.getValue());
						reqMap.put(
								CreditInvestigateConstants.userCode,
								bizObject.getAttribute(
										CreditInvestigateConstants.UserCode)
										.getValue());
						reqMap.put(
								CreditInvestigateConstants.result,
								bizObject
										.getAttribute(
												CreditInvestigateConstants.QueryResultCue)
										.getValue());
						reqMap.put(CreditInvestigateConstants.formatHtml,
								(StringUtil.isNotEmpty(htmlPath) ? htmlPath.substring(htmlPath.indexOf("/imagedatawork")) : ""));
						creditInvestigateService
								.insertCreditReportBrief(reqMap);
					} catch (Exception e) {
						logger.error("reportBrief error:" + e);
						flag = 1;
					}
				}
			}*/

			ReportPart CreditDetailInfo = creditReport.getReportData()
					.getPartByName("CreditDetail");
			// System.out.println(PersonalInfo);
			ReportChapter asset = CreditDetailInfo
					.getChapterByName("AssetDisposition");
			if (asset != null) {
				List<BizObject> assetList = asset.getContent();
				if (assetList != null) {
					for (BizObject bizObject : assetList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.orgName,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Organname)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.receiveDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.GetTime)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.receiveAmt,
									bizObject.getAttribute(
											CreditInvestigateConstants.Money)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.latestRepayDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.LatestRepayDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.balance,
									bizObject.getAttribute(
											CreditInvestigateConstants.Balance)
											.getValue());
							creditInvestigateService
									.insertCreditReportAsset(reqMap);
						} catch (Exception e) {
							logger.error("reportAsset error:" + e);
							flag = 1;
						}
					}
				}
			}
			// 保证人代偿信息
			ReportChapter quid = CreditDetailInfo
					.getChapterByName("AssurerRepay");
			if (quid != null) {
				List<BizObject> quidList = quid.getContent();
				if (quidList != null) {
					for (BizObject bizObject : quidList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.orgName,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Organname)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.latestQuidDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.latestQuidDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.quidAmt,
									bizObject.getAttribute(
											CreditInvestigateConstants.Money)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.latestRepayDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.LatestRepayDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.balance,
									bizObject.getAttribute(
											CreditInvestigateConstants.Balance)
											.getValue());
							creditInvestigateService
									.insertCreditReportQuid(reqMap);
						} catch (Exception e) {
							logger.error("reportQuid error:" + e);
							flag = 1;
						}
					}
				}
			}
			// 贷款信息
			ReportChapter loan = CreditDetailInfo.getChapterByName("Loan");
			if (loan != null) {
				ReportSection loanInfo = loan.getSectionByName("loanInfo");
				List<BizObject> loanInfoList = loanInfo.getContent();
				if (loanInfoList != null) {
					for (BizObject bizObject : loanInfoList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.class5State,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Class5State)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.balance,
									bizObject.getAttribute(
											CreditInvestigateConstants.Balance)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.remainCycles,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.RemainPaymentCyc)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.scheduledAmt,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ScheduledPaymentAmount)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.scheduledDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ScheduledPaymentDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.actualAmt,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ActualPaymentAmount)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.recentPayDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.RecentPayDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.overdueCycle,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.CurrOverdueCyc)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.overdueAmt,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.CurrOverdueAmount)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.overdue31to60Amt,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Overdue31To60Amount)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.overdue61to90Amt,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Overdue61To90Amount)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.overdue91to180Amt,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Overdue91To180Amount)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.bizType,
									bizObject.getAttribute(
											CreditInvestigateConstants.bizType)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.note,
									bizObject.getAttribute(
											CreditInvestigateConstants.Cue)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.financeOrg,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.FinanceOrg)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.account,
									bizObject.getAttribute(
											CreditInvestigateConstants.Account)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.type,
									bizObject.getAttribute(
											CreditInvestigateConstants.Type)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.currency,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Currency)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.openDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.OpenDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.stateEndDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.StateEndDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.stateEndMonth,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.StateEndDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.beginMonth,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.BeginMonth)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.latest24State,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Latest24State)
											.getValue());
							reqMap.put(CreditInvestigateConstants.overdueOver180Amt, bizObject.getAttribute(CreditInvestigateConstants.OverdueOver180Amount).getValue());
							reqMap.put(CreditInvestigateConstants.endDate, bizObject.getAttribute(CreditInvestigateConstants.EndDate).getValue());
							reqMap.put(CreditInvestigateConstants.limitAmt, bizObject.getAttribute(CreditInvestigateConstants.CreditLimitAmount).getValue());
							reqMap.put(CreditInvestigateConstants.guarantyType, bizObject.getAttribute(CreditInvestigateConstants.GuaranteeType).getValue());
							reqMap.put(CreditInvestigateConstants.paymentFreq, bizObject.getAttribute(CreditInvestigateConstants.PaymentRating).getValue());
							reqMap.put(CreditInvestigateConstants.paymentCycle, bizObject.getAttribute(CreditInvestigateConstants.PaymentCyc).getValue());
							reqMap.put(CreditInvestigateConstants.state, bizObject.getAttribute(CreditInvestigateConstants.State).getValue());
							reqMap.put(CreditInvestigateConstants.badBalance, bizObject.getAttribute(CreditInvestigateConstants.BadBalance).getValue());
							reqMap.put(CreditInvestigateConstants.endMonth, bizObject.getAttribute(CreditInvestigateConstants.EndDate).getValue());
							reqMap.put(CreditInvestigateConstants.cueSerialNo, bizObject.getAttribute(CreditInvestigateConstants.CueSerialNo).getValue());
							reqMap.put(CreditInvestigateConstants.financeType, bizObject.getAttribute(CreditInvestigateConstants.FinanceType).getValue());
							creditInvestigateService
									.insertCreditReportLoanInfo(reqMap);
						} catch (Exception e) {
							logger.error("reportLoanInfo error:" + e);
							flag = 1;
						}
					}
				}
				// 最近五年内的贷款逾期记录
				ReportSection overDueSection = loan
						.getSectionByName("Latest5YearOverduesection_Loan");
				if(loan != null){
					List<BizObject> overDueList = overDueSection.getContent();
					if (null != overDueList) {
						for (BizObject bizObject : overDueList) {
							try {
								Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
								reqMap.put(
										CreditInvestigateConstants.reportNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ReportNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.account,
										bizObject.getAttribute(
												CreditInvestigateConstants.Account)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.beginMonth,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.BeginMonth)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.endMonth,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.EndMonth)
												.getValue());
								creditInvestigateService
										.insertCreditReportOverdue5Years(reqMap);
							} catch (Exception e) {
								logger.error("reportOverdue5Years error:" + e);
								flag = 1;
							}
						}
				}
				
				}
				// 贷款逾期记录明细
				ReportSection overDueDetailSection = loan
						.getSectionByName("Latest5YearOverdueDetail_Loan");
				if(overDueDetailSection != null){
					List<BizObject> overDueDetailList = overDueDetailSection
							.getContent();
					if (null != overDueDetailList) {
						for (BizObject bizObject : overDueDetailList) {
							try {
								Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
								reqMap.put(
										CreditInvestigateConstants.reportNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ReportNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.account,
										bizObject.getAttribute(
												CreditInvestigateConstants.Account)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.serialNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.SerialNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.month,
										bizObject.getAttribute(
												CreditInvestigateConstants.Month)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.lastMonths,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.LastMonths)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.amount,
										bizObject.getAttribute(
												CreditInvestigateConstants.Amount)
												.getValue());
								creditInvestigateService
										.insertCreditReportOverdueDetail(reqMap);
							} catch (Exception e) {
								logger.error("reportOverdueDetail error:" + e);
								flag = 1;
							}
						}
				}
				
				}
				// 贷款特殊信息
				ReportSection specialSection = loan
						.getSectionByName("SpecialTrade_Loan");
				if(specialSection != null){
					List<BizObject> specialList = specialSection.getContent();
					if (null != specialList) {
						for (BizObject bizObject : specialList) {
							try {
								Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
								reqMap.put(
										CreditInvestigateConstants.reportNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ReportNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.account,
										bizObject.getAttribute(
												CreditInvestigateConstants.Account)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.serialNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.SerialNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.type,
										bizObject.getAttribute(
												CreditInvestigateConstants.Type)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.occurDate,
										bizObject.getAttribute(
												CreditInvestigateConstants.GetTime)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.months,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ChangingMonths)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.amount,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ChangingAmount)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.content,
										bizObject.getAttribute(
												CreditInvestigateConstants.Content)
												.getValue());
								creditInvestigateService
										.insertCreditReportSpecial(reqMap);
							} catch (Exception e) {
								logger.error("reportSpecial error:" + e);
								flag = 1;
							}
						}
				}
				
				}
				// 贷款机构说明
				ReportSection institutionSection = loan
						.getSectionByName("BankIlluminate_Loan");
				if(institutionSection != null){
					List<BizObject> institutionList = institutionSection
							.getContent();
					if (institutionList != null) {
						for (BizObject bizObject : institutionList) {
							try {
								Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
								reqMap.put(
										CreditInvestigateConstants.reportNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ReportNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.account,
										bizObject.getAttribute(
												CreditInvestigateConstants.Account)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.serialNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.SerialNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.content,
										bizObject.getAttribute(
												CreditInvestigateConstants.Content)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.occurDate,
										bizObject.getAttribute(
												CreditInvestigateConstants.GetTime)
												.getValue());
								creditInvestigateService
										.insertCreditReportInstitution(reqMap);
							} catch (Exception e) {
								logger.error("reportInstitution error:" + e);
								flag = 1;
							}
						}
				}
				
				}
				// 贷款本人申明
				ReportSection announceSection = loan
						.getSectionByName("AnnounceInfo_Loan");
				if(announceSection != null){
					List<BizObject> announceList = announceSection.getContent();
					if (null != announceList) {
						for (BizObject bizObject : announceList) {
							try {
								Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
								reqMap.put(
										CreditInvestigateConstants.reportNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ReportNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.account,
										bizObject.getAttribute(
												CreditInvestigateConstants.Account)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.serialNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.SerialNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.content,
										bizObject.getAttribute(
												CreditInvestigateConstants.Content)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.occurDate,
										bizObject.getAttribute(
												CreditInvestigateConstants.GetTime)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.type,
										bizObject.getAttribute(
												CreditInvestigateConstants.Type)
												.getValue());
								creditInvestigateService
										.insertCreditReportAnnounce(reqMap);
							} catch (Exception e) {
								logger.error("reportAnnounce error:" + e);
								flag = 1;
							}
						}
				}
				
				}

				// 贷款异议标注
				ReportSection dissentSection = loan
						.getSectionByName("DissentInfo_Loan");
				if(dissentSection != null){
					List<BizObject> dissentList = dissentSection.getContent();
					if (null != dissentList) {
						for (BizObject bizObject : dissentList) {
							try {
								Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
								reqMap.put(
										CreditInvestigateConstants.reportNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ReportNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.account,
										bizObject.getAttribute(
												CreditInvestigateConstants.Account)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.serialNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.SerialNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.content,
										bizObject.getAttribute(
												CreditInvestigateConstants.Content)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.occurDate,
										bizObject.getAttribute(
												CreditInvestigateConstants.GetTime)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.type,
										bizObject.getAttribute(
												CreditInvestigateConstants.Type)
												.getValue());
								creditInvestigateService
										.insertCreditReportDissent(reqMap);
							} catch (Exception e) {
								logger.error("reportDissent error :" + e);
								flag = 1;
							}
						}
				}
				
				}

				//Loancard
				ReportChapter loanCard = CreditDetailInfo.getChapterByName("Loancard"); 
				if(loanCard != null){
					ReportSection loanCardInfo = loanCard.getSectionByName("LoancardInfo");
					if(loanCardInfo != null){
						List<BizObject> loanCardInfoList = loanCardInfo.getContent();
						if(loanCardInfoList != null){
							for(BizObject bizObject : loanCardInfoList){
								try{
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(CreditInvestigateConstants.shareLimit, bizObject.getAttribute(CreditInvestigateConstants.ShareCreditLimitAmount).getValue());
									reqMap.put(CreditInvestigateConstants.usedLimit, bizObject.getAttribute(CreditInvestigateConstants.UsedCreditLimitAmount).getValue());
									reqMap.put(CreditInvestigateConstants.usedAvgAmt6m, bizObject.getAttribute(CreditInvestigateConstants.Latest6MonthUsedAvgAmount).getValue());
									reqMap.put(CreditInvestigateConstants.usedAmtMax, bizObject.getAttribute(CreditInvestigateConstants.UsedHighestAmount).getValue());
									reqMap.put(CreditInvestigateConstants.scheduledAmt, bizObject.getAttribute(CreditInvestigateConstants.ScheduledPaymentAmount).getValue());
									reqMap.put(CreditInvestigateConstants.scheduledDate, bizObject.getAttribute(CreditInvestigateConstants.ScheduledPaymentDate).getValue());
									reqMap.put(CreditInvestigateConstants.actualAmt, bizObject.getAttribute(CreditInvestigateConstants.ActualPaymentAmount).getValue());
									reqMap.put(CreditInvestigateConstants.recentPayDate, bizObject.getAttribute(CreditInvestigateConstants.RecentPayDate).getValue());
									reqMap.put(CreditInvestigateConstants.overdueCycle, bizObject.getAttribute(CreditInvestigateConstants.CurrOverdueCyc).getValue());
									reqMap.put(CreditInvestigateConstants.overdueAmt, bizObject.getAttribute(CreditInvestigateConstants.CurrOverdueAmount).getValue());
									reqMap.put(CreditInvestigateConstants.reportNo, bizObject.getAttribute(CreditInvestigateConstants.ReportNo).getValue());
									reqMap.put(CreditInvestigateConstants.serialNo, bizObject.getAttribute(CreditInvestigateConstants.SerialNo).getValue());
									reqMap.put(CreditInvestigateConstants.bizType, bizObject.getAttribute(CreditInvestigateConstants.bizType).getValue());
									reqMap.put(CreditInvestigateConstants.account, bizObject.getAttribute(CreditInvestigateConstants.Account).getValue());
									reqMap.put(CreditInvestigateConstants.note, bizObject.getAttribute(CreditInvestigateConstants.Cue).getValue());
									reqMap.put(CreditInvestigateConstants.financeOrg, bizObject.getAttribute(CreditInvestigateConstants.FinanceOrg).getValue());
									reqMap.put(CreditInvestigateConstants.currency, bizObject.getAttribute(CreditInvestigateConstants.Currency).getValue());
									reqMap.put(CreditInvestigateConstants.openDate, bizObject.getAttribute(CreditInvestigateConstants.OpenDate).getValue());
									reqMap.put(CreditInvestigateConstants.limitAmt, bizObject.getAttribute(CreditInvestigateConstants.CreditLimitAmount).getValue());
									reqMap.put(CreditInvestigateConstants.guarantyType, bizObject.getAttribute(CreditInvestigateConstants.GuaranteeType).getValue());
									reqMap.put(CreditInvestigateConstants.state, bizObject.getAttribute(CreditInvestigateConstants.State).getValue());
									reqMap.put(CreditInvestigateConstants.badBalance, bizObject.getAttribute(CreditInvestigateConstants.BadBalance).getValue());
									reqMap.put(CreditInvestigateConstants.stateEndDate, bizObject.getAttribute(CreditInvestigateConstants.StateEndDate).getValue());
									reqMap.put(CreditInvestigateConstants.beginMonth, bizObject.getAttribute(CreditInvestigateConstants.BeginMonth).getValue());
									reqMap.put(CreditInvestigateConstants.endMonth, bizObject.getAttribute(CreditInvestigateConstants.EndMonth).getValue());
									reqMap.put(CreditInvestigateConstants.latest24State, bizObject.getAttribute(CreditInvestigateConstants.Latest24State).getValue());
									reqMap.put(CreditInvestigateConstants.cueSerialNo, bizObject.getAttribute(CreditInvestigateConstants.CueSerialNo).getValue());
									reqMap.put(CreditInvestigateConstants.financeType, bizObject.getAttribute(CreditInvestigateConstants.FinanceType).getValue());
									creditInvestigateService.insertCreditReportCreditCard(reqMap);
								}catch(Exception e){
									logger.error("report credit card error...."+e.getMessage());
									flag = 1;
								}
							}
						}
					}
					
					ReportSection fiveYearOverdue = loanCard.getSectionByName("Latest5YearOverduesection_Loancard");
					if(fiveYearOverdue != null){
						List<BizObject> fiveYearOverdueList = fiveYearOverdue.getContent();
						if(fiveYearOverdueList != null){
							for(BizObject bizObject : fiveYearOverdueList){
								try{
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(CreditInvestigateConstants.reportNo, bizObject.getAttribute(CreditInvestigateConstants.ReportNo).getValue());
									reqMap.put(CreditInvestigateConstants.account, bizObject.getAttribute(CreditInvestigateConstants.Account).getValue());
									reqMap.put(CreditInvestigateConstants.beginMonth, bizObject.getAttribute(CreditInvestigateConstants.BeginMonth).getValue());
									reqMap.put(CreditInvestigateConstants.endMonth, bizObject.getAttribute(CreditInvestigateConstants.EndMonth).getValue());
									creditInvestigateService.insertCreditReportOverdue5Years(reqMap);
								}catch(Exception e){
									logger.error("report fiverYearOverdu_loancard error.." + e.getMessage());
									flag = 1;
								}
							}
						}
					}
					
					
					ReportSection fiveYeardueDetail = loanCard.getSectionByName("Latest5YearOverdueDetail_Loancard");
					if(fiveYeardueDetail != null){
						List<BizObject> fiveYeardueDetailList = fiveYeardueDetail.getContent();
						if(fiveYeardueDetailList != null){
							for(BizObject bizObject : fiveYeardueDetailList){
								try{
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(
											CreditInvestigateConstants.reportNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ReportNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.account,
											bizObject.getAttribute(
													CreditInvestigateConstants.Account)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.serialNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.SerialNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.month,
											bizObject.getAttribute(
													CreditInvestigateConstants.Month)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.lastMonths,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.LastMonths)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.amount,
											bizObject.getAttribute(
													CreditInvestigateConstants.Amount)
													.getValue());
									creditInvestigateService
											.insertCreditReportOverdueDetail(reqMap);
								}catch(Exception e){
									logger.error("report overdueDetail_loancard error:" + e.getMessage());
									flag = 1;
								}
							}
						}
					}
					//贷记卡特殊交易
					ReportSection specialTrade = loanCard.getSectionByName("SpecialTrade_Loancard");
					if(specialTrade != null){
						List<BizObject> specialTradeList = specialTrade.getContent();
						if (null != specialTradeList) {
							for (BizObject bizObject : specialTradeList) {
								try {
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(
											CreditInvestigateConstants.reportNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ReportNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.account,
											bizObject.getAttribute(
													CreditInvestigateConstants.Account)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.serialNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.SerialNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.type,
											bizObject.getAttribute(
													CreditInvestigateConstants.Type)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.occurDate,
											bizObject.getAttribute(
													CreditInvestigateConstants.GetTime)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.months,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ChangingMonths)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.amount,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ChangingAmount)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.content,
											bizObject.getAttribute(
													CreditInvestigateConstants.Content)
													.getValue());
									creditInvestigateService
											.insertCreditReportSpecial(reqMap);
								} catch (Exception e) {
									logger.error("reportSpecialTrade_loanCard error:" + e);
									flag = 1;
								}
							}
						}
						
					}
					
					// 贷记卡机构说明
					ReportSection loanCardInstitutionSection = loanCard
							.getSectionByName("BankIlluminate_Loancard");
					if(loanCardInstitutionSection != null){
						List<BizObject> loanCardInstitutionList = loanCardInstitutionSection
								.getContent();
						if (loanCardInstitutionList != null) {
							for (BizObject bizObject : loanCardInstitutionList) {
								try {
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(
											CreditInvestigateConstants.reportNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ReportNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.account,
											bizObject.getAttribute(
													CreditInvestigateConstants.Account)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.serialNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.SerialNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.content,
											bizObject.getAttribute(
													CreditInvestigateConstants.Content)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.occurDate,
											bizObject.getAttribute(
													CreditInvestigateConstants.GetTime)
													.getValue());
									creditInvestigateService
											.insertCreditReportInstitution(reqMap);
								} catch (Exception e) {
									logger.error("reportInstitutionLoanCard error:" + e);
									flag = 1;
								}
							}
						}
					}
					
					// 贷记卡本人申明
					ReportSection announceLoanCardSection = loanCard
							.getSectionByName("AnnounceInfo_Loancard");
					if(announceLoanCardSection != null){
						List<BizObject> announceLoanCardList = announceLoanCardSection.getContent();
						if (null != announceLoanCardList) {
							for (BizObject bizObject : announceLoanCardList) {
								try {
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(
											CreditInvestigateConstants.reportNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ReportNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.account,
											bizObject.getAttribute(
													CreditInvestigateConstants.Account)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.serialNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.SerialNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.content,
											bizObject.getAttribute(
													CreditInvestigateConstants.Content)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.occurDate,
											bizObject.getAttribute(
													CreditInvestigateConstants.GetTime)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.type,
											bizObject.getAttribute(
													CreditInvestigateConstants.Type)
													.getValue());
									creditInvestigateService
											.insertCreditReportAnnounce(reqMap);
								} catch (Exception e) {
									logger.error("reportAnnounceLoanCard error:" + e);
									flag = 1;
								}
							}
						}
					}
					
					
					// 贷记卡异议标注
					ReportSection dissentSectionLoancard = loanCard
							.getSectionByName("DissentInfo_Loancard");
					if(dissentSectionLoancard != null){
						List<BizObject> dissentLoanCardList = dissentSectionLoancard.getContent();
						if (null != dissentLoanCardList) {
							for (BizObject bizObject : dissentLoanCardList) {
								try {
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(
											CreditInvestigateConstants.reportNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ReportNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.account,
											bizObject.getAttribute(
													CreditInvestigateConstants.Account)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.serialNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.SerialNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.content,
											bizObject.getAttribute(
													CreditInvestigateConstants.Content)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.occurDate,
											bizObject.getAttribute(
													CreditInvestigateConstants.GetTime)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.type,
											bizObject.getAttribute(
													CreditInvestigateConstants.Type)
													.getValue());
									creditInvestigateService
											.insertCreditReportDissent(reqMap);
								} catch (Exception e) {
									logger.error("reportDissentLoanCard error :" + e);
									flag = 1;
								}
							}
						}
					}
					
				}
				
				
				
				
				//stdLoanCard准贷记卡
				ReportChapter semiCard = CreditDetailInfo.getChapterByName("StandardLoancard"); 
				if(semiCard != null){
					ReportSection stdloanCardInfo = semiCard.getSectionByName("stdLoancardInfo");
					if(stdloanCardInfo != null){
						List<BizObject> stdloanCardInfoList = stdloanCardInfo.getContent();
						if(stdloanCardInfoList != null){
							for(BizObject bizObject : stdloanCardInfoList){
								try{
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(CreditInvestigateConstants.shareLimit, bizObject.getAttribute(CreditInvestigateConstants.ShareCreditLimitAmount).getValue());
									reqMap.put(CreditInvestigateConstants.usedLimit, bizObject.getAttribute(CreditInvestigateConstants.UsedCreditLimitAmount).getValue());
									reqMap.put(CreditInvestigateConstants.usedAvgAmt6m, bizObject.getAttribute(CreditInvestigateConstants.Latest6MonthUsedAvgAmount).getValue());
									reqMap.put(CreditInvestigateConstants.usedAmtMax, bizObject.getAttribute(CreditInvestigateConstants.UsedHighestAmount).getValue());
									reqMap.put(CreditInvestigateConstants.scheduledDate, bizObject.getAttribute(CreditInvestigateConstants.ScheduledPaymentDate).getValue());
									reqMap.put(CreditInvestigateConstants.actualAmt, bizObject.getAttribute(CreditInvestigateConstants.ActualPaymentAmount).getValue());
									reqMap.put(CreditInvestigateConstants.recentPayDate, bizObject.getAttribute(CreditInvestigateConstants.RecentPayDate).getValue());
									reqMap.put(CreditInvestigateConstants.overdueAmt, bizObject.getAttribute(CreditInvestigateConstants.CurrOverdueAmount).getValue());
									reqMap.put(CreditInvestigateConstants.reportNo, bizObject.getAttribute(CreditInvestigateConstants.ReportNo).getValue());
									reqMap.put(CreditInvestigateConstants.serialNo, bizObject.getAttribute(CreditInvestigateConstants.SerialNo).getValue());
									reqMap.put(CreditInvestigateConstants.bizType, bizObject.getAttribute(CreditInvestigateConstants.bizType).getValue());
									reqMap.put(CreditInvestigateConstants.account, bizObject.getAttribute(CreditInvestigateConstants.Account).getValue());
									reqMap.put(CreditInvestigateConstants.note, bizObject.getAttribute(CreditInvestigateConstants.Cue).getValue());
									reqMap.put(CreditInvestigateConstants.financeOrg, bizObject.getAttribute(CreditInvestigateConstants.FinanceOrg).getValue());
									reqMap.put(CreditInvestigateConstants.currency, bizObject.getAttribute(CreditInvestigateConstants.Currency).getValue());
									reqMap.put(CreditInvestigateConstants.openDate, bizObject.getAttribute(CreditInvestigateConstants.OpenDate).getValue());
									reqMap.put(CreditInvestigateConstants.limitAmt, bizObject.getAttribute(CreditInvestigateConstants.CreditLimitAmount).getValue());
									reqMap.put(CreditInvestigateConstants.guarantyType, bizObject.getAttribute(CreditInvestigateConstants.GuaranteeType).getValue());
									reqMap.put(CreditInvestigateConstants.state, bizObject.getAttribute(CreditInvestigateConstants.State).getValue());
									reqMap.put(CreditInvestigateConstants.badBalance, bizObject.getAttribute(CreditInvestigateConstants.BadBalance).getValue());
									reqMap.put(CreditInvestigateConstants.stateEndDate, bizObject.getAttribute(CreditInvestigateConstants.StateEndDate).getValue());
									reqMap.put(CreditInvestigateConstants.beginMonth, bizObject.getAttribute(CreditInvestigateConstants.BeginMonth).getValue());
									reqMap.put(CreditInvestigateConstants.endMonth, bizObject.getAttribute(CreditInvestigateConstants.EndMonth).getValue());
									reqMap.put(CreditInvestigateConstants.latest24State, bizObject.getAttribute(CreditInvestigateConstants.Latest24State).getValue());
									reqMap.put(CreditInvestigateConstants.cueSerialNo, bizObject.getAttribute(CreditInvestigateConstants.CueSerialNo).getValue());
									reqMap.put(CreditInvestigateConstants.financeType, bizObject.getAttribute(CreditInvestigateConstants.FinanceType).getValue());
									creditInvestigateService.insertCreditReportSemicard(reqMap);
								}catch(Exception e){
									logger.error("report stdLoancardInfo error...."+e.getMessage());
									flag = 1;
								}
							}
						}
					}
					
					ReportSection fiveYearOverdue = semiCard.getSectionByName("Latest5YearOverduesection_stdLoancard");
					if(fiveYearOverdue != null){
						List<BizObject> fiveYearOverdueList = fiveYearOverdue.getContent();
						if(fiveYearOverdueList != null){
							for(BizObject bizObject : fiveYearOverdueList){
								try{
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(CreditInvestigateConstants.reportNo, bizObject.getAttribute(CreditInvestigateConstants.ReportNo).getValue());
									reqMap.put(CreditInvestigateConstants.account, bizObject.getAttribute(CreditInvestigateConstants.Account).getValue());
									reqMap.put(CreditInvestigateConstants.beginMonth, bizObject.getAttribute(CreditInvestigateConstants.BeginMonth).getValue());
									reqMap.put(CreditInvestigateConstants.endMonth, bizObject.getAttribute(CreditInvestigateConstants.EndMonth).getValue());
									creditInvestigateService.insertCreditReportOverdue5Years(reqMap);
								}catch(Exception e){
									logger.error("report fiverYearOverdu_stdloancard error.." + e.getMessage());
									flag = 1;
								}
							}
						}
					}
					
					
					ReportSection fiveYeardueDetail = semiCard.getSectionByName("Latest5YearOverdueDetail_stdLoancard");
					if(fiveYeardueDetail != null){
						List<BizObject> fiveYeardueDetailList = fiveYeardueDetail.getContent();
						if(fiveYeardueDetailList != null){
							for(BizObject bizObject : fiveYeardueDetailList){
								try{
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(
											CreditInvestigateConstants.reportNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ReportNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.account,
											bizObject.getAttribute(
													CreditInvestigateConstants.Account)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.serialNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.SerialNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.month,
											bizObject.getAttribute(
													CreditInvestigateConstants.Month)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.lastMonths,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.LastMonths)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.amount,
											bizObject.getAttribute(
													CreditInvestigateConstants.Amount)
													.getValue());
									creditInvestigateService
											.insertCreditReportOverdueDetail(reqMap);
								}catch(Exception e){
									logger.error("report overdueDetail_stdloancard error:" + e.getMessage());
									flag = 1;
								}
							}
						}
					}
					//贷记卡特殊交易
					ReportSection specialTrade = semiCard.getSectionByName("SpecialTrade_stdLoancard");
					if(specialTrade != null){
						List<BizObject> specialTradeList = specialTrade.getContent();
						if (null != specialTradeList) {
							for (BizObject bizObject : specialTradeList) {
								try {
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(
											CreditInvestigateConstants.reportNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ReportNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.account,
											bizObject.getAttribute(
													CreditInvestigateConstants.Account)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.serialNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.SerialNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.type,
											bizObject.getAttribute(
													CreditInvestigateConstants.Type)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.occurDate,
											bizObject.getAttribute(
													CreditInvestigateConstants.GetTime)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.months,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ChangingMonths)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.amount,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ChangingAmount)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.content,
											bizObject.getAttribute(
													CreditInvestigateConstants.Content)
													.getValue());
									creditInvestigateService
											.insertCreditReportSpecial(reqMap);
								} catch (Exception e) {
									logger.error("reportSpecialTrade_loanCard error:" + e);
									flag = 1;
								}
							}
						}
						
					}
					
					// 贷记卡机构说明
					ReportSection loanCardInstitutionSection = semiCard
							.getSectionByName("BankIlluminate_stdLoancard");
					if(loanCardInstitutionSection != null){
						List<BizObject> loanCardInstitutionList = loanCardInstitutionSection
								.getContent();
						if (loanCardInstitutionList != null) {
							for (BizObject bizObject : loanCardInstitutionList) {
								try {
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(
											CreditInvestigateConstants.reportNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ReportNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.account,
											bizObject.getAttribute(
													CreditInvestigateConstants.Account)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.serialNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.SerialNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.content,
											bizObject.getAttribute(
													CreditInvestigateConstants.Content)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.occurDate,
											bizObject.getAttribute(
													CreditInvestigateConstants.GetTime)
													.getValue());
									creditInvestigateService
											.insertCreditReportInstitution(reqMap);
								} catch (Exception e) {
									logger.error("reportInstitutionLoanCard error:" + e);
									flag = 1;
								}
							}
						}
					}
					
					// 贷记卡本人申明
					ReportSection announceLoanCardSection = semiCard
							.getSectionByName("AnnounceInfo_stdLoancard");
					if(announceLoanCardSection != null){
						List<BizObject> announceLoanCardList = announceLoanCardSection.getContent();
						if (null != announceLoanCardList) {
							for (BizObject bizObject : announceLoanCardList) {
								try {
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(
											CreditInvestigateConstants.reportNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ReportNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.account,
											bizObject.getAttribute(
													CreditInvestigateConstants.Account)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.serialNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.SerialNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.content,
											bizObject.getAttribute(
													CreditInvestigateConstants.Content)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.occurDate,
											bizObject.getAttribute(
													CreditInvestigateConstants.GetTime)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.type,
											bizObject.getAttribute(
													CreditInvestigateConstants.Type)
													.getValue());
									creditInvestigateService
											.insertCreditReportAnnounce(reqMap);
								} catch (Exception e) {
									logger.error("reportAnnounceLoanCard error:" + e);
									flag = 1;
								}
							}
					}
					
					}
					
					
					// 贷记卡异议标注
					ReportSection dissentSectionLoancard = loanCard
							.getSectionByName("DissentInfo_Loancard");
					if(dissentSectionLoancard != null){
						List<BizObject> dissentLoanCardList = dissentSectionLoancard.getContent();
						if (null != dissentLoanCardList) {
							for (BizObject bizObject : dissentLoanCardList) {
								try {
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(
											CreditInvestigateConstants.reportNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.ReportNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.account,
											bizObject.getAttribute(
													CreditInvestigateConstants.Account)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.serialNo,
											bizObject
													.getAttribute(
															CreditInvestigateConstants.SerialNo)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.content,
											bizObject.getAttribute(
													CreditInvestigateConstants.Content)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.occurDate,
											bizObject.getAttribute(
													CreditInvestigateConstants.GetTime)
													.getValue());
									reqMap.put(
											CreditInvestigateConstants.type,
											bizObject.getAttribute(
													CreditInvestigateConstants.Type)
													.getValue());
									creditInvestigateService
											.insertCreditReportDissent(reqMap);
								} catch (Exception e) {
									logger.error("reportDissentLoanCard error :" + e);
									flag = 1;
								}
							}
						}
					}
					
				}
				
				//对外担保信息
				ReportChapter guaranteeChapter = CreditDetailInfo.getChapterByName("Guarantee");
				if(guaranteeChapter != null){
					ReportSection loanGuaranteeSection = guaranteeChapter.getSectionByName("LoanGuarantee");
					if(loanGuaranteeSection != null){
						List<BizObject> guaranteeList = loanGuaranteeSection.getContent();
						if(guaranteeList != null){
							for(BizObject bizObject : guaranteeList){
								try{
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(CreditInvestigateConstants.reportNo, bizObject.getAttribute(CreditInvestigateConstants.ReportNo).getValue());
									reqMap.put(CreditInvestigateConstants.serialNo, bizObject.getAttribute(CreditInvestigateConstants.SerialNo).getValue());
									reqMap.put(CreditInvestigateConstants.organName, bizObject.getAttribute(CreditInvestigateConstants.Organname).getValue());
									reqMap.put(CreditInvestigateConstants.contractAmt, bizObject.getAttribute(CreditInvestigateConstants.ContractMoney).getValue());
									reqMap.put(CreditInvestigateConstants.beginDate, bizObject.getAttribute(CreditInvestigateConstants.BeginDate).getValue());
									reqMap.put(CreditInvestigateConstants.endDate, bizObject.getAttribute(CreditInvestigateConstants.EndDate).getValue());
									reqMap.put(CreditInvestigateConstants.guaranteeAmt, bizObject.getAttribute(CreditInvestigateConstants.GuananteeMoney).getValue());
									reqMap.put(CreditInvestigateConstants.guaranteeBalance, bizObject.getAttribute(CreditInvestigateConstants.GuaranteeBalance).getValue());
									reqMap.put(CreditInvestigateConstants.class5State, bizObject.getAttribute(CreditInvestigateConstants.Class5State).getValue());
									reqMap.put(CreditInvestigateConstants.billingDate, bizObject.getAttribute(CreditInvestigateConstants.BillingDate).getValue());
									creditInvestigateService.insertCreditReportGuarantee(reqMap);
								}catch(Exception e){
									logger.error("report guarantee error:" + e.getMessage());
									flag = 1;
								}
							}
						}
					}
					
					ReportSection loanCardGuaranteeSection = guaranteeChapter.getSectionByName("CardGuarantee");
					if(loanCardGuaranteeSection != null){
						List<BizObject> guaranteeList = loanCardGuaranteeSection.getContent();
						if(guaranteeList != null){
							for(BizObject bizObject : guaranteeList){
								try{
									Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
									reqMap.put(CreditInvestigateConstants.reportNo, bizObject.getAttribute(CreditInvestigateConstants.ReportNo).getValue());
									reqMap.put(CreditInvestigateConstants.serialNo, bizObject.getAttribute(CreditInvestigateConstants.SerialNo).getValue());
									reqMap.put(CreditInvestigateConstants.organName, bizObject.getAttribute(CreditInvestigateConstants.Organname).getValue());
									reqMap.put(CreditInvestigateConstants.creditLimit, bizObject.getAttribute(CreditInvestigateConstants.CreditLimit).getValue());
									reqMap.put(CreditInvestigateConstants.beginDate, bizObject.getAttribute(CreditInvestigateConstants.BeginDate).getValue());
									reqMap.put(CreditInvestigateConstants.guaranteeAmt, bizObject.getAttribute(CreditInvestigateConstants.GuananteeMoney).getValue());
									reqMap.put(CreditInvestigateConstants.usedLimit, bizObject.getAttribute(CreditInvestigateConstants.UsedLimit).getValue());
									reqMap.put(CreditInvestigateConstants.billingDate, bizObject.getAttribute(CreditInvestigateConstants.BillingDate).getValue());
									creditInvestigateService.insertCreditReportCardGuarantee(reqMap);
								}catch(Exception e){
									logger.error("report guarantee error:" + e.getMessage());
									flag = 1;
								}
							}
						}
					}
				}
				
				
				
				
			}

			// ReportPart:PersonalInfo
			ReportPart personalInfo = creditReport.getReportData()
					.getPartByName("PersonalInfo");
			ReportChapter identityChapter = personalInfo
					.getChapterByName("Identity");
			if (identityChapter != null) {
				// identity
				List<BizObject> identityList = identityChapter.getContent();
				if (identityList != null) {
					for (BizObject bizObject : identityList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.gender,
									bizObject.getAttribute(
											CreditInvestigateConstants.Gender)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.birthDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Birthday)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.marital,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.MaritalState)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.mobile,
									bizObject.getAttribute(
											CreditInvestigateConstants.Mobile)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.officeTel,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.OfficeTelephoneNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.homeTel,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.HomeTelephoneNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.education,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.EduLevel)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.degree,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.EduDegree)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.postAddr,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.PostAddress)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.regiAddr,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.RegisteredAddress)
											.getValue());
							creditInvestigateService
									.insertCreditReportIdentity(reqMap);
						} catch (Exception e) {
							logger.error("reportIdentity error:" + e);
							flag = 1;
						}
					}
				}
			}

			// spouse
			ReportChapter spouseChapter = personalInfo
					.getChapterByName("Spouse");
			if (spouseChapter != null) {

				List<BizObject> spouseList = spouseChapter.getContent();
				if (spouseList != null) {
					for (BizObject bizObject : spouseList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.name,
									bizObject.getAttribute(
											CreditInvestigateConstants.Name)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.certType,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Certtype)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.certNo,
									bizObject.getAttribute(
											CreditInvestigateConstants.Certno)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.employer,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Employer)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.phoneNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.TelephoneNo)
											.getValue());
							creditInvestigateService
									.insertCreditReportSpouse(reqMap);
						} catch (Exception e) {
							logger.error("reportSpouse error:" + e);
							flag = 1;
						}
					}
				}
			}

			// Residence
			ReportChapter residenceChapter = personalInfo
					.getChapterByName("Residence");
			if (residenceChapter != null) {
				List<BizObject> residenceList = residenceChapter.getContent();
				if (residenceList != null) {
					for (BizObject bizObject : residenceList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.address,
									bizObject.getAttribute(
											CreditInvestigateConstants.Address)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.type,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ResidenceType)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.updateDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.GetTime)
											.getValue());
							creditInvestigateService
									.insertCreditReportResidence(reqMap);
						} catch (Exception e) {
							logger.error("reportResidence error:" + e);
							flag = 1;
						}
					}
				}

			}
			// Professional
			ReportChapter professionalChapter = personalInfo
					.getChapterByName("Professional");
			if (professionalChapter != null) {
				List<BizObject> professionalList = professionalChapter
						.getContent();
				if (professionalList != null) {
					for (BizObject bizObject : professionalList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.employer,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Employer)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.employerAddr,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.EmployerAddress)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.occupation,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Occupation)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.industry,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Industry)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.duty,
									bizObject.getAttribute(
											CreditInvestigateConstants.Duty)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.title,
									bizObject.getAttribute(
											CreditInvestigateConstants.Title)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.startYear,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.StartYear)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.updateDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.GetTime)
											.getValue());
							creditInvestigateService
									.insertCreditReportProfession(reqMap);
						} catch (Exception e) {
							logger.error("reportProfession error:" + e);
							flag = 1;
						}
					}
				}
			}
			// ReportPart: InfoSummary
			ReportPart infoSummaryPart = creditReport.getReportData()
					.getPartByName("InfoSummary");
			// CreditCue
			ReportChapter creditCueChapter = infoSummaryPart
					.getChapterByName("CreditCue");
			if (creditCueChapter != null) {
				List<BizObject> creditCueList = creditCueChapter.getContent();
				if (creditCueList != null) {
					for (BizObject bizObject : creditCueList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.houseLoans,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.HouseLoanCount)
											.getValue());
							reqMap.put(CreditInvestigateConstants.houseTwoLoans, bizObject.getAttribute(CreditInvestigateConstants.HouseLoan2Count).getValue());
							reqMap.put(
									CreditInvestigateConstants.otherLoans,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.OtherLoanCount)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.firstIssueMonth,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.FirstLoanOpenMonth)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.creditCards,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.LoancardCount)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.firstIssueMonth2,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.FirstLoancardOpenMonth)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.semiCreidtCards,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.StandardLoancardCount)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.firstIssueMonth3,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.FirstStandardLoancardOpenMonth)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.announceCount,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.AnnounceCount)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.dissentCount,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.DissentCount)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.score,
									bizObject.getAttribute(
											CreditInvestigateConstants.Score)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.month,
									bizObject.getAttribute(
											CreditInvestigateConstants.Month)
											.getValue());
							creditInvestigateService
									.insertCreditReportCreditInfo(reqMap);
						} catch (Exception e) {
							logger.error("reportCreditInfo error:" + e);
							flag = 1;
						}
					}
				}
			}
			// OverdueAndFellback
			ReportChapter overdueAnFellback = infoSummaryPart
					.getChapterByName("OverdueAndFellback");
			if (overdueAnFellback != null) {
				ReportSection fellbackSummarySection = overdueAnFellback
						.getSectionByName("FellbackSummary");
				if (fellbackSummarySection != null) {
					List<BizObject> fellbackSummaryList = fellbackSummarySection
							.getContent();
					if (fellbackSummaryList != null) {
						for (BizObject bizObject : fellbackSummaryList) {
							try {
								Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
								reqMap.put(
										CreditInvestigateConstants.reportNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ReportNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.badDebits,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Count)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.balance,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Balance)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.disposals,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Count2)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.balance2,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Balance2)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.quids,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Count3)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.balance3,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Balance3)
												.getValue());
								creditInvestigateService
										.insertCreditReportNpl(reqMap);
							} catch (Exception e) {
								logger.error("reportNpl error:" + e);
								flag = 1;
							}
						}
					}
				}

				ReportSection overDueSummary = overdueAnFellback
						.getSectionByName("OverdueSummary");
				if (overDueSummary != null) {
					List<BizObject> overDueSummaryList = overDueSummary
							.getContent();
					if (overDueSummaryList != null) {
						for (BizObject bizObject : overDueSummaryList) {
							try {
								Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
								reqMap.put(
										CreditInvestigateConstants.reportNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ReportNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.count,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Count)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.months,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Months)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.nplHighestAmt,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.HighestOverdueAmountPerMon)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.maxDuration,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.MaxDuration)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.count2,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Count2)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.months2,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Months2)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.nplHighestAmt2,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.HighestOverdueAmountPerMon2)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.maxDuration2,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.MaxDuration2)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.count3,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Count3)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.months3,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Months3)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.maxDuration3,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.MaxDuration3)
												.getValue());
								creditInvestigateService
										.insertCreditReportOverdue(reqMap);
							} catch (Exception e) {
								logger.error("reportOverdue error:" + e);
								flag = 1;
							}
						}
					}
				}
			}
			// ShareAndDebt
			ReportChapter shareAndDebtChapter = infoSummaryPart
					.getChapterByName("ShareAndDebt");
			if (shareAndDebtChapter != null) {
				// UnpaidLoan
				ReportSection unpaidLoanSection = shareAndDebtChapter
						.getSectionByName("UnpaidLoan");
				if (unpaidLoanSection != null) {
					List<BizObject> unpaidLoanList = unpaidLoanSection
							.getContent();
					if (unpaidLoanList != null) {
						for (BizObject bizObject : unpaidLoanList) {
							try {
								Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
								reqMap.put(
										CreditInvestigateConstants.reportNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ReportNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.financeCorps,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.FinanceCorpCount)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.financeOrgs,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.FinanceOrgCount)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.accounts,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.AccountCount)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.creditLimit,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.CreditLimit)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.balance,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Balance)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.avgAmt6m,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Latest6MonthUsedAvgAmount)
												.getValue());
								creditInvestigateService
										.insertCreditReportNormal(reqMap);
							} catch (Exception e) {
								logger.error("reportNormal error:" + e);
								flag = 1;
							}
						}
					}
				}

				// UndestoryLoancard
				ReportSection undestoryLoancard = shareAndDebtChapter
						.getSectionByName("UndestoryLoancard");
				if (undestoryLoancard != null) {
					List<BizObject> undestoryLoanCardList = undestoryLoancard
							.getContent();
					if (undestoryLoanCardList != null) {
						for (BizObject bizObject : undestoryLoanCardList) {
							try {
								Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
								reqMap.put(
										CreditInvestigateConstants.reportNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ReportNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.financeCorps,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.FinanceCorpCount)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.financeOrgs,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.FinanceOrgCount)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.accounts,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.AccountCount)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.creditLimit,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.CreditLimit)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.creditMax,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.MaxCreditLimitPerOrg)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.creditMin,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.MinCreditLimitPerOrg)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.usedCredit,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.UsedCreditLimit)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.avgAmt6m,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Latest6MonthUsedAvgAmount)
												.getValue());
								creditInvestigateService
										.insertCreditReportCreditCardBrief(reqMap);
							} catch (Exception e) {
								logger.error("reportCreditCardBrief error:" + e);
								flag = 1;
							}
						}
					}
				}

				ReportSection undestoryStandardLoancard = shareAndDebtChapter
						.getSectionByName("UndestoryStandardLoancard");
				if (undestoryStandardLoancard != null) {
					List<BizObject> undestoryStandardLoancardList = undestoryStandardLoancard
							.getContent();
					if (undestoryStandardLoancardList != null) {
						for (BizObject bizObject : undestoryStandardLoancardList) {
							try {
								Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
								reqMap.put(
										CreditInvestigateConstants.reportNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ReportNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.financeCorps,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.FinanceCorpCount)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.financeOrgs,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.FinanceOrgCount)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.accounts,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.AccountCount)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.creditLimit,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.CreditLimit)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.creditMax,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.MaxCreditLimitPerOrg)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.creditMin,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.MinCreditLimitPerOrg)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.usedCredit,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.UsedCreditLimit)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.avgAmt6m,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Latest6MonthUsedAvgAmount)
												.getValue());
								creditInvestigateService
										.insertCreditReportSemicardBrief(reqMap);
							} catch (Exception e) {
								logger.error("reportSemicardBrief error:" + e);
								flag = 1;
							}
						}
					}
				}

				// GuaranteeSummary
				ReportSection guaranteeSummary = shareAndDebtChapter
						.getSectionByName("GuaranteeSummary");
				if (guaranteeSummary != null) {
					List<BizObject> guaranteeSummaryList = guaranteeSummary
							.getContent();
					if (guaranteeSummaryList != null) {
						for (BizObject bizObject : guaranteeSummaryList) {
							try {
								Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
								reqMap.put(
										CreditInvestigateConstants.reportNo,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.ReportNo)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.count,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Count)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.amount,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Amount)
												.getValue());
								reqMap.put(
										CreditInvestigateConstants.balance,
										bizObject
												.getAttribute(
														CreditInvestigateConstants.Balance)
												.getValue());
								creditInvestigateService
										.insertCreditReportGuaranteeBrief(reqMap);
							} catch (Exception e) {
								logger.error("reportGuaranteeBrief error:" + e);
								flag = 1;
							}

						}
					}
				}
			}

			// publicInfo
			ReportPart publicInfo = creditReport.getReportData().getPartByName(
					"PublicInfo");
			// TaxArrear
			ReportChapter taxArrear = publicInfo.getChapterByName("TaxArrear");
			if (taxArrear != null) {
				List<BizObject> taxArrearList = taxArrear.getContent();
				if (taxArrearList != null) {
					for (BizObject bizObject : taxArrearList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.authority,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Organname)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.taxAmt,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.TaxArreaAmount)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.statisDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Revenuedate)
											.getValue());
							creditInvestigateService
									.insertCreditReportTaxarrear(reqMap);
						} catch (Exception e) {
							logger.error("reportTaxarrear error:" + e);
							flag = 1;
						}
					}
				}
			}
			// CivilJudgement
			ReportChapter civilJudgementChapter = publicInfo
					.getChapterByName("CivilJudgement");
			if (civilJudgementChapter != null) {
				List<BizObject> judgementList = civilJudgementChapter
						.getContent();
				if (judgementList != null) {
					for (BizObject bizObject : judgementList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.court,
									bizObject.getAttribute(
											CreditInvestigateConstants.Court)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.caseReason,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.CaseReason)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.registerDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.RegisterDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.closedType,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ClosedType)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.caseResult,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.CaseResult)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.effectiveDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.CaseValidatedate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.suitObj,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SuitObject)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.suitAmt,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SuitObjectMoney)
											.getValue());
							creditInvestigateService
									.insertCreditReportJudgment(reqMap);
						} catch (Exception e) {
							logger.error("reportJudgment error:" + e);
							flag = 1;
						}
					}
				}
			}
			// ForceExecution
			ReportChapter forceExecution = publicInfo
					.getChapterByName("ForceExecution");
			if (forceExecution != null) {
				List<BizObject> forceExecutionList = forceExecution
						.getContent();
				if (forceExecutionList != null) {
					for (BizObject bizObject : forceExecutionList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.court,
									bizObject.getAttribute(
											CreditInvestigateConstants.Court)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.caseReason,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.CaseReason)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.registerDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.RegisterDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.closedType,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ClosedType)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.caseState,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.CaseState)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.closedDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ClosedDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.enforceObj,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.EnforceObject)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.enforceAmt,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.EnforceObjectMoney)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.executedObj,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.AlreadyEnforceObject)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.executedAmt,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.AlreadyEnforceObjectMoney)
											.getValue());
							creditInvestigateService
									.insertCreditReportExecution(reqMap);
						} catch (Exception e) {
							logger.error("reportExecution error:" + e);
							flag = 1;
						}
					}
				}
			}
			// AdminPunishment
			ReportChapter adminPunishment = publicInfo
					.getChapterByName("AdminPunishment");
			if (adminPunishment != null) {
				List<BizObject> adminPunishmentList = adminPunishment
						.getContent();
				if (adminPunishmentList != null) {
					for (BizObject bizObject : adminPunishmentList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.penaltyOrg,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Organname)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.content,
									bizObject.getAttribute(
											CreditInvestigateConstants.Content)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.amount,
									bizObject.getAttribute(
											CreditInvestigateConstants.Money)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.beginDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.BeginDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.endDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.EndDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.result,
									bizObject.getAttribute(
											CreditInvestigateConstants.Result)
											.getValue());
							creditInvestigateService
									.insertCreditReportPenalty(reqMap);
						} catch (Exception e) {
							logger.error("reportPenalty error:" + e);
							flag = 1;
						}
					}
				}
			}

			// AccFund
			ReportChapter accFundChapter = publicInfo
					.getChapterByName("AccFund");
			if (accFundChapter != null) {
				List<BizObject> accFundList = accFundChapter.getContent();
				if (accFundList != null) {
					for (BizObject bizObject : accFundList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							
							reqMap.put(CreditInvestigateConstants.commitTime, new Date());
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.area,
									bizObject.getAttribute(
											CreditInvestigateConstants.Area)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.registerDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.RegisterDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.firstMonth,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.FirstMonth)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.toMonth,
									bizObject.getAttribute(
											CreditInvestigateConstants.ToMonth)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.state,
									bizObject.getAttribute(
											CreditInvestigateConstants.State)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.depositAmt,
									bizObject.getAttribute(
											CreditInvestigateConstants.Pay)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.ownPCT,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.OwnPercent)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.corpPCT,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ComPercent)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.depositUnit,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Organname)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.updateDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.GetTime)
											.getValue());
							creditInvestigateService
									.insertCreditReportHousing(reqMap);
						} catch (Exception e) {
							logger.error("reportHousing error:" + e);
							flag = 1;
						}
					}
				}

			}

			// EndowmentInsuranceDeposit
			ReportChapter endowmentInsuranceDeposit = publicInfo
					.getChapterByName("EndowmentInsuranceDeposit");
			if (endowmentInsuranceDeposit != null) {
				List<BizObject> endowmentInsuranceDepositList = endowmentInsuranceDeposit
						.getContent();
				if (endowmentInsuranceDepositList != null) {
					for (BizObject bizObject : endowmentInsuranceDepositList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.area,
									bizObject.getAttribute(
											CreditInvestigateConstants.Area)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.registerDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.RegisterDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.duration,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.MonthDuration)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.workDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.WorkDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.state,
									bizObject.getAttribute(
											CreditInvestigateConstants.State)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.depositBasic,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.OwnBasicMoney)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.depositAmt,
									bizObject.getAttribute(
											CreditInvestigateConstants.Money)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.updateDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.GetTime)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.depositUnit,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Organname)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.reason,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.PauseReason)
											.getValue());
							creditInvestigateService
									.insertCreditReportPensionDeposit(reqMap);
						} catch (Exception e) {
							logger.error("reportPensionDeposit error:" + e);
							flag = 1;
						}
					}
				}
			}
			// EndowmentInsuranceDeliver
			ReportChapter endowmentInsuranceDeliver = publicInfo
					.getChapterByName("EndowmentInsuranceDeliver");
			if (endowmentInsuranceDeliver != null) {
				List<BizObject> endowmentInsuranceDeliverList = endowmentInsuranceDeliver
						.getContent();
				if (endowmentInsuranceDeliverList != null) {
					for (BizObject bizObject : endowmentInsuranceDeliverList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.area,
									bizObject.getAttribute(
											CreditInvestigateConstants.Area)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.retireType,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.RetireType)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.retireDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.RetiredDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.workDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.WorkDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.issueAmt,
									bizObject.getAttribute(
											CreditInvestigateConstants.Money)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.reason,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.PauseReason)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.workUnit,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Organname)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.updateDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.GetTime)
											.getValue());
							creditInvestigateService
									.insertCreditReportPensionIssue(reqMap);
						} catch (Exception e) {
							logger.error("reportPensionIssue error:" + e);
							flag = 1;
						}
					}
				}
			}

			// Salvation
			ReportChapter salvation = publicInfo.getChapterByName("Salvation");
			if (salvation != null) {
				List<BizObject> salvationList = salvation.getContent();
				if (salvationList != null) {
					for (BizObject bizObject : salvationList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.type,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.PersonnelType)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.area,
									bizObject.getAttribute(
											CreditInvestigateConstants.Area)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.workUnit,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Organname)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.income,
									bizObject.getAttribute(
											CreditInvestigateConstants.Money)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.applyDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.RegisterDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.approveDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.PassDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.updateDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.GetTime)
											.getValue());
							creditInvestigateService
									.insertCreditReportRelief(reqMap);
						} catch (Exception e) {
							logger.error("reportRelief error:" + e);
							flag = 1;
						}
					}
				}
			}

			// Competence
			ReportChapter competence = publicInfo
					.getChapterByName("Competence");
			if (competence != null) {
				List<BizObject> competenceList = competence.getContent();
				if (competenceList != null) {
					for (BizObject bizObject : competenceList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.competency,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.CompetencyName)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.grade,
									bizObject.getAttribute(
											CreditInvestigateConstants.Grade)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.awardDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.AwardDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.endDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.EndDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.revokeDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.RevokeDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.authority,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Organname)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.area,
									bizObject.getAttribute(
											CreditInvestigateConstants.Area)
											.getValue());
							creditInvestigateService
									.insertCreditReportQualify(reqMap);
						} catch (Exception e) {
							logger.error("reportQualify error:" + e);
							flag = 1;
						}
					}
				}
			}

			// AdminAward
			ReportChapter adminChapter = publicInfo
					.getChapterByName("ReportChapter");
			if (adminChapter != null) {
				List<BizObject> adminList = adminChapter.getContent();
				if (adminList != null) {
					for (BizObject bizObject : adminList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.awardOrg,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Organname)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.content,
									bizObject.getAttribute(
											CreditInvestigateConstants.Content)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.beginDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.BeginDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.endDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.EndDate)
											.getValue());
							creditInvestigateService
									.insertCreditReportAward(reqMap);
						} catch (Exception e) {
							logger.error("reportAward error:" + e);
							flag = 1;
						}
					}
				}
			}
			// Vehicle
			ReportChapter vehicleChapter = publicInfo
					.getChapterByName("Vehicle");
			if (vehicleChapter != null) {
				List<BizObject> vehicleList = vehicleChapter.getContent();
				if (vehicleList != null) {
					for (BizObject bizObject : vehicleList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.engineCode,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.EngineCode)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.licenseCode,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.LicenseCode)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.brand,
									bizObject.getAttribute(
											CreditInvestigateConstants.Brand)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.carType,
									bizObject.getAttribute(
											CreditInvestigateConstants.CarType)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.useType,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.UseCharacter)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.state,
									bizObject.getAttribute(
											CreditInvestigateConstants.State)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.pledgeFlag,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.PledgeFlag)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.updateDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.GetTime)
											.getValue());
							creditInvestigateService
									.insertCreditReportVehicle(reqMap);
						} catch (Exception e) {
							logger.error("reportVehicle error:" + e);
							flag = 1;
						}
					}
				}
			}

			// TelPayment
			ReportChapter telPaymentChapter = publicInfo
					.getChapterByName("TelPayment");
			if (telPaymentChapter != null) {
				List<BizObject> telPaymentList = telPaymentChapter.getContent();
				if (telPaymentList != null) {
					for (BizObject bizObject : telPaymentList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.carrier,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Organname)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.type,
									bizObject.getAttribute(
											CreditInvestigateConstants.Type)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.registerDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.RegisterDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.state,
									bizObject.getAttribute(
											CreditInvestigateConstants.State)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.arrearMoney,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ArrearMoney)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.arrearMonths,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ArrearMonths)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.status24m,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.Status24)
											.getValue());
							creditInvestigateService
									.insertCreditReportTelpayment(reqMap);
						} catch (Exception e) {
							logger.error("reportTelpayment error:" + e);
							flag = 1;
						}
					}
				}
			}

			// Announce
			ReportPart announcePart = creditReport.getReportData()
					.getPartByName("Announce");
			ReportChapter announceInfoChapter = announcePart
					.getChapterByName("AnnounceInfo");
			if (announceInfoChapter != null) {
				List<BizObject> announceInfoList = announceInfoChapter
						.getContent();
				if (announceInfoList != null) {
					for (BizObject bizObject : announceInfoList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.account,
									bizObject.getAttribute(
											CreditInvestigateConstants.Account)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.content,
									bizObject.getAttribute(
											CreditInvestigateConstants.Content)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.occurDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.GetTime)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.type,
									bizObject.getAttribute(
											CreditInvestigateConstants.Type)
											.getValue());
							creditInvestigateService
									.insertCreditReportAnnounce(reqMap);
						} catch (Exception e) {
							logger.error("reportAnnounce error:" + e);
							flag = 1;
						}
					}
				}
			}

			// DissentInfo
			ReportChapter dissentInfo = announcePart
					.getChapterByName("DissentInfo");
			if (dissentInfo != null) {
				List<BizObject> dissentInfoList = dissentInfo.getContent();
				if (dissentInfoList != null) {
					for (BizObject bizObject : dissentInfoList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.account,
									bizObject.getAttribute(
											CreditInvestigateConstants.Account)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.content,
									bizObject.getAttribute(
											CreditInvestigateConstants.Content)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.occurDate,
									bizObject.getAttribute(
											CreditInvestigateConstants.GetTime)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.type,
									bizObject.getAttribute(
											CreditInvestigateConstants.Type)
											.getValue());
							creditInvestigateService
									.insertCreditReportDissent(reqMap);
						} catch (Exception e) {
							logger.error("creditReportDissent error:" + e);
							flag = 1;
						}
					}
				}
			}

			ReportPart queryRecord = creditReport.getReportData()
					.getPartByName("QueryRecord");
			ReportChapter recordSummary = queryRecord
					.getChapterByName("RecordSummary");
			if (recordSummary != null) {
				List<BizObject> recordList = recordSummary.getContent();
				if (recordList != null) {
					for (BizObject bizObject : recordList) {

						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.qryOrgs1,
									bizObject.getAttribute(
											CreditInvestigateConstants.orgSum1)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.qryOrgs2,
									bizObject.getAttribute(
											CreditInvestigateConstants.orgSum2)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.qryTimes1,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.recordSum1)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.qryTimes2,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.recordSum2)
											.getValue());
							reqMap.put(CreditInvestigateConstants.qryTimes3, bizObject.getAttribute(CreditInvestigateConstants.recordSum3).getValue());
							reqMap.put(
									CreditInvestigateConstants.qryTimes2y1,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.towYearRecordSum1)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.qryTimes2y2,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.towYearRecordSum2)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.qryTimes2y3,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.towYearRecordSum3)
											.getValue());
							creditInvestigateService
									.insertCreditReportQueryBrief(reqMap);
						} catch (Exception e) {
							logger.error("reportQueryBrief error:" + e);
							flag = 1;
						}
					}
				}
			}

			ReportChapter recordDetail = queryRecord
					.getChapterByName("RecordDetail");
			if (recordDetail != null) {
				List<BizObject> recordDetailList = recordDetail.getContent();
				if (recordDetailList != null) {
					for (BizObject bizObject : recordDetailList) {
						try {
							Map<String, Object> reqMap = Maps.newHashMap(); reqMap.put("queryId", queryId);
							reqMap.put(
									CreditInvestigateConstants.reportNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.ReportNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.serialNo,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.SerialNo)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.queryDate,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.QueryDate)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.querier,
									bizObject.getAttribute(
											CreditInvestigateConstants.Querier)
											.getValue());
							reqMap.put(
									CreditInvestigateConstants.reason,
									bizObject
											.getAttribute(
													CreditInvestigateConstants.QueryReason)
											.getValue());
							creditInvestigateService
									.insertCreditReportQueryDetail(reqMap);
						} catch (Exception e) {
							logger.error("reportDetail Error:" + e);
							flag = 1;
						}
					}
				}
			}
			logger.debug("完成征信报告入库,删除格式化征信报告");

		} catch (Exception e) {
			logger.error("征信异常:", e);
			resMap.put("respCode", "01");
			resMap.put("respMsg", "征信失败");
			
			return resMap;
		}
		if(deleteFile != null){
			deleteFile.deleteOnExit();
		}
		
		if(flag == 1){

				logger.debug(reportNo + "征信数据没有入库完整");
				
				Map<String, Object> reqMap = Maps.newHashMap();
				/*reqMap.put("reportNo", reportNo);
				//1代表不完整
				reqMap.put("completeFlag", "1");
				creditInvestigateService.updateCreditBriefMap(reqMap);*/
				//直接除0抛异常
				int a = 1/0;
			resMap.put("respCode", "01");
			resMap.put("respMsg", "征信数据入库失败");
			return resMap;
		}else{
			if(reportNo != null){
				logger.debug(reportNo + "征信数据入库完整");
				Map<String, Object> reqMap = Maps.newHashMap();
				reqMap.put("reportNo", reportNo);
				//0代表完整
				reqMap.put("completeFlag", "0");
				creditInvestigateService.updateCreditBriefMap(reqMap);
			}
			resMap.put("respCode", "00");
			resMap.put("respMsg", "征信成功");
			return resMap;
		}
		
		
	}

	// 银商状态转义
	private static ImmutableMap<String, String> umStatusMap = new ImmutableMap.Builder<String, String>()
			.put("10", "04").put("20", "04").put("21", "03").put("30", "04")
			.put("31", "04").put("32", "03").put("40", "04").put("41", "04")
			.put("90", "05").put("91", "01").put("92", "01").put("93", "02")
			.build();

	@Override
	public Map<String, Object> getCreditApplyDetailByShmerno(String stdshno,
			String shmerno, String startnum, String recnum, String channel) {
		try {
			Map<String, Object> respMap = Maps.newHashMap();
			
			List<Map<String, Object>> respList = loanPosCreditApplyService
					.queryChannelDetail(stdshno, shmerno, channel, null,
							null);
			logger.debug("返回的list信息为"+respList);
			Long respCountLong = loanPosCreditApplyService.countChannelDetail(
					stdshno, shmerno, channel);
			logger.debug("返回的记录数为"+respCountLong);
			if (respCountLong == 0) {
				respMap.put("totalnum", "0");
				respMap.put("retnum", "0");
			} else {
				respMap.put("totalnum", "1");
				respMap.put("retnum", "1");
			}
			for (Map<String, Object> map : respList) {
				map.put("addittype", "");
				map.put("additspec", "");
				map.put("imgadditdetail", "");
				map.put("refusereason", "");
				map.put("appmaxcred", "");
				map.put("apptterm", "");
				map.put("interate", "");
				map.put("appenddate", "");
				map.put("industrytypeid", "");
				map.put("retukind1", StatusChannelMapFactory.getChannelQueryReturnKind(channel, (String)map.get("retukind1")));
				map.put("apprstate",
						umStatusMap.get((String) map.get("apprstate")));
				if (map.get("weixinno") == null) {
					map.put("weixinno", "");
				}
				if (map.get("qqno") == null) {
					map.put("qqno", "");
				}
				if (map.get("email") == null) {
					map.put("email", "");
				}

				if (map.get("relaTel") == null) {
					map.put("relatel", "");
				}

				if (map.get("familycustname") == null) {
					map.put("familycustname", "");
				}

				if (map.get("matepaperkind") == null) {
					map.put("matepaperkind", "");
				}
				if (map.get("matepaperid") == null) {
					map.put("matepaperid", "");
				}
				if (map.get("matemobilephone") == null) {
					map.put("matemobilephone", "");
				}
				if (map.get("relacustname") == null) {
					map.put("relacustname", "");
				}
				if (map.get("relakind") == null) {
					map.put("relakind", "");
				}
				if (map.get("relamobilephone") == null) {
					map.put("relamobilephone", "");
				}

			}
			respMap.put("rows", respList);
			return respMap;

		} catch (Exception e) {
			logger.error("查询申请信息异常:" + e);
			Map<String, Object> reqMap = Maps.newHashMap();
			return Maps.newHashMap();
		}
	}

	@Override
	public void deletePosSerial(String loanId) {
		loanPosCreditApplyService.deletePosSerial(loanId);
	}

	@Override
	public Long countPosSerials(String loanId) {
		return loanPosCreditApplyService.countPosSerials(loanId);
	}

	@Override
	public boolean getUmImg(Map<String, Object> reqMap) {
		
		String webUrl = SysConfigFactory.getInstance().getService("basicService").getPropertyValue("webUrl");
		
		// 申请流水号
    	String loanid1 = (String) reqMap.get("loanid");
    	String channel = (String) reqMap.get("channel");
    			
    	// 取得申请信息
    	TCreditApply ca = daoCA.selectOne(loanid1);
		// 申请信息不存在
		if (ca == null) {
			logger.debug("没有该申请："+loanid1);
			return false;
		}
		// 影像资料文件包名
		String imagefilepackagename1 = (String) reqMap
						.get("imagefilepackagename");
		logger.debug("loanid=" + loanid1);
		logger.debug("channel="+channel);
		logger.debug("imagefilepackagename=" + imagefilepackagename1);
		// 其他字段校验
		// ftp
		ParamReqBean paramReqBean = new ParamReqBean();
		paramReqBean.setApplyNo(loanid1);
		paramReqBean.setCustId(ca.getCustId());
		logger.debug("CustId=" + ca.getCustId());
		paramReqBean.setVersion(ftp_version);
		paramReqBean.setBizType(ftp_biztype);
		paramReqBean.setTransType(ftp_transtype);
		paramReqBean.setKeyValue(
				ftp_map_key_instno,
				channel);
		paramReqBean.setKeyValue(
				ftp_map_key_prefix,
				ftp_prefix_app);
		paramReqBean.setKeyValue(
				ftp_map_key_remotefilename,
				imagefilepackagename1);
		paramReqBean.setKeyValue(
				ftp_map_key_LocalSubFolderNameDef,
				loanid1);
		logger.debug("下载影像资料渠道为:" + channel);
		if("02".equals(ca.getLoanType())){
			logger.debug("进入自动下载征信报告模式");
			paramReqBean.setKeyValue(ftp_map_key_url,
					webUrl + ftp_map_key_returnUrl_download_file2);
		}else if("01".equals(ca.getLoanType())){
			paramReqBean.setKeyValue(ftp_map_key_url,
					webUrl + ftp_map_key_returnUrl_download_file);
			
		}
		ParamResBean paramResBean = HttpClient.send(paramReqBean);
		if (paramResBean == null) {
			logger.error(loanid1+"下载影像资料失败");
			return false;
		} else {
			logger.error(loanid1+"结果:"+paramResBean.getRespCode()+"  "+paramResBean.getRespMsg());
			if("000".equals(paramResBean.getRespCode())){
				return true;
			}else{
				return false;
			}
		}
	}
	
	@Override
    public boolean getImgSync(Map<String, Object> reqMap) {
        // 申请流水号
        String loanid1 = (String) reqMap.get("loanid");
        String channel = (String) reqMap.get("channel");
        // 取得申请信息
        TCreditApply ca = daoCA.selectOne(loanid1);
        // 申请信息不存在
        if (ca == null) {
            logger.debug("没有该申请："+loanid1);
            return false;
        }
        // 影像资料文件包名
        String imagefilepackagename1 = (String) reqMap
                        .get("imagefilepackagename");
        logger.debug("loanid=" + loanid1);
        logger.debug("imagefilepackagename=" + imagefilepackagename1);
        // 其他字段校验
        // ftp
        ParamReqBean paramReqBean = new ParamReqBean();
        paramReqBean.setApplyNo(loanid1);
        paramReqBean.setKeyValue(
                ftp_map_key_instno,
                channel);
        paramReqBean.setCustId(ca.getCustId());
        logger.debug("CustId=" + ca.getCustId());
        paramReqBean.setVersion(ftp_version);
        paramReqBean.setBizType(ftp_biztype);
        paramReqBean.setTransType(ftp_transtype_sync);
        paramReqBean.setKeyValue(
                ftp_map_key_prefix,
                ftp_prefix_app);
        paramReqBean.setKeyValue(
                ftp_map_key_remotefilename,
                imagefilepackagename1);
        paramReqBean.setKeyValue(
                ftp_map_key_LocalSubFolderNameDef,
                loanid1);
        ParamResBean paramResBean = HttpClient.send(paramReqBean);
        if (paramResBean == null) {
            logger.error(loanid1+"下载影像资料失败");
            return false;
        } else {
            logger.error(loanid1+"结果:"+paramResBean.getRespCode()+"  "+paramResBean.getRespMsg());
            if("000".equals(paramResBean.getRespCode())){
                return true;
            }else{
                return false;
            }
        }
    }
	
	public List<Map<String, Object>> selectSelectiveMap(Map<String, Object> reqMap){
	    return tCreditApplyDao.selectSelectiveMap(reqMap);
	    
	}
	
	public Long selectSelectiveCount(Map<String, Object> reqMap){
        return tCreditApplyDao.countCreitApply(reqMap);
        
    }

	@Override
	public Map<String, String> getAllBankMapByChannel(String channel) {
		Map<String, String> resMap = Maps.newHashMap();
		List<Map<String, Object>> bankList = loanPosCreditApplyService.getBankMapList(channel, null);
		for(Map<String, Object> map : bankList){
			resMap.put((String)map.get("bankName"), (String)map.get("mapBankId"));
		}
		return resMap;
	}

	@Override
	public Map<String, Object> uploadCreditInvestHtml(Map<String, Object> reqMap) {
		
		String htmlUrl = SysConfigFactory.getInstance().getService("basicService").getPropertyValue("fsUrl");
		
		Map<String, Object> resultMap = Maps.newHashMap();
		String certNo = (String)reqMap.get("certNo");
		String channel = (String)reqMap.get("channel");
		logger.info("====================开始"+certNo+"的征信报告打包上传");
		if(StringUtil.isEmpty(certNo)){
			resultMap.put("respCode", "1");
			resultMap.put("respMsg", "身份证号为空");
			return resultMap;
		}
		Map<String, Object> queryMap = Maps.newHashMap();
		queryMap.put("certType", "身份证");
		queryMap.put("certNo", certNo);
		try{
			TCreditReportBrief brief = tCreditReportBriefDao.selectOneByCertNo(queryMap);
			if(brief != null && StringUtil.isNotEmpty(brief.getFormatHtml())){
				//拉取征信报告
				String fileName = brief.getFormatHtml().substring(brief.getFormatHtml().lastIndexOf("/") + 1, brief.getFormatHtml().length());
				logger.info("征信报告位置为：" + fileName);
				String zipUploadFolder = System.getProperty("java.io.tmpdir") + "/zipupload";
				File zipUploadFolderFile = new File(zipUploadFolder);
				if(!zipUploadFolderFile.exists()){
					zipUploadFolderFile.mkdir();
				}
				String destFileName = System.getProperty("java.io.tmpdir") + "/zipupload/"
						+ fileName;
				logger.info("征信报告全路径为：" + htmlUrl + brief.getFormatHtml());
				logger.info("下载路径为：" + destFileName);
				if(HttpUtil.getHttpFile(htmlUrl + brief.getFormatHtml(), destFileName)){
					logger.info("拉取征信报告成功");
					String pwd = PwdUtil.getTcZipPwd(certNo);
					logger.info("本地缓存地址："+ destFileName + ", 密码为:" + pwd);
					//接着打包文件
					String currTime = "_" + IdUtil.getCreditInvestigateId();
					if(ZipUtils.zipEncrypt(destFileName.replace(".html", currTime+".zip"), destFileName, pwd)){
						logger.info("征信报告打包成功");
						//调ftp上传
						HFTPFile ftpFile = new HFTPFile();
						InputStream is = new FileInputStream(destFileName.replace(".html", currTime+".zip"));
						
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
				        byte[] b= new byte[2048];
				        while((is.read(b)) != -1){
				            bos.write(b);
				        }
				        byte[] bytes = bos.toByteArray();
				        
				        bos.flush();
				        bos.close();
				        is.close();
						ftpFile.setData(bytes);
						ftpFile.setName(fileName.replace(".html", currTime+".zip"));
						logger.info("getFtpCode开始");
						String ftpCode = FtpCodeFactory.getFtpCode(channel);
						logger.info("getFtpCode结束");
						ParamResBean resBean =  ftpTransUpload.uploadFile(ftpCode, ftpFile);
						logger.info("uploadFile结束");
						if("000".equals(resBean.getRespCode())){
							logger.info("ftp上传成功");
							resultMap.put("respCode", "0");
							resultMap.put("zipFileName", fileName.replace(".html", currTime+".zip"));
							resultMap.put("zipFilePwd", pwd);
							resultMap.put("respMsg", "操作成功");
							//删除该文件
							File deleteFile = new File(destFileName);
							File deleteZipFile = new File(destFileName.replace(".html",currTime+ ".zip"));
							deleteFile.deleteOnExit();
							deleteZipFile.deleteOnExit();
							
						}else{
							logger.info("ftp上传失败");
							resultMap.put("respCode", "1");
							resultMap.put("respMsg", "ftp上传失败");
							return resultMap;
						}
					}else{
						logger.error("征信报告打包异常");
						resultMap.put("respCode", "1");
						resultMap.put("respMsg", "征信报告打包异常");
						return resultMap;
					}
				}else{
					resultMap.put("respCode", "1");
					resultMap.put("respMsg", "获取征信报告失败");
					return resultMap;
				}
				
			}else{
				resultMap.put("respCode", "1");
				resultMap.put("respMsg", "征信报告不存在");
			}
			return resultMap;
			
		}catch(Exception e){
				logger.error("发生异常:",e);
				resultMap.put("respCode", "1");
				resultMap.put("respMsg", "发生异常");
				return resultMap;
		}
	}
	
	
	@Transactional (readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor=Exception.class)
	private void updateApplyWithTrack(Map<String, Object> creMap,
			Map<String, Object> custMap, Map<String, Object> posCustMap,
			Map<String, Object> bankMap, Map<String, Object> relMap,
			String userName, boolean sync) {
		/*更新申请*/
		TCreditApplyTracker applyTrack = new TCreditApplyTracker((String)creMap.get("loanId"));
		applyTrack.updateWithTrack(creMap, userName);
		/*更新客户*/
		TCustomerTracker customerTrack = new TCustomerTracker((String)custMap.get("custId"));
		customerTrack.updateWithTrack(custMap, userName);
		/*更新商户*/
		TPosCustomerTracker posCustTrack = new TPosCustomerTracker((String)posCustMap.get("posCustId"));
		posCustTrack.updateWithTrack(posCustMap, userName);
		/*亲属*/
		TRelativeTracker relaTrack = new TRelativeTracker((String)relMap.get("relativeId"));
		relaTrack.updateWithTrack(relMap, userName);
		/*银行卡信息*/
		TBankAcntTracker bankTrack = new TBankAcntTracker((String)bankMap.get("bankAccno"),(String)creMap.get("loanId"));
		bankTrack.updateWithTrack(bankMap, userName);
		
		if(sync){
			/*生成更新同步消息*/
			if(applyTrack.getTrakeIssues().containsKey("custName") 					//商户名称
					||applyTrack.getTrakeIssues().containsKey("posCustName")		//客户姓名
					||customerTrack.getTrakeIssues().containsKey("mobilePhone")		//手机
					||customerTrack.getTrakeIssues().containsKey("tel")				//办公电话
					||customerTrack.getTrakeIssues().containsKey("familyCustName")	//配偶姓名
					||customerTrack.getTrakeIssues().containsKey("matePapterId")	//配偶证件号码
					||customerTrack.getTrakeIssues().containsKey("mateMobilePhone")	//配偶手机
					){
			
				try {
					POSEventsSource source = new POSEventsSource();
			        source.setAttribute(POSEvent.监听事件名称_异步通知, NotifyCab.异步通知_申请变更同步);
					source.setAttribute("loanId", applyTrack.getBizNo());
//					source.setAttribute("custId", customerTrack.getBizNo());
//					source.setAttribute("bankAccno", bankTrack.getBizNo());
			        source.addListener(new NtfListener());
					source.notifyEvent();
				} catch (EventException e) {
					logger.error("申请更新同步通知生成失败.",e);
				}
			}
		}
	}
	
	@Override
	public void updateApplyWithTrack(Map<String, Object> creMap,
			Map<String, Object> custMap, Map<String, Object> posCustMap,
			Map<String, Object> bankMap, Map<String, Object> relMap,
			String user) {
		
		updateApplyWithTrack(creMap, custMap, posCustMap, bankMap, relMap, user, false);
	}
	
	@Override
	public void updateApplyWithSync(Map<String, Object> creMap,
			Map<String, Object> custMap, Map<String, Object> posCustMap,
			Map<String, Object> bankMap, Map<String, Object> relMap, String user) {
		
		updateApplyWithTrack(creMap, custMap, posCustMap, bankMap, relMap, user, true);
	}

	@Override
	public boolean downloadContract(Map<String, Object> reqMap) {
		// 申请流水号
    	String loanid1 = (String) reqMap.get("loanid");
    	String channel = (String) reqMap.get("channel");
    			
    	// 取得申请信息
    	TCreditApply ca = daoCA.selectOne(loanid1);
		// 申请信息不存在
		if (ca == null) {
			logger.debug("没有该申请："+loanid1);
			return false;
		}
		
		//查询合同信息
		TContractManagement contract = tContractInfoDao.selectContractInfoByLoanId(loanid1);
		if(contract == null){
			logger.error("没有该申请对应的合同" + loanid1);
			return false;
		}
		
		// 影像资料文件包名
		String imagefilepackagename1 = (String) reqMap.get("imagefilepackagename");
		logger.debug("loanid=" + loanid1);
		logger.debug("imagefilepackagename=" + imagefilepackagename1);
		// 其他字段校验
		String webUrl = SysConfigFactory.getInstance().getService("basicService").getPropertyValue("webUrl");
		// ftp
		ParamReqBean paramReqBean = new ParamReqBean();
		paramReqBean.setApplyNo(loanid1);
		paramReqBean.setCustId(ca.getCustId());
//		logger.debug("CustId=" + ca.getCustId());
		paramReqBean.setVersion(ftp_version);
		paramReqBean.setBizType(ftp_biztype);
		paramReqBean.setTransType(ftp_transtype);
		paramReqBean.setKeyValue(ftp_map_key_instno,channel);
		paramReqBean.setKeyValue(ftp_map_key_prefix,"CNT");
		paramReqBean.setKeyValue(ftp_map_key_remotefilename,imagefilepackagename1);
		paramReqBean.setKeyValue(ftp_map_key_LocalSubFolderNameDef,	loanid1);
		paramReqBean.setKeyValue(ftp_map_key_url,
					webUrl + ftp_map_key_returnUrl_download_contract+"?contNo="+contract.getContNo()+"&imagefilename="+imagefilepackagename1);
		ParamResBean paramResBean = HttpClient.send(paramReqBean);
		if (paramResBean == null) {
			logger.error(loanid1+"下载影像资料失败");
			return false;
		} else {
			logger.error(loanid1+"结果:"+paramResBean.getRespCode()+"  "+paramResBean.getRespMsg());
			if("000".equals(paramResBean.getRespCode())){
				return true;
			}else{
				return false;
			}
		}
	}


    @Override
    public String getInChannelKindByLoanId(String loanId) {
        return tCreditApplyDao.selectInChannelKindByLoanId(loanId);
    }


	public Map<String, Object> queryRepaymentPlan(Map<String, Object> reqMap) {
		if(reqMap == null){
			return null;
		}
		
		return loanPosCreditApplyService.selectRepaymentPlayById(reqMap);
	}

    @Override
    public String getLoanType(String loanId) {
        return tCreditApplyDao.selectLoanTypeByLoanId(loanId);
    }

}