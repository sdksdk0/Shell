package shell;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.text.Document;

public class test1 {
	public final static int OS_TYPE_LINUX = 1; // linux操作系统
	public final static int OS_TYPE_WINDOWS = 2; // window操作系统
	private static int fileLevel;

	public static void main(String[] args) throws IOException {
		// getSystemInfo();
		// showCopyRight();
		String userPath = System.getProperty("user.home");
		Scanner sc = new Scanner(System.in);
		// 死循环
		while (true) {
			System.out.println(userPath + ">:");
			String command = sc.nextLine().trim();
			// command是用户输入的命令，这种命令有的会改变userPath的值
			if ("exit".equals(command)) {
				// 推出程序，打断循环
				break;
			} else if ("help".equals(command)) {
				// 使用FileInputStream来读取 help.txt文件
				helpOp();
			} else if ("date".equals(command)) {
				dateOp();
			} else if (command != null && !"".equals(command)
					&& command.startsWith("dir")) {
				// command:dir显示userPath下的内容
				dirOp(userPath, command);
			} else if (command != null && !"".equals(command)
					&& command.startsWith("cat")) {
				catOp(command); // cat绝对路径
			} else if (command != null && !"".equals(command)
					&& command.startsWith("type")) {
				typeOp(command); // 绝对路径
			} else if (command != null && !"".equals(command)
					&& command.startsWith("md")) {
				mdOp(userPath, command); // 相对路径
			} else if (command != null && !"".equals(command)
					&& command.startsWith("ren")) {
				renOp(userPath, command); // 原文件的相对路径名 新文件名 ren/home/a/a.txt
											// /home/a/a/b.txt
			} else if (command != null && !"".equals(command)
					&& command.startsWith("rd")) {
				rdOp(userPath, command); // 目录相对路径名
			} else if (command != null && !"".equals(command)
					&& command.startsWith("del")) {
				delOp(userPath, command); // 文件相对路径名
			} else if (command != null && !"".equals(command)
					&& command.startsWith("copy")) {
				copyOp(userPath, command); // 文件相对路径 绝对路径
			} else if (command != null && !"".equals(command)
					&& command.startsWith("cut")) {
				cutOp(userPath, command); // 原文件的相对路径名 相对路径
			} else if (command != null && !"".equals(command)
					&& command.startsWith("tree")) {
				treeOp(userPath); // 输出当前目录下的所有文件 递归
			} else if (command != null && !"".equals(command)
					&& command.startsWith("cd")) {
				userPath = cdOp(userPath, command); // cd. cd.. cd/ cd 目录 有返回值的
			} else {
				System.out.println("找不到这条命令");
			}
		}
	}

	private static String cdOp(String userPath, String command) {
		// TODO Auto-generated method stub

		if (command.equals("cd")) {
			return userPath;
		}
		if (command.equals("cd .")) {
			return userPath;
		}
		String[] strs = command.split(" ");
		if (strs == null || strs.length != 2) {
			System.out.println("命令格式错误，正确格式是：cd 路径表示法");
			return userPath;
		}
		File file = new File(userPath); // 当前路径
		if (command.equals("cd ..")) {
			if(file.getParent()!=null){   //判断父路径是否为空
				return file.getParent();
			}
			
		}
		if (command.equals("cd /")) {
			return getRoot(userPath); 
		}
		File f = new File(strs[1]);
		if (f.exists() && f.isDirectory()) {
			return f.getAbsolutePath();
		}
		System.out.println(strs[1] + "路径异常");
		return userPath;
	}

	private static String getRoot(String userPath) {
		int system_type = getSystemInfo();
		if (system_type == OS_TYPE_LINUX) {
			return "/";
		} else {
			int last = userPath.indexOf(":\\" + 2);
			String path = userPath.substring(0, last);
			return path;
		}
	}


	private static void copyOp(String userPath, String command) {
		String[] strs = command.split(" ");
		// 读取command格式是否正确
		if (strs == null || strs.length != 3) {
			System.out.println(command
					+ "格式，标准格式：copy   原文件的相对路径  新文件的绝对路径名，请确认后重新输入");
			return;
		}
		// 创建一个file表示原文件 new File(userPath,新文件相对路径)
		File old = new File(userPath, strs[1]);
		// 判断原文件是否存在，不存在则返回
		if (old.exists() == false) {
			System.out.println(old.getAbsolutePath() + "不存在，请确认后输入");
			return;
		}
		// 判断新文件是否存在，若存在，则返回
		File newfile = new File(userPath, strs[2]);
		if (newfile.exists()) {
			System.out.println(newfile.getAbsolutePath() + "已存在，不能复制");
			return;
		}
		// 判断原文件和新文件是否重复
		if (old.equals(newfile)) {
			System.out.println("目录文件夹是源文件夹的子文件夹");
		} else {
			copy(old, newfile);
		}

	}

