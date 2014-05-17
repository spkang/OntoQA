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
	
	/**
	 * 判断一个字符串是不是中文
	 *
	 * @param sentence 输入的句子
	 * @return boolean 
	 */
	public static final boolean isChinese(String sentence) {
		if (sentence == null || sentence.isEmpty())
			return false;
		char[] ch = sentence.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断一个字符是是不是中文
	 *
	 * @param c, 输入的字符
	 * @return boolean 
	 */
	private static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}
}
