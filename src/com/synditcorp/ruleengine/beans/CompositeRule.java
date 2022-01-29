/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.beans;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class CompositeRule extends BaseRule {

	private ArrayList<Integer> compositeRules;
	private ArrayList<Integer> compositePassKeys;
	private ArrayList<Integer> compositeFailKeys;
	private ArrayList<Integer> compositePassScore;
	private ArrayList<Integer> compositeFailScore;
	private ArrayList<Integer> compositePassFlags;
	private ArrayList<Integer> compositeFailFlags;
	private ArrayList<Integer> compositePassReasons;
	private ArrayList<Integer> compositeFailReasons;
	private ArrayList<Integer> compositePassActions;
	private ArrayList<Integer> compositeFailActions;

	
	public CompositeRule() {
		
	}

	public ArrayList<Integer> getCompositePassActions() {
		return compositePassActions;
	}

	public void setCompositePassActions(ArrayList<Integer> compositePassActions) {
		this.compositePassActions = compositePassActions;
	}

	public ArrayList<Integer> getCompositeFailActions() {
		return compositeFailActions;
	}

	public void setCompositeFailActions(ArrayList<Integer> compositeFailActions) {
		this.compositeFailActions = compositeFailActions;
	}

	public ArrayList<Integer> getCompositePassFlags() {
		return compositePassFlags;
	}

	public void setCompositePassFlags(ArrayList<Integer> compositePassFlags) {
		this.compositePassFlags = compositePassFlags;
	}

	public ArrayList<Integer> getCompositeFailFlags() {
		return compositeFailFlags;
	}

	public void setCompositeFailFlags(ArrayList<Integer> compositeFailFlags) {
		this.compositeFailFlags = compositeFailFlags;
	}

	public ArrayList<Integer> getCompositePassReasons() {
		return compositePassReasons;
	}

	public void setCompositePassReasons(ArrayList<Integer> compositePassReasons) {
		this.compositePassReasons = compositePassReasons;
	}

	public ArrayList<Integer> getCompositeFailReasons() {
		return compositeFailReasons;
	}

	public void setCompositeFailReasons(ArrayList<Integer> compositeFailReasons) {
		this.compositeFailReasons = compositeFailReasons;
	}

	public ArrayList<Integer> getCompositeRules() {
		return compositeRules;
	}

	public void setCompositeRules(ArrayList<Integer> compositeRules) {
		this.compositeRules = compositeRules;
	}

	public ArrayList<Integer> getCompositePassKeys() {
		return compositePassKeys;
	}

	public void setCompositePassKeys(ArrayList<Integer> compositePassKeys) {
		this.compositePassKeys = compositePassKeys;
	}

	public ArrayList<Integer> getCompositeFailKeys() {
		return compositeFailKeys;
	}

	public void setCompositeFailKeys(ArrayList<Integer> compositeFailKeys) {
		this.compositeFailKeys = compositeFailKeys;
	}

	public ArrayList<Integer> getCompositePassScore() {
		return compositePassScore;
	}

	public void setCompositePassScore(ArrayList<Integer> compositePassScore) {
		this.compositePassScore = compositePassScore;
	}

	public ArrayList<Integer> getCompositeFailScore() {
		return compositeFailScore;
	}

	public void setCompositeFailScore(ArrayList<Integer> compositeFailScore) {
		this.compositeFailScore = compositeFailScore;
	}

}
