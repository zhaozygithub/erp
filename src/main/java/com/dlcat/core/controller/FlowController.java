package com.dlcat.core.controller;


import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.service.flow.impl.FlowServiceImpl;


public class FlowController extends BaseController {
	/**
	 * 显示审批流程信息
	 */
	public void index() {	
		
		// 流程对象编号值从表单获取
//		String objectNo = getPara("objectNo");
		String objectNo = "1";
		FlowServiceImpl fImpl = new FlowServiceImpl();
		List<Map<String, Object>> list = fImpl.flowNodeHandleInfo(objectNo);
		setAttr("list", list);
		render("flow.html");	
	}
}
