package com.synditcorp.ruleengine.interfaces;

import java.util.ArrayList;

public interface CompositeRuleOutcome extends Outcome {
	
	public void setRules(ArrayList<Integer> rules);
	public ArrayList<Integer> getRules();

}
