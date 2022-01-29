/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.tree;

import java.util.TreeMap;

public class TestTree {

	public static Node setTree() {
		
		TreeMap<Integer, Node> nodes = new TreeMap<Integer, Node>();

		Node node1 = new Node(1);
		Node node2 = new Node(2);
		Node node3 = new Node(3);
		Node node4 = new Node(4);
		Node node5 = new Node(5);
		Node node8 = new Node(8);
		Node node9 = new Node(9);
		Node node10 = new Node(10);
		Node node11 = new Node(11);
		Node node12 = new Node(12);
		Node node13 = new Node(13);
		Node node14 = new Node(14);
		Node node16 = new Node(16);
		Node node17 = new Node(17);

		node1.setTrueNode(node2);
		node1.setFalseNode(node3);
		node2.setTrueNode(node4);
		node2.setFalseNode(node5);
		node3.setTrueNode(node8);
		node3.setFalseNode(node9);
		node4.setTrueNode(node16);
		node4.setFalseNode(node17);
		node5.setTrueNode(node10);
		node5.setFalseNode(node11);
		node8.setTrueNode(node12);
		node8.setFalseNode(node13);
		node9.setTrueNode(node14);
		
		return node1;
		
	}
	
}
