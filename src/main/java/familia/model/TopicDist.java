package familia.model;

import java.util.*;

import familia.java.util.*; 

/**
 * 多个主题计数构成主题分布
 * @author zhhaogen
 *
 */
public class TopicDist extends ArrayList<TopicCount>
{  

	/**
	 * 
	 */
	public TopicDist()
	{
		super();
	}

	/**
	 * @param initialCapacity
	 */
	public TopicDist(int initialCapacity)
	{
		super(initialCapacity);
	}

}
