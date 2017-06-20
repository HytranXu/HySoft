package HyRules;

public class ASTLoadDeclare extends SimpleNode 
{
	private String strLibrays = null;
		
	public void setStrLibrays(String strLibrays) 
	{
		this.strLibrays = strLibrays;
	}

	public ASTLoadDeclare(int id) 
	{
		super(id);
	}

	public ASTLoadDeclare(HySoft p, int id) 
	{	
		super(p, id);
	}
		
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("LoadDeclare\n{\n");
		infor.append("\tstrLibrays : ");
		infor.append(strLibrays);
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------LoadDeclare interpret------>",InforType.Infor);
		if(null != strLibrays)
		{
			String libs[] = strLibrays.split(",");
			for(String lib : libs)
			{
				logShow.AppendShow("Try to Load library：" + lib,InforType.Infor);
			}
		}
		super.interpret(mod);
		
		//展示
		if(parser.getShowTab())
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("<------LoadDeclare systab: ");
			infor.append(strLibrays);
			infor.append(" ------>");
			logShow.AppendShow(infor.toString(),InforType.Infor);
			g_Systab.DumpVariables();
		}
	}
}
