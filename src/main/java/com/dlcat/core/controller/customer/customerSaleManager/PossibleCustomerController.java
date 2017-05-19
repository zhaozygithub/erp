package com.dlcat.core.controller.customer.customerSaleManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dlcat.common.BaseController;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.FormBuilder;
import com.dlcat.common.entity.FormField;
import com.dlcat.common.entity.QueryItem;
import com.dlcat.common.entity.QueryWhere;
import com.dlcat.common.entity.Search;
import com.dlcat.common.entity.TableHeader;
import com.dlcat.common.utils.DateUtil;
import com.dlcat.common.utils.OptionUtil;
import com.dlcat.common.utils.PageUtil;
import com.dlcat.core.model.CuPossibleCustomer;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysUser;
import com.dlcat.core.model.ToCodeLibrary;
import com.jfinal.aop.Before;
import com.jfinal.kit.JMap;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinal.plugin.activerecord.tx.Tx;

public class PossibleCustomerController extends BaseController {
	public void index() {
		// 首先注意：此处的列表页面应该是在Tab下面的，所以加载Tab中的url，
		// Tab的url中需要将Tab的id拼接到url最后
		// 1.定义列表的表头和字段 注意：字段名称必须是数据库中的实际存在
		// 的字段名称或者虚拟字段名称，如cn_status
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[] { "id", "name", "phone", "cn_type", "cn_op_status", "time" });
		tableHeader.setCNNames(new String[] { "ID", "名称", "电话", "客户类型", "处理进度", "添加时间" });
		tableHeader.setMultiple(true);
		// 2.定义检索区域检索框 注意：字段名称必须是实际存在的字段名称.
		// CNName中文标示这个检索字段的含义，type标示检索框的类型
		Search search = new Search();
		search.setFieldNames(new String[] { "phone", "startTime", "endTime" });
		search.setCNNames(new String[] { "手机号", "开始时间", "结束时间" });
		search.setTypes(new String[] { "text", "date", "date" });
		// 3.定义下拉数据源 如果检索区域中存在select，必须定义下拉数据源
		/*
		 * String[] aStrings={"",""}; List<Object> list=new ArrayList<Object>();
		 * list.add(aStrings);
		 */

		// 注意：这里的资源必须和表头字段中的一致，可以定义多个
		// Map<String,List<ToCodeLibrary>> clListMap = new HashMap<String,
		// List<ToCodeLibrary>>();
		// clListMap.put("status",
		// ToCodeLibrary.getCodeLibrariesBySQL("YesNo",true,null));
		// search.setOptionListMap(clListMap);
		DyResponse response = null;

