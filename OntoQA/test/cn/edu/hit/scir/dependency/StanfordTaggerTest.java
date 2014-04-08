package cn.edu.hit.scir.dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class StanfordTaggerTest {

	private static StanfordTagger tagger = StanfordTagger.getInstance(); 
	private static Logger logger = Logger.getLogger(StanfordTaggerTest.class);
	private StanfordParser sdParser = new StanfordParser ();
	@Before 
	public void setUp () {
		logger.info("setUp");
	}
	
	@After
	public void tearDown () {
		logger.info("tearDown");
	}
	
	
	//@Test
	public void testTagger() {
		logger.info("@testTagger");
		final String inFileName = "./data/geo880.txt";
		final String outFileName = "./data/output/geo880_tagger_depenceyparser_analysis.txt";
		analysisFile(inFileName, outFileName);
	}
	
	public void analysisFile (String inFileName, String outFileName) {
		if (inFileName == null || outFileName == null  )
			return ;
		if (inFileName.isEmpty() || outFileName.isEmpty() )
			return ;

		final String SEPERATOR_LINE = "------------------------------------------------------";
		List<String> sentences;
		try {
			sentences = FileUtils.readLines(new File(inFileName), "utf-8");
			List<String> outLines = new ArrayList<String>();
			for (String st : sentences) {
				outLines.add(st);
				st = st.trim().replaceAll("(\\?|\\.|!)$", "");
				st = st.replaceAll(",", " ");
				st = st.replaceAll("\"", ""); 
				st = st.replaceAll("\\s+", " "); 
				outLines.add(st);
				
				List<TaggedWord> taggedWds = tagger.taggerSentence(st);
				String tws = "[";
				
				for (TaggedWord tw : taggedWds ) {
					if (tws == "[")
						tws += tw.toString();
					else 
						tws += ", " + tw.toString();
				}
				tws += "]";
				outLines.add(tws);
				
				List<TypedDependency> tdl = null;
				if (sdParser.getLexicalizedParser() != null ) {
					Tree parse = sdParser.getLexicalizedParser().apply(taggedWds);
					TreebankLanguagePack tlp = new PennTreebankLanguagePack();	
					GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
					GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
					//gs.typedDependencies()
					tdl = gs.typedDependenciesCCprocessed();
					for (TypedDependency td : tdl ) {
						outLines.add(td.toString());
						
					}
					outLines.add(SEPERATOR_LINE);
				}
			}
			FileUtils.writeLines(new File(outFileName), outLines);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testProcessSentence () {
		logger.info("@testPrcocessSentence");
		String sentence = "what is the lowest point of all states through which the colorado river runs through ?";
		processSentence(sentence);
	} 
	
	public void processSentence ( String sentence ) {
		if (sentence == null )
			return;
		sentence = sentence.trim().replaceAll("(\\?|\\.|!)$", "");
		sentence = sentence.replaceAll(",", " ");
		sentence = sentence.replaceAll("\"", ""); 
		sentence = sentence.replaceAll("\\s+", " ");
		
		logger.info("Sentence  :"  + sentence);
		
		List<TaggedWord> taggedWds = tagger.taggerSentence(sentence);
		
		List<TypedDependency> tdl = null;
		if (sdParser.getLexicalizedParser() != null ) {
			Tree parse = sdParser.getLexicalizedParser().apply(taggedWds);
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();	
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			Collection<TypedDependency> tds = gs.typedDependencies();
			
			logger.info("tds size : " + tds.size());
			for (TypedDependency td  : tds ) {
				logger.info("td : " + td.toString());
			} 
			
			logger.info("------------------------------------------------");
			tdl = gs.typedDependenciesCCprocessed();
			logger.info("tdl size : " + tdl.size());
			for (TypedDependency td : tdl ) {
				logger.info(td.toString());
			}
		}
	}
	
}
