package com.dlcat.common.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 收益计算：投资待收计算结果 
 */
public class Tender implements Serializable {
	private static final long serialVersionUID = -2266542156249797713L;
	/**
	 * 代收本息总额
	 */
	private BigDecimal amountAll;
	/**
	 * 代收投资本金
	 */
	private BigDecimal principalAll;
	/**
	 * 代收总利息
	 */
	private BigDecimal interestAll;
	/**
	 * 还款明细列表
	 */
	private List<TenderDetail> repayDetailList;

	public BigDecimal getAmountAll() {
		return amountAll;
	}

	public void setAmountAll(BigDecimal amountAll) {
		this.amountAll = amountAll;
	}

	public BigDecimal getPrincipalAll() {
		return principalAll;
	}

	public void setPrincipalAll(BigDecimal principalAll) {
		this.principalAll = principalAll;
	}

	public BigDecimal getInterestAll() {
		return interestAll;
	}

	public void setInterestAll(BigDecimal interestAll) {
		this.interestAll = interestAll;
	}

	public List<TenderDetail> getRepayDetailList() {
		return repayDetailList == null ? new ArrayList<TenderDetail>() : repayDetailList;
	}

	public void setRepayDetailList(List<TenderDetail> repayDetailList) {
		this.repayDetailList = repayDetailList;
	}
}
