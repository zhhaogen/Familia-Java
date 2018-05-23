/**
 * 创建于2018-05-23 12:15:18
 * @author zhhaogen
 */
package familia.demo;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author zhhaogen
 *
 */
public class DocKeywordsDemoTest
{
 
	@Test
	public void testNewsLda()
	{
		String document="2018人工智能再次写入政府工作报告，崭新的时代来临，你准备好了吗？";
		DocKeywordsDemo demo = new DocKeywordsDemo(Demo.ModelDir + "news", "lda.conf", "news_twe_lda.model", "TWE", 10);
		demo.cal_doc_words_similarity(document);
	}

}
