package cn.edu.hit.scir.dependency;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.hit.ir.nlp.OpenNLP;
import edu.stanford.nlp.ling.TaggedWord;

/**
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月14日 
 */
public class TargetCompletion {
	
	private static Logger logger = Logger.getLogger(TargetCompletion.class);
	
	//private static NlpTool nlptool = EnglishNlpTool.getInstance();
	public static OpenNLP openNLP = OpenNLP.getInstance();
	
	private static TargetCompletion instance = null; 
	
	private static QuestionTargetRecognizer qtRecognizer = QuestionTargetRecognizer.getInstance();
	
	private static StanfordTagger tagger = StanfordTagger.getInstance();
	
	private static Question questionInstance = Question.getInstance();
	
	public  static TargetCompletion getInstance () {
		if (instance == null )
			return new TargetCompletion () ;
		return instance;
	}
	
	private TargetCompletion () {
		if (false == openNLP.createChunker())
			logger.info("create chuncker error!");
	}
	
	
	public String completeTarget (String question) {
		if (question == null ) 
			return null;
		
		questionInstance.initialize(question);
		List<TaggedWord> taggedWords =  tagger.taggerSentence(questionInstance.getProcessedQuestion());
		
		String [] words = new String[taggedWords.size()];
		String [] tags  = new String[taggedWords.size()];
		
		int idx = 0;
		for (TaggedWord tw : taggedWords ) {
			words[idx] = tw.word() + "-" + (idx + 1);
			tags[idx++]  = tw.tag();
		}
		
		String [] chunkers =  openNLP.chunkAsWords(words, tags);
		
		String orgTarget = qtRecognizer.recognizeQuestionTarget(question);
		
		return locateTarget (chunkers, orgTarget);
	} 
	
	
	/**
	 * locate the target in the chunker
	 *
	 * @param  chunkers, orgTarget : single word
	 * @return String , the complete target
	 */
	public String locateTarget (String [] chunkers, String orgTarget) {
		
		if (chunkers == null || orgTarget == null )
			return null;
		
		String [] tmp = orgTarget.trim().split("-");
		logger.info("chunker num : " + chunkers.length);
		String target = null;
		Integer targetPos = -1;
		if (tmp.length == 2) {
			target = tmp[0].trim();
			targetPos = Integer.parseInt(tmp[1].trim ());
			for (String ck : chunkers ) {
				logger.info("ck : " + ck);
				String [] subBlock  = ck.split("\\s+");
				boolean isIn = false;
				for (String wd : subBlock ) {
					
					Integer wdPos = Integer.parseInt(wd.substring(wd.indexOf("-") + 1, wd.length()).trim());
					if (wdPos.equals(targetPos) ) {
						isIn = true;
						break;
					}
				}
				if (isIn) {
					target = ck.replaceAll("-|\\d+", "").replaceAll("\\s+"," ");
					break;
				}
			}
		}
		logger.info("Completion Target : "  + target);
		return target == null ? orgTarget.replaceAll("-|\\d+", "").replaceAll("\\s+"," ") : target;
	} 
	
	public void completeTargetFiles ( String inFileName, String outFileName ) {
		if (inFileName == null || outFileName == null ) {
			logger.error("parameter is null!");
			return ;
		}
		
		try {
			BufferedReader input = new BufferedReader (new FileReader (new File (inFileName)));
			BufferedWriter output = new BufferedWriter (new FileWriter (new File(outFileName )));
			try {
				String line = null;
				while ((line = input.readLine()) != null ) {
					String target = completeTarget (line.trim());
					output.write(line + "\t" + target + "\n");
				}
				
			}
			finally {
				if (input != null ) 
					input.close();
				if (output != null )
					output.close();
			}
			
		}catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}catch (IOException ex ) {
			ex.printStackTrace();
		}
	}
	
	public static void main (String [] args ) {
		
		
		//String qt = "how high is the highest point in montana ?";
		
		TargetCompletion tc = TargetCompletion.getInstance();
		
		final String IN_FILE_NAME = "./data/geo880.txt";
		final String OUT_FILE_NAME = "./data/output/geo880.completeTarget";
		
		tc.completeTargetFiles(IN_FILE_NAME, OUT_FILE_NAME);
		
		//logger.info("Complete Target : " + tc.completeTarget(qt));
		
		logger.info("Finished!");
	}
	
	
	
	
	
}
