package com.dlcat.core.controller.customer.customerSaleManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.dlcat.common.BaseController;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.FormField;
import com.dlcat.common.entity.QueryItem;
import com.dlcat.common.entity.QueryWhere;
import com.dlcat.common.entity.Search;
import com.dlcat.common.entity.TableHeader;
import com.dlcat.common.utils.DateUtil;
import com.dlcat.common.utils.ExportExcelUtil;
import com.dlcat.common.utils.OptionUtil;
import com.dlcat.common.utils.PageUtil;
import com.dlcat.core.model.CuPossibleCustomer;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysUser;
import com.dlcat.core.model.ToCodeLibrary;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;

public class CustomerAllotControllor extends BaseController {
    //接收列表选择的参数
	private static String[] idarrays;
    private static Map<String, String> fields;
	

	public void index() {
		// 首先注意：此处的列表页面应该是在Tab下面的，所以加载Tab中的url，
		// Tab的url中需要将Tab的id拼接到url最后
		// 1.定义列表的表头和字段 注意：字段名称必须是数据库中的实际存在
		// 的字段名称或者虚拟字段名称，如cn_status
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[] { "id", "name", "phone", "cn_type", "cn_op_status", "cn_input_time" });
		tableHeader.setCNNames(new String[] { "ID", "名称", "电话", "客户类型", "处理进度", "添加时间" });
		tableHeader.setMultiple(true);
		//获取真实的字段名，后面导出用
		setFields(getTrueFields(tableHeader.getFieldNames(),tableHeader.getCNNames()));
		// 2.定义检索区域检索框 注意：字段名称必须是实际存在的字段名称.
		// CNName中文标示这个检索字段的含义，type标示检索框的类型
		Search search = new Search();
		search.setFieldNames(new String[] { "phone", "startTime", "endTime", "type" });
		search.setCNNames(new String[] { "手机号", "开始时间", "结束时间", "客户类型" });
		search.setTypes(new String[] { "text", "date", "date", "select" });
		// 3.定义下拉数据源 如果检索区域中存在select，必须定义下拉数据源
		
		//注意：这里的资源必须和表头字段中的一致，可以定义多个
		Map<String,List<Map>> clListMap = new HashMap<String, List<Map>>();
		clListMap.put("type", OptionUtil.getOptionListByCodeLibrary("CustomerType",true,null));
		search.setOptionListMap(clListMap);
		
		
		DyResponse response = null;

