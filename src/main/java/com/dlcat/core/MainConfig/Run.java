package com.dlcat.core.MainConfig;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.wall.WallFilter;
import com.dlcat.core.Interceptor.LoginInterceptor;
import com.dlcat.core.controller.CrudController;
import com.dlcat.core.controller.IndexController;
import com.dlcat.core.controller.TestController;
import com.dlcat.core.model._MappingKit;
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
		//所有的html文件都放在web-inf下
		me.setBaseViewPath("/WEB-INF");
		
		me.add("/", IndexController.class);
		//测试路由
		me.add("/test", TestController.class,"/views");
		//测试路由
		me.add("/form", CrudController.class,"/views");
	}

	@Override
	public void configConstant(Constants me) {
		me.setDevMode(true);
		PropKit.use("db.config");
		

	}

	@Override
	public void configEngine(Engine me) {
		//js.css压缩插件
		me.addDirective("assets",new AssetsDirective());
		me.addSharedFunction("/WEB-INF/Template/core.html");
	}

	@Override
	public void configHandler(Handlers me) {
		//开启druid sql监控
		DruidStatViewHandler dvh = new DruidStatViewHandler("/druid");
		me.add(dvh);

	}

	@Override
	public void configInterceptor(Interceptors me) {
		//开启session
		me.addGlobalActionInterceptor(new SessionInViewInterceptor());
		//登录验证
		//me.addGlobalActionInterceptor(new LoginInterceptor());

	}

	@Override
	public void configPlugin(Plugins me) {
		ActiveRecordPlugin arp = addDataSource(me, "dlcat_erp", JdbcUtils.MYSQL);

	}

	private ActiveRecordPlugin addDataSource(Plugins plugins, String datasource, String dbType) {
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

		_MappingKit.mapping(arp);

		plugins.add(dp).add(arp);

		return arp;
	}

}
