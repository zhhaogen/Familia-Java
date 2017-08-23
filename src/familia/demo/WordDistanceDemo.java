/**
 * 创建于 2017年8月15日 下午8:22:16
 * @author zhg
 */
package familia.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import familia.TopicalWordEmbedding;
import familia.TopicalWordEmbedding.WordAndDis;
import familia.util.Lists;

/**
 * 
 */
public class WordDistanceDemo extends Demo
{
	// Topic Word Embedding模型
	TopicalWordEmbedding _twe;

	/**
	 * @param model_dir
	 * @param emb_file
	 */
	public WordDistanceDemo(String model_dir, String emb_file)
	{
		_twe = new TopicalWordEmbedding(model_dir, emb_file);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String model_dir = getModelDir() + "/news";
		String emb_file = "news_twe_lda.model";
		int top_k = 20;
		WordDistanceDemo wd_demo = new WordDistanceDemo(model_dir, emb_file);
		getConsole(br -> {
			System.out.println("请输入词语:");
			String word = br.nextLine();
			wd_demo.find_nearest_words(word, top_k);
		});
	}

	/**
	 * @param word
	 * @param top_k
	 */
	private void find_nearest_words(String word, int top_k)
	{
		List<WordAndDis> items = Lists.newArrayList(top_k, WordAndDis.class);
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
