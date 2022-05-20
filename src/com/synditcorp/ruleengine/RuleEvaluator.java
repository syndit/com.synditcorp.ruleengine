/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine;

import static com.synditcorp.ruleengine.logging.RuleLogger.LOGGER;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;

import com.synditcorp.ruleengine.beans.CompositeRule;
import com.synditcorp.ruleengine.beans.ThreadResults;
import com.synditcorp.ruleengine.exceptions.NoRuleEvaluatedException;
import com.synditcorp.ruleengine.handlers.ExpressionHandler;
import com.synditcorp.ruleengine.interfaces.Rule;
import com.synditcorp.ruleengine.interfaces.RuleDefinition;
import com.synditcorp.ruleengine.logging.TimeTrack;
import com.synditcorp.ruleengine.processors.CalcRuleProcessor;
import com.synditcorp.ruleengine.processors.ThreadRuleProcessor;

/**
 * This class provides the runtime methods for the rule engine.  Injected is a RuleDefinitions object that implements the RuleDefinitions interface.  This class is the 
 * primary class used to interact with the rule engine.  RuleDefinitions, the RuleParser, or other rule engine classes need not be accessed directly.
 */
public class RuleEvaluator implements Cloneable {

	private RuleDefinition ruleDefinition;
	private TreeMap<Integer, Boolean> cache = new TreeMap<Integer, Boolean>();
	private TreeMap<String, Object> variables = new TreeMap<String, Object>();
	private ArrayList<Integer> runtimePasses = new ArrayList<Integer>(1000);
	private ArrayList<Integer> runtimeFails = new ArrayList<Integer>(1000);
	private ArrayList<Integer> runtineExpressionFails = new ArrayList<Integer>(1000);
	private int threadBlockSize = 100;
	private ForkJoinPool pool = null;

	public RuleEvaluator(RuleDefinition rulesDefinition) {
		this.ruleDefinition = rulesDefinition;
	}
	
	/**
	 * Returns the ID of the rule definition
	 * @return document ID
	 */
	public String getDocumentId() {
		return ruleDefinition.getDocumentId();
	}
	
	/**
	 * Returns the description of the rule definition 
	 * @return document description
	 */
	public String getDescription() {
		return ruleDefinition.getDescription();
	}
	
	/**
	 * Returns the version of the rule definition
	 * @return document version
	 */
	public String getVersion() {
		return ruleDefinition.getVersion();
	}

	/**
	 * Optional document tags are used to further define a document.  Document tags are not used at runtime
	 * to evaluate rules.  Tags can be used for things like authorization in databases or display control in
	 * custom rule definition editors
	 * @return list of document tags
	 */
	public ArrayList<String> getDocumentTags() {
		return ruleDefinition.getDocumentTags();
	}
	
	/**
	 * This is intended to hold the rule number at the base of the decision tree so calling programs can refer to
	 * this value at runtime rather than having to rely on other processes to communicate the starting rule of
	 * a decision tree.  This is optional: any rule can be called directly.  This is not specifically used when
	 * evaluating rules at runtime.
	 * @return document's start rule number
	 */
	public Integer getStartRule() {
		return ruleDefinition.getStartRule();
	}
	
	/**
	 * Optional rule tags are used to further define a rule, but are not specifically used when evaluating
	 * rules at runtime.  Tags can be used for things like authorization in databases or display control in
	 * custom rule definition editors
	 * @return list of a rule's tags
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getRuleTags(Integer ruleNumber) throws Exception {
		return ruleDefinition.getRuleTags(ruleNumber);
	}
	
	/**
	 * Evaluate the rule referenced by rule number.  The rule number must be one of the rules referenced in the document parsed by the engine's parser.
	 * @return a rule's evaluation results
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public boolean evaluateRule(Integer ruleNumber) throws Exception {
		
		LOGGER.info("Syndit Rule Engine evaluating rule number {} using document ID {}, version {}", ruleNumber, this.getDocumentId(), this.getVersion());

		Boolean result = callRule(ruleNumber);
		
		if(result == null) throw new NoRuleEvaluatedException();

		LOGGER.info("Syndit Rule Engine completed evaluation of rule number {} with result equal to {}", ruleNumber, result);
		
		return ( result.booleanValue() );

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
	 * Set the block size to use when processing ThreadRules.  The default is 100.
	 * @param threadBlockSize for processing ThreadRules
	 */
	public void setThreadBlockSize(int threadBlockSize) {
		this.threadBlockSize = threadBlockSize;
	}
	
