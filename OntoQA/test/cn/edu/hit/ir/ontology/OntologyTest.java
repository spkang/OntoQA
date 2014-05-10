/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.ontology;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.questionanalysis.Clause;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * TODO
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-26
 */

public class OntologyTest {
	
	Ontology ontology = Ontology.getInstance();

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

	public void testSearch(String query) {
		RDFNode[] nodes = ontology.search(query);
		System.out.println("\nquery: " + query);
		if (nodes != null && nodes.length > 0) {
			for (int i = 0; i < nodes.length; i++) {
				
				System.out.println(nodes[i] + "\t is Resource : " + nodes[i].isResource() + "\t type : " + ontology.getRDFNodeType(nodes[i]));
			}
		} else {
			System.out.println("No result.");
		}
	}
	
	//@Test
	public void testReadData() {
		testSearch("type");
		testSearch("customers");
		testSearch("texas");
	}
	
	public void testFindShortestPath(Clause clause, RDFNode src, RDFNode des, int maxStep) {
		Path path = ontology.findShortestPath(clause, src, des, maxStep);
		System.out.println(src + " -> " + des);
		System.out.println(path);
		System.out.println();
	}
	
	public void testFindShortestPath(RDFNode src, RDFNode des) {
		testFindShortestPath(null, src, des, Path.DEFAULT_MAX_STEP);
	}
	
	//@Test
	public void testFindShortestPath() {
		Resource texas = ontology.getResource("http://ir.hit.edu/nli/geo/state/texas");
		Resource arkansas = ontology.getResource("http://ir.hit.edu/nli/geo/state/arkansas");
		Resource arlington = ontology.getResource("http://ir.hit.edu/nli/geo/city/texas/arlington");
		Resource state = ontology.getResource("http://ir.hit.edu/nli/geo/state");
		Resource highestPoint = ontology.getResource("http://ir.hit.edu/nli/geo/hasHighestPoint");
		
		testFindShortestPath(texas, arkansas);
		testFindShortestPath(arkansas, texas);
		testFindShortestPath(texas, arlington);
		testFindShortestPath(arlington, arkansas);/**/
		
		//testFindShortestPath(state, highestPoint);
		testFindShortestPath(highestPoint, state);
	}
	
	public void checkResourceOrProperty(RDFNode node) {
		Boolean isResource = node instanceof Resource;
		Boolean isProperty = node instanceof Property;
		System.out.println("@checkResourceOrProperty");
		System.out.println(node);
		System.out.println("isResource:" + isResource + ", isProperty:" + isProperty);
	}
	
	public void showPropeties(StmtIterator sit) {
		System.out.println("@showStatements");
		
		String uri = "http://ir.hit.edu/nli/geo/hasHighestPoint";
		Resource highestPoint = ontology.getResource(uri);
		checkResourceOrProperty(highestPoint);
		
		Model model = ontology.getModel();
		Property highestPointProperty = model.getProperty(uri);
		checkResourceOrProperty(highestPointProperty);
		
		while (sit.hasNext()) {
			Statement stmt = sit.next();
			Property property = stmt.getPredicate();
			if (highestPoint.equals(property)) {
				System.out.println(stmt);
				System.out.println("property: " + property);
				System.out.println("highestPoint.equals(property): " + highestPoint.equals(property));
				System.out.println("property.equals(highestPoint): " + property.equals(highestPoint));
				
				checkResourceOrProperty(property);				
			}		
		}
	}
	
	public void showStatements(StmtIterator sit) {
		System.out.println("@showPropeties");
		
		while (sit.hasNext()) {
			Statement stmt = sit.next();
			System.out.println(stmt);	
		}
	}
	
	//@Test
	public void testGetModel() {
		Model model = ontology.getModel();
		Resource highestPoint = ontology.getResource("http://ir.hit.edu/nli/geo/hasHighestPoint");
		
		Resource type = ontology.getType(highestPoint);
		System.out.println("type: " + type);
		
		Boolean isResource = highestPoint instanceof Resource;
		Boolean isProperty = highestPoint instanceof Property;
		System.out.println("isResource:" + isResource + ", isProperty:" + isProperty);
		
		StmtIterator sit;
		//sit = model.listStatements(null, (Property)highestPoint, (RDFNode)null);
		//showStatements(sit);
		
		Resource miane = ontology.getResource("http://ir.hit.edu/nli/geo/state/maine");
		sit = model.listStatements(miane, null, (RDFNode)null);
		showStatements(sit);
	}
	
	public void testAsProperty(RDFNode node) {
		System.out.println(node + " asProperty: " + ontology.asProperty(node));
	}

	//@Test
	public void testAsProperty() {
		String uri = "http://ir.hit.edu/nli/geo/hasHighestPoint";
		Resource highestPoint = ontology.getResource(uri);
		checkResourceOrProperty(highestPoint);
		testAsProperty(highestPoint);
		
		Model model = ontology.getModel();
		Property highestPointProperty = model.getProperty(uri);
		checkResourceOrProperty(highestPointProperty);
		testAsProperty(highestPointProperty);
		
		Resource texas = ontology.getResource("http://ir.hit.edu/nli/geo/state/texas");
		Property texasProperty = ontology.asProperty(texas);
		
		assertEquals(highestPointProperty, ontology.asProperty(highestPoint));
		assertEquals(null, texasProperty);	// TODO
	}
	
