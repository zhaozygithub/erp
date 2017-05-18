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
		tableHeader.setFieldNames(new String[] { "id", "name", "phone", "cn_type", "belong_user_id", "belong_user_name",
				"belong_org_id", "belong_org_name", "cn_cu_status_remark" });
		tableHeader.setCNNames(
				new String[] { "id", "名称", "电话号码", "客户类型", "管户人编号", "管户人名称", "管户机构编号", "管户机构名称", "客户状态备注" });
		//多选框
		tableHeader.setMultiple(true);
		// 2.定义检索区域检索框 注意：字段名称必须是实际存在的字段名称，
		// CNName中文标示这个检索字段的含义，type标示检索框的类型
		Search search = new Search();
		search.setFieldNames(new String[] { "id", "name", "type" });
		search.setCNNames(new String[] { "客户编号", "名称", "客户类型" });
		search.setTypes(new String[] { "text", "text", "select" });
		// 3.定义下拉数据源 如果检索区域中存在select，必须定义下拉数据源
		// 注意：这里的资源必须和表头字段中的一致，可以定义多个
		Map<String, List<Map>> clListMap = new HashMap<String, List<Map>>();
		clListMap.put("type", OptionUtil.getOptionListByCodeLibrary("CustomerType", true, null));
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
		// 1.从页面获取参数 condition参数名称固定 key=condition 格式为：name:ms,age:24
		// 获取分页参数 固定格式 key=page
		String conditation = this.getPara("condition");
		String page = this.getPara("page");
		System.out.println("查询条件：" + conditation);
		// 2.列表数据sql 特别注意：如果需要如果需要转化成为字典值调用getCodeItemName()数据库函数，
		// 字典值别名一律为 cn_原名称
		/*
		 * StringBuffer sql = new StringBuffer(
		 * "select *, getCodeItemName('YesNo',status) " +
		 * "as cn_status from loan_product_category where 1=1 ");
		 */
		// 3.获取参数Map，处理字符串拼接
		Map whereMap = super.getWhereMap(conditation);
		// 定义查询对象
		QueryItem item = new QueryItem();
		// 设置表名（多表用逗号间隔）
		item.setTableNames("cu_object_customer");
		// 设置查询字段（可以使用函数包括聚合函数）
		item.setFields("*, " + "getCodeItemName('CustomerType',type) as cn_type,"
				+ "getCodeItemName('CuStatusRemark',cu_status_remark) as cn_cu_status_remark");
		// 设置分页步长 如果不设置 或者 值<=0，则会默认歩长为20
//		item.setLimit(10);
		// 注意：此处whereMap在页面初始化的时候为null，此处有必要判空
		List<QueryWhere> whereList = new ArrayList<QueryWhere>();
		// 检索条件
		if (whereMap != null) {
			whereList.add(new QueryWhere("id", whereMap.get("id")));
			whereList.add(new QueryWhere("name", whereMap.get("name")));
			whereList.add(new QueryWhere("type", whereMap.get("type")));
		}
		// 默认追加条件
		// whereList.add(new QueryWhere("2","2")); //注意：jfinal仅仅支持一个常量条件，超过一个会报错
		whereList.add(new QueryWhere("status", "2"));
		whereList.add(new QueryWhere("belong_org_id", LIKE_RIGHT, getCurrentUserBelongID()));
		item.setWhereList(whereList);
		// 4.获取数据 格式为List<Record>
		DyResponse dyResponse = super.getTableData(item, page);
		// 5.返回数据到页面 response 固定值 不可改变
		this.setAttr("response", dyResponse);

		renderJson();
	}

	/**
	 * @author:zhaozhongyuan
	 * @Description:移除黑名单
	 * @return void
	 * @date 2017年5月15日 下午2:52:07
	 */
	@Before(Tx.class)
	public void btnRemoveBlackList() {
		// 获取要更新的主键数组
		String[] ids = getPara("id").toString().split(",");

		// 更新status 值为"1"
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "1");
		try {
			updateByIds(CuObjectCustomer.class, ids, map);
			renderHtml("<h1>操作成功！！</h1>");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderHtml("<h1>操作失败！！</h1>");
		}
	}

}
