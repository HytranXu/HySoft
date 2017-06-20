package HyRules;

import java.io.Reader;
import java.io.Writer;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Pattern;

//错误类型定义
enum ErrorType
{
	Error, Warn,Infor;
}
//编译器运行模式
enum InterpreType
{
	Compile, Build, Run;
}
//使用哪个符号表
enum UseWhichTable
{
	LocalFunc,FunctionDefine,LoadOrLibrary;
}
class NamesForVar
{
	public String name = null;
	public boolean bWaitForChild = false;
	
	public NamesForVar(String strName,boolean bWaitforCh) 
	{
		this.name = strName;
		this.bWaitForChild = bWaitforCh;
	}
}
//自定义节点
public class HyRuleNode
{
  //protected static Writer out = new PrintWriter(System.out);
  //protected static Reader in = new InputStreamReader(System.in);
	
	//当前处理数据表需要在libary或Loaddeclare中声明并设置，本地符号表
	protected DataTAB g_Systab = null;
	//当前处理数据表需要在funcDefine、DataDefine中声明并设置，本地符号表
	protected DataTAB func_Systab = null;
	//当前处理数据表需要在functionDefine中声明并设置，本地符号表
	protected DataTAB function_Systab = null;
	//工作栈
	protected Stack<Object> sysStack = null;
	//当前节点，子节点只需要在此节点增加变量，但查询时如果在此节点查不到需要继续查全局符号表
	protected DataTAB cur_tab = null;
	//全局数据表
	protected DataTAB g_DataDefine = null;
	protected LogOrShow logShow = null;
	//只有debug下展示
	protected static boolean bDebug = false;
	protected boolean bInitialed = false;
	//声明变量时可能存在前后依赖关系比如bool btt = true,ttResult = (btt && true)
	protected Vector<NamesForVar> VariableNames = new Vector<NamesForVar>();
	//根据函数名保存参数信息
	protected FuncInfor funDefineInfor = null;
	//根据函数名保存参数信息
	protected FunctionLib parentFun = null;

	//函数参数及语法树管理
	protected FunctionLib IniLib()
	{
		if(null == parentFun)
			parentFun = new FunctionLib(logShow);
		return parentFun;
	}
	protected FuncInfor InitFuncForFuncDef(FunctionLib pLib)
	{
		if(null != pLib)
			parentFun = pLib;
		funDefineInfor = new FuncInfor(logShow);
		return funDefineInfor;
	}
	protected void setFunc(FuncInfor func, FunctionLib pFun)
	{
		if(null != func)
			funDefineInfor = func;
		if(null != pFun)
			parentFun = pFun;
	}
	public void AddFunLib(String strLibName, FuncInfor funInf)
	{
		if(null != parentFun)
			parentFun.AddFunLib(strLibName, funInf);
	}
	public FuncInfor getFunLib(String strLibName)
	{
		FuncInfor param = null;
		if(null != parentFun)
			param = parentFun.getFunLib(strLibName);
		return param;
	}
	public void AddFunInfor(String strFunName, FuncParameter param,String funRet ,SimpleNode func)
	{
		if(null != funDefineInfor)
			funDefineInfor.AddFunInfor(strFunName, param,funRet, func);
	}
	public FuncParameter getFunInfor(String strFunName)
	{
		return funDefineInfor.getFunInfor(strFunName);
	}
	
	//设置看那些变量需要计算出来
	public void addToVarName(String strName,boolean bWaitforChild) 
	{
		if(null != strName && !strName.isEmpty())
		{
			NamesForVar names = new NamesForVar(strName,bWaitforChild);
			VariableNames.add(names);
		}
	}
	//获取日志级别
	public static void setDebug(boolean bDebug) 
	{
		HyRuleNode.bDebug = bDebug;
	}
	//设置日志组件
	public void setLogShow(LogOrShow ls)
	{
		if(null != ls)
			logShow = ls;
	}
	//在libary、Loaddeclare中使用
	public DataTAB[] IniTabAndStackForLoadOrLib(Stack<Object> sStack, String strVariableTableName) 
	{
		DataTAB[] result = new DataTAB[2];
		if(null != sStack)
			this.sysStack = sStack;
		g_Systab = new DataTAB(strVariableTableName,logShow);
		cur_tab = g_Systab;
		//全局数据表
		g_DataDefine = new DataTAB("G_DataDefine",logShow);
		
		result[0] = g_Systab;
		result[1] = g_DataDefine;
		return result;
	}
	//在FuncDefine中使用
	public DataTAB setTabAndStackForFuncDef(DataTAB gt, Stack<Object> sStack, String strVariableTableName, DataTAB gDataDefine) 
	{
		if(null != gt)
			this.g_Systab = gt;
		if(null != sStack)
			this.sysStack = sStack;
		func_Systab = new DataTAB(strVariableTableName,logShow);
		cur_tab = func_Systab;
		if(null != gDataDefine)
			g_DataDefine = gDataDefine;
		
		return func_Systab;
	}

