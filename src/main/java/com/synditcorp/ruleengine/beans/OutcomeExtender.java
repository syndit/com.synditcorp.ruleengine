package com.synditcorp.ruleengine.beans;

import com.synditcorp.ruleengine.interfaces.Outcome;

public class OutcomeExtender extends BaseOutcome {
	
	public OutcomeExtender(Outcome outcome) {
		
		super(outcome);
		
	}
		
//		@JsonProperty("key") String key,
//		@JsonProperty("result") String result,
//		@JsonProperty("type") String type,
//		@JsonProperty("expression") String expression,
//		@JsonProperty("global") Boolean global,
//		@JsonProperty("compositeOutcomeRules") ArrayList<Integer> compositeOutcomeRules
//		
//		
//		super.setType(outcome.getType());
//		super.setKey(outcome.getKey());
//		super.setResult(outcome.getResult());
//		super.setExpression(c);
//		if(outcome.getGlobal() == null) {
//			super.setGlobal(null);
//		} else {
//			super.setGlobal(outcome.getGlobal().toString());
//		}
//		super.setVariableName(outcome.getVariableName());
//		super.setCompositeOutcomeRules(outcome.getCompositeOutcomeRules());

	

}
