/**
 * 
 */
package com.hrbb.loan.pos.factory.msgr;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.hrbb.loan.pos.dao.entity.TMessage;
import com.hrbb.loan.pos.dao.entity.TMessageHist;
import com.hrbb.loan.pos.entity.SpringBeans;
import com.hrbb.loan.pos.service.LoanPosMessageService;

/**
 * <p>Title: AbsMessenger.java </p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (C) 2015 Hrbb Ltd. Co.</p> 
 *     
 * @author linzhaolin@hrbb.com.cn
 * @version 
 * @date Aug 26, 2015
 *
 * logs: 1. 
 */
public abstract class AbsMessenger implements MessengerService {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected Map<String, Object> rspResult = Maps.newHashMap();
	
	private TMessage message = null;
	
	private LoanPosMessageService loanPosMessageService;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7031776245930381383L;
	
	/* (non-Javadoc)
	 * @see com.hrbb.loan.pos.biz.bean.HInternalService#getRspResult()
	 */
	@Override
	public Map<String, Object> getRspResult() {
		return rspResult;
	}
	
	protected TMessage getMessage(){
		return message;
	}
	
	protected Map<String, Object> parseToMap(String message) throws Exception {
		Assert.notNull(message);

		if (StringUtils.isEmpty(message)) {
			throw new Exception("unable to take empty parameter");
		}

		Map<String, Object> properties = new HashMap<String, Object>();
		String[] tokens = message.split(",");
		if (tokens == null || tokens.length < 1) {
			throw new Exception(String.format("invalid token format %s", message));
		}

		for (String token : tokens) {
			String subTokens[] = token.split(":");
			if (subTokens == null || subTokens.length != 2) {
				throw new Exception(String.format("invalid subToken format %s",	token));
			}

			String key = subTokens[0].trim().replaceAll("\"", "");
			String value = subTokens[1].trim().replaceAll("\"", "");
			properties.put(key, value);
		}

		return properties;
	}
	
	public boolean execute(){
		try {
			if(!initProcess()){
				logger.error("消息推送服务初始化失败!");
				return false;
			}
		} catch (Exception e) {
			logger.error("初始化消息通知失败!",e);
			return false;
		}
		
		try {
			if(!executeSend()){
				logger.error("消息推送服务执行失败!");
				return false;
			}
		} catch (Exception e) {
			logger.error("消息通知执行失败!",e);
			return false;
		}
		
		loanPosMessageService = (LoanPosMessageService)SpringBeans.getBean("loanPosMessageService");
		try {
			if(!postProcess()){
				logger.error("消息推送后续处理失败!");
				return false;
			}
		} catch (Exception e) {
			logger.error("消息通知后续处理失败!",e);
			return false;
		}
		
		return true;
	}
	
	abstract boolean executeSend() throws Exception;
	
	abstract boolean initProcess() throws Exception;
	
	protected boolean postProcess() throws Exception{
		try{
			String msgInfo = getMessage().getMessageInfo();
			if(msgInfo!=null) msgInfo = msgInfo.replaceAll("\r\n", "");	//换行符处理
			
			TMessageHist tMessageHist = new TMessageHist();
			tMessageHist.setId(message.getId());
			tMessageHist.setMessageType(message.getMessageType());
			tMessageHist.setMessageInfo(msgInfo);
			tMessageHist.setMessageAddi(message.getMessageAddi());
			tMessageHist.setCustId(message.getCustId());
			tMessageHist.setLoanId(message.getLoanId());
			tMessageHist.setContNo(message.getContNo());
			tMessageHist.setListId(message.getListId());
			tMessageHist.setLoanAcNo(message.getLoanAcNo());
			tMessageHist.setCreateTime(message.getCreateTime());
			tMessageHist.setTimerDate(message.getTimerDate());
			tMessageHist.setStdshNo(message.getStdshNo());
			tMessageHist.setStdMerNo(message.getStdMerNo());
			tMessageHist.setChannel(message.getChannel());
			tMessageHist.setInChannelKind(message.getInChannelKind());
			
			loanPosMessageService.insertMessageHist(tMessageHist);
			loanPosMessageService.deleteById(message.getId());
			logger.info(">>>>>>发送成功，删除消息：id=" + message.getId());
			
		}catch(Exception e){
			logger.error("消息发送后续处理失败!",e);
			return false;
		}
		return true;
	}
	
	@Override
	public void addMessage(TMessage msg){
		this.message = msg;
	}

	@Override
	public String getListId(){
		return "issueid";
	}
}
