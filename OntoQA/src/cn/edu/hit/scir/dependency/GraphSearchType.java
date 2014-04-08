/**
 * Copyright 2014 HIT-SCIR
 * Research Center for Information Retrieval and Social Network
 * Harbin Institute of Technology
 * http://ir.hit.edu.cn
 */
package cn.edu.hit.scir.dependency;

/**
 * 	PRE_NOUN_DFS, search the pre modifiers for the given noun word
 *  POST_NOUN_DFS , search the post modifiers for the given noun word
 *  NOUN_DFS, search all the modifiers for the given  noun word
 *  PRE_VERB_DFS , search the pre modifiers for the given verb word
 *  POST_VERB_DFS, search the post modifiers for the given verb word
 *  VERB_DFS, search all the modifiers for the given verb word
 *
 * @author spkang (kangshupeng@163.com)
 * @version 0.1.0
 * @date 2014年3月29日 
 */
public enum GraphSearchType {
	PRE_NOUN_DFS, POST_NOUN_DFS, NOUN_DFS, PRE_VERB_DFS, POST_VERB_DFS, VERB_DFS, PRE_IN_DFS, POST_IN_DFS, IN_DFS
}
