/**
 * 创建于 2017年8月15日 下午8:37:34
 * @author zhg
 */
package familia.demo;

import java.util.*;
import familia.document.*;
import familia.inferenceengine.*;
import familia.semanticmatching.*;
import familia.tokenizer.*;

/**
 * 主题模型语义匹配计算Demo类
 */
public class DocDistanceDemo extends Demo
{
	InferenceEngine _engine;
	Tokenizer _tokenizer;

	/**
	 * @param model_dir 存放model文件夹
	 * @param conf_file 配置文件名
	 */
	public DocDistanceDemo(String model_dir, String conf_file)
	{
		_engine = new InferenceEngine(model_dir, conf_file);
		// 初始化分词器, 加载主题模型词表
		_tokenizer = new SimpleTokenizer(model_dir + "/vocab_info.txt");
		 
	}
	/**
	 *  计算长文本之间的相似度 <br>
	 *  可选的指标包括常用的分布间距离Jensen-Shannon Diveregnce和Hellinger Distance
	 * @param doc_text1
	 * @param doc_text2
	 */
	public void cal_doc_distance(String doc_text1, String doc_text2)
	{
		// 分词
		List<String> doc1_tokens = _tokenizer.tokenize(doc_text1);
		List<String> doc2_tokens = _tokenizer.tokenize(doc_text2);
		print_tokens("文本1分词", doc1_tokens);
		print_tokens("文本2分词", doc2_tokens);

		// 文档主题推断, 输入分词结果，主题推断结果存放于LDADoc中
		LDADoc doc1 = new LDADoc(), doc2 = new LDADoc();
		_engine.infer(doc1_tokens, doc1);
		_engine.infer(doc2_tokens, doc2);

		// 计算jsd需要传入稠密型分布
		// 获取稠密的文档主题分布
		List<Float> dense_dist1 = doc1.dense_topic_dist();
		List<Float> dense_dist2 = doc2.dense_topic_dist();
		// 计算分布之间的距离, 值越小则表示文档语义相似度越高
		float jsd = SemanticMatching.jensen_shannon_divergence(dense_dist1, dense_dist2);
		float hd = SemanticMatching.hellinger_distance(dense_dist1, dense_dist2);
		Logger.debug("Jensen-Shannon Divergence = " + jsd);
		Logger.debug("Hellinger Distance = " + hd);
	}



	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
	 new DocDistanceDemo(ModelDir+"webpage","lda.conf").init();
	}

	private void init()
	{
		getConsole(br -> {
			System.out.println("请输入文档1:");
			String doc1 = br.nextLine();
			System.out.println("请输入文档2:");
			String doc2 = br.nextLine();
			cal_doc_distance(doc1, doc2);
		});
	}
}
