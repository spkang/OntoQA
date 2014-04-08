package cn.edu.hit.scir.dependency;

import edu.stanford.nlp.trees.TreeGraphNode;


public class RelationPair {
	public TreeGraphNode gov;
	public TreeGraphNode dep;
	public RelationPair (TreeGraphNode g, TreeGraphNode d) {
		gov = g;
		dep = d;
	}
	
	public boolean equals (RelationPair other ) {
		if (other == null )
			return false;
		if (other.gov.equals(this.gov) && other.dep.equals(this.dep))
			return true;
		return false;
	}
	
	public String toString () {
		StringBuffer sb = new StringBuffer ();
		sb.append("[").append(gov.toString()  + ", " + dep.toString() ).append("]");
		return sb.toString();
	}
}
