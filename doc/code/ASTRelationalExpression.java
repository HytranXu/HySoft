package HyRules;

public class ASTRelationalExpression extends SimpleNode 
{
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

	private String strOpera = new String();
	private String strLeftValue;
	private String strRightValue;
	
	public ASTRelationalExpression(int id) 
	{
		super(id);
	}

	public ASTRelationalExpression(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("RelationalExpression\n{\n");
		infor.append("\tLeftValue : ");
		infor.append(strLeftValue);
		infor.append("\tOperator : ");
		infor.append(strOpera);
		infor.append("\tRightValue : ");
		infor.append(strRightValue);
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------RelationalExpression interpret------>",InforType.Infor);
		//没有操作只需计算子项
		if(strOpera.isEmpty())
		{
			logShow.AppendShow("Calculate child",InforType.Debug);
			super.interpret(mod);
		}
		//存在关系操作
		else
		{
			//展示帮助信息
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("LeftValue : ");
			infor.append(strLeftValue);
			infor.append("  Operator : ");
			infor.append(strOpera);
			infor.append("  RightValue : ");
			infor.append(strRightValue);
			logShow.AppendShow(infor.toString(),InforType.Debug);
			//计算子项
			super.interpret(mod);
			//获取子项计算结果
			Double rightValue = getDouble(this.popStack());
			Double leftValue = getDouble(this.popStack());
			//计算结果必须存在
			if(null == leftValue || null == rightValue)
			{
				infor = new InforLogBuilder();
				infor.setImportant(true);
				infor.append("Wrong happened, inforation list below:-->");
				infor.append("\tLeftValue : ");
				infor.append(strLeftValue);
				infor.append("\tOperator : ");
				infor.append(strOpera);
				infor.append("\tRightValue : ");
				infor.append(strRightValue);
				logShow.AppendShow(infor.toString(),InforType.Error);
				//异常模拟操作结果一致
				this.pushStack(null);
			}
			//计算关系操作
			else
			{
				Boolean result;
				if(strOpera.equals(">"))
					result = (leftValue > rightValue);
				else if(strOpera.equals("<"))
					result = (leftValue < rightValue);
				else if(strOpera.equals(">="))
					result = (leftValue >= rightValue);
				else
					result = (leftValue <= rightValue);
				this.pushStack(result);
				
				infor = new InforLogBuilder();
				infor.append("push result : ");
				infor.append(result.toString());
				logShow.AppendShow(infor.toString(),InforType.Debug);
			}	
		}
	}
}
