/**
 * 创建于2018-03-27 12:56:53
 * @author zhhaogen
 */
package familia.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.TextFormat;

import familia.Config.ModelConfig;
import familia.Config.ModelType;
import static familia.util.Util.*;
/**
 * @author zhhaogen
 *
 */
public class UtilTest implements familia.java.inter.Constant
{
	String modelDir;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		modelDir = System.getProperty("user.home") + "/Desktop/Familia/model";
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	} 
	@Test
	public void testSaveModel() throws  Exception
	{
		ModelConfig model=ModelConfig.newBuilder()
				.setType(ModelType.LDA)
				.setNumTopics(2000)
				.setAlpha(0.1f)
				.setBeta(0.1f)
				.setWordTopicFile("news_lda.model")
				.setVocabFile("vocab_info.txt")
				.setTweModelFile("news_twe_lda.model")
				.build();
		String str=model.toString();
		Logger.debug(str);
		ModelConfig conf1 = ModelConfig.parseFrom(model.toByteArray());
		Logger.debug(conf1);
		ModelConfig.Builder builder=ModelConfig.newBuilder();
		TextFormat.merge(str, builder);
		Logger.debug(builder.build());
	}
	/**
	 * {@link familia.util.Util#load_prototxt(java.lang.String)} 的测试方法。
	 */
	@Test
	public void testLoad_prototxt()
	{
		assertNotNull(load_prototxt(modelDir + "/news/lda.conf"));
	}
	@Test
	public void testRand()
	{
		fix_random_seed();
		for(int i=0;i<2000;i++)
		{
			System.out.println(i+" : "+rand());
		}
	}
}
