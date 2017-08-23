/**
 * 创建于 2017年8月16日 下午3:58:57
 * @author zhg
 */
package familia;

import java.util.List;
import java.util.Map;

/**
 * SentenceLDA文档存储基本单元，包含句子的词id以及对应的主题id
 */
public class Sentence
{
	 int topic;
	 List<Integer> tokens;
}
