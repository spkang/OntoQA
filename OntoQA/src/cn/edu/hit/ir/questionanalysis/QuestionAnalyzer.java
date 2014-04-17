/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.questionanalysis;

import java.util.List;

import cn.edu.hit.ir.dict.EntityMatcher;
import cn.edu.hit.ir.dict.MatchedEntitiesSentence;
import cn.edu.hit.ir.nlp.EnglishNlpTool;
import cn.edu.hit.ir.nlp.NlpSentence;
import cn.edu.hit.ir.nlp.NlpTool;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.ontology.SparqlGenerator;
import cn.edu.hit.ir.graph.GraphSearcher;
import cn.edu.hit.ir.graph.QueryGraph;

/**
 * The question analyzer.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-28
 */

public class QuestionAnalyzer {
	
	private NlpTool nlpTool;
	
	private Ontology ontology;	
	
	private QuestionNormalizer questionNormalizer;
	
	private EntityMatcher entityMatcher;
	private GraphSearcher graphSeracher;
	private SparqlGenerator sparqlGenerator;

	public QuestionAnalyzer() {
		initResources();
	}
	
	void initResources() {
		nlpTool = EnglishNlpTool.getInstance();
		ontology = Ontology.getInstance();
		
		questionNormalizer = QuestionNormalizer.getInstance();
		entityMatcher = new EntityMatcher();
		graphSeracher = new GraphSearcher(ontology);
		sparqlGenerator = new SparqlGenerator(ontology);
		
		// TODO
		sparqlGenerator.addPrefix("http://ir.hit.edu/nli/geo/", "geo");
	}
	
	/**
	 * Get the nlpTool.
	 *
	 * @return The nlpTool
	 */
	public NlpTool getNlpTool() {
		return nlpTool;
	}

	/**
	 * Set the nlpTool.
	 *
	 * @param nlpTool The nlpTool to set
	 */
	public void setNlpTool(NlpTool nlpTool) {
		this.nlpTool = nlpTool;
	}

	/**
	 * Get the ontology.
	 *
	 * @return The ontology
	 */
	public Ontology getOntology() {
		return ontology;
	}

	/**
	 * Set the ontology.
	 *
	 * @param ontology The ontology to set
	 */
	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}

	public String normalize(String question) {
		return questionNormalizer.normalize(question);
	}
	
	public NlpSentence getNlpSentence(String question) {
		String qn = normalize(question);
		return NlpSentence.process(nlpTool, qn);
	}
	
	public MatchedEntitiesSentence getMatchedEntitiesSentence(String question) {
		NlpSentence nlps = getNlpSentence(question);
		return entityMatcher.match(ontology, nlps);
	}
	
	public QueryGraph getQueryGraph(String question) {
		MatchedEntitiesSentence sentence = getMatchedEntitiesSentence(question);
		System.out.println("matched entity sentence : " + sentence.toString()); // debug
		QueryGraph queryGraph = graphSeracher.bestMatch(sentence);
		return queryGraph;
	}
	
	public String getSparql(String question) {
		QueryGraph queryGraph = getQueryGraph(question);
		String sparql = sparqlGenerator.generate(queryGraph);
		return sparql;
	}
	
	public Object analyze(String question) {
		String qn = questionNormalizer.normalize(question);
		NlpSentence nlps = NlpSentence.process(nlpTool, qn);
		MatchedEntitiesSentence mes = entityMatcher.match(ontology, nlps);
		QueryGraph queryGraph = graphSeracher.bestMatch(mes);
		String sparql = sparqlGenerator.generate(queryGraph);
		List<String> results = ontology.getResults(sparql);
		return results;
	}
	
	
	
	/**
	 * For test. 
	 */
	public Object analyzeData(String question) {
		String qn = questionNormalizer.normalize(question);
		System.out.println("qn : " + qn);
		NlpSentence nlps = NlpSentence.process(nlpTool, qn);
		MatchedEntitiesSentence mes = entityMatcher.match(ontology, nlps);
		System.out.println("matched entitis : " + mes);
		QueryGraph queryGraph = graphSeracher.bestMatch(mes);
		return queryGraph;
	}
}
