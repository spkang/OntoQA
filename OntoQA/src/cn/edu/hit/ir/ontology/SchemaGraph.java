/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.hit.ir.graph.LoopMultiGraph;
import cn.edu.hit.ir.nlp.Vocabulary;
import cn.edu.hit.ir.nlp.WeightedWord;
import cn.edu.hit.ir.util.ObjectToSet;
import cn.edu.hit.ir.util.Pair;
import cn.edu.hit.ir.util.Util;

import com.aliasi.spell.EditDistance;
import com.aliasi.util.Distance;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import edu.stanford.nlp.util.StringUtils;

/**
 * A schema graph of an ontology.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-5
 */

public class SchemaGraph {
	
	public static final double MAX_DISTANCE = 1;
	
	private Ontology ontology;
	
	private Vocabulary vocabulary;
	
	private LoopMultiGraph<SchemaNode, SchemaEdge> graph;
	
	private List<SchemaNode> nodes;
	private Map<Resource, SchemaNode> res2nodeMap;
	private Set<SchemaEdgeKey> edgeSet;
	
	private SchemaNode literalNode;
	
	private ObjectToSet<Resource, Resource> prop2subjMap;
	private ObjectToSet<Resource, Resource> prop2objMap;
	/**
	 * Map of subjects to its literal properties
	 */
	private ObjectToSet<Resource, Resource> subj2litPropMap;
	/**
	 * Map of subjects to its comparable literal properties
	 */
	private ObjectToSet<Resource, Resource> subj2cmpPropMap;
	
	private ObjectToSet<Pair<Resource, Resource>, Resource> subjectMap;
	private ObjectToSet<Pair<Resource, Resource>, Resource> objectMap;
	private ObjectToSet<Pair<Resource, Resource>, Resource> propertyMap;
	
	/**
	 * 一对属性(prop1, prop2)映射到一个资源，该资源是prop1和prop2的subject
	 * e.g. (hasName, hasCapital) -> state
	 */
	private ObjectToSet<Pair<Resource, Resource>, Resource> subjsubjMap;
	/**
	 * 一对属性(prop1, prop2)映射到一个资源，该资源是prop1和prop2的object
	 * e.g. (inState, runThrough) -> state
	 */
	private ObjectToSet<Pair<Resource, Resource>, Resource> objobjMap;
	/**
	 * 一对属性(prop1, prop2)映射到一个资源，该资源是prop1的subject，是prop2的object
	 * e.g. (hasElevation, hasHighestPoint) -> point
	 */
	private ObjectToSet<Pair<Resource, Resource>, Resource> subjobjMap;	
	
	private Set<Resource> resourceSet;
	private Map<Resource, String> res2labelMap;
	
	
	// new added
	/**
	 * 一对实体映射到一个属性
	 * 
	 * (subject, object) --> property
	 * like (river, state) --> runThrough 
	 * 
	 * */
	private ObjectToSet<Pair<Resource, Resource>, ScoredResource> subjObj2PropSet;
	
	/**
	 * 一个subject和一个property决定一个object 
	 * (subject, property) --> object
	 * like : (state, hasPopulation) --> literal
	 * 
	 */
	private ObjectToSet<Pair<Resource, Resource>, ScoredResource> subjProp2ObjSet;

	/**
	 * 一个object和一个property决定一个subject 
	 * (object, property) --> object
	 * like : (Literal, hasPopulation) --> state, city
	 *  
	 */
	private ObjectToSet<Pair<Resource, Resource>, ScoredResource>  objProp2SubjSet;
	
	
	private static Distance<CharSequence> editDistance = new EditDistance(false);
	
	public SchemaGraph(Ontology ontology) {
		this.ontology = ontology;
		vocabulary = Vocabulary.getInstance();
		
		initData();
		build();
		initRes2LabelMap();
	}
	