	//在FunctionDefine中使用
	public DataTAB setTabAndStackForFunction(DataTAB gt,DataTAB ft, Stack<Object> sStack, String strVariableTableName, DataTAB gDataDefine) 
	{
		if(null != gt)
			this.g_Systab = gt;
		if(null != sStack)
			this.sysStack = sStack;
		if(null != ft)
			this.func_Systab = ft;
		function_Systab = new DataTAB(strVariableTableName,logShow);
		cur_tab = function_Systab;
		if(null != gDataDefine)
			g_DataDefine = gDataDefine;
		
		return function_Systab;
	}
	//除libary、Loaddeclare、FuncDefine外在其他节点使用
	public void setTabAndStack(DataTAB gt,DataTAB ft,DataTAB functiontable, Stack<Object> sStack, DataTAB gDataDefine) 
	{
		if(null != gt)
		{
			this.g_Systab = gt;
			cur_tab = gt;
		}
		if(null != sStack)
			this.sysStack = sStack;
		if(null != ft)
		{
			func_Systab = ft;
			cur_tab = ft;
		}
		if(null != functiontable)
		{
			function_Systab = functiontable;
			cur_tab = functiontable;
		}
		if(null != gDataDefine)
			g_DataDefine = gDataDefine;
	}
	//获取当前正在使用的符号节点
	DataTAB getCurentTab(UseWhichTable[] b)
	{
		if(null == cur_tab)
		{
			if(null != function_Systab)
			{
				b[0] = UseWhichTable.LocalFunc;
				return function_Systab;
			}
			else if(null != func_Systab)
			{
				b[0] = UseWhichTable.FunctionDefine;
				return func_Systab;
			}
			else
			{
				b[0] = UseWhichTable.LoadOrLibrary;
				return g_Systab;
			}
		}
		else
		{
			if(cur_tab.equals(g_Systab))
				b[0] = UseWhichTable.LoadOrLibrary;
			else if(cur_tab.equals(func_Systab))
				b[0] = UseWhichTable.FunctionDefine;
			else
				b[0] = UseWhichTable.LocalFunc;
			return cur_tab;
		}
	}
	//打印使用哪个符号表
	void printUseWhichTable(UseWhichTable uw)
	{
		if(uw.equals(UseWhichTable.LoadOrLibrary))
			logShow.AppendShow("Use LoadLibray table",InforType.Debug);
		else if(uw.equals(UseWhichTable.FunctionDefine))
			logShow.AppendShow("Use FuncLib table",InforType.Debug);
		else
			logShow.AppendShow("Use LocalFunction table",InforType.Debug);
	}
	//获取数据定义表
	DataTAB getDataDefTab()
	{
		if(null != g_DataDefine)
			return g_DataDefine;
		else
			return null;
	}
	//深拷贝数组
	@SuppressWarnings("unchecked")
	protected static Vector<Object> copyVector(int counts, Vector<Object> source)
	{
		Vector<Object> result = new Vector<Object>();
		Vector<Object> from = source;
		if(null == from)
			return result;
		
		for(int tmp=0; tmp<counts; tmp++)
		{
			Object obj = null;
			if(tmp < from.size())
			{
				obj = from.get(tmp);
				if(null != obj)
				{
					if(obj instanceof Vector<?>)
					{
						Vector<Object> re = (Vector<Object>)obj;
						result.add(copyVector(re.size(),re));
					}
					else if(obj instanceof ObjData)
					{
						ObjData re = (ObjData)obj;
						if(re.dataType.equals(DataType.SelfDef))
							result.add(re.copyValue(true));
						else
							result.add(re.copyValue());
					}
					else if(obj instanceof ObjDef)
					{
						ObjDef re = (ObjDef)obj;
						result.add(re.copy());
					}
					else if(obj instanceof Integer)
					{
						Integer re = new Integer((Integer)obj);
						result.add(re);
					}
					else if(obj instanceof Character)
					{
						Character re = new Character((Character)obj);
						result.add(re);
					}
					else if(obj instanceof String)
					{
						String re = new String((String)obj);
						result.add(re);
					}
					else if(obj instanceof Boolean)
					{
						Boolean re = new Boolean((Boolean)obj);
						result.add(re);
					}
					else if(obj instanceof Double)
					{
						Double re = new Double((Double)obj);
						result.add(re);
					}
					else
						result.add(obj);
				}
				else
					result.add(null);
			}
			else
				result.add(null);
		}
		return result;
	}
	/*
	 * 增加数组比如string[2][3][4]到符号表，此时indexs应该项分别是234
	 * 此时针对数组的最后[4]将使用iniValues进行初始化
	 */
	Vector<Object> PrepareArray(Vector<Integer> indexs, Vector<Object> iniValues)
	{
		Vector<Object> result = null;
		//之后最后[4]时才初始化数值
		if(indexs.size() > 1)
		{
			Integer tmpIndex = indexs.remove(0);
			result = new Vector<Object>();
			Vector<Object> temp = PrepareArray(indexs,iniValues);
			for(int tmp=0; tmp<tmpIndex; tmp++)
				result.add(copyVector(temp.size(),temp));
		}
		//最后[4]初始化数值
		else
		{
			Integer tmpIndex = indexs.remove(0);
			result = copyVector(tmpIndex.intValue(),iniValues);
		}
		
		return result;
	}
	//判断是否数字
	static boolean isDigit(String str)
	{
		Pattern pattern = Pattern.compile("[0-9]*");  
	    return pattern.matcher(str).matches();    
	}
	//将字符串标识符分解成单项默认使用".",不可以返回空项或" "
	static Object[] splitMemb(String strMembers)
	{
		return splitMemb(strMembers,"");
	}
	//制定分隔符,不可以返回空项或" "
	static Object[] splitMemb(String strMembers,String seprator)
	{
		return splitMemb(strMembers,seprator,false);
	}
	//默认使用"."作为分隔符
	static Object[] splitMemb(String strMembers,String seprator,boolean bCannull)
	{
		Vector<String> vResult = new Vector<String>();
		
		if(null != strMembers && !strMembers.isEmpty())
		{
			if(seprator.isEmpty())
				seprator = "\\.";
			
			strMembers = strMembers.replaceAll("\\[", ",");
			String[] memb = strMembers.split(seprator);
			for(String str : memb)
			{
				String[] arr = str.split("]");
				for(String str1 : arr)
				{
					String[] op = str1.split(",");
					for(String ob : op)
					{
						if(!bCannull)
						{
							if(!ob.isEmpty() && !ob.equals(" "))
								vResult.add(ob);
						}
						else
							vResult.add(ob);
					}
				}
			}
		}
		else if(null != strMembers)
			vResult.add(strMembers);
		return vResult.toArray();
	}
	//根据名字查变量
	public ObjData findObjbyName(String strName, DataTAB[] used)
	{
		return findObjbyName(strName,used,false);
	}
	public ObjData findObjbyName(String strName, DataTAB[] used,boolean bOnlyForThis)
	{
		ObjData fobj = null;
		//查找赋值值是否存在
		if(null != strName && !strName.isEmpty())
		{
			if(null != cur_tab)
			{
				fobj = (ObjData)cur_tab.lookup(strName);
				if(null != fobj)
				{
					used[0] = cur_tab;
					return fobj;
				}
			}
			if(null != this.function_Systab)
			{
				fobj = (ObjData)function_Systab.lookup(strName);
				if(null != fobj)
				{
					used[0] = function_Systab;
					return fobj;
				}
			}
			if(bOnlyForThis)
				return fobj;
			
			if(null != this.func_Systab)
			{
				fobj = (ObjData)func_Systab.lookup(strName);
				if(null != fobj)
				{
					used[0] = func_Systab;
					return fobj;
				}
			}
			if(null != this.g_Systab)
			{
				fobj = (ObjData)g_Systab.lookup(strName);
				if(null != fobj)
				{
					used[0] = g_Systab;
					return fobj;
				}
			}
		}
		return fobj;
	}
	//根据对象声明信息找对象或对象的成员属性
	//适合对象，数组，属性名分开场景
	//类型都是最终返回对象的类型
	//无论是member、value、values他们的拥有者都是fobj
	public FindObjMemResult getObjAndMem(String strOjbAndMem, String strArrayIndex, String strMem)
	{
		//根据名字查询对象
		FindObjMemResult result = getObjAndMembyNameAndMemName(strOjbAndMem);
		//根据数组信息检索
		if(null != result && null != strArrayIndex && !strArrayIndex.isEmpty())
		{
			if(result.whichUse.equals(WhichValue.member))
				result = getObjArrayItem(result.member,strArrayIndex);
			else if(null != result && result.whichUse.equals(WhichValue.fojb))
				result = getObjArrayItem(result.fobj,strArrayIndex);
			else
				result = null;
		}
		//根据属性名检索
		if(null != result && null != strMem && !strMem.isEmpty())
		{
			ObjData obd = result.fobj; 
			if(result.whichUse.equals(WhichValue.valueFromArray))
				result = getObjAndMemofMembyNameAarryOrMemName((ObjDef)result.value,strMem);
			else if(null != result && result.whichUse.equals(WhichValue.selfMemFromArray))
				result = getObjAndMemofMembyNameAarryOrMemName(result.sefMem,strMem);
			if(null != result && null == result.fobj)
				result.fobj = obd;
			else
				result = null;
		}
		return result;
	}
	//根据对象声明信息找对象或对象的成员属性类似 user1.strname
	public FindObjMemResult getObjAndMembyNameAndMemName(String strOjbAndMem)
	{
		FindObjMemResult result = null;
		if(null == strOjbAndMem || (null != strOjbAndMem && strOjbAndMem.isEmpty()))
			return null;
		
		//查询结果
		result = new FindObjMemResult();
		ObjData fobj = null;	//对象
		ObjDef fsubobj = null;	//对象自定义属性
		ObjData member = null;	//自定义属性的定义
		String strType = null;	//类型
		
		//将strObjInfor分成操作子查询对象
		Object obj[] = splitMemb(strOjbAndMem);
		boolean bOnlyLookThis = false;
		for(Object mem : obj)
		{
			//所有操作子都必须是字符串型
			if(mem instanceof String)
			{
				String infor = (String)mem;	
				//this操作只操作本节点本身
				if(infor.equals(FuncConst.THIS))
				{
					bOnlyLookThis = true;
					continue;
				}
				
				//第一次查变量对象本身
				if(null == fobj)
				{
					DataTAB[] tbdused = new DataTAB[1];
					//查找赋值值是否存在
					fobj = findObjbyName(infor,tbdused,bOnlyLookThis);
					//赋值操作左值应该已经存在
					if(null != fobj)
					{
						strType = fobj.strType;
						result.whichUse = WhichValue.fojb;
					}
					else
						return null;
				}
				//如果变量已经存在查变量对象的属性，此时必须是自定义对象
				else if(fobj.dataType.equals(DataType.SelfDef))
				{
					//如果上次已经查到对象属性，但根据查询仍需要查属性，就是对对象属性的属性查询
					//此时需要循环
					do
					{
						//第一次查对象属性
						if(null == member)
						{
							//类型必须正确
							if(null != fobj.Value && fobj.Value instanceof ObjDef)
							{
								fsubobj = (ObjDef)fobj.Value;
								member = (ObjData)fsubobj.mem.get(infor);
								if(null != member)
								{
									strType = member.strType;
									result.whichUse = WhichValue.member;
								}
								else
									return null;
							}
							//类型不正确
							else
							{
								fobj = null;
								fsubobj = null;
								return null;
							}
						}
						//查对象属性的属性
						else
						{
							fobj = member;
							fsubobj = null;
							member = null;
						}
					}
					while(null == member);
				}
				//对象没有查到或不是自定义对象说明格式有问题
				else
				{
					fobj = null;
					fsubobj = null;
					return null;
				}
			}
		}
		
		//根据情况组织返回数据
		result.member = member;
		result.fobj = fobj;
		result.strType = strType;
		result.sefMem = fsubobj;
		return result;
	}
	//处理strArrayIndex可能是变量t++形式类似于temp[t++]格式要先处理temp[t]后再t++
	private String PreHandleIndex(String strArrayIndex)
	{
		String result = strArrayIndex;
		if(null == strArrayIndex || (null != strArrayIndex && strArrayIndex.isEmpty()))
			return null;
		
		//处理strArrayIndex可能是变量t++形式
		if(!isDigit(strArrayIndex))
		{
			String varName = null;
			Boolean bAdd = null;
			boolean atBegin = false;
			if(strArrayIndex.startsWith("++"))
			{
				varName = strArrayIndex.substring(2, strArrayIndex.length());
				varName.trim();
				bAdd = new Boolean(true);
				atBegin = true;
			}
			else if(strArrayIndex.endsWith("++"))
			{
				varName = strArrayIndex.substring(0, strArrayIndex.length()-2);
				bAdd = new Boolean(true);
				varName.trim();
			}
			else if(strArrayIndex.startsWith("--"))
			{
				varName = strArrayIndex.substring(2, strArrayIndex.length());
				bAdd = new Boolean(false);
				varName.trim();
				atBegin = true;
			}
			else if(strArrayIndex.endsWith("--"))
			{
				varName = strArrayIndex.substring(0, strArrayIndex.length()-2);
				bAdd = new Boolean(false);
				varName.trim();
			}
			
			FindObjMemResult findr = getObjAndMembyNameAndMemName(varName);			
			if(null != findr && findr.whichUse.equals(WhichValue.fojb))
			{
				if(null != findr.fobj.Value)
				{
					if(findr.fobj.Value instanceof Integer)
					{
						Integer value = (Integer)findr.fobj.Value;
						Integer bakValue = value;
						
						if(null != bAdd)
						{
							if(bAdd)
								value++;
							else
								value--;

							findr.fobj.Value = value;
							if(atBegin)
								result = value.toString();
							else
								result = bakValue.toString();
						}
						else
							result = bakValue.toString();
					}
					else if(findr.fobj.Value instanceof Double)
					{
						Double value = (Double)findr.fobj.Value;
						Double bakValue = value;
						
						if(null != bAdd)
						{
							if(bAdd)
								value++;
							else
								value--;

							findr.fobj.Value = value;
							if(atBegin)
								result = value.toString();
							else
								result = bakValue.toString();
						}
						else
							result = value.toString();
					}
				}
			}
			else if(null != findr && findr.whichUse.equals(WhichValue.member))
			{
				if(null != findr.member.Value)
				{
					if(findr.member.Value instanceof Integer)
					{
						Integer value = (Integer)findr.member.Value;
						Integer bakValue = value;
						
						if(null != bAdd)
						{
							if(bAdd)
								value++;
							else
								value--;

							findr.member.Value = value;	
							if(atBegin)
								result = value.toString();
							else
								result = bakValue.toString();
						}
						else
							result = value.toString();
					}
					else if(findr.member.Value instanceof Double)
					{
						Double value = (Double)findr.member.Value;
						Double bakValue = value;
						
						if(null != bAdd)
						{
							if(bAdd)
								value++;
							else
								value--;

							findr.member.Value = value;
							if(atBegin)
								result = value.toString();
							else
								result = bakValue.toString();
						}
						else
							result = value.toString();
					}
				}
			}
			else if(null != findr && findr.whichUse.equals(WhichValue.valueFromArray))
			{
				if(findr.value instanceof Integer)
				{
					Integer value = (Integer)findr.value;
					Integer bakValue = value;
					
					if(null != bAdd && null != findr.values && FindObjMemResult.Wrong != findr.index)
					{
						if(bAdd)
							value++;
						else
							value--;

						findr.values.set(findr.index,value);
						if(atBegin)
							result = value.toString();
						else
							result = bakValue.toString();
					}
					else
						result = value.toString();
				}
				else if(findr.fobj.Value instanceof Double)
				{
					Double value = (Double)findr.value;
					Double bakValue = value;
					
					if(null != bAdd && null != findr.values && FindObjMemResult.Wrong != findr.index)
					{
						if(bAdd)
							value++;
						else
							value--;

						findr.values.set(findr.index,value);
						if(atBegin)
							result = value.toString();
						else
							result = bakValue.toString();
					}
					else
						result = value.toString();
				}
			}
		}
		
		return result;
	}
	//查询对象的数组项比如user1.fam[1][1]
	@SuppressWarnings("unchecked")
	public FindObjMemResult getObjArrayItem(ObjData member,String strArrayIndex)
	{
		FindObjMemResult result = null;
		if(null == member || null == strArrayIndex || (null != strArrayIndex && strArrayIndex.isEmpty()))
			return null;
		
		//查询结果
		result = new FindObjMemResult();
		ObjData fobj = member;	//对象
		ObjDef sefMem = null;	//对象自定义属性
		Object value = null;	//自定义属性的定
		String strType = null;
		Vector<Object> values = null;
		
		//将strObjInfor分成操作子查询对象
		Object obj[] = splitMemb(strArrayIndex,",");
		for(Object mem : obj)
		{
			//所有操作子都必须是字符串型
			if(mem instanceof String)
			{
				String index = (String) mem;
				//处理strArrayIndex可能是变量t++形式
				index = PreHandleIndex(index);
				if(isDigit(index))
				{
					//数字就是要查询数组要求对象已经查到并且是数组型
					if(fobj.bArray && null == sefMem && null == values)
					{
						//小标从0开始应该小于数组元素个数
						if(Integer.valueOf(index) >= fobj.iCounts)
							return null;

						values = fobj.Values;
						//确认是否自定义类型
						if(fobj.dataType.equals(DataType.SelfDef))
						{
							Object item = fobj.Values.get(Integer.valueOf(index));
							if(null != item)
							{
								if(item instanceof ObjDef)
								{
									sefMem = (ObjDef)item;
									strType = fobj.strType;
									result.whichUse = WhichValue.selfMemFromArray;
									result.index = Integer.valueOf(index);
								}
								else
								{
									value = item;
									strType = fobj.strType;
									result.whichUse = WhichValue.valueFromArray;
									result.index = Integer.valueOf(index);
								}
							}
							else
								return null;
						}
						else
						{
							value = fobj.Values.get(Integer.valueOf(index));
							if(null == value)
								return null;
							result.whichUse = WhichValue.valueFromArray;
							result.index = Integer.valueOf(index);
							values = fobj.Values;
							strType = fobj.strType;
						}
					}
					//在数组中继续寻找找
					else if(null != value && value instanceof Vector<?>)
					{
						values = (Vector<Object>)value;
						//小标从0开始应该小于数组元素个数
						if(Integer.valueOf(index) >= values.size())
							return null;
						value = values.get(Integer.valueOf(index));
						if(null == value)
							return null;
						result.whichUse = WhichValue.valueFromArray;
						result.index = Integer.valueOf(index);
						strType = fobj.strType;
					}
					else
						return null;
				}
				//参数类型错误
				else
					return null;
			}
			else
				return null;
		}
		
		//根据情况组织返回数据
		result.sefMem = sefMem;
		result.value = value;
		result.strType = strType;
		result.fobj = fobj;
		result.values = values;
		return result;
	}
	//根据名字、数组下标、属性名查对象属性的属性比如User1.family.child User1.family[1].wife,其中User1后的都是
	@SuppressWarnings("unchecked")
	public FindObjMemResult getObjAndMemofMembyNameAarryOrMemName(ObjDef sefMem,String strObjInfor)
	{
		FindObjMemResult result = null;
		if(null == strObjInfor || strObjInfor.isEmpty() || null == sefMem)
			return null;
		
		//将strObjInfor分成操作子 
		result = new FindObjMemResult();
		Object obj[] = splitMemb(strObjInfor);
		ObjData member = null;	//自定义属性的定义
		ObjDef fsubobj = sefMem;
		Object value = null;
		String strType = null;
		ObjData fobj = null;
		Vector<Object> values = null;
		//根据操作子类型进行查询
		for(Object mem : obj)
		{
			//所有操作子都必须是字符串型
			if(mem instanceof String)
			{
				String infor = (String)mem;
				//非数字型应该是获取对象或对象的属性
				if(!isDigit(infor))
				{
					//如果上次已经查到对象属性，但根据查询仍需要查属性，就是对对象属性的属性查询
					//此时需要循环
					do
					{
						//第一次查对象属性
						if(null == member)
						{
							member = (ObjData)fsubobj.mem.get(infor);
							if(null == member)
								return null;
							result.whichUse = WhichValue.member;
							strType = member.strType;
						}
						//查对象属性的属性
						else if(member.Value instanceof ObjDef)
						{
							fsubobj = (ObjDef)member.Value;
							member = null;
						}
						//类型错误
						else
							return null;
					}
					while(null == member);
				}
				//数字就是要查询数组要求对象已经查到并且是数组型
				else if(null != member && member.bArray)
				{
					//确认是否自定义类型
					if(null == value)
					{
						//小标从0开始应该小于数组元素个数
						if(Integer.valueOf(infor) >= member.iCounts)
							return null;
						
						values = member.Values;
						if(null != member.Values)
						{
							value = member.Values.get(Integer.valueOf(infor));
							if(null == value)
								return null;
							result.whichUse = WhichValue.valueFromArray;
							result.index = Integer.valueOf(infor);
							strType = member.strType;
							fobj = member;
						}
						else
							return null;
					}
					else if(value instanceof Vector<?>)
					{
						values = (Vector<Object>)value;
						//小标从0开始应该小于数组元素个数
						if(Integer.valueOf(infor) >= values.size())
							return null;
						value = values.get(Integer.valueOf(infor));
						if(null == value)
							return null;
						result.whichUse = WhichValue.valueFromArray;
						result.index = Integer.valueOf(infor);
						strType = member.strType;
					}
				}
				//是数字但是对象没有找到或对象不是数组书名错误发生
				else
					return null;
			}
		}
		
		result.member = member;
		result.sefMem = fsubobj;
		result.value = value;
		result.strType = strType;
		result.fobj = fobj;
		result.values = values;
		return result;
	}

