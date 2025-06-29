package com.synditcorp.ruleengine.exceptions;

public class DuplicateKeyException extends Exception {

	static final long serialVersionUID = 1L;

	public DuplicateKeyException() {
		super(getErrorMessage(null));
	}
	
	public DuplicateKeyException(String errorMessage ){
		super(getErrorMessage(errorMessage));
	};
	
	private static String getErrorMessage(String errorMessage) {
		String defaultMessage = "Cannot add variable to collection: variable already exists";
		if( errorMessage == null ) return defaultMessage;
		return defaultMessage + ": " + errorMessage;
	}
	
}
