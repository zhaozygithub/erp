package com.dlcat.entity;

import java.io.Serializable;

/**
 * 列表页 -- 表头
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
	private String fildName;
	/**
	 * 列名称集合
	 */
	private String[] names;
	/**
	 * 列字段集合
	 */
	private String[] fildNames;
	
	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public String[] getFildNames() {
		return fildNames;
	}

	public void setFildNames(String[] fildNames) {
		this.fildNames = fildNames;
	}

	public String getFildName() {
		return fildName;
	}

	public void setFildName(String fildName) {
		this.fildName = fildName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}