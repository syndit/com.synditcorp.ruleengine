package com.synditcorp.ruleengine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HandlerClass {
	
	private final String handlerClassID;
	private final String handlerClass;
	private final String[] handlerConstParams;
	
	@JsonCreator
	public HandlerClass(
			@JsonProperty("handlerClassID") String handlerClassID,
			@JsonProperty("handlerClass") String handlerClass,
			@JsonProperty("handlerConstParams") String[] handlerConstParams
		) {
		
		this.handlerClassID = handlerClassID;
		this.handlerClass = handlerClass;
		this.handlerConstParams = handlerConstParams;

	}
	
	protected String getHandlerClassID() {
		return this.handlerClassID;
	}

	protected String getHandlerClass() {
		return this.handlerClass;
	}

	protected String[] getHandlerConstParams() {
		return this.handlerConstParams;
	}	

}
