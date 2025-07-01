/*
dThe MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine;

import static com.synditcorp.ruleengine.logging.RuleLogger.LOGGER;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;

import com.synditcorp.ruleengine.beans.BaseOutcome;
import com.synditcorp.ruleengine.beans.FailNumberOutcome;
import com.synditcorp.ruleengine.beans.FailTagOutcome;
import com.synditcorp.ruleengine.beans.PassNumberOutcome;
import com.synditcorp.ruleengine.beans.PassTagOutcome;
import com.synditcorp.ruleengine.beans.ThreadProcObjects;
import com.synditcorp.ruleengine.beans.ThreadResults;
import com.synditcorp.ruleengine.beans.ThreadRule;
import com.synditcorp.ruleengine.exceptions.DuplicateKeyException;
import com.synditcorp.ruleengine.exceptions.EngineSafeguardException;
import com.synditcorp.ruleengine.exceptions.NoRuleEvaluatedException;
import com.synditcorp.ruleengine.handlers.ExpressionHandler;
import com.synditcorp.ruleengine.interfaces.CalcRule;
import com.synditcorp.ruleengine.interfaces.CompositeRule;
import com.synditcorp.ruleengine.interfaces.ExecRule;
import com.synditcorp.ruleengine.interfaces.Outcome;
import com.synditcorp.ruleengine.interfaces.Rule;
import com.synditcorp.ruleengine.interfaces.RuleDefinition;
import com.synditcorp.ruleengine.logging.TimeTrack;
import com.synditcorp.ruleengine.processors.CalcRuleProcessor;
import com.synditcorp.ruleengine.processors.ExecRuleProcessor;
import com.synditcorp.ruleengine.processors.ThreadRuleProcessor;

/**
 * This class provides the runtime methods for the rule engine. Injected is a
 * RuleDefinitions object that implements the RuleDefinitions interface. This
 * class is the primary class used to interact with the rule engine.
 * RuleDefinitions, the RuleParser, or other rule engine classes need not be
 * accessed directly.
 */
public class RuleEvaluator implements Cloneable {

	private RuleDefinition ruleDefinition;
	protected TreeMap<Integer, Boolean> cache = new TreeMap<Integer, Boolean>();
	protected TreeMap<String, Object> variables = new TreeMap<String, Object>();
//	private ArrayList<Integer> runtimePasses = new ArrayList<Integer>(1000);
//	private ArrayList<Integer> runtimeFails = new ArrayList<Integer>(1000);
	private ArrayList<Integer> runtineExpressionFails = new ArrayList<Integer>(1000);
	private int threadBlockSize = 100;
	private ForkJoinPool pool = null;
	//private Date todayDate = new Date();

	public RuleEvaluator(RuleDefinition rulesDefinition) {
		this.ruleDefinition = rulesDefinition;
	}

	/**
	 * Returns the ID of the rule definition
	 * 
	 * @return document ID
	 */
	public String getDocumentId() {
		return ruleDefinition.getDocumentId();
	}

	/**
	 * Returns the description of the rule definition
	 * 
	 * @return document description
	 */
	public String getDescription() {
		return ruleDefinition.getDescription();
	}

	/**
	 * Returns the version of the rule definition
	 * 
	 * @return document version
	 */
	public String getVersion() {
		return ruleDefinition.getVersion();
	}

	/**
	 * Optional document tags are used to further define a document. Document tags
	 * are not used at runtime to evaluate rules. Tags can be used for things like
	 * authorization in databases or display control in custom rule definition
	 * editors
	 * 
	 * @return list of document tags
	 */
	public ArrayList<String> getDocumentTags() {
		return ruleDefinition.getDocumentTags();
	}

	/**
	 * This is intended to hold the rule number at the base of a decision tree so
	 * calling programs can refer to this value at runtime rather than having to
	 * rely on outside process and documentation to communicate the starting rule of
	 * a decision tree. This is optional: any rule can be called directly. This is
	 * not specifically used when evaluating rules at runtime.
	 * 
	 * @return document's start rule number
	 */
	public Integer getStartRule() {
		return ruleDefinition.getStartRule();
	}

