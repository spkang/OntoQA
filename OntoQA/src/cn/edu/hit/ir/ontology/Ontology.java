/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.ontology;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import cn.edu.hit.ir.questionanalysis.Clause;
import cn.edu.hit.ir.util.ConfigUtil;
import cn.edu.hit.ir.util.Util;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.query.larq.IndexBuilderSubject;
import com.hp.hpl.jena.query.larq.IndexLARQ;
import com.hp.hpl.jena.query.larq.LARQ;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

/**
 * An ontology using Jena for data manipulation.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-26
 */

public class Ontology {
	
	public static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public static final String RDFS_LABEL_URI = "http://www.w3.org/2000/01/rdf-schema#label";
	public static final String RDF_PROPERTY_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property";
	public static final String RDFS_CLASS_URI = "http://www.w3.org/2000/01/rdf-schema#Class";
	public static final String RDFS_LITERAL_URI = "http://www.w3.org/2000/01/rdf-schema#Literal";
	
	public static final String DATA_FILES = "data.files";
	
	public static final String DEFAULT_PREFIX = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPREFIX geo: <http://ir.hit.edu/nli/geo/>\n";
	
	// 支持中文
	public static final String YUETAN_PREFIX = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPREFIX  yuetan: <http://ir.hit.edu/nli/yuetan/>\nPREFIX  base: <http://ir.hit.edu/nli/>\nPREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>\nPREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	private boolean isChinese = false;
	
	
	public static final String RDFS_LABEL = "rdfs:label";
	
	private String hasNameUri;
	
	private static final RDFNode[] RDF_NODES = new RDFNode[0];
	
	private static Ontology instance;
	
	private Configuration config;
	
	public Property typeProperty;
	public Property labelProperty;
	public Resource propertyClass;
	public Resource classClass;
	public Resource literalClass;
	
	public Property hasNameProperty;
	
    private Model model;
    
    private SchemaGraph schemaGraph;
        
    private List<IndexLARQ> indexes;
    
    private Set<String> comparableDatatypeSet;
	
	public static Ontology getInstance() {
		if (instance == null) {
			instance = new Ontology();
		}
		return instance;
	}
	
	private Ontology() {
		initConfig();
		initModel();
		initResources();
		
		readData();
	}
	
