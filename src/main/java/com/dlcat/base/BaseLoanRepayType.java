package com.dlcat.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseLoanRepayType<M extends BaseLoanRepayType<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setContents(java.lang.String contents) {
		set("contents", contents);
	}

	public java.lang.String getContents() {
		return get("contents");
	}

	public void setStatus(java.lang.Boolean status) {
		set("status", status);
	}

	public java.lang.Boolean getStatus() {
		return get("status");
	}

}
