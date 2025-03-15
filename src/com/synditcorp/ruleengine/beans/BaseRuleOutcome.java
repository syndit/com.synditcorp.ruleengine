package com.synditcorp.ruleengine.beans;

import com.synditcorp.ruleengine.interfaces.RuleOutcome;

public class BaseRuleOutcome extends BaseOutcome implements RuleOutcome {
	
	private String expression;
	private Boolean global;

	
	@Override
	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public String getExpression() {
		return this.expression;
	}

	@Override
	public void setGlobal(Boolean global) {
		this.global = global;
	}

	@Override
	public Boolean getGlobal() {
		return this.global;
	}

	
	

}
