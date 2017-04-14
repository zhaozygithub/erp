package com.dlcat.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("cu_corp_info", "cu_corp_id", CuCorpInfo.class);
		arp.addMapping("cu_ind_info", "cu_ind_id", CuIndInfo.class);
		arp.addMapping("cu_object_customer", "id", CuObjectCustomer.class);
		arp.addMapping("cu_partners", "id", CuPartners.class);
		arp.addMapping("cu_possible_customer", "id", CuPossibleCustomer.class);
		arp.addMapping("cu_property", "id", CuProperty.class);
		arp.addMapping("flow_data", "id", FlowData.class);
		arp.addMapping("flow_model", "id", FlowModel.class);
		arp.addMapping("flow_node", "node_no", FlowNode.class);
		arp.addMapping("flow_object", "object_no", FlowObject.class);
		arp.addMapping("flow_task", "task_no", FlowTask.class);
		arp.addMapping("loan_apply_approve", "id", LoanApplyApprove.class);
		arp.addMapping("loan_apply_approve_data", "id", LoanApplyApproveData.class);
		arp.addMapping("loan_business_category", "id", LoanBusinessCategory.class);
		arp.addMapping("loan_product_category", "id", LoanProductCategory.class);
		arp.addMapping("loan_repay_type", "id", LoanRepayType.class);
		arp.addMapping("sys_menu", "id", SysMenu.class);
		arp.addMapping("sys_org", "org_id", SysOrg.class);
		arp.addMapping("sys_role", "id", SysRole.class);
		arp.addMapping("sys_user", "id", SysUser.class);
		// Composite Primary Key order: code_no,item_no
		arp.addMapping("to_code_library", "code_no,item_no", ToCodeLibrary.class);
		arp.addMapping("to_task", "id", ToTask.class);
	}
}