	//backupResult因为存在Variable++操作
	protected FindObjMemResult bakResult(FindObjMemResult result)
	{
		FindObjMemResult reRe = null;		
		if(null != result)
		{
			reRe = result.copy();
			
			//查到对象自定义属性
			if(result.whichUse.equals(WhichValue.valueFromArray))
			{
				Object var = result.value;		
				if(var instanceof ObjData)
				{
					try 
					{
						reRe.value = ((ObjData)var).copy();
					} 
					catch (CloneNotSupportedException e) 
					{
						e.printStackTrace();
					}
				}
				else if(var instanceof ObjDef)
				{
					reRe.value = ((ObjDef)var).copy();
				}
				
				reRe.values = copyVector(result.values.size(), result.values);
			}
		}
		
		return reRe;
	}
	//根据操作要求进行结果操作默认step为1
	protected void OperateOnResult(String strOper, FindObjMemResult result)
	{
		OperateOnResult(strOper,result,null);
	}
	//根据操作要求进行结果操作
	protected void OperateOnResult(String strOper, FindObjMemResult result,Integer step)
	{
		InforLogBuilder infor = null;
		boolean bStepWrong = false;
		boolean TypeWrong = false;
		Object objOn = null;
		if(null != result && null != strOper && !strOper.isEmpty())
		{
			//根据查到值的情况进行赋值
			if(result.whichUse.equals(WhichValue.fojb) || result.whichUse.equals(WhichValue.member))
			{
				ObjData var = null;
				//查到对象
				if(result.whichUse.equals(WhichValue.fojb))
					var = result.fobj;
				//查到对象属性
				else if(result.whichUse.equals(WhichValue.member))
					var = result.member;
				
				objOn = var;
				//值必须存在
				if(null == var.Value)
				{
					if(null == infor)
						infor = new InforLogBuilder();
					infor.setImportant(true);
					infor.append("Ojbect Value null");
				}
				else
				{
					//根据数据类型和要赋值类型进行适当赋值操作
					if(result.strType.equals("int"))
					{
						if(strOper.equals("++"))
							var.Value = ((Integer)var.Value) + 1;
						else if(strOper.equals("--"))
							var.Value = ((Integer)var.Value) - 1;
						else if(strOper.equals("+="))
						{
							if(null != step)
								var.Value = ((Integer)var.Value) + step;
							else
								bStepWrong = true;
						}
						else if(strOper.equals("-="))
						{
							if(null != step)
								var.Value = ((Integer)var.Value) - step;
							else
								bStepWrong = true;
						}
						else
							TypeWrong = true;
					}
					else if(result.strType.equals("double"))
					{
						if(strOper.equals("++"))
							var.Value = ((Double)var.Value) + 1;
						else if(strOper.equals("--"))
							var.Value = ((Double)var.Value) - 1;
						else if(strOper.equals("+="))
						{
							if(null != step)
								var.Value = ((Double)var.Value) + step;
							else
								bStepWrong = true;
						}
						else if(strOper.equals("-="))
						{
							if(null != step)
								var.Value = ((Double)var.Value) - step;
							else
								bStepWrong = true;
						}
						else
							TypeWrong = true;
					}
					else if(result.strType.equals("bool"))
					{
						if(strOper.equals("!"))
							var.Value = !((Boolean)var.Value);
						else
							TypeWrong = true;
					}
					else
						TypeWrong = true;
				}
			}
			//查到对象自定义属性
			else if(result.whichUse.equals(WhichValue.valueFromArray))
			{
				Object var = result.value;
				objOn = var;
				//数组索引必须存在
				if(result.index == FindObjMemResult.Wrong)
				{
					if(null == infor)
						infor = new InforLogBuilder();
					infor.setImportant(true);
					infor.append("Array index wrong");
				}
				else
				{
					//根据数据类型和要赋值类型进行适当赋值操作
					if(result.strType.equals("int"))
					{
						if(strOper.equals("++"))
							result.values.set(result.index, ((Integer)var) + 1);
						else if(strOper.equals("--"))
							result.values.set(result.index, ((Integer)var) - 1);
						else if(strOper.equals("+="))
						{
							if(null != step)
								result.values.set(result.index, ((Integer)var) + step);
							else
								bStepWrong = true;
						}
						else if(strOper.equals("-="))
						{
							if(null != step)
								result.values.set(result.index, ((Integer)var) - step);
							else
								bStepWrong = true;
						}
						else
							TypeWrong = true;
					}
					else if(result.strType.equals("double"))
					{
						if(strOper.equals("++"))
							result.values.set(result.index, ((Double)var) + 1);
						else if(strOper.equals("--"))
							result.values.set(result.index, ((Double)var) - 1);
						else if(strOper.equals("+="))
						{
							if(null != step)
								result.values.set(result.index, ((Double)var) + step);
							else
								bStepWrong = true;
						}
						else if(strOper.equals("-="))
						{
							if(null != step)
								result.values.set(result.index, ((Double)var) - step);
							else
								bStepWrong = true;
						}
						else
							TypeWrong = true;
					}
					else if(result.strType.equals("bool"))
					{
						if(strOper.equals("!"))
							result.values.set(result.index, !((Boolean)var));
						else
							TypeWrong = true;
					}
					else
						TypeWrong = true;
				}
			}
		}
		
		if(bStepWrong)
		{
			if(null == infor)
				infor = new InforLogBuilder();
			infor.setImportant(true);
			infor.append("Opertor : ");
			infor.append(strOper);
			infor.append(" Setp null");
		}
		if(TypeWrong)
		{
			if(null == infor)
				infor = new InforLogBuilder();
			infor.setImportant(true);
			infor.append("Opertor : ");
			infor.append(strOper);
			infor.append("  Value : ");
			infor.append(objOn.toString());
			infor.append("Wrong duiring operate on variable value, type wrong!!!");
		}
		if(null != infor)
			logShow.AppendShow(infor.toString(),InforType.Error);
	}
	//将Value赋值到Var
	protected void fillVar(ObjData var, Object Value,int index,Vector<Object> arrayofVar)
	{
		InforLogBuilder infor = null;
		boolean typewrong = false;
		
		//根据数据类型和要赋值类型进行适当赋值操作
		if(Value instanceof ObjData)
			Value = ((ObjData)Value).Value;
		
		//设置数组必须数组对数组，设置数组的项还是数组，被设置不是数组则类型不对
		if(null != arrayofVar && index != FindObjMemResult.Wrong)
		{
			Object item = arrayofVar.get(0);
			boolean bWrong = false;
			if(item instanceof Vector<?>)
			{
				if(!(Value instanceof Vector<?>))
					bWrong = true;
			}
			else
			{
				if(Value instanceof Vector<?>)
					bWrong = true;
			}
			if(bWrong)
			{
				infor = new InforLogBuilder();
				infor.setImportant(true);
				infor.append("Type wrong when fillvar : ");
				infor.append(Value.toString());
				logShow.AppendShow(infor.toString(),InforType.Error);
				return;
			}
		}
		
		if(var.dataType.equals(DataType.SelfDef))
		{
			if(Value instanceof ObjDef)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, ((ObjDef)Value).copy());
				else
					var.Value = ((ObjDef)Value).copy();
			}
			else
				typewrong = true;
		}
		else if(var.strType.equals("int"))
		{
			if(Value instanceof Integer)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, (Integer)Value);
				else
					var.Value = (Integer)Value;
			}
			else if(Value instanceof String)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index,Integer.valueOf((String)Value));
				else
					var.Value = Integer.valueOf((String)Value);
			}
			else if(Value instanceof Double)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index,((Double)Value).intValue());
				else
					var.Value = ((Double)Value).intValue();
			}
			else
				typewrong = true;
		}
		else if(var.strType.equals("double"))
		{
			if(Value instanceof Integer)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, ((Integer)Value).doubleValue());
				else
					var.Value = ((Integer)Value).doubleValue();
			}
			else if(Value instanceof String)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, Double.valueOf((String)Value));
				else
					var.Value = Double.valueOf((String)Value);
			}
			else if(Value instanceof Double)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, (Double)Value);
				else
					var.Value = (Double)Value;
			}
			else
				typewrong = true;
		}
		else if(var.strType.equals("char"))
		{
			if(Value instanceof String)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, ((String)Value).charAt(0));
				else
					var.Value = ((String)Value).charAt(0);
			}
			else if(Value instanceof Character)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, Value);
				else
					var.Value = Value;
			}
			else
				typewrong = true;
		}
		else if(var.strType.equals("string"))
		{
			if(Value instanceof String)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, (String)Value);
				else
					var.Value = (String)Value;
			}
			else if(Value instanceof Integer)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, ((Integer)Value).toString());
				else
					var.Value = ((Integer)Value).toString();
			}
			else if(Value instanceof Double)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, ((Double)Value).toString());
				else
					var.Value = ((Double)Value).toString();
			}
			else if(Value instanceof Character)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, ((Character)Value).toString());
				else
					var.Value = ((Character)Value).toString();
			}
			else if(Value instanceof Boolean)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, ((Boolean)Value).toString());
				else
					var.Value = ((Boolean)Value).toString();
			}
			else
				typewrong = true;
		}
		else if(var.strType.equals("bool"))
		{
			if(Value instanceof String)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, Boolean.valueOf((String)Value));
				else
					var.Value = Boolean.valueOf((String)Value);
			}
			else if(Value instanceof Boolean)
			{
				if(null != var.Values && index != FindObjMemResult.Wrong)
					arrayofVar.set(index, Value);
				else
					var.Value = Value;
			}
			else
				typewrong = true;
		}
		else
			typewrong = true;
		
		if(typewrong)
		{
			if(null == infor)
				infor = new InforLogBuilder();
			infor.setImportant(true);
			infor.append("Value : ");
			infor.append(Value.toString());
			infor.append("Wrong duiring set variable value, type wrong!!!");
		}
		if(null != infor)
			logShow.AppendShow(infor.toString(),InforType.Error);
	}
	//为找到变量赋值
	void setVariableValue(FindObjMemResult result, Object Value)
	{
		InforLogBuilder infor = null;
		if(null != result && null != Value)
		{
			ObjData var = null;
			//根据查到值的情况进行赋值
			if(result.whichUse.equals(WhichValue.fojb) || result.whichUse.equals(WhichValue.member))
			{
				//查到对象
				if(result.whichUse.equals(WhichValue.fojb))
					var = result.fobj;
				//查到对象属性
				else if(result.whichUse.equals(WhichValue.member))
					var = result.member;
				
				fillVar(var,Value,result.index,var.Values);
			}
			//查到对象自定义属性
			else if(result.whichUse.equals(WhichValue.valueFromArray) 
					|| result.whichUse.equals(WhichValue.selfMemFromArray))
			{
				if(null != result.values && FindObjMemResult.Wrong != result.index)
				{
					var = result.fobj;
					fillVar(var,Value,result.index,result.values);
				}
				else
				{
					if(null == infor)
						infor = new InforLogBuilder();
					infor.setImportant(true);
					infor.append("Value : ");
					infor.append(Value.toString());
					infor.append("Wrong duiring set variable value, type wrong!!!");
				}
			}
		}
		//参数错误
		else
		{
			if(null == infor)
				infor = new InforLogBuilder();
			infor.setImportant(true);
			infor.append("Wrong duiring set variable value, parameter null or not found!!!");
		}
		
		if(null != infor)
			logShow.AppendShow(infor.toString(),InforType.Error);
	}
	//栈添加项
	void pushStack(Object obj)
	{
		this.sysStack.push(obj);
	}
	//从栈获取第一项
	Object popStack()
	{
		if(!this.sysStack.isEmpty())
			return this.sysStack.pop();
		else
			return null;
	}
	//查看第一项
	Object peekStack()
	{
		if(!this.sysStack.isEmpty())
			return this.sysStack.peek();
		else
			return null;
	}
	//判断栈是否空
	boolean isStackEmpty()
	{
		return this.sysStack.isEmpty();
	}

	//保存结果入栈
	void PushVariableToStack(FindObjMemResult result)
	{
		InforLogBuilder infor = null;
		if(null != result)
		{
			//根据查到值的情况进行赋值
			if(result.whichUse.equals(WhichValue.fojb))
				pushStack(result.fobj);
			else if(result.whichUse.equals(WhichValue.member))
				pushStack(result.member.Value);
			//查到对象自定义属性
			else if(result.whichUse.equals(WhichValue.valueFromArray))
				pushStack(result.value);
			else if(result.whichUse.equals(WhichValue.selfMemFromArray))
				pushStack(result.sefMem);
			return;
		}
		
		//参数错误
		if(null == infor)
			infor = new InforLogBuilder();
		infor.setImportant(true);
		infor.append("Parameter wrong!!!");
		logShow.AppendShow(infor.toString(),InforType.Error);
	}
	
  /**
   * @param in the input to set
   */
  public static void setIn(Reader in) 
  {
	 // HyRuleNode.in = in;
  }

  /**
   * @param out the output to set
   */
  public static void setOut(Writer out) 
  {
	 // HyRuleNode.out = out;
  }
  
  	//把string转换成char
	protected Character getChar(Object value)
	{
		try
		{
			if(null != value)
			{
				if(value instanceof String)
					return ((String)value).charAt(0);
				else if(value instanceof ObjData)
				{
					value = ((ObjData)value).Value;
					return getChar(value);
				}
			}
		}
		catch(Exception e)
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("getChar transform wrong!!!");
			logShow.AppendShow(infor.toString(),InforType.Warn);
		}
		return null;
	}
	//返回类型
	protected String getType(Object obj)
	{
		String type = null;
		if(null != obj)
		{
			if(obj instanceof Integer)
				type = FuncConst.INTEGER;
			else if(obj instanceof Double)
				type = FuncConst.DOUBLE;
			else if(obj instanceof Boolean)
				type = FuncConst.BOOLEAN;
			else if(obj instanceof Character)
				type = FuncConst.CHARACTER;
			else if(obj instanceof String)
				type = FuncConst.STRING;
			else if(obj instanceof ObjData)
				type = FuncConst.OBJDATA;
			else if(obj instanceof ObjDef)
				type = FuncConst.OBJDEF;
		}
		return type;
	}
  	//把int,string,ObjData转换成Double
	protected Integer getInt(Object value)
	{
		try
		{
			if(null != value)
			{
				if(value instanceof Integer)
					return (Integer)value;
				else if(value instanceof Double)
					return ((Double) value).intValue();
				else if(value instanceof String)
					return Integer.valueOf((String)value);
				else if(value instanceof ObjData)
				{
					value = ((ObjData)value).Value;
					return getInt(value);
				}
			}
		}
		catch(Exception e)
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("getInt transform wrong!!!");
			logShow.AppendShow(infor.toString(),InforType.Warn);
		}
		return null;
	}
	//把int,string,ObjData转换成Double
	protected Double getDouble(Object value)
	{
		try
		{
			if(null != value)
			{
				if(value instanceof Double)
					return (Double)value;
				else if(value instanceof Integer)
					return ((Integer) value).doubleValue();
				else if(value instanceof String)
					return Double.valueOf((String)value);
				else if(value instanceof ObjData)
				{
					value = ((ObjData)value).Value;
					return getDouble(value);
				}
			}
		}
		catch(Exception e)
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("getDouble transform wrong!!!");
			logShow.AppendShow(infor.toString(),InforType.Warn);
		}
		return null;
	}
	//把string,ObjData转换成Double
	protected Boolean getBool(Object value)
	{
		try
		{
			if(null != value)
			{
				if(value instanceof Boolean)
					return (Boolean)value;
				else if(value instanceof String)
					return Boolean.valueOf((String)value);
				else if(value instanceof ObjData)
				{
					value = ((ObjData)value).Value;
					return getBool(value);
				}
			}
		}
		catch(Exception e)
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("getBool transform wrong!!!");
			logShow.AppendShow(infor.toString(),InforType.Warn);
		}
		return null;
	}
	//把string,ObjData转换成Double
	protected String getStr(Object value)
	{
		try
		{
			if(null != value)
			{
				if(value instanceof String)
				{
					return (String)value;
				}
				else if(value instanceof ObjData)
				{
					value = ((ObjData)value).Value;
					return getStr(value);
				}
				else if(value instanceof Integer)
					return ((Integer)value).toString();
				else if(value instanceof Double)
					return ((Double)value).toString();
				else if(value instanceof Boolean)
					return ((Boolean)value).toString();
				else if(value instanceof Character)
					return ((Character)value).toString();
			}
		}
		catch(Exception e)
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("getStr transform wrong!!!");
			logShow.AppendShow(infor.toString(),InforType.Warn);
		}
		return null;
	}
	//判断某个变量是否在某个对象中
	@SuppressWarnings("unchecked")
	protected Boolean objIn(FindObjMemResult left, FindObjMemResult right)
	{
		if(null == left || null == right)
			return false;
		else
		{	
			Object leftValue = null;
			if(left.whichUse.equals(WhichValue.fojb))
				leftValue = left.fobj.Value;
			else if(left.whichUse.equals(WhichValue.member))
				leftValue = left.member.Value;
			else if(left.whichUse.equals(WhichValue.valueFromArray))
				leftValue = left.value;
			else
				return false;
			
			Object rightValues = null;
			if(right.whichUse.equals(WhichValue.fojb))
				rightValues = right.fobj.Values;
			else if(right.whichUse.equals(WhichValue.member))
				rightValues = right.member.Values;
			else if(right.whichUse.equals(WhichValue.valueFromArray))
				rightValues = right.values;
			else
				return false;
			
			Vector<Object> values = (Vector<Object>)rightValues;
			if(null != values && values.contains(leftValue))
				return true;
			else
				return false;
		}
	}

	//判断两个变量是否相等
	protected Boolean IsEqual(Object left, Object right)
	{
		if(null == left || null == right)
			return false;
		else
		{	
			Object leftValue = null;
			Object rightValues = null;
			
			if(left instanceof ObjData)
			{
				ObjData lValue = (ObjData)left;
				leftValue = lValue.Value;
			}
			else
				leftValue = left;
			
			if(right instanceof ObjData)
			{
				ObjData rValue = (ObjData)right;
				rightValues = rValue.Value;
			}
			else
				rightValues = right;
			
			if(leftValue instanceof Integer)
			{
				if(rightValues instanceof String)
					rightValues = Integer.valueOf((String)rightValues);
				else if(rightValues instanceof Double)
					rightValues = ((Double)rightValues).intValue();
			}
			else if(leftValue instanceof Double)
			{
				if(rightValues instanceof String)
					rightValues = Double.valueOf((String)rightValues);
				else if(rightValues instanceof Integer)
					rightValues = ((Integer)rightValues).doubleValue();
			}
			else if(leftValue instanceof String)
			{
				if(rightValues instanceof Integer)
					rightValues = ((Integer)rightValues).toString();
				else if(rightValues instanceof Double)
					rightValues = ((Double)rightValues).toString();
				else if(rightValues instanceof Boolean)
					rightValues = ((Boolean)rightValues).toString();
			}
			else if(leftValue instanceof Boolean)
			{
				if(rightValues instanceof String)
					rightValues = Boolean.valueOf((String)rightValues);
			}
			else if(leftValue instanceof Character)
			{
				if(rightValues instanceof String)
					rightValues = Boolean.valueOf((String)rightValues);
				else if(rightValues instanceof Character)
					rightValues = (Character)rightValues;
			}
			
			if(null == leftValue || null == rightValues)
				return false;
			else
				return leftValue.equals(rightValues);
		}
	}
	//添加单个变量进变量表
	protected void setParam(String name,String strType, Object Value)
	{
		if(null == name || null == strType || name.isEmpty() || strType.isEmpty())
			return;
		
		//获取向哪里添加可能是本地符号表或全局符号表
		UseWhichTable bGlobal[] = new UseWhichTable[1];
		DataTAB tab = getCurentTab(bGlobal);
		printUseWhichTable(bGlobal[0]);
		
		int result = 0;
		if(strType.equalsIgnoreCase("int"))
		{
			if(null != Value)
				result = tab.setParam(name,strType,getInt(Value),false);
			else
				result = tab.setParam(name,strType,new Integer(0),false);
		}
		else if(strType.equalsIgnoreCase("bool"))
		{
			if(null != Value)
				result = tab.setParam(name,strType,getBool(Value),false);
			else
				result = tab.setParam(name,strType,new Boolean(false),false);
		}
		else if(strType.equalsIgnoreCase("char"))
		{
			if(null != Value)
				result = tab.setParam(name,strType,getChar(Value),false);
			else
				result = tab.setParam(name,strType,new Character('x'),false);
		}
		else if(strType.equalsIgnoreCase("double"))
		{
			if(null != Value)
				result = tab.setParam(name,strType,getDouble(Value),false);
			else
				result = tab.setParam(name,strType,new Double(0),false);
		}
		else if(strType.equalsIgnoreCase("string"))
		{
			if(null != Value)
				result = tab.setParam(name,strType,getStr(Value),false);
			else
				result = tab.setParam(name,strType,new String(""),false);
		}
		else
		{
			ObjDef objd = null;
			if(Value instanceof ObjData)
			{
				ObjData obj = ((ObjData)Value);
				if(null != obj && obj.Value instanceof ObjDef)
					objd = (ObjDef)obj.Value;
			}
			else if(Value instanceof ObjDef)
				objd = ((ObjDef)Value);
			
			if(null == objd)
			{
				DataTAB DataDeftab = getDataDefTab();
				if(null == objd)
					objd = DataDeftab.lookupDataDef(strType);
			}
			if(null != objd)
				result = tab.setParam(name,strType,objd.copy(),false);
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
				
		//根据结果展示信息
		if(result == DataTAB.dt_OK)
		{				
			InforLogBuilder infor = new InforLogBuilder();
			infor.append("Add parameter name: ");
			infor.append(name);
			infor.append(" type: ");
			infor.append(strType);
			logShow.AppendShow(infor.toString(),InforType.Debug);
		}
		else if(result == DataTAB.dt_NameExist)
		{
			InforLogBuilder infor = new InforLogBuilder();
			infor.setImportant(true);
			infor.append("Add parameter wrong name:");
			infor.append(name);
			infor.append(" already exists.");
			logShow.AppendShow(infor.toString(),InforType.Error);
		}
		else if(result == DataTAB.dt_ParamWrong)
			logShow.AppendShow("Add parameter wrong param.",InforType.Error);
	}
	//检查是否需要停止
	protected boolean isBreaked()
	{
		boolean reuslt = false;
		//检查是否需要停止
		Object result = this.peekStack();
		if(result instanceof String)
		{
			String strRe = (String)result;
			if(strRe.equals(FuncConst.BREAK))
			{
				this.popStack();
				reuslt = true;
			}
		}
		return reuslt;
	}

	//检查是否需要停止
	protected boolean isReturned()
	{
		boolean reuslt = false;
		//检查是否需要停止
		Object result = this.peekStack();
        if(result instanceof String)
        {
        	String strFin = (String)result;
        	if(strFin.equals(FuncConst.RETURN))
				reuslt = true;
        }
		return reuslt;
	}
	//检查是否需要continue
	protected boolean isContinued()
	{
		boolean reuslt = false;
		//检查是否需要停止
		Object result = this.peekStack();
		if(result instanceof String)
		{
			String strRe = (String)result;
			if(strRe.equals(FuncConst.CONTINUE))
			{
				this.popStack();
				reuslt = true;
			}
		}
		return reuslt;
	}
	//检查是否需要停止
	protected boolean isConditonTrue()
	{
		boolean reuslt = false;
		//检查是否需要停止
		Object result = this.peekStack();
	    if(result instanceof Boolean)
	    {
	    	Boolean bcont = (Boolean)result;
			this.popStack();
	        if(bcont)
				reuslt = true;
	    }
		return reuslt;		
	}
}

