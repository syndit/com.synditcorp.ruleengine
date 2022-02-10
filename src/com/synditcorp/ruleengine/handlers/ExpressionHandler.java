/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.handlers;

import java.util.TreeMap;

import org.mvel2.MVEL;

public class ExpressionHandler {

	/**
	 * Run expressions where a Boolean is returned, i.e. expressions that have '==', '>', '<', 'matches', 'contains', etc. 
	 */
	public static Boolean evaluateExpression(String expression, TreeMap<String, Object> variables) {

		Object obj = runExpression(expression, variables);
		return (Boolean) obj;

	}

	/**
	 *  Run expressions that do math to return a Double value.  
	 */
	public static Double getProductOf(String expression, TreeMap<String, Object> variables) {

		Object obj = runExpression(expression, variables);
		if(obj instanceof Double) {
			return (Double) obj;
		}
		if(obj instanceof Integer) {
			return ((Integer) obj).doubleValue();
		}
		return null;

	}
	
	private static Object runExpression(String expression, TreeMap<String, Object> variables) {
		/*
		 * Implemented with MVEL here.
		 */
		return MVEL.eval(expression, variables);
	}
	

}
