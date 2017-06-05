package com.dlcat.core.controller.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.kisso.SSOHelper;
import com.dlcat.core.Interceptor.SSOJfinalInterceptor;
import com.dlcat.core.model.SysOrg;
import com.dlcat.core.model.SysRole;
import com.dlcat.core.model.SysUser;
import com.dlcat.service.echarts.IndexEchartsService;
import com.dlcat.service.echarts.impl.IndexEchartsImpl;
import com.jfinal.aop.Before;
import com.jfinal.captcha.CaptchaRender;
import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;

/** 
* @author zhaozhongyuan
* @date 2017年4月17日 上午11:21:15 
* @Description: TODO  
*/
public class IndexController extends Controller {

	private IndexEchartsService indexEchartsService=new IndexEchartsImpl();

	/**
	 * 默认方法
	 */
	@Before(SSOJfinalInterceptor.class)
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
		int userId = user.getInt("id");
		Map<String, Long> messageMap = messageCount(userId);
		setAttr("message", messageMap);
		setAttr("orgName", SysOrg.getNameByUserId(userId));
		setAttr("roleName", SysRole.getRoleNameByUserId(userId));
		
		render("/WEB-INF/index.html");
	}
	
	
	public void echarts() {
		SysUser user=getSessionAttr("user");
		initEchars(user);
		flownodes();
		
		render("console/echarts.html");
	}
	/**
	 * 主页流程信息
	 * @author liuran
	 * @time 2017年5月4日 下午12:57:16 void
	 */
	public void flownodes() {
		List<Map<String, Object>> list = indexEchartsService.flownodes();
		setAttr("flowlist", list);
	}
	


	/**
	 * 到登录页面
	 */
	private void toLogin() {
		render("/WEB-INF/login.html");
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
		//单用户登录，退出的时候删除cookies
		SSOHelper.clearLogin(getRequest(), getResponse());
		//删除session
		removeSessionAttr("user");
		removeSessionAttr("menuTree");
		redirect("/");
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
		String defaultYear="";
		//获取年份
		String[] years=indexEchartsService.getYear_mouth(user);
		//默认为当年
		if (years.length>0) {
			defaultYear= years[0];
			setAttr("years_month", years);
		}
		
		//获取页面需要的数据
	if (!defaultYear.equals("")) {
		Map<String, Number> map=indexEchartsService.getMdNum(defaultYear, user);
		setAttr("barMd", map);
	}
	}

	/**
	* @author:zhaozhongyuan 
	* @Description:初始化违约表格
	* @return void   
	* @date 2017年4月26日 下午8:12:23
	*/
	private void initBarWy(SysUser user) {
		
	
	}
	
	public void getDataByYearMonth() {
		String currentYear=getPara("year");
		SysUser user=getSessionAttr("user");
		Map<String, Number> map=indexEchartsService.getMdNum(currentYear, user);
		renderHtml(JsonKit.toJson(map));
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
			String[] years=indexEchartsService.getyear(user);
			//默认为当年
			if (years.length>0) {
				defaultYear= years[0];
				setAttr("years", years);
			}
		if (!defaultYear.equals("")) {	
			Map<String, List<Number>> map=indexEchartsService.getMonthNum(defaultYear, user);
			setAttr("bar", map);
		}
	}
	
	public void initBarByYear() {
		String currentYear=getPara("year");
		SysUser user=getSessionAttr("user");
		Map<String, List<Number>> map=indexEchartsService.getMonthNum(currentYear, user);
		renderHtml(JsonKit.toJson(map));
	}
	

	private void initPie(SysUser user){
		Map<String, Integer> pie=indexEchartsService.getPieData(user);
		if (pie!=null) {
			setAttr("pie", pie);
		}
	}
	
	/**
	 * 首页消息统计
	 * @author liuran
	 * @time 2017年5月15日 下午5:47:50
	 * @param userId
	 * @return Map<String,Integer>
	 */
	private Map<String, Long> messageCount(int userId) {		
		//流程统计 
		long flowncount = Db.queryLong("select count(*) as count from flow_task where cur_approve_user_id = ? and is_approve = '2'", userId);		
			
		Map<String, Long> messageMap = new HashMap<String, Long>();
		//此处增加消息处理类型
		messageMap.put("flowcount", flowncount);	
		return messageMap;
	}

}
