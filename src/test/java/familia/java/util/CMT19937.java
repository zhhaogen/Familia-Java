/**
 * 创建于 2017年8月25日 上午10:34:05
 * @author zhg
 */
package familia.java.util;

import java.io.File;

/**
 * 
 */
public class CMT19937
{
	static
	{
		System.load(new File("src/test/jni/x"+System.getProperty("sun.arch.data.model")+"/CMT19937.dll").getAbsolutePath());
	}
	public native void setSeed(long seed) ;
	public native double nextDouble();
	public   float nextFloat(){
		return (float) nextDouble();
	}
}
