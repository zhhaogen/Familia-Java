/**
 * 创建于 2017年8月15日 下午12:09:53
 * @author zhg
 */
package familia;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import familia.TopicalWordEmbedding.Embedding;
import static familia.Vocab.OOV;

/**
 * 
 */
public class SemanticMatching
{
	final static double EPS = 1e-06; // epsilon
 
	// 计算向量的长度，传入的是embedding
	// NOTE: 可用SSE进行向量运算加速，此处为了代码可读性不进行优化

	static float l2_norm(Embedding vec)
	{
		float result = 0.0f;
		for (int i = 0, size = vec.size(); i < size; ++i)
		{
			result += vec.get(i) * vec.get(i);
		}

		return (float) Math.sqrt(result);
	}

	// 计算两个embedding的余弦相似度
	static float cosine_similarity(Embedding vec1, Embedding vec2)
	{
		float result = 0.0f;
		float norm1 = l2_norm(vec1);
		float norm2 = l2_norm(vec2);

		// NOTE: 可用SSE进行向量运算加速，此处为了代码可读性不进行优化
		for (int i = 0, size = vec1.size(); i < size; ++i)
		{
			result += vec1.get(i) * vec2.get(i);
		}
		result = result / norm1 / norm2;

		return result;
	}

	/**
	 *  Kullback–Leibler divergence ,KL散度,     https://zh.wikipedia.org/wiki/%E7%9B%B8%E5%AF%B9%E7%86%B5  <br>
	 * <img src="https://wikimedia.org/api/rest_v1/media/math/render/svg/b1c44a164308ced602825bacc5122ca7d4715c78" >
	 * @param P
	 * @param Q
	 * @return
	 */
	public static float kullback_leibler_divergence(List<Float> P, List<Float> Q)
	{
		assert (P.size() == Q.size());
		float result = 0.0f;
		for (int i = 0, size = P.size(); i < size; ++i)
		{
			float pi = P.get(i);
			float qi = Q.get(i);
			qi = (float) (qi < EPS ? EPS : qi);
			Q.set(i, qi);
			result += pi * Math.log(pi / qi);
		}

		return result;
	}

	/**
	 *  Jensen-Shannon Divergence,  https://en.wikipedia.org/wiki/Jensen%E2%80%93Shannon_divergence<br>
	 *  <img src="https://wikimedia.org/api/rest_v1/media/math/render/svg/f2b4b683f73e3e1cd325265a2dc08d2136f957c8" >
	 * @param P
	 * @param Q
	 * @return
	 */
	public static float jensen_shannon_divergence(List<Float> P, List<Float> Q)
	{
		int size = P.size();
		assert (P.size() == Q.size()):"两个维度必须一样"; //
		// 检测分布值小于epsilon的情况
		for (int i = 0; i < size; ++i)
		{
			float p1 = P.get(i);
			P.set(i, (float) (p1 < EPS ? EPS : p1));
			float qi = Q.get(i);
			Q.set(i, (float) (qi < EPS ? EPS : qi));
		}

		List<Float> M = new ArrayList<>(P.size());
		for (int i = 0; i < size; ++i)
		{
			float pi = P.get(i);
			float qi = Q.get(i);
			M.add((pi + qi) * 0.5f);
		}

		float jsd = (float) (kullback_leibler_divergence(P, M) * 0.5
				+ kullback_leibler_divergence(Q, M) * 0.5);
		return jsd;
	}

	/**
	 * Hellinger Distance ,https://en.wikipedia.org/wiki/Hellinger_distance <br>
	 * <img src="https://wikimedia.org/api/rest_v1/media/math/render/svg/b02b905abe1b4f23a83df08ff89216587e033c39" >
	 * @param P
	 * @param Q
	 * @return
	 */ 
	public static float hellinger_distance(List<Float> P, List<Float> Q)
	{
		int size=P.size();
		assert (P.size() == Q.size()):"传入的两个参数维度须一致";

		// NOTE: 可用SSE进行向量运算加速，此处为了代码可读性不进行优化
		float result = 0.0f;
		for (int i = 0 ; i < size; ++i)
		{
			float pi = P.get(i);
			float qi = Q.get(i);
			double tmp =  (Math.sqrt(pi) - Math.sqrt(qi));
			result += tmp * tmp;
		}
		
		// 1/√2 = 0.7071067812
		result = (float) (Math.sqrt(result) * 0.7071067812 );
		return result;
	}

	// 使用短文本到长文本之间的似然值表示之间的相似度
	public static float likelihood_based_similarity(List<String> terms, List<Topic> doc_topic_dist, TopicModel model)
	{
		int num_of_term_in_vocab = 0;
		float result = 0.0f;

		for (int i = 0; i < terms.size(); ++i)
		{
			int term_id = model.term_id(terms.get(i));
			if (term_id == OOV)
			{
				continue;
			}

			// 统计在词表中的单词
			num_of_term_in_vocab += 1;
			for (int j = 0; j < doc_topic_dist.size(); ++j)
			{
				int topic_id = doc_topic_dist.get(j).tid;
				float prob = (float) doc_topic_dist.get(j).prob;
				result += model.word_topic(term_id, topic_id) * 1.0 / model.topic_sum(topic_id) * prob;
			}
		}

		if (num_of_term_in_vocab == 0)
		{
			return result;
		}

		return result / num_of_term_in_vocab;
	}

	// 基于Topical Word Embedding (TWE) 计算短文本与长文本的相似度
	// 输入短文本明文分词结果，长文本主题分布，TWE模型，返回长文本与短文本语义相似度
	public static float twe_based_similarity(List<String> terms, List<Topic> doc_topic_dist, TopicalWordEmbedding twe)
	{
		int short_text_length = terms.size();
		float result = 0.0f;

		for (int i = 0; i < terms.size(); ++i)
		{
			if (!twe.contains_word(terms.get(i)))
			{
				short_text_length--;
				continue;
			}
			Embedding word_emb = twe.word_emb(terms.get(i));
			for (Topic topic : doc_topic_dist)
			{
				Embedding topic_emb = twe.topic_emb(topic.tid);
				result += cosine_similarity(word_emb, topic_emb) * topic.prob;
			}
		}

		if (short_text_length == 0)
		{ // 如果短文本中的词均不在词表中
			return 0.0f;
		}

		return result / short_text_length; // 针对短文本长度进行归一化
	}
}
