/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.ontology;

import org.jgrapht.graph.DefaultEdge;

import cn.edu.hit.ir.util.Util;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * The edge class of schema graph.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-5
 */

public class SchemaEdge extends DefaultEdge {

	private static final long serialVersionUID = 1L;
	
	private Resource property;
	
	/*private SchemaNode source;
	private SchemaNode target;*/

	/**
	 * Set the property.
	 *
	 * @param property The property to set
	 */
	public void setProperty(Resource property) {
		this.property = property;
	}

	/**
	 * Get the property.
	 *
	 * @return The property
	 */
	public Resource getProperty() {
		return property;
	}

	public String toString() {
		String prop = Util.lastWord(property.toString());
		return "(" + getSource() + " -" + prop + "-> " + getTarget() + ")";
	}

}
