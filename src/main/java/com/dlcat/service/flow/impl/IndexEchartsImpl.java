package com.dlcat.service.flow.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.core.model.CuObjectCustomer;
import com.dlcat.core.model.SysOrg;
import com.dlcat.core.model.SysUser;
import com.dlcat.service.flow.IndexEchartsService;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class IndexEchartsImpl implements IndexEchartsService {

	public Map<String, Integer> getPieData(SysUser user) {
		// TODO Auto-generated method stub
		//客户统计 :只统计本节点和以下机构
				int org_id=user.getInt("belong_org_id");
				int level=SysOrg.getLevel(org_id);
				if (level==-1) {
					return null;
				}
				
				//int level=3;
				List<CuObjectCustomer> list=null;
				//1为总部
				if (level==1) {
					list=CuObjectCustomer.getAll();
				//2分部
				}else if (level==2) {
				//获取分部以及支部
					list=CuObjectCustomer.getCuObjectCustomers(SysOrg.getDeptAndChildren(org_id));
				//3支部
				}else if (level==3) {
					list=SysOrg.getBelongCustomer(org_id);
				}
				
				int black=0,individual=0,company=0;
				
				for (CuObjectCustomer cuObjectCustomer : list) {
					String status=cuObjectCustomer.getStr("status");
					//3为黑名单
					if (status.equals("3")) {
						black++;
						
					//1为正常
					}else if (status.equals("1")) {
						String type=cuObjectCustomer.getStr("type");
						//01为公司客户
						if (type.equals("01")) {
							company++;
							
						//02为个人客户
						}else if (type.equals("02")) {
							individual++;
						}
						
						
					}
					
				}
				//填充圆饼图数据
				Map<String, Integer> pie=new HashMap<String, Integer>();
				pie.put("黑名单", black);
				pie.put("个人客户", individual);
				pie.put("对公客户", company);
				
				return pie;
	}

	public Map<String, List<Number>> getMonthNum(String year, SysUser user) {
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
		
		String where=getWhere(user);
		
		String sql=""; 
		String sql2="";
		for (int i = 0; i < 12; i++) {
			//统计每月的成交笔数
			sql="SELECT COUNT(t.id) " +
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

	public String getWhere(SysUser user) {
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

	public Map<String, Number> getMdNum(String defaultYear, SysUser user) {
		Map<String, Number> map=new HashMap<String, Number>();
		int org_id=user.getInt("belong_org_id");
        int level=SysOrg.getLevel(org_id);
		
		//添加条件约束
		String ids="";
		
		//1为总部
		if (level==1) {
			//总部可以看任意数据
			ids=SysOrg.getChildrenIds(org_id);
		//2分部
		}else if (level==2) {
		//获取分部以及支部
			  ids=SysOrg.getChildrenIds(org_id);
		//3支部
		}else if (level==3) {
			ids=org_id+"";
		}
		
		String[] arr=ids.split(",");
		String sql="";
		for (String id : arr) {
			int id1=Integer.parseInt(id);
			StringBuilder sb=new StringBuilder();
			sb.append(id);
			String orgName=SysOrg.getNameById(id1);
			List<SysOrg> children=SysOrg.getChildren(id1);
			if (children.size()>0) {
				for (SysOrg sysOrg : children) {
					sb.append(","+sysOrg.getInt("org_id"));
				}
			}
			//查出对应月数当前所有门店的月总额
			sql="SELECT  SUM(t.amount)\n" +
					"FROM loan_apply_approve t \n" +
					"WHERE LEFT(FROM_UNIXTIME(t.apply_time),7)='"+defaultYear+"' AND t.apply_org_id in ("+sb.toString()+")";
			Object object=Db.queryFirst(sql);
			if (object!=null) {
				map.put(orgName,Float.parseFloat(object.toString()));
			}else {
				map.put(orgName, 0);
			}
			
		}
		return map;
	}

	public String[] getyear(SysUser user) {
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

	public String[] getYear_mouth(SysUser user) {
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

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
