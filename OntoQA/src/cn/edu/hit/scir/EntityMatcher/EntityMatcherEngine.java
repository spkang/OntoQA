/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.EntityMatcher;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.edu.hit.ir.dict.EnglishSynonym;
import cn.edu.hit.ir.dict.Entity;
import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.dict.PrefixToEntitiesMap;
import cn.edu.hit.ir.dict.StringToEntitiesMap;
import cn.edu.hit.ir.dict.SuffixToEntitiesMap;
import cn.edu.hit.ir.nlp.WordNet;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.ontology.RDFNodeType;
import cn.edu.hit.ir.util.ConfigUtil;
import cn.edu.hit.ir.util.Util;
import cn.edu.hit.scir.ontologymatch.PathNode;
import cn.edu.hit.scir.semanticgraph.DGEdge;
import cn.edu.hit.scir.semanticgraph.DGNode;
import cn.edu.hit.scir.semanticgraph.SemanticEdge;
import cn.edu.hit.scir.semanticgraph.SemanticGraph;
import cn.edu.hit.scir.semanticgraph.SemanticNode;

import com.hp.hpl.jena.rdf.model.Resource;


/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年4月24日 
 */

public class EntityMatcherEngine {
	private static EntityMatcherEngine instance = new EntityMatcherEngine ();
	public static final String TOKEN_DELIMITER = " ";
	private static Logger logger = Logger.getLogger(EntityMatcherEngine.class);
	//public static final String TOTAL_MATCH_SCORE = "socre.completeMatchScore";
	//public static final String SYNONYM_MATCH_SCORE = "socre.synonymMatchScore";
	public double completeMatchScore = 1.0;
	public double ontologyMatchScore = 0.80;
	public double synonymMatchScore = 0.75;
	
	private Configuration config;

	private Ontology ontology;
	
	//private PrefixToEntitiesMap p2eMap;
	
	private StringToEntitiesMap s2eMap;
	private PrefixToEntitiesMap prefix2eMap;
	private SuffixToEntitiesMap suffix2eMap;
	
	private EnglishSynonym synonym;
	
	private SemanticGraph semanticGraph = null; 
	
	private Map <DGNode, List<List<MatchedEntity>>>  matchedEntityMap = null;
	
	private List<List<MatchedEntity>> matchedQuery = null;
	
	public static EntityMatcherEngine getInstance () {
		return instance;
	} 
	
	/**
	 * Creates a new instance of EntityMatcher.
	 *
	 */
	private EntityMatcherEngine() {
		//initConfig();
		initResources();
		initData();
		//initScores();
		
	}
	
	public void setSemanticGraph (SemanticGraph semanticGraph ) {
		this.semanticGraph = semanticGraph; 
		
		this.matchedQuery = new ArrayList<List<MatchedEntity>>();
		
		for (int i = 0; i < this.semanticGraph.getDependencyGraph().getVertexs().size(); ++i ) {
			this.matchedQuery.add(new ArrayList<MatchedEntity>());
		}
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
		
		prefix2eMap = new PrefixToEntitiesMap ();
		prefix2eMap.indexOntology(ontology);

		suffix2eMap = new SuffixToEntitiesMap ();
		suffix2eMap.indexOntology(ontology);
		
		this.matchedEntityMap = new HashMap<DGNode, List<List<MatchedEntity>>>();
	}
	
	private void initScores() {
		;
		//completeMatchScore = config.getDouble(TOTAL_MATCH_SCORE);
		//synonymMatchScore = config.getDouble(SYNONYM_MATCH_SCORE);
	}
	
	public List<List<MatchedEntity>> runEntityMatcherEngine (SemanticGraph smtcGraph ) {
		this.setSemanticGraph(smtcGraph);
		
		// 匹配单个实体
		this.matchQuery(this.semanticGraph.getDependencyGraph().getVertexs());
		
		// 匹配两个单词实体
		this.mergeEntities(this.semanticGraph.getDependencyGraph().getVertexs());
		
		// 合并实体
		this.mergeEntities();
		
		// 对已经匹配的实体进行重排
		return this.rearrangeMatchedEntityList ();
	}
	
	
	/**
	 *   判断这个位置是否有匹配的实体并且这个位置的词是一个动词， 如果是，返回真
	 *   否则返回 假
	 * 
	 * @param pos, the vertex node position 
	 * @return boolean 
	 */
	private boolean isPropertyMatchVerb (int pos) {
		// 判断是否匹配了实体
		if (this.matchedQuery.get(pos) == null || this.matchedQuery.get(pos).isEmpty())
			return false;
		
		// 判断这个位置匹配的是否是都是属性（property）
		for (MatchedEntity me : this.matchedQuery.get(pos)) {
			if (! me.isProperty())
				return false;
		}
		
		// 判断这个位置的属性匹配的词中是否存在动词
		for (MatchedEntity me : this.matchedQuery.get(pos)) {
			int next = me.getBegin(); 
			while (next != -1) {
				if (this.semanticGraph.getDependencyGraph().getVertexNode(next).tag.toUpperCase().startsWith(this.semanticGraph.getDependencyGraph().VERB))
					return true;
				next = this.semanticGraph.getDependencyGraph().getVertexNode(next).nextIndex;
			}
		}
		
		return false;
	}
	
	
	
