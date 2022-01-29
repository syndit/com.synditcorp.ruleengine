/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.interfaces;

import java.util.ArrayList;

import com.synditcorp.ruleengine.beans.AllRule;
import com.synditcorp.ruleengine.beans.AndRule;
import com.synditcorp.ruleengine.beans.CalcRule;
import com.synditcorp.ruleengine.beans.OrRule;

public interface Rules {
	
	public ArrayList<CalcRule> getCalcRules();
	public void setCalcRules(ArrayList<CalcRule> calcRules);
	public ArrayList<AndRule> getAndRules();
	public void setAndRules(ArrayList<AndRule> andRules);
	public ArrayList<OrRule> getOrRules();
	public void setOrRules(ArrayList<OrRule> orRules);
	public ArrayList<AllRule> getAllRules();
	public void setAllRules(ArrayList<AllRule> allRules);
	public String getdefinitionID();
	public void setdefinitionID(String definitionID);
	public String getDescription();
	public void setDescription(String description);
	public String getVersion();
	public void setVersion(String version);
	
}
