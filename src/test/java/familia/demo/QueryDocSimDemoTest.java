/**
 * 创建于2018-05-23 12:15:18
 * @author zhhaogen
 */
package familia.demo;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author zhhaogen
 *
 */
public class QueryDocSimDemoTest
{
 
	@Test
	public void testNewsLda()
	{
		String document1="2018人工智能再次写入政府工作报告，崭新的时代来临，你准备好了吗？";
		String document2="政府工作报告强调了“产业级的人工智能应用”。做大做强新兴产业集群，实施大数据发展行动，加强新一代人工智能研发应用，在医疗、养老、教育、文化、体育等多领域推进“互联网+”。发展智能产业，拓展智能生活。运用新技术、新业态、新模式，大力改造提升传统产业。”";
		QueryDocSimDemo demo = new QueryDocSimDemo(Demo.ModelDir + "news", "lda.conf","news_twe_lda.model" );
		demo.cal_query_doc_similarity( document1,document2);
	}

}
