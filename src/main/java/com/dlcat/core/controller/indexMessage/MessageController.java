package com.dlcat.core.controller.indexMessage;

import java.util.HashMap;
import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.core.model.SysUser;
import com.dlcat.service.indexMessage.MessageStatisticService;
import com.dlcat.service.indexMessage.impl.MessageStatisticServiceImpl;

public class MessageController extends BaseController {
	private MessageStatisticService messageStatisticService = new MessageStatisticServiceImpl();
	
	public void index(){
		SysUser user = (SysUser) getSessionAttr("user");
		int userId = user.getInt("id");
		Map<String, Integer> messageMap = messageStatisticService.messageStatistic(userId);
		setAttr("message", messageMap);
		render("index_message.html");
	}
}
