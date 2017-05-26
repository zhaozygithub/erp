package com.dlcat.core.controller.collateral;

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
import com.dlcat.core.model.CuProperty;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysUser;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * 押品管理模块
 * @ClassName CollateralController
 * @author liuran
 * @time 2017年5月8日 下午1:50:36
 */
public class CollateralController extends BaseController {
	public void index(){
		//首先注意：此处的列表页面应该是在Tab下面的，所以加载Tab中的url，
		//Tab的url中需要将Tab的id拼接到url最后
		//1.定义列表的表头和字段  注意：字段名称必须是数据库中的实际存在
		//的字段名称或者虚拟字段名称，如cn_status
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[]{"id","cn_type","cu_id","cu_name","cn_certificate_type","certificate_no","cn_buy_time","cn_buy_type","buy_price","brand","color","run_km","area","addr","cn_status"});
		tableHeader.setCNNames(new String []{"财产编号","财产种类","所属客户编号","所属客户名称","证件类型","证件编号","购买时间","购买方式","购买价格","品牌","颜色","已行驶里程数（㎞）","面积（㎡）","详细地址","状态"});
		//多选框
		tableHeader.setMultiple(true);
		//2.定义检索区域检索框 注意：字段名称必须是实际存在的字段名称，
		//CNName中文标示这个检索字段的含义，type标示检索框的类型
		//字段名称与下边whereList对应
		Search search = new Search();
		search.setFieldNames(new String[]{"id","type","certificate_type","certificate_no","start_time","end_time","status"});
		search.setCNNames(new String[]{"财产编号","财产种类","证件类型","证件编号","开始时间","截止时间","状态"});
		search.setTypes(new String[]{"text","select","select","text","date","date","select"});
		
		//serch类型为select需在此配置
		//3.定义下拉数据源 如果检索区域中存在select，必须定义下拉数据源
		//注意：这里的资源必须和表头字段(库里真实存在字段)中的一致，可以定义多个
		Map<String,List<Map>> clListMap = new HashMap<String, List<Map>>();
		clListMap.put("type", OptionUtil.getOptionListByCodeLibrary("CustomerProperty",true,null));
		clListMap.put("certificate_type", OptionUtil.getOptionListByCodeLibrary("CertType",true,null));
		clListMap.put("status", OptionUtil.getOptionListByCodeLibrary("ProStatus",true,null));
		search.setOptionListMap(clListMap);
		
		DyResponse response = null;
		
