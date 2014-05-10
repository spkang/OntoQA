/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.dependency;

/**
 *  ChineseBaseGraph 的图节点
 * 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月10日 
 */
public class GraphNode {
	// the relation between the gov node and dep node 
	public String reln  = null;
	public ChineseWord govNode = null;
	public ChineseWord depNode = null;
	public boolean isDirected = false; // 有向图
	public boolean status = false; // 是否链接，无向图
	
	public GraphNode (String reln, ChineseWord govNode , ChineseWord depNode, boolean isDirected, boolean status ) {
		this.reln = reln;
		this.govNode = govNode;
		this.depNode = depNode;
		this.isDirected = isDirected;
		this.status = status;
	}
	
	public GraphNode () {
		this.reln = null;
		this.govNode = null;
		this.depNode = null;
		this.isDirected = false;
		this.status = false;
	}
	
	public GraphNode (GraphNode other ) {
		this(other.reln, other.govNode, other.depNode, other.isDirected, other.status);
	}

	/**
	 * 根据输入的node节点对graph节点进行设定
	 *
	 * @param 
	 * @return void 
	 */
	public void setGraphNode (GraphNode node ) {
		this.reln = node.reln;
		this.govNode = node.govNode;
		this.depNode = node.depNode;
		this.isDirected = node.isDirected;
		this.status = node.status;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((depNode == null) ? 0 : depNode.hashCode());
		result = prime * result + ((govNode == null) ? 0 : govNode.hashCode());
		result = prime * result + (isDirected ? 1231 : 1237);
		result = prime * result + ((reln == null) ? 0 : reln.hashCode());
		result = prime * result + (status ? 1231 : 1237);
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
		GraphNode other = (GraphNode) obj;
		if (depNode == null) {
			if (other.depNode != null)
				return false;
		} else if (!depNode.equals(other.depNode))
			return false;
		if (govNode == null) {
			if (other.govNode != null)
				return false;
		} else if (!govNode.equals(other.govNode))
			return false;
		if (isDirected != other.isDirected)
			return false;
		if (reln == null) {
			if (other.reln != null)
				return false;
		} else if (!reln.equals(other.reln))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GraphNode [reln=" + reln + ", govNode=" + govNode
				+ ", depNode=" + depNode + ", isDirected=" + isDirected
				+ ", status=" + status + "]";
	}
}
