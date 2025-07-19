package com.synditcorp.ruleengine.tests;



import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import com.synditcorp.ruleengine.RuleDefinition;
import com.synditcorp.ruleengine.RuleEvaluator;
import com.synditcorp.ruleengine.exceptions.NoRuleEvaluatedException;
import com.synditcorp.ruleengine.exceptions.RuleEvaluationException;
import com.synditcorp.ruleengine.parser.RuleJSONParser;

public class DefinitionTests {
	
	RuleEvaluator ruleEvaluator = null;

	/*
	 * Fails because a calc rule can't be in an andRules array 
	 */
	@Test
	void test1() throws Exception {

		Exception exception = assertThrows(com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.class, () -> {
			String fileName = "test1.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
	    });

		
	}
	
	/*
	 * Fails because an 'and' rule can't be in an orRules array 
	 */
	@Test
	void test2() throws Exception {
		
		Exception exception = assertThrows(java.lang.IllegalArgumentException.class, () -> {
			String fileName = "test2.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);
	    });
		
	}
	
	/*
	 * Fails because an 'or' rule can't be in an allRules array 
	 */
	@Test
	void test3() throws Exception {
		
		Exception exception = assertThrows(java.lang.IllegalArgumentException.class, () -> {
			String fileName = "test3.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);
			
	    });
		
	}
	
	/*
	 * Fails because an 'all' rule can't be in a threadRules array 
	 */
	@Test
	void test4() throws Exception {
		
		Exception exception = assertThrows(com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.class, () -> {
			String fileName = "test4.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);
	    });
		
	}
	
	/*
	 * Fails because a thread rule can't be in a calcRules array 
	 */
	@Test
	void test5() throws Exception {
		
		Exception exception = assertThrows(com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.class, () -> {
			String fileName = "test5.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
	    });
		
	}
	
	/*
	 * Fails because of an invalid rule type 
	 */
	@Test
	void test6() throws Exception {
		
		Exception exception = assertThrows(com.fasterxml.jackson.databind.exc.ValueInstantiationException.class, () -> {
			String fileName = "test6.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
	    });
		
	}
	
	/*
	 * Rule 19 is set to false, throws NoRuleEvaluatedException
	 */
	@Test
	void test7() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();

		Exception exception = assertThrows(NoRuleEvaluatedException.class, () -> {
			ruleEvaluator.evaluateRule(19);
	    });
		
	}
	
	/*
	 * Rule 20 has expired, throws NoRuleEvaluatedException
	 */
	@Test
	void test8() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();

		Exception exception = assertThrows(NoRuleEvaluatedException.class, () -> {
			ruleEvaluator.evaluateRule(20);
	    });
		
	}
	
	/*
	 * Rule 21 is not effective yet, throws NoRuleEvaluatedException
	 */
	@Test
	void test9() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();

		Exception exception = assertThrows(NoRuleEvaluatedException.class, () -> {
			ruleEvaluator.evaluateRule(21);
	    });
		
	}
	
	/*
	 * Rule 22 this rule will pass because it is active and falls within the date range
	 */
	@Test
	void test10() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();
		
		assertTrue(ruleEvaluator.evaluateRule(22),"Rule 22 should have passed.");
		
	}
	
	/*
	 * Rule 1 should return a score of 10 and a flag with value '1FlagP'
	 */
	@Test
	void test11() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();
		ruleEvaluator.evaluateRule(1);
		Double score = ruleEvaluator.getNumberOutcome(1,"score");
		boolean included = ruleEvaluator.getTagOutcome(1, "flag").contains("1FlagP");
		
		
		assertTrue( (score == 10 && included), "Rule 1 values not returned as expected.");
		
	}
	
	/*
	 * Rule 1 should fail because variables do not exist in collection (in this case, they weren't loaded)
	 */
	@Test
	void test12() throws Exception {
		
		String fileName = "definitionsForTesting.json";
		Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
		RuleJSONParser parser = new RuleJSONParser();
		parser.loadRules(definitionResource);
		RuleDefinition rules = new RuleDefinition(parser);
		this.ruleEvaluator = new RuleEvaluator(rules);
		
		Exception exception = assertThrows(RuleEvaluationException.class, () -> {
			ruleEvaluator.evaluateRule(1);
	    });
		
	}
	
	/*
	 * Rule 1 should return a score of 10 and a flag with value '1FlagP'
	 */
	@Test
	void test13() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();
		ruleEvaluator.evaluateRule(1);
		Double score = ruleEvaluator.getNumberOutcome(1,"score");
		boolean included = ruleEvaluator.getTagOutcome(1, "flag").contains("1FlagP");
		
		assertTrue( (score == 10 && included), "Rule 1 values not returned as expected.");
		
	}
	
	
	
	private RuleEvaluator getRuleEvaluator() throws Exception {
		
		if(this.ruleEvaluator == null) {
		
			TreeMap<String, Object> variables = new TreeMap<String, Object>();
			
			Double amount1 = 1.50;
			Double amount2 = 5.00;
			String name1 = "Buggs Bunny";
			String ID = "987654321";
			String phone = "724.555.1027";
	
			variables.put("amount1", amount1);
			variables.put("amount2", amount2);
			variables.put("name1", name1);
			variables.put("ID", ID);
			variables.put("phone", phone);

			String fileName = "definitionsForTesting.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);
			this.ruleEvaluator = new RuleEvaluator(rules);
			this.ruleEvaluator.setVariables(variables);

		}
		
		return this.ruleEvaluator;
		
	}
	
}


