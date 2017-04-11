package com.dlcat.controller;

import com.dlcat.core.utils.SecurityUtil;
import com.dlcat.model.SysUser;
import com.jfinal.captcha.CaptchaRender;
import com.jfinal.core.Controller;


public class IndexController extends Controller {
	public void index() {
		SysUser user = (SysUser) getSessionAttr("user");
		if (user != null) {
			toIndex();
			return;
		}

		toLogin();
	}

	private void toIndex() {
		// TODO Auto-generated method stub
		render("index.html");
	}

	private void toLogin() {
		// TODO Auto-generated method stub
		render("login.html");
	}
	
	public void doLogin() {
		String loginId = getPara("userName");
		String loginPwd = getPara("pwd");

		boolean istrue=validateCaptcha("captcha");
		
		if (istrue==false) {
			keepPara(new String[] { "userName","pwd"});
			setAttr("msg", "验证码错误，请重新输入！");
			toLogin();
			return;
		}


		SysUser user=SysUser.dao.findFirst("select * from sys_user where name = ?", loginId);
		if (user == null) {
			setAttr("msg", "用户名不存在");
			toLogin();
			return;
		}

		if (!(user.getStr("password").equals(SecurityUtil.sha1(loginPwd)))) {
			setAttr("msg", "密码错误");
			keepPara(new String[] { "userName" });
			toLogin();
			return;
		}

		
/*		try {
//			loginInit(this, user);
		} catch (Exception e) {
			setAttr("msg", e.getMessage());
			keepPara(new String[] { "userName" });
			toLogin();
			return;
		}*/
		setSessionAttr("user", user);

		redirect("/");
	}
	
	/**
	 *添加验证码
	 */
	public void captcha() {
		render(new CaptchaRender());
	}
	
	/*
	 * 退出登录
	 * */
	public void doExit() {
		removeSessionAttr("user");
		redirect("/");
	}
	
	
	
}
