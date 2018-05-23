/**
 * 创建于 2017年8月16日 下午4:30:48
 * @author zhg
 */
package familia.sampler;

import java.util.ArrayList;
import java.util.List;
import familia.document.*;
import familia.java.util.*;
import familia.model.*;
import familia.vocabalias.*;
import static familia.util.Util.*;

/**
 * 基于Metropolis-Hastings的采样器实现，包含LDA和SentenceLDA两个模型的实现
 */
public class MHSampler implements familia.java.inter.Constant, Sampler
{
	/** LDA model pointer, shared by sampler and inference engine**/
	TopicModel _model;

	/** 主题的下标映射**/
	SparseArray<TopicIndex> _topic_indexes;

	/** 存放每个单词使用VoseAlias Method构建的alias结果(word-proposal无先验参数部分)**/
	SparseArray<VoseAlias> _alias_tables;

	/** 存放每个单词各个主题下概率之和(word-proposal无先验参数部分)**/
	SparseDoubleArray _prob_sum;

	/** 存放先验参数部分使用VoseAlias Method构建的alias结果(word-proposal先验参数部分)**/
	VoseAlias _beta_alias;

	/** 存放先验参数各个主题下概率之和(word-proposal先验参数部分)**/
	double _beta_prior_sum;

	/** Metropolis-Hastings steps, 默认值为2**/
	static final int _mh_steps = 2;

	/**
	 * @param _model
	 */
	public MHSampler(TopicModel _model)
	{
		this._model = _model;
		construct_alias_table();
	}

	@Override
	public void sample_doc(LDADoc doc)
	{
		for (int i = 0, size = doc.size(); i < size; ++i)
		{
			int new_topic = sample_token(doc, doc.token(i));
			doc.set_topic(i, new_topic);
		}
	}

	// 对文档中的一个词进行主题采样, 返回采样结果对应的主题ID
	int sample_token(LDADoc doc, Token token)
	{
		int new_topic = token.topic;

		for (int i = 0; i < _mh_steps; ++i)
		{
			int doc_proposed_topic = doc_proposal(doc, token);
			new_topic = word_proposal(doc, token, doc_proposed_topic);
		}
		return new_topic;
	}

	// doc proposal for LDA
	int doc_proposal(LDADoc doc, Token token)
	{
		int old_topic = token.topic;
		int new_topic = old_topic;
		double dart = rand() * (doc.size() + _model._alpha_sum);

		if (dart < doc.size())
		{
			int token_index = (int) (dart);
			new_topic = doc.token(token_index).topic;
		} else
		{
			// 命中文档先验部分, 则随机进行主题采样
			new_topic = rand_k(_model._num_topics);
		}

		if (new_topic != old_topic)
		{
			float proposal_old = doc_proposal_distribution(doc, old_topic);
			float proposal_new = doc_proposal_distribution(doc, new_topic);
			float proportion_old = proportional_funtion(doc, token, old_topic);
			float proportion_new = proportional_funtion(doc, token, new_topic);
			double transition_prob = (proportion_new * proposal_old) / (proportion_old * proposal_new);
			double rejection = rand();

			int mask = -(rejection < transition_prob ? 1 : 0);
			return (new_topic & mask) | (old_topic & ~mask); // 用位运算避免if分支判断
		}

		return new_topic;
	}

	@Override
	public void sample_doc(SLDADoc doc)
	{
		int new_topic = 0;
		for (int i = 0, size = doc.size(); i < size; ++i)
		{
			new_topic = sample_sentence(doc, doc.sent(i));
			doc.set_topic(i, new_topic);
		}
	}

