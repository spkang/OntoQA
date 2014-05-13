/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.EntityMatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.edu.hit.ir.dict.ChineseSynonym;
import cn.edu.hit.ir.dict.Entity;
import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.dict.StringToEntitiesMap;
import cn.edu.hit.ir.dict.Synonym;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.util.Util;
import cn.edu.hit.scir.ChineseQuery.ChineseQueryNormalizer;
import cn.edu.hit.scir.Similarity.CharBasedSimilarity;
import cn.edu.hit.scir.Similarity.Similaritable;

/**
 *  匹配中文问题中的实体
 * 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月7日 
 */
public class ChineseEntityMatcherEngine {
	private static ChineseEntityMatcherEngine instance = new ChineseEntityMatcherEngine ();
	private static Logger logger = Logger.getLogger (ChineseEntityMatcherEngine.class);
	private static ChineseQueryNormalizer queryNormalizer = ChineseQueryNormalizer.getInstance();
	
	// 用于正向匹配的开始值
	private final int MAX_MATCH_LENGTH = 8;
	
	private Ontology ontology = null;
	private Synonym synonym = ChineseSynonym.getInstance ();
	
	// label －> Resource 
	private StringToEntitiesMap s2eMap;
	
	private Similaritable sim = new CharBasedSimilarity ();
	
	private List<List<MatchedEntity>> matchedQuery = null;
	private List<String> matchedWords = null;
	
	private String orgQuery = null;
	private String processedQuery = null;
	
	private ChineseEntityMatcherEngine () {
		initData ();
	}
	
	public static ChineseEntityMatcherEngine getInstance () {
		if (instance == null )
			instance = new ChineseEntityMatcherEngine ();
		return instance;
	}

	private void initData() {
		ontology = Ontology.getInstance();
		s2eMap = new StringToEntitiesMap();
		s2eMap.indexOntology(ontology);
		this.matchedQuery = new ArrayList<List<MatchedEntity>> ();
		this.matchedWords = new ArrayList<String> ();
	}

	public StringToEntitiesMap getS2EMap () {
		return this.s2eMap;
	} 
	
	/** 
	 * 对输入的问题进行实体匹配
	 *
	 * @param 
	 * @return void 
	 */
	public void queryEntityMatcher (String orgQuery ) {
		if (orgQuery == null || orgQuery.isEmpty() )
			return ;
		setOrgQuery (orgQuery); // set the origin query
		
		String query = this.queryNormalizer.removePunctuation(orgQuery);
		
		setProcessedQuery (query); // set the processed query
		
		matcher(query);
	} 
	
	
	/**
	 * Returns the matched entities for a given token.
	 *
	 * @param token The phase
	 * @return The matched entities
	 */
	public List<MatchedEntity> getMatchedEntities(Set<Entity> entities,
			String phrase, int begin, int numTokens) {
		List<MatchedEntity> mes = new ArrayList<MatchedEntity>();
		
		if (entities != null) {
			for (Entity entity : entities) {
				MatchedEntity me = new MatchedEntity(entity, phrase, sim.getSimilarity(phrase, Util.lastWord(entity.getResource())),
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
	
	/**
	 * 核心实体匹配程序
	 *
	 * @param String query, 经过处理后查询。
	 * @return void 
	 */
	private void matcher (String query) {
		logger.info("query : " + query);
		this.matchedQuery.clear();
		for (int i = 0; i < query.length(); ++i ) {
			this.matchedQuery.add(new ArrayList<MatchedEntity>());
		}
		List<List<Entity>> matchedEntity = new ArrayList<List<Entity>> ();
		int begin = 0, end = 0;
		String subStr = "";
		while (begin < query.length()) {
			for (int j = this.MAX_MATCH_LENGTH; j >= 0; --j ) {
				end = Math.min(query.length(), begin + j);
				subStr = query.substring(begin, end);
				Set<Entity> meSet = this.s2eMap.get(subStr);
				Set<String> subStrSynSet = synonym.getSet(subStr);
				
				Set<Entity> meSynSet = getEntities(subStrSynSet);
				
				if (meSet != null && !meSet.isEmpty()) {
					this.matchedWords.add (subStr);
					List<MatchedEntity> mes = getMatchedEntities (meSet, subStr, begin, 1);
					this.matchedQuery.set(begin, mes);
					List<Entity> meList = new ArrayList<Entity>(meSet);
					matchedEntity.add (meList);
					begin = end - 1;
					break;
				}
				else if (meSynSet != null && !meSynSet.isEmpty()){
					//logger.info("syn  " + StringUtils.join(subStrSynSet, ", "));
					//logger.info("synEntity  " + StringUtils.join(meSynSet, ", "));
					this.matchedWords.add (subStr);
					List<MatchedEntity> mes = getMatchedEntities (meSynSet, subStr, begin, 1);
					//logger.info("synMes  " + StringUtils.join(mes, ", "));
					this.matchedQuery.set(begin, mes);
					break;
				}
				
			}
			++begin;
			if (begin > matchedEntity.size()) {
				for (int t = matchedEntity.size(); t < begin; ++t) {
					List<Entity> meList = new ArrayList<Entity>();
					matchedEntity.add (meList);
				}
			}
		}
	}

	public List<List<MatchedEntity>> getMatchedQuery() {
		return matchedQuery;
	}

	public void setMatchedQuery(List<List<MatchedEntity>> matchedQuery) {
		this.matchedQuery = matchedQuery;
	}

	public String getOrgQuery() {
		return orgQuery;
	}

	public void setOrgQuery(String orgQuery) {
		this.orgQuery = orgQuery;
	}

	public String getProcessedQuery() {
		return processedQuery;
	}

	public void setProcessedQuery(String processedQuery) {
		this.processedQuery = processedQuery;
	}

	public List<String> getMatchedWords() {
		return matchedWords;
	}

	public void setMatchedWords(List<String> matchedWords) {
		this.matchedWords = matchedWords;
	}
}
