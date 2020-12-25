package db;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
/**
 *
 *
 * @Author: CoffeeChicken
 * @Date: 2020/12/25 16:29
 * @Usage:
 *
*/

public class Insert implements Operate {
	private Table table = null;
	private String account = null;
	private String values = null;
	private String tableName = null;

	public Insert(String account) {
		this.account = account;
	}

	@Override
	public void start() throws Exception {
		//检查insert语句是否合法, 并初始化values和tableName
		ParseAccount.parseInsert(this);
		//根据tableName初始化table
		this.table = OperUtil.loadTable(this.getTableName());
		//检查插入的值是否合法(判断数量和类型)
		if (Check.checkValues(this.table.getFile(), this.table, this.values, true)) {
			this.insert();
			System.out.println("一条记录插入成功！");
		}
	}

	/**
	 * 向文件中插入一行!!!
	 * @param
	 * @param
	 * @throws Exception 
	 */
	private void insert() throws Exception {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.table.getFile(), true), "GBK"));
		pw.println(this.values);
		pw.close();
	}
	
	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
