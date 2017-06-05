package com.dlcat.common.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.dlcat.common.entity.Tender;
import com.dlcat.common.entity.TenderCondition;
import com.dlcat.common.entity.TenderDetail;

/**
 * 还款工具类
 */
public class RepayUtil {
	
	/**
	 * 计算逾期天数
	 * optTime 实际操作天数
	 * @return
	 */
    public static Integer lateDays(Long time, Long optTime) {
		Date optDate = optTime != null ? DateUtil.dateParse(optTime) : DateUtil.getCurrentDate();
		Integer lateDays = DateUtil.daysBetween(DateUtil.dateParse(time), optDate);
		return lateDays > 0 ? lateDays : 0;
	}
	
	/**
	 * 生成投资回款信息
	 * @param repayCondition
	 * @return
	 */
	public static Tender getRepayInfo(TenderCondition repayCondition) {
		if(repayCondition == null
				|| repayCondition.getApr() == null//利率
				|| repayCondition.getPeriod() == null//还款期数
				|| repayCondition.getAmount() == null//金额
				|| repayCondition.getRepayType() == null//还款方式
				|| repayCondition.getAmount().compareTo(BigDecimal.ZERO) <= 0){
			return null;
		}
		
		if(repayCondition.getTenderTime() == null)
			repayCondition.setTenderTime(DateUtil.getCurrentTime());
		
		//按还款方式生成还款信息
		if(1 == repayCondition.getRepayType()) {//等额本息
			return getByMonth(repayCondition);
		} else if(2 == repayCondition.getRepayType()) {//按季还款
			return getBySeason(repayCondition);
		} else if(3 == repayCondition.getRepayType()) {//到期还本还息
			return getByEnd(repayCondition);
		} else if(4 == repayCondition.getRepayType()) {//按月付息(按月付息到期还本息)
			return getByEndMonth(repayCondition);
		} else if(5 == repayCondition.getRepayType()) {//按天计息到期还本息
			return getByEndDay(repayCondition);
		} else if(6 == repayCondition.getRepayType()) {//先息后本(到期还本，按月付息，且当月还息)
			return getByEndMonths(repayCondition);
		}
		
		return null;
	}
	
	/**
	 * 等额本息法,返回每个月的数据集合
	 * 贷款本金×月利率×（1+月利率）^还款月数/[（1+月利率）^还款月数-1]
	 * a*[i*(1+i)^n]/[(1+i)^n-1]
	 */
	private static Tender getByMonth(TenderCondition repayCondtion) {
		BigDecimal monthApr = NumberUtils.div(repayCondtion.getApr(), new BigDecimal(1200), 10);//月利率
		
		//每月还款本息
		BigDecimal repayAmount = BigDecimal.ZERO;
		BigDecimal totalApr = new BigDecimal(Math.pow(monthApr.doubleValue() + 1, repayCondtion.getPeriod()));
		if(totalApr.compareTo(BigDecimal.ONE) > 0) {
			repayAmount = NumberUtils.round(NumberUtils.div(NumberUtils.mul(repayCondtion.getAmount(), NumberUtils.mul(monthApr, totalApr)), NumberUtils.sub(totalApr, BigDecimal.ONE)));
		} else {
			repayAmount =  repayCondtion.getAmount();
		}
		
		//生成还款信息
		BigDecimal amountAll = BigDecimal.ZERO, capitalAll = BigDecimal.ZERO, interestAll = BigDecimal.ZERO;
		List<TenderDetail> repayDetailList = new ArrayList<TenderDetail>();
		for(int i=0;i<repayCondtion.getPeriod();i++) {
	    	BigDecimal interest = BigDecimal.ZERO;
			if(totalApr.compareTo(BigDecimal.ONE) <= 0) {
			} else if(i == 0) {
				interest = NumberUtils.round(NumberUtils.mul(repayCondtion.getAmount(), monthApr));
			} else {
				interest = NumberUtils.round(NumberUtils.add(NumberUtils.mul(NumberUtils.sub(NumberUtils.mul(repayCondtion.getAmount(), monthApr),
						repayAmount), new BigDecimal(Math.pow(NumberUtils.add(BigDecimal.ONE, monthApr).doubleValue(), i))), repayAmount));
			}
			
			//防止一分钱的问题
			BigDecimal capital = BigDecimal.ZERO;
			if(i == repayCondtion.getPeriod() - 1) {
				capital = NumberUtils.sub(repayCondtion.getAmount(), capitalAll);
				interest = NumberUtils.sub(repayAmount, capital);
				if (interest.compareTo(BigDecimal.ZERO) < 0) {
					interest = BigDecimal.ZERO;
				}
			} else {
				capital = NumberUtils.sub(repayAmount, interest);
			}
			
			//总还款信息
			capitalAll = NumberUtils.add(capitalAll, capital);
			amountAll = NumberUtils.add(amountAll, repayAmount);
			interestAll = NumberUtils.add(interestAll, interest);
			
			//还款明细
			TenderDetail repayDetailInfo = new TenderDetail();
			repayDetailInfo.setPrincipal(capital);
			repayDetailInfo.setInterest(NumberUtils.round(interest));
			repayDetailInfo.setAmount(NumberUtils.round(repayAmount));
			repayDetailInfo.setRepayTime(DateUtil.addMonth(repayCondtion.getTenderTime(), i + 1));
			repayDetailList.add(repayDetailInfo);
		}
		
		Tender reapy = new Tender();
		reapy.setAmountAll(amountAll);
		reapy.setPrincipalAll(capitalAll);
		reapy.setInterestAll(interestAll);
		reapy.setRepayDetailList(repayDetailList);
		
		return reapy;
	}
	
