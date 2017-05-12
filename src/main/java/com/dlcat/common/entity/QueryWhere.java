package com.dlcat.common.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dlcat.common.utils.StringUtils;

/**
 * 查询条件构造
 * @author masai
 * @time 2017年5月9日 下午9:04:00
 */
public class QueryWhere {
	public static final String EQ = "=";
	public static final String GT = ">";
	public static final String GE = ">=";
	public static final String LT = "<";
	public static final String LE = "<=";
	public static final String NEQ = "<>";
	public static final String IN = "in";
	public static final String NOT_IN = "not in";
	public static final String LIKE_LEFT = "like_left";
	public static final String LIKE_RIGHT = "like_right";
	public static final String LIKE_ALL = "like_all";
	public static final String NULL = "is null";
	public static final String NOT_NULL = "is not null";
	
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
	 * 获取查询条件 
	 * @param whereList
	 * @return
	 * @author masai
	 * @time 2017年5月9日 下午9:29:20
	 */
	public static String getWhereConditation(List<QueryWhere> whereList){
		StringBuffer whereCondiation = new StringBuffer(" ");
		for(QueryWhere where:whereList){
			if(where.getCondition() == null || (EQ.equals(where.getCondition()) && where.getValue() == null)){
				continue;
			}
			whereCondiation.append(" and ");
			if(NULL.equals (where.getCondition())||NOT_NULL .equals (where.getCondition())){
				whereCondiation.append(" " + where.getName() + " ")
							   .append(" " + where.getCondition() + " ");
			}else if(where.getCondition().startsWith("like") && where.getValue() != null){
				String tempValue = where.getValue().toString().substring(1, where.getValue().toString().length()-1);
				if(LIKE_LEFT.equals (where.getCondition())){
					whereCondiation.append(" " + where.getName() + " ")
								   .append(" like ")
								   .append(" '%" + tempValue + "' ");
				}else if(LIKE_RIGHT.equals (where.getCondition())){
					whereCondiation.append(" " + where.getName() + " ")
								   .append(" like ")
							       .append(" '" + tempValue + "%' ");
				}else if(LIKE_ALL.equals (where.getCondition())){
					whereCondiation.append(" " + where.getName() + " ")
								   .append(" like ")
								   .append(" '%" + tempValue + "%' ");
				}
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
		return new QueryWhere(name, "not in",value);
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
		return new QueryWhere(name , "is null" , null);
	}

	public static QueryWhere isNotNull(String name) {
		return new QueryWhere(name , "is not null" , null);
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

}
