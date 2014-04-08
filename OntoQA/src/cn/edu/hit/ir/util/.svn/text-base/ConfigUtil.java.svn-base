/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.util;

/**
 * Configuration utility.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-26
 */

public class ConfigUtil {
	/**
	 * The default configuration file directory
	 */
	public static final String DEFAULT_CONFIG_DIR = "conf/";
	
	/**
	 * The default postfix of configuration file.
	 */
	public static final String DEFAULT_CONFIG_POSTFIX = ".properties";

	/**
	 * Gets the path of configuration file with the given class,
	 * directory and postfix of the configuration file.
	 *
	 * @param c The given class
	 * @param dir The directory of the configuration file
	 * @param postfix The postfix of the configuration file
	 * @return The path of configuration file
	 */
	public static String getPath(Class<?> c, String dir, String postfix) {
		String path = dir + c.getName() + postfix;
		return path;
	}
	
	/**
	 * Gets the path of configuration file with the given class and
	 * default directory and postfix of the configuration file.
	 *
	 * @param c The given class
	 * @return The path of configuration file
	 */
	public static String getPath(Class<?> c) {
		return getPath(c, DEFAULT_CONFIG_DIR, DEFAULT_CONFIG_POSTFIX);
	}
}
