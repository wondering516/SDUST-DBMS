package db;

/**
 *
 * @Author: CoffeeChicken
 * @Date: 2020/12/22 20:28
 *
 * ��Ҫ����չʾ����database�͵�ǰdatabase������table
 * չʾtableʱ��Ҫѡ��ʹ�õ�database
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
				System.out.println("����Ƿ������飡");
				break;
		}
	}
	/**
	 * ��ʾ�������ݿ�!!!
	 */
	private void showDatabase() {
		ArrayList<String> arrayList = new ArrayList<>();
		File file = new File(DBMS.ROOTPATH);
		//ȡ��database�������ļ��У�һ���ļ��ж�Ӧһ��database
		for(File file1:file.listFiles()){
			if (file1.isDirectory()){
				arrayList.add(file1.getName());
			}
		}
		System.out.println("�������µ����ݿ�: "+arrayList.toString());
	}
	/**
	 * ��ʾ��ǰ���ݿ��µ����б�!!!
	 */
	private void showTables() {
		System.out.println("��ǰ���ݿ��¹������µ����ݱ�: "+DBMS.dataDictionary.getTables());
	}
	
}
