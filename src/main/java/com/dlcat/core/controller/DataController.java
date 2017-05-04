package com.dlcat.core.controller;

import java.util.List;

import com.dlcat.common.BaseController;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysOrg;

public class DataController extends BaseController{
	public void getOrg() {
		List<SysOrg> orgs=SysOrg.dao.find("select * from sys_org;");
		//setAttr("orgs", orgs);
		//render("view/orgnization-tree.htm");
		renderJson(orgs);
		return;
	}
	public void getMenu() {
		List<SysMenu> menus=SysMenu.dao.find("select * from sys_menu;");
		renderJson(menus);
		return;
	}
}
