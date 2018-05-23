/**
 * 创建于 2017年8月16日 下午3:58:31
 * @author zhg
 */
package familia.document;

/**
 * LDA文档存储基本单元，包含词id以及对应的主题id
 */
public class Token implements familia.java.inter.Constant
{
	public int topic;
	public int id;

	@Override
	public String toString()
	{
		return "#id=" + id + ",#topic=" + topic;
	}

}
