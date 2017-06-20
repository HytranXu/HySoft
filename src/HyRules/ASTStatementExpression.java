package HyRules;

public class ASTStatementExpression extends SimpleNode 
{
	private String strLeftValue;
	private String strRightVlaue;
	private String strOper;
	//[0]数组名 [1]下标名 [2]变量名
	private String[] strArrayItem = null;
	private boolean bArray = false;
	private boolean bPopNeed = false;
	
	public void setPopNeed(boolean bNeed) 
	{
		bPopNeed = bNeed;
	}
	
	public void setStrLeftValue(String strLeftValue) 
	{
		this.strLeftValue = strLeftValue;
		bArray = false;
	}

	public void setStrRightVlaue(String strRightVlaue) 
	{
		this.strRightVlaue = strRightVlaue;
	}

	public void setStrOper(String strOper) 
	{
		this.strOper = strOper;
	}
	
	public void setStrArrayItem(String strArrayItem[]) 
	{
		this.strArrayItem = strArrayItem;
		bArray = true;
	}

	public ASTStatementExpression(int id) 
	{  
		super(id);
	}

	public ASTStatementExpression(HySoft p, int id) 
	{
		super(p, id);
	}
	//展示
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("StatementExpression\n{\n");
		if(bArray)
		{
			InforLogBuilder inforbd = new InforLogBuilder();
			inforbd.append("\tArrayItem : ");
			//添加名称
			inforbd.append(strArrayItem[0]);
			
			//添加数组索引
			String[] indexs = strArrayItem[1].split(",");
			for(String in : indexs)
			{
				inforbd.append("[");
				inforbd.append(in);
				inforbd.append("]");
			}
			//可能是数组对象的成员，成员也可能是数组
			if(strArrayItem[2] != null)
			{
				inforbd.append(".");
				indexs = strArrayItem[2].split(",");
				if(indexs.length > 1)
				{
					inforbd.append(indexs[0]);
					for(int t=1; t<indexs.length; t++)
					{
						inforbd.append("[");
						inforbd.append(indexs[t]);
						inforbd.append("]");
					}
				}
				else
					inforbd.append(strArrayItem[2]);
			}
			infor.append(inforbd.toString());	
		}
		else
		{
			infor.append("\tLeftValue : ");
			infor.append(strLeftValue);
		}
		
		infor.append("\tOperator : ");
		infor.append(strOper);
		infor.append("\tRightValue : ");
		infor.append(strRightVlaue);
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	//执行
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------StatementExpression interpret------>",InforType.Infor);
		
		//存在=号，获取左边数值
		if(null != strOper && !strOper.isEmpty())
		{
			//展示帮助信息
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("LeftValue : ");
			infor.append(strLeftValue);
			infor.append("  Operator : ");
			infor.append(strOper);
			infor.append("  RightValue : ");
			infor.append(strRightVlaue);

			////[0]数组名 [1]下标名 [2]变量名
			if(null != strArrayItem)
			{
				if(null != strArrayItem[0])
				{
					infor.append("\nArrayName : ");
					infor.append(strArrayItem[0]);
				}
				if(null != strArrayItem[1])
				{
					infor.append("\nArrayIndex : ");
					infor.append(strArrayItem[1]);
				}
				if(null != strArrayItem[2])
				{
					infor.append("\nArrayItemAttr : ");
					infor.append(strArrayItem[2]);
				}
			}
			logShow.AppendShow(infor.toString(),InforType.Debug);
			
			//左值是非数组
			if(null != strLeftValue && !strLeftValue.isEmpty())
			{
				FindObjMemResult result = getObjAndMem(strLeftValue,"","");
				super.interpret(mod);
				Object rValue = this.popStack();
				if(null != rValue)
					setVariableValue(result,rValue);
				else
					logShow.AppendShow("PopStack null in StatementExpression",InforType.Infor);
			}
			//左值是数组
			else if(null != strArrayItem)
			{
				FindObjMemResult result = getObjAndMem(strArrayItem[0],strArrayItem[1],strArrayItem[2]);
				super.interpret(mod);
				Object rValue = this.popStack();
				if(null != rValue)
					setVariableValue(result,rValue);
				else
					logShow.AppendShow("StatementExpression PopStack null in StatementExpression",InforType.Infor);
			}
			//错误发生
			else
			{
				infor = new InforLogBuilder();
				infor.setImportant(true);
				infor.append("No leftvalue Exists!!!");
				logShow.AppendShow(infor.toString(),InforType.Error);
			}
		}
		//不存在直接执行子即可
		else
		{
			logShow.AppendShow("Calculate child",InforType.Debug);
			super.interpret(mod);
			if(bPopNeed)
			{
				if(isReturned())
					this.popStack();
				this.popStack();
			}
		}
	}
}
