/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.demo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.util.ObjectToSet;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * The information table of a series of RDFNodes.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-21
 */

public class RDFNodesTable {
	
	public static final String EMPTY_CELL = "";
	public static final String ELLIPSIS = "...";
	public static final String SEPARATOR = ", ";
	
	public static final int MAX_ELEMENTS_IN_CELL = 3;
	
	private Ontology ontology;

	private List<RDFNode> nodes;
	
	private Set<Resource> propertySet;
	private List<Resource> properties;
	private List<ObjectToSet<Resource, RDFNode>> rows;
	
	private List<ArrayList<String>> table;
	private List<String> heads;
	
	public RDFNodesTable(Ontology ontology, List<RDFNode> nodes) {
		this.ontology = ontology;
		setNodes(nodes);
		build();
	}

	/**
	 * Set the nodes.
	 *
	 * @param nodes The nodes to set
	 */
	public void setNodes(List<RDFNode> nodes) {
		this.nodes = nodes;
	}

	/**
	 * Get the nodes.
	 *
	 * @return The nodes
	 */
	public List<RDFNode> getNodes() {
		return nodes;
	}
	
	private void init() {
		propertySet = new HashSet<Resource>();
		rows = new ArrayList<ObjectToSet<Resource,RDFNode>>();
	}
	
	private void addRow(Resource subject) {
		ObjectToSet<Resource, RDFNode> row = new ObjectToSet<Resource, RDFNode>();
		
		StmtIterator sit = ontology.listStatementsWithSubject(subject);
		List<Statement> stmts = ontology.getStatements(sit);
		if (stmts != null) {
			for (Statement stmt : stmts) {
				Property prop = stmt.getPredicate();
				RDFNode obj = stmt.getObject();
				row.addMember(prop, obj);
				
				//if (prop == null) System.out.println("@addRow stmt: " + stmt);	// debug
				propertySet.add(prop);
			}
		}
		rows.add(row);
	}
	
	private void setHeads() {
		// donn't show rdfs:label property
		propertySet.remove(ontology.labelProperty);
		properties = new ArrayList<Resource>(propertySet);
		
		// put hasNameProperty as the first property if it's existed
		if (properties.remove(ontology.hasNameProperty)) {
			properties.add(0, ontology.hasNameProperty);
		}		
		
		heads = new ArrayList<String>();
		for (Resource property : properties) {
			if (ontology.isLabelClass(property)) continue;
			
			String head = ontology.getName(property);
			//if (head == null) System.out.println("@setHeads head is null. property: " + property);	// debug
			heads.add(head);
		}
	}
	
	private void build() {
		init();
		
		for (RDFNode node : nodes) {
			if (node instanceof Resource) {
				Resource resource = (Resource)node;
				addRow(resource);
			} else if (node instanceof Literal) {
				/*Literal literal = (Literal)node;
				Resource subject = ontology.getSubjectWithLiteral(literal);
				addRow(subject);*/
			}
		}
		
		setHeads();
		
		int numRow = getNumRow();
		table = new ArrayList<ArrayList<String>>(numRow);
		int numCol = getNumColumn();
		
		for (ObjectToSet<Resource, RDFNode> row : rows) {
			ArrayList<String> strRow = new ArrayList<String>(numCol);
			for (Resource property : properties) {
				Set<RDFNode> objects = row.get(property);
				if (objects != null) {
					List<String> names = new ArrayList<String>();
					Iterator<RDFNode> it = objects.iterator();
					int cnt = 0;
					while (it.hasNext()) {
						RDFNode object = it.next();
						String name = ontology.getName(object);
						names.add(name);
						if (++cnt >= MAX_ELEMENTS_IN_CELL) {
							break;
						}
					}
					if (cnt < objects.size()) {
						names.add(ELLIPSIS);
					}
					String cell = StringUtils.join(names, SEPARATOR);
					strRow.add(cell);
				} else {
					strRow.add(EMPTY_CELL);
				}
			}
			table.add(strRow);
		}
	}
	
	public List<ArrayList<String>> getTable() {
		return table;
	}
	
	public List<String> getHeads() {
		return heads;
	}
	
	public List<String> getRow(int index) {
		return table.get(index);
	}
	
	public int getNumRow() {
		return rows.size();
	}
	
	public int getNumColumn() {
		return heads.size();
	}
}
