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
public class WordDistanceDemoTest
{
 
	@Test
	public void testNewsLda()
	{ 
		String document1="人工智能";
		WordDistanceDemo demo = new WordDistanceDemo(Demo.ModelDir + "/news", "news_twe_lda.model", 20);
		demo.find_nearest_words(document1);
	}

}
