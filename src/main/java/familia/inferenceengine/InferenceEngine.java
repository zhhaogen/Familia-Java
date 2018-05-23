/**
 * 创建于 2017年8月15日 下午8:43:28
 * @author zhg
 */
package familia.inferenceengine;

import java.util.ArrayList;
import java.util.List;

import familia.Config.*;
import familia.document.*;
import familia.model.*;
import familia.sampler.*;
import static familia.util.Util.*;
/**
 * 
 */
public class InferenceEngine implements familia.java.inter.Constant
{
	  

	// 模型结构指针
	TopicModel _model;
	// 采样器指针, 作用域仅在InferenceEngine
	Sampler _sampler;

	public InferenceEngine(String model_dir, String conf_file)
	{
		this(model_dir, conf_file, SamplerType.MetropolisHastings);
	}

	public InferenceEngine(String model_dir, String conf_file, SamplerType type)
	{
		ModelConfig config = load_prototxt(model_dir + "/" + conf_file);
		_model = new TopicModel(model_dir, config); 
		// 根据配置初始化采样器
		if (type == SamplerType.GibbsSampling)
		{
			Logger.debug("Use GibbsSamling.");
			_sampler = new GibbsSampler(_model);
		} else if (type == SamplerType.MetropolisHastings)
		{
			Logger.debug("Use MetropolisHastings.");
			_sampler = new MHSampler(_model);
		}
	}

	// 对input的输入进行LDA主题推断，输出结果存放在doc中
	// 其中input是分词后字符串的集合
	public int infer(List<String> input, LDADoc doc)
	{ 
		 fix_random_seed(); // 固定随机数种子, 保证同样输入下推断的的主题分布稳定 
		doc.init(_model._num_topics);
		doc.set_alpha(_model._alpha);  
		for (String token : input)
		{
			int id = _model.term_id(token);
			if (id != OOV)
			{
				int init_topic = rand_k(_model._num_topics);  
				Token tk = new Token();
				tk.id = id;
				tk.topic = init_topic; 
				doc.add_token(tk);
			}
		}   
		lda_infer(doc, 20, 50);   
		return 0;

	}

	// 对input的输入进行SentenceLDA主题推断，输出结果存放在doc中
	// 其中input是句子的集合
	public int infer(List<List<String>> input, SLDADoc doc)
	{
		 fix_random_seed(); // 固定随机数种子, 保证同样输入下推断的的主题分布稳定
		doc.init(_model._num_topics);
		doc.set_alpha(_model._alpha);
		for (List<String> sent : input)
		{
			List<Integer> words = new ArrayList<>();
			for (String token : sent)
			{
				int id = _model.term_id(token);
				if (id != OOV)
				{
					words.add(id);
				}
			}
			// 随机初始化
			int init_topic = rand_k(_model._num_topics); 
			Sentence et = new Sentence();
			et.topic = init_topic;
			et.tokens = words;
			doc.add_sentence(et);
		}

		slda_infer(doc, 20, 50);

		return 0;
	}

	// REQUIRE: 总轮数需要大于burn-in迭代轮数, 其中总轮数越大，得到的文档主题分布越平滑
	void lda_infer(LDADoc doc, int burn_in_iter, int total_iter)
	{
		for (int iter = 0; iter < total_iter; ++iter)
		{  
			_sampler.sample_doc(doc);  
			if (iter >= burn_in_iter)
			{
				// 经过burn-in阶段后, 对每轮采样的结果进行累积，以得到更平滑的分布
				doc.accumulate_topic_sum();
			}
		}
	}

	// REQUIRE: 总轮数需要大于burn-in迭代轮数, 其中总轮数越大，得到的文档主题分布越平滑
	void slda_infer(SLDADoc doc, int burn_in_iter, int total_iter)
	{
		for (int iter = 0; iter < total_iter; ++iter)
		{
			_sampler.sample_doc(doc);
			if (iter >= burn_in_iter)
			{
				// 经过burn-in阶段后，对每轮采样的结果进行累积，以得到更平滑的分布
				doc.accumulate_topic_sum();
			}
		}
	}

	// 返回模型指针以便获取模型参数
	public TopicModel get_model()
	{
		return _model;
	}

	// 返回模型类型, 指明为LDA还是SetennceLDA
	public ModelType model_type()
	{
		return _model._type;
	}
}
