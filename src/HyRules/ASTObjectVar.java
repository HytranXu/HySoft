package HyRules;

import java.util.Vector;

public class ASTObjectVar extends SimpleNode 
{
	private boolean bWaitForChild = false;
	private String strType = null;
	
	public void setStrType(String strType) 
	{
		this.strType = strType;
	}

	//构造函数
	public ASTObjectVar(int id) 
	{
		super(id);
	}

	public ASTObjectVar(HySoft p, int id) 
	{
		super(p, id);
	}
	
	//执行解释
	@SuppressWarnings("unchecked")
	private void interpretForRun()
	{
		logShow.AppendShow("<------ObjectVar interpret------>",InforType.Infor);
		
		//获取向哪里添加可能是本地符号表或全局符号表
		UseWhichTable bGlobal[] = new UseWhichTable[1];
		DataTAB tab = getCurentTab(bGlobal);
		printUseWhichTable(bGlobal[0]);
		DataTAB DataDeftab = getDataDefTab();
		
		//主项添加到符号表
		int result = 0;
		DataTAB tabwillbeused = null;
		ObjDef objd = null;
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
					String[] values = value.split(",");
					iniValues = new Vector<Object>();
					for(String obj : values)
					{
						//对象名必须非空
						if(!obj.isEmpty())
						{
							DataTAB[] tbdused = new DataTAB[1];
							//查找赋值值是否存在
							ObjData fobj = findObjbyName(obj,tbdused);
							tabwillbeused = tbdused[0];
								
							//如果赋值值存在表明对象定义存在
							if(null != fobj)
								iniValues.add(fobj.copyValue(true));
							//如果没有赋值值看对象是否定义
							else if(null != (objd =  DataDeftab.lookupDataDef(strType)))
							{
								tabwillbeused = null;
								iniValues.add(objd.copy());
							}
							else
							{
								InforLogBuilder infor = new InforLogBuilder();
								infor.setImportant(true);
								infor.append("Add data array wrong type:");
								infor.append(strType);
								infor.append(" !Type Not Defines.");
								logShow.AppendShow(infor.toString(),InforType.Error);
								return;
							}
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
						infor.append(" type: ");
						infor.append(strType);
						infor.append(" value: ");
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

				if(iniValues.size() < MaxIndex)
				{
					if(null == objd)
						objd =  DataDeftab.lookupDataDef(strType);
					if(null != objd)
					{
						int tmp = iniValues.size() > 0? iniValues.size()-1:0;
						for(; tmp<MaxIndex; tmp++)
							iniValues.add(objd.copy());
					}
				}
					
				if(tabwillbeused != null)
					result = tabwillbeused.setParamArray(names[0],strType,PrepareArray(indexs,iniValues),true,true);
				else
					result = tab.setParamArray(names[0],strType,PrepareArray(indexs,iniValues),true,true);
			}
			//单个
			else
			{
				//查找赋值对象
				ObjData fobj = null;
				if(null != value && !value.isEmpty())
				{
					DataTAB[] tbdused = new DataTAB[1];
					//查找赋值值是否存在
					fobj = findObjbyName(value,tbdused);
					tabwillbeused = tbdused[0];
				}
					
				//如果赋值值存在表明对象定义存在
				if(fobj != null)
					result = tabwillbeused.setParam(key,strType,fobj.copyValue(true),true);
				//如果没有赋值值看对象是否定义
				else
				{
					if(var.bWaitForChild)
					{
						InforLogBuilder infor = new InforLogBuilder();
						infor.append("Try add calculated  data name: ");
						infor.append(key);
						infor.append(" type: ");
						infor.append(strType);
						infor.append(" value: ");
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
									fobj = (ObjData)objv;
							}
							else
								this.popStack();
						}
						else
							this.popStack();
					}
					
					if(null == fobj)
						objd = DataDeftab.lookupDataDef(strType);
					else
						objd = fobj.copyValue(true);
					if(null != objd)
						result = tab.setParam(key,strType,objd.copy(),true);
					else
					{
						InforLogBuilder infor = new InforLogBuilder();
						infor.setImportant(true);
						infor.append("Add data wrong type:");
						infor.append(strType);
						infor.append(" !Type Not Defines.");
						logShow.AppendShow(infor.toString(),InforType.Error);
						return;
					}
				}
			}
				
			//根据结果展示信息
			if(result == DataTAB.dt_OK)
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.append("Add data name: ");
				infor.append(key);
				infor.append(" type: ");
				infor.append(strType);
				infor.append(" value: ");
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
		ObjDef fobj = null;
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
					if(!obj.isEmpty())
					{
						//查找赋值类型是否存在
						fobj = tab.lookupDataDef(obj);
							
						//赋值值存在
						if(null != fobj)
							iniValues.add(fobj.copy());
						else
						{
							InforLogBuilder infor = new InforLogBuilder();
							infor.setImportant(true);
							infor.append("Add data array wrong type:");
							infor.append(key);
							infor.append(" !Type Not Defines.");
							logShow.AppendShow(infor.toString(),InforType.Error);
							return;
						}
					}
				}
				if(iniValues.size() < MaxIndex)
				{
					if(null == fobj)
						fobj =  tab.lookupDataDef(strType);
					if(null != fobj)
					{
						int t = iniValues.size() > 0? iniValues.size()-1:0;
						for(; t<MaxIndex; t++)
							iniValues.add(fobj.copy());
					}
				}	
				result = tab.addDataDefArray(dataName,names[0],strType,PrepareArray(indexs,iniValues),true);
			}
			//单个
			else
			{
				//查找赋值类型是否存在
				fobj = tab.lookupDataDef(strType);
					
				if(fobj != null)
					result = tab.addDataDef(dataName,key,strType,fobj.copy());
				else
				{
					InforLogBuilder infor = new InforLogBuilder();
					infor.setImportant(true);
					infor.append("Add data array wrong type:");
					infor.append(strType);
					infor.append(" name:");
					infor.append(key);
					infor.append(" !Type Not Defines.");
					logShow.AppendShow(infor.toString(),InforType.Error);
					return;
				}
			}
			
			//根据结果展示信息
			if(result == DataTAB.dt_OK)
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.append("Add data type name: ");
				infor.append(dataName);
				infor.append(" mem--> type: ");
				infor.append(strType);
				infor.append(" name: ");
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
				infor.append(strType);
				infor.append(" name: ");
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
  		infor.append("ObjectVar\n{\n");
  		infor.append("\tType : ");
  		infor.append(strType);
		//逐项展示
		for(String key : Arraymap.keySet())
		{
			String value = Arraymap.get(key);
			InforLogBuilder namebd = new InforLogBuilder();
			
			//需要关注是否是数组
			String[] names = key.split(",");
			//第一项是名字
			namebd.append(names[0]);
			//从第二项开始是数组各层个数比如[2][3]中的2和3
			for(int t=1; t<names.length; t++)
			{
				namebd.append("[");
				namebd.append(names[t]);
				namebd.append("]");
			}
			//展示信息
			infor.append("\n\tName : ");
			infor.append(namebd.toString());
			infor.append("\tvalues : ");
			infor.append(value);
		}
		infor.append("\n}");
		logShow.AppendShow(infor.toString(),InforType.Debug);
		//继续显示子项
		super.dumpMember(prefix);
	}
}
