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

1. Simple, document based.  The definition document has the intelligence, not the Java code.  
1. The definition documents are self-contained, making it easy to store a complete set of rules for a given purpose.
1. The Engine's footprint is quite small, allowing as many instances as needed.
1. Reusable rules (configure one rule to be used by other rules).
1. Caches runtime rule results so reusable rules only need to be executed once.
1. Evaluate rules with:
    1. MVEL expressions.
    1. Optional custom Java classes.
    1. Optional APIs.
1. Sets rule evaluation values that can be used in expressions and/or called externally for other needs:  
    1. Keys, intended for i18n keys.
    1. Scores, for such things as tree scoring
    1. Flags
    1. Actions
    1. Reasons
1. Easily allows for multiple tenant implementations:
    1. Separate documents for each client, division, department, etc.

# Changes

## Version 2.2.0

There are three changes in version 2.2.0

 1. A new rule type was added to support multi-threaded processing.  See Thread rules below for more information.
 1. Uniquely identify document-specific field artifacts using namespaces. 
 1. Logging was changed to be easier to use.  Now, a logger doesn't need to be injected into the Evaluator.

## Version 2.1.0

There are two changes in version 2.1.0:

 1. Support for empty _compositeRule_ fields to allow rules to be stubbed-out in preparation for future use: Empty _compositeRule_ fields evaluate to null in the Engine.
 1. Support for multiple rule field values, except for passScore and failScore.  Basically, the rule fields types were changed from String to ArrayList<String>. The rule field strings are intended be evaluated in whole, but if pattern matching is needed in an expression, then a target value will need to be referenced by index in the field's array.  
  

# Using

## Parser

