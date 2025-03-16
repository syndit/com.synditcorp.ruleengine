package com.synditcorp.ruleengine.interfaces;

import java.util.ArrayList;

public interface CompositeOutcome extends Outcome {
	
	public void setCompositeRules(ArrayList<Integer> compositeRules);
	public ArrayList<Integer> getCompositeRules();

}
