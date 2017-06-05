package com.dlcat.core.controller.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.kisso.SSOHelper;
import com.baomidou.kisso.SSOToken;
import com.baomidou.kisso.common.IpHelper;
import com.baomidou.kisso.common.util.HttpUtil;
import com.dlcat.common.BaseController;
import com.dlcat.common.utils.DateUtil;
import com.dlcat.common.utils.IpUtil;
import com.dlcat.core.model.SysLoginLog;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysRole;
import com.dlcat.core.model.SysUser;
import com.jfinal.kit.HashKit;
import com.jfinal.plugin.activerecord.Db;

/** 
* @author zhaozhongyuan
* @date 2017年5月27日 上午9:14:04 
* @Description: 添加单点登录支持
*/
public class LoginController extends BaseController {

		public void index() {
			SSOToken mt = SSOHelper.getToken(getRequest());
			if (mt != null) {
				 redirect("/");
				return;
			}

			/**
			 * 登录 生产环境需要过滤sql注入
			 */
			if (HttpUtil.isPost(getRequest())) {
				// 验证登录
				if (!doLogin()) {
					return;
				}

				SysUser user = (SysUser) getSessionAttr("user");
				SysMenu sysMenus=getSessionAttr("menuTree");
				Map<Integer, SysMenu> menus=getSessionAttr("menus");
				System.out.println(user);
				if (user != null) {
					mt = new SSOToken();
					mt.setId(1354L);
					mt.setUid("1888");
					mt.setIp(IpHelper.getIpAddr(getRequest()));

					// 记住密码，设置 cookie 时长 1 周 = 604800 秒 【动态设置 maxAge 实现记住密码功能】
					// String rememberMe = req.getParameter("rememberMe");
					// if ( "on".equals(rememberMe) ) {
					// request.setAttribute(SSOConfig.SSO_COOKIE_MAXAGE, 604800);
					// }
					
					
					SSOHelper.setSSOCookie(getRequest(), getResponse(), mt, true);
					setSessionAttr("user", user);
					setSessionAttr("menuTree", sysMenus);
					setSessionAttr("menus", menus);
					
					redirect("/");
	                return;
				}
			}
			toLogin();
		}

		/**
		 * 登录验证
		 */
		public boolean doLogin() {

			String loginId = getPara("userName");
			String loginPwd = getPara("pwd");

			/*boolean istrue = validateCaptcha("captcha");

			if (istrue == false) {
				keepPara(new String[] { "userName", "pwd" });
				setAttr("msg", "验证码错误，请重新输入！");
				toLogin();
				return;
			}*/

			SysUser user = SysUser.dao.findFirst("select * from sys_user where name = ?", loginId);
			if (user == null) {
				setAttr("msg", "用户名或者密码错误");
				toLogin();
				return false;
			}

			if (!user.getStr("status").equals("1")) {
				setAttr("msg", "该用户不可用。");
				try {
					throw new Exception("该用户不可用。");
				} catch (Exception e) {
					e.printStackTrace();
				}
				toLogin();
				return false;
			}

			if (!(user.getStr("password").equals(HashKit.sha1(loginPwd)))) {
				setAttr("msg", "用户名或者密码错误");
				keepPara(new String[] { "userName" });
				toLogin();
				return false;
			}

			try {
				loginInit(this, user);
			} catch (Exception e) {
				setAttr("msg", e.getMessage());
				keepPara(new String[] { "userName" });
				toLogin();
				return false;
			}
			// 验证通过后，登录次数+1 ,并更新登录ip和登录时间
			String ip = "";
			try {
				ip = IpUtil.getRemoteIp(this.getRequest());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("获取用户IP失败。");
			}

			Db.update("update sys_user set login_count = login_count+1 ,last_login_time = ? ,last_login_ip = ? where id =?",
					DateUtil.getCurrentTime(), ip, user.getInt("id"));
			// 登录日志
			SysLoginLog log = new SysLoginLog();
			log.set("login_id", user.getInt("id"));
			log.set("login_name", loginId);
			log.set("belong_org_id", user.getInt("belong_org_id"));
			log.set("belong_org_name", user.getStr("belong_org_name"));
			log.set("login_count", user.getInt("login_count"));
			log.set("login_time", DateUtil.getCurrentTime());
			log.set("ip", ip);
			log.save();

			SysUser user2 = SysUser.dao.findById(user.getInt("id"));
			setSessionAttr("user", user2);
			return true;
		}

		private void loginInit(LoginController loginController, SysUser user) throws Exception {
			// TODO Auto-generated method stub
			int rid = user.getInt("role_id");

			// 获得角色信息
			SysRole sysRole = SysRole.dao.findById(rid);

			// 先判断此角色是否可用
			if (!sysRole.getStr("status").equals("1")) {
				setAttr("msg", "该角色不可用。");
				throw new Exception("该角色不可用。");
			}

			// 根据rid去查看所具有的所有菜单
			String roleMenus = sysRole.getStr("role_menus");

			List<SysMenu> list = null;
			// 如果为管理员那么就查找全部有效菜单
			if (roleMenus.equals("*")) {
				list = SysMenu.dao.find("select * from sys_menu where status='1' ORDER BY pid,sort_no,id");
			} else {
				if (roleMenus.endsWith(",")) {
					roleMenus = roleMenus.substring(0, roleMenus.length() - 1);
				}
				// 查询当前角色的有效菜单
				list = SysMenu.dao.find(
						"select * from sys_menu where id in (" + roleMenus + ") and status='1' ORDER BY pid,sort_no,id");

			}
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
				throw new Exception("没有查看任何菜单的权限，请联系管理员。");
			} else {
				for (SysMenu sysMenu : firstMenu) {
					SysMenu child = recursiveTree(sysMenu.getInt("id"));
					sysMenus.getChildren().add(child);
				}
			}
			sysMenus.set("name", "顶级菜单");
			setSessionAttr("menuTree", sysMenus);
		}

		/**
		 * 到登录页面
		 */
		private void toLogin() {
			render("/WEB-INF/login.html");
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
			Comparator<SysMenu> comparator = new Comparator<SysMenu>() {

				public int compare(SysMenu s1, SysMenu s2) {
					// TODO Auto-generated method stub
					return s1.getInt("sort_no") - s2.getInt("sort_no");
				}
			};
			Collections.sort(list, comparator);

			return list;
		}

		/**
		 * @author:zhaozhongyuan
		 * @Description: 根据二级或者三级菜单ID获取tab页
		 * @param
		 * @return void
		 * @date 2017年4月17日 上午11:26:07
		 */
		public void getTabsById() {
			int id = getParaToInt(0);
			List<SysMenu> tabs = getChildren(id);
			if (tabs.size() > 0) {
				renderJson(tabs);
			} else {
				renderNull();
			}

		}
}
