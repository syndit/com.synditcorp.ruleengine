/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine;

import static com.synditcorp.ruleengine.logging.RuleLogger.LOGGER;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.RecursiveTask;

import com.synditcorp.ruleengine.exceptions.NoRuleEvaluatedException;

public class ThreadRuleCompute extends RecursiveTask<ThreadResults> {

	private static final long serialVersionUID = 1L;
	private final Integer threadRuleNumber;
	private final ArrayList<Integer> block;
	private final ArrayList<String> numberKeys;
	private final ArrayList<String> tagKeys;
	private final RuleEvaluator ruleEvaluator;

	public ThreadRuleCompute(ThreadProcObjects objects) {
		this.threadRuleNumber = objects.getThreadRuleNumber();
		this.block = objects.getBlock();
		this.numberKeys = objects.getNumberKeys();
		this.tagKeys = objects.getTagKeys();
		this.ruleEvaluator = objects.getRuleEvaluator();
	}

	@Override
	protected ThreadResults compute() {

		ThreadResults threadResults = new ThreadResults();

		for (int i = 0; i < block.size(); i++) {
			Integer ruleNumber = block.get(i);

			try {

				try {
					ruleEvaluator.evaluateRule(ruleNumber);
				} catch (NoRuleEvaluatedException e) {
					LOGGER.info("No rule evaluated exception in thread rule " + threadRuleNumber + " for rule number "
							+ ruleNumber + ".");
					continue;
				}

				Iterator<String> numberIterator = numberKeys.iterator();
				while (numberIterator.hasNext()) {
					String key = numberIterator.next();
					Double value = ruleEvaluator.getNumberOutcome(ruleNumber, key);
					if (value == null)
						continue;
					threadResults.setNumberOutcome(key, value);
				}
				Iterator<String> tagIterator = tagKeys.iterator();
				while (tagIterator.hasNext()) {
					String key = tagIterator.next();
					ArrayList<String> value = ruleEvaluator.getTagOutcome(ruleNumber, key);
					if (value == null)
						continue;
					threadResults.setTagOutcome(key, value);
				}

			} catch (NullPointerException e) {
				LOGGER.info("Null pointer exception when evaluating " + ruleNumber + " in Thread rule.");
				continue;
			} catch (Exception e) {
				LOGGER.info("Exception in thread when evaluating rule number " + ruleNumber + ": " + e);
			}
		}

		return threadResults;

	}


}
