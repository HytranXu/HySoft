package HyRules;

public class ASTBreakStatement extends SimpleNode 
{
	public ASTBreakStatement(int id) 
	{
		super(id);
	}

	public ASTBreakStatement(HySoft p, int id) 
	{
		super(p, id);
	}

	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("BreakStatement\n{\n");
		infor.append("\tNo members");
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		if (mod.equals(InterpreType.Run)) 
		{
			logShow.AppendShow("<------BreakStatement interpret------>",InforType.Infor);
			super.interpret(mod);
			logShow.AppendShow("push break",InforType.Debug);
			this.pushStack(FuncConst.BREAK);
		}
	}
}
