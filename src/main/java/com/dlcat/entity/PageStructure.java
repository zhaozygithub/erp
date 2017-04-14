package com.dlcat.entity;

import java.io.Serializable;
import java.util.List;

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
	 * 表头
	 */
	private List<TableHeader> tableHeader;
	/**
	 * 表格数据url
	 */
	private String tableDataUrl;
	
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

	public String getTableDataUrl() {
		return tableDataUrl;
	}

	public void setTableDataUrl(String tableDataUrl) {
		this.tableDataUrl = tableDataUrl;
	}

}