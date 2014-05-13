/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.dependency;

/** 
 *  用于存储中文的词
 *  
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月10日 
 */
public class ChineseWord {
	public String word = null; // 存放这个词 
	public String tag = null;  // 词的词性
	public int idx = 0; // 这个词在句子中的位置
	public int wordBegin = -1;  // 这个词在句子中的开始位置
	public int wordEnd = -1;    // 这个词在句子中的结束位置
	public int prevIndex = -1;  // 用于实体合并
	public int nextIndex = -1;  // 用于实体合并
	
	
	public ChineseWord (String word, String tag, int idx, int wordBegin, int wordEnd) {
		this(word, tag, idx, wordBegin, wordEnd, -1, -1);
	}
	
	public ChineseWord (String word, String tag, int idx, int wordBegin, int wordEnd, int prevIndex, int nextIndex) {
		this.word = word;
		this.tag = tag;
		this.idx = idx;
		this.wordBegin = wordBegin;
		this.wordEnd = wordEnd;
		this.prevIndex = prevIndex;
		this.nextIndex = nextIndex;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idx;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		result = prime * result + wordBegin;
		result = prime * result + wordEnd;
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
		ChineseWord other = (ChineseWord) obj;
		if (idx != other.idx)
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
		if (wordBegin != other.wordBegin)
			return false;
		if (wordEnd != other.wordEnd)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChineseWord [" + word + " " + tag + " " + idx
				+ " " + wordBegin + " " + wordEnd
				+ " " + prevIndex + " " + nextIndex + "]";
	}
}
