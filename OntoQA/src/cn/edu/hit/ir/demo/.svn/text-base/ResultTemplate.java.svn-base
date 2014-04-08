/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.demo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.edu.hit.ir.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * The template to generate result for the demo.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-21
 */

public class ResultTemplate {
	
	public static final String DIV_TAG = "div";
	public static final String PRE_TAG = "pre";
	public static final String TABLE_TAG = "table";
	public static final String TR_TAG = "tr";
	public static final String TH_TAG = "th";
	public static final String TD_TAG = "td";
		
	public static final String ID_ATTR = "id";
	public static final String CLASS_ATTR = "class";
	public static final String BORDER_ATTR = "border";
	
	public static final String BORDER_VALUE = "1";
	
	public static final String RESULTS = "results";
	public static final String RESULT = "result";
	public static final String RESULT_TITLE = "result-title";
	public static final String RESULT_CONTENT = "result-content";
	
	public static final String ANSWER = "Answer";
	public static final String ENTITIES = "Entities";
	public static final String SPARQL = "Sparql";
	public static final String GRAPH = "Graph";
	
	public static final String NO_ANSWER = "No answer";
	public static final String NO_ANSWER_MSG = "Please rewrite your query.";
	
	public static final String SEPARATOR = ", ";
	
	private static String NO_ANSWER_RESULT;
	
	static {
		Document document = new Document("");
		Element results = document.prependElement(DIV_TAG).attr(CLASS_ATTR, RESULTS);
		Element result = results.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT);
		result.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT_TITLE).text(NO_ANSWER);
		result.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT_CONTENT).text(NO_ANSWER_MSG);
		NO_ANSWER_RESULT = results.toString();
	}
	
	private Ontology ontology;
	
	private Document document;
	private Element results;
	
	public ResultTemplate(Ontology ontology) {
		this.ontology = ontology;
		
		document = new Document("");
		results = document.prependElement(DIV_TAG).attr(CLASS_ATTR, RESULTS);
	}
	
	public void addAnswer(String answer) {
		Element result = results.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT);
		result.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT_TITLE).text(ANSWER);
		result.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT_CONTENT).text(answer);
	}
	
	public void addAnswer(List<String> answers) {
		String answer = StringUtils.join(answers, SEPARATOR);
		Element result = results.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT);
		result.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT_TITLE).text(ANSWER);
		result.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT_CONTENT).text(answer);
	}
	
	public List<String> getNames(List<RDFNode> nodes) {
		if (nodes == null) return null;
		
		List<String> names = new ArrayList<String>(nodes.size());
		for (RDFNode node : nodes) {
			names.add(ontology.getName(node));
		}
		return names;
	}
	
	public void addAnswerNodes(List<RDFNode> answers) {
		List<String> names = getNames(answers);
		String answer = StringUtils.join(names, SEPARATOR);
		Element result = results.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT);
		result.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT_TITLE).text(ANSWER);
		result.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT_CONTENT).text(answer);
	}
	
	public void addSparql(String sparql) {
		Element result = results.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT);
		result.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT_TITLE).text(SPARQL);
		result.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT_CONTENT)
			.appendElement(PRE_TAG).text(sparql);
	}
	
	public void addEntityTable(RDFNodesTable table) {
		if (table == null || table.getNumRow() <= 0) return;
		
		Element result = results.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT);
		result.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT_TITLE).text(ENTITIES);
		Element tableElement = result.appendElement(DIV_TAG).attr(CLASS_ATTR, RESULT_CONTENT)
			.appendElement(TABLE_TAG).attr(BORDER_ATTR, BORDER_VALUE);
		
		Element trHead = tableElement.appendElement(TR_TAG);
		List<String> heads = table.getHeads();
		System.out.println("@addEntityTable heads: " + heads);	// debug
		for (String head : heads) {
			trHead.appendElement(TH_TAG).text(head);
		}
		
		for (int i = 0; i < table.getNumRow(); ++i) {
			List<String> row = table.getRow(i);
			Element tr = tableElement.appendElement(TR_TAG);
			for (String cell : row) {
				tr.appendElement(TD_TAG).text(cell);
			}
		}
	}
	
	public static String getNoAnswerResult() {
		return NO_ANSWER_RESULT;
	}
	
	public String toString() {
		return results.toString();
	}

}
