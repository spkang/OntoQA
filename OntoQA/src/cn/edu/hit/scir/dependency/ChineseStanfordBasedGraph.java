/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.dependency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.ontology.RDFNodeType;
import cn.edu.hit.scir.ChineseQuery.ChineseQueryDict;
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
	
	// 疑问词典
	private static ChineseQueryDict cnQueryDict = ChineseQueryDict.getInstance();
	
	// 用于存储最初匹配的到的实体
	private List<List<MatchedEntity>> queryWordMatchedEntities = null;
	
	// 存储合并后的实体
	//private List<List<MatchedEntity>> mergedMatchedEntities = null;
	
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
		this.queryWordMatchedEntities = new ArrayList<List<MatchedEntity>>(this.querySeg.getEmEngine().getQueryWordMatchedEntities());
		
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
		
		logger.info("chineseWordList : " + StringUtils.join(chineseWordList, ", "));
		
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
		
		mergeAdjoinEntities();
		
		mergeTopAttrEntities();
		
		tagSelectTarget();
		
//		logger.info("equal--------: " + (this.queryWordMatchedEntities.size() == this.querySeg.getMergedWords().size())  + "\t merged word size : " + this.querySeg.getMergedWords().size() + "\tmes size : " + this.queryWordMatchedEntities.size());
//		logger.info ("word : " + StringUtils.join(this.querySeg.getMergedWords(), ", "));
//		logger.info("mes : ");
//		for (List<MatchedEntity> mes : this.queryWordMatchedEntities) {
//			System.out.println ("me : " + StringUtils.join (mes, ", "));
//		}
	}
	
	
	/**
	 * 根据疑问词对匹配的实体进行查询点标注
	 *
	 * @param 
	 * @return void 
	 */
	private void tagSelectTarget () {
		int queryWordIndex = -1;
		for (int i = 0; i < this.querySeg.getMergedWords ().size(); ++i ) {
			String word = this.querySeg.getMergedWords().get(i);
			if (this.cnQueryDict.isInDict(word)) { // 找到疑问词
				queryWordIndex = i;
				break;
			}
		}
		
		if (queryWordIndex == -1) // 没有疑问词， 或者是疑问词被分词的时候切分了
			return ;
		
		// 查找疑问词链接的实体
		int lhs = queryWordIndex - 1;
		int rhs = queryWordIndex + 1;
		List<List<Integer>> allPath = new ArrayList<List<Integer>> ();
		while (true) {
			if (lhs >= 0 || rhs < this.queryWordMatchedEntities.size()) {
				// 向疑问词的左边搜索
				if (lhs >= 0) { 
					List<Integer> subPath = this.cnBasedGraph.searchPath(queryWordIndex, lhs);
					if (subPath != null && subPath.size () > 1) {
						allPath.add(subPath);
					}
					--lhs;
				}
				// 向疑问词的右边搜索实体
				if (rhs < this.queryWordMatchedEntities.size() ) {
					List<Integer> subPath = this.cnBasedGraph.searchPath(queryWordIndex, rhs);
					if (subPath != null && subPath.size () > 1) {
						allPath.add(subPath);
					}
					++rhs;
				}
			}
			else {
				break;
			}
		}
		
		// 对搜索到的路径进行排序
		Collections.sort(allPath, new Comparator () {
			public int compare (Object o1, Object o2) {
				List<Integer> path1 = (List<Integer>)o1;
				List<Integer> path2 = (List<Integer>)o2;
				return path1.size() - path2.size();
			}
		});
		
		for (List<Integer> path : allPath) {
			// 疑问词<--det---实体
			if ( this.queryWordMatchedEntities.get(path.get(path.size()-1)).isEmpty())
				continue;
			if (isSingleRelation (path, "det")) {
				setQueryTarget (path.get(path.size()-1));
//				logger.info("target-----------=======>>>>>>>>" + this.queryWordMatchedEntities.get(path.get(path.size()-1)));
				break;
			}
			// 疑问词<--assmod---实体
			else if (isSingleRelation (path, "assmod")) {
				setQueryTarget (path.get(path.size()-1));
//				logger.info("target-----------=======>>>>>>>>" + this.queryWordMatchedEntities.get(path.get(path.size()-1)));
				break;
			}
			// 疑问词<--nummod---实体 like ： 多少专辑
			else if (isSingleRelation(path, "nummod")) {
				setQueryTarget (path.get(path.size()-1));
//				logger.info("target-----------=======>>>>>>>>" + this.queryWordMatchedEntities.get(path.get(path.size()-1)));
				break;
			}
			// 实体 <--top-- linker or holder---attr--> 疑问词
			else if (isDoubleRelation (path, "top", "attr")) {
				setQueryTarget (path.get(path.size()-1));
//				logger.info("target-----------=======>>>>>>>>" + this.queryWordMatchedEntities.get(path.get(path.size()-1)));
				break;
			}
			// 实体 <--top-- linker or holder---dobj--> 疑问词
			else if (isDoubleRelation (path, "top", "dobj")) {
				setQueryTarget (path.get(path.size()-1));
//				logger.info("target-----------=======>>>>>>>>" + this.queryWordMatchedEntities.get(path.get(path.size()-1)));
				break;
			}
		}
		
	}
	
	private void setQueryTarget (int matchedEntityIndex) {
		if (matchedEntityIndex < 0 || matchedEntityIndex >= this.queryWordMatchedEntities.size()) 
			return ;
		for (MatchedEntity me : this.getQueryWordMatchedEntities().get(matchedEntityIndex)) {
			me.setQueryTarget(true);
		}
	}
	
	
	/**
	 * 判断这个路径是不是 reln 链接的路径
	 *
	 * @param 链接路径(路径上只有两个节点)
	 * @return boolean 
	 */
	private boolean isSingleRelation (List<Integer> path, final String reln) {
		if (reln == null || path == null || path.size() != 2) return false;
		List<String> relnPath = this.cnBasedGraph.searchRelationPath(path);
		if (relnPath != null && relnPath.size() == 1) {
			if (relnPath.get(0).toLowerCase().equals(reln.toLowerCase()))
				return true;
		}
		return false;
	}
	
	
	/**
	 * 判断一个路径上是不是只有两个特定的关系链接
	 *
	 * @param path,路径节点
	 * @param lhsReln , one relation
	 * @param rhsReln , one relation
	 * @return boolean 
	 */
	private boolean isDoubleRelation (List<Integer> path, final String lhsReln, final String rhsReln) {
		if (path == null || path.size() != 3) return false;
		List<String> relnPath = this.cnBasedGraph.searchRelationPath(path);
		if (relnPath != null && relnPath.size() > 1) {
			if (relnPath.get(0).toLowerCase().equals(lhsReln.toLowerCase()) && relnPath.get(relnPath.size()-1).toLowerCase().equals(rhsReln.toLowerCase())) {
				return true;
			}
			else if (relnPath.get(0).toLowerCase().equals(rhsReln.toLowerCase()) && relnPath.get(relnPath.size()-1).toLowerCase().equals(lhsReln.toLowerCase())) {
				return true;
			}
		} 
		return false;
	} 
	
	
	/**
	 * 判断path之间连接的收拾top－attr关系
	 *
	 * @param 实体之间的路径
	 * @return boolean 
	 */
	private boolean isTopAttrRellationOfPath (List<Integer> path ) {
		if (path == null || path.size() != 3) return false;
		List<String> relnPath = this.cnBasedGraph.searchRelationPath(path);
		if (relnPath != null && relnPath.size() > 1) {
			if (relnPath.get(0).toLowerCase().equals("top") && relnPath.get(relnPath.size()-1).toLowerCase().equals("attr")) {
				return true;
			}
			else if (relnPath.get(0).toLowerCase().equals("attr") && relnPath.get(relnPath.size()-1).toLowerCase().equals("top")) {
				return true;
			}
		} 
		return false;
	}
	
	/**
	 * 对top attr的实体进行合并
	 * like : 刘德华是哪里的歌手？
	 * 	刘德华 <--top--是--attr-->歌手
	 *
	 * @param 
	 * @return void 
	 */
	private void mergeTopAttrEntities () {
		for (int i = 0; i < this.queryWordMatchedEntities.size (); ++i ) {
			if (this.queryWordMatchedEntities.get(i) == null || this.queryWordMatchedEntities.get(i).isEmpty() || this.cnBasedGraph.getVertexs().get(i).prevIndex != -1)
				continue;
			for (int j = i + 1; j < this.queryWordMatchedEntities.size(); ++j ) {
				if (this.queryWordMatchedEntities.get(j) == null || this.queryWordMatchedEntities.get(j).isEmpty() || this.cnBasedGraph.getVertexs().get(j).prevIndex != -1)
					continue;
				List<Integer> path = this.cnBasedGraph.searchPath(i, j);
				if (isTopAttrRellationOfPath (path)) {
					List<MatchedEntity> srcMes = this.queryWordMatchedEntities.get(i);
					List<MatchedEntity> desMes = this.queryWordMatchedEntities.get(j);
					
					boolean stop = false;
					for (int k = 0; k < srcMes.size() && !stop; ++k ) {
						for (int m = 0; m  < desMes.size(); ++m ) {
							if (merger (srcMes.get(k), desMes.get(m))) {
								logger.info ("relation merged : " + srcMes.toString());
								logger.info ("relation merged : " + desMes.toString());
								stop = true;
								break;
							}
						} 
					}
				}
			}
		}
	}
	
	
	/**
	 * 对相邻的两个实体进行合并
	 *
	 * @param void
	 * @return void 
	 */
	public void  mergeAdjoinEntities () {
		for (int i = 0; i < this.queryWordMatchedEntities.size(); ++i) {
			List<MatchedEntity> lhsMes = this.queryWordMatchedEntities.get(i);
			if (lhsMes == null || lhsMes.isEmpty() )
				continue;
			
			//  查找下一非空的实体
			int j = i + 1;
			List<MatchedEntity> rhsMes = null;
			while ( j < this.queryWordMatchedEntities.size() ) {
				rhsMes = queryWordMatchedEntities.get(j);
				if (rhsMes != null && !rhsMes.isEmpty() && this.cnBasedGraph.getVertex(rhsMes.get(0).getBegin()).prevIndex == -1)
					break;
				++j;
			}
			if (rhsMes == null )
				continue;
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
	
	
	/**
	 * 链接两个实体
	 *
	 * @param lhs, 第一个实体
	 * @param rhs, 第二个实体
	 * @param isLhsClass, 第一个实体是不是class
	 * @return void 
	 */
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
			this.queryWordMatchedEntities.get(nextIndex).clear();
			this.queryWordMatchedEntities.get(nextIndex).add(mergedEntity);
			nextIndex = queryNodes.get(nextIndex).nextIndex;
		}
	}
	
	/**
	 *  首先判断给定的两个实体能不能合并，如果可以合并，然后进行合并
	 *
	 * @param 
	 * @return boolean 
	 */
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

	public List<List<MatchedEntity>> getQueryWordMatchedEntities() {
		return queryWordMatchedEntities;
	}


	public ChineseBasedGraph getCnBasedGraph() {
		return cnBasedGraph;
	}


	public void setCnBasedGraph(ChineseBasedGraph cnBasedGraph) {
		this.cnBasedGraph = cnBasedGraph;
	}


	public ChineseQuerySegment getQuerySeg() {
		return querySeg;
	}


	public void setQuerySeg(ChineseQuerySegment querySeg) {
		this.querySeg = querySeg;
	}


	public static Ontology getOntology() {
		return ontology;
	}


	public static void setOntology(Ontology ontology) {
		ChineseStanfordBasedGraph.ontology = ontology;
	}


	public static ChineseQueryDict getCnQueryDict() {
		return cnQueryDict;
	}


	public static void setCnQueryDict(ChineseQueryDict cnQueryDict) {
		ChineseStanfordBasedGraph.cnQueryDict = cnQueryDict;
	}


	public void setQueryWordMatchedEntities(
			List<List<MatchedEntity>> queryWordMatchedEntities) {
		this.queryWordMatchedEntities = queryWordMatchedEntities;
	}

	@Override
	public String toString() {
		return "ChineseStanfordBasedGraph [cnBasedGraph=" + cnBasedGraph + "]";
	}
}
