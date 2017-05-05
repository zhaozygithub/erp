package com.dlcat.service.indexMessage;

import java.util.List;
import java.util.Map;

/**
 * 首页消息统计 相关接口
 * @ClassName MessageStatisticService
 * @author liuran
 * @time 2017年5月5日 下午2:23:34
 */
public interface MessageStatisticService  {
	/**
	 * 首页消息统计Map
	 * @author liuran
	 * @time 2017年5月5日 下午2:29:50
	 * @param userId
	 * @return Map<String,Integer>
	 */
	public Map<String, Integer> messageStatistic(int userId);
	/**
	 * 流程信息统计
	 * @author liuran
	 * @time 2017年5月5日 下午2:22:22
	 * @param userId
	 * @return int
	 */
	public int flowcount(int userId);
	
	
}
