package com.dlcat.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dlcat.core.utils.SecurityUtil;
import com.dlcat.model.SysMenu;
import com.dlcat.model.SysRole;
import com.dlcat.model.SysUser;
import com.jfinal.captcha.CaptchaRender;
import com.jfinal.core.Controller;

public class IndexController extends Controller {

	/**
	 * 默认方法
	 */
	public void index() {
		SysUser user = (SysUser) getSessionAttr("user");
		if (user != null) {
			toIndex();
			return;
		}

		toLogin();
	}

	/**
	 * 去首页
	 */
	private void toIndex() {
		SysUser user = (SysUser) getSessionAttr("user");
		int rid = user.getRoleId();
		// 根据rid去查看所具有的所有菜单
		String roleMenus = SysRole.dao.findById(rid).getRoleMenus();
		roleMenus = roleMenus.substring(0, roleMenus.length() - 1);
		List<SysMenu> list = SysMenu.dao.find("select * from sys_menu where id in (" + roleMenus + ")");
		// 构造一级菜单
		Map<Integer, List<SysMenu>> map0 = new HashMap<Integer,List<SysMenu>>();

		// 二级菜单map
		Map<Integer, List<SysMenu>> map = new HashMap<Integer,List<SysMenu>>();

		// 三级菜单
		Map<Integer, List<SysMenu>> map2 = new HashMap<Integer, List<SysMenu>>();
		
		for (SysMenu sysMenu : list) {
			Integer level = sysMenu.getLevel();
			Integer pid = sysMenu.getPid();
			Integer id = sysMenu.getId();
			if (level == 2) {
				getChildTree(pid, sysMenu, map);
			} else if (level == 3) {
				getChildTree(pid, sysMenu, map2);

			} else if (level == 1) {
				getChildTree(pid, sysMenu, map0);
			} else{
				//留着给四级菜单
//				fristMenu.add(sysMenu);
			}

		}
		
		Map<Integer, Map<Integer, List<SysMenu>>> map3=new HashMap<Integer, Map<Integer,List<SysMenu>>>();
		map3.put(1, map0);
		map3.put(2, map);
		map3.put(3, map2);
		setSessionAttr("menu", map3);
		render("index.html");
	}
	public void secMenu() {
		List<SysMenu> thirdMenu=getSessionAttr("thirdMenu");
		if (thirdMenu!=null) {
			removeSessionAttr("thirdMenu");
		}
		int index=getParaToInt(0);
		List<SysMenu> secMenu=getMenu(index, 2);
		setSessionAttr("secMenu", secMenu);
		render("index.html");
	}
	public void thirdMenu() {
		int index=getParaToInt(0);
		List<SysMenu> thirdMenu=getMenu(index, 3);
		setSessionAttr("thirdMenu", thirdMenu);
		render("index.html");
	}
	
	
	public List<SysMenu> getMenu(Integer index,Integer level) {
		List<SysMenu> secMenu=new ArrayList<SysMenu>();
		Map<Integer, Map<Integer, List<SysMenu>>> map=getSessionAttr("menu");
		Map<Integer, List<SysMenu>> m=map.get(level);
		Set<Integer>  keys= m.keySet();
		for (Integer integer : keys) {
			List<SysMenu> list=m.get(integer);
			for (SysMenu sysMenu : list) {
				if (sysMenu.getPid()==index) {
					secMenu.add(sysMenu);
				}
			}
		}
		return secMenu;
	}
	
	public void getChildTree(Integer pid,SysMenu sysMenu,Map<Integer, List<SysMenu>> map) {
		// 如果具有相同的一级菜单
		if (map.containsKey(pid)) {
			List<SysMenu> lMenus=map.get(pid);
			lMenus.add(sysMenu);
			
			map.put(pid, lMenus);
		} else {
			List<SysMenu> lMenus=new ArrayList<SysMenu>();
			lMenus.add(sysMenu);
			
			map.put(pid, lMenus);
		}
	}

	/**
	 * 滚到登录页面
	 */
	private void toLogin() {
		// TODO Auto-generated method stub
		render("login.html");
	}

	/**
	 * 登录验证
	 */
	public void doLogin() {
		String loginId = getPara("userName");
		String loginPwd = getPara("pwd");

		boolean istrue = validateCaptcha("captcha");

		if (istrue == false) {
			keepPara(new String[] { "userName", "pwd" });
			setAttr("msg", "验证码错误，请重新输入！");
			toLogin();
			return;
		}

		SysUser user = SysUser.dao.findFirst("select * from sys_user where name = ?", loginId);
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

		/*
		 * user.getRole(); try { // loginInit(this, user); } catch (Exception e)
		 * { setAttr("msg", e.getMessage()); keepPara(new String[] { "userName"
		 * }); toLogin(); return; }
		 */
		setSessionAttr("user", user);

		redirect("/");
	}

	/**
	 * 添加验证码
	 */
	public void captcha() {
		render(new CaptchaRender());
	}

	/*
	 * 退出登录
	 */
	public void doExit() {
		removeSessionAttr("user");
		redirect("/");
	}

}
