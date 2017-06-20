package HyRules;

public class ASTStatementClause extends SimpleNode 
{
	public ASTStatementClause(int id) 
	{
		super(id);
	}

	public ASTStatementClause(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("StatementClause\n{\n");
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------StatementClause interpret------>",InforType.Infor);
		
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("Try to run Statement");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		
		super.interpret(mod);
	}
}
