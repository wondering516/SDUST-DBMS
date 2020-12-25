package db;
/**
 *
 *
 * @Author: CoffeeChicken
 * @Date: 2020/12/25 16:28
 * @Usage: 主处理类
 *
*/

class Handler {
	private Operate operate = null;
	
	public Handler(String account) {
		//按空字符分割输入语句
		String[] operationArray = account.split("\\s");
		//声明操作符
		OperationEnum operation = null;
		try {
			//将操作字符串转化为枚举对象，方便switch操作
			operation = OperationEnum.valueOf(operationArray[0].toUpperCase());
		} catch (Exception e) {
			System.out.println("请检查您的输入语句！！");
			return;
		}
		//匹配操作
		switch (operation) {
		case SHOW://处理show命令
			this.operate = new Show(account);
			break;
		case CREATE://处理create命令
			this.operate = new Create(account);
			break;
		case INSERT://处理insert命令
			Check.hadUseDatabase();//检查是否选择了数据库
			this.operate = new Insert(account);
			break;
		case DELETE://处理delete命令
			Check.hadUseDatabase();
			this.operate = new Delete(account);
			break;
		case UPDATE://处理update命令
			Check.hadUseDatabase();
			this.operate = new Update(account);
			break;
		case DROP://处理drop命令
			Check.hadUseDatabase();
			this.operate = new Drop(account);
			break;
		case SELECT://处理select命令
			Check.hadUseDatabase();
			this.operate = new Select(account);
			break;
		case USE://处理use命令
			this.operate = new Use(account);
			break;
		default:
			System.out.println("指令不合法，请检查输入！！");
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
