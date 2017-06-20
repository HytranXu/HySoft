package HyRules;

public class ASTCompile extends SimpleNode 
{
	//是否编辑状态还是运行状态，true编辑状态
	public boolean bCompile = true;
	
	public ASTCompile(int id) 
	{
		super(id);
	}

	public ASTCompile(HySoft p, int id) 
	{
		super(p, id);
	}
	
	//首节点重载
	public void dumpMember(String prefix) 
	{
		if(!HyRuleNode.bDebug)
			return;
		
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("Compile\n{\n");
		infor.append("\tbCompile : ");
		infor.append(new Boolean(bCompile).toString());
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------Compile interpret------>",InforType.Infor);
		logShow.AppendShow("HyRule Running--->.........\n",InforType.Infor);
		super.interpret(mod);
	}
}
