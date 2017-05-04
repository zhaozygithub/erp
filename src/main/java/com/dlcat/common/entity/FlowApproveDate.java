package com.dlcat.common.entity;

import java.util.List;

import com.dlcat.core.model.FlowNode;
import com.dlcat.core.model.FlowObject;
import com.dlcat.core.model.FlowTask;
import com.dlcat.core.model.LoanApplyApprove;
import com.dlcat.core.model.SysUser;

/**
 * 流程审批数据类
 * 注意：当一个流程即将提交的时候，提交人就是当前审批人，
 * 选择提交给下一个步审批人称为下一个审批人
 * @author masai
 * @time 2017年5月3日 上午10:52:22
 */
public class FlowApproveDate {
	/**
	 * 流程审批对象
	 */
	private FlowObject flowObject;
	/**
	 * 借款申请
	 */
	private LoanApplyApprove loanApplyApprove;
	/**
	 * 当前审批人
	 */
	private SysUser curApproveUser;
	/**
	 * 下一个审批人
	 */
	private SysUser nextApproveUser;
	/**
	 * 当前流程审批意见
	 */
	private String opinion;
	/**
	 * 当前审批时间(时间戳)
	 */
	private Long approveTime;
	/**
	 * 流程审批节点链	上一个节点+当前节点+下一个节点
	 */
	private List<FlowNode> flowNodeChainList;
	/**
	 * 上一个流程审批任务
	 */
	private FlowTask preFlowTask;
	
	public Long getApproveTime() {
		return approveTime;
	}
	public void setApproveTime(Long approveTime) {
		this.approveTime = approveTime;
	}
	public FlowTask getPreFlowTask() {
		return preFlowTask;
	}
	public void setPreFlowTask(FlowTask preFlowTask) {
		this.preFlowTask = preFlowTask;
	}
	public FlowObject getFlowObject() {
		return flowObject;
	}
	public void setFlowObject(FlowObject flowObject) {
		this.flowObject = flowObject;
	}
	public String getOpinion() {
		return opinion;
	}
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
	public List<FlowNode> getFlowNodeChainList() {
		return flowNodeChainList;
	}
	public void setFlowNodeChainList(List<FlowNode> flowNodeChainList) {
		this.flowNodeChainList = flowNodeChainList;
	}
	public SysUser getCurApproveUser() {
		return curApproveUser;
	}
	public void setCurApproveUser(SysUser curApproveUser) {
		this.curApproveUser = curApproveUser;
	}
	public SysUser getNextApproveUser() {
		return nextApproveUser;
	}
	public void setNextApproveUser(SysUser nextApproveUser) {
		this.nextApproveUser = nextApproveUser;
	}
	public LoanApplyApprove getLoanApplyApprove() {
		return loanApplyApprove;
	}
	public void setLoanApplyApprove(LoanApplyApprove loanApplyApprove) {
		this.loanApplyApprove = loanApplyApprove;
	}
}
