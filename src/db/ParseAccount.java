package db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseAccount {
	/***
	 *
	 *
	 * @Author: CoffeeChicken
	 * @Date: 2020/12/25 16:30
	 * @Usage:
	 *
	 * �����������ʽ������
	 * @param left
	 * @param right
	 * @param condition
	 * @throws Exception
	 */
	private static void parseCondition(String left, String right, ConditionalExpression condition) throws Exception{
		String[] sp = null;
		String table = null;
		String name = null;
		
		condition.setLeft(left);
		condition.setRight(right);
		
		if (left.contains(".") && !left.matches("^\".+\"$")) {//�������xx.xx��ʽ��Ϊ����
			boolean flag = true;
			sp = left.split("\\.");
			table = sp[0];
			name = sp[1];
			if (!DBMS.loadedTables.containsKey(table)) {//û�������
				System.out.println("���ԷǷ���" + sp);
				return;
			}
			for (Field field : DBMS.loadedTables.get(table).getAttributes()) {//�������������Լ���
				if (field.getFieldName().equals(name)) {//��������������ʽ�󲿵����ԣ����ñ��е����Ը��������ʽ�е����Ը�ֵ
					condition.setLeftType(field.getType());//����������
					condition.setLeftcolumn(field.column);//������������
					condition.setLeftIsIndex(field.isIndex());//�������Ƿ�Ϊ������
					condition.setLeftRoot(field.indexRoot);//�����������������������������У���Ϊnull
					flag = false;
					break;
				}
			}
			if (flag == false) {
				condition.setLeftConstant(flag);//�������ʽ����������
				condition.setLeftTableName(table);//����������
				condition.setLeftAttribute(name);//��������
			} else {
				throw new RuntimeException("��ϵ" + table + "û��" + name + "������ԣ�");//���쳣�׳�
			}
			
		}
		if (right.contains(".") && !right.matches("^\".+\"$")) {//�������XX.XX��ʽ����Ϊ����
			boolean flag = true;
			sp = right.split("\\.");
			table = sp[0];
			name = sp[1];
			if (!DBMS.loadedTables.containsKey(table)) {
				System.out.println("���Ƿ���" + sp);
				return;
			}
			for (Field field : DBMS.loadedTables.get(table).getAttributes()) {//�������������Լ���
				if (field.getFieldName().equals(name)) {//��������������ʽ�Ҳ������ԣ����ñ��е����Ը��������ʽ�е����Ը�ֵ
					condition.setRightType(field.getType());//����������
					condition.setRightcolumn(field.column);//������������
					condition.setRightIsIndex(field.isIndex());//�������Ƿ�Ϊ������
					condition.setRightRoot(field.indexRoot);//�����������������������������У���Ϊnull
					flag = false;
					break;
				}
			}
			if (flag == false) {
				condition.setRightConstant(flag);//�������ʽ���ֲ�������
				condition.setRightTableName(table);//�ֲ���������
				condition.setRightAttribute(name);//�ֲ�������
			} else {
				throw new RuntimeException("��ϵ" + table + "û��" + name + "������ԣ�");//���쳣�׳�
			}	
		}
		if (!left.contains(".") && !right.contains(".")) {
			System.out.println("���Ƿ��������Ƚ�");
		}
	}


	/**
	 * ����create-table��� ��ȡtable���󣡣���
	 * 
	 * @param account: sql���
	 * CREATE TABLE person(name VARCHAR(12) PRIMARY KEY, age INTEGER NOT NULL, sex VARCHAR(4));
	 * @param name: ���������ݿ���
	 * @return ����һ��Tableʵ��
	 * @throws Exception
	 *
	 */
	public static Table parseTable(String account, String name) throws Exception {
		// ��ȡ����������ڵ��ַ���
		String attr = account.substring(account.indexOf('(') + 1, account.lastIndexOf(')')).trim();
		// �ָ���ֶ�����
		String[] attrs = attr.split(",");// ���ն��ŷָ�����ֶ�
		//name VARCHAR(12) PRIMARY KEY, age INTEGER NOT NULL, sex VARCHAR(4)
		if (attrs.length == 0) {
			throw new RuntimeException("�Ƿ���䣡");
		}

		List<Field> fields = new ArrayList<Field>();
		Field field = null;
		String[] infos = null;
		for (int i = 0; i < attrs.length; i++) {// �����ֶ�����
			infos = attrs[i].trim().split("\\s");
			field = new Field();
			field.setColumn(i);
			field.setFieldName(infos[0]);

			if (infos[1].matches("(?i)(Integer)")) {// integer����
				field.setType(infos[1]);
				field.setLength(4);
			} else {// varchar����
				String[] t = infos[1].split("\\(|\\)");
				field.setType(t[0]);
				field.setLength(Integer.parseInt(t[1]));
			}

			if (infos.length == 4) {// �����Դ���������ǿ�
				if (infos[2].equalsIgnoreCase("not")) {// �ǿ�
					field.setNull(false);
				} else {// ����
					field.setPrimary(true);
				}
			}
			fields.add(field);
		}
		File file = new File(DBMS.currentPath + File.separator + name);// �����ļ�
		File configFile = new File(DBMS.currentPath + File.separator + name + ".config");// �����ļ�

		return new Table(name, file, configFile, fields);// ���� �����ļ� �����ļ� �ֶμ���
	}
	/**
	 * ����Use��� ��ȡuse���������ݿ���������
	 * @param account
	 * @return
	 */
	public static String parseUse(String account) {
		String pattern = "(?i)^(use)\\s(database)\\s\\S+\\s?;$";
		if (!account.matches(pattern)) {
			throw new RuntimeException("ƥ��ʧ�ܣ�����������䣡��");
		}
		//���������������ݿ���
		return account.split("\\s|;")[2];
	}
	/**
	 * ����create��䣡����
	 * 
	 * @param create��:�����һ������
	 * @return 1���������ݿ� 2�������� 3: ��������
	 */
	public static int parseCreate(Create create) {
		String tableP = "(?i)^(create)\\s(table)\\s(\\w+)\\s?\\(\\s?((\\S+\\sInteger)|(\\S+\\sVarchar\\(\\w+\\)))((\\sPrimary\\sKey)|(\\sNot\\sNull))?"
				+ "(\\s?,\\s?((\\S+\\sInteger)|(\\S+\\sVarchar\\(\\w+\\)))((\\sPrimary\\sKey)|(\\sNot\\sNull))?)*\\);$";

		String databaseP = "(?i)^(create)\\s(database)\\s\\S+\\s?;$";
		
		String indexP = "(?i)^(create)\\s(index)\\s(\\S+)\\s(on)\\s(\\S+)\\s(\\S+)\\s?;$";
		
		String[] arr = create.getAccount().split("\\s|;|\\(");//�����ָ�Ϊ��������

		if (create.getAccount().matches(tableP)) {// ������
			create.setName(arr[2]);// �������ı���
			if (DBMS.dataDictionary == null) {
				throw new RuntimeException("����ѡ�����ݿ⣡");
			}
			if (DBMS.dataDictionary.getTables().contains(create.getName())) {// ���Ѵ���
				throw new RuntimeException("�˱��Ѵ��ڣ�");
			} else {// �����ڣ��򽨱�
				return 2;
			}
		} else if (create.getAccount().matches(databaseP)) {// �������ݿ�
			create.setName(arr[2]);//�����������ݿ���
			return 1;
		} else if (create.getAccount().matches(indexP)) {
			create.setName(arr[4]);//�����������ı���
			return 3;
		} else {
			throw new RuntimeException("�Ƿ���䣬�������룡");
		}
	}

	/**
	 * ����show����������
	 * 
	 * @param account
	 * @return 1��showdatabase 2��showtables
	 */
	public static int parseShow(String account) {
		String showDatabase = "(?i)(show)\\s(database)\\s?;";
		String shoeTables = "(?i)(show)\\s(tables)\\s?;";

		if (account.matches(shoeTables)) {
			return 2;
		} else if (account.matches(showDatabase)) {
			return 1;
		} else {
			throw new RuntimeException("�Ƿ���䣡");
		}

	}

	/**
	 * ����insert��� ��ȡtable���󣡣���
	 * 
	 * @param
	 * @return
	 * @throws Exception
	 */
	public static void parseInsert(Insert insert) throws Exception {
		String account = insert.getAccount();
		String pattern = "(?i)^(insert)\\s(into)\\s[a-zA-Z]+\\s(values)\\s?"
				+ "\\(\\s?([\\S]+)\\s?(,\\s?[\\S]+\\s?)*\\);$";
		if (!account.matches(pattern)) {
			throw new RuntimeException("ƥ��ʧ�ܣ����Ƿ���");
		}
		String tableName = account.split("\\s")[2];
		insert.setTableName(tableName);
		insert.setValues(ParseAccount.subValues(account));


	}
	/**
	 * ����delete����!!!
	 * @param
	 */
	public static void parseDelete(Delete delete) throws Exception{
		String account = delete.getAccount();
		
		String pattern = "(?i)^(delete)\\s(from)\\s[\\S]+(\\s(where)\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+(\\s((and)|(or))\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+)*)?;$";
		if (!account.matches(pattern)) {
			throw new RuntimeException("ƥ��ʧ�ܣ����Ƿ�!");
		}
		
		String[] array = account.split("\\s|;");
		String tableName = array[2];
		
		delete.setTableName(tableName);

		
		if (!account.contains("where") && !account.contains("WHERE")) {//û��where
			delete.setExistWhereCondition(false);
		} else {//��where�������������ʽ
			List<List<String>> arr = ParseAccount.subOper(account, "where", ";");
			
			List<List<ConditionalExpression>> lc = new ArrayList<List<ConditionalExpression>>();//or���ʽ
			List<ConditionalExpression> conditions = null;//and���ʽ
			ConditionalExpression condition = null;//�������ʽ
			
			String[] lr = null;
			String left = null;
			String right = null;
			try {
				for (List<String> lor : arr) {
					conditions = new ArrayList<ConditionalExpression>();
					for (String str : lor) {
						condition = new ConditionalExpression();
						if (str.contains("<")) {
							lr = str.split("<");
							left = lr[0];
							right = lr[1];
							condition.setOper("<");
							parseCondition(left, right, condition);
						} else if (str.contains(">")) {
							lr = str.split(">");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper(">");
						} else if (str.contains("<=")) {
							lr = str.split("<=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper("<=");
						} else if (str.contains(">=")) {
							lr = str.split(">=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper(">=");
						} else {
							lr = str.split("=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper("=");
						}
						conditions.add(condition);
					}
					lc.add(conditions);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return;
			}
			delete.setConditions(lc);
		}

	}

	/**
	 * ����drop��䣬���ر���!!!
	 * @param account
	 * @return
	 */
	public static String parseTableName(String account) {
		return account.split("\\s|;")[2];
	}

	
	/**
	 * ��ȡ����������е��ַ�����ɾ���ַ����еĿո�
	 * 
	 * @param account
	 * @return
	 */
	public static String subValues(String account) {
		String[] values = account.substring(account.indexOf('(') + 1, account.lastIndexOf(')')).split(",");
		String newLine = "";
		for (String str : values) {
			newLine = newLine + str.trim() + ",";
		}
		return newLine.substring(0, newLine.lastIndexOf(","));
	}
	/**
	 * ��ȡ���!!!
	 * 
	 * @param account�����
	 *
	 * @param start����ȡ�Ŀ�ʼλ��
	 *
	 * @param end����ȡ��ĩβ
	 *
	 * @return ��ȡ��ָ���ַ�������
	 *
	 */
	public static String[] subAccount(String account, String start, String end) {
		String temp = null;
		Pattern pattern = null;
		if (start == null) {
			pattern = Pattern.compile("(?i)(?=(add)|(drop)).+(?<=" + end + ")");
		} else {
			pattern = Pattern.compile("(?i)(?=" + start + ").+(?<=" + end + ")");
		}
		Matcher matcher = pattern.matcher(account);
		if (matcher.find()) {
			temp = matcher.group(0);
		}
		temp = temp.split("(?i)(select)|(set)|(add)|(drop)|(from)|(where)|;")[1];// ����в��ܳ��ֹؼ��֣���ʹ�����к���Ҳ����
		String[] fields = temp.split("(?i),|(and)");// Ҫ�鿴���ֶ�
		for (int i = 0; i < fields.length; i++) {
			fields[i] = fields[i].trim();
		}

		return fields;
	}


	/**
	 * ��ȡdelete��update�е��������ʽ
	 * @return
	 */
	public static List<List<String>> subOper(String account, String start, String end) {
		List<List<String>> list= new ArrayList<List<String>>();
		List<String> tlist = null;
		
		Pattern pattern = Pattern.compile("(?i)(?=" + start + ").+(?<=" + end + ")");
		Matcher matcher = pattern.matcher(account);
		String temp = null;
		if (matcher.find()) {
			temp = matcher.group(0);
		}
		temp = temp.split("(?i)(where)|;")[1];// ����в��ܳ��ֹؼ��֣���ʹ�����к���Ҳ����
		//�����ж��а���or
		String[] arr1 = temp.split("(?i)(or)");
		for (String arr2 : arr1) {
			tlist = new ArrayList<String>();
			//ȡ�������ж�, ����and
			for (String arr3 : arr2.trim().split("(?i)(and)")) {
				tlist.add(arr3.trim());
			}
			list.add(tlist);
		}
		return list;
	}


	/**
	 * ����update��䣡����
	 * @param update
	 * @param account
	 */
	public static void parseUpdate(Update update, String account) {
		//�﷨ƥ������
		String pattern = "(?i)^(update)\\s\\w+\\s(set)(\\s\\w+(=)\\S+)(\\s?,\\s?\\w+(=)\\S+)*(\\s(where)\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+(\\s((and)|(or))\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+)*)?;$";
		if (!account.matches(pattern)) {
			System.out.println("��䲻�Ϸ���");
			return;
		}
		//��ȡ��tableName
		String tableName = account.split("\\s")[1];
		//��ʼ��tableName
		update.setTableName(tableName);
		
		String end = "where";
		//��䲻����where�����û��where����
		if (!account.contains("WHERE") && !account.contains("where")) {
			update.setExistWhereCondition(false);//û��where����
			end = ";";
		}
		//ȡ����Ҫ�޸ĵ�ֵ
		//UPDATE person SET name="�ֳ�" WHERE person.name="�ν�";
		String[] setValues = ParseAccount.subAccount(account, "set", end);
		
		List<String[]> set =  new ArrayList<String[]>();
		String[] kv = null;
		for (String string : setValues) {
			kv = new String[2];
			kv[0] = string.split("=")[0];//����
			kv[1] = string.split("=")[1];//ֵ
			set.add(kv);
		}
		update.setSet(set);
		//WHERE salary.age>113 AND salary.salary<7000;
		if (update.isExistWhereCondition()) {//��where�������ж�������������
			List<List<String>> cs = ParseAccount.subOper(account, "where", ";");
			List<List<ConditionalExpression>> lo = new ArrayList<List<ConditionalExpression>>();
			List<ConditionalExpression> conditions = null;
			ConditionalExpression condition = null;
			String[] lr = null;
			String left = null;
			String right = null;
			try {
				for (List<String> arr : cs) {
					conditions = new ArrayList<ConditionalExpression>();
					for (String str : arr) {
						condition = new ConditionalExpression();
						//��������ֱ���>,<,>=,<=,=
						if (str.contains("<")) {
							lr = str.split("<");
							left = lr[0];
							right = lr[1];
							condition.setOper("<");
							parseCondition(left, right, condition);
						} else if (str.contains(">")) {
							lr = str.split(">");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper(">");
						} else if (str.contains("<=")) {
							lr = str.split("<=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper("<=");
						} else if (str.contains(">=")) {
							lr = str.split(">=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper(">=");
						} else {
							lr = str.split("=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper("=");
						}
						conditions.add(condition);
					}
					lo.add(conditions);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return;
			}
			update.setConditions(lo);
		}
		
	}

	/**
	 * ����select������
	 * @param account
	 */
	public static void parseSelect(Select select, String account) {
		/*
		 * ������ʽ pattern1
		 * �����ִ�Сд ƥ��select��䣬���磺
		 * SELECT XX.XX, XX.XX
		 * FROM XX, XX
		 * WHERE XX.XX<15 AND XX.XX=XX.XX AND XX.XX>28;
		 *
		 */
		String pattern = "(?i)^(select)\\s([\\S]+)\\s?(,\\s?[\\S]+)*\\s(from)\\s[\\S]+\\s?(,\\s?[\\S]+)*"
				+ "(\\s(where)\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+(\\s((and)|(or))\\s[\\S]+[(<)(>)(=)(<=)(>=)][\\S]+)*)?;$";
		if (!account.matches(pattern)) {
			System.out.println("ƥ��ʧ�ܣ����Ƿ���");
			return;
		}
		/************************************************************************************************************************************/
//		select = new Select();//ʵ����select��
		/*********************************************************************
		 * ��ȡfrom��where֮�������
		 */
		String end = "where";
		if (!account.contains("where") && !account.contains("WHERE")) {
			end = ";";
		}
		String[] fromWhere = ParseAccount.subAccount(account, "from", end);
		List<String> list = new ArrayList<String>();
		for (String string : fromWhere) {
			Table table = null;
			try {
				if ((table = DBMS.loadedTables.get(string)) == null) {
					table = OperUtil.loadTable(string);//����Ҫ��ѯ�Ĺ�ϵ��
					DBMS.loadedTables.put(string, table);//�����صĹ�ϵ�����loadedTables��
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			list.add(string);
			select.setNumberTable(select.getNumberTable() + 1);
		}
		select.setTableName(list);
		/*************************************************************************************************************************************
		 * ��ȡselect��from֮�������
		 */
		String[] selectFrom = ParseAccount.subAccount(account, "select", "from");//�ֶ�ȫ�����飬����A.m
		if (selectFrom[0].equals("*")) {
			select.setSeeAll(true);
		} else {
			List<DisplayField> attributes = new ArrayList<DisplayField>();//���ڴ洢select����ֶ���Ϣ
			DisplayField df = null;//��ʾ�ֶ�
			String tableName = null;//Ҫ��ʾ�ֶ����漰�ı���
			String fieldName = null;//�ֶ���
			for (String fullName : selectFrom) {
				/*
				 * �ԡ�.��Ϊ�ָ�㣬ǰ�벿��Ϊ�ֶ�������������벿��Ϊ�ֶ���
				 */
				try {
					tableName = fullName.split("\\.")[0];
					fieldName = fullName.split("\\.")[1];
				} catch (Exception e) {
					System.out.println(fullName + "��ʽ����ȷ��");
					return;
				}
				if (DBMS.loadedTables.get(tableName) == null) {
					System.out.println("���Ƿ���û��" + tableName + "�����");
					return;
				}
				for (Field field : DBMS.loadedTables.get(tableName).getAttributes()) {//����tableName����ֶμ���
					if (field.getFieldName().equals(fieldName)) {//�ҵ����ֶ�
						df = new DisplayField(field, fullName, tableName);
						break;
					}
				}
				if (df == null) {
					System.out.println("���Ƿ���" + fullName + "�����ڣ�");
					return;
				}
				attributes.add(df);
				select.setNumberFeld(select.getNumberFeld() + 1);
			}
			select.setAttributes(attributes);
		}
		/*********************************************************************
		 * ��ȡwhere����������ʽ
		 */
		if (!account.contains("where") && !account.contains("WHERE")) {//û��where
			select.setExistWhereCondition(false);
		} else {//��where�������������ʽ
			List<List<String>> arr = ParseAccount.subOper(account, "where", ";");
			
			List<List<ConditionalExpression>> lc = new ArrayList<List<ConditionalExpression>>();//or���ʽ
			List<ConditionalExpression> conditions = null;//and���ʽ
			ConditionalExpression condition = null;//�������ʽ
			
			String[] lr = null;
			String left = null;
			String right = null;
			try {
				for (List<String> lor : arr) {
					conditions = new ArrayList<ConditionalExpression>();
					for (String str : lor) {
						condition = new ConditionalExpression();
						if (str.contains("<")) {
							lr = str.split("<");
							left = lr[0];
							right = lr[1];
							condition.setOper("<");
							parseCondition(left, right, condition);
						} else if (str.contains(">")) {
							lr = str.split(">");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper(">");
						} else if (str.contains("<=")) {
							lr = str.split("<=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper("<=");
						} else if (str.contains(">=")) {
							lr = str.split(">=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper(">=");
						} else {
							lr = str.split("=");
							left = lr[0];
							right = lr[1];
							parseCondition(left, right, condition);
							condition.setOper("=");
						}
						conditions.add(condition);
					}
					lc.add(conditions);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				return;
			}
			select.setConditions(lc);
		}

	}

	/**
	 * ����create-index���
	 * @param create
	 */
	public static void parseIndex(Create create) {
		create.setIndexName(create.getAccount().split("\\s|;")[2]);//����������
		create.setFieldName(create.getAccount().split("\\s|;")[5]);//����������
	}

	/**
	 * ����drop����
	 * @param
	 * @return
	 */
	public static int parseDrop(String account) {
		String tableP = "(?i)^(drop)\\s(table)\\s\\S+\\s?;$";
		String indexP = "(?i)^(drop)\\s(index)\\s(\\S+)\\s(on)\\s(\\S+)\\s?;$";

		if (account.matches(tableP)) {
			return 0;
		} else if (account.matches(indexP)) {
			return 1;
		} else {
			return -1;
		}
	}

	public static String parseIndexName(String account) {
		return account.split("\\s")[2];
	}
}
