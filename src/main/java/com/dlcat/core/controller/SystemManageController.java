package com.dlcat.core.controller;

import java.util.*;

import com.dlcat.common.BaseController;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.FormBuilder;
import com.dlcat.common.entity.FormField;
import com.dlcat.common.utils.PageUtil;
import com.dlcat.core.model.SysMenu;
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
	public void addsubmenu() {
		SysMenu sysMenu=getModel(SysMenu.class ,"");//getBean(SysMenu.class ,"");
		renderText(  sysMenu.save()?"成功":"失败");
	}
	public void delmenu() {
		SysMenu sysMenu=getBean(SysMenu.class ,"");
		renderText(  SysMenu.dao.deleteById(sysMenu.get("id")) ?"成功":"失败");
	}
	/*public void addsubmenuform(){
		List<FormField> formFieldGroup=new ArrayList<FormField>();
		formFieldGroup.add(new FormField("菜单名字","name","text",""));
	}*/
}