	private void initData() {
		nodes = new ArrayList<SchemaNode>();
		res2nodeMap = new HashMap<Resource, SchemaNode>();
		edgeSet = new HashSet<SchemaEdgeKey>();
		prop2subjMap = new ObjectToSet<Resource, Resource>();
		prop2objMap = new ObjectToSet<Resource, Resource>();
		subj2litPropMap = new ObjectToSet<Resource, Resource>();
		subj2cmpPropMap = new ObjectToSet<Resource, Resource>();
		
		resourceSet = new HashSet<Resource>();
		
		subjectMap = new ObjectToSet<Pair<Resource, Resource>, Resource>();
		objectMap = new ObjectToSet<Pair<Resource, Resource>, Resource>();
		propertyMap = new ObjectToSet<Pair<Resource, Resource>, Resource>();
		
		subjsubjMap = new ObjectToSet<Pair<Resource, Resource>, Resource>();
		objobjMap = new ObjectToSet<Pair<Resource, Resource>, Resource>();
		subjobjMap = new ObjectToSet<Pair<Resource, Resource>, Resource>();
		
		// new added
		subjObj2PropSet = new ObjectToSet<Pair<Resource, Resource>, ScoredResource>();
		subjProp2ObjSet = new ObjectToSet<Pair<Resource, Resource>, ScoredResource>();
		objProp2SubjSet = new ObjectToSet<Pair<Resource, Resource>, ScoredResource>();
	}
	
	private void build() {
		Model model = ontology.getModel();
		
		graph = new LoopMultiGraph<SchemaNode, SchemaEdge>(SchemaEdge.class);
		
		// build vertexes
		// add class nodes
		ResIterator classRit = model.listResourcesWithProperty(ontology.typeProperty, ontology.classClass);
		addNodes(classRit);
		literalNode = addNode(ontology.literalClass);
		
		// build edges brutely
		StmtIterator sit = model.listStatements();
		while (sit.hasNext()) {
			Statement stmt = sit.next();
			addEdge(stmt);
		}
		//System.out.println("cnt : " + cnt + "\t edgeSet size : " + edgeSet.size() + ": " + edgeSet);	//debug
		
		initPropertyPairMap();
		calculateObjetcToSetScore(this.subjObj2PropSet);
		calculateObjetcToSetScore(this.subjProp2ObjSet);
		calculateObjetcToSetScore(this.objProp2SubjSet);
	}
	
	private SchemaNode addNode(Resource resource) {
		SchemaNode node = new SchemaNode(resource);
		graph.addVertex(node);
		nodes.add(node);
		res2nodeMap.put(resource, node);
		//System.out.println("add node ,resource : " + resource);	// debug
		return node;
	}
	
	private void addNodes(ResIterator rit) {
		while (rit.hasNext()) {
			Resource resource = rit.next();
			addNode(resource);	
		}
	}
	
	private SchemaNode getNode(Resource resource) {
		return res2nodeMap.get(resource);
	}
	
	public SchemaNode getTypeNode(Resource resource) {
		Resource type = ontology.getType(resource);
		return getNode(type);
	}
	
	private void addEdge(Statement stmt) {
		Resource subject = stmt.getSubject();
		Property property = stmt.getPredicate();
		RDFNode object = stmt.getObject();

		SchemaNode subjectTypeNode = getTypeNode(subject);
		if (subjectTypeNode == null) return;
		
//		System.out.println(stmt);	// debug
		if (object instanceof Resource) {
			SchemaNode objectTypeNode = getTypeNode((Resource)object);
			//System.out.println(subjectTypeNode + ", " + objectTypeNode);	// debug
			if (objectTypeNode != null) {
				addEdge(subjectTypeNode, objectTypeNode, property);
				addScoredObjectToSet(subjectTypeNode, objectTypeNode, property);
			}
		} else {
			addEdge(subjectTypeNode, literalNode, property);
			addScoredObjectToSet(subjectTypeNode, literalNode, property);
			Resource propRes = ontology.asResource(property);
			// Add literal property
			subj2litPropMap.addMember(subjectTypeNode.getResource(), propRes);
			// Add comparable literal property
			if (ontology.isComparableLiteral(object)) {
				subj2cmpPropMap.addMember(subjectTypeNode.getResource(), propRes);
			}
		}
	}
	