	/**
	 * Set the variables the rules engine will use in expressions, or passed to custom rule handlers.
	 * @param variables for the Engine's expressions
	 */
	public void setVariables(TreeMap<String, Object> variables) {
		this.variables = variables;
	}

	/**
	 * Get the variables used by the rules engine.  This includes set variables as well as runtime variables generated at runtime
	 * @return the TreeMap object containing the variables passed to and generated by the Engine
	 */
	public TreeMap<String, Object> getVariables() {
		return this.variables;
	}

	/**
	 * Get the runtime rule processing results cache.
	 * @return the TreeMap object containing the rule number and Boolean processing results
	 */
	public TreeMap<Integer, Boolean> getCache() {
		return this.cache;
	}

	/**
	 * Get the list of rules that passed at runtime.
	 * @return an ArrayList of rule numbers that passed
	 */
	public ArrayList<Integer> getRuntimePasses() {
		return this.runtimePasses;
	}

	/**
	 * Get the list of rules that failed at runtime.
	 * @return an ArrayList of rule numbers that failed
	 */
	public ArrayList<Integer> getRuntimeFails() {
		return this.runtimeFails;
	}
	
	/**
	 * Get the list of rules whose expressions failed at runtime.
	 * @return an ArrayList of rule number expression that failed
	 */
	public ArrayList<Integer> getRuntimeExpressionFails() {
		return this.runtineExpressionFails;
	}

	/**
	 * Get the passKey for a particular rule.  This returns the passKey set in the rules document and that evaluated to "true" at runtime.
	 * @return the pass keys for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getPassKeys(Integer ruleNumber) throws Exception {
		if(!runtimePasses.contains(ruleNumber)) return null;
		return ruleDefinition.getPassKeys(ruleNumber);
	}
	
	/**
	 * Get the failKeys for a particular rule.  This returns the failKey set in the rules document and that evaluated to "true" at runtime.
	 * @return the fail keys a the rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getFailKeys(Integer ruleNumber) throws Exception {
		if(!runtimeFails.contains(ruleNumber)) return null;
		return ruleDefinition.getFailKeys(ruleNumber);
	}

	/**
	 * Get the passScore for a particular rule.  This returns the results of the passScore expression set in the rules document and that evaluated to "true" at runtime.
	 * @return the pass score a the rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public Double getPassScore(Integer ruleNumber) throws Exception {
		if(!runtimePasses.contains(ruleNumber)) return null;
		if(ruleDefinition.getPassScore(ruleNumber) == null) return null;
		return evaluateExpression(ruleDefinition.getPassScore(ruleNumber));
	}
	
	/**
	 * Get the failScore for a particular rule.  This returns the results of the failScore expression set in the rules document and that evaluated to "true" at runtime.
	 * @return the fail score for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public Double getFailScore(Integer ruleNumber) throws Exception {
		if(!runtimeFails.contains(ruleNumber)) return null;
		if(ruleDefinition.getFailScore(ruleNumber) == null) return null;
		return evaluateExpression(ruleDefinition.getFailScore(ruleNumber));
	}
	
	/**
	 * Get the passFlags for a particular rule.  This returns the passFlag set in the rules document and that evaluated to "true" at runtime.
	 * @return the pass flags for the rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getPassFlags(Integer ruleNumber) throws Exception {
		if(!runtimePasses.contains(ruleNumber)) return null;
		return getRule(ruleNumber).getPassFlags();
	}
	
	/**
	 * Get the failFlags for a particular rule.  This returns the failFlag set in the rules document and that evaluated to "true" at runtime.
	 * @return the fail flags for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getFailFlags(Integer ruleNumber) throws Exception {
		if(!runtimeFails.contains(ruleNumber)) return null;
		return ruleDefinition.getFailFlags(ruleNumber);
	}
	
	/**
	 * Get the passReasons for a particular rule.  This returns the passReason set in the rules document and that evaluated to "true" at runtime.
	 * @return the pass reasons for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getPassReasons(Integer ruleNumber) throws Exception {
		if(!runtimePasses.contains(ruleNumber)) return null;
		return ruleDefinition.getPassReasons(ruleNumber);
	}
	
	/**
	 * Get the failReasons for a particular rule.  This returns the failReason set in the rules document and that evaluated to "true" at runtime.
	 * @return the fail reasons for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getFailReasons(Integer ruleNumber) throws Exception {
		if(!runtimeFails.contains(ruleNumber)) return null;
		return ruleDefinition.getFailReasons(ruleNumber);
	}
	
	/**
	 * Get the passAction for a particular rule.  This returns the passAction set in the rules document and that evaluated to "true" at runtime.
	 * @return the pass actions for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getPassActions(Integer ruleNumber) throws Exception {
		if(!runtimePasses.contains(ruleNumber)) return null;
		return ruleDefinition.getPassActions(ruleNumber);
	}
	
	/**
	 * Get the failAction for a particular rule.  This returns the failAction set in the rules document and that evaluated to "true" at runtime.
	 * @return the fail actions for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getFailActions(Integer ruleNumber) throws Exception {
		if(!runtimeFails.contains(ruleNumber)) return null;
		return ruleDefinition.getFailActions(ruleNumber);
	}
	
	/**
	 * This returns the rule cache, which is merely the list of calc rules and their runtime boolean results.  The cache is used to store calc rule results so
	 * multiple calls to a calc rule don't have to evaluate once initally evaluated.
	 * @return a TreeMap object with the runtime evaluation rule results
	 * @throws Exception when any exception occurs
	 */
	public TreeMap<Integer, Boolean> getCacheMap() throws Exception {
		return this.cache;
	}

