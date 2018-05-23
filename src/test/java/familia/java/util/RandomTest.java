package familia.java.util;

import static org.junit.Assert.*;

import org.junit.Test; 

/**
 * 用于测试随机数
 */
public class RandomTest
{
	/**
	 * 误差值,java跟cpp精确度不一样
	 */
	private static final double delta = 1e-8;
	private static final int TIMES =200;
	/**
	 * 
	 */
	@Test
	public final void testMinstdRand0()
	{  
		testMinstdRand0( Integer.MAX_VALUE);  
	}

	public final void testMinstdRand0(long seed)
	{
		CMinstdRand0 rnd = new CMinstdRand0();
		rnd.setSeed(seed);
		MinstdRand0 _rnd = new MinstdRand0();
		_rnd.setSeed(seed);
		for (int i = 0; i < TIMES; i++)
		{
			double d = rnd.nextDouble();
			double _d = _rnd.nextDouble();
			System.out.println(d+" = "+_d);
			assertEquals(d, _d, delta);
		}
	}

	/**
	 * 
	 */
	@Test
	public final void testMersenneTwister()
	{
		testMersenneTwister(System.currentTimeMillis());
	}

	public final void testMersenneTwister(long seed)
	{
		CMT19937 rnd = new CMT19937();
		rnd.setSeed(seed);
		MersenneTwister _rnd = new MersenneTwister();
		_rnd.setSeed(seed);
		for (int i = 0; i < TIMES; i++)
		{
			double d = rnd.nextDouble();
			double _d = _rnd.nextDouble(); 
			 assertEquals(d,_d,delta);
		}
	}
}
