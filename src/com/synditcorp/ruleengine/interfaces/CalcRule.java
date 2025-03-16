package com.synditcorp.ruleengine.interfaces;

import java.util.ArrayList;

public interface CalcRule extends Rule {

	public void setOutcomes(ArrayList<Outcome> outcomes);
	public ArrayList<Outcome> getOutcomes();

	
}
