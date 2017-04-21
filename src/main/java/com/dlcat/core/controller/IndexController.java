package com.dlcat.core.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysRole;
import com.dlcat.core.model.SysUser;
import com.jfinal.captcha.CaptchaRender;
import com.jfinal.core.Controller;
import com.jfinal.kit.HashKit;
import com.jfinal.plugin.activerecord.Db;

/** 
* @author zhaozhongyuan
* @date 2017年4月17日 上午11:21:15 
* @Description: TODO  
*/
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
		render("index.html");
	}

	/**
	 * 递归构建菜单树
	 * 
	 * @param id
	 * @return
	 * @author masai
	 * @time 2017年4月14日 下午6:49:32
	 */
	private SysMenu recursiveTree(int id) {
		// TODO Auto-generated method stub
		// 根据id获取节点对象
		Map<Integer, SysMenu> map = getSessionAttr("menus");
		SysMenu sysMenu = map.get(id);

		// 获取该节点的所有children
		List<SysMenu> childTreeNodes = getChildren(id);
		if (childTreeNodes.size() > 0) {
			for (SysMenu child : childTreeNodes) {
				SysMenu sysMenu2 = recursiveTree(child.getInt("id")); // 递归
				sysMenu.getChildren().add(sysMenu2);
			}
		}
		return sysMenu;
	}

	/**
	 * 获取子菜单
	 * 
	 * @param pid
	 * @return
	 * @author masai
	 * @time 2017年4月14日 下午6:49:23
	 */
	private List<SysMenu> getChildren(int pid) {
		List<SysMenu> list = new ArrayList<SysMenu>();
		Map<Integer, SysMenu> map = getSessionAttr("menus");
		Collection<SysMenu> values = map.values();
		for (SysMenu sysMenu : values) {
			if (sysMenu.getInt("pid") == pid) {
				list.add(sysMenu);
			}
		}
		return list;
	}

	/**
	 * 滚到登录页面
	 */
	private void toLogin() {
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
			setAttr("msg", "用户名或者密码错误");
			toLogin();
			return;
		}

		if (!(user.getStr("password").equals(HashKit.sha1(loginPwd)))) {
			setAttr("msg", "用户名或者密码错误");
			keepPara(new String[] { "userName" });
			toLogin();
			return;
		}

		try {
			loginInit(this, user);
		} catch (Exception e) {
			setAttr("msg", e.getMessage());
			keepPara(new String[] { "userName" });
			toLogin();
			return;
		}
		setSessionAttr("user", user);
		redirect("/");
	}

	private void loginInit(IndexController indexController, SysUser user) {
		// TODO Auto-generated method stub
		int rid = user.getInt("role_id");
		// 根据rid去查看所具有的所有菜单
		String roleMenus = SysRole.dao.findById(rid).getStr("role_menus");
		if (roleMenus.endsWith(",")) {
			roleMenus = roleMenus.substring(0, roleMenus.length() - 1);
		}
		// 查询当前角色的有效菜单
		List<SysMenu> list = SysMenu.dao.find("select * from sys_menu where id in (" + roleMenus + ") and status='1'");
		Map<Integer, SysMenu> map = new HashMap<Integer, SysMenu>();

		List<SysMenu> firstMenu = new ArrayList<SysMenu>();
		for (SysMenu sysMenu : list) {
			map.put(sysMenu.getInt("id"), sysMenu);
			if (sysMenu.getInt("level") == 1) {
				// 一级菜单
				firstMenu.add(sysMenu);
			}
		}
		// 该用户的所有菜单
		setSessionAttr("menus", map);

		SysMenu sysMenus = new SysMenu();
		if (firstMenu.size() < 1) {
			try {
				throw new Exception("没有查看任何菜单的权限，请联系管理员。");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			for (SysMenu sysMenu : firstMenu) {
				SysMenu child = recursiveTree(sysMenu.getInt("id"));
				sysMenus.getChildren().add(child);
			}
		}

		sysMenus.set("name", "顶级菜单");
		System.out.println(sysMenus);

		setSessionAttr("menuTree", sysMenus);
	}

	/**
	 * 添加验证码
	 */
	public void captcha() {
		render(new CaptchaRender());
	}

	/**
	 * 退出登录
	 */
	public void doExit() {
		removeSessionAttr("user");
		removeSessionAttr("menuTree");
		redirect("/");
	}

	/**
	* @author:zhaozhongyuan 
	* @Description: 根据tabID获取按钮
	* @param 
	* @return void
	* @date 2017年4月17日 上午11:26:07  
	*/
	public void getBtnsBytabId() {
		int id = getParaToInt(0);
		List<SysMenu> btns = getChildren(id);
		if (btns.size() > 0) {
			setAttr("btns", btns);
		}
		render("index.html");

	}

	public void table() {
		String tableName = "sys_user";

		String queryField = "id,name,password,role_id";
		String[] columns_ = { "ID", "用户名", "密码", "角色ID" };

		List<Object> list = Db.query("SELECT " + queryField + " FROM " + tableName);

		setAttr("columns", columns_);
		setAttr("data", list);

		render("views/table.html");
	}


}