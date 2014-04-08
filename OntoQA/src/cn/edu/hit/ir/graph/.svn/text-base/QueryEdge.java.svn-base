/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.graph;

import org.jgrapht.graph.DefaultEdge;

import cn.edu.hit.ir.util.Util;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * The edge class of QueryGraph.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-7
 */

public class QueryEdge extends DefaultEdge {

	private static final long serialVersionUID = 1L;
	
	private PropertyNode propertyNode;

	private boolean isReverse = false;
	
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
