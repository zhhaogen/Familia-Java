/**
 * 创建于2018-03-27 12:17:08
 * @author zhhaogen
 */
package familia.sampler;
/**
 * 一个int数组
 * @author zhhaogen
 *
 */
public class TopicIndex 
{
	/** */
	private static final long serialVersionUID = 1L;
	private int[] items;
	public TopicIndex()
	{
		super();
		items=new int[0];
	}

	/**
	 * @param length
	 */
	public void setLength(int length)
	{
		items=new int[length];
	}

	/**
	 * @param i
	 * @param value
	 */
	public void set(int 	i, int value)
	{
		items[i]=value;
	}

	/**
	 * @param i
	 * @return
	 */
	public int get(int i)
	{ 
		return items[i];
	}
}