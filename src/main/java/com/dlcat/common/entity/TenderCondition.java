package com.dlcat.common.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 收益计算：投资待收计算条件
 */
public class TenderCondition implements Serializable {
	private static final long serialVersionUID = 8805193980833236596L;
	/**
	 * 年化收益（百分制）
	 */
	private BigDecimal apr;
	/**
	 * 投资金额
	 */
	private BigDecimal amount;
	/**
	 * 投资期数（单位：月）
	 */
	private Integer period;
	/**
	 * 投资时间
	 */
	private Long tenderTime;
	/**
	 * 还款类型 1等额本息、2按季度还款、3到期还本还息、4按月付息到期还本息、5按天计息到期还本息、6先息后本(到期还本，按月付息，且当月还息)
	 */
	private Integer repayType;

	public BigDecimal getApr() {
		return apr;
	}

	public void setApr(BigDecimal apr) {
		this.apr = apr;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public Integer getRepayType() {
		return repayType;
	}

	public void setRepayType(Integer repayType) {
		this.repayType = repayType;
	}

	public Long getTenderTime() {
		return tenderTime;
	}

	public void setTenderTime(Long tenderTime) {
		this.tenderTime = tenderTime;
	}
}