package com.dlcat.core.controller.customer.customerRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.QueryItem;
import com.dlcat.common.entity.QueryWhere;
import com.dlcat.common.entity.Search;
import com.dlcat.common.entity.TableHeader;
import com.dlcat.common.utils.DateUtil;
import com.dlcat.common.utils.OptionUtil;
import com.dlcat.common.utils.PageUtil;
import com.dlcat.core.model.CuObjectCustomer;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysUser;
import com.dlcat.core.model.ToCodeLibrary;
import com.jfinal.aop.Before;
import com.jfinal.kit.JMap;
import com.jfinal.plugin.activerecord.tx.Tx;

public class BlackListController extends BaseController {
	public void index() {
		// 首先注意：此处的列表页面应该是在Tab下面的，所以加载Tab中的url，
		// Tab的url中需要将Tab的id拼接到url最后
		// 1.定义列表的表头和字段 注意：字段名称必须是数据库中的实际存在
		// 的字段名称或者虚拟字段名称，如cn_status
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[] { "id", "name", "phone","cn_type","cn_card_type","card_id",
				"belong_user_id", "belong_user_name", "belong_org_id",
				"belong_org_name"});
		tableHeader.setCNNames(new String[] { "客户编号", "名称", "电话号码",  "客户类型","证件类型","证件编号","管户人编号",
				"管户人名称", "管户机构编号", "管户机构名称"});
		//多选框
		tableHeader.setMultiple(true);
		// 2.定义检索区域检索框 注意：字段名称必须是实际存在的字段名称，
		// CNName中文标示这个检索字段的含义，type标示检索框的类型
		Search search = new Search();
		search.setFieldNames(new String[] { "id", "name","type" ,"card_type","card_id"});
		search.setCNNames(new String[] { "客户编号", "名称" ,"客户类型","证件类型","证件编号"});
		search.setTypes(new String[] { "text", "text" ,"select","select","text"});
		// 3.定义下拉数据源 如果检索区域中存在select，必须定义下拉数据源
		// 注意：这里的资源必须和表头字段中的一致，可以定义多个
		Map<String, List<Map>> clListMap = new HashMap<String, List<Map>>();
		clListMap.put("type", OptionUtil.getOptionListByCodeLibrary("CustomerType", true, null));
		clListMap.put("card_type", OptionUtil.getOptionListByCodeLibrary(
				"CertType", true, null));
		search.setOptionListMap(clListMap);
		DyResponse response = null;

		try {
			// 4.构造页面结构并返回，注意：第一个参数标示请求列表数据时候的url，必须是存在的
			// 第二个参数是这个列表数据的名称，如果页面中存在这个导出功能，这个名称就是导出的
			// excel文件的文件名称
			response = PageUtil.createTablePageStructure("/blackList/data", "测试列表数据", tableHeader, search,
					super.getLastPara().toString(), (Map<Integer, SysMenu>) this.getSessionAttr("menus"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 5.返回数据到页面 response不可改变,页面名称也不可改变
		this.setAttr("response", response);
		this.render("common/table.html");
	}

	public void data() {
		// 从页面获取参数 condition参数名称固定 key=condition 格式为：name:ms,age:24
		// 获取分页参数 固定格式 key=page
		String conditation = this.getPara("condition");
		String page = this.getPara("page");
		System.out.println("查询条件：" + conditation);
		// 获取参数Map，处理字符串拼接
		Map whereMap = super.getWhereMap(conditation);
		// 定义查询对象
		QueryItem item = new QueryItem();
		// 设置表名（多表用逗号间隔）
		item.setTableNames("cu_object_customer");
		// 设置查询字段（可以使用函数包括聚合函数）
		item.setFields("*, " + "getCodeItemName('CustomerType',type) as cn_type,"
				+ "getCodeItemName('CertType',card_type) as cn_card_type");
		// 设置分页步长 如果不设置 或者 值<=0，则会默认歩长为20
//		item.setLimit(10);
		// 注意：此处whereMap在页面初始化的时候为null，此处有必要判空
		List<QueryWhere> whereList = new ArrayList<QueryWhere>();
		// 检索条件
		if (whereMap != null) {
			whereList.add(new QueryWhere("id", LIKE_ALL,whereMap.get("id")));
			whereList.add(new QueryWhere("name",LIKE_ALL, whereMap.get("name")));
			whereList.add(new QueryWhere("type", whereMap.get("type")));
			whereList.add(new QueryWhere("card_type", whereMap.get("card_type")));
			whereList.add(new QueryWhere("card_id",LIKE_ALL, whereMap.get("card_id")));
		}
		// 默认追加条件
		// whereList.add(new QueryWhere("2","2")); //注意：jfinal仅仅支持一个常量条件，超过一个会报错
		whereList.add(new QueryWhere("status", "2"));
		whereList.add(new QueryWhere("belong_org_id", LIKE_RIGHT, getCurrentUserBelongID()));
		item.setOrder("update_time desc");
		item.setWhereList(whereList);
		// 获取数据 格式为List<Record>
		DyResponse dyResponse = super.getTableData(item, page);
		// 返回数据到页面 response 固定值 不可改变
		this.setAttr("response", dyResponse);

		renderJson(dyResponse);
	}

	/**
	 * @author:zhaozhongyuan
	 * @Description:移除黑名单
	 * @return void
	 * @date 2017年5月15日 下午2:52:07
	 */
	@Before(Tx.class)
	public void btnRemoveBlackList() {
		String id=getPara("id");
		if (id==null || id.equals("")) {
			renderText("请选择至少一条记录！！！");
			return;
		}
		
		// 获取要更新的主键数组
		String[] ids = id.split(",");
		SysUser user = getSessionAttr("user");
		// 更新status 值为"1"
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "1");
		map.put("update_time", DateUtil.getCurrentTime());
		map.put("update_user_id", user.getInt("id"));
		map.put("update_user_name", user.getStr("name"));
		map.put("update_org_id", user.getInt("belong_org_id"));
		map.put("update_org_name", user.getStr("belong_org_name"));
		
		try {
			updateByIds(CuObjectCustomer.class, ids, map);
			renderText("操作成功！！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderText("操作失败！！");
		}
	}

}
