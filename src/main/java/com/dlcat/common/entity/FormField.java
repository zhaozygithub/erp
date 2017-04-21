package com.dlcat.common.entity;

import java.io.Serializable;
import java.util.List;

import com.dlcat.core.model.ToCodeLibrary;
/**
 * 表单构造元素类
 * @author masai
 * @time 2017年4月19日 上午11:36:24
 */
public class FormField implements Serializable {
	private static final long serialVersionUID = -8269558838532027061L;
	/**
	 * 字段名称
	 */
	private String filedName;
	/**
	 * 字段文本描述
	 */
	private String text;
	/**
	 * 表单元素类型
	 * 如：span	span标签、text 输入文本框、radio 单选框、select 下拉选择框、file 文件上传、
	 */
	private String type;
	/**
	 * 显性提示	直接跟在节点后面
	 */
	private String notice;
	/**
	 * 隐性提示	需要鼠标放到提醒符号上面再显示
	 */
	private String hint;
	/**
	 * 文本框内提示
	 */
	private String inNotice;
	/**
	 * 节点数据源，适用下拉框、单选框等
	 */
	private List<ToCodeLibrary> options;
	/**
	 * 计量单位
	 */
	private String unit;
	/**
	 * 是否有子节点
	 */
	private Boolean isHaschild;
	/**
	 * 是否隐藏
	 */
	private Boolean isHide;
	/**
	 * 是否必须
	 */
	private Boolean isRequired;
	/**
	 * 父节点名称
	 */
	private String fatherFiledName;
	/**
	 * 输入值类型
	 */
	private String inType;
	/**
	 * 默认值
	 */
	private String defaultValue;

	public String getFiledName() {
		return filedName;
	}
	public void setFiledName(String filedName) {
		this.filedName = filedName;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	public String getInNotice() {
		return inNotice;
	}
	public void setInNotice(String inNotice) {
		this.inNotice = inNotice;
	}
	public List<ToCodeLibrary> getOptions() {
		return options;
	}
	public void setOptions(List<ToCodeLibrary> options) {
		this.options = options;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Boolean getIsHaschild() {
		return isHaschild;
	}
	public void setIsHaschild(Boolean isHaschild) {
		this.isHaschild = isHaschild;
	}
	public Boolean getIsHide() {
		return isHide;
	}
	public void setIsHide(Boolean isHide) {
		this.isHide = isHide;
	}
	public Boolean getIsRequired() {
		return isRequired;
	}
	public void setIsRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}
	public String getFatherFiledName() {
		return fatherFiledName;
	}
	public void setFatherFiledName(String fatherFiledName) {
		this.fatherFiledName = fatherFiledName;
	}
	public String getInType() {
		return inType;
	}
	public void setInType(String inType) {
		this.inType = inType;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}