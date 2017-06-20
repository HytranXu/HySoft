package HyRules;

import java.util.Vector;

public class ASTConditionalExpression extends SimpleNode 
{
	public void setStrLeftValue(String strLeftValue) 
	{
		this.strLeftValue = strLeftValue;
	}

	public void addStrRightValue(String strRightValue) 
	{
		vecRightValue.add(strRightValue);
	}

	private String strLeftValue;
	private Vector<String> vecRightValue = new Vector<String>();
	private Vector<String> vecOperator = new Vector<String>();
	
	public void addStrOperator(String strOperator) 
	{
		vecOperator.add(strOperator);
	}
	
	public ASTConditionalExpression(int id) 
	{
		super(id);
	}

	public ASTConditionalExpression(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("ConditionalExpression\n{\n");
		infor.append("\tLeftValue : ");
		infor.append(strLeftValue);
		for(int t=0; t<vecOperator.size(); t++)
		{
			infor.append("\tOperator : ");
			infor.append(vecOperator.get(t));
			infor.append("\tRightValue : ");
			infor.append(vecRightValue.get(t));
		}
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------ConditionalExpression interpret------>",InforType.Infor);
		//没有||操作直接计算子项
		if(vecOperator.isEmpty())
		{
			logShow.AppendShow("Calculate child",InforType.Debug);
			super.interpret(mod);
		}
		//存在||操作
		else
		{
			//展示帮助信息
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("LeftValue : ");
			infor.append(strLeftValue);
			for(int t=0; t<vecOperator.size(); t++)
			{
				infor.append("\tOperator : ");
				infor.append(vecOperator.get(t));
				infor.append("\tRightValue : ");
				infor.append(vecRightValue.get(t));
			}
			logShow.AppendShow(infor.toString(),InforType.Debug);
			
			//左值和右值
			Boolean Value = null;
			Boolean result = new Boolean(false);
			//只有存在子项才有||意义
			if(children != null)
			{
				for (int i = 0; i < children.length; ++i)
				{
					SimpleNode n = (SimpleNode)children[i];
					if(n != null)
					{
						//首先计算左值
						if(n instanceof ASTConditionalAndExpression)
						{
							ASTConditionalAndExpression lr = (ASTConditionalAndExpression)n;
							lr.interpret(mod);
							Value = getBool(this.popStack());
							//只要有值为true停止计算
							if(Value)
							{
								result = true;
								break;
							}
						}
						else
							break;
					}
				}
			}
			//没有子项是逻辑错误
			else
			{
				infor = new InforLogBuilder();
				infor.setImportant(true);
				infor.append("Wrong happened, inforation list below:-->");
				infor.append("\tLeftValue : ");
				infor.append(strLeftValue);
				for(int t=0; t<vecOperator.size(); t++)
				{
					infor.append("\tOperator : ");
					infor.append(vecOperator.get(t));
					infor.append("\tRightValue : ");
					infor.append(vecRightValue.get(t));
				}
				logShow.AppendShow(infor.toString(),InforType.Error);
				//模拟结果一致性
				this.pushStack(null);
			}
			
			//子项值必须存在
			if(null == Value)
			{
				infor = new InforLogBuilder();
				infor.setImportant(true);
				infor.append("Wrong happened, inforation list below:-->");
				infor.append("\tLeftValue : ");
				infor.append(strLeftValue);
				for(int t=0; t<vecOperator.size(); t++)
				{
					infor.append("\tOperator : ");
					infor.append(vecOperator.get(t));
					infor.append("\tRightValue : ");
					infor.append(vecRightValue.get(t));
				}
				logShow.AppendShow(infor.toString(),InforType.Error);
				//结果一致操作
				this.pushStack(null);
			}
			//计算||
			else
			{
				this.pushStack(result);
				
				infor = new InforLogBuilder();
				infor.append("push result : ");
				infor.append(result.toString());
				logShow.AppendShow(infor.toString(),InforType.Debug);
			}
		}
	}
}
