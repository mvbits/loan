package com.hrbb.loan.pos.dao;

import java.util.List;
import java.util.Map;

import com.hrbb.loan.pos.dao.entity.TAICEntinvInfo;

public interface TAICEntinvInfoDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TAICEntinvInfo record);

    int insertSelective(TAICEntinvInfo record);

    TAICEntinvInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TAICEntinvInfo record);

    int updateByPrimaryKey(TAICEntinvInfo record);
    
    public int insertSelectiveMap(Map<String, Object> reqMap);
    
    /**
     * 条件查询
     * 
     * @param reqMap
     * @return
     */
    List<Map<String,Object>> selectBySelectiveMap(Map<String,Object> reqMap);
}