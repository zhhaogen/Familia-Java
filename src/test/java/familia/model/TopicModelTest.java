/**
 * 创建于2018-05-22 19:42:42
 * @author zhhaogen
 */
package familia.model;

import static familia.util.Util.load_prototxt;
import static org.junit.Assert.*;

import org.junit.Test;

import familia.Config.ModelConfig;
import familia.demo.Demo;

/**
 * @author zhhaogen
 *
 */
public class TopicModelTest
{

	/**
	 * {@link familia.model.TopicModel#load_model(java.lang.String, java.lang.String)} 的测试方法。
	 */
	@Test
	public void testLoad_model()
	{
		ModelConfig config = load_prototxt(Demo.ModelDir +"news"+ "/" + "slda.conf");
		TopicModel _model = new TopicModel(Demo.ModelDir+"news" , config); 
		System.out.println(_model);
	}
	@Test
	public void testBigArray()
	{
		int size=30816835;
		TopicCount array[]=new TopicCount[size];
		for(int i=0;i<array.length;i++)
		{
			array[i]=new TopicCount();
		}
		System.out.println(array);
	}
}
