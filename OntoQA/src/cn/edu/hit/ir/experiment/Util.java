/**
 * Copyright 2011 HIT-CIR
 * Research Center for Information Retrieval
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */

package cn.edu.hit.ir.experiment;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * Some utilities for experiment.
 *
 * @author   bin3 (bin183cs@gmail.com)
 * @version  0.1.0
 * @date	 2011-6-19
 */

public class Util {
	
	private static Logger logger = Logger.getLogger(Util.class);
	
	public static final String wrongPathname = CorpusTool.DATA_DIR + "wrong.xml";
	public static final String preWrongPathname = CorpusTool.DATA_DIR + "pre_wrong.xml";
	public static final String curWrongPathname = CorpusTool.DATA_DIR + "cur_wrong.xml";
	
	public static void updateWrongFile() {
		System.out.println("@Util.updateWrongFile");
		
		try {
			FileUtils.copyFile(new File(curWrongPathname), new File(preWrongPathname));
			FileUtils.copyFile(new File(wrongPathname), new File(curWrongPathname));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("@Util.updateWrongFile done!");
	}
	
	public static void logSet(Set<String> set, String name) {
		logger.info(name + "(" + set.size() + "):");
		logger.info(set);
	}
	
	public static void compareIds() {
		
		List<Example> preExamples = CorpusTool.readCorpus(preWrongPathname);
		List<Example> curExamples = CorpusTool.readCorpus(curWrongPathname);
		
		Comparator<String> strComparator = new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.hashCode() - o2.hashCode();
			}
		};
		
		Set<String> preIdSet = new TreeSet<String>(strComparator);
		Set<String> curIdSet = new TreeSet<String>(strComparator);
		Set<String> removedIdSet = new TreeSet<String>(strComparator);
		Set<String> newIdSet = new TreeSet<String>(strComparator);
		
		for (Example example : preExamples) {
			String id = example.getId();
			preIdSet.add(id);
			removedIdSet.add(id);
		}
		for (Example example : curExamples) {
			String id = example.getId();
			curIdSet.add(id);
			newIdSet.add(id);
		}
		
		removedIdSet.removeAll(curIdSet);
		newIdSet.removeAll(preIdSet);
		
		logSet(preIdSet, "preIdSet");
		logSet(curIdSet, "curIdSet");
		logSet(removedIdSet, "removedIdSet");
		logSet(newIdSet, "newIdSet");
	}
	
	public static void main(String[] args) {
		updateWrongFile();
		compareIds();
	}

}
