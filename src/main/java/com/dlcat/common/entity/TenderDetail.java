package com.dlcat.common.entity;

import java.io.Serializable;
import java.math.BigDecimal;
/**
 * 收益计算：投资待收计算结果明细 
 */
public class TenderDetail implements Serializable {
	private static final long serialVersionUID = 7315509892243529447L;
	/**
	 * 本月应还总额
	 */
	private BigDecimal amount;
	/**
	 * 本月应还本金
	 */
	private BigDecimal principal;
	/**
	 * 本月应还利息
	 */
	private BigDecimal interest;
	/**
	 * 本月应还时间
	 */
	private Long repayTime;

	public BigDecimal getAmount() {
		return amount == null ? BigDecimal.ZERO : amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getPrincipal() {
		return principal == null ? BigDecimal.ZERO : principal;
	}

	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}

	public BigDecimal getInterest() {
		return interest == null ? BigDecimal.ZERO : interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}
	public Long getRepayTime() {
		return repayTime;
	}

	public void setRepayTime(Long repayTime) {
		this.repayTime = repayTime;
	}
}