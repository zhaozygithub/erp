package com.dlcat.core.controller.customer.customerRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.FormField;
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
import com.jfinal.plugin.activerecord.tx.Tx;

public class CustomerRecordController extends BaseController {

	/**
	 * 客户
	 * @author liuran
	 * @time 2017年5月13日 下午1:35:56 void
	 */
	public void index() {
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[] { "id", "name", "phone","cn_type","cn_card_type","card_id",
				"belong_user_id", "belong_user_name", "belong_org_id",
				"belong_org_name", "cn_status"});
		tableHeader.setCNNames(new String[] { "客户编号", "名称", "电话号码",  "客户类型","证件类型","证件编号","管户人编号",
				"管户人名称", "管户机构编号", "管户机构名称", "客户状态"});
		// 多选框
		tableHeader.setMultiple(true);
		
		Search search = new Search();
		search.setFieldNames(new String[] { "id", "name","type" ,"card_type","card_id"});
		search.setCNNames(new String[] { "客户编号", "名称" ,"客户类型","证件类型","证件编号"});
		search.setTypes(new String[] { "text", "text" ,"select","select","text"});

		Map<String, List<Map>> clListMap = new HashMap<String, List<Map>>();
		clListMap.put("type", OptionUtil.getOptionListByCodeLibrary(
				"CustomerType", true, null));
		clListMap.put("card_type", OptionUtil.getOptionListByCodeLibrary(
				"CertType", true, null));
		search.setOptionListMap(clListMap);
		DyResponse response = null;

