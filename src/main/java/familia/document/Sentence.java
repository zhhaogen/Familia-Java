/**
 * 创建于 2017年8月16日 下午3:58:57
 * @author zhg
 */
package familia.document;

import java.util.List;

/**
 * SentenceLDA文档存储基本单元，包含句子的词id以及对应的主题id
 */
public class Sentence implements familia.java.inter.Constant
{
	public	 int topic;
	public	 List<Integer> tokens;
	public String toString()
	{
		return "#topic=" + topic + ",#tokens=" + tokens;
	}
}
