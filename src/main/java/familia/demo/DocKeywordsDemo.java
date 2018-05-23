/**
 * 创建于2018-05-23 10:41:10
 * @author zhhaogen
 */
package familia.demo;

import java.util.*;

import familia.document.*;
import familia.inferenceengine.*;
import familia.semanticmatching.*;
import familia.tokenizer.*;

/**
 * @author zhhaogen
 *
 */
public class DocKeywordsDemo extends Demo
{
	InferenceEngine _engine;
	Tokenizer _tokenizer;
	// Topic Word Embedding模型
	TopicalWordEmbedding _twe;
	private String model_type;
	private int top_k;

	public DocKeywordsDemo(String model_dir, String conf_file, String emb_file, String model_type, int top_k)
	{
		this.model_type = model_type;
		this.top_k = top_k;
		_engine = new InferenceEngine(model_dir, conf_file);
		// 初始化分词器, 加载主题模型词表
		_tokenizer = new SimpleTokenizer(model_dir + "/vocab_info.txt");
		_twe = new TopicalWordEmbedding(model_dir, emb_file);
	}

	// 计算document内每个词与其之间的相关性
	// 可选的指标包括:
	// 1. document主题分布生成词的likelihood, 值越大越相关
	// 2. 基于TWE模型的相关性计算
	public void cal_doc_words_similarity(String document)
	{
		// 分词
		List<String> d_tokens = _tokenizer.tokenize(document);
		print_tokens("Doc Tokens", d_tokens);

		// 对长文本进行主题推断，获取主题分布
		LDADoc doc = new LDADoc();
		_engine.infer(d_tokens, doc);
		List<Topic> doc_topic_dist = new ArrayList<>();
		doc.sparse_topic_dist(doc_topic_dist);

		// 计算文档内每个词与文档的相关性
		List<WordAndDis> items = new ArrayList<>();
		List<String> words = new ArrayList<>();
		for (String word : d_tokens)
		{
			if (words.contains(word))
			{
				continue;
			}
			words.add(word);
			List<String> single_token = new ArrayList<>();
			single_token.add(word);
			WordAndDis wd = new WordAndDis();
			wd.word = word;
			if ("LDA".equals(model_type))
			{
				wd.distance = SemanticMatching.likelihood_based_similarity(single_token, doc_topic_dist,
						_engine.get_model());
			} else
			{
				wd.distance = SemanticMatching.twe_based_similarity(single_token, doc_topic_dist, _twe);
			}
			items.add(wd);
		}

		// 排序
		items.sort(new Comparator<WordAndDis>()
		{
			public int compare(WordAndDis o1, WordAndDis o2)
			{
				return Float.compare(o2.distance,o1.distance);
			}
		});

		// 打印结果
		System.out.println("Word                    Similarity          ");
		System.out.println("--------------------------------------------");
		int size = items.size();
		for (int i = 0; i < top_k; i++)
		{
			if (i >= size)
			{
				break;
			} 
			System.out.println( items.get(i).word +"\t"+ items.get(i).distance );   
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		DocKeywordsDemo demo = new DocKeywordsDemo(Demo.ModelDir + "news", "lda.conf", "news_twe_lda.model", "TWE", 10);
		demo.init();
	}
	private void init()
	{
		getConsole(br -> {
			System.out.println("请输入:");
			String doc1 = br.nextLine(); 
			this.cal_doc_words_similarity(doc1);
		});
	}
}