	/**
	 * 判断这个位置的动词匹配的属性是否存在顺序不合理的情况
	 *  
	 *   e.g. what are the populations of the states through which the mississippi run ?
	 *   其中 run 匹配的不合理
	 * @param the property position in matchedQuery
	 * @return boolean 
	 */
	private boolean isPropertyVerbPosIllegal (int pos, List<DGNode> nsubjObjNodes ){
		if (pos < 0 || pos >= this.matchedQuery.size() || nsubjObjNodes == null )
			return false;
		if (this.semanticGraph.getDependencyGraph().isVerbPositionIllegal(pos, nsubjObjNodes)) {
			if (   this.matchedQuery.get(nsubjObjNodes.get(0).idx) != null && ! this.matchedQuery.get(nsubjObjNodes.get(0).idx).isEmpty() 
				&& this.matchedQuery.get(nsubjObjNodes.get(1).idx) != null && ! this.matchedQuery.get(nsubjObjNodes.get(1).idx).isEmpty() ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 重排匹配的实体列表
	 *
	 * @param null
	 * @return  
	 */
	public List<List<MatchedEntity>> rearrangeMatchedEntityList () {
		// 对动词在两个实体后面的实体列表进行更正
		// e.g. what are the populations of the states through which the mississippi run ?
		// locate the verb which matched a property
		List<List<MatchedEntity>> rearrangeMeList = new ArrayList<List<MatchedEntity>>();
		List<Integer> mergePropertyIndex = new ArrayList<Integer> ();
		List<Integer> pos = new ArrayList<Integer> ();
		for (int index = 0; index < this.matchedQuery.size(); ++index ) {
			pos.add(index);
			if (isPropertyMatchVerb (index) ) {
				// 获得verb the subj node and obj node
				List<DGNode> nsubjObjNodes = this.semanticGraph.getDependencyGraph().getSubObjNode(index);
				// 判断给定位置定动词次序是不合理的
				if (isPropertyVerbPosIllegal (index, nsubjObjNodes)) { 
					// 是否有介词链接， e.g. : through which states does the mississippi run ?, 
					// run and through匹配的属性要合并，并且将合并的属性放在obj和sub之间
					DGNode subjNode = nsubjObjNodes.get(0);
					DGNode objNode = nsubjObjNodes.get(1);
					DGEdge objEdge = this.semanticGraph.getDependencyGraph().getEdge(index, objNode.idx);
					// obj和verb之间没有介词
					if (objEdge != null && objEdge.status && objEdge.reln.toLowerCase().equals("rcmod")) {
						;
					}
					else {
						List<Integer> path = this.semanticGraph.getDependencyGraph().searchPath(index,  objNode.idx);
						for (Integer p : path ) {
							if (p != index && p != objNode.idx && this.semanticGraph.getDependencyGraph().getVertexNode(p).tag.toUpperCase().equals(this.semanticGraph.getDependencyGraph().IN)) {
								objEdge = this.semanticGraph.getDependencyGraph().getEdge(index, p);
								// 中间链接词是介词，并且可以和verb匹配的属性进行合并
								if (objEdge.status && objEdge.reln.toLowerCase().equals("prep") 
										&& this.matchedQuery.get(p) != null && this.matchedQuery.get(p).size() == 1 
										&& this.matchedQuery.get(index).size() == 1
										&& this.matchedQuery.get(p).get(0).getResource().equals(this.matchedQuery.get(index).get(0).getResource())) 
								{
									mergePropertyIndex.add(p);
									// 合并属性
									this.semanticGraph.getDependencyGraph().getVertexNode(index).nextIndex = p;
									this.semanticGraph.getDependencyGraph().getVertexNode(p).prevIndex = index;
									this.matchedQuery.get(index).get(0).setLabel(this.matchedQuery.get(index).get(0).getLabel() + " " + this.matchedQuery.get(p).get(0).getLabel());
									this.matchedQuery.get(index).get(0).setQuery(this.matchedQuery.get(index).get(0).getQuery() + " " + this.matchedQuery.get(p).get(0).getQuery());
									this.matchedQuery.get(index).get(0).setNumTokens(this.matchedQuery.get(index).get(0).getNumTokens() +  this.matchedQuery.get(p).get(0).getNumTokens());
									this.matchedQuery.get(index).get(0).setScore((this.matchedQuery.get(index).get(0).getScore() +  this.matchedQuery.get(p).get(0).getScore()) / 2.0);
									break;
								} // if
							} // if 
						} // for
					} // else 
					if (subjNode.idx < objNode.idx ) {
						// 交换pos中objNode.idx 与 index的位置
						for (int i = index - 1; i >= objNode.idx; --i ) {
							int tmp  = pos.get(i);
							pos.set(i, index);
							pos.set(i + 1, tmp);
						}
					}
					else {
						for (int i = index - 1; i >= subjNode.idx; --i ) {
							int tmp  = pos.get(i);
							pos.set(i, index);
							pos.set(i + 1, tmp);
						}
					}
				} // if
			} // if		
		} // for
		
		for (int i = 0; i < this.matchedQuery.size(); ++i) {
			if (this.matchedQuery.get(pos.get(i)) != null && ! this.matchedQuery.get(pos.get(i)).isEmpty() && ! mergePropertyIndex.contains(pos.get(i)) && this.semanticGraph.getDependencyGraph().getVertexNode(pos.get(i)).prevIndex == -1) {
				rearrangeMeList.add(new ArrayList<MatchedEntity> (this.matchedQuery.get(pos.get(i))));
			}
		}
		
		for (int i = 0; i < this.matchedQuery.size(); ++i ) {
			logger.info("DGNode  : " + this.semanticGraph.getDependencyGraph().getVertexNode(i).toString()); 
		}
		
		return rearrangeMeList;
	}
	
	
	public List<List<MatchedEntity>> getMatchedQuery() {
		return matchedQuery;
	}

	public void setMatchedQuery(List<List<MatchedEntity>> matchedQuery) {
		this.matchedQuery = matchedQuery;
	}
	
	private void connectMatchedEntiies (MatchedEntity lhs, MatchedEntity rhs, boolean isLhsClass ) {
		String [] tokens = this.semanticGraph.getDependencyGraph().getTokens();
		int numTokens = rhs.getBegin() + rhs.getNumTokens() - lhs.getBegin();
		String query = StringUtils.join (tokens, " ", lhs.getBegin(), lhs.getBegin() + numTokens);
		MatchedEntity mergedEntity = null;
		if (isLhsClass) {
			mergedEntity= new MatchedEntity (rhs.getResource(), rhs.getLabel(), RDFNodeType.INSTANCE, query, rhs.getScore(), lhs.getBegin(), numTokens);
		}
		else {
			mergedEntity = new MatchedEntity (lhs.getResource(), lhs.getLabel(), RDFNodeType.INSTANCE, query, lhs.getScore(), rhs.getBegin(), numTokens);
		}
		
		List<DGNode> queryNodes = this.semanticGraph.getDependencyGraph().getVertexs ();
		int nextIndex = lhs.getBegin();
		int prevIndex = -1;
		while (nextIndex != -1) {
			prevIndex = nextIndex;
			nextIndex = queryNodes.get(nextIndex).nextIndex;
		}
		queryNodes.get(prevIndex).nextIndex =  rhs.getBegin();
		queryNodes.get(rhs.getBegin()).prevIndex = prevIndex;
		
		nextIndex = lhs.getBegin();
		while (nextIndex != -1) {
			this.matchedQuery.get(nextIndex).clear();
			this.matchedQuery.get(nextIndex).add(mergedEntity);
			nextIndex = queryNodes.get(nextIndex).nextIndex;
		}
	} 
	
	private List<Integer> getMatchedEntityDGNodeIndexes (MatchedEntity me) {
		List<Integer> nodeIndex = new ArrayList<Integer> ();
		int next = me.getBegin();
		
		while (next != -1 ){
			nodeIndex.add(next);
			next = this.semanticGraph.getDependencyGraph().getVertexNode(next).nextIndex;
		}
		return nodeIndex;
	}
	
	private boolean isExistsVerbBetweenMatchedEntities (MatchedEntity lhs, MatchedEntity rhs) {
		List<Integer> lhsIndex = this.getMatchedEntityDGNodeIndexes(lhs);
		List<Integer> rhsIndex = this.getMatchedEntityDGNodeIndexes(rhs);
		
		for (Integer i : lhsIndex) {
			for (Integer j : rhsIndex) {
				if (this.semanticGraph.getDependencyGraph().isContainVerbInPath(i,  j)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isCanMerge (MatchedEntity lhs, MatchedEntity rhs)  {
		if (lhs == null || rhs == null )
			return false;
		MatchedEntity mergedEntity = null;
		if (this.isExistsVerbBetweenMatchedEntities(lhs, rhs)) {
			return false;
		}
		if (lhs.isClass() && rhs.isInstance() && ontology.isInstanceOf(rhs.getResource(), lhs.getResource())) {
			this.connectMatchedEntiies(lhs, rhs, true);
			return true;
		}
		else if (lhs.isInstance() && rhs.isClass() && ontology.isInstanceOf(lhs.getResource(), rhs.getResource())) {
			this.connectMatchedEntiies(lhs, rhs, false);
			return true;
		}
		return false;
	}
	
	public boolean removeEntity (MatchedEntity me) {
		return this.matchedQuery.get(me.getBegin()).remove(me);
	}
	
	public int removeEntity (String label, int index ) {
		if (index < 0 || index >= this.matchedQuery.size() || label == null || label.isEmpty())
			return -1;
		int cnt = 0;
		List<MatchedEntity> mes = this.matchedQuery.get(index);
		
		for (int i = 0; i < mes.size(); ++i ) {
			if (mes.get(i).getLabel().equals(label)) {
				removeEntity(mes.get(i));
				--i;
				++cnt;
			}
		} 
		return cnt;
	}
	
	
	
	/**
	 * 对经过连续两个词匹配后的两个匹配的实体进行合并
	 * 如： city of new york, new york city, mississippi river, and so on 
	 *
	 *
	 * @param 
	 * @return void 
	 */
	public void mergeEntities () {
		List<DGNode> queryNodes = this.semanticGraph.getDependencyGraph().getVertexs();
		for (int i = 0; i < this.matchedQuery.size(); ++i ) {
			List<MatchedEntity> meLhs = this.matchedQuery.get(i);
			if (meLhs == null || meLhs.isEmpty() || queryNodes.get(i).prevIndex != -1)
				continue;
//			System.out.println("i = " + i + "\t mes: " + meLhs);
			// 查找下一个匹配得实体列表
			int j = i + 1;
			while (j < this.matchedQuery.size() ) {
				if (this.matchedQuery.get(j) == null || this.matchedQuery.get(j).isEmpty() || queryNodes.get(j).prevIndex != -1 )
					++j;
				else 
					break;
			}
			if ( j == this.matchedQuery.size() )
				break;
			List<MatchedEntity> meRhs = this.matchedQuery.get(j);
			
			// deal with: majorCity , inState
			if (meLhs.size() == 1 && meLhs.get(0).isProperty() && meRhs.size() == 1 && meRhs.get(0).getResource().equals(ontology.getResource("http://ir.hit.edu/nli/geo/inState"))) {
				this.matchedQuery.get(j).clear();
				continue;
			}
			
			boolean stop = false;
			for (int k = 0; k < meLhs.size() && !stop; ++k ) {
				for (int m = 0; m < meRhs.size(); ++m) {
					if (isCanMerge (meLhs.get(k), meRhs.get(m))) {
						stop = true;
						break;
					}
				}
			} 
		}
	}

	/**
	 * 对连续的两个词进匹配合并，同义词匹配
	 * 
	 * @param 
	 * @return void 
	 */
	public void mergeEntities (List<DGNode> queryNodes ) {
		if (queryNodes == null )
			return ;
		Set<String> phrase = new HashSet<String> ();
		for (int i = 0; i < queryNodes.size() - 1; ++i ) {
			phrase.clear();
			phrase.add(queryNodes.get(i).word + " " + queryNodes.get(i + 1).word);
			phrase.add(queryNodes.get(i).stem + " " + queryNodes.get(i + 1).stem);

			// Set<Entity> prefixMatchedEntities = completeMatchDGNodeEntity (queryNodes.get(i), this.prefix2eMap);
			Set<Entity> prefixMatchedEntities = queryNodes.get(i).matchedEntitySet;
			
			Set<Entity> stringPhraseMatchedEntities = matchEntity (phrase, this.s2eMap);
			
			Set<Entity> suffixMatchedEntities = completeMatchDGNodeEntity (queryNodes.get(i + 1), this.suffix2eMap);
			//Set<Entity> suffixMatchedEntities = queryNodes.get(i + 1).matchedEntitySet;
			
//			System.out.println ("prefixMatchedEntities : " + StringUtils.join(prefixMatchedEntities, ", "));
//			System.out.println ("stringPhraseMatchedEntities : " + StringUtils.join(stringPhraseMatchedEntities, ", "));
//			System.out.println ("suffixMatchedEntities : " + StringUtils.join(suffixMatchedEntities, ", "));
			
			if (prefixMatchedEntities != null && suffixMatchedEntities != null && stringPhraseMatchedEntities != null ) {
				Set<Entity> intersectSet = Util.intersect(prefixMatchedEntities, suffixMatchedEntities);
				if (intersectSet != null && stringPhraseMatchedEntities.equals(Util.intersect(intersectSet, stringPhraseMatchedEntities)))  { //  合并 
					List<MatchedEntity> matchedEntities = this.getMatchedEntities(stringPhraseMatchedEntities, queryNodes.get(i).word + " " + queryNodes.get(i + 1).word, this.completeMatchScore,queryNodes.get(i).idx, 2);
					this.matchedQuery.get(i).addAll(matchedEntities);
					if (matchedEntities.size() == 1 && matchedEntities.get(0).getResource().equals(ontology.getResource("http://ir.hit.edu/nli/geo/inState"))) {
						; // 不做处理
					}
					else {
//						System.out.println("1 matchedEntities : " + matchedEntities.toString());
//						if (matchedEntities.size() == 1 && matchedEntities.get(0).getResource().equals(ontology.getResource("http://ir.hit.edu/nli/geo/majorCity"))) {
//							System.out.println("matchedEntities : " + matchedEntities.toString());
//							;
//						}
//						else {
						this.matchedQuery.get(i + 1).addAll(matchedEntities);
//						System.out.println ("stringPhraseMatchedEntities  is not null, 合并1" + "\tbefore connect : node (i) : " +  queryNodes.get(i) + "\tnode(i + 1) : " + queryNodes.get(i + 1));				
						connectDGNode (queryNodes.get(i), queryNodes.get(i + 1));
//						System.out.println ("stringPhraseMatchedEntities  is not null, 合并1" + "\t after connect : node (i) : " +  queryNodes.get(i) + "\tnode(i + 1) : " + queryNodes.get(i + 1));
//						}
					}
				}
			}
			//  like , run -> runThrough , through -> runThroungh
			else if (prefixMatchedEntities != null && suffixMatchedEntities != null && stringPhraseMatchedEntities == null ) {
				if (prefixMatchedEntities.equals(suffixMatchedEntities)) {
					double score = 0.0;
					if (queryNodes.get(i).isSynonymMatch && queryNodes.get(i + 1).isSynonymMatch) {
						score = this.synonymMatchScore;
					}
					else if (queryNodes.get(i).isSynonymMatch || queryNodes.get(i + 1).isSynonymMatch) {
						score = (this.completeMatchScore + this.synonymMatchScore) / 2.0;
					}
					else {
						score = this.completeMatchScore;
					}
					
					this.matchedQuery.get(i).addAll(this.getMatchedEntities(prefixMatchedEntities, 
							queryNodes.get(i).word + " " + queryNodes.get(i + 1).word, score ,queryNodes.get(i).idx, 2));
					this.matchedQuery.get(i + 1).addAll(this.getMatchedEntities(prefixMatchedEntities, 
							queryNodes.get(i).word + " " + queryNodes.get(i + 1).word, score ,queryNodes.get(i).idx, 2));
//					System.out.println ("stringPhraseMatchedEntities  is NULL, 合并2" + "\tbefore connect : node (i) : " +  queryNodes.get(i) + "\tnode(i + 1) : " + queryNodes.get(i + 1));
					connectDGNode (queryNodes.get(i), queryNodes.get(i + 1));
//					System.out.println ("stringPhraseMatchedEntities  is NULL, 合并2" + "\t after connect : node (i) : " +  queryNodes.get(i) + "\tnode(i + 1) : " + queryNodes.get(i + 1));
				}
				else {
//					System.out.println ("prefix : " + prefixMatchedEntities.toString());
					// majar river, majar -> majarCity, 
					if (prefixMatchedEntities.size() == 1) {
						List<Entity> tmpMes = new ArrayList<Entity> (prefixMatchedEntities);
						if (tmpMes.get(0).getResource().equals(ontology.getResource("http://ir.hit.edu/nli/geo/majorCity"))) {
							queryNodes.get(i).matchedEntitySet.clear();
						}
					}
				} 
			}
			// synonym 
			else {
				Set<String> synonyms = new HashSet<String>();
				for (String p : phrase ) {
					Set <String> tmpSyn = synonym.getSet(p, queryNodes.get(i).tag);
					if (tmpSyn != null )
						synonyms.addAll(tmpSyn);
				}
				
				//System.out.println(phrase + ": " + synonyms);// debug
				Set<Entity> synEntities = getEntities(synonyms);
				// remove completely matched entities
				List<MatchedEntity> synMes = getMatchedEntities(synEntities,
						queryNodes.get(i).word + " " + queryNodes.get(i +1).word, synonymMatchScore, queryNodes.get(i).idx, 2);
				if (synMes != null && !synMes.isEmpty()) {
//					System.out.println ("synonym , 合并3" + "\tbefore connect : node (i) : " +  queryNodes.get(i) + "\tnode(i + 1) : " + queryNodes.get(i + 1));
					this.matchedQuery.get(i).addAll(synMes);
					this.matchedQuery.get(i + 1).addAll(synMes);
					connectDGNode (queryNodes.get(i), queryNodes.get(i + 1));
//					System.out.println ("synonym , 合并3" + "\t after connect : node (i) : " +  queryNodes.get(i) + "\tnode(i + 1) : " + queryNodes.get(i + 1));
				}
			}
			// 不能合并，将单个得实体进行封装。
			if (this.matchedQuery.get(i).isEmpty() && queryNodes.get(i).matchedEntitySet != null && ! queryNodes.get(i).matchedEntitySet.isEmpty()) {
				if (queryNodes.get(i).isSynonymMatch) {
					this.matchedQuery.get(i).addAll(this.getMatchedEntities(queryNodes.get(i).matchedEntitySet, queryNodes.get(i).word, this.synonymMatchScore, queryNodes.get(i).idx, 1)); 
				}
				else {
					this.matchedQuery.get(i).addAll(this.getMatchedEntities(queryNodes.get(i).matchedEntitySet, queryNodes.get(i).word, this.completeMatchScore, queryNodes.get(i).idx, 1));
				}
			}
			//  不能合并，  最后一个
			if (i + 1 == queryNodes.size() - 1 && queryNodes.get(i+1).prevIndex == -1 && queryNodes.get(i + 1).nextIndex == -1 && queryNodes.get(i + 1).matchedEntitySet != null && ! queryNodes.get(i  +1).matchedEntitySet.isEmpty()) { // the last one 
				if (! queryNodes.get(i + 1).isSynonymMatch)
					this.matchedQuery.get(i + 1).addAll(this.getMatchedEntities(queryNodes.get(i + 1).matchedEntitySet, queryNodes.get( i + 1 ).word, this.completeMatchScore, queryNodes.get(i + 1).idx, 1));
				else 
					this.matchedQuery.get(i + 1).addAll(this.getMatchedEntities(queryNodes.get(i + 1).matchedEntitySet, queryNodes.get( i + 1 ).word, this.synonymMatchScore, queryNodes.get(i + 1).idx, 1));
			}
		}
	}
	
	
	private boolean connectDGNode (DGNode lhs, DGNode rhs) {
		if (lhs == null || rhs == null )
			return false;
		lhs.nextIndex = rhs.idx;
		rhs.prevIndex = lhs.idx;
		return true;
	}
	
	
	/**
	 *   对问句中的每个词进行匹配
	 *
	 * @param 
	 * @return void 
	 */
	public void matchQuery (List<DGNode> queryNodes ) {
		if (queryNodes == null )
			return ;
		
		for (DGNode node : queryNodes) {
			Set<Entity> matchSet = new HashSet<Entity> ();
			Set<Entity> prefixCompleteMatchSet = this.completeMatchDGNodeEntity(node, this.prefix2eMap);
			Set<Entity> stringCompleteMatchSet = this.completeMatchDGNodeEntity(node, this.s2eMap);
			Set<Entity> suffixCompleteMatchSet = this.completeMatchDGNodeEntity(node, this.suffix2eMap);
			
			// 决策，如果前缀匹配的内容和完全匹配的内容一致，那么实体集合直接付给node的匹配的实体集合
			// 如果前缀匹配和完全匹配的集合不一致（stringCompleteMatchSet != null ）， 以stringCompleteMatch 为准，
			// 如果stringCompleteMatchSet == null , 以前缀匹配为准
			if (stringCompleteMatchSet != null) {
				Set<Entity> intersectSet = Util.intersect(prefixCompleteMatchSet, stringCompleteMatchSet);
				if (intersectSet == null )
					matchSet.addAll (stringCompleteMatchSet);
				else {
					matchSet.addAll (intersectSet);
				}
			}
			else {
				if (prefixCompleteMatchSet != null )
					matchSet.addAll(prefixCompleteMatchSet);
				else {
					if (suffixCompleteMatchSet != null )
						matchSet.addAll(suffixCompleteMatchSet);
				}
			}
			
						
			
			// only use synonym match when the complete matching is not work
			Set<Entity> synonymMatchSet = null;
			if (prefixCompleteMatchSet == null && stringCompleteMatchSet == null && suffixCompleteMatchSet == null )
				synonymMatchSet = this.synonymMatchDGNodeEntity(node, this.prefix2eMap);
			if (synonymMatchSet != null ) {
				matchSet.addAll(synonymMatchSet); 
				node.isSynonymMatch = true;
			}
			node.matchedEntitySet = (matchSet.isEmpty() ? null : matchSet);
			
			
			// 如果当前是Instate，前一个实体是mountain， city， lake 则保留，否则不进行保留
			if (node.matchedEntitySet != null && !node.matchedEntitySet.isEmpty() && node.matchedEntitySet.size() == 1 ) {
				List<Entity> tmpEntityList = new ArrayList<Entity> (node.matchedEntitySet);
//				System.out.println ("inState : " + tmpEntityList.get(0));
				if (tmpEntityList.get(0).getResource().equals(ontology.getResource("http://ir.hit.edu/nli/geo/inState"))){
					System.out.println ("inState : " + tmpEntityList.get(0));
					int i = node.idx - 1;
					while (i >= 0) {
						if (queryNodes.get(i).matchedEntitySet != null && !queryNodes.get(i).matchedEntitySet.isEmpty()) {
							break;
						}
						--i;
					}
					boolean isRemoveCurrentProperty = false;
					Resource mountain = ontology.getResource("http://ir.hit.edu/nli/geo/mountain");
					Resource lake = ontology.getResource("http://ir.hit.edu/nli/geo/lake");
					Resource city = ontology.getResource("http://ir.hit.edu/nli/geo/city");
					if (i >= 0 ) {
						tmpEntityList.clear();
						tmpEntityList.addAll(queryNodes.get(i).matchedEntitySet);
						for (Entity e : tmpEntityList) {
							if (e.isClass() &&  !e.getResource().equals(mountain) && !e.getResource().equals(lake) && !e.getResource().equals(city)) {
								isRemoveCurrentProperty = true;
								break;
							}
							else if (e.isInstance() && !ontology.isInstanceOf(e.getResource(), mountain) && !ontology.isInstanceOf(e.getResource(), lake) && !ontology.isInstanceOf(e.getResource(), city)){
								isRemoveCurrentProperty = true;
								break;
							}
							else if (e.isProperty()) {
								isRemoveCurrentProperty = true;
								break;
							}
						}
					}
					else if (i < 0) {
						isRemoveCurrentProperty = true;
					}
					if (isRemoveCurrentProperty) {
						node.matchedEntitySet =null;
					}
				}
			}

		}
	}
	
	public Set<Entity> matchEntity (Collection <String> phrases, Object entityIndexMap) {
		Set<Entity> resSet = new HashSet<Entity> ();
		for (String phrase : phrases) {
			Set<Entity> tmpRes = matchEntity(phrase, entityIndexMap);
			if (tmpRes != null )
				resSet.addAll(tmpRes);
		}
		return (resSet.isEmpty() ? null : resSet);
	}
	
	public Set<Entity> matchEntity (String phrase, Object entityIndexMap ) {
		if (phrase == null || entityIndexMap == null )
			return null;
		
		Set<Entity> phraseSet = null; 
		if (entityIndexMap instanceof PrefixToEntitiesMap) {
			phraseSet = prefix2eMap.getEntitySet(phrase);
		}
		else if (entityIndexMap instanceof SuffixToEntitiesMap ) {
			phraseSet = suffix2eMap.getEntitySet(phrase);
		}
		else if (entityIndexMap instanceof StringToEntitiesMap ) {
			phraseSet = s2eMap.get(phrase);
		}	
		return phraseSet; 
	}	
	
	
	public Set<Entity> completeMatchDGNodeEntity (DGNode node, Object entityIndexMap) {
		if (node == null )
			return null;
		
		
		Set<Entity> wordSet = matchEntity (node.word, entityIndexMap);
		Set<Entity> stemSet = matchEntity (node.stem, entityIndexMap);

		if (wordSet == null  &&  stemSet == null )
			return null;
		Set<Entity> matchedEntitySet = new HashSet<Entity>();
		if (wordSet != null) {
			matchedEntitySet.addAll (wordSet); 
		}
		if (stemSet != null ) 
			matchedEntitySet.addAll (stemSet);
		
		if (matchedEntitySet.isEmpty() )
			return null;
		return matchedEntitySet;
	}
	
	
	
	public Set<Entity> synonymMatchDGNodeEntity (DGNode node , Object entityIndexMap) {
		if ( node == null )
			return null;
		Set<Entity> synMatchSet = new HashSet<Entity> ();
		Set<String> wordSyn = synonym.getSet(node.word, node.tag);
		Set<String> stemSyn = synonym.getSet(node.stem, node.tag);
		Set<String> synonyms = new HashSet<String> ();
		if (wordSyn != null )
			synonyms.addAll (wordSyn);
		if (stemSyn != null )
			synonyms.addAll (stemSyn);
		
		
//		System.out.println("syn : " + node.toString() + "\t synSet : " + StringUtils.join(synonyms, ", "));
		if (synonyms.isEmpty())
			return null;
		
		for (String p : synonyms) {
			Set<Entity> synSet = matchEntity(p, entityIndexMap);
			if (synSet != null )
				synMatchSet.addAll (synSet);
		}
		if (synMatchSet.isEmpty() )
			return null;
		return synMatchSet;
	}
	
	public void mather () {
		if ( semanticGraph == null )
			return ;
		List<DGNode> tokens = this.semanticGraph.getDependencyGraph().getVertexs();

		for (DGNode node : tokens ) {
			Map<MatchedEntity, Double> countMap = new HashMap <MatchedEntity, Double> (); 
			List<List<MatchedEntity>> matchedEntityList = new ArrayList<List<MatchedEntity>>();
			// str2map complete mathing
			
			Set<Entity> wordSet = s2eMap.get(node.word);
			Set<Entity> stemSet = s2eMap.get(node.stem);
			if (wordSet == null )
				wordSet = stemSet;
			else { 
				if (stemSet != null )
					wordSet.addAll(stemSet);
			}
			
			List<MatchedEntity> completeMatchedEntities = getMatchedEntities (wordSet, node.word, this.completeMatchScore, node.idx, 1);
			
			matchedEntityList.add (completeMatchedEntities);
			Set<String> wordSyn = synonym.getSet(node.word, node.tag);
			Set<String> stemSyn = synonym.getSet(node.stem, node.tag);
			Set<String> synonyms = new HashSet<String>();
			if (wordSyn != null )
				synonyms.addAll(wordSyn);
			if (stemSyn != null )
				synonyms.addAll(stemSyn);
			//System.out.println(phrase + ": " + synonyms);// debug
			Set<Entity> synEntities = getEntities(synonyms);
			// remove completely matched entities
			if (wordSet != null )
				synEntities.removeAll(wordSet);
			List<MatchedEntity> synMes = getMatchedEntities(synEntities,
					node.word, synonymMatchScore, node.idx, 1);
			
			matchedEntityList.add(synMes);
			
			/*RDFNode[] wordRdfNodes = ontology.search(node.word);
			RDFNode[] stemRdfNodes = ontology.search(node.stem);
			Set<RDFNode> rdfNodeSet = new HashSet<RDFNode> (Arrays.asList(wordRdfNodes));
			rdfNodeSet.addAll (new HashSet<RDFNode>(Arrays.asList(stemRdfNodes)));
			Set<Entity> entitySet = new HashSet<Entity>();
			for (RDFNode rdfNode : rdfNodeSet ) {
				if (rdfNode.isResource()) {
					Resource resource = rdfNode.asResource();
					entitySet.add (new Entity (resource ,  ontology.getLabel(rdfNode), ontology.getRDFNodeType(rdfNode)));
				}
			}
			List<MatchedEntity> ontoEntities = getMatchedEntities (entitySet, node.word, this.ontologyMatchScore, node.idx, 1);
			matchedEntityList.add ( ontoEntities );
			*/
			
			for (List<MatchedEntity> entityList : matchedEntityList) {
				for (MatchedEntity entity : entityList) {
					if (countMap.containsKey(entity)) {
						countMap.put(entity, countMap.get(entity) + 1.0);
					}
					else {
						countMap.put(entity, 1.0);
					}
				}
			}
			List<Map.Entry<MatchedEntity, Double>> sortEntityMapList = new ArrayList<Map.Entry<MatchedEntity, Double>>(countMap.entrySet());
			
			Collections.sort(sortEntityMapList, new Comparator <Map.Entry<MatchedEntity, Double>> () {
				public int compare (Map.Entry<MatchedEntity, Double> o1, Map.Entry<MatchedEntity, Double> o2) {
					return (int)((o2.getValue() - o1.getValue()) ); 
				}
			});
			List<MatchedEntity> tmpList = new ArrayList<MatchedEntity>();
			for (Map.Entry<MatchedEntity, Double> item : sortEntityMapList) {
				tmpList.add(item.getKey());
			}
			matchedEntityList.add(tmpList);
			this.matchedEntityMap.put(node,  matchedEntityList);
		}
	} 
	
	public Map <DGNode, List<List<MatchedEntity>>> getMatchedEntityMap () {
		return this.matchedEntityMap;
	}	
	
	public List <DGNode> getSentenceVertex () {
		if (this.semanticGraph == null )
			return null;
		if (this.semanticGraph.getDependencyGraph() == null )
			return null;
		return this.semanticGraph.getDependencyGraph().getVertexs();
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
				System.out.println ("phrase : " + phrase);
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
