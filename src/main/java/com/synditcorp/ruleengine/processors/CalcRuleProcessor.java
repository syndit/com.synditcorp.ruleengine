/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package com.synditcorp.ruleengine.processors;

import java.util.TreeMap;

import com.synditcorp.ruleengine.interfaces.RuleClassHandler;

public class CalcRuleProcessor {

	/**
	 * This method processes an expression using a rule handler class that implements RuleClassHandler.
	 * @param ruleClassHandler is the value from the "handlerClass" field of the BaseRule class
	 * @param ruleExpression is the expression to evaluate
	 * @param variables contains the variables needed by the expression
	 * @return a boolean is returned based on the evaluation of the expression
	 * @throws Exception when any exception occurs
	 */
	public static Boolean processCalcRule(String ruleClassHandler, String ruleExpression, TreeMap<String, Object> variables) throws Exception {
		
		if(ruleClassHandler == null || ruleClassHandler == "") throw new Exception("No ruleClassHandler");
		
		RuleClassHandler h = (RuleClassHandler) Class.forName(ruleClassHandler).getDeclaredConstructor().newInstance();
		Boolean b =  h.processCalcRule(ruleExpression, variables);
		
		return b;
		
	}
	
}
