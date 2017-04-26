package com.dlcat.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.core.model.LoanApplyApprove;
import com.dlcat.core.model.SysUser;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

/** 
* @author zhaozhongyuan
* @date 2017年4月25日 上午9:18:21 
* @Description: 添加所有model的一些常用方法
* @param <M> 
*/
public class BaseModel<M extends Model<M>> extends Model<M> implements IBean {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	* @author:zhaozhongyuan 
	* @Description: 根据年份去找出相应的12个月的数据
	* @param @return
	* @return List<Integer>
	* @date 2017年4月26日 上午11:44:11  
	*/
	public static Map<String, List<Number>> getMonthNum(String year,SysUser user) {
		Map<String, List<Number>> map=new HashMap<String, List<Number>>();
		
		//存放1-12月每个月的成交笔数
		List<Number> success=new ArrayList<Number>();
		
		//存放1-12月每个月的借款总额
		List<Number> amounts=new ArrayList<Number>();
		
		String[] months={"01","02","03","04","05","06","07","08","09","10","11","12"};
		String[] year_month=new String[12];
		for (int i = 0; i < 12; i++) {
			year_month[i]=year+"-"+months[i];
		}
		
		String where=LoanApplyApprove.getWhere(user);
		
		String sql="";
		String sql2="";
		for (int i = 0; i < 12; i++) {
			//统计每月的成交笔数
			sql="SELECT COUNT(t.id) count " +
					"FROM loan_apply_approve t " +
					"WHERE LEFT(FROM_UNIXTIME(t.apply_time),7)='"+year_month[i]+"' AND t.status='1'"+where;
			//统计每个月的借款总额
			sql2="SELECT  SUM(t.amount) FROM loan_apply_approve t WHERE LEFT(FROM_UNIXTIME(t.apply_time),7)='"+year_month[i]+"'"+where;
			success.add(Integer.parseInt(Db.queryFirst(sql).toString()));
			Object object=Db.queryFirst(sql2);
			if (object!=null) {
				amounts.add(Float.parseFloat(object.toString()));
			}else {
				amounts.add(0);
			}
		}
		map.put("成交笔数", success);
		map.put("借款总额", amounts);
		
		return map;
	}
}
