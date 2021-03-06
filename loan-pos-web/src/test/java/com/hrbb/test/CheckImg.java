package com.hrbb.test;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.hrbb.loan.imgdata.validate.ImageValidate;

public class CheckImg {

	@Test
	public void test() {
		String instNo = "UM";
		String loanId = "LO201500241759187039262367530";
		String marrSign = "00";
		ImageValidate imageValidate = new ImageValidate();
		Map<String, String> dataMap = imageValidate.checkImage(loanId, instNo, marrSign);
		String RespCode = dataMap.get("RespCode");
		if ("001".equals(RespCode)) {
			System.out.println("影像资料尚未下载");
		} else if ("000".equals(RespCode)) {
			System.out.println("处理成功");
		} else {
			System.out.println(dataMap.get("RespMsg"));
		}
		
		
	}

}
