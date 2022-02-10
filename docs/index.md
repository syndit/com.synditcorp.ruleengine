- [Features](#features)
- [Using](#using)
- [Application structure](#application-structure)
- [Rule fields](#rule-fields)
- [JSON document format](#json-document-format)
- [Usage tips](#usage-tips)
- [License](#license)

# Features

1. Simple, document based.  The definition document has the intelligence, not the Java code.
1. Reusable rules (configure one rule and use by other rules).
1. Supports expression language.
1. Include rule results in other rule expressions.
1. Caches runtime rule results so reusable rule only needs to be executed once.
1. To evaluate rules:
    1. Use MVEL expressions.
    1. Optional custom Java classes.
    1. Optional APIs.
1. Set and capture rule evaluation values that can be used in expressions and/or called externally for other needs.  
    1. Keys, intended for i18n keys.
    1. Scores, for such things as tree scoring
    1. Flags
    1. Actions
    1. Reasons
1. Easily allows for multiple tenant implementations:
    1. Separate documents for each client, division, department, etc.

# Using

## Parser

Step 1 is to instantiate a parser.  Included is a parser for JSON using the Jackson parser (com.fasterxml.jackson.core).  Any parser can be implemented, for instance for XML, MongoDB, etc., by writing a custom parser that implements the RuleParser interface.  In this example, a JSON file is parsed.  To reference the JSON document format, see verifyRulesDefinitions.JSON in the test.java folder.  See JSON document format below for more information.

	RuleJSONParser parser = new RuleJSONParser();
	parser.loadRules(jsonFileName);

## Definition

In step 2, inject the parser into the RuleDefinition implementation to load the rule objects.

	DefaultRuleDefinition rules = new DefaultRuleDefinition();
	rules.loadRules(parser);

## Logger

In step 3, create a logger that implements org.slf4j.Logger interface.  Here com.synditcorp.ruleengine.logging.MinimalLogger is used.  MinimalLogger is intended to be replaced.

	Logger logger = new MinimalLogger(MinimalLogger.DEBUG);

## Evaluator

In step 4, inject the definitions and the logger into the evaluator.

	RuleEvaluator ruleEvaluator = new RuleEvaluator(rules, logger);

## Variables

For optional step 5, load any variables to be used by the rule expressions, APIs handlers, or Java handlers.

	TreeMap<String, Object> variables = new TreeMap<String, Object>();
	Double amount = 11.50;
	ruleEvaluator.setVariables(variables);

## Call a rule

Then, call a rule.

	Integer ruleNumber = 17;
	Boolean result = ruleEvaluator.evaluateRule(ruleNumber);

And, get whatever you need.

	String passFlag = ruleEvaluator.getPassFlag(ruleNumber);
	Double passScore = ruleEvaluator.getCompositePassScore(ruleNumber);

# Application structure

The rules engine uses "calc" rules, "and" rules, "or" rules, and "all" rules.

## Calc rules

_Calc_ (calculated) rules are where the expressions are evaluated, APIs called, or Java libraries called.  A _calc_ rule returns a Boolean based on the expression's evaluation, or the API's or Java class' results. A _calc_ rule cannot reference other _calc_ rules.

## Composite rules

Composite rules are used to, effectively, build a decision tree using a document.  Composite rules can reference _calc_ rules and other composite rules.  So, it's very simple to support a business requirement that involves as many decisions, or branches, as needed.

### And rules

An _and_ rule is a composite rule that references one or more _calc_ rules, and/or one or more composite rules.  Each of the _calc_ rules referenced directly, or via composite rules, must evaluate to TRUE.  At the first _calc_ rule or other composite rule failure, the _and_ rule returns FALSE.  If all referenced rules pass, TRUE is returned.

### Or rules

Similar to an _and_ rule, an _or_ rule is a composite rule that references one or more _calc_ rules, and/or one or more composite rules.  The difference is that only one rule needs to evaluate to TRUE.  At the first _calc_ rule or composite rule pass, the _or_ rule returns TRUE. If no rules pass, then FALSE is returned.

### All rules

_All_ composite rules are for when all the rules referenced need to be evaluated.  This type of rule is used when variables need to be set that other rules rely upon.  _All_ rules always return TRUE.

### Not rules

_Not_ rules are _calc_ rules or composite rules referenced in a composite rule that need the inverse to be true.  In other words, a _not_ rule is used when something needs to be not true or not false.  For example, if you need a rule that evaluates if a property is not in the state of Florida, reference as a _not_ rule a rule that returns if the property is in Florida.  If the rule returns FALSE, that is the property is not in Florida, and it is being referenced as a _not_ rule, the evaluation will be TRUE.  _Not_ rules are denoted in composite rules with a minus sign before the rule number.

# Rule fields

There are 3 required fields:

 1. ruleType - either "calc", "or", "and", or "all".
 1. ruleNumber - an Integer unique to the particular rule JSON document.
 1. handlerClass - is a Java class that implements RuleClassHandler and evaluates the MVEL expression, or makes an API or Java class call.

If a MVEL expression is to be used, then an "expression" field is required.

The optional "description" field holds a meaningful description for the rule. 

There are currently 10 other optional informational Rule fields that can be used to store values associated with either a passing rule, or a failing rule.  These values can be accessed at runtime for whatever is needed.  For example, if a rule fails and the fail message needs to be presented in the UI, retrieve the rule's unique failKey field value, which is intended to hold an i18n key (but really can hold a value for whatever scheme is being used).  Note that field values are only available if the rule has been evaluated at runtime. The 10 fields are: 

 1. passKey - String, intended to hold an i18n key
 1. failKey - String, intended to hold an i18n key
 1. passScore - Double, intended for scoring a rule on a pass
 1. failScore - Double, intended for scoring a rule on a fail
 1. passFlag -  String, a flag for particular rule's pass
 1. failFlag - String, a flag for particular rule's fail 
 1. passReason - String, use to store a reason for particular rule's pass
 1. failReason - String, use to store a reason for particular rule's fail
 1. passAction -  String, use to store an action for particular rule's pass
 1. failAction -  String, use to store an action for particular rule's fail

If a field is not to be used, set it to null in the JSON document, or just don't include it in the JSON record.

## Composite Rule fields 

The "compositeRules" field is a comma separated list of calc and composite rules to evaluate. 

There are 10 other composite rule fields and these reference calc rule fields, or other composite rule fields, for a given rule number.  Referenced rule fields need not be in the composite field list ("compositeRules" field).  At runtime, the list of composite rule field values can be retrieved, but only for those calc and composite rules that have been evaluated at runtime.  For example, with an _or_ rule, not all rules may have been evaluated.  With the exception of the score fields, all composite fields return a list of field values.  The score fields add the scores from the referenced fields that have been evaluated.  The 10 composite rule fields are:

 1. compositePassKeys - ArrayList<String> 
 1. compositeFailKeys - ArrayList<String>
 1. compositePassScore - Double
 1. compositeFailScore - Double
 1. compositePassFlags -  ArrayList<String>
 1. compositeFailFlags - ArrayList<String> 
 1. compositePassReasons - ArrayList<String>
 1. compositeFailReasons - ArrayList<String>
 1. compositePassActions -  ArrayList<String>
 1. compositeFailActions -  ArrayList<String>

If a composite field is not to be used, set it to an empty array ([]), or don't include it in the record.

## Accessing field values from other rules at runtime

Each of the rule fields values can be accessed by other rules at runtime.  They are added to the variables collection and can be referenced using the field name followed by an underscore and then the rule number.  For example to access the passReasons value for rule 15, use `passReasons_15`.  To access the compositePassActions list, use `compositePassActions_15`.  Scores can be used to calculate expressions, like `passScore_231 * (passScore_17 / compositePassScore_31)`.


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
				"expression" : "amount1 >= 1 && name1.matches('Buggs.*') && ID.matches('.*654.*')",
				"handlerClass" : "com.synditcorp.rulesengine.handlers.DefaultRuleHandler",`
				"active" : "true",
				"effectiveDate" : null,
				"expirationDate" : null,
				"passKey" : "passKey_1",
				"failKey" : "failKey_1",
				"passScore" : "1",
				"failScore" : -1,
				"passFlag" : "1FlagP",
				"failFlag" : "1FlagF",
				"passReason" : null,
				"failReason" : null,
				"passAction" : "1ActionP",
				"failAction" : "1ActionF"			`
			},




Note the field values set to null.

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
				"passKey" : "passKey_10",
				"failKey" : "failKey_10",
				"passScore" : "passScore_27 + 13",
				"failScore" : "passScore_27 - 13",			
				"passFlag" : "10FlagP",
				"failFlag" : "10FlagF",
				"passReason" : "10ReasonP",
				"failReason" : "10ReasonF",
				"passAction" : "10ActionP",
				"failAction" : "10ActionF",		
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



Note the composite field values set to an empty array.

Here is an example of an "or" rule:



	"orRules" : 
		[
			{
				"ruleType" : "or",
				"ruleNumber" : "12",
				"description" : "Rule 12 is true",
				"compositeRules" : [10,11],
				"active" : "true",
				"effectiveDate" : null,
				"expirationDate" : null,
				"passKey" : "passKey_12",
				"failKey" : "failKey_12",
				"passScore" : "12",
				"failScore" : "-12",			
				"passFlag" : "12FlagP",
				"failFlag" : "12FlagF",
				"passReason" : "12ReasonP",
				"failReason" : "12ReasonF",
				"passAction" : null,
				"failAction" : null,		
				"compositePassKeys": [10],
				"compositeFailKeys" : [10],
				"compositePassScores" : [10],
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
				"description" : "Evaluate all these rules",
				"compositeRules" : [31, 32, 33],
				"active" : "true",
				"effectiveDate" : null,
				"expirationDate" : null,
				"passKey" : "passKey_15",
				"passScore" : "",
				"passFlag" : "15FlagP",
				"passReason" : "15ReasonP",
				"passAction" : null,
				"compositePassKeys": [],
				"compositeFailKeys" : [],
				"compositePassScores" : [],
				"compositeFailScores" : [],
				"compositePassFlags" : [31, 32, 33],
				"compositeFailFlags" : [31, 32, 33],
				"compositePassReasons" : [],
				"compositeFailReasons" : [],
				"compositePassActions" : [],
				"compositeFailActions" : []
			},




Note that _all_ rules evaluates all of the rules in the compositeRules field list, therefore an _all_ rule may set both true and false pass and fail field values depending upon the results of each of the rules it calls.  So care should be taken using the _all_ rule field pass and fail values.

# Calling an API or Java classe

If an API or a Java class needs to be used, simply create a new handler that implements the com.synditcorp.ruleengine.interfaces.RuleClassHandler interface and then pass whatever variables the API or class needs.  Then, in the rule definition, reference the new class:


	"calcRules":
		[
			{
				"ruleType" : "calc",
				"ruleNumber" : "1",
				"description" : "Get forecast for today",
				"expression" : null,
				"handlerClass" : "com.yourcompany.handlers.CallWeatherServiceAPI",
				"passFlag" : "Sunny",
				"failFlag" : "Rain",


# Usage tips

The Syndit Rule Engine is intended to be very simple, with the JSON definition document providing the intelligence.  Over the years, the core Engine has not changed, only fields have been added, like passKey and passScore.  So, avoid the temptation of coding new functionality when a little bit of creativity with the definition document or a new handler can provide the solution.    

# License

The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
