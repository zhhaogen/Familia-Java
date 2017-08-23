/**
 * 创建于 2017年8月16日 下午4:41:49
 * @author zhg
 */
package familia;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xiaogen.util.Logger;

/**
 * 简单版本FMM分词器，仅用于主题模型应用Demo，非真实业务应用场景使用 NOTE: 该分词器只识别主题模型中词表的单词
 */
public class SimpleTokenizer implements Tokenizer
{

	// 词表中单词最大长度
	int _max_word_len;
	// 词典数据结构
	Set<String> _vocab;

	/**
	 * @param vocab_path
	 */
	public SimpleTokenizer(String vocab_path)
	{
		_vocab = new HashSet<>();
		load_vocab(vocab_path);
	}

	// 检查word是否在词表中
	boolean contains(String word)
	{
		return _vocab.contains(word);
	}

	// 加载分词词典, 与主题模型共享一套词典
	void load_vocab(String vocab_path)
	{
		Logger.d("加载词典:"+vocab_path);
		try
		{
			Files.lines(Paths.get(vocab_path)).forEach(line -> {
				String[] fields = line.split("\t");
				assert fields.length >= 2;
				String word = fields[1];
				if (word.length() > _max_word_len)
				{
					_max_word_len = word.length();
				}
				_vocab.add(word);
			});
			Logger.d("总共词数:" + _vocab.size()+",词最大长度:"+_max_word_len);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// 检查字符是否为英文字符
	static boolean is_eng_char(char c)
	{
		// 'A' - 'Z' & 'a' - 'z'
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}

	// 返回对应字符的小写字符，如无对应小写字符则返回原字符
	static char tolower(char c)
	{
		if (c >= 'A' && c <= 'Z')
		{
			return (char) ('a' + (c - 'A'));
		} else
		{
			return c;
		}
	}

	/**
	 * 对输入text字符串进行简单分词，结果存放在result中
	 */
	public List<String> tokenize(String text)
	{
		List<String> result = new ArrayList<>();
		StringBuilder word=new StringBuilder();
		String found_word;
		char[] texts = text.toCharArray();
		int text_len =texts.length;
		for (int i = 0; i < text_len; ++i)
		{
			word.setLength(0);
			found_word = "";
			// 处理英文字符的分支
			if (is_eng_char(texts[i]))
			{
				// 遍历至字符串末尾\0以保证纯英文串切分
				for (int j = i; j <= text_len; ++j)
				{
					// 一直寻找英文字符,直到遇到非英文字符串
					if (j < text_len && is_eng_char(texts[j]))
					{
						// 词表中只包含小写字母单词, 对所有英文字符均转小写
						word.append(tolower(texts[j]));
					} else
					{
						// 按字符粒度正向匹配
						if (_vocab.contains(word.toString())  )
						{
							result.add(word.toString());
						}
						i = j - 1;
						break;
					}
				}
			} else
			{
				for (int j = i; j < i + _max_word_len && j < text_len; ++j)
				{
					word.append(texts[j]);
					if (_vocab.contains(word.toString())  )
					{
						found_word = word.toString();
					}
				}
				if (!found_word.isEmpty())
				{
					result.add(found_word);
					i += found_word.length() - 1;
				}
			}
		}
		return result;
	}

}
