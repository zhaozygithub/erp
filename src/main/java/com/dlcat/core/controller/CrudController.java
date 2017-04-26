package com.dlcat.core.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.dlcat.core.model.Form;
import com.dlcat.core.model.SysUser;
import com.dlcat.core.model.Widget;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinal.plugin.activerecord.generator.MappingKitGenerator;
import com.jfinal.upload.UploadFile;

public class CrudController extends Controller {
	
	public void index() {
	//   /query-tableName-id
		
		String type=getPara(0);
		String tableName=getPara(1);
		
		 List<Widget> list=new ArrayList<Widget>();
		if (type.equals("add")) {
			Table table=null;
			if (tableName.toLowerCase().equals("sys_user")) {
				table=TableMapping.me().getTable(SysUser.class);
			}
			
			Set<String> keys=table.getColumnTypeMap().keySet();
			for (String key : keys) {
				 list.add(new Widget(key, key, "text"));
			}
			
		}else if (type.equals("update")) {
			String id=getPara(2);
		}
		
		
		Form form=new Form(type, tableName, list);
		setAttr("form", form);
		
		/* List<String> seList=new ArrayList<String>();
		 seList.add("java");
		 seList.add("js");
		 seList.add("c#");
		 List<String> seList2=new ArrayList<String>();
		 seList2.add("js");
		 seList2.add("c#");
		
		
		 List<Widget> list=new ArrayList<Widget>();
		   list.add(new Widget("用户ID", "id", "text"));
		   list.add(new Widget("用户名", "name", "text"));
		   list.add(new Widget("密码", "password", "password"));
		   list.add(new Widget("科目", "sub", "select",seList));
		   
		   Form form=new Form("query", "sys_user", list);
		   
		   //根据给的id去数据库里面查数据一般是一个对象
		   SysUser sysUser=SysUser.dao.findById(1);
		   Table table=TableMapping.me().getTable(SysUser.class);
		   Set<String> keys=table.getColumnTypeMap().keySet();
		   
		   
		   
		 List<String> model=new ArrayList<String>();
		 model.add(sysUser.get("id").toString());
		 model.add(sysUser.get("name").toString());
		 model.add(sysUser.get("password").toString());
		 model.add("js");

		   
		   setAttr("form", form);
		   setAttr("model", model);
		   setAttr("list", seList);
		   setAttr("list2", seList2);*/
		  render("userAdd.html");
	}

	
	/**
	* @author:zhaozhongyuan 
	* @Description: 提交添加form表单 
	* @param 
	* @return void
	* @date 2017年4月19日 上午11:58:01  
	*/
	public void add() {
		
	}
	public void upload() {
		UploadFile uploadFile=getFile();
		String path=uploadFile.getUploadPath();
		String pString=uploadFile.getFileName();
		/*String pString2=uploadFile.getOriginalFileName();
		String pString3=uploadFile.getParameterName();*/
		
		String imgpath=PropKit.get("img_base")+"/"+pString;
		
		renderHtml("<img src='"+imgpath+"'>");
	}

	/**
	* @author:zhaozhongyuan 
	* @Description: 执行删除
	* @param 
	* @return void
	* @date 2017年4月19日 上午11:56:25  
	*/
	public void del() {

	}

	
	/**
	* @author:zhaozhongyuan 
	* @Description: 对form表单执行真正的更新操作
	* @param 
	* @return void
	* @date 2017年4月19日 上午11:55:12  
	*/
	public void update() {
	
	
	renderText("usa");

	}

	public void query() {
		
		
		render("userAdd.html");

	}
}
