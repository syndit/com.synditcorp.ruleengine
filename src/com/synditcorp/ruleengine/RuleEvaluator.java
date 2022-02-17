/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine;

import java.util.ArrayList;
import java.util.TreeMap;

import org.slf4j.Logger;

import com.synditcorp.ruleengine.logging.TimeTrack;
import com.synditcorp.ruleengine.beans.CompositeRule;
import com.synditcorp.ruleengine.handlers.ExpressionHandler;
import com.synditcorp.ruleengine.interfaces.Rule;
import com.synditcorp.ruleengine.interfaces.RuleDefinition;
import com.synditcorp.ruleengine.logging.RuleLogger;

/**
 * This class provides the runtime methods for the rule engine.  Injected is a RuleDefinitions object that implements the RuleDefinitions interface.  This class is the 
 * primary class used to interact with the rule engine.  RuleDefinitions, the RuleParser, or other rule engine classes need not be accessed directly.
 */
public class RuleEvaluator {

	private RuleDefinition ruleDefinition;
	private TreeMap<Integer, Boolean> cache = new TreeMap<Integer, Boolean>();
	private TreeMap<String, Object> variables = new TreeMap<String, Object>();
	private ArrayList<Integer> runtimePasses = new ArrayList<Integer>();
	private ArrayList<Integer> runtimeFails = new ArrayList<Integer>();

	public RuleEvaluator(RuleDefinition rulesDefinition, Logger logger) {
		this.ruleDefinition = rulesDefinition;
		RuleLogger.logger = logger;
	}
	
	/**
	 * Returns the ID of the rule definition 
	 */
	public String getDefinitionID() {
		return ruleDefinition.getDefinitionID();
	}
	
	/**
	 * Returns the description of the rule definition 
	 */
	public String getDescription() {
		return ruleDefinition.getDescription();
	}
	
	/**
	 * Returns the version of the rule definition
	 */
	public String getVersion() {
		return ruleDefinition.getVersion();
	}

	/**
	 * Optional document tags are used to further define a document.  Document tags are not used at runtime
	 * to evaluate rules.  Tags can be used for things like authorization in databases or display control in
	 * custom rule definition editors
	 */
	public ArrayList<String> getDocumentTags() {
		return ruleDefinition.getDocumentTags();
	}
	
	/**
	 * This is intended to hold the rule number at the base of the decision tree so calling programs can refer to
	 * this value at runtime rather than having to rely on other processes to communicate the starting rule of
	 * a decision tree.  This is optional: any rule can be called directly.  This is not specifically used when
	 * evaluating rules at runtime.
	 */
	public Integer getStartRule() {
		return ruleDefinition.getStartRule();
	}
	
	/**
	 * Optional rule tags are used to further define a rule, but are not specifically used when evaluating
	 * rules at runtime.  Tags can be used for things like authorization in databases or display control in
	 * custom rule definition editors
	 */
	public ArrayList<String> getRuleTags(Integer ruleNumber) throws Exception {
		return ruleDefinition.getRuleTags(ruleNumber);
	}
	
	/**
	 * Evaluate the rule referenced by rule number.  The rule number must be one of the rules referenced in the document parsed by the engine's parser.
	 */
	public boolean evaluateRule(Integer ruleNumber) throws Exception {
		
		boolean result = callRule(ruleNumber);
		
		return ( result );

	}
	
	/**
	 * This clears three collections.  First, the rule "passes" (those that evaluate to true) are cleared.  Next, the rule "fails" (those that evaluate to false) 
	 * are cleared. Third, variables passed into the engine, as well as those accumulated by the engine (e.g. scores, actions, etc.) at runtime, are cleared.
	 * WARNING: variables must be set using setVariables() before the engine can be run again if the expressions to be evaluated need the variables.
	 */
	public void reset() {
		clearCache();
		clearRuntimePasses();
		clearRuntimeFails();
		clearVariables();
	}
	
	/**
	 * Set the variables the rules engine will use in expressions, or passed to custom rule handlers.
	 */
	public void setVariables(TreeMap<String, Object> variables) {
		this.variables = variables;
	}

	/**
	 * Get the variables used by the rules engine.  This includes set variables as well as runtime variables generated at runtime
	 */
	public TreeMap<String, Object> getVariables() {
		return this.variables;
	}

