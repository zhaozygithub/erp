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
import com.dlcat.common.utils.StringUtils;
import com.dlcat.core.model.CuObjectCustomer;
import com.dlcat.core.model.CuPossibleCustomer;
import com.dlcat.core.model.SysAdminLog;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysRole;
import com.dlcat.core.model.SysUser;
import com.dlcat.core.model.ToCodeLibrary;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
/**
 * @ClassName ObjectCustomerAllotController  
 * @Description 正式客户分配   
 * @author liuran  
 * @time 2017年5月26日上午9:53:05
 */
public class ObjectCustomerAllotController extends BaseController {

	
	public void index() {
		// 首先注意：此处的列表页面应该是在Tab下面的，所以加载Tab中的url，
		// Tab的url中需要将Tab的id拼接到url最后
		// 定义列表的表头和字段 注意：字段名称必须是数据库中的实际存在
		// 的字段名称或者虚拟字段名称，如cn_status
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[] { "id", "name", "phone","cn_type","cn_card_type","card_id",
				"belong_user_id", "belong_user_name", "belong_org_id",
				"belong_org_name", "cn_status"});
		tableHeader.setCNNames(new String[] { "客户编号", "名称", "电话号码",  "客户类型","证件类型","证件编号","管户人编号",
				"管户人名称", "管户机构编号", "管户机构名称", "客户状态"});
		// 多选框
		tableHeader.setMultiple(true);
		// 定义检索区域检索框 注意：字段名称必须是实际存在的字段名称，
		// CNName中文标示这个检索字段的含义，type标示检索框的类型
		Search search = new Search();
		search.setFieldNames(new String[] { "id", "name","type" ,"card_type","card_id"});
		search.setCNNames(new String[] { "客户编号", "名称" ,"客户类型","证件类型","证件编号"});
		search.setTypes(new String[] { "text", "text" ,"select","select","text"});
		// 定义下拉数据源 如果检索区域中存在select，必须定义下拉数据源
		// 注意：这里的资源必须和表头字段中的一致，可以定义多个
		Map<String, List<Map>> clListMap = new HashMap<String, List<Map>>();
		clListMap.put("type", OptionUtil.getOptionListByCodeLibrary(
				"CustomerType", true, null));
		clListMap.put("card_type", OptionUtil.getOptionListByCodeLibrary(
				"CertType", true, null));
		search.setOptionListMap(clListMap);
		DyResponse response = null;

