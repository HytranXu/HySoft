package HyRules;

public class ASTPostfixExpression extends SimpleNode 
{
	private String strOper = new String();
	private String strValue;
	private boolean bArray = false;
	//是否等待子节点完成
	private boolean bWaitForChild = false;
	//oper下标，left数组名,right变量名
	private OperationDef arrayItem = null;
	//是否value直接使用无需计算
	private boolean bDirectValue = false;
	
	public void setValueNotCalc(boolean bNotCalc) 
	{
		this.bDirectValue = bNotCalc;
	}
	public void setWaitForChild(boolean bwait) 
	{
		this.bWaitForChild = bwait;
	}
	
	public void setStrOper(String strOper) 
	{
		this.strOper = strOper;
	}

	public void setStrValue(String strValue) 
	{
		this.strValue = strValue;
	}

	public void setArrayItem(OperationDef arrayItem) 
	{
		this.arrayItem = arrayItem;
		bArray = true;
	}
	
	public ASTPostfixExpression(int id) 
	{
		super(id);
	}

	public ASTPostfixExpression(HySoft p, int id) 
	{
		super(p, id);
	}
	//展示内容	
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("PostfixExpression\n{\n");
		if(bArray)
		{
			infor.append("\tArrayItem\n\t{\n");
			InforLogBuilder arrinfo = new InforLogBuilder();
			arrinfo.append("\t\tArray : ");
			arrinfo.append(arrayItem.strLeftValue);
			//oper下标，left数组名,right变量名
			//添加数组索引
			String[] indexs = arrayItem.strOperator.split(",");
			for(String in : indexs)
			{
				arrinfo.append("[");
				arrinfo.append(in);
				arrinfo.append("]");
			}
			if(arrayItem.strRightValue != null)
			{
				indexs = arrayItem.strRightValue.split(",");
				if(indexs.length > 1)
				{
					arrinfo.append(".");
					arrinfo.append(indexs[0]);
					for(int t=1; t<indexs.length; t++)
					{
						arrinfo.append("[");
						arrinfo.append(indexs[t]);
						arrinfo.append("]");
					}
				}
				else
				{
					arrinfo.append(".");
					arrinfo.append(arrayItem.strRightValue);
				}
			}

			infor.append(arrinfo.toString());
			infor.append("\n\t}");
		}
		else
		{
			infor.append("\tOperation : ");
			infor.append(strOper);
			infor.append("\tValue : ");
			infor.append(strValue);
		}
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
	
	//根据内容进行计算
	private void calculate(String strOper)
	{
		//输出调试信息
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("Get variable : ");
		if(null != strValue)
		{
			infor.append(strValue);
			if(null != strOper)
				infor.append(strOper);
		}
		//oper下标，left数组名,right变量名 
		else if(null != arrayItem)
		{
			if(null != arrayItem.strLeftValue)
			{
				infor.append("\nArrayName : ");
				infor.append(arrayItem.strLeftValue);
			}
			if(null != arrayItem.strOperator)
			{
				infor.append("\nArrayIndex : ");
				infor.append(arrayItem.strOperator);
			}
			if(null != arrayItem.strRightValue)
			{
				infor.append("\nArrayItemAttr : ");
				infor.append(arrayItem.strRightValue);
			}
			
			if(null != strOper)
				infor.append(strOper);
		}
		logShow.AppendShow(infor.toString(),InforType.Debug);
		
		//右值是非数组
		if(null != strValue && !strValue.isEmpty())
		{
			FindObjMemResult result = getObjAndMem(strValue,"","");
			if(null != result)
			{
				FindObjMemResult bacup = null;
				if(null != strOper && !strOper.isEmpty() && strOper.equals("!"))
					bacup = result;
				else
					bacup = bakResult(result);

				OperateOnResult(strOper,result);
				PushVariableToStack(bacup);
				
				infor = new InforLogBuilder();
				infor.append("Push value: ");
				infor.append(strValue);
				logShow.AppendShow(infor.toString(),InforType.Debug);
			}
			else
			{
				infor = new InforLogBuilder();
				infor.setImportant(true);
				infor.append("Variable not found!!!");
				logShow.AppendShow(infor.toString(),InforType.Error);
				this.pushStack(null);
			}
		}
		//右值是数组
		else if(null != arrayItem)
		{
			//oper下标，left数组名,right变量名
			FindObjMemResult result = getObjAndMem(arrayItem.strLeftValue,arrayItem.strOperator,arrayItem.strRightValue);
			if(null != result)
			{
				FindObjMemResult bacup = null;
				if(null != strOper && !strOper.isEmpty() && strOper.equals("!"))
					bacup = result;
				else
					bacup = bakResult(result);

				OperateOnResult(strOper,result);
				PushVariableToStack(bacup);
				
				infor = new InforLogBuilder();
				infor.append("Push value: ");
				infor.append(arrayItem.strLeftValue);
				logShow.AppendShow(infor.toString(),InforType.Debug);
			}
			else
			{
				infor = new InforLogBuilder();
				infor.setImportant(true);
				infor.append("Variable not found!!!");
				logShow.AppendShow(infor.toString(),InforType.Error);
				this.pushStack(null);
			}
		}
		//错误发生
		else
		{
			infor = new InforLogBuilder();
			infor.setImportant(true);
			infor.append("No rightvalue Exists!!!");
			logShow.AppendShow(infor.toString(),InforType.Error);
			this.pushStack(null);
		}
	}
	//执行
	public void interpret(InterpreType mod)
	{
		logShow.AppendShow("<------PostfixExpression interpret------>",InforType.Infor);
		
		//没有操作符
		if(strOper.isEmpty())
		{
			//只有子值
			if(bWaitForChild)
			{
				//这个是调用函数直接调用返回即可
				logShow.AppendShow("Calculate child",InforType.Debug);
				super.interpret(mod);
			}
			//直接获取值
			else
			{
				//简单类直接读取返回，比如字符串、数字等
				if(bDirectValue)
				{
					InforLogBuilder infor = new InforLogBuilder();
					infor.append("Push value: ");
					infor.append(strValue);
					logShow.AppendShow(infor.toString(),InforType.Debug);
					this.pushStack(strValue);
				}
				//需要获取的比如变量名称
				else
					calculate(null);
			}
		}
		//需要运算
		else
			calculate(strOper);
	}
}
