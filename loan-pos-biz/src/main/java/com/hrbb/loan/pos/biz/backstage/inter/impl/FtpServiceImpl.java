/**
 * 
 *	哈尔滨银行
 * Copyright (c) 2007-2015 HRBB,Inc.All Rights Reserved.
 */
package com.hrbb.loan.pos.biz.backstage.inter.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.hrbb.loan.pos.biz.IFtpService;
import com.hrbb.loan.pos.biz.bean.Notify4Contract;
import com.hrbb.loan.pos.biz.bean.Upload2FileSystem;
import com.hrbb.loan.pos.factory.SysConfigFactory;
import com.hrbb.loan.pos.util.DateUtil;
import com.hrbb.loan.pos.util.ZipUtils;

/**
 * @author yida
 * @date 2015年11月5日 下午7:25:04
 */

@Service
public class FtpServiceImpl implements IFtpService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpServiceImpl.class);
    
    @Autowired
    @Qualifier("upload2FileSystem")
    private Upload2FileSystem u2fs;
    private static String webUrl;
    
    static {
        webUrl = SysConfigFactory.getInstance().getService("basicService").getPropertyValue("webUrl");
    }

    public File zip(File sourceFile,String fileDir,String loanId) {
        //创建打包文件临时目录
        if(null == fileDir){
            fileDir = System.getProperty("java.io.tmpdir");
        }
        String zipFilePath = new StringBuilder(fileDir).append(loanId).append("_").append(DateUtil.getNowTime("HHmmss")).append(ZipUtils.EXT).toString();
        File zipFile = new File(zipFilePath);
        Long beginTime = System.currentTimeMillis();
        // 影像资料打包
        try {
            ZipUtils.compress(sourceFile, zipFile);
            Long endTime = System.currentTimeMillis();
            LOGGER.debug("文件打包完成。耗时" + (endTime - beginTime) + "毫秒");
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(e));
            return null;
        }
        
        return zipFile;
    }

    public void upload(File file,String loanId) {
        InputStream is = null;
       try {
           is = new FileInputStream(file);
           
           ByteArrayOutputStream bos = new ByteArrayOutputStream();
           byte[] b= new byte[2048];
           while((is.read(b)) != -1){
               bos.write(b);
           }
           byte[] bytes = bos.toByteArray();
           
           bos.flush();
           bos.close();
           is.close();
           
           u2fs.setUploadFile(file);
           if (u2fs.execute()) {
               /* 上传成功后，提示归档 */
               Notify4Contract n4c = new Notify4Contract();
               n4c.setLoanId(loanId);
               n4c.setImagefilepackagename(file.getName());
               n4c.setUrl(webUrl);
               if (n4c.execute()) {
                   /* 暂时性文档归档备份, 运行稳定后去除 */
                   LOGGER.warn("文件上传归档成功");

               } else {
                   LOGGER.warn("文件提示归档失败");
               }

           } else {
               LOGGER.error("文件上传失败");
           }
       } catch (Exception e) {
           LOGGER.error("文件上传失败\n"+ExceptionUtils.getFullStackTrace(e));
       }finally{
           if(is != null){
               try{
                   is.close();
               }catch(Exception e){
                   LOGGER.error("关闭流异常\n"+ExceptionUtils.getFullStackTrace(e));
               }
           }
       }
    }

}
