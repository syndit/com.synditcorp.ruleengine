package com.synditcorp.ruleengine.interfaces;

public interface ExecRule extends Rule {

	public String getHandlerClass();
	public void setHandlerClass(String handlerClass) throws IllegalArgumentException;

	
}
