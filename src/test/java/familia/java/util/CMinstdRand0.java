/**
 * 创建于 2017年8月25日 下午12:12:55
 * @author zhg
 */
package familia.java.util;

import java.io.File;

/**
 * 
 */
public class CMinstdRand0
{
	static
	{
		System.load(new File("src/test/jni/x"+System.getProperty("sun.arch.data.model")+"/CMinstdRand0.dll").getAbsolutePath());
	}
	public native void setSeed(long seed) ;
	public native double nextDouble();
}
