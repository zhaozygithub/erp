package com.dlcat.common;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

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
	public void testBaseModel(String cacheName) {
		System.out.println(cacheName);
	}
}
