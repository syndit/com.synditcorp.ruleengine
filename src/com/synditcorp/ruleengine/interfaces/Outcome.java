package com.synditcorp.ruleengine.interfaces;

public interface Outcome {
	
	public void setKey(String key) throws IllegalArgumentException;
	public String getKey();
	public void setResult(String result) throws IllegalArgumentException;
	public String getResult();
	public void setType(String type) throws IllegalArgumentException;
	public String getType();
	public void setGlobal(String global);
	public Boolean getGlobal();
	public void setExpression(String expression);
	public String getExpression();
	

}
