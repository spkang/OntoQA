/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.dict;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import cn.edu.hit.ir.util.ConfigUtil;

/**
 * An English synonym tool.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-4
 */

public class EnglishSynonym extends Synonym {
	
	public static final String DATA_FILES = "data.files";
	
	public static final int MAX_LENGTH = 3;
	
	private static EnglishSynonym instance;
	
	private Configuration config;
	
	public static EnglishSynonym getInstance() {
		if (instance == null) {
			instance = new EnglishSynonym();
		}
		return instance;
	}

	private EnglishSynonym() {
		super();
		initConfig();
		initData();
	}
	
	private void initConfig() {
		try {
			config = new PropertiesConfiguration(ConfigUtil.getPath(getClass()));
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private void initData() {
		loadFiles();
		addOntologies();
	}
	
	private void loadFiles() {
		String[] files = config.getStringArray(DATA_FILES);
    	if (files != null) {
    		for (int i = 0; i < files.length; i++) {
				load(files[i]);
			}
    	}
	}
	
	private void addOntologies() {
		WordNetSynonymAdapter wordNetSynonym = new WordNetSynonymAdapter();
    	addOntology(wordNetSynonym);
	}
}
