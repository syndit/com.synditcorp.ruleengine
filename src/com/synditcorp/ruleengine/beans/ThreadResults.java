package com.synditcorp.ruleengine.beans;

public class ThreadResults {

	private Integer ruleNumber = null;
	private Boolean result = null;
	private Double passScore = null;
	private Double failScore = null;

	public Integer getRuleNumber() {
		return ruleNumber;
	}

	public void setRuleNumber(Integer ruleNumber) {
		this.ruleNumber = ruleNumber;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public Double getPassScore() {
		return passScore;
	}

	public void setPassScore(Double passScore) {
		this.passScore = passScore;
	}

	public Double getFailScore() {
		return failScore;
	}

	public void setFailScore(Double failScore) {
		this.failScore = failScore;
	}
	
}