	/**
	 * 按季等额本息法，返回每个月的数据集合
	 */
	private static Tender getBySeason(TenderCondition repayCondition){
		//按季还款必须是3的倍数
		if(repayCondition.getPeriod()%3 != 0) return null;
		
		int season = repayCondition.getPeriod()/3;//得到总季数
		BigDecimal monthApr = NumberUtils.div(repayCondition.getApr(), new BigDecimal("1200"), 10);//月利率
		BigDecimal seasonAmount = NumberUtils.round(NumberUtils.div(repayCondition.getAmount(), new BigDecimal(season)));//每季应还的本金
		
		BigDecimal amountYes = BigDecimal.ZERO;//已还金额
		BigDecimal amountAll = BigDecimal.ZERO, capitalAll = BigDecimal.ZERO, interestAll = BigDecimal.ZERO;
		List<TenderDetail> repayDetailList = new ArrayList<TenderDetail>();
		for(int i=0;i<repayCondition.getPeriod();i++) {
			BigDecimal amount = BigDecimal.ZERO, capital = BigDecimal.ZERO;
			BigDecimal interest = NumberUtils.round(NumberUtils.mul(NumberUtils.sub(repayCondition.getAmount(), amountYes), monthApr));//利息等于应还金额乘月利率
			if(i%3 == 2) {//本金只在第三个月还，本金等于借款金额除季度
				capital = seasonAmount;
    			amountYes = NumberUtils.add(amountYes, capital);
    			
			}
			amount = NumberUtils.add(capital, interest);
			amountAll = NumberUtils.add(amountAll, amount);
			capitalAll = NumberUtils.add(capitalAll, capital);
			interestAll = NumberUtils.add(interestAll, interest);
			
			TenderDetail repayDetailInfo = new TenderDetail();
			repayDetailInfo.setInterest(interest);
			repayDetailInfo.setPrincipal(capital);
			repayDetailInfo.setAmount(amount);
			repayDetailInfo.setRepayTime(DateUtil.addMonth(repayCondition.getTenderTime(), i + 1));
			repayDetailList.add(repayDetailInfo);
		}
		
		Tender repay = new Tender();
		repay.setAmountAll(amountAll);
		repay.setPrincipalAll(capitalAll);
		repay.setInterestAll(interestAll);
		repay.setRepayDetailList(repayDetailList);
		
		return repay;
	}
	
