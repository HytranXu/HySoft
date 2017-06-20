package HyRules;

public class ASTDataDefine extends SimpleNode 
{	
	//定义数据名称
	private String strName = new String();
	
	public void setStrName(String strName) 
	{
		this.strName = strName;
	}
	//构造函数
	public ASTDataDefine(int id) 
	{
		super(id);
	}

	public ASTDataDefine(HySoft p, int id) 
	{
		super(p, id);
	}
	//展示本节点信息
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("DataDefine\n{\n");
		infor.append("\tName : ");
		infor.append(strName);
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	//执行函数
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------DataDefine interpret------>",InforType.Infor);
		if(bInitialed)
			return;
		
		bInitialed = true;
		logShow.AppendShow("Data Type name: " + strName,InforType.Infor);
		
		//数据格式定义编译中其中编译模式，同时把数据定义名称入栈以便后续使用
		this.pushStack(strName);
		super.interpret(InterpreType.Compile);
		this.popStack();
		
		//展示
		if(parser.getShowTab())
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("<------DataDefine dataDeftab: ");
			infor.append(strName);
			infor.append(" ------>");
			logShow.AppendShow(infor.toString(),InforType.Infor);
			g_DataDefine.DumpDefines();
		}
	}
}
