/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.dependency;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.edu.hit.ir.questionanalysis.QuestionNormalizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.StringUtils;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月27日 
 */
public class StanfordEnglishNlpToolTest {

	private StanfordEnglishNlpTool tool = StanfordEnglishNlpTool.getInstance();
	
	@Before
	public void setUp () throws Exception {
		System.out.println ("setup");
	}
	
	@After
	public void tearDown () throws Exception {
		System.out.println ("tearDown");
	}
	
	@Test
	public void test() {
		System.out.println ("@test");
		
		testStanfordEnglishNlpTool("give me the states that border utah ?");
		
		
	}
	
	
	
	public void testStanfordEnglishNlpTool (String sentence) {
		System.out.println ("@testStanfordEnglishNlpTool");
		QuestionNormalizer qn = QuestionNormalizer.getInstance ();
		
		String proSentence = qn.dropPunctuationMarks(sentence);
		proSentence = qn.normalize(proSentence);
		
		List<CoreLabel> tokenList = tool.tokenize(proSentence);
		String[] tokens = tool.token(proSentence);
		
		String[] tagsOne = tool.tag(tokenList);
		String[] tagsTwo = tool.tag(proSentence);
		String[] chunks = tool.chunk(tokens, tagsOne);
		String[] chunkAsWords = tool.chunkAsWords(tokens, tagsTwo);
		String[] stems = tool.stem(tokens, tagsOne);
		List<TaggedWord> taggedWords = tool.taggedWord(proSentence);
		List<TypedDependency> ccTds = tool.typedDependenciesCCprocessed(taggedWords);
		List<TypedDependency> tds = tool.typedDependencies(taggedWords);
		
		System.out.println ("org sentence : " + sentence);
		System.out.println ("pro sentence : " + proSentence);
		System.out.println ("tokenList : " + StringUtils.join(tokenList, ", "));
		System.out.println ("tokens : " + StringUtils.join(tokens, ", "));
		System.out.println ("tagsOne : " + StringUtils.join(tagsOne, ", "));
		System.out.println ("tagsTwo : " + StringUtils.join(tagsTwo, ", "));
		System.out.println ("chunks : " + StringUtils.join(chunks, ", "));
		System.out.println ("chunkAsWords : " + StringUtils.join(chunkAsWords, ", "));
		System.out.println ("stems : " + StringUtils.join(stems, ", "));
		System.out.println ("taggedWords : " + StringUtils.join(taggedWords, ", "));
		System.out.println ("ccTds : " + StringUtils.join(ccTds, ", "));
		System.out.println ("tds : " + StringUtils.join(tds, ", "));
	}
}
