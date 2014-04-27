/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

import java.util.Set;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.ontology.RDFNodeType;
import cn.edu.hit.ir.util.ObjectToSet;

/**
 * A map which maps all the suffixes of the label 
 * to its entity.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-27
 */


/**
 * 修改
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月25日 
 */



public class SuffixToEntitiesMap {
	
	private ObjectToSet<String, Entity> suffixToEntities;
	
	/**
	 * Creates a new instance of SuffixToEntityMap.
	 *
	 */
	public SuffixToEntitiesMap() {
		suffixToEntities = new ObjectToSet<String, Entity>();
	}

	/**
	 * Index a label and its resource.
	 *
	 * @param ontology The ontology of the resource
	 * @param label The label
	 * @param resource The resource
	 */
	public void index(Ontology ontology, String label, Resource resource) {
		String[] tokens = label.split("\\s+");
		StringBuffer sb = new StringBuffer();
		for (int i = tokens.length - 1; i >= 0; --i) {		
			if (i != tokens.length - 1) {
				sb.append(' ');
			}
			sb.append(tokens[i]);
			String suffix = sb.toString();
			RDFNodeType type = ontology.getRDFNodeType(resource);
			Entity entity = new Entity(resource, label, type);
			// Index all the suffixes
			suffixToEntities.addMember(suffix, entity);
		}
	}
	
	/**
	 * Index all the resources with label property in a given ontology.
	 *
	 * @param ontology The ontology
	 */
	public void indexOntology(Ontology ontology) {
		StmtIterator sit = ontology.listStatementsWithLabel();
		while (sit.hasNext()) {
			Statement stmt = sit.next();
			RDFNode node = stmt.getObject();
			if (node instanceof Literal) {			
				String label = ((Literal)node).getString();
				if (label != null) {
					Resource resource = stmt.getSubject();
					index(ontology, label, resource);
				}
			}
		}
	}
	
	/**
	 * Returns the entity set matched for a given suffix.
	 *
	 * @param suffix The suffix
	 * @return The entity set matched for the given suffix
	 */
	public Set<Entity> getEntitySet(String suffix) {
		return suffixToEntities.getSet(suffix);
	}
	
}
