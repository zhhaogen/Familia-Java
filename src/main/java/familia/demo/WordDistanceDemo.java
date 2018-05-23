/**
 * 创建于 2017年8月15日 下午8:22:16
 * @author zhg
 */
package familia.demo;

import java.util.ArrayList;
import java.util.List;

import familia.Config.ModelConfig;
import familia.document.*;
import familia.inferenceengine.*;
import familia.semanticmatching.*;
import familia.tokenizer.*;
import familia.util.Util;

/**
 * 
 */
public class WordDistanceDemo extends Demo
{
	// Topic Word Embedding模型
	TopicalWordEmbedding _twe;
	private int top_k;

	/**
	 * @param model_dir
	 * @param emb_file
	 */
	public WordDistanceDemo(String model_dir, String emb_file, int top_k)
	{
		_twe = new TopicalWordEmbedding(model_dir, emb_file);
		this.top_k = top_k;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		WordDistanceDemo demo = new WordDistanceDemo(ModelDir + "/news", "news_twe_lda.model", 20);
		demo.init();

	}

	/**
	 * 
	 */
	private void init()
	{
		getConsole(br -> {
			System.out.println("请输入词语:");
			String word = br.nextLine();
			find_nearest_words(word);
		});
	}

	/**
	 * @param word
	 * @param top_k
	 */
	public void find_nearest_words(String word)
	{
		List<WordAndDis> items = new ArrayList<>(this.top_k);
		for (int i = 0; i < top_k; i++)
		{
			items.add(new WordAndDis());
		}
		// 如果词不存在模型词典，则返回
		if (!_twe.contains_word(word))
		{
			System.out.println(word + "不在词典中");
			return;
		}
		_twe.nearest_words(word, items);
		print_result(items);
	}

	// 打印结果
	void print_result(List<WordAndDis> items)
	{
		System.out.println("Word                       Cosine Distance  ");
		System.out.println("--------------------------------------------");
		for (WordAndDis item : items)
		{
			System.out.println(item.word + "\t" + item.distance);
		}
	}
}
