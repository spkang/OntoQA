/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.ontology;


import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Resource;
import cn.edu.hit.ir.graph.LoopMultiGraph;

/**
 * TODO
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-5
 */

public class SchemaGraphTest {
	
	Ontology ontology;
	SchemaGraph schemaGraph;
	LoopMultiGraph<SchemaNode, SchemaEdge> graph;
	
	public SchemaGraphTest() {
		ontology = Ontology.getInstance();
		schemaGraph = ontology.getSchemaGraph();		
		graph = schemaGraph.getGraph();
	}

	/**
	 * TODO
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * TODO
	 *
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testSchemaGraph() {
		System.out.println(schemaGraph);
		Set<SchemaNode> nodes = graph.vertexSet();
		Set<SchemaEdge> edges = graph.edgeSet();
		System.out.println(nodes.size() + ", " + edges.size());
	}
	
	@Test
	public void testScoredObjectToSet () {
		System.out.println("subjObj2PropSet : " + schemaGraph.getSubjObj2PropSet().toString().replaceAll("http://ir.hit.edu/nli/geo/", "").replaceAll("http://www.w3.org/2000/01/rdf-schema#", ""));
		System.out.println("subjProp2ObjSet : " + schemaGraph.getSubjProp2ObjSet().toString().replaceAll("http://ir.hit.edu/nli/geo/", "").replaceAll("http://www.w3.org/2000/01/rdf-schema#", ""));
		System.out.println("objProp2SubjSet : " + schemaGraph.getObjProp2SubjSet().toString().replaceAll("http://ir.hit.edu/nli/geo/", "").replaceAll("http://www.w3.org/2000/01/rdf-schema#", ""));
	}
	
//	@Test
	public void testGetSet() {
		System.out.println("@testGetSet");
		
		Set<Resource> properties =  schemaGraph.getPropertySet();
		for (Resource property : properties) {
			Set<Resource> subjects = schemaGraph.getSubjectSet(property);
			Set<Resource> objects = schemaGraph.getObjectSet(property);
			System.out.println(property);
			System.out.println(subjects);
			System.out.println(objects);
			System.out.println();
		}
	}
	
//	@Test
	public void testGetObjectSet() {
		System.out.println("@testGetObjectSet");
		
		Set<Resource> properties =  schemaGraph.getPropertySet();
		Set<Resource> resources = schemaGraph.getResourceSet();
		for (Resource property : properties) {
			System.out.println("proprety: " + property);
			for (Resource resource : resources) {
				Set<Resource> subjects = schemaGraph.getSubjectSet(property, resource);
				Set<Resource> objects = schemaGraph.getObjectSet(resource, property);
				
				System.out.println("resource: " + resource);
				System.out.println(subjects);
				System.out.println(objects);
			}
		}
	}
	
//	@Test
	public void testPropertyPairMap() {
		System.out.println("@testPropertyPairMap");
		
		System.out.println(schemaGraph.getSubjsubjMap().toString().replaceAll("http://ir.hit.edu/nli/geo/", "geo:"));
		System.out.println(schemaGraph.getObjobjMap().toString().replaceAll("http://ir.hit.edu/nli/geo/", "geo:"));
		System.out.println(schemaGraph.getSubjobjMap().toString().replaceAll("http://ir.hit.edu/nli/geo/", "geo:"));
	}
	
//	@Test
	public void testGetPropertySet() {
		System.out.println("@testGetPropertySet");
		
		Set<Resource> resources = schemaGraph.getResourceSet();
		for (Resource r : resources) {
			for (Resource r2 : resources) {
				Set<Resource> props = schemaGraph.getPropertySet(r, r2);
				Set<Resource> props2 = schemaGraph.getPropertySet(r2, r);
				
				System.out.println(r + ", " + r2);
				System.out.println(props);
				System.out.println(props2);
			}
		}
	}
	
//	@Test
	public void testGetLiteralSet() {
		System.out.println("@testGetLiteralSet");
		
		Set<Resource> resources = schemaGraph.getResourceSet();
		for (Resource s : resources) {
			Set<Resource> literals = schemaGraph.getComparablePropertySet(s);
			System.out.println("subject: " + s);
			System.out.println("literals:");
			System.out.println(literals);
			System.out.println();
		}
	}
	
	public void testGetComparableLiteral(Resource subject, String word) {
		Resource literal = schemaGraph.getComparableProperty(subject, word);
		System.out.println(subject + ", " + word + ": " + literal);
	}
	
//	@Test
	public void testGetComparableLiteral() {
		System.out.println("@testGetComparableLiteral");
		
		Resource state = ontology.getResource("http://ir.hit.edu/nli/geo/state");
		Resource city = ontology.getResource("http://ir.hit.edu/nli/geo/city");
		Resource river = ontology.getResource("http://ir.hit.edu/nli/geo/river");
		
		testGetComparableLiteral(state, "area");
		testGetComparableLiteral(state, "density");
		testGetComparableLiteral(state, "dense");
		testGetComparableLiteral(state, "populous");
		testGetComparableLiteral(state, "large");
		testGetComparableLiteral(state, "big");
		testGetComparableLiteral(state, "small");
		
		testGetComparableLiteral(city, "big");
		testGetComparableLiteral(city, "small");
		
		testGetComparableLiteral(river, "long");
	}
}
