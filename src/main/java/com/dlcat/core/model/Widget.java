package com.dlcat.core.model;

import java.util.List;

/** 
* @author zhaozhongyuan
* @date 2017年4月19日 上午11:08:25 
* @Description: 封装组件 
*/
public class Widget {
	//type为组件类型
	private String cnName, enName, type;
	//为list等组件设置默认值
	private List<String> values;

	public Widget(String cnName, String enName, String type) {
		super();
		
		this.cnName = cnName;
		this.enName = enName;
		this.type = type;
	}

	public Widget(String cnName, String enName, String type, List<String> values) {
		super();

		this.cnName = cnName;
		this.enName = enName;
		this.type = type;
		this.values = values;
	}


	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

}
