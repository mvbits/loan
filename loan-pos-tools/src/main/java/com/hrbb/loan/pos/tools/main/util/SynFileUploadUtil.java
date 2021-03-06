/**
 * 
 *	哈尔滨银行
 * Copyright (c) 2007-2015 HRBB,Inc.All Rights Reserved.
 */
package com.hrbb.loan.pos.tools.main.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hrbb.sh.framework.ftp.FtpTransUpload;
import com.hrbb.sh.framework.ftp.ParamResBean;
import com.hrbb.sh.framework.HServiceException;
import com.hrbb.sh.framework.ftpproxy.HFTPFile;

/**
 * 
 * @author marco
 * @version $Id: SynFileUpload.java, v 0.1 2015-4-28 下午2:52:13 marco Exp $
 */
public class SynFileUploadUtil {

	private final static Logger LOG = LoggerFactory
			.getLogger(SynFileUploadUtil.class);

	public static ResourceBundle toolsInfo = ResourceBundle
			.getBundle("toolsInfo");

	/**
	 * 上传文件
	 * 
	 * @param hftpService
	 * @param fileName
	 * @return
	 */
	public static boolean uploadFile(String fileName, String channelFtpIds) {

		// 读取客户端的Hessian代理工厂配置文件
		ApplicationContext contex = new ClassPathXmlApplicationContext(
				"remote-client.xml");
		// 获得客户端的Hessian代理工厂bean
		FtpTransUpload ftpTransUpload = (FtpTransUpload) contex
				.getBean("ftpTransUpload");

		// 取得文件
		LOG.info("取得文件=" + fileName);
		File file = new File(fileName);
		// 取得文件读入流
		InputStream zis;
		try {
			zis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			LOG.error("上传文件不存在！");
			return false;
		}
		LOG.info("读取文件...");
		// 获取文件大小
		long length = file.length();
		LOG.info("文件大小length=" + length);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] b = new byte[(int) length];
		try {
			int i = 0;
			while ((i = zis.read(b)) != -1) {
				bos.write(b);
			}
			LOG.info("读取文件成功。");
		} catch (IOException e) {
			LOG.error("读取文件失败！");
			return false;
		} finally {
			try {
				zis.close();
			} catch (IOException e) {
				LOG.info("zis释放失败.");
				zis = null;
			}
		}

		byte[] bytes = bos.toByteArray();
		LOG.info("准备上传...");
		// 上传文件
		HFTPFile hFTPFile = new HFTPFile();
		hFTPFile.setData(bytes);
		hFTPFile.setName(file.getName());
		LOG.info("file.getName()=" + file.getName());
		LOG.info("bytes.length=" + bytes.length);
		LOG.info("开始上传");

		// 渠道
		LOG.info("channelFtpIds=" + channelFtpIds);
		try {
			ParamResBean paramResBean = ftpTransUpload.uploadFile(
					channelFtpIds, hFTPFile);
			if (null != paramResBean.getRespCode()
					&& "000".equals(paramResBean.getRespCode())) {
				LOG.info("RespCode=" + paramResBean.getRespCode());
				LOG.info("上传文件成功。");
				return true;
			} else {
				LOG.error("RespCode=" + paramResBean.getRespCode());
				LOG.error("RespMsg=" + paramResBean.getRespMsg());
				LOG.error("上传文件失败！");
				return false;
			}
		} catch (HServiceException e) {
			LOG.error("上传文件失败！", e);
			return false;
		}
	}
}
