package com.dlcat.core.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;






import com.dlcat.entity.DyResponse;
import com.dlcat.entity.PageStructure;
import com.dlcat.entity.Search;
import com.dlcat.entity.TableHeader;
import com.dlcat.model.ToCodeLibrary;

/**
 * 页面工具类
 * @author masai
 * @time 2017年4月13日 下午7:32:07
 */
public class PageUtil {
	/*public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("######0.00");
	
	*//**
	 * 构造查看/编辑页面
	 * @param view
	 * @param subUrl
	 * @param names
	 * @return
	 *//*
	public static DyResponse createFormPageStructure(String view, String subUrl, FormField formField) throws Exception {
		return createFormPageStructure(view, subUrl, null, formField);
	}
	
	*//**
	 * 构造查看/编辑页面
	 * @param view
	 * @param subUrl
	 * @param jumpUrl
	 * @param names
	 * @return
	 *//*
	public static DyResponse createFormPageStructure(String view, String subUrl, String jumpUrl, FormField formField) throws Exception {
		List<FormField> formFieldList = new ArrayList<FormField>();
		formFieldList.add(formField);
		
		return createFormPageStructure(view, subUrl, jumpUrl, formFieldList, true, null);
	}
	
	*//**
	 * 构造查看/编辑页面
	 * @param view
	 * @param subUrl
	 * @param formFieldList
	 * @return
	 *//*
	public static DyResponse createFormPageStructure(String view, String subUrl, List<FormField> formFieldList) throws Exception {
		return createFormPageStructure(view, subUrl, null, formFieldList, true, null);
	}
	
	*//**
	 * 构造查看/编辑页面
	 * @param view
	 * @param subUrl
	 * @param jumpUrl
	 * @param formFieldList
	 * @return
	 *//*
	public static DyResponse createFormPageStructure(String view, String subUrl, String jumpUrl, List<FormField> formFieldList) throws Exception {
		return createFormPageStructure(view, subUrl, jumpUrl, formFieldList, true, null);
	}
	
	*//**
	 * 构造查看/编辑页面
	 * @param view
	 * @param subUrl
	 * @param jumpUrl
	 * @param formFieldList
	 * @param cancelBtn 是否添加取消按钮
	 * @return
	 *//*
	public static DyResponse createFormPageStructure(String view, String subUrl, String jumpUrl, List<FormField> formFieldList, Boolean cancelBtn, String submitBtn) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("view", view);
		data.put("SubUrl", subUrl);
		if (com.dy.core.utils.StringUtils.isNotBlank(jumpUrl)) {
			data.put("backSkip", 1);
		}
		if (com.dy.core.utils.StringUtils.isNotBlank(submitBtn)) {
			data.put("Submit", submitBtn);
		}
		if (cancelBtn == null || !cancelBtn) {
			data.put("cancelbtn", "false");
		}
		if (StringUtils.isNotEmpty(jumpUrl))
			data.put("jumpUrl", jumpUrl);
		data.put("FormFields", formFieldList);

		DyResponse response = new DyResponse();
		response.setStatus(DyResponse.OK);
		response.setDescription("OK");
		response.setData(data);

		return response;
	}
	
	
	*//**
	 * 构造查看/编辑页面
	 * @param view
	 * @param subUrl
	 * @param names
	 * @param texts
	 * @param types
	 * @return
	 *//*
	public static DyResponse createFormPageStructure(String view, String subUrl, String[] names, String[] texts, String[] types) throws Exception {
		return createFormPageStructure(view, subUrl, null, names, texts, types);
	}
	
	*//**
	 * 构造查看/编辑页面
	 * @param view
	 * @param subUrl
	 * @param jumpUrl
	 * @param names
	 * @param texts
	 * @param types
	 * @return
	 *//*
	public static DyResponse createFormPageStructure(String view, String subUrl, String jumpUrl, String[] names, String[] texts, String[] types) throws Exception {
		List<FormField> formFieldList = new ArrayList<FormField>();
		if(names != null) {
			for(int i=0;i<names.length;i++) {
				FormField formField = new FormField();
				formField.setName(names[i]);
				formField.setText(texts[i]);
				formField.setType(types[i]);
				
				formFieldList.add(formField);
			}
		}
		
		return createFormPageStructure(view, subUrl, jumpUrl, formFieldList);
	}
	
	*//**
	 * 构造表单数据
	 * @param data Map/BaseEntity
	 * @param fields
	 * @return
	 * @throws Exception
	 *//*
	public static DyResponse createFormDataList(Object data, String[] fields) throws Exception {
		return createFormDataList(data, fields, null, null);
	}
	
	*//**
	 * 构造表单数据
	 * @param data Map/BaseEntity
	 * @param fields
	 * @param doubleFields
	 * @param df 默认保留两位小数
	 * @return
	 * @throws Exception
	 *//*
	@SuppressWarnings("unchecked")
	public static DyResponse createFormDataList(Object data, String[] fields, String doubleFields, DecimalFormat df) throws Exception {
		//只出路Map/BaseEntity
		if(!(data instanceof Map || data instanceof BaseEntity)) return null;
		
		//Double
		if(df == null) df = DECIMAL_FORMAT;
		if(StringUtils.isEmpty(doubleFields))
			doubleFields = ",";
		else
			doubleFields = "," + doubleFields;
		
		//要处理的字段
		String field = "";
		Map<String, Object> map = new HashMap<String, Object>();
		if(fields == null || fields.length <= 0) {
			if(data instanceof Map) {
				map = (Map<String, Object>) data;
				for(String key : ((Map<String, Object>) data).keySet()) {
					field += "," + key;
				}
			} else if(data instanceof BaseEntity) {
				for(Field temp : data.getClass().getDeclaredFields()) {
					if("serialVersionUID".equals(temp.getName())) continue;
					field += "," + temp.getName();
				}
			}
		} else {
			for(String temp : fields) {
				field += "," + temp;
			}
		}
		field = field.substring(1);
		
		//生成FormData
		List<FormData> formDatas = new ArrayList<FormData>();
		for(String key : field.split(",")) {
			Object value = null;
			if(data instanceof Map) {
				map = (Map<String, Object>) data;
				value = map.get(key);
			}
			else if(data instanceof BaseEntity) value = ReflectUtil.getFieldValue(data, key);
					
			if(doubleFields.indexOf("," + key) >= 0 && value instanceof Double) value = df.format((Double)value);
			if(value instanceof BigDecimal) value = NumberUtils.round((BigDecimal) value);
			
			FormData formData = new FormData();
			formData.setName(key);
			formData.setValue(value);
			formDatas.add(formData);
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("FormData", formDatas);
		
		DyResponse response = new DyResponse();
		response.setStatus(DyResponse.OK);
		response.setDescription("OK");
		response.setData(result);
		
		return response;
	}*/
	
