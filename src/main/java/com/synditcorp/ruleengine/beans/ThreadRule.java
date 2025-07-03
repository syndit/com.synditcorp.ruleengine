/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.beans;

import java.util.ArrayList;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ThreadRule extends BaseRule {

	private final ArrayList<Integer> threadRules;
	private final ArrayList<String> numberKeys;
	private final ArrayList<String> tagKeys;
	
	@JsonCreator
	public ThreadRule(
			@JsonProperty("ruleNumber") Integer ruleNumber,
			@JsonProperty("ruleType") String ruleType,
			@JsonProperty("ruleTags") ArrayList<String> ruleTags,
			@JsonProperty("description") String description,
			@JsonProperty("active") Boolean active,
			@JsonProperty("expirationDate") Date expirationDate,
			@JsonProperty("effecitveDate") Date effectiveDate,
			@JsonProperty("outcomes") ArrayList<BaseOutcome> outcomes,
			@JsonProperty("threadRules") ArrayList<Integer> threadRules,
			@JsonProperty("numberKeys") ArrayList<String> numberKeys,
			@JsonProperty("tagKeys") ArrayList<String> tagKeys
		) {
		
		super(ruleNumber, ruleType, ruleTags, description, active, expirationDate, effectiveDate, outcomes);

		this.threadRules = threadRules;
		this.numberKeys = numberKeys;
		this.tagKeys = tagKeys;
		
	}

	public ArrayList<Integer> getThreadRules() {
		return threadRules;
	}

//	public void setThreadRules(ArrayList<Integer> threadRules) {
//		this.threadRules = threadRules;
//	}

	public ArrayList<String> getNumberKeys() {
		return numberKeys;
	}

//	public void setNumberKeys(ArrayList<String> numberKeys) {
//		this.numberKeys = numberKeys;
//	}

	public ArrayList<String> getTagKeys() {
		return tagKeys;
	}

//	public void setTagKeys(ArrayList<String> tagKeys) {
//		this.tagKeys = tagKeys;
//	}
	
	
}
