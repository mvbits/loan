package com.hrbb.loan.timer;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.hrbb.loan.channel.sms.SmsProcesser;
import com.hrbb.loan.channel.user.UserProcesser;
import com.hrbb.loan.pos.dao.TReceiptInfoDao;
import com.hrbb.sh.framework.HServiceException;
import com.hrbb.ump.facade.bean.requests.user.UserInformationQueryRequest;
import com.hrbb.ump.facade.bean.responses.user.UserInfoQueryResponse;

/**
 * 消费贷 还款管理     2.6.1   到期提醒.
 * 1. 小于一年的短期贷款，消费贷业务处理系统自动在单笔借据的每一期的到期日或单笔借据的最后到期日之前7个和前3个自然日对客户进行短信提醒及APP端消息推送。
 * 2. 超过一年（含）的中长期贷款（该类贷款目前仅有等额本息的按月分期还款方式），在单笔借据的每一期的到期日前7个和前3个自然日对客户进行短信提醒及APP端消息推送，
 *    在单笔借据的最后到期日（即最后一期的到期日）前1个月、前7个和前3个自然日对客户进行短信提醒及APP端消息推送。
 * 3. 如有逾期，逾期次日（含）起，每7个自然日对客户进行短信提醒及APP端消息推送。
 * 
 * @author xiongshaogang
 * @version $Id: ZzSaleAppRepayReminderTimer.java, v 0.1 2015年5月12日 下午6:09:12 xiongshaogang Exp $
 */
@Service("zzSaleAppRepayReminderTimer")
public class ZzSaleAppRepayReminderTimer {
	
	private static final Logger logger = LoggerFactory.getLogger(ZzSaleAppRepayReminderTimer.class);
	
	@Autowired
	private TReceiptInfoDao tReceiptInfoDao;
	
	@Resource(name="userProcesser")
	private UserProcesser userProcesser;
	
	@Autowired
    @Qualifier("smsProcesser")
    private SmsProcesser smsProcesser;
	
