package familia.java.util;

import java.util.Arrays;

/**
 * @author zhhaogen
 *
 */
public class SparseDoubleArray 
{
	/**
	 * 实际长度
	 */
	private int size;
	/**
	 * 内容储存
	 */
	private double[] items; 
	/**
	 * 记录位置是否有值
	 */
	private boolean[] positions;
	/**
	 * 
	 */
	public SparseDoubleArray()
	{
		this(0);
	}

	/**
	 * @param initialCapacity
	 */
	public SparseDoubleArray(int initialCapacity)
	{
		items=new double[initialCapacity];
		positions=new boolean[initialCapacity];
	}
	
	public double get(int key )
	{
//		if(key>=items.length)
//		{
//			throw new IndexOutOfBoundsException ("current size is"+items.length); 
//		}
		return  items[key];  
	} 
	
	public double set(int key ,double value)
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
	public double remove(int key )
	{ 
		double ret = items[key];
		if(positions[key] )// 
		{ 
			items[key]=0;
			size--;
			positions[key]=false;
		}  
		return ret; 
	}
	public double setOrPlus(int key, double value)
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
		double dest[]=new double[length];
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
