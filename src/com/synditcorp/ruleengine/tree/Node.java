/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package com.synditcorp.ruleengine.tree;

public class Node {

	public Integer nodeNumber = null;
	public Node trueNode = null;
	public Node falseNode = null;
	
	public Node(Integer nodeNumber) {
		setNode(nodeNumber);
	}
	
	public void setNode(Integer nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	public Integer getNodeNumber() {
		return nodeNumber;
	}

	public void setNodeNumber(Integer nodeNumber) {
		this.nodeNumber = nodeNumber;
	}

	public Node getTrueNode() {
		return trueNode;
	}

	public void setTrueNode(Node trueNode) {
		this.trueNode = trueNode;
	}

	public Node getFalseNode() {
		return falseNode;
	}

	public void setFalseNode(Node falseNode) {
		this.falseNode = falseNode;
	}
	
}
