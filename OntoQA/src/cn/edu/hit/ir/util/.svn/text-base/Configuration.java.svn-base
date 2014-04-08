/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A configuration utility.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-17
 */

public class Configuration {
	
	/**
	 * The default configuration file directory
	 */
	public static final String DEFAULT_CONFIG_DIR = "conf/";
	
	private static Map<String, Configuration> nameConfigMap;

	private Properties properties;

	static {
		nameConfigMap = new HashMap<String, Configuration>();
	}
	
	/**
	 * Creates a new instance of Configuration.
	 *
	 * @param configFilePath the configuration file
	 */
	public Configuration(String configFilePath) {
		properties = new Properties();
		load(configFilePath);
	}
	
	/**
	 * Loads the configuration file.
	 *
	 * @param configFilePath the configuration file
	 * @return true if loads successfully
	 */
	private boolean load(String configFilePath) {
		try {
            File file = new File(configFilePath);
            if(file.exists() && file.isFile()){
            	properties.load(new FileInputStream(file));
                return true;
            }
        } catch (Exception e) {
        	System.err.println("Caught exception while loading properties: "
					 + e.getMessage());
        }
        return false;
	}
	
	/**
	 * Returns the property with the specified key. The method returns
	 * the default value argument if the property is not found.
	 *
	 * @param key the key
	 * @param defaultValue a default value
	 * @return the value with the specified key
	 */
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
	
	/**
	 * Returns the property with the specified key. The method returns
	 * <code>null</code> if the property is not found.
	 *
	 * @param key the key
	 * @return the value with the specified key
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public String toString() {
		//StringBuffer buffer = new StringBuffer();
		return properties.toString();
		//return buffer.toString();
	}
	
	/**
	 * Returns a configuration instance with the specified name.
	 *
	 * @param name the name
	 * @return the configuration instance with the specified name
	 */
	public static Configuration getInstance(String name) {
		if (nameConfigMap.get(name) == null) {
			Configuration config = new Configuration(name);
			nameConfigMap.put(name, config);
		}
		return nameConfigMap.get(name);
	}
	
	/**
	 * Returns a configuration instance with the specified class name.
	 * Assumes the configuration file path is
	 * <code>DEFAULT_CONFIG_DIR + className  + ".propertiess".</code>
	 *
	 * @param className the class name
	 * @return the configuration instance with the specified class name
	 */
	public static Configuration getInstanceFromClassName(String className) {
		return getInstance(DEFAULT_CONFIG_DIR + className + ".properties");
	}

}
