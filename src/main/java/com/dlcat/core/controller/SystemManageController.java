package com.dlcat.core.controller;

import com.dlcat.common.BaseController;
import com.jfinal.core.Controller;
//系统管理菜单下的功能
public class SystemManageController extends BaseController {
	public void menuManage(){
		render("menuManage.html");
	}
	public void orgManage(){
		render("orgManage.html");
	}
}