	/**
	 * Get the passKey for a particular rule.  This returns the passKey set in the rules document and that evaluated to "true" at runtime.
	 */
	public String getPassKey(Integer ruleNumber) throws Exception {
		if(!runtimePasses.contains(ruleNumber)) return null;
		return ruleDefinition.getPassKey(ruleNumber);
	}
	
	/**
	 * Get the failKey for a particular rule.  This returns the failKey set in the rules document and that evaluated to "true" at runtime.
	 */
	public String getFailKey(Integer ruleNumber) throws Exception {
		if(!runtimeFails.contains(ruleNumber)) return null;
		return ruleDefinition.getFailKey(ruleNumber);
	}

	/**
	 * Get the passScore for a particular rule.  This returns the results of the passScore expression set in the rules document and that evaluated to "true" at runtime.
	 */
	public Double getPassScore(Integer ruleNumber) throws Exception {
		if(!runtimePasses.contains(ruleNumber)) return null;
		if(ruleDefinition.getPassScore(ruleNumber) == null) return null;
		return evaluateExpression(ruleDefinition.getPassScore(ruleNumber));
	}
	
	/**
	 * Get the failScore for a particular rule.  This returns the results of the failScore expression set in the rules document and that evaluated to "true" at runtime.
	 */
	public Double getFailScore(Integer ruleNumber) throws Exception {
		if(!runtimeFails.contains(ruleNumber)) return null;
		if(ruleDefinition.getFailScore(ruleNumber) == null) return null;
		return evaluateExpression(ruleDefinition.getFailScore(ruleNumber));
	}
	
	/**
	 * Get the passFlag for a particular rule.  This returns the passFlag set in the rules document and that evaluated to "true" at runtime.
	 */
	public String getPassFlag(Integer ruleNumber) throws Exception {
		if(!runtimePasses.contains(ruleNumber)) return null;
		return getRule(ruleNumber).getPassFlag();
	}
	
	/**
	 * Get the failFlag for a particular rule.  This returns the failFlag set in the rules document and that evaluated to "true" at runtime.
	 */
	public String getFailFlag(Integer ruleNumber) throws Exception {
		if(!runtimeFails.contains(ruleNumber)) return null;
		return ruleDefinition.getFailFlag(ruleNumber);
	}
	
	/**
	 * Get the passReason for a particular rule.  This returns the passReason set in the rules document and that evaluated to "true" at runtime.
	 */
	public String getPassReason(Integer ruleNumber) throws Exception {
		if(!runtimePasses.contains(ruleNumber)) return null;
		return ruleDefinition.getPassReason(ruleNumber);
	}
	
	/**
	 * Get the failReason for a particular rule.  This returns the failReason set in the rules document and that evaluated to "true" at runtime.
	 */
	public String getFailReason(Integer ruleNumber) throws Exception {
		if(!runtimeFails.contains(ruleNumber)) return null;
		return ruleDefinition.getFailReason(ruleNumber);
	}
	
	/**
	 * Get the passAction for a particular rule.  This returns the passAction set in the rules document and that evaluated to "true" at runtime.
	 */
	public String getPassAction(Integer ruleNumber) throws Exception {
		if(!runtimePasses.contains(ruleNumber)) return null;
		return ruleDefinition.getPassAction(ruleNumber);
	}
	
	/**
	 * Get the failAction for a particular rule.  This returns the failAction set in the rules document and that evaluated to "true" at runtime.
	 */
	public String getFailAction(Integer ruleNumber) throws Exception {
		if(!runtimeFails.contains(ruleNumber)) return null;
		return ruleDefinition.getFailAction(ruleNumber);
	}
	
	/**
	 * This returns the rule cache, which is merely the list of calc rules and their runtime boolean results.  The cache is used to store calc rule results so
	 * multiple calls to a calc rule don't have to evaluate once initally evaluated.
	 */
	public TreeMap<Integer, Boolean> getCacheMap() throws Exception {
		return this.cache;
	}

	/**
	 * This allows adding to the rule cache.  Use this if rule evaluation needs to continue where it left off from a previous run.
	 */
	public void addMapToCache(TreeMap<Integer, Boolean> cache) throws Exception {
		this.cache.putAll(cache);
	}
	
	/**
	 * Gets the compositePassKeys for a particular rule.  This returns a list of passKeys for the composite rule (set in the rules document)    
	 * that evaluated to "true" at runtime.
	 */
	public ArrayList<String> getCompositePassKeys(Integer ruleNumber) throws Exception {

		ArrayList<String> keys = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositePassKeys();
			if(list == null) return keys;

			for (int i = 0; i < list.size(); i++) {
				if(getPassKey(list.get(i)) == null) continue;
				keys.add(getPassKey(list.get(i)));
			}
		}

