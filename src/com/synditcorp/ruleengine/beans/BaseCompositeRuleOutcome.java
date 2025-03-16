package com.synditcorp.ruleengine.beans;

import java.util.ArrayList;

import com.synditcorp.ruleengine.interfaces.CompositeOutcome;

public class BaseCompositeRuleOutcome extends BaseOutcome implements CompositeOutcome {
	
	private ArrayList<Integer> compositeRules;
	
	@Override
	public void setCompositeRules(ArrayList<Integer> rules) {
		this.compositeRules = rules;
	}

	@Override
	public ArrayList<Integer> getCompositeRules() {
		return this.compositeRules;
	}

	
	

}
