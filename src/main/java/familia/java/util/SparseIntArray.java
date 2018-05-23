package familia.java.util;

import java.util.Arrays;

/**
 * @author zhhaogen
 *
 */
public class SparseIntArray 
{
	/**
	 * 实际长度
	 */
	private int size;
	/**
	 * 内容储存
	 */
	private int[] items; 
	/**
	 * 记录位置是否有值
	 */
	private boolean[] positions;
	/**
	 * 
	 */
	public SparseIntArray()
	{
		this(0);
	}

	/**
	 * @param initialCapacity
	 */
	public SparseIntArray(int initialCapacity)
	{
		items=new int[initialCapacity];
		positions=new boolean[initialCapacity];
	}
	
	public int get(int key )
	{
//		if(key>=items.length)
//		{
//			throw new IndexOutOfBoundsException ("current size is"+items.length); 
//		}
		return  items[key];  
	} 
	
	public int set(int key ,int value)
	{
		if(key>=items.length)
		{
			newLength(key+1);
		} 
		if(!positions[key] )// 
		{ 
			size++;
			positions[key]=true;
		} 
		items[key]=value;
		return value;
	}
	public int remove(int key )
	{ 
		int ret = items[key];
		if(positions[key] )// 
		{ 
			items[key]=0;
			size--;
			positions[key]=false;
		}  
		return ret; 
	}
	public double setOrPlus(int key, int value)
	{
		if(key>=items.length)
		{
			newLength(key+1);
		}  
		if(!positions[key] )// 
		{ 
			size++;
			positions[key]=true;
		} 
		 items[key]= items[key]+value; 
		 return items[key];
	}
	/**
	 *更改数组长度
	 * @param length
	 */
	private void newLength(int length)
	{
		int dest[]=new int[length];
		boolean[] destPositions = new boolean[length];
		System.arraycopy(items, 0, dest , 0, items.length);
		System.arraycopy(positions, 0, destPositions , 0, positions.length); 
		items=dest;
		positions=destPositions;
	}
	public int size()
	{ 
		return size;
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
