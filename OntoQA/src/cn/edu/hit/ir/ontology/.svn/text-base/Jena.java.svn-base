/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.ontology;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
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
 * A wrapper of Jena.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-15
 */

public class Jena {
	
	public static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	public static final String RDFS_LABEL_URI = "http://www.w3.org/2000/01/rdf-schema#label";

	public static final int MAX_ANSWERS = 10240;
	
	private static final RDFNode[] RDF_NODES = new RDFNode[0];
	
	private Property typeProperty;
	private Property labelProperty;
	
    Model model;
        
    List<IndexLARQ> indexes;
    
    /**
	 * Creates a new instance of Jena.
	 *
	 */
	public Jena() {
		initModel();
		initProperties();
	}
    
    private void initModel() {
    	model = ModelFactory.createDefaultModel();
    	indexes = new ArrayList<IndexLARQ>();
    }
    
    private void initProperties() {
    	typeProperty = model.getProperty(RDF_TYPE_URI);
    	labelProperty = model.getProperty(RDFS_LABEL_URI);
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
    
    public String query(String queryString, OutputStream out) {
    	Query query = QueryFactory.create(queryString);
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();
		// Output query results	
		ResultSetFormatter.out(out, results, query);
		//ResultSetFormatter.out(System.out, results, query);
		// Important - free up resources used running the query
		qe.close();
		
		return results.toString();
    }
    
    public String query(String query) {
    	Query q = QueryFactory.create(query);
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(q, model);
		ResultSet results = qe.execSelect();
		// Output query results	
		//ResultSetFormatter.out(System.out, results, query);

		String resultString = "";
		while (results.hasNext()) {
			QuerySolution result = results.next();
			resultString += result.toString() + "\n";
		}
		
		qe.close();
		
		return resultString;
    }
    
	public Set<RDFNode> search(IndexLARQ index, String query) {
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
    
    public void bfs(Resource resource, int maxAnswers) {
    	StmtIterator iterator = model.listStatements(resource, null, (RDFNode)null);
    	while (iterator.hasNext()) {
    		Statement statement = iterator.nextStatement();
    		System.out.println(statement);
		}
    	iterator = model.listStatements(null, null, resource);
    	while (iterator.hasNext()) {
    		Statement statement = iterator.nextStatement();
    		System.out.println(statement);
		}
    }
    
    public void bfs(Resource resource) {
    	bfs(resource, MAX_ANSWERS);
    }
    
    public Model getModel() {
    	return model;
    }
    
    /**
     * Returns all the object rdf nodes of the statement whose subject is
     * the given resource and predicate is the given property.
     * The objects includes literal and resource.
     *
     * @param resource The given resource
     * @param property The given property
     * @return The list of all the objects
     */
    public List<RDFNode> getPropertyRdfNodes(Resource resource, Property property) {
    	StmtIterator sit = resource.listProperties(property);
    	List<RDFNode> nodes = new ArrayList<RDFNode>();
    	while (sit.hasNext()) {
    		Statement stmt = sit.next();
    		nodes.add(stmt.getObject());
    	}
    	return nodes;
    }
    
    /**
     * Returns all the object literals of the statement whose subject is 
     * the given resource and predicate is the given property.
     *
     * @param resource The given resource
     * @param property The given property
     * @return The list of all the object literals
     */
    public List<Literal> getPropertyLiterals(Resource resource, Property property) {
    	List<RDFNode> nodes = getPropertyRdfNodes(resource, property);
    	List<Literal> literals = new ArrayList<Literal>();
    	for (RDFNode node:nodes) {
    		if (node instanceof Literal) {
    			literals.add((Literal)node);
    		}
    	}
    	return literals;
    }
    
    /**
     * Returns all the object resources of the statement whose subject is
     * the given resource and predicate is the given property.
     *
     * @param resource The given resource
     * @param property The given property
     * @return The list of all the object resources
     */
    public List<Resource> getPropertyResources(Resource resource, Property property) {
    	List<RDFNode> nodes = getPropertyRdfNodes(resource, property);
    	List<Resource> resources = new ArrayList<Resource>();
    	for (RDFNode node:nodes) {
    		if (node instanceof Resource) {
    			resources.add((Resource)node);
    		}
    	}
    	return resources;
    }
    
    /**
     * Returns the label of a given resource.
     *
     * @param resource The given resource
     * @return The type resource or <code>null</code> if not exists
     */
    public String getLabel(Resource resource) {
    	List<Literal> labels = getPropertyLiterals(resource, labelProperty);
    	if (labels == null || labels.size() == 0) {
    		return null;
    	} else {
    		return labels.get(0).getString();
    	}
    }
    
    /**
     * Returns the type resource of a given resource.
     *
     * @param resource The given resource
     * @return The type resource or <code>null</code> if not exists
     */
    public Resource getType(Resource resource) {
    	List<Resource> types = getPropertyResources(resource, typeProperty);
    	if (types == null || types.size() == 0) {
    		return null;
    	} else {
    		return types.get(0);
    	}
    }
    
    /**
     * Returns the label of the type resource of a given resource.
     *
     * @param resource The given resource
     * @return The label of the type resource or <code>null</code> if not exists
     */
    public String getTypeLabel(Resource resource) {
    	Resource type = getType(resource);
    	if (type == null) {
    		return null;
    	} else {
    		return getLabel(type);
    	}
    }
    
    /**
     * Returns a list of all the statements with the label property.
     *
     * @return The list of all the statements with the label property
     */
    public List<Statement> getLabeledStatements() {
    	List<Statement> statements = new ArrayList<Statement>();
    	StmtIterator sit = model.listStatements(null, labelProperty, (RDFNode)null);
    	while (sit.hasNext()) {
    		statements.add(sit.next());
    	}
    	return statements;
    }
}
