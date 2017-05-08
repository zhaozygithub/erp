package com.dlcat.service.flow.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseModel;
import com.dlcat.common.BaseService;
import com.dlcat.common.entity.FlowApproveDate;
import com.dlcat.common.utils.DateUtil;
import com.dlcat.core.model.FlowData;
import com.dlcat.core.model.FlowModel;
import com.dlcat.core.model.FlowNode;
import com.dlcat.core.model.FlowObject;
import com.dlcat.core.model.FlowTask;
import com.dlcat.core.model.LoanApplyApprove;
import com.dlcat.core.model.LoanApplyApproveData;
import com.dlcat.core.model.SysRole;
import com.dlcat.core.model.SysUser;
import com.dlcat.service.flow.FlowService;
import com.jfinal.plugin.activerecord.Record;
/**
 * 流程接口实现类
 * @author masai
 * @time 2017年4月19日 上午9:55:35
 */
public class FlowServiceImpl extends BaseService implements FlowService {
	
	public List<Map<String, Object>> flowNodeHandleInfo(String flowObjectNo) {	
		//得到节点
		List<FlowNode> nodes = FlowNode.getFlowNodeByObjectNo(flowObjectNo);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();	
		for(FlowNode node:nodes){
			Map<String, Object> flowMap= new HashMap<String, Object>();
			//获取节点名称
			String nodeName = node.getStr("node_name");
			flowMap.put("nodeName", nodeName);
			//获取该流程对象下 该节点的task
			FlowTask task = FlowTask.getFlowTaskByObjectNoAndNodeNo(flowObjectNo,node.getStr("node_no"));
			// 模型对象的节点没有流程任务信息，就跳出循环，找下一个节点的流程任务信息
			if (task == null){
				list.add(flowMap);	
				continue;
			}		
			flowMap.put("task", task);
			// 获取角色名
			String roleName = SysRole.getRoleNameByUserId(task.getInt("cur_approve_user_id"));	
			flowMap.put("roleName", roleName);
			//获取流程相关数据地址
			List<FlowData> datas = FlowData.getFlowDatasByFlowTaskNo(task.getStr("task_no"));
			List<String> pictures = new ArrayList<String>();
			for (FlowData data : datas) {
				pictures.add(data.getStr("url"));
			}
			flowMap.put("pictures", pictures);
			list.add(flowMap);	
		}
		return list;
	}
	public int flowInit(FlowApproveDate approveDate) {
		/*1.订单录入  2.订单评估   3.订单审核	4.区域风控	5.总部风控	6.签订合同	
		  7.总部贷款中心审核（线上 or 线下）	8.若是线下则进行财务审核，否则借款申请流程结束*/
		try {
			//（1）.创建流程对象
			insertFlowObject(approveDate.getLoanApplyApprove() , approveDate.getCurApproveUser());
			//（2）.创建流程初始化任务
			insertFlowTask(approveDate);
			//（3）更新流程对象（初始化更新）
			updateFlowObject(approveDate);
			//（4）.创建流程下一个任务
				//修改链式流程节点
			approveDate.setFlowNodeChainList(FlowNode.getFlowNodeChain(
					approveDate.getFlowNodeChainList().get(1).getStr("node_no")));
				//修改上一个流程任务
			approveDate.setPreFlowTask(FlowTask.getFristFlowTaskByObjectNo(
					approveDate.getLoanApplyApprove().getStr("id")));//流程对象编号=借款编号
			insertFlowTask(approveDate);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	public int handleFlowTask(FlowApproveDate approveDate) {
		//从页面获取下一个执行人、审批意见、
		int result = -1;
		try {
			//（1）.更新上一个流程任务记录	审批意见+审批时间+下一个流程任务审批人信息(编号、名称)
			updateCurFlowTask(approveDate);
			//（2）.创建下一个流程记录
			result = insertFlowTask(approveDate);
			//（3）.更新流程对象表
			updateFlowObject(approveDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 更新当前流程任务记录
	 * @param approveDate
	 * @return
	 * @author masai
	 * @time 2017年5月4日 下午4:17:49
	 */
	private int updateCurFlowTask(FlowApproveDate approveDate){
		try {
			FlowTask flowTask = approveDate.getCurFlowTask();
			flowTask.set("cur_approve_time", approveDate.getApproveTime());	//当前流程任务审批时间
			flowTask.set("cur_approve_opinion", approveDate.getOpinion());  //当前流程审批意见
			flowTask.set("next_approve_user_id", approveDate.getNextApproveUser().get("id"));	//下一个审批人编号
			flowTask.set("next_approve_user_name", approveDate.getNextApproveUser().get("name"));//下一个审批人名称
			return baseModel.baseUpdate(FlowTask.class	, "task_no", flowTask.toRecord());
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	/**
	 * 更新流程对象
	 * @param approveDate
	 * @return
	 * @author masai
	 * @time 2017年5月4日 下午4:30:28
	 */
	private int updateFlowObject(FlowApproveDate approveDate){
		try {
			FlowObject flowObject = approveDate.getFlowObject();
			flowObject.set("current_node_no", approveDate.getFlowNodeChainList().get(1).getStr("node_no"));	//当前节点编号
			flowObject.set("current_node_name", approveDate.getFlowNodeChainList().get(1).getStr("node_name"));//当前节点名称
			flowObject.set("current_node_user_id", approveDate.getCurApproveUser().get("id"));//当前审批人编号
			flowObject.set("current_node_user_name", approveDate.getCurApproveUser().get("name"));//当前审批人名称
			return baseModel.baseUpdate(FlowObject.class, "object_no", flowObject.toRecord());
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	/**
	 * 插入一条流程任务记录
	 * 注意：流程任务初始化(创建第一条流程任务) 与 创建下一个流程任务 都走此方法
	 * @param approveDate	流程审批数据
	 * @return
	 * @author masai
	 * @time 2017年5月3日 下午8:40:53
	 */
	private int insertFlowTask(FlowApproveDate approveDate){
		//创建下一个流程任务，并指定执行人，如果是第一个流程任务则状态是已审批，否则是未审批
		Record record = new Record();
		Map<String,Object> columnsMap = new HashMap<String, Object>();
		columnsMap.put("task_no",FlowTask.generateFlowTaskNo(approveDate.getFlowObject().getStr("object_no")));
		columnsMap.put("flow_object_no",approveDate.getFlowObject().getStr("object_no"));
		List<FlowNode> flowNodes = approveDate.getFlowNodeChainList();
		//上一节点
		if(flowNodes.get(0) != null){
			//表示不是首个节点 就有上一个节点
			columnsMap.put("pre_node_no",approveDate.getPreFlowTask().get("cur_node_no"));
			columnsMap.put("pre_node_name",approveDate.getPreFlowTask().get("cur_node_name"));
			columnsMap.put("pre_approve_user_id",approveDate.getPreFlowTask().get("cur_approve_user_id"));//当前审批人是待提交用户
			columnsMap.put("pre_approve_user_name",approveDate.getPreFlowTask().get("cur_approve_user_name"));
			columnsMap.put("pre_approve_opinion",approveDate.getPreFlowTask().get("cur_approve_opinion"));
			columnsMap.put("pre_approve_time",approveDate.getPreFlowTask().get("cur_approve_time"));
			//当前节点	上一节点不为空表示不是流程任务初始化
			columnsMap.put("cur_node_no",flowNodes.get(1).get("node_no"));
			columnsMap.put("cur_node_name",flowNodes.get(1).get("node_name"));
			columnsMap.put("cur_approve_user_id",approveDate.getNextApproveUser().get("id"));
			columnsMap.put("cur_approve_user_name",approveDate.getNextApproveUser().get("name"));
			columnsMap.put("is_approve",2);//未审批
		}else{
			//当前节点	上一节点为空表示为流程任务初始化
			columnsMap.put("cur_node_no",flowNodes.get(1).get("node_no"));
			columnsMap.put("cur_node_name",flowNodes.get(1).get("node_name"));
			columnsMap.put("cur_approve_user_id",approveDate.getCurApproveUser().get("id"));//当前审批人是当前用户
			columnsMap.put("cur_approve_user_name",approveDate.getCurApproveUser().get("name"));
			columnsMap.put("cur_approve_opinion",approveDate.getOpinion());
			columnsMap.put("cur_approve_time",approveDate.getApproveTime());
			columnsMap.put("is_approve",1);//已审批
		}
		//下一节点
		if(flowNodes.get(2) != null){
			columnsMap.put("next_node_no",flowNodes.get(2).get("node_no"));
			columnsMap.put("next_node_name",flowNodes.get(2).get("node_name"));
		}
		record.setColumns(columnsMap);
		return baseModel.baseInsert(FlowTask.class, "task_no", record);
	}
	/**
	 * 插入流程对象记录
	 * @param loanApplyApprove	借款申请
	 * @param sysUser	当前登录用户
	 * @return
	 * @throws Exception
	 * @author masai
	 * @time 2017年5月2日 下午6:29:35
	 */
	@SuppressWarnings("unchecked")
	private int insertFlowObject(LoanApplyApprove loanApplyApprove , SysUser sysUser) throws Exception{
		//根据借款业务种类获取流程模型 流程模型的flow_type也就是借款业务种类的id
		FlowModel flowModel = FlowModel.getFlowModelByFlowType(loanApplyApprove.getInt("category_id"));
		Record record = new Record();
		Map<String,Object> columnsMap = new HashMap<String, Object>();
		columnsMap.put("object_no", loanApplyApprove.get("id"));//业务编号=流程对象编号
		columnsMap.put("object_name", loanApplyApprove.get("loan_name"));
		columnsMap.put("flow_model_id", flowModel.get("id"));
		columnsMap.put("flow_model_name", flowModel.get("name"));
		columnsMap.put("sponsor_user_id", sysUser.get("id"));
		columnsMap.put("sponsor_user_name", sysUser.get("name"));
		columnsMap.put("sponsor_org_id", sysUser.get("belong_org_id"));
		columnsMap.put("sponsor_org_name", sysUser.get("belong_org_name"));
		columnsMap.put("sponsor_time", DateUtil.getCurrentTime());
		record.setColumns(columnsMap);
		return baseModel.baseInsert(FlowObject.class, "object_no", record);
	}
	
}
