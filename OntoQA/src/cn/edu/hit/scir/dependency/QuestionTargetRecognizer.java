/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.scir.dependency;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.edu.hit.ir.nlp.EnglishNlpTool;
import cn.edu.hit.ir.nlp.NlpTool;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.TypedDependency;




/**
 * recognize the query target
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014��3��5��
 */
public class QuestionTargetRecognizer {
	private static Logger logger = Logger.getLogger(QuestionTargetRecognizer.class);
	private static Question questionInstance = Question.getInstance();
	private static QuestionTargetRecognizer instance;
	private static StanfordTagger tagger = StanfordTagger.getInstance();
	
	/**
	 * get the instance of QuestionTargetRecognizer
	 * 
	 * @return the instance
	 */
	public static QuestionTargetRecognizer getInstance () {
		if (instance == null ) 
			return new QuestionTargetRecognizer ();
		return instance;
	}
	
	private QuestionTargetRecognizer (){}
	
	
	/**
	 *	 recognize the question target 
	 *
	 * @param  the question 
	 * @return String , target
	 */
	public String recognizeQuestionTarget (String question ) {
		// default parameter
		List<Integer> param = Arrays.asList(1, 1, 3, 1, 1, 1, 3, 2, 2, 1, 1, 2);
		return recognizeQuestionTarget (question, param);
	}
	
	
	public String recognizeQuestionTarget(List<TaggedWord> taggedWds, Map<Integer, Integer> targetsCntMap, List<TypedDependency> dependency,  List<Integer> params) {
		
	
		// reln<gov, dep>
		for (TypedDependency td : dependency ) {
		
			String reln = td.reln().toString();

			if (reln.equals("root"))
				continue;

			Integer govIndex = td.gov().index();
			Integer depIndex = td.dep().index();
			String govTag  = taggedWds.get(govIndex - 1).tag();
			String depTag  = taggedWds.get(depIndex - 1).tag();

			// templates of "amod" , gov is target
			//{1, 1, 3, 1, 1, 1, 3,2,2,1,1,2}
			if (targetsCntMap.containsKey(govIndex)) {
				if 		( reln.equals("amod") && govTag.startsWith("NN") && depTag.startsWith("JJ")) {
					targetsCntMap.put(govIndex, targetsCntMap.get(govIndex) + params.get(0));//1
				}
				else if ( reln.equals("cop")  && govTag.startsWith("NN") && depTag.startsWith("VB")) {	
					targetsCntMap.put(govIndex, targetsCntMap.get(govIndex) + params.get(1)); //1
				}
				else if ( reln.equals("det")  && govTag.startsWith("NN") && depTag.equals("WDT")) {	
					targetsCntMap.put(govIndex, targetsCntMap.get(govIndex) + params.get(2)); // 3
				}
				else if ( reln.equals("det")  && govTag.startsWith("NN") && depTag.equals("DT")) {
					targetsCntMap.put(govIndex, targetsCntMap.get(govIndex) + params.get(3)); // 1
				}
				else if (reln.equals("vmod")  && govTag.startsWith("NN") && depTag.startsWith("VB")) {
					targetsCntMap.put(govIndex, targetsCntMap.get(govIndex) + params.get(4)); // 1
				}
				else if (reln.equals("rcmod")  && govTag.startsWith("NN") && depTag.startsWith("VB")) {
					targetsCntMap.put(govIndex, targetsCntMap.get(govIndex) + params.get(5)); // 1
				}
			}
			else if (targetsCntMap.containsKey(depIndex)) {
				if 		(reln.equals("nsubj")  && govTag.equals("WP") && depTag.startsWith("NN")) {
					targetsCntMap.put(depIndex, targetsCntMap.get(depIndex) + params.get(6)); //3
				}
				else if (reln.equals("nsubj")  && govTag.startsWith("VB") && depTag.startsWith("NN")) {
					targetsCntMap.put(depIndex, targetsCntMap.get(depIndex) + params.get(7)); // 2
				}
				else if (reln.equals("dep")  && govTag.startsWith("VB") && depTag.startsWith("NN")) {
					targetsCntMap.put(depIndex, targetsCntMap.get(depIndex) + params.get(8)); //2
				}
				else if (reln.equals("dobj")  && govTag.startsWith("VB") && depTag.startsWith("NN")) {
					targetsCntMap.put(depIndex, targetsCntMap.get(depIndex) + params.get(9)); //1
				}
				else if (reln.equals("pobj")  && govTag.equals("RP") && depTag.startsWith("NN")) {
					targetsCntMap.put(depIndex, targetsCntMap.get(depIndex) + params.get(10)); //1
				}
				else if (reln.equals("pobj")  && govTag.equals("IN") && depTag.startsWith("NN")) {
					targetsCntMap.put(depIndex, targetsCntMap.get(depIndex) + params.get(11)); //2
				}
			}
		}
		
//		logger.info("Conut Map : ");
//		for (Integer key : targetsCntMap.keySet()) {
//			logger.info("word : " + taggedWds.get(key - 1).word() + "\tkey : " + key + "\tval : " + targetsCntMap.get(key));
//		}
		
		
//		Integer maxVal = -1;
//		Integer keyIdx = taggedWds.size() + 1;
//		for (Integer key : targetsCntMap.keySet()) {
//			if (maxVal < targetsCntMap.get(key)) { 
//				maxVal = targetsCntMap.get(key);
//				keyIdx = key; 
//			}
//			else if (maxVal == targetsCntMap.get(key)) {
//				if (keyIdx > key)
//					keyIdx = key;
//			}
//		}
		
		List<Map.Entry<Integer, Integer>> list =
		            new LinkedList<Map.Entry<Integer, Integer>>( targetsCntMap.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<Integer, Integer>>()
        {
            public int compare( Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2 )
            {
            	if (o1.getValue().equals(o2.getValue()))
            		return (o1.getKey() - o2.getKey());
                return -( o1.getValue()).compareTo( o2.getValue() );
            }
        } );
//		logger.info("after sort:");
//        for (Map.Entry<Integer, Integer> en : list) {
//	    	logger.info(en.getKey() + "\t" + en.getValue());
//	    }
		return (list == null || list.isEmpty()? null : taggedWds.get(list.get(0).getKey()-1).word() + "-" + list.get(0).getKey().toString());
		//return ((maxVal == -1 || keyIdx > taggedWds.size() || keyIdx < 1)? null : taggedWds.get(keyIdx-1).word() + "-" + keyIdx.toString());
	}
	
