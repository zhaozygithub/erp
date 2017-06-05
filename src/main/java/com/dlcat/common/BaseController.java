package com.dlcat.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.shiro.codec.Base64;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.QueryItem;
import com.dlcat.common.utils.JsonUtils;
import com.dlcat.common.utils.StringUtils;
import com.dlcat.core.model.SysAdminLog;
import com.dlcat.core.model.SysUser;
import com.jfinal.core.Controller;
import com.jfinal.kit.JMap;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.TableMapping;

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
	* @author:zhaozhongyuan 
	* @Description:
	* @return void   
	* @date 2017年6月5日 下午7:32:05  
	*/
	public void SetAdminLog(String btnID, String data) {
		SysUser user=getCurrentUser();
		SysAdminLog.SetAdminLog(user, btnID, data, getRequest());
	}
	
	
	/**
	 * 请求列表数据（自动组装sql）
	 * @param item	请求的sql语句
	 * @param page	分页数
	 * @return
	 * @author masai
	 * @time 2017年5月9日 上午10:21:17
	 */
	public DyResponse getTableData(QueryItem item,String page){
		Map<String,Object> resultData = new HashMap<String, Object>();
		try {
			 if(item == null){
				throw new Exception("数据查询出错");
			 }
			 //分页步长重新赋值
			 item.setLimit((item.getLimit()==null || item.getLimit()<=0) ? 20 : item.getLimit());
			//获取页面数
			 int p = StringUtils.isBlank(page) ? 1 : Integer.parseInt(page);
			 String queryItemSql = QueryItem.getQuerySql(item).toLowerCase();
			 String queryCountSql = QueryItem.getQueryTotalCountSql(item).toLowerCase();
			
			 //获取sql语句，并查询总数
			 Long dataCount = baseModel.baseFindFrist(queryCountSql).getLong("count");
			 //获取sql语句，并查询数据
			 List<Map> res = baseModel.baseFind(Map.class, queryItemSql, new Object[]{(p-1)*item.getLimit()});
			 //返回数据
			 resultData.put("dataCount", dataCount); //本次请求总条数
			 //处理字段反射
			 resultData.put("dataList", handleFieldReflect(item.getFieldsReflect(), res));        //列表数据
			//进行简单的加密
			 resultData.put("queryItem", Base64.encodeToString(JsonUtils.object2JsonNoEscaping(item).getBytes()));   //查询Item转化成json返回到页面（配合导出）
			 resultData.put("page", item.getLimit());//分页步长
		} catch (Exception e) {
			e.printStackTrace();
			//请求不正常的时候description为错误信息
			return createErrorJsonResonse("请求数据出错");
		}
		return createSuccessJsonResonse(resultData);
	}
	/**
	 * 处理列表数据反射
	 * 注意：fieldReflect 必须按照规定的格式来传参数，否则会出错，见QueryItem.fieldsReflect用法
	 * @param fieldReflect	反射参数列表
	 * @param dataList		带发射数据集合
	 * @author masai
	 * @time 2017年5月29日 上午11:18:20
	 */
	public List<Map> handleFieldReflect(String fieldsReflect , List<Map> dataList){
		try {
			if(dataList != null){
				 if(StringUtils.isNotBlank(fieldsReflect)){
					 fieldsReflect = fieldsReflect.trim();
					 //所有字段映射map集合
					 Map<String, Map<String, Object>> reflectMap = new HashMap<String, Map<String, Object>>();
					 //单个字段映射结果map集合
					 Map<String , Object> resultMap = null;
					 String[] allReflect = fieldsReflect.split(";");
					 List<String> listSql = new ArrayList<String>();
					 StringBuffer sb = null;
					 for(String temp : allReflect){
						 temp = temp.trim();
						 String[] singleFieldReflect = temp.split(":");
						 String reflectKey = singleFieldReflect[0].trim();
						 String[] tableInfo = singleFieldReflect[1].trim().split(",");//获取反射的单个字段的数据源，包括 表名，key,keyField
						 //拼接反射查询字符串
						 sb = new StringBuffer(" select ");
						 sb.append(" "+tableInfo[1].trim()+" ");
						 sb.append(" , ");
						 sb.append(" "+tableInfo[2].trim()+" ");
						 sb.append(" from ");
						 sb.append(" "+tableInfo[0].trim()+" ");
						 //将查询到的反射值转为map类型， 反射结果分别作为key和value
						 List<Map> mapList = this.baseModel.baseFind(Map.class , sb.toString() , null);
						 resultMap = new HashMap<String, Object>();
						 for(Map m : mapList){//, 
							 resultMap.put(m.get(tableInfo[1].trim()).toString() ,m.get(tableInfo[2].trim()));//将查询的两个字段值分别作为key和value
						 }
						 reflectMap.put(reflectKey, resultMap);
					 }
					 //处理字段发射
					 for(String key : reflectMap.keySet()){
						 Map fieldRef = reflectMap.get(key);
						 for(Map m : dataList){
							 Object value = fieldRef.get(m.get(key).toString());
							 if(m.containsKey(key) && value != null){
								 m.put(key, value);
							 }
						 }
					 }
				 }
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataList;
	}
	/**
	 * 请求列表数据（手动组装sql）（默认分页歩长20，无需手动加）
	 * @param sql	查询sql,自己拼接完整sql
	 * @param page	当前页，默认为1
	 * @return
	 * @author masai
	 * @time 2017年5月18日 下午7:29:12
	 */
	 public DyResponse getTableData(String sql,String page){
		String queryCountSql  = "";
		 Map<String,Object> resultData = new HashMap<String, Object>();
		try {
			if(StringUtils.isNotBlank(sql)){
				//无论sql给出的是分页歩长是多少，默认是20
				sql = sql.split("limit")[0] + " limit ?,20";
				sql = sql.trim().toLowerCase();//去两端空格，并转为小写
				int currentPage = StringUtils.isNotBlank(page) ? Integer.valueOf(page) : 1 ;
				if(sql.startsWith("select") && sql.indexOf("from") >= 0){//判断sql语句是否以select开头，是否包含from
					String tempSql = sql.split("limit")[0];
					int indexOf = tempSql.indexOf("from");
					queryCountSql = "select count(*) as count " + tempSql.substring(indexOf, tempSql.length());
					 //获取sql语句，并查询总数
					 Long dataCount = baseModel.baseFindFrist(queryCountSql).getLong("count");
					 //获取sql语句，并查询数据
					 List<Record> res = baseModel.baseFind(sql, new Object[]{(currentPage-1)*20});
					 //返回数据
					 resultData.put("dataCount", dataCount); //本次请求总条数
					 resultData.put("dataList", res);        //数据
					 resultData.put("querySql", Base64.encodeToString(sql.getBytes())); //Base64加密
					 resultData.put("page", "20");//分页步长
				}else{
					return createErrorJsonResonse("无效请求语句");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			//请求不正常的时候description为错误信息
			return createErrorJsonResonse("请求数据出错");
		}
		return createSuccessJsonResonse(resultData);
	}
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
	 * 获取当前登录用户
	 * @return
	 * @author masai
	 * @time 2017年5月25日 下午6:48:29
	 */
	protected SysUser getCurrentUser() {
		SysUser sysUser=getSessionAttr("user");
		return sysUser;
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
	
	/**
	 * 非空校验
	 * @param obj 要做校验的数据 
	 * @param texts 提示信息
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String validateNull(Object[] obj, String[] texts) {
		//校验参数
		if(obj == null || texts == null || texts.length <= 0 || obj.length != texts.length){
			return null;
		}
		String errorMsg = null;
		for(int i=0;i<obj.length;i++){
			Object value = obj[i];
			boolean isNull = false;
			if(value != null){
				if(value instanceof String){
					isNull = StringUtils.isBlank(value.toString()) ? true : false;
				}
			}else{
				isNull = true;
			}
			if(isNull){
				errorMsg = texts[i]+"不能为空";
				break;
			}
		}
		return errorMsg;
	}
	/**
	 * 响应信息
	 * @return
	 * @author masai
	 * @time 2017年5月25日 下午8:41:20
	 */
	public DyResponse createSuccessJsonResonse() {
		return createSuccessJsonResonse(null);
	}
	/**
	 * 响应信息
	 * @param data
	 * @return
	 * @author masai
	 * @time 2017年5月23日 下午6:01:07
	 */
	public DyResponse createSuccessJsonResonse(Object data) {
		return createSuccessJsonResonse(data, "OK");
	}
	/**
	 * 响应信息
	 * @param data
	 * @param description
	 * @return
	 * @author masai
	 * @time 2017年5月23日 下午6:02:17
	 */
	public DyResponse createSuccessJsonResonse(Object data, Object description) {
		DyResponse response = new DyResponse();
		response.setStatus(200);
		response.setDescription(description);
		response.setData(data);
		return response;
	}
	/**
	 * 响应错误信息
	 * @param errorMsg
	 * @return
	 * @author masai
	 * @time 2017年5月23日 下午6:02:24
	 */
	public DyResponse createErrorJsonResonse(Object errorMsg) {
		DyResponse response = new DyResponse();
		response.setStatus(100);
		response.setDescription(errorMsg);
		return response;
	}
	
}
