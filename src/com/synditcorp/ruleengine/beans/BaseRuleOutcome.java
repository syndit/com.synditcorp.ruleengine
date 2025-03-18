package com.synditcorp.ruleengine.beans;

import java.util.List;

import com.synditcorp.ruleengine.interfaces.Outcome;

public class BaseRuleOutcome extends BaseOutcome implements Outcome {
	
	private String expression;
	
	@Override
	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public String getExpression() {
		return this.expression;
	}

}
