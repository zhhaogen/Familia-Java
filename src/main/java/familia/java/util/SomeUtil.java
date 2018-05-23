/**
 * 创建于2018-03-26 19:48:06
 * @author zhhaogen
 */
package familia.java.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

/**
 * 一些工具
 * @author zhhaogen
 *
 */
public class SomeUtil implements familia.java.inter.Constant
{ 

	/**
	 * 下载http 文件
	 */
	public static boolean download(String url, File outFile)
	{
		Logger.debug(url + " => " + outFile);
		try (InputStream is = new URL(url).openStream();OutputStream os=new FileOutputStream(outFile))
		{
			IOUtils.copy(is, os);
			Logger.debug( " 下载完成,文件大小:" + outFile.length());
			return true;
		} catch (Exception e)
		{ 
			Logger.error("下载文件错误", e);
			return false;
		}
	
	}

	/**
	 * 解压*.tar.gz文件
	 */
	public static boolean upTarGz(File file, File outDir)
	{ 
		Logger.debug(file + " => " + outDir);
		if (!outDir.exists())
		{
			outDir.mkdirs();
		}  
		try (TarArchiveInputStream ais = new TarArchiveInputStream(
				new GzipCompressorInputStream(new FileInputStream(file)));)
		{ 
			TarArchiveEntry entry = null;
			while ((entry = ais.getNextTarEntry()) != null)
			{  
				Logger.debug(entry.getName());
				if (entry.isDirectory())
				{
					new File(outDir, entry.getName()).mkdirs();
				} else if(entry.isFile())
				{
					byte[] data = new byte[(int) entry.getSize()];
					ais.read(data);
					Files.write(new File(outDir, entry.getName()).toPath(), data, StandardOpenOption.CREATE);
				}
			}
			return true;
		} catch (Exception e)
		{
			Logger.error("解压tar错误 ", e);
			return false;
		}

	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param file
	 */
	public static void deleteFile(File file)
	{
		if (file == null || !file.exists())
		{
			return;
		}
		if (file.isDirectory())
		{
			File[] fs = file.listFiles();
			if (fs != null && fs.length > 0)
			{
				for (File f : fs)
				{
					deleteFile(f);
				}
			}
		}
		file.delete();
	}
}