	private void initConfig() {
		try {
			config = new PropertiesConfiguration(ConfigUtil.getPath(getClass()));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private void initModel() {
    	model = ModelFactory.createDefaultModel();
    	indexes = new ArrayList<IndexLARQ>();
    }
    
    private void initResources() {
    	typeProperty = model.getProperty(RDF_TYPE_URI);
    	labelProperty = model.getProperty(RDFS_LABEL_URI);
    	propertyClass = model.getResource(RDF_PROPERTY_URI);
    	classClass = model.getResource(RDFS_CLASS_URI);
    	literalClass = model.getResource(RDFS_LITERAL_URI);
    	
    	comparableDatatypeSet = new HashSet<String>();
    	List<String> cds = config.getList("uri.comparableDatatypes");
    	comparableDatatypeSet.addAll(cds);
    	
    	hasNameUri = config.getString("uri.hasName");
    	hasNameProperty = model.getProperty(hasNameUri);
    }
    
    /**
     * Adds RDF statements from a file.
     * <p>
     * Predefined values for <code>lang</code> are "RDF/XML", "N-TRIPLE", "TURTLE" (or "TTL") and "N3".
     * <code>null</code> represents the default language, "RDF/XML". "RDF/XML-ABBREV" is 
     * a synonym for "RDF/XML".
     * 
     * @param filename The file name
     * @param base The base URI to be used when converting relative URI's to absolute URI's
     * @param lang The language of the serialization
     */
    public void read(String filename, String base, String lang) {
    	InputStream in = FileManager.get().open(filename);
    	
    	IndexBuilderSubject larqBuilder = new IndexBuilderSubject();
    	
    	// Index statements as they are added to the model
    	model.register(larqBuilder);
    	
    	model.read(in, base, lang);
		
		// Finish indexing
		larqBuilder.closeWriter();
		model.unregister(larqBuilder);

		// Create the access index
		IndexLARQ index = larqBuilder.getIndex();
		
		// Store the new index
		indexes.add(index);
		
		// Make globally available
		LARQ.setDefaultIndex(index);
    }
    
    public void read(String filename, String base) {
    	read(filename, base, null);
    }
    
    public void read(String filename) {
    	read(filename, "");
    }
    
    /**
     * Reads data files listed in the configuration file.
     *
     */
    private void readData() {
    	String[] files = config.getStringArray(DATA_FILES);
    	if (files != null) {
    		for (int i = 0; i < files.length; i++) {
				read(files[i]);
			}
    	}
    }
    
    private Set<RDFNode> search(IndexLARQ index, String query) {
		Set<RDFNode> nodeSet = new HashSet<RDFNode>();
		if (index != null) {
			NodeIterator nit = index.searchModelByIndex(query);		
			for (; nit.hasNext();) {
				RDFNode node = nit.nextNode();
				nodeSet.add(node);
			}
		}		
		return nodeSet;
	}
    
    public Model getModel() {
    	return model;
    }
	
	/**
	 * Performs a free text Lucene search and returns an array of RDF nodes.
	 *
	 * @param query The query
	 * @return An array of RDF nodes as the search result
	 */
	public RDFNode[] search(String query) {
		Set<RDFNode> nodeSet = new HashSet<RDFNode>();
		for (IndexLARQ index : indexes) {
			nodeSet.addAll(search(index, query));
		}
		return nodeSet.toArray(RDF_NODES);
	}
	

	public boolean isChinese() {
		return isChinese;
	}

	public void setChinese(boolean isChinese) {
		this.isChinese = isChinese;
	}

	// TODO
	public String fixPrefix(String sparql) {
		if (this.isChinese)
			return this.YUETAN_PREFIX + sparql;
		return DEFAULT_PREFIX + sparql;
	}
	
	public static boolean isLegalSparql(String sparql) {
		return sparql != null && sparql.length() > 20;
	}
	
	public ResultSet query(String sparql) {
		if (!isLegalSparql(sparql)) {
			return null;
		}
		
		sparql = fixPrefix(sparql);
		try {
			Query q = QueryFactory.create(sparql, Syntax.syntaxARQ);
			// Execute the query and obtain results
			QueryExecution qe = QueryExecutionFactory.create(q, model);
			ResultSet results = qe.execSelect();
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		return null;
    }
	
	public List<String> getResults(ResultSet resultSet) {
		List<String> results = new ArrayList<String>();
		if (resultSet != null) {
			List<String> vars = resultSet.getResultVars();
			String var = vars.get(0);
			while (resultSet.hasNext()) {
				QuerySolution result = resultSet.next();
				//results.add(result.toString());
				RDFNode node = result.get(var);
				if (node != null) {
					results.add(node.toString());
				}			
			}
		}
		return results;
	}
	
	public List<String> getResults(String sparql) {
		ResultSet resultSet = query(sparql);
		List<String> results = getResults(resultSet);
		return results;
	}
	
	public List<RDFNode> getResultNodes(String sparql) {
		ResultSet resultSet = query(sparql);
		if (resultSet != null) {
			List<String> vars = resultSet.getResultVars();
			if (vars != null && vars.size() > 0) {
				// Fetch the first node
				String var = vars.get(0);
				List<RDFNode> results = new ArrayList<RDFNode>();
				while (resultSet.hasNext()) {
					QuerySolution solution = resultSet.next();
					RDFNode node = solution.get(var);
					if (node != null) {
						results.add(node);
					}			
				}
				if (results.size() > 0) {
					return results;
				}
			}	
		}
		return null;
	}
	
	
	public Resource getResource(String uri) {
		return model.getResource(uri);
	}
	
	public StmtIterator listStatementsWithSubject(Resource subject) {
		StmtIterator sit = model.listStatements(subject, null, (RDFNode)null);
		return sit;
	}
	
	public StmtIterator listStatementsWithSubject(String subjectUri) {
		Resource subject = getResource(subjectUri);
		return listStatementsWithSubject(subject);
	}
	
	public String getName(RDFNode node) {
		if (node instanceof Resource) {
			String label = getLabel((Resource)node);
			if (label != null) {
				return label;
			} else {	// if node is rdfs:label, rdf:type
				return Util.lastWord(node);
			}
		} else if (node instanceof Literal) {
			Literal literal = (Literal)node;
			return literal.getString();
		}
		return Util.lastWord(node);
	}
	
	public List<Statement> getStatements(StmtIterator sit) {
		if (sit == null) return null;
		
		List<Statement> stmts = new ArrayList<Statement>();
		while (sit.hasNext()) {
			Statement stmt = sit.next();
			stmts.add(stmt);
		}
		return stmts;
	}
	
	// TODO 值相同的Literal即相等，故不能用以区分subject
	public Resource getSubjectWithLiteral(Literal literal) {
		StmtIterator sit = model.listStatements(null, null, literal);
		List<Statement> stmts = getStatements(sit);
		if (stmts != null && stmts.size() > 0) {
			return stmts.get(0).getSubject();
		}
		return null;
	}
	
	/**
     * Returns the label of a given resource.
     *
     * @param resource The given resource
     * @return The type resource or <code>null</code> if not exists
     */
    public String getLabel(Resource resource) {
    	if (resource == null) return null;
    	
    	NodeIterator nit = model.listObjectsOfProperty(resource, labelProperty);
    	while (nit.hasNext()) {
    		RDFNode node = nit.next();
    		if (node instanceof Literal) {
    			String label = ((Literal)node).getString();
    			if (label != null) {
    				return label;
    			}
			}
    	}
    	return null;
    }
    
    public String getLabel(RDFNode node) {
    	if (node instanceof Resource) {
    		return getLabel((Resource)node);
    	} else {
    		return null;
    	}
    }
    
    /**
     * Returns the type resource of a given resource.
     *
     * @param resource The given resource
     * @return The type resource or <code>null</code> if not exists
     */
    public Resource getType(Resource resource) {
    	if (resource == null) return null;
    	
    	NodeIterator nit = model.listObjectsOfProperty(resource, typeProperty);
    	while (nit.hasNext()) {
    		RDFNode node = nit.next();
    		if (node instanceof Resource) {
				return (Resource)node;
			}
    	}
    	return null;
    }
    
    /**
     * Returns the label of the type resource of a given resource.
     *
     * @param resource The given resource
     * @return The label of the type resource or <code>null</code> if not exists
     */
    public String getTypeLabel(Resource resource) {
    	Resource type = getType(resource);
    	return getLabel(type);
    }
    
    /**
     * Returns an iterator over all the statements with the label property.
     *
     * @return The iterator
     */
    public StmtIterator listStatementsWithLabel() {
    	StmtIterator sit = model.listStatements(null, labelProperty, (RDFNode)null);
    	return sit;
    }
    
    /**
     * Determines whether a node is a property.
     *
     * @param node the node
     * @return <code>true</code> if the node is a property
     */
    public boolean isProperty(RDFNode node) {
		if (node instanceof Resource) {
			Resource type = getType((Resource)node);
			return type.equals(propertyClass);
		}
		return false;
	}
    
    /**
     * Transforms a node to a property.
     *
     * @param node the node
     * @return the transformed property or <code>null<code> if the node 
     * is not a property
     */
    public Property asProperty(RDFNode node) {
    	if (isProperty(node)) {
			String uri = ((Resource)node).getURI();
			return model.getProperty(uri);
		}
    	return null;
    }
    
    public Resource asResource(Property property) {
    	String uri = property.getURI();
		return model.getResource(uri);
    }
    
    public boolean isClass(RDFNode node) {
    	if (node instanceof Resource) {
			Resource type = getType((Resource)node);
			return classClass.equals(type);
		}
		return false;
    }
    
    public boolean isLiteralClass(RDFNode node) {
    	return literalClass.equals(node);
    }
    
    public boolean isLabelClass(Resource resource) {
    	return resource.getURI().equals(RDFS_LABEL_URI);
    }
    
    public boolean isComparableLiteral(RDFNode node) {
		if (node instanceof Literal) {
			String datatype = ((Literal)node).getDatatypeURI();
			return comparableDatatypeSet.contains(datatype);
		}
		return false;
	}
    
    public RDFNodeType getRDFNodeType(RDFNode node) {
    	if (node instanceof Literal) {
    		return RDFNodeType.LITERAL;
    	}
    	if (isProperty(node)) {
    		return RDFNodeType.PROPERTY;
    	}
    	if (isClass(node)) {
    		return RDFNodeType.CLASS;
    	}
    	return RDFNodeType.INSTANCE;
    }
    
    public boolean isInstanceOf(Resource instance, Resource c) {
    	Resource type = getType(instance);
    	return type.equals(c);
    }
    
    public boolean isLabelProperty(RDFNode node) {
    	return labelProperty.equals(node);
    }
    
    public SchemaGraph getSchemaGraph() {
    	if (schemaGraph == null) {
    		schemaGraph = new SchemaGraph(this);
    	}
    	return schemaGraph;
    }
    
    private boolean doUpdate(Map<RDFNode, Double> scoreMap, 
    		PriorityQueue<ScoredNode> pq, RDFNode node, double score, int step, int maxStep,
    		Map<RDFNode, Statement> edgeMap, Statement stmt) {
    	if ((step > maxStep)
    			|| (scoreMap.containsKey(node) && scoreMap.get(node) <= score)) {
    		return false;
    	}
    	scoreMap.put(node, score);
    	pq.add(new ScoredNode(node, score, step));
    	edgeMap.put(node, stmt);
    	return true;
    }
    
    /**
     * Returns the shortest path between two RDF nodes.
     * <p>
     * Uses A* search algorithm.
     *
     * @param clause the clause relevant to those two nodes
     * @param src the source node
     * @param des the destination node
     * @param maxStep maximum steps can search from the source node
     * @return the shortest path between the two nodes or <code>null</code> 
     * if not exists
     */
    public Path findShortestPath(Clause clause, RDFNode src, RDFNode des, int maxStep) {    	
    	Map<RDFNode, Statement> edgeMap = new HashMap<RDFNode, Statement>();
    	Map<RDFNode, Double> scoreMap = new HashMap<RDFNode, Double>();
    	
    	PriorityQueue<ScoredNode> pq = new PriorityQueue<ScoredNode>();
    	
    	//pq.add(new ScoredNode(src, 0, 0));
    	doUpdate(scoreMap, pq, src, 0, 0, maxStep, edgeMap, null);
    	
    	// TODO
    	// If src is a Property, add all the subjects and objects of it as search seeds
    	Property srcProperty = asProperty(src);
    	if (srcProperty != null) {
    		StmtIterator sit = model.listStatements(null, srcProperty, (RDFNode)null);
			while (sit.hasNext()) {
				Statement stmt = sit.next();
				Resource subject = stmt.getSubject();
				RDFNode object = stmt.getObject();
				doUpdate(scoreMap, pq, subject, 0, 0, maxStep, edgeMap, stmt);
				doUpdate(scoreMap, pq, object, 0, 0, maxStep, edgeMap, stmt);
			}
    	}
    	
    	while (!pq.isEmpty()) {
    		ScoredNode cur = pq.poll();
    		System.out.println(cur);	// debug
    		RDFNode node = cur.getNode();
    		
    		// Check if des is a resource
    		if (node.equals(des)) {
				return Path.build(edgeMap, src, des);
			}
    		
    		// Check if des is a property
    		Statement statement = edgeMap.get(node);
    		if (statement != null) {
    			Property property = statement.getPredicate();
    			//System.out.println(property);	// debug
    			if (property.equals(des)) {
    				return Path.build(edgeMap, src, node);
    			}
    		}			
    		
    		// Extends statements in which this node is subject
    		if (node instanceof Resource) {
				StmtIterator sit = model.listStatements((Resource)node, null, (RDFNode)null);
				while (sit.hasNext()) {
					Statement stmt = sit.next();
					RDFNode object = stmt.getObject();
					double score = cur.getScore() + Path.DEFAULT_STATEMENT_SCORE;	// TODO
					int step = cur.getStep() + 1;
					doUpdate(scoreMap, pq, object, score, step, maxStep, edgeMap, stmt);
				}
			}
    		// Extends statements in which this node is object
    		StmtIterator sit = model.listStatements(null, null, node);
			while (sit.hasNext()) {
				Statement stmt = sit.next();
				Resource subject = stmt.getSubject();
				double score = cur.getScore() + Path.DEFAULT_STATEMENT_SCORE;	// TODO
				int step = cur.getStep() + 1;
				doUpdate(scoreMap, pq, subject, score, step, maxStep, edgeMap, stmt);
			}
    	}
    	
    	return null;
    }
}

class ScoredNode implements Comparable<ScoredNode> {
	
	private RDFNode node;
	
	private double score;
	
	private int step;
	
	/**
	 * Creates a new instance of ScoredNode.
	 *
	 */
	public ScoredNode(RDFNode node, double scroe, int step) {
		setNode(node);
		setScore(scroe);
		setStep(step);
	}
	
	/**
	 * Get the node.
	 *
	 * @return The node
	 */
	public RDFNode getNode() {
		return node;
	}
	/**
	 * Set the node.
	 *
	 * @param node The node to set
	 */
	public void setNode(RDFNode node) {
		this.node = node;
	}
	/**
	 * Get the score.
	 *
	 * @return The score
	 */
	public double getScore() {
		return score;
	}
	/**
	 * Set the score.
	 *
	 * @param score The score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * Set the step.
	 *
	 * @param step The step to set
	 */
	public void setStep(int step) {
		this.step = step;
	}

	/**
	 * Get the step.
	 *
	 * @return The step
	 */
	public int getStep() {
		return step;
	}

	/**
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(ScoredNode other) {
		if (score < other.score) return -1;
		else if (score > other.score) return 1;
		return 0;
		
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[").append(node).append(", ");
		sb.append(score).append(", ");
		sb.append(step).append("]");
		return sb.toString();
	}
}
