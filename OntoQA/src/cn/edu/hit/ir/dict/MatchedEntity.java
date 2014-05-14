/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

import java.util.List;

import cn.edu.hit.ir.ontology.RDFNodeType;
import cn.edu.hit.scir.semanticgraph.DGNode;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * A data structure representing a query and its matched entity and
 * some other matching information.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-26
 */

public class MatchedEntity extends Entity {
	
	/**
	 * The query to match the entity.
	 */
	private String query;

	/**
	 * The score representing how much the query matches the entity.
	 */
	private double score;
	
	/**
	 * The index of the first token in this query
	 */
	private int begin;
	
	/**
	 * The number of the tokens in this query.
	 */
	private int numTokens;
	
	/**
	 * store the matched entity modifiers 
	 * 
	 */
	
	private List<DGNode> modifiers = null; 
	
	// 是否是查询点－－》 中文中使用
	private boolean isQueryTarget = false;
	
	/**
	 * Creates a new instance of MatchedEntity.
	 *
	 */
	public MatchedEntity(Resource resource, String label, RDFNodeType type, 
			String query, double score, int begin, int numTokens) {
		super(resource, label, type);
		setQuery(query);
		setScore(score);
		setBegin(begin);
		setNumTokens(numTokens);
	}
	
	public MatchedEntity(Entity entity, 
			String query, double score, int begin, int numTokens) {
		this(entity.getResource(), entity.getLabel(), entity.getType(), 
				query, score, begin, numTokens);
	}

//	public void setPathNode (PathNode pathNode ) {
//		this.pathNode = pathNode;
//	}	
//	
//	public PathNode getPathNode () {
//		return this.pathNode;
//	}
//	
	
	
	/**
	 * Get the query.
	 *
	 * @return The query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Set the query.
	 *
	 * @param query The query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Get the score.
	 *
	 * @return The score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Set the score.
	 *
	 * @param score The score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}
	
	/**
	 * Set the begin.
	 *
	 * @param begin The begin to set
	 */
	public void setBegin(int begin) {
		this.begin = begin;
	}

	/**
	 * Get the index of the first token in this query.
	 *
	 * @return The begin
	 */
	public int getBegin() {
		return begin;
	}
	
	/**
	 * Get the index of the last token in this query.
	 *
	 * @return
	 */
	public int getEnd() {
		return getBegin() + getNumTokens() - 1;
	}

	/**
	 * Get the numTokens.
	 *
	 * @return The numTokens
	 */
	public int getNumTokens() {
		return numTokens;
	}
	

	/**
	 * Set the numTokens.
	 *
	 * @param numTokens The numTokens to set
	 */
	public void setNumTokens(int numTokens) {
		this.numTokens = numTokens;
	}
	
	
	/**
	 * 获得这个这个实体是否是查询点
	 *
	 * @param 
	 * @return boolean 
	 */
	public boolean isQueryTarget() {
		return isQueryTarget;
	}

	/**
	 * 设定实体是否是查询点
	 *
	 * @param boolean 
	 * @return void 
	 */
	public void setQueryTarget(boolean isQueryTarget) {
		this.isQueryTarget = isQueryTarget;
	}

	/**
	 * set the entity modifiers 
	 * 
	 * @param 
	 * @return List<DGNode> 
	 */
	public List<DGNode> getModifizers() {
		return modifiers;
	}

	/**
	 * get the matched entity modifiers
	 *
	 * @param 
	 * @return void 
	 */
	public void setModifiers(List<DGNode> modifiers) {
		this.modifiers = modifiers;
	}

	public double getDistance() {
		double dis = 1 - score;
		if (dis < 0) {
			dis = 0;
		}
		return dis;
	}
	
	public String toString() {
		return "[" + super.toString() + ", " + query + ", " + numTokens + ", " + score + "]";
	}

}
