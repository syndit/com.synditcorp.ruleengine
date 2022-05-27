/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.handlers;

import java.util.TreeMap;

import static com.synditcorp.ruleengine.logging.RuleLogger.LOGGER;
import com.synditcorp.ruleengine.RuleEvaluator;
import com.synditcorp.ruleengine.exceptions.NoRuleEvaluatedException;
import com.synditcorp.ruleengine.interfaces.RuleClassHandler;

/**
 * This class provides the ability to call another RuleEvaluator instance's rules.  A separate instance is added to the main instance's 
 * variables collection and referenced by its collection variable name.  Any artifacts created at runtime are added to the variables collection 
 * and can be referenced by the main instance's rules.  The variable "instanceName" must be set by the subclass. 
 */
public abstract class InstanceHandler implements RuleClassHandler {

	protected String instanceName;
	
	/**
	 * This method evaluates a rule found in a separate Rule Engine instance that has been added to the main instance's variable collection.
	 * @return A Boolean is returned.
	 * @throws Exception when any exception occurs
	 * @param The ruleExpression variable contains the rule number to call in the nested instance.  The main variables' TreeMap collection contains the 
	 * nested Rule Engine instance.
	 */
	public Boolean processCalcRule(String ruleExpression, TreeMap<String, Object> variables) throws Exception {
        try {
            RuleEvaluator ruleEvaluator = (RuleEvaluator) variables.get(instanceName); 
            variables.remove(instanceName); //remove so don't have recursive collection
            ruleEvaluator.setVariables(variables);
            Integer ruleNumber = Integer.parseInt(ruleExpression); 
            boolean result = ruleEvaluator.evaluateRule(ruleNumber);
            ruleEvaluator.setVariables(null); 
            variables.put(instanceName, ruleEvaluator); //put it back to collection
            return Boolean.valueOf(result);
		} catch (NoRuleEvaluatedException e) {
			 LOGGER.info("Rule number " + ruleExpression + " not evaluated.");
			throw e;
       } catch(Exception e) {
            LOGGER.info("Unable to process expression in InstanceHandler");
            return false;
        }
    }

}
