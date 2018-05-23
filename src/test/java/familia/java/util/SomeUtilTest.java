/**
 * 创建于2018-03-26 19:50:22
 * @author zhhaogen
 */
package familia.java.util;

import static familia.java.util.SomeUtil.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import familia.demo.Demo;

/**
 * @author zhhaogen
 *
 */
public class SomeUtilTest  implements familia.java.inter.Constant
{ 
	private File tmpDir;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		Logger.debug("创建临时文件夹");
		tmpDir = new File(System.getProperty("java.io.tmpdir"), "familiaforjava"+"-"+System.currentTimeMillis());
		tmpDir.mkdirs();
	}

	@Test
	public void testUpTarGz() throws IOException
	{
		File file = new File(tmpDir, "glog-0.3.4.tar.gz");  
		try (FileOutputStream os = new FileOutputStream(file);
				InputStream is = new FileInputStream(Demo.ModelDir+"/weibo.tar.gz");)
		{
			Logger.debug("input :" + is);
			assertNotNull(is);
			IOUtils.copy(is, os); 
		}
		assertTrue(upTarGz(file, tmpDir));
	}
	@Test
	public void testDownload() 	
	{
		File file = new File(tmpDir, "protobuf-2.5.0.tar.gz");  
		assertTrue(download("https://raw.githubusercontent.com/ZeyuChen/third_party/master/package/protobuf-2.5.0.tar.gz",file));
	}
	@After
	public void tearDown() throws Exception
	{
		Logger.debug("删除临时文件夹:" + tmpDir);
		deleteFile(tmpDir);
	}
}
