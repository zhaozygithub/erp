package com.dlcat.core.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.common.entity.QueryItem;
import com.dlcat.common.utils.DateUtil;
import com.dlcat.common.utils.ExportExcelUtil;
import com.dlcat.common.utils.JsonUtils;
import com.dlcat.common.utils.StringUtils;
import com.dlcat.core.model.SysUser;
import com.google.gson.Gson;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
/**
 * 通用Controller类
 * @author masai
 * @time 2017年5月11日 下午1:33:42
 */
public class CommonController extends BaseController {
	/**
	 * 导出Excel表格，通用路由 
	 * 适用于：列表模板导出excele表格
	 * @author masai
	 * @time 2017年5月11日 下午1:50:58
	 */
	public void exportExcel() throws Exception{
		try {
			//（1）.数据准备
			String jsonstr=getPara("requestPara");
			jsonstr= java.net.URLDecoder.decode(jsonstr);
			//xd.getAsString("")
			Gson gson=new Gson();
			HashMap requestParaMap=gson.fromJson(jsonstr, HashMap.class);
			//获取导出文件名称
			String fileName = requestParaMap.get("tableName").toString();
			//获取表头字符串（逗号间隔）
			String tableHeader = requestParaMap.get("tableHeaders").toString();
			if(tableHeader.indexOf("[")>=0 && tableHeader.lastIndexOf("]") >= 0){
				tableHeader = tableHeader.substring(tableHeader.indexOf("[")+1, tableHeader.lastIndexOf("]"));
			}
			//获取表头对应字段
			String tableFields = requestParaMap.get("tableFields").toString();
			if(tableFields.indexOf("[")>=0 && tableFields.lastIndexOf("]") >= 0){
				tableFields = tableFields.substring(tableFields.indexOf("[")+1, tableFields.lastIndexOf("]"));
			}
			//获取json格式的QueryItem字符串
			String queryItem = requestParaMap.get("queryItem").toString();
			//获取当前分页数 为空则为默认值1
			int currentPage = requestParaMap.get("currentPage") == null ?  1 :
								Integer.parseInt(requestParaMap.get("page").toString());
			if(StringUtils.isBlank(queryItem)){
				throw new Exception("导出对象异常");
			}
			//获取导出对象类型，url拼接的第一个参数(导出类型)，全部导出(all) or 导出当前页(current) ？
			/*
			 * 注：全部导出是指的是在当前查询条件下全部导出，而不是指的是无查询条件，与导出当前
			 * 的区别是导出全部无分页限制，导出当前有分页限制。
			 */
			String exprotType = this.getPara(0, "all");
			//（2）.将json格式的QueryItem字符串转化为QueryItem对象
			QueryItem item = JsonUtils.fromJson(queryItem, QueryItem.class);
			//（3）.获取导出数据sql
			//如果参数为空默认为导出类型为：全部导出（all）
			String querySql = QueryItem.getQuerySql(item);
			Object[] pageCount = null;
			if(exprotType.equals("all")){//全部导出
				querySql = querySql.split("limit")[0];
			}else{
				pageCount = new Object[]{(currentPage-1)*item.getLimit()};
			}
			//（4）.查询数据
			List<Map> resMapList = baseModel.baseFind(Map.class, querySql, pageCount);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ExportExcelUtil exportExcel = new ExportExcelUtil();
			//调用导出工具类
			exportExcel.exportExcel(fileName, tableHeader.split(","), resMapList, tableFields.split(","), out);

			getResponse().setHeader("Content-Type", "application/-excel");
	    	getResponse().setContentType("application/vnd.ms-excel;charset=utf-8");
	    	getResponse().setCharacterEncoding("utf-8");
	    	getResponse().setHeader("Content-Disposition", "attachment;filename="+ new String((fileName + DateUtil.getCurrentDateStr().replaceAll("(:|\\s)", "_")+ ".xls").getBytes(), "iso-8859-1"));
	    	
	    	InputStream excelStream = new ByteArrayInputStream(out.toByteArray());
	    	//向浏览器输出数据流
	    	byte[] buf = new byte[1024];
			int len=0;
			while ((len = excelStream.read(buf)) > 0) {
				getResponse().getOutputStream().write(buf, 0, len);
			}
			excelStream.close();
			out.close();
	    	
	    	renderNull();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		String a = "[*[*]*]";
		System.out.println(a.indexOf("["));
		System.out.println(a.lastIndexOf("]"));
		
		System.out.println(a.substring(1,0));
	}
}
