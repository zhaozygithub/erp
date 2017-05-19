package com.dlcat.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.QueryItem;
import com.dlcat.common.utils.JsonUtils;
import com.dlcat.common.utils.StringUtils;
import com.dlcat.core.model.SysMenu;
import com.dlcat.core.model.SysUser;
import com.google.gson.Gson;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.JMap;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * controller基础类
 * @author masai
 * @time 2017年4月16日 下午3:07:38
 */
public class BaseController extends Controller {
	 //接收列表选择的参数
	private static String[] idarrays;
	//创建数据库操作引擎
	public static BaseModel<?> baseModel = new BaseModel();
	
	public static final String EQ = "=";
	public static final String GT = ">";
	public static final String GE = ">=";
	public static final String LT = "<";
	public static final String LE = "<=";
	public static final String NEQ = "<>";
	public static final String IN = "in";	//注意：in条件和not in条件的值两侧必须拼接上括号
	public static final String NOT_IN = "not_in";
	public static final String AND = "and";
	public static final String OR = "or";
	public static final String LIKE_LEFT = "like_left";
	public static final String LIKE_RIGHT = "like_right";
	public static final String LIKE_ALL = "like_all";
	public static final String NULL = "is_null";	//注意：此处nulL和空值不同
	public static final String NOT_NULL = "is_not_null";
	
