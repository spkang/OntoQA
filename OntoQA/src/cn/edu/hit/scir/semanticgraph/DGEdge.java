/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.scir.semanticgraph;

public class DGEdge {
	public String reln = null;
	public Boolean isDirected = false;
	public DGNode gov;
	public DGNode dep;
	public Boolean status = false;
	public Double weight = 0.0;
	
	/**
	 * contruct a DGEdge 
	 *
	 * @param String , reln, the relation of the edge
	 * @param Boolean , isDirected , it is used for  signing the edge is directed
	 * @param DGNode , gov , the goverment node 
	 * @param DGNode , dep, the dependent node 
	 * @param Boolean, status, the sign represent whether the gov node and dep node are connected.
	 * @param Double, weight , specified the weight of the edge 
	 * @return 
	 */
	public DGEdge ( String reln,  Boolean isDirected, DGNode gov, DGNode dep , Boolean status, Double weight ) {
		this.reln = reln;
		this.isDirected = isDirected;
		this.gov = gov;
		this.dep = dep;
		this.status  = status;
		this.weight = weight;
	}
	
	public DGEdge () {
		this.reln = null;
		this.isDirected = false;
		this.gov = null;
		this.dep = null;
		this.status = false;
		this.weight = 0.0;
	}
	
	public DGEdge ( String reln,  Boolean isDirected, DGNode gov, DGNode dep , Boolean status ) {
		this(reln, isDirected, gov, dep, status, 0.0);
	} 
	
	public DGEdge (DGEdge other) {
		this(other.reln, other.isDirected, other.gov, other.dep, other.status, other.weight);
	}
	
	public boolean equals (DGEdge other ) {
		if (other == null )
			return false;
		return (this.reln.equals(other.reln) && this.isDirected.equals(other.isDirected) && this.gov.equals(other.gov) && this.dep.equals(other.dep) && this.status.equals(other.status));
	} 
	
	public String toString () {
		StringBuffer sb = new StringBuffer ();
		sb.append("[").append(this.reln + ", " + this.isDirected + ", " + this.gov.toString() + ", " + this.dep.toString() + ", " + this.status + ", " + this.weight.toString()).append("]");
		return sb.toString();
	}
}