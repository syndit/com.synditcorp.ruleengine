/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synditcorp.ruleengine.exceptions.EngineSafeguardException;
import com.synditcorp.ruleengine.interfaces.Outcome;

public class BaseOutcome implements Outcome {

	private final String key;
	private final String result;
	private final String type;
	private final String expression;
	private final Boolean global;
	private final ArrayList<Integer> compositeOutcomeRules;
	private String variableName;
	private static final Set<String> validTypes = Set.of("number", "tag");
	private static final List<String> validResult = List.of("pass", "fail");

	@JsonCreator
	public BaseOutcome(@JsonProperty("key") String key, @JsonProperty("result") String result,
			@JsonProperty("type") String type, @JsonProperty("expression") String expression,
			@JsonProperty("global") Boolean global,
			@JsonProperty("compositeOutcomeRules") ArrayList<Integer> compositeOutcomeRules) {

		this.key = validateKey(key);
		this.result = validateResult(result);
		this.type = validateType(type);
		this.expression = expression;
		this.global = global;
		this.compositeOutcomeRules = compositeOutcomeRules;

	}

	public BaseOutcome(Outcome outcome) {
		this.key = validateKey(outcome.getKey());
		this.result = validateResult(outcome.getResult());
		this.type = validateType(outcome.getType());
		this.expression = outcome.getExpression();
		this.global = outcome.getGlobal();
		this.compositeOutcomeRules = outcome.getCompositeOutcomeRules();
		this.variableName = outcome.getVariableName();
	}

	private String validateResult(String result) throws IllegalArgumentException {
		if (result == null)
			throw new IllegalArgumentException("Outcome result must be specified");
		if (!validResult.contains(result.toLowerCase()))
			throw new IllegalArgumentException("Outcome result must be one of these: " + validResult);
		return result.toLowerCase();
	}

	@Override
	public String getResult() {
		return this.result;
	}

	private String validateType(String type) throws IllegalArgumentException {
		if (type == null)
			throw new IllegalArgumentException("Outcome type must be specified");
		if (!validTypes.contains(type.toLowerCase()))
			throw new IllegalArgumentException("Rule outcome type must be one of these: " + validTypes);
		return type.toLowerCase();
	}

	@Override
	public String getType() {
		return this.type;
	}

	private String validateKey(String key) throws IllegalArgumentException {
		if (key == null)
			throw new IllegalArgumentException("Outcome key must be specified");
		return key;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String getExpression() {
		return this.expression;
	}

	@Override
	public Boolean getGlobal() {
		return this.global;
	}

	@Override
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	/*
	 * Variable name uses document ID, so must be set when Rules are being
	 * configured in the definition where the document ID is available. Need to
	 * safeguard this variable as it can't be FINAL.
	 */
	public void setVariableName(String documentId, Integer ruleNumber) throws EngineSafeguardException {
		if (this.variableName != null && this.variableName.length() > 0)
			throw new EngineSafeguardException("Variable name already populated.");
		String varName = documentId + "_" + ruleNumber + "_" + this.key;
		this.variableName = varName;
	}

	@Override
	public String getVariableName() {
		return this.variableName;
	}

	@Override
	public ArrayList<Integer> getCompositeOutcomeRules() {
		return this.compositeOutcomeRules;
	}

}