	private static void copy(File old, File newfile) {
		// TODO Auto-generated method stub
		
		try (InputStream is = new FileInputStream(old);
				OutputStream os = new FileOutputStream(newfile);) {
			byte[] b = new byte[1024];
			int len;
			while ((len = is.read(b)) != -1) {
				os.write(b);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("复制成功");
	}


	private static void cutOp(String userPath, String command) {
		// TODO Auto-generated method stub
		
		copyOp(userPath, command);     //
		String[] strs = command.split(" ");
		File old = new File(userPath, strs[1]);
		old.delete();
		System.out.println("剪切成功");
	}

	/*
	 * public String createPrintStr(String name, int level) { // 输出的前缀 String
	 * printStr = ""; // 按层次进行缩进 for (int i = 0; i < level; i++) { printStr =
	 * printStr + " "; } printStr = printStr + "- " + name; return printStr; }
	 */

	/**
	 * 输出初始给定的目录
	 * 
	 * @param dirPath
	 *            给定的目录
	 */

	private static void treeOp(String userPath) {

		tree(userPath, 1);

		// TODO Auto-generated method stub
		/*
		 * String[] contents = command.split(" "); String filePath = null; if
		 * (contents.length == 2) { // 如果长度为2，就显示userPath下的内容 filePath =
		 * contents[1]; } File f = new File(filePath);// 定义文件路径 printLev(f, 0);
		 */

	}

	// 默写：递归调用
	private static void tree(String path, int level) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < level; i++) {
			sb.append("      ");
		}
		File f = new File(path);
		File[] fs = f.listFiles();
		System.out.println(sb.toString() + f.getName());
		if (fs != null && fs.length > 0) {
			for (File file : fs) {
				if (file.isFile()) {
					System.out
							.println(sb.toString() + "     " + file.getName());
				} else {
					tree(file.getAbsolutePath(), level + 1);
				}
			}
		}
	}

	/*
	 * private static void printLev(File f, int level) { // TODO Auto-generated
	 * method stub File[] subFiles = f.listFiles(); for (File file : subFiles) {
	 * for (int i = 0; i <= level; i++) { System.out.print("     ");
	 * 
	 * } System.out.println(file); if (file.isDirectory()) { printLev(file,
	 * level + 1);
	 * 
	 * } } }
	 */

	private static void delOp(String userPath, String command) {
		// TODO Auto-generated method stub
		// 删除文件
		String[] strs = command.split(" ");
		// 读取command格式是否正确
		if (strs == null || strs.length != 2) {
			System.out.println(command + "格式，标准格式：del   文件的相对路径，请确认后重新输入");
			return;
		}
		// 创建一个文件 new File(userPath,strs[1]）;
		File f = new File(userPath, strs[1]); // 定义文件路径
		// 判断是否存在，是否为目录
		if (f.exists() == false) {
			System.out.println("文件不存在");
			return;
		}
		if (f.isFile() == false) {
			System.out.println("不是文件，不能删除");
			return;
		}
		// 判断是否文件，是则删除
		boolean result = f.delete();
		// 显示删除成功
		System.out.println(result ? "删除" + f.getName() + "成功" : "删除"
				+ f.getName() + "失败");

		/*
		 * String filePath = null; if (contents.length == 2) { //
		 * 如果长度为2，就显示userPath下的内容 filePath = contents[1]; } File dir = new
		 * File(filePath);// 定义文件路径 if (dir.isFile()) { dir.delete();
		 * System.out.println("已成功删除文件"); } else { System.out.println("文件不存在");
		 * }
		 */

	}

	private static void rdOp(String userPath, String command) {
		// TODO Auto-generated method stub
		// 删目录
		String[] strs = command.split(" ");
		// 读取command格式是否正确
		if (strs == null || strs.length != 2) {
			System.out.println(command + "格式，标准格式：rd   目录的相对路径，请确认后重新输入");
			return;
		}
		// 创建一个文件 new File(userPath,strs[1]）;
		File f = new File(userPath, strs[1]);
		// 判断是否存在，是否为目录
		if (f.exists() == false) {
			System.out.println(f.getAbsolutePath() + "不存在，无法删除");
			return;
		}
		// 判断这个目录是否为空目录，空目录才可以删除
		File[] fs = f.listFiles();
		if (fs != null && fs.length > 0) {
			System.out.println(f.getAbsolutePath() + "不是空目录，不能删除");
			return;
		}
		// 若是空目录，则删除
		boolean result = f.delete();

		// 显示删除成功
		System.out.println(result ? "删除" + f.getName() + "成功" : "删除"
				+ f.getName() + "失败");

		/*
		 * String[] contents = command.split(" "); String filePath = null; if
		 * (contents.length == 2) { // 如果长度为2，就显示userPath下的内容 filePath =
		 * contents[1]; } File dir = new File(filePath);// 定义文件路径
		 * deleteFile(dir);
		 */
	}

