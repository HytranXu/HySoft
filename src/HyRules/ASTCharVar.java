package HyRules;

import java.util.Vector;

public class ASTCharVar extends SimpleNode 
{
	//当赋值时采用表达式或调用函数时需要这些完成后再处理
	private boolean bWaitForChild = false;
		
	//构造函数
	public ASTCharVar(int id) 
	{
		super(id);
	}

	public ASTCharVar(HySoft p, int id) 
	{
		super(p, id);
	}
	//执行解释
	@SuppressWarnings("unchecked")
	private void interpretForRun()
	{
		logShow.AppendShow("<------CharVar interpret------>",InforType.Infor);
		
		//获取向哪里添加可能是本地符号表或全局符号表
		UseWhichTable bGlobal[] = new UseWhichTable[1];
		DataTAB tab = getCurentTab(bGlobal);
		printUseWhichTable(bGlobal[0]);
		
		//主项添加到符号表
		int result = 0;
		int MaxIndex = 0;
		for(NamesForVar var : VariableNames)
		{
			String key = var.name;
			//脚本中传递的初始化值
			String value = Arraymap.get(key);
			
			//检查是数组还是单项
			String[] names = key.split(",");
			//数组
			if(names.length > 1)
			{
				Vector<Integer> indexs = new Vector<Integer>();
				for(int t=1; t<names.length; t++)
				{
					int temp = Integer.valueOf(names[t]);
					indexs.add(Integer.valueOf(names[t]));
					MaxIndex = MaxIndex > temp ? MaxIndex : temp;
				}			
				
				//本身带的初始化值
				Vector<Object> iniValues = null;
				if(!value.isEmpty())
				{
					iniValues = new Vector<Object>();
					String[] values = value.split(",");
					if(null != values)
					{
						for(String obj : values)
						{
							if(null != obj && !obj.isEmpty())
								iniValues.add(obj.charAt(0));
							else
								iniValues.add(new Character('x'));
						}
					}
				}
				//需要计算的
				else
				{
					Vector<Object> getValues = null;		
					if(var.bWaitForChild)
					{
						InforLogBuilder infor = new InforLogBuilder();
						infor.append("Try add calculated data name: ");
						infor.append(key);
						infor.append(" type: char value: ");
						infor.append(value);
						logShow.AppendShow(infor.toString(),InforType.Debug);
						
						bWaitForChild = true;
						super.interpretNext(InterpreType.Run);
						Object objv = this.peekStack();
						if(null != objv)
						{
							if(null != objv && objv instanceof String)
							{
								//可能是return回来的也可能是其他回来的都需要popstatck
								if(isReturned())
								{
									this.popStack();
									objv = this.popStack();
								}
								else
									objv = this.popStack();
								
								//取对象的数组
								if(objv instanceof ObjData)
									objv = ((ObjData)objv).Values;
							}
							else
								this.popStack();
							
							if(null != objv && (objv instanceof Vector<?>))
								getValues = (Vector<Object>)objv;
						}
						else
							this.popStack();
					}

					//把数组复制一份
					if(null != getValues)
						iniValues = HyRuleNode.copyVector(getValues.size(), getValues);
					else
						iniValues = new Vector<Object>();
				}
				int t = iniValues.size() > 0? iniValues.size()-1:0;
				for(; t<MaxIndex; t++)
					iniValues.add(new Character('x'));
				
				result = tab.setParamArray(names[0],"char",PrepareArray(indexs,iniValues),false,true);
			}
			//单个
			else
			{
				if(value != null && !value.isEmpty())
					result = tab.setParam(key,"char",value.charAt(0),true);
				else
				{
					Character bValue = null;
					if(var.bWaitForChild)
					{
						InforLogBuilder infor = new InforLogBuilder();
						infor.append("Try add calculated  data name: ");
						infor.append(key);
						infor.append(" type: char value: ");
						infor.append(value);
						logShow.AppendShow(infor.toString(),InforType.Debug);
						
						bWaitForChild = true;
						super.interpretNext(InterpreType.Run);
						Object objv = this.peekStack();
						if(null != objv)
						{
							if(null != objv && objv instanceof String)
							{
								//可能是return回来的也可能是其他回来的都需要popstatck
								if(isReturned())
								{
									this.popStack();
									objv = this.popStack();
								}
								else
									objv = this.popStack();
								
								//取对象的值
								if(objv instanceof ObjData)
									objv = ((ObjData)objv).Value;
							}
							else
								this.popStack();
						}
						else
							this.popStack();
						
						//取具体值
						if(null != objv && objv instanceof Character)
							bValue = getChar(objv);
					}
					
					if(null != bValue)
						result = tab.setParam(key,"char",bValue,true);
					else 
						result = tab.setParam(key,"char",new Character('x'),true);
				}			
			}
			
			//根据结果展示信息
			if(result == DataTAB.dt_OK)
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.append("Add data name: ");
				infor.append(key);
				infor.append(" type: char value: ");
				infor.append(value);
				logShow.AppendShow(infor.toString(),InforType.Debug);
			}
			else if(result == DataTAB.dt_NameExist)
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.setImportant(true);
				infor.append("Add data wrong name:");
				infor.append(key);
				infor.append(" already exists.");
				logShow.AppendShow(infor.toString(),InforType.Error);
			}
			else if(result == DataTAB.dt_ParamWrong)
				logShow.AppendShow("Add data wrong param.",InforType.Error);
		}
	}

	//编译解释
	private void interpretForCompile()
	{
		//获取向哪里添加可能是本地符号表或全局符号表
		DataTAB tab = getDataDefTab();
		String dataName = (String)peekStack();
		
		//主项添加到符号表
		int result = 0;
		int MaxIndex = 0;
		for(String key : Arraymap.keySet())
		{
			//脚本中传递的初始化值
			String value = Arraymap.get(key);
			
			//检查是数组还是单项
			String[] names = key.split(",");
			//数组
			if(names.length > 1)
			{
				Vector<Integer> indexs = new Vector<Integer>();
				for(int t=1; t<names.length; t++)
				{
					int temp = Integer.valueOf(names[t]);
					indexs.add(Integer.valueOf(names[t]));
					MaxIndex = MaxIndex > temp ? MaxIndex : temp;
				}
				String[] values = value.split(",");
				Vector<Object> iniValues = new Vector<Object>();
				for(String obj : values)
				{
					if(null != obj && !obj.isEmpty())
						iniValues.add(obj.charAt(0));
					else
						iniValues.add(new Character('x'));
				}
				int t = iniValues.size() > 0? iniValues.size()-1:0;
				for(; t<MaxIndex; t++)
					iniValues.add(new Character('x'));
				
				result = tab.addDataDefArray(dataName,names[0],"char",PrepareArray(indexs,iniValues),false);
			}
			//单个
			else
			{
				if(value != null && !value.isEmpty())
					result = tab.addDataDef(dataName,key,"char",value.charAt(0));
				else
					result = tab.addDataDef(dataName,key,"char",new Character('x'));
			}
			
			//根据结果展示信息
			if(result == DataTAB.dt_OK)
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.append("Add data type name: ");
				infor.append(dataName);
				infor.append(" mem--> type: char name: ");
				infor.append(key);
				infor.append(" value: ");
				infor.append(value);
				logShow.AppendShow(infor.toString(),InforType.Debug);
			}
			else if(result == DataTAB.dt_NameExist)
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.setImportant(true);
				infor.append("Add data type wrong name:");
				infor.append(dataName);
				infor.append(" mem--> type: char name: ");
				infor.append(key);
				infor.append(" already exists.");
				logShow.AppendShow(infor.toString(),InforType.Error);
			}
			else if(result == DataTAB.dt_ParamWrong)
				logShow.AppendShow("Add data type wrong param.",InforType.Error);
		}
	}
	//执行函数
	public void interpret(InterpreType mod)
	{
		if(mod.equals(InterpreType.Compile))
			interpretForCompile();
		else if(mod.equals(InterpreType.Build) || !bInitialed)
		{
			interpretForRun();
			bInitialed = true;
		}

		if(!bWaitForChild)
			super.interpret(mod);
	}
	//展示内容
	public void dumpContent(String prefix) 
	{
		InforLogBuilder infor = new InforLogBuilder();
		infor.append("CharVar\n{");
		for(String key : Arraymap.keySet())
		{
			String value = Arraymap.get(key);
			infor.append("\n\tName : ");
			infor.append(key);
			infor.append("\tvalues : ");
			infor.append(value);
		}
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		super.dumpMember(prefix);
	}
}
