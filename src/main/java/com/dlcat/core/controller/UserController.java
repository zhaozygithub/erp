package com.dlcat.core.controller;

import com.dlcat.common.BaseController;
import com.dlcat.core.model.SysUser;

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
		SysUser user=getModel(SysUser.class,"user");
		user.update();
		redirect("/");
	}
}
