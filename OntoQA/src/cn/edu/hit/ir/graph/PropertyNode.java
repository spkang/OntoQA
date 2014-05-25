/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.graph;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.util.Util;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * The property node with some useful information.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-8
 */

public class PropertyNode {

	private MatchedEntity entity;
	
	private Resource property;
	
	private double weight;
	
	
	// ProbabilityGraph 消歧时候使用
	private boolean isLegal = false;
	
	
	public PropertyNode(MatchedEntity entity, double weight) {
		setEntity(entity);
		setProperty(entity.getResource());
		setWeight(weight);
		setLegal (false);
	}
	
	public PropertyNode(Resource property, double weight) {
		setEntity(null);
		setProperty(property);
		setWeight(weight);
		setLegal (false);
	}

	/**
	 * Set the entity.
	 *
	 * @param entity The entity to set
	 */
	public void setEntity(MatchedEntity entity) {
		this.entity = entity;
	}

	/**
	 * Get the entity.
	 *
	 * @return The entity
	 */
	public MatchedEntity getEntity() {
		return entity;
	}

	/**
	 * Get the property.
	 *
	 * @return The property
	 */
	public Resource getProperty() {
		return property;
	}

	/**
	 * Set the property.
	 *
	 * @param property The property to set
	 */
	public void setProperty(Resource property) {
		this.property = property;
	}

	/**
	 * Get the weight.
	 *
	 * @return The weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Set the weight.
	 *
	 * @param weight The weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public boolean isLegal() {
		return isLegal;
	}

	public void setLegal(boolean isLegal) {
		this.isLegal = isLegal;
	}

	/**
	 * 判断该node是不是添加的，而不是匹配而来的
	 * spkang add
	 * @param 
	 * @return boolean 
	 */
	public boolean isAdded () {
		return this.entity == null;
	} 
	
	public String toString() {
		return "[" + Util.lastWord(property) + " -> " + (this.entity == null) + "]" ;
	}
}
