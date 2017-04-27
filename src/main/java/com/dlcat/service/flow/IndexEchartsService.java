package com.dlcat.service.flow;

import java.util.List;
import java.util.Map;

import com.dlcat.core.model.SysUser;

public interface IndexEchartsService {

	public Map<String, Integer> getPieData(SysUser user);
	
	public Map<String, List<Number>> getMonthNum(String year,SysUser user);
	
	public String getWhere(SysUser user);
	
	public Map<String, Number> getMdNum(String defaultYear, SysUser user);
	
	public String[] getyear(SysUser user);
	
	public String[] getYear_mouth(SysUser user);
	
	
}
