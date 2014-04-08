/**
 * ParseTreeNode.java
 * edu.hit.ir.ontoqa
 * Function:	TODO
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
import java.util.List;

/**
 * ClassName:ParseTreeNode
 * Function: TODO
 *
 * @author   bin3
 * @version  
 * @date	 2011	2011-3-1		上午10:20:53
 */

public class ParseTreeNode {
	public static final Character LEFT_BRACKET = '(';
	public static final Character RIGHT_BRACKET = ')';
	public static final Character SPACE = ' ';
	public static final String ROOT_TAG = "ROOT";
	
	public static final String ILLEGAL_ARGUMENT_MSG = "The format of tree string is illegal.";

	public int beginIndex;
	public String text;
	public String tag;
	public String treeString;
	public List<ParseTreeNode> children;
	public List<ParseTreeNode> baseNPs = new ArrayList<ParseTreeNode>();
	public QueryTripleElement tripleElement = null;
	
	/**
	 * Creates a new instance of ParseTree.ParseTreeNode.
	 *
	 */
	public ParseTreeNode(final int beginIndex, final String treeString) {
		// TODO Auto-generated constructor stub
		this.beginIndex = beginIndex;
		this.treeString = treeString;
		
		int begin = treeString.indexOf(LEFT_BRACKET);
		int end = treeString.lastIndexOf(RIGHT_BRACKET);
		if (begin != -1 && end != -1) {
			int tagBegin = begin;
			while (++tagBegin < treeString.length() 
					&& treeString.charAt(tagBegin) == SPACE) {			
			}
			if (tagBegin >= treeString.length()) {
				throw new IllegalArgumentException(ILLEGAL_ARGUMENT_MSG);
			}
			int tagEnd = treeString.indexOf(SPACE, tagBegin);
			if (tagEnd == -1) {
				throw new IllegalArgumentException(ILLEGAL_ARGUMENT_MSG);
			}
			// Extract the tag
			tag = treeString.substring(tagBegin, tagEnd);
			int bracketBegin = treeString.indexOf(LEFT_BRACKET, tagEnd);
			// If it's leaf node
			if (bracketBegin == -1) {
				if (tagEnd >= end) {
					throw new IllegalArgumentException(ILLEGAL_ARGUMENT_MSG);
				}
				text = treeString.substring(tagEnd, end).trim();
				children = null;
			}
			// If it's parent node
			else {
				children = new ArrayList<ParseTreeNode>();
				StringBuffer textBuffer = new StringBuffer();
				int count = 0;
				for (int i = bracketBegin; i < end; ++i) {
					if (treeString.charAt(i) == LEFT_BRACKET) {
						if (count++ == 0) {
							bracketBegin = i;
						}
					}
					else if(treeString.charAt(i) == RIGHT_BRACKET) {
						if (--count == 0) {
							int childBeginIndex = beginIndex + textBuffer.length();
							String childTreeString = treeString.substring(bracketBegin, i+1);
							ParseTreeNode child = new ParseTreeNode(childBeginIndex, childTreeString);
							children.add(child);
							textBuffer.append(child.text).append(SPACE);				
						}
					}
				}
				text = textBuffer.toString().trim();
			}
		}
		else {
			throw new IllegalArgumentException(ILLEGAL_ARGUMENT_MSG);
		}
	}
	
	public String toString() {
		return treeString;
	}
	
	/**
	 * 是否为叶节点
	 *
	 * @return
	 */
	public boolean isLeaf() {
		return children == null;
	}
	
	/**
	 * 是否为NP节点
	 *
	 * @return
	 */
	public boolean isNP() {
		return ParseTree.isNPTag(tag);
	}
	
	/**
	 * 生成Query三元组元素，暂不生成内部结构
	 * TODO
	 *
	 * @return
	 */
	public QueryTripleElement toQueryTripleElement() {
		if (tripleElement == null) {
			tripleElement = new QueryTripleElement(this, text);			
		}
		return tripleElement;
	}
}