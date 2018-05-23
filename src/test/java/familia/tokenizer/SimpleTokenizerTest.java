/**
 * 创建于2018-03-28 18:14:02
 * @author zhhaogen
 */
package familia.tokenizer;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test; 
import familia.demo.Demo;
import familia.java.inter.Constant;

/**
 * @author zhhaogen
 *
 */
public class SimpleTokenizerTest implements Constant
{
	SimpleTokenizer _tokenizer;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		_tokenizer = new SimpleTokenizer(Demo.ModelDir + "/news/vocab_info.txt");
	}

	@Test
	public void test()
	{
		String text = "曾经需要跑到公积金中心办理、还要带上各种证明材料才能提取的公积金，现在在支付宝上刷脸，就能坐等钱到账了。只要是浙江省直单位缴存公积金的职工，即日起可在支付宝上办理无房提取、离退休提取、本市本人购房提取等业务。一次都不用跑、一份材料都不用带，这项服务在全国尚属首创";
		List<String> words = _tokenizer.tokenize(text);
		Logger.debug(words.size());
		Logger.debug(words);
		String tokenStr = "公积金,中心,办理,带上,证明,材料,提取,公积金,支付宝,刷脸,坐等,到账,浙江省,单位,缴存,公积金,职工,即日,支付宝,办理,无房,提取,离退休,提取,购房,提取,业务,材料,服务,全国,首创";
		List<String> tokens = Arrays.asList(tokenStr.split(","));
		Logger.debug(tokens.size());
		Logger.debug(tokens);
		assertArrayEquals(words.toArray(new String[0]), tokens.toArray(new String[0]));
	}

}
