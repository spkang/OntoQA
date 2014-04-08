/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

import java.util.Set;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.ontology.RDFNodeType;
import cn.edu.hit.ir.util.ObjectToSet;

/**
 * A map which maps string to its relevant entity set.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-28
 */

public class StringToEntitiesMap {

	private ObjectToSet<String, Entity> stringToEntities;
	
	/**
	 * Creates a new instance of StringToEntitiesMap.
	 */
	public StringToEntitiesMap() {
		stringToEntities = new ObjectToSet<String, Entity>();
	}
	
	/**
	 * Add a key and its mapped entity.
	 *
	 * @param key the key
	 * @param entity the entity
	 */
	public void add(String key, Entity entity) {
		stringToEntities.addMember(key, entity);
	}
	
	/**
	 * Returns the entity set to which the specified key is mapped, or null 
	 * if this map contains no mapping for the key.
	 *
	 * @param key the key
	 * @return the mapped entity set or <code>null</code> if no mapping
	 */
	public Set<Entity> get(String key) {
		return stringToEntities.get(key);
	}
	
	/**
	 * Returns the entity set mapped by a key, or a empty set if this map 
	 * contains no mapping for the key.
	 *
	 * @param key the key
	 * @return the entity set
	 */
	public Set<Entity> getSet(String key) {
		return stringToEntities.getSet(key);
	}
	
	/**
	 * Index a label and its resource.
	 *
	 * @param ontology The ontology of the resource
	 * @param label The label
	 * @param resource The resource
	 */
	public void index(Ontology ontology, String label, Resource resource) {
		RDFNodeType type = ontology.getRDFNodeType(resource);
		Entity entity = new Entity(resource, label, type);
		stringToEntities.addMember(label, entity);
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
			String label = stmt.getString();
			if (label != null) {			
				Resource resource = stmt.getSubject();
				index(ontology, label, resource);
			}
		}
	}
	
	/**
	 * Returns the entity set matched for a given phrase.
	 *
	 * @param phrase The phrase
	 * @return The entity set matched for the given phrase
	 */
	public Set<Entity> getEntitySet(String phrase) {
		return stringToEntities.getSet(phrase);
	}
}
