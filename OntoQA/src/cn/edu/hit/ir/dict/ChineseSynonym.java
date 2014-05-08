/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.ir.dict;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import cn.edu.hit.ir.util.ConfigUtil;

/**
 * 中文的同义词扩展
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月8日 
 */
public class ChineseSynonym extends Synonym {
	public static final String DATA_FILES = "data.files";
	
	private static ChineseSynonym instance;
	
	private Configuration config;
	
	public static ChineseSynonym getInstance() {
		if (instance == null) {
			instance = new ChineseSynonym();
		}
		return instance;
	}

	private ChineseSynonym() {
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
	}
	
	private void loadFiles() {
		String[] files = config.getStringArray(DATA_FILES);
    	if (files != null) {
    		for (int i = 0; i < files.length; i++) {
				load(files[i]);
			}
    	}
	}
}
