/**
 * 创建于2018-03-27 12:09:24
 * @author zhhaogen
 */
package familia.semanticmatching;

public class Embedding  
{ 
	float[] elements;
public	Embedding(int size)
	{
		elements=new float[size];
	}
	/**
	 * @param index
	 * @param d
	 */
	public void set(int index, float d)
	{
		elements[index]=d;
	}
	/**
	 * @return
	 */
	public int size()
	{
		return elements.length;
	}
	/**
	 * @param index
	 * @return
	 */
	public float get(int index)
	{
		return elements[index];
	}
}