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
 * @date	 2011-6-5
 */

public class SchemaEdgeKey {
	private Resource property;
	
	private SchemaNode source;
	private SchemaNode target;
	
	public SchemaEdgeKey(Resource property, SchemaNode source, SchemaNode target) {
		this.property = property;
		this.source = source;
		this.target = target;
	}
	
	public int hashCode() {
		final int MOD = 10000009;
		int code = 0;
		code += property.hashCode() % MOD;
		code += source.hashCode() % MOD;
		code += target.hashCode() % MOD;
		return code;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof SchemaEdgeKey) {
			SchemaEdgeKey key = (SchemaEdgeKey)obj;
			return key.property.equals(property) 
			&& key.source.equals(source) 
			&& key.target.equals(target);
		}
		return false;
	}
	
}
