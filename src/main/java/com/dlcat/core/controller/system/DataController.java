package com.dlcat.core.controller.system;

import java.util.List;

import com.dlcat.common.BaseController;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysOrg;
import com.dlcat.core.model.SysUser;

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
	public void getUser(){
		int p=1;
		try{
			p=getParaToInt("p");}
		catch(Exception e){}
		String condition=getPara("condition");
		condition=(condition==null||"".equals(condition))?"1=1":condition.replaceAll("(^\\s*?,|,\\s*?$)", "")
				.replaceAll(",", " and ").replaceAll(":", " = ");
		System.out.println(condition);
		List<SysUser> users=SysUser.dao.find("select id,img_url,name,real_name,role_id,belong_org_id,belong_org_name,phone,status,input_user_id,input_user_name,input_time,update_time,update_user_id,last_login_time,last_login_ip,login_count from sys_user where "+condition+" limit ?,20;",(p-1)*20);
		renderJson(users);
	}
}
