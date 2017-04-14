package com.dlcat.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseToCodeLibrary<M extends BaseToCodeLibrary<M>> extends Model<M> implements IBean {

	public void setCodeNo(java.lang.String codeNo) {
		set("code_no", codeNo);
	}

	public java.lang.String getCodeNo() {
		return get("code_no");
	}

	public void setItemNo(java.lang.String itemNo) {
		set("item_no", itemNo);
	}

	public java.lang.String getItemNo() {
		return get("item_no");
	}

	public void setItemName(java.lang.String itemName) {
		set("item_name", itemName);
	}

	public java.lang.String getItemName() {
		return get("item_name");
	}

	public void setSortNo(java.lang.String sortNo) {
		set("sort_no", sortNo);
	}

	public java.lang.String getSortNo() {
		return get("sort_no");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	public java.lang.Integer getStatus() {
		return get("status");
	}

	public void setItemDesc(java.lang.String itemDesc) {
		set("item_desc", itemDesc);
	}

	public java.lang.String getItemDesc() {
		return get("item_desc");
	}

	public void setRemark(java.lang.String remark) {
		set("remark", remark);
	}

	public java.lang.String getRemark() {
		return get("remark");
	}

}