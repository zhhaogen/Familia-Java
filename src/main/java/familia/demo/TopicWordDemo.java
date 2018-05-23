/**
 * 创建于 2017年8月14日 下午8:47:37
 * @author zhg
 */
package familia.demo;

import java.io.*;
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
public class TopicWordDemo extends Demo
{
	TopicalWordEmbedding _twe;
	// LDA中每个主题下出现概率最高的词
	Map<Integer, List<String>> _topic_words;
	// 每个主题下展示的词的数目, 默认为10
	static int _top_k = 10;

	TopicWordDemo(String model_dir, String emb_file, String topic_words_file)
	{
		_topic_words = new HashMap<>();
		_twe = new TopicalWordEmbedding(model_dir, emb_file);
		load_topic_words(model_dir + "/" + topic_words_file);

	}

	/**
	 * 读取主题模型的每个主题下的展现结果
	 * 
	 * @param topic_words_file
	 */
	private void load_topic_words(String topic_words_file)
	{
		Logger.debug("读取主题模型:" + topic_words_file + ",主题数:" + _twe.num_topics());
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(topic_words_file), "utf-8")))
		{
			String line;
			for (int t = 0; t < _twe.num_topics(); t++)
			{
				// 读取每个主题第一行信息，解析出topk
				line = br.readLine();
				String[] cols = line.split("\t");
				assert cols.length == 2;
				int topk = Integer.parseInt(cols[1]);
				// 读取多余行
				br.readLine();
				// 读取前k个词
				List<String> words = new ArrayList<>();
				for (int i = 0; i < topk; i++)
				{
					line = br.readLine();
					cols = line.split("\t");
					words.add(cols[0]);
				}
				_topic_words.put(t, words);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		Logger.debug("主题词数:" + _topic_words.size());
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{  
		TopicWordDemo demo = new TopicWordDemo(ModelDir  + "/news", "news_twe_lda.model", "topic_words.lda.txt");
		demo.init(); 
	}

	/**
	 * 
	 */
	private void init()
	{
		getConsole(br -> {
			System.out.println("请输入主题编号(0-" +(num_topics() - 1 )+"):");
			int topic_id = br.nextInt(); 
			show_topics(topic_id); 
		});
	}

	/**
	 * @return
	 */
	private int num_topics()
	{
		return _twe.num_topics();
	}

	/**
	 * @param topic_id
	 */
	public void show_topics(int topic_id)
	{
		List<WordAndDis> items = _twe.nearest_words_around_topic(topic_id, _top_k);
		print_result(items, _topic_words.get(topic_id));
	}

	/**
	 * @param items
	 * @param words
	 */
	private void print_result(List<WordAndDis> items, List<String> words)
	{
		System.out.println("Embedding Result              Multinomial Result");
		System.out.println("------------------------------------------------");
		for (int i = 0; i < _top_k; i++)
		{
			System.out.println(items.get(i).word + "\t\t" + words.get(i));
		}
	}

}
