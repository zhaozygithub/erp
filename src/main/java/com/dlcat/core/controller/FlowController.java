package com.dlcat.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dlcat.core.model.FlowData;
import com.dlcat.core.model.FlowNode;
import com.dlcat.core.model.FlowObject;
import com.dlcat.core.model.FlowTask;
import com.dlcat.core.model.SysRole;
import com.dlcat.core.model.SysUser;
import com.jfinal.core.Controller;

public class FlowController extends Controller {
	/**
	 * 显示审批流程信息
	 */
	public void index() {	
		
		// 流程对象编号值从表单获取
//		String objectNo = getPara("objectNo");
		String objectNo = "1";
		try {
			// 获取流程对象和对象名称
			String objectName = FlowObject.getFlowObjectsById(objectNo).get(0).getStr("object_name");
			setAttr("objectName", objectName);
		} catch (Exception e) {
			// 流程对象编号在数据库查不到
			e.printStackTrace();
			redirect("/");
			return;
		}	
		List<FlowNode> nodes = FlowNode.getFlowNodeByObjectNo(objectNo);  // 根据流程对象编号获取节点信息 流程编号objectNo
		Map<Integer, String> nodesmap = new HashMap<Integer, String>();// 节点map
		List<FlowTask> Tasks = new ArrayList<FlowTask>(); //流程任务list
		Map<Integer, String> rolesmap = new HashMap<Integer, String>(); // 角色map
		Map<Integer, List<String>> datasMap = new HashMap<Integer, List<String>>();// 资料map

		for (FlowNode node : nodes) {
			int nodeSortOrder= node.getInt("node_sort_order");
			nodesmap.put(nodeSortOrder, node.getStr("node_name")); // 按照节点顺序put到map
			FlowTask flowTask = FlowTask.getFlowTaskByObjectNoNodeNo(objectNo,node.getStr("node_no"));
			if (flowTask == null)// 模型对象的节点没有流程任务信息，就跳出循环，找下一个节点的流程任务信息
				continue;
			Tasks.add(nodeSortOrder - 1, flowTask);// 按节点顺序 获取流程任务信息
			// 获取角色名与资料
			SysUser user = SysUser.dao.findById(flowTask.getInt("cur_approve_user_id"));
			SysRole role = SysRole.dao.findById(user.getInt("role_id"));
			rolesmap.put(nodeSortOrder, role.getStr("role_name"));// 角色
			List<FlowData> datas = FlowData.getFlowDatasByFlowTaskNo(flowTask.getStr("task_no"));
			List<String> datasUrl = new ArrayList<String>();
			for (FlowData data : datas) {
				datasUrl.add(data.getStr("url"));
			}
			datasMap.put(nodeSortOrder, datasUrl);
		}
		setAttr("roles", rolesmap); // key节点顺序
		setAttr("nodes", nodesmap); // key节点顺序
		setAttr("tasks", Tasks);	// 根据节点顺序
		setAttr("datas", datasMap); // key节点顺序
		render("flow.html");
	}
}