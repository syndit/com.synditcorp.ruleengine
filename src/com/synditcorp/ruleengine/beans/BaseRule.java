/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.beans;

import java.util.ArrayList;
import java.util.Date;

import com.synditcorp.ruleengine.interfaces.Rule;

abstract class BaseRule implements Rule {

	private Integer ruleNumber;
	private String ruleType;
	private ArrayList<String> ruleTags;
	private String description;
	private Boolean active;
	private Date expirationDate;
	private Date effectiveDate;

	public BaseRule() {
		
	}
	
	@Override
	public void setRuleType(String ruleType) throws IllegalArgumentException {
		this.ruleType = ruleType;
	}


	@Override
	public String getRuleType() {
		return this.ruleType;
	}


	@Override
	public void setRuleNumber(Integer ruleNumber) throws IllegalArgumentException {
		this.ruleNumber = ruleNumber;
	}

	@Override
	public Integer getRuleNumber() {
		return this.ruleNumber;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public Boolean getActive() {
		return this.active;
	}

	@Override
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	public Date getEffectiveDate() {
		return this.effectiveDate;
	}

	@Override
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public Date getExpirationDate() {
		return this.expirationDate;
	}
	
	@Override
	public ArrayList<String> getRuleTags() {
		return this.ruleTags;
	}

	@Override
	public void setRuleTags(ArrayList<String> ruleTags) {
		if(this.ruleTags == null) this.ruleTags = new ArrayList<String>();
		this.ruleTags = ruleTags;
	}



}
