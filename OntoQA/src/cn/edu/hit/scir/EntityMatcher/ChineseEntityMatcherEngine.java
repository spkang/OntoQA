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

import org.apache.log4j.Logger;

import cn.edu.hit.ir.dict.ChineseSynonym;
import cn.edu.hit.ir.dict.Entity;
import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.dict.StringToEntitiesMap;
import cn.edu.hit.ir.dict.Synonym;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.util.Util;
import cn.edu.hit.scir.ChineseQuery.ChineseQueryDict;
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
	private Synonym synonym = null;
	
	// 中文的疑问词表
	private ChineseQueryDict cnQueryDict = null;
	
	
	// label －> Resource 
	private StringToEntitiesMap s2eMap;
	
	private Similaritable sim = new CharBasedSimilarity ();
	
	// 这个里面存储的是query 中的每个字都对应一个实体列表
	private List<List<MatchedEntity>> matchedQuery = null;
	
	// 这里存储的是query经过分词后（），每个词对应的实体列表, 这个列表在ChineseQuerySegment中设定
	private List<List<MatchedEntity>> queryWordMatchedEntities = null;
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
		synonym = ChineseSynonym.getInstance ();
		cnQueryDict = ChineseQueryDict.getInstance();
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
				double score = sim.getSimilarity(phrase, Util.lastWord(entity.getResource()));
				if (score > 0) {
					MatchedEntity me = new MatchedEntity(entity, phrase, score, begin, numTokens);
					mes.add(me);
				}
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
	 * 判断一个句子中是否有疑问词，并判断这个句子前面是否有书名号
	 *
	 * @param sentence , 待判断的句子
	 * @return boolean 
	 */
	private boolean isContainQueryWord (String sentence ) {
		 if (sentence == null )
			 return false;
		 
		 // 判断是否有疑问词
		 if (this.cnQueryDict.containsQueryWord(sentence)) {
			 int beginIndex = this.orgQuery.indexOf(sentence);
			 if (beginIndex == -1)
				 return true;
			 int endIndex = beginIndex + sentence.length();
			 if (beginIndex < 1 || endIndex >= this.orgQuery.length())
				 return true;
			 if (this.orgQuery.indexOf("《") == beginIndex - 1 && this.orgQuery.indexOf("》") == endIndex - 1) {
				 return false;
			 }
			 return true;
		 }
		 return false;
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
//		List<List<Entity>> matchedEntity = new ArrayList<List<Entity>> ();
		int begin = 0, end = 0;
		String subStr = "";
		while (begin < query.length()) {
			for (int j = Math.min(query.length() - begin + 1, this.MAX_MATCH_LENGTH); j >= 0; --j ) {
				end = Math.min(query.length(), begin + j);
				subStr = query.substring(begin, end);
				if (isContainQueryWord (subStr)) {
					continue;
				}
				
				Set<Entity> meSet = this.s2eMap.get(subStr);
				Set<String> subStrSynSet = synonym.getSet(subStr);
				
				Set<Entity> meSynSet = getEntities(subStrSynSet);
				List<MatchedEntity> toMes = new ArrayList<MatchedEntity>();
				if (meSet != null && !meSet.isEmpty() ) {
//					this.matchedWords.add (subStr);
					List<MatchedEntity> mes = getMatchedEntities (meSet, subStr, begin, 1);
					toMes.addAll(mes);
//					this.matchedQuery.set(begin, mes);
//					List<Entity> meList = new ArrayList<Entity>(meSet);
//					matchedEntity.add (meList);
				}
				if (meSynSet != null && !meSynSet.isEmpty() ){
					//logger.info("syn  " + StringUtils.join(subStrSynSet, ", "));
					//logger.info("synEntity  " + StringUtils.join(meSynSet, ", "));
					List<MatchedEntity> mes = getMatchedEntities (meSynSet, subStr, begin, 1);
					//logger.info("synMes  " + StringUtils.join(mes, ", "));
					toMes.addAll(mes);
//					this.matchedQuery.set(begin, mes);
				}
				if (! toMes.isEmpty()) {
					this.matchedQuery.set(begin, toMes);
					this.matchedWords.add (subStr);
					begin = end - 1;
					break;
				}
					
			}
			++begin;
//			if (begin > matchedEntity.size()) {
//				for (int t = matchedEntity.size(); t < begin; ++t) {
//					List<Entity> meList = new ArrayList<Entity>();
//					matchedEntity.add (meList);
//				}
//			}
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

	public List<List<MatchedEntity>> getQueryWordMatchedEntities() {
		return queryWordMatchedEntities;
	}

	public void setQueryWordMatchedEntities(
			List<List<MatchedEntity>> queryWordMatchedEntities) {
		this.queryWordMatchedEntities = queryWordMatchedEntities;
	}

	public Ontology getOntology() {
		return ontology;
	}

	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}
}
