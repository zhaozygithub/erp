package com.dlcat.common.entity;

import java.io.Serializable;

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
	private String name;
	/**
	 * 列字段
	 */
	private String fieldName;
	/**
	 * 列名称集合
	 */
	private String[] names;
	/**
	 * 列字段集合
	 */
	private String[] fieldNames;
	
	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public String[] getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}