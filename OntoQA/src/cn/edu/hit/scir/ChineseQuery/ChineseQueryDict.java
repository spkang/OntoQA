/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.ChineseQuery;

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

/**
 *  疑问词典
 * 
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年5月13日 
 */
public class ChineseQueryDict {
	private static ChineseQueryDict instance;
	
	private final String DICT_PATH = "chinese.dict.path";
	
	private Configuration config;
	
	private Set<String> cnQueryDict = null;
	
	
	private ChineseQueryDict () {
		this.cnQueryDict = new HashSet<String> ();
		initConfig ();
		loadDict();
	}
	
	public static ChineseQueryDict getInstance () {
		if ( instance == null )
			return new ChineseQueryDict ();
		return instance;
	}
	
	/**
	 *   导入初始化配置文件
	 *
	 * @param 
	 * @return void 
	 */
	private void initConfig () {
		try {
			config = new PropertiesConfiguration (ConfigUtil.getPath(ChineseQueryDict.class));
		}catch (ConfigurationException ex ) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 导入疑问词典
	 *
	 * @param 
	 * @return void  
	 */
	private void loadDict ()  {
		String [] dictPath = config.getStringArray(this.DICT_PATH);
		if (dictPath != null ) {
			try {
				for (String fileName : dictPath ) {
					List<String> lines = FileUtils.readLines(new File (fileName));
					for (String wd : lines ) {
						this.cnQueryDict.add(wd.trim());
					}
				}
			}catch (IOException ex ) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 返回词典
	 *
	 * @param 
	 * @return Set<String> 
	 */
	public Set<String> getQueryDict () {
		return this.cnQueryDict;
	}

	/**
	 * 判断一个词是否在词典中
	 *
	 * @param keyWord, the word which is going to judge
	 * @return boolean 
	 */
	public boolean isInDict (String keyWord ) {
		if (keyWord == null )
			return false;
		if (this.cnQueryDict.contains(keyWord))
			return true;
		return false;
	}

	
	/**
	 * 向词典中添加单词
	 *
	 * @param addWord, 待添加的单词
	 * @return boolean 
	 */
	public boolean addToDict (String addWord) {
		if (addWord == null )
			return false;
		if (this.cnQueryDict.contains(addWord))
			return true;
		else {
			return this.cnQueryDict.add(addWord);
		}
	}
	
	 /**
	 * 从词典删除特定的单词
	 *
	 * @param removeWord, the word going to remove
	 * @return boolean , is remove successfully
	 */
	public boolean removeFromDict (String removeWord) {
		if (removeWord == null )
			return false;
		if (this.cnQueryDict.contains(removeWord))
			return this.cnQueryDict.remove(removeWord);
		return false;
	}
	
	
	/**
	 * 判断一个给定的句子是否包含疑问词
	 *
	 * @param sentence, 待检测的词
	 * @return boolean 
	 */
	public boolean containsQueryWord (String sentence ) {
		if (sentence == null )
			return false;
		for (String key : this.cnQueryDict) {
			if (sentence.contains(key)) {
				return true;
			}
		}
		return false;
	} 
}
