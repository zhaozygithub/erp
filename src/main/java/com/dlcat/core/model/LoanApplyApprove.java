package com.dlcat.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseModel;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

/**
 * Generated by JFinal.
 */
/** 
* @author zhaozhongyuan
* @date 2017年4月26日 下午7:33:33 
* @Description: TODO  
*/
@SuppressWarnings("serial")
public class LoanApplyApprove extends BaseModel<LoanApplyApprove>{
	public static final LoanApplyApprove dao = new LoanApplyApprove().dao();
	
	/**
	* @author:zhaozhongyuan 
	* @Description: TODO 
	* @param  4查不同的年份
	* @return 返回本表中所有不同的年份
	* @return List<LoanApplyApprove>
	* @date 2017年4月26日 上午11:09:30  
	*/
	public static String[] getyear(SysUser user) {
		//获取约束条件
		String where=getWhere(user);
		
		String sql="SELECT LEFT(FROM_UNIXTIME(t.apply_time),4) year FROM loan_apply_approve t where 1=1 "+where+" GROUP BY LEFT(FROM_UNIXTIME(t.apply_time),4) ORDER BY LEFT(FROM_UNIXTIME(t.apply_time),4) DESC";
		List<Record> list=Db.find(sql);
		String[] years=new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			years[i]=list.get(i).getStr("year");
		}
		
		return years;
	}
	
	/**
	* @author:zhaozhongyuan 
	* @Description: 获取年份加月份
	* @return String[]   
	* @date 2017年4月26日 下午8:24:56  
	*/
	public static String[] getYear_mouth(SysUser user) {
		//获取约束条件
		String where=getWhere(user);
		
		String sql="SELECT LEFT(FROM_UNIXTIME(t.apply_time),7) year FROM loan_apply_approve t where 1=1 "+where+" GROUP BY LEFT(FROM_UNIXTIME(t.apply_time),7) ORDER BY LEFT(FROM_UNIXTIME(t.apply_time),7) DESC";
		List<Record> list=Db.find(sql);
		String[] years=new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			years[i]=list.get(i).getStr("year");
		}
		
		return years;
	}
	

	/**
	* @author:zhaozhongyuan 
	* @Description:
	* @return String   
	* @date 2017年4月26日 下午7:47:44  
	*/
	public static String getWhere(SysUser user) {
		int org_id=user.getInt("belong_org_id");
		int level=SysOrg.getLevel(org_id);
		
		//添加条件约束
		String where="";
		
		//1为总部
		if (level==1) {
			//总部可以看任意数据
		//2分部
		}else if (level==2) {
		//获取分部以及支部
			  where=" and apply_org_id in ("+SysOrg.getDeptAndChildren(org_id)+")";
		//3支部
		}else if (level==3) {
			where=" and apply_org_id = "+org_id;
		}
		return where;
	}

	public static Map<String, Number> getMdNum(String defaultYear, SysUser user) {
		// TODO Auto-generated method stub
		Map<String, Number> map=new HashMap<String, Number>();
		int org_id=user.getInt("belong_org_id");
        int level=SysOrg.getLevel(org_id);
		
		//添加条件约束
		String ids="";
		
		//1为总部
		if (level==1) {
			//总部可以看任意数据
			List<Record> list=Db.query("select org_id id from sys_org");
			for (Record record : list) {
				ids+=record.getInt("id")+",";
			}
		//2分部
		}else if (level==2) {
		//获取分部以及支部
			  ids=SysOrg.getDeptAndChildren(org_id);
		//3支部
		}else if (level==3) {
			ids=org_id+"";
		}
		
		String[] arr=ids.split(",");
		String sql="";
		for (String id : arr) {
			int id1=Integer.parseInt(id);
			String orgName=SysOrg.getNameById(id1);
			
			sql="SELECT  SUM(t.amount)\n" +
					"FROM loan_apply_approve t \n" +
					"WHERE LEFT(FROM_UNIXTIME(t.apply_time),7)='"+defaultYear+"' AND t.apply_org_id="+id1;
			
			
			
			
			
			//map.put(orgName, value);
		}
		
		
		
		
		
		//String where=getWhere(user);
		
		
		
		
		return map;
	}
	
	
}