	private SchemaEdge addEdge(SchemaNode source, SchemaNode target, Property property) {
		if (source == null || target == null) return null;
		if (containsEdge(source, target, property)) return null;
		edgeSet.add(new SchemaEdgeKey(property, source, target));
		
		Resource subject = source.getResource();
		Resource object = target.getResource();
		Resource propRes = ontology.asResource(property);
		prop2subjMap.addMember(propRes, subject);
		prop2objMap.addMember(propRes, object);
		addSubject(subject, propRes, object);
		addObject(subject, propRes, object);
		addProperty(subject, propRes, object);
		resourceSet.add(subject);
		resourceSet.add(object);
		
		SchemaEdge edge = graph.addEdge(source, target);
		edge.setProperty(property);
		return edge;
	}
	
	private boolean containsEdge(SchemaNode source, SchemaNode target, Property property) {
		SchemaEdgeKey key = new SchemaEdgeKey(property, source, target);
		return edgeSet.contains(key);
	}
	
	private void initRes2LabelMap() {
		res2labelMap = new HashMap<Resource, String>();
		
		for (Resource resource : resourceSet) {
			String label = ontology.getLabel(resource);
			res2labelMap.put(resource, label);
		}
		
		for (Resource property : getPropertySet()) {
			String label = ontology.getLabel(property);
			res2labelMap.put(property, label);
		}
	}
	
	public Set<ScoredResource> getSubjObj2PropSet (Resource s, Resource o ) {
		if (s == null || o == null ) return null;
		
		Pair<Resource, Resource> key = Pair.of(this.getSchemaResource(s),  this.getSchemaResource(o));
		if (this.subjObj2PropSet.containsKey(key)) {
			return this.subjObj2PropSet.get(key);
		}
		return null;
	}
	
	public ObjectToSet<Pair<Resource, Resource>, ScoredResource> getSubjObj2PropSet() {
		return subjObj2PropSet;
	}
	
	
	private void addScoredObjectToSet (SchemaNode source, SchemaNode target, Property property) {
		if (source == null || target == null || property == null ) return ;
		Resource subject = source.getResource();
		Resource object  = target.getResource();
		Resource propRes = ontology.asResource(property);
		if (ontology.isLabelClass(propRes))
			return;
		this.addSubjObj2PropSet(subject, object, propRes);
		this.addSubjProp2ObjSet(subject, propRes, object);
		this.addObjProp2SubjSet(object, propRes, subject);
	}
	
	/**
	 * 计算
	 * subjObj2PropSet
	 * subjProp2ObjSet
	 * objProp2SubjSet 这三个set中的对应的valset的score的最大似然估计score
	 * 
	 * 计算方法, 首先根据特定的key获得相应的valSet,然后将valSet中的所有的score相加求和得到sum，
	 * 然后再用valset中每个元素score除以sum得到最大似然估计
	 *
	 * @param 
	 * @return void 
	 */
	private void calculateObjetcToSetScore (ObjectToSet<Pair<Resource, Resource>, ScoredResource> objSet) {
		if (objSet == null ) return ;
		
		for (Pair<Resource, Resource> key : objSet.keySet()) {
			double sum = 0.0;
			List<ScoredResource> valList = new ArrayList<ScoredResource>(objSet.get(key));
			for (ScoredResource sr : valList) {
				sum += sr.score;
			}
			for (ScoredResource sr : valList) {
				if (sum < 10e-8) {// sum == 0
					sr.score = 0;
				}
				else 
					sr.score = sr.score / sum;
			}
		}
		
	}
	
	
	/**
	 * 根据a，b的pair进行数据加入，如果valset中已经存在v了，那么就对valset中的对应项的score进行更新，加一
	 * 否则加入
	 *
	 * @param 
	 * @return void 
	 */
	private void addScoredTriple (ObjectToSet<Pair<Resource, Resource>, ScoredResource> objSet, Resource a, Resource b, Resource v) {
		Pair<Resource, Resource> pair = Pair.of(a,  b);
		if (objSet.containsKey(pair)) {
			Set<ScoredResource> valSet = objSet.get(pair);
			ScoredResource sr = new ScoredResource(v, 1.0);
			ScoredResource removeSr = null;
			if (valSet.contains(sr)) {
				ScoredResource [] valArray = new ScoredResource[valSet.size()];
				valSet.toArray(valArray);
				//System.out.println("valArray : " + StringUtils.join(valArray, ", "));
				for (ScoredResource tsr : valArray) {
					if (tsr.resource.equals(v)) {
						sr.score += tsr.score;
						removeSr = tsr;
						break;
					}
				}
				if (removeSr != null )  {
					objSet.removeMember(pair, removeSr);
					objSet.addMember(pair, sr); // 加入新的
				}
				else {
					objSet.addMember(pair, sr); // 加入新的
				}
			}
			else {
				objSet.addMember(pair, sr); // 加入新的
			}
		}
		else {
			ScoredResource sr = new ScoredResource(v, 1.0);
			objSet.addMember(pair, sr);
		}
	}