		try {
			// 4.构造页面结构并返回，注意：第一个参数标示请求列表数据时候的url，必须是存在的
			// 第二个参数是这个列表数据的名称，如果页面中存在这个导出功能，这个名称就是导出的
			// excel文件的文件名称
			response = PageUtil.createTablePageStructure("/possibleCustomer/data", "测试列表数据", tableHeader, search,
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
		// 别名一律为 cn_原名称

		Map<?, ?> whereMap = super.getWhereMap(conditation);

		// 定义查询对象OpCuPosStatus
		QueryItem item = new QueryItem();
		item.setTableNames("cu_possible_customer");
		item.setFields(
				"*, getCodeItemName('CustomerType',type) as cn_type,getCodeItemName('OpCuPosStatus',op_status) as cn_op_status,FROM_UNIXTIME(input_time) as time");
		SysUser sysUser = getSessionAttr("user");
		int org_id = sysUser.getInt("belong_org_id");
		int id = sysUser.getInt("id");

		List<QueryWhere> whereList = new ArrayList<QueryWhere>();
		whereList.add(new QueryWhere("belong_org_id", LIKE_RIGHT, org_id));
		whereList.add(new QueryWhere("belong_user_id", id));
		// 未处理
		whereList.add(new QueryWhere("op_status", "1"));

		// 注意：此处whereMap在页面初始化的时候为null，此处有必要判空
		if (whereMap != null) {
			whereList.add(new QueryWhere("phone", LIKE_RIGHT, whereMap.get("phone")));
			if (whereMap.get("startTime") != null) {
				whereList.add(new QueryWhere("FROM_UNIXTIME(input_time)", GE, whereMap.get("startTime")));
			}
			if (whereMap.get("endTime") != null) {
				whereList.add(new QueryWhere("FROM_UNIXTIME(input_time)", LE, whereMap.get("endTime")));
			}
		}
		item.setWhereList(whereList);
		// 4.获取数据 格式为List<Record>
		DyResponse dyResponse = super.getTableData(item, page);
		// 5.返回数据到页面 response 固定值 不可改变
		this.setAttr("response", dyResponse);
		renderJson();
	}

	public void tab2() {
		// 首先注意：此处的列表页面应该是在Tab下面的，所以加载Tab中的url，
		// Tab的url中需要将Tab的id拼接到url最后
		// 1.定义列表的表头和字段 注意：字段名称必须是数据库中的实际存在
		// 的字段名称或者虚拟字段名称，如cn_status
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[] { "id", "name", "phone", "cn_type", "cn_op_status", "time" });
		tableHeader.setCNNames(new String[] { "ID", "名称", "电话", "客户类型", "处理进度", "添加时间" });
		tableHeader.setMultiple(true);
		// 2.定义检索区域检索框 注意：字段名称必须是实际存在的字段名称.
		// CNName中文标示这个检索字段的含义，type标示检索框的类型
		Search search = new Search();
		search.setFieldNames(new String[] { "phone", "startTime", "endTime" });
		search.setCNNames(new String[] { "手机号", "开始时间", "结束时间" });
		search.setTypes(new String[] { "text", "date", "date" });
		// 3.定义下拉数据源 如果检索区域中存在select，必须定义下拉数据源
		/*
		 * String[] aStrings={"",""}; List<Object> list=new ArrayList<Object>();
		 * list.add(aStrings);
		 */

		// 注意：这里的资源必须和表头字段中的一致，可以定义多个
		/*
		 * Map<String,List<Map>> clListMap = new HashMap<String, List<Map>>();
		 * 
		 * clListMap.put("status",
		 * OptionUtil.getOptionListByCodeLibrary("YesNo",true,null));
		 * clListMap.put("remark", OptionUtil.getOptionListByOther("id", "name",
		 * "loan_business_category", null)); clListMap.put("status",
		 * ToCodeLibrary.getCodeLibrariesBySQL("YesNo",true,null));
		 * search.setOptionListMap(clListMap);
		 */

		DyResponse response = null;

		try {
			// 4.构造页面结构并返回，注意：第一个参数标示请求列表数据时候的url，必须是存在的
			// 第二个参数是这个列表数据的名称，如果页面中存在这个导出功能，这个名称就是导出的
			// excel文件的文件名称
			response = PageUtil.createTablePageStructure("/possibleCustomer/data2", "测试列表数据", tableHeader, search,
					super.getLastPara().toString(), (Map<Integer, SysMenu>) this.getSessionAttr("menus"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 5.返回数据到页面 response不可改变,页面名称也不可改变
		this.setAttr("response", response);
		this.render("common/table.html");
	}

	public void data2() {
		// 1.从页面获取参数 condition参数名称固定 key=condition 格式为：name:ms,age:24
		// 获取分页参数 固定格式 key=page
		String conditation = this.getPara("condition");
		String page = this.getPara("page");
		System.out.println("查询条件：" + conditation);
		// 2.列表数据sql 特别注意：如果需要如果需要转化成为字典值调用getCodeItemName()数据库函数，
		// 别名一律为 cn_原名称

		Map<?, ?> whereMap = super.getWhereMap(conditation);

		// 定义查询对象OpCuPosStatus
		QueryItem item = new QueryItem();
		item.setTableNames("cu_possible_customer");
		item.setFields(
				"*, getCodeItemName('CustomerType',type) as cn_type,getCodeItemName('OpCuPosStatus',op_status) as cn_op_status,FROM_UNIXTIME(input_time) as time");
		SysUser sysUser = getSessionAttr("user");
		int org_id =getCurrentUserBelongID();
		int id = sysUser.getInt("id");

		List<QueryWhere> whereList = new ArrayList<QueryWhere>();
		whereList.add(new QueryWhere("belong_org_id", LIKE_RIGHT, org_id));
		whereList.add(new QueryWhere("belong_user_id", id));
		//
		whereList.add(new QueryWhere("op_status",NEQ, "1"));

		// 注意：此处whereMap在页面初始化的时候为null，此处有必要判空
		if (whereMap != null) {
			whereList.add(new QueryWhere("phone", LIKE_RIGHT, whereMap.get("phone")));
			if (whereMap.get("startTime") != null) {
				whereList.add(new QueryWhere("FROM_UNIXTIME(input_time)", GE, whereMap.get("startTime")));
			}
			if (whereMap.get("endTime") != null) {
				whereList.add(new QueryWhere("FROM_UNIXTIME(input_time)", LE, whereMap.get("endTime")));
			}
		}
		item.setWhereList(whereList);
		// 4.获取数据 格式为List<Record>
		DyResponse dyResponse = super.getTableData(item, page);
		// 5.返回数据到页面 response 固定值 不可改变
		this.setAttr("response", dyResponse);
		renderJson();
	}

	/**
	 * @author:zhaozhongyuan
	 * @Description:响应未处理tab页，客户跟进按钮
	 * @return void
	 * @date 2017年5月13日 下午5:44:34
	 */
	@Before(Tx.class)
	public void btnCustomeFollowUp() {
		// 获取要更新的主键数组
		String[] ids = getPara("id").toString().split(",");

		// 更新op_status 值为2
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("op_status", 2);
		try {
			updateByIds(CuPossibleCustomer.class, ids, map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @author:zhaozhongyuan
	 * @return
	 * @Description:转为无效客户
	 * @return void
	 * @date 2017年5月15日 上午11:35:02
	 */
	@Before(Tx.class)
	public void btnCustomeToInvalid() {
		// 获取要更新的主键数组
		String[] ids = getPara("id").toString().split(",");

		// 更新op_status 值为4
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("op_status", 4);
		try {
			updateByIds(CuPossibleCustomer.class, ids, map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @author:zhaozhongyuan
	 * @Description:转为正式客户
	 * @return void
	 * @date 2017年5月15日 上午11:35:02
	 */
	@Before(Tx.class)
	public void btnCustomeToFormal() {
		// 获取要更新的主键数组
		String[] ids = getPara("id").toString().split(",");

		// 更新op_status 值为3
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("op_status", 3);
		try {
			// 更新op_status
			updateByIds(CuPossibleCustomer.class, ids, map);
			CuPossibleCustomer cuPossibleCustomer = null;
			Record record = null;
			// 并把这些客户加入正式客户表
			for (String id : ids) {
				cuPossibleCustomer = CuPossibleCustomer.dao.findById(id);
				record = cuPossibleCustomer.toRecord();
				record.set("cu_possible_id", record.get("id"));
				record.set("id", "fomal" + DateUtil.getCurrentTime());
				record.set("input_time", DateUtil.getCurrentTime());
				record.remove("op_status", "remark");

				try {
					Db.save("cu_object_customer", record);
					renderText("操作成功");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					renderText("操作失败");
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	* @author:zhaozhongyuan 
	* @Description:构建表单页面，增改查
	* @return void   
	* @date 2017年5月18日 上午11:19:57  
	*/
	public void form() {
		String type = getPara(0);
		String id=getPara("id");
		
		List<FormField> formFieldList = new ArrayList<FormField>();
		formFieldList.add(new FormField("id", "", "hidden"));
		formFieldList.add(new FormField("name", "客户姓名", "text"));
		formFieldList.add(new FormField("phone", "电话", "text"));
		formFieldList.add(new FormField("type", "客户类型", "select", "",
				OptionUtil.getOptionListByCodeLibrary("CustomerType", true, "")));

		DyResponse response = null;
		if (type.equals("add")) {
			response = PageUtil.createFormPageStructure("意向客户添加", formFieldList, "/possibleCustomer/toAdd");
		} else if (type.equals("edit")) {
			if (id.equals("")) {
				renderText("请选择一条记录来编辑！");
				return;
			}
			response = PageUtil.createFormPageStructure("意向客户编辑", formFieldList, "/possibleCustomer/toEdit");
		}else if (type.equals("detail")) {
			if (id.equals("")) {
				renderText("请选择一条记录来查看！");
				return;
			}
			//第三个参数 由于校验非空，所以要随便写点什么即可
			response = PageUtil.createFormPageStructure("查看详细信息", formFieldList, "/s");
		}
		this.setAttr("response", response);
		this.render("common/form_editarea.html");
	}

	public void toAdd() {
		SysUser sysUser = getSessionAttr("user");
		CuPossibleCustomer cuPossibleCustomer = getModel(CuPossibleCustomer.class, "");
		cuPossibleCustomer.set("id", "dct" + DateUtil.getCurrentTime());
		cuPossibleCustomer.set("belong_org_id", getCurrentUserBelongID());
		cuPossibleCustomer.set("belong_user_name", sysUser.getStr("name"));
		cuPossibleCustomer.set("input_time", DateUtil.getCurrentTime());
		cuPossibleCustomer.set("belong_user_id", sysUser.getInt("id"));

		try {
			cuPossibleCustomer.save();
			renderHtml("<h1>操作成功！！</h1>");
		} catch (Exception e) {
			renderHtml("<h1>操作失败！！</h1>");
		}
	}

	/**
	 * @author:zhaozhongyuan
	 * @Description:批量操作都使用事务
	 * @return void
	 * @date 2017年5月16日 下午2:24:02
	 */
	@Before(Tx.class)
	public void del() {
		String[] ids = getPara("id").toString().split(",");
		
		if (ids[0].equals("")) {
			renderText("请选择至少一条记录！！！");
			return;
		}
		
		// 批量删除
		CuPossibleCustomer cuPossibleCustomer = new CuPossibleCustomer();

		try {
			for (String id : ids) {
				cuPossibleCustomer.deleteById(id);
			}
			renderText("操作成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderText("操作失败");
		}
	}

	/**
	 * @author:zhaozhongyuan
	 * @Description:响应编辑事件更新字段值
	 * @return void
	 * @date 2017年5月16日 下午2:36:39
	 */
	public void toEdit() {
		CuPossibleCustomer cuPossibleCustomer = getModel(CuPossibleCustomer.class, "");
		try {
			cuPossibleCustomer.update();
			renderText("操作成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderText("操作成功");
		}
	}

}
