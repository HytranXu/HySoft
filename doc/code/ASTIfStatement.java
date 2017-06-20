package HyRules;

public class ASTIfStatement extends SimpleNode 
{
	public void setOper(OperationDef oper) 
	{
		this.oper = oper;
	}

	public void setbResult(boolean bResult) 
	{
		this.bResult = bResult;
	}

	private OperationDef oper = null;
	private boolean bResult = false;
	
	public ASTIfStatement(int id) 
	{
		super(id);
	}

	public ASTIfStatement(HySoft p, int id) 
	{
		super(p, id);
	}	
	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("IfStatement\n{\n");
		infor.append("\tResult : ");
		infor.append(new Boolean(bResult).toString());
		if(oper != null)
		{
			infor.append("\tIfCondition\n\t{\n");
			if(oper.bBool)
				infor.append("\t\tTBD bool\tIndex : if");
			else
			{
				infor.append("\t\tLeftValue : ");
				infor.append(oper.strLeftValue);
				infor.append("\t\tOperator : ");
				infor.append(oper.strOperator);
				infor.append("\t\tRightValue : ");
				infor.append(oper.strRightValue);
			}
			infor.append("\n\t}");
		}
		if(!Arraymap.isEmpty())
		{
			infor.append("\tElseCondition\n\t{\n");
			for(String key : Arraymap.keySet())
			{
				infor.append("\t\tName : ");
				infor.append(key);
			}
			infor.append("\n\t}");
		}
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		if (children != null && mod.equals(InterpreType.Run)) 
		{
			logShow.AppendShow("<------IfStatement interpret------>",InforType.Infor);
			logShow.AppendShow("Begin if Statement",InforType.Debug);
			
			//if语句必须有if存在，不能只是有statement
			boolean bHaveif = false;
			//进行条件计算
			for (int i = 0; i < children.length; ++i) 
			{
				SimpleNode n = (SimpleNode)children[i];
				if (n != null)
				{
					//计算条件,只有有个条件true或else存在
					if(n instanceof ASTIfConditionExpr)
					{
						//计算条件
						n.interpret(mod);
						//获取条件结果和那个节点为true
						Object strIndex = this.popStack();
						Object result = this.popStack();
						bHaveif = true;
						
						//展示帮助信息
						InforLogBuilder infor = new InforLogBuilder();
						infor.append("index: ");
						infor.append(strIndex.toString());
						infor.append(" Result: ");
						if(null != result)
						{
							infor.append(result.toString());
							logShow.AppendShow(infor.toString(),InforType.Debug);
							
							//如果条件true直接执行后跳出
							Boolean bResut = getBool(result);
							if(null != bResut && bResut)
							{
								this.interpretNext(mod);
								break;
							}
							else
								this.skipNextNode();
						}
						//没有结果出现异常
						else
						{
							infor.append(" result null wrong!!");
							logShow.AppendShow(infor.toString(),InforType.Warn);
							break;
						}
					}
					//如果没有条件为true，看是否有else存在才执行
					if(i == children.length-1 && bHaveif)
					{
						ASTStatementClause node = null;
						if(n instanceof ASTStatementClause)
						{
							node = ((ASTStatementClause)n);
							if(null != node)
							{
								node.interpret(mod);
								break;
							}
						}
					}
					//节点异常
					else 
					{
						InforLogBuilder infor = new InforLogBuilder();
						infor.append("IfStatement ");
						infor.append("condition type wrong!!");
						logShow.AppendShow(infor.toString(),InforType.Debug);
						break;
					}
				}
			}
		}
	}
}