	/**
	 * set subjObj2PropSet
	 *
	 * @param a, b are the pair
	 * @param v is the value
	 * @return void 
	 */
	public void addSubjObj2PropSet(Resource a, Resource b, Resource v) {
		if (a == null || b == null || v == null ) return ;
		addScoredTriple(this.subjObj2PropSet, a, b, v);
	}
	
	public Set<ScoredResource> getSubjProp2ObjSet (Resource s, Resource p) {
		if (s == null || p == null )
			return null;
		Pair<Resource, Resource> key = Pair.of(this.getSchemaResource(s),  this.getSchemaResource(p));
		if (this.subjProp2ObjSet.containsKey(key)) {
			return this.subjProp2ObjSet.get(key);
		}
		return null;
 	}
	
	public ObjectToSet<Pair<Resource, Resource>, ScoredResource> getSubjProp2ObjSet() {
		return subjProp2ObjSet;
	}

	public void addSubjProp2ObjSet(Resource a, Resource b, Resource v) {
		if (a == null || b == null || v == null ) return ;
		addScoredTriple(this.subjProp2ObjSet, a, b, v);
	}

	public Set<ScoredResource> getObjProp2SubjSet (Resource o, Resource p ) {
		if (o == null ||p == null ) return null;
		
		Pair <Resource, Resource> key = Pair.of(this.getSchemaResource(o), this.getSchemaResource(p));
		if (this.objProp2SubjSet.containsKey(key)) {
			return this.objProp2SubjSet.get(key);
		}
		return null;
	}
	
	public ObjectToSet<Pair<Resource, Resource>, ScoredResource> getObjProp2SubjSet() {
		return objProp2SubjSet;
	}

	public void addObjProp2SubjSet(Resource a, Resource b, Resource v) {
		if (a == null || b == null || v == null ) return ;
		addScoredTriple(this.objProp2SubjSet, a, b, v);
	}

	/**
	 * Returns the label of a resource(including properties) in schema graph.
	 *
	 * @param resource the resource
	 * @return the label of the resource or <code>null</code> if the resource 
	 * is not existed in this schema graph.
	 */
	public String getLabel(Resource resource) {
		return res2labelMap.get(resource);
	}
	
	public LoopMultiGraph<SchemaNode, SchemaEdge> getGraph() {
		return graph;
	}
	
	public Set<Resource> getSubjectSet(Resource property) {
		return prop2subjMap.get(property);
	}
	
	public Set<Resource> getObjectSet(Resource property) {
		return prop2objMap.get(property);
	}
	
	public Set<Resource> getPropertySet() {
		return prop2subjMap.keySet();
	}
	
	public void addSubject(Resource s, Resource p, Resource o) {
		Pair<Resource, Resource> pair = new Pair<Resource, Resource>(o, p);
		subjectMap.addMember(pair, s);
	}
	
	public void addObject(Resource s, Resource p, Resource o) {
		Pair<Resource, Resource> pair = new Pair<Resource, Resource>(s, p);
		objectMap.addMember(pair, o);
	}
	
	public void addProperty(Resource s, Resource p, Resource o) {
		Pair<Resource, Resource> pair = Pair.of(s, o);
		propertyMap.addMember(pair, p);
	}
	
	public Set<Resource> getSubjectSet(Resource p, Resource o) {
		Pair<Resource, Resource> pair = new Pair<Resource, Resource>(o, p);
		return subjectMap.get(pair);
	}
	
	public Set<Resource> getObjectSet(Resource s, Resource p) {
		Pair<Resource, Resource> pair = new Pair<Resource, Resource>(s, p);
		return objectMap.get(pair);
	}
	
	public Set<Resource> getPropertySet(Resource s, Resource o) {
		Pair<Resource, Resource> pair = Pair.of(s, o);
		return propertyMap.get(pair);
	}
	
