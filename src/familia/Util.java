/**
 * 创建于 2017年8月14日 下午5:23:34
 * @author zhg
 */
package familia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import familia.ModelConfig.ModelType;
import familia.util.MinstdRand0;
import xiaogen.util.Logger;

/**
 * 
 */
public class Util
{
	// linux 默认随机数生成 minstd_rand0
	private static  MinstdRand0 rnd = new  MinstdRand0( );//seed 2147483647
	 
	public static void fix_random_seed()
	{ 
//		java.security.SecureRandom r=new SecureRandom();
		rnd .setSeed(Integer.MAX_VALUE);
//		rnd.reset();
	}
	 
	// 返回min~max之间的随机浮点数, 默认返回0~1的浮点数
	public static double rand()
	{ 
		return rnd.nextDouble();
	}

	// 返回[0, k - 1]之间的整型浮点数
	static int rand_k(int k)
	{
		return (int) (rand()*k);
	}

	/**
	 * 读取模型配置
	 * 
	 * @param conf_file
	 * @return
	 */
	public static ModelConfig load_prototxt(String conf_file)
	{
		 Logger.d("读取模型配置:"+conf_file);
		try
		{
			ModelConfig conf = new ModelConfig();
			List<String> lines = Files.readAllLines(Paths.get(conf_file));
			for (String line : lines)
			{
				String _line = line.trim();
				if (!_line.isEmpty())
				{
					String[] kvs = _line.split(":");
					if (kvs.length == 2)
					{
						String k = kvs[0].trim();
						String v = kvs[1].trim();
						if (k.equals("type"))
						{
							conf.type = ModelType.valueOf(v);
						} else if (k.equals("num_topics"))
						{
							conf.num_topics = Integer.parseInt(v);
						} else if (k.equals("alpha"))
						{
							conf.alpha = Float.parseFloat(v);
						} else if (k.equals("beta"))
						{
							conf.beta = Float.parseFloat(v);
						} else if (k.equals("word_topic_file"))
						{
							conf.word_topic_file = v.substring(1, v.length() - 1);
						} else if (k.equals("vocab_file"))
						{
							conf.vocab_file = v.substring(1, v.length() - 1);
						} else if (k.equals("twe_model_file"))
						{
							conf.twe_model_file = v.substring(1, v.length() - 1);
						}
					}
				}
			} 
		  Logger.d("模型信息:"+conf);
			return conf;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	} 
	
}
