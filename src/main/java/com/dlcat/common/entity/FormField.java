package com.dlcat.common.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dlcat.core.model.ToCodeLibrary;
/**
 * 表单构造元素类
 * @author masai
 * @time 2017年4月19日 上午11:36:24
 * 构造方法可接受4或者5个参数:
 * String text ,String name,String type,String value[,List<ToCodeLibrary> options]
 */
public class FormField implements Serializable {
	private static final long serialVersionUID = -8269558838532027061L;
	/**
	 * 请注意:radio类型的其name,value和描述要放到options中
	 */
	/**
	 * 字段名称
	 */
	private String filedName;
	/**
	 * 字段文本描述,就是左边的label
	 */
	private String text;
	/**
	 * 表单元素类型type
	 * 如：text 输入文本框、radio 单选框、textarea 多行文本域、select 下拉选择框、file 文件上传、
	 * checkbox复选框、date日期,password密码、hidden隐藏域、email邮箱地址、color颜色拾取 、
	 */
	private String type;
	/**
	 * 显性提示	直接跟在节点后面
	 */
	private String notice;
	/**
	 * 隐性提示	需要鼠标放到提醒符号上面再显示,就是title
	 */
	private String hint;
	/**
	 * 文本框内提示,就是placeholder
	 */
	private String inNotice;
	/**
	 * 节点数据源，适用下拉框、单选框等
	 * 数据来源：调用OptionUtil类
	 */
	private List<Map> optionList;
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
	 * 三种: decimal digital string 分别是小数 整数 字符串
	 */
	private String inType;
	/**
	 * 默认值
	 */
	private String defaultValue;
	/**
	 * disable 禁止用户修改(但是可以用JS修改)
	 */
	private boolean disable=false;
	//构造方法
	public FormField(){
	}
	//推荐使用表态方法createFormField来构造,此方法支持输入的内容的校验.
	/**
	 * 构造添加函数.最核心的三个参数,适用于除select和radio以外的构造
	 * @param name  即input的name属性
	 * @param text 旁边的描述,用于显示到左边的label中.
	 * @param type 即input的type属性
	 */
	public FormField(String name ,String text,String type){
		setText(text);
		setFiledName(name);
		setType(type);
	}
	
	public FormField(String name ,String text,String type,String value){
		//FormField( text , name, type, value,new ArrayList<ToCodeLibrary>());
		setFiledName(name);
		setText(text);
		setType(type);
		setDefaultValue(value);
	}
	public FormField(String name ,String text,String type,String value,boolean isRequired ){
		//FormField( text , name, type, value,new ArrayList<ToCodeLibrary>());
		setFiledName(name);
		setText(text);
		setType(type);
		setDefaultValue(value);
		setIsRequired(isRequired);
	}
	/**
	 * 在上面的基础上增加select和radio的构造方法
	 * @param name  即input的name属性
	 * @param text 旁边的描述,用于显示到左边的label中.
	 * @param type 即input的type属性
	 * @param options 是select和radio的选项
	 * */
	public FormField(String name ,String text,String type,List<Map> options){
		setFiledName(name);
		setText(text);
		setType(type);
		setOptionList(options);
		//return;
	}
	public FormField(String name ,String text,String type,String value,List<Map> options){
		setFiledName(name);
		setText(text);
		setType(type);
		setDefaultValue(value);
		setOptionList(options);
		//return;
	}
	public FormField(String name ,String text,String type,String value,List<Map> options,boolean isRequired){
		setFiledName(name);
		setText(text);
		setType(type);
		setDefaultValue(value);
		setOptionList(options);
		setIsRequired(isRequired);
		//return;
	}
	/**
	 * 张松添加于2017年5月17日
	 * 非构造函数便于复用
	 * @param name  即input的name属性
	 * @param text 旁边的描述,用于显示到左边的label中.
	 * @param type 即input的type属性
	 * @param value 默认值.
	 * @param options 此参数仅用于select和radio类,非select和radio类请设为null
	 * @param isRequired 是否是必填的.必填字段会进行非空验证.
	 * @return
	 */
	public static FormField createFormField(String name ,String text,String type,String value,List<Map> options,boolean isRequired){
		FormField formField=createFormField( name , text, type, value, options, isRequired,null);
		return formField;
		
	}

	/**
	 * 张松添加于2017年5月17日
	 * 非构造函数便于复用
	 * @param name  即input的name属性
	 * @param text 旁边的描述,用于显示到左边的label中.
	 * @param type 即input的type属性
	 * @param value 默认值.
	 * @param options 此参数仅用于select和radio类,非select和radio类请设为null
	 * @param isRequired 是否是必填的.必填字段会进行非空验证.
	 */
	public static FormField createFormField(String name ,String text,String type,String value,boolean isRequired){
		FormField formField=createFormField( name , text, type, value, null, isRequired,null);
		return formField;
		
	}
	/**
	 * @param name  即input的name属性
	 * @param text 旁边的描述,用于显示到左边的label中.
	 * @param type 即input的type属性
	 * @param value 默认值.
	 * @param options 此参数仅用于select和radio类,非select和radio类请设为null
	 * @param isRequired 是否是必填的.必填字段会进行非空验证.
	 * @param inType 校验类型分三种:decimal digital string 分别是小数 整数 字符串
	 * @return
	 */
	public static FormField createFormField(String name ,String text,String type,String value,boolean isRequired,String inType){
		FormField formField=createFormField(name, text, type, value, null, isRequired, inType) ;
		return formField;
		
	}
	/**此方法是另外三个createFormField的来源.另外三个createFormField调用的是这个方法.
	 * @param name  即input的name属性
	 * @param text 旁边的描述,用于显示到左边的label中.
	 * @param type 即input的type属性
	 * @param value 默认值.
	 * @param options 此参数仅用于select和radio类,非select和radio类请设为null
	 * @param isRequired 是否是必填的.必填字段会进行非空验证.
	 * @param inType 校验类型分三种:decimal digital string 分别是小数 整数 字符串
	 * @return
	 */
	public static FormField createFormField(String name ,String text,String type,String value,List<Map> options,boolean isRequired,String inType){
		FormField formField=new FormField( name , text, type, value, options, isRequired);
		formField.setInType(inType);
		return formField;
		
	}
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
	public List<Map> getOptionList() {
		return optionList;
	}
	public void setOptionList(List<Map> optionList) {
		this.optionList = optionList;
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
	public boolean getDisable() {
		return disable;
	}
	public void setDisable(boolean disable) {
		this.disable = disable;
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
	@Override
	public String toString() {
		return "FormField [filedName=" + filedName + ", text=" + text + ", type=" + type + ", notice=" + notice
				+ ", hint=" + hint + ", inNotice=" + inNotice + ", optionList=" + optionList + ", unit=" + unit
				+ ", isHaschild=" + isHaschild + ", isHide=" + isHide + ", isRequired=" + isRequired
				+ ", fatherFiledName=" + fatherFiledName + ", inType=" + inType + ", defaultValue=" + defaultValue
				+ "]";
	}
	
}