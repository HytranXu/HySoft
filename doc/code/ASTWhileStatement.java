package HyRules;

public class ASTWhileStatement extends SimpleNode 
{
	public ASTWhileStatement(int id) 
	{
		super(id);
	}

	public ASTWhileStatement(HySoft p, int id) 
	{
		super(p, id);
	}

	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("WhileStatement\n{\n");
		infor.append("\tNo members, children will be display");
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		if (children != null && mod.equals(InterpreType.Run)) 
		{
			logShow.AppendShow("<------WhileStatement interpret------>",InforType.Infor);

			//根据条件执行
			boolean bContiue = true;
			while(bContiue)
			{
				if(children != null) 
				{
					boolean bCondition = true;
					//按语法应该是首先ASTConditionalExpression然后是ASTStatementClause
					for (int i = 0; i < children.length; ++i)
					{
						SimpleNode n = (SimpleNode)children[i];
						if (n != null)
						{
							//第一次执行条件语句计算条件
							if(bCondition && n instanceof ASTConditionalExpression)
							{
								ASTConditionalExpression cr = (ASTConditionalExpression)n;
								cr.interpret(mod);
								bCondition = false;
								if(isConditonTrue())
									bContiue = true;
								else
								{
									bContiue = false;	
									break;
								}
							}
							else if(bCondition)
							{
								logShow.AppendShow("While statment condition wrong",InforType.Warn);
								bContiue = false;
								break;
							}
							//执行
							else if(bContiue && !bCondition)
							{
								if(n instanceof ASTStatementClause)
								{
									ASTStatementClause cr = (ASTStatementClause)n;
									//检查是否需要调过
									if(!isContinued())
										cr.interpret(mod);
									//检查是否需要停止
									if(isBreaked())
										break;
									else if(isReturned())
								        break;
								}
								else if(bCondition)
								{
									logShow.AppendShow("While statment statement wrong",InforType.Warn);
									bContiue = false;
									break;
								}
								bCondition = true;
							}
						}
						else
						{
							bContiue = false;
							break;
						}
					}
					
					//消耗掉可能循环结束时正好错过的标记
					if(isBreaked())
					{}
					else if(isReturned())
					{}
				}
			}
		}
	}
}
