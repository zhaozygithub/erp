package com.dlcat.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSysBtn<M extends BaseSysBtn<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setBtnName(java.lang.String btnName) {
		set("btnName", btnName);
	}

	public java.lang.String getBtnName() {
		return get("btnName");
	}

	public void setMenuId(java.lang.Integer menuId) {
		set("menuId", menuId);
	}

	public java.lang.Integer getMenuId() {
		return get("menuId");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("createTime", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("createTime");
	}

	public void setUrl(java.lang.String url) {
		set("url", url);
	}

	public java.lang.String getUrl() {
		return get("url");
	}

	public void setSTATUS(java.lang.Integer STATUS) {
		set("STATUS", STATUS);
	}

	public java.lang.Integer getSTATUS() {
		return get("STATUS");
	}

}
