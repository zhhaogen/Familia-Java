/**
 * 创建于2018-03-27 11:58:58
 * @author zhhaogen
 */
package familia.model;

import java.util.Objects;

/**
 * 主题计数
 * @author zhhaogen
 *
 */
public class TopicCount implements Comparable<TopicCount>
{
	/**
	 * 主题id
	 */
	public int topic_id;
	/**
	 * 计数
	 */
	public	int count;
	public TopicCount( )
	{ 
	}
	/**
	 * @param topic_id 主题id
	 * @param count 计数
	 */
	public TopicCount(int topic_id, int count)
	{ 
		this.topic_id = topic_id;
		this.count = count;
	}

	@Override
	public int compareTo(TopicCount o)
	{ 
		return 	Integer.compare( topic_id, o.topic_id) ;
	} 
}