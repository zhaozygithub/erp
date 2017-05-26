package com.dlcat.core.controller.index;

import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.SSOToken;
import com.baomidou.kisso.common.IpHelper;
import com.baomidou.kisso.common.util.HttpUtil;
import com.dlcat.common.BaseController;

public class LoginController extends BaseController {
	public void index() {
		SSOToken mt = SSOHelper.getToken(getRequest());
		if ( mt != null ) {
			redirect("/");
			return;
		}

		/**
		 * 登录 生产环境需要过滤sql注入
		 */
		if ( HttpUtil.isPost(getRequest()) ) {
				mt = new SSOToken();
				mt.setId(1354L);
				mt.setUid("1888");
				mt.setIp(IpHelper.getIpAddr(getRequest()));
				
				//记住密码，设置 cookie 时长 1 周 = 604800 秒 【动态设置 maxAge 实现记住密码功能】
				//String rememberMe = req.getParameter("rememberMe");
				//if ( "on".equals(rememberMe) ) {
				//	request.setAttribute(SSOConfig.SSO_COOKIE_MAXAGE, 604800); 
				//}
				
				SSOHelper.setSSOCookie(getRequest(), getResponse(), mt, true);
				forwardAction("/doLogin");
				return;
			}
		render("/WEB-INF/login.html");
	}
}
