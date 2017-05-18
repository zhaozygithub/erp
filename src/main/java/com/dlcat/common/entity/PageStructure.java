package com.dlcat.common.entity;

import java.io.Serializable;
import java.util.List;

import com.dlcat.core.model.SysMenu;
import com.jfinal.plugin.activerecord.Record;

/**
 * 列表页 -- 页面结构
 * @ClassName: PageStructure
 * @author: masai
 * @date: 2017年4月13日 下午12:33:07
 */
public class PageStructure implements Serializable {
	private static final long serialVersionUID = 406270128258960210L;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 列表记录表示（适用于选中某条列表记录后，传到后台的参数）
	 */
	private String check;
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
	 * 请求表格数据地址
	 */
	private String dataUrl;
	
	//private boolean multiple;
	//此处存放的是原来的TableHeader,其中的 Multiple和主键会被用到.
	private TableHeader tableHeaderData;
	
	public TableHeader getTableHeaderData() {
		return tableHeaderData;
	}

	public void setTableHeaderData(TableHeader tableHeaderData) {
		this.tableHeaderData = tableHeaderData;
	}

	/*public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}*/

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

	public List<SysMenu> getButton() {
		return button;
	}

	public void setButton(List<SysMenu> button) {
		this.button = button;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataUrl() {
		return dataUrl;
	}

	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}

	public String getCheck() {
		return check;
	}

	public void setCheck(String check) {
		this.check = check;
	}

}