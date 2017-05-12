package com.dlcat.core.controller;

import java.util.Arrays;

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
		setAttr("itemcount",c);
		render("userManage.html");
	}
	public void adduser(){
		SysUser sysUser=getBean(SysUser.class, "");
		try {
			sysUser.save();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			renderText(Arrays.toString(e.getStackTrace()));
			return;
		}
		renderText("seccess");
	}
	public void updateuser() {
		SysUser sysUser=getBean(SysUser.class, "");
		try {
			//SysUser.dao.findFirst("").
			//sysUser.update();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
