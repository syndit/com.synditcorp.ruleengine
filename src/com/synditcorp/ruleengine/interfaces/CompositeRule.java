package com.synditcorp.ruleengine.interfaces;

import java.util.ArrayList;

public interface CompositeRule extends Rule {

	public void setCompositeRules(ArrayList<Integer> compositeRules);
	public ArrayList<Integer> getCompositeRules();
	public void setCompositeOutcomes(ArrayList<CompositeRuleOutcome> compositeOutcomes);
	public ArrayList<CompositeRuleOutcome> getCompositeOutcomes();
	
}
