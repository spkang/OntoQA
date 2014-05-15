/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ChineseEngine;

import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.graph.QueryGraph;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.scir.EntityMatcher.ChineseQueryMatchedEntityWrapper;
import edu.stanford.nlp.util.StringUtils;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月14日 
 */

public class ChineseQueryAnalyzer {
	private Ontology ontology;
	private GenerateChineseGraph generateGraph;
	private GenerateChineseSparql sparqlGenerator;
	private ChineseQueryMatchedEntityWrapper queryMeWrapper = null;
	private static Logger logger = Logger.getLogger(ChineseQueryAnalyzer.class);
	
	public ChineseQueryAnalyzer () {
		initResource ();
	}
	
	private void initResource  () {
		ontology = Ontology.getInstance();
		ontology.setChinese(true);
		
		generateGraph = new GenerateChineseGraph (ontology);
		sparqlGenerator = new GenerateChineseSparql (ontology);
		sparqlGenerator.addPrefix("http://ir.hit.edu/nli/yuetan/", "yuetan");
	}
	
	
	public ChineseQueryMatchedEntityWrapper getChineseQueryMatchedEntityWrapper (String sentence) {
		if (sentence == null ) return null;
		queryMeWrapper = new ChineseQueryMatchedEntityWrapper (sentence); 
		logger.info("query matched entity : ");
		for (List<MatchedEntity> mes : queryMeWrapper.getMatchedQueryWrapper()) {
			logger.info("me : " + StringUtils.join(mes, ", "));
		}
		return queryMeWrapper;
	}	
	public GenerateChineseGraph getGenerateGraph (String sentence) {
		if (sentence == null )
			return null;
		
		queryMeWrapper = getChineseQueryMatchedEntityWrapper (sentence);
		if (this.generateGraph == null )
			this.generateGraph = new GenerateChineseGraph (this.ontology);
		return this.generateGraph;
	}
	
	public GenerateChineseGraph getGenerateChineseGraph () {
		return this.generateGraph;
	}
	
	public QueryGraph getQueryGraph (String sentence) {
		if (sentence == null )
			return null;
		return this.generateGraph.optionalMatch(this.getChineseQueryMatchedEntityWrapper(sentence));
	}

	public String getSparql (String sentence ) {
		if (sentence == null)
			return null;
		QueryGraph  queryGraph  = this.getQueryGraph(sentence);
		System.out.println ("queryGraph : " + queryGraph);
		if (queryGraph == null )
			return null;
		return sparqlGenerator.generate(queryGraph,this.queryMeWrapper.getCnStanfordBasedGraph().getCnBasedGraph().getVertexs(), this.queryMeWrapper);
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

