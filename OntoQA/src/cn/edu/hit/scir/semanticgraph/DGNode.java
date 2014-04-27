/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.scir.semanticgraph;

import java.util.Set;

import cn.edu.hit.ir.dict.Entity;
import edu.stanford.nlp.util.StringUtils;

/**
 * the node of semantic graph
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月28日 
 */
public class DGNode  {
	public String word = null;
	public String stem = null;
	public String tag = null;
	public Integer idx = null; 
	public Set<Entity> matchedEntitySet = null;
	public boolean isSynonymMatch = false;
	public int prevIndex = -1;
	public int nextIndex = -1;
	/**
	 * construct dependency node
	 *
	 * @param word, the word in processed question
	 * @param stem, the stem in processed question
	 * @param tag,  the tag  in processed question
	 * @return 
	 */
	public DGNode (String word, String stem, String tag, Integer idx) {
		this.word = word;
		this.stem = stem;
		this.tag = tag;
		this.idx = idx;
		this.matchedEntitySet = null;
		this.isSynonymMatch = false;
		this.prevIndex = -1;
		this.nextIndex = -1;
	}
	
	public DGNode (DGNode other) {
		this(other.word, other.stem, other.tag, other.idx);
		this.matchedEntitySet = other.matchedEntitySet;
		this.isSynonymMatch = other.isSynonymMatch;
		this.prevIndex = other.prevIndex;
		this.nextIndex = other.nextIndex;
	}
	
//	@Override
//	public boolean equals (Object obj ) {
//		if (obj  == null || !(obj instanceof DGNode))
//			return false;
//		DGNode other = (DGNode) (obj);
//		System.out.println("call DGNode equals");
//		return (this.word.equals(other.word) && this.stem.equals(other.stem) && this.tag.equals(other.tag) && this.idx.equals(other.idx));
//	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idx == null) ? 0 : idx.hashCode());
		result = prime * result + ((stem == null) ? 0 : stem.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DGNode other = (DGNode) obj;
		if (idx == null) {
			if (other.idx != null)
				return false;
		} else if (!idx.equals(other.idx))
			return false;
		if (stem == null) {
			if (other.stem != null)
				return false;
		} else if (!stem.equals(other.stem))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}
	
	public String toString () {
		StringBuffer sb = new StringBuffer ();
		sb.append("[").append (this.word + ", " + this.stem + ", " + this.tag + ", " + this.idx.toString() + ", (" + (this.matchedEntitySet == null ? "null" : StringUtils.join(this.matchedEntitySet, ", ")) + "), " + this.isSynonymMatch  + ", " + this.prevIndex + ", " + this.nextIndex).append("]");
		return sb.toString();
	}
	
}