	/**
	 * Optional rule tags are used to further define a rule, but are not
	 * specifically used when evaluating rules at runtime. Tags can be used for
	 * things like authorization in databases or display control in custom rule
	 * definition editors
	 * 
	 * @return list of a rule's tags
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public ArrayList<String> getRuleTags(Integer ruleNumber) throws Exception {
		return ruleDefinition.getRuleTags(ruleNumber);
	}
	
	/**
	 * Evaluate the rule referenced by rule number. The rule number must be one of
	 * the rules referenced in the document parsed by the engine's parser.
	 * 
	 * @return a rule's evaluation results
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	public boolean evaluateRule(Integer ruleNumber) throws Exception {

		LOGGER.info("Syndit Rule Engine evaluating rule number {} using document ID {}, version {}", ruleNumber,
				this.getDocumentId(), this.getVersion());

		Boolean result = callRule(ruleNumber);

		if (result == null)
			throw new NoRuleEvaluatedException();

		LOGGER.info("Syndit Rule Engine completed evaluation of rule number {} with result equal to {}", ruleNumber,
				result);

		return (result.booleanValue());

	}

	/**
	 * This clears three collections. First, the rule "passes" (those that evaluate
	 * to true) are cleared. Next, the rule "fails" (those that evaluate to false)
	 * are cleared. Third, variables passed into the engine, as well as those
	 * accumulated by the engine (e.g. scores, actions, etc.) at runtime, are
	 * cleared. WARNING: variables must be set using setVariables() before the
	 * engine can be run again if the expressions to be evaluated need the
	 * variables.
	 */
	public void reset() {
		clearCache();
//		clearRuntimePasses();
//		clearRuntimeFails();
		clearVariables();
	}

	/**
	 * Set the block size to use when processing ThreadRules. The default is 100.
	 * 
	 * @param threadBlockSize for processing ThreadRules
	 */
	public void setThreadBlockSize(int threadBlockSize) {
		this.threadBlockSize = threadBlockSize;
	}

	/**
	 * Set the variables the rules engine will use in expressions, or passed to
	 * custom rule handlers. To protect the integrity of the engine, the passed map
	 * collection entries are added to the internal map collection.  Also to protect the integrity of
	 * the Engine, if variables exist, a VariablesExistException is thrown.
	 * 
	 * @throws VariablesExistException when variables exist in the internal map collection
	 * @param variables for the Engine's expressions
	 */
	public void setVariables(TreeMap<String, Object> variables) throws EngineSafeguardException {
		
		if(!this.variables.isEmpty()) throw new EngineSafeguardException("Variables collection already populated.  Use 'reset' to clear variables.");

		this.variables.putAll(variables);

	}

	/**
	 * Get the variables used by the rules engine. This includes global runtime
	 * variables generated at runtime. To protect the integrity of the engine's
	 * results, a copy of the variables is returned rather than the object instance
	 * used by the engine.
	 * 
	 * @return the TreeMap object containing the variables passed to and generated
	 *         by the Engine
	 */
	public TreeMap<String, Object> getVariables() {

		TreeMap<String, Object> copyOf = new TreeMap<String, Object>();
		copyOf.putAll(this.variables);

		return copyOf;
	}

//	/**
//	 * Get the runtime rule processing results cache.
//	 * @return the TreeMap object containing the rule number and Boolean processing results
//	 */
//	public TreeMap<Integer, Boolean> getCache() {
//		return this.cache;
//	}

//	/**
//	 * Get the list of rules that passed at runtime.
//	 * 
//	 * @return an ArrayList of rule numbers that passed
//	 */
//	public ArrayList<Integer> getRuntimePasses() {
//		ArrayList<Integer> copyOf = new ArrayList<Integer>();
//		copyOf.addAll(this.runtimePasses);
//		return copyOf;
//	}

//	/**
//	 * Get the list of rules that failed at runtime.
//	 * 
//	 * @return an ArrayList of rule numbers that failed
//	 */
//	public ArrayList<Integer> getRuntimeFails() {
//		ArrayList<Integer> copyOf = new ArrayList<Integer>();
//		copyOf.addAll(this.runtimeFails);
//		return copyOf;
//	}

	protected void setCache(TreeMap<Integer, Boolean> cache) throws EngineSafeguardException {
		if(!this.cache.isEmpty()) throw new EngineSafeguardException("Cache contains evaulation results.  Use 'reset' to clear results.");
		this.cache.putAll(cache);
	}
	
