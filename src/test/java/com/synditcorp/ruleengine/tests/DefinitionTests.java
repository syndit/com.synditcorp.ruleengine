package com.synditcorp.ruleengine.tests;



import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
	 * Fails because of a duplicate outcome number pass key 
	 */
	@Test
	void test7() throws Exception {
		
		Exception exception = assertThrows(com.synditcorp.ruleengine.exceptions.DuplicateKeyException.class, () -> {
			String fileName = "test7.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);

	    });
		
	}
	
	/*
	 * Fails because of a duplicate outcome number fail key 
	 */
	@Test
	void test8() throws Exception {
		
		Exception exception = assertThrows(com.synditcorp.ruleengine.exceptions.DuplicateKeyException.class, () -> {
			String fileName = "test8.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);
	    });
		
	}
	
	/*
	 * Fails because of a duplicate outcome tag pass key 
	 */
	@Test
	void test9() throws Exception {
		
		Exception exception = assertThrows(com.synditcorp.ruleengine.exceptions.DuplicateKeyException.class, () -> {
			String fileName = "test9.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);
	    });
		
	}
	
	/*
	 * Fails because of a duplicate outcome tag fail key 
	 */
	@Test
	void test10() throws Exception {
		
		Exception exception = assertThrows(com.synditcorp.ruleengine.exceptions.DuplicateKeyException.class, () -> {
			String fileName = "test10.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);
	    });
		
	}
	
	/*
	 * Rule 19 is set to false, throws NoRuleEvaluatedException
	 */
	@Test
	void test11() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();

		Exception exception = assertThrows(NoRuleEvaluatedException.class, () -> {
			ruleEvaluator.evaluateRule(19);
	    });
		
	}
	
	/*
	 * Rule 20 has expired, throws NoRuleEvaluatedException
	 */
	@Test
	void test12() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();

		Exception exception = assertThrows(NoRuleEvaluatedException.class, () -> {
			ruleEvaluator.evaluateRule(20);
	    });
		
	}
	
	/*
	 * Rule 21 is not effective yet, throws NoRuleEvaluatedException
	 */
	@Test
	void test13() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();

		Exception exception = assertThrows(NoRuleEvaluatedException.class, () -> {
			ruleEvaluator.evaluateRule(21);
	    });
		
	}
	
	/*
	 * Rule 22 this rule will pass because it is active and falls within the date range
	 */
	@Test
	void test14() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();
		
		assertTrue(ruleEvaluator.evaluateRule(22),"Rule 22 should have passed.");
		
	}
	
	/*
	 * Rule 1 should return a score of 10 and a flag with value '1FlagP'
	 */
	@Test
	void test15() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();
		ruleEvaluator.evaluateRule(1);
		Double amount = ruleEvaluator.getNumberOutcome(1,"amount");
		boolean included = ruleEvaluator.getTagOutcome(1, "flag").contains("1FlagP");
		
		
		assertTrue( (amount == 10 && included), "Rule 1 values not returned as expected.");
		
	}
	
	/*
	 * Rule 1 should fail because variables do not exist in collection (in this case, they simply weren't loaded)
	 */
	@Test
	void test16() throws Exception {
		
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
	void test17() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();
		ruleEvaluator.evaluateRule(1);
		boolean correctScore = ruleEvaluator.getNumberOutcome(1,"amount") == 10;
		boolean included = ruleEvaluator.getTagOutcome(1, "flag").contains("1FlagP");
		
		assertTrue( (correctScore && included), "Rule 1 values not returned as expected.");
		
	}
	
	/*
	 * Rule 18 is a ThreadRule and should return an amount of 74.25 and include a flag with value '7FlagP'
	 */
	@Test
	void test18() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();
		ruleEvaluator.setThreadBlockSize(4);
		ruleEvaluator.evaluateRule(18);
		
		boolean correctScore = ruleEvaluator.getNumberOutcome(18,"amount") == 74.25;
		boolean included = ruleEvaluator.getTagOutcome(18,"flag").contains("7FlagP");
		
		assertTrue( (correctScore && included), "Rule 18 values not returned as expected.");
		
	}
	
	/*
	 * Rule 10 fails should return an amount of -530 and include a flag with value '10FlagF'
	 */
	@Test
	void test19() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();
		Boolean result = ruleEvaluator.evaluateRule(10);
		
		boolean correctScore = ruleEvaluator.getNumberOutcome(10,"amount") == -530;
		boolean included = ruleEvaluator.getTagOutcome(10,"flag").contains("10FlagF");
		
		assertTrue( (!result && correctScore && included), "Rule 10 values not returned as expected.");
		
	}
	
	/*
	 * Rule 11 passes and should return an amount of 473 and includes flags with values '10FlagF', 1FlagP, 2FlagP, 3FlagF, 10FlagF, 11ReasonP  
	 */
	@Test
	void test20() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();
		Boolean result = ruleEvaluator.evaluateRule(11);
		
		boolean correctScore = ruleEvaluator.getNumberOutcome(11,"amount") == 473;
		
		String[] tags = {"11FlagP", "1FlagP", "2FlagP", "3FlagF"};
		boolean included = true;
		for (int i = 0; i < tags.length; i++) {
	        if(!ruleEvaluator.getTagOutcome(11,"flag").contains(tags[i])) {
	        	included = false;
	        	break;
	        }
	    }
		
		boolean alsoIncluded = ruleEvaluator.getTagOutcome(11,"reason").contains("11ReasonP");
		
		assertTrue( (result && correctScore && included && alsoIncluded), "Rule 11 values not returned as expected.");
		
	}
	

	
	/*
	 * Rule 10 passes and returns an amount of -7
	 */
	@Test
	void test21() throws Exception {
		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();
		Boolean result = ruleEvaluator.evaluateRule(24);
		
		boolean correctScore = ruleEvaluator.getNumberOutcome(24,"amount") == 46;
		
		assertTrue( (result && correctScore), "Rule 24 values not returned as expected.");
		
	}
	
	/*
	 * Fails because of a duplicate rule number in definition file 
	 */
	@Test
	void test22() throws Exception {
		
		Exception exception = assertThrows(com.synditcorp.ruleengine.exceptions.DuplicateKeyException.class, () -> {
			String fileName = "test11.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);
	    });
		
	}
	
	/*
	 * Fails because of a duplicate rule number in definition file 
	 */
	@Test
	void test23() throws Exception {
		
		Exception exception = assertThrows(com.synditcorp.ruleengine.exceptions.DuplicateKeyException.class, () -> {
			String fileName = "test12.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);
	    });
		
	}
	
	/*
	 * Fails because of a duplicate rule number in definition file 
	 */
	@Test
	void test24() throws Exception {
		
		Exception exception = assertThrows(com.synditcorp.ruleengine.exceptions.DuplicateKeyException.class, () -> {
			String fileName = "test13.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);
	    });
		
	}
	
	/*
	 * Fails because of a duplicate rule number in definition file 
	 */
	@Test
	void test25() throws Exception {
		
		Exception exception = assertThrows(com.synditcorp.ruleengine.exceptions.DuplicateKeyException.class, () -> {
			String fileName = "test14.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);
	    });
		
	}
	
	/*
	 * Fails because of a duplicate rule number in definition file 
	 */
	@Test
	void test26() throws Exception {
		
		Exception exception = assertThrows(com.synditcorp.ruleengine.exceptions.DuplicateKeyException.class, () -> {
			String fileName = "test15.json";
			Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(definitionResource);
			RuleDefinition rules = new RuleDefinition(parser);
	    });
		
	}
	
	/*
	 * Common rule outcome should be in the Variables collection 
	 */
	@Test
	void test27() throws Exception {
		
		String fileName = "commonRuleDefinitions.json";
		Path definitionResource = Paths.get(this.getClass().getResource(fileName).toURI());
		RuleJSONParser parser = new RuleJSONParser();
		parser.loadRules(definitionResource);
		RuleDefinition ruleDefinition = new RuleDefinition(parser);
		RuleEvaluator commonEvaluator = new RuleEvaluator(ruleDefinition);

		
		RuleEvaluator ruleEvaluator = getRuleEvaluator();
		ruleEvaluator.setVariable("commonRuleHandler", commonEvaluator);
		ruleEvaluator.evaluateRule(17);
		
		Double amount = ruleEvaluator.getNumberOutcome(17,"amount");
		boolean included = ruleEvaluator.getTagOutcome(17, "flag").contains("17FlagP");
		
		Double commonAmount = (Double) ruleEvaluator.getVariables().get("COMMON_RULES_1_amount"); 
		
		assertTrue( (amount == 17 && included && commonAmount == 999), "Rule 17 values not returned as expected.");
			
	}
	
	private RuleEvaluator getRuleEvaluator() throws Exception {
		
		if(this.ruleEvaluator == null) {
		
			TreeMap<String, Object> variables = new TreeMap<String, Object>();
			
			Double amount1 = 1.50;
			Double amount2 = 5.00;
			Double amount3 = 7.15;
			Integer score = 4;
			String name1 = "Buggs Bunny";
			String ID = "987654321";
			String phone = "724.555.1027";
	
			variables.put("amount1", amount1);
			variables.put("amount2", amount2);
			variables.put("amount3", amount3);
			variables.put("score", score);
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


