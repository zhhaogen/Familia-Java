package familia.java.util;

import java.util.Arrays;

/**
 * 一个可以随意设置位置的列表,相当于cpp的vector<br>
 * 用java list、或map方法表示std::vector,1.不能初始化长度,2.不能随意位置
 * 
 * @author zhhaogen
 */
public class SparseArray<T>
{
	/**
	 * 实际长度
	 */
	private int size;
	/**
	 * 内容储存
	 */
	private Object[] items; 
	public SparseArray( )
	{
		this(0);
	}
	/**
	 * 
	 * @param initialCapacity
	 *            初始化容量,并不代表实际大小
	 */
	public SparseArray(int initialCapacity)
	{
		items =new Object[initialCapacity];
	}
	 /**
	  * 初始化容量
	  * @param size
	  * @param clz
	  */
	public SparseArray(int size,Class<T> clz)
	{
		items =new Object[size];
		for(int i=0;i<size;i++)
		{
			try
			{
				items[i]=clz.newInstance();
			} catch ( Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public T get(int key )
	{
		if(key>=items.length)
		{
			return null;
		}
		return (T) items[key];  
	}
	public T set(int key ,T value)
	{
		if(key>=items.length)
		{
			newLength(key+1);
		} 
		if(items[key]==null&&value!=null)// 
		{
			size++;
		}
		if(items[key]!=null&&value==null)// 
		{
			size--;
		}
		items[key]=value;
		return value;
	}
	/**
	 * 获取或设置
	 * @param key
	 * @param value
	 * @return
	 */
	public T getOrSet(int key,T value)
	{ 
		T val = get(key);
		if(val==null)
		{
			set(key, value);
			return value; 
		}else
		{
			return val; 
		} 
	}
 
	/**
	 * @return
	 */
	public int size()
	{ 
		return size;
	} 
	/**
	 *更改数组长度
	 * @param length
	 */
	private void newLength(int length)
	{
		Object dest[]=new Object[length];
		System.arraycopy(items, 0, dest , 0, items.length); 
		items=dest;
	}
	/**
	 *更改数组长度
	 * @param length
	 */
	public void resize(int length)
	{
		if(length<items.length)
		{
			return;
		}
		newLength(length);
	}
	public String toString()
	{ 
		return Arrays.toString(items);
	}
	
}
