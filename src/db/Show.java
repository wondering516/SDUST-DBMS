package db;

/**
 *
 * @Author: CoffeeChicken
 * @Date: 2020/12/22 20:28
 *
 * 主要用于展示所有database和当前database下所有table
 * 展示table时需要选择使用的database
 *
*/


import java.io.File;
import java.util.ArrayList;

public class Show implements Operate{
	private String account = null;

	public Show(String account) {
		this.account = account;
	}
	
	@Override
	public void start() throws Exception {
		switch(ParseAccount.parseShow(this.account)) {
			case 1:
				showDatabase();
				break;
			case 2:
				Check.hadUseDatabase();
				showTables();
				break;
			default:
				System.out.println("输入非法，请检查！");
				break;
		}
	}
	/**
	 * 显示所有数据库!!!
	 */
	private void showDatabase() {
		ArrayList<String> arrayList = new ArrayList<>();
		File file = new File(DBMS.ROOTPATH);
		//取出database下所有文件夹，一个文件夹对应一个database
		for(File file1:file.listFiles()){
			if (file1.isDirectory()){
				arrayList.add(file1.getName());
			}
		}
		System.out.println("共有如下的数据库: "+arrayList.toString());
	}
	/**
	 * 显示当前数据库下的所有表!!!
	 */
	private void showTables() {
		System.out.println("当前数据库下共有如下的数据表: "+DBMS.dataDictionary.getTables());
	}
	
}
