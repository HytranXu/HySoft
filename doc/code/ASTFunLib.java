package HyRules;

public class ASTFunLib extends SimpleNode 
{
	private String strName = new String();
	
	public void setStrName(String strName) 
	{
		this.strName = strName;
	}
	
	public ASTFunLib(int id) 
	{
		super(id);
	}

	public ASTFunLib(HySoft p, int id) 
	{
		super(p, id);
	}

	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("FunLib\n{\n");
		infor.append("\tName : ");
		infor.append(strName);
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------FunLib interpret------>",InforType.Infor);
		if(bInitialed)
			return;
		
		bInitialed = true;
		logShow.AppendShow("Func Type name: " + strName,InforType.Infor);
		
		if(null != funDefineInfor && null != parentFun)
			AddFunLib(strName,funDefineInfor);
		
		//数据格式定义编译中其中编译模式，同时把数据定义名称入栈以便后续使用
		this.pushStack(strName);
		super.interpret(InterpreType.Build);
		this.popStack();

		//展示
		if(parser.getShowTab())
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("<------FunLib systab: ");
			infor.append(strName);
			infor.append("------>");
			logShow.AppendShow(infor.toString(),InforType.Infor);
			this.func_Systab.DumpVariables();
			
			if(null != parentFun)
			{
				infor = new InforLogBuilder();
				infor.append("<------FunLib:------>");
				logShow.AppendShow(infor.toString(),InforType.Infor);
				parentFun.dump("");
			}
		}
	}
}
