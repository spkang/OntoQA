/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.scir.semanticgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	private List<DGNode> semanticUnit = null;
	private DGNode superlativeModifier = null;
	private int index = -1;
	private boolean isQueryNode = false;
	private boolean isBlankNode = false;// used for sign the blank node 
	
	public SemanticNode () {
		preModifiers = new ArrayList<DGNode>();
		postModifiers = new ArrayList<DGNode>();
		coreWords = new ArrayList<DGNode>();
		semanticUnit  = new ArrayList <DGNode>();
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
	
	private void initSuperlativeModifier () {
		if (this.superlativeModifier == null ) {
			if (this.semanticUnit == null )
				this.semanticUnit = this.getSemanticUnit();
			for (DGNode node : this.semanticUnit) {
				if (node.tag.toUpperCase().equals("JJS")) {
					this.superlativeModifier = new DGNode(node);
					break;
				}
				else if (node.tag.toUpperCase().equals("RBS")) {
					this.superlativeModifier = new DGNode(node);
					break;
				}
			}
		}
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
	
	public List<DGNode> getAllDGNodes (List<DGNode> preModifiers, List<DGNode> coreWords, List<DGNode> postModifiers) {
		List<DGNode> tmpList = new ArrayList<DGNode>();
		 if (this.preModifiers != null )
			 tmpList.addAll(this.preModifiers);
		 if (this.coreWords != null )
			 tmpList.addAll (this.coreWords);
		 if (this.postModifiers != null )
			 tmpList.addAll (this.postModifiers);
		 Collections.sort (tmpList, new Comparator () {
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				DGNode lhs = (DGNode) o1;
				DGNode rhs = (DGNode) o2;
				return lhs.idx - rhs.idx;
			} 
		 });
		System.out.println("getAllNodes : " + tmpList);
		return tmpList;
	}
	
	
	public List<DGNode> getSemanticUnit () {
		if (this.semanticUnit == null ) {
			return this.semanticUnit = this.getAllDGNodes(preModifiers, coreWords, postModifiers);
		}
		return this.semanticUnit;
	}
	
	
	public List<String> getWordPhrase (List<DGNode> nodeList) {
		if (nodeList == null )
			return null;
		List<String> phrase = new ArrayList<String> ();
		for (DGNode node  : nodeList ) {
			phrase.add(node.word);
		}
		return phrase;
	}
	
	
	public List<String> getStemPhrase (List<DGNode> nodeList) {
		if (nodeList == null )
			return null;
		List<String> phrase = new ArrayList<String> ();
		for (DGNode node  : nodeList ) {
			phrase.add(node.stem);
		}
		return phrase;
	}
	
	
	
	public boolean isCount () {
		 List<DGNode> tmpList = getSemanticUnit ();
		 String phrase = "";
		 for (DGNode node : tmpList ) {
			 phrase += node.word + " ";
		 }
		 phrase = phrase.trim();
		 if (-1 != phrase.indexOf("how many")) {
			 return true;
		 }
		 return false;
	}

	public DGNode getSuperlativeModifier () {
		return this.superlativeModifier;
	}
	
	public boolean existsSuperModifer (String modifierTag ) {
		if (this.superlativeModifier == null )
			this.initSuperlativeModifier();
		if (modifierTag == null )
			return false;
		if (this.superlativeModifier == null )
			initSuperlativeModifier();
		if (this.superlativeModifier == null)
			return false;
		if (this.superlativeModifier.tag.toUpperCase().equals(modifierTag.toUpperCase()))
			return true;
		return false;
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