		try {

			response = PageUtil.createTablePageStructure(
					"/record/data", "客户列表数据", tableHeader, search,
					super.getLastPara().toString(),
					(Map<Integer, SysMenu>) this.getSessionAttr("menus"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.setAttr("response", response);
		this.render("common/table.html");
	}

	public void data() {
		String conditation = this.getPara("condition");
		String page = this.getPara("page");
		System.out.println("查询条件：" + conditation);
		
		Map whereMap = super.getWhereMap(conditation);
		// 定义查询对象
		QueryItem item = new QueryItem();
		// 设置表名（多表用逗号间隔）
		item.setTableNames("cu_object_customer");
		// 设置查询字段（可以使用函数包括聚合函数）
		item.setFields("*, "
				+ "getCodeItemName('cu_status',status) as cn_status,"
				+ "getCodeItemName('CertType',card_type) as cn_card_type,"
				+ "FROM_UNIXTIME(update_time) as cn_update_time,"
				+ "getCodeItemName('CustomerType',type) as cn_type");
		// 设置分页步长 如果不设置 或者 值<=0，则会默认歩长为20
		// item.setLimit(10);
		// 注意：此处whereMap在页面初始化的时候为null，此处有必要判空
		List<QueryWhere> whereList = new ArrayList<QueryWhere>();
		
		// 默认追加条件
		// 检索条件
		if (whereMap != null) {
			whereList.add(new QueryWhere("id",LIKE_ALL, whereMap.get("id")));
			whereList.add(new QueryWhere("name",LIKE_ALL, whereMap.get("name")));
			whereList.add(new QueryWhere("type", whereMap.get("type")));
			whereList.add(new QueryWhere("card_type", whereMap.get("card_type")));
			whereList.add(new QueryWhere("card_id",LIKE_ALL, whereMap.get("card_id")));
		}
		whereList.add(new QueryWhere("belong_org_id", LIKE_RIGHT,getCurrentUserBelongID()));
		item.setOrder("update_time desc");
		item.setWhereList(whereList);
		// 4.获取数据 格式为List<Record>
		DyResponse dyResponse = super.getTableData(item, page);
		// 5.返回数据到页面 response 固定值 不可改变
		this.setAttr("response", dyResponse);
		renderJson(dyResponse);
	}
	/**
	 * 构建表单页面，增改查
	 * @author liuran
	 * @time 2017年5月16日 下午5:24:47 void
	 */
	public void form() {
		String type = getPara(0);
		String id = getPara("id");
		if(type == null){
			renderHtml("<h1>数据库错误，请联系管理员。</h1>");
			return;
		}
		List<FormField> formFieldList = new ArrayList<FormField>();
		formFieldList.add(new FormField("id", "", "hidden"));
		
		formFieldList.add(new FormField("name", "客户姓名", "text"));
		formFieldList.add(new FormField("phone", "电话", "text"));	
		formFieldList.add(new FormField("status", "状态", "select", "1", OptionUtil.getOptionListByCodeLibrary("cu_status", true, "")));
		formFieldList.add(new FormField("card_type", "证件类型", "select", "", OptionUtil.getOptionListByCodeLibrary("CertType", true, "")));
		formFieldList.add(new FormField("card_id", "证件编号", "text"));
		 
		DyResponse response = null;

			if (type.equals("add")) {
				formFieldList.add(new FormField("type", "客户类型", "select", "",OptionUtil.getOptionListByCodeLibrary("CustomerType", true, ""),true));
				response = PageUtil.createFormPageStructure("客户添加",formFieldList, "/record/toAdd");
			} else if (type.equals("edit")) {
				if (id==null || id.equals("")) {
					renderHtml("<h1>请先选择一条记录。</h1>");
					return;
				}
				response = PageUtil.createFormPageStructure("客户编辑",formFieldList, "/record/toEdit");
			} else if (type.equals("detail")) {
				if (id==null || id.equals("")) {
					renderHtml("<h1>请先选择一条记录。</h1>");
					return;
				}
				formFieldList.add(new FormField("type", "客户类型", "select", "",OptionUtil.getOptionListByCodeLibrary("CustomerType", true, "")));
				formFieldList.add(new FormField("cn_update_time", "更新时间", "text"));
				// 第三个参数 由于校验非空，所以要随便写点什么即可
				response = PageUtil.createFormPageStructure("查看详细信息",formFieldList, "/detail");
			}
			if (response == null) {
				renderHtml("<h1>数据库错误，请联系管理员。</h1>");
				return;
			}
		this.setAttr("response", response);
		this.render("common/form.html");
	}

	// 提交
	public void toAdd() {
		SysUser user = getSessionAttr("user");
		String type = getPara("type");//客户类型
		CuObjectCustomer cuObjectCustomer = getModel(CuObjectCustomer.class, "");
		if (type.equals("1")) {
			cuObjectCustomer.set("id", "COR" + DateUtil.getCurrentTime());//公司客户
		}
		if(type.equals("2")) {
			cuObjectCustomer.set("id", "IND" + DateUtil.getCurrentTime());//个人客户
		}
		cuObjectCustomer.set("input_time", DateUtil.getCurrentTime());
		cuObjectCustomer.set("update_time", DateUtil.getCurrentTime());		
		cuObjectCustomer.set("belong_org_id", getCurrentUserBelongID());
		cuObjectCustomer.set("belong_org_name",user.getStr("belong_org_name"));		
		cuObjectCustomer.set("input_user_id",user.getInt("id"));
		cuObjectCustomer.set("input_user_name",user.getStr("name"));
		cuObjectCustomer.set("input_org_id",user.getInt("belong_org_id"));
		cuObjectCustomer.set("input_org_name",user.getStr("belong_org_name"));

		try {
			cuObjectCustomer.save();
			renderHtml("<h1>操作成功！！</h1>");
		} catch (Exception e) {
			renderHtml("<h1>操作失败！！</h1>");
		}
	}

	/**
	 * 删除
	 * @author liuran
	 * @time 2017年5月17日 下午4:44:10 void
	 */
	@Before(Tx.class)
	public void del() {
		String id=getPara("id");
		if (id==null || id.equals("")) {
			renderText("请选择至少一条记录");
			return;
		}		
		String[] ids = id.split(",");
		// 批量删除
		CuObjectCustomer cuObjectCustomer = new CuObjectCustomer();

		try {
			for (String id1 : ids) {
				cuObjectCustomer.deleteById(id1);
			}
			renderText("操作成功！！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderText("操作失败！！");
		}
	}
	
	public void toEdit() {
		SysUser user = getSessionAttr("user");
		CuObjectCustomer cuObjectCustomer = getModel(CuObjectCustomer.class, "");
		
		try {
			cuObjectCustomer.set("update_time", DateUtil.getCurrentTime());
			cuObjectCustomer.set("update_user_id", user.getInt("id"));
			cuObjectCustomer.set("update_user_name", user.getStr("name"));
			cuObjectCustomer.set("update_org_id", user.getInt("belong_org_id"));
			cuObjectCustomer.set("update_org_name", user.getStr("belong_org_name"));
			cuObjectCustomer.update();
			renderHtml("<h1>操作成功！！</h1>");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderHtml("<h1>操作失败！！</h1>");
		}
	}
	/**
	 * @author:zhaozhongyuan
	 * @Description:放弃维护
	 * @return void
	 * @date 2017年5月15日 下午3:29:53
	 */
	public void btnAbandon() {
		String id=getPara("id");
		if (id==null || id.equals("")) {
			renderText("请选择至少一条记录！！！");
			return;
		}
		// 接收要放弃维护的id数组
		String[] ids = id.split(",");
		SysUser user = getSessionAttr("user");
		// 更新status 值为"1"
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("belong_user_id", 0);
		map.put("update_time", DateUtil.getCurrentTime());
		map.put("update_user_id", user.getInt("id"));
		map.put("update_user_name", user.getStr("name"));
		map.put("update_org_id", user.getInt("belong_org_id"));
		map.put("update_org_name", user.getStr("belong_org_name"));
		try {
			updateByIds(CuObjectCustomer.class, ids, map);
			renderText("操作成功！！");;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderText("操作失败！！");
		}

	}

}
