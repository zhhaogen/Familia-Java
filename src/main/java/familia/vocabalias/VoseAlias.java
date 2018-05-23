/**
 * 创建于 2017年8月16日 下午6:03:04
 * @author zhg
 */
package familia.vocabalias;

import java.util.*;
import familia.java.util.*;
import static familia.util.Util.*;

/**
 * Vose's Alias Method 数值稳定版本实现 更多的具体细节可以参考
 * http://www.keithschwarz.com/darts-dice-coins/
 */
public class VoseAlias
{
	/** alias table **/
	public SparseIntArray _alias;
	/** probability table **/
	public SparseDoubleArray _prob;

	public VoseAlias()
	{
		_prob = new SparseDoubleArray();
		_alias = new SparseIntArray();
	}

	// 根据输入分布初始化alias table
	public void initialize(double[] distribution)
	{
		int size = distribution.length;
		_prob.resize(size);
		_alias.resize(size);
		double[] p = new double[size];
		double sum = 0;
		for (int i = 0; i < size; ++i)
		{
			sum += distribution[i];
		}
		for (int i = 0; i < size; ++i)
		{
			p[i] = (distribution[i] / sum * size); // scale up probability
		}
		ArrayDeque<Integer> large = new ArrayDeque<>();
		ArrayDeque<Integer> small = new ArrayDeque<>();

		for (int i = 0; i < size; ++i)
		{
			if (p[i] < 1.0)
			{
				small.add(i);
			} else
			{
				large.add(i);
			}
		}

		while (!small.isEmpty() && !large.isEmpty())
		{
			int l = small.pop();
			int g = large.pop();

			_prob.set(l, p[l]);
			_alias.set(l, g);
			p[g] = p[g] + p[l] - 1;// a more numerically stable
									// option

			if (p[g] < 1.0)
			{
				small.add(g);
			} else
			{
				large.add(g);
			}
		}
		while (!large.isEmpty())
		{
			int g = large.getFirst();
			large.pop();
			_prob.set(g, 1.0d);
		}
		while (!small.isEmpty())
		{
			int l = small.getFirst();
			small.pop();
			_prob.set(l, 1.0d);
		}
	}

	// 从给定分布中生成采样样本
	public int generate()
	{
		int dart1 = rand_k(size());
		int dart2 = (int) rand();// TODO ==0? 
		return dart2 > _prob.get(dart1) ? dart1 : _alias.get(dart1);
	}

	// 离散分布的维度
	public int size()
	{
		return _prob.size();
	}

}
