/**
 * 创建于 2017年8月21日 下午9:45:05
 * @author zhg
 */
package familia.java.util;

/**
 *TODO 并不准确
 */
public class MinstdRand0
{
	private static final int multiplier = 16807; // 乘数
	private static final int addend = 0;
	private static final int mask = Integer.MAX_VALUE; //
	private long seed;

	public MinstdRand0()
	{
		seed = 3510019087l;
		// TODO setSeed(new byte[]{0,1,2,3});

	}

	public void setSeed(long seed)
	{
		 if(seed==mask)
		 {
			 seed=1;
		 }
		this.seed = seed; 
	}

	long next()
	{
		long _seed = (multiplier * seed + addend) % mask; // 计算 mod
		if (_seed < 0)
		{
			_seed += mask; // 将结果调整到 0 ~ m
		}
		return (seed = _seed);
	}

	public double nextDouble()
	{
		next();
		return (next() * 1d / mask * 1d);
	}
}
