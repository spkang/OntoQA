/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.dependency;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.hit.scir.EntityMatcher.ChineseQuerySegment;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.StringUtils;

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
		logger.info("GraphNodeList : " + StringUtils.join(graphNodeList, ", "));
		
		this.cnBasedGraph = new ChineseBasedGraph (chineseWordList, graphNodeList);
	}
	
	
	
}
