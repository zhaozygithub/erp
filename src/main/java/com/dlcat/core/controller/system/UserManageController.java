package com.dlcat.core.controller.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.FormField;
import com.dlcat.common.entity.QueryItem;
import com.dlcat.common.entity.QueryWhere;
import com.dlcat.common.entity.Search;
import com.dlcat.common.entity.TableHeader;
import com.dlcat.common.utils.PageUtil;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysUser;

public class UserManageController extends BaseController {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public void index(){
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[]{"id","name","phone"});
		tableHeader.setCNNames (new String[]{"id","名字","手机号"});
		Search search = new Search();
		search.setFieldNames(new String[]{"name","phone","role_id","update_time"});
		search.setCNNames(new String[]{"名称","手机号","角色","时间"});
		search.setTypes(new String[]{"text","text","select","date"});
		DyResponse response = null;
		try {
			//4.构造页面结构并返回，注意：第一个参数标示请求列表数据时候的url，必须是存在的
			//第二个参数是这个列表数据的名称，如果页面中存在这个导出功能，这个名称就是导出的
			//excel文件的文件名称
			response = PageUtil.createTablePageStructure("/userManage/data", "用户数据", tableHeader, 
					search,"18",(Map<Integer, SysMenu>)this.getSessionAttr("menus"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//5.返回数据到页面 response不可改变,页面名称也不可改变
		this.setAttr("response", response);
		this.render("common/table.html");
	}
	public void data() {
		   String conditation = this.getPara("condition");
		   String page = this.getPara("page");
		   System.out.println("查询条件："+conditation);
		   //2.列表数据sql	特别注意：如果需要如果需要转化成为字典值调用getCodeItemName()数据库函数，
		   //字典值别名一律为	cn_原名称
		  /*StringBuffer sql = new StringBuffer("select *, getCodeItemName('YesNo',status) "
		   		+ "as cn_status from loan_product_category where 1=1 ");*/
		   //3.获取参数Map，处理字符串拼接
		   Map<String, String> whereMap = super.getWhereMap(conditation);
		   //定义查询对象
		   QueryItem item = new QueryItem();
		   //设置表名（多表用逗号间隔）
		   item.setTableNames("sys_user");
		   //设置查询字段（可以使用函数包括聚合函数）
		   item.setFields("*, getCodeItemName('YesNo',status) as cn_status");
		   //设置分页步长  如果不设置 或者 值<=0，则会默认歩长为20
		   item.setLimit(10);
		   //注意：此处whereMap在页面初始化的时候为null，此处有必要判空
		   List<QueryWhere> whereList = new ArrayList<QueryWhere>();
		   //检索条件
		   if(whereMap!=null)
		   for(Map.Entry<String, String> mEntry:whereMap.entrySet()  ){
			   whereList.add(new QueryWhere(mEntry.getKey(), mEntry.getValue()));
		   }
		   item.setWhereList(whereList);
		   //4.获取数据  格式为List<Record>
		   DyResponse dyResponse = super.getTableData(item , page);
		   //5.返回数据到页面	response 固定值 不可改变
		   this.setAttr("response", dyResponse);
		   
		   renderJson();
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
	public void adduserform(){
		List<FormField> formFieldGroup=new ArrayList<FormField>();
		formFieldGroup.add(new FormField("名字","name","text",""));
		DyResponse response = PageUtil.createFormPageStructure("添加用户", formFieldGroup,  "adduser");
	    this.setAttr("response", response);
	    this.render("common/form_editarea.html");
	}
	public void edituser() {
		
	}

}
