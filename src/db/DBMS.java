package db;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
/***
 *
 *
 * @Author: CoffeeChicken
 * @Date: 2020/12/25 16:27
 * @Usage:
 *
 * 数据库主类
 *
 */
public class DBMS {
	public static final String ROOTPATH = "." + File.separator + "database";//根目录
	public static String currentPath;//当前目录
	public static DataDictionary dataDictionary = null;//数据库字典
	public static Map<String, Table> loadedTables;//已加载的关系表
	public static Map<String, Field> indexEntry;//索引集合
	
	public DBMS() {
		DBMS.loadedTables = new HashMap<String, Table>();
		DBMS.indexEntry = new HashMap<String, Field>();
		DBMS.currentPath = ROOTPATH;
	}
	/**
	 * 输入指令
	 * @throws Exception 
	 */
	public void start() throws Exception {
		System.out.println("-----------------------------");
		System.out.println("|      数据库实验			    |");
		System.out.println("|       java	            |");
		System.out.println("|     计科18-4王正鑫          |");
		System.out.println("-----------------------------");
		Scanner scan = new Scanner(System.in);
		String account = null;
		while (true) {
			//从控制台接收一条命令
			account = OperUtil.input(scan);
			//处理命令
			new Handler(account).start();
		}
	}
	/**
	 * 启动程序
	 * @param args
	 */
	public static void main(String[] args) {	
		try {
			//主程序入口
			new DBMS().start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
	}

}
