/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.beans;

import java.util.ArrayList;

import com.synditcorp.ruleengine.interfaces.Rules;

public class BaseRules implements Rules {

	private String definitionID;
	private String description;
	private String version;
	private ArrayList<CalcRule> calcRules;
	private ArrayList<AndRule> andRules;
	private ArrayList<OrRule> orRules;
	private ArrayList<AllRule> allRules;
	
	public BaseRules() {
		
	}

	@Override
	public ArrayList<CalcRule> getCalcRules() {
		if(calcRules == null) calcRules = new ArrayList<CalcRule>();
		return calcRules;
	}

	@Override
	public void setCalcRules(ArrayList<CalcRule> calcRules) {
		this.calcRules = calcRules;
	}


	@Override
	public ArrayList<AndRule> getAndRules() {
		if(andRules == null) andRules = new ArrayList<AndRule>();
		return andRules;
	}


	@Override
	public void setAndRules(ArrayList<AndRule> andRules) {
		this.andRules = andRules;
	}


	@Override
	public ArrayList<OrRule> getOrRules() {
		if(orRules == null) orRules = new ArrayList<OrRule>();
		return orRules;
	}


	@Override
	public void setOrRules(ArrayList<OrRule> orRules) {
		this.orRules = orRules;
	}

	@Override
	public ArrayList<AllRule> getAllRules() {
		if(allRules == null) allRules = new ArrayList<AllRule>();
		return allRules;
	}

	@Override
	public void setAllRules(ArrayList<AllRule> allRules) {
		this.allRules = allRules;
	}

	@Override
	public String getdefinitionID() {
		return definitionID;
	}

	@Override
	public void setdefinitionID(String definitionID) {
		this.definitionID = definitionID;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getVersion() {
		return version;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}


}
