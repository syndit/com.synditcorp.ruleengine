


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import com.synditcorp.ruleengine.RuleDefinition;
import com.synditcorp.ruleengine.RuleEvaluator;
import com.synditcorp.ruleengine.exceptions.InvalidDefinitionException;
import com.synditcorp.ruleengine.parser.RuleJSONParser;

public class DefinitionTests {

	@Test
	void test1() throws Exception {
		
		try {
			
			String fileName = "test1.json";
			String jsonFile = this.getClass().getResource(fileName).toExternalForm();
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules(jsonFile);

		} catch(Exception e) {;
			assertTrue(e instanceof InvalidDefinitionException, "Expecting an InvalidDefinitionException");
		}
		
	}
	
}
