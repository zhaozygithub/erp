package com.dlcat.common;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

public class BaseModel<M extends Model<M>> extends Model<M> implements IBean {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;
	public void testBaseModel(String cacheName) {
		System.out.println(cacheName);
	}
}
