/**
 * 创建于2018-03-27 12:09:12
 * @author zhhaogen
 */
package familia.semanticmatching;

public class WordAndDis
{
	public String word;
	public float distance;

	@Override
	public String toString()
	{
		return word + "#" + distance;
	}

}