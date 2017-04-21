package com.dlcat.common.entity;

import java.io.Serializable;
import java.util.List;
/**
 * 表单构造数据类
 * @author masai
 * @time 2017年4月19日 下午12:08:25
 */
public class FormData implements Serializable{
	private static final long serialVersionUID = -5743764757818253572L;
	/**
	 * 表单名称
	 */
	private String formName;
	/**
	 * 表单提交url
	 */
	private String submitUrl;
	/**
	 * 表单元素列表
	 */
	private List<FormField> formFieldList;
	/**
	 * 栏目数 表示将表单元素分几栏	为空默认是一栏
	 */
	private Integer columnAmount; 

	public String getSubmitUrl() {
		return submitUrl;
	}

	public void setSubmitUrl(String submitUrl) {
		this.submitUrl = submitUrl;
	}

	public List<FormField> getFormFieldList() {
		return formFieldList;
	}

	public void setFormFieldList(List<FormField> formFieldList) {
		this.formFieldList = formFieldList;
	}

	public Integer getColumnAmount() {
		return columnAmount;
	}

	public void setColumnAmount(Integer columnAmount) {
		this.columnAmount = columnAmount;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

}