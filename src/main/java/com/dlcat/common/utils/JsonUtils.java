package com.dlcat.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


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
	 * 注意：此方法将转化josn串中的数值类型如1改为1.0，字符串不会修改
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
	/**
	 * json转map，不会修改json中的任何值
	 * 额外处理：去掉value中的所有的双引号	注意：所有双引号
	 * 解决了：gson能够将json字符串转换成map, 但是在转成map时, 会默认将字符串中的int , 
	 * long型的数字, 转换成double类型 , 数字会多一个小数点 , 如 1 会转成 的问题
	 * @param data
	 * @return
	 * @author masai
	 * @time 2017年5月27日 下午5:11:37
	 */
	public static HashMap<String, Object> fromJson2HashMapNoChange(String data){
		//通过这种方式可以将json保持数据完整性的转化为HashMap（即不做任何改变）
		Gson gson = new GsonBuilder()
        .registerTypeAdapter(
            new TypeToken<HashMap<String, Object>>(){}.getType(), 
            new JsonDeserializer<HashMap<String, Object>>() {
            public HashMap<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            	HashMap<String, Object> hashMap = new HashMap<String, Object>();
                JsonObject jsonObject = json.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    hashMap.put(entry.getKey(), entry.getValue());
                }
                return hashMap;
            }
        }).create();
		HashMap<String, Object> map = gson.fromJson(data, new TypeToken<HashMap<String, Object>>(){}.getType());
		//以上不做任何改变的转化成了HashMap，所以字符串的收尾双引号也是保留
		//注意：将会去掉value内的所有的双引号
		for(String key : map.keySet()){
			Object value = map.get(key);
			if(value != null){
				String tempValue = value.toString();
				tempValue = tempValue.replaceAll("\"", "");
				map.put(key, tempValue);
			}
		}
		return map;
	}
 }
