/**
 * 创建于 2017年8月16日 下午1:10:10
 * @author zhg
 */
package familia.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import familia.Config.*; 
import familia.java.util.*;
import familia.vocab.*;

/**
 * 主题模型模型存储结构，包含词表和word topic count两分布 其中LDA和SentenceLDA使用同样的模型存储格式
 */
public class TopicModel  implements familia.java.inter.Constant
{  
	/*** 模型对应的词表数据结构**/
	public	Vocab _vocab;
	/*** word topic 模型参数**/
	public	SparseArray<TopicDist>  _word_topic;
	/** word topic对应的每一维主题的计数总和**/
	public SparseIntArray _topic_sum;
	/** 主题数**/
	public int _num_topics;
	/** 主题模型超参数**/
	public float _alpha;
	public	float _alpha_sum;
	public	float _beta;
	public	float _beta_sum;
	/**模型类型**/
	public	ModelType _type;

	/**
	 * @param model_dir
	 * @param config
	 */
	public TopicModel(String model_dir, ModelConfig config)
	{
		_num_topics = config.getNumTopics();
		_beta = config.getBeta();
		_alpha = config.getAlpha();
		_alpha_sum = _alpha * _num_topics;
		_topic_sum = new SparseIntArray(_num_topics);
		_type = config.getType() ;
		_vocab = new Vocab();
		// 加载模型
		load_model(model_dir + "/" + config.getWordTopicFile(), model_dir + "/" + config.getVocabFile());
	}

	public	int term_id(String term)
	{
		return _vocab.get_id(term);
	}

	// 加载word topic count以及词表文件
	public	void load_model(String word_topic_path, String vocab_path)
	{
		_vocab.load(vocab_path);
		_beta_sum = _beta * _vocab.size();
		_word_topic = new SparseArray<>(_vocab.size()); 
		load_word_topic(word_topic_path); 
	}

	public	void load_word_topic(String word_topic_path)
	{
		Logger.debug("加载 word topic 模型: " + word_topic_path);
		 AtomicLong dumpSize=new AtomicLong();//调试统计TopicCount类数量
		try
		{ 
			Files.lines(Paths.get(word_topic_path)).parallel().forEach(line -> {
				String[] fields = line.split(" ");
//				assert fields.length > 0; 
				int term_id = Integer.parseInt(fields[0]); //
				for (int i = 1; i < fields.length; ++i)
				{
					String[] topic_count = fields[i].split(":");//格式 主题ID:计数
					int topic_id = Integer.parseInt(topic_count[0]);
					int count = Integer.parseInt(topic_count[1]);
					TopicCount e = new TopicCount(topic_id,count);  
					dumpSize.addAndGet(1);
					_word_topic.getOrSet(term_id,  new TopicDist()).add(e);   
					_topic_sum.setOrPlus(topic_id, count); 
				}
				// 按照主题下标进行排序,  不考虑性能
				_word_topic.get(term_id).sort(null);
			});   
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		//TODO Topic Count size =30 816 835 ,占用内存太大
		Logger.debug("word topic 主题数量:" + _word_topic.size()+",Topic Count 数量:"+dumpSize.longValue());
	}

	// 返回模型中某个词在某个主题下的参数值，由于模型采用稀疏存储，若找不到则返回0
	public int word_topic(int word_id, int topic_id)
	{
		List<TopicCount> ts = _word_topic.get(word_id);
		if (ts != null)
		{
			for (TopicCount t : ts)
			{
				if (t.topic_id == topic_id)
				{
					return t.count;
				}
			}
		}
		return 0;
	}

	public	int vocab_size()
	{
		return _vocab.size();
	}

	// 返回某个词的主题分布
	public List<TopicCount> word_topic(int term_id)
	{
		return _word_topic.get(term_id);
	}

	// 返回指定topic id的topic sum参数
	public	int topic_sum(int topic_id)
	{
		return _topic_sum.get(topic_id);
	}
}
