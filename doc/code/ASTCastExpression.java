package HyRules;

public class ASTCastExpression extends SimpleNode 
{
	private String strType;
	private String strValue;
	
	public void setStrType(String strType) 
	{
		this.strType = strType;
	}

	public void setStrValue(String strValue) 
	{
		this.strValue = strValue;
	}

	public ASTCastExpression(int id) 
	{
		super(id);
	}

	public ASTCastExpression(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("CastExpression\n{\n");
		infor.append("\tType : ");
		infor.append(strType);
		infor.append("\tValue : ");
		infor.append(strValue);
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------CastExpression interpret------>",InforType.Infor);
		
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("Cast");
		infor.append("\tType : ");
		infor.append(strType);
		infor.append("\tValue : ");
		infor.append(strValue);
		logShow.AppendShow(infor.toString(),InforType.Debug);
		
		//int<-->double<-->string<-->int. char<-->int<--->bool<--->char
		if(null != strType && null != strValue && !strType.isEmpty())
		{
			//计算子项
			super.interpret(mod);
			//获取子项结果
			Object cresult = this.popStack();
			//根据type进行转换
			if(strType.equals("int"))
			{
				Integer result = this.getInt(cresult);
				this.pushStack(result);
			}
			else if(strType.equals("double"))
			{
				Double result = this.getDouble(cresult);
				this.pushStack(result);
			}
			else if(strType.equals("bool"))
			{
				Boolean result = this.getBool(cresult);
				this.pushStack(result);
			}
			else if(strType.equals("string"))
			{
				String result = this.getStr(cresult);
				this.pushStack(result);
			}
			else if(strType.equals("char"))
			{
				Character result = this.getChar(cresult);
				this.pushStack(result);
			}
			else
				this.pushStack(null);
		}
	}
}
