package com.dlcat.service.echarts.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseService;
import com.dlcat.core.model.CuObjectCustomer;
import com.dlcat.core.model.FlowModel;
import com.dlcat.core.model.FlowNode;
import com.dlcat.core.model.SysOrg;
import com.dlcat.core.model.SysUser;
import com.dlcat.service.echarts.IndexEchartsService;
import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class IndexEchartsImpl extends BaseService implements IndexEchartsService {

	public Map<String, Integer> getPieData(SysUser user) {
		// TODO Auto-generated method stub
		// 客户统计 :只统计本节点和以下机构
		int org_id = user.getInt("belong_org_id");

		int like = org_id;
		// 判断level
		int level = SysOrg.getLevel(org_id);

		if (level == 2 || level == 4 || level == 5) {
			like = SysOrg.getPid(org_id);
		}

		List<CuObjectCustomer> list = CuObjectCustomer.dao
				.find("select * from cu_object_customer where belong_org_id like '" + like + "%'");

		int black = 0, individual = 0, company = 0;

		for (CuObjectCustomer cuObjectCustomer : list) {
			String status = cuObjectCustomer.getStr("status");
			// 3为黑名单
			if (status.equals("2")) {
				black++;

				// 1为正常
			} else if (status.equals("1")) {
				String type = cuObjectCustomer.getStr("type");
				// 01为公司客户
				if (type.equals("1")) {
					company++;

					// 02为个人客户
				} else if (type.equals("2")) {
					individual++;
				}

			}

		}
		// 填充圆饼图数据
		Map<String, Integer> pie = new HashMap<String, Integer>();
		pie.put("黑名单", black);
		pie.put("个人客户", individual);
		pie.put("对公客户", company);

		return pie;

	}

	public Map<String, List<Number>> getMonthNum(String year, SysUser user) {
		Map<String, List<Number>> map = new HashMap<String, List<Number>>();

		// 存放1-12月每个月的成交笔数
		List<Number> success = new ArrayList<Number>();

		// 存放1-12月每个月的借款总额
		List<Number> amounts = new ArrayList<Number>();

		String[] months = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
		String[] year_month = new String[12];
		
		
		for (int i = 0; i < 12; i++) {
			year_month[i] = year + "-" + months[i];
		}
		int org_id = user.getInt("belong_org_id");
		
		int like=org_id;
		//判断level
		int level = SysOrg.getLevel(org_id);
		
		if (level==2||level==4||level==5) {
			like=SysOrg.getPid(org_id);
		}
		
		String where = " and apply_org_id like '" + like + "%'";

		String sql="SELECT FROM_UNIXTIME(t.apply_time,'%Y-%m') month,COUNT(t.id) count,SUM(t.amount) sum FROM loan_apply_approve t WHERE FROM_UNIXTIME(t.apply_time,'%Y')='"+year+"' "+where+" GROUP BY FROM_UNIXTIME(t.apply_time,'%Y-%m')";
		
		Map<String, Record> monthMap=new HashMap<String, Record>();
		
		List<Object> list = Db.query(sql);
		for (Object object : list) {
			String string=JsonKit.toJson(object);
			String[] record=string.substring(1, string.length()-1).replaceAll("\"", "").split(",");
			Record data=new Record();
			data.set("count", Integer.parseInt(record[1]));
			data.set("sum", Float.parseFloat(record[2]));
			monthMap.put(record[0], data);
		}
		
		
		for (int i = 0; i < year_month.length; i++) {
			
			if (monthMap.containsKey(year_month[i])) {
				success.add(monthMap.get(year_month[i]).getNumber("count"));
				amounts.add(monthMap.get(year_month[i]).getNumber("sum"));
			}else {
				success.add(0);
				amounts.add(0);
			}
			
		}
		map.put("成交笔数", success);
		map.put("借款总额", amounts);

		return map;
	}

	public Map<String, Number> getMdNum(String defaultYear, SysUser user) {
		Map<String, Number> map = new HashMap<String, Number>();
		int org_id = user.getInt("belong_org_id");
		int level = SysOrg.getLevel(org_id);
		if (level != -1) {
			// 添加条件约束
			List<Integer> ids = null;

			// 1为总部
			if (level == 1) {
				// 总部可以看分部数据
				ids = SysOrg.getChildrenIds(org_id, "and org_level=3");
				// 总部部门看分部数据
			} else if (level == 2) {
				ids = SysOrg.getChildrenIds(SysOrg.getPid(org_id), "and org_level=3");
				// 3支部
			} else if (level == 3) {
				// 获取分部以及支部
				ids = SysOrg.getChildrenIds(org_id, "and org_level=5");
				// 3支部
			} else if (level == 5 || level == 4) {
				// 获取pid
				int pid = SysOrg.getPid(org_id);
				ids = SysOrg.getChildrenIds(pid, "and org_level=5");
			}

			String sql = "";
			for (Integer id : ids) {
				String orgName = SysOrg.getNameById(id);

				// 查出对应月数当前所有门店的月总额
				sql = "SELECT  SUM(t.amount)\n" + "FROM loan_apply_approve t \n"
						+ "WHERE FROM_UNIXTIME(t.apply_time,'%Y-%m')='" + defaultYear + "' AND t.apply_org_id like '"
						+ id + "%'";
				Object object = Db.queryFirst(sql);

				if (object != null) {
					map.put(orgName, Float.parseFloat(object.toString()));
				} else {
					map.put(orgName, 0);
				}

			}
			return map;
		}

		return null;
	}

	public String[] getyear(SysUser user) {
		// 获取约束条件
		int org_id = user.getInt("belong_org_id");

		int like = org_id;
		// 判断level
		int level = SysOrg.getLevel(org_id);

		if (level == 2 || level == 4 || level == 5) {
			like = SysOrg.getPid(org_id);
		}

		String where = " and apply_org_id like '" + like + "%'";

		String sql = "SELECT FROM_UNIXTIME(t.apply_time,'%Y') year FROM loan_apply_approve t where 1=1 " + where
				+ " GROUP BY FROM_UNIXTIME(t.apply_time,'%Y') ORDER BY FROM_UNIXTIME(t.apply_time,'%Y') DESC";
		List<Record> list = Db.find(sql);
		String[] years = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			years[i] = list.get(i).getStr("year");
		}

		return years;
	}

	public String[] getYear_mouth(SysUser user) {
		// 获取约束条件
		int org_id = user.getInt("belong_org_id");
		int like = org_id;
		// 判断level
		int level = SysOrg.getLevel(org_id);

		if (level == 2 || level == 4 || level == 5) {
			like = SysOrg.getPid(org_id);
		}

		String where = "and apply_org_id like '" + like + "%'";

		String sql = "SELECT FROM_UNIXTIME(t.apply_time,'%Y-%m') year FROM loan_apply_approve t where 1=1 " + where
				+ " GROUP BY FROM_UNIXTIME(t.apply_time,'%Y-%m') ORDER BY FROM_UNIXTIME(t.apply_time,'%Y-%m') DESC";
		List<Record> list = Db.find(sql);
		String[] years = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			years[i] = list.get(i).getStr("year");
		}

		return years;
	}

	public List<Map<String, Object>> flownodes() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 获取model
		String getflowmodelsql = "select * from flow_model";
		List<FlowModel> models = FlowModel.getFlowModelsBySql(getflowmodelsql);

		for (FlowModel mode : models) {
			Map<String, Object> flowMap = new HashMap<String, Object>();
			List<FlowNode> nodes = FlowNode.getFlowNodeByModelId(mode.getInt("id"));
			flowMap.put("modelName", mode.getStr("name"));
			flowMap.put("nodes", nodes);
			list.add(flowMap);
		}
		;
		return list;
	}

}
