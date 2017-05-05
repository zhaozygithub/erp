package com.dlcat.core.controller;

import com.dlcat.common.BaseController;
import com.dlcat.common.utils.CommonUtils;
import com.dlcat.core.model.SysUser;
import com.jfinal.kit.HashKit;
import com.jfinal.plugin.activerecord.Db;

public class UserController extends BaseController {
	
	/**
	 * 个人信息设置
	 * @author liuran
	 * @time 2017年5月3日 上午11:16:14 void
	 */
	public void index() {
		SysUser user = (SysUser) getSessionAttr("user");
		setAttr("user", user);
		render("userSet.html");
	}
	
	public void submit(){
		SysUser user = (SysUser) getSessionAttr("user");
		int userid = user.getInt("id");
		
		String name = getPara("userName");
		String oldpassword = getPara("oldPassword");
		String newpassword = getPara("newPassword");
		String newpassword2 = getPara("newPassword2");
		String phone = getPara("phone");
		
		
		
		if(SysUser.isRepeatName(userid,name)){
			setAttr("msguserName", "用户名已存在，请重新输入");
			index();
			return;
		}
		
		if(SysUser.isRepeatPhone(userid, phone)){
			setAttr("msgphone", "手机号码已存在，请重新输入");
			index();
			return;
		}
		
		if(!(CommonUtils.checkPhone(phone))){
			setAttr("msgphone", "手机号码格式不正确，请重新输入");
			index();
			return;
		}
		
		//旧密码为空
		if(oldpassword.length() == 0){
			//新密码为空
			if (newpassword.length() == 0) {
				Db.update("update sys_user set name = ? ,phone = ? where id=?",name,phone,userid);
				removeSessionAttr("user");
				SysUser user1 = SysUser.dao.findById(userid);
				setSessionAttr("user", user1);
				redirect("/");
			}else {
				setAttr("msgpassword2", "旧密码输入有误，请重新输入");
				index();
				return;
			}
		}else {
			//旧密码非空
			//新密码为空
			if(newpassword.length() == 0){
				setAttr("msgpassword2", "新密码不能为空，请重新输入");
				index();
				return;
			}else {
			//新密码非空	
					//旧密码正确
					if(SysUser.isRightPassword(userid, oldpassword)){
						Db.update("update sys_user set name = ? ,password = ?,phone = ?  where id=?",name,HashKit.sha1(newpassword),phone,userid);
						removeSessionAttr("user");
						redirect("/");
					}else {
						setAttr("msgpassword2", "旧密码输入有误，请重新输入");
						index();
						return;
					}
			}
		}
	}
}
