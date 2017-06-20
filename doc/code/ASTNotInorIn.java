package HyRules;

public class ASTNotInorIn extends SimpleNode 
{
	private String strIndex = null;
	private String strOpera = new String();
	private String strLeftValue;
	private String strRightValue;
	
	public void setStrIndex(String strIndex) 
	{
		this.strIndex = strIndex;
	}
	public void setStrLeftValue(String strLeftValue) 
	{
		this.strLeftValue = strLeftValue;
	}

	public void setStrRightValue(String strRightValue) 
	{
		this.strRightValue = strRightValue;
	}

	public void strOpera(String strOpera) 
	{
		this.strOpera = strOpera;
	}
	
	public ASTNotInorIn(int id) 
	{
		super(id);
	}

	public ASTNotInorIn(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("NotInorIn\n{\n");
		infor.append("\tstrLeftValue : ");
		infor.append(strLeftValue);
		infor.append("\tstrOpera : ");
		infor.append(strOpera);
		infor.append("\tstrRightValue : ");
		infor.append(strRightValue);
		infor.append("\tstrIndex : ");
		infor.append(strIndex);
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------NotInorIn interpret------>",InforType.Infor);
		//没有操作，模拟计算结果，此处异常
		if(null == strOpera || strOpera.isEmpty())
		{
			logShow.AppendShow("if condition wrong",InforType.Error);
			this.pushStack(null);
		}
		//存在操作
		else
		{
			//寻找左值
			FindObjMemResult lefresult = getObjAndMem(strLeftValue,null,null);
			//左值必须存在
			if(null == lefresult)
			{
				this.pushStack(null);
				InforLogBuilder infor = new InforLogBuilder();
				infor.setImportant(true);
				infor.append("Variable: ");
				infor.append(strLeftValue);
				infor.append(" not found!");
				logShow.AppendShow(infor.toString(),InforType.Error);
				return;
			}
			//寻找右值
			FindObjMemResult rigresult = getObjAndMem(strRightValue,null,null);
			//右值必须存在
			if(null == rigresult)
			{
				this.pushStack(null);
				InforLogBuilder infor = new InforLogBuilder();
				infor.setImportant(true);
				infor.append("Variable: ");
				infor.append(strRightValue);
				infor.append(" not found!");
				logShow.AppendShow(infor.toString(),InforType.Error);
				return;
			}
			//计算左值是否在右值范围
			Boolean result = objIn(lefresult,rigresult);
			//提示帮助信息
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("Push :  ");
			infor.append(strOpera);
			infor.append("\tresult : ");
			//设置操作结果同时进行帮助展示
			if(strOpera.equalsIgnoreCase("in"))
			{
				if(result)
				{
					this.pushStack(true);
					infor.append("true");
				}
				else
				{
					this.pushStack(false);
					infor.append("false");
				}
			}
			else
			{
				if(result)
				{
					this.pushStack(false);
					infor.append("false");
				}
				else
				{
					this.pushStack(true);
					infor.append("true");
				}
			}

			logShow.AppendShow(infor.toString(),InforType.Debug);
		}
	}
}
