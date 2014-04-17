/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ontologymatch;

import cn.edu.hit.scir.semanticgraph.SemanticEdge;
import cn.edu.hit.scir.semanticgraph.SemanticNode;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月10日 
 */
public class PathNode {
	private Object nodeObject = null; // store the semantic edge or semantic node
	private boolean isEdge = false; // semantic edge 
	
	public PathNode (Object object) {
		if (object instanceof SemanticNode) {
			nodeObject = (SemanticNode) object;
			this.isEdge = false;
		}
		else if (object instanceof SemanticEdge){
			nodeObject = (SemanticEdge) object;
			this.isEdge = true;
		}
		else {
			nodeObject = object;
			this.isEdge = false;
		}
	}
	public Object getNode () {
		if (this.isEdge) {
			return (SemanticEdge) (this.nodeObject);
		}
		else {
			return (SemanticNode) (this.nodeObject);
		}
	}
	
	public boolean isSemanticEdge ( ) {
		return this.isEdge;
	}
	@Override
	public String toString() {
		return "PathNode [nodeObject=" + nodeObject + ", isEdge=" + isEdge
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isEdge ? 1231 : 1237);
		result = prime * result
				+ ((nodeObject == null) ? 0 : nodeObject.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PathNode other = (PathNode) obj;
		if (isEdge != other.isEdge)
			return false;
		if (nodeObject == null) {
			if (other.nodeObject != null)
				return false;
		} else if (!nodeObject.equals(other.nodeObject))
			return false;
		return true;
	}
}
