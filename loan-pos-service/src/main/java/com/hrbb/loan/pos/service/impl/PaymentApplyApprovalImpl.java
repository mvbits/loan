package com.hrbb.loan.pos.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hrbb.loan.pos.dao.TPaymentApplyApprovalDao;
import com.hrbb.loan.pos.dao.entity.TPaymentApply;
import com.hrbb.loan.pos.dao.entity.TPaymentApplyApproval;
import com.hrbb.loan.pos.service.IGenericConfigService;
import com.hrbb.loan.pos.service.PaymentApplyApprovalService;
import com.hrbb.loan.pos.util.DateUtil;
@Service("paymentApplyApprovalService")
public class PaymentApplyApprovalImpl implements PaymentApplyApprovalService {
	
	private Logger logger = LoggerFactory.getLogger(PaymentApplyApprovalService.class);
    
    @Autowired
    TPaymentApplyApprovalDao payemntApplyApprovalDao;

    @Autowired
    private IGenericConfigService genericConfigService;

    @Override
    public TPaymentApplyApproval selectOneByPayApplyIdAndBeforeStatus(Map<String, Object> map) {
       
        return payemntApplyApprovalDao.selectOneByPayApplyIdAndBeforeStatus(map);
    }

    @Override
    public int insert(TPaymentApplyApproval tp) {
        return 0;
    }

    @Override
    public int insertSelective(TPaymentApplyApproval tp) {
        return payemntApplyApprovalDao.insertSelective(tp);
    }

    @Override
    public int updateByPrimaryKeySelective(TPaymentApplyApproval tp) {
        return 0;
    }

    @Override
    public int updateByPrimaryKey(TPaymentApplyApproval tp) {
        return 0;
    }

    @Override
    public int update(TPaymentApplyApproval tp) {
        return 0;
    }

    @Override
    public int updateSelectiveMap(Map<String, Object> map) {
        return payemntApplyApprovalDao.updateSelectiveMap(map);
    }

    @Override
    public List<Map<String, Object>> queryPaymentApplyCurr(Map<String, Object> reqMap) {
        return payemntApplyApprovalDao.queryPaymentApplyCurr(reqMap);
    }

    @Override
    public Long countPaymentApplyCurr(Map<String, Object> reqMap) {
        return payemntApplyApprovalDao.countPaymentApplyCurr(reqMap);
    }
    
    @Override
    public List<Map<String, Object>> queryPaymentApplyFinished(Map<String, Object> reqMap) {
        return payemntApplyApprovalDao.queryPaymentApplyFinished(reqMap);
    }

    @Override
    public Long countPaymentApplyFinished(Map<String, Object> reqMap) {
        return payemntApplyApprovalDao.countPaymentApplyFinished(reqMap);
    }

	@Override
	public TPaymentApplyApproval selectApprOpinion(Map<String, Object> map, TPaymentApply paymentApply) {
		
		TPaymentApplyApproval payAppr = payemntApplyApprovalDao.selectApprOpinion(map);
		
		/*根据用款审核记录中的审核日期与当前日期做比较，针对结果进行修正*/
//		TPaymentApply paymentApply = (TPaymentApply)map.get("paymentApply");
		String payApplyId = paymentApply.getPayApplyId();
		String userName = (String)map.get("userName");
		
		if(payAppr==null){
        	payAppr = new TPaymentApplyApproval();		//首次执行审批时没有
        	payAppr.setPayApplyId(payApplyId);
        	payAppr.setApprovalName(userName);
        }
		Date currDate = new Date();
        if(payAppr.getApprBeginDate()==null){		//日期为空时取当日
        	payAppr.setApprBeginDate(currDate);
        }else{
        	if(payAppr.getApprBeginDate().compareTo(currDate)>0){	//日期小于当日时取当日
        		payAppr.setApprBeginDate(currDate);
        	}else{
        		//do nothing
        	}
        }
        
        //同时更新到期日
        try {
            if(paymentApply.getReturnType()!=null && paymentApply.getReturnType().equals("90")){    //利随本清 到期日跳过节假日
        	
    		    String temp = DateUtil.getRelativeDate(payAppr.getApprBeginDate(), 0, Integer.parseInt(paymentApply.getPayApplyTerm()), -1);
    			Date actualEndDate = DateUtil.parse2Date(temp, DateUtil.HRRB_SHORT_DATETIME_FORMAT);
    			/*利随本清到期日跳过到工作日*/
				String workingDate = genericConfigService.getNextWorkingDate(actualEndDate);
				actualEndDate = DateUtil.parse2Date(workingDate, DateUtil.HRRB_SHORT_DATETIME_FORMAT);
    			
				payAppr.setApprEndDate(actualEndDate);
				
    		}else{
    		    //非利随本清对月对日
    		    String temp = DateUtil.getRelativeDate(payAppr.getApprBeginDate(), 0, Integer.parseInt(paymentApply.getPayApplyTerm()), 0);
                Date actualEndDate = DateUtil.parse2Date(temp, DateUtil.HRRB_SHORT_DATETIME_FORMAT);
                
                payAppr.setApprEndDate(actualEndDate);
    		}
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("期望用款到期日转换失败!",e);
        }
        
		return payAppr;
	}

    @Override
    public int deleteByPayApplyId(String payApplyId) {
        return payemntApplyApprovalDao.deleteByPaymentApplyId(payApplyId);
    }
}
