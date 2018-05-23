/**
 * 创建于 2017年8月16日 下午4:31:29
 * @author zhg
 */
package familia.sampler;

import java.util.ArrayList;
import java.util.List;

import familia.document.*;
import familia.model.*; 
import static familia.util.Util.*;
/**
 * 吉布斯采样器，实现了LDA和SentenceLDA两种模型的采样算法
 */
public class GibbsSampler  implements familia.java.inter.Constant, Sampler
{

	TopicModel _model;

	/**
	 * @param _model
	 */
	public GibbsSampler(TopicModel _model)
	{
		this._model = _model;
	}

	@Override
	public void sample_doc(LDADoc doc)
	{
		int new_topic = -1;
		for (int i = 0, size = doc.size(); i < size; ++i)
		{
			new_topic = sample_token(doc, doc.token(i));
			doc.set_topic(i, new_topic);
		}
	}

	@Override
	public void sample_doc(SLDADoc doc)
	{
		int new_topic = -1;
		for (int i = 0, size = doc.size(); i < size; ++i)
		{
			new_topic = sample_sentence(doc, doc.sent(i));
			doc.set_topic(i, new_topic);
		}
	}

	public	int sample_token(LDADoc doc, Token token)
	{
		int old_topic = token.topic;
		int num_topics = _model._num_topics;
		List<Float> accum_prob = new ArrayList<>(num_topics);
		List<Float> prob = new ArrayList<>(num_topics);
		for (int i = 0; i < num_topics; i++)
		{
			accum_prob.add(0.0f);
			prob.add(0.0f);
		}
		float sum = 0.0f;
		float dt_alpha = 0.0f;
		float wt_beta = 0.0f;
		float t_sum_beta_sum = 0.0f;
		for (int t = 0; t < num_topics; ++t)
		{
			dt_alpha = doc.topic_sum(t) + _model._alpha;
			wt_beta = _model.word_topic(token.id, t) + _model._beta;
			t_sum_beta_sum = _model.topic_sum(t) + _model._beta_sum;
			if (t == old_topic && wt_beta > 1)
			{
				if (dt_alpha > 1)
				{
					dt_alpha -= 1;
				}
				wt_beta -= 1;
				t_sum_beta_sum -= 1;
			}
			float d = dt_alpha * wt_beta / t_sum_beta_sum;
			prob.set(t, d);
			sum += d;
			accum_prob.set(t, (t == 0 ? d : accum_prob.get(t - 1) + d));
		}

		double dart = rand() * sum;
		if (dart <= accum_prob.get(0))
		{
			return 0;
		}
		for (int t = 1; t < num_topics; ++t)
		{
			if (dart > accum_prob.get(t - 1) && dart <= accum_prob.get(t))
			{
				return t;
			}
		}

		return num_topics - 1; // 返回最后一个主题id
	}

public	int sample_sentence(SLDADoc doc, Sentence sent)
	{
		int old_topic = sent.topic;
		int num_topics = _model._num_topics;
		List<Float> accum_prob = new ArrayList<>(num_topics);
		List<Float> prob = new ArrayList<>(num_topics);
		for (int i = 0; i < num_topics; i++)
		{
			accum_prob.add(0.0f);
			prob.add(0.0f);
		}
		float sum = 0.0f;
		float dt_alpha = 0.0f;
		float t_sum_beta_sum = 0.0f;
		float wt_beta = 0.0f;
		// 为了保证数值计算的稳定，以下实现为SentenceLDA的采样近似实现
		for (int t = 0; t < num_topics; ++t)
		{
			dt_alpha = doc.topic_sum(t) + _model._alpha;
			t_sum_beta_sum = _model.topic_sum(t) + _model._beta_sum;
			if (t == old_topic)
			{
				if (dt_alpha > 1)
				{
					dt_alpha -= 1;
				}
				if (t_sum_beta_sum > 1)
				{
					t_sum_beta_sum -= 1;
				}
			}
			prob.set(t, dt_alpha);
			for (int i = 0; i < sent.tokens.size(); ++i)
			{
				int w = sent.tokens.get(i);
				wt_beta = _model.word_topic(w, t) + _model._beta;
				if (t == old_topic && wt_beta > 1)
				{
					wt_beta -= 1;
				}
				// NOTE: 若句子长度过长，此处连乘项过多会导致概率过小, 丢失精度
				float d = prob.get(t) * wt_beta / t_sum_beta_sum;
				prob.set(t, d);
			}
			sum += prob.get(t);
			accum_prob.set(t, (t == 0 ? prob.get(t) : accum_prob.get(t - 1) + prob.get(t)));
		}
		double dart = rand() * sum;
		if (dart <= accum_prob.get(0))
		{
			return 0;
		}
		for (int t = 1; t < num_topics; ++t)
		{
			if (dart > accum_prob.get(t - 1) && dart <= accum_prob.get(t))
			{
				return t;
			}
		}

		return num_topics - 1; // 返回最后一个主题id
	}
}
