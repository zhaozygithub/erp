package com.dlcat.core.model;

import java.util.List;

import com.dlcat.common.BaseModel;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class FlowObject extends BaseModel<FlowObject> {
	public static final FlowObject dao = new FlowObject().dao();
	/**
	 * 根据sql语句查询
	 * @param sql
	 * @return
	 * @author masai
	 * @time 2017年4月26日 上午11:35:10
	 */
	public static List<FlowObject> getFlowObjectsBySql(String sql) {
		List<FlowObject> datas = FlowObject.dao.find(sql);
		return datas;
	}
	/**
	 * 根据主键获取流程对象
	 * @param objectNo
	 * @return
	 * @author masai
	 * @time 2017年4月26日 上午11:37:43
	 */
	public static FlowObject getFlowObjectsById(String objectNo) {
		return FlowObject.dao.findById(objectNo);
	}
}
