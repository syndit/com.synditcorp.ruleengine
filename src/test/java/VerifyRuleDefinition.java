/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package test.java;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;

import com.synditcorp.ruleengine.logging.MinimalLogger;
import com.synditcorp.ruleengine.logging.RuleLogger;
import com.synditcorp.ruleengine.logging.TimeTrack;
import com.synditcorp.ruleengine.DefaultRuleDefinition;
import com.synditcorp.ruleengine.RuleEvaluator;
import com.synditcorp.ruleengine.parser.RuleJSONParser;

public class VerifyRuleDefinition {

	public static void main(String[] args) {
		
		try {

			String jsonFileName = (String) args[0];
			
			verifyRules(jsonFileName);
			
		} catch (Exception e) {
			System.out.println("RuleEngine exception: " + e );
		}

	}

	private static void verifyRules(String jsonFileName) throws Exception {
		
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
		
		TimeTrack t1 = new TimeTrack();
		
		RuleJSONParser parser = new RuleJSONParser();
		parser.loadRules(jsonFileName);
		
		DefaultRuleDefinition rules = new DefaultRuleDefinition();
		rules.loadRules(parser);
		
		Logger logger = new MinimalLogger(MinimalLogger.ERROR);

		RuleEvaluator eval = new RuleEvaluator(rules, logger);
		eval.setVariables(variables);
		
		System.out.println("Definition: ID " + eval.getDefinitionID() + ", " + eval.getDescription() + ", version " + eval.getVersion());

		RuleLogger.debug(TimeTrack.getElapsedTime(t1) + " milliseconds to load rules");
		
		Integer startRule = eval.getStartRule();
		Integer[] testRules = {startRule};

		for (int j = 0; j < 3; j++) {
			
			//RulesEvaluator's MVEL expressions initialized in first loop
			System.out.println("Loop " + j + "***************************************");
			
			TimeTrack t2 = new TimeTrack();
			
			for (int i = 0; i < testRules.length; i++) {
				try {
					Boolean result = eval.evaluateRule(testRules[i]);
					String successMsg;
					if(result) successMsg = "Verify rules definitions was successful!";
					else successMsg = "Verify rules definitions failed.";
					RuleLogger.debug(successMsg);
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			
			RuleLogger.debug(TimeTrack.getElapsedTime(t2) + " milliseconds to process rules");
			
			for (Map.Entry<String, Object> entry : variables.entrySet()) {
		        System.out.println(entry.getKey() +  " = " + entry.getValue());
			}
			
			eval.reset();
			eval.setVariables(variables);
		}
		
	}



}
