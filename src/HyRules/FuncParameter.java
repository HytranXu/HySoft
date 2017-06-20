package HyRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//用在Funclib定义函数库
class FunctionLib
{
	//名字和函数信息对应表
	private Map<String,FuncInfor> fParam = null;
	private LogOrShow logShow = null;
	
	public FunctionLib(LogOrShow log)
	{
		fParam = new HashMap<String,FuncInfor>();
		logShow = log;
	}
	
	public void AddFunLib(String strLibName, FuncInfor funInf)
	{
		if(null != strLibName && null != funInf && !strLibName.isEmpty())
			fParam.put(strLibName, funInf);
	}
	
	public FuncInfor getFunLib(String strLibName)
	{
		FuncInfor param = null;
		if(null != strLibName && !strLibName.isEmpty())
			param = fParam.get(strLibName);
		return param;
	}
	
	public void dump(String pre)
	{
		if(null != fParam)
		{
			for(String strFun : fParam.keySet())
			{
				InforLogBuilder infor = new InforLogBuilder();
				infor.append("FunctionLib: ");
				infor.append(strFun);
				logShow.AppendShow(infor.toString(),InforType.Infor);
				FuncInfor fi = fParam.get(strFun);
				fi.dumpName(pre);				
			}
		}
		else
			logShow.AppendShow("No function defined!",InforType.Infor);
	}
}
//函数信息，用在functionDefine中定义函数的返回值、参数及语法树
class FuncInfor
{
	private Map<String,FuncParameter> fParam = null;
	private LogOrShow logShow = null;
	
	public FuncInfor(LogOrShow log)
	{
		fParam = new HashMap<String,FuncParameter>();
		logShow = log;
	}
	
	public void AddFunInfor(String strFunName, FuncParameter param,String funRet ,SimpleNode func)
	{
		if(null != strFunName && null != param && !strFunName.isEmpty() && null != funRet)
		{
			param.setFuncNode(func);
			param.retType = funRet;
			fParam.put(strFunName, param);
		}
	}
	
	public FuncParameter getFunInfor(String strFunName)
	{
		FuncParameter param = null;
		if(null != strFunName && !strFunName.isEmpty())
			param = fParam.get(strFunName);
		return param;
	}
	
	public void dump(String pre)
	{
		if(null != fParam)
		{
			InforLogBuilder infor = new InforLogBuilder();
			for(String strFun : fParam.keySet())
			{
				FuncParameter funparam = fParam.get(strFun);
				infor.append("Function Name: ");
				infor.append(funparam.retType);
				infor.append(" ");
				infor.append(strFun);

				int paracount = funparam.getCount();
				if(paracount > 0)
				{
					Object[] param = funparam.toArray();
					Param tmpParm = null;
					for(int t=0; t<paracount; t++)
					{
						tmpParm = (Param)param[t];
						if(t > 0)
							infor.append(", ");
						else
							infor.append("(");
						infor.append(tmpParm.strType);
						infor.append(" ");
						infor.append(tmpParm.strName);
					}
					infor.append(")");
				}

				if(null != funparam.awtFunc)
					infor.append("  awt define!");
				infor.append("\n");
			}
			logShow.AppendShow(infor.toString(),InforType.Infor);
		}
		else
			logShow.AppendShow("No function defined!",InforType.Infor);
	}
	public void dumpName(String pre)
	{
		if(null != fParam)
		{
			InforLogBuilder infor = new InforLogBuilder();
			for(String strFun : fParam.keySet())
			{
				infor.append("Function Name: ");
				infor.append(strFun);
				infor.append("  ");
			}
			logShow.AppendShow(infor.toString(),InforType.Infor);
		}
		else
			logShow.AppendShow("No function defined!",InforType.Infor);
	}
}
//定义函数的参数、返回类型和语法树
public class FuncParameter 
{
	//参数列表
	private List<Param> paraList = new ArrayList<Param>();
	//函数语法树
	public SimpleNode awtFunc = null;
	//函数返回类型
	public String retType = null;
	
	public FuncParameter() 
	{
		
	}
	public void setFuncNode(SimpleNode funNode)
	{
		if(null != funNode)
			this.awtFunc = funNode;
	}
	
	public int getCount()
	{
		return paraList.size();
	}
	
	public void addParam(Param param)
	{
		paraList.add(param);
	}
	
	public Param getParam(int index)
	{
		Param result = paraList.get(index);
		return result;
	}
	
	public Object[] toArray()
	{
		return paraList.toArray();
	}
}
//定义函数参数
class Param
{
	public String strName = null;
	public String strType = null;
	boolean bArray = false;
	public String strvalue = null;
	
	//几个[]
	int iBracketCount = 0;
	
	public Param(String type, String name,boolean bArray, int iBracketCount, String iniValue)
	{
		this.strName = name;
		this.strType = type;
		this.bArray = bArray;
		this.iBracketCount = iBracketCount;
		this.strvalue = iniValue;
	}
}
//定义双目操作
class OperationDef
{
	public String strLeftValue = null;
	public String strRightValue = null;
	public String strOperator = null;
	public boolean bBool = true;
	
	public OperationDef()
	{
		
	}
	
	public OperationDef(String oper, String left, String right)
	{
		strLeftValue = left;
		strRightValue = right;
		strOperator = oper;
		bBool = false;
	}
}
//定义if条件初始操作
class Initor
{
	public String strName = null;
	public String StrType = null;
	public String strInitial = null;
	
	public Initor()
	{
		
	}
	
	public Initor(String name, String type, String strInitialval)
	{
		strName = name;
		StrType = type;
		strInitial = strInitialval;
	}
}
//定义函数常量
class FuncConst
{
	final static String RETURN = "returned";
	final static String BREAK = "break";
	final static String CONTINUE = "continue";
	final static String THIS = "this";
	final static String DOT = ".";
	final static String INTEGER = "Integer";
	final static String DOUBLE = "Double";
	final static String BOOLEAN = "Bollean";
	final static String CHARACTER = "Charactor";
	final static String STRING = "String";
	final static String OBJDATA = "ObjData";
	final static String OBJDEF = "ObjDef";
}
