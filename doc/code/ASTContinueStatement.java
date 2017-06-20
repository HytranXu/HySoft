package HyRules;

public class ASTContinueStatement extends SimpleNode 
{
	public ASTContinueStatement(int id) 
	{
		super(id);
	}

	public ASTContinueStatement(HySoft p, int id) 
	{
		super(p, id);
	}

	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("ContinueStatement\n{\n");
		infor.append("\tNo members");
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		if(mod.equals(InterpreType.Run)) 
		{
			logShow.AppendShow("<------ContinueStatement interpret------>",InforType.Infor);
			super.interpret(mod);
			logShow.AppendShow("push continue",InforType.Debug);
			this.pushStack(FuncConst.CONTINUE);
		}
	}
}
