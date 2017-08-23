/**
 * 创建于 2017年8月14日 下午5:24:09
 * @author zhg
 */
package familia;

/**
 * 
 */
public class ModelConfig
{
	public static enum ModelType
	{
		LDA, SLDA

	}

	ModelType type;
	public int num_topics;
	float alpha;
	float beta;
	public String word_topic_file;
	public String vocab_file;
	String twe_model_file;
	@Override
	public String toString()
	{
		return "{num_topics:"+num_topics+",type:"+type+",alpha:"+alpha+",beta:"+beta+",word_topic_file:"+word_topic_file+",twe_model_file:"+twe_model_file+"}";
	}
	
}
