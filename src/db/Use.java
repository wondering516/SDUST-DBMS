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
	//�����SQL���
	private String account = null;
	private String databaseName = null;
	
	public Use(String account) {
		this.account = account;

	}

	@Override
	public void start() throws Exception {
		//����ParseAccount���н���������������ǰʹ�õ����ݿ���
		this.databaseName = ParseAccount.parseUse(this.account);
		this.use();
		
	}

	private void use() {
		//�־û���ϵ��ṹ�����ļ������ݿ�ṹ�����ļ�
		OperUtil.persistence();
		//��ʼ����ϵ��
		DBMS.loadedTables = new HashMap<String, Table>();
		//�ж���ʹ�õ��ļ����Ƿ����
		File databaseDir = new File(DBMS.ROOTPATH, this.databaseName);
		if (!databaseDir.exists()) {
			System.out.println("�����ݿⲻ���ڣ����ȴ������ݿ⣡");
			return;
		}
		//�޸ĵ�ǰʹ�õ��ļ���
		DBMS.currentPath = DBMS.ROOTPATH + File.separator + this.databaseName;
		//Ϊÿһ���ļ���(���ݿ�)����һ��config�ļ�
		File file = new File(DBMS.ROOTPATH + File.separator + this.databaseName + ".config");
		ObjectInputStream ois = null;//���ö�����������ȡ�����ļ�
		ObjectOutputStream oos = null;
		try {
			DBMS.dataDictionary = null;
			if (file.exists()) {//������ݿ�Ľṹ�����ļ����ڣ�������ڴ�
				ois = new ObjectInputStream(new FileInputStream(file));
				DBMS.dataDictionary = (DataDictionary) ois.readObject();
				System.out.println("�������ݿⶨ���ļ��ɹ���");
			} else {//����������򴴽�һ�����ݿ�ṹ�����ļ�
				file.createNewFile();//�������ݿ�ṹ�����ļ�
				DBMS.dataDictionary = new DataDictionary(this.databaseName);//�������ݿ�ṹ����
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
