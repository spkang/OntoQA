/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.scir.semanticgraph;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月30日 
 */
public class SemanticNode {
	private List<DGNode> preModifiers = null; // the modifiers of core word before the core word
	private List<DGNode> postModifiers = null;
	private List<DGNode> coreWords = null; // store core word of this semantic node , e.g. : the largest river, river is the core word, 
	private int index = -1;
	private boolean isQueryNode = false;
	private boolean isBlankNode = false;// used for sign the blank node 
	
	public SemanticNode () {
		preModifiers = new ArrayList<DGNode>();
		postModifiers = new ArrayList<DGNode>();
		coreWords = new ArrayList<DGNode>();
	}
	
	public SemanticNode (List<DGNode> preModifiers, List<DGNode> postModifiers, List<DGNode> coreWords, int index, boolean isQueryNode, boolean isBlankNode)  {
		this.preModifiers = preModifiers;
		this.postModifiers = postModifiers;
		this.coreWords = coreWords;
		this.index = index;
		this.isQueryNode = isQueryNode;
		this.isBlankNode = isBlankNode;
	}
	
	public SemanticNode (List<DGNode> preModifiers, List<DGNode> postModifiers, List<DGNode> coreWords, int index)  {
		this(preModifiers, postModifiers, coreWords, index, false, false);
	}
	
	public SemanticNode (SemanticNode other) {
		this(other.getPreModifiers(), other.getPostModifiers(), other.getCoreWords(), other.getIndex(), other.isQueryNode(), other.isBlankNode());
	}

	public List<DGNode> getPreModifiers() {
		return preModifiers;
	}

	public void setPreModifiers(List<DGNode> preModifiers) {
		this.preModifiers = preModifiers;
	}

	public List<DGNode> getPostModifiers() {
		return postModifiers;
	}

	public void setPostModifiers(List<DGNode> postModifiers) {
		this.postModifiers = postModifiers;
	}

	public List<DGNode> getCoreWords() {
		return coreWords;
	}

	public void setCoreWords(List<DGNode> coreWords) {
		this.coreWords = coreWords;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public boolean equals (Object obj) {
		if (!( obj instanceof SemanticNode) )
			return false;
		SemanticNode sn = (SemanticNode)obj;
		if (this.index == sn.index)
			return true;
		return (this.index == sn.index);
	}
	
	public boolean isQueryNode() {
		return isQueryNode;
	}

	public void setQueryNode(boolean isQueryNode) {
		this.isQueryNode = isQueryNode;
	}

	public boolean isBlankNode() {
		return isBlankNode;
	}

	public void setBlankNode(boolean isBlankNode) {
		this.isBlankNode = isBlankNode;
	}

	public String toString () {
		StringBuffer sb = new StringBuffer ();
		sb.append("[\n").append("preModifiers : " + StringUtils.join(preModifiers, ", ") + "\n");
		sb.append("coreWords : " + StringUtils.join(this.coreWords, ", ") + "\n");
		sb.append("postModifiers : " + StringUtils.join(this.postModifiers, ", ")  + "\n");
		sb.append("index : " + index + "\n");
		sb.append("isQueryNode : " + this.isQueryNode() + "\n");
		sb.append("isBlankNode : " + this.isBlankNode() + "\n");
		sb.append("]\n");
		return sb.toString();
	}
}

