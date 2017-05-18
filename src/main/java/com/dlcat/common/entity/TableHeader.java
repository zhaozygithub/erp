package com.dlcat.common.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox.KeySelectionManager;

/**
 * 列表页 -- 表头
 * @author masai
 * @time 2017年4月13日 下午8:50:41
 */
public class TableHeader implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 列名称
	 */
	private String CNName;
	/**
	 * 列字段
	 */
	private String fieldName;
	/**
	 * 列名称集合
	 */
	private String[] CNNames;
	/**
	 * 列字段集合
	 */
	private String[] fieldNames;
	/**
	 * 多行复选
	 */
	private boolean multiple=false;
	//多行复选时,需要指明主键等字段,不指明时默认是表格最左边一列
	private ArrayList<String> keys;
	
	public ArrayList<String> getKeys() {
		return keys;
	}
	public void setKeys(ArrayList<String> keys) {
		this.keys = keys;
	}
	public boolean isMultiple() {
		return multiple;
	}
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String[] getFieldNames() {
		return fieldNames;
	}
	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}
	public String getCNName() {
		return CNName;
	}
	public void setCNName(String cNName) {
		CNName = cNName;
	}
	public String[] getCNNames() {
		return CNNames;
	}
	public void setCNNames(String[] cNNames) {
		CNNames = cNNames;
	}
}