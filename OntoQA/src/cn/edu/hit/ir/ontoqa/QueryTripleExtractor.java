/**
 * QueryTripleExtractor.java
 * edu.hit.ir.ontoqa
 * Function:	TODO
 * Reason:		TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *  0.1.0	2011-2-28 		bin3
 *
 * Copyright (c) 2011, CIR@HIT All Rights Reserved.
*/

package cn.edu.hit.ir.ontoqa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ClassName:QueryTripleExtractor
 * Function: TODO
 *
 * @author   bin3
 * @version  
 * @date	 2011	2011-2-28		下午08:11:20
 */

public class QueryTripleExtractor {
	
	ParseTree parseTree;
	
	List<QueryTriple> queryTriples;
	
	/**
	 * Creates a new instance of QueryTripleExtractor.
	 *
	 */
	public QueryTripleExtractor() {
		// TODO Auto-generated constructor stub
		parseTree = new ParseTree();
		queryTriples = new ArrayList<QueryTriple>();
	}
	
	/**
	 * 从句法树串上抽取QueryTriple
	 *
	 * @param treeString
	 * @return
	 */
	public List<QueryTriple> extract(String treeString) {
		parseTree.parse(treeString);
		parseTree.identifyBaseNP();
		
		queryTriples.clear();
		extract(parseTree.root);
		Collections.reverse(queryTriples);
		return queryTriples;
	}
	
	/**
	 * 暂不处理or、and等并列子句的情况
	 * TODO
	 *
	 * @param parent
	 * @return
	 */
	private List<ParseTreeNode> extract(ParseTreeNode parent) {
		List<ParseTreeNode> headNPs = new ArrayList<ParseTreeNode>();
		if (parent.children != null) {
			for (ParseTreeNode child: parent.children) {
				List<ParseTreeNode> childHeadNPs = extract(child);
				if (childHeadNPs.size() > 0) {
					headNPs.add(childHeadNPs.get(0));
				}
			}
		}
		if (headNPs.size() == 0 && parent.baseNPs.size() > 0) {
			headNPs.add(parent.baseNPs.get(0));
		}
		// TODO
		if (headNPs.size() >= 2) {
			ParseTreeNode subjectNode = headNPs.get(0);
			ParseTreeNode objectNode = headNPs.get(1);
			QueryTripleElement subject = subjectNode.toQueryTripleElement();
			QueryTripleElement object = objectNode.toQueryTripleElement();
			
			// 计算谓词元素
			int begin = subjectNode.beginIndex + subjectNode.text.length() - parent.beginIndex;
			int end = objectNode.beginIndex - parent.beginIndex;
			String predicateText = parent.text.substring(begin, end).trim();
			QueryTripleElement predicate = new QueryTripleElement(predicateText);
			
			QueryTriple queryTriple = new QueryTriple(subject, predicate, object);
			queryTriples.add(queryTriple);
			
			// 只保留一个往上传播
			headNPs.clear();
			headNPs.add(subjectNode);
		}
		return headNPs;
	}
	
}