	/**
	 * 构造列表页面
	 * @param url
	 * @param tableHeader
	 * @param search
	 * @return
	 * @throws Exception
	 * @author masai
	 * @time:2017年4月13日 下午7:30:28
	 */
	@SuppressWarnings("unchecked")
	public static DyResponse createTablePageStructure(String url, TableHeader tableHeader, Search search) throws Exception {
		PageStructure pageStructure = new PageStructure();
		
		//列表获取数据url
		pageStructure.setTableDataUrl(url);
		
		//表头
		List<TableHeader> tableHeaderList = null;
		if(tableHeader != null && tableHeader.getNames() != null && tableHeader.getFildNames() != null) {
			tableHeaderList = new ArrayList<TableHeader>();
			for(int i=0;i<tableHeader.getNames().length;i++) {
				if(tableHeader.getNames()[i] == null) continue;
				
				TableHeader temp = new TableHeader();
				temp.setName(tableHeader.getNames()[i]);
				temp.setFildName(tableHeader.getFildNames()[i]);
				tableHeaderList.add(temp);
			}
		}
		pageStructure.setTableHeader(tableHeaderList);
		
		//查询框
		List<Search> searchList = null;
		if(search != null && search.getNames() != null && search.getTypes() != null) {
			searchList = new ArrayList<Search>();
			Map<String, List<ToCodeLibrary>> optionListMap = search.getOptionListMap();
			if(optionListMap == null) optionListMap = new HashMap<String, List<ToCodeLibrary>>();
			for(int i=0;i<search.getNames().length;i++) {
				Search temp = new Search();
				temp.setName(search.getNames()[i]);
				temp.setType(search.getTypes()[i]);
				temp.setFiledName(search.getFiledNames()[i]);
				temp.setOptionList(optionListMap.get(temp.getName()));
				searchList.add(temp);
			}
		}
		pageStructure.setSearch(searchList);
		
		DyResponse response = new DyResponse();
		response.setStatus(DyResponse.OK);
		response.setDescription("OK");
		response.setData(pageStructure);
		return response;
	}
}