	public Set<Resource> getLiteralPropertySet(Resource subject) {
		return subj2litPropMap.get(subject);
	}
	
	public Set<Resource> getComparablePropertySet(Resource subject) {
		return subj2cmpPropMap.get(subject);
	}
	
	public double getDistanceOfWords(String s, String t) {
		if (s == null || t == null || s.length() == 0 || t.length() == 0) {
			return MAX_DISTANCE;
		}
		
		int length = s.length() + t.length();
		double dis = editDistance.distance(s, t) / length;
		return dis;
	}

	public double getDistance(String s, String t) {
		if (s == null || t == null || s.length() == 0 || t.length() == 0) {
			return MAX_DISTANCE;
		}
		
		String[] ss = s.split("\\s+");
		String[] ts = t.split("\\s+");
		double minDis = MAX_DISTANCE;
		for (int i = 0; i < ss.length; i++) {
			for (int j = 0; j < ts.length; j++) {
				double dis = getDistanceOfWords(ss[i], ts[j]);
				if (minDis > dis) {
					minDis = dis;
				}
			}
		}
		// TODO
		int length = ss.length + ts.length;
		minDis += minDis * ((double)(length - 2) / 10);
		if (minDis > MAX_DISTANCE) {
			minDis = MAX_DISTANCE;
		}
		return minDis;
	}
	
	// TODO
	public double getDistance(String word, Resource literal) {
		String label = getLabel(literal);
		Set<WeightedWord> wws = vocabulary.getNouns(word);
		if (wws == null) {
			double dis = getDistance(word, label);
			//System.out.println("@getDistance " + label + ", " + label + ", " + dis);	// debug
			return dis;
		} else {
			double minDis = MAX_DISTANCE;
			for (WeightedWord ww : wws) {
				String noun = ww.getWord();
				double dis = getDistance(noun, label) * ww.getWeight();
				if (minDis > dis) {
					minDis = dis;
				}
			}
			return minDis;
		}
	}
	
	public Resource getComparableProperty(Resource subject, String word) {
		Resource bestLiteral = null;
		Set<Resource> literals = getComparablePropertySet(subject);
		if (literals != null) {
			double minDis = 1000000;
			for (Resource literal : literals) {
				double dis = getDistance(word, literal);
				if (minDis > dis) {
					minDis = dis;
					bestLiteral = literal;
				}
			}
		}
		return bestLiteral;
	}
	
	public boolean isLiteralProperty(Resource subject, Resource property) {
		Set<Resource> litPropSet = getLiteralPropertySet(subject);
		if (litPropSet == null) {
			return false;
		} else {
			return litPropSet.contains(property);
		}
	}
	
	public boolean isComparableProperty(Resource subject, Resource property) {
		Set<Resource> cmpPropSet = getComparablePropertySet(subject);
		if (cmpPropSet == null) {
			return false;
		} else {
			return cmpPropSet.contains(property);
		}
	}
	
	public Set<Resource> getResourceSet() {
		return resourceSet;
	}
	
	private void addTriple(ObjectToSet<Pair<Resource, Resource>, Resource> map, 
			Resource a, Resource b, Set<Resource> v) {
		
		if (v == null || v.isEmpty()) return;
		
		Pair<Resource, Resource> pair = Pair.of(a, b);
		map.addMembers(pair, v);
	}
	
	private void initPropertyPairMap() {
		Set<Resource> properties = getPropertySet();
		for (Resource p : properties) {
			Set<Resource> subjects = getSubjectSet(p);
			Set<Resource> objects = getObjectSet(p);
			if (subjects != null) {
				for (Resource p2 : properties) {
					Set<Resource> subjects2 = getSubjectSet(p2);
					Set<Resource> objects2 = getObjectSet(p2);
					
					addTriple(subjsubjMap, p, p2, Util.intersect(subjects, subjects2));
					addTriple(subjobjMap, p, p2, Util.intersect(subjects, objects2));
				}
			}
			
			if (objects != null) {
				for (Resource p2 : properties) {
					Set<Resource> objects2 = getObjectSet(p2);

					addTriple(objobjMap, p, p2, Util.intersect(objects, objects2));
				}
			}
		}
	}
	
	public ObjectToSet<Pair<Resource, Resource>, Resource> getSubjsubjMap() {
		return subjsubjMap;
	}
	
