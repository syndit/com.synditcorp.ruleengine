

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Test;

import com.synditcorp.ruleengine.RuleDefinition;
import com.synditcorp.ruleengine.parser.RuleJSONParser;

public class DefinitionTests {

	@Test
	void test1() {
		
		try {
		
			RuleJSONParser parser = new RuleJSONParser();
			parser.loadRules("src/test/java/definitionDocs/test1.json");
			RuleDefinition rules = new RuleDefinition(parser);

		} catch(Exception e) {
			assertEquals("Problem with definition file", e.getMessage());
		}
		
		fail("Exception not thrown");
		
		
	}
	
}
