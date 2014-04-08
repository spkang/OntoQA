package cn.edu.hit.scir.dependency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MultiMap;
import org.apache.log4j.Logger;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TypedDependency;



/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月25日 
 */
public class TripleExtracter {
	private static Logger logger = Logger.getLogger(TripleExtracter.class);
	private Question question = Question.getInstance();
	private StanfordTagger tagger = StanfordTagger.getInstance();
	private static TripleExtracter instance = new TripleExtracter();
	 
	public static TripleExtracter getInstance () {
		return instance;
	}  
	
	private TripleExtracter () {
	}

	public void initSentence (String rawSentence ) {
		question.initialize(rawSentence);
		List<TaggedWord> taggedWords = tagger.taggerSentence(question.getProcessedQuestion()); 
		question.setTaggedWord(taggedWords);
	} 
	
	public List<String> extractTriple ()  {
		// nsubj(ref) -> prep -> pobj
		final String nsubj = "nsubj";
		final String prep  = "prep";
		final String pobj  = "pobj";
		final String ref   = "ref";
		final String dobj  = "dobj";
		
		List<TypedDependency> tdl = question.getDependencies(question.getTaggedWord());
		Map<String, List<RelationPair>> mapReln = new HashMap <String, List<RelationPair>>();
		
//		List<TreeGraphNode> wordList = new ArrayList<TreeGraphNode> ();
//		StanfordParser parser = question.getStanfordParser();
//		GrammaticalStructure gs = parser.getGrammaticalStructure(question.getTaggedWord());
//		
		//logger.info("path : " + gs.getDependencyPath(10, 0).toString());
		
		QueryTriple qt = new QueryTriple();
		QueryTripleElement s = new QueryTripleElement();
		QueryTripleElement p = new QueryTripleElement();
		QueryTripleElement o = new QueryTripleElement();
		
		if (mapReln.containsKey(nsubj)) {
			List<RelationPair> relnPairs = mapReln.get(nsubj);
			for (RelationPair rp : relnPairs) {
				//if ()
			}
		}
		
		for (TypedDependency td : tdl) {
			logger.info(td.toString());
//			if (mapReln.containsKey(td.reln().toString())) {
//				
//			}
		}
		return null;
	}
	
	/**
	 * tdl --> map relation
	 *
	 * @param tdl, the relation list
	 * @return Map<String,List<RelationPair>> 
	 */
	public Map<String, List<RelationPair>> extractRelationMap (List<TypedDependency> tdl)  {
		Map<String, List<RelationPair>> mapReln = new HashMap <String, List<RelationPair>>();
		for (TypedDependency td : tdl) {
			RelationPair rp = new RelationPair(td.gov(), td.dep());
			if (mapReln.containsKey(td.reln().toString())) {
				List<RelationPair> tmpList = mapReln.get(td.reln().toString());
				tmpList.add(rp);
			}
			else {
				List<RelationPair> tmpList = new ArrayList<RelationPair>();
				tmpList.add(rp);
				mapReln.put(td.reln().toString(), tmpList);
			}
		}
		return mapReln;
	}
	
	public static void main (String [] args) {
		
		TripleExtracter instance = TripleExtracter.getInstance();
		instance.initSentence("how high is the highest point in the largest state ?");
		instance.extractTriple();
	}
}
