package com.dlcat.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.model.ConvertAnchor;
import org.apache.shiro.codec.Base64;

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
			//（1）.基本数据准备
			String jsonstr=getPara("requestPara");
			//jsonstr= java.net.URLDecoder.decode(jsonstr);
			Gson gson=new Gson();
			//HashMap requestParaMap = gson.fromJson(jsonstr, HashMap.class);//此种方式转化造成数值型数据的改变，不采用
			HashMap requestParaMap = JsonUtils.fromJson2HashMapNoChange(jsonstr);
			//导出文件—名称
			String fileName = requestParaMap.get("tableName").toString();
			//导出文件—表头（逗号间隔）
			String tableHeader = requestParaMap.get("tableHeaders").toString();
			if(tableHeader.indexOf("[")>=0 && tableHeader.lastIndexOf("]") >= 0){
				tableHeader = tableHeader.substring(tableHeader.indexOf("[")+1, tableHeader.lastIndexOf("]"));
			}
			//导出文件—表头对应字段
			String tableFields = requestParaMap.get("tableFields").toString();
			if(tableFields.indexOf("[")>=0 && tableFields.lastIndexOf("]") >= 0){
				tableFields = tableFields.substring(tableFields.indexOf("[")+1, tableFields.lastIndexOf("]"));
			}
			//（2）.导出sql语句合成
			String querySql = "";
			Object[] pageCount = null;
			QueryItem item = null;
			//自动组装sql方式—sql重新组装
			if(requestParaMap.containsKey("queryItem") && StringUtils.isNotBlank(requestParaMap.get("queryItem").toString())){
				//获取json格式的QueryItem字符串
				String queryItem = Base64.decodeToString( requestParaMap.get("queryItem").toString());//Base64解密
				if(StringUtils.isBlank(queryItem)){
					throw new Exception("导出对象异常");
				}
				//将json格式的QueryItem字符串转化为QueryItem对象
				item = JsonUtils.fromJson(queryItem, QueryItem.class);
				querySql = QueryItem.getQuerySql(item).toLowerCase();
			}else{//表示手动组装sql
				querySql = Base64.decodeToString(requestParaMap.get("querySql").toString()).toLowerCase();
			}
			//（3）.处理分页相关
			//获取当前分页数 为空则为默认值1
			int currentPage = requestParaMap.get("page") == null ?  1 :
				Integer.parseInt(requestParaMap.get("page").toString());
			/*
			 * 获取导出对象类型，url拼接的第一个参数(导出类型)，全部导出(all) or 导出当前页(current) ？
			 * 注：全部导出是指的是在当前查询条件下全部导出，而不是指的是无查询条件，与导出当前
			 * 的区别是导出全部无分页限制，导出当前有分页限制。
			 */
			String exprotType = this.getPara(0, "all");//获取第一个参数，全部导出（all）
			if(exprotType.equals("all")){//全部导出，不需要分页
				querySql = querySql.split("limit")[0];
			}else{//导出当前，需要分页
				pageCount = new Object[]{(currentPage-1) * (item == null ? 20 : item.getLimit())};
			}
			//（4）.查询数据
			if(StringUtils.isBlank(querySql)){
				throw new Exception("导出数据异常");
			}
			List<Map> resMapList = baseModel.baseFind(Map.class, querySql, pageCount);
			//处理字段发射
			resMapList = super.handleFieldReflect(item.getFieldsReflect(), resMapList);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ExportExcelUtil exportExcel = new ExportExcelUtil();
			//（5）.调用导出工具类，输出文件流到页面
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
}
