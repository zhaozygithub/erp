package com.dlcat.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.FormBuilder;
import com.dlcat.common.entity.Search;
import com.dlcat.common.entity.TableHeader;
import com.dlcat.common.utils.PageUtil;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.ToCodeLibrary;
import com.dlcat.service.flow.FlowService;
import com.dlcat.service.flow.impl.FlowServiceImpl;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class TestController extends BaseController{
	public void index(){
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[]{"id","name","business_id","repay_type","remark","status"});
		tableHeader.setNames(new String []{"id","名称","业务类型编号","还款方式","备注","是否开启"});
		
		Search search = new Search();
		search.setNames(new String[]{"name","status"});
		search.setFiledNames(new String[]{"名称","是否有效"});
		search.setTypes(new String[]{"text","select"});
		Map<String,List<ToCodeLibrary>> clListMap = new HashMap<String, List<ToCodeLibrary>>();
		clListMap.put("status", ToCodeLibrary.getCodeLibrariesBySQL("YesNo",true,null));
		search.setOptionListMap(clListMap);
		DyResponse response = null;
		
		try {
			//String sql = "select * from loan_product_category";
			response = PageUtil.createTablePageStructure("/test/data", "测试列表数据", tableHeader, search,super.getLastPara().toString(),(Map<Integer, SysMenu>)this.getSessionAttr("menus"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setAttr("response", response);
		//
		//this.renderJson();
		/*this.renderJson(object);
		this.renderJson(jsonText);
		this.renderJson(attrs);
		this.renderJson(key, value);
		this.render*/
		//
		this.render("test_table.html");
	}
   public void data(){
	   String a = this.getPara("tableHeader");
	   System.out.println("aaaaaaaaa");
	   System.out.println("aaaaaaaaa");
	   System.out.println("aaaaaaaaa");
	   
	   List<Record> res = Db.find("select * from loan_product_category");
		
	   /*  DyResponse dr = new DyResponse();
	   dr.setData(res);
	   dr.setStatus(200);
	   dr.setDescription("OK");
	   this.setAttr("dataResponse", dr);*/
	   this.setAttr("status", 200);
	   this.setAttr("name", "masai");
	   this.setAttr("dataList", res);
	   renderJson();
   }
   public void getFild(){
	   String a = super.getLastPara().toString();
	   System.out.println(a);
   }
   public void formTest(){
	   DyResponse response = PageUtil.createFormPageStructure("表单测试", FormBuilder.formBuilderTest(), 2, "aaa");
       this.setAttr("response", response);
       this.render("test_form.html");
   }
   public void mm(){
	   FlowService fs = new FlowServiceImpl();
	   System.out.println("11111");
	   //fs.flowInit("aa");
	   System.out.println("22222");
   }
}
