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
 * The node class of QueryGraph.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-7
 */

public class QueryNode {
	
	private static int nextId = 1000000;

	private MatchedEntity entity;
	
	private Resource resource;
	
	private Resource schemaResource;
	
	private String value;
	
	private int id;
	
	private double weight;
	
	public QueryNode(MatchedEntity entity, Resource schemaResource, 
			int id, double weight) {
		setEntity(entity);
		setResource(entity.getResource());
		setSchemaResource(schemaResource);
		setValue(null);
		setId(id);
		setWeight(weight);
	}
	
	public QueryNode(MatchedEntity entity, Resource schemaResource, 
			double weight) {
		this(entity, schemaResource, nextId++, weight);
	}
	
	public QueryNode(Resource resource, Resource schemaResource, 
			int id, double weight) {
		setEntity(null);
		setResource(resource);
		setSchemaResource(schemaResource);
		setValue(null);
		setId(id);
		setWeight(weight);
	}
	
	public QueryNode(Resource resource, double weight) {
		this(resource, resource, nextId++, weight);
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
	 * Get the resource.
	 *
	 * @return The resource
	 */
	public Resource getResource() {
		return resource;
	}

	/**
	 * Set the resource.
	 *
	 * @param resource The resource to set
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Set the schemaResource.
	 *
	 * @param schemaResource The schemaResource to set
	 */
	public void setSchemaResource(Resource schemaResource) {
		this.schemaResource = schemaResource;
	}

	/**
	 * Get the schemaResource.
	 *
	 * @return The schemaResource
	 */
	public Resource getSchemaResource() {
		return schemaResource;
	}

	/**
	 * Set the value.
	 *
	 * @param value The value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Get the value.
	 *
	 * @return The value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Get the id.
	 *
	 * @return The id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the id.
	 *
	 * @param id The id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Set the weight.
	 *
	 * @param weight The weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * Get the weight.
	 *
	 * @return The weight
	 */
	public double getWeight() {
		return weight;
	}
	
	public boolean isLiteralValue() {
		return value != null;
	}

	public int hashCode() {
		return resource.hashCode() + id;
	}

	public boolean equals(Object obj) {
		if (obj instanceof QueryNode) {
			QueryNode other = (QueryNode)obj;
			return  other.getId() == id && other.getResource().equals(resource);
		}
		return false;
	}
	
	public String toString() {
		String s = (entity == null) ? "_" : "";
		s += Util.lastWord(resource) + "_" + id;
		return s;
	}
}
