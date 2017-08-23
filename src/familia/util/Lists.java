/**
 * 创建于 2017年8月16日 下午7:38:17
 * @author zhg
 */
package familia.util;

import java.util.ArrayList;

/**
 * 
 */
public class Lists
{
	public	static <T> ArrayList<T> newArrayList(int size, Number num)
	{
		ArrayList<T>	list=	new ArrayList<>(size);
		for(int i=0;i<size;i++)
		{
			list.add((T) num);
		} 
		return list; 
	}
	public static <T> ArrayList<T> newArrayList(int size, Class<T> clz)
	{
		ArrayList<T>	list=	new ArrayList<>(size);
		for(int i=0;i<size;i++)
		{
			try
			{
				list.add(clz.newInstance());
			} catch (InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}
 
}
