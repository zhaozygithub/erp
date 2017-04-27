package com.dlcat.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.core.model.LoanApplyApprove;
import com.dlcat.core.model.SysUser;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

/** 
* @author zhaozhongyuan
* @date 2017年4月25日 上午9:18:21 
* @Description: 添加所有model的一些常用方法
* @param <M> 
*/
public class BaseModel<M extends Model<M>> extends Model<M> implements IBean {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;
		
		/*public int baseInsert(Class<?> clazz,Map<String,Object> map){
		try {
			BaseModel<?> baseDao  = (BaseModel<?>) ((BaseModel<?>) clazz.newInstance()).dao();
			System.out.println("eeeeeeeeee");
			//baseDao.put(map);
			baseDao.put("name", "abcdef");
			System.out.println("bbbbbbbbbb");
			Db.use("").save(tableName, primaryKey, record);
			Db.use("").del
			baseDao.save();
			System.out.println("aaaaaaaaa");
		} catch (Exception e) {
		}
		return 0;
	}*/
	
	/**
	 * 根据主键删除操作
	 * @param baseModel		BaseModel对象
	 * @param idValues	主键  单一主键直接放入；多主键使用字符串放入使用逗号间隔
	 * @return
	 * @author masai
	 * @time 2017年4月27日 上午12:02:46
	 */
	public int baseDeleteById(BaseModel baseModel,Object idValues){
		int result = 0;
		try {
			BaseModel baseDao  = (BaseModel) baseModel.dao();
			result = baseDao.deleteById(idValues) == true ? 1 : 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 根据主键删除操作
	 * @param clazz		model类class对象
	 * @param idValues	主键  单一主键直接放入；多主键使用字符串放入使用逗号间隔
	 * @return
	 * @author masai
	 * @time 2017年4月26日 下午10:18:55
	 */
	public int baseDeleteById(Class<?> clazz,Object idValues){
		int result = 0;
		try {
			BaseModel baseDao  = (BaseModel) ((BaseModel) clazz.newInstance()).dao();
			result = baseDeleteById(baseDao,idValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
