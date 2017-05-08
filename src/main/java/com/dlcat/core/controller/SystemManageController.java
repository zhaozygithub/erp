package com.dlcat.core.controller;

import com.dlcat.common.BaseController;
import com.dlcat.core.model.SysUser;
import com.jfinal.core.Controller;
//系统管理菜单下的功能
public class SystemManageController extends BaseController {
	public void menuManage(){
		render("menuManage.html");
	}
	public void orgManage(){
		render("orgManage.html");
	}
	public void userManage(){
		Long c=SysUser.dao.findFirst("select count(id) as c from sys_user ;").getLong("c");
		setAttr("total", c/20+1);
		render("userManage.html");
	}
}
