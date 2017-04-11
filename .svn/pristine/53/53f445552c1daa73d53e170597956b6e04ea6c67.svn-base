package com.dlcat.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class JsonUtils {
	
	public static final Gson gson = new Gson();
	public static final GsonBuilder gb = new GsonBuilder();

	public static <T> T json2Object(Object data, Class<T> clazz) {
		return gson.fromJson(gson.toJson(data), clazz);
	}

	public static String object2JsonString(Object obj) {
		if (obj == null) {
			return null;
		}
		return gson.toJson(obj).toString();
	}

	/**
	 * json字符串转为 T类型
	 * @author masai
	 * @time 2016年11月16日 上午10:07:53
	 * @param data
	 * @param clazz
	 * @return
	 */
	public static <T> T fromJson(String data, Class<T> clazz) {
		return gson.fromJson(data, clazz);
	}
	
	/**
	 * 转化成Json，不转化hmtl字符及不忽略空值
	 * @param obj
	 * @return
	 */
	public static String object2JsonNoEscaping(Object obj){
		gb.disableHtmlEscaping();
		gb.serializeNulls();
		return gb.create().toJson(obj);
	}
 }
