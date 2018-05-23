/**
 * 创建于 2017年8月14日 下午5:23:34
 * @author zhg
 */
package familia.util;

import java.io.*;
import java.nio.file.*; 

import com.google.protobuf.ByteString;
import com.google.protobuf.TextFormat;

import familia.Config.*;
import familia.java.util.*;

/**
 * 
 */
public class Util  implements familia.java.inter.Constant
{ 
	// linux 默认随机数生成 minstd_rand0
	private static MersenneTwister rnd = new MersenneTwister();// seed 2147483647

	public static void fix_random_seed()
	{
		// java.security.SecureRandom r=new SecureRandom();
		rnd.setSeed(Integer.MAX_VALUE);
		// rnd.reset();
	}

	// 返回min~max之间的随机浮点数, 默认返回0~1的浮点数
	public static double rand()
	{
		return rnd.nextDouble();
	}

	// 返回[0, k - 1]之间的整型浮点数
	public static int rand_k(int k)
	{
		return (int) (rand() * k);
	}

	/**
	 * 读取模型配置
	 * 
	 * @param conf_file
	 * @return
	 */
	public static ModelConfig load_prototxt(String conf_file)
	{
		Logger.debug("读取模型配置:" + conf_file);
		try
		{
			File confFile = new File(conf_file);
			byte[] datas = Files.readAllBytes(confFile.toPath()); 
			ModelConfig.Builder builder=ModelConfig.newBuilder(); 
			TextFormat.merge(new String(datas), builder);
			ModelConfig conf = builder.build();
//			Logger.debug("模型信息:" + conf);
			return conf;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
