package com.synditcorp.ruleengine.beans;

import java.util.ArrayList;

import com.synditcorp.ruleengine.interfaces.CompositeRuleOutcome;

public class BaseCompositeRuleOutcome extends BaseOutcome implements CompositeRuleOutcome {
	
	private ArrayList<Integer> rules;
	
	@Override
	public void setRules(ArrayList<Integer> rules) {
		this.rules = rules;
	}

	@Override
	public ArrayList<Integer> getRules() {
		return this.rules;
	}

	
	

}
