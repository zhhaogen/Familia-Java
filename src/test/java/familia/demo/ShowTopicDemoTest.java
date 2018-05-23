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
public class ShowTopicDemoTest
{
 
	@Test
	public void testNewsLda()
	{
	 
		ShowTopicDemo demo = new ShowTopicDemo(Demo.ModelDir + "news", "lda.conf",10 );
		demo.show_topics(33);
	}

}
