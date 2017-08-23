/**
 * 创建于 2017年8月15日 下午8:41:06
 * @author zhg
 */
package familia;

import java.util.List;

/**
 * 分词器基类
 */
public interface Tokenizer
{
	List<String> tokenize(String  text )  ;
}
