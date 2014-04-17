/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ontologymatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import cn.edu.hit.ir.dict.EnglishSynonym;
import cn.edu.hit.ir.dict.Entity;
import cn.edu.hit.ir.dict.MatchedEntitiesSentence;
import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.dict.StringToEntitiesMap;
import cn.edu.hit.ir.nlp.EnglishNlpTool;
import cn.edu.hit.ir.nlp.NlpSentence;
import cn.edu.hit.ir.nlp.WordNet;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.util.ConfigUtil;
import cn.edu.hit.scir.semanticgraph.DGNode;
import cn.edu.hit.scir.semanticgraph.SemanticEdge;
import cn.edu.hit.scir.semanticgraph.SemanticNode;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月15日 
 */
public class OntologyEntityMatcher {

	public static final String TOKEN_DELIMITER = " ";
	
	public static final String TOTAL_MATCH_SCORE = "socre.completeMatchScore";
	public static final String SYNONYM_MATCH_SCORE = "socre.synonymMatchScore";
	public double completeMatchScore = 1.0;
	public double synonymMatchScore = 1.0;
	
	private Configuration config;

	private Ontology ontology;
	
	//private PrefixToEntitiesMap p2eMap;
	
	private StringToEntitiesMap s2eMap;
	
	private EnglishSynonym synonym;
	
	/**
	 * Creates a new instance of EntityMatcher.
	 *
	 */
	public OntologyEntityMatcher() {
		initConfig();
		initResources();
		initData();
		initScores();
	}
	
	private void initConfig() {
		try {
			config = new PropertiesConfiguration(ConfigUtil.getPath(getClass()));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private void initResources() {
		WordNet.getInstance();
		synonym = EnglishSynonym.getInstance();
	}
	
	private void initData() {
		ontology = Ontology.getInstance();

		s2eMap = new StringToEntitiesMap();
		s2eMap.indexOntology(ontology);
	}
	
	private void initScores() {
		completeMatchScore = config.getDouble(TOTAL_MATCH_SCORE);
		synonymMatchScore = config.getDouble(SYNONYM_MATCH_SCORE);
	}
	
	/**
	 * Returns the matched entities for a given token.
	 *
	 * @param token The phase
	 * @return The matched entities
	 */
	public List<MatchedEntity> getMatchedEntities(Set<Entity> entities,
			String phrase, double score, int begin, int numTokens) {
		List<MatchedEntity> mes = new ArrayList<MatchedEntity>();
		if (entities != null) {
			for (Entity entity : entities) {
				MatchedEntity me = new MatchedEntity(entity, phrase, score,
						begin, numTokens);
				mes.add(me);
			}
		}
		return mes;
	}
	
	public Set<Entity> getEntities(Collection<String> phrasees) {
		Set<Entity> entities = new HashSet<Entity>();
		for (String p : phrasees) {
			Set<Entity> set = s2eMap.get(p);
			if (set != null) {
				entities.addAll(set);
			}
		}
		return entities;
	}
	
	
	public List<MatchedEntity> mathPathNode (Ontology ontology, PathNode pNode ) {
		if (ontology == null || pNode == null )
			return null;
		List<MatchedEntity> matchEntities = new ArrayList<MatchedEntity> ();
		if (pNode.isSemanticEdge()) { // semantic edge 
			// core words 
			SemanticEdge edge = (SemanticEdge) pNode.getNode();
			List<DGNode> preModifiers = edge.getPreEdgeModifiers();
			List<DGNode> postModifiers = edge.getPostEdgeModifiers();
			List<DGNode> linkWds = edge.getLinkWords();
			if (preModifiers != null)
				linkWds.addAll(0, preModifiers);
			if (postModifiers != null )
				linkWds.addAll (postModifiers);

			return this.coreMatcher(matchEntities, linkWds);
		}
		else { // semantic node 
			SemanticNode node  = (SemanticNode)pNode.getNode();
//			List<DGNode> coreWords = node.getCoreWords();
			List<DGNode> coreWords = node.getSemanticUnit();
			return this.coreMatcher(matchEntities, coreWords);
		}
		
	}
	
	
	private  List<MatchedEntity>  coreMatcher (List<MatchedEntity> matchEntities, List<DGNode> coreWords ) {
		if (matchEntities == null || coreWords == null)
			return null;
		
		List<String> tokens = new ArrayList<String> ();
		List<String> stems  = new ArrayList<String> ();
		for (DGNode wd : coreWords ) {
			if (wd.word.toLowerCase().equals("the"))
				continue;
			tokens.add(wd.word);
			stems.add(wd.stem);
		}
		if (tokens == null)
			return null;
		for (int i = 0; i < tokens.size(); ++i ) {
			int endIndex  = i + EnglishSynonym.MAX_LENGTH;
			for (int j = i + 1; j <= endIndex && j <= tokens.size(); ++j ) {
				//System.out.println ("i : " + i + "\tj : " + j + "\ttokens : " + tokens);
				String phrase = StringUtils.join(tokens.toArray(), " ", i, j);
				String stemedPhrase = StringUtils.join(stems.toArray(), " ", i, j);
//				System.out.println ("phrase : " + phrase);
				// completely match
				List<String> phrases = new ArrayList<String> ();
				phrases.add(phrase);
				phrases.add(stemedPhrase);
				Set<Entity> entities = getEntities (phrases);
				List<MatchedEntity> mes = getMatchedEntities (entities, phrase, completeMatchScore, i, j - i);
				matchEntities.addAll(mes);
				
				// matching entities using synonyms 
				
				Set<String> synonyms = synonym.getSet(phrase, coreWords.get(i).tag);
				//System.out.println(phrase + ": " + synonyms);// debug
				Set<Entity> synEntities = getEntities(synonyms);
				// remove completely matched entities
				synEntities.removeAll(entities);
				List<MatchedEntity> synMes = getMatchedEntities(synEntities,
						phrase, synonymMatchScore, i, j - i);
				matchEntities.addAll(synMes);
			}
		}
		System.out.println  ("matchEntities : " + matchEntities);
		return matchEntities;
	}
}
