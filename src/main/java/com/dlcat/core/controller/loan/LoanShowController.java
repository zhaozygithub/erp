package com.dlcat.core.controller.loan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.common.entity.DyResponse;
import com.dlcat.common.entity.QueryItem;
import com.dlcat.common.entity.QueryWhere;
import com.dlcat.common.entity.Search;
import com.dlcat.common.entity.TableHeader;
import com.dlcat.common.utils.PageUtil;
import com.dlcat.common.utils.StringUtils;
import com.dlcat.core.model.LoanApplyApprove;
import com.dlcat.core.model.SysMenu;
/**
 * 借款列表展示
 * @author masai
 * @time 2017年5月22日 下午2:50:49
 */
public class LoanShowController extends BaseController {
	
	/**
	 * 展示借款申请列表
	 * @author masai
	 * @time 2017年5月24日 下午8:16:34
	 */
	public void showLoanApprovePage(){
		//要求：所有借款 和 我发起的借款：构造页面的url是一个；给出数据的url也是一个
		//思路：1.通过url后面拼接参数来控制  构造的页面是 所有借款  or   我的借款
		TableHeader tableHeader = new TableHeader();
		tableHeader.setFieldNames(new String[]{"id","loan_name","loan_product_id","cu_id","cu_name","card_type","card_id","amount","period","apr","repay_type","collateral_type","collateral_no","apply_user_name","apply_org_name","apply_time"});
		tableHeader.setCNNames(new String []{"借款编号","借款名称","产品种类","借款客户编号","借款客户名称","证件类型","证件号码","金额","期数(月)","年化收益(%)","还款方式","抵押物类型","抵押物编号","申请人","申请机构","申请时间"});
		tableHeader.setMultiple(false);
		
		Search search = new Search();
		search.setFieldNames(new String[]{"id","loan_name","cu_id","cu_name","apply_user_name","apply_org_name","sta_apply_time","end_apply_time"});
		search.setCNNames(new String[]{"借款编号","借款名称","客户编号","客户名称","发起人","发起机构","发起开始时间","发起结束时间"});
		search.setTypes(new String[]{"text","text","text","text","text","text","date","date"});
		//第一个参数为1表示我发起的借款，为2表示当前机构发起的借款，为0表示所有借款
		String dataUrl = "/loan/getLoanApproveData/";
		String applyObject = this.getPara(0);
		//如果参数不为0、1、2 则默认为 1
		if(StringUtils.isBlank(applyObject) || 
				!(applyObject.equals("0") || applyObject.equals("1") || applyObject.equals("2"))){
			dataUrl += "1";
		}else{
			dataUrl += applyObject;
		}
		DyResponse response = null;
		try {
			response = PageUtil.createTablePageStructure(dataUrl, "借款申请列表", tableHeader, search, 
					getLastPara().toString(), (Map<Integer, SysMenu>)this.getSessionAttr("menus"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setAttr("response", response);
		this.render("common/table.html");
	}
	/**
	 * 借款申请数据
	 * @author masai
	 * @time 2017年5月25日 下午12:37:59
	 */
	public void getLoanApproveData(){
		//获取查询数据类型  0表示所有借款，1表示我发起的借款（有效），10表示我发起的借款（无效），
		//2表示当前机构及其下属机构发起的借款，20表示当前机构发起的借款
	   String temp = this.getPara(0);
	   if(StringUtils.isBlank(temp)){
		   try {
			   throw new Exception("借款数据无法查询，请检查参数");
		   } catch (Exception e) {
			   e.printStackTrace();
		   }
	   }
	   String conditation = this.getPara("condition");
	   Map whereMap = super.getWhereMap(conditation);
	   //校验时间是否合法	结束时间>=开始时间
	   if(whereMap.get("sta_apply_time") != null && whereMap.get("end_apply_time") != null){
		   if(Long.valueOf(whereMap.get("sta_apply_time").toString()) > Long.valueOf(whereMap.get("end_apply_time").toString())){
			   renderJson(createErrorJsonResonse("开始时间不能大于结束时间"));
		   }
	   }
	   QueryItem item = new QueryItem();
	   item.setTableNames("loan_apply_approve");
	   item.setLimit(10);
	   item.setFields("*");
	   item.setOrder("apply_time desc");
	   List<QueryWhere> whereList = new ArrayList<QueryWhere>();
	   if(whereMap != null){
		   whereList.add(new QueryWhere("id", whereMap.get("id")));
		   whereList.add(new QueryWhere("loan_name", LIKE_RIGHT , whereMap.get("loan_name")));
		   whereList.add(new QueryWhere("cu_id",whereMap.get("cu_id")));
		   whereList.add(new QueryWhere("cu_name",LIKE_RIGHT , whereMap.get("cu_name")));
		   whereList.add(new QueryWhere("apply_user_name",LIKE_RIGHT , whereMap.get("apply_user_name")));
		   whereList.add(new QueryWhere("apply_org_name", LIKE_RIGHT , whereMap.get("apply_org_name")));
		   //时间限制
		   if(whereMap.get("sta_apply_time") != null && whereMap.get("end_apply_time") == null){
			   whereList.add(new QueryWhere("apply_time",GE , whereMap.get("sta_apply_time")));
		   }else if(whereMap.get("sta_apply_time") == null && whereMap.get("end_apply_time") != null){
			   whereList.add(new QueryWhere("apply_time",LE , whereMap.get("end_apply_time")));
		   }else{
			   whereList.add(new QueryWhere("apply_time",GE , whereMap.get("sta_apply_time")));
			   whereList.add(new QueryWhere("apply_time",LE , whereMap.get("end_apply_time")));
		   }
	   }
	   if(temp.startsWith("1")){//我发起的借款申请
		   whereList.add(new QueryWhere("apply_user_id" , getCurrentUser().getStr("id")));
		   if(temp.equals("1")){//有效借款
			   whereList.add(new QueryWhere("status","-10"));
		   }else if(temp.equals("10")){//无效借款
			   whereList.add(new QueryWhere("status","10"));
		   }
	   }else if(temp.startsWith("2")){//机构发起的借款申请
		   if(temp.equals("2")){
			   //级联机构(当前机构及其下属机构)借款
			   whereList.add(new QueryWhere("apply_org_id", LIKE_RIGHT , getCurrentUser().getStr("belong_org_id")));
		   }else if(temp.equals("20")){
			   //当前机构借款
			   whereList.add(new QueryWhere("apply_org_id",getCurrentUser().getStr("belong_org_id")));
		   }
	   }else if(temp.equals("0")){//所有借款，不加任何限制
	   }
	   item.setWhereList(whereList);
	   DyResponse dyResponse = super.getTableData(item , this.getPara("page"));
	   renderJson(dyResponse);
	}
	/**
	 * 废弃或放弃借款申请
	 * 适用条件：业务员放弃某笔已经录入的借款申请，放弃后不能发起流程审批
	 * @author masai
	 * @time 2017年5月25日 下午7:49:58
	 */
	public void giveUpLoanApply(){
		String loanId = this.getPara("id");
		if(StringUtils.isNotBlank(loanId)){
			renderJson(createErrorJsonResonse("借款申请编号不能为空"));
		}
		LoanApplyApprove applyApprove = LoanApplyApprove.getLoanApplyApproveById(loanId);
		if(applyApprove == null || "-10".equals(applyApprove.get("status"))){
			renderJson(createErrorJsonResonse("借款申请不存在或无法放弃"));
		}
		applyApprove.set("status", "-10");
		try {
			baseModel.baseUpdateByEntity(applyApprove);
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(createSuccessJsonResonse());
	}
	/**
	 * 恢复借款申请
	 * 适用条件：业务员将某笔已经放弃的借款申请重新恢复，恢复后可以重新发起流程审批
	 * @author masai
	 * @time 2017年5月25日 下午7:50:02
	 */
	public void recoverLoanApply(){
		String loanId = this.getPara("id");
		if(StringUtils.isNotBlank(loanId)){
			renderJson(createErrorJsonResonse("借款申请编号不能为空"));
		}
		LoanApplyApprove applyApprove = LoanApplyApprove.getLoanApplyApproveById(loanId);
		if(applyApprove == null || "10".equals(applyApprove.get("status"))){
			renderJson(createErrorJsonResonse("借款申请不存在或无法恢复"));
		}
		applyApprove.set("status", "-10");
		try {
			baseModel.baseUpdateByEntity(applyApprove);
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(createSuccessJsonResonse());
	}
	
}