		try {
			// 构造页面结构并返回，注意：第一个参数标示请求列表数据时候的url，必须是存在的
			// 第二个参数是这个列表数据的名称，如果页面中存在这个导出功能，这个名称就是导出的
			// excel文件的文件名称
			response = PageUtil.createTablePageStructure(
					"/distribution/data", "正式客户分配列表数据", tableHeader, search,
					super.getLastPara().toString(),
					(Map<Integer, SysMenu>) this.getSessionAttr("menus"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 返回数据到页面 response不可改变,页面名称也不可改变
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
		// whereList.add(new QueryWhere("2","2")); //注意：jfinal仅仅支持一个常量条件，超过一个会报错
			// 检索条件
		if (whereMap != null) {
			whereList.add(new QueryWhere("id",LIKE_ALL, whereMap.get("id")));
			whereList.add(new QueryWhere("name",LIKE_ALL, whereMap.get("name")));
			whereList.add(new QueryWhere("type", whereMap.get("type")));
			whereList.add(new QueryWhere("card_type", whereMap.get("card_type")));
			whereList.add(new QueryWhere("card_id",LIKE_ALL, whereMap.get("card_id")));
		}
		// 默认追加条件
		// whereList.add(new QueryWhere("2","2")); //注意：jfinal仅仅支持一个常量条件，超过一个会报错
		whereList.add(new QueryWhere("belong_user_id", 0));
		whereList.add(new QueryWhere("belong_org_id", LIKE_RIGHT,getCurrentUserBelongID()));
		item.setOrder("update_time desc");
		item.setWhereList(whereList);
		DyResponse dyResponse = super.getTableData(item, page);
		renderJson(dyResponse);
	}

	/**
	 * 构建表单页面，增改查
	 * 
	 * @author liuran
	 * @time 2017年5月16日 下午5:24:47 void
	 */
	public void form() {
		String btnId = getPara("btnid");
		String type = getPara(0);
		String id = getPara("id");
		if(type == null){
			renderJson(createErrorJsonResonse("数据库错误，请联系管理员！"));
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
			//由于代码相同，这里使用CustomerRecordControllor里面的toAdd方法去添加，如果有差异可以使用本类的toAdd方法去改造
			//response = PageUtil.createFormPageStructure("正式客户分配添加",formFieldList, "/distribution/toAdd");
			response = PageUtil.createFormPageStructure("正式客户分配添加",formFieldList, "/record/toAdd?btnId="+btnId);
		} else if (type.equals("edit")) {
			if (id==null || id.equals("")) {
				renderJson(createErrorJsonResonse("请先选择一条记录！"));
				return;
			}
			//由于代码相同，这里使用CustomerRecordControllor里面的toEdit方法去更新，如果有差异可以使用本类的toEdit方法去改造
			//response = PageUtil.createFormPageStructure("正式客户分配编辑",formFieldList, "/distribution/toEdit");
			response = PageUtil.createFormPageStructure("正式客户分配编辑",formFieldList, "/record/toEdit?btnId="+btnId);
		} else if (type.equals("detail")) {
			if (id==null || id.equals("")) {
				renderJson(createErrorJsonResonse("请先选择一条记录！"));
				return;
			}
			formFieldList.add(new FormField("type", "客户类型", "select", "",OptionUtil.getOptionListByCodeLibrary("CustomerType", true, "")));
			formFieldList.add(new FormField("cn_update_time", "更新时间", "text"));
			// 第三个参数 由于校验非空，所以要随便写点什么即可
			response = PageUtil.createFormPageStructure("查看详细信息",
					formFieldList, "/detail");
		}
		if (response == null) {
			renderJson(createErrorJsonResonse("数据库错误，请联系管理员！"));
			return;
		}
		this.setAttr("response", response);
		this.render("common/form.html");
	}

	/*public void toAdd() {
		SysUser user = getSessionAttr("user");
		String card_type =getPara("card_type");//证件类型
		String card_id =getPara("card_id");//证件编号
		String type = getPara("type");//客户类型
		if (StringUtils.isBlank(card_type)) {
			renderJson(createErrorJsonResonse("证件类型不能为空，请重新输入"));
			return;
		}
		if (StringUtils.isBlank(card_id)) {
			renderJson(createErrorJsonResonse("证件编号不能为空，请重新输入"));
			return;
		}
		
		CuObjectCustomer cuObjectCustomer = getModel(CuObjectCustomer.class, "");
		
		if (StringUtils.isNotBlank(type)) {
			if (type.equals("1")) {
				cuObjectCustomer.set("id", "COR" + DateUtil.getCurrentTime());//公司客户
			}
			if(type.equals("2")) {
				cuObjectCustomer.set("id", "IND" + DateUtil.getCurrentTime());//个人客户
			}
		}else {
			renderJson(createErrorJsonResonse("客户类型不能为空，请重新输入"));
			return;
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
			renderJson(createSuccessJsonResonse());
		} catch (Exception e) {
			renderJson(createErrorJsonResonse("操作失败！"));
		}
	}*/

	/**
	 * 删除
	 * @author liuran
	 * @time 2017年5月17日 下午4:44:10 void
	 */
	/*@Before(Tx.class)
	public void del() {
		String id=getPara("id");
		if (id==null || id.equals("")) {
			renderJson(createErrorJsonResonse("请选择至少一条记录！"));		
			return;
		}		
		String[] ids = id.split(",");
		// 批量删除
		CuObjectCustomer cuObjectCustomer = new CuObjectCustomer();

		try {
			for (String id1 : ids) {
				cuObjectCustomer.deleteById(id1);
			}
			renderJson(createSuccessJsonResonse());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(createErrorJsonResonse("操作失败！"));
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
			renderJson(createSuccessJsonResonse());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson(createErrorJsonResonse("操作失败！"));
		}
	}*/
	
	
	/**
	* @author:zhaozhongyuan 
	* @Description:正式客户分配
	* @return void   
	* @date 2017年5月15日 上午11:44:25  
	*/
	public void btnCustomerAllot() {
		String btnId=getPara("btnid");
		//获取选中的意向客户id数组
		String[] ids=getIdarrays();
		//分配给某一个用户的用户ID
		Integer id=getParaToInt("id");
		
		if (id==null || id.equals("")) {
			renderJson(createErrorJsonResonse("请选择至少一条记录！"));
			return;
		}
		SysUser user = getSessionAttr("user"); //当前用户
		SysUser allotToUser = SysUser.dao.findById(id); //分配给该用户的id
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("belong_user_id", id);
		map.put("belong_user_name", allotToUser.getStr("name"));
		map.put("belong_org_id", allotToUser.getInt("belong_org_id"));
		map.put("belong_org_name", allotToUser.getStr("belong_org_name"));
		map.put("update_time", DateUtil.getCurrentTime());
		map.put("update_user_id", user.getInt("id"));
		map.put("update_user_name", user.getStr("name"));
		map.put("update_org_id", user.getInt("belong_org_id"));
		map.put("update_org_name", user.getStr("belong_org_name"));
		try {
			updateByIds(CuObjectCustomer.class, ids, map);
			SysAdminLog.SetAdminLog(user, btnId, "把客户："+StringUtils.arrayToStr(ids, ",")+"分配给"+id);
			renderJson(createSuccessJsonResonse());
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(createErrorJsonResonse("操作失败！"));
		}
	}

	

}
