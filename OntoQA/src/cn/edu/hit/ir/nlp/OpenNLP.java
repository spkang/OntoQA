/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.chunker.Chunker;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

/**
 * An interface to the
 * <a href="http://incubator.apache.org/opennlp/">OpenNLP</a> toolkit.
 * 
 * <p>It supports the following natural language processing tools:
 * <ul>
 * <li>Sentence detection</li>
 * <li>Tokenization/untokenization</li>
 * <li>Part of speech (POS) tagging</li>
 * <li>Chunking</li>
 * <li>Full parsing</li>
 * <li>Coreference resolution</li>
 * </ul>
 * </p>
 * 
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-15
 */

public class OpenNLP {
	public static final String DEFAUL_RES_DIR = "res/nlp/opennlp/";
	public static final String DEFAUL_SENT_MODEL = DEFAUL_RES_DIR + "en-sent.bin";
	public static final String DEFAUL_TOKEN_MODEL = DEFAUL_RES_DIR + "en-token.bin";
	public static final String DEFAUL_POS_MODEL = DEFAUL_RES_DIR + "en-pos-maxent.bin";
	public static final String DEFAUL_CHUNKER_MODEL = DEFAUL_RES_DIR + "en-chunker.bin";
	public static final String DEFAUL_PARSER_MODEL = DEFAUL_RES_DIR + "en-parser-chunking.bin";
	
	/** Sentence detector from the OpenNLP project. */
	private SentenceDetector sentenceDetector;
	/** Tokenizer from the OpenNLP project. */
	private Tokenizer tokenizer;
	/** Part of speech tagger from the OpenNLP project. */
	private POSTagger tagger;
	/** Chunker from the OpenNLP project. */
	private Chunker chunker;
	/** Full parser from the OpenNLP project. */
	private Parser parser;
	
	private static OpenNLP instance = null;
	
	public static OpenNLP getInstance() {
		if (instance == null) {
			instance = new OpenNLP();
		}
		return instance;
	}
    
    /**
	 * Creates a new instance of OpenNLP.
	 *
	 */
	private OpenNLP() {
		
	}
	
	/*private void loadModels() {
		chunkerModel  = new ChunkerModelLoader().load(new File(args[0]));
		chunker = new ChunkerME(chunkerModel, ChunkerME.DEFAULT_BEAM_SIZE,
		        new DefaultChunkerSequenceValidator());
	}*/
	
	/**
	 * Creates all the NLP tools.
	 *
	 * @return true, if all the NLP tools were created successfully
	 */
	public boolean createAll() {
		boolean result = false;
		result |= createSentenceDetector();
		result |= createTokenizer();
		result |= createPosTagger();
		result |= createChunker();
		result |= createParser();
		return result;
	}
	