	/**
	 * 到期还本还息，返回汇总数据集合
	 */
	private static Tender getByEnd(TenderCondition repayCondition){
		BigDecimal monthApr = NumberUtils.div(repayCondition.getApr(), new BigDecimal("1200"), 10);//月利率
		
		BigDecimal interest = NumberUtils.round(NumberUtils.mul(monthApr, new BigDecimal(repayCondition.getPeriod()), repayCondition.getAmount()));
		BigDecimal amountAll = NumberUtils.add(repayCondition.getAmount(), interest);
		
		TenderDetail repayDetaiInfo = new TenderDetail();
		repayDetaiInfo.setAmount(amountAll);
		repayDetaiInfo.setInterest(interest);
		repayDetaiInfo.setPrincipal(repayCondition.getAmount());
		repayDetaiInfo.setRepayTime(DateUtil.addMonth(repayCondition.getTenderTime(), repayCondition.getPeriod()));
		
		Tender reapy = new Tender();
		reapy.setAmountAll(amountAll);
		reapy.setInterestAll(interest);
		reapy.setPrincipalAll(repayCondition.getAmount());
		reapy.setRepayDetailList(Collections.singletonList(repayDetaiInfo));
		
		return reapy;
	}
	
	/**
	 * 到期还本，按月付息，返回汇总信息
	 */
	private static Tender getByEndMonth(TenderCondition repayCondition){
		BigDecimal monthApr = NumberUtils.div(repayCondition.getApr(), new BigDecimal("1200"), 10);//月利率
		BigDecimal interest = NumberUtils.round(NumberUtils.mul(repayCondition.getAmount(), monthApr));//利息等于应还金额乘月利率
    	
		BigDecimal amountAll = BigDecimal.ZERO;
		BigDecimal capitalAll = BigDecimal.ZERO;
		BigDecimal interestAll = BigDecimal.ZERO;
		List<TenderDetail> repayDetailList = new ArrayList<TenderDetail>();
		for (int i = 0; i < repayCondition.getPeriod(); i++) {
			BigDecimal capital = BigDecimal.ZERO;
			if (i + 1 == repayCondition.getPeriod()) {
				capital = repayCondition.getAmount();
			}
			BigDecimal amount = NumberUtils.add(interest, capital);

			amountAll = NumberUtils.add(amountAll, amount);
			capitalAll = NumberUtils.add(capitalAll, capital);
			interestAll = NumberUtils.add(interestAll, interest);

			//还款明细
			TenderDetail repayDetailInfo = new TenderDetail();
			repayDetailInfo.setPrincipal(capital);
			repayDetailInfo.setInterest(interest);
			repayDetailInfo.setAmount(NumberUtils.add(interest, capital));
			repayDetailInfo.setRepayTime(DateUtil.addMonth(repayCondition.getTenderTime(), i + 1));
			repayDetailList.add(repayDetailInfo);
		}
		
		Tender reapy = new Tender();
		reapy.setAmountAll(amountAll);
		reapy.setPrincipalAll(capitalAll);
		reapy.setInterestAll(interestAll);
		reapy.setRepayDetailList(repayDetailList);
		
		return reapy;
	}
	
