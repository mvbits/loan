package com.hrbb.loan.web.security.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hrbb.loan.web.security.dao.UserDao;
import com.hrbb.loan.web.security.entity.User;
import com.hrbb.loan.web.security.entity.UserRole;
import com.hrbb.loan.web.security.utils.SqlId;

@Repository
public class UserDaoImpl extends BaseDaoImpl<User,Integer> implements UserDao{

    private static final String namespace= "com.hrbb.loan.web.security.entity.User.";
	@Override
	public User selectByName(String name) {
		// TODO Auto-generated method stub
		return getSqlSession().selectOne(namespace+SqlId.SQL_SELECT_BY_NAME,name);
	}

    /** 
     * @see com.hrbb.loan.web.security.dao.UserDao#lockById(java.lang.Integer)
     */
    @Override
    public int lockById(Integer id) {
        return getSqlSession().update(namespace+"lockById", id);
    }

    /** 
     * @see com.hrbb.loan.web.security.dao.UserDao#unlockById(java.lang.Integer)
     */
    @Override
    public int unlockById(Integer id) {
        return getSqlSession().update(namespace+"unlockById", id);
    }

    /** 
     * @see com.hrbb.loan.web.security.dao.UserDao#lockByIdInBatch(java.util.List)
     */
    @Override
    public int lockByIdInBatch(List<Integer> idList) {
        int count = 0;
        if (idList == null || idList.isEmpty())
            return count;
        for (Integer id : idList) {
            count += this.lockById(id);
        }
        return count;
    }

    /** 
     * @see com.hrbb.loan.web.security.dao.UserDao#unlockByIdInBatch(java.util.List)
     */
    @Override
    public int unlockByIdInBatch(List<Integer> idList) {
        int count = 0;
        if (idList == null || idList.isEmpty())
            return count;
        for (Integer id : idList) {
            count += this.unlockById(id);
        }
        return count;
    }

    /** 
     * @see com.hrbb.loan.web.security.dao.UserDao#deleteUserRole(java.lang.Integer, java.lang.Integer)
     */
    @Override
    public int deleteUserRole(Integer userId, Integer roleId) {
        UserRole ur = new UserRole();
        ur.setRoleId(roleId);
        ur.setUserId(userId);        
        return getSqlSession().delete(namespace+"deleteUserRole", ur);
    }

    /** 
     * @see com.hrbb.loan.web.security.dao.UserDao#insertUserRole(java.lang.Integer, java.lang.Integer)
     */
    @Override
    public int insertUserRole(Integer userId, Integer roleId) {
        UserRole ur = new UserRole();
        ur.setRoleId(roleId);
        ur.setUserId(userId);        
        return getSqlSession().insert(namespace+"insertUserRole", ur);
    }

    /** 
     * @see com.hrbb.loan.web.security.dao.UserDao#selectByCellphone(java.lang.String)
     */
    @Override
    public User selectByCellphone(String cellphone) {
        return getSqlSession().selectOne(namespace+"selectByCellphone",cellphone);
    }

    @Override
    public List<Map<String, Object>> selectUsersByPrivilegeName(String privilegeName) {
        return getSqlSession().selectList(namespace + "selectUsersByPrivilegeName", privilegeName);
    }

}
