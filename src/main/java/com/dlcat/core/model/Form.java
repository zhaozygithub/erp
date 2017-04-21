package com.dlcat.core.model;

import java.util.List;

/** 
* @author zhaozhongyuan
* @date 2017年4月19日 上午11:11:47 
* @Description: 封装form表单  
*/
public class Form {
	//表单type为增删改查
private String type,tableName;
private List<Widget> widgets;

public Form(String type, String tableName, List<Widget> widgets) {
	super();
	this.type = type;
	this.tableName = tableName;
	this.widgets = widgets;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getTableName() {
	return tableName;
}
public void setTableName(String tableName) {
	this.tableName = tableName;
}
public List<Widget> getWidgets() {
	return widgets;
}
public void setWidgets(List<Widget> widgets) {
	this.widgets = widgets;
}
}
