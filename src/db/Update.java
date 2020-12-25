package db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @Author: CoffeeChicken
 * @Date: 2020/12/25 16:14
 *
*/

public class Update extends Oper implements Operate {

    private List<String[]> set;// 要更改的属性和值
    private Table table = null;
    private String account = null;

    public Update(String account) {
        this.account = account;
    }

    @Override
    public void start() throws Exception {
        // 解析update语句并初始化set与conditions
        ParseAccount.parseUpdate(this, this.account);
        this.table = OperUtil.loadTable(this.getTableName());// 加载关系表
        if (this.update()) {
            System.out.println("更新记录成功！");
        } else {
            System.out.println("更新记录失败！");
        }
    }

    /**
     * 执行update
     */
    private boolean update() throws Exception {
        //get当前操作数据表的上级目录
        String parentPath = this.table.getFile().getParent();
        File temp = new File(parentPath + File.separator + "temp");
        if (temp.exists()) {
            temp.delete();
        }
        //新建一个temp文件并复制数据避免修改失败文件数据丢失
        this.table.getFile().renameTo(temp);
        //新建一个原table文件并写入temp中的数据
        this.table.setFile(new File(parentPath + File.separator + this.table.getTableName()));
        //按行将文件中的数据读到内存
        List<String> list = IOUtil.readFile(temp);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.table.getFile()), "GBK"));
            String[] fields = null;
            for (String line : list) {
                //如果满足更新条件
                if (Check.isSatisfiedOper(line, this)) {
                    String newLine = "";//新字符串
                    fields = line.split(",");//字段数组
                    boolean isUpdatePrimary = false;
                    for (String[] arr : this.getSet()) {
                        for (Field field : DBMS.loadedTables.get(this.getTableName()).getAttributes()) {
                            if (field.getFieldName().equals(arr[0]) && field.isPrimary) {
                                isUpdatePrimary = true;
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < fields.length; i++) {//遍历一条记录中的每个字段
                        for (String[] kvs : this.getSet()) {//遍历所有要更新的属性
                            if (DBMS.loadedTables.get(this.getTableName()).getAttributes().get(i).getFieldName().equals(kvs[0])) {//更新待更新属性对应的字段
                                fields[i] = kvs[1];
                            }
                        }
                        newLine = newLine + fields[i] + ",";
                    }
                    if (newLine.endsWith(",")) {
                        newLine = newLine.substring(0, newLine.length() - 1);
                    }

                    if (Check.checkValues(temp, this.getTable(), newLine, isUpdatePrimary)) {
                        pw.println(newLine);
                    } else {
                        pw.println(line);
                    }
                } else {
                    pw.println(line);//不满足更新条件直接写出到文件
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (pw != null) {
                pw.close();
            }
            //删除临时文件
            temp.delete();
        }
        return true;
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
    public String toString() {
        return "Update [set=" + set + ", toString()=" + super.toString() + "]";
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

    public List<String[]> getSet() {
        return set;
    }

    public void setSet(List<String[]> set) {
        this.set = set;
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

}