		try {
			// 4.构造页面结构并返回，注意：第一个参数标示请求列表数据时候的url，必须是存在的
			// 第二个参数是这个列表数据的名称，如果页面中存在这个导出功能，这个名称就是导出的
			// excel文件的文件名称
			response = PageUtil.createTablePageStructure("/customerAllot/data", "测试列表数据", tableHeader, search,
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
				"*, getCodeItemName('CustomerType',type) as cn_type,getCodeItemName('OpCuPosStatus',op_status) as cn_op_status,FROM_UNIXTIME(input_time) as cn_input_time");

		List<QueryWhere> whereList = new ArrayList<QueryWhere>();
		//获取本部门以及以下部门
		whereList.add(new QueryWhere("belong_org_id", LIKE_RIGHT, getCurrentUserBelongID()));
		//或者所属部门为空
		//whereList.add(new QueryWhere("belong_org_id", NULL));
		whereList.add(new QueryWhere("belong_user_id", 0));

		// 注意：此处whereMap在页面初始化的时候为null，此处有必要判空
		if (whereMap != null) {
			whereList.add(new QueryWhere("phone", LIKE_RIGHT, whereMap.get("phone")));
			whereList.add(new QueryWhere("type", whereMap.get("type")));
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
	* @Description:意向客户分配
	* @return void   
	* @date 2017年5月15日 上午11:44:25  
	*/
	public void btnCustomerAllot() {
		//获取选中的意向客户id数组
		String[] ids=getIdarrays();
		//分配给某一个用户的用户ID
		int id=getParaToInt("id");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("belong_user_id", id);
		try {
			updateByIds(CuPossibleCustomer.class, ids, map);
			renderText("操作成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderText("操作失败");
		}
	}
	
	/**
	 * @author:zhaozhongyuan 
	 * @Description:获取本部门的业务员列表
	 * @return void   
	 * @date 2017年5月15日 上午11:44:25  
	 */
	public void getUsers() {
		//获取当前登录用户的所属部门ID
		setIdarrays(getPara("id").toString().split(","));
		
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[] { "id", "name", "role_id", "belong_org_id", "status"});
		tableHeader.setCNNames(new String[] { "ID", "名称", "角色", "所属部门Id", "是否可用" });
		// 2.定义检索区域检索框 注意：字段名称必须是实际存在的字段名称.
		
		// 3.定义下拉数据源 如果检索区域中存在select，必须定义下拉数据源
		
		//注意：这里的资源必须和表头字段中的一致，可以定义多个
		
		
		
		DyResponse response = null;

		try {
			// 4.构造页面结构并返回，注意：第一个参数标示请求列表数据时候的url，必须是存在的
			// 第二个参数是这个列表数据的名称，如果页面中存在这个导出功能，这个名称就是导出的
			// excel文件的文件名称
			response = PageUtil.createTablePageStructure("/customerAllot/userdata", "测试列表数据", tableHeader, null,
					super.getLastPara().toString(), (Map<Integer, SysMenu>) this.getSessionAttr("menus"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 5.返回数据到页面 response不可改变,页面名称也不可改变
		this.setAttr("response", response);
		this.render("common/table.html");
		
		
		
		
		
	}
	public void userdata() {
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
		item.setTableNames("sys_user");
		item.setFields("*");

		List<QueryWhere> whereList = new ArrayList<QueryWhere>();
		//获取本部门以及以下部门
		whereList.add(new QueryWhere("belong_org_id", LIKE_RIGHT, getCurrentUserBelongID()));

		// 注意：此处whereMap在页面初始化的时候为null，此处有必要判空
		
		item.setWhereList(whereList);
		// 4.获取数据 格式为List<Record>
		DyResponse dyResponse = super.getTableData(item, page);
		// 5.返回数据到页面 response 固定值 不可改变
		this.setAttr("response", dyResponse);
		renderJson();
	}
	
	public void add() {
		List<FormField> formFieldList = new ArrayList<FormField>();

		formFieldList.add(new FormField("name", "客户姓名", "text"));
		formFieldList.add(new FormField("phone", "电话", "text"));
		formFieldList.add(new FormField("type", "客户类型", "select", "",
				ToCodeLibrary.getCodeLibrariesBySQL("CustomerType", true, null)));

		DyResponse response = PageUtil.createFormPageStructure("意向客户添加", formFieldList, "toAdd");
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
	
	public void edit() {
		String[] ids = getPara("id").toString().split(",");
		if (ids.length != 1) {

		} else {
			CuPossibleCustomer cuPossibleCustomer = CuPossibleCustomer.dao.findById(ids[0]);
			List<FormField> formFieldList = new ArrayList<FormField>();

			formFieldList.add(new FormField("id", "", "hidden", cuPossibleCustomer.getStr("id")));
			formFieldList.add(new FormField("name", "客户姓名", "text", cuPossibleCustomer.getStr("name")));
			formFieldList.add(new FormField("phone", "电话", "text", cuPossibleCustomer.getStr("phone")));
			formFieldList.add(new FormField("type", "客户类型", "select", cuPossibleCustomer.getStr("type"),
					ToCodeLibrary.getCodeLibrariesBySQL("CustomerType", true, null)));

			DyResponse response = PageUtil.createFormPageStructure("意向客户编辑", formFieldList, "toEdit");
			this.setAttr("response", response);
			this.render("common/form_editarea.html");
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
	
	@Before(Tx.class)
	public void del() {
		String[] ids = getPara("id").toString().split(",");
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
	
	public void toImport() {
		List<FormField> formFieldList = new ArrayList<FormField>();
		formFieldList.add(new FormField("file", "请选择xls格式的Excel文件", "file"));
		DyResponse response = PageUtil.createFormPageStructure("导入", formFieldList, "importExcel");
		this.setAttr("response", response);
		this.render("common/form_editarea.html");
	}
	
	public void importExcel() {
		UploadFile uploadFile=getFile();
		// 获得上传的文件
		File file=uploadFile.getFile();
		if (!file.getName().endsWith(".xls")) {
			renderHtml("<h1>上传失败！！请选择正确格式的文件进行上传。</h1>");
		};
		try {
			ExportExcelUtil.imporeExcel("cu_possible_customer", getFields(), file);
			renderHtml("<h1>导入数据成功</h1>");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderHtml("<h1>导入数据失败</h1>");
		}
	}

	
	
	
	public static Map<String, String> getFields() {
		return fields;
	}

	public static void setFields(Map<String, String> fields) {
		CustomerAllotControllor.fields = fields;
	}

	public static String[] getIdarrays() {
		return idarrays;
	}

	public static void setIdarrays(String[] idarrays) {
		CustomerAllotControllor.idarrays = idarrays;
	}
	
}
