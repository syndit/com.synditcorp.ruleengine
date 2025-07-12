# Syndit Rule Engine

The Syndit Rule Engine is a light-weight, simple rule engine that can be used to implement complex business rules using documents rather than code.


- [Features](#features)
- [Using](#using)
- [Application structure](#application-structure)
- [Rule fields](#rule-fields)
- [JSON document format](#json-document-format)
- [Validation](#validation)
- [Usage tips](#usage-tips)
- [License](#license)


# Features

1. Simple, document based.  The intuitive definition document has the intelligence, not the Java code.  
1. The definition documents are self-contained, making it easy to store a complete set of rules for a given purpose.
    1. Include rules in a Mongo database or locally within a project's artifacts.
1. The Engine's footprint is quite small, allowing as many instances as needed.  
1. Cache definition and evaluator instances for fast rule evaluation.
1. Reusable rules (configure one rule to be used by other rules).
1. Evaluate rules with:
    1. MVEL expressions.
    1. Optional custom Java classes.
    1. Optional APIs.
1. Set rule evaluation outcomes for use in expressions and for reference externally:  
    1. Numbers can be used to calculate and accumulate values, like tree scores or invoice amounts.
    1. Tags are used for such things as i18n keys or other text values that denote a rule's state or outcome, e.g. "passed" or "insufficient funds".
1. Easily allows for multiple tenant implementations:
    1. Separate documents for each client, division, department, etc.
1. Easily control document version
   1. Store in Mongo DB databases and retrieve based on document ID, version, and active flag.
   2. Store local copies in project source code and control using the repository's version control.

# Changes

## Version 3.0.0

The changes in version 3 include

 1. Outcomes have been simplified to allow for greater flexibility, essentially letting the developer designate any outcome tag or outcome number that is needed.
 2. To safeguard runtime manipulation and subsequent integrity of the Engine, rule objects implement the FINAL field keyword.  If a rule definition needs to change, a new rule definition must be loaded.
 3. Performance tuning for much faster runtimes.
 4. The effectiveDate, expirationDate, and active fields have been implemented.

## Version 2.2.0

There are three changes in version 2.2.0

 1. A new rule type was added to support multi-threaded processing.  See Thread rules below for more information.
 1. Uniquely identify document-specific field artifacts using namespaces. 

## Version 2.1.0

There are two changes in version 2.1.0:

 1. Support for empty _compositeRule_ fields to allow rules to be stubbed-out in preparation for future use: Empty _compositeRule_ fields evaluate to null in the Engine.
 1. Support for multiple rule field values, except for passScore and failScore.  Basically, the rule fields types were changed from String to ArrayList<String>. The rule field strings are intended be evaluated in whole, but if pattern matching is needed in an expression, then a target value will need to be referenced by index in the field's array.  
  

# Using

## Parser

Step 1 is to instantiate a parser.  Included is a parser for JSON using the Jackson parser (com.fasterxml.jackson.core).  Any parser can be implemented, for instance for XML, MongoDB, etc., by writing a simple custom class that implements the RuleParser interface and uses your preferred parser.  In this example, a JSON file is parsed.  To reference the JSON document format, see verifyRulesDefinitions.JSON in the test.java folder.  See [JSON document format](#json-document-format) below for more information.

	RuleJSONParser parser = new RuleJSONParser();
	parser.loadRules(jsonDefinitionFileName);

## Definition

In step 2, instantiate a RuleDefinition object and pass the parser in the constructor.

	DefaultRuleDefinition rules = new DefaultRuleDefinition(parser);

## Evaluator

In step 3, instantiate an evaluator and pass the definition in the constructor.

	RuleEvaluator ruleEvaluator = new RuleEvaluator(rules);

## Variables

For optional step 4, load any variables to be used by rule expressions, API handlers, or Java handlers.

	TreeMap<String, Object> variables = new TreeMap<String, Object>();
	Double amount = 11.50;
	variables.put("AMOUNT", amount);
	ruleEvaluator.setVariables(variables);

## Call a rule

Then, call a rule and use the boolean value make a decision in the calling code.

	Integer ruleNumber = 17;
	boolean result = ruleEvaluator.evaluateRule(ruleNumber);
	if(result) doThis();
	else doThat();

And, get whatever you need before discarding the RuleEvaluator instance.

	String i18n = ruleEvaluator.getTagOutcome(ruleNumber, "i18n");
	Double score = ruleEvaluator.getNumberOutcome(ruleNumber, "score");

# Application structure

The Syndit Rule Engine uses "calc" rules, "and" rules, "or" rules, "all" rules, and "thread" rules.

## Calc rules

_Calc_ (calculated) rules are where the expressions are evaluated, APIs called, or Java libraries called.  A _calc_ rule returns a Boolean based on the expression's evaluation, or the API's or Java class' results. A _calc_ rule cannot reference other _calc_ rules.  MVEL is the default expression language.

## Composite rules

Composite rules are used to, effectively, build a decision tree.  Composite rules can reference _calc_ rules and other composite rules.  So, it's a very simple task to support complex business requirements that involve many rules.

### And rules

An _and_ rule is a composite rule that references one or more _calc_ rules, and/or one or more composite rules.  Each of the _calc_ rules referenced directly, or via composite rules, must evaluate to TRUE.  At the first _calc_ rule or composite rule failure, the _and_ rule returns FALSE.  If all referenced rules pass, TRUE is returned.  Rules are evaluated in the order listed in the rule's definition.

### Or rules

Similar to an _and_ rule, an _or_ rule is a composite rule that references one or more _calc_ rules, and/or one or more composite rules.  The difference is that only one rule needs to evaluate to TRUE.  At the first _calc_ rule or composite rule pass, the _or_ rule returns TRUE. If no rules pass, then FALSE is returned.  Rules are evaluated in the order listed in the rule's definition.

### All rules

_All_ composite rules are for when all the rules need to be evaluated.  This type of rule is used when variable artifacts need to be set that other rules rely upon.  _All_ rules always return TRUE.  Rules are evaluated in the order listed in the rule's definition.

### Thread rules

_Thread_ composite rules are for when multi-threaded processing is needed.  The _thread_ rule was created to process very large decision trees, but can be used in any circumstances where runtime is an issue.  Like _all_ rules, all the rules referenced will run regardless of individual rule outcome.  The difference is the rules will be separated into blocks of rules with each block being processed in a different thread.  The default block size is 100, but this value can easily be overridden by calling RuleEvaluator.setThreadBlockSize() method.  Variables set in the threads do not persist after evaluation.  Thread rules return designated outcomes that are accumulated from each runtime thread. Use _thread_ rules when the referenced rules can be processed independently, when processing order does not matter, and when performance is an issue.

### Not rules

_Not_ rules are _calc_ rules or composite rules referenced in a composite rule that need the opposite to be true.  In other words, a _not_ rule is used when something needs to be not true or not false.  For example, if you need a rule that evaluates if a property is not in the state of Florida, reference as a _not_ rule a rule that returns if the property is in Florida.  If the rule returns FALSE, that is the property is not in Florida, and it is being referenced as a _not_ rule, the evaluation will be TRUE that the property is not in Florida.  _Not_ rules are denoted in composite rules with a minus sign before the rule number.

## Work flow

Work flow is essentially doing something based on a set of rules and steps.  Work flow is quite easy to implement in the Syndit Rule Engine.  At the end of a series of rules, merely call Java classes that implement the RuleClassHandler interface to make JDBC database calls, call APIs, etc.  Any transaction initiated in Java that an application needs can be integrated into the Engine.  

# Rule fields

There are 2 required fields:

 1. ruleType - either "calc", "or", "and", "all", or "thread".
 1. ruleNumber - an Integer unique to the particular rule JSON document.
 
 For _calc_ rules, a handlerClass is required.  A handlerClass is a Java class that implements RuleClassHandler and evaluates the MVEL expression, or makes an API or Java class call.  The handlerClass to evaluate MVEL expressions is `com.synditcorp.ruleengine.handlers.ExpressionRuleHandler`.  If a MVEL expression is to be used, then an "expression" field is required.

The optional "description" field holds a meaningful description for the rule. 

The optional "ruleTags" field is a String array useful in further describing a rule.  The ruleTags values are not used to evaluate rules at runtime.  They are intended for such things as authorization in databases or display control in custom document definition editors. 

Use the optional active, effective date, and expiration date fields to control rule availability.  If a rule is not active, or does not meet the date criteria, it is simply ignored by the Engine

Composite rules require a "compositeRules" array field that lists the rule numbers of the rules to be evaluated.  The rules are evaluated in the order they appear in the array.


## Outcomes 

Outcomes can be specified for _calc_, _or_, and _and_ rule types.  Merely include an "outcomes" field in the rule's definition.  Exactly like _calc_ expressions, number outcomes expressions can be single numbers or math expressions that can use global variables.  An outcome can include other rules' outcomes by including a "compositeOutcomeRules" field array with a list of rule numbers.  A negative rule number tells the Engine to retrieve the fail outcome for the included rule.  Number outcomes are summed.  Tag outcomes are accumulated in a String array.  Outcomes are only included if the associated rule has been evaluated at runtime.  After evaluation, outcomes can be accessed by passing the rule number and outcome key to "getTagOutcome" or "getNumberOutcome".  Outcomes flagged as "global" will be added to the Variables collection so they are available to other rules' rule and outcome expressions.  See "Accessing outcome values from other rules at runtime" below. 

	"outcomes": [
			{"result":"pass", "type":"number", "key":"score","expression":"10","global":"true","compositeOutcomeRules":[3,10,5]},
			{"result":"fail", "type":"number", "key":"score","expression":"-5","global":"true","compositeOutcomeRules":[1,2,-3]},
			{"result":"pass", "type":"tag", "key":"flag","expression":"11FlagP","global":"false","compositeOutcomeRules":[1,2,3]},
			{"result":"fail", "type":"tag", "key":"flag","expression":"11FlagF","global":"false","compositeOutcomeRules":[-1,-2,-3]}
		]

### Thread Rule fields

_Thread_ rules process all the rules listed in the "threadRules" field list.  But, because _thread_ rules are processed in separate threads, global outcomes generated in individual threads are not included in the Engine's Variables collection, they are only available within the thread itself.  So, rules referenced by _thread_ rules must be able to be processed independently of other threads.  But, variables in the Variables collection when the _thread_ rule is called are available to the individual threads.  To retrieve outcomes from the threads after processing, use the "numberKeys" and "tagkeys" array fields in the _thread_ rule definition.  These fields list the global outcome keys the Engine should accumulate from the individual threads at runtime.  These accumulated outcomes are included in the Variables collection using the standard outcome variable naming, see "Accessing outcome values from other rules at runtime" below.  For example, to access the "score" number value from the collection for a document ID of "CORE-RULES", reference "CORE-RULES_18_score".  After evaluation, access by calling `ruleEvaluator.getNumberOutcome(18, "score")`.  

	"threadRules":
		[
			{
					"ruleType" : "thread",
					"ruleNumber" : "18",
					"description" : "Run all rules",
					"threadRules" : [1, 2, 4, 11, 23, 56, 35, 55, 64, 98, 114, 123, 134, 138, 155],
					"active" : "true",
					"numberKeys" : ["score"],
					"tagKeys" : ["flag"]
			}
 

## Document definition fields

There are five fields for use in identifying a particular document:

 1. documentId - an ID unique to the particular document.  A documentId is required as it used for variable namespace at runtime, and for this reason no spaces should be used.  It is recommended the documentId value be kept to a minimum so variable names are manageable.  Don't use the documentId for lengthy descriptions.  Instead use the description field when more detail is needed.
 1. description - this is for providing a meaningful description of the rules in the document.
 1. version - always a good idea to version your documents.
 1. active - Set to true or false.  This can be used in document database queries.  For example, select the max version number for active documents.  If a document version is put into production, but needs to be rolled back, merely set the document active flag to false and re-intialize your Engines.
 1. documentTags - document tags are used to further define a document.  Tags can be used for things like authorization in databases or display control in custom rule definition editors.
 1. startRule - for very large decision trees, this holds the value of the base rule of the tree.  It is intended for the developers to retrieve at runtime so they don't have to rely on Jira tickets, emails, text messages, etc. to know the starting base rule to call. 



	"definitionID" : "ORDACC",
	"description" : "New vehicle order accept tree",
	"version" : "1.0.17",
	"active" : true,
	"documentTags" : ["test","partial"],
	"startRule" : "14",

## Accessing outcome values from other rules at runtime

Each of the rule outcome values designated as global can be accessed by other rules in their rule expressions.  Global outcome values are available at runtime only if the associated rule has been evaluated up to the point at runtime: if an outcome hasn't been evaluated yet, it won't be available.  Global outcomes are added to the Variables collection and can be referenced using the documentId, the rule number, and the outcome key.  For example to access an outcome with key "fee" for rule 15 in document "CORE-RULES", use `CORE-RULES_15_fee`.  Number outcomes can be used to calculate expressions, like `CORE-RULES_27_pctFee * (CORE-RULES_17_sumTransAmt / CORE-RULES_6_cumDays)`.  Expressions using can use tag outcomes to check for a particular tag value: `CORE-RULES_34_flag.contains('alert')`.


# JSON document format

Content pending.

For now, see the JSON documents in src/test/java. 

# Calling an API or Java class

If an API or a Java class needs to be used, simply create a new handler that implements the com.synditcorp.ruleengine.interfaces.RuleClassHandler interface and then pass whatever variables the API or class needs.  Then, in the rule definition, reference the new class:


	"calcRules":
		[
			{
				"ruleType" : "calc",
				"ruleNumber" : "117",
				"description" : "Get forecast for today",
				"handlerClass" : "com.yourcompany.handlers.CallWeatherServiceAPI",
				"outcomes": [
					{"result":"pass", "type":"tag", "key":"forecast","expression":"sunny"},
					{"result":"fail", "type":"tag", "key":"forecast","expression":"rain"}
				]
			}


# Validation

The Engine does not prevent mistakes in the definition document, like recursive rules (a rule calling itself, which, by the way, is quite obvious during document definition testing).  There is structure validation, like checking to make sure only calc rules are in the calcRule document array.  But, the code is purposefully kept simple, with the intelligence in the document definition.  It is very easy to perform automated testing, particularly because any rule can be called directly.  So, be sure to create and regularly use test scripts before going to UAT, and most certainly before PROD. 

# Usage tips

## Keep it simple

The Syndit Rule Engine code is intended to be very simple, with the definition document providing the intelligence.  Over the years, the core Engine has not changed.  So, avoid the temptation of coding changes to the Engine's functionality when a little bit of creativity with the definition document or a new handler can provide the solution.  

## Document structure

The Syndit Rule Engine is very flexible.  Because at runtime any rule can be called directly, you can put all your company's rules into one document.  Or, you can organize you rules into multiple documents for use in multiple instances.  Even if you have a decision tree within a document, nothing is stopping you from having other trees or other stand alone rules in the same document. 

### Calling rules in other documents

Rules can reference other document rules.  For instance, rules can be organized into a common rules document and then use-specific rule documents.  To refer to other document rules, write a simple handler that extends InstanceHandler:

	import com.synditcorp.ruleengine.handlers.InstanceHandler;
	
	public class CommonRuleHandler extends InstanceHandler {
	
		public CommonRuleHandler() {
			super.instanceName = "commonRuleHandler";
		}
	}
	
Reference this handler class in main document calc rules.  The "expression" field contains the rule number to evaluate in the common rule document.

	"handlerClass" : "com.yourcompany.handlers.CommonRuleHandler",
	"expression" : "23"

Then, create an instance of the Engine with common rules and add this instance to the main Engine instance's variables collection.  Do not set variables in the instance because the main Engine's variables will replace the instance's variables at runtime.

	myVariablesMap.put("commonRuleHandler", commonInstance);
	mainInstance.setVariables(myVariablesMap);

Keep in mind the documentId field value is used for variable namespace to keep the variable artifact names unique across the common and use-specific documents.

An example of nesting Rule Engine instances can be found in the test.java package.

## Use many instances

The Engine's footprint is quite small.  Use as many implementations of the Engine as needed in your organization.  For instance, one instance can be used in an application for processing complete business rules with another instance in a different application providing simple rules for UI control.

## Performance

### Thread safe

It takes time to parse rule documents, but the rule document object is thread safe, so instances can be stored in application cache.  Rule evaluator instances are not thread safe.

### Expressions

MVEL is the expression language used by the Engine.  At runtime, it takes time for each type of expression to initialize, so if milliseconds are critical to your SLA, keep the rule evaluator instance in memory for a given transaction's processing and, be sure to reset if variables need to be refreshed between calls.  But, rule evaluators are not thread safe so be mindful of this when saving an object instance.  Another technique is to evaluate a rule that does not set global variables as part of a server's startup.  

## Logging

Like any Java process, writing large volumes to log files will affect performance.  So, be sure to set your logger to 'warn' or 'error' before deploying to production.

## Be organized

Being organized is the key to a successful, lasting implementation.  The Rule Engine was written long ago to solve the problem of out-of-control rules in code, so don't over think your rules, particularly because they are quite easy to create.

Also, control is important, so limit definition maintenance to a team knowledgeable in process formulation.  And, prune when possible. 

## CI/CD pipeline

Do integrate the rule definition documents into the CI/CD pipeline.  Automated testing is very easy with the Engine.  And, be sure to put process in place to roll-back in the event it is discovered a new rule is too restrictive or too lax.  It is very easy to roll-back a rule definition document version: the definition documents are self contained.

# License

The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
