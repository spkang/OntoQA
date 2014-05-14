/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ChineseEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import cn.edu.hit.ir.dict.MatchedEntitiesSentence;
import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.graph.PropertyNode;
import cn.edu.hit.ir.graph.QueryEdge;
import cn.edu.hit.ir.graph.QueryGraph;
import cn.edu.hit.ir.graph.QueryNode;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.ontology.SchemaGraph;
import cn.edu.hit.ir.util.ConfigUtil;
import cn.edu.hit.ir.util.Util;
import cn.edu.hit.scir.EntityMatcher.QueryMatchedEntityWrapper;


import com.hp.hpl.jena.rdf.model.Resource;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月14日 
 */


public class GenerateChineseGraph {

	public static final String COMPLETE_MATCH_DISTANCE = "score.completeMatchDistance";
	public static final String RESOURCE_TO_RESOURCE_DISTANCE = "score.resourceToResourceDistance";
	public static final String PROPERTY_TO_PROPERTY_DISTANCE = "score.propertyToPropertyDistance";
	public static final String REVERSE_TRIPLE_DISTANCE = "score.reverseTripleDistance";
	public static final String ADDED_RESOURCE_DISTANCE = "score.addedResourceDistance";
	public static final String ADDED_PROPERTY_DISTANCE = "score.addedPropertyDistance";
	public double completeMatchDistance = 1.0;
	public double resourceToResourceDistance = 1.0;
	public double propertyToPropertyDistance = 1.0;
	public double reverseTripleDistance = 1.0;
	public double addedResourceDistance = 1.0;
	public double addedPropertyDistance = 1.0;

	private static Logger logger = Logger.getLogger(GenerateChineseGraph.class);

	private Configuration config;

	private Ontology ontology;
	private SchemaGraph schemaGraph;

	private MatchedEntitiesSentence sentence;
	//private MatchedPath matchedPath;
	private QueryMatchedEntityWrapper entityWrapper = null;

	private QueryGraph queryGraph;
	private List<QueryGraph> graphs;

	public GenerateChineseGraph(Ontology ontology) {
		this.ontology = ontology;
		this.schemaGraph = ontology.getSchemaGraph();
		initConfig();
		initScores();
	}

