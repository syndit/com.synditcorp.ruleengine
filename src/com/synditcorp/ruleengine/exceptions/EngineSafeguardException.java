package com.synditcorp.ruleengine.exceptions;

public class EngineSafeguardException extends Exception {

	static final long serialVersionUID = 1L;

	public EngineSafeguardException() {
		super(getErrorMessage(null));
	}
	
	public EngineSafeguardException(String errorMessage ){
		super(getErrorMessage(errorMessage));
	};
	
	private static String getErrorMessage(String errorMessage) {
		String defaultMessage = "Engine safeguard triggered";
		if( errorMessage == null ) return defaultMessage;
		return defaultMessage + ": " + errorMessage;
	}
	
}
