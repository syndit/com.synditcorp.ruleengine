/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import com.synditcorp.ruleengine.beans.AllRule;
import com.synditcorp.ruleengine.beans.AndRule;
import com.synditcorp.ruleengine.beans.BaseRules;
import com.synditcorp.ruleengine.beans.CalcRule;
import com.synditcorp.ruleengine.beans.OrRule;
import com.synditcorp.ruleengine.beans.ThreadRule;
import com.synditcorp.ruleengine.interfaces.CompositeRule;
import com.synditcorp.ruleengine.interfaces.CompositeRuleOutcome;
import com.synditcorp.ruleengine.interfaces.Rule;
import com.synditcorp.ruleengine.interfaces.RuleDefinition;
import com.synditcorp.ruleengine.interfaces.RuleOutcome;
import com.synditcorp.ruleengine.interfaces.RuleParser;

/**
 * This class loads rule definitions from parsers that implement com.synditcorp.ruleengine.interfaces.RuleParser.  Methods of this class provide
 * access to base and composite rule field objects.  After loading the rules, this class is primarily to be used by the rule engine evaluator
 * and shouldn't be accessed directly.
 */
public class DefaultRuleDefinition implements RuleDefinition {

	private BaseRules baseRules;
	private TreeMap<Integer, Rule> rules = new TreeMap<Integer, Rule>();
	
	public DefaultRuleDefinition() {
		
	}

	/**
	 * Returns the ID of the rules definition.  Definition is not used at runtime to evaluate rules.
	 */
	@Override
	public String getDocumentId() {
		return baseRules.getDocumentId();
	}
	
	/**
	 * Returns the description of the rules definition.  Description is not used at runtime to evaluate rules. 
	 */
	@Override
	public String getDescription() {
		return baseRules.getDescription();
	}
	
	/**
	 * Returns the version of the rules definition.  Version is not used at runtime to evaluate rules.
	 */
	@Override
	public String getVersion() {
		return baseRules.getVersion();
	}
	
	/**
	 * Optional document tags are used to further define a document.  Document tags are not used at runtime
	 * to evaluate rules.  Tags can be used for things like authorization in databases or display control in
	 * custom rule definition editors
	 */
	@Override
	public ArrayList<String> getDocumentTags() {
		return baseRules.getDocumentTags();
	}

	/**
	 * This is intended to hold the rule number at the base of the decision tree so calling programs can refer to
	 * this value at runtime rather than having to rely on other processes to communicate the starting rule of
	 * a decision tree.  This is optional: any rule can be called directly.  This is not used when
	 * evaluating rules at runtime.
	 */
	@Override
	public Integer getStartRule() {
		return baseRules.getStartRule();
	}
	
	
	/**
	 * Optional rule tags are used to further define a rule, but are not used when evaluating
	 * rules at runtime.  Tags can be used for things like authorization in databases or display control in
	 * custom rule definition editors
	 */
	@Override
	public ArrayList<String> getRuleTags(Integer ruleNumber) throws Exception {
		return getRule(ruleNumber).getRuleTags();
	}

	/**
	 * Load the rules engine rules objects using a parser that implements com.synditcorp.ruleengine.interfaces.RulesParser
	 */
	@Override
	public void loadRules(RuleParser parser) throws Exception {
		
		this.baseRules = parser.getRules();
		setToRulesMap();
		
	}
	
	/**
	 * Reload the rules engine rules objects using a parser that implements com.synditcorp.ruleengine.interfaces.RulesParser.  Previous
	 * rule definitions are discarded.
	 */
	@Override
	public void reloadRules(RuleParser parser) throws Exception {

		if(this.baseRules != null) {
			this.baseRules = null;
		}
		loadRules(parser);
		
	}


	/**
	 * Returns "true" if the rule is a "base" rule
	 */
	@Override
	public boolean isCalcRule(Integer ruleNumber) throws Exception {
		return rules.get(ruleNumber).getClass().toString().equalsIgnoreCase("calcRule");
	}
	
	/**
	 * Returns "true" if the rule is an "or" rule
	 */
	@Override
	public boolean isOrRule(Integer ruleNumber) throws Exception {
		return rules.get(ruleNumber).getClass().toString().equalsIgnoreCase("orRule");
	}
	
