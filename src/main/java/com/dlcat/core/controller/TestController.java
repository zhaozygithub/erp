package com.dlcat.core.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.dlcat.common.BaseController;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.FormBuilder;
import com.dlcat.common.entity.FormField;
import com.dlcat.common.entity.QueryItem;
import com.dlcat.common.entity.QueryWhere;
import com.dlcat.common.entity.Search;
import com.dlcat.common.entity.TableHeader;
import com.dlcat.common.utils.OptionUtil;
import com.dlcat.common.utils.PageUtil;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.ToCodeLibrary;
import com.dlcat.service.flow.FlowService;
import com.dlcat.service.flow.impl.FlowServiceImpl;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

public class TestController extends BaseController{
	/**
	 * 自动组装sql，构造页面（单表）
	 * @author masai
	 * @time 2017年5月26日 下午3:42:01
	 */
	public void index(){
		//首先注意：此处的列表页面应该是在Tab下面的，所以加载Tab中的url，
		//Tab的url中需要将Tab的id拼接到url最后
		//1.定义列表的表头和字段  注意：字段名称必须是数据库中的实际存在
		//的字段名称或者虚拟字段名称，如cn_status
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[]{"id","name","business_id","repay_type","remark","cn_status"});
		tableHeader.setCNNames(new String []{"id","名称","业务类型编号","还款方式","备注","是否开启"});
		tableHeader.setMultiple(true);
		//2.定义检索区域检索框 注意：字段名称必须是实际存在的字段名称，
		//CNName中文标示这个检索字段的含义，type标示检索框的类型
		Search search = new Search();
		search.setFieldNames(new String[]{"name","status","remark","add_time","testM"});
		search.setCNNames(new String[]{"名称","是否有效","备注","时间","测试"});
		search.setTypes(new String[]{"text","select","select","date","select"});
		//3.定义下拉数据源 如果检索区域中存在select，必须定义下拉数据源
		//注意：这里的资源必须和表头字段中的一致，可以定义多个
		Map<String,List<Map>> clListMap = new HashMap<String, List<Map>>();
		clListMap.put("status", OptionUtil.getOptionListByCodeLibrary("YesNo",true,null));
		clListMap.put("remark", OptionUtil.getOptionListByOther("id", "name", "loan_business_category", null,null));
		clListMap.put("testM", OptionUtil.getOptionListByManual(new String[]{"shang","xia","wan"}, new String[]{"上午","下午","晚上"}));
		/*clListMap.put("status", ToCodeLibrary.getCodeLibrariesBySQL("YesNo",true,null));*/
		search.setOptionListMap(clListMap);
		
		DyResponse response = null;
		
