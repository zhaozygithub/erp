package com.dlcat.common.entity;

import java.util.ArrayList;
import java.util.List;

import com.dlcat.core.model.ToCodeLibrary;

/**
 * 表单元素构造
 * @author masai
 * @time 2017年4月19日 下午5:41:47
 */
public class FormBuilder {
	/**
	 * 测试方法
	 * @return
	 * @author masai
	 * @time 2017年4月19日 下午5:45:48
	 */
	public static List<FormField> formBuilderTest(){
		List<FormField> formFieldList = new ArrayList<FormField>();
		
		FormField formField = new FormField();
		formField.setFiledName("a");
		formField.setText("字母A");
		formField.setUnit("");
		formField.setType("text");
		formField.setNotice("这个文本框是字母A");
		formField.setDefaultValue("aaa");
		formFieldList.add(formField);
		
		formField = new FormField();
		formField.setFiledName("b");
		formField.setText("字母b");
		formField.setType("span");
		formFieldList.add(formField);

		formField = new FormField();
		formField.setFiledName("c");
		formField.setText("字母c");
		formField.setType("radio");
		formField.setOptions(ToCodeLibrary.getCodeLibrariesBySQL("YesNo",true,null));
		formFieldList.add(formField);

		formField = new FormField();
		formField.setFiledName("d");
		formField.setText("字母d");
		formField.setType("select");
		formField.setOptions(ToCodeLibrary.getCodeLibrariesBySQL("YesNo",true,null));
		formFieldList.add(formField);

		return formFieldList;
	}
}
