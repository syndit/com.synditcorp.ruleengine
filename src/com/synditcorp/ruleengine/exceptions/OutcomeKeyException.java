package com.synditcorp.ruleengine.exceptions;

public class OutcomeKeyException extends Exception {

	static final long serialVersionUID = 1L;

	public OutcomeKeyException() {
		super(getErrorMessage(null));
	}
	
	public OutcomeKeyException(String errorMessage ){
		super(getErrorMessage(errorMessage));
	};
	
	private static String getErrorMessage(String errorMessage) {
		String defaultMessage = "Outcome tag mismatch";
		if( errorMessage == null ) return defaultMessage;
		return defaultMessage + ": " + errorMessage;
	}
	
}