	/**
	 * This allows adding to the rule cache.  Use this if rule evaluation needs to continue where it left off from a previous run.
	 * @throws Exception when any exception occurs
	 * @param cache TreeMap values to add to the current cache
	 */
	public void addMapToCache(TreeMap<Integer, Boolean> cache) throws Exception {
		this.cache.putAll(cache);
	}
	
	/**
	 * Gets the compositePassKeys for a particular rule.  This returns a list of passKeys for the composite rule (set in the rules document)    
	 * that evaluated to "true" at runtime.
	 * @return the composite pass keys for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getCompositePassKeys(Integer ruleNumber) throws Exception {

		ArrayList<String> keys = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositePassKeys();
			if(list == null) return keys;

			for (int i = 0; i < list.size(); i++) {
				ArrayList<String> passKeys = getPassKeys(list.get(i));
				if(passKeys == null) continue;
				for(int j = 0; j < passKeys.size(); j++) keys.add(passKeys.get(j));
			}
		}

		return keys;

	}

	/**
	 * Gets the compositeFailKeys for a particular rule.  This returns a list of failKeys for the composite rule (set in the rules document)    
	 * that evaluated to "false" at runtime.
	 * @return the composite fail keys for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getCompositeFailKeys(Integer ruleNumber) throws Exception {

		ArrayList<String> keys = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositeFailKeys();
			if(list == null) return keys;

			for (int i = 0; i < list.size(); i++) {
				ArrayList<String> failKeys = getFailKeys(list.get(i));
				if(failKeys == null) continue;
				for(int j = 0; j < failKeys.size(); j++) keys.add(failKeys.get(j));
			}
		}

		return keys;

	}

	/**
	 * Gets the compositePassScore for a particular rule.  This returns the sum of the passScores for the composite rule (set in the rules document)    
	 * that evaluated to "true" at runtime.
	 * @return the composite pass score for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
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
	 * @return the composite fail score for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
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
	 * @return the composite pass flags for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getCompositePassFlags(Integer ruleNumber) throws Exception {

		ArrayList<String> flags = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositePassFlags();
			if(list == null) return flags;
			
			for (int i = 0; i < list.size(); i++) {
				ArrayList<String> passFlags = getPassFlags(list.get(i));
				if(passFlags == null) continue;
				for(int j = 0; j < passFlags.size(); j++) flags.add(passFlags.get(j));
			}
		}

		return flags;

	}

	/**
	 * Gets the compositeFailFlags for a particular rule.  This returns a list of failFlags for the composite rule (set in the rules document)    
	 * that evaluated to "false" at runtime.
	 * @return the composite fail flags for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getCompositeFailFlags(Integer ruleNumber) throws Exception {

		ArrayList<String> flags = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositeFailFlags();
			if(list == null) return flags;
			
			for (int i = 0; i < list.size(); i++) {
				ArrayList<String> failFlags = getFailFlags(list.get(i));
				if(failFlags == null) continue;
				for(int j = 0; j < failFlags.size(); j++) flags.add(failFlags.get(j));
			}
		}

		return flags;

	}

	/**
	 * Gets the compositePassReasons for a particular rule.  This returns a list of passReasons for the composite rule (set in the rules document)    
	 * that evaluated to "true" at runtime.
	 * @return the composite pass reasons for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getCompositePassReasons(Integer ruleNumber) throws Exception {

		ArrayList<String> reasons = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositePassReasons();
			if(list == null) return reasons;
			
			for (int i = 0; i < list.size(); i++) {
				ArrayList<String> passReasons = getPassReasons(list.get(i));
				if(passReasons == null) continue;
				for(int j = 0; j < passReasons.size(); j++) reasons.add(passReasons.get(j));
			}
		}

		return reasons;

	}

	/**
	 * Gets the compositeFailReasons for a particular rule.  This returns a list of failReasons for the composite rule (set in the rules document)    
	 * that evaluated to "false" at runtime.
	 * @return the composite fail reasons for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getCompositeFailReasons(Integer ruleNumber) throws Exception {

		ArrayList<String> reasons = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositeFailReasons();
			if(list == null) return reasons;
			
			for (int i = 0; i < list.size(); i++) {
				ArrayList<String> failReasons = getFailReasons(list.get(i));
				if(failReasons == null) continue;
				for(int j = 0; j < failReasons.size(); j++) reasons.add(failReasons.get(j));
			}
		}

		return reasons;

	}

	/**
	 * Gets the compositePassActions for a particular rule.  This returns a list of passActions for the composite rule (set in the rules document)    
	 * that evaluated to "true" at runtime.
	 * @return the composite pass actions for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getCompositePassActions(Integer ruleNumber) throws Exception {

		ArrayList<String> actions = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositePassActions();
			if(list == null) return actions;
			
			for (int i = 0; i < list.size(); i++) {
				ArrayList<String> passActions = getPassActions(list.get(i));
				if(passActions == null) continue;
				for(int j = 0; j < passActions.size(); j++) actions.add(passActions.get(j));
			}
		}

		return actions;

	}

	/**
	 * Gets the compositeFailActions for a particular rule.  This returns a list of failActions for the composite rule (set in the rules document)    
	 * that evaluated to "false" at runtime.
	 * @return the composite fail actions for a rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getCompositeFailActions(Integer ruleNumber) throws Exception {

		ArrayList<String> actions = new ArrayList<String>();

		Rule rule = getRule(ruleNumber);
		if( rule instanceof CompositeRule) {
			CompositeRule cr = (CompositeRule) rule;
			ArrayList<Integer> list = cr.getCompositeFailActions();
			if(list == null) return actions;
			
			for (int i = 0; i < list.size(); i++) {
				ArrayList<String> failActions = getFailActions(list.get(i));
				if(failActions == null) continue;
				for(int j = 0; j < failActions.size(); j++) actions.add(failActions.get(j));
			}
		}

		return actions;

	}
	


	/**
	 * This method sets a rule's passKey, passScore, passFlag, passReason, and passAction values to the engine's variables, which can be used by 
	 * other rules' expressions.  The intent is to call this function when a rule passes at runtime in the rule engine evaluator.  Only rules that
	 * evaluate to "true" are included (included in runtimePasses).  A rule must have been called prior to using its variables.
	 * @throws Exception when any exception occurs
	 * @param ruleNumber, variables
	 */
	private void addRulePassResultsToVariables(Integer ruleNumber, TreeMap<String, Object> variables) throws Exception {
		
		if(! runtimePasses.contains(ruleNumber)) return;
		
		String ruleNoStr = ruleNumber.toString();

		ArrayList<String> passKeys = getPassKeys(ruleNumber); 
		if(passKeys != null && passKeys.size() > 0) variables.put( (this.getDocumentId() + "_passKeys_" +ruleNoStr), passKeys);
		
		Double passScore = getPassScore(ruleNumber);
		if(passScore != null) variables.put( (this.getDocumentId() + "_passScore_" +ruleNoStr), passScore);

		ArrayList<String> passFlags = getPassFlags(ruleNumber); 
		if(passFlags != null && passFlags.size() > 0) variables.put( (this.getDocumentId() + "_passFlags_" +ruleNoStr), passFlags);
		
		ArrayList<String> passReasons = getPassReasons(ruleNumber); 
		if(passReasons != null && passReasons.size() > 0) variables.put( (this.getDocumentId() + "_passReasons_" +ruleNoStr), passReasons);

		ArrayList<String> passActions = getPassActions(ruleNumber); 
		if(passActions != null) variables.put( (this.getDocumentId() + "_passActions_" +ruleNoStr), passActions);
		
	}
	
