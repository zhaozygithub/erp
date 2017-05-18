package com.dlcat.common.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.common.utils.StringUtils;

/**
 * 查询条件构造
 * @author masai
 * @time 2017年5月9日 下午9:04:00
 */
public class QueryWhere {
	private static Map<String, String> conditationMap;
	static{
		conditationMap = new HashMap<String, String>();
		conditationMap.put(BaseController.EQ, "=");
		conditationMap.put(BaseController.GT, ">");
		conditationMap.put(BaseController.GE, ">=");
		conditationMap.put(BaseController.LT, "<");
		conditationMap.put(BaseController.LE, "<=");
		conditationMap.put(BaseController.NEQ, "<>");
		conditationMap.put(BaseController.IN, "in");
		conditationMap.put(BaseController.NOT_IN, "not_in");
		conditationMap.put(BaseController.OR, "or");
		conditationMap.put(BaseController.LIKE_LEFT, "like_left");
		conditationMap.put(BaseController.LIKE_RIGHT, "like_right");
		conditationMap.put(BaseController.LIKE_ALL, "like_all");
		conditationMap.put(BaseController.NULL, "is_null");
		conditationMap.put(BaseController.NOT_NULL, "is_not_null");
	}
	/**
	 * 字段名称
	 */
	private String name;
	/**
	 * 字段值
	 */
	private Object value;
	/**
	 * 条件
	 */
	private String condition;
	/**
	 * 校验条件是否合法	合法返回trus，反之返回false
	 * @param conditation
	 * @return
	 * @author masai
	 * @time 2017年5月12日 下午4:47:35
	 */
	public static Boolean checkOutWhereConditation(String conditation){
		return conditationMap.containsKey(conditation);
	}
	/**
	 * 获取查询条件 
	 * @param whereList
	 * @return
	 * @author masai
	 * @time 2017年5月9日 下午9:29:20
	 */
	public static String getWhereConditation(List<QueryWhere> whereList){
		StringBuffer whereCondiation = new StringBuffer(" ");
		for(QueryWhere where:whereList){
			if(where.getCondition() == null || (!checkOutWhereConditation(where.getCondition())) ||
							(BaseController.EQ.equals(where.getCondition()) && where.getValue() == null) ||
							(where.getCondition().startsWith("like") && where.getValue() == null)){
				continue;
			}
			whereCondiation.append(" and ");
			if(BaseController.NULL.equals (where.getCondition())||BaseController.NOT_NULL .equals (where.getCondition())){
				whereCondiation.append(" " + where.getName() + " ");
				if(BaseController.NULL.equals (where.getCondition())){
					whereCondiation.append(" is null ");
				}else{
					whereCondiation.append(" is not null ");
				}
			}else if(where.getCondition().startsWith("like") && where.getValue() != null){
				String tempValue = where.getValue().toString().trim();
				tempValue = tempValue.replaceAll("'", "");
				if(BaseController.LIKE_LEFT.equals (where.getCondition())){
					whereCondiation.append(" " + where.getName() + " ")
								   .append(" like ")
								   .append(" '%" + tempValue + "' ");
				}else if(BaseController.LIKE_RIGHT.equals (where.getCondition())){
					whereCondiation.append(" " + where.getName() + " ")
								   .append(" like ")
							       .append(" '" + tempValue + "%' ");
				}else if(BaseController.LIKE_ALL.equals (where.getCondition())){
					whereCondiation.append(" " + where.getName() + " ")
								   .append(" like ")
								   .append(" '%" + tempValue + "%' ");
				}
			}else if(BaseController.NOT_IN.equals(where.getCondition())){
				whereCondiation.append(" " + where.getName() + " ")
				   .append(" not in ")
				   .append(" " + where.getValue() + " ");
			}else{
				whereCondiation.append(" " + where.getName() + " ")
							   .append(" " + where.getCondition() + " ")
							   .append(" " + where.getValue() + " ");
			}
		}
		return whereCondiation.toString();
	}
	public QueryWhere(String name, Object value) {
		this.name = name;
		this.value = value;
		this.condition = "=";
	}

	public QueryWhere(String name, String condition, Object value) {
		this.name = name;
		this.value = value;
		this.condition = condition;
	}
	
	public static QueryWhere eq(String name, Object value) {
		return new QueryWhere(name, "=", value);
	}

	public static QueryWhere notEq(String name, Object value) {
		return new QueryWhere(name, "<>",value);
	}

	public static QueryWhere gt(String name, Object value) {
		return new QueryWhere(name,">",value);
	}

	public static QueryWhere ge(String name, Object value) {
		return new QueryWhere(name, ">=",value);
	}

	public static QueryWhere lt(String name, Object value) {
		return new QueryWhere(name,  "<",value);
	}

	public static QueryWhere le(String name, Object value) {
		return new QueryWhere(name, "<=",value);
	}

	public static QueryWhere in(String name, Object value) {
		return new QueryWhere(name, "in",value);
	}

	public static QueryWhere notIn(String name, Object value) {
		return new QueryWhere(name, "not_in",value);
	}
	
	public static QueryWhere like(String name, Object value) {
		//右侧模糊查询
		return new QueryWhere(name, "like",value);
	}

	public static QueryWhere likeAll(String name, Object value) {
		//全模糊查询
		return new QueryWhere(name, "like",value);
	}

	public static QueryWhere isNull(String name) {
		return new QueryWhere(name , "is_null" , null);
	}

	public static QueryWhere isNotNull(String name) {
		return new QueryWhere(name , "is_not_null" , null);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	@Override
	public String toString() {
		return "QueryWhere [name=" + name + ", value=" + value + ", condition=" + condition + "]";
	}
	

}
