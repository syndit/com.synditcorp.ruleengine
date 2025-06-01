package com.synditcorp.ruleengine.beans;

import com.synditcorp.ruleengine.interfaces.Outcome;

public class OutcomeConverter extends BaseOutcome {
	
	public OutcomeConverter(Outcome outcome) {
		
		super.setType(outcome.getType());
		super.setKey(outcome.getKey());
		super.setResult(outcome.getResult());
		super.setExpression(outcome.getExpression());
		if(outcome.getGlobal() == null) {
			super.setGlobal(null);
		} else {
			super.setGlobal(outcome.getGlobal().toString());
		}
		super.setCompositeOutcomeRules(outcome.getCompositeOutcomeRules());

	}
	

}
