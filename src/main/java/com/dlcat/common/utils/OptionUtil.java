package com.dlcat.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseModel;
import com.dlcat.core.model.ToCodeLibrary;
import com.jfinal.plugin.activerecord.Db;

/**
 * Option字典生成工具类
 * 注意：此类生成option字典有三种方式：字典表、其他表、手动生成
 * @author masai
 * @time 2017年5月13日 下午4:53:03
 */
public class OptionUtil {
	//创建数据库操作引擎
	public static BaseModel baseModel = new BaseModel();
	/**
	 * 从字典表中获取option列表（此方法禁止修改）
	 * @param codeNo		 字典表码值
	 * @param isEffectStatus状态是否有效 true表示有效则限制了status=1，false或者null则不限制status
	 * @param whereList		 where条件，多个条件使用and间隔，第一个条件前面，最后一个条件后面无and，会自动去除
	 * @return
	 * @author masai
	 * @time 2017年5月13日 下午5:09:45
	 */
	public static List<Map> getOptionListByCodeLibrary(String codeNo , Boolean isEffectStatus , String whereList){
		List<Map> codeLibraries = null;
		if(StringUtils.isNotBlank(codeNo)){
			codeNo = "'"+codeNo+"'";
			String statusWhere = "";
			if(isEffectStatus != null && isEffectStatus){
				statusWhere = " status=1 and ";
			}
			String sql = "select * from to_code_library where  "+statusWhere+" code_no="+codeNo+" and ";
			if(StringUtils.isNotBlank(whereList)){
				whereList = whereList.trim();
				if(whereList.endsWith("and")){
					whereList = whereList.substring(0, whereList.length()-3);
				}
				if(whereList.startsWith("and")){
					whereList = whereList.substring(3, whereList.length());
				}
			}else{
				whereList = "";
				sql = sql.trim();
				sql = sql.substring(0, sql.length()-3);
			}
			sql +=whereList;
			try {
				codeLibraries = baseModel.baseFind(Map.class, sql , null);;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return codeLibraries;
	}
	
	/**
	 * 从其他表中获取option列表（此方法禁止修改）
	 * @param key		字典的Key	
	 * @param value		字典的Value
	 * @param table		要查询的表名
	 * @param whereList 条件
	 * @return
	 * @author masai
	 * @throws Exception 
	 * @time 2017年5月13日 下午5:34:29
	 */
	public static List<Map> getOptionListByOther(String key , String value , String table , String whereList){
		//（1）.参数校验
		if(StringUtils.isBlank(key) || StringUtils.isBlank(value) || StringUtils.isBlank(table)){
			try {
				throw new Exception("获取option，参数有误");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//（2）.拼接查询语句
		StringBuffer strBuffer = new StringBuffer("select ");
		strBuffer.append(key + " as item_no ");
		strBuffer.append(" , ");
		strBuffer.append(value + " as item_name");
		strBuffer.append(" from "); 
		strBuffer.append(table);
		if(StringUtils.isNotBlank(whereList)){
			strBuffer.append(" where ");
			strBuffer.append(whereList);
		}
		//（3）.获取查询结果
		List<Map> resList = new ArrayList<Map>();
		try {
			resList = baseModel.baseFind(Map.class, strBuffer.toString(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resList;
	}
	/**
	 * 手动生成option（此方法禁止修改）
	 * 注意：key和value是从前到后一一对应的，请注意key和value的顺序
	 * @param keys		key  数组
	 * @param values	value数组
	 * @return
	 * @author masai
	 * @time 2017年5月15日 上午10:15:07
	 */
	public static List<Map> getOptionListByManual(String[] keys , String[] values){
		//参数校验
		//keys和values必须同时是非空的，keys的长度必须小于等于values的长度
		if(keys == null || keys.length == 0 || 
		   values == null || values.length == 0 || 
		   keys.length > values.length){
			try {
				throw new Exception("生成option字典失败");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//生成
		List<Map> resList = new ArrayList<Map>();
		Map map = null;
		for(int i = 0 ; i < keys.length ; i++){
			map = new HashMap<String, Object>();
			map.put(keys[i], values[i]);
			resList.add(map);
		}
		return resList;
	} 
}
