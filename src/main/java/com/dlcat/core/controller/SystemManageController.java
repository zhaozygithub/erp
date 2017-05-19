package com.dlcat.core.controller;

import java.util.*;

import com.dlcat.common.BaseController;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.FormBuilder;
import com.dlcat.common.entity.FormField;
import com.dlcat.common.utils.OptionUtil;
import com.dlcat.common.utils.PageUtil;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysUser;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
//系统管理菜单下的功能
public class SystemManageController extends BaseController {
	public void menuManage(){
		render("systemManage/menuManage.html");
	}
	public void orgManage(){
		render("systemManage/orgManage.html");
	}
	public void userManage(){
		Long c=SysUser.dao.findFirst("select count(id) as c from sys_user ;").getLong("c");
		setAttr("total", c/20+1);
		setAttr("itemcount",c);
		render("systemManage/userManage.html");
	}
	public void addsubmenu() {
		SysMenu sysMenu=getModel(SysMenu.class ,"");//getBean(SysMenu.class ,"");
		renderText(  sysMenu.save()?"成功":"失败");
	}
	public void delsubmenu() {
		//传来一些ID,将level最末的删掉.
		String ids=getPara("id");
		int level=SysMenu.dao.findFirst("SELECT  max(`level`) as max FROM  sys_menu WHERE id in (?)  ",ids).getInt("max");
		String sqlString="delete from sys_menu where `level`='?' and `id` in (?)  ";
		boolean del=Db.queryBoolean(sqlString, level,ids);
		SysMenu sysMenu=getModel(SysMenu.class ,"");
		// SysMenu.dao.deleteById(sysMenu.get("id"))
		renderText(  del?"成功":"失败");
	}
	public void submenuform(){
		//String pid=getPara("pid");
		String type=getPara(0);//getPara("type");
		if("".equals(type)||type==null)
			type="add";
		List<FormField> formFieldGroup=new ArrayList<FormField>();
		formFieldGroup.add(new FormField("name","菜单名字","text","",true));
		FormField formField;
		formField=new FormField("pid","父菜单ID","hidden","",true);
		formField.setDisable(true);
		formFieldGroup.add(formField);
		formField=new FormField("level","等级","text","",true);
		//formField.setDisable(true);
		formFieldGroup.add(formField);
		formFieldGroup.add(new FormField("url","菜单访问地址","text","",true));
		List<Map> opts=OptionUtil.getOptionListByOther("item_no", "item_name", "to_code_library", "code_no='BtnType' ");
		formFieldGroup.add(new FormField("use_type","类型","radio","0",opts));
		formFieldGroup.add(new FormField("sort_no","排序","text","30"));
		formFieldGroup.add(new FormField("status","状态","text","1"));
		formFieldGroup.add(new FormField("remark","备注","text"));
		Map<String , String> map=new HashMap<String, String>();
		map.put("add", "添加");
		map.put("edit", "编辑");
		map.put("detail", "查看");
		String cndesc=map.get(type);
		DyResponse response = PageUtil.createFormPageStructure(cndesc+"菜单", formFieldGroup,  "/systemManage/"+type+"submenu");
	    this.setAttr("response", response);
	    this.render("common/form_editarea.html");
	}
}
