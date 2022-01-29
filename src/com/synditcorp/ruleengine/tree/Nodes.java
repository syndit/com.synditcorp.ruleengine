/*
The MIT License (MIT)
Copyright © 2021 Syndit Business Solutions, Inc. 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.synditcorp.ruleengine.tree;

import java.util.ArrayList;

import com.synditcorp.ruleengine.logging.RuleLogger;

/**
 * Nodes is a way to map the paths in a decision tree
 */
public class Nodes {

	public Nodes() {
		
	}
	
	/**
	 * Put paths to array list for injecting into rule engine with program 
	 * rootNode is the first Node object in a tree
	 */
	public static ArrayList<ArrayList<Integer>> getPaths(Node rootNode) {
		
		ArrayList<ArrayList<Integer>> paths = new ArrayList<ArrayList<Integer>>();
		printAllPathsToLeaf(rootNode, new int[1000],0, paths, true);
		return paths;
		
	}
	
	private static void printAllPathsToLeaf(Node node, int[] path, int len, ArrayList<ArrayList<Integer>> paths, boolean which) {
	
		if ( node == null ) return;
		
		if(!which) path[len-1] = path[len-1] * -1;

		path[len] = node.getNodeNumber();
        len++;
		 
		if(node.getTrueNode() == null && node.getFalseNode() == null) {
			// leaf node is reached
			paths.add( getPath(path,len) ); 
			return;
		}
		
		printAllPathsToLeaf(node.getTrueNode(), path, len, paths, true);
		printAllPathsToLeaf(node.getFalseNode(), path, len, paths, false);
	
	}

	private static ArrayList<Integer> getPath(int[] path, int len) {

		ArrayList<Integer> list = new ArrayList<Integer>();
		String pathList = "";
		for (int i = 0; i < len; i++) {
				list.add(path[i]);
	            pathList = pathList + " " + path[i];
		}
		RuleLogger.debug("Path: " + pathList);
		
		return list;  
	       
	}
	
}