	/**
	 * 请求列表数据（自动组装sql）
	 * @param item	请求的sql语句
	 * @param page	分页数
	 * @return
	 * @author masai
	 * @time 2017年5月9日 上午10:21:17
	 */
	public DyResponse getTableData(QueryItem item,String page){
		DyResponse dyResponse = new DyResponse();
		try {
			 //分页步长重新赋值
			 item.setLimit((item.getLimit()==null || item.getLimit()<=0) ? 20 : item.getLimit());
			//获取页面数
			 int p = StringUtils.isBlank(page) ? 1 : Integer.parseInt(page);
			 String queryItemSql = QueryItem.getQuerySql(item);
			 String queryCountSql = QueryItem.getQueryTotalCountSql(item);
			
			 //获取sql语句，并查询总数
			 Long dataCount = baseModel.baseFindFrist(queryCountSql).getLong("count");
			 //获取sql语句，并查询数据
			 List<Record> res = baseModel.baseFind(queryItemSql, new Object[]{(p-1)*item.getLimit()});
			 //返回数据
			 Map<String,Object> resultData = new HashMap<String, Object>();
			 resultData.put("dataCount", dataCount); //本次请求总条数
			 resultData.put("dataList", res);        //数据
			 resultData.put("queryItem", JsonUtils.object2JsonNoEscaping(item));   //查询Item转化成json返回到页面（配合导出）
			 resultData.put("page", item.getLimit());//分页步长
			 //QueryItem qi = JsonUtils.fromJson(a, QueryItem.class);
			 dyResponse.setData(resultData);
			 dyResponse.setDescription("OK");
			 dyResponse.setStatus(DyResponse.OK);
		} catch (Exception e) {
			e.printStackTrace();
			//请求不正常的时候description为错误信息
			dyResponse.setDescription("请求数据出错");
			dyResponse.setStatus(DyResponse.ERROR);
		}
		return dyResponse;
	}
	/**
	 * 请求列表数据（手动拼接sql）
	 * @param sql	查询sql,自己拼接完整sql
	 * @return
	 * @author masai
	 * @time 2017年5月18日 下午7:29:12
	 */
	/*public DyResponse getTableData(String sql){
		DyResponse dyResponse = new DyResponse();
		String queryCountSql  = "";
		try {
			if(StringUtils.isNotBlank(sql)){
				sql = sql.trim();
				if(sql.startsWith("select") && sql.indexOf("from") >= 0){//判断sql语句是否以select开头，是否包含from
					String tempSql = sql.split("limit")[0];
					int indexOf = tempSql.indexOf("from");
					queryCountSql = "select count(*) as count " + tempSql.substring(indexOf, tempSql.length());
					
					 //获取sql语句，并查询总数
					 Long dataCount = baseModel.baseFindFrist(queryCountSql).getLong("count");
					 //获取sql语句，并查询数据
					 List<Record> res = baseModel.baseFind(sql, null);
					 //返回数据
					 Map<String,Object> resultData = new HashMap<String, Object>();
					 resultData.put("dataCount", dataCount); //本次请求总条数
					 resultData.put("dataList", res);        //数据
					 resultData.put("queryItem", JsonUtils.object2JsonNoEscaping(item));   //查询Item转化成json返回到页面（配合导出）
					 resultData.put("page", item.getLimit());//分页步长
					 //QueryItem qi = JsonUtils.fromJson(a, QueryItem.class);
					 dyResponse.setData(resultData);
					 dyResponse.setDescription("OK");
				}else{
					return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			//请求不正常的时候description为错误信息
			dyResponse.setDescription("请求数据出错");
			dyResponse.setStatus(DyResponse.ERROR);
		}
		
		return null;
	}*/
	/**
	 * 获取列表where条件
	 * @param para	参数字符串  ps：name:1,age:20
	 * @return
	 * @author masai
	 * @time 2017年5月9日 上午10:31:59
	 */
	public Map<String,String> getWhereMap(String para){
		Map<String,String> whereMap = null; 
		if(StringUtils.isNotBlank(para)){
			//正则表达式替换开头结尾存在的逗号和多个相连的逗号, 已考虑空格之类的空白区
			 para=para.replaceAll("(^\\s*?,|,\\s*?$)", "").replaceAll("(\\s*,)+", ",");
		   whereMap = new HashMap<String, String>();
		   String[] arrStr = para.split(",");
		   for(String str :  arrStr){
			   String[] subArrsStr = str.split(":");
			 //正则表达式替换除开头和结尾的单引号外的单双引号为转义引号
			   whereMap.put(subArrsStr[0], "'"+subArrsStr[1].substring(1,
					   subArrsStr[1].length()-1).replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"")+"'" );		   }
	     }
		return whereMap;
	}
	/**
	 * 获取url拼接参数中最后一个参数
	 * @return
	 * @author masai
	 * @time 2017年4月16日 下午5:02:44
	 */
	public Object getLastPara(){
		//获取系统设置的参数分隔符
		Object fildValue = getSupperClassFild("URL_PARA_SEPARATOR");
		String paraAll = this.getPara();
		Object result = null;
		if(paraAll != null){
			String[]resValue = paraAll.split(fildValue.toString());
			result = resValue[resValue.length-1];
		}
		return result;
	}
	
	/**
	 * 获取系统设置的参数分隔符
	 * 获取祖先类中的所有属性值，忽略修饰符 private,protect,public
	 * @param filedName	父类中的属性名称
	 * @return
	 * @author masai
	 * @time 2017年4月16日 下午6:16:57
	 */
	private Object getSupperClassFild(String filedName){
	   Field field = null;
	   Class<?> clazz = this.getClass();
       for(; clazz != Object.class ; clazz = clazz.getSuperclass()) {  
           try {
               field = clazz.getDeclaredField(filedName);  
           } catch (Exception e) {
               //这里甚么都不能抛出去。  
               //如果这里的异常打印或者往外抛，则就不会进入
           }
       }
       //抑制Java对其的检查  
       field.setAccessible(true);
       
       try {
		   return field.get(filedName);
       } catch (Exception e) {
       }
       return null;
	}
	
	/**
	* @author:zhaozhongyuan 
	* @Description:获取当前用户所属的org_id
	* @return int   
	* @date 2017年5月15日 下午3:01:03  
	*/
	protected int getCurrentUserBelongID() {
		SysUser sysUser=getSessionAttr("user");
		return sysUser.getInt("belong_org_id");
	}
	
	/**
	* @author:zhaozhongyuan 
	* @Description:仅能更新单表，单主键的表
	* ids:要更新的主键数组，
	* Map:字段name，value
	* @return List<Record>   
	* @date 2017年5月15日 下午3:03:28  
	*/
	@SuppressWarnings("rawtypes")
	protected void updateByIds(Class<? extends Model> modelClass,String[] ids,Map<String, Object> map) {
		//获取主键
		String[] primaryKey=TableMapping.me().getTable(modelClass).getPrimaryKey();
		String tableName=TableMapping.me().getTable(modelClass).getName();
		if (primaryKey.length!=1) {
			return;
		}
		
		List<Record> recordList=new ArrayList<Record>();
		for (int i = 0; i < ids.length; i++) {
			Record record=new Record();
			record.set(primaryKey[0],ids[i]);
			Set<String> keys=map.keySet();
			for (String key : keys) {
				record.set(key, map.get(key).toString());
			}
            recordList.add(record);			
		}
		//更新数据
		try {
			Db.batchUpdate(tableName, recordList, ids.length);
			renderText("操作成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderText("操作失败");
		}
	}
	
	/**
	* @author:zhaozhongyuan 
	* @Description:获取字段中文名和字段名
	* @return Map<String, String>   
	* @date 2017年5月17日 下午4:10:18  
	*/
	public  Map<String, String> getTrueFields(String[] arr,String[] cnNames) {
		Map<String, String> map=new HashMap<String, String>();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].startsWith("cn_")) {
				map.put(cnNames[i], arr[i].substring(3, arr[i].length()));
			}else {
				map.put(cnNames[i], arr[i]);
			}
		}
		
		return map;
		
	}
	public static String[] getIdarrays() {
		return BaseController.idarrays;
	}
	public static void setIdarrays(String[] idarrays) {
		BaseController.idarrays = idarrays;
	}
	

	
}