	/**
	 * Get the list of rules whose expressions failed at runtime.
	 * 
	 * @return an ArrayList of rule number expression that failed
	 */
	public ArrayList<Integer> getRuntimeExpressionFails() {
		ArrayList<Integer> copyOf = new ArrayList<Integer>();
		copyOf.addAll(this.runtineExpressionFails);
		return copyOf;
	}

//	/**
//	 * Get the pass number outcome for a particular rule's outcome key.  This returns the number number results of an expression.  If composite
//	 * rules are specified, the number returned is the sum of each composite rule key's expressions.  
//	 * Values are only returned if the rule evaluated to "true" at runtime.
//	 * @return a rule's pass number outcome for the given key.
//	 * @throws Exception when any exception occurs.
//	 * @param ruleNumber value for a given rule number and the rule's outcome key.
//	 */
//	public Double getPassNumberOutcome(Integer ruleNumber, String key) throws Exception {
//
//		if(!runtimePasses.contains(ruleNumber)) throw new NoRuleEvaluatedException("rule number " + ruleNumber);
//
//		BaseOutcome outcome = (BaseOutcome) getRule(ruleNumber).getPassNumberOutcome(key);
//		if(outcome == null) return null;
//		return getNumberOutcome(outcome);
//		
//	}
//	
//	/**
//	 * Get the fail number outcome for a particular rule's outcome key.  This returns the number results of an expression.  If composite
//	 * rules are specified, the number returned is the sum of each composite rule key's expressions.  
//	 * Values are only returned if the rule evaluated to "false" at runtime.
//	 * @return a rule's fail number outcome for the given key.
//	 * @throws Exception when any exception occurs.
//	 * @param ruleNumber value for a given rule number and the rule's outcome key.
//	 */
//	public Double getFailNumberOutcome(Integer ruleNumber, String key) throws Exception {
//
//		if(!runtimeFails.contains(ruleNumber)) throw new NoRuleEvaluatedException("rule number " + ruleNumber);
//
//		BaseOutcome outcome = (BaseOutcome) getRule(ruleNumber).getFailNumberOutcome(key);
//		if(outcome == null) return null;
//		return getNumberOutcome(outcome);
//
//	}
//	
//	/**
//	 * Gets a String array of the pass tag outcomes for a particular rule's outcome key.  This returns the string expression set in the rules document.  If composite
//	 * rules are specified, the array contains all the string expressions for each composite rule key's expression.  Values are only included in the array if the rule evaluated to "true" at runtime.
//	 * @return a rule's string array of the tag outcomes for the given key.
//	 * @throws Exception when any exception occurs.
//	 * @param ruleNumber value for a given rule number and the rule's outcome key.
//	 */
//	public ArrayList<String> getPassTagOutcome(Integer ruleNumber, String key) throws Exception {
//
//		if(!runtimePasses.contains(ruleNumber)) throw new NoRuleEvaluatedException("rule number " + ruleNumber);
//
//		BaseOutcome outcome = (BaseOutcome) getRule(ruleNumber).getPassTagOutcome(key);
//		if(outcome == null) return null;
//		return getTagOutcome(outcome);
//		
//	}
//	
//	/**
//	 * Gets a String array of the fail tag outcomes for a particular rule's outcome key.  This returns the string expression set in the rules document.  If composite
//	 * rules are specified, the array contains all the string expressions for each composite rule key's expression.  Values are only included in the array if the rule evaluated to "false" at runtime.
//	 * @return a rule's string array of the tag outcomes for the given key.
//	 * @throws Exception when any exception occurs.
//	 * @param ruleNumber value for a given rule number and the rule's outcome key.
//	 */
//	public ArrayList<String> getFailTagOutcome(Integer ruleNumber, String key) throws Exception {
//
//		if(!runtimeFails.contains(ruleNumber)) throw new NoRuleEvaluatedException("rule number " + ruleNumber);
//
//		BaseOutcome outcome = (BaseOutcome) getRule(ruleNumber).getFailTagOutcome(key);
//		if(outcome == null) return null;
//		return getTagOutcome(outcome);
//		
//	}
//	

	/**
	 * Get the pass number outcome for a particular rule's outcome key. This returns
	 * the number number results of an expression. If composite rules are specified,
	 * the number returned is the sum of each composite rule key's expressions.
	 * Values are only returned if the rule was evaluated at runtime and passed of
	 * failed.
	 * 
	 * @return a rule's number outcome for the given key.
	 * @throws Exception when any exception occurs.
	 * @param ruleNumber value for a given rule number and the rule's outcome key.
	 */
	public Double getNumberOutcome(Integer ruleNumber, String key) throws Exception {

		BaseOutcome outcome;

		if (isRuntimePass(ruleNumber)) {
			outcome = (BaseOutcome) getRule(ruleNumber).getPassNumberOutcome(key);
		} else if (isRuntimeFail(ruleNumber)) {
			outcome = (BaseOutcome) getRule(ruleNumber).getFailNumberOutcome(key);
		} else {
			throw new NoRuleEvaluatedException("rule number " + ruleNumber);
		}

		if (outcome == null)
			return null;
		return getNumberOutcome(outcome);

	}

