/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.ir.demo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openjena.atlas.json.JsonArray;
import org.openjena.atlas.json.JsonObject;

import cn.edu.hit.ir.ontology.Ontology;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import edu.stanford.nlp.trees.TypedDependency;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年6月15日 
 */
public class JsonResult {
	// json keys 
	private static final String ANWSER = "answer";
	private static final String SPARQL = "sparql";
	private static final String INTERPRETATION = "interpretation";
	private static final String DEPENDENCY = "dependency";
	private static final String EDGE = "edge";
	private static final String SEPARATOR = ", ";
	
	private Ontology ontology;
	private List<RDFNode> result;
	private JsonObject jsonResult;
	private String sparql;
	private List<TypedDependency> dependency;
	private List<Object> path;
	
	JsonResult (Ontology ontology, List<RDFNode> result, String sparql, List<Object> path, List<TypedDependency> dependency ) {
		this.ontology = ontology;
		this.result = result;
		this.jsonResult = new JsonObject();
		this.sparql = sparql;
		this.dependency = dependency;
		this.path = path;
	}
	
	private void addAnswer () {
		this.jsonResult.put(this.ANWSER, this.answerNodesString());
		this.jsonResult.put(this.SPARQL, this.sparql);
		this.jsonResult.put(this.INTERPRETATION, interpretation());
		this.jsonResult.put(this.DEPENDENCY, dependency());
	}
	
	private List<String> getNames(List<RDFNode> nodes) {
		if (nodes == null) return null;
		
		List<String> names = new ArrayList<String>(nodes.size());
		for (RDFNode node : nodes) {
			names.add(ontology.getName(node));
		}
		return names;
	}
	private String answerNodesString() {
		List<String> names = getNames(result);
		String answer = StringUtils.join(names, SEPARATOR);
		return answer;
	}
	
	private JsonArray interpretation () {
		if (this.path == null || this.path.isEmpty())
			return null;
		JsonArray res = new JsonArray();
		for (Object obj : this.path) {
			res.add(this.ontology.getName(((Resource)obj)));
		}
		return res;
	}
	
	private JsonObject dependency () {
		if (this.dependency == null) return null;
		
		JsonObject jsonObj = new JsonObject();
		JsonArray edges = new JsonArray();
		for (TypedDependency dep : this.dependency) {
			edges.add(this.constructEdge(dep));
		}
		jsonObj.put(this.EDGE, edges);
		return jsonObj;
	}
	
	private String constructEdge (TypedDependency dep ) {
		if (dep == null ) return null;
		
		return dep.gov().toString() + "-" + dep.gov().index() + "->" + dep.reln().toString() + "->" + dep.dep().toString() + "-" + dep.dep().index();
	}

	public JsonObject getJsonResult() {
		return jsonResult;
	}

	public void setJsonResult(JsonObject jsonResult) {
		this.jsonResult = jsonResult;
	}
	
	
}
