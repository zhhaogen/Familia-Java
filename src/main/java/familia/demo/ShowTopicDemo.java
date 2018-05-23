package familia.demo;
/**
 * 创建于 2017年8月14日 下午5:27:46
 * @author zhg
 */

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import familia.Config.ModelConfig;
import familia.document.*;
import familia.inferenceengine.*;
import familia.semanticmatching.*;
import familia.tokenizer.*;
import familia.util.Util;

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
			return Integer.compare(w.count, count);
		}
	}

	private static final double EPS = 1e-8;;

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
	Map<Integer, String> _vocabulary;

	private ModelConfig config;

	private int top_k;

	public ShowTopicDemo(String model_dir, String conf_file, int top_k)
	{
		config = Util.load_prototxt(model_dir + "/" + conf_file);
		this.top_k = top_k;
		load_vocabulary(model_dir + "/" + config.getVocabFile());
		load_item_topic_table(model_dir + "/" + config.getWordTopicFile());
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
		_topic_sum_table = new HashMap<>();
		_topic_words = new HashMap<>();
		Logger.debug("加载模型信息:" + item_topic_table_path);
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
						assert temps.length == 2;
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
		_vocabulary = new HashMap<>();
		Logger.debug("加载词典:" + vocabulary_path);
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
	void show_topics(int topic_id )
	{
		long total = _topic_sum_table.get(topic_id);
		Logger.debug("显示主题:" + topic_id + "，词总数:" + total);
		System.out.println("词语\t权重比\t权重");
		if (topic_id >= 0 && topic_id < config.getNumTopics())
		{
			List<WordCount> words = _topic_words.get(topic_id);
			int k = Math.min(top_k, words.size());
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

		ShowTopicDemo demo = new ShowTopicDemo(ModelDir + "/news", "lda.conf", 10);
		demo.init();
	}

	private void init()
	{
		getConsole(br -> {
			System.out.println("请输入主题编号(0-" + (config.getNumTopics() - 1) + "):");
			int topic_id = br.nextInt();
			show_topics(topic_id);
		});
	}
}