	/**
	 * Gets a String array of the tag outcomes for a particular rule's outcome key.
	 * This returns the string tag expression set in the rules document. If
	 * composite rules are specified, the array contains all the string expressions
	 * for each composite rule key's expression. Values are only included in the
	 * array if the rule was evaluated at runtime and passed or failed.
	 * 
	 * @return a rule's string array of the tag outcomes for the given key.
	 * @throws Exception when any exception occurs.
	 * @param ruleNumber value for a given rule number and the rule's outcome key.
	 */
	public ArrayList<String> getTagOutcome(Integer ruleNumber, String key) throws Exception {

		BaseOutcome outcome;

		if (isRuntimePass(ruleNumber)) {
			outcome = (BaseOutcome) getRule(ruleNumber).getPassTagOutcome(key);
		} else if (isRuntimeFail(ruleNumber)) {
			outcome = (BaseOutcome) getRule(ruleNumber).getFailTagOutcome(key);
		} else {
			throw new NoRuleEvaluatedException("rule number " + ruleNumber);
		}

		if (outcome == null)
			return null;
		return getTagOutcome(outcome);

	}

	/**
	 * Used to prevent existing variables from being overwritten at runtime.
	 * 
	 * @param variableName to be added to the collection and the variable's object
	 */
	private void putToVariables(String variableName, Object value) throws DuplicateKeyException {
		
		if (this.variables.containsKey(variableName)) throw new DuplicateKeyException(variableName);

		this.variables.put(variableName, value);

	}
	
	private boolean isRuntimePass(Integer ruleNumber) {
		
		if( cache.containsKey(ruleNumber) && cache.get(ruleNumber) == true ) {
			return true;
		}
		
		return false;

	}
	
	private boolean isRuntimeFail(Integer ruleNumber) {

		if( cache.containsKey(ruleNumber) && cache.get(ruleNumber) == false ) {
			return true;
		}
		
		return false;
		
	}

	private void addRuntimePass(Integer ruleNumber) {
		
		if(cache.containsKey(ruleNumber)) return;
		
		cache.put(ruleNumber, true);

	}

	private void addRuntimeFail(Integer ruleNumber) {
		
		if(cache.containsKey(ruleNumber)) return;
		
		cache.put(ruleNumber, false);
		
	}

	private void setGlobalPassOutcomes(Integer ruleNumber) throws Exception {

		setGlobalPassNumberOutcomesToVars(ruleNumber);
		setGlobalPassTagOutcomesToVars(ruleNumber);

	}

	private void setGlobalFailOutcomes(Integer ruleNumber) throws Exception {

		setGlobalFailNumberOutcomesToVars(ruleNumber);
		setGlobalFailTagOutcomesToVars(ruleNumber);

	}

	private void setGlobalPassNumberOutcomesToVars(Integer ruleNumber) throws Exception {

		Rule rule = getRule(ruleNumber);
		if (!isRuntimePass(ruleNumber))
			return;
		ArrayList<BaseOutcome> outcomes = rule.getPassNumberOutcomes();
		if (outcomes == null) return;

		setGlobalNumberOutcomes(outcomes);

	}

	private void setGlobalFailNumberOutcomesToVars(Integer ruleNumber) throws Exception {

		Rule rule = getRule(ruleNumber);
		if (!isRuntimeFail(ruleNumber)) 
			return;
		ArrayList<BaseOutcome> outcomes = rule.getFailNumberOutcomes();
		if (outcomes == null) return;

		setGlobalNumberOutcomes(outcomes);

	}

	private void setGlobalPassTagOutcomesToVars(Integer ruleNumber) throws Exception {

		Rule rule = getRule(ruleNumber);
		if (!isRuntimePass(ruleNumber))
			return;
		ArrayList<BaseOutcome> outcomes = rule.getPassTagOutcomes();
		if (outcomes == null) return;

		setGlobalTagOutcomes(outcomes);

	}

	private void setGlobalFailTagOutcomesToVars(Integer ruleNumber) throws Exception {

		Rule rule = getRule(ruleNumber);
		if (!isRuntimeFail(ruleNumber))
			return;
		ArrayList<BaseOutcome> outcomes = rule.getFailTagOutcomes();
		if (outcomes == null) return;

		setGlobalTagOutcomes(outcomes);

	}

	private void setGlobalNumberOutcomes(ArrayList<BaseOutcome> outcomes) throws Exception {

		Iterator<BaseOutcome> iterator = outcomes.iterator();
		while (iterator.hasNext()) {

			BaseOutcome outcome = (BaseOutcome) iterator.next();
			if (outcome.getGlobal() == null || !outcome.getGlobal())
				continue;
			Double dbl = getNumberOutcome(outcome);

			if (dbl == null) continue;

			putToVariables(outcome.getVariableName(), dbl);

		}

	}

	private void setGlobalTagOutcomes(ArrayList<BaseOutcome> outcomes) throws Exception {

		Iterator<BaseOutcome> iterator = outcomes.iterator();
		while (iterator.hasNext()) {

			BaseOutcome outcome = (BaseOutcome) iterator.next();
			if (outcome.getGlobal() == null || !outcome.getGlobal())
				continue;
			ArrayList<String> list = getTagOutcome(outcome);
			if (list == null) continue;

			putToVariables(outcome.getVariableName(), list);

		}

	}

