/**
 * 创建于 2017年8月16日 下午4:00:03
 * @author zhg
 */
package familia.document;

import java.util.*; 

/**
 * 
 */
public class LDADoc implements familia.java.inter.Constant
{
	// 主题数
	int _num_topics;
	// 累积的采样轮数
	int _num_accum;
	// 文档先验参数alpha
	float _alpha;
	// inference 结果存储结构
	List<Token> _tokens = new ArrayList<>();
	// 文档在一轮采样中的topic sum
	int[] _topic_sum;
	// topic sum在多轮采样中的累积结果
	int[] _accum_topic_sum;

	LDADoc(int num_topics)
	{
		init(num_topics);
	}

	/**
	 * 
	 */
	public LDADoc()
	{
	}

	// 根据主题数初始化文档结构
	public void init(int num_topics)
	{
		_num_topics = num_topics;
		_num_accum = 0; // 清空采样累积次数
		_tokens.clear();
		_topic_sum = new int[_num_topics];
		_accum_topic_sum = new int[_num_topics];
	}

	// 添加新的单词
	public void add_token(Token token)
	{
		_tokens.add(token);
		_topic_sum[token.topic]++;
	}

	public Token token(int index)
	{
		return _tokens.get(index);
	}

	// 对文档中第index个单词的主题置为new_topic, 并更新相应的文档主题分布
	public void set_topic(int index, int new_topic)
	{
		Token ele = _tokens.get(index);
		int old_topic = ele.topic;
		if (new_topic == old_topic)
		{
			return;
		}
		ele.topic = new_topic;
		_tokens.set(index, ele);
		_topic_sum[old_topic]--;
		_topic_sum[new_topic]++;
	}

	// 配置文档先验参数alpha
	public void set_alpha(float alpha)
	{
		_alpha = alpha;
	}

	// 返回文档中词的数量
	public int size()
	{
		return _tokens.size();
	}

	public	int topic_sum(int topic_id)
	{
		return _topic_sum[topic_id];
	}

	public void sparse_topic_dist(List<Topic> topic_dist)
	{
		sparse_topic_dist(topic_dist, null);
	}

	// 返回稀疏格式的文档主题分布, 默认按照主题概率从大到小的排序
	// NOTE: 这一接口返回结果为了稀疏化，忽略了先验参数的作用
	public	void sparse_topic_dist(List<Topic> topic_dist, Boolean sort)
	{
		if (sort == null)
		{
			sort = true;
		}
		topic_dist.clear();
		int sum = 0;
		for (int i = 0; i < _num_topics; ++i)
		{
			sum += _accum_topic_sum[i];
		}
		if (sum == 0)
		{
			return; // 返回空结果
		}
		for (int i = 0; i < _num_topics; ++i)
		{
			// 跳过0的的项，得到稀疏主题分布
			if (_accum_topic_sum[i] == 0)
			{
				continue;
			}
			Topic e = new Topic();
			e.tid = i;
			e.prob = _accum_topic_sum[i] * 1.0 / sum;
			topic_dist.add(e);
		}
		if (sort)
		{
			Collections.sort(topic_dist);
		}
	}

	/**
	 * 返回稠密格式的文档主题分布, 考虑了先验参数的结果
	 * @return
	 */
	public List<Float> dense_topic_dist()
	{
		List<Float> result = new ArrayList<>();
		if (size() == 0)
		{
			return result;
		}
		for (int i = 0; i < _num_topics; ++i)
		{
			float e = (float) ((_accum_topic_sum[i] * 1.0 / _num_accum + _alpha) / (size() + _alpha * _num_topics));
			result.add(e);
		}
		return result;
	}

	// 对每轮采样结果进行累积, 以得到一个更逼近真实后验的分布
	public void accumulate_topic_sum()
	{
		for (int i = 0; i < _num_topics; ++i)
		{
			_accum_topic_sum[i] += _topic_sum[i];
		}
		_num_accum += 1;
	} 

}
