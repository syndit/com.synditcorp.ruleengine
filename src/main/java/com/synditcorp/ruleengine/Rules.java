/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Rules {

	private final String documentId;
	private final String description;
	private final String version;
	private final Boolean active;
	private final Integer startRule;
	private final ArrayList<String> documentTags;
	private final ArrayList<HandlerClass> handlerClasses;
	private final ArrayList<CalcRule> calcRules;
	private final ArrayList<AndRule> andRules;
	private final ArrayList<OrRule> orRules;
	private final ArrayList<AllRule> allRules;
	private final ArrayList<ThreadRule> threadRules;

	@JsonCreator
	public Rules(
			@JsonProperty("documentId") String documentId,
			@JsonProperty("description") String description,
			@JsonProperty("version") String version,
			@JsonProperty("active") Boolean active,
			@JsonProperty("startRule") Integer startRule,
			@JsonProperty("documentTags") ArrayList<String> documentTags,
			@JsonProperty("handlerClasses") ArrayList<HandlerClass> handlerClasses,
			@JsonProperty("calcRules") ArrayList<CalcRule> calcRules,
			@JsonProperty("andRules") ArrayList<AndRule> andRules,
			@JsonProperty("orRules") ArrayList<OrRule> orRules,
			@JsonProperty("allRules") ArrayList<AllRule> allRules,
			@JsonProperty("threadRules") ArrayList<ThreadRule> threadRules
		) {
		
		this.documentId = documentId;
		this.description = description;
		this.version = version;
		this.active = active;
		this.startRule = startRule;
		this.documentTags = documentTags;
		this.handlerClasses = handlerClasses;
		this.calcRules = calcRules;
		this.andRules = andRules;
		this.orRules = orRules;
		this.allRules = allRules;
		this.threadRules = threadRules;

	}

	protected ArrayList<CalcRule> getCalcRules() {
		return this.calcRules;
	}

	protected ArrayList<AndRule> getAndRules() {
		return this.andRules;
	}

	protected ArrayList<OrRule> getOrRules() {
		return this.orRules;
	}

	protected ArrayList<AllRule> getAllRules() {
		return this.allRules;
	}

	protected ArrayList<ThreadRule> getThreadRules() {
		return this.threadRules;
	}

	protected String getDocumentId() {
		return documentId;
	}

	protected String getDescription() {
		return this.description;
	}

	protected String getVersion() {
		return this.version;
	}

	protected Boolean getActive() {
		return this.active;
	}

	protected Integer getStartRule() {
		return this.startRule;
	}

	protected ArrayList<String> getDocumentTags() {
		return this.documentTags;
	}
	
	protected ArrayList<HandlerClass> gethandlerClasses() {
		return this.handlerClasses;
	}

}
