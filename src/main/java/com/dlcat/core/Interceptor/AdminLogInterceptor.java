package com.dlcat.core.Interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.dlcat.common.utils.StringUtils;
import com.dlcat.core.model.SysAdminLog;
import com.dlcat.core.model.SysUser;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class AdminLogInterceptor implements Interceptor {

	public void intercept(Invocation inv) {
		// TODO Auto-generated method stub

		Controller controller=inv.getController();
		
		if (StringUtils.isNotBlank(controller.getPara("btnid"))) {
			Map<String, String[]> parms=controller.getParaMap();
			HttpServletRequest request=controller.getRequest();
			SysUser user=controller.getSessionAttr("user");
			SysAdminLog.SetAdminLog(user, parms.get("btnid")[0], parms.toString(), request);
		}
		
		inv.invoke();
	}

}
