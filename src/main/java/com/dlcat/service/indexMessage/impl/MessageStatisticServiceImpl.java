package com.dlcat.service.indexMessage.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseService;
import com.dlcat.core.model.FlowTask;
import com.dlcat.service.indexMessage.MessageStatisticService;

/**
 * 首页消息统计 接口实现类
 * @ClassName MessageStatisticServiceImpl
 * @author liuran
 * @time 2017年5月5日 下午2:24:12
 */
public class MessageStatisticServiceImpl extends BaseService implements
		MessageStatisticService {
	
	/**
	 * 首页消息统计
	 */
	public Map<String, Integer> messageStatistic(int userId) {
		Map<String, Integer> messageMap = new HashMap<String, Integer>();	
		//此处增加消息处理类型
		messageMap.put("flowcount", flowcount(userId));
		
		return messageMap;
	}

	/**
	 * 流程信息统计
	 */
	public int flowcount(int userId) {		
		return FlowTask.getWaitFlowTaskCount(userId);
	}

	
}
