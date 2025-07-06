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
import java.util.Map;
import java.util.TreeMap;

import com.synditcorp.ruleengine.beans.AllRule;
import com.synditcorp.ruleengine.beans.AndRule;
import com.synditcorp.ruleengine.beans.BaseRules;
import com.synditcorp.ruleengine.beans.CalcRule;
import com.synditcorp.ruleengine.beans.OrRule;
import com.synditcorp.ruleengine.beans.ThreadRule;
import com.synditcorp.ruleengine.exceptions.NoRuleFoundException;
import com.synditcorp.ruleengine.exceptions.OutcomeKeyException;
import com.synditcorp.ruleengine.interfaces.Outcome;
import com.synditcorp.ruleengine.interfaces.Rule;
import com.synditcorp.ruleengine.interfaces.RuleDefinition;
import com.synditcorp.ruleengine.interfaces.RuleParser;

/**
 * This class loads rule definitions from parsers that implement
 * com.synditcorp.ruleengine.interfaces.RuleParser. Methods of this class
 * provide access to base and composite rule field objects. After loading the
 * rules, this class is primarily to be used by the rule engine evaluator and
 * shouldn't be accessed directly.
 */
public class DefaultRuleDefinition implements RuleDefinition {

	private final BaseRules baseRules;
	private final TreeMap<Integer, Rule> aggregateRules = new TreeMap<Integer, Rule>();

	public DefaultRuleDefinition(RuleParser parser) throws Exception {
		this.baseRules = parser.getRules();
		setToAggregateRules();
	}

	/**
	 * Returns the ID of the rules definition. Definition is not used at runtime to
	 * evaluate rules.
	 */
	@Override
	public String getDocumentId() {
		return baseRules.getDocumentId();
	}

	/**
	 * Returns the description of the rules definition. Description is not used at
	 * runtime to evaluate rules.
	 */
	@Override
	public String getDescription() {
		return baseRules.getDescription();
	}

	/**
	 * Returns the version of the rules definition. Version is not used at runtime
	 * to evaluate rules.
	 */
	@Override
	public String getVersion() {
		return baseRules.getVersion();
	}

	/**
	 * Returns the version of the rules definition. Version is not used at runtime
	 * to evaluate rules.
	 */
	@Override
	public Boolean getActive() {
		return baseRules.getActive();
	}

	/**
	 * Optional document tags are used to further define a document. Document tags
	 * are not used at runtime to evaluate rules. Tags can be used for things like
	 * authorization in databases or display control in custom rule definition
	 * editors
	 */
	@Override
	public ArrayList<String> getDocumentTags() {
		return baseRules.getDocumentTags();
	}

	/**
	 * This is intended to hold the rule number at the base of the decision tree so
	 * calling programs can refer to this value at runtime rather than having to
	 * rely on other processes to communicate the starting rule of a decision tree.
	 * This is optional: any rule can be called directly. This is not used when
	 * evaluating rules at runtime.
	 */
	@Override
	public Integer getStartRule() {
		return baseRules.getStartRule();
	}

	/**
	 * Optional rule tags are used to further define a rule, but are not used when
	 * evaluating rules at runtime. Tags can be used for things like authorization
	 * in databases or display control in custom rule definition editors
	 */
	@Override
	public ArrayList<String> getRuleTags(Integer ruleNumber) throws Exception {
		return getRule(ruleNumber).getRuleTags();
	}

	/**
	 * Returns "true" if the rule is a "base" rule
	 */
	@Override
	public void isRule(Integer ruleNumber) throws NoRuleFoundException {
		if (!aggregateRules.containsKey(ruleNumber))
			throw new NoRuleFoundException(ruleNumber.toString());
	}

	/**
	 * Returns "true" if the rule is a "calc" rule
	 */
	@Override
	public boolean isCalcRule(Integer ruleNumber) throws Exception {
		return aggregateRules.get(ruleNumber).getClass().getSimpleName().equals("CalcRule");
	}

	/**
	 * Returns "true" if the rule is an "or" rule
	 */
	@Override
	public boolean isOrRule(Integer ruleNumber) throws Exception {
		return aggregateRules.get(ruleNumber).getClass().getSimpleName().equals("OrRule");
	}

	/**
	 * Returns "true" if the rule is an "and" rule
	 */
	@Override
	public boolean isAndRule(Integer ruleNumber) throws Exception {
		return aggregateRules.get(ruleNumber).getClass().getSimpleName().equals("AndRule");
	}

	/**
	 * Returns "true" if the rule is an "all" rule
	 */
	@Override
	public boolean isAllRule(Integer ruleNumber) throws Exception {
		return aggregateRules.get(ruleNumber).getClass().getSimpleName().equals("AllRule");
	}

	/**
	 * Returns "true" if the rule is a "thread" rule
	 */
	@Override
	public boolean isThreadRule(Integer ruleNumber) throws Exception {
		return aggregateRules.get(ruleNumber).getClass().getSimpleName().equals("ThreadRule");
	}

	/**
	 * Returns a Rule object for a particular rule number
	 */
	@Override
	public Rule getRule(Integer ruleNumber) throws Exception {

		return this.aggregateRules.get(ruleNumber);

	}

