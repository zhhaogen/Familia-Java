package familia.demo;
/**
 * 创建于 2017年8月14日 下午5:27:46
 * @author zhg
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import familia.ModelConfig;
import xiaogen.util.Logger;

import static familia.Util.*;

/**
 * 
 */
public class ShowTopicDemo extends Demo
{
	static class WordCount implements Comparable<WordCount>
	{
		int word_id;
		int count;

		@Override
		public int compareTo(WordCount w)
		{
			return w.count - count;
		}
	}

	private static final double EPS = 1e-8;;

	int _num_topics;
	/**
	 * 统计主题词总数
	 */
	Map<Integer, Long> _topic_sum_table;
	/**
	 * 统计主题词分布数
	 */
	Map<Integer, List<WordCount>> _topic_words;
	/**
	 * 词典
	 */
	Map<Integer, String> _vocabulary = new HashMap<>();

	/**
	 * @param num_topics
	 *            主题个数
	 * @param vocabulary_path
	 *            词典文件
	 * @param item_topic_table_path
	 *            模型文件
	 */
	public ShowTopicDemo(int num_topics, String vocabulary_path, String item_topic_table_path)
	{
		_num_topics = num_topics;
		_topic_sum_table = new HashMap<>();
		_topic_words = new HashMap<>();
		_vocabulary.clear();
		load_vocabulary(vocabulary_path);
		load_item_topic_table(item_topic_table_path);
		_topic_words.forEach((k, v) -> {
			Collections.sort(v);
		});
	}

	static void sort(List<WordCount> list)
	{
		list.sort((w1, w2) -> {
			return w1.count - w2.count;
		});
	}

	/**
	 * 加载模型信息
	 * 
	 * @param item_topic_table_path
	 */
	private void load_item_topic_table(String item_topic_table_path)
	{
		Logger.d("加载模型信息:" + item_topic_table_path);
		try
		{
			Files.lines(Paths.get(item_topic_table_path)).forEach(line -> {
				if (!line.isEmpty())
				{
					String[] items = line.split(" ");
					assert items.length >= 2;
					int word_id = Integer.parseInt(items[0]);// 词id
					for (int i = 1; i < items.length; i++)
					{
						String[] temps = items[i].split(":");// 主题id:权重
						assert temps.length == 2 ;
						int topic_index = Integer.parseInt(temps[0]);
						int count = Integer.parseInt(temps[1]);
						// 统计每个主题下词的总数
						WordCount current_word_count = new WordCount();
						current_word_count.count = count;
						current_word_count.word_id = word_id;
						{
							long element = 0;
							if (_topic_sum_table.containsKey(topic_index))
							{
								element = _topic_sum_table.get(topic_index);
							}
							element = element + count;
							_topic_sum_table.put(topic_index, element);
						}
						{
							List<WordCount> element = _topic_words.get(topic_index);
							if (element == null)
							{
								element = new ArrayList<>();
							} else
							{
								// Logger.d("topic_index :"+topic_index);
							}
							element.add(current_word_count);
							_topic_words.put(topic_index, element);
						}
					}

				}
			});
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 加载词典信息
	 * 
	 * @param vocabulary_path
	 */
	private void load_vocabulary(String vocabulary_path)
	{
		Logger.d("加载词典:" + vocabulary_path);
		try
		{
			Files.lines(Paths.get(vocabulary_path)).forEach(line -> {
				if (!line.isEmpty())
				{
					String[] items = line.split("\t");
					// 需要保证至少有前三列
					assert items.length >= 3;
					_vocabulary.put(Integer.parseInt(items[2]), items[1]);
				}
			});
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 打印指定主题下的前k个词
	 * 
	 * @param topic_id
	 * @param k
	 */
	void show_topics(int topic_id, int k)
	{
		long total = _topic_sum_table.get(topic_id);
		Logger.d("显示主题:" + topic_id + "，词总数:" + total);
		System.out.println("词语\t权重比\t权重");
		if (topic_id >= 0 && topic_id < _num_topics)
		{
			List<WordCount> words = _topic_words.get(topic_id);
			k = Math.min(k, words.size());
			for (int i = 0; i < k; i++)
			{
				int count = words.get(i).count;
				float prob = (float) ((count * 1.0) / (total + EPS));
				System.out.println(_vocabulary.get(words.get(i).word_id) + "\t" + prob + "\t" + count);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String model_dir = getModelDir() + "/news";
		String conf_file = "lda.conf";
		int top_k = 10;
		ModelConfig config = load_prototxt(model_dir + "/" + conf_file);
		ShowTopicDemo st_demo = new ShowTopicDemo(config.num_topics, model_dir + "/" + config.vocab_file,
				model_dir + "/" + config.word_topic_file);

		getConsole(br -> { 
			System.out.println("请输入主题编号(0-" +(config.num_topics - 1 )+"):");
			int topic_id=br.nextInt();
			st_demo.show_topics(topic_id, top_k);
		});
		System.out.println("退出程序");
	}

}