	/**
	 * This method sets a rule's failKey, failScore, failFlag, failReason, and failAction values to the engine's variables, which can be used by 
	 * other rules' expressions. The intent is to call this function when a rule fails at runtime in the rule engine evaluator.  Only rules that
	 * evaluate to "false" are included (included in runtimeFails).  A rule must have been called prior to using its variables.
	 * @throws Exception when any exception occurs
	 * @param ruleNumber, variables
	 */
	private void addRuleFailResultsToVariables(Integer ruleNumber, TreeMap<String, Object> variables) throws Exception {
		
		if(! runtimeFails.contains(ruleNumber)) return;
		
		String ruleNumberStr = ruleNumber.toString();

		ArrayList<String> failKeys = getFailKeys(ruleNumber); 
		if(failKeys != null && failKeys.size() > 0) variables.put( (this.getDocumentId() + "_failKeys_" + ruleNumberStr), failKeys);
		
		Double failScore = getFailScore(ruleNumber);
		if(failScore != null) variables.put( (this.getDocumentId() + "_failScore_" + ruleNumberStr), failScore);
		
		ArrayList<String> failFlags = getFailFlags(ruleNumber); 
		if(failFlags != null && failFlags.size() > 0) variables.put( (this.getDocumentId() + "_failFlags_" + ruleNumberStr), failFlags);
		
		ArrayList<String> failReasons = getFailReasons(ruleNumber); 
		if(failReasons != null && failReasons.size() > 0) variables.put( (this.getDocumentId() + "_failReasons_" + ruleNumberStr), failReasons);
		
		ArrayList<String> failActions = getFailActions(ruleNumber); 
		if(failActions != null && failActions.size() > 0) variables.put( (this.getDocumentId() + "_failActions_" + ruleNumberStr), failActions);
		
	}
	
