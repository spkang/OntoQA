/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * The string utility.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-15
 */

public class StringUtil {

	/**
	 * Joins all the elements in a collection with a delimiter.
	 *
	 * @param collection the collection
	 * @param delimiter the delimiter
	 * @return the joined string
	 */
	public static String join(Collection<String> collection, String delimiter) {
		if (collection.isEmpty()) return "";
		Iterator<String> iter = collection.iterator();
		StringBuffer buffer = new StringBuffer(iter.next());
		while (iter.hasNext()) {
			buffer.append(delimiter).append(iter.next());
		}
		return buffer.toString();
	}
	
	/**
	 * Joins all the elements in an string array with a delimiter.
	 *
	 * @param strings the string array
	 * @param delimiter the delimiter
	 * @return the joined string
	 */
	public static String join(String[] strings, String delimiter) {
		if (strings.length == 0) return "";
		StringBuffer buffer = new StringBuffer(strings[0]);
		for (int si = 1; si < strings.length; ++si) {
			buffer.append(delimiter).append(strings[si]);
		}
		return buffer.toString();
	}
}
