package com.dlcat.core.model;

import java.util.List;

import com.dlcat.common.BaseModel;
import com.dlcat.common.utils.StringUtils;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class FlowTask extends BaseModel<FlowTask> {
	public static final FlowTask dao = new FlowTask().dao();
	
	/**
	 * 根据sql语句查询流程任务
	 * @param sql
	 * @return
	 * @author masai
	 * @time 2017年4月26日 上午11:25:42
	 */
	public List<FlowTask> getFlowTasksBySql(String sql) {
		List<FlowTask> datas = FlowTask.dao.find(sql);
		return datas;
	}
	/**
	 * 根据主键查询流程任务
	 * @param taskNo
	 * @return
	 * @author masai
	 * @time 2017年4月26日 上午11:34:33
	 */
	public static FlowTask getFlowTasksById(String taskNo) {
		return StringUtils.isNotBlank(taskNo) ? FlowTask.dao.findById(taskNo) : null;
	}
	/**
	 * 根据流程对象编号和节点编号获取流程任务
	 */
	public static FlowTask getFlowTaskByObjectNoAndNodeNo(String objectNo,String nodeNo){
		return (StringUtils.isNotBlank(objectNo)&&StringUtils.isNotBlank(nodeNo)) ?
				FlowTask.dao.findFirst("select * from flow_task where flow_object_no=? and cur_node_no=? ",objectNo,nodeNo) : null;
	}
	/**
	 * 根据流程对象获取首个流程任务
	 * @param flowObjectNo 流程对象编号
	 * @return
	 * @author masai
	 * @time 2017年5月4日 下午3:12:22
	 */
	public static FlowTask getFristFlowTaskByObjectNo(String flowObjectNo){
		return StringUtils.isNotBlank(flowObjectNo) ? FlowTask.dao.findFirst("select * from flow_task where flow_object=?", flowObjectNo) : null;
	}
	/**
	 * 生成流程任务流水号
	 * 注：流程首个节点对应的流程任务流水号 eg:201705030001，不是首个节点则递增1
	 * @param flowObjectNo
	 * @return
	 * @author masai
	 * @time 2017年5月3日 下午3:58:38
	 */
	public static String generateFlowTaskNo(String flowObjectNo){
		String flowTaskNo = null;
		FlowTask fTask = null;
		if(StringUtils.isNotBlank(flowObjectNo)){
			fTask = FlowTask.dao.findFirst("select * from flow_task where flow_object_no=？ order by task_no desc", flowObjectNo); 
		}
		if(fTask != null){
			flowTaskNo = (fTask.getInt("task_no") + 1) + "";
		}else{
			flowTaskNo = flowObjectNo + "0001";
		}
		return flowTaskNo;
	}
	/**
	 * 根据用户id获取当前用户未审批的流程总数
	 * @author liuran
	 * @time 2017年5月5日 下午2:46:29
	 * @param userId
	 * @return int
	 */
	public static int getWaitFlowTaskCount(int userId){
		List<FlowTask> tasks = FlowTask.dao.find("select * from flow_task where cur_approve_user_id = ? and is_approve = '2'",userId);		
		return tasks.size();
	}
}

