/*
dThe MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mvel2.PropertyAccessException;

import com.synditcorp.ruleengine.exceptions.DuplicateKeyException;
import com.synditcorp.ruleengine.exceptions.EngineSafeguardException;
import com.synditcorp.ruleengine.exceptions.NoRuleEvaluatedException;
import com.synditcorp.ruleengine.exceptions.RuleEvaluationException;
import com.synditcorp.ruleengine.handlers.ExpressionHandler;
import com.synditcorp.ruleengine.interfaces.RuleClassHandler;
import com.synditcorp.ruleengine.logging.TimeTrack;
import com.synditcorp.ruleengine.processors.CalcRuleProcessor;

/**
 * This class provides the runtime methods for the Rule Engine. This
 * class is the primary class used to interact with the Rule Engine.
 * RuleDefinitions, the RuleParser, or other rule engine classes need not be
 * accessed directly.
 */
public class RuleEvaluator implements Cloneable {
	
	public static final Logger logger = LogManager.getLogger(RuleEvaluator.class);

	private RuleDefinition ruleDefinition;
	private TreeMap<Integer, Boolean> cache = new TreeMap<Integer, Boolean>();
	private TreeMap<String, Object> variables = new TreeMap<String, Object>();
	private TreeMap<String, Object> calcRuleInstances = new TreeMap<String, Object>();
	private int threadBlockSize = 100;
	private ForkJoinPool pool = null;

