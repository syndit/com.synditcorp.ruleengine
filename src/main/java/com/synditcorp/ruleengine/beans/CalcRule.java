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

public class CalcRule extends BaseRule {

	private final String expression;
	private final String handlerClass;

	@JsonCreator
	public CalcRule(@JsonProperty("ruleNumber") Integer ruleNumber, @JsonProperty("ruleType") String ruleType,
			@JsonProperty("ruleTags") ArrayList<String> ruleTags, @JsonProperty("description") String description,
			@JsonProperty("active") Boolean active, @JsonProperty("expirationDate") Date expirationDate,
			@JsonProperty("effecitveDate") Date effectiveDate,
			@JsonProperty("outcomes") ArrayList<BaseOutcome> outcomes, @JsonProperty("expression") String expression,
			@JsonProperty("handlerClass") String handlerClass) {

		super(ruleNumber, ruleType, ruleTags, description, active, expirationDate, effectiveDate, outcomes);

		this.expression = expression;
		this.handlerClass = handlerClass;

	}

	public String getExpression() {
		return expression;
	}

	public String getHandlerClass() {
		return this.handlerClass;
	}

}