	/*
	 * private static void deleteFile(File dir) { // TODO Auto-generated method
	 * stub //
	 * 
	 * 
	 * File[] subFiles = dir.listFiles(); for (File file : subFiles) { if
	 * (file.isFile()) { file.delete(); System.out.println("已成功删除文件"); } else {
	 * deleteFile(file); } } dir.delete(); System.out.println("已成功删除文件夹"); }
	 */
	private static void renOp(String userPath, String command) {
		// TODO Auto-generated method stub
		// File类的renameTo()
		String[] strs = command.split(" ");
		// 读取command格式是否正确
		if (strs == null || strs.length != 3) {
			System.out.println(command
					+ "格式，标准格式：ren  原文件的相对路径    新文件相对路径名，请确认后重新输入");
			return;
		}

		// 创建一个file表示原文件 new File(userPath,新文件相对路径)
		File old = new File(userPath, strs[1]);
		// 判断原文件是否存在，不存在则返回
		if (old.exists() == false) {
			System.out.println(old.getAbsolutePath() + "不存在，请确认后输入");
			return;
		}

		// 判断新文件是否存在，若存在，则返回
		File newfile = new File(userPath, strs[2]);
		if (newfile.exists()) {
			System.out.println(newfile.getAbsolutePath() + "已存在，不能重名");
			return;
		}
		// 将原文件改名为新文件
		old.renameTo(newfile);
		System.out.println("重命名成功");
		System.out.println();

	}

	private static void mdOp(String userPath, String command) {
		// 在userPath下面创建一个目录 md/a/b/c md/a
		// File类中的mkdirs()
		String[] strs = command.split(" ");

		if (strs == null || strs.length != 2) {
			System.out.println(command + "格式，标准格式：md  文件的相对路径，请确认后重新输入");
			return;
		}
		File f = new File(userPath, strs[1]);
		if (f.exists()) {
			System.out.println("文件已存在，无需创建");
			return;
		}
		// 创建目录
		System.out.println(f.mkdir() ? "创建目录成功" : "创建目录失败");
		System.out.println();

		/*
		 * String[] contents = command.split(" "); String filePath = null; if
		 * (contents.length == 2) { // 如果长度为2，就显示userPath下的内容 filePath =
		 * contents[1]; } // y用file类来完成取到这个目录的信息 File file = new File(filePath);
		 * if (!file.exists() && !file.isDirectory()) { file.mkdirs();
		 * System.out.println("已成功创建文件夹"); } else {
		 * System.out.println("目录已存在，无需创建"); }
		 */

	}

