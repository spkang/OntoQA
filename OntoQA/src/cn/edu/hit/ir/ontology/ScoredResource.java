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
 * TODO
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-15
 */

public class ScoredResource implements Comparable<ScoredResource> {
	public Resource resource;
	public double score;
	
	public ScoredResource(Resource resource, double score) {
		this.resource = resource;
		this.score = score;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
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
		ScoredResource other = (ScoredResource) obj;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}




	/**
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ScoredResource o) {
		// TODO Auto-generated method stub
		if (score > o.score)
			return +1;
		else if (score < o.score)
			return -1;
		return 0;		
	}
	
	public String toString() {
		String s = resource.toString();
		return "[" + Util.lastWord(s) + " -> " + score + "]\n";
	}
	
}
