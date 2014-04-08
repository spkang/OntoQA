/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.ontology;

import cn.edu.hit.ir.util.Util;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * The node class of schema graph.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-5
 */

public class SchemaNode {
	
	private Resource resource;
	
	public SchemaNode(Resource resource) {
		setResource(resource);
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
	 * Get the resource.
	 *
	 * @return The resource
	 */
	public Resource getResource() {
		return resource;
	}

	public String toString() {
		String s = resource.toString();
		return Util.lastWord(s);
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof SchemaNode) {
			SchemaNode schemaNode = (SchemaNode)obj;
			return schemaNode.getResource().equals(resource);
		}
		return false;
	}
}
