package com.dlcat.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseModel;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class SysMenu extends BaseModel<SysMenu> {
	public static final SysMenu dao = new SysMenu().dao();
	
	/**
	* @author:zhaozhongyuan 
	* @Description:根据菜单的ID获取名字
	* @return String   
	* @date 2017年5月26日 上午11:04:16  
	*/
	public static String getNamebyID(String btnId) {
		String name="";
		SysMenu sysMenu=dao.findById(btnId);
		if (sysMenu!=null) {
			name=sysMenu.getStr("name");
		}
		
		return name;
		
	}
	
	
	
	/**
	 * 用于递归查询菜单
	 */
	private List<SysMenu> children=new ArrayList<SysMenu>();

	public List<SysMenu> getChildren() {

		return children;
	}

	public void setChildren(List<SysMenu> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return super.toString()+"SysMenu [children=" + children + "]";
	}
	
	/**
	 * 获取用户菜单集合，某个节点的一级子节点
	 * @param menuId
	 * @param sysMenu
	 * @return
	 * @author masai
	 * @throws Exception 
	 * @time 2017年4月16日 下午10:04:56
	 */
	public static List<SysMenu> getFristChildNode(String menuId,Map<Integer, SysMenu> menuMap) throws Exception{
		List<SysMenu> menuList = new ArrayList<SysMenu>();
		Collection<SysMenu> values = menuMap.values();
		for (SysMenu sysMenu : values) {
			if (sysMenu.getInt("pid") == Integer.parseInt(menuId)) {
				menuList.add(sysMenu);
			}
		}
		//排序
		Comparator<SysMenu> comparator=new Comparator<SysMenu>() {
			public int compare(SysMenu s1, SysMenu s2) {
				return s1.getInt("sort_no")-s2.getInt("sort_no");
			}
		};
		Collections.sort(menuList, comparator);
		return menuList;
	}
}
