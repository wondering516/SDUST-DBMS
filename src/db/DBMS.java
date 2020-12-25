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
 * ���ݿ�����
 *
 */
public class DBMS {
	public static final String ROOTPATH = "." + File.separator + "database";//��Ŀ¼
	public static String currentPath;//��ǰĿ¼
	public static DataDictionary dataDictionary = null;//���ݿ��ֵ�
	public static Map<String, Table> loadedTables;//�Ѽ��صĹ�ϵ��
	public static Map<String, Field> indexEntry;//��������
	
	public DBMS() {
		DBMS.loadedTables = new HashMap<String, Table>();
		DBMS.indexEntry = new HashMap<String, Field>();
		DBMS.currentPath = ROOTPATH;
	}
	/**
	 * ����ָ��
	 * @throws Exception 
	 */
	public void start() throws Exception {
		System.out.println("-----------------------------");
		System.out.println("|      ���ݿ�ʵ��			    |");
		System.out.println("|       java	            |");
		System.out.println("|     �ƿ�18-4������          |");
		System.out.println("-----------------------------");
		Scanner scan = new Scanner(System.in);
		String account = null;
		while (true) {
			//�ӿ���̨����һ������
			account = OperUtil.input(scan);
			//��������
			new Handler(account).start();
		}
	}
	/**
	 * ��������
	 * @param args
	 */
	public static void main(String[] args) {	
		try {
			//���������
			new DBMS().start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}	
	}

}
