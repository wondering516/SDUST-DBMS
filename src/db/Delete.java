package db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 *
 * @Author: CoffeeChicken
 * @Date: 2020/12/25 16:27
 * @Usage: 删除操作
 *
*/


public class Delete extends Oper implements Operate{
	private Table table = null;
	private String account = null;
	
	public Delete(String account) {
		this.account = account;
	}

	@Override
	public void start() throws Exception {
		ParseAccount.parseDelete(this);
		this.table = OperUtil.loadTable(this.getTableName());
		if (this.delete()) {
			System.out.println("删除记录成功！");
		}
		
	}

	/**
	 * 删除记录!!!
	 */
	private boolean delete() {
		Table table = this.getTable();
		
		BufferedReader br = null;
		PrintWriter pw = null;
		//获取到上级目录database文件名
		String parentPath = table.getFile().getParent();
		//创建temp文件避免改失败造成数据丢失
		File temp = new File(parentPath + File.separator + "temp");
		if (temp.exists()) {
			temp.delete();
		}
		//修改原table文件名
		if (!table.getFile().renameTo(temp)) {
			return false;
		}
		//指向一个新文件
		table.setFile(new File(parentPath + File.separator + table.getTableName()));
		
		try {
			//创建新文件,文件名为原table名
			table.getFile().createNewFile();
			//读取temp文件中内容并加载到内存
			br = new BufferedReader(new InputStreamReader(new FileInputStream(temp), "GBK"));
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(table.getFile())));
			String line = null;
			
			while ((line = br.readLine()) != null) {
				if (!Check.isSatisfiedOper(line, this)) {//保留不满足删除条件的记录
					pw.println(line);
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (pw != null) {
					pw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//成功删除数据后,删除temp文件
			temp.delete();
		}
		
		return false;
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

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public String getTableName() {
		return super.getTableName();
	}

	@Override
	public void setTableName(String tableName) {
		super.setTableName(tableName);
	}

	@Override
	public boolean isExistWhereCondition() {
		return super.isExistWhereCondition();
	}

	@Override
	public void setExistWhereCondition(boolean existWhereCondition) {
		super.setExistWhereCondition(existWhereCondition);
	}

	@Override
	public List<List<ConditionalExpression>> getConditions() {
		return super.getConditions();
	}

	@Override
	public void setConditions(List<List<ConditionalExpression>> conditions) {
		super.setConditions(conditions);
	}
}
