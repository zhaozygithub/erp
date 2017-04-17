package com.dlcat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;

import com.dlcat.core.utils.PageUtil;
import com.dlcat.entity.DyResponse;
import com.dlcat.entity.Search;
import com.dlcat.entity.TableHeader;
import com.dlcat.model.SysMenu;
import com.dlcat.model.ToCodeLibrary;

public class TestController extends BaseController{
	public void index(){
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFildNames(new String[]{"id","name","business_id","repay_type","remark","status"});
		tableHeader.setNames(new String []{"id","名称","业务类型编号","还款方式","备注","是否开启"});
		
		Search search = new Search();
		search.setNames(new String[]{"name","status"});
		search.setFiledNames(new String[]{"名称","是否有效"});
		search.setTypes(new String[]{"text","select"});
		Map<String,List<ToCodeLibrary>> clListMap = new HashMap<String, List<ToCodeLibrary>>();
		clListMap.put("status", ToCodeLibrary.getCodeLibrariesByCodeNo("YesNo"));
		search.setOptionListMap(clListMap);
		DyResponse response = null;
		
		try {
			String sql = "select * from loan_product_category";
			response = PageUtil.createTablePageStructure(sql, tableHeader, search,super.getLastPara().toString(),(SysMenu)this.getSessionAttr("menuTree"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setAttr("response", response);
		this.render("test_table.html");
	}
   public void getFild(){
	   String a = super.getLastPara().toString();
	   System.out.println(a);
   }
   
   
}
