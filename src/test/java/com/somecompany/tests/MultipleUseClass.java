package com.somecompany.tests;

import java.util.TreeMap;

import com.synditcorp.ruleengine.interfaces.RuleClassHandler;

public class MultipleUseClass implements RuleClassHandler {

	/*
	 * This class is for testing the ignoreCache feature.  The first evaluation will put the fail outcome in the variables collection.
	 * The second evaluation will return true because fail outcome variable will be in the collection.
	 */
	@Override
	public Boolean processCalcRule(String ruleExpression, TreeMap<String, Object> variables) throws Exception {
		
		String variableKey = ruleExpression;
		boolean varExists = variables.containsKey(variableKey);

		if(varExists) {
			return true;
		} else {
			return false;
		}
		
	}

}