	// 对当前词id的单词使用Metroplis-Hastings方法proprose一个主题id
	int propose(int word_id)
	{
		// 决定是否要从先验参数的alias table生成一个样本
		double dart = rand() * (_prob_sum.get(word_id) + _beta_prior_sum);

		int topic = -1;
		if (dart < _prob_sum.get(word_id))
		{
			VoseAlias va = _alias_tables.get(word_id);// 不正确372.60914062533465
			int idx = va.generate(); // 从alias
										// table中生成一个样本
			topic = _topic_indexes.get(word_id).get(idx); // 找到当前idx对应的真实主题id
		} else
		{ // 命中先验概率部分
			// 先验alias table为稠密分布，无需再做ID映射
			topic = _beta_alias.generate();

		}

		return topic;
	}

	// 对文档中的一个句子进行主题采样, 返回采样结果对应的主题ID
	int sample_sentence(SLDADoc doc, Sentence sent)
	{
		int new_topic = sent.topic;
		for (int i = 0; i < _mh_steps; ++i)
		{
			int doc_proposed_topic = doc_proposal(doc, sent);
			new_topic = word_proposal(doc, sent, doc_proposed_topic);
		}

		return new_topic;
	}

	// doc proposal for Sentence-LDA
	int doc_proposal(SLDADoc doc, Sentence sent)
	{
		int old_topic = sent.topic;
		int new_topic = -1;

		double dart = rand() * (doc.size() + _model._alpha_sum);
		if (dart < doc.size())
		{
			int token_index = (int) (dart);

			new_topic = doc.sent(token_index).topic;
		} else
		{
			// 命中文档先验部分, 则随机进行主题采样
			new_topic = rand_k(_model._num_topics);
		}

		if (new_topic != old_topic)
		{
			float proportion_old = proportional_funtion(doc, sent, old_topic);
			float proportion_new = proportional_funtion(doc, sent, new_topic);
			float proposal_old = doc_proposal_distribution(doc, old_topic);
			float proposal_new = doc_proposal_distribution(doc, new_topic);
			double transition_prob = (proportion_new * proposal_old) / (proportion_old * proposal_new);
			double rejection = rand();
			int mask = -(rejection < transition_prob ? 1 : 0);
			return (new_topic & mask) | (old_topic & ~mask);
		}

		return new_topic;
	}

	// word proposal for LDA
	int word_proposal(LDADoc doc, Token token, int old_topic)
	{
		int new_topic = propose(token.id); // prpose a new topic from alias
											// table

		if (new_topic != old_topic)
		{
			float proposal_old = word_proposal_distribution(token.id, old_topic);
			float proposal_new = word_proposal_distribution(token.id, new_topic);
			float proportion_old = proportional_funtion(doc, token, old_topic);
			float proportion_new = proportional_funtion(doc, token, new_topic);
			double transition_prob = (proportion_new * proposal_old) / (proportion_old * proposal_new);
			double rejection = rand();
			int mask = -(rejection < transition_prob ? 1 : 0);
			return (new_topic & mask) | (old_topic & ~mask);
		}

		return new_topic;
	}

	// word proposal for Sentence-LDA
	int word_proposal(SLDADoc doc, Sentence sent, int old_topic)
	{
		int new_topic = old_topic;
		for (int word_id : sent.tokens)
		{
			new_topic = propose(word_id); // prpose a new topic from alias table
			if (new_topic != old_topic)
			{
				float proportion_old = proportional_funtion(doc, sent, old_topic);
				float proportion_new = proportional_funtion(doc, sent, new_topic);
				float proposal_old = word_proposal_distribution(word_id, old_topic);
				float proposal_new = word_proposal_distribution(word_id, new_topic);
				double transition_prob = (proportion_new * proposal_old) / (proportion_old * proposal_new);

				double rejection = rand();
				int mask = -(rejection < transition_prob ? 1 : 0);
				new_topic = (new_topic & mask) | (old_topic & ~mask);
			}
		}

		return new_topic;
	}

