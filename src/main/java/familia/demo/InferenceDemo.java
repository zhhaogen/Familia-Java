/**
 * 创建于 2017年8月15日 下午5:34:24
 * @author zhg
 */
package familia.demo;

import java.util.ArrayList;
import java.util.List;
 
import familia.Config.ModelType;
import familia.document.*;
import familia.inferenceengine.InferenceEngine;
import familia.inferenceengine.SamplerType;
import familia.tokenizer.SimpleTokenizer;
import familia.tokenizer.Tokenizer;

/**
 * 
 */
public class InferenceDemo extends Demo
{
	InferenceEngine _engine;
	Tokenizer _tokenizer;
	public InferenceDemo(String model_dir,	String conf_file)
	{  
		_engine = new InferenceEngine(model_dir, conf_file, SamplerType.MetropolisHastings);
		_tokenizer = new SimpleTokenizer(model_dir + "/vocab_info.txt");
	}
	// 打印文档的主题分布
	public void print_doc_topic_dist(List<Topic> topics)
	{
		System.out.println("Document Topic Distribution:\n");
		topics.sort(null);
		for (int i = 0; i < topics.size(); ++i)
		{
			System.out.println("tid =" + topics.get(i).tid + ", prob=" + topics.get(i).prob);
		}
	}
	public void printTokenize(String line)
	{
		List<List<String>> sentences = new ArrayList<>();
		List<String> input = _tokenizer.tokenize(line);
		if (_engine.model_type() == ModelType.LDA)
		{
			LDADoc doc = new LDADoc();
			_engine.infer(input, doc);
			List<Topic> topics = new ArrayList<>();
			doc.sparse_topic_dist(topics);
			print_doc_topic_dist(topics);
		} else if (_engine.model_type() == ModelType.SLDA)
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
			_engine.infer(sentences, doc);
			List<Topic> topics = new ArrayList<>();
			doc.sparse_topic_dist(topics);
			print_doc_topic_dist(topics);
			sentences.clear();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		InferenceDemo demo = new InferenceDemo(Demo.ModelDir + "news", "lda.conf" );
		demo.init();
	}
	private void init()
	{
		getConsole(br -> {
			System.out.println("请输入文档:");
			String line = br.nextLine();
			printTokenize(line);
		});
	}
}
