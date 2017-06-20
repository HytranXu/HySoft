package HyRules;

import java.util.Vector;

public class ASTForStatement extends SimpleNode 
{
	public void setIntiValue(Initor intiValue) 
	{
		this.intiValue = intiValue;
	}

	public void setOfState(OperationDef ofState) 
	{
		this.ofState = ofState;
		bOfStatement = true;
	}

	public void setStepOper(OperationDef stepOper) 
	{
		StepOper = stepOper;
	}

	private boolean bOfStatement = false;
	private Initor intiValue = null;
	private OperationDef ofState = null;
	private OperationDef StepOper = null;
	
	public ASTForStatement(int id) 
	{
		super(id);
	}

	public ASTForStatement(HySoft p, int id) 
	{
		super(p, id);
	}
	
	public void dumpContent(String prefix) 
	{
		logShow.AppendShow("ForStatement\n{\n",InforType.Infor);
		if(bOfStatement)
		{	
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("\tOfCondition\n\t{\n");
			infor.append("\t\tLeftValue : ");
			infor.append(ofState.strLeftValue);
			infor.append("\t\tOperator : ");
			infor.append(ofState.strOperator);
			infor.append("\t\tRightValue : ");
			infor.append(ofState.strRightValue);
			infor.append("\n\t}");
			logShow.AppendShow(infor.toString(),InforType.Debug);
		}
		else
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("\tStepCondition\n\t{\n");
			
			infor.append("\t\tInitialValue\n\t\t{\n");
			infor.append("\t\t\tType : ");
			infor.append(intiValue.StrType);
			infor.append("\t\t\tName : ");
			infor.append(intiValue.strName);
			infor.append("\t\t\tinitValue : ");
			infor.append(intiValue.strInitial);
			infor.append("\n\t\t}");
			
			infor.append("\t\tStepValue\n\t\t{\n");
			infor.append("\t\t\tLeftValue : ");
			infor.append(StepOper.strLeftValue);
			infor.append("\t\t\tOperator : ");
			infor.append(StepOper.strOperator);
			infor.append("\t\t\tRightValue : ");
			infor.append(StepOper.strRightValue);
			infor.append("\n\t\t}");
			
			infor.append("\n\t}");
			logShow.AppendShow(infor.toString(),InforType.Debug);
		}
		logShow.AppendShow("\n}",InforType.Debug);
		super.dumpMember(prefix);
	}
	
	public void interpret(InterpreType mod)
	{
		if (children != null && mod.equals(InterpreType.Run)) 
		{
			logShow.AppendShow("<------ForStatement interpret------>",InforType.Infor);
			
			//处理of循环for(one of sups)语法
			if(this.bOfStatement && null != ofState && ofState.strOperator.equals("of") &&
				null != this.ofState.strLeftValue && null != this.ofState.strRightValue &&
				!ofState.strLeftValue.isEmpty() && !ofState.strRightValue.isEmpty())
			{
				FindObjMemResult rValue = null;
				Vector<Object> Values = null;
				String strType = null;
				
				//寻找又参数
				rValue = getObjAndMem(ofState.strRightValue,"","");
				if(null != rValue)
				{
					//根据找到结果取值
					if(rValue.whichUse.equals(WhichValue.fojb))
					{
						Values = rValue.fobj.Values;
						strType = rValue.fobj.strType;
					}
					else if(rValue.whichUse.equals(WhichValue.member))
					{
						Values = rValue.member.Values;
						strType = rValue.member.strType;
					}
					else if(rValue.whichUse.equals(WhichValue.valueFromArray))
					{
						Values = rValue.member.Values;
						strType = rValue.strType;
					}
				}
				
				//找到
				if(null != Values)
				{
					//根据定义进行循环
					for(int t=0; t<Values.size(); t++)
					{
						setParam(ofState.strLeftValue,strType,Values.get(t));						
		
						//检查是否需要调过
						if(!isContinued())
							super.interpret(mod);
						//检查是否需要停止
						if(isBreaked())
							break;
						else if(isReturned())
					        break;
					}
					//消耗掉可能循环结束时正好错过的标记
					if(isBreaked())
					{}
					else if(isReturned())
					{}
				}
				//没有找到
				else
				{
					InforLogBuilder infor = new InforLogBuilder();
					infor.append(ofState.strRightValue);
					infor.append(" can't be found!!");
					logShow.AppendShow(infor.toString(),InforType.Warn);
				}
			}
			//处理for(int t = 2; t > 3; t++)语法
			else
			{
				//计算初值加入变量
				if(null != intiValue)
				{
					Integer ini = Integer.valueOf(intiValue.strInitial);
					setParam(intiValue.strName,intiValue.StrType,ini);
				}
				Integer iStep = null;
				if(null != StepOper && null != StepOper.strRightValue)
				{
					if(!StepOper.strRightValue.isEmpty())
						iStep = Integer.valueOf(StepOper.strRightValue);
					else
						iStep = new Integer(0);
				}
				
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
								//第一次执行条件语句
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
									logShow.AppendShow("For statment condition wrong",InforType.Warn);
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
										{
											bContiue = false;
											break;
										}
										else if(isReturned())
										{
											bContiue = false;
									        break;
										}
									}
									else if(bCondition)
									{
										logShow.AppendShow("For statment statement wrong",InforType.Warn);
										bContiue = false;
										break;
									}
									bCondition = true;
												
									//执行步进
									if(bContiue && null != StepOper)
									{
										FindObjMemResult lleft = getObjAndMem(StepOper.strLeftValue, "", "");
										if(null != lleft)
											OperateOnResult(StepOper.strOperator,lleft,iStep);
										else
										{
											InforLogBuilder infor = new InforLogBuilder();
											infor.append(StepOper.strLeftValue);
											infor.append(" can't be found!!");
											logShow.AppendShow(infor.toString(),InforType.Warn);
											bContiue = false;
											break;
										}
									}
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
}