	public RuleEvaluator(RuleDefinition rulesDefinition) {
		this.ruleDefinition = rulesDefinition;
		instantiateClassHandlers();				
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

		logger.info("Syndit Rule Engine evaluating rule number {} using document ID {}, version {}", ruleNumber,
				this.getDocumentId(), this.getVersion());
		
		TimeTrack t = null;
		if(logger.isDebugEnabled()) {
			t = new TimeTrack();
		}

		Boolean result = callRule(ruleNumber);
		
		if(logger.isDebugEnabled()) {
			if( t != null) {
				long l = TimeTrack.getElapsedTime(t);
				logger.debug("{} milliseconds ({} nanoseconds) to evaluate rule number {}", TimeUnit.NANOSECONDS.toMillis(l), l,  ruleNumber);
			}
		}
		

		if (result == null)
			throw new NoRuleEvaluatedException();

		logger.info("Syndit Rule Engine completed evaluation of rule number {} with result equal to {}", ruleNumber,
				result);

		return (result.booleanValue());

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
	 * collection entries are added to the internal map collection. Also to protect
	 * the integrity of the Engine, if variables exist, a EngineSafeguardException is
	 * thrown.  Use resetVariables to set a new variable collection.
	 * 
	 * @throws EngineSafeguardException when variables collection is null or if variables exist in the internal map collection
	 * @param variables for the Engine's expressions
	 */
	public void setVariables(TreeMap<String, Object> variables) throws EngineSafeguardException {
		
		if (!this.variables.isEmpty())
			throw new EngineSafeguardException(
					"Variables collection already populated.  Use 'resetVariables' to set a new variables collection.");

		setVariablesProtected(variables);
		
	}
	
	/**
	 * Replaces the Engine variable collection.  This is used internally by the engine
	 * to set the collection without safeguards. 
	 * 
	 * @throws EngineSafeguardException if the variable collection is null
	 * @param variables for the Engine's expressions
	 */
	protected void setVariablesProtected(TreeMap<String, Object> variables) throws EngineSafeguardException {
		
		if(variables == null) throw new EngineSafeguardException("Variables collection cannot be null");
		this.variables = variables;

	}

	/**
	 * Set a variable to the rules engine variables collection.  To protect
	 * the integrity of the Engine, if variables exist, an DuplicateKeyException is 
	 * thrown.  If the key or variable is null, an EngineSafeguardException is thrown.
	 * 
	 * @throws EngineSafeguardException when the key or variable is null.
	 * @throws DuplicateKeyException when the key or variable is already exist in the variable
	 * collection.
	 * @param variable key
	 * @param variable object
	 */
	public void setVariable(String key, Object variable) throws Exception {
		
		if(key == null || variable == null)
			throw new EngineSafeguardException("Variable key or object is null");
		
		if(this.variables.containsKey(key))
			throw new DuplicateKeyException("Variable already exists in variables collection");
		
		this.variables.put(key, variable);	
		
	}
	
	/**
	 * Reset the runtime cache and current variable collection then set the variables the Engine 
	 * will use in expressions, or passed to custom rule handlers. To protect the integrity of the 
	 * engine, the passed map collection entries are added to the internal map collection.
	 * 
	 * @throws Exception 
	 * @param variables for the Engine's expressions
	 */
	public void resetVariables(TreeMap<String, Object> variables) throws Exception {
		
		clearCache();
		setVariablesProtected(variables);

	}

	/**
	 * Get a copy of the TreeMap object containing the variables passed to and generated
	 * by the Engine. This includes global runtime variables generated at runtime. To protect the 
	 * integrity of the engine's results, a copy of the variables is returned rather than the 
	 * object instance used by the engine.  
	 * 
	 * @return A new instance copy of the TreeMap object containing the variables passed to and generated
	 *         by the Engine
	 */
	public TreeMap<String, Object> getVariables() {

		return this.variables;

	}

	/**
	 * Get the pass number outcome for a particular rule's outcome key. This returns
	 * the number number results of an expression. If composite rules are specified,
	 * the number returned is the sum of each composite rule key's expressions.
	 * Values are only returned if the rule was evaluated at runtime and passed or
	 * failed.
	 * 
	 * @return a rule's number outcome for the given key.
	 * @throws Exception when any exception occurs.
	 * @param ruleNumber value for a given rule number and the rule's outcome key.
	 */
	public Double getNumberOutcome(Integer ruleNumber, String key) throws Exception {

		Outcome outcome;

		if (isRuntimePass(ruleNumber)) {
			outcome = (Outcome) getRule(ruleNumber).getPassNumberOutcome(key);
		} else if (isRuntimeFail(ruleNumber)) {
			outcome = (Outcome) getRule(ruleNumber).getFailNumberOutcome(key);
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
	 * array if the composite rule was evaluated at runtime and passed or failed.
	 * 
	 * @return a rule's string array of the tag outcomes for the given key.
	 * @throws Exception when any exception occurs.
	 * @param ruleNumber value for a given rule number and the rule's outcome key.
	 */
	public ArrayList<String> getTagOutcome(Integer ruleNumber, String key) throws Exception {

		Outcome outcome;

		if (isRuntimePass(ruleNumber)) {
			outcome = (Outcome) getRule(ruleNumber).getPassTagOutcome(key);
		} else if (isRuntimeFail(ruleNumber)) {
			outcome = (Outcome) getRule(ruleNumber).getFailTagOutcome(key);
		} else {
			throw new NoRuleEvaluatedException("rule number " + ruleNumber);
		}

		if (outcome == null)
			return null;
		return getTagOutcome(outcome);

	}

	/**
	 * Gets a clone of the RuleEvaluator instance.
	 * 
	 * @return a cloned RuleEvaluator.
	 */
	protected Object clone() {

		RuleEvaluator newRuleEvaluator = new RuleEvaluator(this.ruleDefinition);

		for (int i = 0; i < 2; i++) {
			try {
				newRuleEvaluator.setVariablesProtected(this.variables);
				newRuleEvaluator.setCache(this.cache);
			} catch (Exception e) {
				newRuleEvaluator.reset();
				continue;
			}
			break;
		}

		return newRuleEvaluator;

	}

	/**
	 * Sets the runtime cache. This is used to clone a RuleEvaluator instance.
	 * 
	 * @throws EngineSafeguardException
	 */
	protected void setCache(TreeMap<Integer, Boolean> cache) throws EngineSafeguardException {
		if (!this.cache.isEmpty())
			throw new EngineSafeguardException("Cache contains evaulation results.  Use 'reset' to clear results.");
		this.cache.putAll(cache);
	}
	

	/**
	 * Instantiates all RuleClassHandlers and puts to TreeMap collection for retrieval at runtime.
	 * 
	 */
	private void instantiateClassHandlers() {
		instantiateHandlerClasses();
		instantiateCalcRuleClasses();
	}
	
	private void instantiateHandlerClasses() {
		
		ArrayList<HandlerClass> handlerClasses = this.ruleDefinition.getHandlerClasses();
		
		if(handlerClasses == null || handlerClasses.isEmpty()) return;
		
		Iterator<HandlerClass> iterator = handlerClasses.iterator();
		while (iterator.hasNext()) {
			
			HandlerClass handlerClass = iterator.next();
			
			try {
				
				String handlerClassID = handlerClass.getHandlerClassID();
				
				if(this.calcRuleInstances.containsKey(handlerClassID)) continue;
				
				if(handlerClass.getHandlerConstParams() != null) {
					String[] params = handlerClass.getHandlerConstParams();
					RuleClassHandler handler = (RuleClassHandler) Class.forName( handlerClass.getHandlerClass() ).getDeclaredConstructor(String[].class).newInstance((Object) params);
					this.calcRuleInstances.put(handlerClass.getHandlerClassID(), handler);
					
				} else {
					RuleClassHandler handler = (RuleClassHandler) Class.forName( handlerClass.getHandlerClass() ).getDeclaredConstructor().newInstance();
					this.calcRuleInstances.put(handlerClass.getHandlerClassID(), handler);
				}
			
			} catch (Exception e) {
				logger.info("RuleEvaluator: Exception creating rule class handler: " + handlerClass.getHandlerClassID());
			}
		}
		
	}
	
	/*
	 * For backward compatibility
	 */
	private void instantiateCalcRuleClasses() {
		
		HashSet<String> ruleClasses = this.ruleDefinition.getRuleClasses();
		
		for(Iterator iterator = ruleClasses.iterator();iterator.hasNext();) {
			String ruleClassHandler = (String) iterator.next();
			try {
				
				if(this.calcRuleInstances.containsKey(ruleClassHandler)) continue;
				
				RuleClassHandler handler = (RuleClassHandler) Class.forName(ruleClassHandler).getDeclaredConstructor().newInstance();
				this.calcRuleInstances.put(ruleClassHandler, handler);

			} catch (Exception e) {
				logger.info("RuleEvaluator: No Java class found for rule class in definition: " + ruleClassHandler);
			}
		}
		
	}
	
	
	/**
	 * To protect the integrity of the engine, this is used to prevent existing variables from 
	 * being overwritten at runtime.
	 * 
	 * @param variableName to be added to the collection and the variable's object
	 */
	private void putToVariables(String variableName, Object value) throws Exception {

		if(logger.isDebugEnabled()) {
			if (this.variables.containsKey(variableName)) 
				logger.debug("Variable value being overwritten: " + variableName);
		}

		this.variables.put(variableName, value);

	}

	private boolean isRuntimePass(Integer ruleNumber) {

		if (cache.containsKey(ruleNumber) && cache.get(ruleNumber) == true) {
			return true;
		}

		return false;

	}

	private boolean isRuntimeFail(Integer ruleNumber) {

		if (cache.containsKey(ruleNumber) && cache.get(ruleNumber) == false) {
			return true;
		}

		return false;

	}

	private void addRuntimePass(Integer ruleNumber) {

		if (cache.containsKey(ruleNumber))
			return;

		cache.put(ruleNumber, true);

	}

	private void addRuntimeFail(Integer ruleNumber) {

		if (cache.containsKey(ruleNumber))
			return;

		cache.put(ruleNumber, false);

	}

	/**
	 * After a rule evaluates, this sets global pass outcomes to the variable collection 
	 * @param ruleNumber
	 * @throws Exception
	 */
	private void setGlobalPassOutcomes(Integer ruleNumber) throws Exception {

		setGlobalPassNumberOutcomesToVars(ruleNumber);
		setGlobalPassTagOutcomesToVars(ruleNumber);

	}

	/**
	 * After a rule evaluates, this sets global fail outcomes to the variable collection 
	 * @param ruleNumber
	 * @throws Exception
	 */
	private void setGlobalFailOutcomes(Integer ruleNumber) throws Exception {

		setGlobalFailNumberOutcomesToVars(ruleNumber);
		setGlobalFailTagOutcomesToVars(ruleNumber);

	}

	private void setGlobalPassNumberOutcomesToVars(Integer ruleNumber) throws Exception {

		Rule rule = getRule(ruleNumber);
		if (!isRuntimePass(ruleNumber))
			return;
		ArrayList<Outcome> outcomes = rule.getPassNumberOutcomes();
		if (outcomes == null)
			return;

		setGlobalNumberOutcomes(outcomes);

	}

	private void setGlobalFailNumberOutcomesToVars(Integer ruleNumber) throws Exception {

		Rule rule = getRule(ruleNumber);
		if (!isRuntimeFail(ruleNumber))
			return;
		ArrayList<Outcome> outcomes = rule.getFailNumberOutcomes();
		if (outcomes == null)
			return;

		setGlobalNumberOutcomes(outcomes);

	}

	private void setGlobalPassTagOutcomesToVars(Integer ruleNumber) throws Exception {

		Rule rule = getRule(ruleNumber);
		if (!isRuntimePass(ruleNumber))
			return;
		ArrayList<Outcome> outcomes = rule.getPassTagOutcomes();
		if (outcomes == null)
			return;

		setGlobalTagOutcomes(outcomes);

	}

	private void setGlobalFailTagOutcomesToVars(Integer ruleNumber) throws Exception {

		Rule rule = getRule(ruleNumber);
		if (!isRuntimeFail(ruleNumber))
			return;
		ArrayList<Outcome> outcomes = rule.getFailTagOutcomes();
		if (outcomes == null)
			return;

		setGlobalTagOutcomes(outcomes);

	}

	private void setGlobalNumberOutcomes(ArrayList<Outcome> outcomes) throws Exception {

		Iterator<Outcome> iterator = outcomes.iterator();
		while (iterator.hasNext()) {

			Outcome outcome = (Outcome) iterator.next();
			if (outcome.getGlobal() == null || !outcome.getGlobal())
				continue;
			Double dbl = getNumberOutcome(outcome);

			if (dbl == null)
				continue;

			putToVariables(outcome.getVariableName(), dbl);

		}

	}

	private void setGlobalTagOutcomes(ArrayList<Outcome> outcomes) throws Exception {

		Iterator<Outcome> iterator = outcomes.iterator();
		while (iterator.hasNext()) {

			Outcome outcome = (Outcome) iterator.next();
			if (outcome.getGlobal() == null || !outcome.getGlobal())
				continue;
			ArrayList<String> list = getTagOutcome(outcome);
			if (list == null)
				continue;

			putToVariables(outcome.getVariableName(), list);

		}

	}

	private Double getNumberOutcome(Outcome outcome) throws Exception {

		//if the outcome has already been calculated and added to variable collection, just get value from collection
		if (this.variables.containsKey(outcome.getVariableName()))
			return (Double) this.variables.get(outcome.getVariableName());

		Double outcomeNumber = calcNumberOutcome(outcome);

		ArrayList<Integer> compositeRulesList = outcome.getCompositeOutcomeRules();
		if (compositeRulesList == null)
			return outcomeNumber;

		Iterator<Integer> iterator = compositeRulesList.iterator();
		while (iterator.hasNext()) {

			Outcome nextOutcome = null;

			Integer ruleNumber = iterator.next();

			int abs = Math.abs(ruleNumber);

			if (ruleNumber.intValue() > 0) {
				if (!isRuntimePass(abs))
					continue;
				nextOutcome = (Outcome) getRule(abs).getPassNumberOutcome(outcome.getKey());
			} else if (ruleNumber.intValue() < 0) {
				if (!isRuntimeFail(abs))
					continue;
				nextOutcome = (Outcome) getRule(abs).getFailNumberOutcome(outcome.getKey());
			}

			if (nextOutcome == null)
				continue;

			if (outcomeNumber == null)
				outcomeNumber = getNumberOutcome(nextOutcome);
			else
				outcomeNumber = Double.sum(outcomeNumber, getNumberOutcome(nextOutcome));

		}

		return outcomeNumber;

	}

	private ArrayList<String> getTagOutcome(Outcome outcome) throws Exception {

		//if the outcome has already been calculated and added to variable collection, just get value from collection
		if (this.variables.containsKey(outcome.getVariableName()))
			return (ArrayList<String>) this.variables.get(outcome.getVariableName());

		ArrayList<String> outcomeTags = new ArrayList<String>();
		
		String outcomeStr = calcTagOutcome(outcome);
		if(outcomeStr != null && outcomeStr.length() > 0) {
			outcomeTags.add(outcomeStr);
		}

		ArrayList<Integer> compositeRulesList = outcome.getCompositeOutcomeRules();
		if (compositeRulesList == null)
			return outcomeTags;

		Iterator<Integer> iterator = compositeRulesList.iterator();
		while (iterator.hasNext()) {

			Outcome nextOutcome = null;

			Integer ruleNumber = iterator.next();

			int abs = Math.abs(ruleNumber);

			if (ruleNumber.intValue() > 0) {
				if (!isRuntimePass(abs))
					continue;
				nextOutcome = (Outcome) getRule(abs).getPassTagOutcome(outcome.getKey());
			} else if (ruleNumber.intValue() < 0) {
				if (!isRuntimeFail(abs))
					continue;
				nextOutcome = (Outcome) getRule(abs).getFailTagOutcome(outcome.getKey());
			}

			if (nextOutcome == null)
				continue;

			outcomeTags.addAll(getTagOutcome(nextOutcome));

		}

		// return after removing duplicates
		return (ArrayList<String>) outcomeTags.stream().distinct().collect(Collectors.toList());

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
		
		//if it's already been evaluated and rule uses cache, get result from cache
		if (cache.containsKey(ruleNumber) && useCache(ruleNumber))
			return cache.get(ruleNumber);
		
		//checking active, effective and expiration dates
		if (!isRuleApplicable(ruleNumber))
			return null;

		if (ruleDefinition.isCalcRule(ruleNumber))
			return (processCalcRule(ruleNumber));

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

	private boolean isRuleApplicable(Integer ruleNumber) throws Exception {

		Date nowDate = new Date();

		Date effectiveDate = getRule(ruleNumber).getEffectiveDate();
		Date expirationDate = getRule(ruleNumber).getExpirationDate();
		Boolean active = getRule(ruleNumber).getActive();

		boolean isApplicable = (active == null || active == true)
				&& (nowDate == null || effectiveDate == null || nowDate.after(effectiveDate)
						|| nowDate.equals(effectiveDate))
				&& (expirationDate == null || expirationDate == null || nowDate.before(expirationDate));

		return isApplicable;

	}
	
	/**
	 * Returns whether to use the runtime cache when determining whether to evaluate.  Defaults to true.
	 */
	private boolean useCache(Integer ruleNumber) throws Exception {
		
		return (getRule(ruleNumber).getIgnoreCache() == null) || (getRule(ruleNumber).getIgnoreCache() == false);
		
	}

	private Rule getRule(Integer ruleNumber) throws Exception {
		return ruleDefinition.getRule(ruleNumber);
	}

	private void reset() {
		clearCache();
		clearVariables();
	}

	private void clearVariables() {
		this.variables.clear();
	}

	private void clearCache() {
		cache.clear();
	}

	private void addToCache(Integer ruleNumber, Boolean result) {
		cache.put(ruleNumber, result);
	}

	private Boolean processCalcRule(Integer ruleNumber) throws Exception {

		String ruleHandler = ((CalcRule) getRule(ruleNumber)).getHandlerClass();
		RuleClassHandler ruleClassHandler = (RuleClassHandler) this.calcRuleInstances.get(ruleHandler);
		String expression = ((CalcRule) getRule(ruleNumber)).getExpression();

		Boolean result = null;
		try {
			result = CalcRuleProcessor.processCalcRule(ruleClassHandler, expression, variables);
		} catch (NoRuleEvaluatedException e) {
			logger.info("Unable to process rule expression: \"" + expression + "\", reason: " + e);
			throw e;
		} catch (PropertyAccessException e) {
			logger.info("Unable to process rule expression: \"" + expression + "\", reason: " + e);
			throw new RuleEvaluationException("Unable to process rule expression: \"" + expression + "\", reason: " + e);
		}

		addToCache(ruleNumber, result);

		if (result.booleanValue()) {
			addRuntimePass(ruleNumber);
			setGlobalPassOutcomes(ruleNumber);
		} else {
			addRuntimeFail(ruleNumber);
			setGlobalFailOutcomes(ruleNumber);
		}

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

		boolean noRulesProcessed = true;

		ArrayList<Integer> compositeRuleList = getCompositeRulesList(ruleNumber);
		if (compositeRuleList.size() == 0)
			return null;
		for (int i = 0; i < compositeRuleList.size(); i++) {
			Boolean result = processRule(compositeRuleList.get(i));
			if (result == null)
				continue;
			noRulesProcessed = false;
		}

		if (noRulesProcessed)
			return null;

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
	 * persist when evaluation is complete.  The values of global Outcome keys listed in 
	 * the 'numberKeys' and 'tagKeys' arrays are retrieved from the threads and added
	 * to to the variables collection.
	 * 
	 * @return returns true
	 * @throws Exception when any exception occurs
	 * @param ruleNumber value for a given rule number
	 */
	private Boolean processThreadRules(Integer ruleNumber) throws Exception {

		ThreadRule threadRule = ((ThreadRule) getRule(ruleNumber));
		ArrayList<ThreadResults> threadResults = getThreadResults(threadRule);
		String variableName = ruleDefinition.getDocumentId() + "_" + ruleNumber + "_";

		// loop through all the rule results processed in the threads
		for (int i = 0; i < threadResults.size(); i++) {

			if (threadResults.get(i).getNumberOutcomes() != null) {
				TreeMap<String, Double> numberOutcomes = threadResults.get(i).getNumberOutcomes();
				for (Map.Entry<String, Double> entry : numberOutcomes.entrySet()) {

					String threadVariable = variableName + entry.getKey();
					if (this.variables.containsKey(threadVariable)) {

						Double sumOf = Double.sum((Double) this.variables.get(threadVariable), entry.getValue());
						this.variables.put(threadVariable, sumOf);

					} else {
						this.variables.put(threadVariable, entry.getValue());
						threadRule.setPassNumberOutcome(entry.getKey(), threadVariable);
					}

				}

			}

			if (threadResults.get(i).getTagOutcomes() != null) {
				TreeMap<String, ArrayList<String>> tagOutcomes = threadResults.get(i).getTagOutcomes();
				for (Map.Entry<String, ArrayList<String>> entry : tagOutcomes.entrySet()) {

					String threadVariable = variableName + entry.getKey();
					if (this.variables.containsKey(threadVariable)) {
						((ArrayList<String>) this.variables.get(threadVariable)).addAll(entry.getValue());
					} else {
						this.variables.put(threadVariable, entry.getValue());
						threadRule.setPassTagOutcome(entry.getKey(), threadVariable);
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
	private ArrayList<ThreadResults> getThreadResults(ThreadRule threadRule) throws Exception {

		if (pool == null)
			pool = new ForkJoinPool();

		ArrayList<String> numberKeys = threadRule.getNumberKeys();
		ArrayList<String> tagKeys = threadRule.getTagKeys();
		ArrayList<Integer> threadRulesList = threadRule.getThreadRules();
		ArrayList<Integer> block = new ArrayList<Integer>();

		ArrayList<ThreadRuleCompute> tasks = new ArrayList<ThreadRuleCompute>();
		int listSize = threadRulesList.size();
		int ctr = 0;
		for (int i = 0; i < listSize; i++) {
			block.add(threadRulesList.get(i));
			ctr++;
			if (ctr == this.threadBlockSize || (i + 1) == listSize) {
				ArrayList<Integer> passBlock = new ArrayList<Integer>();
				passBlock.addAll(block);
				
				RuleEvaluator cloneOf = (RuleEvaluator) this.clone();
				
				ThreadProcObjects objects = new ThreadProcObjects(
						threadRule.getRuleNumber(),
						cloneOf,
						passBlock,
						numberKeys,
						tagKeys
						);

				ThreadRuleCompute threadRuleCompute = new ThreadRuleCompute(objects);
				tasks.add(threadRuleCompute);
				pool.execute(threadRuleCompute);
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
				return (true);
			}
		}

		if (noRulesProcessed)
			return null;

		addRuntimeFail(ruleNumber);
		setGlobalFailOutcomes(ruleNumber);

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
				return false;
			}

		}

		if (noRulesProcessed)
			return null;

		addRuntimePass(ruleNumber);
		setGlobalPassOutcomes(ruleNumber);

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
		return ((CompositeRule) getRule(ruleNumber)).getCompositeRules();
	}

	private Double evaluateNumberExpression(String expression) {
		return ExpressionHandler.getProductOf(expression, variables);
	}

	private String evaluateStringExpression(String expression) {
		return ExpressionHandler.evaluateStringExpression(expression, variables);
	}

}
