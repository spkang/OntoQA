/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.experiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.edu.hit.ir.nlp.EnglishNlpTool;
import cn.edu.hit.ir.ontology.Ontology;
import cn.edu.hit.ir.questionanalysis.QuestionAnalyzer;
import cn.edu.hit.scir.dependency.StanfordEnglishNlpTool;
import cn.edu.hit.scir.ontologymatch.QueryAnalyzer;

/**
 * The evaluator which evaluate the performance of the whole OntoQA system.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-7
 */

public class Evaluator {

	public static final String corpusFilename = CorpusTool.DATA_DIR + "corpus.xml";
	//public static final String corpusFilename = CorpusTool.DATA_DIR + "small_corpus.xml";
	public static final String outputFilename = CorpusTool.DATA_DIR + "output_spkang.xml";
	public static final String wrongFilename = CorpusTool.DATA_DIR + "wrong_spkang.xml";
	public static final String sparqloutFilename = CorpusTool.DATA_DIR + "sparql_out_spkang.xml";
	
	private static Logger logger = Logger.getLogger(Evaluator.class);
	
	public static String formatDouble(double value, int min) {
		java.text.NumberFormat ft = java.text.NumberFormat.getPercentInstance(); // 鐧惧垎鏁�		
		ft.setMinimumFractionDigits(min);// 淇濈暀min浣嶅皬鏁�		
		String string = ft.format(value);
		return string;
	}
	
	public static void showResult(int total, int answered, int correct) {
		double accuracy =  total > 0 ? ((double)correct / total) : 0;
		double precision = answered > 0 ? ((double)correct / answered) : 0;
		double recall =  total > 0 ? ((double)answered / total) : 0;
		logger.info("total: " + total + ", answered: " + answered + ", correct: " + correct + 
				", accuracy: " + formatDouble(accuracy, 2) +
				", precision: " + formatDouble(precision, 2) +
				", recall: " + formatDouble(recall, 2));
	}
	
	public static boolean sameResult(Ontology ontology, String sparql1, String sparql2) {
		if (!Ontology.isLegalSparql(sparql1)
				|| !Ontology.isLegalSparql(sparql2)) {
			return false;
		}
		List<String> results1 = ontology.getResults(sparql1);
		List<String> results2 = ontology.getResults(sparql2);
		
		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		
		set1.addAll(results1);
		set2.addAll(results2);
		
		return set1.equals(set2);
	}
	
	public static void evaluate() {
		long begin = System.currentTimeMillis();
		
		//boolean testFlag = true;
		boolean testFlag = false;
		int numTestExamples = 93;
		
		int total = 0;
		int correct = 0;
		int answered = 0;
		List<String> failureIds = new ArrayList<String>();
		
//		QuestionAnalyzer analyzer = new QuestionAnalyzer(); // bin3
		QueryAnalyzer analyzer = new QueryAnalyzer (); // spakng
		Ontology ontology = Ontology.getInstance();
		
		List<Example> examples = CorpusTool.readCorpus(corpusFilename);
		List<Example> wrongExamples = new ArrayList<Example>();
		
//		EnglishNlpTool nlpTool = EnglishNlpTool.getInstance();
//		StanfordEnglishNlpTool tool = StanfordEnglishNlpTool.getInstance();
		for (Example example : examples) {
			if (testFlag) {
				if (total >= numTestExamples) break;
			}
			
			String id = example.getId();
			String query = example.getQuery();
			
//			String[] tags = tool.tag(query);
			
			//String[] tags = nlpTool.tag(query);
//			boolean flag= false;
//			for (int i = 0; i < tags.length && ! flag; ++i ) {
//				if (tags[i].toUpperCase().equals("JJS") || tags[i].toUpperCase().equals("RBS") )
//					flag  = true;
//			}
//			
//			if (flag) {
//				logger.info("query : " + query);
//				continue;
//			}
			
			String sparql = example.getSparql();
			
			if (!Ontology.isLegalSparql(sparql) 
					|| example.isTodoStatus()) {
				continue;
			}
			
			logger.info("\n#" + id);
			logger.info("@query: " + query);
			logger.info("\n@sparql: \n" + sparql);
			
			String sparqlOut = analyzer.getSparql(query);			
			example.setSparqlOut(sparqlOut);
			
			logger.info("\n@sparqlOut: \n" + sparqlOut);
			logger.info("----------------------------------------------------------------------------------------------------------------------------");
						
			++total;
			
			if (Ontology.isLegalSparql(sparqlOut)) {
				++answered;

				if (sameResult(ontology, sparql, sparqlOut)) {
					++correct;
					example.setStatus(CorpusTool.CORRECT_STATUS);
					
					logger.info("[Correct]");
				} else {
					example.setStatus(CorpusTool.WROONG_STATUS);
					wrongExamples.add(example);
					
					logger.info("[Wrong]");
					failureIds.add(id);
				}
			} else {
				logger.info("[No answer]");
				failureIds.add(id);
				
				example.setStatus(CorpusTool.NOANSWER_STATUS);
				wrongExamples.add(example);
			}
			
			logger.info("----------------------------------------------------------------------------------------------------------------------------");
			showResult(total, answered, correct);
		}
		
		logger.info("Failure Ids(" + failureIds.size() + "): ");
		logger.info(failureIds);
		logger.info("----------------------------------------------------------------------------------------------------------------------------");
		
		long end = System.currentTimeMillis();
		double cost = (end - begin) / 1000;
		logger.info("Cost " + cost + "s");
		
		CorpusTool.saveCorpus(outputFilename, examples);
		CorpusTool.saveCorpus(wrongFilename, wrongExamples);
	}
	
	public static void genSparqlOut() {
		QuestionAnalyzer analyzer = new QuestionAnalyzer();
		
		List<Example> examples = CorpusTool.readCorpus(corpusFilename);
		
		for (Example example : examples) {
			String id = example.getId();
			String query = example.getQuery();
			
			String sparqlOut = analyzer.getSparql(query);			
			example.setSparqlOut(sparqlOut);
			
			
			logger.info("----------------------------------------------------------------------------------------------------------------------------");
			logger.info("#" + id);
			logger.info("@query: " + query);
			logger.info("\n@sparqlOut: \n" + sparqlOut);
		}
		
		CorpusTool.saveCorpus(sparqloutFilename, examples);
	}
	
	public static void testLogger() {
		//System.out.println(Evaluator.class);
		logger.info(logger);
		logger.error("It's a error.");
	}
	
	/**
	 * Runs the evaluator.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		evaluate();
		//genSparqlOut();
		//testLogger();
	}
}
