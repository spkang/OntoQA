/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.EntityMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.edu.hit.ir.dict.Entity;
import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.dict.StringToEntitiesMap;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.scir.ChineseQuery.ChineseQueryNormalizer;

import com.hp.hpl.jena.rdf.model.RDFNode;

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
	
	// label －> Resource 
	private StringToEntitiesMap s2eMap;
	
	
	private List<List<MatchedEntity>> matchedQuery = null;
	
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
		String query = this.queryNormalizer.removePunctuation(orgQuery);
		matcher(query);
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
	
	/**
	 * 核心实体匹配程序
	 *
	 * @param String query, 经过处理后查询。
	 * @return void 
	 */
	private void matcher (String query) {
		logger.info("query : " + query);
//		for (int i = 0; i < query.length(); ++i ) {
//			this.matchedQuery.add(new ArrayList<MatchedEntity>());
//		}
		List<List<Entity>> matchedEntity = new ArrayList<List<Entity>> ();
		int begin = 0, end = 0;
		String subStr = "";
		while (begin < query.length()) {
			for (int j = this.MAX_MATCH_LENGTH; j >= 0; --j ) {
				end = Math.min(query.length(), begin + j);
				subStr = query.substring(begin, end);
				Set<Entity> meSet = this.s2eMap.get(subStr);
				if (meSet != null ) {
					List<Entity> meList = new ArrayList<Entity>(meSet);
					matchedEntity.add (meList);
					begin = end - 1;
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
		for (List<Entity> eLst : matchedEntity) {
			logger.info("me:  ");
			for (Entity e : eLst) {
				logger.info("e : " + e.toString());
			}
		}
	}
}
