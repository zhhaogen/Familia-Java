/**
 * 创建于 2017年8月16日 下午6:03:04
 * @author zhg
 */
package familia;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import familia.util.Lists;
import xiaogen.util.Logger;

import static familia.Util.*;

/**
 * 
 */
// Vose's Alias Method 数值稳定版本实现
// 更多的具体细节可以参考 http://www.keithschwarz.com/darts-dice-coins/
public class VoseAlias
{
	// alias table
	List<Integer> _alias;
	// probability table
	List<Double> _prob;

	public VoseAlias()
	{
	}

	// 根据输入分布初始化alias table
	void initialize(List<Double> distribution)
	{ 
		int size = distribution.size();
		_prob = Lists.newArrayList(size, 0d);
		_alias = Lists.newArrayList(size, 0);
		List<Double> p = new ArrayList<>(size);

		double sum = 0;
		for (int i = 0; i < size; ++i)
		{
			sum += distribution.get(i);
		}
		for (int i = 0; i < size; ++i)
		{
			p.add(distribution.get(i) / sum * size); // scale up probability
		}

		LinkedList<Integer> large = new LinkedList<>();
		LinkedList<Integer> small = new LinkedList<>();
		for (int i = 0; i < size; ++i)
		{
			if (p.get(i) < 1.0)
			{
				small.add(i);
			} else
			{
				large.add(i);
			}
		}
		while (!small.isEmpty() && !large.isEmpty())
		{
			int l = small.getFirst();
			int g = large.getFirst();
			small.pop();
			large.pop();
			_prob.set(l, p.get(l));
			_alias.set(l, g);
			p.set(g, p.get(g) + p.get(l) - 1);// a more numerically stable
												// option

			if (p.get(g) < 1.0)
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
	int generate()
	{
		int dart1 = rand_k(size());
		int dart2 = (int) rand();// TODO ==0?
		return dart2 > _prob.get(dart1) ? dart1 : _alias.get(dart1);
	}

	// 离散分布的维度
	int size()
	{
		return _prob.size();
	}

	 
}
