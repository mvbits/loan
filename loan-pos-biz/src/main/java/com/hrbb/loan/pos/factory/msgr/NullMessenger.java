/**
 * 
 */
package com.hrbb.loan.pos.factory.msgr;


/**
 * <p>Title: NullMessenger.java </p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (C) 2015 Hrbb Ltd. Co.</p> 
 *     
 * @author linzhaolin@hrbb.com.cn
 * @version 
 * @date Aug 26, 2015
 *
 * logs: 1. 
 */
public class NullMessenger extends AbsMessenger{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2734051696147059630L;

	@Override
	public String getChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEncryptedRander() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTransCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean executeSend() throws Exception {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected boolean initProcess() throws Exception {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	protected boolean postProcess() throws Exception{
		logger.info("渠道["+getMessage().getChannel()+"] [id="+getMessage().getId()+"] 不推送消息.");
		return super.postProcess();
	}

}
