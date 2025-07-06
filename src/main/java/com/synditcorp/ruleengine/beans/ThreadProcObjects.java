/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synditcorp.ruleengine.RuleEvaluator;

public class ThreadProcObjects {

	private Integer threadRuleNumber;
	private RuleEvaluator ruleEvaluator;
	private ArrayList<Integer> block;
	private ArrayList<String> numberKeys;
	private ArrayList<String> tagKeys;

	@JsonCreator
	public ThreadProcObjects(
			@JsonProperty("threadRuleNumber") Integer threadRuleNumber,
			@JsonProperty("ruleEvaluator") RuleEvaluator ruleEvaluator,
			@JsonProperty("block") ArrayList<Integer> block,
			@JsonProperty("numberKeys") ArrayList<String> numberKeys,
			@JsonProperty("tagKeys") ArrayList<String> tagKeys
		) {
		
		this.threadRuleNumber = threadRuleNumber;
		this.ruleEvaluator = ruleEvaluator;
		this.block = block;
		this.numberKeys = numberKeys;
		this.tagKeys = tagKeys;

	};

	public Integer getThreadRuleNumber() {
		return threadRuleNumber;
	}

	public RuleEvaluator getRuleEvaluator() {
		return ruleEvaluator;
	}

	public ArrayList<Integer> getBlock() {
		return block;
	}

	public ArrayList<String> getNumberKeys() {
		return numberKeys;
	}

	public ArrayList<String> getTagKeys() {
		return tagKeys;
	}

}