		try {
			//4.构造页面结构并返回，注意：第一个参数标示请求列表数据时候的url，必须是存在的
			//第二个参数是这个列表数据的名称，如果页面中存在这个导出功能，这个名称就是导出的
			//excel文件的文件名称
			response = PageUtil.createTablePageStructure("/test/data", "测试列表数据", tableHeader, 
					search,super.getLastPara()==null?"41":getLastPara().toString(),(Map<Integer, SysMenu>)this.getSessionAttr("menus"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//5.返回数据到页面 response不可改变,页面名称也不可改变
		this.setAttr("response", response);
		this.render("common/table.html");
	}
	/**
	 * 自动组装sql，数据（单表） 
	 * @author masai
	 * @time 2017年5月26日 下午3:42:32
	 */
   public void data(){
	   //1.从页面获取参数 condition参数名称固定 key=condition 格式为：name:ms,age:24
	   //获取分页参数	固定格式 key=page
	   String conditation = this.getPara("condition");
	   String page = this.getPara("page");
	   System.out.println("查询条件："+conditation);
	   //2.列表数据sql	特别注意：如果需要如果需要转化成为字典值调用getCodeItemName()数据库函数，
	   //字典值别名一律为	cn_原名称
	  /*StringBuffer sql = new StringBuffer("select *, getCodeItemName('YesNo',status) "
	   		+ "as cn_status from loan_product_category where 1=1 ");*/
	   //3.获取参数Map，处理字符串拼接
	   Map whereMap = super.getWhereMap(conditation);
	   //定义查询对象
	   QueryItem item = new QueryItem();
	   //设置表名（多表用逗号间隔）
	   item.setTableNames("loan_product_category");
	   //设置查询字段（可以使用函数包括聚合函数）
	   item.setFields("*, getCodeItemName('YesNo',status) as cn_status");
	   //设置分页步长  如果不设置 或者 值<=0，则会默认歩长为20
	   item.setLimit(10);
	   //处理字段反射(注意格式)
	   item.setFieldsReflect("repay_type:loan_repay_type,id,name;business_id:loan_business_category,id,name");
	   //注意：此处whereMap在页面初始化的时候为null，此处有必要判空
	   List<QueryWhere> whereList = new ArrayList<QueryWhere>();
	   //检索条件
	   if(whereMap != null){
		   whereList.add(new QueryWhere("name", LIKE_RIGHT ,whereMap.get("name")));
		   whereList.add(new QueryWhere("status", whereMap.get("status")));
	   }
	   //默认追加条件
	   //whereList.add(new QueryWhere("2","2"));	//注意：jfinal仅仅支持一个常量条件，超过一个会报错
	   whereList.add(QueryWhere.isNotNull("remark"));
	   whereList.add(new QueryWhere("business_id", EQ, "4", OR));
	   item.setWhereList(whereList);
	   //4.获取数据  格式为List<Record>
	   //DyResponse dyResponse = super.getTableData(item , page);
	   DyResponse dyResponse = super.getTableData(item , page);
	   //5.返回数据到页面	response 固定值 不可改变
	   //this.setAttr("response", dyResponse);
	   
	   renderJson(dyResponse);
   }
   /**
    * 手动组装sql，构造页面（多表） 
    * @author masai
    * @time 2017年5月26日 下午3:43:30
    */
   public void handPage(){
	   TableHeader tableHeader = new TableHeader();
	   tableHeader.setFieldNames(new String[]{"su_id","su_name","sr_role_name","so_org_name"});
	   tableHeader.setCNNames(new String []{"用户编号","用户名称","角色名称","所属机构名称"});
	   tableHeader.setMultiple(true);
	   DyResponse response = null;
		try {
			//4.构造页面结构并返回，注意：第一个参数标示请求列表数据时候的url，必须是存在的
			//第二个参数是这个列表数据的名称，如果页面中存在这个导出功能，这个名称就是导出的
			//excel文件的文件名称
			response = PageUtil.createTablePageStructure("/test/handData", "测试手动组装列表数据", tableHeader, 
					null,super.getLastPara()==null?"41":getLastPara().toString(),(Map<Integer, SysMenu>)this.getSessionAttr("menus"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//5.返回数据到页面 response不可改变,页面名称也不可改变
		this.setAttr("response", response);
		this.render("common/table.html");
   }
   /**
    * 手动组装sql，数据（多表） 
    * @author masai
    * @time 2017年5月26日 下午3:43:54
    */
   public void handData(){
	  String sql = "select su.id as su_id,su.name as su_name,sr.role_name as sr_role_name,so.org_name as so_org_name "
		  		+ " from "
		  		+ " sys_user su,sys_org so,sys_role sr"
		  		+ " where "
		  		+ " su.role_id=sr.id and su.belong_org_id=so.org_id";
	  //检索条件自己拼接，多表较为复杂慎用，且多表时分页默认是20条
	  DyResponse dyResponse = super.getTableData(sql , this.getPara("page"));
	  renderJson(dyResponse);
   }
   
   public void formTest(){
	   List<FormField> formFieldGroup=new ArrayList<FormField>();
	   formFieldGroup.add(new FormField("name","名字","text",""));
	   formFieldGroup.add(new FormField("date","date","date",""));
	   formFieldGroup.add(new FormField("f","文件","file",""));
	   formFieldGroup.add(FormField.createFormField("cnt", "计数", "text", "", true, "digital"));
	   formFieldGroup.add(new FormField("ck","checkbox","checkbox","checkbox1"));
	   formFieldGroup.add(new FormField("ck","checkbox","checkbox","checkbox2"));
	   formFieldGroup.add(new FormField("textarea","ar","textarea","1\n2\n泽民"));
	   formFieldGroup.add(FormField.createFormField("名字","name2","text","",null,true) );
	   formFieldGroup.addAll(FormBuilder.formBuilderTest());
	   DyResponse response = PageUtil.createFormPageStructure("表单测试", formFieldGroup,  "/test/testReceive");
       this.setAttr("response", response);
       this.render("common/form.html");
   }
   public void testReceive() {
	   HttpServletRequest  req=getRequest();
	   Enumeration<String> es=getRequest().getHeaderNames();
	   Map<String, String[]> map=getParaMap();
	   System.out.println(map);
	   //renderJson(map);
	   try{List<UploadFile> lstFiles=getFiles();}
	   catch(Exception e){e.printStackTrace();}
	   map=getParaMap();
	   System.out.println(map);
	   renderJson(map);
   }
   public void mm() throws Exception{
	   String aa = this.getPara("aa", "aa");
	   FlowService fs = new FlowServiceImpl();
	   System.out.println("3333333");
	   System.out.println("11111");
	   //fs.flowInit("aa");
	   System.out.println("22222");
	   
	   List<Map> resLsit = Db.query("select * from sys_user");
	   //List<Rec> rs = baseModel.baseQuery("select * from sys_user", null);
	   System.out.println("ddddddddd");
	   
	   List<HashMap> c = baseModel.baseFind(HashMap.class, "select * from sys_user where id<2", null);
	   Map a = c.get(0);
	   String e = a.get("id").toString();
	   
	   List<Record> f = baseModel.baseFind(null,"select * from sys_user where id<2",null);
	   Record g = f.get(0);
	   String h = g.getStr("id");
	   
	   System.out.println("aaaaaaa");
   }
}
