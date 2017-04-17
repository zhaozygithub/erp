package com.dlcat.entity;

import java.io.Serializable;
import java.util.List;

import com.dlcat.model.SysMenu;

/**
 * 列表页 -- 页面结构
 * @ClassName: PageStructure
 * @author: masai
 * @date: 2017年4月13日 下午12:33:07
 */
public class PageStructure implements Serializable {
	private static final long serialVersionUID = 406270128258960210L;
	/**
	 * 搜索框
	 */
	private List<Search> search;
	/**
	 * 按钮
	 */
	private List<SysMenu> button;
	/**
	 * 表头
	 */
	private List<TableHeader> tableHeader;
	/**
	 * 表格数据
	 */
	private List<Object> dataList;
	
	public List<Search> getSearch() {
		return search;
	}

	public void setSearch(List<Search> search) {
		this.search = search;
	}

	public List<TableHeader> getTableHeader() {
		return tableHeader;
	}

	public void setTableHeader(List<TableHeader> tableHeader) {
		this.tableHeader = tableHeader;
	}

	public List<Object> getDataList() {
		return dataList;
	}

	public void setDataList(List<Object> dataList) {
		this.dataList = dataList;
	}

	public List<SysMenu> getButton() {
		return button;
	}

	public void setButton(List<SysMenu> button) {
		this.button = button;
	}

}