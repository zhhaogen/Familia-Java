/**
 * 创建于2018-05-21 12:37:57
 * @author zhhaogen
 */
package familia.demo;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author zhhaogen
 *
 */
public class DocDistanceDemoTest
{
	@Test
	public void testNewsLda()
	{
		String doc_text1 = "比如，中国从美国进口油气，既是我国天然气缺口的有益补充，又有助于我国获取相关开发经验，提升在全球天然气市场的议价权。“十三五”规划和十九大报告中均指出，要推进能源革命，构建清洁低碳、安全高效的能源体系。对美方而言，美国可借助这一对华大宗商品出口，降低对华贸易逆差，同时刺激就业，助力经济发展。此外，中美两国在可再生能源和提高能源效率与利用方面开展了大量合作，未来在全球向清洁能源转型进程中将发挥核心作用。";
		String doc_text2 = "中国扩大进口，并非是应对贸易摩擦的权宜之计，更不是迫于外界压力，而是推动国家长远发展、满足人民美好生活的市场行为和时代选择。随着中等收入阶层的不断扩大，中国将成为全球最大的市场。中国不但从美国买东西，也从全世界广泛进口不同类型的产品。今年11月，中国将举办第一届进口博览会，迄今已有80多个国家报名参加。很多人想把东西卖给中国，使得中国市场变得有高度竞争性。如果想要在中国获得一定的市场份额，本身还要让中国人民高兴。如果中国人民不买，不管提什么要求，都是没用的。";
		new DocDistanceDemo(Demo.ModelDir + "news", "lda.conf").cal_doc_distance(doc_text1, doc_text2);
	}
	@Test
	public void testNewsSLda()
	{
		String doc_text1 = "比如，中国从美国进口油气，既是我国天然气缺口的有益补充，又有助于我国获取相关开发经验，提升在全球天然气市场的议价权。“十三五”规划和十九大报告中均指出，要推进能源革命，构建清洁低碳、安全高效的能源体系。对美方而言，美国可借助这一对华大宗商品出口，降低对华贸易逆差，同时刺激就业，助力经济发展。此外，中美两国在可再生能源和提高能源效率与利用方面开展了大量合作，未来在全球向清洁能源转型进程中将发挥核心作用。";
		String doc_text2 = "中国扩大进口，并非是应对贸易摩擦的权宜之计，更不是迫于外界压力，而是推动国家长远发展、满足人民美好生活的市场行为和时代选择。随着中等收入阶层的不断扩大，中国将成为全球最大的市场。中国不但从美国买东西，也从全世界广泛进口不同类型的产品。今年11月，中国将举办第一届进口博览会，迄今已有80多个国家报名参加。很多人想把东西卖给中国，使得中国市场变得有高度竞争性。如果想要在中国获得一定的市场份额，本身还要让中国人民高兴。如果中国人民不买，不管提什么要求，都是没用的。";
		new DocDistanceDemo(Demo.ModelDir + "news", "slda.conf").cal_doc_distance(doc_text1, doc_text2);
	}
	@Test
	public void testNovelLda()
	{
		String doc_text1 = "今天，是他军转干之后，正式上任镇长的第二天。他是前天下午在景林县县委组织部的一个排名最末的副部长李有福的陪同下来到关山镇的。当天晚上在镇里领导陪同下吃了晚饭之后，李有福便连夜赶回县里了";
		String doc_text2 = "此刻，是上午10点钟，柳擎宇已经在办公室里面坐了2个多小时了，然而，在过去的一天一夜外加2个小时的时间内，镇里面没有一个人来他这里汇报工作，更没有任何文件和资料传递到他这里。他好像被整个关山镇给遗忘了一般，又有像是透明人，被人完全忽略掉了";
		new DocDistanceDemo(Demo.ModelDir + "novel", "lda.conf").cal_doc_distance(doc_text1, doc_text2);
	}
	@Test
	public void testNovelSLda()
	{
		String doc_text1 = "今天，是他军转干之后，正式上任镇长的第二天。他是前天下午在景林县县委组织部的一个排名最末的副部长李有福的陪同下来到关山镇的。当天晚上在镇里领导陪同下吃了晚饭之后，李有福便连夜赶回县里了";
		String doc_text2 = "此刻，是上午10点钟，柳擎宇已经在办公室里面坐了2个多小时了，然而，在过去的一天一夜外加2个小时的时间内，镇里面没有一个人来他这里汇报工作，更没有任何文件和资料传递到他这里。他好像被整个关山镇给遗忘了一般，又有像是透明人，被人完全忽略掉了";
		new DocDistanceDemo(Demo.ModelDir + "novel", "slda.conf").cal_doc_distance(doc_text1, doc_text2);
	}
	@Test
	public void testWebpageLda()
	{
		String doc_text1 = ".对中华民族的英雄，要心怀崇敬，浓墨重彩记录英雄、塑造英雄，让英雄在文艺作品中得到传扬，引导人民树立正确的历史观、民族观、国家观、文化观，绝不做亵渎祖先、亵渎经典、亵渎英雄的事情";
		String doc_text2 = "理想之光不灭，信念之光不灭。我们一定要铭记烈士们的遗愿，永志不忘他们为之流血牺牲的伟大理想";
		new DocDistanceDemo(Demo.ModelDir + "webpage", "lda.conf").cal_doc_distance(doc_text1, doc_text2);
	}
	@Test
	public void testWebpageSLda()
	{
		String doc_text1 = ".对中华民族的英雄，要心怀崇敬，浓墨重彩记录英雄、塑造英雄，让英雄在文艺作品中得到传扬，引导人民树立正确的历史观、民族观、国家观、文化观，绝不做亵渎祖先、亵渎经典、亵渎英雄的事情";
		String doc_text2 = "理想之光不灭，信念之光不灭。我们一定要铭记烈士们的遗愿，永志不忘他们为之流血牺牲的伟大理想";
		new DocDistanceDemo(Demo.ModelDir + "webpage", "slda.conf").cal_doc_distance(doc_text1, doc_text2);
	}
	@Test
	public void testWeiboSLda()
	{
		String doc_text1 = "恭喜RNG！全华班！华人的骄傲！狗爷的总冠军！顺便祝大家521快乐[并不简单] ​​​​";
		String doc_text2 = "冠军冠军🏆 也属于你@Zz1tai姿态也请大家提起2018MSI冠军 想起的是我们七位队员 ​​​​";
		new DocDistanceDemo(Demo.ModelDir + "weibo", "slda.conf").cal_doc_distance(doc_text1, doc_text2);
	}
}
