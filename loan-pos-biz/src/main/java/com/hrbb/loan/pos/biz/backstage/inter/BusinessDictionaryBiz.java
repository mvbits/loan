package com.hrbb.loan.pos.biz.backstage.inter;

import java.util.List;
import java.util.Map;

import com.hrbb.loan.pos.dao.entity.TBusinessDictionary;

public interface BusinessDictionaryBiz {
    int deleteByPrimaryKey(Integer id);

    int insert(TBusinessDictionary record);

    int insertSelective(TBusinessDictionary record);

    TBusinessDictionary selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TBusinessDictionary record);

    int updateByPrimaryKey(TBusinessDictionary record);
    
    public List<Map<String, Object>> getBusinessCode(Map<String, Object> reqMap);
    
    public TBusinessDictionary selectOne(TBusinessDictionary record);
    
    public String getApprInfoExtMsg(String apprInfoExt);
    
    public List<Map<String, Object>> selectRefuseReaonMap(Map<String, Object> reqMap);
}