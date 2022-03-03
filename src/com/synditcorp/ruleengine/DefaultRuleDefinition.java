/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine;

import com.synditcorp.ruleengine.interfaces.RuleDefinition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import com.synditcorp.ruleengine.beans.AllRule;
import com.synditcorp.ruleengine.beans.AndRule;
import com.synditcorp.ruleengine.beans.CalcRule;
import com.synditcorp.ruleengine.beans.OrRule;
import com.synditcorp.ruleengine.beans.BaseRules;
import com.synditcorp.ruleengine.beans.CompositeRule;
import com.synditcorp.ruleengine.interfaces.Rule;
import com.synditcorp.ruleengine.interfaces.RuleParser;

/**
 * This class loads rule definitions from parsers that implement com.synditcorp.ruleengine.interfaces.RuleParser.  Methods of this class provide
 * access to base and composite rule field objects.  After loading the rules, this class is primarily to be used by the rule engine evaluator
 * and shouldn't be accessed directly.
 */
public class DefaultRuleDefinition implements RuleDefinition {

	private BaseRules baseRules;
	private TreeMap<Integer, CalcRule> calcRules = new TreeMap<Integer, CalcRule>();
	private TreeMap<Integer, OrRule> orRules = new TreeMap<Integer, OrRule>();
	private TreeMap<Integer, AndRule> andRules = new TreeMap<Integer, AndRule>();
	private TreeMap<Integer, AllRule> allRules = new TreeMap<Integer, AllRule>();
	
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
		setManifest(this.baseRules);
		
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
	 * Gets the passAction for a particular rule.  This returns the passAction set in the rules document.
	 */
	@Override
	public String getPassAction(Integer ruleNumber) throws Exception {
		return getRule(ruleNumber).getPassAction();	}

	/**
	 * Gets the failAction for a particular rule.  This returns the failAction set in the rules document.
	 */
	@Override
	public String getFailAction(Integer ruleNumber) throws Exception {
		return getRule(ruleNumber).getFailAction();	}

	
	/**
	 * Gets the list of composite rule numbers for a particular composite rule's list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getCompositeRulesList(Integer ruleNumber) throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositeRules();
	}
	
	/**
	 * Gets the list of composite rule numbers for a particular composite rule's passScore list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getCompositePassScoreList(Integer ruleNumber) throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositePassScore();
	}

	/**
	 * Gets the list of composite rule numbers for a particular composite rule's failScore list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getCompositeFailScoreList(Integer ruleNumber) throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositeFailScore();
	}

	/**
	 * Gets the list of composite rule numbers for a particular composite rule's passActions list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getCompositePassActionsList(Integer ruleNumber) throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositePassActions();
	}

	/**
	 * Gets the list of composite rule numbers for a particular composite rule's failActions list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getCompositeFailActionsList(Integer ruleNumber) throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositeFailActions();
	}

	/**
	 * Gets the list of composite rule numbers for a particular composite rule's passFlags list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getCompositePassFlagsList(Integer ruleNumber) throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositePassFlags();
	}

	/**
	 * Gets the list of composite rule numbers for a particular composite rule's failFlags list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getCompositeFailFlagsList(Integer ruleNumber) throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositeFailFlags();
	}

	/**
	 * Gets the list of composite rule numbers for a particular composite rule's passReasons list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getCompositePassReasonsList(Integer ruleNumber) throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositePassReasons();
	}

	/**
	 * Gets the list of composite rule numbers for a particular composite rule's failReasons list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getCompositeFailReasonsList(Integer ruleNumber) throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositeFailReasons();
	}

	/**
	 * Gets the list of composite rule numbers for a particular composite rule's passKeys list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getCompositePassKeysList(Integer ruleNumber) throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositePassKeys();
	}

	/**
	 * Gets the list of composite rule numbers for a particular composite rule's failKeys list that is set in the rules document.
	 */
	@Override
	public ArrayList<Integer> getCompositeFailKeysList(Integer ruleNumber) throws Exception {
		return ((CompositeRule) getRule(ruleNumber)).getCompositeFailKeys();
	}

	/**
	 * Returns "true" if the rule is a "base" rule
	 */
	@Override
	public boolean isCalcRule(Integer ruleNumber) throws Exception {
		return calcRules.containsKey(ruleNumber);
	}
	
	/**
	 * Returns "true" if the rule is an "or" rule
	 */
	@Override
	public boolean isOrRule(Integer ruleNumber) throws Exception {
		return orRules.containsKey(ruleNumber);
	}
	
	/**
	 * Returns "true" if the rule is an "and" rule
	 */
	@Override
	public boolean isAndRule(Integer ruleNumber) throws Exception {
		return andRules.containsKey(ruleNumber);
	}