	/*
	 * For runtime performance, configure as much as possible when loading rules
	 * where elapsed time is not as critical as it is when evaluating rules at
	 * runtime. Rule definitions are intended to be loaded ahead of time (e.g.
	 * server startup) and cached for reuse by RuleEvaluator instances.
	 */
	private void setToAggregateRules() throws Exception {
		setCalcRules();
		setOrRules();
		setAndRules();
		setAllRules();
		setThreadRules();
		validateOutcomeKeys();

	}

	private void setCalcRules() throws Exception {
		if (this.baseRules.getCalcRules() == null)
			return;
		ArrayList<CalcRule> ar = this.baseRules.getCalcRules();
		for (Iterator<CalcRule> iterator = ar.iterator(); iterator.hasNext();) {
			CalcRule calcRule = (CalcRule) iterator.next();
			if (!calcRule.getRuleType().equalsIgnoreCase("calc"))
				throw new IllegalArgumentException("calc rules list can't have '" + calcRule.getRuleType() + "' rules");
			calcRule.setOutcomesToCategories(this.getDocumentId());
			this.aggregateRules.put(calcRule.getRuleNumber(), calcRule);
		}
	}

	private void setOrRules() throws Exception {
		if (this.baseRules.getOrRules() == null)
			return;
		ArrayList<OrRule> ar = this.baseRules.getOrRules();
		for (Iterator<OrRule> iterator = ar.iterator(); iterator.hasNext();) {
			OrRule orRule = (OrRule) iterator.next();
			if (!orRule.getRuleType().equalsIgnoreCase("or"))
				throw new IllegalArgumentException("'Or' rules list can't have '" + orRule.getRuleType() + "' rules");
			orRule.setOutcomesToCategories(this.getDocumentId());
			this.aggregateRules.put(orRule.getRuleNumber(), orRule);
		}
	}

	private void setAndRules() throws Exception {
		if (this.baseRules.getAndRules() == null)
			return;
		ArrayList<AndRule> ar = this.baseRules.getAndRules();
		for (Iterator<AndRule> iterator = ar.iterator(); iterator.hasNext();) {
			AndRule andRule = (AndRule) iterator.next();
			if (!andRule.getRuleType().equalsIgnoreCase("and"))
				throw new IllegalArgumentException("'And' rules list can't have '" + andRule.getRuleType() + "' rules");
			andRule.setOutcomesToCategories(this.getDocumentId());
			this.aggregateRules.put(andRule.getRuleNumber(), andRule);
		}
	}

	private void setAllRules() throws Exception {
		if (this.baseRules.getAllRules() == null)
			return;
		ArrayList<AllRule> ar = this.baseRules.getAllRules();
		for (Iterator<AllRule> iterator = ar.iterator(); iterator.hasNext();) {
			AllRule allRule = (AllRule) iterator.next();
			if (!allRule.getRuleType().equalsIgnoreCase("all"))
				throw new IllegalArgumentException("'All' rules list can't have '" + allRule.getRuleType() + "' rules");
			this.aggregateRules.put(allRule.getRuleNumber(), allRule);
		}
	}

	private void setThreadRules() throws Exception {
		if (this.baseRules.getThreadRules() == null)
			return;
		ArrayList<ThreadRule> ar = this.baseRules.getThreadRules();
		for (Iterator<ThreadRule> iterator = ar.iterator(); iterator.hasNext();) {
			ThreadRule threadRule = (ThreadRule) iterator.next();
			if (!threadRule.getRuleType().equalsIgnoreCase("thread"))
				throw new IllegalArgumentException(
						"'Thread' rules list can't have '" + threadRule.getRuleType() + "' rules");
			threadRule.setOutcomesToCategories(this.getDocumentId());
			this.aggregateRules.put(threadRule.getRuleNumber(), threadRule);
		}
	}

	private void validateOutcomeKeys() throws Exception {

		ArrayList<Outcome> outcomes = new ArrayList<Outcome>();
		ArrayList<String> numberOutcomes = new ArrayList<String>();
		ArrayList<String> tagOutcomes = new ArrayList<String>();

		for (Map.Entry<Integer, Rule> entry : aggregateRules.entrySet()) {
			if (entry.getValue().getRuleType().equalsIgnoreCase("thread"))
				continue;
			if (entry.getValue().getOutcomes() == null)
				continue;
			outcomes.addAll(entry.getValue().getOutcomes());
		}

		for (Iterator<Outcome> iterator = outcomes.iterator(); iterator.hasNext();) {
			Outcome outcome = iterator.next();
			if (outcome.getType().equalsIgnoreCase("number")) {
				if (tagOutcomes.contains(outcome.getKey())) {
					throw new OutcomeKeyException(
							"Outcome key '" + outcome.getKey() + "' cannot be used for different outcome types.");
				} else {
					if (!numberOutcomes.contains(outcome.getKey()))
						numberOutcomes.add(outcome.getKey());
				}
			} else if (outcome.getType().equalsIgnoreCase("tag")) {
				if (numberOutcomes.contains(outcome.getKey())) {
					throw new OutcomeKeyException(
							"Outcome key '" + outcome.getKey() + "' cannot be used for different outcome types.");
				} else {
					if (!tagOutcomes.contains(outcome.getKey()))
						tagOutcomes.add(outcome.getKey());
				}

			}
		}

	}

}