	@Test
	public void testReadChineseRdf () {
		System.out.println ("@testReadChineseRdf");

		testSearch("哈尔滨");
		testSearch("纽约") ;
		testSearch ("哈尔");
		testSearch ("约");
		testSearch ("哈尔滨隶属于");
		testSearch ("隶属于");
		testSearch ("隶属");
		testSearch ("属于");
		testSearch("hasName");
		testSearch("belongto");
		testSearch("belong");
		testSearch("hasArea");
		testSearch("hasHeight");
		testSearch("river");
		testSearch("rivers");
		testSearch("hightest point");
		testSearch("new mexico");
		testSearch("new_mexico");
	}
	
	
	//@Test
	public void testProperty() {
		Model model = ontology.getModel();
		
//		String uri = "http://ir.hit.edu/nli/geo/hasHighestPoint";
		String uri = "http://ir.hit.edu/nli/geo/隶属于";
		Resource highestPoint = ontology.getResource(uri);
		if (highestPoint == null ) {
			System.out.println("null");
			return ;
		}
		Property highestPointProperty = ontology.asProperty(highestPoint);
		
		StmtIterator sit;
		sit = model.listStatements(highestPoint, null, (RDFNode)null);	
		showStatements(sit);
		sit = model.listStatements(null, null, highestPoint);	
		showStatements(sit);
		sit = model.listStatements(null, highestPointProperty, (RDFNode)null);	
		showStatements(sit);
	}
	
	//@Test
	public void testListStatementsWithLabel() {
		System.out.println("@testListStatementsWithLabel");
		
		StmtIterator sit = ontology.listStatementsWithLabel();
		while (sit.hasNext()) {
			Statement stmt = sit.next();
			String label = stmt.getString();
			if (label != null) {			
				Resource resource = stmt.getSubject();
				boolean isProperty = resource instanceof Property;
				System.out.println(resource + ": " + isProperty);
			}
		}
	}
	
//	@Test
	public void testGetRDFNodeType() {
		Resource state = ontology.getResource("http://ir.hit.edu/nli/geo/state");
		Resource texas = ontology.getResource("http://ir.hit.edu/nli/geo/state/texas");
		Resource highestPoint = ontology.getResource("http://ir.hit.edu/nli/geo/hasHighestPoint");
		
		assertEquals(RDFNodeType.CLASS, ontology.getRDFNodeType(state));
		assertEquals(RDFNodeType.INSTANCE, ontology.getRDFNodeType(texas));
		assertEquals(RDFNodeType.PROPERTY, ontology.getRDFNodeType(highestPoint));
	}
	
	@Test
	public void testIsInstanceOf() {
		System.out.println("@testIsInstanceOf");
		Resource state = ontology.getResource("http://ir.hit.edu/nli/geo/state");
		Resource texas = ontology.getResource("http://ir.hit.edu/nli/geo/state/texas");
		Resource highestPoint = ontology.getResource("http://ir.hit.edu/nli/geo/hasHighestPoint");

		assertTrue(ontology.isInstanceOf(texas, state));
		assertFalse(ontology.isInstanceOf(state, state));
		assertFalse(ontology.isInstanceOf(state, texas));
		assertFalse(ontology.isInstanceOf(highestPoint, state));
	}
	
//	@Test
	public void testQueryResult () {
		System.out.println("@testQueryResult");
		//String querySparql = "SELECT ?a WHERE { ?a rdf:type geo:city . ?a geo:inState ?c . ?c geo:hasName + \"virginia\" .}";
		String querySparql = "SELECT DISTINCT (COUNT(DISTINCT ?city_0) AS ?city_0_count) WHERE { ?city_0 a geo:city . ?state_8 a geo:state . ?city_0 geo:inState ?state_8 .}";
		String querySparqlStd = "SELECT (COUNT(DISTINCT ?c) AS ?cnt) WHERE {?c a geo:city .}";
		
		Set<String> a = new HashSet<String>(ontology.getResults(querySparql));
		Set<String> b = new HashSet<String>(ontology.getResults(querySparqlStd));
		if(a.equals( b))
			System.out.println("yes");
		
		String querySparqltest1 = "SELECT DISTINCT ?c  WHERE {?c a geo:city . ?c geo:hasName \"springfield\" .}";
		//String querySparqltest1 = "SELECT (DISTINCT ?c)  WHERE {?c a geo:city . ?c geo:hasName \"springfield\" .}";
		System.out.println("querySparqltest1: ");
		for (String s : ontology.getResults(querySparqltest1)) {
			System.out.println(s);
		}
		
		/*String qs = "SELECT DISTINCT ?river_0 (COUNT(DISTINCT ?state_6) AS ?state_6_count) WHERE { ?river_0 a geo:river . ?state_6 a geo:state . ?river_0 geo:runThrough ?state_6 . }GROUP BY ?river_0 ORDER BY DESC(?state_6_count) LIMIT 1";
		
		System.out.println("Standard: ");
		for (String s : ontology.getResults(qs)) {
			System.out.println(s);
		}
		
		
		//String query = "which river runs through the most states?";
		//String query = "what are the high points of states surrounding mississippi ?";
		String query = "name all the rivers in mississippi";
		QuestionAnalyzer analyzer = new QuestionAnalyzer();
		String sparqlOut = analyzer.getSparql(query);			
		System.out.println("sparqlOut : " + sparqlOut);
		System.out.println("Our   : ");
		for (String s : ontology.getResults(sparqlOut)) {
			System.out.println(s);
		}*/
		System.out.println("end for our result");
	}
	
	//@Test
	public void testForSparql () {
		String query = "SELECT ?city_0 WHERE { ?city_0 a geo:city . ?city_0 rdfs:label \"new york\" . } limit 10";
		testQuery (query);
	}
	
	@Test
	public  void testMerge () {
		//testQuery ("what states in the united states have a city of springfield ?");
		//testQuery ("what states have a city of springfield ?");
	}
	
	public void testQuery (String query) {
		System.out.println("test query : " + query);
		for (String s : ontology.getResults(query)) {
			System.out.println(s);
		}
	} 
	
}
