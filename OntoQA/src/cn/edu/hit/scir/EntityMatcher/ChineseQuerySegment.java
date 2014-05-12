/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.EntityMatcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import cn.edu.hit.scir.ltp.LtpTool;
import cn.edu.hit.scir.ltp.LtpUtil;

/**
 * 根据实体匹配的结果和ltp分词的结果对
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月10日 
 */
public class ChineseQuerySegment {
	// 对给定的查询进行实体匹配（正向最大匹配）
	private ChineseEntityMatcherEngine emEngine = ChineseEntityMatcherEngine.getInstance();
	
	// 用于分词
	private LtpTool tool = LtpUtil.getInstance();
	// 最初分词结果
	private List<String> words = null;
	
	// 最初实体匹配的字符串
	private List<String> matchedEntityWord = null;
	
	// words 经过matchedEntityWord合并后的结果
	private List<String> mergedWords = null;
	
	private String mergedQuery = null;
	
	/**
	 * 构造函数
	 *
	 * @param
	 * @return 
	 */
	public ChineseQuerySegment (String orgQuery) {
		// 实体匹配
		emEngine.queryEntityMatcher(orgQuery);
		
		// 获得匹配上实体的短语
		this.matchedEntityWord = emEngine.getMatchedWords();
		// 进行分词结果和实体匹配结果合并
		
		// segment words 
		this.words = tool.ltpSegment(emEngine.getProcessedQuery());
		
		mergeWords();
		
		mergedQuery ();
		
	}
	
	

	/**
	 * 分词结果和实体匹配结果进行合并
	 *
	 * @param 
	 * @return void 
	 */
	public void mergeWords () {
		this.mergedWords = new ArrayList<String> ();
		Set<String> matchedSet = new HashSet<String>(this.matchedEntityWord);
		String tmpWord = "";
		int i = 0; 
		while (i < words.size()){
			boolean merge = false;
			for (int j = words.size(); j > i + 1; --j ) {
				tmpWord = join(words, "", i, j);
				if (matchedSet.contains(tmpWord)) {
					this.mergedWords.add(tmpWord);
					merge = true;
					i = j - 1;
					break;
				}
			}
			if (!merge) {
				this.mergedWords.add(words.get(i));
			}
			++i;
		}
	}
	
	/**
	 * 对begin到end的array中的字符进行合并，中间以指定的分隔符进行链接
	 *
	 * @param array
	 * @param seperator 
	 * @param begin , include 
	 * @param end, not include 
	 * @return String 
	 */
	private String join (List<String> array, String seperator, int begin, int end) {
		if (begin < 0 || begin > end || begin > array.size() || end < 0 || end > array.size())
			return "";
		
		String res = "";
		for (int i = begin; i < end; ++i ) {
			res += array.get(i);
			if (i != end - 1)
				res += seperator;
		}
		return res;
	}
	
	/**
	 * 根据合并后的结果，合成一个以空格链接的字符串，用来给parser进行分析
	 *
	 * @param 
	 * @return void 
	 */
	private void mergedQuery () {
		if (this.mergedWords == null )
			return ;
		this.mergedQuery = StringUtils.join (this.mergedWords, " ");
	}
	
	public String getMergedQuery () {
		return this.mergedQuery;
	}

	public List<String> getMergedWords() {
		return mergedWords;
	}

	public void setMergedWords(List<String> mergedWords) {
		this.mergedWords = mergedWords;
	}
	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}
}
