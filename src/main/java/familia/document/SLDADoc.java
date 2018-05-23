/**
 * 创建于 2017年8月16日 下午4:23:43
 * @author zhg
 */
package familia.document;

import java.util.ArrayList;
import java.util.List;

/**
 *Sentence LDA Document  继承自LDADoc，新增了add_sentence接口
 */
public class SLDADoc extends LDADoc
{
	List<Sentence> _sentences = new ArrayList<>();

	/**
	 * @param num_topics
	 */
	public	SLDADoc(int num_topics)
	{
		super(num_topics);
	}
	/**
	 * 
	 */
	public SLDADoc()
	{
	}
	public void  init(int num_topics) {
	    _num_topics = num_topics;
	    _sentences.clear();
	    _topic_sum =new int[_num_topics] ;
		_accum_topic_sum = new int[_num_topics] ;
	}
	/** 新增句子**/
	public	void add_sentence(Sentence sent){
		 _sentences.add(sent);
		 _topic_sum[sent.topic]++;
	}

	// 对文档中第index个句子的主题置为new_topic, 并更新相应的文档主题分布
	public void set_topic(int index, int new_topic)
	{
		Sentence ele = _sentences.get(index);
		int old_topic = ele.topic;
		if (new_topic == old_topic)
		{
			return;
		}
		ele.topic = new_topic;
		_sentences.set(index, ele);
		 _topic_sum[old_topic]--;
		    _topic_sum[new_topic]++;
	}

	// 返回文档句子数量
	public int size()
	{
		return _sentences.size();
	}

	public Sentence sent(int index)
	{
		return _sentences.get(index); 
	}
}
