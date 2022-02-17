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

public class BaseRule implements Rule {

	private Integer ruleNumber;
	private String ruleType;
	private ArrayList<String> ruleTags;
	private String description;
	private Boolean active;
	private Date expirationDate;
	private Date effectiveDate;
	private String passKey = null;
	private String failKey = null;
	private String passScore = null;
	private String failScore = null;
	private String passFlag = null;
	private String failFlag = null;
	private String passReason = null;
	private String failReason = null;
	private String passAction = null;
	private String failAction = null;
	

	public BaseRule() {
		
	}
	
	@Override
	public String getPassFlag() {
		return passFlag;
	}

	@Override
	public void setPassFlag(String passFlag) {
		this.passFlag = passFlag;
	}

	@Override
	public String getFailFlag() {
		return failFlag;
	}

	@Override
	public void setFailFlag(String failFlag) {
		this.failFlag = failFlag;
	}

	@Override
	public String getPassReason() {
		return passReason;
	}

	@Override
	public void setPassReason(String passReason) {
		this.passReason = passReason;
	}

	@Override
	public String getFailReason() {
		return failReason;
	}

	@Override
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}


	@Override
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}


	@Override
	public String getRuleType() {
		return this.ruleType;
	}


	@Override
	public void setRuleNumber(Integer ruleNumber) {
		this.ruleNumber = ruleNumber;
	}

	@Override
	public Integer getRuleNumber() {
		return this.ruleNumber;
	}

	@Override
	public void setPassKey(String passKey) {
		this.passKey = passKey;
	}

	@Override
	public String getPassKey() {
		return this.passKey;
	}

	@Override
	public void setFailKey(String failKey) {
		this.failKey = failKey;
	}

	@Override
	public String getFailKey() {
		return this.failKey;
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
	public void setFailAction(String failAction) {
		this.failAction = failAction;
	}

	@Override
	public String getFailAction() {
		return this.failAction;
	}

	@Override
	public void setPassAction(String passAction) {
		this.passAction = passAction;
	}

	@Override
	public String getPassAction() {
		return this.passAction;
	}

	@Override
	public String getPassScore() {
		return passScore;
	}

	@Override
	public void setPassScore(String passScore) {
		this.passScore = passScore;
	}

	@Override
	public String getFailScore() {
		return failScore;
	}

	@Override
	public void setFailScore(String failScore) {
		this.failScore = failScore;
	}

	public ArrayList<String> getRuleTags() {
		return ruleTags;
	}

	public void setRuleTags(ArrayList<String> ruleTags) {
		this.ruleTags = ruleTags;
	}

	
	
}