		return keys;

	}

	/**
	 * Gets the compositeFailKeys for a particular rule.  This returns a list of failKeys for the composite rule (set in the rules document)    
	 * that evaluated to "false" at runtime.
	 */
	public ArrayList<String> getCompositeFailKeys(Integer ruleNumber) throws Exception {

		ArrayList<String> keys = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositeFailKeys();
			if(list == null) return keys;

			for (int i = 0; i < list.size(); i++) {
				if(getFailKey(list.get(i)) == null) continue;
				keys.add(getFailKey(list.get(i)));
			}
		}

		return keys;

	}

	/**
	 * Gets the compositePassScore for a particular rule.  This returns the sum of the passScores for the composite rule (set in the rules document)    
	 * that evaluated to "true" at runtime.
	 */
	public Double getCompositePassScore(Integer ruleNumber) throws Exception {

		double calcScore = 0.00;

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositePassScore();			
			if(list == null) return null;
			for (int i = 0; i < list.size(); i++) {
				if(getPassScore(list.get(i)) == null) continue;
				double ruleScore = getPassScore(list.get(i)).doubleValue();
				calcScore = calcScore + ruleScore;
			}
		}
		
		return Double.valueOf(calcScore);

	}

	/**
	 * Gets the compositeFailScore for a particular rule.  This returns the sum of the failScores for the composite rule (set in the rules document)    
	 * that evaluated to "false" at runtime.
	 */
	public Double getCompositeFailScore(Integer ruleNumber) throws Exception {

		double calcScore = 0.00;

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositeFailScore();
			if(list == null) return null;
			for (int i = 0; i < list.size(); i++) {
				if(getFailScore(list.get(i)) == null) continue;
				double ruleScore = getFailScore(list.get(i)).doubleValue();
				calcScore = calcScore + ruleScore;
			}
		}

		return Double.valueOf(calcScore);

	}

	/**
	 * Gets the compositePassFlags for a particular rule.  This returns a list of passFlags for the composite rule (set in the rules document)    
	 * that evaluated to "true" at runtime.
	 */
	public ArrayList<String> getCompositePassFlags(Integer ruleNumber) throws Exception {

		ArrayList<String> flags = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositePassFlags();
			if(list == null) return flags;
			
			for (int i = 0; i < list.size(); i++) {
				if(getPassFlag(list.get(i)) == null) continue;
				flags.add(getPassFlag(list.get(i)));
			}
		}

		return flags;

	}

	/**
	 * Gets the compositeFailFlags for a particular rule.  This returns a list of failFlags for the composite rule (set in the rules document)    
	 * that evaluated to "false" at runtime.
	 */
	public ArrayList<String> getCompositeFailFlags(Integer ruleNumber) throws Exception {

		ArrayList<String> flags = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositeFailFlags();
			if(list == null) return flags;
			
			for (int i = 0; i < list.size(); i++) {
				if(getFailFlag(list.get(i)) == null) continue;
				flags.add(getFailFlag(list.get(i)));
			}
		}

		return flags;

	}

	/**
	 * Gets the compositePassReasons for a particular rule.  This returns a list of passReasons for the composite rule (set in the rules document)    
	 * that evaluated to "true" at runtime.
	 */
	public ArrayList<String> getCompositePassReasons(Integer ruleNumber) throws Exception {

		ArrayList<String> reasons = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositePassReasons();
			if(list == null) return reasons;
			
			for (int i = 0; i < list.size(); i++) {
				if(getPassReason(list.get(i)) == null) continue;
				reasons.add(getPassReason(list.get(i)));
			}
		}

		return reasons;

	}

	/**
	 * Gets the compositeFailReasons for a particular rule.  This returns a list of failReasons for the composite rule (set in the rules document)    
	 * that evaluated to "false" at runtime.
	 */
	public ArrayList<String> getCompositeFailReasons(Integer ruleNumber) throws Exception {

		ArrayList<String> reasons = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositeFailReasons();
			if(list == null) return reasons;
			
			for (int i = 0; i < list.size(); i++) {
				if(getFailReason(list.get(i)) == null) continue;
				reasons.add(getFailReason(list.get(i)));
			}
		}

		return reasons;

	}

	/**
	 * Gets the compositePassActions for a particular rule.  This returns a list of passActions for the composite rule (set in the rules document)    
	 * that evaluated to "true" at runtime.
	 */
	public ArrayList<String> getCompositePassActions(Integer ruleNumber) throws Exception {

		ArrayList<String> actions = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositePassActions();
			if(list == null) return actions;
			
			for (int i = 0; i < list.size(); i++) {
				if(getPassAction(list.get(i)) == null) continue;
				actions.add(getPassAction(list.get(i)));
			}
		}

		return actions;

	}

	/**
	 * Gets the compositeFailActions for a particular rule.  This returns a list of failActions for the composite rule (set in the rules document)    
	 * that evaluated to "false" at runtime.
	 */
	public ArrayList<String> getCompositeFailActions(Integer ruleNumber) throws Exception {

		ArrayList<String> actions = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositeFailActions();
			if(list == null) return actions;
			
			for (int i = 0; i < list.size(); i++) {
				if(getFailAction(list.get(i)) == null) continue;
				actions.add(getFailAction(list.get(i)));
			}
		}

		return actions;

	}
	


	/**
	 * This method sets a rule's passKey, passScore, passFlag, passReason, and passAction values to the engine's variables, which can be used by 
	 * other rules' expressions.  The intent is to call this function when a rule passes at runtime in the rule engine evaluator.  Only rules that
	 * evaluate to "true" are included (included in runtimePasses).  A rule must have been called prior to using its variables.
	 */
	private void addRulePassResultsToVariables(Integer ruleNumber, TreeMap<String, Object> variables) throws Exception {
		
		if(! runtimePasses.contains(ruleNumber)) return;
		
		String ruleNoStr = ruleNumber.toString();

		String passKey = getPassKey(ruleNumber); 
		if(passKey != null) variables.put( ("passKey_" +ruleNoStr), passKey);
		
		Double passScore = getPassScore(ruleNumber);
		if(passScore != null) variables.put( ("passScore_" +ruleNoStr), passScore);

		String passFlag = getPassFlag(ruleNumber); 
		if(passFlag != null) variables.put( ("passFlag_" +ruleNoStr), passFlag);
		
		String passReason = getPassReason(ruleNumber); 
		if(passReason != null) variables.put( ("passReason_" +ruleNoStr), passReason);

		String passAction = getPassAction(ruleNumber); 
		if(passAction != null) variables.put( ("passAction_" +ruleNoStr), passAction);
		
	}
	
	/**
	 * This method sets a rule's failKey, failScore, failFlag, failReason, and failAction values to the engine's variables, which can be used by 
	 * other rules' expressions. The intent is to call this function when a rule fails at runtime in the rule engine evaluator.  Only rules that
	 * evaluate to "false" are included (included in runtimeFails).  A rule must have been called prior to using its variables.
	 */
	private void addRuleFailResultsToVariables(Integer ruleNumber, TreeMap<String, Object> variables) throws Exception {
		
		if(! runtimeFails.contains(ruleNumber)) return;
		
		String ruleNumberStr = ruleNumber.toString();

		String failKey = getFailKey(ruleNumber); 
		if(failKey != null) variables.put( ("failKey_" + ruleNumberStr), failKey);
		
		Double failScore = getFailScore(ruleNumber);
		if(failScore != null) variables.put( ("failScore_" + ruleNumberStr), failScore);
		
		String failFlag = getFailFlag(ruleNumber); 
		if(failFlag != null) variables.put( ("failFlag_" + ruleNumberStr), failFlag);
		
		String failReason = getFailReason(ruleNumber); 
		if(failReason != null) variables.put( ("failReason_" + ruleNumberStr), failReason);
		
		String failAction = getFailAction(ruleNumber); 
		if(failAction != null) variables.put( ("failAction_" + ruleNumberStr), failAction);
		
	}
	
	/**
	 * This method sets a "calc" or composite rule's passKey, passScore, passFlag, passReason, and passAction values as well as the corresponding composite pass values 
	 * to the engine's variables, which can be used by other rules' expressions.  The intent is to call this function when a composite rule passes at runtime in the rule
	 * engine evaluator.  Only composite rules that evaluate to "true" are included (included in runtimePasses).  A rule must have been called prior to using its variables.
	 */
	private void addCompositeRulePassResultsToVariables(Integer ruleNumber, TreeMap<String, Object> variables) throws Exception {

		if(! runtimePasses.contains(ruleNumber)) return;
		
		addRulePassResultsToVariables(ruleNumber, variables);
		
		String ruleNumberStr = ruleNumber.toString();

		ArrayList<String> passKeys = getCompositePassKeys(ruleNumber);
		if(passKeys != null && !passKeys.isEmpty()) variables.put( ("compositePassKeys_" + ruleNumberStr), passKeys);
		
		Double passScore = getCompositePassScore(ruleNumber);
		if(passScore != null) variables.put( ("compositePassScore_" + ruleNumberStr), passScore);
		
		ArrayList<String> passFlags = getCompositePassFlags(ruleNumber);
		if(passFlags != null && !passFlags.isEmpty()) variables.put( ("compositePassFlags_" + ruleNumberStr), passFlags);
		
		ArrayList<String> passReasons = getCompositePassReasons(ruleNumber);
		if(passReasons != null && !passReasons.isEmpty()) variables.put( ("compositePassReasons_" + ruleNumberStr), passReasons);
		
		ArrayList<String> passActions = getCompositePassActions(ruleNumber);
		if(passActions != null && !passActions.isEmpty()) variables.put( ("compositePassActions_" + ruleNumberStr), passActions);

	}

	/**
	 * This method sets a "calc" or composite rule's failKey, failScore, failFlag, failReason, and failAction values as well as the corresponding composite fail values 
	 * to the engine's variables, which can be used by other rules' expressions.  The intent is to call this function when a composite rule fails at runtime in the rule
	 * engine evaluator.  Only composite rules that evaluate to "false" are included (included in runtimeFails).  A rule must have been called prior to using its variables.
	 */
	private void addCompositeRuleFailResultsToVariables(Integer ruleNumber, TreeMap<String, Object> variables) throws Exception {
		
		if(! runtimeFails.contains(ruleNumber)) return;
		
		addRuleFailResultsToVariables(ruleNumber, variables);
		
		String ruleNumberStr = ruleNumber.toString();

		ArrayList<String> failKeys = getCompositeFailKeys(ruleNumber);
		if(failKeys != null && !failKeys.isEmpty()) variables.put( ("compositeFailKeys_" + ruleNumberStr), failKeys);
		
		Double failScore = getCompositeFailScore(ruleNumber);
		if(failScore != null) variables.put( ("compositeFailScore_" + ruleNumberStr), failScore);

		ArrayList<String> failFlags = getCompositeFailFlags(ruleNumber);
		if(failFlags != null && !failFlags.isEmpty()) variables.put( ("compositeFailFlags_" + ruleNumberStr), failFlags);
		
		ArrayList<String> failReasons = getCompositeFailReasons(ruleNumber);
		if(failReasons != null && !failReasons.isEmpty()) variables.put( ("compositeFailReasons_" + ruleNumberStr), failReasons);
		
		ArrayList<String> failActions = getCompositeFailActions(ruleNumber);
		if(failActions != null && !failActions.isEmpty()) variables.put( ("compositeFailActions_" + ruleNumberStr), failActions);

	}
	
	private boolean callRule(Integer ruleNumber) throws Exception {

		if(isInCalcRules(ruleNumber)) return (processCalcRule(ruleNumber));

		if(isInOrRules(ruleNumber)) return (processOrRules(ruleNumber));
		
		if(isInAndRules(ruleNumber)) return (processAndRules(ruleNumber));
		
		if(isInAllRules(ruleNumber)) return (processAllRules(ruleNumber));
		
		throw new Exception("Rule number " + ruleNumber + " not found in rule definitions.");

	}

	private Rule getRule(Integer ruleNumber) throws Exception {
		return ruleDefinition.getRule(ruleNumber);
	}
	
	private void clearVariables() {
		this.variables = null;
	}
	
	private void addToCache(Integer ruleNumber, Boolean result) {
		cache.put(ruleNumber, result);
	}
	
	private void clearCache() {
		cache.clear();
	}

	private void addRuntimePass(Integer ruleNumber) {
		if(!runtimePasses.contains(ruleNumber)) runtimePasses.add(ruleNumber);
	}
	
	private void addRuntimeFail(Integer ruleNumber) {
		if(!runtimeFails.contains(ruleNumber)) runtimeFails.add(ruleNumber);
	}
	
	private void clearRuntimePasses() {
		runtimePasses.clear();
	}
	
	private void clearRuntimeFails() {
		runtimeFails.clear();
	}
	
	private boolean isInCalcRules(Integer ruleNumber) throws Exception {
		return (ruleDefinition.isCalcRule(ruleNumber));
	}

	private boolean isInOrRules(Integer ruleNumber) throws Exception {
		return (ruleDefinition.isOrRule(ruleNumber));
	}
	
	private boolean isInAndRules(Integer ruleNumber) throws Exception {
		return (ruleDefinition.isAndRule(ruleNumber));
	}
	
	private boolean isInAllRules(Integer ruleNumber) throws Exception {
		return (ruleDefinition.isAllRule(ruleNumber));
	}
	
	private boolean processCalcRule(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();
		
		Boolean cachedResult = cache.get(ruleNumber);
		if( cachedResult != null ) {
			return cachedResult;
		}

		String ruleHandler = ruleDefinition.getHandlerClass(ruleNumber);
		String expression = ruleDefinition.getExpression(ruleNumber);
		Boolean result = CalcRuleProcessor.processCalcRule(ruleHandler, expression, variables);
		
		addToCache(ruleNumber, result);

		if(result) {
			addRuntimePass(ruleNumber);
			addRulePassResultsToVariables(ruleNumber, variables);
		}
		else {
			addRuntimeFail(ruleNumber);
			addRuleFailResultsToVariables(ruleNumber, variables);
		}

		RuleLogger.log("{} milleseconds to evaluate rule number {} expression: {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, expression, result);
		
		return ( result );

	}

	/**
	 * The engine processes all rules listed in the AllRule bean compositeRules field list. AllRule rule types should be used when all rules must be processed
	 * for such things as setting bean field values that will be accessed by other rules. 
	 */
	private boolean processAllRules(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();
		
		ArrayList<Integer> compositeRuleList = getCompositeRulesList(ruleNumber);
		for (int i = 0; i < compositeRuleList.size(); i++) {

			if(processRule(compositeRuleList.get(i))) {
				addRuntimePass(ruleNumber);
				addCompositeRulePassResultsToVariables(ruleNumber, variables);
				RuleLogger.log("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, true);
			} else {
				addRuntimeFail(ruleNumber);
				addCompositeRuleFailResultsToVariables(ruleNumber, variables);
				RuleLogger.log("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, false);
			}

		}
		return true;
		
	}
	
	/**
	 * The engine process all rules listed in the OrRule bean compositeRules field list up to the first pass.  Only those rules evaluated at runtime
	 * set bean field values. 
	 */
	private boolean processOrRules(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();
		
		ArrayList<Integer> compositeRuleList = getCompositeRulesList(ruleNumber);
		for (int i = 0; i < compositeRuleList.size(); i++) {
			if(processRule(compositeRuleList.get(i))) {
				addRuntimePass(ruleNumber);
				addCompositeRulePassResultsToVariables(ruleNumber, variables);
				RuleLogger.log("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, true);
				return (true);
			}
		}

		addRuntimeFail(ruleNumber);
		addCompositeRuleFailResultsToVariables(ruleNumber, variables);
		RuleLogger.log("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, false);

		return false;

	}
	
	/**
	 * The engine processes all rules listed in the AndRule bean compositeRules field list up to the first fail.  Only those rules evaluated at runtime
	 * set bean field values. 
	 */
	private boolean processAndRules(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();

		ArrayList<Integer> compositeRuleList = getCompositeRulesList(ruleNumber);

		for (int i = 0; i < compositeRuleList.size(); i++) {
			if(!processRule(compositeRuleList.get(i))) {
				addRuntimeFail(ruleNumber);
				addCompositeRuleFailResultsToVariables(ruleNumber, variables);
				RuleLogger.log("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, false);
				return false;
			}			
			
		}
		
		addRuntimePass(ruleNumber);
		addCompositeRulePassResultsToVariables(ruleNumber, variables);
		RuleLogger.log("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, true);
		
		return true;

	}

	private boolean processRule(Integer ruleNumber) throws Exception {

		int abs = Math.abs(ruleNumber); 
		boolean result = callRule(abs);
		if( ruleNumber.intValue() < 0 ) { //negative number means a "not" rule
			return (!result);
		}
		return (result);

	}
	
	private ArrayList<Integer> getCompositeRulesList(Integer ruleNumber) throws Exception {
		return ruleDefinition.getCompositeRulesList(ruleNumber);
	}

	private Double evaluateExpression(String expression) {
		return ExpressionHandler.getProductOf(expression, variables);
	}
	
	
}
