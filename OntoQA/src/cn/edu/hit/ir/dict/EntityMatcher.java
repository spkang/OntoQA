/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import cn.edu.hit.ir.nlp.EnglishNlpTool;
import cn.edu.hit.ir.nlp.NlpSentence;
import cn.edu.hit.ir.nlp.WordNet;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.util.ConfigUtil;

/**
 * This class provides methods that match the phrases in
 * the question to the entities in ontology.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-26
 */

public class EntityMatcher {
	
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
	public EntityMatcher() {
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
	
	public MatchedEntitiesSentence match(Ontology ontology, NlpSentence nlpSentence) {
		if (ontology == null || nlpSentence == null) {
			return null;
		}
		MatchedEntitiesSentence meSent = new MatchedEntitiesSentence(nlpSentence);
		String[] tokens = nlpSentence.getTokens();
		String[] tags = nlpSentence.getTags();
		String[] stems = nlpSentence.getStems();

		for (int i = 0; i < tokens.length; i++) {
			ArrayList<MatchedEntity> totMes = new ArrayList<MatchedEntity>();
			int endIndex = i + EnglishSynonym.MAX_LENGTH;
			for (int j = i + 1; j <= endIndex && j <= tokens.length; ++j) {
				String phrase = StringUtils.join(tokens, " ", i, j);
				String stemmedPhrase = StringUtils.join(stems, " ", i, j);
				
				// Completely match
				List<String> phrasees = new ArrayList<String>();
				phrasees.add(phrase);
				phrasees.add(stemmedPhrase);
				Set<Entity> entities = getEntities(phrasees);
				List<MatchedEntity> mes = getMatchedEntities(entities, 
						phrase, completeMatchScore, i, j - i);
				totMes.addAll(mes);			

				// Match using synonyms
				Set<String> synonyms = synonym.getSet(phrase, tags[i]);
				//System.out.println(phrase + ": " + synonyms);// debug
				Set<Entity> synEntities = getEntities(synonyms);
				// remove completely matched entities
				synEntities.removeAll(entities);
				List<MatchedEntity> synMes = getMatchedEntities(synEntities,
						phrase, synonymMatchScore, i, j - i);
				totMes.addAll(synMes);
			}
			meSent.addMatchedEntities(i, totMes);
		}
		meSent.mergeResources(ontology);
		meSent.initIndex();
		return meSent;
	}
	
	public MatchedEntitiesSentence match(Ontology ontology, String sentence) {
		NlpSentence nlpSentence = 
			NlpSentence.process(EnglishNlpTool.getInstance(), sentence);
		return match(ontology, nlpSentence);
	}
}
