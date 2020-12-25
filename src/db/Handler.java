package db;
/**
 *
 *
 * @Author: CoffeeChicken
 * @Date: 2020/12/25 16:28
 * @Usage: ��������
 *
*/

class Handler {
	private Operate operate = null;
	
	public Handler(String account) {
		//�����ַ��ָ��������
		String[] operationArray = account.split("\\s");
		//����������
		OperationEnum operation = null;
		try {
			//�������ַ���ת��Ϊö�ٶ��󣬷���switch����
			operation = OperationEnum.valueOf(operationArray[0].toUpperCase());
		} catch (Exception e) {
			System.out.println("��������������䣡��");
			return;
		}
		//ƥ�����
		switch (operation) {
		case SHOW://����show����
			this.operate = new Show(account);
			break;
		case CREATE://����create����
			this.operate = new Create(account);
			break;
		case INSERT://����insert����
			Check.hadUseDatabase();//����Ƿ�ѡ�������ݿ�
			this.operate = new Insert(account);
			break;
		case DELETE://����delete����
			Check.hadUseDatabase();
			this.operate = new Delete(account);
			break;
		case UPDATE://����update����
			Check.hadUseDatabase();
			this.operate = new Update(account);
			break;
		case DROP://����drop����
			Check.hadUseDatabase();
			this.operate = new Drop(account);
			break;
		case SELECT://����select����
			Check.hadUseDatabase();
			this.operate = new Select(account);
			break;
		case USE://����use����
			this.operate = new Use(account);
			break;
		default:
			System.out.println("ָ��Ϸ����������룡��");
			break;
		}
	}
	
	public void start() {
		try {
			this.operate.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
