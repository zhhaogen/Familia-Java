/**
 * 创建于 2017年8月16日 下午3:57:03
 * @author zhg
 */
package familia;

/**
 * 主题的基本数据结构，包含id以及对应的概率
 */
public class Topic implements Comparable<Topic>
{
public	int tid; // topic id
public    double prob; // topic probability
	@Override
	public int compareTo(Topic o)
	{
		double def=o.prob- prob;
		if(def>0)
		{
			return 1;
		}else if(def<0)
		{
			return -1;
		}
		return 0;
	}

}
