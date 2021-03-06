package com.dlcat.common.entity;

import java.util.List;

import com.dlcat.common.utils.StringUtils;

/**
 * 查询构造（适用于单数据库查询）
 * 查询sql写的顺序：select ... from... where.... group by... having... order by.. 
 * 查询sql执行顺序：from... where...group by... having.... select ... order by...
 * @author masai
 * @time 2017年5月9日 下午9:37:04
 */
public class QueryItem {
	/**
	 * 表名称(多个用逗号间隔)
	 */
	private String tableNames;
	/**
	 * 查询字段列表
	 */
	private String fields;
	/**
	 * 排序条件	注：order by 与 group by 结合使用group在前order在后
	 */
	private String order;
	/**
	 * 分组条件(一般为空，不常用)
	 */
	private String group;
	/**
	 * 分组having条件(一般为空，不常用)
	 */
	private String having;
	/**
	 * 分页步长
	 */
	private Integer limit;
	/**
	 * where条件集合
	 */
	private List<QueryWhere> whereList;
	/**
	 * 其他where条件字符串（处理无法自动生成的where条件） ps: or(name is not null and name<>'dlcat')
	 * otherWhere必须以 and 或者 or 开头，否则会顾虑该条件
	 */
	private String otherWhere;	
	/**
	 * 字段key反射
	 * 注：将表中字段的key值，反射为value（格式为：字段名：表名，字段名，映射字段名；字段名：表名，字段名，映射字段名...）
	 * 一条映射与另一条映射之间用分号间隔，表名，字段名，映射字段名
	 */
	private String fieldsReflect;
	
	//表名的过滤:防止出现/*和点(.),出现点会导致表名变成类似'/*表名*/mysql.user'这种
	final static String TABREG="^(\\w|\\d|_|\\s|,|\\(|\\)|\\*)+$";
	
	public QueryItem(String tableNames){
		//this.tableNames = tableNames;
		setTableNames(tableNames);
	}
	
	public QueryItem() {
	}

	/**
	 * 根据查询对象获取统计查询总数的sql
	 * @param item
	 * @return
	 * @throws Exception
	 * @author masai
	 * @time 2017年5月10日 下午2:17:13
	 */
	String getFilteredTableNames(String tableNames){
		if(!tableNames.matches(TABREG))
			tableNames = " ";
		return tableNames;
		
	}
	public static String getQueryTotalCountSql(QueryItem item)throws Exception{
		StringBuffer querySql = new StringBuffer();
		if(item != null && StringUtils.isNotBlank(item.getTableNames())){
			querySql.append("select count(*) as count from ");
			querySql.append(item.getTableNames() + " ");
			querySql.append("where 1=1 ");
			if(item.getWhereList() != null){
				querySql.append(QueryWhere.getWhereConditation(item.getWhereList()) + " ");
			}
			 String otherWhere = item.getOtherWhere();
			 if(StringUtils.isNotBlank(otherWhere) && (otherWhere.trim().startsWith("and") || otherWhere.trim().startsWith("or"))){
				 querySql.append(otherWhere);
			 }
		}else{
			throw new Exception("查询对象或者表名称为空");
		}
		return querySql.toString();
	}
	/**
	 * 根据查询对象获取查询的sql
	 * @param item
	 * @return
	 * @throws Exception
	 * @author masai
	 * @time 2017年5月10日 上午11:14:43
	 */
	public static String getQuerySql(QueryItem item) throws Exception{
		StringBuffer querySql = new StringBuffer();
		if(item != null && StringUtils.isNotBlank(item.getTableNames())){
			querySql.append("select ");
			querySql.append(StringUtils.isNotBlank(
					item.getFields()) ? item.getFields() + " " : "* ");
			querySql.append("from ");
			querySql.append(item.getTableNames() + " ");
			querySql.append("where 1=1 ");
			if(item.getWhereList() != null){
				querySql.append(QueryWhere.getWhereConditation(item.getWhereList()) + " ");
			}
			String otherWhere = item.getOtherWhere();
			if(StringUtils.isNotBlank(otherWhere) && (otherWhere.trim().startsWith("and") || otherWhere.trim().startsWith("or"))){
				querySql.append(otherWhere);
			}
			if(StringUtils.isNotBlank(item.getGroup())){
				querySql.append(" group by ");
				querySql.append(item.getGroup());
			}
			if(StringUtils.isNotBlank(item.getHaving())){
				querySql.append(" having ");
				querySql.append(item.getHaving());
			}
			if(StringUtils.isNotBlank(item.getOrder())){
				querySql.append(" order by ");
				querySql.append(item.getOrder());
			}
			Integer page = item.getLimit() != null ? item.getLimit() : 20;
			querySql.append(" limit ?, " + page + " ");
		}else{
			throw new Exception("查询对象或者表名称为空");
		}
		return querySql.toString();
	}

	public String getTableNames() {
		return tableNames;
	}
	public void setTableNames(String tableNames) {
		this.tableNames=getFilteredTableNames(tableNames);
	}
	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getHaving() {
		return having;
	}

	public void setHaving(String having) {
		this.having = having;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public List<QueryWhere> getWhereList() {
		return whereList;
	}

	public void setWhereList(List<QueryWhere> whereList) {
		this.whereList = whereList;
	}
	public String getOtherWhere() {
			return otherWhere;
	}
	public void setOtherWhere(String otherWhere) {
		this.otherWhere = otherWhere;
	}
	@Override
	public String toString() {
		return "QueryItem [tableNames=" + tableNames + ", fields=" + fields + ", order=" + order + ", group=" + group
				+ ", having=" + having + ", limit=" + limit + ", whereList=" + whereList + "]";
	}

	public String getFieldsReflect() {
		return fieldsReflect;
	}

	public void setFieldsReflect(String fieldsReflect) {
		this.fieldsReflect = fieldsReflect;
	}
	
}
