package com.dlcat.core.MainConfig;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.wall.WallFilter;
import com.dlcat.common.CommonController;
import com.dlcat.core.controller.FlowController;
import com.dlcat.core.controller.TestController;
import com.dlcat.core.controller.TestUserController;
import com.dlcat.core.controller.collateral.CollateralController;
import com.dlcat.core.controller.customer.customerRecord.BlackListController;
import com.dlcat.core.controller.customer.customerRecord.CustomerRecordController;
import com.dlcat.core.controller.customer.customerRecord.MyCustomerController;
import com.dlcat.core.controller.customer.customerRecord.ObjectCustomerAllotController;
import com.dlcat.core.controller.customer.customerSaleManager.CustomerAllotControllor;
import com.dlcat.core.controller.customer.customerSaleManager.PossibleCustomerController;
import com.dlcat.core.controller.index.IndexController;
import com.dlcat.core.controller.index.LoginController;
import com.dlcat.core.controller.index.PersonalInfoSetController;
import com.dlcat.core.controller.loan.LoanApplyController;
import com.dlcat.core.controller.loan.LoanFlowApproveController;
import com.dlcat.core.controller.loan.LoanShowController;
import com.dlcat.core.controller.system.DataController;
import com.dlcat.core.controller.system.RoleManageController;
import com.dlcat.core.controller.system.SystemManageController;
import com.dlcat.core.controller.system.UserManageController;
import com.dlcat.core.model._MappingKit;
import com.dlcat.core.plugins.KissoJfinalPlugin;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.template.Engine;

import net.dreamlu.ui.jfinal.AssetsDirective;

public class Run extends JFinalConfig {

	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 80, "/", 5);
	}

	@Override
	public void configRoute(Routes me) {
		// 所有的html文件都放在WEB-INF下
		me.setBaseViewPath("/WEB-INF/views");
		//通用路由
		me.add("/common",CommonController.class);
		
		// 控制台模块
		// 登录，首页
		me.add("/", IndexController.class);
		me.add("/login", LoginController.class);

		// 个人信息设置
		me.add("/set", PersonalInfoSetController.class, "/console");

		// 客户管理模块
		// 客户营销管理
		// 意向客户分配
		me.add("/customerAllot", CustomerAllotControllor.class, "/");
		// 我的意向客户
		me.add("/possibleCustomer", PossibleCustomerController.class, "/");

		// 客户档案管理
		// 我的客户
		me.add("/myCustomer", MyCustomerController.class,"/");
		// 正式客户分配
		me.add("/distribution", ObjectCustomerAllotController.class, "/");
		// 档案管理
		me.add("/record", CustomerRecordController.class, "/");
		// 黑名单
		me.add("/blackList", BlackListController.class, "/");

		// 押品管理模块
		me.add("/collateral", CollateralController.class, "/");

		// 系统管理模块
		me.add("/systemManage", SystemManageController.class, "/");
		me.add("/role", RoleManageController.class, "/");
		me.add("/userManage", UserManageController.class, "/");

		//业务管理模块
		me.add("/loanShow", LoanShowController.class, "/");//借款展示
		me.add("/loanApply", LoanApplyController.class, "/");//借款申请
		me.add("/loanFlowApprove", LoanFlowApproveController.class, "/");//借款流程审批
		
		// 测试路由
		me.add("/test", TestController.class, "/");
		me.add("/flow", FlowController.class, "/views");
		me.add("/user", TestUserController.class, "/views");
		me.add("/data", DataController.class);
		
	}

	@Override
	public void configConstant(Constants me) {
		me.setDevMode(true);
		PropKit.use("db.config");
		me.setBaseUploadPath(PropKit.get("img_path"));
		// me.setBaseDownloadPath(PropKit.get("baseDownloadPath"));

	}

	@Override
	public void configEngine(Engine me) {
		// js.css压缩插件
		me.addDirective("assets", new AssetsDirective());
		me.addSharedFunction("/WEB-INF/Template/core.html");
	}

	@Override
	public void configHandler(Handlers me) {
		// 开启druid sql监控
		DruidStatViewHandler dvh = new DruidStatViewHandler("/druid");
		me.add(dvh);

	}

	@Override
	public void configInterceptor(Interceptors me) {
		// 开启session
		me.addGlobalActionInterceptor(new SessionInViewInterceptor());
		// 登录验证
		// me.addGlobalActionInterceptor(new LoginInterceptor());
		
		//拦截带此URL的添加log日志
		//me.addGlobalActionInterceptor(new SysLogInterceptor("/toEdit","/possibleCustomer/toAdd","/del"));
		

	}

	@Override
	public void configPlugin(Plugins me) {
		ActiveRecordPlugin arp = addDataSource(me, "dlcat_erp", JdbcUtils.MYSQL);
		_MappingKit.mapping(arp);
		
		//kisso 初始化
		me.add(new KissoJfinalPlugin());

	}

	private ActiveRecordPlugin addDataSource(Plugins plugins,
			String datasource, String dbType) {
		// 添加数据源
		String url, user, pwd;
		url = PropKit.get(datasource + "_url");
		user = PropKit.get(datasource + "_user");
		pwd = PropKit.get(datasource + "_pwd");

		WallFilter wall = new WallFilter();
		wall.setDbType(dbType);

		DruidPlugin dp = new DruidPlugin(url, user, pwd);

		dp.addFilter(new StatFilter());
		dp.addFilter(wall);

		ActiveRecordPlugin arp = new ActiveRecordPlugin(datasource, dp);
		// 方言
		arp.setDialect(new MysqlDialect());
		// 事务级别
		arp.setTransactionLevel(4);
		// 统一全部默认小写
		arp.setContainerFactory(new CaseInsensitiveContainerFactory(true));
		// 是否显示SQL
		arp.setShowSql(true);
		System.out.println("load data source:" + url + "/" + user);

		plugins.add(dp).add(arp);

		return arp;
	}

}
