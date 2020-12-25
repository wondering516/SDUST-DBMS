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

    private List<String[]> set;// Ҫ���ĵ����Ժ�ֵ
    private Table table = null;
    private String account = null;

    public Update(String account) {
        this.account = account;
    }

    @Override
    public void start() throws Exception {
        // ����update��䲢��ʼ��set��conditions
        ParseAccount.parseUpdate(this, this.account);
        this.table = OperUtil.loadTable(this.getTableName());// ���ع�ϵ��
        if (this.update()) {
            System.out.println("���¼�¼�ɹ���");
        } else {
            System.out.println("���¼�¼ʧ�ܣ�");
        }
    }

    /**
     * ִ��update
     */
    private boolean update() throws Exception {
        //get��ǰ�������ݱ���ϼ�Ŀ¼
        String parentPath = this.table.getFile().getParent();
        File temp = new File(parentPath + File.separator + "temp");
        if (temp.exists()) {
            temp.delete();
        }
        //�½�һ��temp�ļ����������ݱ����޸�ʧ���ļ����ݶ�ʧ
        this.table.getFile().renameTo(temp);
        //�½�һ��ԭtable�ļ���д��temp�е�����
        this.table.setFile(new File(parentPath + File.separator + this.table.getTableName()));
        //���н��ļ��е����ݶ����ڴ�
        List<String> list = IOUtil.readFile(temp);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.table.getFile()), "GBK"));
            String[] fields = null;
            for (String line : list) {
                //��������������
                if (Check.isSatisfiedOper(line, this)) {
                    String newLine = "";//���ַ���
                    fields = line.split(",");//�ֶ�����
                    boolean isUpdatePrimary = false;
                    for (String[] arr : this.getSet()) {
                        for (Field field : DBMS.loadedTables.get(this.getTableName()).getAttributes()) {
                            if (field.getFieldName().equals(arr[0]) && field.isPrimary) {
                                isUpdatePrimary = true;
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < fields.length; i++) {//����һ����¼�е�ÿ���ֶ�
                        for (String[] kvs : this.getSet()) {//��������Ҫ���µ�����
                            if (DBMS.loadedTables.get(this.getTableName()).getAttributes().get(i).getFieldName().equals(kvs[0])) {//���´��������Զ�Ӧ���ֶ�
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
                    pw.println(line);//�������������ֱ��д�����ļ�
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (pw != null) {
                pw.close();
            }
            //ɾ����ʱ�ļ�
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
