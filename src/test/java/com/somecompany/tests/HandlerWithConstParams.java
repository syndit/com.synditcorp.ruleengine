package com.somecompany.tests;

import java.util.TreeMap;

import com.synditcorp.ruleengine.interfaces.RuleClassHandler;

/*
 * This class is for testing the handler classes with constructor parameters
 * 
*/

public class HandlerWithConstParams implements RuleClassHandler {
	
	String compareStr0;
	Integer compareInt1;
	
	public HandlerWithConstParams(String... params) {
		
		this.compareStr0 = params[0];
		this.compareInt1 = Integer.parseInt(params[1]);
		
	}	
	
	@Override
	public Boolean processCalcRule(String ruleExpression, TreeMap<String, Object> variables) throws Exception {
		
		Integer comparePassedInt = Integer.parseInt(ruleExpression);
		
		return (ruleExpression.equalsIgnoreCase(compareStr0) && comparePassedInt.equals(compareInt1));
		
	}

}
