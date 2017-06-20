package HyRules;

public class ASTReturnStatement extends SimpleNode 
{
	private String strValue = new String();
	
	public void setStrValue(String strValue) 
	{
		this.strValue = strValue;
	}
	
	public ASTReturnStatement(int id) 
	{
		super(id);
	}

	public ASTReturnStatement(HySoft p, int id) 
	{
		super(p, id);
	}

	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("ReturnStatement\n{\n");
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
			logShow.AppendShow("<------ReturnStatement interpret------>",InforType.Infor);
			
			if(strValue.equals(""))
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.append("calculate Child");
				logShow.AppendShow(infor.toString(),InforType.Debug);
				super.interpret(mod);
				
				Object result = this.popStack();
				logShow.AppendShow("Return push result",InforType.Debug);
				this.pushStack(result);
				this.pushStack(FuncConst.RETURN);
			}
			else
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.append("Result push result: ");
				infor.append(strValue);
				logShow.AppendShow(infor.toString(),InforType.Debug);
				
				this.pushStack(strValue);
				this.pushStack(FuncConst.RETURN);
			}
		}
	}
}
