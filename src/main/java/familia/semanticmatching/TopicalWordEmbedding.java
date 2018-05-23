/**
 * 创建于 2017年8月14日 下午9:04:43
 * @author zhg
 */
package familia.semanticmatching;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

import familia.java.inter.Constant;
 

/**
 * Topical Word Embedding (TWE) 模型类 包括了模型的加载及embedding的获取
 */
public class TopicalWordEmbedding implements Constant
{
	// word embedding
	Map<String, Embedding> _word_emb;
	// topic embedding
	List<Embedding> _topic_emb;
	// num of topics
	int _num_topics;
	// TWE模型embeeding size
	int _emb_size;
	// TWE中word embedding的词表大小
	int _vocab_size;

	/**
	 * @param model_dir
	 * @param emb_file
	 */
	public TopicalWordEmbedding(String model_dir, String emb_file)
	{
		String emb_path = model_dir + "/" + emb_file;

		load_emb(emb_path);
	}

	// 加载Topical Word Embedding
	int load_emb(String emb_file)
	{
		Logger.debug("加载TWE模型:" + emb_file); 
		try (BufferedInputStream br = new BufferedInputStream(new FileInputStream(emb_file));)
		{
			String line = readLine(br);
			String[] items = line.split(" ");
			_vocab_size = Integer.parseInt(items[0]);
			_num_topics = Integer.parseInt(items[1]);
			_emb_size = Integer.parseInt(items[2]);
			_topic_emb = new ArrayList<>(_num_topics);
			_word_emb = new TreeMap<>();
			Logger.debug("#vocab_size=" + _vocab_size + "," + "#num_topics=" + _num_topics + "," + "#emb_size=" + _emb_size);
			int total_num = _vocab_size + _num_topics;
			int MAX_TOKEN_LENGTH = 50;
			for (int i = 0; i < total_num; i++)
			{
				String term = readString(br, MAX_TOKEN_LENGTH);
				Embedding eleme = new Embedding(_emb_size);
				readFloat(br, _emb_size, eleme);
				if (i < _vocab_size)
				{
					_word_emb.put(term, eleme);
				} else
				{
					_topic_emb.add(eleme);
				}
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		Logger.debug("加载twe文件完成");
		return 0;
	}

	/**
	 * http://www.iteye.com/problems/42004
	 * 
	 * @param is
	 * @param maxSize
	 * @return
	 * @throws IOException
	 */
	private void readFloat(InputStream is, int maxSize, Embedding list) throws IOException
	{
		// List<Float> list=new ArrayList<>();
	 
		for(int count=0;count<maxSize;count++)
		{
			int ch4 = is.read();
			int ch3 = is.read();
			int ch2 = is.read();
			int ch1 = is.read();
			if ((ch1 | ch2 | ch3 | ch4) < 0)
			{
				// throw new EOFException();
				break;
			}
			int i = ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
			float b = Float.intBitsToFloat(i); 
			if(list!=null)
			{	
				list.set(count,b);
//				list.add(b); 
			}
		
		} 
	}

	/**
	 * @param is
	 * @param maxSize
	 * @return
	 * @throws IOException
	 */
	private String readString(InputStream is, int maxSize) throws IOException
	{
		ByteArrayOutputStream bot = new ByteArrayOutputStream();
		int b = 0;
		int count = 0;
		boolean skip = false;
		while (count < maxSize)
		{
			b = is.read();
			if (b == -1)
			{
				break;
			}
			if (!skip)
			{
				if (b == '\0')
				{
					skip = true;
				} else
				{
					bot.write(b);
				}
			}
			count++;
		}
		return new String(bot.toByteArray(), "utf-8");
	}

	/**
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private String readLine(InputStream is) throws IOException
	{
		ByteArrayOutputStream bot = new ByteArrayOutputStream();
		int b = 0;
		while (true)
		{
			b = is.read();
			if (b == -1 || b == '\n')
			{
				break;
			}
			bot.write(b);
		}
		return new String(bot.toByteArray());
	}

	// 根据topic id返回topic的embedding
	Embedding topic_emb(int topic_id)
	{
		return _topic_emb.get(topic_id);
	}

	// 根据明文返回词的embedding
	Embedding word_emb(String term)
	{
		return _word_emb.get(term);
	}

	// 返回距离词最近的K个词
	public void nearest_words(String word, List<WordAndDis> items)
	{
		Embedding target_word_emb = _word_emb.get(word);
		int num_k = items.size();
		_word_emb.forEach((first, second) -> {
			if (first.equals(word))
			{
				return;
			}
			float dist = SemanticMatching.cosine_similarity(target_word_emb, second);
			for (int i = 0; i < num_k; i++)
			{
				if (dist > items.get(i).distance)
				{
					for (int j = num_k - 1; j > i; j--)
					{
						items.set(j, items.get(j - 1));
					}
					WordAndDis item=new WordAndDis();
					item.word=first;
					item.distance=dist;
					items.set(i, item); 
					break;
				}
			}
		});
	}

	// 返回离主题最近的K个词
	public List<WordAndDis> nearest_words_around_topic(int topic_id, int num_k)
	{
		Embedding target_topic_emb = _topic_emb.get(topic_id);

		ConcurrentSkipListSet<WordAndDis> items = new ConcurrentSkipListSet<>((w1, w2) -> {
			float def = w2.distance - w1.distance;
			if (def == 0)
			{
				return 0;
			}
			if (def < 0)
			{
				return -1;
			}
			return 1;
		});
		_word_emb.forEach((first, second) -> {
			float dist = SemanticMatching.cosine_similarity(target_topic_emb, second);
			WordAndDis item = new WordAndDis();
			item.word = first;
			item.distance = dist;
			items.add(item);
			if (items.size() > num_k)
			{
				items.pollLast();
			}
		});
		return new ArrayList<>(items);
	}

	// 检查当前词是否在TWE模型中
	public boolean contains_word(String term)
	{
		return _word_emb.containsKey(term);
	}

	// 返回主题数
	public int num_topics()
	{
		return _num_topics;
	}
}