	/**
	 * This method sets a "calc" or composite rule's passKey, passScore, passFlag, passReason, and passAction values as well as the corresponding composite pass values 
	 * to the engine's variables, which can be used by other rules' expressions.  The intent is to call this function when a composite rule passes at runtime in the rule
	 * engine evaluator.  Only composite rules that evaluate to "true" are included (included in runtimePasses).  A rule must have been called prior to using its variables.
	 * @throws Exception when any exception occurs
	 * @param ruleNumber, variables
	 */
	private void addCompositeRulePassResultsToVariables(Integer ruleNumber, TreeMap<String, Object> variables) throws Exception {

		if(! runtimePasses.contains(ruleNumber)) return;
		
		addRulePassResultsToVariables(ruleNumber, variables);
		
		String ruleNumberStr = ruleNumber.toString();

		ArrayList<String> passKeys = getCompositePassKeys(ruleNumber);
		variables.put( (this.getDocumentId() + "_compositePassKeys_" + ruleNumberStr), passKeys);
		
		Double passScore = getCompositePassScore(ruleNumber);
		variables.put( (this.getDocumentId() + "_compositePassScore_" + ruleNumberStr), passScore);
		
		ArrayList<String> passFlags = getCompositePassFlags(ruleNumber);
		variables.put( (this.getDocumentId() + "_compositePassFlags_" + ruleNumberStr), passFlags);
		
		ArrayList<String> passReasons = getCompositePassReasons(ruleNumber);
		variables.put( (this.getDocumentId() + "_compositePassReasons_" + ruleNumberStr), passReasons);
		
		ArrayList<String> passActions = getCompositePassActions(ruleNumber);
		variables.put( (this.getDocumentId() + "_compositePassActions_" + ruleNumberStr), passActions);

	}

