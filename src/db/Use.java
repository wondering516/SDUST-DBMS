package db;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 *
 * @Author: CoffeeChicken
 * @Date: 2020/12/25 16:14
 *
*/


public class Use implements Operate{
	//输入的SQL语句
	private String account = null;
	private String databaseName = null;
	
	public Use(String account) {
		this.account = account;

	}

	@Override
	public void start() throws Exception {
		//调用ParseAccount类中解析方法解析出当前使用的数据库名
		this.databaseName = ParseAccount.parseUse(this.account);
		this.use();
		
	}

	private void use() {
		//持久化关系表结构定义文件和数据库结构定义文件
		OperUtil.persistence();
		//初始化关系表
		DBMS.loadedTables = new HashMap<String, Table>();
		//判断需使用的文件夹是否存在
		File databaseDir = new File(DBMS.ROOTPATH, this.databaseName);
		if (!databaseDir.exists()) {
			System.out.println("此数据库不存在，请先创建数据库！");
			return;
		}
		//修改当前使用的文件夹
		DBMS.currentPath = DBMS.ROOTPATH + File.separator + this.databaseName;
		//为每一个文件夹(数据库)创建一个config文件
		File file = new File(DBMS.ROOTPATH + File.separator + this.databaseName + ".config");
		ObjectInputStream ois = null;//利用对象输入流读取配置文件
		ObjectOutputStream oos = null;
		try {
			DBMS.dataDictionary = null;
			if (file.exists()) {//如果数据库的结构定义文件存在，则读入内存
				ois = new ObjectInputStream(new FileInputStream(file));
				DBMS.dataDictionary = (DataDictionary) ois.readObject();
				System.out.println("加载数据库定义文件成功！");
			} else {//如果不存在则创建一个数据库结构定义文件
				file.createNewFile();//创建数据库结构定义文件
				DBMS.dataDictionary = new DataDictionary(this.databaseName);//创建数据库结构对象
			}	
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (oos != null) {
					oos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
