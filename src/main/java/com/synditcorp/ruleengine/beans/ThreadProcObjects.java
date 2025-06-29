package com.synditcorp.ruleengine.beans;

import java.util.ArrayList;

import com.synditcorp.ruleengine.RuleEvaluator;

public class ThreadProcObjects {

	private Integer threadRuleNumber;
	private RuleEvaluator ruleEvaluator;
	private ArrayList<Integer> block;
	private ArrayList<String> numberKeys;
	private ArrayList<String> tagKeys;
	
	public ThreadProcObjects() {
		
	};

	public Integer getThreadRuleNumber() {
		return threadRuleNumber;
	}

	public void setThreadRuleNumber(Integer threadRuleNumber) {
		this.threadRuleNumber = threadRuleNumber;
	}

	public RuleEvaluator getRuleEvaluator() {
		return ruleEvaluator;
	}

	public void setRuleEvaluator(RuleEvaluator ruleEvaluator) {
		this.ruleEvaluator = ruleEvaluator;
	}

	public ArrayList<Integer> getBlock() {
		return block;
	}

	public void setBlock(ArrayList<Integer> block) {
		this.block = block;
	}

	public ArrayList<String> getNumberKeys() {
		return numberKeys;
	}

	public void setNumberKeys(ArrayList<String> numberKeys) {
		this.numberKeys = numberKeys;
	}

	public ArrayList<String> getTagKeys() {
		return tagKeys;
	}

	public void setTagKeys(ArrayList<String> tagKeys) {
		this.tagKeys = tagKeys;
	}	
	
}
