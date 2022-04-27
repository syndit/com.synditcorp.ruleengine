/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.processors;

import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

import com.synditcorp.ruleengine.RuleEvaluator;
import com.synditcorp.ruleengine.beans.ThreadResults;

public class ThreadRuleProcessor extends RecursiveTask<ArrayList<ThreadResults>> {

	private static final long serialVersionUID = 1L;
	private ArrayList<Integer> block;
	private RuleEvaluator ruleEvaluator;
	
	public ThreadRuleProcessor(ArrayList<Integer> block, RuleEvaluator ruleEvaluator) {
		this.block = block;
		this.ruleEvaluator = ruleEvaluator;
	}

	@Override
	protected ArrayList<ThreadResults> compute() {

		ArrayList<ThreadResults> threadResults = new ArrayList<ThreadResults>();

		for (int i = 0; i < block.size(); i++) {
			Integer ruleNumber = block.get(i);
			try {
				Boolean result = ruleEvaluator.evaluateRule(ruleNumber);
				ThreadResults results = new ThreadResults();
				results.setRuleNumber(ruleNumber);
				results.setResult(result);
				if(result) {
					results.setPassScore( (Double) ruleEvaluator.getVariables().get( ("compositePassScore_" + ruleNumber) ) );
				} 	else {
					results.setFailScore( (Double) ruleEvaluator.getVariables().get( ("compositeFailScore_" + ruleNumber) ) );
				}
				threadResults.add(results);
			} catch (Exception e) {
				//let it throw back to caller
			}
		}

		return threadResults;

	}
	
}
