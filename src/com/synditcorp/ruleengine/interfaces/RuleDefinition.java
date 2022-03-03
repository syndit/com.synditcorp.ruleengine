/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.interfaces;

import java.util.ArrayList;

public interface RuleDefinition {

	public String getDocumentId();
	public String getDescription();
	public String getVersion();
	public Integer getStartRule();
	public ArrayList<String> getDocumentTags();
	public ArrayList<String> getRuleTags(Integer ruleNumber) throws Exception;
	public void loadRules(RuleParser parser) throws Exception;
	public void reloadRules(RuleParser parser) throws Exception;
	public Rule getRule(Integer ruleNumber) throws Exception;
	public boolean isCalcRule(Integer ruleNumber) throws Exception;
	public boolean isOrRule(Integer ruleNumber) throws Exception;
	public boolean isAndRule(Integer ruleNumber) throws Exception;
	public boolean isAllRule(Integer ruleNumber) throws Exception;
	public String getPassKey(Integer ruleNumber) throws Exception;
	public String getFailKey(Integer ruleNumber) throws Exception;
	public String getPassScore(Integer ruleNumber) throws Exception;
	public String getFailScore(Integer ruleNumber) throws Exception;
	public String getPassFlag(Integer ruleNumber) throws Exception;
	public String getFailFlag(Integer ruleNumber) throws Exception;
	public String getPassReason(Integer ruleNumber) throws Exception;
	public String getFailReason(Integer ruleNumber) throws Exception;
	public String getPassAction(Integer ruleNumber) throws Exception;
	public String getFailAction(Integer ruleNumber) throws Exception;
	public String getExpression(Integer ruleNumber) throws Exception;
	public String getHandlerClass(Integer ruleNumber) throws Exception;

	public ArrayList<Integer> getCompositeRulesList(Integer ruleNumber) throws Exception;
	public ArrayList<Integer>  getCompositePassKeysList(Integer ruleNumber) throws Exception;
	public ArrayList<Integer>  getCompositeFailKeysList(Integer ruleNumber) throws Exception;
	public ArrayList<Integer>  getCompositePassScoreList(Integer ruleNumber) throws Exception;
	public ArrayList<Integer>  getCompositeFailScoreList(Integer ruleNumber) throws Exception;
	public ArrayList<Integer>  getCompositePassFlagsList(Integer ruleNumber) throws Exception;
	public ArrayList<Integer>  getCompositeFailFlagsList(Integer ruleNumber) throws Exception;
	public ArrayList<Integer>  getCompositePassReasonsList(Integer ruleNumber) throws Exception;
	public ArrayList<Integer>  getCompositeFailReasonsList(Integer ruleNumber) throws Exception;
	public ArrayList<Integer>  getCompositePassActionsList(Integer ruleNumber) throws Exception;
	public ArrayList<Integer>  getCompositeFailActionsList(Integer ruleNumber) throws Exception;

}
