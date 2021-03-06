package com.dlcat.core.model;

import java.util.List;

import com.dlcat.common.BaseModel;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class SysRole extends BaseModel<SysRole> {
	public static final SysRole dao = new SysRole().dao();
	
	/**
	 * 获取指定角色下的所有有效用户
	 * @param roleId	角色编号
	 * @return
	 * @author masai
	 * @time 2017年4月21日 下午3:22:05
	 */
	public static List<SysUser> getUserBelongRole(int roleId){
		String sql  = "select * from sys_user where status = '1' role_id = ?";
		return SysUser.dao.find(sql, roleId);
	}
	
	/**
	 * @Title getRoleNameByUserId 
	 * @Description 根据用户id获取用户角色名称
	 * @param userId 用户id
	 * @return String 所属机构名称
	 * @author liuran 
	 * @time 2017年5月24日下午1:58:42
	 */
	public static String getRoleNameByUserId(Integer userId){
		String roleName = null;
		if (userId != null) {
			SysUser user = SysUser.dao.findById(userId);
			roleName = SysRole.dao.findById(user.getInt("role_id")).getStr("role_name");
			return roleName;
		}else {
			return roleName;
		}
		
	}
}
