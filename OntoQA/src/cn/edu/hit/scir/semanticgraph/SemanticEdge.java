/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.scir.semanticgraph;

import java.util.List;

import edu.stanford.nlp.util.StringUtils;


/**
 * the edge of the semanticGraph 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月30日 
 */
public class SemanticEdge {
	private SemanticNode lhsNode = null; // the left hands word that linked by linkWords
	private SemanticNode rhsNode = null; // the right hands  word that linked by linkWords
	private List<DGNode> linkWords = null; // store the verbs, conjunction，prepositions and so on. 
	private List<DGNode> preEdgeModifiers = null;
	private List<DGNode> postEdgeModifiers = null;
	private boolean isReversed = false;
	private boolean isConnected = false; 
	
	public SemanticEdge() {
		this.lhsNode = null;
		this.rhsNode = null;
		this.linkWords = null;
		this.preEdgeModifiers = null;
		this.postEdgeModifiers = null;
		this.isReversed = false;
		this.isConnected = false;
	}
	
	public SemanticEdge (SemanticNode lhsNode , SemanticNode rhsNode, List<DGNode> linkWords, List<DGNode> preEdgeModifiers, List<DGNode> postEdgeModifiers, boolean isReversed, boolean isConnected ) {
		this.lhsNode = lhsNode;
		this.rhsNode = rhsNode;
		this.linkWords = linkWords;
		this.preEdgeModifiers = preEdgeModifiers;
		this.postEdgeModifiers = postEdgeModifiers;
		this.isReversed = isReversed;
		this.isConnected = false;
	}
	
	public SemanticEdge (SemanticNode lhsNode , SemanticNode rhsNode, List<DGNode> linkWords, List<DGNode> preEdgeModifiers, List<DGNode> postEdgeModifiers,  boolean isReversed)  {
		this(lhsNode, rhsNode, linkWords, preEdgeModifiers, postEdgeModifiers, isReversed, false);
	}
	
	public SemanticEdge (SemanticNode lhsNode, SemanticNode rhsNode, List<DGNode> linkWords, List<DGNode> preEdgeModifiers, List<DGNode> postEdgeModifiers) {
		this(lhsNode, rhsNode, linkWords, preEdgeModifiers, postEdgeModifiers, false, false);
	}
	
	public SemanticEdge (SemanticEdge edge ) {
		this(edge.getLhsNode(), edge.getRhsNode(), edge.getLinkWords(),edge.getPreEdgeModifiers(), edge.getPostEdgeModifiers(),  edge.isReversed(), edge.isConnected());
	}

	public SemanticNode getLhsNode() {
		return lhsNode;
	}

	public void setLhsNode(SemanticNode lhsNode) {
		this.lhsNode = lhsNode;
	}

	public SemanticNode getRhsNode() {
		return rhsNode;
	}

	public void setRhsNode(SemanticNode rhsNode) {
		this.rhsNode = rhsNode;
	}

	public List<DGNode> getLinkWords() {
		return linkWords;
	}

	public void setLinkWords(List<DGNode> linkWords) {
		this.linkWords = linkWords;
	}	
	
	public boolean isReversed() {
		return isReversed;
	}

	public void setReversed(boolean isReversed) {
		this.isReversed = isReversed;
	}
	
	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	
	
	public List<DGNode> getPreEdgeModifiers() {
		return preEdgeModifiers;
	}

	public void setPreEdgeModifiers(List<DGNode> preEdgeModifiers) {
		this.preEdgeModifiers = preEdgeModifiers;
	}

	public List<DGNode> getPostEdgeModifiers() {
		return postEdgeModifiers;
	}

	public void setPostEdgeModifiers(List<DGNode> postEdgeModifiers) {
		this.postEdgeModifiers = postEdgeModifiers;
	}

//	@Override
//	public String toString() {
//		return "SemanticEdge [lhsNode=" + lhsNode + ", rhsNode=" + rhsNode
//				+ ", linkWords=" + linkWords + ", preEdgeModifiers="
//				+ preEdgeModifiers + ", postEdgeModifiers=" + postEdgeModifiers
//				+ ", isReversed=" + isReversed + ", isConnected=" + isConnected
//				+ "]";
//	}
	@Override
	public String toString () {
		StringBuffer bf = new StringBuffer ();
		bf.append("[\n").append("lhsNode : " + this.lhsNode + "\n");
		bf.append("preEdgeModifiers : " + (preEdgeModifiers == null ? "null " : StringUtils.join(preEdgeModifiers, ", "))  + "\n");
		bf.append("linkWords : " + (linkWords == null || linkWords.isEmpty() ? " null " : StringUtils.join( this.linkWords, ", ")) + "\n");
		bf.append("postEdgeModifiers : " + (postEdgeModifiers == null ? "null" : StringUtils.join(postEdgeModifiers, ", ")) + "\n");
		bf.append("\nrhsNode : " + this.rhsNode + "\n");
		bf.append("]");
		return bf.toString();
	}
}
