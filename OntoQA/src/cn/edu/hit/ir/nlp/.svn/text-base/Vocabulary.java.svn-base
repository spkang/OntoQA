/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.nlp;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;

import cn.edu.hit.ir.util.ConfigUtil;
import cn.edu.hit.ir.util.ObjectToSet;

/**
 * This class supports some operators about vocabulary.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-17
 */

public class Vocabulary {
	
	public static String SEPARATOR = ",";

	private static Vocabulary instance;
	
	private Configuration config;
	
	private SnowballStemmer snowballStemmer;
	
	private Set<String> smallWordSet;
	
	private ObjectToSet<String, WeightedWord> abj2nounMap;
	
	public static Vocabulary getInstance() {
		if (instance == null) {
			instance = new Vocabulary();
		}
		return instance;
	}
	
	private Vocabulary() {
		initConfig();
		initResources();
	}
	
	private void initConfig() {
		try {
			config = new PropertiesConfiguration(ConfigUtil.getPath(getClass()));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private void initResources() {
		snowballStemmer = SnowballStemmer.getInstance();
		
		initSmallWordSet();
		initAbj2NounMap();
	}
	
	private void initSmallWordSet() {
		smallWordSet = new HashSet<String>();
		
		String pathname = config.getString("data.small_words_file");
		try {
			List<String> smallWords = FileUtils.readLines(new File(pathname));
			smallWordSet.addAll(smallWords);
			for (String smallWord : smallWords) {
				String stem = snowballStemmer.stem(smallWord);
				smallWordSet.add(stem);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initAbj2NounMap() {
		abj2nounMap = new ObjectToSet<String, WeightedWord>();
		
		String pathname = config.getString("data.abj2noun_file");
		try {
			List<String> lines = FileUtils.readLines(new File(pathname));
			for (String line : lines) {
				String[] array = line.split(SEPARATOR);
				if (array.length != 3) continue;
				String abj = array[0];
				String noun = array[1];
				Double similarity = Double.valueOf(array[2]);
				WeightedWord ww = new WeightedWord(noun, similarity);
				abj2nounMap.addMember(abj, ww);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Determines whether a word is a "small" word.
	 * <p>
	 * There are some sample "small" words: small, little, short.
	 *
	 * @param word the word
	 * @return <code>true</code> if the word is a "small" word
	 */
	public boolean isSmallWord(String word) {
		String stem = snowballStemmer.stem(word);
		return smallWordSet.contains(stem);
	}
	
	/**
	 * Determines whether a word is a "big" word.
	 * <p>
	 * There are some sample "big" words: big, long.
	 *
	 * @param word the word
	 * @return <code>true</code> if the word is a "big" word
	 */
	public boolean isBigWord(String word) {
		return !isSmallWord(word);
	}
	
	public Set<WeightedWord> getNouns(String word) {
		return abj2nounMap.get(word);
	}
}
