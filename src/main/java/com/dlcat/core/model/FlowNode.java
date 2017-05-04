package com.dlcat.core.model;

import java.util.ArrayList;
import java.util.List;

import com.dlcat.common.BaseModel;
import com.dlcat.common.utils.StringUtils;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class FlowNode extends BaseModel<FlowNode> {
	public static final FlowNode dao = new FlowNode().dao();
	
	/**
	 * 通过sql语句获取流程节点
	 * @param sql
	 * @return
	 * @author masai
	 * @time 2017年4月26日 上午11:54:16
	 */
	public static List<FlowNode> getFlowNodesBySql(String sql) {
		List<FlowNode> datas = FlowNode.dao.find(sql);
		return datas;
	}
	
	/**
	 * 根据主键获取流程节点
	 * @param nodeNo
	 * @return
	 * @author masai
	 * @time 2017年4月26日 上午11:56:25
	 */
	public static FlowNode getFlowNodesById(String nodeNo) {
		return StringUtils.isNotBlank(nodeNo) ? FlowNode.dao.findById(nodeNo) : null;
	}
	
	/**
	 * 根据流程对象编号获取流程节点
	 */
	public static  List<FlowNode> getFlowNodeByObjectNo(String objectNo) {
	    int modelId= FlowObject.getFlowObjectsById(objectNo).getInt("flow_model_id");	
		return getFlowNodeByModelId(modelId);
	}
	/**
	 * 根据流程模型编号获取流程节点
	 * @author liuran
	 * @time 2017年5月4日 下午12:14:46
	 * @param modelId
	 * @return List<FlowNode>
	 */
	public static List<FlowNode> getFlowNodeByModelId(int modelId){
		List<FlowNode> nodes=FlowNode.dao.find("select * from flow_node where flow_model_id = ? AND status = '1' ORDER BY node_sort_order ASC",modelId);
		return nodes;
	}
	
	/**
	 * 根据流程模型编号获取流程节点
	 * @param modelNo	流程模型编号
	 * @return
	 * @author masai
	 * @time 2017年5月2日 上午10:40:13
	 */
	public static List<FlowNode> getFlowNodeByModelNo(String modelNo){
		List<FlowNode> flowNodeList = null;
		if(StringUtils.isNotBlank(modelNo)){
			flowNodeList = FlowNode.dao.find("select * from flow_node where flow_model_id=? and status='1' order by node_sort_order asc",modelNo);
		}
		return flowNodeList;
	}
	/**
	 * 获取当前审批节点链	上一节点-->当前节点-->下一节点
	 * @param nodeNo
	 * @return
	 * @author masai
	 * @time 2017年5月3日 下午12:43:54
	 */
	public static	List<FlowNode> getFlowNodeChain(String nodeNo){
		FlowNode curNode = null;
		List<FlowNode> flowNodeList = new ArrayList<FlowNode>();
		if(StringUtils.isNotBlank(nodeNo)){
			curNode = FlowNode.dao.findById(nodeNo);
		}
		if(curNode != null){
			flowNodeList.add(StringUtils.isNotBlank(curNode.getStr("pre_node_no")) ? 
							FlowNode.dao.findById(curNode.getStr("pre_node_no")) : null);	//上一节点
			flowNodeList.add(curNode);		//当前节点
			flowNodeList.add(StringUtils.isNotBlank(curNode.getStr("next_node_no")) ? 
							FlowNode.dao.findById(curNode.getStr("next_node_no")) : null);	//下一节点
		}else{
			flowNodeList.add(null);
			flowNodeList.add(curNode);
			flowNodeList.add(null);
		}
		return flowNodeList;
	}
	/**
	 * 根据流程模型编号获取流程首个节点对象
	 * @param modelNo	流程模型编号
	 * @return
	 * @author masai
	 * @time 2017年5月2日 下午8:50:57
	 */
	public static FlowNode getFlowNodeFristByModelNo(String modelNo){
		FlowNode flowNode = null;
		if(StringUtils.isNotBlank(modelNo)){
			flowNode = FlowNode.dao.findFirst("select * from flow_node where flow_model_id=? and status='1' order by node_sort_order asc", modelNo);
		}
		return flowNode;
	}
	/**
	 * 根据当前流程节点编号获取下一个流程节点对象
	 * @param nodeNo	当前流程节点编号
	 * @return
	 * @author masai
	 * @time 2017年5月2日 下午9:00:00
	 */
	public static FlowNode getFlowNodeNextByNodeNo(Integer nodeNo){
		FlowNode nextFlowNode = null;
		if(nodeNo != null){
			//根据当前流程节点编号获取流程节点
			FlowNode curFlowNode = FlowNode.dao.findById(nodeNo);
			nextFlowNode = FlowNode.dao.findById(curFlowNode.get("next_node_no"));
		}
		return nextFlowNode;
	}
	/**
	 * 根据当前流程节点编号获取上一个流程节点对象
	 * @param nodeNo	当前流程节点编号
	 * @return
	 * @author masai
	 * @time 2017年5月2日 下午9:16:44
	 */
	public static FlowNode getFlowNodePreByNodeNo(Integer nodeNo){
		FlowNode nextFlowNode = null;
		if(nodeNo != null){
			//根据当前流程节点编号获取流程节点
			FlowNode curFlowNode = FlowNode.dao.findById(nodeNo);
			nextFlowNode = FlowNode.dao.findById(curFlowNode.get("pre_node_no"));
		}
		return nextFlowNode;
	}
	
}