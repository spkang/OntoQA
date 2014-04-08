/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.ontology;

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
}