	/**
	 * This method sets a "calc" or composite rule's failKey, failScore, failFlag, failReason, and failAction values as well as the corresponding composite fail values 
	 * to the engine's variables, which can be used by other rules' expressions.  The intent is to call this function when a composite rule fails at runtime in the rule
	 * engine evaluator.  Only composite rules that evaluate to "false" are included (included in runtimeFails).  A rule must have been called prior to using its variables.
	 * @throws Exception when any exception occurs
	 * @param ruleNumber, variables
	 */
	private void addCompositeRuleFailResultsToVariables(Integer ruleNumber, TreeMap<String, Object> variables) throws Exception {
		
		if(! runtimeFails.contains(ruleNumber)) return;
		
		addRuleFailResultsToVariables(ruleNumber, variables);
		
		String ruleNumberStr = ruleNumber.toString();

		ArrayList<String> failKeys = getCompositeFailKeys(ruleNumber);
		variables.put( (this.getDocumentId() + "_compositeFailKeys_" + ruleNumberStr), failKeys);
		
		Double failScore = getCompositeFailScore(ruleNumber);
		variables.put( (this.getDocumentId() + "_compositeFailScore_" + ruleNumberStr), failScore);

		ArrayList<String> failFlags = getCompositeFailFlags(ruleNumber);
		variables.put( (this.getDocumentId() + "_compositeFailFlags_" + ruleNumberStr), failFlags);
		
		ArrayList<String> failReasons = getCompositeFailReasons(ruleNumber);
		variables.put( (this.getDocumentId() + "_compositeFailReasons_" + ruleNumberStr), failReasons);
		
		ArrayList<String> failActions = getCompositeFailActions(ruleNumber);
		variables.put( (this.getDocumentId() + "_compositeFailActions_" + ruleNumberStr), failActions);

	}
	
