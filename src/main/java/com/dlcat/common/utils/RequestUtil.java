package com.dlcat.common.utils;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应字符串值
	 */
	public static String getString(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		return value ;
	}
	
	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @param define 默认值
	 * @return 从request获取参数对应字符串值
	 */
	public static String getString(HttpServletRequest request, String paramName, String define) {
		String value = request.getParameter(paramName);
		return StringUtils.isNotBlank(value) ? value : define;
	}
	
	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应整型值
	 */
	public static Integer getInteger(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if (StringUtils.isBlank(value))
			return null;
		else {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @param define 默认值           
	 * @return 从request获取参数对应整型值
	 */
	public static Integer getInteger(HttpServletRequest request, String paramName, Integer define) {
		String value = request.getParameter(paramName);
		if (StringUtils.isBlank(value))
			return define;
		else {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				e.printStackTrace();
				return define;
			}
		}
	}
	
	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应长整型值
	 */
	public static Long getLong(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if (StringUtils.isBlank(value))
			return null;
		else {
			try {
				return Long.parseLong(value);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	}

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @param define 默认值
	 * @return 从request获取参数对应长整型值
	 */
	public static Long getLong(HttpServletRequest request, String paramName,Long define) {
		String value = request.getParameter(paramName);
		if (StringUtils.isBlank(value))
			return define;
		else {
			try {
				return Long.parseLong(value);
			} catch (Exception e) {
				e.printStackTrace();
				return define;
			}
		}

	}
	
	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应双精度类型
	 */
	public static Double getDouble(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if (StringUtils.isBlank(value))
			return null;
		else {
			try {
				return Double.parseDouble(value);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

	}

	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @param define 默认值
	 * @return 从request获取参数对应双精度类型
	 */
	public static Double getDouble(HttpServletRequest request, String paramName,Double define) {
		String value = request.getParameter(paramName);
		if (StringUtils.isBlank(value))
			return define;
		else {
			try {
				return Double.parseDouble(value);
			} catch (Exception e) {
				e.printStackTrace();
				return define;
			}
		}

	}
	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应布尔值
	 */
	public static boolean getBoolean(HttpServletRequest request,String paramName) {
		String value = request.getParameter(paramName);
		if (StringUtils.isBlank(value))
			return false;
		else
			return Boolean.valueOf(value).booleanValue();
	}
	
	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取属性对应字符串值
	 */
	public static String getAttrString(HttpServletRequest request, String paramName) {
		String value = (String) request.getAttribute(paramName);
		return value ;
	}
	
	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取属性对应字符串值
	 */
	public static String getAttrString(HttpServletRequest request, String paramName, String define) {
		String value = (String) request.getAttribute(paramName);
		return StringUtils.isNotBlank(value) ? value : define;
	}
	
	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应整型值
	 */
	public static Integer getAttrInteger(HttpServletRequest request, String paramName) {
		String value = request.getAttribute(paramName).toString();
		if (StringUtils.isBlank(value))
			return null;
		else {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	/**
	 * @param request
	 * @param paramName
	 *            参数名称
	 * @return 从request获取参数对应整型值
	 */
	public static Integer getAttrInteger(HttpServletRequest request, String paramName, Integer define) {
		String value = request.getAttribute(paramName).toString();
		if (StringUtils.isBlank(value))
			return define;
		else {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				e.printStackTrace();
				return define;
			}
		}
	}
	
	/**
	 * 判断ajax请求
	 * @param request
	 * @return
	 */
	public static boolean isAjax(HttpServletRequest request) {
	    return (request.getHeader("X-Requested-With") != null && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString()));
	}
}
