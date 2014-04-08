/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.graph;

import cn.edu.hit.ir.util.Util;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * A triple of resources which has a reverse flag.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-6
 */

public class ResourceTriple {
	public Resource s;
	public Resource p;
	public Resource o;
	public boolean isReverse;
	
	public ResourceTriple(Resource s, Resource p, Resource o, boolean isReverse) {
		this.s = s;
		this.p = p;
		this.o = o;
		this.isReverse = isReverse;
	}
	
	public ResourceTriple(Resource s, Resource p, Resource o) {
		this(s, p, o, false);
	}
	
	public String toString() {
		return "[" + Util.lastWord(s) + ", " + Util.lastWord(p) + ", " + 
		Util.lastWord(o) + "]";
	}
}
