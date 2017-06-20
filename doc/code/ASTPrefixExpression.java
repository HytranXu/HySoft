package HyRules;

public class ASTPrefixExpression extends SimpleNode 
{
	private String strOper = null;
	private String strValue = null;
	
	public void setStrOper(String strOper) 
	{
		this.strOper = strOper;
	}

	public void setStrValue(String strValue) 
	{
		this.strValue = strValue;
	}
	
	public ASTPrefixExpression(int id) 
	{
		super(id);
	}

	public ASTPrefixExpression(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("PrefixExpression\n{\n");
		infor.append("\tOperation : ");
		infor.append(strOper);
		infor.append("\tValue : ");
		infor.append(strValue);
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------PrefixExpression interpret------>",InforType.Infor);
		if(null != strOper && null != strValue && !strOper.isEmpty() && !strValue.isEmpty())
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("PrefixExpression ");
			infor.append("\tOperation : ");
			infor.append(strOper);
			infor.append("\tValue : ");
			infor.append(strValue);
			logShow.AppendShow(infor.toString(),InforType.Debug);
			
			super.interpret(mod);
			FindObjMemResult result = getObjAndMem(strValue,"","");
			if(null != result)
			{
				OperateOnResult(strOper,result);
				PushVariableToStack(result);
			}
			else
				this.pushStack(null);
		}
	}
}
