/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.util;

/**
 * A printer utility.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-5-15
 */

public class Printer {
	public static void print(Object[] objects) {
		System.out.print("[");
		if (objects != null && objects.length > 0) {
			System.out.print(objects[0]);
			for (int i = 1; i < objects.length; ++i) {
				System.out.print(", " + objects[i]);
			}
		}
		System.out.println("]");
	}
	
	public static void print(Object object) {
		System.out.println(object);
	}
}
