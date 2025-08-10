/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synditcorp.ruleengine.exceptions.DuplicateKeyException;

public class Rule {

	private final Integer ruleNumber;
	private final String ruleType;
	private final ArrayList<String> ruleTags;
	private final String description;
	private final Boolean active;
	private final Boolean ignoreCache;
	private final Date expirationDate;
	private final Date effectiveDate;
	private final ArrayList<Outcome> outcomes;

	protected TreeMap<String, Outcome> passNumbers;
	private TreeMap<String, Outcome> failNumbers;
	protected TreeMap<String, Outcome> passTags;
	private TreeMap<String, Outcome> failTags;

	private static List<String> validRuleTypes = List.of("calc", "and", "or", "all", "thread");
	private static List<String> canHaveOutcomes = List.of("calc", "and", "or");

	@JsonCreator
	public Rule(@JsonProperty("ruleNumber") Integer ruleNumber, 
			@JsonProperty("ruleType") String ruleType,
			@JsonProperty("ruleTags") ArrayList<String> ruleTags, 
			@JsonProperty("description") String description,
			@JsonProperty("active") Boolean active, 
			@JsonProperty("ignoreCache") Boolean ignoreCache,
			@JsonProperty("expirationDate") Date expirationDate,
			@JsonProperty("effectiveDate") Date effectiveDate,
			@JsonProperty("outcomes") ArrayList<Outcome> outcomes) {

		this.ruleNumber = validateRuleNumber(ruleNumber);
		this.ruleType = validateRuleType(ruleType);
		this.ruleTags = ruleTags;
		this.description = description;
		this.active = active;
		this.ignoreCache = ignoreCache;
		this.expirationDate = expirationDate;
		this.effectiveDate = effectiveDate;
		canHaveOutcomes(ruleType, outcomes);
		this.outcomes = outcomes;

	}

	private String validateRuleType(String ruleType) throws IllegalArgumentException {
		String lowerCaseRuleType = ruleType.toLowerCase();
		if (!validRuleTypes.contains(lowerCaseRuleType))
			throw new IllegalArgumentException("Rule type not valid.  Must be one of these: " + validRuleTypes);
		return lowerCaseRuleType;
	}

	protected String getRuleType() {
		return this.ruleType;
	}

	protected ArrayList<String> getRuleTags() {
		return this.ruleTags;
	}

	private Integer validateRuleNumber(Integer ruleNumber) throws IllegalArgumentException {
		if (ruleNumber.intValue() < 0)
			throw new IllegalArgumentException("Rule number must be a positive number");
		return ruleNumber;
	}

	protected Integer getRuleNumber() {
		return this.ruleNumber;
	}

	protected String getDescription() {
		return this.description;
	}

	protected Boolean getActive() {
		return this.active;
	}

	protected Boolean getIgnoreCache() {
		return this.ignoreCache;
	}

	protected Date getEffectiveDate() {
		return this.effectiveDate;
	}

	protected Date getExpirationDate() {
		return this.expirationDate;
	}

	private void canHaveOutcomes(String ruleType, ArrayList<Outcome> outcomes) throws IllegalArgumentException {
		if (!canHaveOutcomes.contains(ruleType) && outcomes != null)
			throw new IllegalArgumentException("'" + this.ruleType + "' rule types cannot have outcomes");
	}

	protected ArrayList<Outcome> getOutcomes() {
		return this.outcomes;
	}

	protected Outcome getPassNumberOutcome(String key) {
		if (passNumbers == null)
			return null;
		return (Outcome) passNumbers.get(key);

	}

	protected Outcome getFailNumberOutcome(String key) {
		if (failNumbers == null)
			return null;
		return failNumbers.get(key);

	}

	protected Outcome getPassTagOutcome(String key) {
		if (passTags == null)
			return null;
		return passTags.get(key);

	}

	protected Outcome getFailTagOutcome(String key) {
		if (failTags == null)
			return null;
		return failTags.get(key);

	}

	protected ArrayList<Outcome> getPassNumberOutcomes() {
		if (passNumbers == null)
			return null;

		return new ArrayList<Outcome>(passNumbers.values());

	};

	protected ArrayList<Outcome> getFailNumberOutcomes() {
		if (failNumbers == null)
			return null;
		ArrayList<Outcome> list = new ArrayList<Outcome>();
		for (Map.Entry<String, Outcome> entry : failNumbers.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	};

	protected ArrayList<Outcome> getPassTagOutcomes() {
		if (passTags == null)
			return null;
		ArrayList<Outcome> list = new ArrayList<Outcome>();
		for (Map.Entry<String, Outcome> entry : passTags.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	};

	protected ArrayList<Outcome> getFailTagOutcomes() {
		if (failTags == null)
			return null;
		ArrayList<Outcome> list = new ArrayList<Outcome>();
		for (Map.Entry<String, Outcome> entry : failTags.entrySet()) {
			list.add(entry.getValue());
		}
		return list;
	};

	/*
	 * For runtime performance, put outcome types into separate Maps when loading
	 * definitions
	 */
	protected void setOutcomesToCategories(String documentId) throws Exception {

		if (outcomes == null)
			return;

		Iterator<Outcome> iterator = outcomes.iterator();
		while (iterator.hasNext()) {

			Outcome outcome = (Outcome) iterator.next();
			outcome.setVariableName(documentId, this.ruleNumber);

			if (outcome.getType().equalsIgnoreCase("tag") && outcome.getResult().equalsIgnoreCase("pass")) {

				if (this.passTags == null)
					passTags = new TreeMap<String, Outcome>();

				if(this.passTags.containsKey(outcome.getKey()))
					throw new DuplicateKeyException("Duplicate Outcome " + outcome.getKey() + " key for rule " + this.ruleNumber);
				
				this.passTags.put(outcome.getKey(),outcome);
				continue;

			}
			if (outcome.getType().equalsIgnoreCase("tag") && outcome.getResult().equalsIgnoreCase("fail")) {

				if (this.failTags == null)
					failTags = new TreeMap<String, Outcome>();

				if(this.failTags.containsKey(outcome.getKey()))
					throw new DuplicateKeyException("Duplicate Outcome " + outcome.getKey() + " key for rule " + this.ruleNumber);
				
				this.failTags.put(outcome.getKey(), outcome);
				continue;

			}
			if (outcome.getType().equalsIgnoreCase("number") && outcome.getResult().equalsIgnoreCase("pass")) {

				if (this.passNumbers == null)
					passNumbers = new TreeMap<String, Outcome>();
				
				if(this.passNumbers.containsKey(outcome.getKey()))
					throw new DuplicateKeyException("Duplicate Outcome " + outcome.getKey() + " key for rule " + this.ruleNumber);
				
				this.passNumbers.put(outcome.getKey(), outcome);
				continue;

			}
			if (outcome.getType().equalsIgnoreCase("number") && outcome.getResult().equalsIgnoreCase("fail")) {

				if (this.failNumbers == null)
					failNumbers = new TreeMap<String, Outcome>();

				if(this.failNumbers.containsKey(outcome.getKey()))
					throw new DuplicateKeyException("Duplicate Outcome " + outcome.getKey() + " key for rule " + this.ruleNumber);
				
				this.failNumbers.put(outcome.getKey(), outcome);
				continue;
			}
		}

	}

}
