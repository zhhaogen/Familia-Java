/**
 * 创建于 2017年8月22日 下午8:26:24
 * @author zhg
 */
package familia.demo;

import java.util.List;
import java.util.Scanner;

import familia.java.inter.Constant;

/**
 * 例子基础类
 */
public abstract class Demo implements Constant
{
	// 打印分词结果
	public	void print_tokens(String title, List<?> tokens)
	{
		System.out.println(title);
		System.out.println(tokens);
	}
	/**
	 * 存放model文件夹
	 */
//	public static final	String	ModelDir=	new File(FileSystemView.getFileSystemView().getHomeDirectory(), "Familia/model").getPath();
	public static final String ModelDir=	"D:\\Desktop\\Familia\\model\\";
	/**
	 * 获取控制台输入
	 * @param action
	 */
	public static	void getConsole(java.util.function.Consumer<Scanner> action) 
	{
		try(Scanner scanner=new Scanner(System.in)) {
			if(action!=null)
			{
				while(true)
				{
					action.accept(scanner);
				} 
			}
		}
	}
}
