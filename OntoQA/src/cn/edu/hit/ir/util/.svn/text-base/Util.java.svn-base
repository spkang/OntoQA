/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.util;

import java.util.HashSet;
import java.util.Set;

/**
 * A utility collector.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-7
 */

public class Util {

	public static String lastWord(Object o) {
		if (o == null) {
			return "null";
		}
		
		String s = o.toString();
		int index = s.lastIndexOf('/');
		if (index != -1) {
			s = s.substring(index+1);
		}
		index = s.lastIndexOf('#');
		if (index != -1) {
			s = s.substring(index+1);
		}
		return s;
	}
	
	/**
	 * Returns the intersection of two given sets.
	 *
	 * @param <E>
	 * @param a the first set
	 * @param b the second set
	 * @return the intersection or an empty set if no common elements 
	 */
	public static <E> Set<E> intersect(Set<E> a, Set<E> b) {		
		Set<E> result = new HashSet<E>();
		if (a != null && b != null) {
			result.addAll(a);
			result.retainAll(b);
		}		
		return result;
	} 
}
