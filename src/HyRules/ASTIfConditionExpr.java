package HyRules;

public class ASTIfConditionExpr extends SimpleNode 
{
	private String strIndex = null;
	
	public void setStrIndex(String strIndex) 
	{
		this.strIndex = strIndex;
	}
	
	public String getStrIndex() 
	{
		return this.strIndex;
	}

	public ASTIfConditionExpr(int id) 
	{
		super(id);
	}

	public ASTIfConditionExpr(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("IfConditionExpr\n{\n");
		infor.append("\tstrIndex : ");
		infor.append(strIndex);
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------IfConditionExpr interpret------>",InforType.Infor);
		logShow.AppendShow("calcu conditions",InforType.Debug);
		super.interpret(mod);
		logShow.AppendShow("push index",InforType.Debug);
		this.pushStack(strIndex);
	}
}
