package com.hrbb.loan.channel.sms;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hrbb.loan.channel.Processer;
import com.hrbb.loan.pos.biz.backstage.inter.ISmsSenderBiz;
import com.hrbb.loan.pos.dao.entity.TSmsTemplate;
import com.hrbb.loan.pos.service.CommSmsTemplateService;
import com.hrbb.loan.pos.util.StringUtil;
import com.hrbb.sh.framework.SendSMMessageService;

/**
 * 短信下行处理.
 * 
 * @author xiongshaogang
 * @version $Id: SmsProcesser.java, v 0.1 2015年4月29日 下午7:47:15 xiongshaogang Exp
 *          $
 */
@Service("smsProcesser")
public class SmsProcesser extends Processer {

	private static Logger logger = LoggerFactory.getLogger(SmsProcesser.class);

	@Autowired
	private ISmsSenderBiz iSmsSenderBiz;

	@Autowired
	private CommSmsTemplateService commSmsTemplateService;

	@Resource(name = "hSmsService")
	private SendSMMessageService hSmsService;
	
	/**
	 * 通用短信发送.
	 * 
	 * @param mobile   手机号.
	 * @param smsContent    无模板短信内容, 模板发送传"".
	 * @param tempId   短信模板，无模板发送传"".
	 * @param paramsMap    有模板模板参数, 无模板发送传"".
	 * @param withTemplate     是否模板发送 true: 带模板.
	 * @param isEnforcement    是否强制发送 true: 强制.
	 * @return
	 */
	public boolean sendSms(String mobile, String smsContent, String tempId
	                       , Map<String, String> paramsMap, boolean withTemplate, boolean isEnforcement) {
	    boolean isSendSucc = false;
	    if (withTemplate) {
	        if (isEnforcement) {
                paramsMap.put("tmp", "insert");
            }
            isSendSucc = sendSmsByTemplate(mobile, tempId, paramsMap);
        } else {
            isSendSucc = sendSmsNoTemplate(mobile, smsContent);
            if (isEnforcement && !isSendSucc) {
                iSmsSenderBiz.insertSmsMessage("tempId", mobile, smsContent);
            }
        }
	    
	    return isSendSucc;
	}

	/**
	 * 有模板短信下行.
	 * 
	 * @param mobile
	 * @param tempId
	 * @param paramsMap
	 * @return
	 */
	public boolean sendSmsByTemplate(String mobile, String tempId,
			Map<String, String> paramsMap) {
		logger.info("in SmsProcesser ...");

		// 0. 校验
		if (StringUtil.isEmpty(mobile) && StringUtil.isEmpty(tempId)) {
			logger.error("关键参数未上送");
			setRespCode("0002");
			setRespMsg("关键参数未上送");
			return false;
		}

		// 1. 抽取短信模板
		TSmsTemplate tSmsTemplate = commSmsTemplateService
				.queryTSmsTemplateByTempId(tempId);
		if (tSmsTemplate == null) {
			logger.error("短信模板未配置");
			setRespCode("0001");
			setRespMsg("短信模板未配置");
			return false;
		}

		// 2. 翻译模板
		String smsContent = transfer(tSmsTemplate.getSmsContent(), paramsMap);

		// 3. 发送处理
		logger.info("短信前置下行请求 mobile=[" + mobile + "], smsContent=["
				+ smsContent + "]!");
		if (!hSmsService.sendSM(mobile, smsContent)) {
			logger.error("发送失败");
			
			// 发送失败插入t_sms_message
			if ("insert".equals(paramsMap.get("tmp"))) {
			    iSmsSenderBiz.insertSmsMessage(tSmsTemplate.getTempId(), mobile, smsContent);
            }
			
			setRespCode("0003");
			setRespMsg("发送失败");
			return false;
		}
		
		// 移掉无用入参
		paramsMap.remove("tmp");
		
		logger.info("发送成功");
		setRespCode("0000");
		setRespMsg("发送成功");
		logger.info("out SmsProcesser !");
		return true;
	}

	/**
	 * 无模板短信下行.
	 * 
	 * @param mobile
	 * @param smsContent
	 * @return
	 */
	public boolean sendSmsNoTemplate(String mobile, String smsContent) {
		logger.info("in SmsProcesser ...");

		// 0. 校验
		if (StringUtil.isEmpty(mobile) && StringUtil.isEmpty(smsContent)) {
			logger.error("关键参数未上送");
			setRespCode("0002");
			setRespMsg("关键参数未上送");
			return false;
		}

		boolean isSucceed = true;
		try {
			// 1. 发送
			if (!hSmsService.sendSM(mobile, smsContent)) {
				isSucceed = false;
			}
		} catch (Exception ex) {
			logger.warn("failed to send out SMS" + ex);
			isSucceed = false;
		}

		if (isSucceed) {
			logger.info("发送成功");
			setRespCode("0000");
			setRespMsg("发送成功");
			logger.info("out SmsProcesser !");
		} else {
			logger.error("发送失败");
			setRespCode("0003");
			setRespMsg("发送失败");
		}

		return isSucceed;
	}

	/**
	 * 翻译短信模板.
	 * 
	 * @param smsContent
	 * @param paramsMap
	 * @return
	 */
	private String transfer(String smsContent, Map<String, String> paramsMap) {

		if (StringUtil.isEmpty(smsContent)) {
			return null;
		}

		String tmpSmsContent = smsContent;

		if (paramsMap.isEmpty()) {
			return tmpSmsContent;
		}

		for (Map.Entry<String, String> en : paramsMap.entrySet()) {
			String key = en.getKey();
			logger.info("模板key = [" + key + "], value = [" + paramsMap.get(key)
					+ "]");
			tmpSmsContent = tmpSmsContent.replaceAll("%" + key + "%",
					paramsMap.get(key));
		}
		logger.info("模板翻译结果 ：[" + tmpSmsContent + "]");
		return tmpSmsContent;
	}
}
