/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.EntityMatcher;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.scir.dependency.ChineseStanfordBasedGraph;
import cn.edu.hit.scir.dependency.ChineseWord;

/**
 * 中文匹配实体的容器	 
 * 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月8日 
 */
public class ChineseQueryMatchedEntityWrapper {
	private List<List<MatchedEntity>> orgMatchedQuery = null; // 原始匹配的实体
	private List<List<MatchedEntity>> matchedQueryWrapper = null;    // 经过整理的实体
	private ChineseStanfordBasedGraph cnStanfordBasedGraph = null; // 中文的基于stanford的依存图
	
	//private ChineseEntityMatcherEngine cqmEngine = ChineseEntityMatcherEngine.getInstance();
	
	/**
	 * wrapper 构造函数， 对输入的query进行实体匹配
	 *
	 * @param
	 * @return 
	 */
	public ChineseQueryMatchedEntityWrapper (String query ) {
		initResource (query);
	}
	
	private void initResource (String query ) {
//		cqmEngine.queryEntityMatcher(query);
//		this.orgMatchedQuery = cqmEngine.getMatchedQuery();
		this.cnStanfordBasedGraph = new ChineseStanfordBasedGraph (query);
		this.orgMatchedQuery = this.cnStanfordBasedGraph.getQueryWordMatchedEntities();
		this.matchedQueryWrapper = new ArrayList<List<MatchedEntity>> ();
		extractMatchedQueryWrapper();
	}
	
	private void extractMatchedQueryWrapper () {
		this.matchedQueryWrapper.clear();
		List<ChineseWord> queryWord = this.cnStanfordBasedGraph.getCnBasedGraph().getVertexs();
		for (int i = 0; i < this.orgMatchedQuery.size(); ++i ) {
			List<MatchedEntity> mes = this.orgMatchedQuery.get(i);
			if (mes == null || mes.isEmpty())
				continue;
			if (queryWord.get(i).prevIndex == -1) {
				this.matchedQueryWrapper.add(new ArrayList<MatchedEntity>(mes)); 
			}
		}
	}

	public List<List<MatchedEntity>> getOrgMatchedQuery() {
		return orgMatchedQuery;
	}

	public void setOrgMatchedQuery(List<List<MatchedEntity>> orgMatchedQuery) {
		this.orgMatchedQuery = orgMatchedQuery;
	}

	public List<List<MatchedEntity>> getMatchedQueryWrapper() {
		return matchedQueryWrapper;
	}

	public void setMatchedQueryWrapper(List<List<MatchedEntity>> matchedQueryWrapper) {
		this.matchedQueryWrapper = matchedQueryWrapper;
	}
	
	public boolean isIndexLegal (int index ) {
		if (index < 0 || index >= this.matchedQueryWrapper.size())
			return false;
		return true;
	}
	
	public boolean hasNextIndex (int index) {
		if (isIndexLegal (index))
			return true;
		return false;
	}
	
	public int nextIndex (int index ) {
		if (isIndexLegal (index  + 1))
			return  index + 1;
		return -1;
	}
	
	public int getMatchedEntityWrapperSize () {
		return this.matchedQueryWrapper.size();
	}
	
	public boolean isCount () {
		return false;
	}
	
	public ChineseStanfordBasedGraph getCnStanfordBasedGraph() {
		return cnStanfordBasedGraph;
	}

	public void setCnStanfordBasedGraph(
			ChineseStanfordBasedGraph cnStanfordBasedGraph) {
		this.cnStanfordBasedGraph = cnStanfordBasedGraph;
	}

	public List<MatchedEntity> getEntities (int index ) {
		if (isIndexLegal (index)) {
			return this.matchedQueryWrapper.get(index);
		}
		return null;
	} 
	
}
