package HyRules;

public class ASTEqualityExpression extends SimpleNode 
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
	
	public ASTEqualityExpression(int id) 
	{
		super(id);
	}

	public ASTEqualityExpression(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("EqualityExpression\n{\n");
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
		logShow.AppendShow("<------EqualityExpression interpret------>",InforType.Infor);
			
		//没有== !=操作只需计算子项
		if(strOpera.isEmpty())
		{
			logShow.AppendShow("Calculate child",InforType.Debug);
			super.interpret(mod);
		}
		//存在==等操作
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
			//获取子项结果
			Object rightValue = this.popStack();
			Object leftValue = this.popStack();
			//子项结果必须存在	
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
				//结果一致模拟
				this.pushStack(null);
			}
			//计算==  !=操作
			else
			{
				Boolean result;
				if(strOpera.equals("=="))
					result = IsEqual(leftValue,rightValue);
				else
					result = !IsEqual(leftValue,rightValue);
				this.pushStack(result);
					
				infor = new InforLogBuilder();
				infor.append("push result : ");
				infor.append(result.toString());
				logShow.AppendShow(infor.toString(),InforType.Debug);
			}
		}
	}
}
