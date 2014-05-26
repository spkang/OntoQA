/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ProbabilityGraph;

import java.util.List;

import cn.edu.hit.ir.graph.QueryGraph;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.scir.EntityMatcher.QueryMatchedEntityWrapper;
import cn.edu.hit.scir.ontologymatch.GenerateGraph;
import cn.edu.hit.scir.ontologymatch.GenerateSparql;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月26日 
 */
public class ProbabilityGraphAnalyzer {
	private Ontology ontology = Ontology.getInstance();
	private GenerateSparql sparqlGenerator;
	private ProbabilityGraph probGraph = null;
	
	public ProbabilityGraphAnalyzer () {
		initResource ();
	}
	
//	public QueryAnalyzer (String sentence) {
//		queryMeWrapper = new QueryMatchedEntityWrapper (sentence);
//	}
	
	private void initResource  () {
		sparqlGenerator = new GenerateSparql (ontology);
		sparqlGenerator.addPrefix("http://ir.hit.edu/nli/geo/", "geo");
	}
	
	
	
	public QueryGraph getQueryGraph (String sentence) {
		if (sentence == null )
			return null;
		this.probGraph = new ProbabilityGraph (sentence);
		return this.probGraph.getQueryGraph();
	}

	public String getSparql (String sentence ) {
		if (sentence == null)
			return null;
		QueryGraph  queryGraph  = this.getQueryGraph(sentence);
		System.out.println ("queryGraph : " + queryGraph);
		if (queryGraph == null )
			return null;
		return sparqlGenerator.generate(queryGraph,this.probGraph.getEntityWrapper().getDepGraph().getVertexs(), this.probGraph.getEntityWrapper());
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
