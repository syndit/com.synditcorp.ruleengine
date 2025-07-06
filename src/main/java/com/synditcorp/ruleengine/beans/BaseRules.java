/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synditcorp.ruleengine.interfaces.Rules;

public class BaseRules implements Rules {

	private String documentId;
	private String description;
	private String version;
	private Boolean active;
	private Integer startRule;
	private ArrayList<String> documentTags;
	private ArrayList<CalcRule> calcRules;
	private ArrayList<AndRule> andRules;
	private ArrayList<OrRule> orRules;
	private ArrayList<AllRule> allRules;
	private ArrayList<ThreadRule> threadRules;

	@JsonCreator
	public BaseRules(
			@JsonProperty("documentId") String documentId,
			@JsonProperty("description") String description,
			@JsonProperty("version") String version,
			@JsonProperty("active") Boolean active,
			@JsonProperty("startRule") Integer startRule,
			@JsonProperty("documentTags") ArrayList<String> documentTags,
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
		this.calcRules = calcRules;
		this.andRules = andRules;
		this.orRules = orRules;
		this.allRules = allRules;
		this.threadRules = threadRules;

	}

	@Override
	public ArrayList<CalcRule> getCalcRules() {
		return this.calcRules;
	}

	@Override
	public ArrayList<AndRule> getAndRules() {
		return this.andRules;
	}

	@Override
	public ArrayList<OrRule> getOrRules() {
		return this.orRules;
	}

	@Override
	public ArrayList<AllRule> getAllRules() {
		return this.allRules;
	}

	@Override
	public ArrayList<ThreadRule> getThreadRules() {
		return this.threadRules;
	}

	@Override
	public String getDocumentId() {
		return documentId;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public Boolean getActive() {
		return this.active;
	}

	@Override
	public Integer getStartRule() {
		return this.startRule;
	}

	@Override
	public ArrayList<String> getDocumentTags() {
		return this.documentTags;
	}

}
