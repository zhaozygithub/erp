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