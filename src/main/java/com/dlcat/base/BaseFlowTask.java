package com.dlcat.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseFlowTask<M extends BaseFlowTask<M>> extends Model<M> implements IBean {

	public void setTaskNo(java.lang.String taskNo) {
		set("task_no", taskNo);
	}

	public java.lang.String getTaskNo() {
		return get("task_no");
	}

	public void setFlowObjectNo(java.lang.String flowObjectNo) {
		set("flow_object_no", flowObjectNo);
	}

	public java.lang.String getFlowObjectNo() {
		return get("flow_object_no");
	}

	public void setCurNodeNo(java.lang.String curNodeNo) {
		set("cur_node_no", curNodeNo);
	}

	public java.lang.String getCurNodeNo() {
		return get("cur_node_no");
	}

	public void setCurNodeName(java.lang.String curNodeName) {
		set("cur_node_name", curNodeName);
	}

	public java.lang.String getCurNodeName() {
		return get("cur_node_name");
	}

	public void setCurApproveUserId(java.lang.Integer curApproveUserId) {
		set("cur_approve_user_id", curApproveUserId);
	}

	public java.lang.Integer getCurApproveUserId() {
		return get("cur_approve_user_id");
	}

	public void setCurApproveUserName(java.lang.String curApproveUserName) {
		set("cur_approve_user_name", curApproveUserName);
	}

	public java.lang.String getCurApproveUserName() {
		return get("cur_approve_user_name");
	}

	public void setCurApproveOpinion(java.lang.String curApproveOpinion) {
		set("cur_approve_opinion", curApproveOpinion);
	}

	public java.lang.String getCurApproveOpinion() {
		return get("cur_approve_opinion");
	}

	public void setCurApproveTime(java.lang.Integer curApproveTime) {
		set("cur_approve_time", curApproveTime);
	}

	public java.lang.Integer getCurApproveTime() {
		return get("cur_approve_time");
	}

	public void setPreNodeNo(java.lang.String preNodeNo) {
		set("pre_node_no", preNodeNo);
	}

	public java.lang.String getPreNodeNo() {
		return get("pre_node_no");
	}

	public void setPreNodeName(java.lang.String preNodeName) {
		set("pre_node_name", preNodeName);
	}

	public java.lang.String getPreNodeName() {
		return get("pre_node_name");
	}

	public void setPreApproveUserId(java.lang.Integer preApproveUserId) {
		set("pre_approve_user_id", preApproveUserId);
	}

	public java.lang.Integer getPreApproveUserId() {
		return get("pre_approve_user_id");
	}

	public void setPreApproveUserName(java.lang.String preApproveUserName) {
		set("pre_approve_user_name", preApproveUserName);
	}

	public java.lang.String getPreApproveUserName() {
		return get("pre_approve_user_name");
	}

	public void setPreApproveOpinion(java.lang.String preApproveOpinion) {
		set("pre_approve_opinion", preApproveOpinion);
	}

	public java.lang.String getPreApproveOpinion() {
		return get("pre_approve_opinion");
	}

	public void setPreApproveTime(java.lang.Integer preApproveTime) {
		set("pre_approve_time", preApproveTime);
	}

	public java.lang.Integer getPreApproveTime() {
		return get("pre_approve_time");
	}

	public void setNextNodeNo(java.lang.String nextNodeNo) {
		set("next_node_no", nextNodeNo);
	}

	public java.lang.String getNextNodeNo() {
		return get("next_node_no");
	}

	public void setNextNodeName(java.lang.String nextNodeName) {
		set("next_node_name", nextNodeName);
	}

	public java.lang.String getNextNodeName() {
		return get("next_node_name");
	}

	public void setNextApproveUserId(java.lang.Integer nextApproveUserId) {
		set("next_approve_user_id", nextApproveUserId);
	}

	public java.lang.Integer getNextApproveUserId() {
		return get("next_approve_user_id");
	}

	public void setNextApproveUserName(java.lang.String nextApproveUserName) {
		set("next_approve_user_name", nextApproveUserName);
	}

	public java.lang.String getNextApproveUserName() {
		return get("next_approve_user_name");
	}

	public void setIsApprove(java.lang.String isApprove) {
		set("is_approve", isApprove);
	}

	public java.lang.String getIsApprove() {
		return get("is_approve");
	}

}
