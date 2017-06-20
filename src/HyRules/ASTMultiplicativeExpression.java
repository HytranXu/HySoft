package HyRules;

import java.util.Vector;

public class ASTMultiplicativeExpression extends SimpleNode 
{
	final static double Wrong = -1;
	
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
	
	public ASTMultiplicativeExpression(int id) 
	{
		super(id);
	}

	public ASTMultiplicativeExpression(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("MultiplicativeExpression\n{\n");
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
		logShow.AppendShow("<------MultiplicativeExpression interpret------>",InforType.Infor);
		
		//没有乘除操作只需计算子项
		if(vecOperator.isEmpty())
		{
			logShow.AppendShow("Calculate child",InforType.Debug);
			super.interpret(mod);
		}
		//存在乘除操作
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
						if(null == leftValue && n instanceof ASTUnaryExpression)
						{
							ASTUnaryExpression lr = (ASTUnaryExpression)n;
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
						else if(n instanceof ASTUnaryExpression)
						{
							ASTUnaryExpression rr = (ASTUnaryExpression)n;
							rr.interpret(mod);
							rightValue = getDouble(this.popStack());
							
							if(null == leftValue)
								break;
							else
							{
								String operator = vecOperator.get(iOperIndex);
								iOperIndex++;
								if(operator.equals("*"))
									result = (leftValue * rightValue);
								else if(operator.equals("/"))
								{
									//除时除数不能为0
									if(0 == rightValue)
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
										result = ASTMultiplicativeExpression.Wrong;
										break;
									}
									else
										result = (leftValue / rightValue);
								}
								else
									result = (leftValue % rightValue);
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
			
			//子项结果必须存在
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
				//模拟结果一致操作
				this.pushStack(null);
			}
			//计算乘除
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
