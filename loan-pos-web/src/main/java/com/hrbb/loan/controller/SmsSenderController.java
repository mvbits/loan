package com.hrbb.loan.controller;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hrbb.loan.jms.sender.SmsMessageSender;
import com.hrbb.loan.pos.biz.backstage.inter.ISmsSenderBiz;
import com.hrbb.loan.pos.dao.entity.TSmsMessage;
import com.hrbb.loan.pos.service.constants.SmsTypeContants;

/**
 * @author zhangwei5@hrbb.com.cn
 * @date 2015年10月14日上午10:08:52 
 */
@Controller
@RequestMapping("sms")
public class SmsSenderController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SmsSenderController.class);
    private static final String TEMPID = "MNU999";
    
    @Autowired
    private ISmsSenderBiz smsSenderBiz;
    
    @Autowired
    @Qualifier("smsMessageSender")
    private SmsMessageSender smsMessageSender;
    
    /**
     * @param mobile 手机号
     * @param content 短信内容
     * @return 发送状态
     */
    @RequestMapping(value = "sendsingle",method = RequestMethod.POST)
    @ResponseBody
    public String sendSmsBySingle(@RequestParam String mobile,@RequestParam String content){
        //smsSenderBiz.insertSmsMessage(TEMPID, mobile, content);
        
        TSmsMessage smsMessage = new TSmsMessage();
        smsMessage.setTempId(TEMPID);
        smsMessage.setMobile(mobile);
        smsMessage.setSendContent(content);
        smsMessage.setRealtime(false);
        smsMessage.setNotifyType(SmsTypeContants.手动发送);
        smsMessageSender.sendMessage(smsMessage);
        return "发送成功";
    }
    
    /**
     * 上传excel文件，批量发送短信
     * @return 发送状态
     */
    @RequestMapping(value = "batchUpload", method = RequestMethod.POST)
    @ResponseBody
    public String sendSmsByBatch(MultipartHttpServletRequest request){
        MultipartFile excel = request.getFile("smsContent");
        if (excel.isEmpty()) {
            return "文件不能为空";
        }
        File file = new File(excel.getOriginalFilename());
        String filePath = null;
        try {
            excel.transferTo(file);
            filePath = file.getAbsolutePath();
            smsSenderBiz.batchSmsSend(filePath);
        } catch (IllegalStateException | IOException e) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(e));
            return "系统错误，处理失败";
        }finally{
            boolean flag = file.delete();
            if(!flag) LOGGER.error("批量短信发送上传文件无法删除，文件路劲filepath:"+filePath);
        }
        return "发送成功";
    }
}
