/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.graph;

import cn.edu.hit.ir.dict.Entity;
import cn.edu.hit.ir.dict.MatchedEntity;

/**
 * A node class in EntityGraph.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-30
 */


class MatchedEntityNode {
	public MatchedEntity matchedEntity;
	
	public int index;
	
	public MatchedEntityNode(MatchedEntity matchedEntity, int index) {
		this.matchedEntity = matchedEntity;
		this.index = index;
	}
	
	public int getBegin() {
		return matchedEntity.getBegin();
	}
	
	public int getEnd() {
		return matchedEntity.getEnd();
	}
	
	public Entity getEntity() {
		return matchedEntity;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String toString() {
		if (matchedEntity != null) {
			return "#" + getIndex() + " " + matchedEntity.toString();
		} else {
			return "#" + getIndex() + " [null]";
		}
	}
}