	private Boolean callRule(Integer ruleNumber) throws Exception {

		if(isInCalcRules(ruleNumber)) return (processCalcRule(ruleNumber));

		if(isInOrRules(ruleNumber)) return (processOrRules(ruleNumber));
		
		if(isInAndRules(ruleNumber)) return (processAndRules(ruleNumber));
		
		if(isInAllRules(ruleNumber)) return (processAllRules(ruleNumber));
		
		if(isInThreadRules(ruleNumber)) return (processThreadRules(ruleNumber));

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
	
	private void addExpressionFail(Integer ruleNumber) {
		if(!runtineExpressionFails.contains(ruleNumber)) runtineExpressionFails.add(ruleNumber);
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
	
	private boolean isInThreadRules(Integer ruleNumber) throws Exception {
		return (ruleDefinition.isThreadRule(ruleNumber));
	}
	
	private Boolean processCalcRule(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();
		
		Boolean cachedResult = cache.get(ruleNumber);
		if( cachedResult != null ) {
			return cachedResult;
		}

		String ruleHandler = ruleDefinition.getHandlerClass(ruleNumber);
		String expression = ruleDefinition.getExpression(ruleNumber);
		
		Boolean result = null;
		try {
			result = CalcRuleProcessor.processCalcRule(ruleHandler, expression, variables);
		} catch (Exception e) {
			LOGGER.info("Unable to process rule expression: \"" + expression + "\", reason: " + e);
			addExpressionFail(ruleNumber);
			result = false;
		}
		
		addToCache(ruleNumber, result);

		if(result.booleanValue()) {
			addRuntimePass(ruleNumber);
			addRulePassResultsToVariables(ruleNumber, variables);
		}
		else {
			addRuntimeFail(ruleNumber);
			addRuleFailResultsToVariables(ruleNumber, variables);
		}

		LOGGER.debug("{} milleseconds to evaluate rule number {} expression: {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, expression, result);
		
		return ( result );

	}

	/**
	 * The engine processes all rules listed in the AllRule bean compositeRules field list. AllRule rule types should be used when all rules must be processed
	 * for such things as setting bean field values that will be accessed by other rules. 
	 * @return result from processing an All rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	private Boolean processAllRules(Integer ruleNumber) throws Exception {
		
		TimeTrack t = new TimeTrack();
		
		boolean noRulesProcessed = true;
		
		ArrayList<Integer> compositeRuleList = getCompositeRulesList(ruleNumber);
		if(compositeRuleList.size() == 0) return null;
		for (int i = 0; i < compositeRuleList.size(); i++) {
			Boolean result = processRule(compositeRuleList.get(i));
			if(result == null) continue;
			noRulesProcessed = false;
			if(result.booleanValue()) {
				addRuntimePass(ruleNumber);
				addCompositeRulePassResultsToVariables(ruleNumber, variables);
				LOGGER.debug("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, true);
			} else {
				addRuntimeFail(ruleNumber);
				addCompositeRuleFailResultsToVariables(ruleNumber, variables);
				LOGGER.debug("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, false);
			}

		}
		
		if(noRulesProcessed) return null;
		
		return true;
		
	}

	/**
	 * The Engine processes thread rules listed in the ThreadRule bean compositeRules field list. Thread rule types can be used when rules in the compositeRules list can be processed
	 * independently of other rules in the definition document, where processing order does not matter, and when performance is an issue. The rules are processed in blocks.  Set 
	 * the block size by calling setThreadBlockSize() and pass a block size value that performs optimally in your environment.  Variables set in the threads do not persist when evaluation is complete. 
	 * @return null if no rules are processed, otherwise if at least one rule passed, returns true, else returns false
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	private Boolean processThreadRules(Integer ruleNumber ) throws Exception {

		ArrayList<ThreadResults> threadResults = getThreadResults(ruleNumber);

		boolean noRulesProcessed = true;
		Boolean atLeastOneRulePassed = false;
		boolean isPassScore = false;
		boolean isFailScore = false;
		double passScore = 0;
		double failScore = 0;

		//loop through all the rule results processed in the threads
		for (int i = 0; i < threadResults.size(); i++) {

			if( threadResults.get(i).getResult() == null )  continue;
			noRulesProcessed = false;
			
			boolean thisRulePassed = false;

			if( threadResults.get(i).getResult() ) {
				atLeastOneRulePassed = true;
				thisRulePassed = true;
				//addRuntimePass(threadResults.get(i).getRuleNumber());
			} else {
				//addRuntimeFail(threadResults.get(i).getRuleNumber());
			}
			
			if( thisRulePassed && threadResults.get(i).getPassScore() != null ) {
				isPassScore = true;
				passScore += threadResults.get(i).getPassScore().doubleValue();
			}
			
			if( !thisRulePassed && threadResults.get(i).getFailScore() != null ) {
				isFailScore = true;
				failScore += threadResults.get(i).getFailScore().doubleValue();
			}
			
		}

		if(noRulesProcessed) return null;

		if(atLeastOneRulePassed) {
			addRuntimePass(ruleNumber);
			addRulePassResultsToVariables(ruleNumber, variables);
			if(isPassScore) {
				variables.put( ( this.getDocumentId() + "_compositePassScore_" + ruleNumber) , passScore);
			}
		}

		addRuntimeFail(ruleNumber);
		addRuleFailResultsToVariables(ruleNumber, variables);
		
		if(isFailScore) {
			variables.put( ( this.getDocumentId() + "_compositeFailScore_" + ruleNumber) , failScore);
		}
		
		return atLeastOneRulePassed;
		
	}
		
	/**
	 * To improve performance, as the name implies, ThreadRules are processed using threads.
	 * @return results in a ThreadResults object
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	private ArrayList<ThreadResults> getThreadResults(Integer ruleNumber) throws Exception {

		if(pool == null) pool = new ForkJoinPool();
		
		ArrayList<Integer> threadRulesList = getThreadRulesList(ruleNumber);
		ArrayList<Integer> block = new ArrayList<Integer>();
		ArrayList<ThreadRuleProcessor> tasks = new ArrayList<ThreadRuleProcessor>();
		int listSize = threadRulesList.size();
		int ctr = 0;
		for (int i = 0; i < listSize; i++) {
			block.add(threadRulesList.get(i));
			ctr++;
			if(ctr == threadBlockSize || (i+1) == listSize ) {
				ArrayList<Integer> passBlock = new ArrayList<Integer>();
				passBlock.addAll(block);
				ThreadRuleProcessor threadRuleProcessor = new ThreadRuleProcessor(passBlock, (RuleEvaluator) this.clone());
				tasks.add(threadRuleProcessor);
				pool.execute(threadRuleProcessor);
				block.clear();
				ctr = 0;	
			}
		}

		ArrayList<ThreadResults> threadResults = new ArrayList<ThreadResults>();
		
		for (int i = 0; i < tasks.size(); i++) {
			threadResults.addAll( tasks.get(i).get() );
		}

		return threadResults;
		
	}
	
	/**
	 * The engine process all rules listed in the OrRule bean compositeRules field list up to the first pass.  Only those rules evaluated at runtime
	 * set bean field values. 
	 * @return results from processing an Or rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	private Boolean processOrRules(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();
		
		boolean noRulesProcessed = true;
		
		ArrayList<Integer> compositeRuleList = getCompositeRulesList(ruleNumber);
		if(compositeRuleList.size() == 0) return null;
		for (int i = 0; i < compositeRuleList.size(); i++) {
			Boolean result = processRule(compositeRuleList.get(i));
			if(result == null) continue;
			noRulesProcessed = false;
			if(result.booleanValue()) {
				addRuntimePass(ruleNumber);
				addCompositeRulePassResultsToVariables(ruleNumber, variables);
				LOGGER.debug("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, true);
				return (true);
			}
		}

		if(noRulesProcessed) return null;
		
		addRuntimeFail(ruleNumber);
		addCompositeRuleFailResultsToVariables(ruleNumber, variables);
		LOGGER.debug("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, false);

		return false;

	}
	
	/**
	 * The engine processes all rules listed in the AndRule bean compositeRules field list up to the first fail.  Only those rules evaluated at runtime
	 * set bean field values. 
	 * @return results from processing an And rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	private Boolean processAndRules(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();

		boolean noRulesProcessed = true;
		
		ArrayList<Integer> compositeRuleList = getCompositeRulesList(ruleNumber);
		if(compositeRuleList.size() == 0) return null;
		for (int i = 0; i < compositeRuleList.size(); i++) {
			Boolean result = processRule(compositeRuleList.get(i));
			if(result == null) continue;
			noRulesProcessed = false;
			if(!result.booleanValue()) {
				addRuntimeFail(ruleNumber);
				addCompositeRuleFailResultsToVariables(ruleNumber, variables);
				LOGGER.debug("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, false);
				return false;
			}			
			
		}
		
		if(noRulesProcessed) return null;
		
		addRuntimePass(ruleNumber);
		addCompositeRulePassResultsToVariables(ruleNumber, variables);
		LOGGER.debug("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t), ruleNumber, true);
		
		return true;

	}

	private Boolean processRule(Integer ruleNumber) throws Exception {

		int abs = Math.abs(ruleNumber); 
		Boolean result = callRule(abs);
		if(result == null) return null;
		if( ruleNumber.intValue() < 0 ) { //negative number means a "not" rule
			return (!result);
		}
		return (result);

	}
	
	private ArrayList<Integer> getCompositeRulesList(Integer ruleNumber) throws Exception {
		return ruleDefinition.getCompositeRulesList(ruleNumber);
	}

	private ArrayList<Integer> getThreadRulesList(Integer ruleNumber) throws Exception {
		return ruleDefinition.getThreadRulesList(ruleNumber);
	}

	private Double evaluateExpression(String expression) {
		return ExpressionHandler.getProductOf(expression, variables);
	}
	
	public  Object clone() {
		
		RuleEvaluator newRuleEvaluator = new RuleEvaluator(this.ruleDefinition);
		newRuleEvaluator.setVariables(this.getVariables());
		return newRuleEvaluator;
		
	}
	
	
}