enum WhichValue
{
	none,fojb,member,valueFromArray,selfMemFromArray;
}
class FindObjMemResult
{
	public static final int Wrong = -1;
	public ObjData fobj = null;
	public ObjData member = null;
	public Object value = null;
	public String strType = null;
	public ObjDef sefMem = null;
	public Vector<Object> values = null;
	public int index = Wrong ;
	WhichValue whichUse = WhichValue.none;
	
	public FindObjMemResult copy()
	{
		FindObjMemResult result = new FindObjMemResult();
		if(null != this.fobj)
		{
			try 
			{
				result.fobj = this.fobj.copy();
			} 
			catch (CloneNotSupportedException e) 
			{
				e.printStackTrace();
			}
		}
		
		if(null != this.member)
		{
			try 
			{
				result.member = this.member.copy();
			} 
			catch (CloneNotSupportedException e) 
			{
				e.printStackTrace();
			}
		}
		
		if(null != this.value)
			result.value = this.value;
		
		if(Wrong != this.index)
			result.index = this.index;
		
		if(null != this.whichUse)
			result.whichUse = this.whichUse;
		
		if(null != this.strType)
			result.strType = this.strType;
		
		if(null != this.sefMem)
			result.sefMem = this.sefMem.copy();
		
		if(null != this.values)
			result.values = this.values;
		
		return result;
	}
}
