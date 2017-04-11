package com.dlcat.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSysUser<M extends BaseSysUser<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Long id) {
		set("id", id);
	}

	public java.lang.Long getId() {
		return get("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return get("name");
	}

	public void setPassword(java.lang.String password) {
		set("password", password);
	}

	public java.lang.String getPassword() {
		return get("password");
	}

	public void setRoleId(java.lang.Integer roleId) {
		set("role_id", roleId);
	}

	public java.lang.Integer getRoleId() {
		return get("role_id");
	}

	public void setBelongOrgId(java.lang.Integer belongOrgId) {
		set("belong_org_id", belongOrgId);
	}

	public java.lang.Integer getBelongOrgId() {
		return get("belong_org_id");
	}

	public void setBelongOrgName(java.lang.String belongOrgName) {
		set("belong_org_name", belongOrgName);
	}

	public java.lang.String getBelongOrgName() {
		return get("belong_org_name");
	}

	public void setPhone(java.lang.String phone) {
		set("phone", phone);
	}

	public java.lang.String getPhone() {
		return get("phone");
	}

	public void setStatus(java.lang.String status) {
		set("status", status);
	}

	public java.lang.String getStatus() {
		return get("status");
	}

	public void setInputUserId(java.lang.Integer inputUserId) {
		set("input_user_id", inputUserId);
	}

	public java.lang.Integer getInputUserId() {
		return get("input_user_id");
	}

	public void setInputUserName(java.lang.String inputUserName) {
		set("input_user_name", inputUserName);
	}

	public java.lang.String getInputUserName() {
		return get("input_user_name");
	}

	public void setInputTime(java.lang.Integer inputTime) {
		set("input_time", inputTime);
	}

	public java.lang.Integer getInputTime() {
		return get("input_time");
	}

	public void setUpdateTime(java.lang.Integer updateTime) {
		set("update_time", updateTime);
	}

	public java.lang.Integer getUpdateTime() {
		return get("update_time");
	}

	public void setUpdateUserId(java.lang.Integer updateUserId) {
		set("update_user_id", updateUserId);
	}

	public java.lang.Integer getUpdateUserId() {
		return get("update_user_id");
	}

	public void setLastLoginTime(java.lang.Integer lastLoginTime) {
		set("last_login_time", lastLoginTime);
	}

	public java.lang.Integer getLastLoginTime() {
		return get("last_login_time");
	}

	public void setLastLoginIp(java.lang.String lastLoginIp) {
		set("last_login_ip", lastLoginIp);
	}

	public java.lang.String getLastLoginIp() {
		return get("last_login_ip");
	}

	public void setLoginCount(java.lang.Integer loginCount) {
		set("login_count", loginCount);
	}

	public java.lang.Integer getLoginCount() {
		return get("login_count");
	}

}
