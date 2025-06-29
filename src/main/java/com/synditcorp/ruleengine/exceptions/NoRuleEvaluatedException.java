package com.synditcorp.ruleengine.exceptions;

public class NoRuleEvaluatedException extends Exception {

	static final long serialVersionUID = 1L;

	public NoRuleEvaluatedException() {
		super(getErrorMessage(null));
	}
	
	public NoRuleEvaluatedException(String errorMessage ){
		super(getErrorMessage(errorMessage));
	};
	
	private static String getErrorMessage(String errorMessage) {
		String defaultMessage = "No rule was evaluated";
		if( errorMessage == null ) return defaultMessage;
		return defaultMessage + ": " + errorMessage;
	}
	
}
