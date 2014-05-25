/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ProbabilityGraph;

import org.jgrapht.graph.DefaultEdge;

import cn.edu.hit.ir.graph.PropertyNode;
import cn.edu.hit.ir.util.Util;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月24日 
 */
public class ProbabilityEdge extends DefaultEdge {

	private static final long serialVersionUID = 1L;
	
	private PropertyNode propertyNode;

	private boolean isReverse = false;
	
	private boolean isAdded = false;
	
	//概率匹配分值
	private double probabilityScore = 1.0;
	


	/**
	 * Set the propertyNode.
	 *
	 * @param propertyNode The propertyNode to set
	 */
	public void setPropertyNode(PropertyNode propertyNode) {
		this.propertyNode = propertyNode;
	}

	/**
	 * Get the propertyNode.
	 *
	 * @return The propertyNode
	 */
	public PropertyNode getPropertyNode() {
		return propertyNode;
	}

	/**
	 * Get the property.
	 *
	 * @return The property
	 */
	public Resource getProperty() {
		return propertyNode.getProperty();
	}


	/**
	 * Get the isReverse.
	 *
	 * @return The isReverse
	 */
	public boolean isReverse() {
		return isReverse;
	}

	/**
	 * Set the isReverse.
	 *
	 * @param isReverse The isReverse to set
	 */
	public void setReverse(boolean isReverse) {
		this.isReverse = isReverse;
	}
	
	public void set(PropertyNode propertyNode, boolean isReverse) {
		setPropertyNode(propertyNode);
		setReverse(isReverse);
	}
	
	public void set(PropertyNode propertyNode) {
		set(propertyNode, false);
	}

	public double getProbabilityScore() {
		return probabilityScore;
	}

	public void setProbabilityScore(double probabilityScore) {
		this.probabilityScore = probabilityScore;
	}
	
	public boolean isAdded() {
		return isAdded;
	}

	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}

	/**
	 * Get the weight.
	 *
	 * @return The weight
	 */
	public double getWeight() {
		return propertyNode.getWeight();
	}

	public String toString() {
		return "(" + Util.lastWord(getSource()) + ", " + propertyNode + 
		", " + Util.lastWord(getTarget()) + ", " + isReverse + ")";
	}
}
