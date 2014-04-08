/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.graph;

import java.util.List;

/**
 * A graph of best matched entities.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-30
 */

public class MatchedGraph {

	private List<EntityEdge> path;
	
	/**
	 * Creates a new instance of MatchedGraph.
	 */
	public MatchedGraph(List<EntityEdge> path) {
		setPath(path);
	}

	/**
	 * Set the path.
	 *
	 * @param path The path to set
	 */
	public void setPath(List<EntityEdge> path) {
		this.path = path;
	}

	/**
	 * Get the path.
	 *
	 * @return The path
	 */
	public List<EntityEdge> getPath() {
		return path;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{\n");
		for (EntityEdge edge : path) {
			sb.append(edge).append("\n");
			sb.append(edge.getPath()).append("\n");
		}
		sb.append("}\n");
		return sb.toString();
	}
}
