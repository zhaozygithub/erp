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
	 * 搜索框名称
	 */
	private String name;
	/**
	 * 搜索框字段值
	 */
	private String filedName;
	/**
	 * 搜索框类型
	 */
	private String type;
	/**
	 * 搜索框名称集合
	 */
	private String[] names;
	/**
	 * 搜索框字段值集合
	 */
	private String[] filedNames;
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
	
	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public String[] getFiledNames() {
		return filedNames;
	}

	public void setFiledNames(String[] filedNames) {
		this.filedNames = filedNames;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFiledName() {
		return filedName;
	}

	public void setFiledName(String filedName) {
		this.filedName = filedName;
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
}