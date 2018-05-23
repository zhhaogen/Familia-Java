/**
 * 创建于 2017年8月16日 下午3:57:03
 * @author zhg
 */
package familia.document;

/**
 * 主题的基本数据结构，包含id以及对应的概率
 */
public class Topic implements familia.java.inter.Constant, Comparable<Topic>
{
	/** topic id**/
	public int tid; 
	/**  topic probability**/
	public double prob; 

	@Override
	public int compareTo(Topic o)
	{ 
		return Double.compare(prob, o.prob);
	}
	public String toString()
	{
		return "#tid=" + tid + ",#prob=" + prob;
	}
}
