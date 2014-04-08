/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.graph;

import org.jgrapht.graph.DefaultWeightedEdge;

import cn.edu.hit.ir.ontology.Path;

/**
 * An edge class with weight and a path in EntityGraph.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-30
 */

public class EntityEdge extends DefaultWeightedEdge {
	
	private Path path;

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Set the path.
	 *
	 * @param path The path to set
	 */
	public void setPath(Path path) {
		this.path = path;
	}

	/**
	 * Get the path.
	 *
	 * @return The path
	 */
	public Path getPath() {
		return path;
	}
	
	public String toString() {
		MatchedEntityNode s = (MatchedEntityNode)getSource();
		MatchedEntityNode t = (MatchedEntityNode)getTarget();
		return "(#" + s.getIndex() + " -> #" + t.getIndex() + ": " + getWeight() + ")";
	}
}
