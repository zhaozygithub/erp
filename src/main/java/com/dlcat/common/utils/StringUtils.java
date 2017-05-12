package com.dlcat.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	private static final Pattern URL = Pattern.compile(
		"^((https|http|ftp|rtsp|mms)?://)" 
	     + "+(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" 
	     + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" 
	     + "|" 
	     + "([0-9a-z_!~*'()-]+\\.)*" 
	     + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." 
	     + "[a-z]{2,6})" 
	     + "(:[0-9]{1,4})?" 
	     + "((/?)|" 
	     + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$", Pattern.CASE_INSENSITIVE
	);
	private static final Pattern EMAIL = Pattern.compile("^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$");
	private static final Pattern NUMERIC = Pattern.compile("^[0-9]+$");
	private static final Pattern MONEY = Pattern.compile("^[0-9]+$|^[0-9]+\\.[0-9]{1,6}$");
	private static final Pattern CH_CHAR = Pattern.compile("^[\u4e00-\u9fa5]+$");
	private static final Pattern PHONE = Pattern.compile("^1[3|4|5|7|8]([0-9])\\d{8}$");
	private static final Pattern TELPHONE = Pattern.compile("^(0[0-9]{2,4}-?[0-9]{7,8})|(1[3|4|5|7|8][0-9]{9})$");
	
	public static boolean checkUrl(String url) {
		Matcher m = URL.matcher(url);
		return m.matches();
	}
	
	/**
	 * 验证邮箱
	 * @param email
	 * @return
	 */
	public static Boolean checkEmail(String email) {
        Matcher m = EMAIL.matcher(email);     
        return m.matches();
	}
	
	/**
	 * 验证手机号
	 * @param phone
	 * @return
	 */
	public static Boolean checkPhone(String phone) {
        Matcher m = PHONE.matcher(phone);     
        return m.matches();
	}
	
	/**
	 * 验证固话和手机
	 */
	public static Boolean checkTelPhone(String tel){
		Matcher m = TELPHONE.matcher(tel);     
        return m.matches();
	}
	
	/**
	 * 是否是中文字符
	 * @param str
	 * @return
	 */
	public static boolean is_chinese(String str) {
		if (str == null) {
			return false;
		}
		Matcher result = CH_CHAR.matcher(str);
		return result.matches();
	}
	
	/**
	 * 判断字符串是否为数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {  
        Matcher isNum = NUMERIC.matcher(str);  
        if (isNum.matches()) {  
            return true;  
        } else {  
            return false;  
        }  
    }
	
	/**
	 * 是否为金额
	 * @param str
	 * @return
	 */
	public static boolean isMoney(String str) {
		return MONEY.matcher(str).matches();
	}
	
	/**
     * 判断是否含有特殊字符
     * @param str
     * @return true为包含，false为不包含
     */
    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }
	/**
	 * 字符串按指定符号分割为数组
	 * 
	 * @param str
	 * @param Symbol
	 * @return
	 * @throws Exception 
	 */
	public static String[] split(String str, String symbol) throws Exception {
		if (str == null || symbol == null || str.length() < 1 || symbol.length() < 1) {
			try {
				throw new Exception("字符错误");
			} catch (Exception e) {
				throw new Exception(e);
			}
		}
		String[] strSplit = str.split(symbol);
		return strSplit;
	}
	
	/**
	 * 是否为空
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		return str == null || ((str.trim().length()) == 0) || "null".equals(str);
	}

	/**
	 * 是否不为空
	 * @param str
	 * @return
	 */
	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}
	
	/**
	 * 生成32位的唯一序列号
	 * @return
	 */
	public static String genenrateUniqueInd() {
		return SecurityUtil.md5(UUID.randomUUID().toString());
	}

	/**
	 * 生成订单序列号	16位序号 = 年份+日期+4位数字
	 * 日期加上(currentNum+1) pg: 201506160001
	 * @param curNumIndex
	 * @return
	 */
	public static String generateSerialno(int currentNum) {
		String nextNumStr = String.valueOf(currentNum + 1);
		while (nextNumStr.length() < 4) {
			nextNumStr = "0" + nextNumStr;
		}
		//如果nextNumStr长度大于4则取后四位
		if(nextNumStr.length()>4){
			nextNumStr = nextNumStr.substring(nextNumStr.length()-4, nextNumStr.length());
		}
		return DateUtil.getCurrentDateStr() + nextNumStr;
	}
	
	/**
	 * 将list转成String,并按impStr进行分隔
	 * @param objList
	 * @param impStr
	 * @return
	 */
	public static String listToStr(List<?> objList, String impStr) {
		String resultStr = "";
		for (Object obj : objList) {
			if (obj == null) {
				continue;
			}
			if ("".equals(resultStr)) {
				resultStr = obj.toString();
			} else {
				resultStr += impStr + obj.toString();
			}
		}
		return resultStr;
	}
	/**
	 * 将Arrary转成String,并按impStr进行分隔
	 * @param arrayStr
	 * @param impStr
	 * @return
	 * @author masai
	 * @time 2017年5月10日 下午3:02:28
	 */
	public static String arrayToStr(String[] arrayStr , String impStr){
		String resultStr = "";
		for (String obj : arrayStr) {
			if (obj == null) {
				continue;
			}
			if ("".equals(resultStr)) {
				resultStr = obj.toString();
			} else {
				resultStr += impStr + obj;
			}
		}
		return resultStr;
	}
	
	/**
	 * 根据表名称和字段名称拼出查询sql语句
	 * @param tableName		表名
	 * @param fields	要查询的字段名称，为null，默认为查询所有
	 * @return
	 * @author masai
	 * @time 2017年4月17日 下午5:15:37
	 */
	public static String getQuerySql(String tableName,String[] fields){
		if(isBlank(tableName)){
			return null;
		}
		StringBuffer sb = new StringBuffer("select ");
		//字段名称null，则直接给出查询所有字段
		if(fields == null){
			return sb.append(" * from ").append(tableName).toString();
		}
		return sb.append(listToStr(Arrays.asList(fields),",")).append(" from ").append(tableName).toString();
	}
	
	/**
	 * 生成16位唯一序列号
	 * 日期加上(currentNum+1) pg: 201506160001
	 * @param curNumIndex
	 * @return
	 */
	public static String getSerialno() {
		String base = UUID.randomUUID().toString();
		Random random = new Random();     
	    StringBuffer sb = new StringBuffer();     
	    for (int i = 0; i < 16; i++) {     
	        int number = random.nextInt(base.length());     
	        sb.append(base.charAt(number));     
	    }     
	    return sb.toString();     
	}
	
	public static List<String> getImageSrc(String htmlStr) {
		Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
		Matcher m = p.matcher(htmlStr);
		List<String> srcs = new ArrayList<String>();
		while (m.find()) {
			srcs.add(m.group(1));
		}
		return srcs;
	}
	
	/**
	 * 将对象转化成String 进行比较
	 * @param fstObj
	 * @param secObj
	 * @return
	 */
	public static boolean isEqual(Object fstObj, Object secObj) {
		if (fstObj == null && secObj == null) {
			return true;
		}
		if (fstObj == null || secObj == null) {
			return false;
		}
		return fstObj.toString().equals(secObj.toString());
	}
	
	/**
	 * 截取limitLength长度的字符串
	 * @param str
	 * @param limitLength
	 * @return
	 */
	public static String subString(String str, int limitLength) {
		return isNotBlank(str) && str.length() > limitLength ? str.substring(0, limitLength) : str;
	}
	
	/**
	 * 字符串编码
	 * @param sStr
	 * @param sEnc
	 * @return String
	 */
	public static String encode(String sStr, String sEnc) {
		String sReturnCode = sStr;
		try {
			sReturnCode = URLEncoder.encode(sStr, sEnc);
		} catch (UnsupportedEncodingException ex) {}
		return sReturnCode;
	}
	
	/**
	 * 字符串解码
	 * @param sStr
	 * @param sEnc
	 * @return String
	 */
	public static String decode(String sStr, String sEnc) {
		String sReturnCode = sStr;
		try {
			sReturnCode = URLDecoder.decode(sStr, sEnc);
		} catch (UnsupportedEncodingException ex) {}
		return sReturnCode;
	}
}