	/**
	 *
	 *
	 * @param quesiton , params 
	 * @return 
	 */
	public String recognizeQuestionTarget (String question, List<Integer> params) {
		// processed the question
		questionInstance.initialize(question);
		
		//List<TaggedWord> taggedWds = questionInstance.getTaggedWord ();
		List<TaggedWord> taggedWds = tagger.taggerSentence(questionInstance.getProcessedQuestion());
		
		
		// this map is using for store the voting answer for dependency relation templates
		Map<Integer, Integer> targetsCntMap = new HashMap <Integer, Integer>();
		
		
		// map NN* --> map
		int idx = 1;
		for (  TaggedWord tw : taggedWds) {
			
			if (tw.tag().startsWith("NN")) {
				if (targetsCntMap.containsKey(idx)) {
					targetsCntMap.put(idx, targetsCntMap.get(idx));
				}
				else {
					targetsCntMap.put(idx, 0);
				}
			}
			++idx;
		}
		
		//List<TypedDependency> dependency = questionInstance.getDependency();
		List<TypedDependency> dependency = questionInstance.getDependencyCCprocessed(taggedWds);
		
		
		// reln<gov, dep>
		for (TypedDependency td : dependency ) {
		
			String reln = td.reln().toString();
			//logger.info("relation : " + td.toString());
			if (reln.equals("root"))
				continue;
			//logger.info("relation : " + td.toString());
			Integer govIndex = td.gov().index();
			Integer depIndex = td.dep().index();
			String govTag  = taggedWds.get(govIndex - 1).tag();
			String depTag  = taggedWds.get(depIndex - 1).tag();
			// templates of "amod" , gov is target
			//{1, 1, 3, 1, 1, 1, 3, 2, 2, 1, 1, 2}
			if (targetsCntMap.containsKey(govIndex)) {
				if 		( reln.equals("amod") && govTag.startsWith("NN") && depTag.startsWith("JJ")) {
					targetsCntMap.put(govIndex, targetsCntMap.get(govIndex) + params.get(0));//1
				}
				else if ( reln.equals("cop")  && govTag.startsWith("NN") && depTag.startsWith("VB")) {	
					targetsCntMap.put(govIndex, targetsCntMap.get(govIndex) + params.get(1)); //1
				}
				else if ( reln.equals("det")  && govTag.startsWith("NN") && depTag.equals("WDT")) {	
					targetsCntMap.put(govIndex, targetsCntMap.get(govIndex) + params.get(2)); // 3
				}
				else if ( reln.equals("det")  && govTag.startsWith("NN") && depTag.equals("DT")) {
					targetsCntMap.put(govIndex, targetsCntMap.get(govIndex) + params.get(3)); // 1
				}
				else if (reln.equals("vmod")  && govTag.startsWith("NN") && depTag.startsWith("VB")) {
					targetsCntMap.put(govIndex, targetsCntMap.get(govIndex) + params.get(4)); // 1
				}
				else if (reln.equals("rcmod")  && govTag.startsWith("NN") && depTag.startsWith("VB")) {
					targetsCntMap.put(govIndex, targetsCntMap.get(govIndex) + params.get(5)); // 1
				}
			}
			else if (targetsCntMap.containsKey(depIndex)) {
				if 		(reln.equals("nsubj")  && govTag.equals("WP") && depTag.startsWith("NN")) {
					targetsCntMap.put(depIndex, targetsCntMap.get(depIndex) + params.get(6)); //3
				}
				else if (reln.equals("nsubj")  && govTag.startsWith("VB") && depTag.startsWith("NN")) {
					targetsCntMap.put(depIndex, targetsCntMap.get(depIndex) + params.get(7)); // 2
				}
				else if (reln.equals("dep")  && govTag.startsWith("VB") && depTag.startsWith("NN")) {
					targetsCntMap.put(depIndex, targetsCntMap.get(depIndex) + params.get(8)); //2
				}
				else if (reln.equals("dobj")  && govTag.startsWith("VB") && depTag.startsWith("NN")) {
					targetsCntMap.put(depIndex, targetsCntMap.get(depIndex) + params.get(9)); //1
				}
				else if (reln.equals("pobj")  && govTag.equals("RP") && depTag.startsWith("NN")) {
					targetsCntMap.put(depIndex, targetsCntMap.get(depIndex) + params.get(10)); //1
				}
				else if (reln.equals("pobj")  && govTag.equals("IN") && depTag.startsWith("NN")) {
					targetsCntMap.put(depIndex, targetsCntMap.get(depIndex) + params.get(11)); //2
				}
			}
		}
		
		logger.info("Conut Map : ");
		for (Integer key : targetsCntMap.keySet()) {
			logger.info("word : " + taggedWds.get(key - 1).word() + "\tkey : " + key + "\tval : " + targetsCntMap.get(key));
		}
		
		
		List<Map.Entry<Integer, Integer>> list =
		            new LinkedList<Map.Entry<Integer, Integer>>( targetsCntMap.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<Integer, Integer>>()
        {
            public int compare( Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2 )
            {
            	if (o1.getValue().equals(o2.getValue()))
            		return (o1.getKey() - o2.getKey());
                return -( o1.getValue()).compareTo( o2.getValue() );
            }
        } );
//		logger.info("after sort:");
//        for (Map.Entry<Integer, Integer> en : list) {
//	    	logger.info(en.getKey() + "\t" + en.getValue());
//	    }
		return (list == null || list.isEmpty()? null : taggedWds.get(list.get(0).getKey()-1).word() + "-" + list.get(0).getKey().toString());
	}
	
