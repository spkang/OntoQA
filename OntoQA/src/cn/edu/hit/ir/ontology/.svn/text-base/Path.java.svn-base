/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * A path between two nodes in ontologies.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-28
 */

public class Path {
	
	public static final int DEFAULT_MAX_STEP = 5;
	public static final double DEFAULT_STATEMENT_SCORE = 1.0;
	
	public static Ontology ontology = Ontology.getInstance();
	
	private List<Statement> statements;
	
	private List<String> labels;
	
	public static Path build(Map<RDFNode, Statement> edgeMap, 
			RDFNode src, RDFNode des) {	
		List<Statement> stmts = new ArrayList<Statement>();
		List<String> labels = new ArrayList<String>();
		int step = 0;
		for (RDFNode u = des; !u.equals(src); ) {
			if (step++ > DEFAULT_MAX_STEP) {
				return null;
			}
			Statement stmt = edgeMap.get(u);
			stmts.add(stmt);
			String label = ontology.getLabel(u);
			labels.add(label);
			label = ontology.getLabel(stmt.getPredicate());
			labels.add(label);
			
			u = otherNode(stmt, u);
		}
		labels.add(ontology.getLabel(src));
		
		Collections.reverse(stmts);
		Collections.reverse(labels);
		Path path = new Path(stmts, labels);
		return path;
	}
	
	private static RDFNode otherNode(Statement statement, RDFNode node) {
		RDFNode object = statement.getObject();
		if (node.equals(object)) {
			return statement.getSubject();
		} else {
			return object;
		}
	}
	
	public Path(List<Statement> statements, List<String> labels) {
		setStatements(statements);
		setLabels(labels);
	}
	
	/**
	 * Set the statements.
	 *
	 * @param statements The statements to set
	 */
	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	/**
	 * Get the statements.
	 *
	 * @return The statements
	 */
	public List<Statement> getStatements() {
		return statements;
	}
	
	/**
	 * Set the labels.
	 *
	 * @param labels The labels to set
	 */
	public void setLabels(List<String> labels) {
		this.labels = new ArrayList<String>();
		for (String label : labels) {
			// Don't add null string
			if (label != null) {
				this.labels.add(label);
			}
		}
	}

	/**
	 * Get the labels.
	 *
	 * @return The labels
	 */
	public List<String> getLabels() {
		return labels;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("statements: \n");
		for (Statement statement : statements) {
			sb.append(statement).append("\n");
		}
		sb.append("labels: \n");
		sb.append(labels).append("\n");
		return sb.toString();
	}
}
