/**
 * 创建于 2017年8月16日 下午3:54:55
 * @author zhg
 */
package familia.sampler;

import familia.document.*; 

/**
 * 采样器的接口
 */
public interface Sampler
{
	  // 对文档进行LDA主题采样
      void sample_doc(LDADoc  doc) ;

    // 对文档进行SentenceLDA主题采样
      void sample_doc(SLDADoc  doc)  ;

	 
}