	private Double getNumberOutcome(Outcome outcome) throws Exception {
		
		if( this.variables.containsKey( outcome.getVariableName() )) return (Double) this.variables.get(outcome.getVariableName());
		
		Double outcomeNumber = calcNumberOutcome(outcome);
		
		ArrayList<Integer> compositeRulesList = outcome.getCompositeOutcomeRules();
		if(compositeRulesList == null) return outcomeNumber;
		
		Iterator<Integer> iterator = compositeRulesList.iterator();
		while(iterator.hasNext()) {
			
			BaseOutcome nextOutcome = null;
			
			Integer ruleNumber = iterator.next();
			
			int abs = Math.abs(ruleNumber);
			
			boolean pass = outcome instanceof PassNumberOutcome;
			boolean fail = outcome instanceof FailNumberOutcome;
			
			//if ruleNumber is negative, it's a "not" rule, so take the opposite
			if( ruleNumber.intValue() < 0 ) {
				pass = !pass;
				fail = !fail;
			}
			
			if(pass) {
				if(!isRuntimePass(abs)) continue;
				nextOutcome = (BaseOutcome) getRule(abs).getPassNumberOutcome(outcome.getKey());	
			} else if(fail) {
				if(!isRuntimeFail(abs)) continue;
				nextOutcome = (BaseOutcome) getRule(abs).getFailNumberOutcome(outcome.getKey());	
			}
			
			if(nextOutcome == null) continue;
			
			if(outcomeNumber == null) outcomeNumber = getNumberOutcome(nextOutcome);
			else outcomeNumber = Double.sum(outcomeNumber, getNumberOutcome(nextOutcome));

		}
		
		return outcomeNumber;
		
	}

	private ArrayList<String> getTagOutcome(Outcome outcome) throws Exception {

		if( this.variables.containsKey( outcome.getVariableName() )) return (ArrayList<String>) this.variables.get(outcome.getVariableName());

		ArrayList<String> outcomeTags = new ArrayList<String>();
		outcomeTags.add(calcTagOutcome(outcome));

		ArrayList<Integer> compositeRulesList = outcome.getCompositeOutcomeRules();
		if (compositeRulesList == null)
			return outcomeTags;

		Iterator<Integer> iterator = compositeRulesList.iterator();
		while (iterator.hasNext()) {

			BaseOutcome nextOutcome = null;

			Integer ruleNumber = iterator.next();

			int abs = Math.abs(ruleNumber);

			boolean pass = outcome instanceof PassTagOutcome;
			boolean fail = outcome instanceof FailTagOutcome;

			// if ruleNumber is negative, it's a "not" rule, so take the opposite
			if (ruleNumber.intValue() < 0) {
				pass = !pass;
				fail = !fail;
			}
			if (pass) {
				if (!isRuntimePass(abs))
					continue;
				nextOutcome = (BaseOutcome) getRule(abs).getPassTagOutcome(outcome.getKey());
			} else if (fail) {
				if (!isRuntimeFail(abs))
					continue;
				nextOutcome = (BaseOutcome) getRule(abs).getFailTagOutcome(outcome.getKey());
			}

			if (nextOutcome == null)
				continue;

			outcomeTags.addAll(getTagOutcome(nextOutcome));

		}

		return outcomeTags;

	}

	private Double calcNumberOutcome(Outcome outcome) {
		if (outcome.getExpression() == null)
			return null;
		return evaluateNumberExpression(outcome.getExpression());
	}

	private String calcTagOutcome(Outcome outcome) {
		if (outcome.getExpression() == null)
			return null;
		return outcome.getExpression();
	}

	private Boolean callRule(Integer ruleNumber) throws Exception {

		ruleDefinition.isRule(ruleNumber);
		
		if(cache.containsKey(ruleNumber)) return cache.get(ruleNumber);

		if (!isRuleApplicable(ruleNumber))
			return null;

		if (ruleDefinition.isCalcRule(ruleNumber))
			return (processCalcRule(ruleNumber));

		if (ruleDefinition.isExecRule(ruleNumber))
			return (processExecRule(ruleNumber));

		if (ruleDefinition.isOrRule(ruleNumber))
			return (processOrRules(ruleNumber));

		if (ruleDefinition.isAndRule(ruleNumber))
			return (processAndRules(ruleNumber));

		if (ruleDefinition.isAllRule(ruleNumber))
			return (processAllRules(ruleNumber));

		if (ruleDefinition.isThreadRule(ruleNumber))
			return (processThreadRules(ruleNumber));

		throw new Exception("Rule number " + ruleNumber + " not found in rule definitions.");

	}

