package com.dlcat.controller;

import java.lang.reflect.Field;
import com.jfinal.core.Controller;

/**
 * 通用jfinal  controller类
 * @author masai
 * @time 2017年4月16日 下午3:07:38
 */
public class BaseController extends Controller {
	
	/**
	 * 获取url拼接参数中最后一个参数
	 * @return
	 * @author masai
	 * @time 2017年4月16日 下午5:02:44
	 */
	public Object getLastPara(){
		Object fildValue = getSupperClassFild("URL_PARA_SEPARATOR");
		String paraAll = this.getPara();
		Object result = null;
		if(paraAll != null){
			String[]resValue = paraAll.split(fildValue.toString());
			result = resValue[resValue.length-1];
		}
		return result;
	}
	
	/**
	 * 获取祖先类中的所有属性值，忽略修饰符 private,protect,public
	 * @param filedName	父类中的属性名称
	 * @return
	 * @author masai
	 * @time 2017年4月16日 下午6:16:57
	 */
	private Object getSupperClassFild(String filedName){
	   Field field = null;
	   Class<?> clazz = this.getClass();
       for(; clazz != Object.class ; clazz = clazz.getSuperclass()) {  
           try {
               field = clazz.getDeclaredField(filedName);  
           } catch (Exception e) {
               //这里甚么都不能抛出去。  
               //如果这里的异常打印或者往外抛，则就不会进入
           }
       }
       //抑制Java对其的检查  
       field.setAccessible(true);
       
       try {
		   return field.get(filedName);
       } catch (Exception e) {
       }
       return null;
	}
}
