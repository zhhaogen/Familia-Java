/**
 * 创建于2018-05-21 21:00:41
 * @author zhhaogen
 */
package familia.java.util;

import static org.junit.Assert.*;

import org.junit.Test;

import familia.model.TopicCount;

/**
 * @author zhhaogen
 *
 */
public class SparseArrayTest
{

	/**
	 * {@link familia.java.util.SparseArray#getOrSet(int, java.lang.Object)} 的测试方法。
	 */
	@Test
	public void testGetOrSet()
	{
		SparseArray<TopicCount> array = new SparseArray<>();
		array.getOrSet(2, new TopicCount());
		System.out.println(array);
		assertTrue(array.size()==1);
	}

	/**
	 * {@link familia.java.util.SparseArray#setOrPlus(int, java.lang.Object)} 的测试方法。
	 */
	@Test
	public void testSetOrPlus()
	{
		SparseIntArray array = new SparseIntArray();
		array.setOrPlus(2, 2);
		array.setOrPlus(3, 1);
		array.setOrPlus(2, 8);
		System.out.println(array);
		assertEquals(array.get(2) , 10);
	}

}
