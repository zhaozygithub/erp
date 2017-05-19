package com.dlcat.core.controller.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.relation.Role;

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
import com.dlcat.core.model.CuProperty;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysRole;
import com.dlcat.core.model.SysUser;
import com.dlcat.core.model.ToCodeLibrary;
import com.jfinal.aop.Before;
import com.jfinal.kit.HashKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;

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
			//多选框
			tableHeader.setMultiple(true);
			
			Search search = new Search();
			search.setFieldNames(new String[]{"id","status"});
			search.setCNNames(new String[]{"角色编号","状态"});
			search.setTypes(new String[]{"text","select"});
			
			Map<String,List<Map>> clListMap = new HashMap<String, List<Map>>();
			clListMap.put("status", OptionUtil.getOptionListByCodeLibrary("YesNo",true,null));
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
			this.render("common/table.html");
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
//			   item.setLimit(10);
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
		  
		  /**
			 * 构建表单页面，增改查
			 * @author liuran
			 * @time 2017年5月16日 下午5:24:47 void
			 */
			 public void form() {
					String type = getPara(0);
					String id=getPara("id");
					List<FormField> formFieldList = new ArrayList<FormField>();

					formFieldList.add(new FormField("id", "", "hidden"));
					formFieldList.add(new FormField("role_name", "角色名称", "text"));
					formFieldList.add(new FormField("status", "是否可用", "select","",OptionUtil.getOptionListByCodeLibrary("YesNo", true, "")));
//					formFieldList.add(new FormField("remark", "备注", "text"));
					
					
					DyResponse response = null;
					
					if (type.equals("add")) {
						response = PageUtil.createFormPageStructure("角色添加", formFieldList, "/role/toAdd");
					} else if (type.equals("edit")) {
						if (id.equals("")) {
							renderText("请选择一条记录来编辑！");
							return;
						}
						response = PageUtil.createFormPageStructure("角色编辑", formFieldList, "/role/toEdit");
					}else if (type.equals("detail")) {
						if (id.equals("")) {
							renderText("请选择一条记录来查看！");
							return;
						}
						//第三个参数 由于校验非空，所以要随便写点什么即可
						response = PageUtil.createFormPageStructure("查看详细信息", formFieldList, "/detail");
					}
					this.setAttr("response", response);
					this.render("common/form_editarea.html");
				}

				public void toAdd() {
					SysUser sysUser = getSessionAttr("user");
//					String customerId = getPara("cu_id");
					SysRole role = getModel(SysRole.class, "");
//					cuProperty.set("id", "cpr" + DateUtil.getCurrentTime());
					role.set("input_user_id", sysUser.getInt("id"));
					role.set("input_user_name", sysUser.getStr("name"));
					role.set("input_org_id", sysUser.getInt("belong_org_id"));
					role.set("input_org_name", sysUser.getStr("belong_org_name"));
					role.set("input_time", DateUtil.getCurrentTime());

					try {
						role.save();
						renderHtml("<h1>操作成功！！</h1>");
					} catch (Exception e) {
						renderHtml("<h1>操作失败！！</h1>");
					}
				}
				
				/**
				 * 删除
				 * @author liuran
				 * @time 2017年5月16日 下午7:01:32 void
				 */
				@Before(Tx.class)
				public void del() {
					String[] ids = getPara("id").toString().split(",");
					if (ids[0].equals("")) {
						renderText("请选择至少一条记录！！！");
						return;
					}
					// 批量删除
					SysRole role = new SysRole();

					try {
						for (String id : ids) {
							role.deleteById(id);
						}
						renderHtml("<h1>操作成功！！</h1>");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						renderHtml("<h1>操作失败！！</h1>");
					}
				}
				/**
				 * 编辑
				 * @author liuran
				 * @time 2017年5月18日 下午1:42:59 void
				 */
				public void toEdit() {
					SysRole role = getModel(SysRole.class, "");
					try {
						role.update();
						renderText("操作成功");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						renderText("操作失败");
					}
				}
			
			//分配权限
			public void assign(){
				//转到分配权限的页面或表单
			}
			//权限更新
			public void assignUpdate(){
				//从前台传id 和 menu id 集合
//				int id = getParaToInt(0);
//				List<String> menuId = new ArrayList<String>();
//				String roleMenus = "";
//				for (int i = 0; i < menuId.size(); i++) {
//					roleMenus = roleMenus + menuId.get(i)+",";
//				}
//				
//				Db.update("update sys_role set role_menus = ?  where id=?",roleMenus,id);
				
			}
}
