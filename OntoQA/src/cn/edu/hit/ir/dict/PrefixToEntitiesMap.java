/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.ontology.RDFNodeType;
import cn.edu.hit.ir.util.ObjectToSet;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.stanford.nlp.util.StringUtils;

/**
 * A map which maps all the prefixes of the label 
 * to its entity.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-27
 */

public class PrefixToEntitiesMap {
	
	private ObjectToSet<String, Entity> prefexToEntities;
	int max = -1;
	List<String> labelArray = new ArrayList<String>();
	/**
	 * Creates a new instance of PrefixToEntityMap.
	 *
	 */
	public PrefixToEntitiesMap() {
		prefexToEntities = new ObjectToSet<String, Entity>();
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
		if (tokens.length > max) {
			max = tokens.length;
			if (max > 2)
				labelArray.add(label);
		}
		for (int i = 0; i < tokens.length; i++) {		
			if (i > 0) {
				sb.append(' ');
			}
			sb.append(tokens[i]);
			String prefix = sb.toString();
			RDFNodeType type = ontology.getRDFNodeType(resource);
			Entity entity = new Entity(resource, label, type);
			// Index all the prefixes
			prefexToEntities.addMember(prefix, entity);
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
		System.out.println("maxLabelWordNum : " + max + "\tlabels : " + StringUtils.join(labelArray, ", ") );
		
	}
	
	/**
	 * Returns the entity set matched for a given prefix.
	 *
	 * @param prefix The prefix
	 * @return The entity set matched for the given prefix
	 */
	public Set<Entity> getEntitySet(String prefix) {
		return prefexToEntities.getSet(prefix);
	}
	
}
