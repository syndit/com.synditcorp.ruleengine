/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package com.synditcorp.ruleengine.beans;

import java.util.ArrayList;
import java.util.TreeMap;

public class ThreadResults {

	private TreeMap<String, ArrayList<String>> tagOutcomes;
	private TreeMap<String, Double> numberOutcomes;

	public TreeMap<String, ArrayList<String>> getTagOutcomes() {
		return tagOutcomes;
	}

	public TreeMap<String, Double> getNumberOutcomes() {
		return numberOutcomes;
	}
	
	public void setNumberOutcome(String key, Double value) {

		if(value == null) return;

		if(this.numberOutcomes == null) {
			this.numberOutcomes = new TreeMap<String, Double>();
			this.numberOutcomes.put(key, value);
			return;
		}

		if(this.numberOutcomes.containsKey(key)) {
			Double sumOf = Double.sum(this.numberOutcomes.get(key), value);
			this.numberOutcomes.put(key, sumOf);
		} else {
			this.numberOutcomes.put(key, value);
		}
		
	}
	
	public void setTagOutcome(String key, ArrayList<String> values) {
		
		if(values == null) return;

		if(this.tagOutcomes == null) {
			this.tagOutcomes = new TreeMap<String, ArrayList<String>>();
			this.tagOutcomes.put(key, values);
			return;
		}
		
		if(this.tagOutcomes.containsKey(key)) {
			this.tagOutcomes.get(key).addAll(values);
		} else {
			this.tagOutcomes.put(key, values);
		}
		
	}

}