	/**
	 * Returns "true" if the rule is an "all" rule
	 */
	@Override
	public boolean isAllRule(Integer ruleNumber) throws Exception {
		return allRules.containsKey(ruleNumber);
	}

	/**
	 * Returns a "base" rule's MVEL expression as is set in the rules document.
	 */
	@Override
	public String getExpression(Integer ruleNumber) throws Exception {
		return calcRules.get(ruleNumber).getExpression();
	}
	
	/**
	 * Returns a "base" rule's Java handler class as is set in the rules document.
	 */
	@Override
	public String getHandlerClass(Integer ruleNumber) throws Exception {
		return calcRules.get(ruleNumber).getHandlerClass();
	}
	
	/**
	 * Returns the passKey for a particular rule as set in the rules document.
	 */
	@Override
	public String getPassKey(Integer ruleNumber)  throws Exception {
		return getRule(ruleNumber).getPassKey();
	}
	
	/**
	 * Returns the failKey for a particular rule as set in the rules document.
	 */
	@Override
	public String getFailKey(Integer ruleNumber)  throws Exception {
		return getRule(ruleNumber).getFailKey();
	}

	/**
	 * Returns the passScore for a particular rule as set in the rules document.
	 */
	@Override
	public String getPassScore(Integer ruleNumber)  throws Exception {
		return getRule(ruleNumber).getPassScore();
	}
	
	/**
	 * Returns the failScore for a particular rule as set in the rules document.
	 */
	@Override
	public String getFailScore(Integer ruleNumber)  throws Exception {
		return getRule(ruleNumber).getFailScore();
	}

	/**
	 * Returns the passFlag for a particular rule as set in the rules document.
	 */
	@Override
	public String getPassFlag(Integer ruleNumber) throws Exception {
		return getRule(ruleNumber).getPassFlag();
	}

	/**
	 * Returns the failFlag for a particular rule as set in the rules document.
	 */
	@Override
	public String getFailFlag(Integer ruleNumber) throws Exception {
		return getRule(ruleNumber).getFailFlag();
	}

	/**
	 * Returns the passReason for a particular rule as set in the rules document.
	 */
	@Override
	public String getPassReason(Integer ruleNumber) throws Exception {
		return getRule(ruleNumber).getPassReason();
	}

	/**
	 * Returns the failReason for a particular rule as set in the rules document.
	 */
	@Override
	public String getFailReason(Integer ruleNumber) throws Exception {
		return getRule(ruleNumber).getFailReason();
	}

	/**
	 * Returns a Rule object for a particular rule number	
	 */
	@Override
	public Rule getRule(Integer ruleNumber) throws Exception {

		if(isCalcRule(ruleNumber)) {
			return (Rule) calcRules.get(ruleNumber);
		} else if(isAndRule(ruleNumber)) {
			return (Rule) andRules.get(ruleNumber);
		} else if(isOrRule(ruleNumber)) {
			return (Rule) orRules.get(ruleNumber);
		} else if(isAllRule(ruleNumber)) {
			return (Rule) allRules.get(ruleNumber);
		}
		
		return null;
		
	}

	private void setManifest(BaseRules rules) {
		setBaseRulesToManifest(rules);
		setOrRulesToManifest(rules);
		setAndRulesToManifest(rules);
		setAllRulesToManifest(rules);
	}
	
	private void setBaseRulesToManifest(BaseRules rules) {
		ArrayList<CalcRule> ar = rules.getCalcRules();
		for (Iterator<CalcRule> iterator = ar.iterator(); iterator.hasNext();) {
			CalcRule calcRule = (CalcRule) iterator.next();
			calcRules.put(calcRule.getRuleNumber(), calcRule);
		}
	}

	private void setOrRulesToManifest(BaseRules rules) {
		ArrayList<OrRule> ar = rules.getOrRules();
		for (Iterator<OrRule> iterator = ar.iterator(); iterator.hasNext();) {
			OrRule orRule = (OrRule) iterator.next();
			orRules.put(orRule.getRuleNumber(), orRule);
		}
	}
	
	private void setAndRulesToManifest(BaseRules rules) {
		ArrayList<AndRule> ar = rules.getAndRules();
		for (Iterator<AndRule> iterator = ar.iterator(); iterator.hasNext();) {
			AndRule andRule = (AndRule) iterator.next();
			andRules.put(andRule.getRuleNumber(), andRule);
		}
	}

	
	private void setAllRulesToManifest(BaseRules rules) {
		ArrayList<AllRule> ar = rules.getAllRules();
		for (Iterator<AllRule> iterator = ar.iterator(); iterator.hasNext();) {
			AllRule allRule = (AllRule) iterator.next();
			allRules.put(allRule.getRuleNumber(), allRule);
		}
	}

}
