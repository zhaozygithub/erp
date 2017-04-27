package com.dlcat.service.flow.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.core.model.FlowData;
import com.dlcat.core.model.FlowNode;
import com.dlcat.core.model.FlowTask;
import com.dlcat.core.model.SysRole;
import com.dlcat.core.model.SysOrg;
import com.dlcat.service.flow.FlowService;
/**
 * 流程接口实现类
 * @author masai
 * @time 2017年4月19日 上午9:55:35
 */
public class FlowServiceImpl implements FlowService {

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
			FlowTask task = FlowTask.getFlowTaskByObjectNoNodeNo(flowObjectNo,node.getStr("node_no"));
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

	public void flowInit(String flowModelNo) {
		// TODO Auto-generated method stub
		
	}
	
	
}