	private void initConfig() {
		try {
			config = new PropertiesConfiguration(ConfigUtil.getPath(getClass()));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void initScores() {
		completeMatchDistance = config.getDouble(COMPLETE_MATCH_DISTANCE);
		resourceToResourceDistance = config.getDouble(RESOURCE_TO_RESOURCE_DISTANCE);
		propertyToPropertyDistance = config.getDouble(PROPERTY_TO_PROPERTY_DISTANCE);
		reverseTripleDistance = config.getDouble(REVERSE_TRIPLE_DISTANCE);
		addedResourceDistance = config.getDouble(ADDED_RESOURCE_DISTANCE);
		addedPropertyDistance = config.getDouble(ADDED_PROPERTY_DISTANCE);
	}

	private void initQueryGraph(MatchedEntitiesSentence sentence) {
		queryGraph = new QueryGraph(sentence);
		graphs = new ArrayList<QueryGraph>();
	}

	private void initData(MatchedEntitiesSentence sentence) {
		this.sentence = sentence;
	}

//	private void initMatchedPath (MatchedPath matchedPath ) {
//		this.matchedPath = matchedPath;
//	}
	
	private void initMatchedEntityWrapper (QueryMatchedEntityWrapper wrapper ) {
		this.entityWrapper = wrapper;
	}

	/*private QueryEdge pushEdge(QueryNode source, QueryNode target,
			Resource property, boolean isReverse, double distance) {
		queryGraph.addVertex(source);
		queryGraph.addVertex(target);
		QueryEdge edge = queryGraph.addEdge(source, target, property,
				isReverse, distance);
		edges.push(edge);

		if (isReverse) {
			distance += reverseTripleDistance;
		}
		queryGraph.addWeight(distance);
		distances.push(distance);

		return edge;
	}

	private QueryEdge popEdge() {
		QueryEdge edge = edges.pop();
		queryGraph.removeEdge(edge);

		double distance = distances.pop();
		queryGraph.addWeight(-distance);

		return edge;
	}*/

	private QueryEdge pushEdge(QueryNode source, PropertyNode pNode,
			QueryNode target, boolean isReverse) {
		logger.debug("@pushEdge " + source + ", " + pNode + ", " + target + ", " + isReverse);	// debug
		if (isReverse) {
			pNode.setWeight(pNode.getWeight() + reverseTripleDistance);
		}
		return queryGraph.pushEdge(source, pNode, target, isReverse);
	}

	private boolean popEdge() {
		return queryGraph.popEdge();
	}

	private void searchEnding() {
		logger.debug("@searchEnding queryGraph: " + queryGraph);	// debug
		graphs.add((QueryGraph)queryGraph.clone());
	}


	public QueryGraph optionalMatch ( QueryMatchedEntityWrapper entityWrapper) {
		if (entityWrapper == null)
			return null;
		
		this.initMatchedEntityWrapper(entityWrapper);

		if (entityWrapper.getMatchedEntityWrapperSize() == 0) return null;

		initQueryGraph (null);

		// 设置Count操作
		if (entityWrapper.isCount() )
			queryGraph.setCount(true);

		int index = 0;
		logger.debug("@optionalMatch : index " + index);
//		List<MatchedEntity> mes = matchedPath.getPathNodeMap().get(node);
		List<MatchedEntity> mes = entityWrapper.getEntities(index);
		for (MatchedEntity me : mes ) {
			Resource mr = me.getResource();
			double weight = getResourceDistance(me.getDistance());
			logger.debug ("@optionalMatch : me : " + me.toString());
			int nextIndex = entityWrapper.nextIndex(index);
			logger.debug("@optionalMatch : next index " + nextIndex);
			if (me.isProperty()) {
				logger.debug("property me : " + me.toString()); // debug
				PropertyNode pNode = new PropertyNode(me, weight);
				searchResource(pNode, nextIndex);
			} else if (me.isClass() || me.isInstance()) {
				logger.debug("class or instance me : " + me.toString()); // debug
				Resource sr = schemaGraph.getSchemaResource(mr);
				QueryNode source = new QueryNode(me, sr, 0, weight);
				queryGraph.setSource(source);
				searchProperty(source, nextIndex);
			}
		}
		Collections.sort(graphs);
		QueryGraph queryGraph = graphs.size() > 0 ? graphs.get(0) : null;
		return queryGraph;
	}
	
	

	public QueryGraph bestMatch(MatchedEntitiesSentence sentence) {
		if (sentence == null) {
			return null;
		}

		initData(sentence);

		if (sentence.nextIndex(0) == -1) return null;

		initQueryGraph(sentence);

		int begin = sentence.nextIndex(0);

		// determine whether the query require COUNT operator
		String sent = sentence.getSentence().toLowerCase();
		int cntIdx = sent.indexOf("how many");
		if (cntIdx != -1 && cntIdx < begin) {
			queryGraph.setCount(true);
		}

		List<MatchedEntity> mes = sentence.getEntities(begin);
		for (MatchedEntity me : mes) {
			Resource mr = me.getResource();
			double weight = getResourceDistance(me.getDistance());
			int nextIndex = me.getEnd() + 1;

			if (me.isProperty()) {
//				logger.debug("property me : " + me.toString()); // debug
				PropertyNode pNode = new PropertyNode(me, weight);
				searchResource(pNode, nextIndex);
			} else if (me.isClass() || me.isInstance()) {
//				logger.debug("class or instance me : " + me.toString()); // debug
				Resource sr = schemaGraph.getSchemaResource(mr);
				QueryNode source = new QueryNode(me, sr, 0, weight);
				queryGraph.setSource(source);
				searchProperty(source, nextIndex);
			}
		}

		Collections.sort(graphs);
		QueryGraph queryGraph = graphs.size() > 0 ? graphs.get(0) : null;
		return queryGraph;
	}

	public double getResourceDistance(double matchingDistance) {
		return addedResourceDistance * matchingDistance;
	}

	/**
	 * Searches resources for a given property if the property is at
	 * the beginning of all the entities.
	 *
	 * @param pNode the property node
	 * @param index the index of the property
	 */
	private void searchResource(PropertyNode pNode, int index) {
		if (!this.entityWrapper.hasNextIndex(index)) {
			return;
		}
		Resource p = pNode.getProperty();
		logger.debug("@searchResource\t" + p + ", " + index);	// debug

		Set<Resource> subjects = schemaGraph.getSubjectSet(p);
		Set<Resource> objects = schemaGraph.getObjectSet(p);
		if (subjects == null && objects == null) return;
		logger.debug("subjects of p : " + subjects.toString());
		logger.debug("objects  of p : " + objects.toString());
		//index = sentence.nextIndex(index);
		//index = index + 1;
		//index = entityWrapper.nextIndex(index);
		logger.debug("@searchResource index : " + index);
		//List<MatchedEntity> mes = sentence.getEntities(index);
//		List<MatchedEntity> mes = matchedPath.getPathNodeMap().get(matchedPath.getPathNode(index));
		List<MatchedEntity> mes = entityWrapper.getEntities(index);
		for (MatchedEntity me : mes) {
			Resource mr = me.getResource();
			double meWeight = getResourceDistance(me.getDistance());
			logger.debug("match entity me: " + me);
//			int nextIndex = me.getEnd() + 1;
//			int nextIndex = index + 1;
			int nextIndex = entityWrapper.nextIndex(index);
			logger.debug("@searchResource nextIndex : " + nextIndex);
			if (me.isProperty()) {
				Resource p2 = mr;
				PropertyNode pNode2 = new PropertyNode(me, meWeight);
				Set<Resource> inters = null;
				logger.debug("me is property , and p2 = " + p2.toString());
				logger.debug("p and p2 subjsubj schemaGraph set : " + schemaGraph.getSubjobjSet(p,  p2));
				// 如果有resource是p的subject,且是p2的subject
				inters = Util.intersect(subjects,
						schemaGraph.getSubjsubjSet(p, p2));
				if (!inters.isEmpty()) {
					for (Resource s : inters) {
						Set<Resource> os = schemaGraph.getObjectSet(s, p);
						if (os != null) {
							QueryNode target = new QueryNode(s, addedResourceDistance);
							for (Resource o : os) {
								QueryNode source = new QueryNode(o, addedResourceDistance);
								queryGraph.setSource(source);
								// TODO
								//pushEdge(source, target, p, true, pWeight);
								queryGraph.pushEdge(source, pNode, target, true);
								searchObject(target, pNode2, nextIndex);
								popEdge();
							}
						}

					}
				}

				// 如果有resource是p的subject,且是p2的object
				inters = Util.intersect(subjects,
						schemaGraph.getSubjobjSet(p, p2));
				if (!inters.isEmpty()) {
					for (Resource s : inters) {
						Set<Resource> os = schemaGraph.getObjectSet(s, p);
						if (os != null) {
							QueryNode target = new QueryNode(s, addedResourceDistance);
							for (Resource o : os) {
								QueryNode source = new QueryNode(o, addedResourceDistance);
								queryGraph.setSource(source);
								pushEdge(source, pNode, target, true);
								searchSubject(target, pNode2, nextIndex);
								popEdge();
							}
						}

					}
				}

				// 如果有resource是p的object,且是p2的subject
				inters = Util.intersect(objects,
						schemaGraph.getSubjobjSet(p2, p));
				if (!inters.isEmpty()) {
					for (Resource o : inters) {
						Set<Resource> ss = schemaGraph.getSubjectSet(p, o);
						if (ss != null) {
							QueryNode target = new QueryNode(o, addedResourceDistance);
							for (Resource s : ss) {
								QueryNode source = new QueryNode(s, addedResourceDistance);
								queryGraph.setSource(source);
								pushEdge(source, pNode, target, false);
								searchObject(target, pNode2, nextIndex);
								popEdge();
							}
						}

					}
				}

				// 如果有resource是p的object,且是p2的object
				inters = Util.intersect(subjects,
						schemaGraph.getObjobjSet(p, p2));
				if (!inters.isEmpty()) {
					for (Resource o : inters) {
						Set<Resource> ss = schemaGraph.getSubjectSet(p, o);
						if (ss != null) {
							QueryNode target = new QueryNode(o, addedResourceDistance);
							for (Resource s : ss) {
								QueryNode source = new QueryNode(s, addedResourceDistance);
								queryGraph.setSource(source);
								pushEdge(source, pNode, target, false);
								searchSubject(target, pNode2, nextIndex);
								popEdge();
							}
						}

					}
				}

			} else if (me.isClass() || me.isInstance()) {
				Resource r = schemaGraph.getSchemaResource(mr);
				QueryNode target = new QueryNode(me, r, index, meWeight);

				// if a subject is found
				if (subjects.contains(r)) {
					Set<Resource> os = schemaGraph.getObjectSet(r, p);
					if (os != null) {
						for (Resource o : os) {
							QueryNode source = new QueryNode(o, addedResourceDistance);
							queryGraph.setSource(source);
							pushEdge(source, pNode, target, true);
							searchProperty(target, nextIndex);
							popEdge();
						}
					}
				}
				// if an object is found
				if (objects.contains(r)) {
					Set<Resource> ss = schemaGraph.getSubjectSet(p, r);
					if (ss != null) {
						for (Resource s : ss) {
							QueryNode source = new QueryNode(s, addedResourceDistance);
							queryGraph.setSource(source);
							pushEdge(source, pNode, target, false);
							searchProperty(target, nextIndex);
							popEdge();
						}
					}
				}
			}
		}
	}

	/**
	 * Searches properties for a given resource which can be subject or object.
	 *
	 * @param org the original resource of the given resource
	 * @param r	the resource
	 * @param index the search position in matched entities
	 */
	private void searchProperty(QueryNode source, int index) {
		if (!this.entityWrapper.hasNextIndex(index)) {
			searchEnding();
			return;
		}
		Resource r = source.getSchemaResource();
		logger.debug("@searchProperty\t" + r + ", " + index);	// debug

		//index = sentence.nextIndex(index);
		//index = this.entityWrapper.nextIndex(index);
		logger.debug("@searchProperty\t index : " + index);	// debug
		//List<MatchedEntity> mes = sentence.getEntities(index);
//		List<MatchedEntity> mes = matchedPath.getPathNodeMap().get(this.matchedPath.getPathNode(index));
		List<MatchedEntity> mes = this.entityWrapper.getEntities(index);
		for (MatchedEntity me : mes) {
			Resource mr = me.getResource();
			double meWeight = getResourceDistance(me.getDistance());
			//int nextIndex = me.getEnd() + 1;
			int nextIndex  = index + 1;
			logger.debug("@searchProperty\t me : " + me.toString() + "\tnextIndex : " + nextIndex);	// debug
			if (me.isProperty()) {
				Resource p = mr;
				PropertyNode pNode = new PropertyNode(me, meWeight);
				Set<Resource> subjects = schemaGraph.getSubjectSet(p, r);
				logger.debug("@searchProperty\t subjects : " + subjects);
				if (subjects != null) {
					searchSubject(source, pNode, nextIndex);
				}
				Set<Resource> objects = schemaGraph.getObjectSet(r, p);
				logger.debug("@searchProperty\t objects : " + objects);
				if (objects != null) {
					searchObject(source, pNode, nextIndex);
				}
			} else if (me.isClass() || me.isInstance()) {
				Resource r2 = schemaGraph.getSchemaResource(mr);
				QueryNode target = new QueryNode(me, r2, index, meWeight);
				Set<Resource> properties = null;
				
				// if r is subject and r2 is object
				properties = schemaGraph.getPropertySet(r, r2);
				if (properties != null) {
					for (Resource p : properties) {
						PropertyNode pNode = new PropertyNode(p, addedPropertyDistance);
						pushEdge(source, pNode, target, false);
						searchProperty(target, nextIndex);
						popEdge();
					}
				}

				// if r2 is subject and r is object
				properties = schemaGraph.getPropertySet(r2, r);
				if (properties != null) {
					for (Resource p : properties) {
						PropertyNode pNode = new PropertyNode(p, addedPropertyDistance);
						pushEdge(source, pNode, target, true);
						searchProperty(target, nextIndex);
						popEdge();
					}
				}

				searchProperty(source, target, nextIndex);
			}
		}
	}

	/**
	 * Searches whether there is a property behind target which can be used
	 * to make a triple <target, property, source>.
	 * <p>
	 * Handles questions like "Which states do Colorado river run through?".
	 *
	 * @param source
	 * @param target
	 * @param index
	 */
	private void searchProperty(QueryNode source, QueryNode target, int index) {
		if (!this.entityWrapper.hasNextIndex(index)) {
			//searchEnding();
			return;
		}
		Resource src = source.getSchemaResource();
		Resource tgt = target.getSchemaResource();
		logger.debug("@searchProperty111\t" + src + ", " + tgt + ", " + index);

		//Set<Resource> s2tSet = schemaGraph.getPropertySet(src, tgt);
		Set<Resource> t2sSet = schemaGraph.getPropertySet(tgt, src);
		if (t2sSet == null) return;

//		index = sentence.nextIndex(index);
//		index = index + 1;
		//index = entityWrapper.nextIndex(index);
		logger.debug("@searchProperty1111\t index : "  + index);
//		List<MatchedEntity> mes = sentence.getEntities(index);
//		List<MatchedEntity> mes = this.matchedPath.getPathNodeMap().get(this.matchedPath.getPathNode (index));
		List<MatchedEntity> mes = this.entityWrapper.getEntities(index);
		for (MatchedEntity me : mes) {
			double meWeight = getResourceDistance(me.getDistance());
//			int nextIndex = me.getEnd() + 1;
			int nextIndex = index + 1;
			logger.debug("@searchProperty111\t nextIndex : "  + nextIndex);
			if (me.isProperty()) {
				Resource p = me.getResource();
				if (t2sSet.contains(p)) {
					PropertyNode pNode = new PropertyNode(me, meWeight);
					pushEdge(source, pNode, target, true);
					searchProperty(target, nextIndex);
					popEdge();
				}
			}
		}
	}

	/**
	 * Searches subject for a given property and object.
	 *
	 * @param source the query node of the object
	 * @param p the property
	 * @param o the object
	 * @param index the search position in matched entities
	 */
	private void searchSubject(QueryNode source, PropertyNode pNode, int index) {
		if (!this.entityWrapper.hasNextIndex(index)) {
			searchEnding();
			return;
		}
		Resource o = source.getSchemaResource();
		Resource p = pNode.getProperty();
		logger.debug("@searchSubject\t" + index + ", <?, " + p + ", " + o + ">");	// debug

		Set<Resource> subjects = schemaGraph.getSubjectSet(p, o);
		if (subjects == null) return;

//		index = sentence.nextIndex(index);
//		index = index + 1;
		//index = this.entityWrapper.nextIndex(index);
		logger.debug("@searchSubject\t index : " + index );	// debug
//		List<MatchedEntity> mes = sentence.getEntities(index);
//		List<MatchedEntity> mes = this.matchedPath.getPathNodeMap().get(this.matchedPath.getPathNode(index));
		List<MatchedEntity> mes = this.entityWrapper.getEntities(index);
		for (MatchedEntity me : mes) {
			Resource mr = me.getResource();
			double meWeight = getResourceDistance(me.getDistance());

//			int nextIndex = me.getEnd() + 1;
			int nextIndex = index + 1;
			logger.debug("@searchSubject\t nextIndex  :" + nextIndex );	// debug
			if (me.isProperty()) {
				Resource p2 = mr;
				PropertyNode pNode2 = new PropertyNode(me, meWeight);
				Set<Resource> inters = null;

				// 如果有resource是p,o的subject,且是p2的subject
				inters = Util.intersect(subjects,
						schemaGraph.getSubjsubjSet(p, p2));
				if (!inters.isEmpty()) {
					for (Resource s : inters) {
						QueryNode target = new QueryNode(s, addedResourceDistance);
						pushEdge(source, pNode, target, true);
						searchObject(target, pNode2, nextIndex);
						popEdge();
					}
				}

				// 如果有resource是p,o的subject,且是p2的object
				inters = Util.intersect(subjects,
						schemaGraph.getSubjobjSet(p, p2));
				if (!inters.isEmpty()) {
					for (Resource s : inters) {
						QueryNode target = new QueryNode(s, addedResourceDistance);
						pushEdge(source, pNode, target, true);
						searchSubject(target, pNode2, nextIndex);
						popEdge();
					}
				}

			} else if (me.isClass() || me.isInstance()) {
				Resource s = schemaGraph.getSchemaResource(mr);
				QueryNode target = new QueryNode(me, s, index, meWeight);
				// if a subject is found
				if (subjects.contains(s)) {
					pushEdge(source, pNode, target, true);
					searchProperty(target, nextIndex);
					popEdge();
				}
			}
		}
	}

	/**
	 * Searches objects for a given property and subject.
	 *
	 * @param source the query node of the subject
	 * @param p the property
	 * @param o the object
	 * @param index the search position in matched entities
	 */
	private void searchObject(QueryNode source, PropertyNode pNode, int index) {
		if (!this.entityWrapper.hasNextIndex(index)) {
			Resource subject = source.getResource();
			Resource property = pNode.getProperty();
			// Handle query like "what state has the smallest area ?".
			if (schemaGraph.isComparableProperty(subject, property)) {
				QueryNode target = new QueryNode(ontology.literalClass, addedResourceDistance);
				pushEdge(source, pNode, target, false);
				searchEnding();
				popEdge();
			} else {
				//spkang added begin 
				Set<Resource> objects = schemaGraph.getObjectSet(source.getSchemaResource(), pNode.getProperty());
				if (objects == null) {
					searchEnding() ;
					return;
				}
				else if (objects.size() == 1) {
					for (Resource re : objects ) {
						if (ontology.getType(re) != null && ontology.getType(re).equals(ontology.classClass)) {
							QueryNode target = new QueryNode(re, re, index, addedResourceDistance);
							logger.debug("spkang : re : " + re + "\ttarget : " + target);
							pushEdge(source, pNode, target, false);
							searchEnding();
							popEdge();
						}
					}
				}
				// added end 
				else {
					searchEnding();
				}
			}
			return;
		}

		Resource s = source.getSchemaResource();
		Resource p = pNode.getProperty();
		logger.debug("@searchObject\t" + index + ", <" + s + ", " + p + ", ?>");	// debug

		Set<Resource> objects = schemaGraph.getObjectSet(s, p);
		if (objects == null) return;

//		index = sentence.nextIndex(index);
//		index = index + 1;
		//index = entityWrapper.nextIndex(index);
		logger.debug("@searchObject\t index : " + index );	// debug
//		List<MatchedEntity> mes = sentence.getEntities(index);
//		List<MatchedEntity> mes = this.matchedPath.getPathNodeMap().get(this.matchedPath.getPathNode(index));
		List<MatchedEntity> mes = this.entityWrapper.getEntities(index);
		for (MatchedEntity me : mes) {
			Resource mr = me.getResource();
			double meWeight = getResourceDistance(me.getDistance());

//			int nextIndex = me.getEnd() + 1;
			int nextIndex = index + 1;
			logger.debug("@searchObject\t nextIndex : " + nextIndex);	// debug
			if (me.isProperty()) {
				Resource p2 = mr;
				PropertyNode pNode2 = new PropertyNode(me, meWeight);
				Set<Resource> inters = null;

				inters = Util.intersect(objects,
						schemaGraph.getSubjobjSet(p2, p));
				// 如果有resource,是s,p的object，且是p2的subject
				// if there exists a resource, which is the object of s, p
				// and the subject of p2
				if (!inters.isEmpty()) {
					for (Resource o : inters) {
						QueryNode target = new QueryNode(o, addedResourceDistance);
						pushEdge(source, pNode, target, false);
						searchObject(target, pNode2, nextIndex);
						popEdge();
					}
				}

				inters = Util.intersect(objects,
						schemaGraph.getObjobjSet(p2, p));
				// 如果有resource,是s,p的object，且是p2的object
				// if there exists a resource, which is the object of s, p
				// and the object of p2
				if (!inters.isEmpty()) {
					for (Resource o : inters) {
						QueryNode target = new QueryNode(o, addedResourceDistance);
						pushEdge(source, pNode, target, false);
						searchSubject(target, pNode2, nextIndex);
						popEdge();
					}
				}

			} else if (me.isClass() || me.isInstance()) {
				Resource o = schemaGraph.getSchemaResource(mr);
				if (objects.contains(o)) {	// if a object is found
					QueryNode target = new QueryNode(me, o, index, meWeight);
					pushEdge(source, pNode, target, false);
					searchProperty(target, nextIndex);
					popEdge();
				} else if (schemaGraph.isLiteralProperty(s, p)
						&& ontology.isInstanceOf(mr, s)) {	// handles question like "a city named austin"
					QueryNode literalValue = new QueryNode(ontology.literalClass, meWeight);
					literalValue.setValue(me.getLabel());
					pushEdge(source, pNode, literalValue, false);
					searchProperty(source, nextIndex);	// TODO
					popEdge();
				}
			}
		}
	}

	/**
	 * Get the graphs.
	 *
	 * @return The graphs
	 */
	public List<QueryGraph> getGraphs() {
		return graphs;
	}
}

