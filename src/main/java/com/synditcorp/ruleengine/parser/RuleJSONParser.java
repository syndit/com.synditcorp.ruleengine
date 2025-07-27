/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synditcorp.ruleengine.Rules;
import com.synditcorp.ruleengine.exceptions.DefinitionResourceException;
import com.synditcorp.ruleengine.interfaces.RuleParser;

/**
 * This class implements RuleParser and uses the Jackson JSON parser
 * (com.fasterxml.jackson.core)
 */
public class RuleJSONParser implements RuleParser {

	private Rules rules;

	/**
	 * Loads the JSON definition file.  Pass a String with the path and file name, or a java.nio.file.Path object.  This parser uses the Jackson parser. 
	 * If getting definitions from resources like MongoDB, create a new parser that implements the RuleParser interface.
	 * 
	 * @param agruments java.nio.file.Path object
	 * @param agruments java.lang.String
	 * @throws DefinitionResourceException
	 */
	@Override
	public void loadRules(Object... arguments) throws Exception	{
		
		Object object = arguments[0];
		
		if(object instanceof String) {
			byte[] jsonData = Files.readAllBytes(Paths.get( (String) object ));
			ObjectMapper objectMapper = new ObjectMapper();
			rules = objectMapper.readValue(jsonData, Rules.class);
			return;
		}
		
		if(object instanceof Path) {
			byte[] jsonData = Files.readAllBytes( (Path) object );
			ObjectMapper objectMapper = new ObjectMapper();
			rules = objectMapper.readValue(jsonData, Rules.class);
			return;
		}
		
		throw new DefinitionResourceException("Unable to find definition resource.");
		
	}

	/**
	 * Get the rule definitions that have been parsed into a Rules object
	 * 
	 * @return com.synditcorp.ruleengine.Rules object
	 */
	@Override
	public Rules getRules() throws Exception {
		return rules;
	}

}
