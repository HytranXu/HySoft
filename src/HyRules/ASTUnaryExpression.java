package HyRules;

public class ASTUnaryExpression extends SimpleNode 
{
	private String strOper = new String();
	private String strValue;
	
	public void setStrOper(String strOper) 
	{
		this.strOper = strOper;
	}

	public void setStrValue(String strValue) 
	{
		this.strValue = strValue;
	}

	public ASTUnaryExpression(int id) 
	{
		super(id);
	}

	public ASTUnaryExpression(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("UnaryExpression\n{\n");
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
		logShow.AppendShow("<------UnaryExpression interpret------>",InforType.Infor);
		//没有单目操作只计算子项
		if(strOper.isEmpty())
		{
			logShow.AppendShow("Calculate child",InforType.Debug);
			super.interpret(mod);
		}
		//存在单目操作
		else
		{
			//展示帮助信息
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("strValue : ");
			infor.append(strValue);
			infor.append("  Operator : ");
			infor.append(strOper);
			logShow.AppendShow(infor.toString(),InforType.Debug);
			//计算子项
			super.interpret(mod);
			//获取子项计算结果
			Object value = this.popStack();
			
			//单项计算结果不能空，根据返回情况找到具体值
			if(null != value && value instanceof ObjData)
				value = ((ObjData)value).Value;
			
			//单项计算结果不能空
			if(null == value)
			{
				infor = new InforLogBuilder();
				infor.setImportant(true);
				infor.append("Wrong happened, inforation list below:-->");
				infor.append("\tOperator : ");
				infor.append(strOper);
				infor.append("\tValue : ");
				infor.append(strValue);
				logShow.AppendShow(infor.toString(),InforType.Error);

				this.pushStack(null);
			}
			//进行单目计算
			else
			{
				infor = new InforLogBuilder();
				Object result = null;
				if(strOper.equals("!"))
				{
					Boolean bValue = getBool(value);
					if(null != bValue)
						result = (!bValue);
					else
						infor.append("Type wrong");
				}
				else 
				{
					Double dValue = getDouble(value);
					if(null != dValue)
					{
						if(strOper.equals("+"))
							result = dValue + 1;
						else
							result = dValue - 1;
					}
					else
						infor.append("Type wrong");
				}
				this.pushStack(result);
				
				infor.append("push result : ");
				infor.append(result.toString());
				logShow.AppendShow(infor.toString(),InforType.Debug);
			}
		}
	}
}
