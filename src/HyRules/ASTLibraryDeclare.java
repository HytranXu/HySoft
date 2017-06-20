package HyRules;

import java.util.Vector;

public class ASTLibraryDeclare extends SimpleNode 
{
	//租户名字
	private String strLibraryName;
	private Vector<String> includel = new Vector<String>();
	
	public void setStrLibraryName(String strLibraryName) 
	{
		this.strLibraryName = strLibraryName;
	}
	
	public void AddInclude(String strName)
	{
		includel.add(strName);
	}

	public ASTLibraryDeclare(int id) 
	{
		super(id);
	}

	public ASTLibraryDeclare(HySoft p, int id) 
	{
		super(p, id);
	}
		
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("LibraryDeclare\n{\n");
		infor.append("\tLibraryName : ");
		infor.append(strLibraryName);
		infor.append("\tIncludes\n\t{\n");
		if(!includel.isEmpty())
		{
			for(String name : includel)
			{
				infor.append("\t\t");
				infor.append(name);
			}
		}
		infor.append("\n\t}");
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------LibraryDeclare interpret------>",InforType.Infor);
		if(bInitialed)
			return;
		
		bInitialed = true;
		super.interpret(InterpreType.Compile);
		//展示
		if(parser.getShowTab())
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("<------LibraryDeclare systab: ");
			infor.append(strLibraryName);
			infor.append(" ------>");
			logShow.AppendShow(infor.toString(),InforType.Infor);
			g_Systab.DumpVariables();
		}
	}
}
