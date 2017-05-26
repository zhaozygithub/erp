package com.dlcat.common.utils;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String DATE_SHORT_FORMAT = "yyyyMMdd";
	public static final String DATE_SLASH_FORMAT = "yyyy/MM/dd";
	public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String TIME_SHORT_FORMAT="yyyyMMddHHmmss";
	
	//改成线程安全方式20150916
	private static ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATE_FORMAT);
		}
	};
	
	private static ThreadLocal<SimpleDateFormat> dateShortFormat = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATE_SHORT_FORMAT);
		}
	};
	
	private static ThreadLocal<SimpleDateFormat> dateTimeFormat = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(TIME_FORMAT);
		}
	};
	
	private static ThreadLocal<SimpleDateFormat> dateFormatSlash = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATE_SLASH_FORMAT);
		}
	};
	private static ThreadLocal<SimpleDateFormat> TimeShortFormat = new ThreadLocal<SimpleDateFormat>(){
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(TIME_SHORT_FORMAT);
		}
	};
	/**
	 * Date --> String(yyyyMMddHHmmss)
	 * @author masai
	 * @time 2016年11月14日 下午8:25:49
	 * @param date
	 * @return
	 */
	public static String dateSimpleFormat(Date date){
		if(date == null){
			return "";
		}
		return TimeShortFormat.get().format(date);
	}
	/**
	 * Date --> String(yyyy-MM-dd)
	 * @param date
	 * @return
	 */
	public static String dateFormat(Date date) {
		if (date == null) {
			return "";
		}

		return dateFormat.get().format(date);
	}
	
	/**
	 * Double/Integer/Long --> String(yyyy-MM-dd)
	 * @param time
	 * @return
	 */
	public static String dateFormat(Object time){
		if(time == null) return "";
		
		Long value = null;
		if(time instanceof Double) {
			value = ((Double)time).longValue();
		} else if(time instanceof Integer) {
			value = ((Integer)time).longValue();
		} else if(time instanceof BigInteger) {
			value = ((BigInteger)time).longValue();
		} else {
			value = (Long) time;
		}
		if(value.toString().length() == 10) value = value*1000;
		
		return dateFormat.get().format(new Date(value));
	}
	
	/**
	 * Double/Integer/Long --> String(yyyyMMdd)
	 * @param time
	 * @return
	 */
	public static String dateShortFormat(Object time) {
		if (time == null)
			return "";

		Long value = null;
		if (time instanceof Double) {
			value = ((Double) time).longValue();
		} else if (time instanceof Integer) {
			value = ((Integer) time).longValue();
		} else {
			value = (Long) time;
		}
		if (value.toString().length() == 10) {
			value = value * 1000;
		}

		return dateShortFormat.get().format(new Date(value));
	}
	
	/**
	 * Double/Integer/Long --> String(yyyyMMddHHmmss)
	 * @param time
	 * @return
	 */
	public static String dateLongFormat(Object time) {
		if (time == null)
			return "";

		Long value = null;
		if (time instanceof Double) {
			value = ((Double) time).longValue();
		} else if (time instanceof Integer) {
			value = ((Integer) time).longValue();
		} else {
			value = (Long) time;
		}
		if (value.toString().length() == 10) {
			value = value * 1000;
		}

		return TimeShortFormat.get().format(new Date(value));
	}
	
	/**
	 * yyyyMMddHHmmss转yyyy-mm-dd ss:hh:ss
	 */
	public static String stringtoDate(String date){
		String reg="(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})";
		date=date.replaceAll(reg,"$1-$2-$3 $4:$5:$6");
		return date;
	}
	
	
	/**
	 * Double/Integer/Long --> String(yyyy/MM/dd)
	 * @param time
	 * @return
	 */
	public static String dateFormatSlash(Object time){
		if(time == null) return "";
		
		Long value = null;
		if(time instanceof Double) {
			value = ((Double)time).longValue();
		} else if(time instanceof Integer) {
			value = ((Integer)time).longValue();
		} else {
			value = (Long) time;
		}
		if(value.toString().length() == 10) value = value*1000;
		
		return dateFormatSlash.get().format(new Date(value));
	}
	
	/**
	 * Date --> String(yyyy-MM-dd HH:mm:ss)
	 * @param date
	 * @return
	 */
	public static String dateTimeFormat(Date date) {
		if (date == null){
			return "";
		}
		return dateTimeFormat.get().format(date);
	}
	
	/**
	 * Double/Integer/Long --> String(yyyy-MM-dd HH:mm:ss)
	 * @param time
	 * @return
	 */
	public static String dateTimeFormat(Object time){
		if(time == null) return "";
		
		Long value = null;
		if(time instanceof Double) {
			value = ((Double)time).longValue();
		} else if(time instanceof Integer) {
			value = ((Integer)time).longValue();
		} else if(time instanceof BigInteger) {
			value = ((BigInteger)time).longValue();
		} else {
			value = (Long) time;
		}
		
		if(value.toString().length() == 10) value = value*1000;
		
		return dateTimeFormat.get().format(new Date(value));
	}
	
	@SuppressWarnings("deprecation")
	public static Date dateParse(Long date) {
		if(date == null) return null;
		
		Date newDate = new Date(date*1000);
		newDate.setHours(0);
		newDate.setMinutes(0);
		newDate.setSeconds(0);
		
		return newDate;
	}
	
	public static Date dateParse(String date) {
		if (StringUtils.isBlank(date)) {
			return null;
		}
		try {
			return dateFormat.get().parse(date);
		} catch (ParseException e) {
		}
		
		return null;
	}

	public static Date timeParse(String date) {
		if (StringUtils.isBlank(date)) {
			return null;
		}
		try {
			return dateTimeFormat.get().parse(date);
		} catch (ParseException e) {
		}
		return null;
	}
	/**
	 * 获取时间戳 精确到秒
	 */
	public static Long getCurrentTime() {
		return System.currentTimeMillis()/1000;
	}
	/**
	 * 获取时间戳 精确到毫秒
	 */
	public static Long getCurrentTimeMS() {
		return System.currentTimeMillis();
	}
	public static Date getCurrentDate() {
		try {
			return dateFormat.get().parse(dateFormat.get().format(new Date()));
		} catch (ParseException e) {
		}
		return null;
	}
	/**
	 * 获取简化格式日期 ps:20170511
	 * @return
	 * @author masai
	 * @time 2017年5月11日 下午4:18:58
	 */
	public static String getCurrentDateStr() {
		return dateShortFormat.get().format(new Date());
	}
	/**
	 * 获取简化格式时间 ps:20170511162033
	 * @return
	 * @author masai
	 * @time 2017年5月11日 下午4:20:07
	 */
	public static String getCurrentTimeStr(){
		return TimeShortFormat.get().format(new Date());
	}
	/**
	 * 日期装long
	 * @param date
	 * @return
	 */
	public static Long convert(Date date) {
		if(date == null) return null;
		
		return date.getTime()/1000;
	}
	
	/**
	 * 日期字符串转long
	 * @param date
	 * @return
	 */
	public static Long convert(String date) {
		if(date == null) return null;
		
		try {
			if(date.length() > 10) 
				return (dateTimeFormat.get().parse(date)).getTime()/1000;
			
			return (dateFormat.get().parse(date)).getTime()/1000;
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * 在给定的时间上加/减XX天
	 * @param date 当前时间
	 * @param days 要加/减的天数
	 * @return
	 */
	public static Date addDay(Date date, Integer days) {
		if(date == null || days == null || days == 0) return date;
		
		Calendar calendar = Calendar.getInstance();   
		calendar.setTime(date); 
		calendar.add(Calendar.DATE, days);
		
		return calendar.getTime();
	}
	
	/**
	 * 在给定的时间上加/减XX天
	 * @param date 当前时间
	 * @param days 要加/减的天数
	 * @return
	 */
	public static Long addDay(Long date, Integer days) {
		if(date == null || days == null || days == 0) return date;
		
		Calendar calendar = Calendar.getInstance();   
		calendar.setTimeInMillis(date*1000);
		calendar.add(Calendar.DATE, days);
		
		return calendar.getTimeInMillis()/1000;
	}
	
	/**
	 * 在给定的时间上加/减XX个月
	 * @param date 当前时间
	 * @param months 要加/减的月数
	 * @return
	 */
	public static Date addMonth(Date date, Integer months) {
		if(date == null || months == null || months == 0) return date;
		
		Calendar calendar = Calendar.getInstance();   
		calendar.setTime(date); 
		calendar.add(Calendar.MONTH, months);
		
		return calendar.getTime();
	}
	
	/**
	 * 在给定的时间上加/减XX个月
	 * @param date 当前时间
	 * @param months 要加/减的月数
	 * @return
	 */
	public static Long addMonth(Long date, Integer months) {
		if(date == null || months == null || months == 0) return date;
		
		Calendar calendar = Calendar.getInstance();   
		calendar.setTimeInMillis(date*1000); 
		calendar.add(Calendar.MONTH, months);
		
		return calendar.getTimeInMillis()/1000;
	}
	


	/**
	 * 计算两个日期之间相差的天数
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int daysBetween(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		long time1 = cal.getTimeInMillis();
		cal.setTime(date2);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	} 
	
	/**
	 * 计算两个日期之间相差的天数,超过30天的月份按30天算(计算利息用)
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int daysBetweenMore(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		int date1Year = cal.get(Calendar.YEAR);//得到年
		int date1Month = cal.get(Calendar.MONTH) + 1;//得到月
		long time1 = cal.getTimeInMillis();
		cal.setTime(date2);
		int date2Year = cal.get(Calendar.YEAR);//得到年
		int date2Month = cal.get(Calendar.MONTH) + 1;//得到月
		int date2Day = cal.get(Calendar.DAY_OF_MONTH);//得到日
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		int days = 0;
		if(date1Year == date2Year){//相同年份的时候
			for(int i = date1Month;i <= date2Month; i++){
				if("1,3,5,7,8,10,12".contains(String.valueOf(i))){
					days = days + 1;	
				}
			}
			if(date2Day <= 30){
				days = days -1;
			}
		}else{//不同年份的时候，如2015-6-1 至2016-5-1
			//计算两个时间相差月数
			int months = (date2Year - date1Year)*12 + (date2Month - date1Month);
			for(int i = date1Month; i <= (date1Month + months); i++){
				int j = 0;
				if(i/12 > 0){
					j = i - 12*(i/12);
				}
				if("1,3,5,7,8,10,12".contains(String.valueOf(j == 0 ? i : j))){
					days = days + 1;	
				}
			}
			if(date2Day <= 30){
				days = days -1;
			}
		}
		return Integer.parseInt(String.valueOf(between_days-days));
	} 

	/**
	 * 计算两个日期之间相差的月数
	 */
	public static int monthBetween(Date date1, Date date2){
		Calendar bef = Calendar.getInstance();
		Calendar aft = Calendar.getInstance();
		bef.setTime(date1);
		aft.setTime(date2);
		int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
		return result;
	}
	
	/**
	 * 计算两个日期之间相差几分钟
	 */
	public static long minuteBetween(Long date1,Long date2){
		long s = (date2 - date1) / 60;
		return s;
	}
	/**
	 * 获取当前月份的第一天
	 * @param date
	 * @return
	 */
	public static Long getMonthFirstDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));     
	    calendar.set(Calendar.HOUR_OF_DAY, 0);  
	    calendar.set(Calendar.MINUTE, 0);  
	    calendar.set(Calendar.SECOND,0);  
	    return calendar.getTimeInMillis()/1000; 
	}
	/**
	 * 获取当前月份偏移amount个月后的该月份的第一天  
	 * @author masai  2016年10月10日 下午4:11:46
	 * @param date  当前日期
	 * @param amount 月份偏移量  -1 表示上个月第一天，0表示当月，1表示下个月第一天
	 * @return
	 */
	public static Long getMonthFirstDay(Date date,int amount){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, amount);
	    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));     
	    calendar.set(Calendar.HOUR_OF_DAY, 0);  
	    calendar.set(Calendar.MINUTE, 0);  
	    calendar.set(Calendar.SECOND,0);  
	    return calendar.getTimeInMillis()/1000; 
	}
	/**
	 * 获得当前月份的最后一天
	 * @author masai  2016年10月10日 下午8:00:08
	 * @param date  当前日期
	 * @return
	 */
	public static Long getMonthLastDay(Date date){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH,0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);  
	    calendar.set(Calendar.MINUTE, 0);  
	    calendar.set(Calendar.SECOND,0);  
		return calendar.getTimeInMillis()/1000; 
	}
	/**
	 * 获取当前月份偏移amount个月后的该月份的最后一天  
	 * @author masai  2016年10月10日 下午8:02:35
	 * @param date
	 * @param amount 月份偏移量  -1 表示上个月最后一天，0表示当月，1表示下个月最后一天
	 * @return
	 */
	public static Long getMonthLastDay(Date date,int amount){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, amount);
		calendar.set(Calendar.DAY_OF_MONTH,0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);  
	    calendar.set(Calendar.MINUTE, 0);  
	    calendar.set(Calendar.SECOND,0);  
		return calendar.getTimeInMillis()/1000; 
	}
	
	/**
	 * 获取指定时间(0点0分0秒)
	 * @param date
	 * @param type -1：上一天 0：当天 1：下一天
	 * @return
	 */
	public static Long getDateTime(Date date, int type) {
		if(date == null) date = new Date();
		date = addDay(date, type);
		
		String strDate = dateFormat.get().format(date) + " 00:00:00";
		try {
			return dateTimeFormat.get().parse(strDate).getTime()/1000;
		} catch (ParseException e) {
		}
		
		return null;
	}
	

	/**
	 * 获取当天00：00：00
	 * @param date
	 * @return
	 */
	public static Long getDateIntiTime(Long time){
		String strTime=dateTimeFormat(time).substring(0, 10);
		return convert(strTime);
	}
	/**
	 * 获取当天23：59：59
	 * @param date
	 * @return
	 */
	public static Long getDateLastTime(Long time){
		String strTime=dateTimeFormat(time).substring(0, 10);
		return convert(strTime)+(3600*24L)-1L;
	}
	
	/**
	 * 根据时间获取23：59:59
	 */
	public static Date getDateLastDate(Date date){
		Calendar calendar = Calendar.getInstance();    
	    calendar.setTime(addDay(date, 1));    
	    calendar.add(Calendar.SECOND, -1); 
		return calendar.getTime();
	}
}