	private static void typeOp(String command) throws IOException {
		// TODO Auto-generated method stub
		// 使用BuffedInputStream来完成
		// 解析command中的文件
		String[] strs = command.split(" ");

		if (strs == null || strs.length != 2) {
			System.out.println(command + "格式，标准格式：type 文件的绝对路径，请确认后重新输入");
			return;
		}
		File file = new File(strs[1]);
		if (file.exists() == false) {
			System.out.println(file.getAbsolutePath() + "文件不存在");
			return;
		}
		if (file.isFile() == false) {
			System.out.println(file.getAbsolutePath() + "不是一个可以读取的文件");
			return;
		}
		InputStream iis = null;

		try {
			iis = new BufferedInputStream(new FileInputStream(file));
			byte[] bs = new byte[1024];
			int length = -1;
			while ((length = iis.read(bs, 0, bs.length)) != -1) {
				String s = new String(bs, 0, length, "gbk");
				System.out.println(s);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				iis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// 读command中带的文件
	private static void catOp(String command) {

		// 解析command中的文件
		String[] contents = command.split(" ");
		String filePath = null;
		if (contents.length == 2) {
			// 如果长度为2，就显示userPath下的内容
			filePath = contents[1];
		}
		// y用file类来完成取到这个目录的信息
		File f = new File(filePath);
		if (f.exists() == false || f.isFile() == false) {
			System.out.println(f.getName() + "不是一个有效文件，无法读取");
			return;
		}
		FileReader fr = null;
		try {
			fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			String line = null;
			int num = 0;

			while ((line = br.readLine()) != null) {
				num++;
				System.out.println(num + "\t" + line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void dirOp(String userPath, String command) {
		String[] contents = command.split(" ");
		if (contents.length == 2) {
			// 如果长度为2，就显示userPath下的内容
			userPath = contents[1];
		}
		// y用file类来完成取到这个目录的信息
		File f = new File(userPath);
		File[] fs = f.listFiles();

		int totalFile = 0;
		int totalDir = 0;
		long totalFileSize = 0;
		if (fs != null && fs.length > 0) {
			for (File file : fs) {
				long time = file.lastModified();
				Date d = new Date();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-M-d HH:mm");
				String timeString = sd.format(time);

				// 权限
				String read = file.canRead() ? "r" : "-";
				String write = file.canWrite() ? "w" : "-";
				String execute = file.canExecute() ? "x" : "-";

				// 文件还是目录
				String fileorDir = file.isFile() ? "\t" : "<dir>";
				// 取大小
				long fileSize = file.isFile() ? file.length() : 0;
				String fileSizeString = "\t";
				if (file.isFile()) {
					fileSizeString = getSizeInPrety(fileSize);
					totalFile++;
					totalFileSize += fileSize;
				} else {
					totalDir++;
				}
				System.out.println(timeString + "\t\t" + read + write + execute
						+ "\t" + fileorDir + "\t" + fileSizeString
						+ file.getName());
			}
		}
		System.out.println("总共有:" + totalFile + "个文件," + totalDir + "个目录");
	}

	private static void dateOp() {
		// TODO Auto-generated method stub
		Date d = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-M-d HH:mm:ss E");
		String s = sd.format(d);// 这个方法继承于SimpleDateFormat的父类DateFormat类！
		System.out.println(s);
	}

	private static void helpOp() throws IOException {
		// 使用FileInputStream来读取 help.txt文件

		// 通过test1的字节码类，找到它的字节码加载器，这个加载器从bin目录开始扫描，查找help.txt文件，再自动以流的方式加载它
		InputStream fis = test1.class.getClassLoader().getResourceAsStream(
				"help.txt");

		// File filename=new
		// File(System.getProperty("java.class.path")+File.separator+"help.txt");
		// FileInputStream fis = new FileInputStream(filename);

		// 第三步：操作！
		byte[] buff = new byte[1024];
		int len = -1;// 定义缓冲区
		while ((len = fis.read(buff, 0, buff.length)) != -1) {
			String s = new String(buff, 0, len, "gbk");
			System.out.println(s);
		}
		// 第四步：关闭资源(字符流必须关闭资源，因为它中间有缓冲区！对于字节流可以不用关闭，但是还是建议写上，习惯！)
		fis.close();

	}

	// 区分操作系统
	private static int getSystemInfo() {
		// Properties就是一个键值对
		// Properties p=System.getProperties(); //System类当中存有当前系统的所有信息
		// Set<Entry<Object,Object>> set=p.entrySet(); //entry：键值对 Set:集合
		// Iterator<Entry<Object,Object>> its=set.iterator(); //迭代器
		// while(its.hasNext()){ //使用迭代器取出集合中的第一个元素，hasNext()返回true
		// Entry<Object,Object> entry=its.next(); //取出
		// System.out.println(entry.getKey()+":"+entry.getValue());
		// }

		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("linux") >= 0) {
			return OS_TYPE_LINUX;
		} else if (osName.toLowerCase().indexOf("windows") >= 0) {
			return OS_TYPE_WINDOWS;
		} else {
			return -1;
		}
	}

	// 显示版权
	private static void showCopyRight() {
		int ostype = getSystemInfo();
		if (ostype == OS_TYPE_LINUX) {
			System.out.println("ubuntu linux zp");
		} else if (ostype == OS_TYPE_WINDOWS) {
			System.out.println("Micrsoft windows zp");
			System.out.println("Copyright by (2016-2028)");
		}

		System.out.println("当前系统的盘符:");
		File[] fs = File.listRoots();
		System.out.println("盘符名\t总大小\t剩余空间:");
		for (File file : fs) {
			System.out.println(file.getAbsolutePath() + "\t"
					+ getSizeInPrety(file.getTotalSpace()) + "\t"
					+ getSizeInPrety(file.getFreeSpace()));
		}

	}

	private static String getSizeInPrety(long size) {
		if (size / 1024 / 1024 / 1024 / 1024 > 0) {
			return size / 1024 / 1024 / 1024 / 1024 + "T";
		} else if (size / 1024 / 1024 / 1024 > 0) {
			return size / 1024 / 1024 / 1024 + "G";
		} else if (size / 1024 / 1024 > 0) {
			return size / 1024 / 1024 + "M";
		} else if (size / 1024 > 0) {
			return size / 1024 + "K";
		} else {
			return size + "B";
		}
	}
}
