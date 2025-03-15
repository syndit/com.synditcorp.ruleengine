package com.synditcorp.ruleengine.interfaces;

public interface RuleOutcome extends Outcome {

	public void setGlobal(Boolean global);
	public Boolean getGlobal();
	public void setExpression(String expression);
	public String getExpression();

}