	/**
	 * 到期还本，按月付息,且当月还息，返回每个月数据集合
	 */
	private static Tender getByEndMonths(TenderCondition repayCondition){
		BigDecimal monthApr = NumberUtils.div(repayCondition.getApr(), new BigDecimal("1200"), 10);//月利率
		BigDecimal interest = NumberUtils.round(NumberUtils.mul(repayCondition.getAmount(), monthApr));//利息等于应还金额乘月利率
    	
		BigDecimal interestAll = BigDecimal.ZERO;
    	List<TenderDetail> repayDetailList = new ArrayList<TenderDetail>();
    	for(int i=0;i<repayCondition.getPeriod();i++) {
    		interestAll = NumberUtils.add(interestAll, interest);
    		
    		TenderDetail repayDetailInfo = new TenderDetail();
    		repayDetailInfo.setAmount(interest);
    		repayDetailInfo.setInterest(interest);
    		repayDetailInfo.setPrincipal(BigDecimal.ZERO);
    		repayDetailInfo.setRepayTime(DateUtil.addMonth(repayCondition.getTenderTime(), i));
    		repayDetailList.add(repayDetailInfo);
    	}
    	
    	//最后一次只还本金,没有利息,日期顺延一个月
    	TenderDetail repayDetailInfo = new TenderDetail();
    	repayDetailInfo.setAmount(repayCondition.getAmount());
		repayDetailInfo.setInterest(BigDecimal.ZERO);
		repayDetailInfo.setPrincipal(repayCondition.getAmount());
		repayDetailInfo.setRepayTime(DateUtil.addMonth(repayCondition.getTenderTime(), repayCondition.getPeriod()));
		repayDetailList.add(repayDetailInfo);
		
		Tender repay = new Tender();
		repay.setInterestAll(interestAll);
		repay.setPrincipalAll(repayCondition.getAmount());
		repay.setAmountAll(NumberUtils.add(interestAll, repayCondition.getAmount()));
		repay.setRepayDetailList(repayDetailList) ;
		
    	return repay;
	}
	
	/**
	 * 到期还本，按天付息，返回汇总数据集合
	 */
	private static Tender getByEndDay(TenderCondition repayCondition){
		BigDecimal interestAll = NumberUtils.round(NumberUtils.div(NumberUtils.mul(repayCondition.getAmount(), new BigDecimal(repayCondition.getPeriod()),repayCondition.getApr()), new BigDecimal("36000")));
		BigDecimal amountAll = NumberUtils.add(interestAll, repayCondition.getAmount());
    	
		TenderDetail repayDetailInfo = new TenderDetail();
    	repayDetailInfo.setAmount(amountAll);
		repayDetailInfo.setInterest(interestAll);
		repayDetailInfo.setPrincipal(repayCondition.getAmount());
		repayDetailInfo.setRepayTime(DateUtil.addDay(repayCondition.getTenderTime(), repayCondition.getPeriod()));
		
		Tender repay = new Tender();
		repay.setAmountAll(amountAll);
		repay.setPrincipalAll(repayCondition.getAmount());
		repay.setInterestAll(interestAll);
		repay.setRepayDetailList(Collections.singletonList(repayDetailInfo));
		
		return repay;
	}
	
