/**
 * 创建于2018-05-21 17:10:23
 * @author zhhaogen
 */
package familia.demo;

import java.io.File;

import familia.java.inter.Constant;
import familia.java.util.SomeUtil;

/**
 * @author zhhaogen 下载主题模型文件
 */
public class DownloadModel implements Constant
{
	public static void downModel(String url)
	{
		String fileName = url.substring(url.lastIndexOf("/") + 1); 
		File tarFile = new File(Demo.ModelDir,fileName);
		if(!tarFile.exists())
		{ 
			if(SomeUtil.download(url, tarFile))
			{
				return;
			}
		} 
		String fileDir = fileName.substring(0, fileName.indexOf('.')); 
		File dirFile = new File(Demo.ModelDir,fileDir);
		if(!dirFile.exists())
		{ 
			SomeUtil.upTarGz (tarFile,  new File(Demo.ModelDir));
		} 
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		downModel("http://familia.bj.bcebos.com/models/news.v1.tar.gz");
		downModel("http://familia.bj.bcebos.com/models/webpage.tar.gz");
		downModel("http://familia.bj.bcebos.com/models/novel.tar.gz");
		downModel("http://familia.bj.bcebos.com/models/weibo.tar.gz");
	}

}
