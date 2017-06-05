package com.dlcat.core.controller.loan;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dlcat.common.BaseController;
import com.dlcat.common.utils.DateUtil;
import com.dlcat.common.utils.OptionUtil;
import com.dlcat.common.utils.StringUtils;
import com.dlcat.core.model.CuObjectCustomer;
import com.dlcat.core.model.CuProperty;
import com.dlcat.core.model.LoanApplyApprove;
import com.dlcat.core.model.LoanProductCategory;
import com.dlcat.core.model.LoanRepayType;
import com.dlcat.core.model.SysUser;
/**
 * 借款申请
 * @author masai
 * @time 2017年5月25日 下午6:29:47
 */
public class LoanApplyController  extends BaseController {
	/**
	 * 表单页面构造初始化
	 * @author masai
	 * @time 2017年5月22日 下午3:08:34
	 */
	public void index(){
		SysUser user = getSessionAttr("user");
		Map<String, Object> userMap = new HashMap<String, Object>();
		if(user != null){
			userMap.put("curUserId", user.get("id"));
			userMap.put("curUserName", user.get("name"));
			userMap.put("curOrgId", user.get("belong_org_id"));
			userMap.put("curOrgName", user.get("belong_org_name"));	
		}
		//借款种类
		this.setAttr("loanCategoryType", OptionUtil.getOptionListByOther("id", "name", "loan_product_category", null,null));
		//借款对象
		this.setAttr("loanObjectType", OptionUtil.getOptionListByCodeLibrary("CustomerType", true, null));
		//还款方式	--	需要修改  根据  借款产品种类 确定  该产品对应的还款方式
		//this.setAttr("loanRepayType", OptionUtil.getOptionListByOther("id", "name", "loan_repay_type", null));
		//抵押物种类
		this.setAttr("cuPropertyType", OptionUtil.getOptionListByCodeLibrary("CuProperty", true, null));
		//发起人、机构 信息
		this.setAttr("sponsorMessage", userMap);
		this.render("***.html");
	}
	/**
	 * 借款申请录入 
	 * @author masai
	 * @time 2017年5月22日 下午3:56:11
	 */
	public void loanApproveApply(){
		//1.获取参数
		String loanName = this.getPara("loanName");
		Integer loanProductType = this.getParaToInt("loanProductType");//页面元素修改
		Integer amount = this.getParaToInt("amount");
		String apr = this.getPara("apr");
		Integer period = this.getParaToInt("period");
		String loanObjectType = this.getPara("loanObjectType");
		Integer repayType = this.getParaToInt("repayType");
		String loanDesc = this.getPara("loanDesc");
		String loanUseType = this.getPara("loanUseType");
		String cuId = this.getPara("cuId");
		String isHaveCollateral = this.getPara("isHaveCollateral");
		String collateralType = this.getPara("collateralType");
		String collateralId = this.getPara("collateralId");
		String remark = this.getPara("remark");
		//2.校验参数
		//校验借款名称
		if(StringUtils.isBlank(loanName)){
			renderJson(createErrorJsonResonse("借款名称不能为空"));
			return;
		}
		if(!LoanApplyApprove.isLoanApplyNameExist(loanName)){
			renderJson(createErrorJsonResonse("借款名称已存在"));
			return;
		}
		//校验借款产品种类
		LoanProductCategory loanProductCategory = LoanProductCategory.getLoanProductById(loanProductType);
		if(loanProductCategory == null){
			renderJson(createErrorJsonResonse("借款种类不存在"));
			return;
		}
		//校验还款方式
		if(LoanRepayType.getLoanRepayTypeById(repayType) == null){
			renderJson(createErrorJsonResonse("还款方式不存在"));
		}
		//获取借款人，并校验借款人合法性
		CuObjectCustomer loanCustomer = CuObjectCustomer.getEffectCustomer(cuId);
		if(loanCustomer == null){
			renderJson(createErrorJsonResonse("借款人不存在或处于黑名单"));
			return;
		}
		//获取抵押物，并校验抵押物合法性
		CuProperty cuProperty = CuProperty.getPropertyById(collateralId); 
		if(cuProperty == null || StringUtils.isBlank(cuProperty.getStr("certificate_type")) ||
								  StringUtils.isBlank(cuProperty.getStr("certificate_no"))){
			renderJson(createErrorJsonResonse("抵押物不合法"));
			return;
		}
		//校验抵押物能发发起借款抵押
		if(!(cuProperty.getStr("status").equals("A") && cuProperty.getStr("status").equals("B21"))){
			renderJson(createErrorJsonResonse("抵押物无法发起抵押"));
			return;
		}
		//生成借款编号
		String createloanId = DateUtil.dateSimpleFormat(new Date());
		//获取当前登录用户
		SysUser user = getSessionAttr("user");
		//3.插入借款申请记录
		LoanApplyApprove loanApply = new LoanApplyApprove();
		loanApply.set("id", createloanId);
		loanApply.set("loan_name", loanName);
		loanApply.set("loan_category_id", loanProductCategory.getInt("business_id"));
		loanApply.set("loan_product_id", loanProductType);
		loanApply.set("loan_object_type", loanObjectType);
		loanApply.set("amount", amount);
		loanApply.set("period", period);
		loanApply.set("apr", apr);
		loanApply.set("repay_type", repayType);
		loanApply.set("cu_id", cuId);
		loanApply.set("cu_name", loanCustomer.getStr("name"));
		loanApply.set("card_type", loanCustomer.getStr("card_type"));
		loanApply.set("card_id", loanCustomer.getStr("card_id"));
		loanApply.set("loan_dec", loanDesc);
		loanApply.set("loan_use", loanUseType);
		loanApply.set("status", "");
		loanApply.set("apply_user_id", user.get("id"));
		loanApply.set("apply_user_name", user.get("name"));
		loanApply.set("apply_time", DateUtil.getCurrentTime());
		loanApply.set("apply_org_id", user.get("belong_org_id"));
		loanApply.set("apply_org_name", user.get("belong_org_name"));
		loanApply.set("is_has_collateral", isHaveCollateral);
		if(StringUtils.isNotBlank(isHaveCollateral)){
			loanApply.set("collateral_type", collateralType);
			loanApply.set("collateral_no", collateralId);
		}
		loanApply.set("remark", remark);
		try {
			super.baseModel.baseInsertByEntity(loanApply);
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(createSuccessJsonResonse());
	}
}
