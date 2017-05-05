package com.dlcat.service.flow;

import java.util.List;
import java.util.Map;

import com.dlcat.common.entity.FlowApproveDate;
import com.dlcat.core.model.SysUser;


/**
 * 流程相关接口
 * @author masai
 * @time 2017年4月19日 上午9:55:15
 */
public interface FlowService {
	/**
	 * 获取流程处理信息
	 * @param flowObjectNo	流程对象编号
	 * @return
	 * @author masai
	 * @time 2017年4月26日 下午3:04:05
	 */
	public List<Map<String, Object>> flowNodeHandleInfo(String flowObjectNo );
	/**
	 * 流程数据初始化
	 * 注：业务员发起一笔流程时，进行流程初始化
	 * @param approveDate	流程审批数据
	 * @author masai
	 * @time 2017年4月26日 下午5:07:32
	 */
	public int flowInit(FlowApproveDate approveDate);
	/**
	 * 处理流程任务（不包括流程任务）
	 * 注：完成当前流程任务后，即创建下一个流程任务，并指定处理人
	 * @param approveDate	流程审批数据
	 * @author masai
	 * @time 2017年5月3日 上午10:06:38
	 */
	public int handleFlowTask(FlowApproveDate approveDate);
}
