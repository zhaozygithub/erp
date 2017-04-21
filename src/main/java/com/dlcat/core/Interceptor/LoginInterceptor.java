package com.dlcat.core.Interceptor;

import java.util.ArrayList;

import org.apache.shiro.util.AntPathMatcher;

import com.dlcat.core.model.SysUser;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/** 
* @author zhaozhongyuan
* @date 2017年4月17日 下午2:36:00 
* @Description: 登录拦截 
*/
public class LoginInterceptor implements Interceptor {
	public static ArrayList<String> excludes = new ArrayList();

	static {
		excludes.add("/captcha");
		excludes.add("/toLogin");
		excludes.add("/doLogin");
		excludes.add("/");
	}
	
	public void intercept(Invocation inv) {
		String uri = inv.getActionKey();
        //去除excludes的路径
		AntPathMatcher pm = new AntPathMatcher();
		for (String pattern : excludes) {
			if (pm.match(pattern, uri)) {
				inv.invoke();
				return;
			}

		}
		
        //验证是否已经登录
		SysUser user = (SysUser) inv.getController().getSessionAttr("user");
		if (user == null) {
			inv.getController().redirect("/toLogin");
			return;
		}

		inv.invoke();

	}

}
