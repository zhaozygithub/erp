package com.dlcat.core.controller.collateral;

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
import com.dlcat.common.utils.PageUtil;
import com.dlcat.core.model.CuObjectCustomer;
import com.dlcat.core.model.CuProperty;
import com.dlcat.core.model.Form;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysUser;
import com.dlcat.core.model.ToCodeLibrary;

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
		tableHeader.setCNNames(new String []{"财产编号","财产种类","所属客户编号","所属客户名称","证件类型","证件编号","购买时间","购买方式","购买价格","品牌","颜色","已行驶里程数(单位:公里)","面积（单位：平方米）","详细地址","状态"});
		//2.定义检索区域检索框 注意：字段名称必须是实际存在的字段名称，
		//CNName中文标示这个检索字段的含义，type标示检索框的类型
		//字段名称与下边whereList对应
		Search search = new Search();
		search.setFieldNames(new String[]{"id","type","certificate_type","certificate_no","status"});
		search.setCNNames(new String[]{"财产编号","财产种类","证件类型","证件编号","状态"});
		search.setTypes(new String[]{"text","select","select","text","select"});
		
		//serch类型为select需在此配置
		//3.定义下拉数据源 如果检索区域中存在select，必须定义下拉数据源
		//注意：这里的资源必须和表头字段(库里真实存在字段)中的一致，可以定义多个
		Map<String,List<ToCodeLibrary>> clListMap = new HashMap<String, List<ToCodeLibrary>>();
		clListMap.put("type", ToCodeLibrary.getCodeLibrariesBySQL("CustomerProperty",true,null));
		clListMap.put("certificate_type", ToCodeLibrary.getCodeLibrariesBySQL("CertType",true,null));
		clListMap.put("status", ToCodeLibrary.getCodeLibrariesBySQL("ProStatus",true,null));
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
		this.render("table.html");
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
		   item.setLimit(10);
		   //注意：此处whereMap在页面初始化的时候为null，此处有必要判空
		   List<QueryWhere> whereList = new ArrayList<QueryWhere>();
		   //检索条件
		   if(whereMap != null){   
			   //与上边serch对应
			   whereList.add(new QueryWhere("id", whereMap.get("id")));
			   whereList.add(new QueryWhere("type", whereMap.get("type")));
			   whereList.add(new QueryWhere("certificate_type", whereMap.get("certificate_type")));
			   whereList.add(new QueryWhere("certificate_no", whereMap.get("certificate_no")));
			   whereList.add(new QueryWhere("status", whereMap.get("status")));		   
		   }
		   //默认追加条件
		  // whereList.add(new QueryWhere("2","2"));	//注意：jfinal仅仅支持一个常量条件，超过一个会报错
		   whereList.add(new QueryWhere("cu_id",IN,"(select id from cu_object_customer where belong_org_id LIKE '"+user.getInt("belong_org_id")+"%')"));
		   //QueryWhere()2个参数 默认是name=value,3个参数中间加上符号或条件
//		   whereList.add(new QueryWhere("cu_id",GT,"1"));
		   item.setWhereList(whereList);
		   //4.获取数据  格式为List<Record>
		   DyResponse dyResponse = super.getTableData(item,page);
		   //5.返回数据到页面	response 固定值 不可改变
		   this.setAttr("response", dyResponse);
		   renderJson();
	   }
	
	//新增或编辑进入
	public void form(){
		String id = getPara(0);
		if(id != null){
			setAttr("property", CuProperty.dao.findById(id));
		}
		render("collateral_form.html");
	
	}
	//提交 新增
	public void add(){
		String customerId = getPara("cu_id");
		String certificateType = getPara("certificate_type");
		String certificateNo = getPara("certificate_no");
		if (CuObjectCustomer.isHasCustomer(customerId)) {
			if (CuProperty.isRepeatCertificate(certificateType, certificateNo)) {
				setAttr("msgcertificate_no", "证件信息输入有误，请重新填写！");
				return;
			}
			else {
				CuProperty property = getModel(CuProperty.class,"property");
				property.save();
				redirect("/collateral");
			}		
		}else {
			setAttr("msgcu_name", "所属客户不存在，请重新填写！");
			return;
		}	
	}
	
	//提交 修改
	public void update(){
		String customerId = getPara("cu_id");
		String certificateType = getPara("certificate_type");
		String certificateNo = getPara("certificate_no");
		if (CuObjectCustomer.isHasCustomer(customerId)) {
			if (CuProperty.isRepeatCertificate(certificateType, certificateNo)) {
				setAttr("msgcertificate_no", "证件信息输入有误，请重新填写！");
				return;
			}
			else {
				CuProperty property = getModel(CuProperty.class,"property");
				property.update();
				redirect("/collateral");
			}		
		}else {
			setAttr("msgcu_name", "所属客户不存在，请重新填写！");
			return;
		}	
	}
	
	//进入编辑页面
	public void edit() {
		form();
	}
	
	//删除
	public void del(){
		CuProperty.dao.deleteById(getPara(0));
		redirect("/collateral");
	}
	
	
}