Step 1 is to instantiate a parser.  Included is a parser for JSON using the Jackson parser (com.fasterxml.jackson.core).  Any parser can be implemented, for instance for XML, MongoDB, etc., by writing a custom class that implements the RuleParser interface and uses your preferred parser.  In this example, a JSON file is parsed.  To reference the JSON document format, see verifyRulesDefinitions.JSON in the test.java folder.  See [JSON document format](#json-document-format) below for more information.

	RuleJSONParser parser = new RuleJSONParser();
	parser.loadRules(jsonDefinitionFileName);

## Definition

In step 2, inject the parser into a class that implements the RuleDefinition interface to load the rule Java objects.

	DefaultRuleDefinition rules = new DefaultRuleDefinition();
	rules.loadRules(parser);

## Evaluator

In step 3, inject the definitions into the evaluator.

	RuleEvaluator ruleEvaluator = new RuleEvaluator(rules);

## Variables

For optional step 4, load any variables to be used by the rule expressions, API handlers, or Java handlers.

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

	String passFlag = ruleEvaluator.getPassFlag(ruleNumber);
	Double passScore = ruleEvaluator.getCompositePassScore(ruleNumber);

# Application structure

The Syndit Rule Engine uses "calc" rules, "and" rules, "or" rules, "all" rules, and "thread" rules.

## Calc rules

_Calc_ (calculated) rules are where the expressions are evaluated, APIs called, or Java libraries called.  A _calc_ rule returns a Boolean based on the expression's evaluation, or the API's or Java class' results. A _calc_ rule cannot reference other _calc_ rules.  MVEL is the default expression language.

## Composite rules

Composite rules are used to, effectively, build a decision tree using a document.  Composite rules can reference _calc_ rules and other composite rules.  So, it's very simple to support a business requirement that involves as many decisions, or branches, as needed.

### And rules

An _and_ rule is a composite rule that references one or more _calc_ rules, and/or one or more composite rules.  Each of the _calc_ rules referenced directly, or via composite rules, must evaluate to TRUE.  At the first _calc_ rule or composite rule failure, the _and_ rule returns FALSE.  If all referenced rules pass, TRUE is returned.  Rules are evaluated in the order listed in the rule's definition.

### Or rules

Similar to an _and_ rule, an _or_ rule is a composite rule that references one or more _calc_ rules, and/or one or more composite rules.  The difference is that only one rule needs to evaluate to TRUE.  At the first _calc_ rule or composite rule pass, the _or_ rule returns TRUE. If no rules pass, then FALSE is returned.  Rules are evaluated in the order listed in the rule's definition.

### All rules

_All_ composite rules are for when all the rules referenced need to be evaluated.  This type of rule is used when variable artifacts need to be set that other rules rely upon.  _All_ rules always return TRUE.  Rules are evaluated in the order listed in the rule's definition.

### Thread rules

_Thread_ composite rules are for when multi-threaded processing is needed.  The _thread_ rule was created to process very large decision trees, but can be used in any circumstances where runtime is an issue.  Like _all_ rules, all the rules referenced will run regardless of individual rule outcome.  The difference is the rules will be separated into blocks of rules with each block being processed in a different thread.  The default block size is 100, but this value can be overridden by calling RuleEvaluator.setThreadBlockSize() method.  Variables set in the threads do not persist after evaluation.  _Thread_ rules return TRUE if any of the reference rules return TRUE, else FALSE is returned.  Use _thread_ rules when the referenced rules can be processed independently, when processing order does not matter, and when performance is an issue.

### Not rules

_Not_ rules are _calc_ rules or composite rules referenced in a composite rule that need the inverse to be true.  In other words, a _not_ rule is used when something needs to be not true or not false.  For example, if you need a rule that evaluates if a property is not in the state of Florida, reference as a _not_ rule a rule that returns if the property is in Florida.  If the rule returns FALSE, that is the property is not in Florida, and it is being referenced as a _not_ rule, the evaluation will be TRUE that the property is not in Florida.  _Not_ rules are denoted in composite rules with a minus sign before the rule number.

# Rule fields

There are 3 required fields:

 1. ruleType - either "calc", "or", "and", "all", or "thread".
 1. ruleNumber - an Integer unique to the particular rule JSON document.
 1. handlerClass - is a Java class that implements RuleClassHandler and evaluates the MVEL expression, or makes an API or Java class call.  The handlerClass to evaluate MVEL expressions is `com.synditcorp.ruleengine.handlers.ExpressionRuleHandler`.

If a MVEL expression is to be used, then an "expression" field is required.

The optional "description" field holds a meaningful description for the rule. 

The optional "ruleTags" field is a String array useful in further describing a rule.  The ruleTags values are not used to evaluate rules at runtime.  They are intended for such things as authorization in databases or display control in custom document definition editors. 

There are currently 10 other optional rule fields that can be used to store values associated with either a passing rule, or a failing rule.  These values can be accessed at runtime for whatever is needed.  For example, if a rule fails and the fail message needs to be presented in the UI, retrieve the rule's unique failKey field values, which are intended to hold i18n keys (but really can hold values for whatever scheme is being used).  Note that field values are only available if the rule has been evaluated at runtime. The 10 fields are: 

 1. passKeys - ArrayList<String>, intended to hold i18n keys
 1. failKeys - ArrayList<String> , intended to hold i18n keys
 1. passScore - An expression that returns a Double, intended for scoring a rule on a pass.  The expression can be a single value or a complex calculation using other rules' failScore values. 
 1. failScore - An expression that returns a Double, intended for scoring a rule on a fail.  The expression can be a single value or a complex calculation using other rules' failScore values.
 1. passFlags -  ArrayList<String>, flags for a particular rule's pass
 1. failFlags - ArrayList<String>, flags for a particular rule's fail 
 1. passReasons - ArrayList<String>, use to store reasons for a particular rule's pass
 1. failReasons - ArrayList<String>, use to store reasons for a particular rule's fail
 1. passActions -  ArrayList<String>, use to store actions for a particular rule's pass
 1. failActions -  ArrayList<String>, use to store actions for a particular rule's fail

If a field is not to be used, set it to null in the JSON document, or just don't include it in the JSON record.

## Composite Rule fields 

The "compositeRules" field is a comma separated list of calc and composite rules to evaluate.  The Engine evaluates rules in the order listed. 

There are 10 other composite rule fields and these reference calc rule fields, or other composite rule fields, for a given rule number.  Referenced rule fields need not be in the composite field list ("compositeRules" field).  At runtime, the list of composite rule field values can be retrieved, but only for those calc and composite rules that have been evaluated at runtime.  For example, with an _or_ rule, not all rules may have been evaluated.  With the exception of the score fields, all composite fields return a list of field values.  The score fields add the scores from the referenced fields that have been evaluated.  The 10 composite rule fields are:

 1. compositePassKeys - ArrayList<String> 
 1. compositeFailKeys - ArrayList<String>
 1. compositePassScore - Double, sums the passScores for those rules listed.
 1. compositeFailScore - Double, sums the failScores for those rules listed.
 1. compositePassFlags -  ArrayList<String>
 1. compositeFailFlags - ArrayList<String> 
 1. compositePassReasons - ArrayList<String>
 1. compositeFailReasons - ArrayList<String>
 1. compositePassActions -  ArrayList<String>
 1. compositeFailActions -  ArrayList<String>

If a composite field is not to be used, set it to an empty array ([]), or don't include it in the record.

###Thread Rule fields

_Thread_ rules are considered composite rules because they process all the rules listed in the compositeRules field list.  But, _thread_ rules only have the compositePassScore and compositeFailScore composite fields.  The scoring composite field lists cannot be set, but scores accumulated in the threads can be referenced after evaluation.  Also, none of the field values for the rules called by the rules listed in the compositeRules list are available outside the thread in which they are processed.  So, rules referenced by _thread_ rules must be able to be processed independently of other parent thread rules.  But, variable values in the Variables collection when the thread rule is called are available to the thread rules.

## Document definition fields

There are five fields for use in identifying a particular document:

 1. documentId - an ID unique to the particular document.  It is recommended the documentId be set as it is quite useful for identifying various definition documents.  It is also used for field artifact namespace at runtime, and for this reason no spaces should be used.  It is recommended the documentId value be kept to a minimum so variable names are manageable: use the description field when more detail is needed.
 1. description - this is for providing a meaningful description of the rules in the document.
 1. version - always a good idea to version your documents.
 1. documentTags - document tags are used to further define a document.  Tags can be used for things like authorization in databases or display control in custom rule definition editors.
 1. startRule - for decision trees, this holds the value of the base rule of the tree.  It is intended for the developers to retrieve at runtime so you don't have to rely on Jira tickets, emails, text messages, etc. to know the base rule to call. 


	"definitionID" : "ORDACC",
	"description" : "New vehicle order accept tree",
	"version" : "1.0.17",
	"documentTags" : ["test","partial"],
	"startRule" : "14",

## Accessing field artifact values from other rules at runtime

Each of the rule field artifact values can be accessed by other rules at runtime.  Again, field artifacts are available only if the rule has been evaluated at runtime.  They are added to the variables collection and can be referenced using the documentId, the field name, and the rule number.  For example to access the passReason value for rule 15 in document "CORE_RULES_27", use `CORE_RULES_27_passReason_15`.  To access the compositePassActions list, use `CORE_RULES_27_compositePassActions_15`.  Scores can be used to calculate expressions, like `CORE_RULES_27_passScore_231 * (CORE_RULES_27_passScore_17 / CORE_RULES_27_compositePassScore_31)`.

## Fields not yet implemented

The effectiveDate, expirationDate, and active fields are not yet implemented.

# JSON document format

Here is an example of a calc rule:



	"calcRules":
		[
			{
				"ruleType" : "calc",
				"ruleNumber" : "1",
				"description" : "Rule 1 passes",
				"expression" : "amount1 >= 1 && name1.matches('Buggs.*') && CORE_RULES_27_compositePassFlags_23.contains('Sunny')",
				"handlerClass" : "com.synditcorp.ruleengine.handlers.ExpressionRuleHandler",`
				"active" : "true",
				"effectiveDate" : null,
				"expirationDate" : null,
				"passKey" : ["passKey_1"],
				"failKey" : ["failKey_1"],
				"passScore" : "1",
				"failScore" : -1,
				"passFlag" : ["1FlagP"],
				"failFlag" : ["1FlagF"],
				"passReason" : [],
				"failReason" : [],
				"passAction" : ["1ActionP"],
				"failAction" : ["1ActionF"]			`
			},




Note the field values set to null.  Also, note the use of the compositePassFlags ArrayList in the expression.

Here is an example of an "and" rule:



	"andRules" : 
		[
			{
				"ruleType" : "and",
				"ruleNumber" : "10",
				"description" : "Rule 10 returns true",
				"compositeRules" : [1, 2],
				"active" : "true",
				"effectiveDate" : null,
				"expirationDate" : null,
				"passKey" : ["passKey_10"],
				"failKey" : ["failKey_10"],
				"passScore" : "CORE_RULES_27_passScore_27 + 13",
				"failScore" : "CORE_RULES_27_passScore_27 - 13",			
				"passFlag" : ["10FlagP"],
				"failFlag" : ["10FlagF"],
				"passReason" : ["10ReasonP"],
				"failReason" : ["10ReasonF"],
				"passAction" : ["10ActionP"],
				"failAction" : [10ActionF"],		
				"compositePassKeys": [1,2],
				"compositeFailKeys" : [1,2],
				"compositePassScores" : [1,2],
				"compositeFailScores" : [1,2],
				"compositePassFlags" : [1,2],
				"compositeFailFlags" : [1,2],
				"compositePassReasons" : [],
				"compositeFailReasons" : [],
				"compositePassActions" : [1,2],
				"compositeFailActions" : [1,2]
			},



Note the composite field values set to an empty array.  An alternative is to just not include those empty fields in the rule definition.  Also notice the expressions in the passScore and failScore fields.

Here is an example of an "or" rule:



	"orRules" : 
		[
			{
				"ruleType" : "or",
				"ruleNumber" : "12",
				"description" : "Rule 12 is true",
				"compositeRules" : [10,11,15],
				"active" : "true",
				"effectiveDate" : null,
				"expirationDate" : null,
				"passKey" : ["passKey_12"],
				"failKey" : ["failKey_12"],
				"passScore" : "12",
				"failScore" : "-12",			
				"passFlag" : ["12FlagP"],
				"failFlag" : ["12FlagF"],
				"passReason" : ["12ReasonP"],
				"failReason" : ["12ReasonF"],
				"passAction" : [],
				"failAction" : [],		
				"compositePassKeys": [10],
				"compositeFailKeys" : [10],
				"compositePassScores" : [10,32],
				"compositeFailScores" : [10],
				"compositePassFlags" : [],
				"compositeFailFlags" : [],
				"compositePassReasons" : [10],
				"compositeFailReasons" : [10],
				"compositePassActions" : [10],
				"compositeFailActions" : [10]
			},




Note that composite values are only gathered for rule 10, even though this composite rule lists 2 other rules.

Here is an example of an "all" rule:



	"allRules" : 
		[
			{
				"ruleType" : "all",
				"ruleNumber" : "15",
				"ruleTags" : ["protected"],				
				"description" : "Evaluate all these rules",
				"compositeRules" : [31, 32, 33],
				"active" : "true",
				"effectiveDate" : null,
				"expirationDate" : null,
				"passKey" : [],
				"passScore" : null,
				"passFlag" : [],
				"passReason" : [],
				"passAction" : [],
				"compositePassKeys": [],
				"compositeFailKeys": [],
				"compositePassScores" : [31, 32, 33],
				"compositeFailScores" : [31, 32, 33],
				"compositePassFlags" : [],
				"compositeFailFlags" : [],
				"compositePassReasons" : [],
				"compositeFailReasons" : [],
				"compositePassActions" : [],
				"compositeFailActions" : []
			},



Here is an example of a Thread rule:


	"threadRules" : 
		[
			{
				"ruleType" : "thread",
				"ruleNumber" : "24",
				"ruleTags" : ["TREE"],				
				"description" : "Evaluate all these rules",
				"compositeRules" : [52, 74, 18, 93],
				"active" : "true",
				"effectiveDate" : null,
				"expirationDate" : null,
				"passKey" : [],
				"passScore" : null,
				"failScore" : null,
				"passFlag" : [],
				"passReason" : [],
				"passAction" : [],
			},



Note that _thread_ rules evaluate all of the rules in the compositeRules field list in blocks, which are set using the RuleEvaluator.setThreadBlockSize() method.  So, if the block size is set to 1, each of the 4 rules listed in the example would process in a separate thread.

# Calling an API or Java class

If an API or a Java class needs to be used, simply create a new handler that implements the com.synditcorp.ruleengine.interfaces.RuleClassHandler interface and then pass whatever variables the API or class needs.  Then, in the rule definition, reference the new class:


	"calcRules":
		[
			{
				"ruleType" : "calc",
				"ruleNumber" : "117",
				"description" : "Get forecast for today",
				"handlerClass" : "com.yourcompany.handlers.CallWeatherServiceAPI",
				"passFlag" : ["Sunny"],
				"failFlag" : ["Rain"],


# Validation

The Engine does not prevent mistakes in the definition document, like recursive rules (a rule calling itself, which, by the way, is quite obvious during document definition testing).  The code is purposefully kept simple, with the intelligence in the document definition.  It is very easy to perform automated testing, particularly because any rule can be called directly.  So, be sure to create and regularly use test scripts before going to UAT, and most certainly before PROD. 

# Usage tips

## Keep it simple

The Syndit Rule Engine code is intended to be very simple, with the definition document providing the intelligence.  Over the years, the core Engine has not changed, only fields have been added, like passFlag and passScore.  So, avoid the temptation of coding changes to the Engine's functionality when a little bit of creativity with the definition document or a new handler can provide the solution.  

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
	
Reference this handler class in main document calc rules.  The "expression" field contains the rule number in the common rule document.

	"handlerClass" : "com.yourcompany.handlers.CommonRuleHandler",
	"expression" : "23"

Then, create an instance of the Engine with common rules and add this instance to the main Engine instance's variables collection.  

	mainInstance.getVariables().put("commonRuleHandler", commonInstance);

Keep in mind the documentId field value is used for variable namespace to keep the variable artifact names unique across the common and use-specific documents.

An example of nesting Rule Engine instances can be found in the test.java package.

## Use many instances

The Engine's footprint is quite small.  Use as many implementations of the Engine as needed in your organization.  For instance, one instance can be used in an application for processing complete business rules with another instance in a different application providing simple rules for UI control.

## Expressions

MVEL is the expression language used by the Engine (you can change if you want).  At runtime, it takes time for each type of expression to initialize, so if milliseconds are critical to your SLA, keep the RuleEvaluator instance in memory and reset after each request.

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