	public ObjectToSet<Pair<Resource, Resource>, Resource> getObjobjMap() {
		return objobjMap;
	}
	
	public ObjectToSet<Pair<Resource, Resource>, Resource> getSubjobjMap() {
		return subjobjMap;
	}
	
	public Set<Resource> getSubjsubjSet(Resource a, Resource b) {
		Pair<Resource, Resource> pair = Pair.of(a, b);
		return subjsubjMap.get(pair);
	}
	
	public Set<Resource> getObjobjSet(Resource a, Resource b) {
		Pair<Resource, Resource> pair = Pair.of(a, b);
		return objobjMap.get(pair);
	}
	
	public Set<Resource> getSubjobjSet(Resource a, Resource b) {
		Pair<Resource, Resource> pair = Pair.of(a, b);
		return subjobjMap.get(pair);
	}
	
	
	public Resource getSchemaResource(Resource resource) {
		if (resource == null )
			return null;
		if (resource.equals(this.literalNode.getResource()))
			return resource;
		if (ontology.isClass(resource) || ontology.isProperty(resource)) {
			return resource;
		} else {
			return ontology.getType(resource);
		}
	}	
	
	/**
	 * 根据两个属性，获得两个属性之间可能的实体集合
	 *
	 * @param lhsProp, 左边的属性
	 * @param rhsProp, 右边的属性
	 * @return Set<Resource> 
	 */
	public Set<Resource> getPropPropSet (Resource lhsProp, Resource rhsProp) {
		if (lhsProp == null || rhsProp == null ) return null;
		
		Set<Resource> resSet = new HashSet<Resource>();
		
		Set<Resource> subjSubjSet = this.getSubjsubjSet(lhsProp, rhsProp);
//		System.out.println ("subjsubjset : " + subjSubjSet);
		if (subjSubjSet != null && ! subjSubjSet.isEmpty())
			resSet.addAll(subjSubjSet);
		
		Set<Resource> subjObjSet  = this.getSubjobjSet(lhsProp, rhsProp);
//		System.out.println ("subjObjset : " + subjObjSet);
		if (subjObjSet != null && ! subjObjSet.isEmpty())
			resSet.addAll(subjObjSet);
		
		Set<Resource> objObjSet   = this.getObjobjSet(lhsProp, rhsProp);
//		System.out.println ("objObjset : " + objObjSet);
		if (objObjSet != null && ! objObjSet.isEmpty())
			resSet.addAll(objObjSet);
		return (resSet.isEmpty() ? null : resSet); 
	}
	
	
	/**
	 *  判断一个三元组是否合法
	 *
	 * @param 
	 * @return boolean 
	 */
	public boolean isLegalTriple (Resource s, Resource p, Resource o) {
		if (s == null || p == null || o == null)
			return false;
		Resource subj = s;
		if (!this.resourceSet.contains(s)) {
			 subj = getTypeNode(s).getResource();
		}
		Resource obj = o;
		if (!this.resourceSet.contains(o)) {
			 obj = getTypeNode(o).getResource();
		}
		
		
		
		Pair<Resource, Resource> pair = Pair.of(subj,  p);
		if (this.subjProp2ObjSet.containsKey(pair)) {
			ScoredResource sr = new ScoredResource(obj, 1);
			if (this.subjProp2ObjSet.get(pair).contains(sr))
				return true;
		}
		Pair<Resource, Resource> pair2 = Pair.of(obj, p);
		if (this.subjProp2ObjSet.containsKey(pair2)) {
			ScoredResource sr = new ScoredResource(subj, 1);
			if (this.subjProp2ObjSet.get(pair2).contains(sr))
				return true;
		}
		if (isLiteralProperty (s, p) && ontology.getRDFNodeType(o).equals(RDFNodeType.INSTANCE) 
			    || isLiteralProperty(o, p) && ontology.getRDFNodeType(s).equals(RDFNodeType.INSTANCE)) {
				return true;
			}
		return false;
	}
	
	public boolean isLiteralResource (Resource resource) {
		if (resource == null )
			return false;
		return this.getSchemaResource(resource).equals(this.literalNode.getResource());
	}
	
	public String toString() {
		return graph.toString();
	}
}
