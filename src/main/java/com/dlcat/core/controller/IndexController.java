package com.dlcat.core.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.util.ajax.JSON;

import com.dlcat.core.model.CuObjectCustomer;
import com.dlcat.core.model.LoanApplyApprove;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysOrg;
import com.dlcat.core.model.SysRole;
import com.dlcat.core.model.SysUser;
import com.jfinal.captcha.CaptchaRender;
import com.jfinal.core.Controller;
import com.jfinal.json.FastJson;
import com.jfinal.json.Json;
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
		SysUser user=getSessionAttr("user");
		initEchars(user);
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

	/**
	* @author:zhaozhongyuan 
	* @Description: 测试表格模板
	* @param 
	* @return void
	* @date 2017年4月21日 下午8:02:48  
	*/
	public void table() {
		String tableName = "sys_user";

		String queryField = "id,name,password,role_id";
		String[] columns_ = { "ID", "用户名", "密码", "角色ID" };

		List<Object> list = Db.query("SELECT " + queryField + " FROM " + tableName);

		setAttr("columns", columns_);
		setAttr("data", list);

		render("views/table.html");
	}
	
	/**
	* @author:zhaozhongyuan 
	* @Description: 初始化echars，展示页面统计信息 
	* @param 
	* @return void
	* @date 2017年4月21日 下午8:03:02  
	*/
	private void initEchars(SysUser user){
		//圆饼图
		initPie(user);
		//下面的三个条形图
		initBar(user);
		//门店
		initBarMd(user);
		//违约
		initBarWy(user);
		
	}
	/**
	* @author:zhaozhongyuan 
	* @Description:门店表格
	* @return void   
	* @date 2017年4月26日 下午8:12:11  
	*/
	private void initBarMd(SysUser user) {
		
	}

	/**
	* @author:zhaozhongyuan 
	* @Description:初始化违约表格
	* @return void   
	* @date 2017年4月26日 下午8:12:23  
	*/
	private void initBarWy(SysUser user) {
		String defaultYear="";
		//获取年份
		String[] years=LoanApplyApprove.getYear_mouth(user);
		//默认为当年
		if (years.length>0) {
			defaultYear= years[0];
			setAttr("years_month", years);
		}
		
		//获取页面需要的数据
	if (!defaultYear.equals("")) {	
		Map<String, Number> map=LoanApplyApprove.getMdNum(defaultYear,user);
		setAttr("bar", map);
	}
	
	}
	
	/**
	 * @author:zhaozhongyuan 
	 * @Description: 年度借款数据
	 * @param @param user
	 * @return void
	 * @date 2017年4月26日 上午9:52:42  
	 */
	public void initBar(SysUser user) {
		String defaultYear="";
			//获取年份
			String[] years=LoanApplyApprove.getyear(user);
			//默认为当年
			if (years.length>0) {
				defaultYear= years[0];
				setAttr("years", years);
			}
		if (!defaultYear.equals("")) {	
			Map<String, List<Number>> map=LoanApplyApprove.getMonthNum(defaultYear,user);
			setAttr("bar", map);
		}
		
	}
	
	public void initBarByYear() {
		String currentYear=getPara("year");
		SysUser user=getSessionAttr("user");
		Map<String, List<Number>> map=LoanApplyApprove.getMonthNum(currentYear,user);
		
		renderHtml("{\"a\":\""+map.get("成交笔数").toString()+"\",\"b\":\""+map.get("借款总额").toString()+"\"}");

	}
	

	private void initPie(SysUser user){
		//客户统计 :只统计本节点和以下机构
		int org_id=user.getInt("belong_org_id");
		int level=SysOrg.getLevel(org_id);
		//int level=3;
		List<CuObjectCustomer> list=null;
		//1为总部
		if (level==1) {
			list=CuObjectCustomer.getAll();
		//2分部
		}else if (level==2) {
		//获取分部以及支部
			list=CuObjectCustomer.getCuObjectCustomers(SysOrg.getDeptAndChildren(org_id));
		//3支部
		}else if (level==3) {
			list=SysOrg.getBelongCustomer(org_id);
		}
		
		int black=0,individual=0,company=0;
		
		for (CuObjectCustomer cuObjectCustomer : list) {
			String status=cuObjectCustomer.getStr("status");
			//3为黑名单
			if (status.equals("3")) {
				black++;
				
			//1为正常
			}else if (status.equals("1")) {
				String type=cuObjectCustomer.getStr("type");
				//01为公司客户
				if (type.equals("01")) {
					company++;
					
				//02为个人客户
				}else if (type.equals("02")) {
					individual++;
				}
				
				
			}
			
		}
		//填充圆饼图数据
		Map<String, Integer> pie=new HashMap<String, Integer>();
		pie.put("黑名单", black);
		pie.put("个人客户", individual);
		pie.put("对公客户", company);
		
		setAttr("pie", pie);
	}


}
