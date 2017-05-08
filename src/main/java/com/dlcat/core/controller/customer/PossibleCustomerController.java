package com.dlcat.core.controller.customer;

import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.core.model.CuPossibleCustomer;
import com.dlcat.core.model.SysUser;
import com.jfinal.plugin.activerecord.Page;

public class PossibleCustomerController extends BaseController {

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
	
	
	
}
