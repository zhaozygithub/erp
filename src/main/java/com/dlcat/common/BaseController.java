package com.dlcat.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.QueryItem;
import com.dlcat.common.utils.JsonUtils;
import com.dlcat.common.utils.StringUtils;
import com.google.gson.Gson;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;

/**
 * controller基础类
 * @author masai
 * @time 2017年4月16日 下午3:07:38
 */
public class BaseController extends Controller {
	//创建数据库操作引擎
	public static BaseModel<?> baseModel = new BaseModel();
	
	public static final String EQ = "=";
	public static final String GT = ">";
	public static final String GE = ">=";
	public static final String LT = "<";
	public static final String LE = "<=";
	public static final String NEQ = "<>";
	public static final String IN = "in";
	public static final String NOT_IN = "not in";
	public static final String LIKE_LEFT = "like_left";
	public static final String LIKE_RIGHT = "like_right";
	public static final String LIKE_ALL = "like_all";
	public static final String NULL = "is null";	//注意：此处nulL和空值不同
	public static final String NOT_NULL = "is not null";
	
	/**
	 * 请求列表数据
	 * @param sql	请求的sql语句
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
			 //获取查询sql
			 String querySql = QueryItem.getQuerySql(item);
			 String test = QueryItem.getQueryTotalCountSql(item);
			 //获取查询总数
			 Long dataCount = baseModel.baseFindFrist(QueryItem.getQueryTotalCountSql(item)).getLong("count");
			 //查询数据
			 List<Record> res = baseModel.baseFind(querySql, new Object[]{(p-1)*item.getLimit()});
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
}
