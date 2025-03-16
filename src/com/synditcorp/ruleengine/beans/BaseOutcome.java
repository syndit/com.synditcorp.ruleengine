package com.synditcorp.ruleengine.beans;

import java.util.Set;

import com.synditcorp.ruleengine.interfaces.Outcome;

public class BaseOutcome implements Outcome {
	
	public static final Set<String> validTypes = Set.of("number", "tag");

	private String key;
	private String result;
	private String type;
	private String expression;
	private Boolean global;
	

	@Override
	public void setResult(String result) throws IllegalArgumentException {
		if(result == null)  throw new IllegalArgumentException("Outcome result must be specified");
		this.result = result;
	}

	@Override
	public String getResult() {
		return this.result;
	}

	@Override
	public void setType(String type) throws IllegalArgumentException {
		if(result == null)  throw new IllegalArgumentException("Outcome type must be specified");
		if(!validTypes.contains(type.toLowerCase())) throw new IllegalArgumentException("Invalid rule outcome type value: " + type);
		this.type = type;
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
	public void setGlobal(Boolean global) {
		this.global = global;
	}

	@Override
	public Boolean getGlobal() {
		return this.global;
	}
	
	
}