	/**
	 * 费用计算
	 * @param feeLogList 费用列表
	 * @param feeCondition
     * 	amount		本息
     * 	interest	利息
     * 	principal	本金
     * 	rankFee		积分等级费用比例
     * 	delayDays	逾期天数，默认1,表示不逾期
     * 	period		借款时间 (单位：个月,天标单位：天)
     *  vipStatus	是否是会员 -1 代表是普通注册用户 ，1代表普通会员
     * 
     * **************【普通会员字段】**************
     *  
     *  formula_fee_amount_type 			【按比例公式】：计算费用使用金额 interest利息，principal本金，amount本息
     *  proportion_fee_amount_type			【按比例】： 计算费用使用金额 interest利息，principal本金，amount本息
     *  formula_amount_proportion			【按比例公式】：费用比例
     *  proportion_amount_proportion		【按比例】： 费用比例
     *  day_multiple  						比例天数（主要用于天标）
     *  period_multiple 					【按比例公式】：比例期数
     *  proportion_max  					【按比例公式】：会员比例上限
     *  period_month 						【按比例公式】：扣除的期数
     *  period_month 						【按比例公式】：扣除的期数
     *  period_proportion 					【按比例公式】：期数比例值
     *  credit_rank_proportion_multiple 	积分等级比例
     * 
     *  **************【vip会员字段】**************
     * 
     *  formula_fee_amount_type_vip 		【按比例公式】：计算费用使用金额 interest利息，principal本金，amount本息
     *  proportion_fee_amount_type_vip		【按比例】： 计算费用使用金额 interest利息，principal本金，amount本息
     *  formula_amount_proportion_vip		【按比例公式】：费用比例
     *  proportion_amount_proportion_vip	【按比例】： 费用比例
     *  day_multiple_vip					比例天数（主要用于天标）
     *  period_multiple_vip 				【按比例公式】：比例期数
     *  proportion_max_vip  				【按比例公式】：会员比例上限
     *  period_month_vip 					【按比例公式】：扣除的期数
     *  period_proportion_vip 				【按比例公式】：期数比例值
     *  credit_rank_proportion_multiple 	积分等级比例（通用）
     *  
     *  **************【扣费方式deduct_fee_way】**************
     *  扣费方式 proportion：按比例; formula：按比例公式; free：免费
	 * @return
	 */
	/*@SuppressWarnings("unchecked")
	public static List<Fee> getLoanFee(List<FnLoanFeeLog> feeLogList, FeeCondition feeCondition, VipService vipService) throws DyServiceException {
		if (feeCondition == null || feeLogList == null || feeLogList.size() <= 0) {
			return null;
		}

		//循环计算费用
		SerializerUtil serializerUtil = new SerializerUtil();
		Map<Long, Fee> feeMap = new LinkedHashMap<Long, Fee>();
		int period = feeCondition.getPeriod() == null ? 1 : feeCondition.getPeriod();//借款期数
		int delayDays = feeCondition.getDelayDays() == null ? 0 : feeCondition.getDelayDays();//逾期天数
		BigDecimal rankFee = feeCondition.getRankFee() == null ? new BigDecimal("-1") : feeCondition.getRankFee();//积分等级比例
		for (FnLoanFeeLog feeLog : feeLogList) {
			if (StringUtils.isBlank(feeLog.getFeeSnapshots())) {
				continue;
			}
			Object obj = null;
			try {
				obj = serializerUtil.unserialize(feeLog.getFeeSnapshots().getBytes());
			} catch (Exception e) {
				throw new DyServiceException(e);
			}
			if (obj == null) {
				continue;
			}
			Map<String, Object> feeSnapshotMap = (Map<String, Object>) obj;
			
			boolean isVip = feeCondition.getVipStatus() == 1;
			BigDecimal vipFeeScale = feeCondition.getVipFeeScale();
			if(!"0".equals(feeCondition.getTenderMemberId())
					&& "2".equals(feeSnapshotMap.get("deduct_target_member"))) {//费用扣投资人
				vipFeeScale = vipService.getMemberVipFeeScale(feeCondition.getTenderMemberId());
				isVip = vipFeeScale == null ? false : true;
			}

			BigDecimal amount = BigDecimal.ZERO;
			BigDecimal feeAmount = BigDecimal.ZERO;
			Integer mulPeriod = Integer.valueOf(period);
			Integer mulDay = Integer.valueOf(delayDays);
			Object amountType, scale, maxScale, deductMonth, periodScale, period_multiple, day_multiple;
			if ("proportion".equals(feeSnapshotMap.get("deduct_fee_way"))) {//按比例
				//是否是会员
				if (isVip) {
					amountType = feeSnapshotMap.get("proportion_fee_amount_type_vip");//扣费金额
					scale = feeSnapshotMap.get("proportion_amount_proportion_vip");//费用比例
					period_multiple = feeSnapshotMap.get("period_multiple_vip");
					day_multiple = feeSnapshotMap.get("day_multiple_vip");
				} else {
					amountType = feeSnapshotMap.get("proportion_fee_amount_type");//扣费金额
					scale = feeSnapshotMap.get("proportion_amount_proportion");//费用比例
					period_multiple = feeSnapshotMap.get("period_multiple");
					day_multiple = feeSnapshotMap.get("day_multiple");
				}
				if ("amount".equals(amountType)){
					amount = feeCondition.getAmount();
				} else if ("interest".equals(amountType)) {
					amount = feeCondition.getInterest();
				} else if ("principal".equals(amountType)) {
					amount = feeCondition.getPrincipal();
				}
				period_multiple = period_multiple != null ? period_multiple.toString() : "";
				day_multiple = day_multiple != null ? day_multiple.toString() : "";
				if (!"1".equals(period_multiple) && !"1".equals(day_multiple)) {
					mulPeriod = 1;
				} 
				if (feeLog.getFeeId() != 1100L && feeLog.getFeeId() != 1000L && feeLog.getFeeId() != 1010L && feeLog.getFeeId() != 1110L) {
					mulDay = 0;
				}
				feeAmount = getFeeAmountByScale(amount, scale == null ? BigDecimal.ZERO : new BigDecimal(scale.toString()), mulDay, mulPeriod, rankFee);
			} else if ("formula".equals(feeSnapshotMap.get("deduct_fee_way"))) {//按比例公式
				//是否是会员
				if (isVip) {
					amountType = feeSnapshotMap.get("formula_fee_amount_type_vip");//扣费金额
					scale = feeSnapshotMap.get("formula_amount_proportion_vip");//扣费比例
					maxScale = feeSnapshotMap.get("proportion_max_vip");//最大扣费比例
					deductMonth = feeSnapshotMap.get("period_month_vip");//扣除个月
					periodScale = feeSnapshotMap.get("period_proportion_vip");//期数比例
				} else {
					amountType = feeSnapshotMap.get("formula_fee_amount_type");//扣费金额
					scale = feeSnapshotMap.get("formula_amount_proportion");//扣费比例
					maxScale = feeSnapshotMap.get("proportion_max");//最大扣费比例
					deductMonth = feeSnapshotMap.get("period_month");//扣除个月
					periodScale = feeSnapshotMap.get("period_proportion");//期数比例
				}
				if ("amount".equals(amountType)){
					amount = feeCondition.getAmount();
				} else if ("interest".equals(amountType)) {
					amount = feeCondition.getInterest();
				} else if ("principal".equals(amountType)) {
					amount = feeCondition.getPrincipal();
				}
				feeAmount = getFeeAmountByFormula(amount, scale == null ? BigDecimal.ZERO : new BigDecimal(scale.toString()), period,
						deductMonth == null ? 0 : Integer.parseInt(deductMonth.toString()), periodScale == null ? BigDecimal.ZERO : new BigDecimal(
								periodScale.toString()), maxScale == null ? BigDecimal.ZERO : new BigDecimal(maxScale.toString()));
			}

			if (NumberUtils.greaterThanZero(feeAmount)) {
				if(vipFeeScale != null)
					feeAmount = NumberUtils.round(NumberUtils.mul(feeAmount, vipFeeScale.divide(NumberUtils.ONE_HUNDRED)));
				Fee fee = feeMap.get(feeLog.getFeeId());
				if (fee == null) {
					fee = new Fee();
					fee.setAmount(feeAmount);
					fee.setFeeId(feeLog.getFeeId());
					fee.setFeeName(feeLog.getFeeName());
					fee.setPayTargetMember(feeSnapshotMap.get("pay_target_member") != null ? Integer.valueOf(feeSnapshotMap.get("pay_target_member").toString()) : 0);
					fee.setDeductTargetMember(feeSnapshotMap.get("deduct_target_member") != null ? Integer.valueOf(feeSnapshotMap.get("deduct_target_member").toString()) : 0);
				}
				feeMap.put(feeLog.getFeeId(), fee);
			}
		}

		List<Fee> feeList = new ArrayList<Fee>();
		feeList.addAll(feeMap.values());
		return feeList;
	}
	
	*//**
	 * 按比例计算费用
	 * @param amount 金额
	 * @param scale 比例
	 * @param delayDays 逾期天数
	 * @param period 借款期数
	 * @param rankFee 积分等级比例,默认-1表示未开启 乘积分等级比例
	 * @return
	 *//*
	public static BigDecimal getFeeAmountByScale(BigDecimal amount, BigDecimal scale, Integer delayDays, Integer period, BigDecimal rankFee) {
		if (amount == null || scale == null) {
			return BigDecimal.ZERO;
		}
		BigDecimal result = NumberUtils.mul(amount, scale, new BigDecimal(period), NumberUtils.ONE_PERCENT);
		if (delayDays > 0) {
			result = NumberUtils.mul(result, new BigDecimal(delayDays));
		}
		if (rankFee.compareTo(BigDecimal.ZERO) > 0) {
			result = NumberUtils.mul(result, rankFee, NumberUtils.ONE_PERCENT);
		}
		return NumberUtils.round(result);
	}
	
	*//**
	 * 按比例公式计算费用
	 * @param amount 金额
	 * @param scale 比例
	 * @param period 借款个月
	 * @param deducMonth 扣除的个月
	 * @param periodScale 期数比例
	 * @param maxScale 最大扣费比例 计算公式 feeAmount = amount*{scale+[(period-deductMonth) * periodScale] <> maxScale} 金额 * 比例 * 【（借款个月-扣除个月）* 期数比例】 如果【（借款个月-扣除个月）*
	 *        periodScale】 计算出来的的比例大于maxScale则用 maxScale
	 *//*
	public static BigDecimal getFeeAmountByFormula(BigDecimal amount, BigDecimal scale, Integer period, Integer deductMonth, BigDecimal periodScale, BigDecimal maxScale) {
		if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || scale == null)
			return BigDecimal.ZERO;

		scale = NumberUtils.add(scale, NumberUtils.mul(new BigDecimal((period - deductMonth)), periodScale));
		if (scale.compareTo(BigDecimal.ZERO) <= 0)
			return BigDecimal.ZERO;

		//判断是否大于最大比例
		if (scale.compareTo(maxScale) > 0)
			scale = maxScale;

		return NumberUtils.round(NumberUtils.mul(NumberUtils.mul(amount, scale), new BigDecimal("0.01")));
	}
	*//**
	 * 计算(按比例、按比例公式)指定费用
	 * @author masai
	 * @time 2017年1月11日 下午4:37:32
	 * @param loanFeeLog 借款费用记录
	 * @param amount     借款金额(本金)
	 * @param delayDays  逾期天数  比例公式计算是为null；按比例计算不能为null
	 * @param period     借款期数(个月)
	 * @return
	 * @throws DyServiceException 
	 *//*
	public static BigDecimal calculateFee(FnLoanFeeLog loanFeeLog,BigDecimal amount,Integer delayDays,Integer period ) throws DyServiceException {
		if(loanFeeLog == null || amount == null ||period == null)return BigDecimal.ZERO;
		SerializerUtil serializerUtil = new SerializerUtil();
		Object obj = null;
		try {
			obj = serializerUtil.unserialize(loanFeeLog.getFeeSnapshots().getBytes());
		} catch (Exception e) {
			throw new DyServiceException(e);
		}
		if (obj == null) {
			return null;
		}
		Map<String, Object> feeSnapshotMap = (Map<String, Object>) obj;
		Object scale,maxScale,deductMonth,periodScale,calculateType;
		BigDecimal amountFee = null;

		//费用计算
		if("proportion".equals(feeSnapshotMap.get("deduct_fee_way"))){//  按比例计算
			if(delayDays == null)return BigDecimal.ZERO;
			scale = feeSnapshotMap.get("proportion_amount_proportion");//费用比例
			amountFee = getFeeAmountByScale(amount,new BigDecimal(scale.toString()),
					Integer.valueOf(0),period,new BigDecimal("-1"));
		}else if("formula".equals(feeSnapshotMap.get("deduct_fee_way"))){// 按比例公式计算
			scale = feeSnapshotMap.get("formula_amount_proportion");//扣费比例
			maxScale = feeSnapshotMap.get("proportion_max");//最大扣费比例
			deductMonth = feeSnapshotMap.get("period_month");//扣除个月
			periodScale = feeSnapshotMap.get("period_proportion");//期数比例
			calculateType = feeSnapshotMap.get("deduct_fee_way");//计算方式
			
			amountFee = RepayUtil.getFeeAmountByFormula(amount,
					   scale == null ? BigDecimal.ZERO : new BigDecimal(scale.toString()), 
					   period,
					   deductMonth == null ? 0 : Integer.parseInt(deductMonth.toString()),
					   periodScale == null ? BigDecimal.ZERO : new BigDecimal(periodScale.toString()), 
					   maxScale == null ? BigDecimal.ZERO : new BigDecimal(maxScale.toString()));
		}
		return amountFee;
	}*/
}