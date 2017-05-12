package com.dlcat.core.controller.system;

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
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysRole;
import com.dlcat.core.model.ToCodeLibrary;
import com.jfinal.kit.HashKit;
import com.jfinal.plugin.activerecord.Db;

/**
 * 角色管理
 * @ClassName RoleManageController
 * @author liuran
 * @time 2017年5月11日 下午5:51:08
 */
public class RoleManageController extends BaseController {
		public void index(){
			TableHeader tableHeader = new TableHeader();
			tableHeader.setFieldNames(new String[]{"id","role_name","cn_status"});
			tableHeader.setCNNames(new String []{"角色编号","角色名称","状态"});
			
			Search search = new Search();
			search.setFieldNames(new String[]{"id","status"});
			search.setCNNames(new String[]{"角色编号","状态"});
			search.setTypes(new String[]{"text","select"});
			
			Map<String,List<ToCodeLibrary>> clListMap = new HashMap<String, List<ToCodeLibrary>>();
			clListMap.put("status", ToCodeLibrary.getCodeLibrariesBySQL("YesNo",true,null));
			search.setOptionListMap(clListMap);
			DyResponse response = null;
			
			try {
				//4.构造页面结构并返回，注意：第一个参数标示请求列表数据时候的url，必须是存在的
				//第二个参数是这个列表数据的名称，如果页面中存在这个导出功能，这个名称就是导出的
				//excel文件的文件名称
				response = PageUtil.createTablePageStructure("/role/data", "角色管理数据", tableHeader, 
				search,super.getLastPara().toString(),(Map<Integer, SysMenu>)this.getSessionAttr("menus"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			//5.返回数据到页面 response不可改变,页面名称也不可改变
			this.setAttr("response", response);
			this.render("table.html");
		}
		
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
			   item.setTableNames("sys_role");
			   //设置查询字段（可以使用函数包括聚合函数）
			   item.setFields("*, getCodeItemName('YesNo',status) as cn_status");
			   //设置分页步长  如果不设置 或者 值<=0，则会默认歩长为20
			   item.setLimit(10);
			   //注意：此处whereMap在页面初始化的时候为null，此处有必要判空
			   List<QueryWhere> whereList = new ArrayList<QueryWhere>();
			   //检索条件
			   if(whereMap != null){
				   whereList.add(new QueryWhere("id", whereMap.get("id")));
				   whereList.add(new QueryWhere("status", whereMap.get("status")));
			   }
			   	   
			   item.setWhereList(whereList);		   
			   DyResponse dyResponse = super.getTableData(item , page);		   
			   this.setAttr("response", dyResponse);
			   
			   renderJson();
		   }
		  
		//新增或编辑进入
			public void form(){
				String id = getPara(0);
				if(id != null){
					setAttr("role", SysRole.dao.findById(id));
				}
				render("");
			
			}
			//提交 新增
			public void add(){
				SysRole role = getModel(SysRole.class,"role");
				role.save();
				redirect("");
				
			}
			
			//提交 修改
			public void update(){
				SysRole role = getModel(SysRole.class,"role");
				role.update();
				redirect("");
			}
			
			//进入编辑页面
			public void edit() {
				form();
			}
			
			//删除
			public void del(){
				SysRole.dao.deleteById(getPara(0));
				redirect("");
			}
			
			//分配权限
			public void assign(){
				//转到分配权限的页面或表单
			}
			//权限更新
			public void assignUpdate(){
				//从前台传id 和 menu id 集合
				int id = getParaToInt(0);
				List<String> menuId = new ArrayList<String>();
				String roleMenus = "(";
				for (int i = 0; i < menuId.size(); i++) {
					roleMenus = roleMenus + menuId.get(i)+",";
					if (i == menuId.size()-1) {
						roleMenus = roleMenus +")";
					}
				}
				
				Db.update("update sys_role set role_menus = ?  where id=?",roleMenus,id);
				
			}
}
