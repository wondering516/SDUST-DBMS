package db;

import java.util.List;
/***
 *
 *
 * @Author: CoffeeChicken
 * @Date: 2020/12/25 16:29
 * @Usage:
 * B+���ڽڵ�
 */
public class IndexNode {
	public static final int PNUMBER = 6;//ÿ�������������
	private boolean isLeaf;//�Ƿ���Ҷ���
	private IndexNode parent;//���׽��
	private List<NodeEntry> childs;//���ӽ��

	public IndexNode(boolean isLeaf, IndexNode parent, List<NodeEntry> childs2) {
		super();
		this.isLeaf = isLeaf;
		this.parent = parent;
		this.childs = childs2;
	}

	public IndexNode() {
		super();
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public IndexNode getParent() {
		return parent;
	}

	public void setParent(IndexNode parent) {
		this.parent = parent;
	}

	public List<NodeEntry> getChilds() {
		return childs;
	}

	public void setChilds(List<NodeEntry> childs) {
		this.childs = childs;
	}

}
