package com.hrbb.loan.pos.biz.backstage.inter.impl.reader;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.hrbb.loan.pos.dao.entity.TBdExecutor;
import com.hrbb.loan.pos.service.BDManagementService;
import com.hrbb.loan.pos.util.excel.IRowReader;

/**
 * 展业人员批量导入 
 * 
 * @author chenjianqing
 * @version $Id: BDRowReader.java, v 0.1 2015年10月13日 上午10:51:41 chenjianqing Exp $
 */
public class BDRowReader implements IRowReader {

    public BDRowReader() {
    }

    //数据起始行
    protected int                     rowStart         = 2;

    //批处理数据条数
    protected int                     batchNum         = 2000;

    //待处理结果列表
    private List<Map<String, Object>> insertList       = null;

    //展业机构服务
    protected BDManagementService     bdManagementService;

    //reader对应excel版本
    private int                       readerVersion    = READER_VER_UNKOWN;

    //触发器异常
    private boolean                   exceptionTrigger = false;

    @Override
    public void getRows(int sheetIndex, int curRow, List<String> rowlist) {
        /*
        0.渠道简称
        1.所属服务商名称
        2.姓名
        3.身份证件号码
        4.手机号码
        5.常用邮箱
        6.家庭住址
        */
        if (curRow < (rowStart - 1))
            return;
        if (exceptionTrigger)
            return;
        if(processRow(rowlist) == null){
            exceptionTrigger = true;
        }else{
            insertList.add(processRow(rowlist));
        }
        if (insertList.size() >= batchNum) {
            //批量处理
            for (Map<String,Object> insertMap : insertList) {
                //构建对象
                TBdExecutor executor = new TBdExecutor();
                executor.setBelongOrgName((String)insertMap.get("belongOrgName"));
                executor.setMenberName((String)insertMap.get("menberName"));
                executor.setCertNo((String)insertMap.get("certNo"));
                executor.setContactNo((String)insertMap.get("contactNo"));
                executor.setEmail((String)insertMap.get("email"));
                executor.setAddress((String)insertMap.get("address"));
                executor.setActive("Y");
                bdManagementService.importExcelExecutor(executor);
            }
            insertList.clear();
        }

    }

    @Override
    public void setVersion(int ver) {

    }

    public int getVersion() {
        return readerVersion;
    }
    
    public void addHandleList(List<Map<String, Object>> list){
        insertList = list;
    }
    
    public boolean fireTrigger() {
        return exceptionTrigger;
    }
    
    public void addService(BDManagementService service){
        bdManagementService = service;
    }
    /**
     * 解析row
     * 
     * @param rowList
     * @return
     */
    public Map<String, Object> processRow(List<String> rowList) {
        Map<String, Object> insertMap = Maps.newHashMap();
        if(rowList == null || rowList.isEmpty()){
            return null;
        }
        int size = rowList.size();
        while(size < 7){
            rowList.add(null);
            size++;
        }
        insertMap.put("belongOrgName", rowList.get(0));
        insertMap.put("menberName", rowList.get(2));
        insertMap.put("certNo", rowList.get(3));
        insertMap.put("contactNo", rowList.get(4));
        insertMap.put("email", rowList.get(5));
        insertMap.put("address", rowList.get(6));
        return insertMap;
    }

}
