/**
 * 创建于 2017年8月22日 下午8:26:24
 * @author zhg
 */
package familia.demo;

import java.util.Objects;
import java.util.Scanner;

import familia.Util;
import xiaogen.util.Logger;

/**
 * 
 */
public class Demo
{
	/**
	 * 模型下载路径
	 * @return
	 */
	  static String getModelDir()
	{ 
		return System.getProperty("user.home")+"/Desktop/Familia/model";
	}
	static void getConsole(java.util.function.Consumer<Scanner> action)
	{
		Objects.requireNonNull(action); 
		try (Scanner br = new Scanner(System.in))
		{
			while (true)
			{
				action.accept(br);
				//check random
				Logger.d("next random:"+Util.rand());
			} 
		} 
	}
}
