package com.dlcat.core.controller.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.FormField;
import com.dlcat.common.utils.CommonUtils;
import com.dlcat.common.utils.DateUtil;
import com.dlcat.common.utils.OptionUtil;
import com.dlcat.common.utils.PageUtil;
import com.dlcat.common.utils.StringUtils;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysUser;
import com.jfinal.kit.HashKit;
import com.jfinal.plugin.activerecord.Db;

public class PersonalInfoSetController extends BaseController {
	
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
		
		if (!StringUtils.isNotBlank(name)) {
			setAttr("msguserName", "用户名不能为空，请重新输入！");
			renderJson();
			return;
		}
		
		if(SysUser.isRepeatName(userid,name)){
			setAttr("msguserName", "用户名已存在，请重新输入！");
			renderJson();
			return;
		}
		
		if (oldpassword.equals(newpassword)  && oldpassword.length() != 0) {
			setAttr("msgpassword1", "新密码不能和旧密码相同，请重新输入！");
			renderJson();
			return;
		}
		
		if (!newpassword.equals(newpassword2)) {
			setAttr("msgpassword2", "两次输入新密码不同，请重新输入！");
			renderJson();
			return;
		}
		
		if(SysUser.isRepeatPhone(userid, phone)){
			setAttr("msgphone", "手机号码已存在，请重新输入！");
			renderJson();
			return;
		}
		
		if(!(CommonUtils.checkPhone(phone))){
			setAttr("msgphone", "手机号码格式不正确，请重新输入！");
			renderJson();
			return;
		}
		
		//旧密码为空
		if(oldpassword.length() == 0){
			//新密码为空
			if (newpassword.length() == 0) {
				Db.update("update sys_user set name = ? ,phone = ? ,update_time = ? ,update_user_id = ? where id=?",name,phone,DateUtil.getCurrentTime(),userid,userid);
				removeSessionAttr("user");
				SysUser user1 = SysUser.dao.findById(userid);
				setSessionAttr("user", user1);
				setAttr("msg", "更新成功！");
				renderJson();
				return;
			}else {
				setAttr("msgpassword2", "旧密码输入有误，请重新输入!");
				renderJson();
				return;
			}
		}else {
			//旧密码非空
			//新密码为空
			if(newpassword.length() == 0){
				setAttr("msgpassword2", "新密码不能为空，请重新输入!");
				renderJson();
				return;
			}else {
			//新密码非空	
					//旧密码正确
					if(SysUser.isRightPassword(userid, oldpassword)){
						Db.update("update sys_user set name = ? ,password = ?,phone = ? ,update_time = ? ,update_user_id = ? where id=?",name,HashKit.sha1(newpassword),phone,DateUtil.getCurrentTime(),userid,userid);
						removeSessionAttr("user");
						setAttr("msg", "更新成功！");
						renderJson();
						return;
					}else {
						setAttr("msgpassword2", "旧密码输入有误，请重新输入!");
						renderJson();
						return;
					}
			}
		}
	}

	
}