	// propotional function for LDA model
	float proportional_funtion(LDADoc doc, Token token, int new_topic)
	{
		int old_topic = token.topic;
		float dt_alpha = doc.topic_sum(new_topic) + _model._alpha;
		float wt_beta = _model.word_topic(token.id, new_topic) + _model._beta;
		float t_sum_beta_sum = _model.topic_sum(new_topic) + _model._beta_sum;
		if (new_topic == old_topic && wt_beta > 1)
		{
			if (dt_alpha > 1)
			{
				dt_alpha -= 1;
			}
			wt_beta -= 1;
			t_sum_beta_sum -= 1;
		}

		return dt_alpha * wt_beta / t_sum_beta_sum;
	}

	// propotional function for SLDA model
	float proportional_funtion(SLDADoc doc, Sentence sent, int new_topic)
	{
		int old_topic = sent.topic;
		float result = doc.topic_sum(new_topic) + _model._alpha;
		if (new_topic == old_topic)
		{
			result -= 1;
		}
		for (int word_id : sent.tokens)
		{
			float wt_beta = _model.word_topic(word_id, new_topic) + _model._beta;
			float t_sum_beta_sum = _model.topic_sum(new_topic) + _model._beta_sum;
			if (new_topic == old_topic && wt_beta > 1)
			{
				wt_beta -= 1;
				t_sum_beta_sum -= 1;
			}

			result *= wt_beta / t_sum_beta_sum;
		}

		return result;
	}

	// doc proposal distribution for LDA and Sentence-LDA
	float doc_proposal_distribution(LDADoc doc, int topic)
	{
		return doc.topic_sum(topic) + _model._alpha;
	}

	// word proposal distribuiton for LDA and Sentence-LDA
	float word_proposal_distribution(int word_id, int topic)
	{
		float wt_beta = _model.word_topic(word_id, topic) + _model._beta;
		float t_sum_beta_sum = _model.topic_sum(topic) + _model._beta_sum;

		return wt_beta / t_sum_beta_sum;
	}

	// 根据LDA模型参数构建alias table
	int construct_alias_table()
	{
		int vocab_size = _model.vocab_size();
		_topic_indexes =new SparseArray<>(vocab_size, TopicIndex.class)  ;
		_alias_tables =new SparseArray<>(vocab_size, VoseAlias.class);
		_prob_sum = new SparseDoubleArray(vocab_size);
		_beta_alias = new VoseAlias();
		// 构建每个词的alias table (不包含先验部分) 
		double dist[]=null;
		double prob_sum =0;
		for (int i = 0; i < vocab_size; ++i)
		{
			 
		   prob_sum = 0;
			List<TopicCount> itor = _model.word_topic(i);
			if(itor==null)
			{
				dist=new double[0];
			}else 
			{ 
				TopicIndex topic_index = _topic_indexes.get(i); 
				int ksize=itor.size();
				dist=new double[ksize];
				topic_index.setLength(ksize);
				for (int k=0;k<ksize;k++ )
				{
					TopicCount iter = itor.get(k);
					
					int topic_id = iter.topic_id; // topic index
					int word_topic_count = iter.count; // topic count
					int topic_sum = _model.topic_sum(topic_id); // topic sum
					topic_index.set(k,topic_id);  
					
					double q = word_topic_count / (topic_sum + _model._beta_sum);
					dist[k]=q;
					prob_sum += q;
				}
			}

			_prob_sum.set(i, prob_sum);
			if (dist.length> 0)
			{
				_alias_tables.get(i).initialize(dist);
			}
		}
	
	 
		Logger.debug("_pro_sum size="+_prob_sum.size());
		// 构建先验参数beta的alias table
		_beta_prior_sum = 0;
		double[] beta_dist = new double[_model._num_topics];
		for (int i = 0; i < _model._num_topics; ++i)
		{
			double d = _model._beta / (_model.topic_sum(i) + _model._beta_sum);
			beta_dist[i]=d;
			_beta_prior_sum += d;
		}
		_beta_alias.initialize(beta_dist);

		Logger.debug("#beta_prior_sum=" + _beta_prior_sum);
		return 0;
	}

}
