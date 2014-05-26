/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ProbabilityGraph;

import cn.edu.hit.ir.dict.MatchedEntity;
import cn.edu.hit.ir.graph.QueryNode;
import cn.edu.hit.ir.ontology.SchemaNode;
import cn.edu.hit.ir.util.Util;

import com.hp.hpl.jena.rdf.model.Resource;

/**
 * 
 * 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月24日 
 */
public class ProbabilityNode {
	
	private int id = 0;
	private static int nextId  = 100000;
	
	private MatchedEntity matchedEntity;

	private Resource resource;
	
	// 实体匹配的分值
	private double matchScore;
	
	// 和本体的相似度
	private double probabilityScore = 1.0;
	
	private boolean isLegal = false;
	
	// 标志这个节点是不是添加进来的，而不是匹配而来的
	private boolean isAdded = false;
	
	
	public ProbabilityNode(Resource resource, double addEntityScore) {
		this.setMatchedEntity(null);
		this.setResource(resource);
		this.setAdded(true);
		this.setMatchScore(addEntityScore);
		this.setLegal(false);
		id = nextId++;
	}
	
	public ProbabilityNode (MatchedEntity matchedEntity, int id) {
		this.setMatchedEntity(matchedEntity);
		this.setResource(matchedEntity.getResource());
		this.setMatchScore(matchedEntity.getScore());
		this.setLegal(false);
		this.id = id;
	}
	
	public double getMatchScore() {
		return matchScore;
	}

	public void setMatchScore(double matchScore) {
		this.matchScore = matchScore;
	}

	public double getProbabilityScore() {
		return probabilityScore;
	}

	public void setProbabilityScore(double probabilityScore) {
		this.probabilityScore = probabilityScore;
	}

	/**
	 * Set the resource.
	 *
	 * @param resource The resource to set
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Get the resource.
	 *
	 * @return The resource
	 */
	public Resource getResource() {
		return resource;
	}
	
	
	public MatchedEntity getMatchedEntity() {
		return matchedEntity;
	}

	public void setMatchedEntity(MatchedEntity matchedEntity) {
		this.matchedEntity = matchedEntity;
	}

	public boolean isAdded() {
		return isAdded;
	}

	public void setAdded(boolean isAdded) {
		this.isAdded = isAdded;
	}

	
	
//	public String toString() {
//		String s = resource.toString();
//		return "[" + Util.lastWord(s) + "->" + this.isAdded + ", " + this.matchScore + ", " + this.probabilityScore + "]";
//	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isLegal() {
		return isLegal;
	}

	public void setLegal(boolean isLegal) {
		this.isLegal = isLegal;
	}
	
	public int hashCode() {
		return resource.hashCode() + id;
	}

	public boolean equals(Object obj) {
		if (obj instanceof ProbabilityNode) {
			ProbabilityNode probabilityNode = (ProbabilityNode)obj;
			return probabilityNode.id == this.id &&  probabilityNode.getResource().equals(resource);
		}
		return false;
	}
	public String toString() {
		String s = (matchedEntity == null) ? "_" : "";
		s += Util.lastWord(resource) + "_" + id;
		return "[" + s + ", " + this.isAdded + ", " + this.matchScore + ", " + this.probabilityScore + "]";
	}

}
