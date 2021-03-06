package com.hrbb.loan.spi.RNT;

import org.springframework.stereotype.Service;

import com.hrbb.loan.spi.POSHService;
import com.hrbb.loan.spi.TC2.TC2AddCautionMoneyHService;

/**
 * T59保证金补足通知
 * 
 * @author
 * @version $Id: TC2AddCautionMoneyHService.java, v 0.1 2015-8-21 下午2:13:32 Exp
 *          $
 */
@Service("rnAddCautionMoney")
public class RNAddCautionMoneyHServiceImpl extends TC2AddCautionMoneyHService {
	@Override
	public String getChannel() {
		return POSHService.进件渠道_融360;
	}

	/**
	 * 前置加解密标签
	 * 
	 * @return
	 */
	@Override
	public String getEncryptedRender() {
		return "RNT";
	}
}
