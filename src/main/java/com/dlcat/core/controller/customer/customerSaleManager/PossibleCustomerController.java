package com.dlcat.core.controller.customer.customerSaleManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dlcat.common.BaseController;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.QueryItem;
import com.dlcat.common.entity.QueryWhere;
import com.dlcat.common.entity.Search;
import com.dlcat.common.entity.TableHeader;
import com.dlcat.common.utils.PageUtil;
import com.dlcat.core.model.CuPossibleCustomer;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysUser;
import com.dlcat.core.model.ToCodeLibrary;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;

public class PossibleCustomerController extends BaseController {
	public void index(){
		//首先注意：此处的列表页面应该是在Tab下面的，所以加载Tab中的url，
		//Tab的url中需要将Tab的id拼接到url最后
		//1.定义列表的表头和字段  注意：字段名称必须是数据库中的实际存在
		//的字段名称或者虚拟字段名称，如cn_status
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[]{"id","name","phone","cn_type","remark"});
		tableHeader.setCNNames(new String []{"ID","名称","电话","客户类型","备注"});
		//2.定义检索区域检索框 注意：字段名称必须是实际存在的字段名称.
		//CNName中文标示这个检索字段的含义，type标示检索框的类型
		Search search = new Search();
		search.setFieldNames(new String[]{"id","name"});
		search.setCNNames(new String[]{"用户ID","用户名"});
		search.setTypes(new String[]{"text","text"});
		//3.定义下拉数据源 如果检索区域中存在select，必须定义下拉数据源
		String[] aStrings={"",""};
		List<Object> list=new ArrayList<Object>();
		list.add(aStrings); 
		
		
		//注意：这里的资源必须和表头字段中的一致，可以定义多个
//		Map<String,List<ToCodeLibrary>> clListMap = new HashMap<String, List<ToCodeLibrary>>();
//		clListMap.put("status", ToCodeLibrary.getCodeLibrariesBySQL("YesNo",true,null));
//		search.setOptionListMap(clListMap);
		DyResponse response = null;
		
		try {
			//4.构造页面结构并返回，注意：第一个参数标示请求列表数据时候的url，必须是存在的
			//第二个参数是这个列表数据的名称，如果页面中存在这个导出功能，这个名称就是导出的
			//excel文件的文件名称
			response = PageUtil.createTablePageStructure("/possibleCustomer/data", "测试列表数据", tableHeader, 
					search,super.getLastPara().toString(),(Map<Integer, SysMenu>)this.getSessionAttr("menus"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//5.返回数据到页面 response不可改变,页面名称也不可改变
		this.setAttr("response", response);
		this.render("common/table.html");
	}
   public void data(){
	   //1.从页面获取参数 condition参数名称固定 key=condition 格式为：name:ms,age:24
	   //获取分页参数	固定格式 key=page
	   String conditation = this.getPara("condition");
	   String page = this.getPara("page");
	   System.out.println("查询条件："+conditation);
	   //2.列表数据sql	特别注意：如果需要如果需要转化成为字典值调用getCodeItemName()数据库函数，
	   //别名一律为	cn_原名称
	   
	   Map<?, ?> whereMap = super.getWhereMap(conditation);
	   
	   //定义查询对象
	   QueryItem item = new QueryItem();
	   item.setTableNames("cu_possible_customer");
	   item.setFields("*, getCodeItemName('CustomerType',type) as cn_type");
	   List<QueryWhere> whereList = new ArrayList<QueryWhere>();
	   whereList.add(new QueryWhere("type", IN, "(01,02)"));
	   
	   //注意：此处whereMap在页面初始化的时候为null，此处有必要判空
	   if(whereMap != null){
		   whereList.add(new QueryWhere("id", whereMap.get("id")));
		   whereList.add(new QueryWhere("name", whereMap.get("name")));
	   }
	   item.setWhereList(whereList);
	   //4.获取数据  格式为List<Record>
	   DyResponse dyResponse = super.getTableData(item,page);
	   //5.返回数据到页面	response 固定值 不可改变
	   this.setAttr("response", dyResponse);
	   renderJson();
   }
	
	
	
}
