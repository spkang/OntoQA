/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.dependency;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.ontology.RDFNodeType;
import cn.edu.hit.scir.EntityMatcher.ChineseQuerySegment;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.TypedDependency;

/**
 * 根据ChineseQuerySegment 的结果进行Stanford dependency parser 的结果构造
 * 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月10日 
 */
public class ChineseStanfordBasedGraph {
	private static StanfordParser parser = null;
	
	// 用来实现基本的图存储和操作
	private ChineseBasedGraph cnBasedGraph = null;
	
	// 用来分词和实体匹配的结果进行合并
	private ChineseQuerySegment querySeg = null;
	
	// 用于实体合并
	private  static Ontology ontology = Ontology.getInstance();
	
	
	// 用于存储最初匹配的到的实体
	private List<List<MatchedEntity>> matchedEntities = null;
	
	// 存储合并后的实体
	private List<List<MatchedEntity>> mergedMatchedEntities = null;
	
	private static Logger logger = Logger.getLogger(ChineseStanfordBasedGraph.class);
	
	public ChineseStanfordBasedGraph (String query) {
		initGraph (query);
	}
	
	private void initGraph (String query ) {
		if (query == null )
			return ;
		if (parser == null )
			this.parser = new StanfordParser (true); // 中文的parser
		this.querySeg = new ChineseQuerySegment (query);
		List<CoreLabel> words =  this.parser.tokenizerString(this.querySeg.getMergedQuery());
		List<TypedDependency> typedDeps = this.parser.getChineseDependency(words);
		List<TaggedWord> taggedWds = this.parser.getTaggedWords(words);
		List<ChineseWord> chineseWordList = new ArrayList<ChineseWord> ();
		int idx = 0, begin = 0, end = 0;
		for (TaggedWord tw : taggedWds ) {
			end += tw.word().length();
			chineseWordList.add (new ChineseWord (tw.word(), tw.tag(), idx, begin, end));
			++idx;
			begin = end;
		}
		
		//logger.info("chineseWordList : " + StringUtils.join(chineseWordList, ", "));
		
		List<GraphNode> graphNodeList = new ArrayList<GraphNode> ();
		
		for (TypedDependency td : typedDeps ) {
			logger.info ("relation : " + td.toString());
			if (td.reln().toString().toLowerCase().equals("root"))
				continue;
			int govIndex = td.gov().index() - 1;
			int depIndex = td.dep().index() - 1;
			graphNodeList.add(new GraphNode (td.reln().toString(), chineseWordList.get(govIndex), chineseWordList.get(depIndex), true, true));
		}
		//logger.info("GraphNodeList : " + StringUtils.join(graphNodeList, ", "));
		
		this.cnBasedGraph = new ChineseBasedGraph (chineseWordList, graphNodeList);
		this.matchedEntities = this.querySeg.getEmEngine().getMatchedQuery();
		
		mergeAdjoinEntities();
		
	}
	
	
	/**
	 * 对相邻的两个实体进行合并
	 *
	 * @param void
	 * @return void 
	 */
	public void  mergeAdjoinEntities () {
		for (int i = 0; i < this.matchedEntities.size(); ++i) {
			List<MatchedEntity> lhsMes = this.matchedEntities.get(i);
			if (lhsMes == null || lhsMes.isEmpty() )
				continue;
			
			//  查找下一非空的实体
			int j = i + 1;
			List<MatchedEntity> rhsMes = null;
			while ( j < this.matchedEntities.size() ) {
				rhsMes = matchedEntities.get(j);
				if (rhsMes != null && !rhsMes.isEmpty() && this.cnBasedGraph.getVertex(rhsMes.get(0).getBegin()).prevIndex == -1)
					break;
				++j;
			}
			
			boolean stop = false;
			for (int k = 0; k < lhsMes.size() && !stop; ++k ) {
				for (int m = 0; m  < rhsMes.size(); ++m ) {
					if (merger (lhsMes.get(k), rhsMes.get(m))) {
						stop = true;
						break;
					}
				} 
			}
		}
	}
	
	
	private void connectMatchedEntiies (MatchedEntity lhs, MatchedEntity rhs, boolean isLhsClass ) {
//		String [] tokens = new String[this.querySeg.getMergedWords().size ()];
//		this.querySeg.getMergedWords().toArray(tokens);
		int numTokens = lhs.getNumTokens() + rhs.getNumTokens();
		String query = lhs.getQuery() + " " +rhs.getQuery();
		MatchedEntity mergedEntity = null;
		logger.info ("lhs me : " + lhs.toString());
		logger.info ("rhs me : " + rhs.toString());
		if (isLhsClass) {
			mergedEntity= new MatchedEntity (rhs.getResource(), rhs.getLabel(), RDFNodeType.INSTANCE, query, (lhs.getScore() + rhs.getScore())/2.0, lhs.getBegin(), numTokens);
		}
		else {
			mergedEntity = new MatchedEntity (lhs.getResource(), lhs.getLabel(), RDFNodeType.INSTANCE, query, (lhs.getScore() + rhs.getScore())/2.0, lhs.getBegin(), numTokens);
		}
		
		List<ChineseWord> queryNodes = this.cnBasedGraph.getVertexs();
		int nextIndex = lhs.getBegin();
		int prevIndex = -1;
		while (nextIndex != -1) { // 找到最后一个
			prevIndex = nextIndex;
			nextIndex = queryNodes.get(nextIndex).nextIndex;
		}
		queryNodes.get(prevIndex).nextIndex =  rhs.getBegin();
		queryNodes.get(rhs.getBegin()).prevIndex = prevIndex;
		
		logger.info("queryNodes : " + StringUtils.join(queryNodes, ", "));
		
		nextIndex = lhs.getBegin();
		int t = 0;
		while (nextIndex != -1) {
			logger.info("nextIndex : " + nextIndex);
			this.matchedEntities.get(nextIndex).clear();
			this.matchedEntities.get(nextIndex).add(mergedEntity);
			logger.info("querNodes get index : " + queryNodes.get(nextIndex).toString());
			nextIndex = queryNodes.get(nextIndex).nextIndex;
		}
	}
	
	private boolean merger (MatchedEntity lhs, MatchedEntity rhs ) {
		if (lhs == null || rhs == null )
			return false;
		
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

	@Override
	public String toString() {
		return "ChineseStanfordBasedGraph [cnBasedGraph=" + cnBasedGraph + "]";
	}
}
