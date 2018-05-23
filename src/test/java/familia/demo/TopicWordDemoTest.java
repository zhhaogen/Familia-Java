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
public class TopicWordDemoTest
{
 
	@Test
	public void testNewsLda()
	{
	 
		TopicWordDemo demo = new TopicWordDemo(Demo.ModelDir  + "/news", "news_twe_lda.model", "topic_words.lda.txt");
		demo.show_topics(33);
	}

}
