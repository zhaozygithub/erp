package com.dlcat.service.flow;

import java.util.List;
import java.util.Map;


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
	 * @param flowModelNo
	 * @author masai
	 * @time 2017年4月26日 下午5:07:32
	 */
	public void flowInit(String flowModelNo);
	
}
