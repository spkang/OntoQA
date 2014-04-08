/**
 * QueryTripleElement.java
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

/**
 * ClassName:QueryTripleElement
 * Function: TODO
 *
 * @author   bin3
 * @version  
 * @date	 2011	2011-3-1		下午12:56:21
 */

public class QueryTripleElement {
	public String text;
	public String head;
	public String preModifier;
	public String postModifier;
	public ParseTreeNode parseTreeNode;
	
	/**
	 * Creates a new instance of QueryTripleElement.
	 *
	 */
	public QueryTripleElement(String text) {
		// TODO Auto-generated constructor stub
		this.text = text;
	}
	
	/**
	 * Creates a new instance of QueryTripleElement.
	 *
	 */
	public QueryTripleElement(ParseTreeNode parseTreeNode, String text) {
		// TODO Auto-generated constructor stub
		this.parseTreeNode = parseTreeNode;
		this.text = text;
	}
	
	public String toString() {
		return text;
	}
	
	public boolean equals(QueryTripleElement element) {
		if (this.parseTreeNode != null && element.parseTreeNode != null) {
			return this.parseTreeNode == element.parseTreeNode;
		}
		else {
			return text.equals(element.text);
		}
	}
}
