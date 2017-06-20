package HyRules;

public class ASTReturn2Client extends SimpleNode 
{
	private String strValue = new String();
	
	public void setStrValue(String strValue) 
	{
		this.strValue = strValue;
	}
	
	public ASTReturn2Client(int id) 
	{
		super(id);
	}

	public ASTReturn2Client(HySoft p, int id) 
	{
		super(p, id);
	}

	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("Return2Client\n{\n");
		infor.append("\tValue : ");
		infor.append(strValue);
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		if(mod.equals(InterpreType.Run))
		{
			logShow.AppendShow("<------Return2Client interpret------>",InforType.Infor);
			//计算返回值
			super.interpret(mod);
			InforLogBuilder infor = new InforLogBuilder();
			Object returnValue = this.popStack();
			if(null != returnValue)
				infor.append("Have gotten returned value!\n");
			else
				infor.append("Have gotten returned null value!\n");
			infor.append("<------Return2Client finished------>");
			logShow.AppendShow(infor.toString(),InforType.Debug);
		}
	}
}
