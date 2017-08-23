/**
 * 创建于 2017年8月15日 下午5:34:24
 * @author zhg
 */
package familia.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import familia.InferenceEngine;
import familia.InferenceEngine.SamplerType;
import familia.LDADoc;
import familia.ModelConfig.ModelType;
import familia.SLDADoc;
import familia.SimpleTokenizer;
import familia.Tokenizer;
import familia.Topic;

/**
 * 
 */
public class InferenceDemo extends Demo
{
	// 打印文档的主题分布
	static void print_doc_topic_dist(List<Topic> topics)
	{
		System.out.println("Document Topic Distribution:\n");
		topics.sort(null);
		for (int i = 0; i < topics.size(); ++i)
		{
			System.out.println("tid =" + topics.get(i).tid + ", prob=" + topics.get(i).prob);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String model_dir = getModelDir() + "/news";
		String conf_file = "lda.conf";
		String vocab_path = model_dir + "/vocab_info.txt";
		InferenceEngine engine = new InferenceEngine(model_dir, conf_file, SamplerType.MetropolisHastings);
		Tokenizer tokenizer = new SimpleTokenizer(vocab_path);
		List<List<String>> sentences = new ArrayList<>();
		getConsole(br -> {
			System.out.println("请输入文档:");
			String line = br.nextLine();
			List<String> input = tokenizer.tokenize(line);
			if (engine.model_type() == ModelType.LDA)
			{
				LDADoc doc = new LDADoc();
				engine.infer(input, doc);
				List<Topic> topics = new ArrayList<>();
				doc.sparse_topic_dist(topics);
				print_doc_topic_dist(topics);
			} else if (engine.model_type() == ModelType.SLDA)
			{
				List<String> sent = new ArrayList<>();
				for (int i = 0; i < input.size(); ++i)
				{
					sent.add(input.get(i));
					// 为了简化句子边界问题，以5-gram作为一个句子
					// 其中n不宜太大，否则会导致采样过程中数值计算精度下降
					if (sent.size() % 5 == 0)
					{
						sentences.add(sent);
						sent.clear();
					}
				}

				// 剩余单词作为一个句子
				if (sent.size() > 0)
				{
					sentences.add(sent);
				}

				SLDADoc doc = new SLDADoc();
				engine.infer(sentences, doc);
				List<Topic> topics = new ArrayList<>();
				doc.sparse_topic_dist(topics);
				print_doc_topic_dist(topics);
				sentences.clear();
			}
		});
	}

}
