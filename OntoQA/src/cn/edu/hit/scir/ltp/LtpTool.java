/**
 * Project Name:GitEclipseTest
 * File Name:LtpTool.java
 * Package Name:test
 * Date:2014年5月5日上午10:50:25
 * Copyright (c) 2014, kangshupeng@163.com All Rights Reserved.
 *
*/

package cn.edu.hit.scir.ltp;

import java.util.List;

/**
 * ClassName:LtpTool <br/>
 * Function: 提供ltp的分词和词性标注功能 <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2014年5月5日 上午10:50:25 <br/>
 * @author   spkang
 * @version  
 * @since    JDK 1.7
 * @see 	 
 */


/**
 * ClassName: LtpTool <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2014年5月5日 上午10:58:01 <br/>
 *
 * @author spkang
 * @version 
 * @since JDK 1.7
 */
public interface LtpTool { 
	
	/**
	 * ltpSegment:(对输入的句子进行分词). <br/>
	 *
	 * @author spkang
	 * @param sentence
	 * @return 分词结果
	 * @since JDK 1.7
	 */
	public List<String> ltpSegment (String sentence);
	
	/**
	 * ltpSegment:(对输入的句子进行词性标注). <br/>
	 *
	 * @author spkang
	 * @param sentence
	 * @return 分词结果
	 * @since JDK 1.7
	 */
	public List<String> ltpTag (String sentence);
	
	
	
	/**
	 * ltpSegmentTag:(对输入的句子进行分词词性标注). <br/>
	 * 
	 *
	 * @author spkang
	 * @param sentence
	 * @return List<String> <word/tag>的形式存储
	 * @since JDK 1.7
	 */
	public List<String> ltpSegmentTag (String sentence);
	
}

