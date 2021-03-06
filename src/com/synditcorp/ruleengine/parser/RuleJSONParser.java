/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.parser;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synditcorp.ruleengine.beans.BaseRules;
import com.synditcorp.ruleengine.interfaces.RuleParser;

/**
 * This class implements RulesParser and uses the Jackson JSON parser (com.fasterxml.jackson.core)
 */
public class RuleJSONParser implements RuleParser {

	BaseRules rules;
	
	/**
	 * Pass the JSON file name.  See test.java.verifyRulesDefinitions.json for supported JSON file format.
	 */
	@Override
	public void loadRules(String jsonFileName) throws Exception {

		byte[] jsonData = Files.readAllBytes(Paths.get(jsonFileName));
		ObjectMapper objectMapper = new ObjectMapper();
		
		rules = objectMapper.readValue(jsonData, BaseRules.class);
		
	}

	/**
	 * Use this for getting definitions from resources like MongoDB.  See test.java.verifyRulesDefinitions.json for supported JSON file format.
	 */
	@Override
	public void loadRules(Object... arguments) throws Exception {
		// TODO Auto-generated method stub
	}
	
	@Override
	public BaseRules getRules() throws Exception {
		return rules;
	}


}