	/**
	 * Returns "true" if the rule is an "and" rule
	 */
	@Override
	public boolean isAndRule(Integer ruleNumber) throws Exception {
		return rules.get(ruleNumber).getClass().toString().equalsIgnoreCase("andRule");
	}

	/**
	 * Returns "true" if the rule is an "all" rule
	 */
	@Override
	public boolean isAllRule(Integer ruleNumber) throws Exception {
		return rules.get(ruleNumber).getClass().toString().equalsIgnoreCase("allRule");
	}

	/**
	 * Returns "true" if the rule is a "thread" rule
	 */
	@Override
	public boolean isThreadRule(Integer ruleNumber) throws Exception {
		return rules.get(ruleNumber).getClass().toString().equalsIgnoreCase("threadRule");
	}

	/**
	 * Returns a "base" rule's expression as is set in the rules document.
	 */
	@Override
	public String getExpression(Integer ruleNumber) throws Exception {
		return ((CalcRule) getRule(ruleNumber)).getExpression();
	}
	
	/**
	 * Returns a "base" rule's Java handler class as is set in the rules document.
	 */
	@Override
	public String getHandlerClass(Integer ruleNumber) throws Exception {
		return ((CalcRule) getRule(ruleNumber)).getHandlerClass();
	}
	

	/**
	 * Gets the list of thread rule numbers for a particular thread rule's list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getThreadRulesList(Integer ruleNumber) throws Exception {
		return ((ThreadRule) getRule(ruleNumber)).getThreadRules();
	}
	
	/**
	 * Gets the list of composite rule numbers for a particular composite rule's list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getCompositeRulesList(Integer ruleNumber) throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositeRules();
	}


	/**
	 * Returns the passScore for a particular rule as set in the rules document.
	 */
	@Override
	public ArrayList<RuleOutcome> getOutcomes(Integer ruleNumber) throws Exception {
		return ((Rule) getRule(ruleNumber)).getOutcomes();
	}
	
	/**
	 * Returns the failScore for a particular rule as set in the rules document.
	 */
	@Override
	public ArrayList<CompositeRuleOutcome> getCompositeOutcomes(Integer ruleNumber)  throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositeOutcomes();
	}


	/**
	 * Returns a Rule object for a particular rule number	
	 */
	@Override
	public Rule getRule(Integer ruleNumber) throws Exception {
		
		return rules.get(ruleNumber);
		
	}

	private void setToRulesMap() {
		setBaseRules();
		setOrRules();
		setAndRules();
		setAllRules();
		setThreadRules();
	}
	
	private  void setBaseRules() {
		ArrayList<CalcRule> ar = this.baseRules.getCalcRules();
		for (Iterator<CalcRule> iterator = ar.iterator(); iterator.hasNext();) {
			CalcRule calcRule = (CalcRule) iterator.next();
			this.rules.put(calcRule.getRuleNumber(), calcRule);
		}
	}

	private void setOrRules() {
		ArrayList<OrRule> ar = this.baseRules.getOrRules();
		for (Iterator<OrRule> iterator = ar.iterator(); iterator.hasNext();) {
			OrRule orRule = (OrRule) iterator.next();
			rules.put(orRule.getRuleNumber(), orRule);
		}
	}
	
	private void setAndRules() {
		ArrayList<AndRule> ar = this.baseRules.getAndRules();
		for (Iterator<AndRule> iterator = ar.iterator(); iterator.hasNext();) {
			AndRule andRule = (AndRule) iterator.next();
			rules.put(andRule.getRuleNumber(), andRule);
		}
	}
	
	private void setAllRules() {
		ArrayList<AllRule> ar = this.baseRules.getAllRules();
		for (Iterator<AllRule> iterator = ar.iterator(); iterator.hasNext();) {
			AllRule allRule = (AllRule) iterator.next();
			rules.put(allRule.getRuleNumber(), allRule);
		}
	}

	private void setThreadRules() {
		ArrayList<ThreadRule> ar = this.baseRules.getThreadRules();
		for (Iterator<ThreadRule> iterator = ar.iterator(); iterator.hasNext();) {
			ThreadRule threadRule = (ThreadRule) iterator.next();
			rules.put(threadRule.getRuleNumber(), threadRule);
		}
	}

}
