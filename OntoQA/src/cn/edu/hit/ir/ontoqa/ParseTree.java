/**
 * ParseTree.java
 * edu.hit.ir.ontoqa
 * Function:	对Penn Treebank标注格式的树串解析为树结构
 * Reason:		TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *  0.1.0	2011-3-1 		bin3
 *
 * Copyright (c) 2011, CIR@HIT All Rights Reserved.
*/

package cn.edu.hit.ir.ontoqa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ClassName:ParseTree
 * Function: TODO
 *
 * @author   bin3
 * @version  
 * @date	 2011	2011-3-1		上午08:31:29
 */

public class ParseTree {
	
	static final String[] npTags = {"NP", "WHNP", "WHADJP", "WHADVP"};
	static final Set<String> npTagSet;
	static {
		npTagSet = new HashSet<String>();
		npTagSet.addAll(Arrays.asList(npTags));
	}
	
	public ParseTreeNode root;
	
	/**
	 * Creates a new instance of ParseTree.
	 *
	 */
	public ParseTree() {
		// TODO Auto-generated constructor stub

	}
	
	/**
	 * Creates a new instance of ParseTree.
	 *
	 */
	public ParseTree(String treeString) {
		// TODO Auto-generated constructor stub
		parse(treeString);
	}
	
	public void parse(String treeString) {
		root = new ParseTreeNode(0, treeString);
	}
	
	public String toString() {
		return root.toString();
	}
	
	/**
	 * 深度优先遍历句法树
	 *
	 */
	public void dfs() {
		dfs(root, 0);
	}
	
	private void dfs(ParseTreeNode parent, int depth) {
		for (int i = 0; i < depth; ++i) {
			System.out.print("\t");
		}
		System.out.println(parent.tag + "-" + parent.text + "\t[" + parent.toString() + "]");
		if (parent.children != null) {
			for (ParseTreeNode child: parent.children) {
				dfs(child, depth+1);
			}
		}
	}
	
	public void identifyBaseNP() {
		identifyBaseNP(root);
	}
	
	private List<ParseTreeNode> identifyBaseNP(ParseTreeNode parent) {
		if (parent.baseNPs == null) {
			parent.baseNPs = new ArrayList<ParseTreeNode>();
		}
		if (!parent.isLeaf()) {
			for (ParseTreeNode child: parent.children) {
				List<ParseTreeNode> childBaseNPs = identifyBaseNP(child);
				if (childBaseNPs.size() > 0) {
					parent.baseNPs.addAll(childBaseNPs);
				}
			}
		}
		// 如果子结点无NP且自身为NP，则将自身作为BaseNP
		if (parent.baseNPs.size() == 0 && parent.isNP()) {
			parent.baseNPs.add(parent);
		}
		return parent.baseNPs;
	}
	
	public static boolean isNPTag(String tag) {
		return npTagSet.contains(tag);
	}
}
