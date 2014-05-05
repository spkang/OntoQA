/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.experiment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import cn.edu.hit.ir.nlp.EnglishNlpTool;
import cn.edu.hit.ir.nlp.NlpTool;
import cn.edu.hit.ir.questionanalysis.QuestionAnalyzer;

/**
 * A tool of corpus.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-3
 */

public class CorpusTool {
	
	public static final String DATA_DIR = "data/";
	
	public static final String EXAMPLES = "examples";
	public static final String EXAMPLE = "example";
	public static final String QUERY = "query";
	public static final String SPARQL = "sparql";
	public static final String ANSWER = "answer";
	public static final String SPARQL_OUT = "sparqlOut";
	
	public static final String POS = "pos";
	public static final String CHUNK = "chunk";
	public static final String PHRASE = "phrase";
	public static final String ENTITY = "entity";
	
	public static final String ID_ATTR = "id";
	public static final String STATUS_ATTR = "status";
	
	public static final String TODO_STATUS = "todo";
	public static final String CORRECT_STATUS = "corrrect";
	public static final String WROONG_STATUS = "wrong";
	public static final String NOANSWER_STATUS = "noanswer";
	
	public static void generateCorpus() {	
		try {
			final String EMPTY_SPARQL = "\nSELECT \nWHERE {\n\n}\n";
			
			final String inFilename = DATA_DIR + "geo880.txt";
			
			Document corpus = new Document("");
			Element examples = corpus.prependElement(EXAMPLES);
			
			List<String> queries = FileUtils.readLines(new File(inFilename));
			for (int i = 0; i < queries.size(); i++) {
				String query = queries.get(i);
				Element example = examples.appendElement(EXAMPLE);
				example.attr(ID_ATTR, Integer.toString(i));
				example.appendElement(QUERY).text(query);
				example.appendElement(SPARQL).text(EMPTY_SPARQL);
				example.appendElement(ANSWER);
				
				System.out.println(example);
			}
			
			String corpusFilename = DATA_DIR + "corpus2.xml";
			FileUtils.writeStringToFile(new File(corpusFilename), corpus.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Example> readCorpus(String filename) {
		System.out.println("@CorpusTool.readCorpus filename: " + filename);
		
		List<Example> exampleList = new ArrayList<Example>();
		
		try {
			Document corpus = Jsoup.parse(new File(filename), 
					"UTF-8", "http://ir.hit.edu.cn/");
			Elements examples = corpus.select(EXAMPLE);
			for (Element example : examples) {
				String id = example.attr(ID_ATTR);
				String query = example.select(QUERY).text();
				String sparql = example.select(SPARQL).text();
				String answer = example.select(ANSWER).text();
				
				Example ex = new Example(id, query, sparql, answer);
				exampleList.add(ex);
				
				String sparqlStatus = example.select(SPARQL).attr(STATUS_ATTR);
				if (sparqlStatus != null) {
					ex.setSparqlStatus(sparqlStatus);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return exampleList;
	}
	
	public static boolean saveCorpus(String filename, List<Example> exampleList) {
		System.out.println("@CorpusTool.saveCorpus filename: " + filename);
		
		try {
			
			Document corpus = new Document("");
			Element examples = corpus.prependElement(EXAMPLES);

			for (Example ex : exampleList) {
				String id = ex.getId();
				String query = ex.getQuery();
				String sparql = ex.getSparql();
				String answer = ex.getAnswer();
				String sparqlOut = ex.getSparqlOut();
				if (sparqlOut == null )
					sparqlOut = "";
				
				Element example = examples.appendElement(EXAMPLE);
				example.attr(ID_ATTR, id);
				example.appendElement(QUERY).text(query);
				example.appendElement(SPARQL).text(sparql);
				example.appendElement(ANSWER).text(answer);
				example.appendElement(SPARQL_OUT).text(sparqlOut);
				
				String status = ex.getStatus();
				if (status != null && status.length() > 0) {
					example.attr(STATUS_ATTR, status);
				}
				
				//System.out.println(example);
			}
			
			FileUtils.writeStringToFile(new File(filename), corpus.toString());

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static void readCorpus() {
		try {
			String corpusFilename = DATA_DIR + "corpus.xml";
			Document corpus = Jsoup.parse(new File(corpusFilename), 
					"UTF-8", "http://ir.hit.edu.cn/");
			Elements examples = corpus.select(EXAMPLE);
			for (Element example : examples) {
				String sparql = example.select(SPARQL).first().text();
				if (sparql.length() > 0) {
					System.out.println(sparql);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getTokTagString(String[] tokens, String[] tags) {
		if (tokens.length != tags.length) {
			String msg = "The length of tokens and tags must be equal.";
			throw new IllegalArgumentException(msg);
		}
		
		String[] tokTags = new String[tokens.length];
		for (int i = 0; i < tokTags.length; i++) {
			tokTags[i] = tokens[i] + "/" + tags[i];
		}
		return StringUtils.join(tokTags, ", ");
	}
	
	public static void nlpCorpus() {
		try {
			NlpTool nlpTool = EnglishNlpTool.getInstance();

			final String inFilename = DATA_DIR + "geo880.txt";

			Document corpus = new Document("");
			Element examples = corpus.prependElement(EXAMPLES);

			List<String> queries = FileUtils.readLines(new File(inFilename));
			for (int i = 0; i < queries.size(); i++) {
				String query = queries.get(i);
				String[] tokens = nlpTool.tokenize(query);
				String[] tags = nlpTool.tag(tokens);
				String[] chunks = nlpTool.chunk(tokens, tags);
				String[] phrases = nlpTool.chunkAsWords(tokens, tags);

				String tokTagString = getTokTagString(tokens, tags);
				String chunkString = StringUtils.join(chunks, ", ");
				String phraseString = StringUtils.join(phrases, ", ");

				Element example = examples.appendElement(EXAMPLE);
				example.attr(ID_ATTR, Integer.toString(i));
				example.appendElement(QUERY).text(query);
				example.appendElement(POS).text(tokTagString);
				example.appendElement(CHUNK).text(chunkString);
				example.appendElement(PHRASE).text(phraseString);

				System.out.println(example);
			}

			String corpusFilename = DATA_DIR + "corpus_nlp.xml";
			FileUtils.writeStringToFile(new File(corpusFilename),
					corpus.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void analyzeCorpus() {		
		try {
			QuestionAnalyzer analyzer = new QuestionAnalyzer();

			final String inFilename = DATA_DIR + "geo880.txt";

			Document corpus = new Document("");
			Element examples = corpus.prependElement(EXAMPLES);

			List<String> queries = FileUtils.readLines(new File(inFilename));
			for (int i = 0; i < queries.size(); i++) {
				String query = queries.get(i);
				Object res = analyzer.getMatchedEntitiesSentence(query);
				
				Element example = examples.appendElement(EXAMPLE);
				example.attr(ID_ATTR, Integer.toString(i));
				example.appendElement(QUERY).text(query);
				example.appendElement(ENTITY).text(res.toString());

				System.out.println(example);
			}

			String corpusFilename = DATA_DIR + "corpus_analysiss.xml";
			FileUtils.writeStringToFile(new File(corpusFilename),
					corpus.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void genAllSparql() {
		System.out.println("@genAllSparql");
		
		long begin = System.currentTimeMillis();
		
		final String corpusFilename = CorpusTool.DATA_DIR + "corpus.xml";
		final String outputFilename = CorpusTool.DATA_DIR + "sparql_all.xml";
		
		QuestionAnalyzer analyzer = new QuestionAnalyzer();
		
		List<Example> examples = CorpusTool.readCorpus(corpusFilename);
		
		for (Example example : examples) {
			System.out.println("#" + example.getId());
			String query = example.getQuery();
			String sparqlOut = analyzer.getSparql(query);			
			//example.setSparqlOut(sparqlOut);
			String sparql = example.getSparql();
			if (sparql.trim().length() < 24) {
				example.setSparql(sparqlOut);
			}
		}
		
		CorpusTool.saveCorpus(outputFilename, examples);
		
		long end = System.currentTimeMillis();
		double cost = (end - begin) / 1000;
		System.out.println("Cost " + cost + "s");
	}
	
	/**
	 * Runs corpus tool.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		//generateCorpus();
		//readCorpus();
		//nlpCorpus();
		//analyzeCorpus();
		genAllSparql();
	}

}