	@Scheduled(cron="0 0 12 * * ?")
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class })
	public void run() throws HServiceException {
		logger.info("start ZzSaleAppRepayReminderTimer...");
		
		// 当前时间
		Calendar calendar = Calendar.getInstance();
		
		// 1. 短期贷款
		// 1.1 最后到期日7天
		calendar.add(Calendar.DAY_OF_YEAR, 7);
		Map<String, Object> queryMap = Maps.newHashMap();
		queryMap.put("endDate", DateFormatUtils.format(calendar, "yyyyMMdd"));
		List<Map<String, Object>> shortLoan7List = tReceiptInfoDao.selectListMapsByTimer(queryMap);
		if (shortLoan7List != null && shortLoan7List.size() > 0) {
		    for (Map<String, Object> map : shortLoan7List) {
		        
		        if (map.get("userid") == null) {
                    continue;
                }
		        
		        // 1.1.1 用户系统查询手机号
		        UserInformationQueryRequest userInformationQueryRequest = new UserInformationQueryRequest();
		        userInformationQueryRequest.setUserId(Integer.parseInt((String)map.get("userid")));
		        UserInfoQueryResponse userInformationQueryResponse = userProcesser.queryUserInfo(userInformationQueryRequest);
		        
		        if (userInformationQueryResponse == null || !"UMP000000".equals(userInformationQueryResponse.getResponseCode())) {
                    continue;
                }
		                
                // 1.1.2 短信发送
		        smsProcesser.sendSmsNoTemplate(userInformationQueryResponse.getMobile(), "您有一笔贷款7天后到期，请注意还款！");
                
                // 1.1.3 站内信息推送
		        userProcesser.createMessage(1, 5, "还款提醒", "您有一笔贷款7天后到期，请注意还款！", 1, 2, Integer.parseInt((String)map.get("userid")), "还款提醒");        
            }
        }
		
		// 1.2 最后到期日3天
		calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 3);
        queryMap = Maps.newHashMap();
        queryMap.put("endDate", DateFormatUtils.format(calendar, "yyyyMMdd"));
        List<Map<String, Object>> shortLoan3List = tReceiptInfoDao.selectListMapsByTimer(queryMap);
        if (shortLoan3List != null && shortLoan3List.size() > 0) {
            for (Map<String, Object> map : shortLoan3List) {
                
                if (map.get("userid") == null) {
                    continue;
                }
                
                // 1.1.1 用户系统查询手机号
                UserInformationQueryRequest userInformationQueryRequest = new UserInformationQueryRequest();
                userInformationQueryRequest.setUserId(Integer.parseInt((String)map.get("userid")));
                UserInfoQueryResponse userInformationQueryResponse = userProcesser.queryUserInfo(userInformationQueryRequest);
                
                if (userInformationQueryResponse == null || !"UMP000000".equals(userInformationQueryResponse.getResponseCode())) {
                    continue;
                }
                        
                // 1.1.2 短信发送
                smsProcesser.sendSmsNoTemplate(userInformationQueryResponse.getMobile(), "您有一笔贷款3天后到期，请注意还款！");
                
                // 1.1.3 站内信息推送
                userProcesser.createMessage(1, 5, "还款提醒", "您有一笔贷款3天后到期，请注意还款！", 1, 2, Integer.parseInt((String)map.get("userid")), "还款提醒");        
            }
        }
        
        // 1.3 最后到期日2天
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        queryMap = Maps.newHashMap();
        queryMap.put("endDate", DateFormatUtils.format(calendar, "yyyyMMdd"));
        List<Map<String, Object>> shortLoan2List = tReceiptInfoDao.selectListMapsByTimer(queryMap);
        if (shortLoan2List != null && shortLoan2List.size() > 0) {
            for (Map<String, Object> map : shortLoan2List) {
                
                if (map.get("userid") == null) {
                    continue;
                }
                
                // 1.1.1 用户系统查询手机号
                UserInformationQueryRequest userInformationQueryRequest = new UserInformationQueryRequest();
                userInformationQueryRequest.setUserId(Integer.parseInt((String)map.get("userid")));
                UserInfoQueryResponse userInformationQueryResponse = userProcesser.queryUserInfo(userInformationQueryRequest);
                
                if (userInformationQueryResponse == null || !"UMP000000".equals(userInformationQueryResponse.getResponseCode())) {
                    continue;
                }
                        
                // 1.1.2 短信发送
                smsProcesser.sendSmsNoTemplate(userInformationQueryResponse.getMobile(), "您有一笔贷款2天后到期，请注意还款！");
                
                // 1.1.3 站内信息推送
                userProcesser.createMessage(1, 5, "还款提醒", "您有一笔贷款2天后到期，请注意还款！", 1, 2, Integer.parseInt((String)map.get("userid")), "还款提醒");        
            }
        }
        
        // 1.4 最后到期日1天
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        queryMap = Maps.newHashMap();
        queryMap.put("endDate", DateFormatUtils.format(calendar, "yyyyMMdd"));
        List<Map<String, Object>> shortLoan1List = tReceiptInfoDao.selectListMapsByTimer(queryMap);
        if (shortLoan1List != null && shortLoan1List.size() > 0) {
            for (Map<String, Object> map : shortLoan1List) {
                
                if (map.get("userid") == null) {
                    continue;
                }
                
                // 1.1.1 用户系统查询手机号
                UserInformationQueryRequest userInformationQueryRequest = new UserInformationQueryRequest();
                userInformationQueryRequest.setUserId(Integer.parseInt((String)map.get("userid")));
                UserInfoQueryResponse userInformationQueryResponse = userProcesser.queryUserInfo(userInformationQueryRequest);
                
                if (userInformationQueryResponse == null || !"UMP000000".equals(userInformationQueryResponse.getResponseCode())) {
                    continue;
                }
                        
                // 1.1.2 短信发送
                smsProcesser.sendSmsNoTemplate(userInformationQueryResponse.getMobile(), "您有一笔贷款1天后到期，请注意还款！");
                
                // 1.1.3 站内信息推送
                userProcesser.createMessage(1, 5, "还款提醒", "您有一笔贷款1天后到期，请注意还款！", 1, 2, Integer.parseInt((String)map.get("userid")), "还款提醒");        
            }
        }
        
        // 2. 长期贷款
        // 2.0 最后到期日1个月
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        queryMap = Maps.newHashMap();
        queryMap.put("endDate", DateFormatUtils.format(calendar, "yyyyMMdd"));
        List<Map<String, Object>> longLoan30List = tReceiptInfoDao.selectListMapsByTimer(queryMap);
        if (longLoan30List != null && longLoan30List.size() > 0) {
            for (Map<String, Object> map : longLoan30List) {
                
                if (map.get("userid") == null) {
                    continue;
                }
                
                // 1.1.1 用户系统查询手机号
                UserInformationQueryRequest userInformationQueryRequest = new UserInformationQueryRequest();
                userInformationQueryRequest.setUserId(Integer.parseInt((String)map.get("userid")));
                UserInfoQueryResponse userInformationQueryResponse = userProcesser.queryUserInfo(userInformationQueryRequest);
                
                if (userInformationQueryResponse == null || !"UMP000000".equals(userInformationQueryResponse.getResponseCode())) {
                    continue;
                }
                        
                // 1.1.2 短信发送
                smsProcesser.sendSmsNoTemplate(userInformationQueryResponse.getMobile(), "您有一笔贷款一个月后到期，请注意还款！");
                
                // 1.1.3 站内信息推送
                userProcesser.createMessage(1, 5, "还款提醒", "您有一笔贷款一个月后到期，请注意还款！", 1, 2, Integer.parseInt((String)map.get("userid")), "还款提醒");        
            }
        }
        
        // 2.1 最后到期日7天
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        queryMap = Maps.newHashMap();
        queryMap.put("endDate", DateFormatUtils.format(calendar, "yyyyMMdd"));
        List<Map<String, Object>> longLoan7List = tReceiptInfoDao.selectListMapsByTimer(queryMap);
        if (longLoan7List != null && longLoan7List.size() > 0) {
            for (Map<String, Object> map : longLoan7List) {
                
                if (map.get("userid") == null) {
                    continue;
                }
                
                // 1.1.1 用户系统查询手机号
                UserInformationQueryRequest userInformationQueryRequest = new UserInformationQueryRequest();
                userInformationQueryRequest.setUserId(Integer.parseInt((String)map.get("userid")));
                UserInfoQueryResponse userInformationQueryResponse = userProcesser.queryUserInfo(userInformationQueryRequest);
                
                if (userInformationQueryResponse == null || !"UMP000000".equals(userInformationQueryResponse.getResponseCode())) {
                    continue;
                }
                        
                // 1.1.2 短信发送
                smsProcesser.sendSmsNoTemplate(userInformationQueryResponse.getMobile(), "您有一笔贷款7天后到期，请注意还款！");
                
                // 1.1.3 站内信息推送
                userProcesser.createMessage(1, 5, "还款提醒", "您有一笔贷款7天后到期，请注意还款！", 1, 2, Integer.parseInt((String)map.get("userid")), "还款提醒");        
            }
        }
        
        // 1.2 最后到期日3天
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 3);
        queryMap = Maps.newHashMap();
        queryMap.put("endDate", DateFormatUtils.format(calendar, "yyyyMMdd"));
        List<Map<String, Object>> longLoan3List = tReceiptInfoDao.selectListMapsByTimer(queryMap);
        if (longLoan3List != null && longLoan3List.size() > 0) {
            for (Map<String, Object> map : longLoan3List) {
                
                if (map.get("userid") == null) {
                    continue;
                }
                
                // 1.1.1 用户系统查询手机号
                UserInformationQueryRequest userInformationQueryRequest = new UserInformationQueryRequest();
                userInformationQueryRequest.setUserId(Integer.parseInt((String)map.get("userid")));
                UserInfoQueryResponse userInformationQueryResponse = userProcesser.queryUserInfo(userInformationQueryRequest);
                
                if (userInformationQueryResponse == null || !"UMP000000".equals(userInformationQueryResponse.getResponseCode())) {
                    continue;
                }
                        
                // 1.1.2 短信发送
                smsProcesser.sendSmsNoTemplate(userInformationQueryResponse.getMobile(), "您有一笔贷款3天后到期，请注意还款！");
                
                // 1.1.3 站内信息推送
                userProcesser.createMessage(1, 5, "还款提醒", "您有一笔贷款3天后到期，请注意还款！", 1, 2, Integer.parseInt((String)map.get("userid")), "还款提醒");        
            }
        }
        
        // 1.3 最后到期日2天
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        queryMap = Maps.newHashMap();
        queryMap.put("endDate", DateFormatUtils.format(calendar, "yyyyMMdd"));
        List<Map<String, Object>> longLoan2List = tReceiptInfoDao.selectListMapsByTimer(queryMap);
        if (longLoan2List != null && longLoan2List.size() > 0) {
            for (Map<String, Object> map : longLoan2List) {
                
                if (map.get("userid") == null) {
                    continue;
                }
                
                // 1.1.1 用户系统查询手机号
                UserInformationQueryRequest userInformationQueryRequest = new UserInformationQueryRequest();
                userInformationQueryRequest.setUserId(Integer.parseInt((String)map.get("userid")));
                UserInfoQueryResponse userInformationQueryResponse = userProcesser.queryUserInfo(userInformationQueryRequest);
                
                if (userInformationQueryResponse == null || !"UMP000000".equals(userInformationQueryResponse.getResponseCode())) {
                    continue;
                }
                        
                // 1.1.2 短信发送
                smsProcesser.sendSmsNoTemplate(userInformationQueryResponse.getMobile(), "您有一笔贷款2天后到期，请注意还款！");
                
                // 1.1.3 站内信息推送
                userProcesser.createMessage(1, 5, "还款提醒", "您有一笔贷款2天后到期，请注意还款！", 1, 2, Integer.parseInt((String)map.get("userid")), "还款提醒");        
            }
        }
        
        // 1.4 最后到期日1天
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        queryMap = Maps.newHashMap();
        queryMap.put("endDate", DateFormatUtils.format(calendar, "yyyyMMdd"));
        List<Map<String, Object>> longLoan1List = tReceiptInfoDao.selectListMapsByTimer(queryMap);
        if (longLoan1List != null && longLoan1List.size() > 0) {
            for (Map<String, Object> map : longLoan1List) {
                
                if (map.get("userid") == null) {
                    continue;
                }
                
                // 1.1.1 用户系统查询手机号
                UserInformationQueryRequest userInformationQueryRequest = new UserInformationQueryRequest();
                userInformationQueryRequest.setUserId(Integer.parseInt((String)map.get("userid")));
                UserInfoQueryResponse userInformationQueryResponse = userProcesser.queryUserInfo(userInformationQueryRequest);
                
                if (userInformationQueryResponse == null || !"UMP000000".equals(userInformationQueryResponse.getResponseCode())) {
                    continue;
                }
                        
                // 1.1.2 短信发送
                smsProcesser.sendSmsNoTemplate(userInformationQueryResponse.getMobile(), "您有一笔贷款1天后到期，请注意还款！");
                
                // 1.1.3 站内信息推送
                userProcesser.createMessage(1, 5, "还款提醒", "您有一笔贷款1天后到期，请注意还款！", 1, 2, Integer.parseInt((String)map.get("userid")), "还款提醒");        
            }
        } 
		
		// 3. 逾期
        queryMap = Maps.newHashMap();
        queryMap.put("OverdueFlag", "1");
        List<Map<String, Object>> overdueList = tReceiptInfoDao.selectListMapsByTimer(queryMap);
        if (overdueList != null && overdueList.size() > 0) {
            for (Map<String, Object> map : overdueList) {
                
                if (map.get("userid") == null) {
                    continue;
                }
                
                // 1.1.1 用户系统查询手机号
                UserInformationQueryRequest userInformationQueryRequest = new UserInformationQueryRequest();
                userInformationQueryRequest.setUserId(Integer.parseInt((String)map.get("userid")));
                UserInfoQueryResponse userInformationQueryResponse = userProcesser.queryUserInfo(userInformationQueryRequest);
                
                if (userInformationQueryResponse == null || !"UMP000000".equals(userInformationQueryResponse.getResponseCode())) {
                    continue;
                }
                        
                // 1.1.2 短信发送
                smsProcesser.sendSmsNoTemplate(userInformationQueryResponse.getMobile(), "您有一笔贷款已经逾期，请注意还款！");
                
                // 1.1.3 站内信息推送
                userProcesser.createMessage(1, 5, "还款提醒", "您有一笔贷款已经逾期，请注意还款！", 1, 2, Integer.parseInt((String)map.get("userid")), "还款提醒");        
            }
        }
        
		logger.info("end ZzSaleAppRepayReminderTimer!");
	}
}
