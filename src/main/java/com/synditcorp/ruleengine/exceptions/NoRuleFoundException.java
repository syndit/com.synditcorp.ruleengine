package com.synditcorp.ruleengine.exceptions;

public class NoRuleFoundException extends Exception {

	static final long serialVersionUID = 1L;

	public NoRuleFoundException() {
		super(getErrorMessage(null));
	}
	
	public NoRuleFoundException(String errorMessage ){
		super(getErrorMessage(errorMessage));
	};
	
	private static String getErrorMessage(String errorMessage) {
		String defaultMessage = "Rule was not found";
		if( errorMessage == null ) return defaultMessage;
		return defaultMessage + ": " + errorMessage;
	}
	
}
