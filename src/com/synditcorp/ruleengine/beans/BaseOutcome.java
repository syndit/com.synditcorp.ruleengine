package com.synditcorp.ruleengine.beans;

import java.util.List;
import java.util.Set;

import com.synditcorp.ruleengine.interfaces.Outcome;

public class BaseOutcome implements Outcome {
	

	private String key;
	private String result;
	private String type;
	private String expression;
	private Boolean global;
	private static final Set<String> validTypes = Set.of("number", "tag");
	private static final List<String> validResult = List.of("pass", "fail");
	private static final List<String> validTrue = List.of("true", "t", "1");
	private static final List<String> validFalse = List.of("false", "f", "0");
	

	@Override
	public void setResult(String result) throws IllegalArgumentException {
		if(result == null)  throw new IllegalArgumentException("Outcome result must be specified");
		if(!validResult.contains(result.toLowerCase())) throw new IllegalArgumentException("Outcome result must be one of these: " + validResult);
		this.result = result.toLowerCase();
	}

	@Override
	public String getResult() {
		return this.result;
	}

	@Override
	public void setType(String type) throws IllegalArgumentException {
		if(type == null)  throw new IllegalArgumentException("Outcome type must be specified");
		if(!validTypes.contains(type.toLowerCase())) throw new IllegalArgumentException("Rule outcome type must be one of these: " + validTypes);
		this.type = type.toLowerCase();
	}

	@Override
	public String getType() {
		return this.getType();
	}

	@Override
	public void setKey(String key) throws IllegalArgumentException {
		if(result == null)  throw new IllegalArgumentException("Outcome key must be specified");
		this.key = key;
		
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public void setExpression(String expression) {
		this.expression = expression;
	}

	@Override
	public String getExpression() {
		return this.expression;
	}

	@Override
	public void setGlobal(String global) {

		if(global == null) return;
		if(validTrue.contains(global.toLowerCase())) {
			this.global = true;
			return;
		}
		if(validFalse.contains(global.toLowerCase())) {
			this.global = false;
			return;
		}
		throw new IllegalArgumentException("Global field must be true, false, or null");

	}

	@Override
	public Boolean getGlobal() {
		return this.global;
	}
	
	
}
