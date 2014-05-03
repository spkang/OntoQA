/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ontologymatch;

import java.util.List;

import cn.edu.hit.ir.graph.QueryGraph;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.scir.EntityMatcher.QueryMatchedEntityWrapper;

/**
 * a query analyzer for matching the 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月16日 
 */
public class QueryAnalyzer {
	private Ontology ontology = Ontology.getInstance();
	private GenerateGraph generateGraph;
	private GenerateSparql sparqlGenerator;
	private QueryMatchedEntityWrapper queryMeWrapper = null;
	public QueryAnalyzer () {
		initResource ();
	}
	
	public QueryAnalyzer (String sentence) {
		queryMeWrapper = new QueryMatchedEntityWrapper (sentence);
	}
	
	private void initResource  () {
		generateGraph = new GenerateGraph (ontology);
		sparqlGenerator = new GenerateSparql (ontology);
		sparqlGenerator.addPrefix("http://ir.hit.edu/nli/geo/", "geo");
	}
	
	
	public QueryMatchedEntityWrapper getQueryMatchedEntityWrapper (String sentence) {
		if (sentence == null ) return null;
		queryMeWrapper = new QueryMatchedEntityWrapper (sentence); 
		System.out.println("query matched entity : " + queryMeWrapper.toString());
		return queryMeWrapper;
	}	
	public GenerateGraph getGenerateGraph (String sentence) {
		if (sentence == null )
			return null;
		
		queryMeWrapper = getQueryMatchedEntityWrapper (sentence);
		if (this.generateGraph == null )
			this.generateGraph = new GenerateGraph (this.ontology);
		return this.generateGraph;
	}
	
	public GenerateGraph getGenerateGraph () {
		return this.generateGraph;
	}
	
	public QueryGraph getQueryGraph (String sentence) {
		if (sentence == null )
			return null;
		return this.generateGraph.optionalMatch(this.getQueryMatchedEntityWrapper(sentence));
	}

	public String getSparql (String sentence ) {
		if (sentence == null)
			return null;
		QueryGraph  queryGraph  = this.getQueryGraph(sentence);
		System.out.println ("queryGraph : " + queryGraph);
		if (queryGraph == null )
			return null;
		return sparqlGenerator.generate(queryGraph,this.queryMeWrapper.getDepGraph().getVertexs(), this.queryMeWrapper);
	}
	
	public Object analyze(String question) {
		if (question == null )
			return null;
		String sparql = getSparql(question);
		System.out.println ("sparql : " + sparql);
		List<String> results = ontology.getResults(sparql);
		return results;
	}
}
