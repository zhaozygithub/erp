package com.dlcat.core.controller;

import java.io.File;
import java.util.List;

import com.dlcat.core.model.SysUser;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;



public class TestUserController extends Controller {
	public void index() {
		/*List<SysUser> users = SysUser.dao.find("select * from sys_user where status=1");
		setAttr("users", users);*/
		render("console/echarts.html");
	}
	

	public void add(){
		render("testUserForm.html");
	}

	public void del(){
		//不删除数据库数据   只改变状态 
		Db.update("update sys_user set status=0 where id=?",getParaToInt(0));
		List<SysUser> users = SysUser.dao.find("select * from sys_user where status=1");
		setAttr("users", users);	
		redirect("/user");
	}
	
	public void submit(){
		SysUser user = getModel(SysUser.class,"user");
		user.save();
		redirect("/user");
	}

}
