/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.interfaces;

import java.util.ArrayList;

import com.synditcorp.ruleengine.exceptions.EngineSafeguardException;

public interface Outcome {
	
//	public void setKey(String key) throws IllegalArgumentException;
	public String getKey();
//	public void setResult(String result) throws IllegalArgumentException;
	public String getResult();
//	public void setType(String type) throws IllegalArgumentException;
	public String getType();
//	public void setGlobal(String global);
	public Boolean getGlobal();
	public void setVariableName(String variableName);
	public String getVariableName();
//	public void setExpression(String expression);
	public String getExpression();
//	public void setCompositeOutcomeRules(ArrayList<Integer> compositeOutcomeRules);
	public ArrayList<Integer> getCompositeOutcomeRules();

}