	/**
	 * @param inFileName
	 * @param outFileName
	 */
	public static void recognizeDocument (String inFileName, String outFileName) {
		QuestionTargetRecognizer qtRecognizer = QuestionTargetRecognizer.getInstance();
		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(new File(
					inFileName)));
			BufferedWriter writer = new BufferedWriter( new FileWriter(outFileName));
			try {
				String line = null; // not declared within while loop
				/*
				 * readLine is a bit quirky : it returns the content of a line
				 * MINUS the newline. it returns null only for the END of the
				 * stream. it returns an empty String if two newlines appear in
				 * a row.
				 */
				while ((line = input.readLine()) != null) {
					if (!line.trim().isEmpty()) {
						//String target = qtRecognizer.recognizeQuestionTarget(line) ;
						//writer.write(line + "\t" + target + "\n");
					}
				}
			} finally {
				if (input != null )
					input.close();
				if (writer != null )
					writer.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * @param stdFileName
	 * @param rigthFileName
	 * @param wrongFileName
	 */
	public static void evaluateRecognizer (String stdFileName,  String rigthFileName, String wrongFileName, String statisticFile) {
		QuestionTargetRecognizer qtRecognizer = QuestionTargetRecognizer.getInstance();
		try {
			BufferedReader stdReader = null;
			BufferedWriter rightWriter = new BufferedWriter ( new FileWriter(rigthFileName));
			BufferedWriter wrongWriter = new BufferedWriter ( new FileWriter(wrongFileName));
			BufferedWriter statisticWriter = new BufferedWriter (new FileWriter (statisticFile));
			Integer totalCnt = 0;
			Integer rightCnt = 0;
			List<Integer> paramIn = new ArrayList<Integer>();	
			List<Integer> params = new ArrayList<Integer>();	//Arrays.asList(2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0);
		
			final Long BASE = 3L;
			final Long BITS = 12L; 
			for (int i = 0; i < BITS; ++i) {
				params.add(0);
				paramIn.add(0);
			}
			//precision : 0.8174924121156685	params : [3, 3, 3, 3, 3, 3, 3, 2, 1, 1, 1, 1]
			
			Double maxPrecision = 0.0;
			List<Integer> maxParams = null;
			
			
			try {
				
				stdReader = new BufferedReader(new FileReader(new File(stdFileName)));
				
				String line = null; // not declared within while loop
				List<String> queries = new ArrayList<String> ();
				List<String> targets = new ArrayList<String> ();
				
				//(List<TaggedWord> taggedWds, Map<Integer, Integer> targetsCntMap, List<TypedDependency> dependency,  List<Integer> params)
				
				List<List<TaggedWord>> taggedWdsList = new ArrayList<List<TaggedWord>>();
				List<Map<Integer, Integer>> targetsCntMapList = new ArrayList<Map<Integer, Integer>>();
				List<List<TypedDependency>> dependencyList = new ArrayList<List<TypedDependency>>();
				
				while ((line = stdReader.readLine()) != null) {
					line = line.replaceAll("\\r\\n|\\r|\\n", "");
					//logger.info("line : " + line);
					if (!line.trim().isEmpty()) {
						String [] tmp = line.split("\\t");
						if (tmp.length == 2 ) {
							queries.add (tmp[0].trim());
							targets.add (tmp[1].trim());
							questionInstance.initialize(tmp[0].trim());
							
							List<TaggedWord> taggedWds = tagger.taggerSentence(questionInstance.getProcessedQuestion());
							taggedWdsList.add(taggedWds);
							
							Map<Integer, Integer> targetsCntMap = new HashMap <Integer, Integer>();
							// map NN* --> map
							int idx = 1;
							for (  TaggedWord tw : taggedWds) {
//								logger.info("tw"  + tw.toString());
								if (tw.tag().startsWith("NN")) {
									if (targetsCntMap.containsKey(idx)) {
										targetsCntMap.put(idx, targetsCntMap.get(idx));
									}
									else {
										targetsCntMap.put(idx, 0);
									}
								}
								++idx;
							}
							targetsCntMapList.add(targetsCntMap);
							
							List<TypedDependency> dependency = new ArrayList<TypedDependency>();
							for (TypedDependency td :  questionInstance.getDependencyCCprocessed(taggedWds)){
								dependency.add(td);
							}
						
							dependencyList.add(dependency);
						}
					}
				} // while
				
				//logger.info("dp list size : " + dependencyList.size());
				if (stdReader != null )
					stdReader.close();
				
				for (Long i = 0L, rounds = pow (BASE, BITS); i < rounds; ++i) { //531441
					params = getNextParam (params,  Integer.parseInt(BASE.toString()));
					
//					for (int k = 0; k < params.size(); ++k ) {
//						paramIn.set(k, params.get(k) + 1);
//					}
				//	logger.info("paramIn : " + paramIn.toString());
					rightCnt = 0;
					for (int idx = 0, qSize = targets.size(); idx < qSize; ++idx ) {
						//logger.info("dependency size : " + dependencyList.get(idx).size());
						//String target = qtRecognizer.recognizeQuestionTarget(taggedWdsList.get(idx), targetsCntMapList.get(idx), dependencyList.get(idx), params);
						//logger.info(target + "\t" + targets.get(idx));
						String target = qtRecognizer.recognizeQuestionTarget(queries.get(idx));
					
						if (target != null && targets.get(idx).equals(target.trim())) {
							++rightCnt;
							//rightWriter.write(line + "\n");
						}
						//else {
							
							/*wrongWriter.write(line + "\t" + target + "\n");
							String tdwds = "";
							for (TaggedWord tw : tagger.taggerSentence(questionInstance.getProcessedQuestion())) {
								tdwds += tw.toString() + " ";
							}
							tdwds = tdwds.trim () + "\n";
							for (TypedDependency td :  questionInstance.getDependency(tagger.taggerSentence(questionInstance.getProcessedQuestion()))) {
								tdwds += td.toString() + "\n";
							}
							wrongWriter.write(tdwds + "\n");
							*/
					//	}
						//++totalCnt;
					
					}
					
					
				
					totalCnt = targets.size();
					//logger.info("right cnt : " + rightCnt );
					//logger.info("totalCnt : " + totalCnt);
					
					
					Double precision = (rightCnt*1.0/totalCnt);
					if (maxPrecision < precision) {
						maxPrecision = precision;
						maxParams = paramIn;
						logger.info("precision : " + precision + "\tparams : " + paramIn.toString());
						statisticWriter.write ("params: " + paramIn.toString() + "\n");
						statisticWriter.write ("<" + rightCnt.toString() + ",\t" + (totalCnt - rightCnt) + ",\t" + totalCnt + ",\t" + precision + ">\n\n" );
					}
					//statisticWriter.write ("Wrong Number    : " + (totalCnt - rightCnt) + "\n");		
					//statisticWriter.write ("Target precision : " + precision + "\n");
					//statisticWriter.write("--------------------------------------------\n");
					break;
					
				}
			} finally {
				if (stdReader != null )
					stdReader.close();
				if (rightWriter != null )
					rightWriter.close();
				if (wrongWriter != null )
					wrongWriter.close();
				if (statisticWriter != null )
					statisticWriter.close();
			}
			
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testOpenNlp (String sentence ) {
		NlpTool nlpt = EnglishNlpTool.getInstance();
		String [] tags = nlpt.tag(sentence);
		logger.info("nlptool tags : ");
		logger.info("question : " + sentence);
		for (String t : tags) {
			logger.info(t);
		}
	}
	
	public static  List<Integer> getNextParam (List<Integer> param, Integer base) {
		if (param == null || base < 1) {
			logger.error("parameter is wrong");
			return null;
		}
		int curIndex = 0;
		Integer add = 1;
		Integer t = 0;
		while (curIndex < param.size()) {
			t = param.get(curIndex) + add;
			//logger.info("t : " + t);
			if ( t >= base ) {
				param.set(curIndex, t % base);
				add = t / base;
				if (add == 0)
					break;
				++curIndex;
				
			}
			else {
				param.set(curIndex, t);
				break;
			}
		}
		return param;
	} 
	
	public static Long pow (Long n, Long m) {
		if (n == 0L)
			return 0L;
		if (m == 0L )
			return 1L;
		if (m == 1)
			return n;
			
		if (m % 2 == 1) {
			return n*pow(n, m / 2)* pow(n, m/2);
		}
		else {
			return pow(n, m/2)*pow(n, m/2);
		}
	}
	
	public static void main (String [] args) {
		
		//qtRecognizer.recognizeQuestionTarget("what are the major cities in ohio ?");
		String filePath = "data/geo880.txt";
		
		Boolean testFlag = true;
		if (testFlag) {
			String stdFilePath = "data/output/geo880_target_recognize.std"; // Manual annotation
			String posFilePath = "data/output/geo880_target_recognize.pos"; // programming annotation
			String outRightFile = "data/output/geo880_target_recognize_test_tagger.right";
			String outWrongFile = "data/output/geo880_target_recognize_test_tagger.wrong";
			String statisticFile = "data/output/target_statistic_test_tagger.txt";
	
			//recognizeDocument(filePath, posFilePath);
			
			evaluateRecognizer (stdFilePath, outRightFile, outWrongFile, statisticFile);
		}
		else {
			String qt = "What states border texas?";
			QuestionTargetRecognizer qtRecognizer = QuestionTargetRecognizer.getInstance();
			logger.info("Question is : " + qt);
			logger.info("Target : " + qtRecognizer.recognizeQuestionTarget(qt));
//		
//			qtRecognizer.testOpenNlp(qt);
			
//			List<Integer> params = new ArrayList<Integer>();
//			for (int i = 0; i < 3; ++i)
//				params.add(0);
//			
//			for (int i = 0; i < 29 ; ++i) {
//				
//				logger.info(params.toString());
//				
//				params = getNextParam(params, 3);
//				
//			}
			for (Long i = 1L; i < 13; ++i)
				logger.info(pow (2L, i));
			
		}
		
		
		
		
		logger.info("Finished");
	}
	
}
