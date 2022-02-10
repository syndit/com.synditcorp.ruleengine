/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.interfaces;

import java.util.Date;

public interface Rule {

	public void setRuleNumber(int ruleRef);
	public Integer getRuleNumber();
	public void setRuleType(String ruleType);
	public String getRuleType();
	public void setDescription(String ruleDescription);
	public String getDescription();
	public void setActive(Boolean active);
	public Boolean getActive();
	public void setEffectiveDate(Date date);
	public Date getEffectiveDate();
	public void setExpirationDate(Date date);
	public Date getExpirationDate();
	public void setPassKey(String passKey);
	public String getPassKey();
	public void setFailKey(String failKey);
	public String getFailKey();
	public void setPassScore(String passScore);
	public String getPassScore();
	public void setFailScore(String failScore);
	public String getFailScore();
	public void setPassFlag(String passFlag);
	public String getPassFlag();
	public void setFailFlag(String failFlag);
	public String getFailFlag();
	public void setPassReason(String passReason);
	public String getPassReason();
	public void setFailReason(String failReason);
	public String getFailReason();
	public void setFailAction(String failAction);
	public String getFailAction();
	public void setPassAction(String passAction);
	public String getPassAction();
	
}
