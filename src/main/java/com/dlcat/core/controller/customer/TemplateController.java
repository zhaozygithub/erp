package com.dlcat.core.controller.customer;

import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.core.model.CuPossibleCustomer;
import com.dlcat.core.model.SysUser;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

public class TemplateController extends BaseController {

	public void index() {
		Page<SysUser> page=SysUser.dao.paginate(1, 5, false, "select *", "from sys_user");
		String[] heards={"name","id","password","role_id","belong_org_id"};
//		Page<CuPossibleCustomer> page=CuPossibleCustomer.dao.paginate(1, 5, false, "select *", "from cu_possible_customer");
//		String[] heards={"id","input_user_id","input_user_name","name","phone","remark","type"};
		setAttr("page",page);
		setAttr("heards",heards);
		render("possibleCustomer.html");
	}
	public void page() {
		int pageNumber=getParaToInt();
		if (pageNumber==0) {
			pageNumber=1;
		}
		//cu_possible_customer
		Page<SysUser> page=SysUser.dao.paginate(pageNumber, 5, false, "select *", "from sys_user");
		//String[] heards={"id","input_user_id","input_user_name","name","phone","remark","type"};
		String[] heards={"name","id","password","role_id","belong_org_id"};
		setAttr("page",page);
		setAttr("heards",heards);
		
		render("possibleCustomer.html");
	}
	
	public void serch() {
		//String field=getv
		Map<String, String[]> map=getParaMap();
		StringBuilder sb=new StringBuilder();
		String[] heards={"name","id","password","role_id","belong_org_id"};
		for (String field : heards) {
			
			if (!map.get(field)[0].equals("")) {
				sb.append(" and "+field+" = '"+map.get(field)[0]+"'");
			}
		}
		String where=" where 1=1"+sb.toString();
		
		Page<SysUser> page=SysUser.dao.paginate(1, 5, false, "select *", "from sys_user"+where);
		setAttr("page",page);
		setAttr("heards",heards);
		
		render("possibleCustomer.html");
	}
	public void ajax() {
		int pageNow=getParaToInt(0);
		String fields=getPara(1);
		List<Object> db=Db.query("select "+fields+" from sys_user limit "+(pageNow-1)*5+",5");
		renderJson(db);
	}
	public void submit() {
		String fields=getPara(0);
		String where=getPara(1);
		String[] wheres=where.split(",");
		String oString="";
		for (String string : wheres) {
			String[] values=string.split("=");
			if (values.length>1) {
				oString+=" and "+values[0]+"='"+values[1]+"'";
			}
		}
		//默认查第一页数据
		List<Object> data=Db.query("select "+fields+" from sys_user where 1=1 "+oString+" limit 0,5");
		Page<SysUser> page=SysUser.dao.paginate(1, 5, false, "select "+fields, " from sys_user where 1=1"+oString);
		//page.get
		renderJson(page);
	}
	
	
	
}