	/**
	 * Creates the sentence detector from a model file.
	 * 
	 * @param model model file
	 * @return true, if the sentence detector was created successfully
	 */
	public boolean createSentenceDetector(String model) {
		boolean result = true;
		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream(model);
			SentenceModel sentenceModel = new SentenceModel(modelIn);
			sentenceDetector = new SentenceDetectorME(sentenceModel);
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}	
		return result;
	}
	
	/**
	 * Creates the sentence detector from the default sentence model file.
	 *
	 * @return true, if the default sentence detector was created successfully
	 */
	public boolean createSentenceDetector() {
		return createSentenceDetector(DEFAUL_SENT_MODEL);
	}
	
	/**
	 * Creates the tokenizer from a model file.
	 * 
	 * @param model model file
	 * @return true, if the tokenizer was created successfully
	 */
	public boolean createTokenizer(String model) {
		boolean result = true;
		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream(model);
			TokenizerModel tokenizerModel = new TokenizerModel(modelIn);
			tokenizer = new TokenizerME(tokenizerModel);
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}	
		return result;
	}
	
	/**
	 * Creates the tokenizer from the default token model file.
	 *
	 * @return true, if the default tokenizer was created successfully
	 */
	public boolean createTokenizer() {
		return createTokenizer(DEFAUL_TOKEN_MODEL);
	}
	
	/**
	 * Creates the part of speech tagger from a model file and a case sensitive
	 * tag dictionary.
	 * 
	 * @param model model file
	 * @return true, if the POS tagger was created successfully
	 */
	public boolean createPosTagger(String model) {
		boolean result = true;
		InputStream modelIn = null;
		try {
			// Deal with dependency
			if (tokenizer == null) {
				createTokenizer();
			}
			modelIn = new FileInputStream(model);
			POSModel posModel = new POSModel(modelIn);
			tagger = new POSTaggerME(posModel);
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}	
		return result;
	}
	
	/**
	 * Creates the POS tagger from the default POS model file.
	 *
	 * @return true, if the default POS tagger was created successfully
	 */
	public boolean createPosTagger() {
		if (tagger != null) {
			return true;
		} else {
			return createPosTagger(DEFAUL_POS_MODEL);
		}
	}
	
	/**
	 * Creates the chunker from a model file.
	 * 
	 * @param model model file
	 * @return true, if the chunker was created successfully
	 */
	public boolean createChunker(String model) {
		boolean result = true;
		InputStream modelIn = null;
		try {
			// Deal with dependency
			if (tagger == null) {
				createPosTagger();
			}
			
			modelIn = new FileInputStream(model);
			ChunkerModel chunkerModel = new ChunkerModel(modelIn);
			chunker = new ChunkerME(chunkerModel);
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}	
		return result;
	}
	
	/**
	 * Creates the chunker from the default chuncker model file.
	 *
	 * @return true, if the default chunker was created successfully
	 */
	public boolean createChunker() {
		if (chunker != null) {
			return true;
		} else {
			return createChunker(DEFAUL_CHUNKER_MODEL);
		}
	}
	
	/**
	 * Creates the parser from a directory containing models.
	 * 
	 * @param dir model directory
	 * @return true, if the parser was created successfully
	 */
	public boolean createParser(String model) {
		boolean result = true;
		InputStream modelIn = null;
		try {
			// Deal with dependency
			if (tagger == null) {
				createPosTagger();
			}
			
			modelIn = new FileInputStream(model);
			ParserModel parserModel = new ParserModel(modelIn);
			parser = ParserFactory.create(parserModel);
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}	
		return result;
	}
	
	/**
	 * Creates the parser from the default parser model file.
	 *
	 * @return true, if the default parser was created successfully
	 */
	public boolean createParser() {
		if (parser != null) {
			return true;
		} else {
			return createParser(DEFAUL_PARSER_MODEL);
		}
	}
	
	/**
	 * Splits a text into sentences.
	 * 
	 * @param text sequence of sentences
	 * @return array of sentences in the text or null, if the
	 * 		   sentence detector is not initialized
	 */
	public String[] sentDetect(String text) {
		if (sentenceDetector == null) {
			createSentenceDetector();
		}
		return (sentenceDetector != null)
			? sentenceDetector.sentDetect(text)
			: null;
	}
	
	/**
	 * A model-based tokenizer used to prepare a sentence for POS tagging.
	 * 
	 * @param text text to tokenize
	 * @return array of tokens or <code>null</code>, if the tokenizer is not
	 * 		   initialized
	 */
	public String[] tokenize(String text) {
		if (tokenizer == null) {
			createTokenizer();
		}
		return (tokenizer != null) ? tokenizer.tokenize(text) : null;
	}
	
	/**
	 * Assigns POS tags to an array of tokens that form a sentence.
	 * 
	 * @param tokens array of tokens to be annotated with POS tags
	 * @return array of POS tags or <code>null</code>, if the tagger is not
	 * 		   initialized
	 */
	public String[] tag(String[] tokens) {
		if (tagger == null) {
			createPosTagger();
		}
		return (tagger != null) ? tagger.tag(tokens) : null;
	}
	
	/**
	 * Assigns POS tags to a sentence of space-delimited tokens.
	 * 
	 * @param sentence sentence to be annotated with POS tags
	 * @return tagged sentence or <code>null</code>, if the tagger is not
	 * 		   initialized
	 */
	public String[] tag(String sentence) {
		String[] tokens = tokenize(sentence);
		return tag(tokens);
	}
	
	/**
	 * Assigns chunk tags to an array of tokens and POS tags.
	 * 
	 * @param tokens array of tokens
	 * @param tags array of corresponding POS tags
	 * @return array of chunk tags or <code>null</code>, if the chunker is not
	 * 		   initialized
	 */
	public String[] chunk(String[] tokens, String[] tags) {
		if (chunker == null) {
			createChunker();
		}
		return chunker.chunk(tokens, tags);
	}
	
	/**
	 * Generates chunk spans to an array of tokens and POS tags.
	 * 
	 * @param tokens array of tokens
	 * @param tags array of corresponding POS tags
	 * @return array of chunk spans or <code>null</code>, if the chunker is not
	 * 		   initialized
	 */
	public Span[] chunkAsSpans(String[] tokens, String[] tags) {
		if (chunker == null) {
			createChunker();
		}
		return chunker.chunkAsSpans(tokens, tags);
	}
	
	/**
	 * Generates chunk words to an array of tokens and POS tags.
	 * 
	 * @param tokens array of tokens
	 * @param tags array of corresponding POS tags
	 * @return array of chunk words or <code>null</code>, if the chunker is not
	 * 		   initialized
	 */
	public String[] chunkAsWords(String[] tokens, String[] tags) {
		Span[] spans = chunkAsSpans(tokens, tags);
		if (spans == null) {
			return null;
		}
		return Span.spansToStrings(spans, tokens);
	}
	
	/**
	 * Performs a full parsing on a sentence of space-delimited tokens.
	 * 
	 * @param sentence the sentence
	 * @return parse of the sentence or <code>null</code>, if the parser is not
	 * 		   initialized or the sentence is empty
	 */
	public Parse parse(String sentence) {
		if (parser == null) {
			createParser();
		}
		return (parser != null && sentence.length() > 0)
			// only get first parse (that is most likely to be correct)
			? ParserTool.parseLine(sentence, parser, 1)[0]
			: null;
	}
}
