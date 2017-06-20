package HyRules;

import java.util.Vector;

public class ASTConditionalAndExpression extends SimpleNode 
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
	
	public ASTConditionalAndExpression(int id) 
	{
		super(id);
	}

	public ASTConditionalAndExpression(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("ConditionalAndExpression\n{\n");
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
		logShow.AppendShow("<------ConditionalAndExpression interpret------>",InforType.Infor);
		//非&&表达式直接计算子项
		if(vecOperator.isEmpty())
		{
			logShow.AppendShow("Calculate child",InforType.Debug);
			super.interpret(mod);
		}
		//进行&&计算
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
			
			//只有所有的值都true才是true
			Boolean Value = null;
			Boolean result = new Boolean(true);
			//只有存在子项才有&&意义
			if(children != null)
			{
				for (int i = 0; i < children.length; ++i)
				{
					SimpleNode n = (SimpleNode)children[i];
					if(n != null)
					{
						//首先计算左值
						if(n instanceof ASTEqualityExpression)
						{
							ASTEqualityExpression lr = (ASTEqualityExpression)n;
							lr.interpret(mod);
							Value = getBool(this.popStack());
							//只有值为false停止计算
							if(!Value)
							{
								result = false;
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
			
			//必须存在计算的左值和右值	
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
				//模拟结果一致性
				this.pushStack(null);
			}
			//计算&&
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
