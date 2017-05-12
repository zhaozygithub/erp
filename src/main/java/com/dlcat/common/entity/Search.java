package com.dlcat.common.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.dlcat.core.model.ToCodeLibrary;

/**
 * 列表页 -- 查询框结构
 * @ClassName: Search
 * @author: masai
 * @date: 2017年4月13日 上午11:36:41
 */
public class Search implements Serializable {
	private static final long serialVersionUID = 1967564435044525324L;
	/**
	 * 搜索框名称（表中字段）
	 */
	private String fieldName;
	/**
	 * 搜索框字段值（字段汉译）
	 */
	private String CNName;
	/**
	 * 搜索框类型
	 */
	private String type;
	/**
	 * 搜索框名称集合
	 */
	private String[] fieldNames;
	/**
	 * 搜索框字段值集合
	 */
	private String[] CNNames;
	/**
	 * 搜索框类型集合
	 */
	private String[] types;
	/**
	 * 字典映射值，搜索框对应代码表中的字典值
	 */
	private List<ToCodeLibrary> optionList;
	/**
	 * 所有字典映射值列表Map集合
	 */
	private Map<String,List<ToCodeLibrary>> optionListMap;
	
	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ToCodeLibrary> getOptionList() {
		return optionList;
	}

	public void setOptionList(List<ToCodeLibrary> optionList) {
		this.optionList = optionList;
	}

	public Map<String,List<ToCodeLibrary>> getOptionListMap() {
		return optionListMap;
	}

	public void setOptionListMap(Map<String,List<ToCodeLibrary>> optionListMap) {
		this.optionListMap = optionListMap;
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

}