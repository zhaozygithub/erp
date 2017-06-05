package com.dlcat.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import com.dlcat.common.utils.StringUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.TableMapping;

/** 
* @author zhaozhongyuan
* @date 2017年4月25日 上午9:18:21 
* @Description: 添加所有model的一些常用方法
* @param <M> 
*/
public class BaseModel<M extends Model<M>> extends Model<M> implements IBean {
	private static final long serialVersionUID = 1L;
	
	
	
	/**
	 * 根据sql查询第一条记录	Db方式
	 * @param sql	sql语句
	 * @param paras	参数列表
	 * @return	Record	返回值
	 * @author masai
	 * @time 2017年5月2日 下午7:51:54
	 */
	public Record baseFindFrist(String sql , Object[] paras){
		List<Record> recordList = baseFind(sql, paras);
		return (recordList != null && recordList.size()>0) ? recordList.get(0) : null;
	}
	/**
	 * 根据sql查询第一条记录	Db方式
	 * @param sql	sql语句
	 * @return	Record	返回值
	 * @author masai
	 * @time 2017年5月2日 下午7:51:54
	 */
	public Record baseFindFrist(String sql){
		List<Record> recordList = baseFind(sql, null);
		return (recordList != null && recordList.size()>0) ? recordList.get(0) : null;
	}
	/**
	 * 根据sql语句查询 Db方式
	 * @param sql	查询sql
	 * @param paras		参数列表
	 * @return	List<Record> 	返回值
	 * @author masai
	 * @time 2017年5月2日 下午7:50:36
	 */
	public List<Record> baseFind(String sql , Object[] paras){
		List<Record> recordList = null;
		if(StringUtils.isNotBlank(sql)){
			if(paras == null){
				recordList = Db.find(sql);
			}else{
				recordList = Db.find(sql, paras);
			}
		}else{
			try {
				throw new Exception("参数错误");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return recordList;
	}
	/**
	 * 根据sql语句查询 Db方式
	 * @param clazz	此处仅仅支持Map和Record，传空则默认为Record，其他类型clazz则会报错
	 * @param sql
	 * @param paras
	 * @return
	 * @author masai
	 * @time 2017年5月11日 下午8:32:31
	 */
	public <T> List<T> baseFind(Class<T> clazz , String sql , Object[] paras){
		List<Record> resultList = baseFind(sql , paras);
		if(resultList != null){
			if(clazz == null || clazz == Record.class){
				return (List<T>) resultList;
			}
			else if(clazz == Map.class || clazz == HashMap.class || clazz == Hashtable.class){
				List<Map> mapList = new ArrayList<Map>();
				for(Record rc : resultList){
					mapList.add(rc.getColumns());
				}
				return (List<T>) mapList;
			}else{
				try {
					throw new Exception("类型转化错误");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	/**
	 * 根据sql语句查询 Db方式
	 * @param sql	sql语句
	 * @param paras		参数列表 如果为null则表示无参数
	 * @return
	 * @author masai
	 * @time 2017年4月28日 上午11:06:09
	 */
	public  List<Object> baseQuery(String sql , Object[] paras){
		List<Object> objects = null;
		if(StringUtils.isNotBlank(sql)){
			try {
				if(paras == null){
					objects = Db.query(sql);	
				}else{
					objects = Db.query(sql, paras);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return  objects;
	}
	/**
	 * 根据sql语句查询第一条记录
	 * @param sql	sql语句
	 * @param paras		参数列表 如果为null表示无参数
	 * @return
	 * @author masai
	 * @time 2017年4月28日 上午11:07:15
	 */
	public Object  baseQueryFrist(String sql , Object[] paras){
		List<Object> objects = baseQuery(sql , paras);
		return (objects != null && objects.size()>0) ? objects.get(0) : null;
	}
	/**
	 * 根据sql语句更新记录
	 * @param sql	
	 * @param paras		参数列表  如果是null则表示无参数
	 * @return
	 * @author masai
	 * @time 2017年4月27日 下午6:59:58
	 */
	public int baseUpdate(String sql , Object[] paras){
		int result = 0 ;
		try {
			if(paras == null){
				result = Db.update(sql); 
			}else{
				result = Db.update(sql, paras);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 根据主键更新记录	Db方式实现 表的主键是id才可以使用
	 * @param clazz		Model子类 -- 表
	 * @param record	更新数据记录
	 * @return
	 * @author masai
	 * @time 2017年4月27日 下午7:00:49
	 */
	public int baseUpdate(Class<? extends Model> clazz,Record record){
		return baseUpdate(clazz, "id", record);
	}
	/**
	 * 根据主键更新记录	Db方式实现
	 * @param clazz		Model子类 -- 表
	 * @param primaryKey	主键  联合主键使用逗号间隔
	 * @param record	更新数据记录
	 * @return
	 * @author masai
	 * @time 2017年4月27日 下午7:01:26
	 */
	public int baseUpdate(Class<? extends Model> clazz ,String primaryKey , Record record){
		int result = 0 ;
		try {
			result = Db.update(TableMapping.me().getTable(clazz).getName(), primaryKey, record) == true ? 1 : 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 通过model更新记录
	 * @param entity
	 * @return
	 * @author masai
	 * @time 2017年5月23日 下午12:30:31
	 */
	public int baseUpdateByEntity(Model entity){
		int result = 0 ;
		try {
			result = entity.update() == true ? 1 : 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 插入一条数据 Db方式实现	表的主键是id，否则无法使用
	 * @param clazz		Model子类 -- 表
	 * @param record	要插入的记录
	 * @return
	 * @author masai
	 * @time 2017年4月27日 下午6:05:59
	 */
	public int baseInsert(Class<? extends Model> clazz,Record record){
		return baseInsert(clazz , "id" , record);
	}
	/**
	 * 插入一条记录 Db方式实现
	 * @param clazz		Model子类 -- 表
	 * @param primaryKey	主键	若是联合主键 使用逗号间隔
	 * @param record	要插入的记录
	 * @return
	 * @author masai
	 * @time 2017年4月27日 下午6:06:06
	 */
	public int baseInsert(Class<? extends Model> clazz ,String primaryKey , Record record){
		int result = 0 ;
		try {
			result = Db.save(TableMapping.me().getTable(clazz).getName(), primaryKey, record)  == true ? 1 : 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 通过model插入记录
	 * @param entity
	 * @return
	 * @author masai
	 * @time 2017年5月23日 下午12:27:51
	 */
	public int baseInsertByEntity(Model entity){
		int result = 0 ;
		try {
			result = entity.save() == true ? 1 : 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 根据主键删除记录	Db方式，该方法表的主键是id，否则无法使用
	 * @param tableName	表名
	 * @param idValue	主键值
	 * @return
	 * @author masai
	 * @time 2017年4月27日 下午3:25:42
	 */
	public int baseDeleteById(Class<? extends Model> clazz,Object idValue){
		return baseDeleteById(clazz, "id", idValue);
	}
	/**
	 * 根据主键删除记录	Db方式
	 * @param tableName		表名
	 * @param primaryKey	主键，此处只能是单一主键
	 * @param idValue	主键值
	 * @return
	 * @author masai
	 * @time 2017年4月27日 下午3:35:56
	 */
	public int baseDeleteById(Class<? extends Model> clazz,String primaryKey,Object idValue){
		return baseDeleteById(TableMapping.me().getTable(clazz).getName(), primaryKey, new Object[] { idValue });
	}
	/**
	 * 根据主键删记录 	Db方式，该方法必须指定出主键
	 * @param tableName		表名
	 * @param primaryKey	主键，如果是联合主键使用逗号间隔
	 * @param idValue	主键值	数组
	 * @return
	 * @author masai
	 * @time 2017年4月27日 下午3:27:20
	 */
	public int baseDeleteById(String tableName,String primaryKey,Object[] idValue){
		int result = 0;
		try {
			result = Db.deleteById(tableName, primaryKey, idValue) == true ? 1 : 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 根据主键删除操作
	 * @param baseModel		BaseModel对象
	 * @param idValues	主键  单一主键直接放入；多主键使用字符串放入使用逗号间隔
	 * @return
	 * @author masai
	 * @time 2017年4月27日 上午12:02:46
	 */
	/*public int baseDeleteById(BaseModel<?> baseModel,Object idValues){
		int result = 0;
		try {
			BaseModel<?> baseDao  = (BaseModel<?>) baseModel.dao();
			result = baseDao.deleteById(idValues) == true ? 1 : 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}*/
	/**
	 * 根据主键删除操作
	 * @param clazz		model类class对象
	 * @param idValues	主键  单一主键直接放入；多主键使用字符串放入使用逗号间隔
	 * @return
	 * @author masai
	 * @time 2017年4月26日 下午10:18:55
	 */
	/*public int baseDeleteById(Class<?> clazz,Object idValues){
		int result = 0;
		try {
			BaseModel<?> baseDao  = (BaseModel<?>) ((BaseModel<?>) clazz.newInstance()).dao();
			result = baseDeleteById(baseDao,idValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}*/
}
