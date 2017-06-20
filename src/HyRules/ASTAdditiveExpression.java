package HyRules;

import java.util.Vector;

public class ASTAdditiveExpression extends SimpleNode 
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
	
	public ASTAdditiveExpression(int id) 
	{
		super(id);
	}

	public ASTAdditiveExpression(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("AdditiveExpression\n{\n");
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
		logShow.AppendShow("<------AdditiveExpression interpret------>",InforType.Infor);
		
		//路过非此类业务
		if(vecOperator.isEmpty())
		{
			logShow.AppendShow("Calculate child",InforType.Debug);
			super.interpret(mod);
		}
		//两项相加减
		else
		{
			//提示信息
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
			
			//获取子项
			Double rightValue = null;
			Double leftValue = null;
			Double result = new Double(0);
			String type = null;
			//只有存在子项才有意义	
			if(children != null)
			{
				int iOperIndex = 0;
				for (int i = 0; i < children.length; ++i)
				{
					SimpleNode n = (SimpleNode)children[i];
					if(n != null)
					{
						//首先计算左值
						if(null == leftValue && n instanceof ASTMultiplicativeExpression)
						{
							ASTMultiplicativeExpression lr = (ASTMultiplicativeExpression)n;
							lr.interpret(mod);
							Object obj = this.popStack();
							if(null != obj)
							{
								type = getType(obj);
								if(type.equals(FuncConst.OBJDATA))
									type = getType(((ObjData)obj).Value);
								
								leftValue = getDouble(obj);
							}
							
							if(null == leftValue)
								break;
						}	
						else if(n instanceof ASTMultiplicativeExpression)
						{
							ASTMultiplicativeExpression rr = (ASTMultiplicativeExpression)n;
							rr.interpret(mod);
							rightValue = getDouble(this.popStack());
							
							if(null == leftValue)
								break;
							else
							{
								String operator = vecOperator.get(iOperIndex);
								iOperIndex++;
								if(operator.equals("+"))
									result = (leftValue + rightValue);
								else
									result = (leftValue - rightValue);
								leftValue = result;
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
			
			//如果子项异常进行提醒
			if(null == leftValue || null == rightValue)
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
				//模拟结果入栈保持操作一致性
				this.pushStack(null);
			}
			//进行子项加减
			else
			{
				if(type.equals(FuncConst.INTEGER))
					this.pushStack(getInt(result));
				else
					this.pushStack(result);
				
				infor = new InforLogBuilder();
				infor.append("push result : ");
				infor.append(result.toString());
				logShow.AppendShow(infor.toString(),InforType.Debug);
			}
		}
	}
}