		try {
			//4.构造页面结构并返回，注意：第一个参数标示请求列表数据时候的url，必须是存在的
			//第二个参数是这个列表数据的名称，如果页面中存在这个导出功能，这个名称就是导出的
			//excel文件的文件名称
			response = PageUtil.createTablePageStructure("/collateral/data", "押品列表数据", tableHeader, 
			search,super.getLastPara().toString(),(Map<Integer, SysMenu>)this.getSessionAttr("menus"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//5.返回数据到页面 response不可改变,页面名称也不可改变
		this.setAttr("response", response);
		this.render("common/table.html");
	}
	
	 public void data(){
		 SysUser user = (SysUser) getSessionAttr("user");
		 
		   //1.从页面获取参数 condition参数名称固定 key=condition 格式为：name:ms,age:24
		   //获取分页参数	固定格式 key=page
		   String conditation = this.getPara("condition");
		   String page = this.getPara("page");
		   System.out.println("查询条件："+conditation);
		   //2.列表数据sql	特别注意：如果需要如果需要转化成为字典值调用getCodeItemName()数据库函数，
		   //别名一律为	cn_原名称
//		   StringBuffer sql = new StringBuffer("select *, getCodeItemName('CustomerProperty',type) as cn_type,FROM_UNIXTIME(buy_time) as cn_buy_time,getCodeItemName('ProStatus',status) as cn_status from cu_property where cu_id in(select id from cu_object_customer where belong_org_id =  "+user.getInt("belong_org_id")+")");
		   
		   //3.获取参数Map，处理字符串拼接
		   Map whereMap = super.getWhereMap(conditation);
		 //定义查询对象
		   QueryItem item = new QueryItem();
		   item.setTableNames("cu_property");
		   item.setFields("*"
		   		+ ",getCodeItemName('CustomerProperty',type) as cn_type"
		   		+ ",getCodeItemName('CertType',certificate_type) as cn_certificate_type"
		   		+ ",FROM_UNIXTIME(buy_time) as cn_buy_time"
		   		+ ",getCodeItemName('BuyType',buy_type) as cn_buy_type"
		   		+ ",getCodeItemName('ProStatus',status) as cn_status ");
		   
		   //设置分页步长  如果不设置 或者 值<=0，则会默认歩长为20
//		   item.setLimit(10);
		   //注意：此处whereMap在页面初始化的时候为null，此处有必要判空
		   List<QueryWhere> whereList = new ArrayList<QueryWhere>();
		   //检索条件
		   if(whereMap != null){   
			   //与上边serch对应
			   whereList.add(new QueryWhere("id", LIKE_ALL, whereMap.get("id")));
			   whereList.add(new QueryWhere("type", whereMap.get("type")));
			   whereList.add(new QueryWhere("certificate_type", whereMap.get("certificate_type")));
			   whereList.add(new QueryWhere("certificate_no",LIKE_ALL, whereMap.get("certificate_no")));
			   whereList.add(new QueryWhere("status", whereMap.get("status")));				   
			   if (whereMap.get("start_time") != null) {
					whereList.add(new QueryWhere("FROM_UNIXTIME(buy_time)", GE, whereMap.get("start_time")));
				}
				if (whereMap.get("end_time") != null) {
					whereList.add(new QueryWhere("FROM_UNIXTIME(buy_time)", LE, whereMap.get("end_time")));
				}
		   }
		   //默认追加条件
		  // whereList.add(new QueryWhere("2","2"));	//注意：jfinal仅仅支持一个常量条件，超过一个会报错
		   whereList.add(new QueryWhere("cu_id",IN,"(select id from cu_object_customer where belong_org_id LIKE '"+user.getInt("belong_org_id")+"%')"));
		   //QueryWhere()2个参数 默认是name=value,3个参数中间加上符号或条件
//		   whereList.add(new QueryWhere("cu_id",GT,"1"));
		 //获取本部门以及以下部门,或者所属部门为空
//			item.setOtherWhere("and (belong_org_id  like  '1%' or  belong_org_id  is null)");
//			
//			item.setOrder("input_time desc");
		   item.setWhereList(whereList);
		   DyResponse dyResponse = super.getTableData(item,page);
		   renderJson(dyResponse);
	   }
	/**
	 * 构建表单页面，增改查
	 * @author liuran
	 * @time 2017年5月16日 下午5:24:47 void
	 */
	 public void form() {
			String type = getPara(0);
			String id=getPara("id");
			if(type == null){
				renderJson(createErrorJsonResonse("数据库错误，请联系管理员！"));
				return;
			}
			List<FormField> formFieldList = new ArrayList<FormField>();
			formFieldList.add(new FormField("id", "", "hidden"));//必须加上这个，否则edit无法辨别编辑的数据
			
			formFieldList.add(new FormField("type", "财产种类", "select","",OptionUtil.getOptionListByCodeLibrary("CustomerProperty", true, "")));
			formFieldList.add(new FormField("cu_id", "所属客户编号", "text","",true));
			formFieldList.add(new FormField("cu_name", "所属客户名称", "text","",true));
			formFieldList.add(new FormField("certificate_type", "证件类型", "select","",OptionUtil.getOptionListByCodeLibrary("CertType", true, ""),true));
			formFieldList.add(new FormField("certificate_no", "证件编号", "text","",true));
			formFieldList.add(new FormField("buy_time", "购买时间", "text"));
			//这里时间需要date类型支持
			formFieldList.add(new FormField("buy_type", "购买类型", "select","",OptionUtil.getOptionListByCodeLibrary("BuyType", true, "")));
			formFieldList.add(new FormField("buy_price", "购买价格", "text"));		
			formFieldList.add(new FormField("brand", "品牌", "text"));
			formFieldList.add(new FormField("color", "颜色", "text"));
			formFieldList.add(new FormField("run_km", "已行驶里程数（㎞）", "text"));
			formFieldList.add(new FormField("area", "面积（㎡）", "text"));
			formFieldList.add(new FormField("addr", "详细地址", "text"));
			formFieldList.add(new FormField("status", "状态", "select","",OptionUtil.getOptionListByCodeLibrary("ProStatus", true, "")));
			
			DyResponse response = null;
			
			if (type.equals("add")) {
				response = PageUtil.createFormPageStructure("押品添加", formFieldList, "/collateral/toAdd");
			} else if (type.equals("edit")) {
				if (id==null || id.equals("")) {
					renderJson(createErrorJsonResonse("请先选择一条记录！"));
					return;
				}
				response = PageUtil.createFormPageStructure("押品编辑", formFieldList, "/collateral/toEdit");
			}else if (type.equals("detail")) {
				if (id==null || id.equals("")) {
					renderJson(createErrorJsonResonse("请先选择一条记录！"));
					return;
				}
				response = PageUtil.createFormPageStructure("查看详细信息", formFieldList, "/detail");
			}
			if (response == null) {
				renderJson(createErrorJsonResonse("数据库错误，请联系管理员！"));
				return;
			}
			this.setAttr("response", response);
			this.render("common/form.html");
		}

		public void toAdd() {
			String cuId = getPara("cu_id");
			String certificate_type = getPara("certificate_type");
			String certificate_no = getPara("certificate_no");
			if (!CuObjectCustomer.isHasCustomer(cuId)) {
				renderJson(createErrorJsonResonse("所属客户不存在，请重新添加！"));
				return;
			}
			if ((!StringUtils.isNotBlank(certificate_type)) || (!StringUtils.isNotBlank(certificate_no))) {
				renderJson(createErrorJsonResonse("证件填写错误，请重新添加！"));
				return;
			}
			if (CuProperty.isRepeatCertificate(certificate_type, certificate_no)) {
				renderJson(createErrorJsonResonse("证件已存在，请重新添加！"));
				return;
			}
			CuProperty cuProperty = getModel(CuProperty.class, "");
			cuProperty.set("id", "YP" + DateUtil.getCurrentTime());


			try {
				cuProperty.save();
				renderJson(createSuccessJsonResonse());
			} catch (Exception e) {
				renderJson(createErrorJsonResonse("操作失败！"));
			}
		}
		
		/**
		 * 删除
		 * @author liuran
		 * @time 2017年5月16日 下午7:01:32 
		 */
		@Before(Tx.class)
		public void del() {
			String id=getPara("id");
			if (id==null || id.equals("")) {
				renderJson(createErrorJsonResonse("请选择至少一条记录！"));
				return;
			}
			
			String[] ids = id.split(",");
			// 批量删除
			CuProperty cuProperty = new CuProperty();

			try {
				for (String id1 : ids) {
					cuProperty.deleteById(id1);
				}
				renderJson(createSuccessJsonResonse());
			} catch (Exception e) {
				e.printStackTrace();
				renderJson(createErrorJsonResonse("操作失败！"));
			}
		}
	
		public void toEdit() {
			String cuId = getPara("cu_id");
			String certificate_type = getPara("certificate_type");
			String certificate_no = getPara("certificate_type");
			if (!CuObjectCustomer.isHasCustomer(cuId)) {
				renderJson(createErrorJsonResonse("所属客户不存在，请重新编辑！"));
				return;
			}
			if (certificate_type.equals("") || certificate_no.equals("")) {
				renderJson(createErrorJsonResonse("证件填写错误，请重新编辑！"));
				return;
			}
			CuProperty cuProperty = getModel(CuProperty.class, "");
			try {
				cuProperty.update();
				renderJson(createSuccessJsonResonse());
			} catch (Exception e) {
				e.printStackTrace();
				renderJson(createErrorJsonResonse("操作失败！"));
			}
		}
}