	private Boolean isRuleApplicable(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();
		
		Date nowDate = new Date();

		Date effectiveDate = getRule(ruleNumber).getEffectiveDate();
		Date expirationDate = getRule(ruleNumber).getExpirationDate();
		Boolean active = getRule(ruleNumber).getActive();

		Boolean isApplicable = (active == null || active == true)
				&& (nowDate == null || effectiveDate == null || nowDate.after(effectiveDate)
						|| nowDate.equals(effectiveDate))
				&& (expirationDate == null || expirationDate == null || nowDate.before(expirationDate));

		long l = TimeTrack.getElapsedTime(t);

		LOGGER.debug("Time check if rule number " + ruleNumber + " is applicable: " + isApplicable);

		return isApplicable;

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

	private void addExpressionFail(Integer ruleNumber) {
		if (!runtineExpressionFails.contains(ruleNumber))
			runtineExpressionFails.add(ruleNumber);
	}

//	private void clearRuntimePasses() {
//		runtimePasses.clear();
//	}
//
//	private void clearRuntimeFails() {
//		runtimeFails.clear();
//	}

	private Boolean processCalcRule(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();

//		Boolean cachedResult = cache.get(ruleNumber);
//
//		if (cachedResult != null) {
//			return cachedResult;
//		}

		// if(!isRuleApplicable(ruleNumber)) return null;
		
		String ruleHandler = ((CalcRule)getRule(ruleNumber)).getHandlerClass();
		String expression = ((CalcRule)getRule(ruleNumber)).getExpression();

		//String ruleHandler = ruleDefinition.getHandlerClass(ruleNumber);
		//String expression = ruleDefinition.getExpression(ruleNumber);

		Boolean result = null;
		try {
			result = CalcRuleProcessor.processCalcRule(ruleHandler, expression, variables);
		} catch (NoRuleEvaluatedException e) {
			LOGGER.info("Unable to process rule expression: \"" + expression + "\", reason: " + e);
			throw e;
		} catch (Exception e) {
			LOGGER.info("Unable to process rule expression: \"" + expression + "\", reason: " + e);
			addExpressionFail(ruleNumber);
			result = false;
		}

		addToCache(ruleNumber, result);

		if (result.booleanValue()) {
			addRuntimePass(ruleNumber);
			setGlobalPassOutcomes(ruleNumber);
			// addRulePassResultsToVariables(ruleNumber, variables);
		} else {
			addRuntimeFail(ruleNumber);
			setGlobalFailOutcomes(ruleNumber);
			// addRuleFailResultsToVariables(ruleNumber, variables);
		}

		LOGGER.debug("{} milleseconds to evaluate rule number {} expression: {}, which evaluates to {}",
				TimeTrack.getElapsedTime(t), ruleNumber, expression, result);

		return (result);

	}

	private Boolean processExecRule(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();

//		Boolean cachedResult = cache.get(ruleNumber);
//
//		if (cachedResult != null) {
//			return cachedResult;
//		}

		// if(!isRuleApplicable(ruleNumber)) return null;
		
		String ruleHandler = ((ExecRule)getRule(ruleNumber)).getHandlerClass();

		//String ruleHandler = ruleDefinition.getHandlerClass(ruleNumber);
		//String expression = ruleDefinition.getExpression(ruleNumber);

		Boolean result = null;
		try {
			result = ExecRuleProcessor.processExecRule(ruleHandler, variables);
		} catch (NoRuleEvaluatedException e) {
			LOGGER.info("Unable to process rule: \"" + ruleNumber + "\", reason: " + e);
			throw e;
		} catch (Exception e) {
			LOGGER.info("Unable to process rule: \"" + ruleNumber + "\", reason: " + e);
			addExpressionFail(ruleNumber);
			result = false;
		}

		addToCache(ruleNumber, result);

		if (result.booleanValue()) {
			addRuntimePass(ruleNumber);
			setGlobalPassOutcomes(ruleNumber);
			// addRulePassResultsToVariables(ruleNumber, variables);
		} else {
			addRuntimeFail(ruleNumber);
			setGlobalFailOutcomes(ruleNumber);
			// addRuleFailResultsToVariables(ruleNumber, variables);
		}

		LOGGER.debug("{} milleseconds to evaluate rule number {} expression: {}, which evaluates to {}",
				TimeTrack.getElapsedTime(t), ruleNumber, result);

		return (result);

	}

	/**
	 * The engine processes all rules listed in the AllRule bean compositeRules
	 * field list. AllRule rule types should be used when all rules must be
	 * processed for such things as setting bean field values that will be accessed
	 * by other rules.
	 * 
	 * @return result from processing an All rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	private Boolean processAllRules(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();

		boolean noRulesProcessed = true;

		ArrayList<Integer> compositeRuleList = getCompositeRulesList(ruleNumber);
		if (compositeRuleList.size() == 0)
			return null;
		for (int i = 0; i < compositeRuleList.size(); i++) {
			Boolean result = processRule(compositeRuleList.get(i));
			if (result == null)
				continue;
			noRulesProcessed = false;
			if (result.booleanValue()) {
				addRuntimePass(ruleNumber);
				setGlobalPassOutcomes(ruleNumber);
				// addCompositeRulePassResultsToVariables(ruleNumber, variables);
				LOGGER.debug("{} milleseconds to evaluate rule number {}, which evaluates to {}",
						TimeTrack.getElapsedTime(t), ruleNumber, true);
			} else {
				addRuntimeFail(ruleNumber);
				setGlobalFailOutcomes(ruleNumber);
				// addCompositeRuleFailResultsToVariables(ruleNumber, variables);
				LOGGER.debug("{} milleseconds to evaluate rule number {}, which evaluates to {}",
						TimeTrack.getElapsedTime(t), ruleNumber, false);
			}

		}

		if (noRulesProcessed) return null;

		// setGlobalPassOutcomes(ruleNumber);

		return true;

	}

	/**
	 * The Engine processes thread rules listed in the ThreadRule bean
	 * compositeRules field list. Thread rule types can be used when rules in the
	 * compositeRules list can be processed independently of other rules in the
	 * definition document, where processing order does not matter, and when
	 * performance is an issue. The rules are processed in blocks. Set the block
	 * size by calling setThreadBlockSize() and pass a block size value that
	 * performs optimally in your environment. Variables set in the threads do not
	 * persist when evaluation is complete.
	 * 
	 * @return returns true
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	private Boolean processThreadRules(Integer ruleNumber) throws Exception {

		ArrayList<ThreadResults> threadResults = getThreadResults(ruleNumber);
		String variableName = ruleDefinition.getDocumentId() + "_" + ruleNumber + "_";

		// loop through all the rule results processed in the threads
		for (int i = 0; i < threadResults.size(); i++) {

			if(threadResults.get(i).getNumberOutcomes() != null) {
				TreeMap<String, Double> numberOutcomes = threadResults.get(i).getNumberOutcomes();
				for(Map.Entry<String, Double> entry:numberOutcomes.entrySet()) {
					
					String threadVariable = variableName + entry.getKey();
					if(this.variables.containsKey(threadVariable)) {
						
						Double sumOf = Double.sum( (Double) this.variables.get(threadVariable), entry.getValue());
						this.variables.put(threadVariable, sumOf);
						
					} else {
						this.variables.put(threadVariable, entry.getValue());
					}

				}

			}
			
			if(threadResults.get(i).getTagOutcomes() != null) {
				TreeMap<String, ArrayList<String>> tagOutcomes = threadResults.get(i).getTagOutcomes();
				for(Map.Entry<String, ArrayList<String>> entry:tagOutcomes.entrySet()) {
					
					String threadVariable = variableName + entry.getKey();
					if(this.variables.containsKey(threadVariable)) {
						((ArrayList<String>) this.variables.get(threadVariable)).addAll(entry.getValue());
					} else {
						this.variables.put(threadVariable, entry.getValue());
					}

				}

			}
			
		}

		addRuntimePass(ruleNumber);
		return true;

	}

	/**
	 * To improve performance, as the name implies ThreadRules are processed using
	 * threads.
	 * 
	 * @return results in a ThreadResults object
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	private ArrayList<ThreadResults> getThreadResults(Integer ruleNumber) throws Exception {

		if (pool == null) pool = new ForkJoinPool();
		
		ThreadRule threadRule =  ((ThreadRule) getRule(ruleNumber));

		ArrayList<String> numberKeys = threadRule.getNumberKeys();
		ArrayList<String> tagKeys = threadRule.getTagKeys();
		ArrayList<Integer> threadRulesList = threadRule.getThreadRules();
		ArrayList<Integer> block = new ArrayList<Integer>();
		
		ArrayList<ThreadRuleProcessor> tasks = new ArrayList<ThreadRuleProcessor>();
		int listSize = threadRulesList.size();
		int ctr = 0;
		for (int i = 0; i < listSize; i++) {
			block.add(threadRulesList.get(i));
			ctr++;
			if (ctr == this.threadBlockSize || (i + 1) == listSize) {
				ArrayList<Integer> passBlock = new ArrayList<Integer>();
				passBlock.addAll(block);

				ThreadProcObjects objects = new ThreadProcObjects();
				objects.setThreadRuleNumber(ruleNumber);
				objects.setNumberKeys(numberKeys);
				objects.setTagKeys(tagKeys);
				objects.setBlock(passBlock);
				objects.setRuleEvaluator((RuleEvaluator) this.clone());
				
				ThreadRuleProcessor threadRuleProcessor = new ThreadRuleProcessor(objects);
				tasks.add(threadRuleProcessor);
				pool.execute(threadRuleProcessor);
				block.clear();
				ctr = 0;
			}
		}

		ArrayList<ThreadResults> threadResults = new ArrayList<ThreadResults>();

		for (int i = 0; i < tasks.size(); i++) {
			threadResults.add(tasks.get(i).get());
		}

		return threadResults;

	}

	/**
	 * The engine process all rules listed in the OrRule bean compositeRules field
	 * list up to the first pass. Only those rules evaluated at runtime set bean
	 * field values.
	 * 
	 * @return results from processing an Or rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	private Boolean processOrRules(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();

		boolean noRulesProcessed = true;

		ArrayList<Integer> compositeRuleList = getCompositeRulesList(ruleNumber);
		if (compositeRuleList.size() == 0)
			return null;
		for (int i = 0; i < compositeRuleList.size(); i++) {
			Boolean result = processRule(compositeRuleList.get(i));
			if (result == null)
				continue;
			noRulesProcessed = false;
			if (result.booleanValue()) {
				addRuntimePass(ruleNumber);
				setGlobalPassOutcomes(ruleNumber);
				// addCompositeRulePassResultsToVariables(ruleNumber, variables);
				LOGGER.debug("{} milleseconds to evaluate rule number {}, which evaluates to {}",
						TimeTrack.getElapsedTime(t), ruleNumber, true);
				return (true);
			}
		}

		if (noRulesProcessed)
			return null;

		addRuntimeFail(ruleNumber);
		setGlobalFailOutcomes(ruleNumber);
		// addCompositeRuleFailResultsToVariables(ruleNumber, variables);
		LOGGER.debug("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t),
				ruleNumber, false);

		return false;

	}

	/**
	 * The engine processes all rules listed in the AndRule bean compositeRules
	 * field list up to the first fail. Only those rules evaluated at runtime set
	 * bean field values.
	 * 
	 * @return results from processing an And rule
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	private Boolean processAndRules(Integer ruleNumber) throws Exception {

		TimeTrack t = new TimeTrack();

		boolean noRulesProcessed = true;

		ArrayList<Integer> compositeRuleList = getCompositeRulesList(ruleNumber);
		if (compositeRuleList.size() == 0)
			return null;
		for (int i = 0; i < compositeRuleList.size(); i++) {
			Boolean result = processRule(compositeRuleList.get(i));
			if (result == null)
				continue;
			noRulesProcessed = false;
			if (!result.booleanValue()) {
				addRuntimeFail(ruleNumber);
				setGlobalFailOutcomes(ruleNumber);
				// addCompositeRuleFailResultsToVariables(ruleNumber, variables);
				LOGGER.debug("{} milleseconds to evaluate rule number {}, which evaluates to {}",
						TimeTrack.getElapsedTime(t), ruleNumber, false);
				return false;
			}

		}

		if (noRulesProcessed)
			return null;

		addRuntimePass(ruleNumber);
		setGlobalPassOutcomes(ruleNumber);
		// addCompositeRulePassResultsToVariables(ruleNumber, variables);
		LOGGER.debug("{} milleseconds to evaluate rule number {}, which evaluates to {}", TimeTrack.getElapsedTime(t),
				ruleNumber, true);

		return true;

	}

	private Boolean processRule(Integer ruleNumber) throws Exception {

		int abs = Math.abs(ruleNumber);
		Boolean result = callRule(abs);
		if (result == null)
			return null;
		if (ruleNumber.intValue() < 0) { // negative number means a "not" rule
			return (!result);
		}
		return (result);

	}

	private ArrayList<Integer> getCompositeRulesList(Integer ruleNumber) throws Exception {
		//return ruleDefinition.getCompositeRulesList(ruleNumber);
		return ((CompositeRule) getRule(ruleNumber)).getCompositeRules();
	}

//	private ArrayList<Integer> getThreadRulesList(Integer ruleNumber) throws Exception {
//		//return ruleDefinition.getThreadRulesList(ruleNumber);
//		return ((ThreadRule) getRule(ruleNumber)).getThreadRules();
//	}

	private Double evaluateNumberExpression(String expression) {
		return ExpressionHandler.getProductOf(expression, variables);
	}

	private String evaluateStringExpression(String expression) {
		return ExpressionHandler.evaluateStringExpression(expression, variables);
	}

	public Object clone() {

		RuleEvaluator newRuleEvaluator = new RuleEvaluator(this.ruleDefinition);
		
		for (int i = 0; i < 2; i++) {
			try {
				newRuleEvaluator.setVariables(this.variables);
				newRuleEvaluator.setCache(this.cache);
			} catch (EngineSafeguardException e) {
				newRuleEvaluator.clearCache();
				continue;
			}
			break;
		}

		return newRuleEvaluator;

	}

}
